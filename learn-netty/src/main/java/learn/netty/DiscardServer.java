package learn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/19 8:53
 */
public class DiscardServer {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();
    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        //创建一个boss节点，只用于接收客户端连接请求
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //创建work节点，只用于处理客户端数据
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            //初始化服务端辅助类
            ServerBootstrap server = new ServerBootstrap();

            server.group(boss, work)
                    //指定用于接收连接的Channel
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    /**
                     * 将对客户端数据处理的处理类加入到对应Channel里的ChannelPipeline中去
                     * 一个Channel创建后都会有一个与之对应的ChannelPipeline被创建
                     * ChannelPipeline是一个管理ChannelHandler的一个对象，其实内部结构是链表结构，这个对象里存储的就是ChannelHandler对象
                     * ChannelPipeline可以动态添加、删除、替换其中的ChannelHandler，这样的机制可以提高灵活性，例如：
                     * addFirst(...)   //添加ChannelHandler在ChannelPipeline的第一个位置
                     * addBefore(...)   //在ChannelPipeline中指定的ChannelHandler名称之前添加ChannelHandler
                     * addAfter(...)   //在ChannelPipeline中指定的ChannelHandler名称之后添加ChannelHandler
                     * addLast(...)   //在ChannelPipeline的末尾添加ChannelHandler
                     * remove(...)   //删除ChannelPipeline中指定的ChannelHandler
                     * replace(...)   //替换ChannelPipeline中指定的ChannelHandler
                     */

                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            final ChannelPipeline pipeline = ch.pipeline();
//                            // Add the text line codec combination first,
//                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//                            // the encoder and decoder are static as these are sharable
//                            pipeline.addLast(DECODER);
//                            pipeline.addLast(ENCODER);
                            pipeline.addLast(new DiscardServerHandler());
                        }
                    })
                    /**
                     * ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数，
                     * 函数listen(int socketfd,int backlog)用来初始化服务端可连接队列，
                     * 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，
                     * 多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                     */
                    .option(ChannelOption.SO_BACKLOG, 128)
                    /**
                     *  Socket参数，连接保活，默认值为False。启用该功能时，TCP会主动探测空闲连接的有效性。
                     *  可以将此功能视为TCP的心跳机制，需要注意的是：默认的心跳间隔是7200s即2小时。Netty默认关闭该功能。
                     */
                    .childOption(ChannelOption.SO_KEEPALIVE, true);


            server.bind(8080).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();
        }

    }
}
