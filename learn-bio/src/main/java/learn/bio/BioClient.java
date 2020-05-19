package learn.bio;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/6 11:32
 */
public class BioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        try (AA a = new AA()){
            System.out.println("bbbb");
        }catch (Exception e){

        }

    }

    static class AA implements AutoCloseable{

        @Override
        public void close() throws Exception {
            System.out.println("aaaaaaaaaaa");
        }
    }


    private static void SocketTest2() throws IOException, InterruptedException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(InetAddress.getByName("localhost"), 8080), 2000);
        final OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.write("123456789".getBytes());
        socket.shutdownOutput();

        dataOutputStream.write("123456789".getBytes());
        Thread.sleep(Integer.MAX_VALUE);

    }

    private static void SocketClient1() throws IOException {
        //创建Socket Client
        Socket socket = new Socket("localhost", 8080);
        //利用try-with-resources语法来自动关闭流
        try (OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream();
             DataOutputStream write = new DataOutputStream(out);
             DataInputStream read = new DataInputStream(in)) {
            //服务端写数据，直到数据全部写完
            write.writeUTF("你好，服务器");
            //读取服务器返回回来的数据，如果服务器没有写数据过来，当前线程会阻塞在这地方
            System.out.println("服务器发送过来的消息是" + read.readLine());
            System.out.println("服务器发送过来的消息是" + read.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.close();
    }

/**
 * 如果服务端开启了socket.setOOBInline(true),打印结果为A123456789
 * 如果服务端开启了socket.setOOBInline(false),打印结果为123456789
 * 不开启那么紧急数据服务端不会读取到，开启的话服务端会读取到紧急数据
 */
}

