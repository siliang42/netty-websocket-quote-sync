package com.jiayi.exchange.huobi.service.impl;

import com.jiayi.exchange.huobi.service.HuobiProMainService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuobiProMainServiceImplTest {

    @Autowired
    private HuobiProMainService huobiProMainService;

    @Test
    public void start() {
        huobiProMainService.start();
    }

    @Test
    public void refreshSubData() {
        huobiProMainService.refreshSubData();
    }
}