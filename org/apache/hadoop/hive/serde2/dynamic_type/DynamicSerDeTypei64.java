// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.LongObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypei64 extends DynamicSerDeTypeBase
{
    @Override
    public Class getRealType() {
        return Long.valueOf(0L).getClass();
    }
    
    public DynamicSerDeTypei64(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypei64(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        return "i64";
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final long val = iprot.readI64();
        if (val == 0L && iprot instanceof WriteNullsProtocol && ((WriteNullsProtocol)iprot).lastPrimitiveWasNull()) {
            return null;
        }
        return val;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final LongObjectInspector poi = (LongObjectInspector)oi;
        oprot.writeI64(poi.get(o));
    }
    
    @Override
    public byte getType() {
        return 10;
    }
}
