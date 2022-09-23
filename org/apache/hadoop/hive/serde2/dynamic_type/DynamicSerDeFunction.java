// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class DynamicSerDeFunction extends DynamicSerDeStructBase
{
    private static final int FD_FIELD_LIST = 2;
    
    public DynamicSerDeFunction(final int i) {
        super(i);
    }
    
    public DynamicSerDeFunction(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public DynamicSerDeFieldList getFieldList() {
        return (DynamicSerDeFieldList)this.jjtGetChild(2);
    }
    
    @Override
    public String toString() {
        String result = "function " + this.name + " (";
        result += this.getFieldList().toString();
        result += ")";
        return result;
    }
    
    @Override
    public byte getType() {
        return 1;
    }
}
