###	Eureka停更了？试试Zookpper和Consul

在Spring Cloud Netflix中使用Eureak作为注册中心，但是Eureka2.0停止更新，Eureka1.0 进入了维护状态。就像win7一样，同样可以用，但是官方对于新出现的问题并不能及时修复，所以我们就需要使用替代品。目前可用的注册中心替代品主要有：Zookeeper、Consul、Nacos等，这里主要讲前两个，Nacos是Spring Cloud Alilibaba中的组件，后期会说到。

### 使用Zookeeper作为注册中心

####	一、安装Zookeeper并启动服务

> ​	具体可参照

#### 二、将原有的微服务注册进Zookeeper

* 使用过Dubbo的小伙伴对Zookeeper一定不陌生。使用Eureka时，我们是创建一个新的SpringBoot Web项目（如果是Eureka集群的话，就要创建多个项目），然后将其他微服务注册进去，而Zookeeper却不用新建项目，只需要通过修改配置和简单的编码就可以进行注册
* 为了和之前的项目冲突，我们先将之前的项目复制两个新的项目```zookeeper-provider-8001```和```zookeeper-consumer-80```

##### 先修改```zookeeper-provider-8001```这个提供者项目

* 在pom.xml中引入依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zookeeper-discovery</artifactId>
    <exclusions>
        <exclusion>
            <!--为什么要排除这个依赖？-->
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <!--然后又引入这个依赖？-->
    <groupId>org.apache.zookeeper</groupId>
    <artifactId>zookeeper</artifactId>
    <version>3.4.13</version>
</dependency>
```

> ​	一个小细节，为什么要排除这个依赖，然后引入一个新的依赖？这波骚操作？

	1. 主要是在zookeeper-discovery中的zookeeper版本是3.4.8 
 	2. 而这个版本一定要和我们在服务器安装的zookeeper版本一致，不然你在注册的时候，会报错。当然，如果你在服务器本来安装的是3.4.8就不用这么麻烦了。

* 在application.yml中配置上zookeeper的地址

```yaml
server:
  port: 8001

spring:
  application:
#  这个应用的名称，用来注册在注册中心的名称
    name: zookeeper-provider
  cloud:
    zookeeper:
#    如果是zookeeper集群，在这个地址后边加上就可以，用逗号分开
      connect-string: 192.168.25.131:2181
```

* 在启动类中添加注解

```java
@SpringBootApplication
@EnableDiscoveryClient //注意这个注解是SpringCloud包中的
public class ApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationDemo.class, args);
    }
}
```

* 启动这个项目，然后在服务器中查看下zookeeper的节点

![使用Zookeeper作为Spring Cloud注册中心](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200312205017.png)

##### **同样的方法，修改消费者项目zookeeper-consumer-80**

* pom.xml(和上边一样引入zookeeper依赖)

* appliction.yml

```yaml
server:
  port: 80
spring:
  cloud:
    zookeeper:
      connect-string: 192.168.25.131:2181
```

* 和Eureka不同的是，我们同时也要将消费者注册进Zookeeper中

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationDemo.class, args);
    }
}
```

* 我们使用的是Ribbon用服务名进行访问的方式，所以还要修改它的controller类

```java
@GetMapping("consumer/hello/{id}")
    public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口
        return restTemplate.getForObject("http://zookeeper-provider/provider/hello/"+id,String.class);
    }
```

* 然后启动这个项目，使用浏览器访问```http://localhost/consumer/hello/999```没问题

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200312210436.png)

### 使用Consul作为注册中心

[查看它的中文文档](https://www.springcloud.cc/spring-cloud-consul.html)

Consul是使用go语言开发，是一个服务网格（微服务间的 TCP/IP，负责服务之间的网络调用、限流、熔断和监控）解决方案，它是一个一个分布式的，高度可用的系统，而且开发使用都很简便。它提供了一个功能齐全的控制平面，主要特点是：服务发现、健康检查、键值存储、安全服务通信、多数据中心。

#### 一、安装Consul

这个安装要比Zookeeper简单，我只说下windows安装操作，其他的查看官网[https://www.consul.io/](https://www.consul.io/)

* 在官网上下载安装包，windows的下载解压后就是一个.exe文件。使用cmd打开当前目录（在当前目录下按住Shift+鼠标右键，选择Open commond window here）然后运行如下命令：

```shell
consul agent -dev
```

* Consul和Eureka是有图形化界面的，启动Consul后直接用浏览器打开```localhost:8500```就可以看到

#### 二、将原有微服务注册进Consul

> ​	同样，我们复制两个新项目consul-provider-8001 和 consul-consumer-80。
>
> ​	而且和Zookeeeper一样，提供者和消费者都要注册进去。只贴出修改的代码，其他的参见源代码

* pom.xml

```xml
<!--只是添加了这一个依赖，其他的依赖不变，如消费者需要的ribbon等-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-consul-discovery</artifactId>
</dependency>
```

* application.yml（提供者和服务者除了端口号和application.name外，其他的不变）

```yaml
server:
  port: 8001

spring:
  application:
#  这个应用的名称，用来注册在注册中心的名称
    name: consul-provider
  cloud:
    consul:
      host: localhost
      port: 8500
      discovery:
#      这个就是要注册进consul中的服务名，直接使用了上边定义的微服务名
        service-name: ${spring.application.name}
```

* 在主启动类上添加注解（提供者和服务者都需要）

```java
@SpringBootApplication
@EnableDiscoveryClient
public class ApplicationDemo {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationDemo.class, args);
    }
}
```

* 注意修改Controller中的远程调用服务名

```java
@GetMapping("consumer/hello/{id}")
    public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口
        return restTemplate.getForObject("http://consul-provider/provider/hello/"+id,String.class);
    }
```

* 然后启动这个项目，先在浏览器中输入```localhost:8500```看一下我们的注册中心是否有这两个微服务。然后再次进行测试。



### 总结：

1. 这两个注册中心在和Spring Cloud整合时，它们的理念和步骤是一样的。

   ***引依赖***——》***修改application.yml将这个微服务注册进注册中心***——》***在主启动类中添加注解***

2. 和Eureka不同就是，在注册中心能同时看到提供者和消费者
3. 后期在Spring Cloud Alilibaba中，我们会使用Nacos（阿里自研的）作为注册中心