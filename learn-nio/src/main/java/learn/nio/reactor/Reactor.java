package learn.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO服务器反应器，用于调度客户端不同的请求，例如accept请求、read请求、write请求。
 * 这些请求都会被reactor分发到不同的执行引擎中去执行。
 *
 * @author: liyuzhi
 * @date: 2020/5/14 17:03
 * @version: 1
 */
public class Reactor implements Runnable {

    private Selector selector;

    private ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        this.selector = Selector.open();
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        //创建Reactor对象的时候立即注册客户端连接事件
        final SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        /**
         * 上边我们把客户端连接请求注册到了Selector中了，并获取到了selectionKey。
         * 我们可以把用于处理客户端请求的Acceptor对象绑定到这个SelectionKey上，当我们在轮询监控Selector的时候
         * 获取到了该SelectionKey，那么就可以从这SelectionKey中获取到Acceptor对象，然后执行Acceptor对象力用于处理客户端连接请求的
         * 逻辑了。
         * 同理在创建Acceptor对象的时候，需要提前向Selector中注册读事件，在注册读事件后也可以获取到读事件的SelectionKey
         * 拿到这SelectionKey以后也可以把用于处理客户端请求的ReadHandler对象绑定到SelectionKey上，当我们在轮询监控Selector的时候
         * 获取到了该SelectionKey，那么就可以从这SelectionKey中获取到用于处理客户端请求的ReadHandler对象，然后执行ReadHandler对象
         * 用于处理读取数据的逻辑
         */
        selectionKey.attach(new Acceptor(serverSocketChannel, selector));
    }


    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                /**
                 * 如果没有事件则一直阻塞，如果有事件过来了则会继续执行
                 */
                selector.select();
                final Set<SelectionKey> selected = selector.selectedKeys();
                final Iterator<SelectionKey> keyIterator = selected.iterator();
                while (keyIterator.hasNext()) {
                    final SelectionKey selectionKey = keyIterator.next();
                    dispatch(selectionKey);

                    selected.clear();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        //从SelectionKey对象中拿出之前放进去的Acceptor对象，因为Acceptor对象实现了Runnable接口，所以可以强转为Runnable
        Runnable runnable = (Runnable) (selectionKey.attachment());
        if (null != runnable) {
            //调用run方法可是检测客户端连接事件
            System.out.println("111111111");
            runnable.run();
        }
    }
}
