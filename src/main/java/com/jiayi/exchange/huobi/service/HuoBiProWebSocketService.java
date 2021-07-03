package com.jiayi.exchange.huobi.service;

import java.util.List;

/**
 * @Description: 其它服务拉取交易对消息 和调其它服务保存消息 相关接口
 *
 * @author jiayi
 * @date 2021年07月03日16:44:52
 */
public interface HuoBiProWebSocketService {

    /**
     * 获取订阅的消息进行消费
     *
     * @param msg 消息内容
     */
    void onReceive(String msg);

    /**
     * 获取交易所， 交易对缓存
     *
     * @return 返回 已缓存的交易对
     */
    List<String> getChannelCache();
}
