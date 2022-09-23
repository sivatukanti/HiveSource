// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class DynamicSerDeStruct extends DynamicSerDeStructBase
{
    private static final int FD_FIELD_LIST = 0;
    
    public DynamicSerDeStruct(final int i) {
        super(i);
    }
    
    public DynamicSerDeStruct(final thrift_grammar p, final int i) {
        super(p, i);
    }
    
    @Override
    public String toString() {
        String result = "struct " + this.name + "(";
        result += this.getFieldList().toString();
        result += ")";
        return result;
    }
    
    @Override
    public DynamicSerDeFieldList getFieldList() {
        return (DynamicSerDeFieldList)this.jjtGetChild(0);
    }
    
    @Override
    public byte getType() {
        return 12;
    }
}
