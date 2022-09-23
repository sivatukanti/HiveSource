// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

import java.net.InetAddress;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class KrbUdpTransport extends AbstractKrbTransport implements KrbTransport
{
    private DatagramChannel channel;
    private InetSocketAddress remoteAddress;
    private ByteBuffer recvBuffer;
    
    public KrbUdpTransport(final InetSocketAddress remoteAddress) throws IOException {
        this.remoteAddress = remoteAddress;
        final DatagramChannel tmpChannel = DatagramChannel.open();
        tmpChannel.configureBlocking(true);
        tmpChannel.connect(remoteAddress);
        this.setChannel(tmpChannel);
        this.recvBuffer = ByteBuffer.allocate(65507);
    }
    
    protected void setChannel(final DatagramChannel channel) {
        this.channel = channel;
    }
    
    @Override
    public void sendMessage(final ByteBuffer message) throws IOException {
        this.channel.send(message, this.remoteAddress);
    }
    
    @Override
    public ByteBuffer receiveMessage() throws IOException {
        this.recvBuffer.clear();
        this.channel.receive(this.recvBuffer);
        this.recvBuffer.flip();
        return this.recvBuffer;
    }
    
    @Override
    public boolean isTcp() {
        return false;
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        return this.remoteAddress.getAddress();
    }
    
    @Override
    public void release() {
        try {
            this.channel.disconnect();
        }
        catch (IOException ex) {}
    }
}
