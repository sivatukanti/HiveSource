// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.io.Serializable;

public abstract class Expression implements Serializable
{
    public abstract String getExpressionString();
    
    @Override
    public abstract boolean equals(final Object p0);
    
    @Override
    public abstract int hashCode();
    
    public abstract boolean isLiteralText();
}
