package cn.lyn4ever.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @auther 微信公众号 “小鱼与Java”
 * @date 2020/3/22
 */
@SpringBootApplication
@EnableConfigServer //激活配置中心
@EnableDiscoveryClient
public class Config_Application_3000 {
    public static void main(String[] args) {
        SpringApplication.run(Config_Application_3000.class, args);
    }
}
