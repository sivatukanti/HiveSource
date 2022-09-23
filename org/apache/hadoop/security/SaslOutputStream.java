// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.security.sasl.SaslException;
import java.io.BufferedOutputStream;
import javax.security.sasl.SaslServer;
import javax.security.sasl.SaslClient;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.OutputStream;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class SaslOutputStream extends OutputStream
{
    private final OutputStream outStream;
    private byte[] saslToken;
    private final SaslClient saslClient;
    private final SaslServer saslServer;
    private final byte[] ibuffer;
    private final boolean useWrap;
    
    public SaslOutputStream(final OutputStream outStream, final SaslServer saslServer) {
        this.ibuffer = new byte[1];
        this.saslServer = saslServer;
        this.saslClient = null;
        final String qop = (String)saslServer.getNegotiatedProperty("javax.security.sasl.qop");
        this.useWrap = (qop != null && !"auth".equalsIgnoreCase(qop));
        if (this.useWrap) {
            this.outStream = new BufferedOutputStream(outStream, 65536);
        }
        else {
            this.outStream = outStream;
        }
    }
    
    public SaslOutputStream(final OutputStream outStream, final SaslClient saslClient) {
        this.ibuffer = new byte[1];
        this.saslServer = null;
        this.saslClient = saslClient;
        final String qop = (String)saslClient.getNegotiatedProperty("javax.security.sasl.qop");
        this.useWrap = (qop != null && !"auth".equalsIgnoreCase(qop));
        if (this.useWrap) {
            this.outStream = new BufferedOutputStream(outStream, 65536);
        }
        else {
            this.outStream = outStream;
        }
    }
    
    private void disposeSasl() throws SaslException {
        if (this.saslClient != null) {
            this.saslClient.dispose();
        }
        if (this.saslServer != null) {
            this.saslServer.dispose();
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        if (!this.useWrap) {
            this.outStream.write(b);
            return;
        }
        this.ibuffer[0] = (byte)b;
        this.write(this.ibuffer, 0, 1);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] inBuf, final int off, final int len) throws IOException {
        if (!this.useWrap) {
            this.outStream.write(inBuf, off, len);
            return;
        }
        try {
            if (this.saslServer != null) {
                this.saslToken = this.saslServer.wrap(inBuf, off, len);
            }
            else {
                this.saslToken = this.saslClient.wrap(inBuf, off, len);
            }
        }
        catch (SaslException se) {
            try {
                this.disposeSasl();
            }
            catch (SaslException ex) {}
            throw se;
        }
        if (this.saslToken != null) {
            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            final DataOutputStream dout = new DataOutputStream(byteOut);
            dout.writeInt(this.saslToken.length);
            this.outStream.write(byteOut.toByteArray());
            this.outStream.write(this.saslToken, 0, this.saslToken.length);
            this.saslToken = null;
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.outStream.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.disposeSasl();
        this.outStream.close();
    }
}
