// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class DynamicSerDeFieldType extends DynamicSerDeSimpleNode
{
    private static final int FD_FIELD_TYPE = 0;
    
    public DynamicSerDeFieldType(final int i) {
        super(i);
    }
    
    public DynamicSerDeFieldType(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    protected DynamicSerDeTypeBase getMyType() {
        return (DynamicSerDeTypeBase)this.jjtGetChild(0);
    }
}
