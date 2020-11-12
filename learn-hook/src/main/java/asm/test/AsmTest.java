package asm.test;

import java.io.File;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: Lenovo
 * @date:Create：in 2020/11/11 12:05
 */
public class AsmTest {



    public static void main(String[] args) throws UnknownHostException {
        InetAddress[] localhosts = Inet4Address.getAllByName("localhost");
        for (InetAddress localhost : localhosts) {
            final String hostName = localhost.getHostAddress();
            System.out.println(hostName);
        }
    }
}
