// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class DynamicSerDeField extends DynamicSerDeSimpleNode
{
    private static final int FD_REQUIREDNESS = 0;
    private static final int FD_FIELD_TYPE = 1;
    
    public boolean isSkippable() {
        return ((DynamicSerDeFieldRequiredness)this.jjtGetChild(0)).getRequiredness() == DynamicSerDeFieldRequiredness.RequirednessTypes.Skippable;
    }
    
    public DynamicSerDeFieldType getFieldType() {
        return (DynamicSerDeFieldType)this.jjtGetChild(1);
    }
    
    public DynamicSerDeField(final int i) {
        super(i);
    }
    
    public DynamicSerDeField(final thrift_grammar p, final int i) {
        super(p, i);
    }
}
