# mini-dubbo
从0到1写一个简易版得dubbo框架，旨在讲解dubbo原理

1.分支 provider-and-consumer 是一个最简单的rpc模型
可以看到，provider模块依赖的provider-api模块对HelloService进行了实现，consumer想调用HelloService的hello方法。
