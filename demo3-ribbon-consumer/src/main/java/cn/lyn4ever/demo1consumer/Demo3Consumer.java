package cn.lyn4ever.demo1consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Demo3Consumer {
    public static void main(String[] args) {
        SpringApplication.run(Demo3Consumer.class,args);
    }
}
