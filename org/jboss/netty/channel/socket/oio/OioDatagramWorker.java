// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.channel.socket.oio;

import java.nio.ByteBuffer;
import org.jboss.netty.buffer.ChannelBuffer;
import java.net.SocketAddress;
import org.jboss.netty.channel.ChannelFuture;
import java.io.IOException;
import org.jboss.netty.channel.ReceiveBufferSizePredictor;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.Channels;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;

class OioDatagramWorker extends AbstractOioWorker<OioDatagramChannel>
{
    OioDatagramWorker(final OioDatagramChannel channel) {
        super(channel);
    }
    
    @Override
    boolean process() throws IOException {
        final ReceiveBufferSizePredictor predictor = ((OioDatagramChannel)this.channel).getConfig().getReceiveBufferSizePredictor();
        final byte[] buf = new byte[predictor.nextReceiveBufferSize()];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            ((OioDatagramChannel)this.channel).socket.receive(packet);
        }
        catch (InterruptedIOException e) {
            return true;
        }
        Channels.fireMessageReceived(this.channel, ((OioDatagramChannel)this.channel).getConfig().getBufferFactory().getBuffer(buf, 0, packet.getLength()), packet.getSocketAddress());
        return true;
    }
    
    static void write(final OioDatagramChannel channel, final ChannelFuture future, final Object message, final SocketAddress remoteAddress) {
        final boolean iothread = AbstractOioWorker.isIoThread(channel);
        try {
            final ChannelBuffer buf = (ChannelBuffer)message;
            final int offset = buf.readerIndex();
            final int length = buf.readableBytes();
            final ByteBuffer nioBuf = buf.toByteBuffer();
            DatagramPacket packet;
            if (nioBuf.hasArray()) {
                packet = new DatagramPacket(nioBuf.array(), nioBuf.arrayOffset() + offset, length);
            }
            else {
                final byte[] arrayBuf = new byte[length];
                buf.getBytes(0, arrayBuf);
                packet = new DatagramPacket(arrayBuf, length);
            }
            if (remoteAddress != null) {
                packet.setSocketAddress(remoteAddress);
            }
            channel.socket.send(packet);
            if (iothread) {
                Channels.fireWriteComplete(channel, length);
            }
            else {
                Channels.fireWriteCompleteLater(channel, length);
            }
            future.setSuccess();
        }
        catch (Throwable t) {
            future.setFailure(t);
            if (iothread) {
                Channels.fireExceptionCaught(channel, t);
            }
            else {
                Channels.fireExceptionCaughtLater(channel, t);
            }
        }
    }
    
    static void disconnect(final OioDatagramChannel channel, final ChannelFuture future) {
        final boolean connected = channel.isConnected();
        final boolean iothread = AbstractOioWorker.isIoThread(channel);
        try {
            channel.socket.disconnect();
            future.setSuccess();
            if (connected) {
                if (iothread) {
                    Channels.fireChannelDisconnected(channel);
                }
                else {
                    Channels.fireChannelDisconnectedLater(channel);
                }
            }
        }
        catch (Throwable t) {
            future.setFailure(t);
            if (iothread) {
                Channels.fireExceptionCaught(channel, t);
            }
            else {
                Channels.fireExceptionCaughtLater(channel, t);
            }
        }
    }
}
