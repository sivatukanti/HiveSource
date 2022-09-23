// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.thrift.WriteTextProtocol;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypeString extends DynamicSerDeTypeBase
{
    public DynamicSerDeTypeString(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypeString(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public Class getRealType() {
        return String.class;
    }
    
    @Override
    public String toString() {
        return "string";
    }
    
    public String deserialize(final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        return iprot.readString();
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        return iprot.readString();
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        final StringObjectInspector poi = (StringObjectInspector)oi;
        if (oprot instanceof WriteTextProtocol) {
            ((WriteTextProtocol)oprot).writeText(poi.getPrimitiveWritableObject(o));
        }
        else {
            oprot.writeString(poi.getPrimitiveJavaObject(o));
        }
    }
    
    @Override
    public byte getType() {
        return 11;
    }
}
