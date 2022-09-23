// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class IntIdentity extends SingleFieldIdentity
{
    private int key;
    
    private void construct(final int key) {
        this.key = key;
        this.hashCode = (this.hashClassName() ^ key);
    }
    
    public IntIdentity(final Class pcClass, final int key) {
        super(pcClass);
        this.construct(key);
    }
    
    public IntIdentity(final Class pcClass, final Integer key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.construct(key);
    }
    
    public IntIdentity(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.construct(Integer.parseInt(str));
    }
    
    public IntIdentity() {
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
        final IntIdentity other = (IntIdentity)obj;
        return this.key == other.key;
    }
    
    public int compareTo(final Object o) {
        if (o instanceof IntIdentity) {
            final IntIdentity other = (IntIdentity)o;
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
        return new Integer(this.key);
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
