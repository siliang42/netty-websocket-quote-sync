package com.jiayi.exchange.binance.service.impl;


import com.google.common.collect.Lists;
import com.jiayi.exchange.binance.service.BinanceWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: 其它服务拉取交易对消息 和调其它服务保存消息
 * @author jiayi
 * @date 2021年07月03日16:44:52
 */
@Service
@Slf4j
public class BinanceWebSocketServiceImpl implements BinanceWebSocketService {

    @Override
    public void onReceive(String msg) {
        // 直接发送消息给中转服务， 中转服务来处理信息
        if (StringUtils.isBlank(msg)) {
            log.error("====onReceive-huobi==msg is null");
            return;
        }
        log.info("币安网数据:{}", msg);
    }

    @Override
    public synchronized List<String> getChannelCache() {
        // 假设这里是从远处拉取数据
        List<String> list = Lists.newArrayList("btcusdt");
        return list;
    }

}
