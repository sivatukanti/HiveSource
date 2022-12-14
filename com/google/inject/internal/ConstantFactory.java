// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$ToStringBuilder;
import com.google.inject.spi.Dependency;

final class ConstantFactory<T> implements InternalFactory<T>
{
    private final Initializable<T> initializable;
    
    public ConstantFactory(final Initializable<T> initializable) {
        this.initializable = initializable;
    }
    
    public T get(final Errors errors, final InternalContext context, final Dependency dependency, final boolean linked) throws ErrorsException {
        return this.initializable.get(errors);
    }
    
    @Override
    public String toString() {
        return new $ToStringBuilder(ConstantFactory.class).add("value", this.initializable).toString();
    }
}
