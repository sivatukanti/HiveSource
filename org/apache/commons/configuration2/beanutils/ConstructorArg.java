// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.beanutils;

public final class ConstructorArg
{
    private final BeanDeclaration beanDeclaration;
    private final Object value;
    private final String typeName;
    
    private ConstructorArg(final BeanDeclaration decl, final Object val, final String type) {
        this.beanDeclaration = decl;
        this.value = val;
        this.typeName = type;
    }
    
    public static ConstructorArg forBeanDeclaration(final BeanDeclaration decl) {
        return forBeanDeclaration(decl, null);
    }
    
    public static ConstructorArg forBeanDeclaration(final BeanDeclaration decl, final String typeName) {
        if (decl == null) {
            throw new NullPointerException("BeanDeclaration must not be null!");
        }
        return new ConstructorArg(decl, null, typeName);
    }
    
    public static ConstructorArg forValue(final Object value) {
        return forValue(value, null);
    }
    
    public static ConstructorArg forValue(final Object value, final String typeName) {
        return new ConstructorArg(null, value, typeName);
    }
    
    public BeanDeclaration getBeanDeclaration() {
        return this.beanDeclaration;
    }
    
    public boolean isNestedBeanDeclaration() {
        return this.getBeanDeclaration() != null;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
    
    public boolean matches(final Class<?> argCls) {
        return argCls != null && (this.getTypeName() == null || this.getTypeName().equals(argCls.getName()));
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append(" [ value = ");
        buf.append(this.isNestedBeanDeclaration() ? this.getBeanDeclaration() : this.getValue());
        if (this.getTypeName() != null) {
            buf.append(" (").append(this.getTypeName()).append(')');
        }
        buf.append(" ]");
        return buf.toString();
    }
}
