package com.jiayi.scheduler;


import com.jiayi.exchange.huobi.service.HuobiProMainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author jiayi
 * @Description: 火币网定时任务获取时时虚拟币数据
 * @date 2021年07月03日16:42:44
 */
@Component
@Slf4j
public class HuobiTrigger {

    @Autowired
    private HuobiProMainService huobiProMainService;

    /**
     * 首次启动并订阅火币websocket数据
     */
    @PostConstruct
    public void firstSub() {
        try {
            huobiProMainService.start();
        } catch (Exception e) {
            log.error("huobi 首次启动订阅异常", e);
        }
    }

    /**
     * 上面首次启动就已经可以本地websokcet就已经和火币的websokcet连接成功，时时获取数据了。
     * <p>
     * 但是因为火币网的交易对可能会新增和减少，所以这里通过定时任务获取最新交易对数据，并进行订阅
     */
//    @Scheduled(cron = "0 0 */1 * * *")
    public void doSubHuoBiPro() {
        try {
            huobiProMainService.refreshSubData();
        } catch (Exception e) {
            log.error("刷新火币网交易对缓存, 并尝试重新订阅数据, error.", e);
        }
    }
}
