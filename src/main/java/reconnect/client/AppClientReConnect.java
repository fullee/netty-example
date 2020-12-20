package reconnect.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppClientReConnect {

    private final int port;
    private final String host;

    public AppClientReConnect(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new HandlerClientReConnect());
                        }
                    })
                    .remoteAddress(host, port);
            // 1。 客户端端链接由同步阻塞改为异步阻塞
            ChannelFuture future = bootstrap.connect();
            future.channel().writeAndFlush(Unpooled.copiedBuffer("你好服务器", CharsetUtil.UTF_8));
            // 3。 在ChannelFuture实例中增加监听，添加第二步的监听类
            future.addListener(new ReConnectListener(this));

            // 循环发送数据包
            for (int i = 0; i < 60; i++) {
                TimeUnit.SECONDS.sleep(3);
//                TimeUnit.SECONDS.sleep(6);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss SSS");
                future.channel().writeAndFlush(Unpooled.copiedBuffer("客户端发出数据包：" + simpleDateFormat.format(new Date())+"\r\n", CharsetUtil.UTF_8));
            }

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{
        new AppClientReConnect("192.168.101.146", 8090).run();
    }

}
