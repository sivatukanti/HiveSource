// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.BooleanObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypeBool extends DynamicSerDeTypeBase
{
    public DynamicSerDeTypeBool(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypeBool(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        return "bool";
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final boolean val = iprot.readBool();
        if (!val && iprot instanceof WriteNullsProtocol && ((WriteNullsProtocol)iprot).lastPrimitiveWasNull()) {
            return null;
        }
        return val;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final BooleanObjectInspector poi = (BooleanObjectInspector)oi;
        oprot.writeBool(poi.get(o));
    }
    
    @Override
    public byte getType() {
        return 2;
    }
    
    @Override
    public Class getRealType() {
        return Boolean.class;
    }
    
    public Boolean getRealTypeInstance() {
        return Boolean.FALSE;
    }
}
