#	在SpringCloud中使用Feign进行服务的访问

之前已经说过了SpringCloud作为“分布式微服务”的解决方案时的大概原理和方法了。就是一个个web应用之间的访问，之前的访问方式有两种：

* 使用RestTemplate这个封装好的类，使用ip+端口+服务地址进行访问，这是最简单的访问方式了

```java
@GetMapping("demo1/consumer/hello/{id}")
public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口
    return restTemplate.getForObject("http://localhost:8001/demo1/provider/hello/"+id,String.class);
}
```

* 使用Ribbon进行访问负载均衡，也就是将“提供者”注册到注册中心，然后“消费者”使用微服务名进行访问

```java
@GetMapping("demo3/consumer/hello/{id}")
public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口
        return restTemplate.getForObject("http://demo3-ribbon-provider/demo3/provider/hello/"+id,String.class);
}
```

但是呢？这么访问还是很麻烦，使用restTemplate访问时，要对参数进行拼接。那么有没有更好的方法呢？

而且，如果我们想和RPC框架一样（比如Dubbo等），使用本地接口就可以访问呢？那么我们就要引入Feign

### 1. 创建一个服务访问的接口

既然我们想像在本地访问一样来调用远程“微服务”接口，当然，将远程代码写在本项目中是不可能的，那么就创建一个接口来访问

* 创建一个新的项目 demo4-feign-interface

> 其实你创建这个项目就是为了让消费者微服务进行依赖的，当然你可以直接在你的消费者项目中创建这个接口，但是并不利于管理，所以就新创建了一个项目，专门用来写feign的接口

* pom.xml(导入Feign客户端的依赖)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

* 然后编写接口的Service类

```java
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
    public String hello(@PathVariable("id") Integer id);
}
```

主要看一下上边的FeignClient注解中的value值，就是目标微服务中的服务名

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200308230423.png)

这个hello()方法以及注解中的内容，全部都是项目demo3-ribbon-provider中的

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200308230346.png)

* 然后，使用maven的命令，clean 然后install到本地仓库中以便于其他的项目进行调用
* 这个应用只是做为一个消费者的依赖，所以并不需要启动类

###	2.创建一个新的项目，使用Feign进行访问

* 创建新的使用Feign的消费者项目，将之前的demo3-consumer复制一份，然后修改如下：
* pom.xml添加feign的依赖和我们之前创建的demo4-feign-interface

```xml
<!--之前的依赖省略，请查看源码-->
<!--feign的依赖-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>

<!--之前创建的feign接口项目-->
<dependency>
    <groupId>cn.lyn4ever</groupId>
    <artifactId>demo4-feign-interface</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

* application.yml 不用修改，还是将之前的配置文件复制过来
* 在启动类上添加注解，声明Feign的调用包名

```java
@EnableFeignClients(basePackages= {"cn.lyn4ever.provider"})
```

* 接下来就是修改我们的controller类，可以使用本地接口进行访问了

```java
@RestController
public class HelloConsumerController {

    @Autowired
    private ProviderService providerService;

    @GetMapping("demo4/consumer/hello/{id}")
    public String hello(@PathVariable("id") Integer id){
        //直接使用本地的接口就可以访问了
        return providerService.hello(id);
    }
}
```

这们，启动Eureka集群、三个provicder的服务提供者、刚才创建的这个服务消费者，然后使用浏览器进行访问

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200309000130.png)

### 3.使用Feign进行访问有什么好处及其原理？

* 使用FeignClient访问时，可以不用再使用ip+port或者使用微服务名进行访问。可以直接在Autowired后，在本地调用方法
* 其实就是Feign在本地生成了动态代理，可以直接使用Autowired调用。其本质还是使用Ribbon进行负载均衡访问
* Feign中内置了Ribbon进行负载均衡，所以也同样可以像Ribbon一样进行自定义负载均衡算法

更多关于SpringCloud的学习笔记以及代码地址，关注微信公众号“小鱼与Java”回复“SpringCloud”获取

![](https://lyn4ever.gitee.io/img/wx/gzh2.png)

