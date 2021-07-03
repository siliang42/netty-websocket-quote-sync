package com.jiayi.exchange.huobi.service;


/**
 * @Description: 订阅接口
 *
 * @author jiayi
 * @date 2021年07月03日16:44:52
 */
public interface HuobiProMainService {

    /**
     * 首次订阅数据
     */
    void start();

    /**
     * 刷新数据
     */
    void refreshSubData();
}
