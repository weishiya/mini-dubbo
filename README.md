# mini-dubbo
从0到1写一个简易版得dubbo框架，旨在讲解dubbo原理

1.分支 provider-and-consumer 是一个最简单的rpc模型
可以看到，provider模块依赖的provider-api模块对HelloService进行了实现，consumer想调用HelloService的hello方法。

2.分支consumer-proxy是针对消费者方要引用的接口生成代理，核心思想是，针对要远程调用的接口生成代理对象，在代理对象里发起网络请求请求provider服务的提供方，运行一下consumer的main方法，可以看到控制台打印出了日志。

3.分支provider-server模块，是provider启动了网络服务，端口是20883，这就意味着外界可以通过网络请求访问provider。运行provider的main方法，然后在浏览器输入http://127.0.0.1:20883/hello
可以看到浏览器通过http请求访问到了HelloServiceImpl的hello方法。可以想到，只要在consumer端的代理对象发起一次http请求，就是一次rpc调用了（感兴趣可以自己实现一下）。

