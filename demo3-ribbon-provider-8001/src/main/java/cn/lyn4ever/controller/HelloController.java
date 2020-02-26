package cn.lyn4ever.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("demo3/provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id) {
        return "这是来自提供者的一条消息,你传过来的消息" + id+"\n\t你这次访问的服务提供者8001";
    }
}
