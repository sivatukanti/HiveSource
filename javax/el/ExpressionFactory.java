// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public abstract class ExpressionFactory
{
    public abstract ValueExpression createValueExpression(final ELContext p0, final String p1, final Class<?> p2);
    
    public abstract ValueExpression createValueExpression(final Object p0, final Class<?> p1);
    
    public abstract MethodExpression createMethodExpression(final ELContext p0, final String p1, final Class<?> p2, final Class<?>[] p3);
    
    public abstract Object coerceToType(final Object p0, final Class<?> p1);
}
