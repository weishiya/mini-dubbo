package org.minidubbo.rpc;

import io.netty.util.internal.StringUtil;
import org.minidubbo.common.Consant;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * uniform resource locator
 * 如：dubbo://192.168.194.1:20883/org.apache.dubbo.demo.DemoService?methods=sayHello,sayYes&group=abc&version=1.0.0&appId=provider-demo&side=provider
 *  代表192.168.194.1这个IP的20883端口有一个org.apache.dubbo.demo.DemoService服务，提供的方法有sayHello,sayYes
 * 如：consumer://192.168.194.2/org.apache.dubbo.demo.DemoService?appId=consumer-demo&side=consumer
 *  代表192.168.194.2这个IP有一个消费者想要订阅org.apache.dubbo.demo.DemoService服务
 */
public class URL implements Serializable {
    //协议
    private String protocol;
    private String ip;
    private int port;
    private String interfaceName;
    //协议的字符串格式
    private String rawURLString;

    //格式group/interfaceName:version
    private String serviceKey;
    //参数
    private Map<String, String> params = new HashMap<>();

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRawURLString() {
        return rawURLString;
    }

    public String getServiceKey() {
        if (serviceKey != null) {
            return serviceKey;
        }
        String inf = getInterfaceName();
        if (inf == null) {
            return null;
        }
        serviceKey = buildServiceKey(inf, getGroup(), getVersion());
        return serviceKey;
    }

    private String getGroup() {
        return params.get(Consant.GROUP_KEY);
    }

    private String getVersion() {
        return params.get(Consant.VERSION_KEY);
    }


    public static String buildServiceKey(String path, String group, String version) {
        int length = path == null ? 0 : path.length();
        length += group == null ? 0 : group.length();
        length += version == null ? 0 : version.length();
        length += 3;
        StringBuilder buf = new StringBuilder(length);
        if (StringUtil.isNullOrEmpty(group)){
            buf.append(group).append('/');
        }
        buf.append(path);
        if (StringUtil.isNullOrEmpty(version)) {
            buf.append(':').append(version);
        }
        return buf.toString().intern();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void putParams(String key,String value) {
        this.params = params;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
}
