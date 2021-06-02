package cn.lyn4ever.provider;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 这个注解表明这是一个Feign的客户端，
 * 其中的value属性就是目标微服务的服务名
 */
@FeignClient(value = "demo3-ribbon-provider")
public interface ProviderService {
    /**
     * 这个其实是provider提供者中的方法
     *
     * @param id
     * @return
     */
    @GetMapping("demo3/provider/hello/{id}")
    String hello(@PathVariable("id") Integer id);
}
