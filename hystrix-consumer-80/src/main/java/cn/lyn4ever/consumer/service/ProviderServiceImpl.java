package cn.lyn4ever.consumer.service;

import org.springframework.stereotype.Component;

/**
 * 使用这个类实现feign接口，这里每个方法的实现就是每一个方法的“兜底”方法，
 * 空实现就代码不进行服务降级处理
 *
 * @auther 微信公众号 “小鱼与Java”
 * @date 2020/3/19
 */
@Component
public class ProviderServiceImpl implements IProviderService {
    /**
     * 这个其实是provider提供者中的方法
     *
     * @param id
     * @return
     */
    @Override
    public String hello(Integer id) {
        return "调用远程服务错误了";
    }
}
