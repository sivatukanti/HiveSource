// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.TypeListenerBinding;

final class TypeListenerBindingProcessor extends AbstractProcessor
{
    TypeListenerBindingProcessor(final Errors errors) {
        super(errors);
    }
    
    @Override
    public Boolean visit(final TypeListenerBinding binding) {
        this.injector.state.addTypeListener(binding);
        return true;
    }
}
