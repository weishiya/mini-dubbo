package org.minidubbo.rpc.cluster;

import lombok.extern.slf4j.Slf4j;
import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.exception.RpcException;

import java.util.List;

@Slf4j
public class FailoverClusterInvoker extends AbstractClusterInvoker {

    public FailoverClusterInvoker(Directory directory) {
        super(directory);
    }

    @Override
    protected Result doInvoker(List<Invoker> allInvokers, Invocation invocation) {
        for (Invoker invoker : allInvokers) {
            try{
                Result result = invoker.invoke(invocation);
                return result;
            }catch (RpcException e){
                log.error("failover cluster",e);
            }
        }
        return null;
    }
}
