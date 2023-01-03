package org.minidubbo.common;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtil {

    public static InetAddress getLocalAddress() {

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
