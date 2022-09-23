// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.transport;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class KrbTcpTransport extends AbstractKrbTransport implements KrbTransport
{
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    private byte[] messageBuffer;
    
    public KrbTcpTransport(final Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.messageBuffer = new byte[4194304];
    }
    
    @Override
    public void sendMessage(final ByteBuffer message) throws IOException {
        this.outputStream.write(message.array());
    }
    
    @Override
    public ByteBuffer receiveMessage() throws IOException {
        final int msgLen = this.inputStream.readInt();
        if (msgLen <= 0) {
            return null;
        }
        if (msgLen > this.messageBuffer.length) {
            throw new IOException("Recv buffer overflowed, too large message?");
        }
        this.inputStream.readFully(this.messageBuffer, 0, msgLen);
        return ByteBuffer.wrap(this.messageBuffer, 0, msgLen);
    }
    
    @Override
    public boolean isTcp() {
        return true;
    }
    
    @Override
    public InetAddress getRemoteAddress() {
        return this.socket.getInetAddress();
    }
    
    @Override
    public void release() {
        try {
            this.socket.close();
        }
        catch (IOException ex) {}
    }
}
