package org.minidubbo.rpc.cluster;

import org.minidubbo.rpc.Invocation;
import org.minidubbo.rpc.Invoker;
import org.minidubbo.rpc.Result;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.exception.RpcException;

import java.util.List;

public abstract   class AbstractClusterInvoker implements ClusterInvoker{

    protected Directory directory;

    protected AbstractClusterInvoker(Directory directory){
        this.directory = directory;
    }

    @Override
    public Directory getDirectory() {
        return directory;
    }

    @Override
    public Class getInterface() {
        return getDirectory().getInterface();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        List<Invoker> allInvokers = directory.getAllInvokers();

        return doInvoker(allInvokers,invocation);
    }

    @Override
    public URL getUrl() {
        return getDirectory().getConsumerUrl();
    }

    protected abstract Result doInvoker(List<Invoker> allInvokers,Invocation invocation);
}
