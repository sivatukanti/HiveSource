// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class IntFieldPK extends SingleFieldPK
{
    private int key;
    
    public IntFieldPK(final Class pcClass, final int key) {
        super(pcClass);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public IntFieldPK(final Class pcClass, final Integer key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public IntFieldPK(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.key = Integer.parseInt(str);
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public IntFieldPK() {
    }
    
    public int getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.key);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final IntFieldPK other = (IntFieldPK)obj;
        return this.key == other.key;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof IntFieldPK) {
            final IntFieldPK other = (IntFieldPK)o;
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
        out.writeInt(this.key);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.key = in.readInt();
    }
}
