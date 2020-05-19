package learn.nio;

import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @Package learn.nio
 * @ClassName NioBufferTest
 * @Description
 * @Author liyuzhi
 * @Date 2020-05-11 16:38
 */

public class NioBufferTest {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};

        //向Buffer中写入数据
        byteBuffer.put(bytes);
        System.out.println("position==" + byteBuffer.position());
        System.out.println("limit==" + byteBuffer.limit());
        System.out.println("capacity==" + byteBuffer.capacity());
        System.out.println("----------------------------------------------------");
        byteBuffer.flip();
        byte[] bs = new byte[3];
        byteBuffer.get(bs);
        System.out.println(Arrays.toString(bs));
        byteBuffer.mark();

        byteBuffer.flip();
        System.out.println("----------------------------------------------------");
        System.out.println("position==" + byteBuffer.position());
        System.out.println("limit==" + byteBuffer.limit());
        System.out.println("capacity==" + byteBuffer.capacity());
        byteBuffer.flip();
        System.out.println("----------------------------------------------------");
        System.out.println("position==" + byteBuffer.position());
        System.out.println("limit==" + byteBuffer.limit());
        System.out.println("capacity==" + byteBuffer.capacity());
//
//        byte[] bs1= new byte[2];
//        byteBuffer.get(bs1);
//        System.out.println(Arrays.toString(bs1));
//        System.out.println("----------------------------------------------------");
//        System.out.println("position==" + byteBuffer.position());
//        System.out.println("limit==" + byteBuffer.limit());
//        System.out.println("capacity==" + byteBuffer.capacity());
//
//
//
//     byteBuffer.reset();
//        System.out.println("----------------------------------------------------");
//        System.out.println("position==" + byteBuffer.position());
//        System.out.println("limit==" + byteBuffer.limit());
//        System.out.println("capacity==" + byteBuffer.capacity());

    }

    @Test
    public void putByteAarry() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        //目标Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(bytes);

        buffer.put(bytes, 0, 2);
        printBufferIndex(buffer);
    }

    public void putBuffer() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        //目标Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(bytes);
        //源Buffer
        ByteBuffer srcBuffer = ByteBuffer.allocate(1024);
        srcBuffer.put(bytes);

        //源Buffer一定要从写入模式转换为读模式
        srcBuffer.flip();
        //将源Buffer里的数据写入到目标Buffer中去
        buffer.put(srcBuffer);
        printBufferIndex(buffer);
    }

    @Test
    public void putOffsetLength() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //从bytes数组中0的位置开始读取5个长度的数据写入ByteBuffer中
        buffer.put(bytes, 0, 5);
    }


    @Test
    public void remainingTest() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.compact();
        buffer.put(bytes, 0, 5);
        buffer.flip();
        while (buffer.hasRemaining()) {
            int remaining = buffer.remaining();

            byte b = buffer.get();
            System.out.println("当前读取的数据为" + String.valueOf(b));
            System.out.println("当前position和limit之间还有" + remaining + "个数据未读取");
        }
    }

    @Test
    public void compact() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        ByteBuffer buffer = ByteBuffer.allocate(15);
        buffer.put(bytes);
        //切换为读模式
        buffer.flip();

        while (buffer.hasRemaining()) {
            System.out.println(new String(new byte[]{buffer.get()}));
            if (buffer.position() == buffer.limit()) {
//                buffer.compact();
                buffer.clear();
                break;
            }
        }
        byte[] bs = new byte[5];

        buffer.get(bs, 0, 5);
        System.out.println(Arrays.toString(bs));
        printBufferIndex(buffer);
    }

    @Test
    public void array() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        ByteBuffer buffer = ByteBuffer.allocate(15);
        buffer.put(bytes);

        while (buffer.hasRemaining()) {
            buffer.get();
        }

        byte[] array = buffer.array();
        System.out.println(new String(array));
    }


    @Test
    public void mark() {
        byte[] bytes = {'a', 'b', 'c', 'd', 'e'};
        ByteBuffer buffer = ByteBuffer.allocate(15);
        buffer.put(bytes, 0, 2);
        //标记当前position位置
        buffer.mark();
        buffer.put(bytes, 2, 3);
        printBufferIndex(buffer);
        //还原之前标记的position位置
        buffer.reset();
        printBufferIndex(buffer);

    }

    @Test
    public void get() {
        byte[] bytes = {'e', 'a', 'b', 'c', 'd'};
        ByteBuffer buffer = ByteBuffer.allocate(15);
        buffer.put(bytes);
        buffer.flip();
        System.out.println(buffer.isReadOnly());
    }


    public void printBufferIndex(ByteBuffer buffer) {
        System.out.println("position==" + buffer.position());
        System.out.println("limit==" + buffer.limit());
        System.out.println("capacity==" + buffer.capacity());
        System.out.println("-------------------------------------------------------------");
    }
}
