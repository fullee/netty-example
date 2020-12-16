package helloworld.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class AppClientHello {

    private int port;
    private String host;

    AppClientHello(String host, int port) {
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
                            ch.pipeline().addLast(new HandlerClientHello());
                        }
                    })
                    .remoteAddress(host, port);
            ChannelFuture future = bootstrap.connect().sync();
            future.channel().writeAndFlush(Unpooled.copiedBuffer("你好服务器呀", CharsetUtil.UTF_8));

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception{
        new AppClientHello("127.0.0.1", 8090).run();
    }

}
