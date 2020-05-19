package learn.nio.reactor.multiple;

import java.io.IOException;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/15 11:07
 */
public class NioReactorServer {

    public static void main(String[] args) throws IOException {
        MainReactor mainReactor = new MainReactor(8080, 2);
        mainReactor.run();
    }
}
