package reconnect.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class HandlerClientReConnect extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("服务器传来消息：" + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通道链接断开
        Channel channel = ctx.channel();
        System.out.println("与服务器链路中断" + channel.remoteAddress());

        // 服务端正常，但链路中断，自动重连
        new AppClientReConnect("127.0.0.1", 8090).run();
    }

    // 1）超时
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("客户端读超时");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
                ctx.channel().writeAndFlush(Unpooled.copiedBuffer("客户端发出读超时心跳包：" + simpleDateFormat.format(new Date()) + "\r\n", CharsetUtil.UTF_8));

            }
        }
    }
}
