package reconnect.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 笔记：
 * 1）超时种类：读超时，写超时，读写超时
 * 超时检测：
 * 1。重写Handler方法userEventTriggered方法
 * 2。pipeline添加IdleStateHandler
 *
 * 2）心跳机制
 * 使用超时检测实现心跳会产生心率不齐
 *
 * 3）断线重连
 * 客户端处理：服务器端未启动或者崩溃，客户端应该不断重试，而不是直接退出。
 * 服务端处理：服务器检测到客户端断开，自动重连
 * 1。 客户端端链接由同步阻塞改为异步阻塞
 * 2。 创建监听类，实现ChannelFutureListener，在此类中发起重连
 * 3。 在ChannelFuture实例中增加监听，添加第二步的监听类
 *
 *
 */
public class AppServerReConnect {

    private final int port;

    AppServerReConnect(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HandlerServerReConnect());
                        }
                    })
                    .localAddress(port);
            ChannelFuture future = serverBootstrap.bind().sync();
//            future.channel().writeAndFlush(Unpooled.copiedBuffer("你好", CharsetUtil.UTF_8));
            System.out.println("服务端启动成功："+future.channel().localAddress());

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws Exception {
        new AppServerReConnect(8090).run();
    }

}
