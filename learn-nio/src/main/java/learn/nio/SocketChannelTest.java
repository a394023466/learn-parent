package learn.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Package learn.nio
 * @ClassName SocketChannelTest
 * @Description
 * @Author liyuzhi
 * @Date 2020-05-13 10:51
 */
public class SocketChannelTest {


    @Test
    public void initSocketChannel1() throws IOException, InterruptedException {
        for (int i = 0; i < 100; i++) {

            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(8080));
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);


            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isValid()) {
                    if (key.isConnectable()) {
                        connect(selector, key);
                    }
                    SocketChannel channel = (SocketChannel) key.channel();
                    channel.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    buffer.put(("你好服务端。我是客户端" + i).getBytes());
                    buffer.flip();
                    System.out.println("正在向服务端写数据");
                    while (buffer.hasRemaining()) {
                        channel.write(buffer);
                    }
                    System.out.println("向服务端写数据完成");
                }
            }

        }
    }

    @Test
    public void initSocketChannel() throws IOException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            boolean stop = true;
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress(8080));
            Selector selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            while (stop) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid()) {
                        if (key.isConnectable()) {
                            connect(selector, key);
                        } else if (key.isReadable()) {
                            read(key);
                            stop = false;
                        } else if (key.isWritable()) {
                            writeToSercer(key);
                        }
                    }
                }
            }

        }

    }

    @Test
    public void initServerSocketChannel() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();

                if (key.isValid()) {
                    if (key.isAcceptable()) {
                        accept(selector, key);
                    } else if (key.isReadable()) {
                        SocketChannel socketChannel = read(key);
                        socketChannel.register(selector, SelectionKey.OP_WRITE);
                    } else if (key.isWritable()) {
                        writeToClinet(key);
                    }
                }

            }
        }
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = ssc.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }


    private void connect(Selector selector, SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        sc.configureBlocking(false);
        if (sc.isConnectionPending()) {
            sc.finishConnect();
        }

        sc.register(selector, SelectionKey.OP_WRITE);
    }

    private void writeToSercer(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("你好服务端。我是客户端".getBytes());
        buffer.flip();

        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        key.interestOps((key.interestOps() & (~SelectionKey.OP_WRITE))|SelectionKey.OP_READ);

    }

    private SocketChannel read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (channel.read(buffer) > 0) {
            buffer.flip();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            System.out.println(new String(bytes));
            buffer.clear();
        }
        return channel;
    }


    private void writeToClinet(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put("客户端你好，我是服务端".getBytes());
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        //将原来具有的事件再次注册，但是取消写事件
        key.interestOps(key.interestOps() & (~SelectionKey.OP_WRITE));
    }
}