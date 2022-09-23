// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

public class KdcUdpTransport extends KrbUdpTransport
{
    private BlockingQueue<ByteBuffer> bufferQueue;
    
    public KdcUdpTransport(final DatagramChannel channel, final InetSocketAddress remoteAddress) throws IOException {
        super(remoteAddress);
        this.bufferQueue = new ArrayBlockingQueue<ByteBuffer>(2);
        this.setChannel(channel);
    }
    
    @Override
    public synchronized ByteBuffer receiveMessage() throws IOException {
        final long timeout = 1000L;
        ByteBuffer message;
        try {
            message = this.bufferQueue.poll(timeout, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e) {
            throw new IOException(e);
        }
        return message;
    }
    
    protected synchronized void onRecvMessage(final ByteBuffer message) {
        if (message != null) {
            this.bufferQueue.add(message);
        }
    }
}
