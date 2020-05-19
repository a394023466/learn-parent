package learn.nio.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/14 10:48
 */
public class Handler implements Runnable {
    /**
     * 创建线程池，这个线程池用于处理客户端与服务端的读写事件
     */
    ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private SocketChannel socket;

    private SelectionKey sk;


    private ByteBuffer input = ByteBuffer.allocate(1024 * 1024 * 64);
    private ByteBuffer output = ByteBuffer.allocate(1024 * 1024 * 64);

    //具体执行写或者读的标志位
    private volatile int READING = 0, SENDING = 1;
    //判断当前正在处于读状态或者是写状态
    private volatile int state = READING;


    public Handler(SocketChannel c, Selector selector) throws IOException {
        socket = c;
        socket.configureBlocking(false);
        //注册一个事件，作用就是初始换SelectionKey，当前时间并没有实际感兴趣的时间
        sk = socket.register(selector, 0);

        /**
         * 同理在创建Acceptor对象的时候，需要提前向Selector中注册读事件，在注册读事件后也可以获取到读事件的SelectionKey
         * 拿到这SelectionKey以后也可以把用于处理客户端请求的ReadHandler对象绑定到SelectionKey上，当我们在轮询监控Selector的时候
         * 获取到了该SelectionKey，那么就可以从这SelectionKey中获取到用于处理客户端请求的ReadHandler对象，然后执行ReadHandler对象
         * 用于处理读取数据的逻辑
         */
        sk.attach(this);
        /**
         * 向SelectionKey中注册读事件为感兴趣事件,能够执行这个方法是因为上边调用了
         * socket.register(selector, 0)初始化了SelectionKey
         */
        sk.interestOps(SelectionKey.OP_READ);

        //唤醒正在阻塞的select.select()方法
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            if (state == READING) read();
            if (state == SENDING) send();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send() {
        //从线程池中拿出一个线程来执行向客户端写数据
        executorService.submit(new Thread(this::sendProcess));
        //将写事件从Selector中注销掉，注意多线程处理情况下，这个方法一定要写在多线程外边，具体原理可能就是线程可见性的问题吧
        sk.interestOps(sk.interestOps() & (~SelectionKey.OP_WRITE));
        sk.cancel();
    }

    private synchronized void sendProcess() {
        try {
            output.put("你好客户端，我是服务端".getBytes());
            output.flip();
            while (output.hasRemaining()) {
                socket.write(output);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read() {
        LockSupport.park();
        //从线程池中拿出一个线程来执行读取客户端发过来的数据
        executorService.submit(new Thread(this::readProcess));
        //注册写事件，这个写的事件一定不要写在多线程里边，可能是由于线程可见性的问题吧
        sk.interestOps(SelectionKey.OP_WRITE);
        state = SENDING;
        input.clear();
    }

    private synchronized void readProcess() {
        try {
            socket.read(input);
            if (completeRead()) {
                input.flip();
                byte[] bytes = new byte[input.remaining()];
                input.get(bytes);
                System.out.println(new String(bytes));
            } else {
                readProcess();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean completeRead() throws IOException {
        //-1：说明读取完了，说明已经读取完了
        return socket.read(input) == 0;
    }
}
