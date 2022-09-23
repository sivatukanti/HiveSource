// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import javax.jdo.spi.I18NHelper;

public class CharIdentity extends SingleFieldIdentity
{
    private static I18NHelper msg;
    private char key;
    
    private void construct(final char key) {
        this.key = key;
        this.hashCode = (this.hashClassName() ^ key);
    }
    
    public CharIdentity(final Class pcClass, final char key) {
        super(pcClass);
        this.construct(key);
    }
    
    public CharIdentity(final Class pcClass, final Character key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.construct(key);
    }
    
    public CharIdentity(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        if (str.length() != 1) {
            throw new IllegalArgumentException(CharIdentity.msg.msg("EXC_StringWrongLength"));
        }
        this.construct(str.charAt(0));
    }
    
    public CharIdentity() {
    }
    
    public char getKey() {
        return this.key;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.key);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final CharIdentity other = (CharIdentity)obj;
        return this.key == other.key;
    }
    
    public int compareTo(final Object o) {
        if (o instanceof CharIdentity) {
            final CharIdentity other = (CharIdentity)o;
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
        return new Character(this.key);
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeChar(this.key);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.key = in.readChar();
    }
    
    static {
        CharIdentity.msg = I18NHelper.getInstance("javax.jdo.Bundle");
    }
}
