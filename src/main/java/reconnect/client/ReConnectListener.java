package reconnect.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 2。 创建监听类，实现ChannelFutureListener，在此类中发起重连
 */
public class ReConnectListener implements ChannelFutureListener {

    private AppClientReConnect clientReConnect;

    ReConnectListener(AppClientReConnect clientReConnect) {
        this.clientReConnect = clientReConnect;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (!future.isSuccess()) {
            EventLoop eventLoop = future.channel().eventLoop();
            ScheduledFuture<?> schedule = eventLoop.schedule(() -> {
                try {
                    System.out.println("正在重连服务器。。。");
                    clientReConnect.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 5, TimeUnit.SECONDS);

        }
    }
}
