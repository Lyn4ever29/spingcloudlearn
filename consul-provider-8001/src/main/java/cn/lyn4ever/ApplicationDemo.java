package cn.lyn4ever;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //注意这个注解是SpringCloud包中的
public class ApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationDemo.class, args);
    }
}
