package org.minidubbo.common;

public interface Consant {
    int PORT = 20883;
    String DUBBO_PROTOCOL = "dubbo";
    String CONSUMER_PROTOCOL = "consumer";
    String GROUP_KEY = "group";
    String VERSION_KEY = "version";
    String TIMEOUT_KEY = "timeout";
    Integer DEFAULT_TIMEOUT = 1;
    String SHARE_CONNECTIONS_KEY = "shareconnections";
    String CONNECTIONS_KEY = "connections";
}
