package cn.lyn4ever.controller;

import cn.lyn4ever.service.IExampleService;
import cn.lyn4ever.service.impl.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderController {

    @Autowired
    private IExampleService iExampleService;

    @GetMapping("provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id) {
        return iExampleService.timeOutError(id);
    }
}
