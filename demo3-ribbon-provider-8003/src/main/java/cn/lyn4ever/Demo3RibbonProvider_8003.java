package cn.lyn4ever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient //本服务启动后会自动注册进eureka服务中
@EnableDiscoveryClient//这个表明这个服务能被发现，也就是消费者可以调用它
public class Demo3RibbonProvider_8003 {
    public static void main(String[] args) {
        SpringApplication.run(Demo3RibbonProvider_8003.class, args);
    }
}
