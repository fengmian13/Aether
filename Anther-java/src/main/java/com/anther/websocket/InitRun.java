package com.anther.websocket;

import com.anther.websocket.netty.NettyWebSocketStarter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/24 16:43
 */
@Component // 启动类,交给Spring管理
public class InitRun implements ApplicationRunner {

    @Resource
    private NettyWebSocketStarter nettyWebSocketStarter;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(nettyWebSocketStarter).start();
    }
}
