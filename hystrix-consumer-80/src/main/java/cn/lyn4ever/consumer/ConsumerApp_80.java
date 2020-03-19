package cn.lyn4ever.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients(basePackages= {"cn.lyn4ever"})
@EnableHystrix //注意这个和服务端的注解是不一样的
public class ConsumerApp_80 {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApp_80.class,args);
    }
}
