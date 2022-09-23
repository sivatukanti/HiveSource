// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

public abstract class MethodExpression extends Expression
{
    public abstract MethodInfo getMethodInfo(final ELContext p0);
    
    public abstract Object invoke(final ELContext p0, final Object[] p1);
}
