package cn.lyn4ever.eureka;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaAloneStart {
    public static void main(String[] args) {
        SpringApplication.run(EurekaAloneStart.class,args);
    }
}
