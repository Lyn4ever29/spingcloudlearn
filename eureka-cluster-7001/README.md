###	Eureka集群的搭建

上次说过了在SpringCloud应用中使用Eureka注册中心，用来对服务提供者进行服务注册与发现，但同时，它也是一个“微服务”，单个应用使用空间有限，因此和zookeeper一样，它也需要搭建集群（Cluster）。

搭建Eureka集群的原理就是创建多个eureka应用（端口不同），然后将所有的注册中心的地址联合到一起。下边就以创建三个集群为例

####	一、将之前的创建的eureka项目复制三份

> 复制三份是为了不破坏原来的项目，让小伙伴们更好地学习

* 先创建三个子Module，然后复制pom.xml中信依赖，复制application.yml，然后各自创建启动类

```xml
<dependency>
   <groupId>org.springframework.cloud</groupId>
   <artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

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

#### 二、修改application.yml

这是最主要的也是最关键的一步，就是修改其中的```eureka.client.service-url.defaultZone```这一项目。之前是这样的

```yacas
http://${eureka.instance.hostname}:${server.port}/eureka/
```

之前使用的是变量，就是当前文件中定义过的eureka.instance.hostname和server.port 这一个地址，现在有了集群后，就要直接写死了。修改原则如下：

* 在任意一个eureka项目中，将上边这个地址修改为其他两个eureka项目的地址，中间用逗号隔开
* 但是呢，这三个eureka项目的hostname还不能一样，不能全都是localhost，所以开发的时候，要在本地设置虚拟域名
* 在windows下的```C:\Windows\System32\drivers\etc\HOSTS```文件最后添加如下

```
127.0.0.1 eureka1
127.0.0.1 eureka2
127.0.0.1 eureka3
```

这样，就可以将eureka项目中的hostname改为上边三个中的一个了

比如，eureka-cluster-7001这个项目修改后的application.yml如下：

```yaml
server:
  port: 7001

eureka:
  instance:
    hostname: eureka1 #eureka服务端的实例名称
  client:
    register-with-eureka: false     #false表示不向注册中心注册自己。
    fetch-registry: false     #false表示自己端就是注册中心，我的职责就是维护服务实例，并不需要去检索服务
    service-url:
#    单个eureka
#      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/       #设置与Eureka Server交互的地址查询服务和注册服务都需要依赖这个地址。
# euraka集群
      defaultZone: http://eureka2:7002/eureka/,http://eureka3:7003/eureka
```

* 然后依次修改其他的两个eureka项目，最后再用浏览器打开任意一个eureka项目的地址，可以看到如下界面

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200226235126.png)

* 在随意访问三个地址，可以在任意一个项目中看到其他两个集集群

#### 三、修改服务提供者来注册到集群

* 之前是单个eureka项目，所以服务提供者就只有一个注册地址。现在是集群，所以要修改服务提供者的中的eureka注册地址了

我改的是```demo2-providereureka8001```中的配置文件，修改如下：

```yaml
eureka:
  client: #客户端注册进eureka服务列表内
    service-url:
#    这个地址是在eureka的application.yml中定义过的
#     单个eureka项目的的注册地址
#      defaultZone: http://localhost:7001/eureka
#       eureka集群的注册地址
      defaultZone: http://eureka1:7001/eureka,http://eureka2:7002/eureka,http://eureka3:7003/eureka
```

 改完之后，然后启动这个提供者项目 ，就可以在eureka集群的任意一个项目中看到这个服务了

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200227021029.png)