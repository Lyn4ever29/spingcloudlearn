## 使用Ribbon进行负载均衡

在使用Ribbon之前，我们先想一个之前的问题，之前我们将服务提供者注册进了eureka注册中心，但是在消费者端，我们还是使用的restTemplate调用的时候，其中写的还是http://localhost:8001这样的调用方式，是不是有一些不妥呢？是不是应用像dubbo那样，使用服务名进行调用呢？不然，我们使用注册中心有什么用呢？

好的呢，我们先保留这个思考 。来进入Ribbon的学习

## 什么是Ribbon?

Ribbon [ˈrɪbən] ,是SpringCloud Netflix中的一个关于**客户端**的负载均衡插件。

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227004552.png)

主要解释如下：

* 这个客户端主要是指服务消费者，也就是说，这个插件是用在消费者端的，它自己会根据一些算法对相同服务的提供者（也就是这几个服务提供者的application.name要相同）进行甄别，自己决定我要访问哪一个服务者。
* 而Nginx是整个服务器的负载均衡，当浏览器等设备的访问请求进来后，它会根据自身的配置，进行服务的路径选择。

## Ribbon的集成(客户端，即消费者)

上边说了，是在消费端进行的负载均衡，所以要修改cousumer端，但为了方便学习，我就新创建了一个项目，demo3-ribbon-consumer，代码和之前的没什么太大的变化。而且，要有多个相同名称的服务提供者才能进行负载均衡，才能在多个服务提供者之前进行选择吗？这就好比，你当了皇上，但是皇宫只有皇后一个人，那还有的选吗？哈哈，这车开的有点快了啊！

同样，我们要使用服务名进行调用，而不是使用localhost进行调用了。那是不是也要集成eureka注册中心呢？只有这样，才能在注册中心找到的啊

* 之前说过的使用SpringCloud组件的两步，第一步，引入依赖(注意，是对Consumer)

```xml
<!--ribbon-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-ribbon</artifactId>
</dependency>

<!--下边这两个是ribbon所依赖的-->
<!--eureka的客户端-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>

<!--SpringCloud的配置-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

* 在启动类上添加对应的注解

```java
@EnableEurekaClient //eureka的客户端
```

* 修改application.yml

```yaml
server:
  port: 80

#  下边加eureka的配置就是为了能够在eureka注册中心中发现服务提供者
eureka:
  client:
    register-with-eureka: false
    service-url:
#    使用的eureka集群，上一次配置过的
      defaultZone: http://eureka1:7001/eureka/,http://eureka2:7002/eureka/,http://eureka3:7003/eureka/
```

* 在配置RestTemplate的bean的配置类上开启负载均衡

```java
  	@Bean
    @LoadBalanced //开启负载均衡
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
```

* Contoller类，暂时修改如下：

```java
    @GetMapping("demo3/consumer/hello/{id}")
    public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口,这回使用的这个提供者的名称
        return restTemplate.getForObject("http://demo3-ribbon-provider/demo3/provider/hello/"+id,String.class);
    }
```

##	创建多个相同名称的服务提供者

上边说了，这个一定是要有的。只有当你有选择的时候，你才能做出选择。这个步骤太过简单了，只写其中一个的代码，其余的自己复制就可以了（module名称为demo3-ribbon-provider-800x）。

* pom.xml

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

* application.yml

```yaml
server:
#每个服务提供者对应的端口号肯定要不同
  port: 8001

spring:
  application:
#  这个应用的名称，用来注册在注册中心的名称
    name: demo3-ribbon-provider

eureka:
  client: #客户端注册进eureka服务列表内
    service-url:
#       eureka集群的注册地址
      defaultZone: http://eureka1:7001/eureka,http://eureka2:7002/eureka,http://eureka3:7003/eureka
  instance:
    instance-id: demo3-ribbon-provider-8001
    prefer-ip-address: true     #访问路径可以显示IP地址
```

* Controller类

```java
@RestController
public class HelloController {

    @GetMapping("demo3/provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id) {
        return "这是来自提供者的一条消息,你传过来的消息" + id+"\n\t你这次访问的服务提供者8001";
    }
}
```

* 主启动类加上对应的注解

```java
@SpringBootApplication
@EnableEurekaClient //本服务启动后会自动注册进eureka服务中
@EnableDiscoveryClient//这个表明这个服务能被发现，也就是消费者可以调用它
public class Demo3RibbonProvider_8001 {
    public static void main(String[] args) {
        SpringApplication.run(Demo3RibbonProvider_8001.class, args);
    }
}
```

##	最后，齐活。进行测试

启动三个eureka集群、三个相同名称的服务提供者、一个消费者。一共七个服务，七个进程，不行，我的电脑已经开始向我示威了，来看一下我的电脑的内存。我的是8G内存，Idea已经占了我3G多了。

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227013059.png)



* Eureka集群的图片

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227013528.png)

* 连续三次访问```http://localhost/demo3/consumer/hello/99```的结果

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227013611.png)



![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227013628.png)

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227014455.png)

从上边结果可以看出来，三次连续访问竟然结果不一样，这就是ribbon启动的负载均衡算法了啊。至于为什么是这样？

##	Ribbon内置的负载均衡算法

Ribbon默认使用的是轮询算法，即对相同的服务提供者进行挨个轮询，这样，就能会让每一个服务提供者都被访问一次了。当然，Ribbon的负载均衡算法还支持自定义，下期再说喽！

