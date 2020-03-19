package cn.lyn4ever.eureka;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaCluster7002 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaCluster7002.class,args);
    }

}
