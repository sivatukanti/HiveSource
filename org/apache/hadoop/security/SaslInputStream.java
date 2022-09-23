// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import org.slf4j.LoggerFactory;
import java.nio.ByteBuffer;
import java.io.IOException;
import javax.security.sasl.SaslException;
import java.io.EOFException;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslClient;
import java.io.DataInputStream;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.nio.channels.ReadableByteChannel;
import java.io.InputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class SaslInputStream extends InputStream implements ReadableByteChannel
{
    public static final Logger LOG;
    private final DataInputStream inStream;
    private final boolean useWrap;
    private byte[] saslToken;
    private final SaslClient saslClient;
    private final SaslServer saslServer;
    private byte[] lengthBuf;
    private byte[] obuffer;
    private int ostart;
    private int ofinish;
    private boolean isOpen;
    
    private static int unsignedBytesToInt(final byte[] buf) {
        if (buf.length != 4) {
            throw new IllegalArgumentException("Cannot handle byte array other than 4 bytes");
        }
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result <<= 8;
            result |= (buf[i] & 0xFF);
        }
        return result;
    }
    
    private int readMoreData() throws IOException {
        try {
            this.inStream.readFully(this.lengthBuf);
            final int length = unsignedBytesToInt(this.lengthBuf);
            if (SaslInputStream.LOG.isDebugEnabled()) {
                SaslInputStream.LOG.debug("Actual length is " + length);
            }
            this.saslToken = new byte[length];
            this.inStream.readFully(this.saslToken);
        }
        catch (EOFException e) {
            return -1;
        }
        try {
            if (this.saslServer != null) {
                this.obuffer = this.saslServer.unwrap(this.saslToken, 0, this.saslToken.length);
            }
            else {
                this.obuffer = this.saslClient.unwrap(this.saslToken, 0, this.saslToken.length);
            }
        }
        catch (SaslException se) {
            try {
                this.disposeSasl();
            }
            catch (SaslException ex) {}
            throw se;
        }
        this.ostart = 0;
        if (this.obuffer == null) {
            this.ofinish = 0;
        }
        else {
            this.ofinish = this.obuffer.length;
        }
        return this.ofinish;
    }
    
    private void disposeSasl() throws SaslException {
        if (this.saslClient != null) {
            this.saslClient.dispose();
        }
        if (this.saslServer != null) {
            this.saslServer.dispose();
        }
    }
    
    public SaslInputStream(final InputStream inStream, final SaslServer saslServer) {
        this.lengthBuf = new byte[4];
        this.ostart = 0;
        this.ofinish = 0;
        this.isOpen = true;
        this.inStream = new DataInputStream(inStream);
        this.saslServer = saslServer;
        this.saslClient = null;
        final String qop = (String)saslServer.getNegotiatedProperty("javax.security.sasl.qop");
        this.useWrap = (qop != null && !"auth".equalsIgnoreCase(qop));
    }
    
    public SaslInputStream(final InputStream inStream, final SaslClient saslClient) {
        this.lengthBuf = new byte[4];
        this.ostart = 0;
        this.ofinish = 0;
        this.isOpen = true;
        this.inStream = new DataInputStream(inStream);
        this.saslServer = null;
        this.saslClient = saslClient;
        final String qop = (String)saslClient.getNegotiatedProperty("javax.security.sasl.qop");
        this.useWrap = (qop != null && !"auth".equalsIgnoreCase(qop));
    }
    
    @Override
    public int read() throws IOException {
        if (!this.useWrap) {
            return this.inStream.read();
        }
        if (this.ostart >= this.ofinish) {
            int i;
            for (i = 0; i == 0; i = this.readMoreData()) {}
            if (i == -1) {
                return -1;
            }
        }
        return this.obuffer[this.ostart++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (!this.useWrap) {
            return this.inStream.read(b, off, len);
        }
        if (this.ostart >= this.ofinish) {
            int i;
            for (i = 0; i == 0; i = this.readMoreData()) {}
            if (i == -1) {
                return -1;
            }
        }
        if (len <= 0) {
            return 0;
        }
        int available = this.ofinish - this.ostart;
        if (len < available) {
            available = len;
        }
        if (b != null) {
            System.arraycopy(this.obuffer, this.ostart, b, off, available);
        }
        this.ostart += available;
        return available;
    }
    
    @Override
    public long skip(long n) throws IOException {
        if (!this.useWrap) {
            return this.inStream.skip(n);
        }
        final int available = this.ofinish - this.ostart;
        if (n > available) {
            n = available;
        }
        if (n < 0L) {
            return 0L;
        }
        this.ostart += (int)n;
        return n;
    }
    
    @Override
    public int available() throws IOException {
        if (!this.useWrap) {
            return this.inStream.available();
        }
        return this.ofinish - this.ostart;
    }
    
    @Override
    public void close() throws IOException {
        this.disposeSasl();
        this.ostart = 0;
        this.ofinish = 0;
        this.inStream.close();
        this.isOpen = false;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public boolean isOpen() {
        return this.isOpen;
    }
    
    @Override
    public int read(final ByteBuffer dst) throws IOException {
        int bytesRead = 0;
        if (dst.hasArray()) {
            bytesRead = this.read(dst.array(), dst.arrayOffset() + dst.position(), dst.remaining());
            if (bytesRead > -1) {
                dst.position(dst.position() + bytesRead);
            }
        }
        else {
            final byte[] buf = new byte[dst.remaining()];
            bytesRead = this.read(buf);
            if (bytesRead > -1) {
                dst.put(buf, 0, bytesRead);
            }
        }
        return bytesRead;
    }
    
    static {
        LOG = LoggerFactory.getLogger(SaslInputStream.class);
    }
}
