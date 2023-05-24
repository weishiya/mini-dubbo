# mini-dubbo

从0到1写一个简易版的dubbo框架

用不同的分支一步步演化成一个完整的rpc框架

## <u>前奏</u>

> 代码分支： provider-and-consumer 

一个最简单的rpc模型，provider模块依赖的provider-api模块对HelloService进行了实现，consumer想调用HelloService的hello方法。

> 代码分支：consumer-proxy

针对消费者方要引用的接口生成代理，核心思想是，针对要远程调用的接口生成代理对象，在代理对象里发起网络请求请求provider服务的提供方，运行一下consumer的main方法，控制台打印出了日志。

> 代码分支：provider-server模块

provider启动了网络服务，端口是20883，这就意味着外界可以通过网络请求访问provider。运行provider的main方法，然后在浏览器输入http://127.0.0.1:20883/hello
这里为了演示方便，网络协议采用了最常见的http协议
## <u>组件封装</u>

> 代码分支：invoker

抽象出来用到组件

主要增加如下类：

- ServiceConfig，用于provider配置，并且生成provider的代理
- ReferenceConfig，用于consumer配置，生成consumer的代理
- Invoker，consumer发起网络调用，provider触发本地调用
- Invocation，网络调用入参
- JDKProxyFactory，生成代理的工厂，服务端生成Invoker
- Protocol，协议类，consumer生成Invoker，服务端进行发布服务
- URL，uniform resource locator

代码里均已做了注释，重点关注ServiceConfig的export方法和ReferenceConfig的get方法，可以看到生成逻辑。

## <u>封装协议前置之服务暴露</u>

> 代码分支：protocol

增加如下类

- NettyServer，服务端netty网络组件，开启一个server。
- DubboProtocol，实现了Protocol接口。

JDKProxyFactory的getInvoker实现了provider端要使用的invoker，dubbo export方法对服务进行了暴露（还需要定义编解码器来实现dubbo协议），export存放了serviceKey和Invoker的map，后面会用到。

运行Provider的main方法可以看到网络端口已经开启。

## <u>封装协议前置之服务引用</u>

> 代码分支：protocol-refer

增加如下类

- NettyClient，客户端的网络组件，负责开启client端口，连接server，发生请求，关闭连接。
- DubboInvoker，对Invoker进行实现，触发网络调用

netty是异步IO，所以客户端发起网络调用的思想是，client发起网络请求，然后阻塞等待server响应数据包，然后在唤起线程，核心代码如下：

```
@Override
public Result invoke(Invocation invocation) throws RpcException {
    Result result = doInvoke(invocation);
    try {
        //这里做成阻塞的,等待线程唤醒
        //todo 后续会用时间轮算法处理超时
        Object o = ((AsyncResult) result).getResponseFuture().get(3, TimeUnit.SECONDS);
        result.setValue(o);
    } catch (TimeoutException e) {
        result.setException(new RpcException(RpcException.TIMEOUT,invocation.getMethodName()+" timeout",e));
    }catch (InterruptedException | ExecutionException e){
        result.setException(new RpcException(RpcException.UNKNOWN,e.getMessage()));
    }
    return result;
}
```

运行provider的main方法，然后运行Consumer的main，可以看到provider控制台输出了日志 client connected。因为现在还没定义编解码器，还不能发送数据包。接下来就是封装协议。

## <u>封装协议</u>

> 代码分支：minidubbo-protocol

增加如下类：

- DubboEncodeHandler，编码器，实现netty的MessageToByteEncoder，把请求数据序列化成字节序列

- DubboDecoderHandler，解码器，实现Netty的ByteToMessageDecoder，对收到的字节序列解析成对象

数据发送方如何编码的，数据包的接收方如何进行解码。序列号协议这里先用最简单FastJson

**注意：**

1.dubbo对数据包的反序列化由业务线程池处理，这里为了简单用netty的work线程池。

2.dubbo协议的格式以bit为单位，这里为了更直观，取消掉或运算，直接用byte。

```
0-1字节 魔法值
2字节 数据包类型
3字节 调用方式
4字节 事件标识
5字节 序列方式
6-9字节 状态码
10-17字节 请求编号
18-21字节 数据体长度
```

运行provider和consumer的main方法，可以看到consumer端接收到了provider端发回的响应。

## Server<u>如何找到</u>Invoker，Client<u>如何收到响应</u>

> 代码分支：write-response

增加如下类

- RequestHandler，实现Netty的ChannelInboundHandlerAdapter，解码器解析为实体对象后传递给该类，可以看到，服务端如何找调用的哪个服务的哪个接口，就是通过代码分支protocol提到的serviceKey
- ClientHandler，实现Netty的ChannelInboundHandlerAdapter

ClientHandler唤醒了原本发送请求阻塞的线程，主要的原理的client发送请求时携带一个唯一ID，server响应时在携带回来，如此便可找到阻塞的线程。这里也实现了自定义超时事件的功能。核心代码如下：

```java
if(msg instanceof Response){
    Response response = (Response)msg;
    if(response.getStatus()!=Response.OK){
        throw new Exception(response.getErrorMessage());
    }
    CompletableFuture completableFuture = DefaultFuture.getCompletableFuture(response.getId());
    if(completableFuture == null || completableFuture.isCancelled()){
        return;
    }
    Object value = ((Result) response.getData()).getValue();
    completableFuture.complete(value);
}
```

运行provider和consumer的main方法，可以看到成功发送请求和收到响应

## 超时机制

> 代码分支：timeot

用时间轮算法来进行超时计算，时间到了就抛出超时异常，见TimeoutCheckTask。时间未到，收到响应请求，就被上一节提到了代码唤醒，便不会抛出超时异常。

运行单元测试TimeoutTest的testTimeout和testTimeout2，可以发现当超时时间是6秒是不超时的。

## <u>粘包拆包</u>bugfix

> 代码分支decode-bugfix

原来的解码器DubboDecoderHandler对数据包的粘包拆包解析有bug，做了修复。

运行ProtocolProviderTest和ProtocolTest可以看到10个线程发送了10次请求，收到了100次响应。

## 网络连接池

> 代码分支：muti-client-and-share-client

这里已经可以看出来dubbo为什么不适合传大文件，因为默认情况下client和server只会建立一个网络连接，如果某个网络请求传输大文件会阻塞其他所有请求。所以这里创建了类似于连接池，对服务的某个外部引用是否用共享的客户端，以及创建的时候用几个网络连接。如果一个服务可能要传输较大的数据包，这可能会阻塞其他服务的调用，可以单独为这个服务单独建立连接

详见DubboProtocl的getClient方法，默认是一个consumer对一个provider的所有网络请求共用一个连接，可以单独建立链接，或者多个网络链接。

## Reactor模型和server异步化的设计

> 代码分支：provider-async

基于主从reactor线程模型，引入业务线程池。此前所有的网络连接，读写请求，关闭网络连接等均由netty的worker线程池处理，现在提高性能，引入业务线程池，只有连接事件和编解码由Netty的worker线程池处理，具体的请求的执行发送到disruptor队列，由consumer（DubboEventHandler）来消费处理。可以了解reactor模型。

增加如下类

- ParallelFlusher，封装disruptor框架，具体业务请求会封装成事件塞到这个队列，由
- MessageOnlyServerHandler，实现ChannelInboundHandlerAdapter，server端读写请求事件由线程池处理，其他发起链接，关闭链接等事件由netty自带的work线程池处理
- DubboEventHandler，事件消费者，可以看到最终还是调了RequestEventHandler。

注：dubbo源码里没有disruptor，这里是个人拓展的

## <u>心跳机制</u>

> 代码分支：heartbeat

心跳检测机制，原理是client给server发送ping然后收到pong，一旦没有收到pong，就发起重连。

增加如下类：

- ClientIdleHandler，client的WRITER_IDLE检测，检测到一定时间内没有写事件，为了让server感知到没有断线，发送一个ping
- ServerIdleHandler，server的READER_IDLE检测，如果收到了client的ping，那么就返回ping，如果一定时间内没有收到client的写事件，那么默认client掉线，关闭网络连接。

## Provider<u>向注册中心注册</u>

> 代码分支：registry-server

provider把url注册到zookeeper。

增加如下类：

- ZookeeperRegistry，提高注册和取消注册的功能

## Consumer<u>服务发现</u>

> 代码分支：registry-consumer

consumer服务发现和集群容错机制。

增加如下类：

- Directory，consumer把zookeeper的provider注册Url生成Invoker
- ClusterInvoker，获取Directory，子类AbstractClusterInvoker提供容错机制，如发生调用错误可以快速失败，可以重试。
- ServiceDiscoveryProtocol，consumer进行服务发现的protocol，会成一个Directory，装饰本来DubboProtocol

运行RegistryTest和ConsumerRegistryTest

## <u>优雅下线</u>

> 代码分支：shutdown

注册JVM关闭时的钩子函数，包括从注册中心取消注册和销毁所有的网络连接和线程池。

增加如下类：

- ApplicationDeployer，项目初始化，注册JVM关闭时的钩子函数，也可以拓展其他功能

## 集成<u>spring</u>

> 代码分支：spring-1.0

consumer集成spring，用FactoryBean生成代理类。原理是扫描有@MiniDubboReference注解的field，然后注册BeanDefinition,最后FactoryBean生成代理类

增加如下类：

- ReferenceBean，实现spring的FactoryBean，FactoryBean其实也是一种bean，只不过spring在生成这种bean的时候不会反射，而是调getObject方法
- ReferenceAnnotationProcessor，继承InstantiationAwareBeanPostProcessorAdapter，实现postProcessPropertyValues，实例化bean以后，设置属性以前执行。

可看如下两段代码：

一：加@MiniDubboReference注解

```java
@Component
public class HelloController {
    private int id;
    @MiniDubboReference
    HelloService helloService;
}
```

二：调此HelloService的hello()方法

```java
public static void main(String[] args) {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext("org.minidubbo.adapter.spring");
    HelloController helloController = (HelloController)applicationContext.getAutowireCapableBeanFactory().getBean("helloController");
    System.out.println(helloController.helloService.hello());
}
```

## 后续拓展

引入SPI机制
