// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.primitive.DoubleObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.serde2.thrift.WriteNullsProtocol;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypeDouble extends DynamicSerDeTypeBase
{
    public DynamicSerDeTypeDouble(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypeDouble(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        return "double";
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        final double val = iprot.readDouble();
        if (val == 0.0 && iprot instanceof WriteNullsProtocol && ((WriteNullsProtocol)iprot).lastPrimitiveWasNull()) {
            return null;
        }
        return val;
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final DoubleObjectInspector poi = (DoubleObjectInspector)oi;
        oprot.writeDouble(poi.get(o));
    }
    
    @Override
    public byte getType() {
        return 4;
    }
    
    @Override
    public Class getRealType() {
        return Double.class;
    }
    
    public Double getRealTypeInstance() {
        return 0.0;
    }
}
