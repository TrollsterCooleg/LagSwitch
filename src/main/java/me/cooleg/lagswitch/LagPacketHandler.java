package me.cooleg.lagswitch;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;

import java.util.concurrent.TimeUnit;

public class LagPacketHandler extends ChannelDuplexHandler {

    private long delay = 0;
    private long desiredDelay;
    private boolean decreasingDelay = false;
    private long previousMillis;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (delay <= 0) {super.channelRead(ctx, msg); return;}

        handleDecreasingDelay();
        EventExecutor executor = ctx.executor();

        executor.schedule(() -> {
            try {
                delayedRead(ctx, msg);
            } catch (Exception ex) {
                LagSwitch.LOGGER.error("Error occurred in packet read!", ex);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public void delayedRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        if (ctx.isRemoved()) return;
        super.channelRead(ctx, obj);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (delay <= 0) {super.write(ctx, msg, promise); return;}

        handleDecreasingDelay();
        EventExecutor executor = ctx.executor();

        executor.schedule(() -> {
            try {
                delayedWrite(ctx, msg, promise);
            } catch (Exception ex) {
                LagSwitch.LOGGER.error("Error occurred in packet write!", ex);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public void delayedWrite(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
        if (ctx.isRemoved()) return;
        super.write(ctx, obj, promise);
    }

    private void handleDecreasingDelay() {
        if (decreasingDelay) {
            long currentTime = System.currentTimeMillis();
            if (currentTime > previousMillis+1) {
                delay -= (currentTime - previousMillis) - 1;
                if (delay <= desiredDelay) {
                    delay = desiredDelay;
                    decreasingDelay = false;
                }
            }
            previousMillis = currentTime;
        }
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }

    public void setDecreasingDelay(long desiredDelay) {
        this.decreasingDelay = true;
        this.previousMillis = System.currentTimeMillis();
        this.desiredDelay = desiredDelay;
    }
}
