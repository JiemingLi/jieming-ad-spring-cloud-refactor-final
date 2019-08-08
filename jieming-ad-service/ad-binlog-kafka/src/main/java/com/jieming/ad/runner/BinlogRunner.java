package com.jieming.ad.runner;

import com.jieming.ad.mysql.BinlogClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// 实现该接口将会在Spring容器启动后就执行run方法
@Slf4j
@Component
public class BinlogRunner implements CommandLineRunner {

    @Autowired
    private BinlogClient client;

    @Override
    public void run(String... args) throws Exception {
        log.info("Comming binlog runner");
        client.connect();
    }
}
