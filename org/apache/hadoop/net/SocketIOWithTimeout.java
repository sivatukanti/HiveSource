// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.util.Iterator;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.util.LinkedList;
import java.nio.channels.SelectionKey;
import java.io.InterruptedIOException;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.Time;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import org.slf4j.Logger;

abstract class SocketIOWithTimeout
{
    static final Logger LOG;
    private SelectableChannel channel;
    private long timeout;
    private boolean closed;
    private static SelectorPool selector;
    
    SocketIOWithTimeout(final SelectableChannel channel, final long timeout) throws IOException {
        this.closed = false;
        checkChannelValidity(channel);
        this.channel = channel;
        this.timeout = timeout;
        channel.configureBlocking(false);
    }
    
    void close() {
        this.closed = true;
    }
    
    boolean isOpen() {
        return !this.closed && this.channel.isOpen();
    }
    
    SelectableChannel getChannel() {
        return this.channel;
    }
    
    static void checkChannelValidity(final Object channel) throws IOException {
        if (channel == null) {
            throw new IOException("Channel is null. Check how the channel or socket is created.");
        }
        if (!(channel instanceof SelectableChannel)) {
            throw new IOException("Channel should be a SelectableChannel");
        }
    }
    
    abstract int performIO(final ByteBuffer p0) throws IOException;
    
    int doIO(final ByteBuffer buf, final int ops) throws IOException {
        if (!buf.hasRemaining()) {
            throw new IllegalArgumentException("Buffer has no data left.");
        }
        while (buf.hasRemaining()) {
            if (this.closed) {
                return -1;
            }
            try {
                final int n = this.performIO(buf);
                if (n != 0) {
                    return n;
                }
            }
            catch (IOException e) {
                if (!this.channel.isOpen()) {
                    this.closed = true;
                }
                throw e;
            }
            int count = 0;
            try {
                count = SocketIOWithTimeout.selector.select(this.channel, ops, this.timeout);
            }
            catch (IOException e2) {
                this.closed = true;
                throw e2;
            }
            if (count == 0) {
                throw new SocketTimeoutException(timeoutExceptionString(this.channel, this.timeout, ops));
            }
        }
        return 0;
    }
    
    static void connect(final SocketChannel channel, final SocketAddress endpoint, final int timeout) throws IOException {
        final boolean blockingOn = channel.isBlocking();
        if (blockingOn) {
            channel.configureBlocking(false);
        }
        try {
            if (channel.connect(endpoint)) {
                return;
            }
            long timeoutLeft = timeout;
            final long endTime = (timeout > 0) ? (Time.now() + timeout) : 0L;
            while (true) {
                final int ret = SocketIOWithTimeout.selector.select(channel, 8, timeoutLeft);
                if (ret > 0 && channel.finishConnect()) {
                    return;
                }
                if (ret == 0 || (timeout > 0 && (timeoutLeft = endTime - Time.now()) <= 0L)) {
                    throw new SocketTimeoutException(timeoutExceptionString(channel, timeout, 8));
                }
            }
        }
        catch (IOException e) {
            try {
                channel.close();
            }
            catch (IOException ex) {}
            throw e;
        }
        finally {
            if (blockingOn && channel.isOpen()) {
                channel.configureBlocking(true);
            }
        }
    }
    
    void waitForIO(final int ops) throws IOException {
        if (SocketIOWithTimeout.selector.select(this.channel, ops, this.timeout) == 0) {
            throw new SocketTimeoutException(timeoutExceptionString(this.channel, this.timeout, ops));
        }
    }
    
    public void setTimeout(final long timeoutMs) {
        this.timeout = timeoutMs;
    }
    
    private static String timeoutExceptionString(final SelectableChannel channel, final long timeout, final int ops) {
        String waitingFor = null;
        switch (ops) {
            case 1: {
                waitingFor = "read";
                break;
            }
            case 4: {
                waitingFor = "write";
                break;
            }
            case 8: {
                waitingFor = "connect";
                break;
            }
            default: {
                waitingFor = "" + ops;
                break;
            }
        }
        return timeout + " millis timeout while waiting for channel to be ready for " + waitingFor + ". ch : " + channel;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SocketIOWithTimeout.class);
        SocketIOWithTimeout.selector = new SelectorPool();
    }
    
    private static class SelectorPool
    {
        private static final long IDLE_TIMEOUT = 10000L;
        private ProviderInfo providerList;
        
        private SelectorPool() {
            this.providerList = null;
        }
        
        int select(final SelectableChannel channel, final int ops, long timeout) throws IOException {
            final SelectorInfo info = this.get(channel);
            SelectionKey key = null;
            int ret = 0;
            try {
                while (true) {
                    final long start = (timeout == 0L) ? 0L : Time.now();
                    key = channel.register(info.selector, ops);
                    ret = info.selector.select(timeout);
                    if (ret != 0) {
                        return ret;
                    }
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedIOException("Interrupted while waiting for IO on channel " + channel + ". " + timeout + " millis timeout left.");
                    }
                    if (timeout <= 0L) {
                        continue;
                    }
                    timeout -= Time.now() - start;
                    if (timeout <= 0L) {
                        return 0;
                    }
                }
            }
            finally {
                if (key != null) {
                    key.cancel();
                }
                try {
                    info.selector.selectNow();
                }
                catch (IOException e) {
                    SocketIOWithTimeout.LOG.info("Unexpected Exception while clearing selector : ", e);
                    info.close();
                    return ret;
                }
                this.release(info);
            }
        }
        
        private synchronized SelectorInfo get(final SelectableChannel channel) throws IOException {
            SelectorInfo selInfo = null;
            SelectorProvider provider;
            ProviderInfo pList;
            for (provider = channel.provider(), pList = this.providerList; pList != null && pList.provider != provider; pList = pList.next) {}
            if (pList == null) {
                pList = new ProviderInfo();
                pList.provider = provider;
                pList.queue = new LinkedList<SelectorInfo>();
                pList.next = this.providerList;
                this.providerList = pList;
            }
            final LinkedList<SelectorInfo> queue = pList.queue;
            if (queue.isEmpty()) {
                final Selector selector = provider.openSelector();
                selInfo = new SelectorInfo();
                selInfo.selector = selector;
                selInfo.queue = queue;
            }
            else {
                selInfo = queue.removeLast();
            }
            this.trimIdleSelectors(Time.now());
            return selInfo;
        }
        
        private synchronized void release(final SelectorInfo info) {
            final long now = Time.now();
            this.trimIdleSelectors(now);
            info.lastActivityTime = now;
            info.queue.addLast(info);
        }
        
        private void trimIdleSelectors(final long now) {
            final long cutoff = now - 10000L;
            for (ProviderInfo pList = this.providerList; pList != null; pList = pList.next) {
                if (!pList.queue.isEmpty()) {
                    final Iterator<SelectorInfo> it = pList.queue.iterator();
                    while (it.hasNext()) {
                        final SelectorInfo info = it.next();
                        if (info.lastActivityTime > cutoff) {
                            break;
                        }
                        it.remove();
                        info.close();
                    }
                }
            }
        }
        
        private static class SelectorInfo
        {
            Selector selector;
            long lastActivityTime;
            LinkedList<SelectorInfo> queue;
            
            void close() {
                if (this.selector != null) {
                    try {
                        this.selector.close();
                    }
                    catch (IOException e) {
                        SocketIOWithTimeout.LOG.warn("Unexpected exception while closing selector : ", e);
                    }
                }
            }
        }
        
        private static class ProviderInfo
        {
            SelectorProvider provider;
            LinkedList<SelectorInfo> queue;
            ProviderInfo next;
        }
    }
}
