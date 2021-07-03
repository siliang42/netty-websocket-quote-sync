package com.jiayi.exchange.binance.service.impl;


import com.google.common.collect.Lists;
import com.jiayi.exchange.binance.BinanceWebSocketClient;
import com.jiayi.exchange.binance.Topic;
import com.jiayi.exchange.binance.service.BinanceWebSocketService;
import com.jiayi.exchange.binance.service.BinanceMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @Description: 币安网 主服务
 * @author jiayi
 * @date 2021年07月03日16:44:52
 */
@Slf4j
@Component
public class BinanceMainServiceImpl implements BinanceMainService {

    @Autowired
    private BinanceWebSocketService binanceWebSocketService;

    private BinanceWebSocketClient klineClient;

    private static List<String> channelCache = Lists.newLinkedList();

    @Override
    public void start() {
        // 拉取最新的订阅交易对
        List<String> channelList = binanceWebSocketService.getChannelCache();
        if (CollectionUtils.isEmpty(channelList)) {
            return;
        }
        channelCache = channelList;
        // 订阅kline
        firstSub(channelList, Topic.KLINE_SUB);
    }

    /**
     * 首次订阅交易对数据
     *
     * @param channelList 交易对列表
     * @param topicFormat 交易对订阅主题格式
     */
    private void firstSub(List<String> channelList, String topicFormat) {
        //封装huoBiProWebSocketService对象
        klineClient = new BinanceWebSocketClient(binanceWebSocketService);
        //启动连接币安网websocket
        klineClient.start();
        for (String channel : channelList) {
            //订阅具体交易对
            klineClient.addSub(formatChannel(topicFormat, channel));
        }
    }

    /**
     * 刷新数据
     */
    @Override
    public void refreshSubData() {
        // 拉取最新的订阅交易对
        List<String> channelList = binanceWebSocketService.getChannelCache();
        if (CollectionUtils.isEmpty(channelList)) {
            return;
        }
        reSub(channelList, Topic.KLINE_SUB);
    }

    private void reSub(List<String> channelList, String topicFormat) {
        for (String sub : channelList) {
            //如果不存在说明该交易所新增加了交易对 需要订阅该交易对
            if (!channelCache.contains(sub)) {
                klineClient.addSub(formatChannel(topicFormat, sub));
            }
        }
        channelCache = channelList;
       //交易所删除交易对 删除的这边就不做处理了
    }

    /**
     * 拼接订阅主题
     */
    private String formatChannel(String topic, String channel) {
        if (topic.equalsIgnoreCase(Topic.KLINE_SUB)) {
            return String.format(topic, channel, Topic.PERIOD[0]);
        }
        return String.format(topic, channel);
    }
}

