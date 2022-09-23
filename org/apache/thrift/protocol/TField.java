// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift.protocol;

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
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.id;
        result = 31 * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = 31 * result + this.type;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final TField otherField = (TField)obj;
        return this.type == otherField.type && this.id == otherField.id;
    }
}
