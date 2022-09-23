// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.nio;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.nio.ByteBuffer;
import org.eclipse.jetty.io.Buffer;
import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import org.eclipse.jetty.io.NetworkTrafficListener;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;

public class NetworkTrafficSelectChannelEndPoint extends SelectChannelEndPoint
{
    private static final Logger LOG;
    private final List<NetworkTrafficListener> listeners;
    
    public NetworkTrafficSelectChannelEndPoint(final SocketChannel channel, final SelectorManager.SelectSet selectSet, final SelectionKey key, final int maxIdleTime, final List<NetworkTrafficListener> listeners) throws IOException {
        super(channel, selectSet, key, maxIdleTime);
        this.listeners = listeners;
    }
    
    @Override
    public int fill(final Buffer buffer) throws IOException {
        final int read = super.fill(buffer);
        this.notifyIncoming(buffer, read);
        return read;
    }
    
    @Override
    public int flush(final Buffer buffer) throws IOException {
        final int position = buffer.getIndex();
        final int written = super.flush(buffer);
        this.notifyOutgoing(buffer, position, written);
        return written;
    }
    
    @Override
    protected int gatheringFlush(final Buffer header, final ByteBuffer bbuf0, final Buffer buffer, final ByteBuffer bbuf1) throws IOException {
        final int headerPosition = header.getIndex();
        final int headerLength = header.length();
        final int bufferPosition = buffer.getIndex();
        final int written = super.gatheringFlush(header, bbuf0, buffer, bbuf1);
        this.notifyOutgoing(header, headerPosition, (written > headerLength) ? headerLength : written);
        this.notifyOutgoing(buffer, bufferPosition, (written > headerLength) ? (written - headerLength) : 0);
        return written;
    }
    
    public void notifyOpened() {
        if (this.listeners != null && !this.listeners.isEmpty()) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    listener.opened(this._socket);
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    public void notifyIncoming(final Buffer buffer, final int read) {
        if (this.listeners != null && !this.listeners.isEmpty() && read > 0) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    final Buffer view = buffer.asReadOnlyBuffer();
                    listener.incoming(this._socket, view);
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    public void notifyOutgoing(final Buffer buffer, final int position, final int written) {
        if (this.listeners != null && !this.listeners.isEmpty() && written > 0) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    final Buffer view = buffer.asReadOnlyBuffer();
                    view.setGetIndex(position);
                    view.setPutIndex(position + written);
                    listener.outgoing(this._socket, view);
                }
                catch (Exception x) {
                    NetworkTrafficSelectChannelEndPoint.LOG.warn(x);
                }
            }
        }
    }
    
    public void notifyClosed() {
        if (this.listeners != null && !this.listeners.isEmpty()) {
            for (final NetworkTrafficListener listener : this.listeners) {
                try {
                    listener.closed(this._socket);
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
