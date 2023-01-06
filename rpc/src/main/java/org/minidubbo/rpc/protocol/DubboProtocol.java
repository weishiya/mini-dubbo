package org.minidubbo.rpc.protocol;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.*;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.nettyHandler.RequestHandler;
import org.minidubbo.rpc.server.NettyServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DubboProtocol implements Protocol {

    //key为serviekey，等consumer发起网络调用的的时候根据servicekey找对对应exporter，进而找到invoker
    final Map<String/*serviceKey*/, Exporter<?>> exporterMap = new ConcurrentHashMap<>();

    ChannelHandler channelHandler = new RequestHandler() {
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
                }
            }
            return null;
        }
    };


    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        return null;
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
        NettyServer nettyServer = new NettyServer(url,channelHandler);
        nettyServer.open();
    }
}
