package com.jiayi.exchange.binance;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jiayi.exchange.binance.service.BinanceWebSocketService;
import com.jiayi.common.AbstractWebSocketClient;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.net.URI;
import java.util.UUID;

/**
 * @Description: 币安网websckoet类
 *
 * @author jiayi
 * @date 2021年07月03日16:43:45
 */
@Slf4j
public class BinanceWebSocketClient extends AbstractWebSocketClient {

	private BinanceWebSocketService service;

	/**
	 * 订阅的client Id
	 */
	private String subId = Strings.EMPTY;

	public BinanceWebSocketClient(BinanceWebSocketService service) {
		this.service = service;
	}

	@Override
	public void connect() {
		try {
			subId = UUID.randomUUID().toString();
			//TODO 注意这里的订阅地址来自币安网 如果连不上可以试下其它地址。当然也可能需要外网才能访问 币安网官方API https://binance-docs.github.io/apidocs/spot/cn/#5393cd07b4
//			String url = "wss://stream.binance.com/ws";
//			String url = "wss://stream.binance.com:9443/ws";
			String url = "wss://stream.binance.com/stream";
			final URI uri = URI.create(url);

			//自定义handle加入Pipeline管道中
			BinanceWebSocketClientHandler handler = new BinanceWebSocketClientHandler(WebSocketClientHandshakerFactory
					.newHandshaker(uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()), this);
			connectWebSocket(uri, handler);
			if (isAlive()) {
				handler.handshakeFuture().sync();
			}
		} catch (Exception e) {
			log.error("WebSocketClient start error", e);
			if (group != null) {
				group.shutdownGracefully();
			}
		}
	}

	/**
	 * 发送ping消息
	 */
	@Override
	public void sendPing() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ping", System.currentTimeMillis());
		this.sendMessage(jsonObject.toString());
	}

	/**
	 * 发送pong消息
	 *
	 * @param pong
	 */
	public void sendPong(long pong) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("pong", pong);
		this.sendMessage(jsonObject.toString());
	}

	/**
	 * 订阅主题
	 */
	public void addSub(String channel) {
		if (!isAlive()) {
			return;
		}
		/**
		 * {
		 * "method": "SUBSCRIBE",
		 * "params":
		 * [
		 * "btcusdt@aggTrade",
		 * "btcusdt@depth"
		 * ],
		 * "id": 1
		 * }
		 */
		JSONObject jsonObject = new JSONObject().fluentPut("method", "SUBSCRIBE").fluentPut("params", Lists.newArrayList("btcusdt@aggTrade", "btcusdt@depth")).fluentPut("id", 1);
		String msg = jsonObject.toString();
		this.sendMessage(msg);
		this.addChannel(channel);
	}

	/**
	 * 添加交易对 集合
	 */
	@Override
	public void addChannel(String msg) {
		if (channel == null) {
			return;
		}
		subChannel.add(msg);
	}

	/**
	 * 移除交易对 集合
	 */
	@Override
	public void removeChannel(String channel) {
		if (channel == null) {
			return;
		}
		subChannel.remove(channel);
	}

	/**
	 * 重新建立连接
	 */
	@Override
	public void reConnect() {
		if (group != null) {
			this.group.shutdownGracefully();
		}
		this.group = null;
		this.connect();
		if (isAlive()) {
			// 重新订阅历史记录
			for (String channel : subChannel) {
				this.addSub(channel);
			}
		}
	}

	/**
	 * 为什么要发送ping pong这个根据各个交易所的websocket API文档而定
	 * 因为币安是需要隔一短时间发送pong消息，如果长时间不发，币安网会认为与我们已经断开了，就自动断开与我们的连接
	 */
	@Override
	public void onReceive(String msg) {
		// ping 消息
		monitorTask.updateTime();
		if (msg.contains("ping")) {
			this.sendMessage(msg.replace("ping", "pong"));
			return;
		}
		if (msg.contains("pong")) {
			this.sendPing();
			return;
		}
		// 处理订阅成功之后，返回的业务数据
		service.onReceive(msg);
	}

}
