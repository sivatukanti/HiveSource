// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class LongIdentity extends SingleFieldIdentity
{
    private long key;
    
    private void construct(final long key) {
        this.key = key;
        this.hashCode = (this.hashClassName() ^ (int)key);
    }
    
    public LongIdentity(final Class pcClass, final long key) {
        super(pcClass);
        this.construct(key);
    }
    
    public LongIdentity(final Class pcClass, final Long key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.construct(key);
    }
    
    public LongIdentity(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.construct(Long.parseLong(str));
    }
    
    public LongIdentity() {
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
        final LongIdentity other = (LongIdentity)obj;
        return this.key == other.key;
    }
    
    public int compareTo(final Object o) {
        if (o instanceof LongIdentity) {
            final LongIdentity other = (LongIdentity)o;
            final int result = super.compare(other);
            if (result != 0) {
                return result;
            }
            final long diff = this.key - other.key;
            if (diff == 0L) {
                return 0;
            }
            if (diff < 0L) {
                return -1;
            }
            return 1;
        }
        else {
            if (o == null) {
                throw new ClassCastException("object is null");
            }
            throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
        }
    }
    
    @Override
    protected Object createKeyAsObject() {
        return new Long(this.key);
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
