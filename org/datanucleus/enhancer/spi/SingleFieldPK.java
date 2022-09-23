// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer.spi;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import java.io.Externalizable;

public abstract class SingleFieldPK implements Externalizable, Comparable
{
    private transient Class targetClass;
    private String targetClassName;
    protected int hashCode;
    protected Object keyAsObject;
    
    protected SingleFieldPK(final Class pcClass) {
        if (pcClass == null) {
            throw new NullPointerException();
        }
        this.targetClass = pcClass;
        this.targetClassName = pcClass.getName();
    }
    
    public SingleFieldPK() {
    }
    
    protected void setKeyAsObject(final Object key) {
        this.assertKeyNotNull(key);
        this.keyAsObject = key;
    }
    
    protected void assertKeyNotNull(final Object key) {
        if (key == null) {
            throw new NucleusUserException("Cannot create SingleFieldIdentity with null parameter");
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
        throw new NucleusException("SingleFIeldIdentity.createKeyAsObject must not be called.").setFatal();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final SingleFieldPK other = (SingleFieldPK)obj;
        return (this.targetClass != null && this.targetClass == other.targetClass) || this.targetClassName.equals(other.targetClassName);
    }
    
    protected int hashClassName() {
        return this.targetClassName.hashCode();
    }
    
    @Override
    public int hashCode() {
        return this.hashCode;
    }
    
    @Override
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeObject(this.targetClassName);
        out.writeInt(this.hashCode);
    }
    
    @Override
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        this.targetClass = null;
        this.targetClassName = (String)in.readObject();
        this.hashCode = in.readInt();
    }
}
