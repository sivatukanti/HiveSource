// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.ByteObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypeByte extends DynamicSerDeTypeBase
{
    public DynamicSerDeTypeByte(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypeByte(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        return "byte";
    }
    
    public Byte deserialize(final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final byte val = iprot.readByte();
        if (val == 0 && iprot instanceof WriteNullsProtocol && ((WriteNullsProtocol)iprot).lastPrimitiveWasNull()) {
            return null;
        }
        return val;
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        return this.deserialize(iprot);
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final ByteObjectInspector poi = (ByteObjectInspector)oi;
        oprot.writeByte(poi.get(o));
    }
    
    @Override
    public byte getType() {
        return 3;
    }
}
