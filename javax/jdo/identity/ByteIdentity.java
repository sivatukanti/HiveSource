// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class ByteIdentity extends SingleFieldIdentity
{
    private byte key;
    
    private void construct(final byte key) {
        this.key = key;
        this.hashCode = (super.hashClassName() ^ key);
    }
    
    public ByteIdentity(final Class pcClass, final byte key) {
        super(pcClass);
        this.construct(key);
    }
    
    public ByteIdentity(final Class pcClass, final Byte key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.construct(key);
    }
    
    public ByteIdentity(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        this.construct(Byte.parseByte(str));
    }
    
    public ByteIdentity() {
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
        final ByteIdentity other = (ByteIdentity)obj;
        return this.key == other.key;
    }
    
    public int compareTo(final Object o) {
        if (o instanceof ByteIdentity) {
            final ByteIdentity other = (ByteIdentity)o;
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
        return new Byte(this.key);
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
