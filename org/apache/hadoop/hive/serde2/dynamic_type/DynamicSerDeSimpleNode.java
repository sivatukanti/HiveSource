// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class DynamicSerDeSimpleNode extends SimpleNode
{
    protected static final boolean thrift_mode = true;
    protected int fieldid;
    protected String name;
    
    public DynamicSerDeSimpleNode(final int i) {
        super(i);
    }
    
    public DynamicSerDeSimpleNode(final thrift_grammar p, final int i) {
        super(p, i);
    }
}
