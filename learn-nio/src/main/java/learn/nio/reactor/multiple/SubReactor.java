package learn.nio.reactor.multiple;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Reactor从节点，只负责接收Acceptor发送过来的事件，对客户端数据进行读写操作
 *
 * @author: liyuzhi
 * @date: 2020/5/15 14:46
 * @version: 1
 */
public class SubReactor implements Runnable {

    private ReentrantLock lock = new ReentrantLock();


    private final Selector selector;
    /**
     * 是否执行selector.select()的标志位，不让他执行说明有事件将要被注册，让他执行是有两种情况
     * 1.说明注册事件完毕，开始从选择器中选出事件进行逻辑处理，
     * 2.没有可执行的事件，让selector.select()阻塞，防止浪费CPU资源
     */
    private boolean register = true;

    public SubReactor() throws IOException {
        this.selector = SelectorProvider.provider().openSelector();
    }

    /**
     * 接收Acceptor的事件注册
     *
     * @param socketChannel
     * @throws IOException
     */
    public void register(SocketChannel socketChannel) throws IOException {
        /**
         * 在注册事件的时候首先唤醒selector.select()方法，然后设置register设置为false，
         * 设置为false以后，下边的run方法就不会执行while(register)里的selector.select()，这样就是为了防止在注册的时候
         * selector.select()处于阻塞状态，使得注册一直阻塞，造成了死锁，这样后续的客户端将全部阻塞在外边。
         */
        selector.wakeup();
        //register设置为false，作用就是不让selector.select()执行，因为将要有事件被注册
        register = false;
        socketChannel.configureBlocking(false);
        final SelectionKey selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
        selectionKey.attach(new Handler(selectionKey));
        /**
         * 恢复selector.select()方法执行，有以下两种情况
         * 1.说明注册事件完毕，开始从选择器中选出事件进行逻辑处理，
         * 2.没有可执行的事件，让selector.select()阻塞，防止浪费CPU资源
         */
        register = true;
    }

    @Override
    public void run() {
        try {

            while (!Thread.interrupted()) {

                /**
                 * 注意中的注意
                 * 当注册事件的时候，不让他执行selector.select()，因为当selector.select()阻塞的时候是不允许注册事件的
                 */
                try {
                    lock.lock();
                    while (register) {
                        selector.select();
                        final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        final Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()) {
                            final SelectionKey selectionKey = iterator.next();
                            dispatch(selectionKey);
                            iterator.remove();
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey selectionKey) throws IOException {
        Runnable runnable = (Runnable) selectionKey.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }
}
