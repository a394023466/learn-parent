package learn.bio;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * @version BIO写的服务
 * @author: Lenovo
 * @date:Create：in 2020/5/6 11:21
 */
public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);

        //判断是否处于连接状态
        while (!serverSocket.isClosed()) {
            /**
             *  接收客户端的请求，如果没有请求，会一直阻塞
             *  这个Socket其实是客户端的Socket上下文
             */
            Socket socket = serverSocket.accept();
            System.out.println("服务器接收到请求功");
            //利用try-with-resources语法来自动关闭流
            try (OutputStream out = socket.getOutputStream();
                 InputStream in = socket.getInputStream();
                 DataOutputStream write = new DataOutputStream(out);
                 DataInputStream read = new DataInputStream(in)) {
                //接收客户端传输过来的数据，如果客户端没有传输过来数据，则线程会一直阻塞在当前位置
                String line;
                while ((line = read.readLine()) != null) {
                    System.out.println(line);
                }
                //System.out.println("客户端发送的请求为" + read.readLine());
                //向客户端写数据，直到数据全部写完为止
                write.writeUTF("你好，clent");
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }
}
