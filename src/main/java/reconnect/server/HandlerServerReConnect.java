package reconnect.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

@ChannelHandler.Sharable
public class HandlerServerReConnect extends ChannelInboundHandlerAdapter {

    ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);

        ByteBuf in = (ByteBuf) msg;
        System.out.println(ctx.channel().remoteAddress()+"客户端发来消息："+in.toString(CharsetUtil.UTF_8));
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好客户端", CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("客户端："+channel.remoteAddress()+"在线");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
        channel.writeAndFlush(Unpooled.copiedBuffer("服务器端发送消息：" + simpleDateFormat.format(new Date())+"\r\n", CharsetUtil.UTF_8));


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 通道处于非活动状态时调用，此方法只会在通道建立时调用一次
        Channel channel = ctx.channel();
        System.out.println("客户端掉线："+channel.remoteAddress());

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 建立新连接时触发
        Channel channel = ctx.channel();
        System.out.println("新连接：" + channel.remoteAddress());
        channels.add(channel);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("客户端断开："+channel.remoteAddress());
        channels.remove(channel);
    }
}
