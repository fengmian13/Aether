package com.anther.websocket;

import com.anther.websocket.message.MessageHandler;
import com.anther.websocket.netty.NettyWebSocketStarter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @param
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/24 16:43
 */
@Component // 启动类,交给Spring管理
public class InitRun implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitRun.class);

    @Resource
    private DataSource dataSource;

    @Resource
    private NettyWebSocketStarter nettyWebSocketStarter;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MessageHandler messageHandler;
    @Override
    public void run(ApplicationArguments args) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            redisTemplate.getConnectionFactory().getConnection().isClosed();
            //启动Neety
            new Thread(nettyWebSocketStarter).start();

            //启动消息订阅，消息监听
            new Thread(() -> {
                messageHandler.listenMessage();
            }).start();
            logger.error("服务启动成功，可以开始愉快的开发了");
        } catch (SQLException e) {
            logger.error("数据库配置错误，请检查数据库配置");
        } catch (Exception e) {
            logger.error("服务启动失败", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("关闭数据库连接失败");
                }
            }
        }
    }
}
