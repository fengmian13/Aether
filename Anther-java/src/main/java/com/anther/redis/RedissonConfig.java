package com.anther.redis;

import com.anther.entity.constants.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/8/26 11:00
 */
@Component
@ConditionalOnProperty(name = Constants.MESSAGEING_HANDLE_CHANNEL_KEY, havingValue = Constants.MESSAGEING_HANDLE_CHANNEL_REDIS)
@Slf4j
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    // TODO: 2025/8/26 创建RedissonClient实例,不理解：会议5
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        try{
            Config config = new Config();
            config.useSingleServer().setAddress("redis://" + redisHost + ":" + redisPort);//连上redis
            RedissonClient redissonClient = Redisson.create(config); // 创建RedissonClient实例,初始化
            return redissonClient;
        }catch (Exception e){
            log.error("redis配置错误，请检查redis配置", e);
        }

        return null;
    }
}
