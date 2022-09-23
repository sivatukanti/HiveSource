// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net;

import java.nio.channels.ReadableByteChannel;
import java.net.SocketException;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import java.net.Socket;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.FilterInputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Unstable
public class SocketInputWrapper extends FilterInputStream
{
    private final Socket socket;
    private final boolean hasChannel;
    
    SocketInputWrapper(final Socket s, final InputStream is) {
        super(is);
        this.socket = s;
        this.hasChannel = (s.getChannel() != null);
        if (this.hasChannel) {
            Preconditions.checkArgument(is instanceof SocketInputStream, "Expected a SocketInputStream when there is a channel. Got: %s", is);
        }
    }
    
    public void setTimeout(final long timeoutMs) throws SocketException {
        if (this.hasChannel) {
            ((SocketInputStream)this.in).setTimeout(timeoutMs);
        }
        else {
            this.socket.setSoTimeout((int)timeoutMs);
        }
    }
    
    public ReadableByteChannel getReadableByteChannel() {
        Preconditions.checkState(this.hasChannel, "Socket %s does not have a channel", this.socket);
        return (SocketInputStream)this.in;
    }
}
