// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Currency;
import org.datanucleus.util.I18nUtils;
import java.util.Date;
import org.datanucleus.exceptions.NucleusUserException;

public class ObjectFieldPK extends SingleFieldPK
{
    private static final String STRING_DELIMITER = ":";
    
    public ObjectFieldPK(final Class pcClass, final Object param) {
        super(pcClass);
        this.assertKeyNotNull(param);
        String paramString = null;
        String keyString = null;
        String className = null;
        if (param instanceof String) {
            paramString = (String)param;
            if (paramString.length() < 3) {
                throw new NucleusUserException("ObjectIdentity should be passed a String greater than 3 characters in length");
            }
            final int indexOfDelimiter = paramString.indexOf(":");
            if (indexOfDelimiter < 0) {
                throw new NucleusUserException("ObjectIdentity should be passed a String with a delimiter present");
            }
            keyString = paramString.substring(indexOfDelimiter + 1);
            className = paramString.substring(0, indexOfDelimiter);
            if (className.equals("java.util.Date")) {
                this.keyAsObject = new Date(Long.parseLong(keyString));
            }
            else if (className.equals("java.util.Locale")) {
                this.keyAsObject = I18nUtils.getLocaleFromString(keyString);
            }
            else {
                if (!className.equals("java.util.Currency")) {
                    throw new NucleusUserException("Class for ObjectIdentity " + className + " is not supported as a PK type");
                }
                this.keyAsObject = Currency.getInstance(keyString);
            }
        }
        else {
            this.keyAsObject = param;
        }
        this.hashCode = (this.hashClassName() ^ this.keyAsObject.hashCode());
    }
    
    public ObjectFieldPK() {
    }
    
    public Object getKey() {
        return this.keyAsObject;
    }
    
    @Override
    public String toString() {
        return this.keyAsObject.getClass().getName() + ":" + this.keyAsObject.toString();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        final ObjectFieldPK other = (ObjectFieldPK)obj;
        return this.keyAsObject.equals(other.keyAsObject);
    }
    
    @Override
    public int compareTo(final Object o) {
        if (o instanceof ObjectFieldPK) {
            final ObjectFieldPK other = (ObjectFieldPK)o;
            return ((String)this.keyAsObject).compareTo((String)other.keyAsObject);
        }
        if (o == null) {
            throw new ClassCastException("object is null");
        }
        throw new ClassCastException(this.getClass().getName() + " != " + o.getClass().getName());
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
