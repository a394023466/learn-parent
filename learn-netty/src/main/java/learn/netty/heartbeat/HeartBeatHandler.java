package learn.netty.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 用于监控服务端与客户端的通信Handler
 *
 * @author: liyuzhi
 * @date: 2020/5/20 10:31
 * @version: 1
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    /**
     * 用户自动的触发回调，常用于心跳机制，
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            //服务端已经有一段时间没有接收到客户端的数据了
            if (e.state() == IdleState.READER_IDLE) {
                System.out.println("客户端已经10秒没有向服务器发送数据了");
                //断开与客户端的连接
                ctx.close();
            }
            //有一段时间没有发送数据了
            else if (e.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(0);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //这个方法的功能是将消息传递给下一个Handler处理器去处理
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
}
