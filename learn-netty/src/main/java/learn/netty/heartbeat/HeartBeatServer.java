package learn.netty.heartbeat;

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
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author: Lenovo
 * @date:Create：in 2020/5/19 14:20
 */
public class HeartBeatServer {
    private static final StringDecoder DECODER = new StringDecoder();
    private static final StringEncoder ENCODER = new StringEncoder();

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
                     *
                     * ChannelPipeline是一个链表，其中存储的元素就是ChannelHandler，什么意思呢？
                     * 其实就是对Channel中的数据进行多层处理。按照ChannelPipeline中ChannelHandler的顺序
                     * 一次对Channel中的数据进行处理。例如Channel中的数据过来了，那么数据首先会被ChannelPipeline中
                     * 第一个ChannelHandler处理，处理完成以后将处理后的结果交由ChannelPipeline中第二个ChannelHandler
                     * 处理一次类推。这下应该明白了吧
                     *
                     *
                     * 将对客户端数据处理的处理类加入到对应Channel里的ChannelPipeline中去
                     * 一个Channel创建后都会有一个与之对应的ChannelPipeline被创建
                     * ChannelPipeline是一个管理ChannelHandler的一个对象，其实内部结构是链表结构，
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
                            //第一个处理Channel中数据的Handler
//                            pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
//                            //第二个处理Channel中数据的Handler，数据是上一个Handler处理后的结果
//                            pipeline.addLast(DECODER);
//                            //第三个处理Channel中数据的Handler
//                            pipeline.addLast(ENCODER);
                            //第四个处理Channel中数据的Handler，用于监控服务端与客户端的通信状态，也可以理解为心跳机制
                            pipeline.addLast("idleStateHandler", new IdleStateHandler(600, 600, 0));
                            //第五个处理Channel中数据的Handler
                            pipeline.addLast("heartBeatHandler", new HeartBeatHandler());
                            //用于积攒消息的Handler，攒够一定大小后再交由下游Handler处理
                            pipeline.addLast("messageMeetHandler", new MessageMeetHandler());
                            pipeline.addLast("ProcessHandler", new ProcessHandler());
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
