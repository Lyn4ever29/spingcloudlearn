# Hystrix的完全入门指南

<h2 id="1">理论知识</h2>

* <h3 id="1.2">Hystrix是什么？</h3>

  Hystrix是由Netflix开源的一个服务隔离组件，通过服务隔离来避免由于依赖延迟、异常，引起资源耗尽导致系统不可用的解决方案。这说的有点儿太官方了，它的功能主要有以下三个：

* <h4 id="1.2.1">服务降级</h4>

  ​		SpringCloud是通过HTTP Rest的方式在“微服务”之间进行调用的，所以每一个“微服务”都是一个web项目。既然它是一个web项目，它就就有可能会发生错误，这个错误有可能是服务器内存不足、客户端传参错误、网络问题等，也有可能是人为的（这个就是**服务熔断**）。也就是说，会因为一些原因从而不能给调用者返回正确的信息。

  ​		对于我们目前的单个SpringBoot项目来说，我们使用Ajax等一些方式调用接口时，如果服务器发生错误，我们在前端就会对这个错误进行处理。有可能是重试调用接口，或者给用户一个友好的提示，比如“服务繁忙，稍后再试”啥的。

  ​		但是在分布式系统中，同样也会发生一些“错误”，而且在多个服务之间调用时，如果不能对这些“错误”进行友好的处理，就会导致我们整个项目瘫痪，这是万万不能发生的。所以**Hystrix**利用**服务降级**来很好的解决了这个问题。这个其实就类似于我们的***try-catch***这样的机制，发生错误了，我就执行catch中的代码。

  ​		通过服务降级，能保证在某个或某些服务出问题的时间，不会导致整个项目出现问题，避免级联故障，从而来提高分布式系统的弹性。

* <h4 id="1.2.2">服务熔断</h4>

  ​	建设先看下边的服务降级代码，将整个服务降级的代码部分全部看完，再来看下边这段理论，你一定会茅塞顿开的。

  ​	Hystrix意为“断路器”，就和我们生活中的保险丝，开关一个道理。

  ​		当我们给整个服务配置了服务降级后，如果服务提供者发生了错误后，就会调用降级后的方法来保证程序的运行。但是呢？有一个问题，调用者并不知道它调用的这个服务出错了，就会在业务发生的时候一直调用，然后服务会一直报错，然后去调用降级方法。好比下图中：
  
  ![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200320003601.png)
  
  ​		它们的对话如下：
  
  ​		**Client:**我要调用你的方法A
  
  ​		**Server:**不行，我报错了。你调用降级方法吧，你的我的都行！
  
  ​		**Client**:哎呀，服务器报错了，那我就调用一下降级方法吧。
  
  ​		过了一会儿。。。。。。
  
  ​		**Client:**我要调用你的方法A
  
  ​		**Server:**刚才不是说了吗？我报错了。你调用降级方法吧，你的我的都行！
  
  ​		**Client**:哎呀，服务器报错了，那我就调用一下降级方法吧。
  
  ​		又过了一会儿。。。。。。
  
  ​		**Client:**我要调用你的方法A
  
  ​		**Server:**没完了是吧？我说过我报错了，你去调用这个降级方法啊。非要让我的代码又运行一次？

以上的对话说明了一个问题，当服务端发生了错误后，客户端会调用降级方法。但是，当有一个新的访问时，客户端会一直调用服务端，让服务端运行一些明知会报错的代码。这能不能避免啊，我知道我错了，你访问我的时候，就直接去访问降级方法，不要再让我执行错的代码。

​		这就是服务熔断，就好比我们家中的保险丝。当检测到家中的用电负荷过大时，就断开一些用电器，来保证其他的可用。在分布式系统中，就是调用一个系统时，在一定时间内，这个服务发生的错误次数达到一定的值时， 我们就打开这个断路器，不让调用过去，而是让他直接去调用降级方法。再过一段时间后，当一次调用时，发现这个服务通了，就将这个断路器改为“半开”状态，让调用一个一个的慢慢过去，如果一直没有发生错误，就将这个断路器关闭，让所有的服务全部通过。

* <h4 id="1.2.3">服务限流</h4>
  ​		服务限流就容易理解多了，顾名思义，这是对访问的流量进行限制，就比如上边的场景，我们还可能通过服务限流的方法来解决高并发以及秒杀等问题。主流的限流算法主要有：**漏桶算法**和**令牌算法**


<h2 id="2">开始码代码吧</h2>

> ​	不贴代码，说这么多有什么用？这不是耍流氓吗？

* #### 先创建一个我们需要的几个项目：

  |           模块名称           |    代码中项目名称     | 备注 |
  | :--------------------------: | :-------------------: | :--------------------------: |
  |   Eureka注册中心   |   eureka-alone-7000   | 测试期间,使用一个注册中心而不是集群 |
  | 客户端（消费者，服务调用者） |  hystrix-consumer-80  | 使用Feign或OpenFeign进行服务调用 |
  | 服务端（提供者，服务提供者） | hystrix-provider-8001 |  |

	> ​	这三个项目的创建代码略（[项目代码地址](https://github.com/Lyn4ever29/spingcloudlearn)）
	
* 在客户端和服务端都加入Hystrix的依赖（当然是在哪端进行服务降级就在哪端使用）

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
</dependency>
```


* <h3 id="1.2.1">服务降级</h3>

  服务降级有两种解决思路：可以分别从服务调用者和服务提供者进行服务降级，也就是进行错误的“兜底”


######	1. 从服务提供者方进行服务降级

我们先在提供者方的下列方法模拟一个“响应超时错误”。

```java
  /**
     * 这个方法会造成服务调用超时的错误
     * 其实本身体不是错误，而是服务响应时间超过了我们要求的时间，就认为它错了
     * @param id
     * @return
     */
    public String timeOutError(Integer id){
        return "服务调用超时";
    }
```

我们就给它定义一个错误回调方法，加上如下注解：

```java
/**
* 这个方法会造成服务调用超时的错误
* 其实本身体不是错误，而是服务响应时间超过了我们要求的时间，就认为它错了
* @param id
* @return
*/
@HystrixCommand(fallbackMethod = "TimeOutErrorHandler",commandProperties = {
    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "3000")
})
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
*	这个就是当上边方法的“兜底”方法
*/
public String TimeOutErrorHandler(Integer id) {
    return "对不起，系统处理超时"+id;
}
```

上边这个注解要注意三点：

1. fallCallbackMethod中的这个参数就是“兜底”方法
2. fallCallbackMethod中的这个方法的声明要和本方法一致
3. commandProperties属性中可以写多个@HystrixProperty注解，其中的name和value就是配置对应的属性，上例中的这个就是配置响应超时

最后在主启动类上加上这个注解

```java
@SpringBootApplication
@EnableEurekaClient //本服务启动后会自动注册进eureka服务中
@EnableCircuitBreaker
public class ProviderAppication_8001 {
    public static void main(String[] args) {
        SpringApplication.run(ProviderAppication_8001.class, args);
    }
}
```

这个我们是在服务提供者方面进行的错误处理，所以对服务调用者不做任何处理，启动三个项目（consumer,provder,eureka）。然后访问```http://localhost/consumer/hello/999```，理论上是要返回*服务调用正常999*，但是呢，由于我们人为造成了超时错误，所以就一定会返回fallback中的*对不起，系统处理超时999*，而且这个返回是会在3秒后。

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200319222751.png)

> ​	如果你觉得上边这个超时的错误演示很麻烦，可以直接在方法中写一个运行时错误，比如:```int i = 10/0;```也会进行fallbackMethod的调用。之所以要用这个超时配置，就是为了让你知道Hystrix可以对什么样的错误进行fallback，它的更多配置参考[https://github.com/Netflix/Hystrix/wiki/Configuration](https://github.com/Netflix/Hystrix/wiki/Configuration)

###### 2.从服务提供者方进行服务降级

和在服务提供方进行服务降级相比，在服务调用方（客户端、消费者）进行服务降级是更常用的方法。这两者相比，前者是要让服务提供者对自己可发生的错误进行“预处理”，这样，一定要保证调用者访问到我才会调用这个“兜底”方法。但是，大家想一下，如果我这个服务宕机了呢？客户端根本就调用不到我，它怎么可能接收到我的“兜底”方法呢？所以，在客户端进行服务降级是更常用的方法。

> 一个小疑问，如果我在客户端和服务端都进行了服务降级，是都会调用？还是先调用哪个？自己想喽，稍微动动你聪明的小脑袋。

* 为了不和上一个项目的代码冲突，我将上边这个@Service给注掉（也就是让Spring来管理它），从而用另外一个接口的实现，下边是我们新的serive类

```java
@Service
public class OrignService implements IExampleService {
    /**
     * 不用这个做演示，就空实现
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
     */
    @Override
    public String correct(Integer id) {
        return "访问正常，服务端没有进行任何错误"+id;
    }
}
```

* 在主启动类上添加如下注解：

```java
@EnableHystrix //注意这个和服务端的注解是不一样的
```

* 在application.yml中开户feign对Hystrix的支持

```yaml
feign:
	hystrix:
		enabled:true
```

* 将之前在provider项目中的@HystrixCommond放在feign的接口中

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200319231554.png)

###### 3.改进下解决方案

* 以上的两种方案看似可行， 但是，实际呢？心想，这是一个合格程序员应该做的事吗？每个接口我们都要写一个fallback方法？然后和我们的业务代码要写在一起？就好的“低耦合，高内聚”呢？
* 第一种解决方案，就是使用@DefaultProperties在整个Controller类上，顾名思义，就是给它一个默认的“兜底”方法，就不用每一个需要降级的方法进行设置fallbackMethod了，我们只需要加上@HystrixCommand好了。这个方法太过简单，不做代码演示，在文末的代码中专门写了注释
* 第二种解决方法：我们在客户端不是通过Feign调用的吗？是有一个Feign的本地接口类，我们直接对这个类进行设置就好了。直接上代码。

```java
@Component
//@FeignClient(value = "hystrix-provider") //这是之前的调用
@FeignClient(value = "hystrix-provider",fallback = ProviderServiceImpl.class) //这回使用了Hystrix的服务降级
public interface IProviderService {
    @GetMapping("provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id);
}
```

```java
@Component
public class ProviderServiceImpl implements IProviderService {
    @Override
    public String hello(Integer id) {
        return "调用远程服务错误了";
    }
}
```

* 以上两种方法的对比：
  * 第一种和我们的业务类进行了耦合，而且如果要对每个方法进行fallback，就要多写一个方法，代码太过臃肿。但是，它提供了一个DefaultProperties注解，可以提供默认的方法，这个后者是没有的。这种方法适合直接使用Ribbon结合RestTemplate进行调用的方法
  * 第二种提供了一个Feign接口的实现类来处理服务降级问题，将所有的fallback方法写到了一起，和我们的业务代码完全解耦了。对比第一个，我们可以定义一个统一的方法来实现DefalutPropeties。这种方法适合Feign作为客户端的调用，比较推荐这种。

<h3 id="1.2.2">服务熔断</h3>

​		请再回去看一下上边的关于服务熔断的理论知识，我相信你一定能看懂。当启用服务降级时，会默认启用服务熔断机制，我们只需要对一些参数进行配置就可以了，就是在上边的@HystrixCommand中的一些属性，比如：

```java
@HystrixCommand(fallbackMethod = "TimeOutErrorHandler",commandProperties = {
    @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds",value = "3000"),
    @HystrixProperty(name="circuitBreaker.enabled",value="true"),//开户断路器
    @HystrixProperty(name="circuitBreaker.requestVolumeThreshold",value="20"),//请求次数的峰值
    @HystrixProperty(name="circuitBreaker.sleepWindowInMilliseconds",value="10000"),//检测错误次数的时间范围
    @HystrixProperty(name="circuitBreaker.errorThresholdPercentage",value="60")//请求失败率达到多少比例后会打开断路器
})
```

> 这些配置可以在[https://github.com/Netflix/Hystrix/wiki/Configuration](https://github.com/Netflix/Hystrix/wiki/Configuration)了解，也可以打开查看HystrixCommandProperties类中的属性(使用idea一搜索就有)，全部都有默认配置

###	服务限流

东西太多，关注我期待后续。

###	项目代码和更多的学习地址
关注微信公从号“小鱼与Java”，回复“SpringCloud”获取
![](https://lyn4ever.gitee.io/img/wx/gzh2.png)