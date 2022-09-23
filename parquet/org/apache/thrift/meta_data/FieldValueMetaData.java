// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.meta_data;

import java.io.Serializable;

public class FieldValueMetaData implements Serializable
{
    public final byte type;
    private final boolean isTypedefType;
    private final String typedefName;
    private final boolean isBinary;
    
    public FieldValueMetaData(final byte type, final boolean binary) {
        this.type = type;
        this.isTypedefType = false;
        this.typedefName = null;
        this.isBinary = binary;
    }
    
    public FieldValueMetaData(final byte type) {
        this(type, false);
    }
    
    public FieldValueMetaData(final byte type, final String typedefName) {
        this.type = type;
        this.isTypedefType = true;
        this.typedefName = typedefName;
        this.isBinary = false;
    }
    
    public boolean isTypedef() {
        return this.isTypedefType;
    }
    
    public String getTypedefName() {
        return this.typedefName;
    }
    
    public boolean isStruct() {
        return this.type == 12;
    }
    
    public boolean isContainer() {
        return this.type == 15 || this.type == 13 || this.type == 14;
    }
    
    public boolean isBinary() {
        return this.isBinary;
    }
}
