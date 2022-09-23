// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.thrift;

import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.lang.reflect.Type;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.serde2.AbstractDeserializer;

public class ThriftDeserializer extends AbstractDeserializer
{
    private ThriftByteStreamTypedSerDe tsd;
    
    @Override
    public void initialize(final Configuration job, final Properties tbl) throws SerDeException {
        try {
            final String className = tbl.getProperty("serialization.class");
            final Class<?> recordClass = job.getClassByName(className);
            String protoName = tbl.getProperty("serialization.format");
            if (protoName == null) {
                protoName = "TBinaryProtocol";
            }
            protoName = protoName.replace("com.facebook.thrift.protocol", "org.apache.thrift.protocol");
            final TProtocolFactory tp = TReflectionUtils.getProtocolFactoryByName(protoName);
            this.tsd = new ThriftByteStreamTypedSerDe(recordClass, tp, tp);
        }
        catch (Exception e) {
            throw new SerDeException(e);
        }
    }
    
    @Override
    public Object deserialize(final Writable field) throws SerDeException {
        return this.tsd.deserialize(field);
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.tsd.getObjectInspector();
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
}
