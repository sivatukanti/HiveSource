// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.protocol;

public class TField
{
    public final String name;
    public final byte type;
    public final short id;
    
    public TField() {
        this("", (byte)0, (short)0);
    }
    
    public TField(final String n, final byte t, final short i) {
        this.name = n;
        this.type = t;
        this.id = i;
    }
    
    @Override
    public String toString() {
        return "<TField name:'" + this.name + "' type:" + this.type + " field-id:" + this.id + ">";
    }
    
    public boolean equals(final TField otherField) {
        return this.type == otherField.type && this.id == otherField.id;
    }
}
