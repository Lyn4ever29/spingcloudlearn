package cn.lyn4ever.service.impl;

import cn.lyn4ever.service.IExampleService;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OrignService implements IExampleService {
    /**
     * 不用这个做演示，就空实现
     * @param id
     * @return
     */
    @Override
    public String timeOutError(Integer id) {
        try {
            TimeUnit.SECONDS.sleep(3);
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
        return "访问正常，服务端没有进行任何错误"+id;
    }
}
