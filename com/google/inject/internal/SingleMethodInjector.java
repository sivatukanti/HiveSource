// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.cglib.core.$CodeGenerationException;
import java.lang.reflect.InvocationTargetException;
import com.google.inject.internal.cglib.reflect.$FastMethod;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import com.google.inject.spi.InjectionPoint;

final class SingleMethodInjector implements SingleMemberInjector
{
    private final InjectorImpl.MethodInvoker methodInvoker;
    private final SingleParameterInjector<?>[] parameterInjectors;
    private final InjectionPoint injectionPoint;
    
    SingleMethodInjector(final InjectorImpl injector, final InjectionPoint injectionPoint, final Errors errors) throws ErrorsException {
        this.injectionPoint = injectionPoint;
        final Method method = (Method)injectionPoint.getMember();
        this.methodInvoker = this.createMethodInvoker(method);
        this.parameterInjectors = injector.getParametersInjectors(injectionPoint.getDependencies(), errors);
    }
    
    private InjectorImpl.MethodInvoker createMethodInvoker(final Method method) {
        final int modifiers = method.getModifiers();
        if (!Modifier.isPrivate(modifiers) && !Modifier.isProtected(modifiers)) {
            try {
                final $FastMethod fastMethod = BytecodeGen.newFastClass(method.getDeclaringClass(), BytecodeGen.Visibility.forMember(method)).getMethod(method);
                return new InjectorImpl.MethodInvoker() {
                    public Object invoke(final Object target, final Object... parameters) throws IllegalAccessException, InvocationTargetException {
                        return fastMethod.invoke(target, parameters);
                    }
                };
            }
            catch ($CodeGenerationException ex) {}
        }
        if (!Modifier.isPublic(modifiers) || !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
            method.setAccessible(true);
        }
        return new InjectorImpl.MethodInvoker() {
            public Object invoke(final Object target, final Object... parameters) throws IllegalAccessException, InvocationTargetException {
                return method.invoke(target, parameters);
            }
        };
    }
    
    public InjectionPoint getInjectionPoint() {
        return this.injectionPoint;
    }
    
    public void inject(final Errors errors, final InternalContext context, final Object o) {
        Object[] parameters;
        try {
            parameters = SingleParameterInjector.getAll(errors, context, this.parameterInjectors);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
            return;
        }
        try {
            this.methodInvoker.invoke(o, parameters);
        }
        catch (IllegalAccessException e2) {
            throw new AssertionError((Object)e2);
        }
        catch (InvocationTargetException userException) {
            final Throwable cause = (userException.getCause() != null) ? userException.getCause() : userException;
            errors.withSource(this.injectionPoint).errorInjectingMethod(cause);
        }
    }
}
