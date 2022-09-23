// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.InterceptorBinding;

final class InterceptorBindingProcessor extends AbstractProcessor
{
    InterceptorBindingProcessor(final Errors errors) {
        super(errors);
    }
    
    @Override
    public Boolean visit(final InterceptorBinding command) {
        this.injector.state.addMethodAspect(new MethodAspect(command.getClassMatcher(), command.getMethodMatcher(), command.getInterceptors()));
        return true;
    }
}
