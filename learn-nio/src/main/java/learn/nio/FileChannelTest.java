package learn.nio;


import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Package learn.nio
 * @ClassName FileChannelTest
 * @Description
 * @Author liyuzhi
 * @Date 2020-05-12 17:32
 */

public class FileChannelTest {

    @Test
    public void initChannel() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("pom.xml"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("pom1.xml"), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        while (inChannel.read(buffer) != -1) {
            buffer.flip();
            outChannel.write(buffer);
            buffer.clear();
        }
        inChannel.close();
        outChannel.close();
    }

    @Test
    public void fileChannelSize() throws IOException {
        FileChannel inChannel = FileChannel.open(Paths.get("pom.xml"), StandardOpenOption.READ);
        ByteBuffer byteBuffer = ByteBuffer.allocate(500);
        //channel里的byte数据长度,打印为506
        System.out.println(inChannel.size());
        inChannel.read(byteBuffer);
        //channel中position的位置，打印为500
        System.out.println(inChannel.position());
        inChannel.close();
    }
}
