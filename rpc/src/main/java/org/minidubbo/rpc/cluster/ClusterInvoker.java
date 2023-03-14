package org.minidubbo.rpc.cluster;

import org.minidubbo.rpc.Invoker;

public interface ClusterInvoker extends Invoker {
    Directory getDirectory();
}
