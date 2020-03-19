package cn.lyn4ever.consumer.contoller;

import cn.lyn4ever.consumer.service.IProviderService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
//提供一个默认的兜底方法
//@DefaultProperties(defaultFallback = "defalutfallMethod")
public class HelloConsumerController {

    @Autowired
    private IProviderService providerService;

    @GetMapping("consumer/hello/{id}")
    // 当上边有了默认的fallback时，如果不想指定fallback，就可以在下边的注解中不加任何参数
    @HystrixCommand(fallbackMethod = "TimeOutErrorHandler",commandProperties = {
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "3000"),
//            @HystrixProperty(name="circuitBreaker.enabled",value="true"),//开户断路器
//            @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="20"),//请求次数的峰值
//            @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="10000"),//检测错误次数的时间范围
//            @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="60"),//请求失败率达到多少比例后会打开断路器

    })
    public String hello(@PathVariable("id") Integer id) {
        return providerService.hello(id);
    }

    public String TimeOutErrorHandler(Integer id) {
        return "对不起，服务端处理理超时"+id;
    }

    /**
     * 这个默认的fallback的方法声明可以随意
     * @return
     */
    public String defalutfallMethod(){
        return "远程调用失败";
    }
}
