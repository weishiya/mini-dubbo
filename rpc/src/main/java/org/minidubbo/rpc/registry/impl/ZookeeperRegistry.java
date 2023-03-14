package org.minidubbo.rpc.registry.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.registry.RegistryService;

import java.util.List;

@Slf4j
public class ZookeeperRegistry implements RegistryService {

    private CuratorFramework  curatorFramework;

    public ZookeeperRegistry(String zookeeperpath){
        curatorFramework = CuratorFrameworkFactory.newClient(zookeeperpath, new RetryOneTime(3000));
    }


    @Override
    public void start() {
        curatorFramework.start();
    }

    @Override
    public void register(URL url) {

        try {
            String path = toPath(url);
            curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
            log.info("register to zk url={}",path);
        } catch (Exception e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + url + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public void unregister(URL url) {
        try {
            String path = toPath(url);
            curatorFramework.delete().forPath(path);
            log.info("unregister to zk url={}",path);
        } catch (Exception e) {
            throw new RpcException("Failed to register " + url + " to zookeeper " + url + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public List<URL> serviceDiscovery(String category) {
        return null;
    }

    @Override
    public void subscribe(URL url) {

    }

    @Override
    public void unsubscribe(URL url) {

    }

    @Override
    public void destory() {
        curatorFramework.close();
    }

    private String toPath(URL url){
        String interfaceName = url.getInterfaceName();
        StringBuffer path = new StringBuffer();
        path.append("/minidubbo/")
                .append(interfaceName).append("/");
        if("dubbo".equals(url.getProtocol())){
            path.append("providers/");
        }else {
            path.append("consumers/");
        }
        path.append(url.toPath());
        return path.toString();
    }
}
