// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core.$CodeGenerationException;

public class $UndeclaredThrowableException extends $CodeGenerationException
{
    public $UndeclaredThrowableException(final Throwable t) {
        super(t);
    }
    
    public Throwable getUndeclaredThrowable() {
        return this.getCause();
    }
}
