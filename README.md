![](https://img.shields.io/badge/JDK-1.8-brightgreen) ![](https://img.shields.io/badge/SpringBoot-1.5.9.RELEASE-red) ![](https://img.shields.io/badge/SpringCloud-Dalston.SR1-blue)

##	SpringCloud学习笔记

本项目地址主要是学习Sping Netflix 相关组件，使用的SpringBoot与SpringCloud的版本如下：

```xml
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
```

##### 入门理论

* [首个SpringCloud应用创建及基本原理分析-demo1](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/demo1provider8001)

##### 注册中心

* [注册中心Eureka入门学习-demo2](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/demo2-eureka-registy)

* [Eureka集群搭建](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/eureka-cluster-7001)

* [Eureka停更？试下Zookeeper和Consul吧!](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/zookeeper-provider-8001)

##### 负载均衡

* [集成Ribbon负载均衡-demo3](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/demo3-ribbon-consumer)

* [使用Feign作为客户端进行服务的访问](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/demo4-feign-consumer)

####	服务的降级与熔断

*	[Hystrix完全入门指南](https://github.com/Lyn4ever29/spingcloudlearn/tree/master/hystrix-consumer-80)

### 使用Gateway作为路由网关
> 由于Gateway要使用SpringBoot2.0以上，而本仓库中的Springboot版本是1.5.9,所以我将相关教程移到了SpringCloud Alibaba仓库中
>
*  [GateWay入门操作](https://github.com/Lyn4ever29/cloudAlibabaLearn/tree/master/gateway-common-80)
     
* [Gateway与SpringSecurity结合](https://github.com/Lyn4ever29/cloudAlibabaLearn/tree/master/gateway-custom-filter-80)
)

## SpringCloud Alibaba学习地址

* [SpringCloud Alibaba学习地址](https://github.com/Lyn4ever29/cloudAlibabaLearn)


## 学习方法？

> ​	这么多的项目，不知道哪个是哪个？不用关键，往下看。

1. git clone或下载本项目到本地，然后使用IDEA导入要本项目

```

git clone https://github.com/Lyn4ever29/spingcloudlearn.git

```

2. 点击上边的每一个链接查看对应的项目页面的README.md文件
3. 在文档最上边有一个本教程涉及的项目列表。
4. 查看对应的教程时，可以快速地找到对应的项目。

##  更多的教程？
关注“小鱼与Java”微信公众号，回复"SpringCloud"获取
![](https://lyn4ever.gitee.io/img/wx/gzh2.png)