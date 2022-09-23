// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public abstract class ValueExpression extends Expression
{
    public abstract Object getValue(final ELContext p0);
    
    public abstract void setValue(final ELContext p0, final Object p1);
    
    public abstract boolean isReadOnly(final ELContext p0);
    
    public abstract Class<?> getType(final ELContext p0);
    
    public abstract Class<?> getExpectedType();
}
