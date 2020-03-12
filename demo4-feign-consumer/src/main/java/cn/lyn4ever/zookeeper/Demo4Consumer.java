package cn.lyn4ever.zookeeper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages= {"cn.lyn4ever.provider"})
public class Demo4Consumer {
    public static void main(String[] args) {
        SpringApplication.run(Demo4Consumer.class,args);
    }
}
