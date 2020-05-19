package learn.nio.reactor.multiple;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 对客户端数据的读写处理类
 *
 * @author: liyuzhi
 * @date: 2020/5/15 18:19
 * @version: 1
 */
public class Handler implements Runnable {


    final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final SelectionKey selectionKey;

    public Handler(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }


    //具体执行写或者读的标志位
    private volatile int READING = 0, SENDING = 1;
    //判断当前正在处于读状态或者是写状态
    private volatile int state = READING;

    @Override
    public void run() {
        if (state == READING) read();
        if (state == SENDING) write();
    }

    public void read() {
        /**
         * 利用多线程来读取客户端的数据
         */
        service.submit(() -> {
            try {
                SocketChannel channel = (SocketChannel) selectionKey.channel();

                if (selectionKey.isReadable()) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    while (channel.read(buffer) > 0) {
                        buffer.flip();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        System.out.println(new String(bytes));
                        buffer.clear();
                    }
                }
            } catch (IOException e) {

            }
        });
        //读取客户端数据状态切换为向客户端写数据状态
        state = SENDING;
        //注册写事件
        selectionKey.interestOps(SelectionKey.OP_WRITE);

    }

    public void write() {
        /**
         * 利用多线程来向客户端写数据
         */
        service.submit(() -> {
            try {
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                buffer.put("你好客户端，我是服务端".getBytes());
                buffer.flip();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
            } catch (IOException e) {

            }
        });
        //向客户端写数据状态切换为读取客户端数据状态
        state = READING;
        //从selectionKey中取消写数据事件
        selectionKey.interestOps(selectionKey.interestOps() & (~SelectionKey.OP_WRITE));
    }
}
