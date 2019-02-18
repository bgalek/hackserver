package pl.allegro.tech.leaders.hackathon.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetUtils {
    public static InetAddress fromString(String remoteAddr) {
        try {
            return InetAddress.getByName(remoteAddr);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
