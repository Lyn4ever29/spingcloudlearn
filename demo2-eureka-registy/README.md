##	eureka注册中心

###	 一、基本概念

SpringCloud封装 了Netflix公司的eureka作为自己微服务的注册中心。这个注册中心和dubbo中的zookeeper很相似，简单来说，只要你可以将你的”微服务“模块注册到注册中心，就可以供其他服务调用，一般来说，只有provider会注册到eureka，consumer也可以注册，但是并不建议这么做。

eureka的架构图如下：

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200224203820.png)

###	二、在SpringCloud项目中使用eureka

* 要想使用SpringCloud的子模块，一般分为两步:
  1. 引入相关的maven依赖
  2. 然后在配置类上加上@EnableXXX注解

#### 1. 创建一个新的Maven的Module项目，用来开启eureka

> ​	为了让小伙伴们更好地学习，我将上一次的项目复制了一份（见Github项目代码）

* pom.xml中添加依赖

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
    <!--这个是服务端的依赖-->
   <artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>	
```

* 创建启动类，并在启动类上加上@EnableEurekaServer注解

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaRegistApp_7001 {
    public static void main(String[] args) {
        SpringApplication.run(EurekaRegistApp_7001.class,args);
    }
}
```

* 添加application.yml配置文件如下：

```yaml
server:
  port: 7001

eureka:
  instance:
    hostname: localhost #eureka服务端的实例名称
  client:
    register-with-eureka: false     #false表示不向注册中心注册自己。
    fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    service-url:
#    这两个变量就是上边定义过的
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/       #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址。
```

* 然后启动这个项目，就输入访问地址```http://localhost:7001/```，就可以查看eureka注册中心了

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200224221201.png)

当有服务注册到这里时，就会在上图中出现。

#### 2. 将服务提供者注册进eureka

* 将eureka的依赖加入到pom.xml中

```xml
 <dependency>
     <groupId>org.springframework.cloud</groupId>
     <artifactId>spring-cloud-starter-eureka</artifactId>
 </dependency>
```

* 修改provider项目的application.yml

```yaml
server:
  port: 8001

spring:
  application:
#  这个应用的名称，用来注册在注册中心的名称
    name: demo2-provider

eureka:
  client: #客户端注册进eureka服务列表内
    service-url:
#    这个地址是在eureka的application.yml中定义过的
      defaultZone: http://localhost:7001/eureka
  instance:
    instance-id: demo2-provider-8001
    prefer-ip-address: true     #访问路径可以显示IP地址
```

* 修改启动类，加上@EnableEurekaClient注解，表明这是一个eureka的客户端，用来向eureka注册此服务

```java
@EnableEurekaClient //本服务启动后会自动注册进eureka服务中
@SpringBootApplication
public class Demo1Provider {
    public static void main(String[] args) {
        SpringApplication.run(Demo1Provider.class, args);
    }
}
```

* 然后重新启动这个服务提供者项目，就会在注册中心中看到这个服务

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200224222129.png)

### 三、几个小插曲

* 如果你的eureka注册中心出现如图情况

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200224231501.png)

请不要惊慌，这只是eureka的自我保护意识。Eureka Server 在运行期间会去统计心跳失败比例在 15 分钟之内是否低于 85%，如果低于 85%，Eureka Server 会将这些实例保护起来，让这些实例不会过期，但是在保护期内如果服务刚好这个服务提供者非正常下线了，此时服务消费者就会拿到一个无效的服务实例，此时会调用失败，对于这个问题需要服务消费者端要有一些容错机制，如重试，断路器等。

我们在单机测试的时候很容易满足心跳失败比例在 15 分钟之内低于 85%，这个时候就会触发 Eureka 的保护机制，一旦开启了保护机制，则服务注册中心维护的服务实例就不是那么准确了，此时我们可以使用`eureka.server.enable-self-preservation=false`来关闭保护机制，这样可以确保注册中心中不可用的实例被及时的剔除（**不推荐**）。

* 如何搭建eureka集群

原理就是多创建几个eureka项目，但是端口不同，然后在注册的时候，将它们写在一起（下期会说到）

[本项目代码地址](https://github.com/Lyn4ever29/spingcloudlearn)

![](https://lyn4ever.gitee.io/img/wx/gzh2.png)

