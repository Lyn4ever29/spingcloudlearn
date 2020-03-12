package cn.lyn4ever.zookeeper.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HelloConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("demo1/consumer/hello/{id}")
    public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口
        return restTemplate.getForObject("http://localhost:8001/demo1/provider/hello/"+id,String.class);
    }

}
