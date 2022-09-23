// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.nio;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.net.ConnectException;
import org.jboss.netty.channel.ConnectTimeoutException;
import java.util.Iterator;
import org.jboss.netty.channel.Channels;
import java.nio.channels.SelectionKey;
import java.util.Set;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.ThreadRenamingRunnable;
import java.nio.channels.Selector;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.ThreadNameDeterminer;
import java.util.concurrent.Executor;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public final class NioClientBoss extends AbstractNioSelector implements Boss
{
    private final TimerTask wakeupTask;
    private final Timer timer;
    
    NioClientBoss(final Executor bossExecutor, final Timer timer, final ThreadNameDeterminer determiner) {
        super(bossExecutor, determiner);
        this.wakeupTask = new TimerTask() {
            public void run(final Timeout timeout) throws Exception {
                final Selector selector = NioClientBoss.this.selector;
                if (selector != null && NioClientBoss.this.wakenUp.compareAndSet(false, true)) {
                    selector.wakeup();
                }
            }
        };
        this.timer = timer;
    }
    
    @Override
    protected ThreadRenamingRunnable newThreadRenamingRunnable(final int id, final ThreadNameDeterminer determiner) {
        return new ThreadRenamingRunnable(this, "New I/O boss #" + id, determiner);
    }
    
    @Override
    protected Runnable createRegisterTask(final Channel channel, final ChannelFuture future) {
        return new RegisterTask(this, (NioClientSocketChannel)channel);
    }
    
    @Override
    protected void process(final Selector selector) {
        this.processSelectedKeys(selector.selectedKeys());
        final long currentTimeNanos = System.nanoTime();
        processConnectTimeout(selector.keys(), currentTimeNanos);
    }
    
    private void processSelectedKeys(final Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        final Iterator<SelectionKey> i = selectedKeys.iterator();
        while (i.hasNext()) {
            final SelectionKey k = i.next();
            i.remove();
            if (!k.isValid()) {
                this.close(k);
            }
            else {
                try {
                    if (!k.isConnectable()) {
                        continue;
                    }
                    connect(k);
                }
                catch (Throwable t) {
                    final NioClientSocketChannel ch = (NioClientSocketChannel)k.attachment();
                    ch.connectFuture.setFailure(t);
                    Channels.fireExceptionCaught(ch, t);
                    k.cancel();
                    ch.worker.close(ch, Channels.succeededFuture(ch));
                }
            }
        }
    }
    
    private static void processConnectTimeout(final Set<SelectionKey> keys, final long currentTimeNanos) {
        for (final SelectionKey k : keys) {
            if (!k.isValid()) {
                continue;
            }
            final NioClientSocketChannel ch = (NioClientSocketChannel)k.attachment();
            if (ch.connectDeadlineNanos <= 0L || currentTimeNanos < ch.connectDeadlineNanos) {
                continue;
            }
            final ConnectException cause = new ConnectTimeoutException("connection timed out: " + ch.requestedRemoteAddress);
            ch.connectFuture.setFailure(cause);
            Channels.fireExceptionCaught(ch, cause);
            ch.worker.close(ch, Channels.succeededFuture(ch));
        }
    }
    
    private static void connect(final SelectionKey k) throws IOException {
        final NioClientSocketChannel ch = (NioClientSocketChannel)k.attachment();
        try {
            if (((SocketChannel)ch.channel).finishConnect()) {
                k.cancel();
                if (ch.timoutTimer != null) {
                    ch.timoutTimer.cancel();
                }
                ch.worker.register(ch, ch.connectFuture);
            }
        }
        catch (ConnectException e) {
            final ConnectException newE = new ConnectException(e.getMessage() + ": " + ch.requestedRemoteAddress);
            newE.setStackTrace(e.getStackTrace());
            throw newE;
        }
    }
    
    @Override
    protected void close(final SelectionKey k) {
        final NioClientSocketChannel ch = (NioClientSocketChannel)k.attachment();
        ch.worker.close(ch, Channels.succeededFuture(ch));
    }
    
    private final class RegisterTask implements Runnable
    {
        private final NioClientBoss boss;
        private final NioClientSocketChannel channel;
        
        RegisterTask(final NioClientBoss boss, final NioClientSocketChannel channel) {
            this.boss = boss;
            this.channel = channel;
        }
        
        public void run() {
            final int timeout = this.channel.getConfig().getConnectTimeoutMillis();
            if (timeout > 0 && !this.channel.isConnected()) {
                this.channel.timoutTimer = NioClientBoss.this.timer.newTimeout(NioClientBoss.this.wakeupTask, timeout, TimeUnit.MILLISECONDS);
            }
            try {
                ((SocketChannel)this.channel.channel).register(this.boss.selector, 8, this.channel);
            }
            catch (ClosedChannelException e) {
                this.channel.worker.close(this.channel, Channels.succeededFuture(this.channel));
            }
            final int connectTimeout = this.channel.getConfig().getConnectTimeoutMillis();
            if (connectTimeout > 0) {
                this.channel.connectDeadlineNanos = System.nanoTime() + connectTimeout * 1000000L;
            }
        }
    }
}
