# 从0开始手写PRC项目

## 2022 - 03 - 21 第一版本RPC框架
![](../../AppData/Local/Temp/aHR0cHM6Ly9jbi1ndW96aXlhbmcuZ2l0aHViLmlvL015LVJQQy1GcmFtZXdvcmsvaW1nL1JQQyVFNiVBMSU4NiVFNiU5RSVCNiVFNiU4MCU5RCVFOCVCNyVBRi5qcGVn.png)
* 1.RPC框架原理（Netty+Kyro+Zookeeper 实现的 RPC 框架）
  
        整个RPC框架分为三部分，第一部分为客户端（client），第二部分为服务端（server），第三部分为注册中心。
      整个框架的原理为，服务端提供Server向注册中心注册服务，使消费者Client通过注册中心拿到服务相关信息，
      然后再通过网络请求服务提供端Server。

* 2.注册中心

      注册中心是必须要有的。主要采用的是Zookeeper框架。注册中心负责服务地址的注册和查找，相当于目录服务。
      服务端启动的时候将服务名称及其对应的地址（IP + PORT）注册到注册中心种，客户端根据服务名称找到相对
      应的服务地址。有了服务地址之后，服务消费端就可以通过网络请求服务端了。