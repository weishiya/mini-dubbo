package org.minidubbo.rpc.registry.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.CuratorZookeeperClient;
import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.retry.RetryOneTime;
import org.apache.zookeeper.CreateMode;
import org.minidubbo.rpc.URL;
import org.minidubbo.rpc.exception.RpcException;
import org.minidubbo.rpc.registry.RegistryService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public List<URL> serviceDiscovery(String category) throws Exception {
        List<String> urlStr =  curatorFramework.getChildren().forPath(category);
        List<URL> urls = new ArrayList<>();
        if(urlStr!=null && urlStr.size()>0){
            for (String urlPath:urlStr){
                urls.add(toUrl(urlPath));
            }
        }
        return urls;
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

    private URL toUrl(String path){
        String[] datas = path.split("%");
        if(datas.length<3){
            return null;
        }
        URL url = new URL();
        String[] address = datas[1].split(":");

        url.setProtocol(datas[0]);
        url.setInterfaceName(datas[2]);
        url.setIp(address[0]);
        url.setPort(Integer.parseInt(address[1]));
        return url;
    }
}
