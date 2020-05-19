package learn.bio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/6 17:58
 */
public class BIOTest {

    public static void main(String[] args) throws IOException {

        InetAddress inetAddress = InetAddress.getByName("localhost");


        Socket socket = new Socket("locahost", 8080, inetAddress, 8080);

    }


}
