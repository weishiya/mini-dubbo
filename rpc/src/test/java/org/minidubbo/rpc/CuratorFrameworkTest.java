package org.minidubbo.rpc;

import org.apache.curator.RetryPolicy;
import org.apache.curator.RetrySleeper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.DeleteBuilder;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;

public class CuratorFrameworkTest {
    @Test
    public void test() throws Exception {
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new RetryPolicy() {
            @Override
            public boolean allowRetry(int i, long l, RetrySleeper retrySleeper) {
                return true;
            }
        });
        curatorFramework.start();
        DeleteBuilder delete = curatorFramework.delete();
        delete.forPath("/test/abc");
        delete.forPath("/test/efg");


        CreateBuilder createBuilder = curatorFramework.create();

        createBuilder.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/abc");
        createBuilder.creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/test/efg");

        curatorFramework.close();
        System.out.println("1");
    }
}
