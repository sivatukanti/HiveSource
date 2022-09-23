// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io;

import org.eclipse.jetty.util.log.Log;
import java.net.Socket;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.eclipse.jetty.util.thread.Scheduler;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class NetworkTrafficSelectChannelEndPoint extends SelectChannelEndPoint
{
    private static final Logger LOG;
    private final List<NetworkTrafficListener> listeners;
    
    public NetworkTrafficSelectChannelEndPoint(final SocketChannel channel, final ManagedSelector selectSet, final SelectionKey key, final Scheduler scheduler, final long idleTimeout, final List<NetworkTrafficListener> listeners) throws IOException {
        super(channel, selectSet, key, scheduler, idleTimeout);
        this.listeners = listeners;
    }
    
    @Override
    public int fill(final ByteBuffer buffer) throws IOException {
        final int read = super.fill(buffer);
        this.notifyIncoming(buffer, read);
        return read;
    }
    
    @Override
    public boolean flush(final ByteBuffer... buffers) throws IOException {
        boolean flushed = true;
        for (final ByteBuffer b : buffers) {
            if (b.hasRemaining()) {
                final int position = b.position();
                final ByteBuffer view = b.slice();
                flushed &= super.flush(b);
                final int l = b.position() - position;
                view.limit(view.position() + l);
                this.notifyOutgoing(view);
                if (!flushed) {
                    break;
                }
            }
        }
        return flushed;
    }
    
    @Override
    public void onOpen() {
        super.onOpen();
        if (this.listeners != null && !this.listeners.isEmpty()) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    listener.opened(this.getSocket());
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    @Override
    public void onClose() {
        super.onClose();
        if (this.listeners != null && !this.listeners.isEmpty()) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    listener.closed(this.getSocket());
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    public void notifyIncoming(final ByteBuffer buffer, final int read) {
        if (this.listeners != null && !this.listeners.isEmpty() && read > 0) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    final ByteBuffer view = buffer.asReadOnlyBuffer();
                    listener.incoming(this.getSocket(), view);
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    public void notifyOutgoing(final ByteBuffer view) {
        if (this.listeners != null && !this.listeners.isEmpty() && view.hasRemaining()) {
            final Socket socket = this.getSocket();
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    listener.outgoing(socket, view);
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    static {
        LOG = Log.getLogger(NetworkTrafficSelectChannelEndPoint.class);
    }
}
