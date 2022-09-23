// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.enhancer;

import org.datanucleus.util.Localiser;

public class ClassField
{
    protected static Localiser LOCALISER;
    protected ClassEnhancer enhancer;
    protected String fieldName;
    protected int access;
    protected Object type;
    protected Object initialValue;
    
    public ClassField(final ClassEnhancer enhancer, final String name, final int access, final Object type) {
        this.enhancer = enhancer;
        this.fieldName = name;
        this.access = access;
        this.type = type;
    }
    
    public ClassField(final ClassEnhancer enhancer, final String name, final int access, final Object type, final Object value) {
        this.enhancer = enhancer;
        this.fieldName = name;
        this.access = access;
        this.type = type;
        this.initialValue = value;
    }
    
    public String getName() {
        return this.fieldName;
    }
    
    public int getAccess() {
        return this.access;
    }
    
    public Object getType() {
        return this.type;
    }
    
    public Object getInitialValue() {
        return this.initialValue;
    }
    
    @Override
    public int hashCode() {
        return this.fieldName.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ClassField) {
            final ClassField cf = (ClassField)o;
            if (cf.fieldName.equals(this.fieldName)) {
                return this.type == cf.type;
            }
        }
        return false;
    }
    
    static {
        ClassField.LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassEnhancer.class.getClassLoader());
    }
}
