// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import org.apache.thrift.TBase;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import java.lang.reflect.Type;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.transport.TTransport;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.hadoop.hive.serde2.ByteStreamTypedSerDe;

public class ThriftByteStreamTypedSerDe extends ByteStreamTypedSerDe
{
    protected TIOStreamTransport outTransport;
    protected TIOStreamTransport inTransport;
    protected TProtocol outProtocol;
    protected TProtocol inProtocol;
    
    private void init(final TProtocolFactory inFactory, final TProtocolFactory outFactory) throws Exception {
        this.outTransport = new TIOStreamTransport(this.bos);
        this.inTransport = new TIOStreamTransport(this.bis);
        this.outProtocol = outFactory.getProtocol(this.outTransport);
        this.inProtocol = inFactory.getProtocol(this.inTransport);
    }
    
    @Override
    public void initialize(final Configuration job, final Properties tbl) throws SerDeException {
        throw new SerDeException("ThriftByteStreamTypedSerDe is still semi-abstract");
    }
    
    public ThriftByteStreamTypedSerDe(final Type objectType, final TProtocolFactory inFactory, final TProtocolFactory outFactory) throws SerDeException {
        super(objectType);
        try {
            this.init(inFactory, outFactory);
        }
        catch (Exception e) {
            throw new SerDeException(e);
        }
    }
    
    @Override
    protected ObjectInspectorFactory.ObjectInspectorOptions getObjectInspectorOptions() {
        return ObjectInspectorFactory.ObjectInspectorOptions.THRIFT;
    }
    
    @Override
    public Object deserialize(final Writable field) throws SerDeException {
        final Object obj = super.deserialize(field);
        try {
            ((TBase)obj).read(this.inProtocol);
        }
        catch (Exception e) {
            throw new SerDeException(e);
        }
        return obj;
    }
}
