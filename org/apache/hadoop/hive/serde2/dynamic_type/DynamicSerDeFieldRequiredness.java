// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class DynamicSerDeFieldRequiredness extends SimpleNode
{
    protected RequirednessTypes requiredness;
    
    public RequirednessTypes getRequiredness() {
        return this.requiredness;
    }
    
    public DynamicSerDeFieldRequiredness(final int id) {
        super(id);
    }
    
    public DynamicSerDeFieldRequiredness(final thrift_grammar p, final int id) {
        super(p, id);
    }
    
    public enum RequirednessTypes
    {
        Required, 
        Skippable, 
        Optional;
    }
}
