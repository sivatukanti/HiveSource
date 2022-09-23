// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypei32 extends DynamicSerDeTypeBase
{
    public DynamicSerDeTypei32(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypei32(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        return "i32";
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final int val = iprot.readI32();
        if (val == 0 && iprot instanceof WriteNullsProtocol && ((WriteNullsProtocol)iprot).lastPrimitiveWasNull()) {
            return null;
        }
        return val;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final IntObjectInspector poi = (IntObjectInspector)oi;
        oprot.writeI32(poi.get(o));
    }
    
    @Override
    public Class getRealType() {
        return Integer.class;
    }
    
    public Integer getRealTypeInstance() {
        return 0;
    }
    
    @Override
    public byte getType() {
        return 8;
    }
}
