package cn.lyn4ever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient //本服务启动后会自动注册进eureka服务中
@EnableDiscoveryClient//这个表明这个服务能被发现，也就是消费者可以调用它
@SpringBootApplication
public class Demo2Provider {
    public static void main(String[] args) {
        SpringApplication.run(Demo2Provider.class, args);
    }
}
