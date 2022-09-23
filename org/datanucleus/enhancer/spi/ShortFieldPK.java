// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class ShortFieldPK extends SingleFieldPK
{
    private short key;
    
    public ShortFieldPK(final Class pcClass, final short key) {
        super(pcClass);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public ShortFieldPK(final Class pcClass, final Short key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public ShortFieldPK(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.key = Short.parseShort(str);
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public ShortFieldPK() {
    }
    
    public short getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return Short.toString(this.key);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ShortFieldPK other = (ShortFieldPK)obj;
        return this.key == other.key;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof ShortFieldPK) {
            final ShortFieldPK other = (ShortFieldPK)o;
            return this.key - other.key;
        }
        if (o == null) {
            throw new ClassCastException("object is null");
        }
        throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
    }
    
    @Override
    protected Object createKeyAsObject() {
        return this.key;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeShort(this.key);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.key = in.readShort();
    }
}
