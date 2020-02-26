我相信，如果小伙伴们能来到这里，肯定对微服务有一定的认识。

我们之前创建web项目的时候，常见的有两种方式：

1).创建一个war包，然后放在servlet容器中运行（比如Tomcat等）;

2).使用SpringBoot创建一个jar包运行，这也是使用了内嵌式的servlet容器。

这么做的好处是：开发布署方便，直接放在tomcat的webapp目录下或者使用java -jar xxx.jar 方式进行运行。

但是，缺点呢？我们通常在一个tomcat下会运行多个应用程序，就算是使用只装一个应用。如果在同一时间有很大的并发访问量，会导致内存溢出，从而让整个tomcat停止运行，其中的应用也停止服务，这是很可怕的事。

那么，我们就需要使用分布式开发和布署。我们将一个巨大的项目进行“微服务化”，这样，就算其中的一个服务坏了，其他服务依然可以运行。就拿一个最常见的博客系统来说，如果我们将发布博文和评论系统分开部署，当评论系统坏了，并不影响我们发布和阅读博文。

## 什么是微服务？

就是我们对整个系统进行划分的各处微小的“系统”。比如：电商网站中一般会将订单、评论、购物车等各分为一个单独的“微服务”

## 什么是SpringCloud

“微服务化”只是一种思想，目前市面上有很多的解决方案，Dubbo和SpringCloud就是目前最常用的两种解决方案。但是两者有很大的区别:Dubbo采用的是RPC(远程过程调用)，SpringCloud使用的是HTTP REST的通信方式。

> RPC的底层是通过二进制传送的，用的socket的通信方式。
>
> HTTP REST的通信方式就是使用http协议，比如我们常用的httpclient就一种进行http通信的工具类

SpringCloud并不是像SpringBoot一样是一种新型技术，它只是一系列的技术的结合体。

## 如何学习SpringCloud

学习SpringCloud至少要拥有以下基础知识：

- SpringBoot应用的创建以及运行
- Maven的依赖继承，抽象以及管理

## 创建第一个SpringCloud应用

> 我使用的Idea开发工具，当然使用MyEclipse也是可以的。

#### 1. 创建一个Maven项目进行依赖管理

注意将这个项目的打包方式设置为pom(因为只做依赖管理，不进行代码编写，其他的项目都会继承这个项目)

- 在项目中引入相应的依赖

```xml
?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>cn.lyn4ever</groupId>
    <artifactId>spingcloud-learn</artifactId>
    <version>1.0-SNAPSHOT</version>
     <!--打包方式为pom-->
    <packaging>pom</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencyManagement>
      <dependencies>
         <dependency>
           <groupId>org.springframework.cloud</groupId>
           <artifactId>spring-cloud-dependencies</artifactId>
           <!--springcloud的版本号就是这么奇怪-->
           <version>Dalston.SR1</version>
           <type>pom</type>
           <scope>import</scope>
         </dependency>
         <dependency>
           <groupId>org.springframework.boot</groupId>
           <artifactId>spring-boot-dependencies</artifactId>
           <version>1.5.9.RELEASE</version>
           <type>pom</type>
           <scope>import</scope>
          </dependency>
      </dependencies>
    </dependencyManagement>
</project>
```

#### 2.创建一个provider项目（web项目）

- 这个项目是一个maven的module项目，并不是一个project,在这个项目上鼠标右键就可以创建Module。
- 这个子项目是一个SpringBoot项目，但是并不建议使用Idea的Spri ing Initializr进行创建，因为它并不会建立和上一个项目的父子关系，建议创建一个maven项目，然后进行编码

![img](https://mmbiz.qpic.cn/mmbiz_png/GbkJgPaMATniaHoic7f0j8UtbpW8bzGiar4tzUKpiauAiczCyCaWbY2mR8lzMGKCEibT67qb1JmQFEribfaKFgcZiaITuQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 在pom.xml中引入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>spingcloud-learn</artifactId>
        <groupId>cn.lyn4ever</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>demo1-provider-8001</artifactId>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
      </dependency>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
      </dependency>
    </dependencies>
</project>
```

- 在resources下创建application.yml

```yaml
server:
  port: 8001
```

- 编写一个SpringBoot应用的启动类

```java
@RestController
public class HelloController {

    @GetMapping("demo1/provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id) {
        return "这是来自提供者的一条消息,你传过来的消息" + id;
    }
}
```

- 编写一个Controller

```java
@RestController
public class HelloController {

    @GetMapping("demo1/provider/hello/{id}")
    public String hello(@PathVariable("id") Integer id) {
        return "这是来自提供者的一条消息,你传过来的消息" + id;
    }
}
```

- 最后的项目结构如下：

![img](https://mmbiz.qpic.cn/mmbiz_png/GbkJgPaMATniaHoic7f0j8UtbpW8bzGiar4MLPCticARsw2VoCkA7icsy1L2iaNkaWk8AibFFRmH40BmAQoYichvUpGqPw/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 然后，启动这个主类，我们可以使用浏览器`http://localhost:8001/demo1/provider/hello/99`进行访问了

#### 3.创建一个消费者应用（也是一个web项目）

- 同样也是一个Maven的Module项目，创建方式和相关代码略，和上边一样，这里主要写一个配置类和Controller
- 不过，这回的application.yml中的端口号和上个要不一样，我使用的是80
- 配置一个RestTemplate的bean，进行远程访问。这个bean，其实和我们之前使用过的HttpClient是一样的，只不这个是Spring高度抽象后的一个工具类，要好用的多(这是SpringBoot的基础了，这个类相当于Spring中的xml配置文件)。



```java
@Configuration
public class BeanConfig {
    
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

- controller类

```java
@RestController
public class HelloConsumerController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("demo1/consumer/hello/{id}")
    public String hello(@PathVariable("id") String id){
        //远程调用provider中的接口
        return restTemplate.getForObject("http://localhost:8001/demo1/provider/hello"+id,String.class);
    }
}
```

- 完整项目结构如下：

![img](https://mmbiz.qpic.cn/mmbiz_png/GbkJgPaMATniaHoic7f0j8UtbpW8bzGiar4FcbicQlrGvicgY5QsvTA8GEnmlXgkonN02Nmaxfoy9VHDASJptF0YkRQ/640?wx_fmt=png&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1)

- 然后使用

  `http://localhost:80/demo1/consumer/hello/3`进行访问

## SpringCloud学习参考

[Spring官网](https://spring.io/projects/spring-cloud/)

[SpringCloud中文网](https://www.springcloud.cc/)

[本系列文章及代码地址]()

关注我的公众号，每日更新SpringCloud知识

![](https://lyn4ever.gitee.io/img/wx/gzh2.png)

