// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class ShortIdentity extends SingleFieldIdentity
{
    private short key;
    
    private void construct(final short key) {
        this.key = key;
        this.hashCode = (this.hashClassName() ^ key);
    }
    
    public ShortIdentity(final Class pcClass, final short key) {
        super(pcClass);
        this.construct(key);
    }
    
    public ShortIdentity(final Class pcClass, final Short key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.construct(key);
    }
    
    public ShortIdentity(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.construct(Short.parseShort(str));
    }
    
    public ShortIdentity() {
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
        final ShortIdentity other = (ShortIdentity)obj;
        return this.key == other.key;
    }
    
    public int compareTo(final Object o) {
        if (o instanceof ShortIdentity) {
            final ShortIdentity other = (ShortIdentity)o;
            final int result = super.compare(other);
            if (result == 0) {
                return this.key - other.key;
            }
            return result;
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
        return new Short(this.key);
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
