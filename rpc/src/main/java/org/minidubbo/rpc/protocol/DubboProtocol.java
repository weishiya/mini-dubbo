package org.minidubbo.rpc.protocol;

import io.netty.channel.*;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.common.Consant;
import org.minidubbo.rpc.*;
import org.minidubbo.rpc.client.NettyClient;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.invoker.DubboInvoker;
import org.minidubbo.rpc.nettyHandler.RequestHandler;
import org.minidubbo.rpc.server.NettyServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DubboProtocol implements Protocol {

    //key为serviekey，等consumer发起网络调用的的时候根据servicekey找对对应exporter，进而找到invoker
    private  final Map<String/*serviceKey*/, Exporter<?>> exporterMap = new ConcurrentHashMap<>();
    //key的格式为IP:PORT/interface,如127.0.0.1:20883/com.minidubbo.api.HelloService
    private final Map<String/*IP+port+Service*/,Client> clientMap = new ConcurrentHashMap<>();

    ChannelHandler handleRequestHandler = new RequestHandler() {
        @Override
        public Result reply(ChannelHandlerContext ctx, Object msg) throws RpcException {
            if(msg instanceof Invocation){
                Invocation inv = (Invocation) msg;
                //获取serviceKey找到对应的invoker直接调用
                String serviceKey = inv.getProtocolServiceKey();
                Exporter<?> exporter = exporterMap.get(serviceKey);
                if(exporter!=null && exporter.getInvoker()!=null){
                    Invoker<?> invoker = exporter.getInvoker();
                    return invoker.invoke(inv);
                }else {
                    throw new RpcException(RpcException.SERVICE_NOT_FOUND,"service not found");
                }
            }
            throw new RpcException(RpcException.INTERNAL,"format error");
        }
    };


    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        //todo 这里先固定写port，后续引入注册中心在通过服务发现的功能确定provider的IP和port
        url.setPort(Consant.PORT);
        //创建链接server的客户端
        Client client = getClient(url);
        //创建invoker
        DubboInvoker dubboInvoker = new DubboInvoker(type,url,client);
        return dubboInvoker;
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) {
        URL url = invoker.getUrl();

        String serviceKey = url.getServiceKey();

        DubboExporter dubboExporter = new DubboExporter(serviceKey,invoker);

        try {
            openServer(url);
        } catch (Throwable throwable) {
           log.info("oepn server error",throwable);
            return dubboExporter;
        }

        dubboExporter.exported();

        exporterMap.put(serviceKey,dubboExporter);

        return dubboExporter;
    }

    private void openServer(URL url) throws Throwable {
        NettyServer nettyServer = new NettyServer(url,handleRequestHandler);
        nettyServer.open();
    }

    private Client getClient(URL url) {
        String ip = url.getIp();
        int port = url.getPort();
        String interfaceName = url.getInterfaceName();
        String clientKey = ip+":"+port+"/"+interfaceName;
        Client client = clientMap.computeIfAbsent(clientKey, k -> {
            //todo client暂时传入null,后续会开发新的handler
            return new NettyClient(url, null);
        });
        client.connect();

        return client;
    }
}
