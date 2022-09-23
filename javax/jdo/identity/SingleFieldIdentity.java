// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.identity;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import javax.jdo.JDOFatalInternalException;
import javax.jdo.JDONullIdentityException;
import javax.jdo.spi.I18NHelper;
import java.io.Externalizable;

public abstract class SingleFieldIdentity implements Externalizable, Comparable
{
    protected static I18NHelper msg;
    private transient Class targetClass;
    private String targetClassName;
    protected int hashCode;
    protected Object keyAsObject;
    
    protected SingleFieldIdentity(final Class pcClass) {
        if (pcClass == null) {
            throw new NullPointerException();
        }
        this.targetClass = pcClass;
        this.targetClassName = pcClass.getName();
    }
    
    public SingleFieldIdentity() {
    }
    
    protected void setKeyAsObject(final Object key) {
        this.assertKeyNotNull(key);
        this.keyAsObject = key;
    }
    
    protected void assertKeyNotNull(final Object key) {
        if (key == null) {
            throw new JDONullIdentityException(SingleFieldIdentity.msg.msg("EXC_SingleFieldIdentityNullParameter"));
        }
    }
    
    public Class getTargetClass() {
        return this.targetClass;
    }
    
    public String getTargetClassName() {
        return this.targetClassName;
    }
    
    public synchronized Object getKeyAsObject() {
        if (this.keyAsObject == null) {
            this.keyAsObject = this.createKeyAsObject();
        }
        return this.keyAsObject;
    }
    
    protected Object createKeyAsObject() {
        throw new JDOFatalInternalException(SingleFieldIdentity.msg.msg("EXC_CreateKeyAsObjectMustNotBeCalled"));
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final SingleFieldIdentity other = (SingleFieldIdentity)obj;
        return (this.targetClass != null && this.targetClass == other.targetClass) || this.targetClassName.equals(other.targetClassName);
    }
    
    protected int hashClassName() {
        return this.targetClassName.hashCode();
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.targetClassName);
        out.writeInt(this.hashCode);
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.targetClass = null;
        this.targetClassName = (String)in.readObject();
        this.hashCode = in.readInt();
    }
    
    protected int compare(final SingleFieldIdentity o) {
        return this.targetClassName.compareTo(o.targetClassName);
    }
    
    static {
        SingleFieldIdentity.msg = I18NHelper.getInstance("javax.jdo.Bundle");
    }
}
