package learn.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

/**
 * @author: liyuzhi
 * @date: 2020/5/18 17:30
 * @version: 1
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    ByteBuf byteBuf;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            //表示有一段时间没有接收到数据了
            if (e.state() == IdleState.READER_IDLE) {
                ctx.close();
            }
            //表示有一定的时间没有发送数据了
            else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(0);
            }
        }
    }


    /**
     * 已经注册的Channel处于非活跃状态并且Channel生命周期结束的时候被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * 服务端出现异常会回调
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 与变为活跃状态（连接到了远程主机），可以接受和发送数据会回调这个方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * channel已经创建，但是未注册到一个EventLoop里面，也就是没有和Selector绑定，会回调这个方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * 当有一个Channel注册进来的时候的回调方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     * 接收到客户端数据会被调用
     *
     * @param ctx
     * @param msg 客户端传过来的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            byteBuf.writeBytes(buf);

            if (byteBuf.readableBytes() >= 10) {
                final ChannelFuture future = ctx.write(byteBuf);
                /**
                 * 监听服务端写数据到客户端后，客户端收到以后再与客户端断开连接，否则有的时候客户端收不到数据，有两种方式
                 * 1.future.addListener(ChannelFutureListener.CLOSE);
                 * 2.future.addListener((ChannelFutureListener) f -> {
                 *     if (f == future) ctx.close();
                 *   });
                 */
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } finally {
            buf.release();
        }
    }

    /**
     * 读取客户端数据完毕的回调方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //这个方法清空当前类中的成员变量 ByteBuf byteBuf的缓冲区，使得ByteBuf的refCount重置为1
        ctx.flush();
    }

    /**
     * 当前Handler被添加到ChannelPipeline中准备处理事件的时候被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        byteBuf = ctx.alloc().buffer(10);
    }

    /**
     * 当前Handler从ChannelPipeline被删除的时候或者不被使用的时候被调用
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        byteBuf = null;
    }
}
