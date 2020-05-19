package learn.nio.reactor.multiple;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/15 9:58
 */
public class Acceptor implements Runnable {

    private ReentrantLock lock = new ReentrantLock();

    private final ServerSocketChannel serverSocketChannel;
    private final int subCount;

    private SubReactor[] subReactors;

    private int next = 0;

    public Acceptor(ServerSocketChannel c, int subCount) throws IOException {
        this.serverSocketChannel = c;
        this.subCount = subCount;
        if (subReactors == null) {
            //创建subCount长度的SubReactor数组，用于存放从Reactor
            subReactors = new SubReactor[subCount];
        }
        //初始化subCount个从Reactor
        for (int i = 0; i < subCount; i++) {
            subReactors[i] = new SubReactor();
        }
    }

    @Override
    public void run() {

        try {

            final SocketChannel socketChannel = serverSocketChannel.accept();
            if (socketChannel != null) {
                socketChannel.configureBlocking(false);
                try {
                    lock.lock();
                    //在从Reactor数组中获取一个从Reactor用于处理客户端的读写请求
                    SubReactor subReactor = subReactors[next];
                    //首先让向从Reactor中注册客户端请求事件
                    subReactor.register(socketChannel);
                    //多线程去处理客户端的读写请求
                    new Thread(subReactors[next]).start();
                    if (++next == subCount) {
                        next = 0;
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
