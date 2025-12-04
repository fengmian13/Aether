package com.anther;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author 吴磊
 * @version 1.0
 * @description: TODO
 * @date 2025/12/2 23:03
 */
@SpringBootApplication(scanBasePackages = {"com.anther"}, exclude = DataSourceAutoConfiguration.class)
public class AntherApplication {
    public static void main(String[] args) {
        System.out.println("hello world");
        SpringApplication.run(AntherApplication.class, args);
    }

}
