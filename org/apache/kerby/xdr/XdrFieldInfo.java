// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.xdr;

public class XdrFieldInfo
{
    private int index;
    private XdrDataType dataType;
    private Object value;
    
    public XdrFieldInfo(final int index, final XdrDataType dataType, final Object value) {
        this.index = index;
        this.dataType = dataType;
        this.value = value;
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public XdrDataType getDataType() {
        return this.dataType;
    }
    
    public Object getValue() {
        return this.value;
    }
}
