package cn.lyn4ever.service.impl;

import cn.lyn4ever.service.IExampleService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

//@Service
public class DemoService implements IExampleService {

    /**
     * 这个方法会造成服务调用超时的错误
     * 其实本身体不是错误，而是服务响应时间超过了我们要求的时间，就认为它错了
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "TimeOutErrorHandler",commandProperties = {
//            这个属性注解规定这个方法的响应时间为3000毫秒
            @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "3000")
    })
    @Override
    public String timeOutError(Integer id){
        try {
            //我们让这个方法休眠5秒，所以一定会发生错误，也就会调用下边的fallbakcMethod方法
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "服务正常调用"+id;
    }

    /**
     * 不发生错误的正确方法
     *
     * @param id
     * @return
     */
    @Override
    public String correct(Integer id) {
        return null;
    }

    public String TimeOutErrorHandler(Integer id) {
        return "对不起，系统处理超时"+id;
    }
}
