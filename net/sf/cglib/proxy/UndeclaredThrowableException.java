// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.CodeGenerationException;

public class UndeclaredThrowableException extends CodeGenerationException
{
    public UndeclaredThrowableException(final Throwable t) {
        super(t);
    }
    
    public Throwable getUndeclaredThrowable() {
        return this.getCause();
    }
}
