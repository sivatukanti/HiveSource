// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.thrift.protocol.TProtocol;

public class DynamicSerDeTypedef extends DynamicSerDeTypeBase
{
    private static final int FD_DEFINITION_TYPE = 0;
    
    public DynamicSerDeTypedef(final int i) {
        super(i);
    }
    
    public DynamicSerDeTypedef(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    private DynamicSerDeSimpleNode getDefinitionType() {
        return (DynamicSerDeSimpleNode)this.jjtGetChild(0);
    }
    
    public DynamicSerDeTypeBase getMyType() {
        final DynamicSerDeSimpleNode child = this.getDefinitionType();
        final DynamicSerDeTypeBase ret = (DynamicSerDeTypeBase)child.jjtGetChild(0);
        return ret;
    }
    
    @Override
    public String toString() {
        String result = "typedef " + this.name + "(";
        result += this.getDefinitionType().toString();
        result += ")";
        return result;
    }
    
    @Override
    public byte getType() {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public Object deserialize(final Object reuse, final TProtocol iprot) throws SerDeException, TException, IllegalAccessException {
        throw new RuntimeException("not implemented");
    }
    
    @Override
    public void serialize(final Object o, final ObjectInspector oi, final TProtocol oprot) throws TException, SerDeException, NoSuchFieldException, IllegalAccessException {
        throw new RuntimeException("not implemented");
    }
}
