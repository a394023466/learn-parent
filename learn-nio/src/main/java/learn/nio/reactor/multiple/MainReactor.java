package learn.nio.reactor.multiple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 主Reactor只接收Accept请求，然后把读请求和写请求交给Acceptor对象去分发
 *
 * @author: liyuzhi
 * @date: 2020/5/14 18:11
 * @version: 1
 */
public class MainReactor implements Runnable {

    private final Selector mainSelector;

    private final ServerSocketChannel serverSocketChannel;

    public MainReactor(int port, int subCount) throws IOException {
        mainSelector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));

        final SelectionKey selectionKey = serverSocketChannel.register(mainSelector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor(serverSocketChannel, subCount));
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                mainSelector.select();
                Set<SelectionKey> selectionKeys = mainSelector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    final SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        dispatch(selectionKey);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}
