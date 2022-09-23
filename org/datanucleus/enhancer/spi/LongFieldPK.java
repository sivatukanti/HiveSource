// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class LongFieldPK extends SingleFieldPK
{
    private long key;
    
    public LongFieldPK(final Class pcClass, final long key) {
        super(pcClass);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ (int)this.key);
    }
    
    public LongFieldPK(final Class pcClass, final Long key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ (int)this.key);
    }
    
    public LongFieldPK(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.key = Long.parseLong(str);
        this.hashCode = (this.hashClassName() ^ (int)this.key);
    }
    
    public LongFieldPK() {
    }
    
    public long getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return Long.toString(this.key);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final LongFieldPK other = (LongFieldPK)obj;
        return this.key == other.key;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof LongFieldPK) {
            final LongFieldPK other = (LongFieldPK)o;
            return (int)(this.key - other.key);
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
        out.writeLong(this.key);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.key = in.readLong();
    }
}
