package learn.netty.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 用于积攒消息的Handler，攒够一定大小的消息在交由下一个Handler去处理
 *
 * @author: liyuzhi
 * @date: 2020/5/20 11:22
 * @version: 1
 */

public class MessageMeetHandler extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) return;
        out.add(in.readBytes(in.writerIndex()));
    }
}
