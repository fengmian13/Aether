package com.anther;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/2 23:03
 */
@SpringBootApplication(scanBasePackages = {"com.anther"})
@MapperScan(basePackages = "com.anther.mappers")
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class AntherApplication {
    public static void main(String[] args) {
        SpringApplication.run(AntherApplication.class, args);
    }

}
