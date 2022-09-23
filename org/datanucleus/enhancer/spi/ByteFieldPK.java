// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class ByteFieldPK extends SingleFieldPK
{
    private byte key;
    
    public ByteFieldPK(final Class pcClass, final byte key) {
        super(pcClass);
        this.key = key;
        this.hashCode = (super.hashClassName() ^ this.key);
    }
    
    public ByteFieldPK(final Class pcClass, final Byte key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.key = key;
        this.hashCode = (super.hashClassName() ^ this.key);
    }
    
    public ByteFieldPK(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.key = Byte.parseByte(str);
        this.hashCode = (super.hashClassName() ^ this.key);
    }
    
    public ByteFieldPK() {
    }
    
    public byte getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return Byte.toString(this.key);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ByteFieldPK other = (ByteFieldPK)obj;
        return this.key == other.key;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof ByteFieldPK) {
            final ByteFieldPK other = (ByteFieldPK)o;
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
        out.writeByte(this.key);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.key = in.readByte();
    }
}
