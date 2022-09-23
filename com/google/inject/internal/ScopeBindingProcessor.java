// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Scope;
import com.google.inject.internal.util.$Preconditions;
import java.lang.annotation.Annotation;
import com.google.inject.spi.ScopeBinding;

final class ScopeBindingProcessor extends AbstractProcessor
{
    ScopeBindingProcessor(final Errors errors) {
        super(errors);
    }
    
    @Override
    public Boolean visit(final ScopeBinding command) {
        final Scope scope = command.getScope();
        final Class<? extends Annotation> annotationType = command.getAnnotationType();
        if (!Annotations.isScopeAnnotation(annotationType)) {
            this.errors.withSource(annotationType).missingScopeAnnotation();
        }
        if (!Annotations.isRetainedAtRuntime(annotationType)) {
            this.errors.withSource(annotationType).missingRuntimeRetention(command.getSource());
        }
        final Scope existing = this.injector.state.getScope($Preconditions.checkNotNull(annotationType, (Object)"annotation type"));
        if (existing != null) {
            this.errors.duplicateScopes(existing, annotationType, scope);
        }
        else {
            this.injector.state.putAnnotation(annotationType, $Preconditions.checkNotNull(scope, (Object)"scope"));
        }
        return true;
    }
}
