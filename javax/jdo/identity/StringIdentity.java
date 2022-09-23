// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class StringIdentity extends SingleFieldIdentity
{
    public StringIdentity(final Class pcClass, final String key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.hashCode = (this.hashClassName() ^ key.hashCode());
    }
    
    public StringIdentity() {
    }
    
    public String getKey() {
        return (String)this.keyAsObject;
    }
    
    @Override
    public String toString() {
        return (String)this.keyAsObject;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final StringIdentity other = (StringIdentity)obj;
        return this.keyAsObject.equals(other.keyAsObject);
    }
    
    public int compareTo(final Object o) {
        if (o instanceof StringIdentity) {
            final StringIdentity other = (StringIdentity)o;
            final int result = super.compare(other);
            if (result == 0) {
                return ((String)this.keyAsObject).compareTo((String)other.keyAsObject);
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
    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(this.keyAsObject);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.keyAsObject = in.readObject();
    }
}
