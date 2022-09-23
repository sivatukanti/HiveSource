// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.io.Serializable;

public abstract class DynamicSerDeTypeBase extends DynamicSerDeSimpleNode implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public DynamicSerDeTypeBase(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypeBase(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    public void initialize() {
    }
    
    public Class getRealType() throws SerDeException {
        throw new SerDeException("Not implemented in base");
    }
    
    public Object get(final Object obj) {
        throw new RuntimeException("Not implemented in base");
    }
    
    public abstract Object deserialize(final Object p0, final TProtocol p1) throws SerDeException, TException, IllegalAccessException;
    
    public abstract void serialize(final Object p0, final ObjectInspector p1, final TProtocol p2) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException;
    
    @Override
    public String toString() {
        return "BAD";
    }
    
    public byte getType() {
        return -1;
    }
    
    public boolean isPrimitive() {
        return true;
    }
    
    public boolean isList() {
        return false;
    }
    
    public boolean isMap() {
        return false;
    }
}
