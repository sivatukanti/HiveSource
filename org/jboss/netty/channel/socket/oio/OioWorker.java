// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import java.nio.channels.WritableByteChannel;
import java.io.OutputStream;
import java.net.SocketException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.DefaultFileRegion;
import org.jboss.netty.channel.FileRegion;
import java.nio.channels.ClosedChannelException;
import org.jboss.netty.channel.ChannelFuture;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.SocketAddress;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.util.regex.Pattern;

class OioWorker extends AbstractOioWorker<OioSocketChannel>
{
    private static final Pattern SOCKET_CLOSED_MESSAGE;
    
    OioWorker(final OioSocketChannel channel) {
        super(channel);
    }
    
    @Override
    public void run() {
        final boolean fireConnected = this.channel instanceof OioAcceptedSocketChannel;
        if (fireConnected && ((OioSocketChannel)this.channel).isOpen()) {
            Channels.fireChannelConnected(this.channel, ((OioSocketChannel)this.channel).getRemoteAddress());
        }
        super.run();
    }
    
    @Override
    boolean process() throws IOException {
        final PushbackInputStream in = ((OioSocketChannel)this.channel).getInputStream();
        final int bytesToRead = in.available();
        if (bytesToRead > 0) {
            final byte[] buf = new byte[bytesToRead];
            final int readBytes = in.read(buf);
            Channels.fireMessageReceived(this.channel, ((OioSocketChannel)this.channel).getConfig().getBufferFactory().getBuffer(buf, 0, readBytes));
            return true;
        }
        final int b = in.read();
        if (b < 0) {
            return false;
        }
        in.unread(b);
        return true;
    }
    
    static void write(final OioSocketChannel channel, final ChannelFuture future, final Object message) {
        final boolean iothread = AbstractOioWorker.isIoThread(channel);
        final OutputStream out = channel.getOutputStream();
        if (out == null) {
            final Exception e = new ClosedChannelException();
            future.setFailure(e);
            if (iothread) {
                Channels.fireExceptionCaught(channel, e);
            }
            else {
                Channels.fireExceptionCaughtLater(channel, e);
            }
            return;
        }
        try {
            int length = 0;
            if (message instanceof FileRegion) {
                final FileRegion fr = (FileRegion)message;
                try {
                    synchronized (out) {
                        final WritableByteChannel bchannel = java.nio.channels.Channels.newChannel(out);
                        long i;
                        while ((i = fr.transferTo(bchannel, length)) > 0L) {
                            length += (int)i;
                            if (length >= fr.getCount()) {
                                break;
                            }
                        }
                    }
                }
                finally {
                    if (fr instanceof DefaultFileRegion) {
                        final DefaultFileRegion dfr = (DefaultFileRegion)fr;
                        if (dfr.releaseAfterTransfer()) {
                            fr.releaseExternalResources();
                        }
                    }
                }
            }
            else {
                final ChannelBuffer a = (ChannelBuffer)message;
                length = a.readableBytes();
                synchronized (out) {
                    a.getBytes(a.readerIndex(), out, length);
                }
            }
            future.setSuccess();
            if (iothread) {
                Channels.fireWriteComplete(channel, length);
            }
            else {
                Channels.fireWriteCompleteLater(channel, length);
            }
        }
        catch (Throwable t) {
            if (t instanceof SocketException && OioWorker.SOCKET_CLOSED_MESSAGE.matcher(String.valueOf(t.getMessage())).matches()) {
                t = new ClosedChannelException();
            }
            future.setFailure(t);
            if (iothread) {
                Channels.fireExceptionCaught(channel, t);
            }
            else {
                Channels.fireExceptionCaughtLater(channel, t);
            }
        }
    }
    
    static {
        SOCKET_CLOSED_MESSAGE = Pattern.compile("^.*(?:Socket.*closed).*$", 2);
    }
}
