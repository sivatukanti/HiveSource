// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Field;

final class SingleFieldInjector implements SingleMemberInjector
{
    final Field field;
    final InjectionPoint injectionPoint;
    final Dependency<?> dependency;
    final InternalFactory<?> factory;
    
    public SingleFieldInjector(final InjectorImpl injector, final InjectionPoint injectionPoint, final Errors errors) throws ErrorsException {
        this.injectionPoint = injectionPoint;
        this.field = (Field)injectionPoint.getMember();
        this.dependency = injectionPoint.getDependencies().get(0);
        this.field.setAccessible(true);
        this.factory = injector.getInternalFactory(this.dependency.getKey(), errors, InjectorImpl.JitLimitation.NO_JIT);
    }
    
    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }
    
    public void inject(Errors errors, final InternalContext context, final Object o) {
        errors = errors.withSource(this.dependency);
        final Dependency previous = context.setDependency(this.dependency);
        try {
            final Object value = this.factory.get(errors, context, this.dependency, false);
            this.field.set(o, value);
        }
        catch (ErrorsException e) {
            errors.withSource(this.injectionPoint).merge(e.getErrors());
        }
        catch (IllegalAccessException e2) {
            throw new AssertionError((Object)e2);
        }
        finally {
            context.setDependency(previous);
        }
    }
}
