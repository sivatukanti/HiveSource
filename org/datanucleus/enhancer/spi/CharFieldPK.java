// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class CharFieldPK extends SingleFieldPK
{
    private char key;
    
    public CharFieldPK(final Class pcClass, final char key) {
        super(pcClass);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public CharFieldPK(final Class pcClass, final Character key) {
        super(pcClass);
        this.setKeyAsObject(key);
        this.key = key;
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public CharFieldPK(final Class pcClass, final String str) {
        super(pcClass);
        this.assertKeyNotNull(str);
        if (str.length() != 1) {
            throw new IllegalArgumentException("CharIdentity should have a value of length 1");
        }
        this.key = str.charAt(0);
        this.hashCode = (this.hashClassName() ^ this.key);
    }
    
    public CharFieldPK() {
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
        final CharFieldPK other = (CharFieldPK)obj;
        return this.key == other.key;
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof CharFieldPK) {
            final CharFieldPK other = (CharFieldPK)o;
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
        out.writeChar(this.key);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.key = in.readChar();
    }
}
