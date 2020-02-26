package cn.lyn4ever.eurekademo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaCluster7001 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaCluster7001.class,args);
    }

}
