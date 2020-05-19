package learn.nio.reactor;

import java.io.IOException;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/14 13:50
 */
public class NIOServer {

    public static void main(String[] args) {

        try {
            //创建一个Reactor，用于分发客户端请求到不同的处理引擎中
            Reactor reactor = new Reactor(8080);
            reactor.run();
        } catch (IOException e) {
            e.printStackTrace();
            //System.exit(1);
        }
    }
}
