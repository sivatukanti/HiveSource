// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.ShortObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypei16 extends DynamicSerDeTypeBase
{
    @Override
    public Class getRealType() {
        return Integer.valueOf(2).getClass();
    }
    
    public DynamicSerDeTypei16(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypei16(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        return "i16";
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final int val = iprot.readI16();
        if (val == 0 && iprot instanceof WriteNullsProtocol && ((WriteNullsProtocol)iprot).lastPrimitiveWasNull()) {
            return null;
        }
        return val;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final ShortObjectInspector poi = (ShortObjectInspector)oi;
        oprot.writeI16(poi.get(o));
    }
    
    @Override
    public byte getType() {
        return 6;
    }
}
