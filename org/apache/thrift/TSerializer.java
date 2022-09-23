// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import java.io.UnsupportedEncodingException;
import org.apache.thrift.transport.TTransport;
import java.io.OutputStream;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import java.io.ByteArrayOutputStream;

public class TSerializer
{
    private final ByteArrayOutputStream baos_;
    private final TIOStreamTransport transport_;
    private TProtocol protocol_;
    
    public TSerializer() {
        this(new TBinaryProtocol.Factory());
    }
    
    public TSerializer(final TProtocolFactory protocolFactory) {
        this.baos_ = new ByteArrayOutputStream();
        this.transport_ = new TIOStreamTransport(this.baos_);
        this.protocol_ = protocolFactory.getProtocol(this.transport_);
    }
    
    public byte[] serialize(final TBase base) throws TException {
        this.baos_.reset();
        base.write(this.protocol_);
        return this.baos_.toByteArray();
    }
    
    public String toString(final TBase base, final String charset) throws TException {
        try {
            return new String(this.serialize(base), charset);
        }
        catch (UnsupportedEncodingException uex) {
            throw new TException("JVM DOES NOT SUPPORT ENCODING: " + charset);
        }
    }
    
    public String toString(final TBase base) throws TException {
        return new String(this.serialize(base));
    }
}
