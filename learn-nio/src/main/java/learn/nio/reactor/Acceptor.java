package learn.nio.reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author: liyuzhi
 * @date: 2020/5/14 16:59
 * @version: 1
 */
class Acceptor implements Runnable { // inner

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public Acceptor(ServerSocketChannel serverSocketChannel, Selector selector) {
        this.serverSocketChannel = serverSocketChannel;
        this.selector = selector;
    }

    public void run() {
        try {
            SocketChannel c = serverSocketChannel.accept();
            if (c != null)
                new Handler(c, selector);
        } catch (IOException ex) { /* ... */ }
    }
}
