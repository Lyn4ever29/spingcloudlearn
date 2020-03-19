package cn.lyn4ever.consumer.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 为了方便，我直挂将这个Feign的接口写在这里在，更多的Feign学习
 * 请关注微信公众号 “小鱼与Java”
 * 回复“SpringCloud”获取
 */
@Component
//@FeignClient(value = "hystrix-provider") //这是之前的调用
@FeignClient(value = "hystrix-provider",fallback = ProviderServiceImpl.class) //这回使用了Hystrix的服务降级
public interface IProviderService {
    /**
     * 这个其实是provider提供者中的方法
     *
     * @param id
     * @return
     */

    @GetMapping("provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id);
}
