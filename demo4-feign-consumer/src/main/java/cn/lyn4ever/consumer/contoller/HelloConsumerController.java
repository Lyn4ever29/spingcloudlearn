package cn.lyn4ever.consumer.contoller;

import cn.lyn4ever.provider.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloConsumerController {

    @Autowired
    private ProviderService providerService;

    
    @GetMapping("demo4/consumer/hello/{id}")
    public String hello(@PathVariable("id") Integer id) {
        if(id==null){
            return "";
        }
        //直接使用本地的接口就可以访问了
        return providerService.hello(id);

    }
}
