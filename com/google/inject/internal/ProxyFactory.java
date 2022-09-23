// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.InvocationTargetException;
import com.google.inject.internal.cglib.reflect.$FastClass;
import com.google.inject.internal.cglib.reflect.$FastConstructor;
import com.google.inject.internal.util.$Maps;
import java.util.Map;
import java.util.Collection;
import com.google.inject.internal.cglib.proxy.$CallbackFilter;
import com.google.inject.internal.cglib.proxy.$MethodInterceptor;
import java.util.Iterator;
import com.google.inject.internal.cglib.proxy.$NoOp;
import java.lang.reflect.Member;
import java.util.logging.Level;
import com.google.inject.internal.cglib.proxy.$Enhancer;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Lists;
import java.lang.reflect.Constructor;
import com.google.inject.internal.cglib.proxy.$Callback;
import org.aopalliance.intercept.MethodInterceptor;
import java.util.List;
import java.lang.reflect.Method;
import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.spi.InjectionPoint;
import java.util.logging.Logger;

final class ProxyFactory<T> implements ConstructionProxyFactory<T>
{
    private static final Logger logger;
    private final InjectionPoint injectionPoint;
    private final $ImmutableMap<Method, List<MethodInterceptor>> interceptors;
    private final Class<T> declaringClass;
    private final List<Method> methods;
    private final $Callback[] callbacks;
    private BytecodeGen.Visibility visibility;
    
    ProxyFactory(final InjectionPoint injectionPoint, final Iterable<MethodAspect> methodAspects) {
        this.visibility = BytecodeGen.Visibility.PUBLIC;
        this.injectionPoint = injectionPoint;
        final Constructor<T> constructor = (Constructor<T>)injectionPoint.getMember();
        this.declaringClass = constructor.getDeclaringClass();
        final List<MethodAspect> applicableAspects = (List<MethodAspect>)$Lists.newArrayList();
        for (final MethodAspect methodAspect : methodAspects) {
            if (methodAspect.matches(this.declaringClass)) {
                applicableAspects.add(methodAspect);
            }
        }
        if (applicableAspects.isEmpty()) {
            this.interceptors = $ImmutableMap.of();
            this.methods = (List<Method>)$ImmutableList.of();
            this.callbacks = null;
            return;
        }
        this.methods = (List<Method>)$Lists.newArrayList();
        $Enhancer.getMethods(this.declaringClass, null, this.methods);
        final List<MethodInterceptorsPair> methodInterceptorsPairs = (List<MethodInterceptorsPair>)$Lists.newArrayList();
        for (final Method method : this.methods) {
            methodInterceptorsPairs.add(new MethodInterceptorsPair(method));
        }
        boolean anyMatched = false;
        for (final MethodAspect methodAspect2 : applicableAspects) {
            for (final MethodInterceptorsPair pair : methodInterceptorsPairs) {
                if (methodAspect2.matches(pair.method)) {
                    if (pair.method.isSynthetic()) {
                        ProxyFactory.logger.log(Level.WARNING, "Method [{0}] is synthetic and is being intercepted by {1}. This could indicate a bug.  The method may be intercepted twice, or may not be intercepted at all.", new Object[] { pair.method, methodAspect2.interceptors() });
                    }
                    this.visibility = this.visibility.and(BytecodeGen.Visibility.forMember(pair.method));
                    pair.addAll(methodAspect2.interceptors());
                    anyMatched = true;
                }
            }
        }
        if (!anyMatched) {
            this.interceptors = $ImmutableMap.of();
            this.callbacks = null;
            return;
        }
        $ImmutableMap.Builder<Method, List<MethodInterceptor>> interceptorsMapBuilder = null;
        this.callbacks = new $Callback[this.methods.size()];
        for (int i = 0; i < this.methods.size(); ++i) {
            final MethodInterceptorsPair pair2 = methodInterceptorsPairs.get(i);
            if (!pair2.hasInterceptors()) {
                this.callbacks[i] = $NoOp.INSTANCE;
            }
            else {
                if (interceptorsMapBuilder == null) {
                    interceptorsMapBuilder = $ImmutableMap.builder();
                }
                interceptorsMapBuilder.put(pair2.method, (List<MethodInterceptor>)$ImmutableList.copyOf((Iterable<?>)pair2.interceptors));
                this.callbacks[i] = new InterceptorStackCallback(pair2.method, pair2.interceptors);
            }
        }
        this.interceptors = ((interceptorsMapBuilder != null) ? interceptorsMapBuilder.build() : $ImmutableMap.of());
    }
    
    public $ImmutableMap<Method, List<MethodInterceptor>> getInterceptors() {
        return this.interceptors;
    }
    
    public ConstructionProxy<T> create() throws ErrorsException {
        if (this.interceptors.isEmpty()) {
            return new DefaultConstructionProxyFactory<T>(this.injectionPoint).create();
        }
        final Class<? extends $Callback>[] callbackTypes = (Class<? extends $Callback>[])new Class[this.callbacks.length];
        for (int i = 0; i < this.callbacks.length; ++i) {
            if (this.callbacks[i] == $NoOp.INSTANCE) {
                callbackTypes[i] = $NoOp.class;
            }
            else {
                callbackTypes[i] = $MethodInterceptor.class;
            }
        }
        try {
            final $Enhancer enhancer = BytecodeGen.newEnhancer(this.declaringClass, this.visibility);
            enhancer.setCallbackFilter(new IndicesCallbackFilter(this.declaringClass, this.methods));
            enhancer.setCallbackTypes(callbackTypes);
            return new ProxyConstructor<T>(enhancer, this.injectionPoint, this.callbacks, this.interceptors);
        }
        catch (Throwable e) {
            throw new Errors().errorEnhancingClass(this.declaringClass, e).toException();
        }
    }
    
    static {
        logger = Logger.getLogger(ProxyFactory.class.getName());
    }
    
    private static class MethodInterceptorsPair
    {
        final Method method;
        List<MethodInterceptor> interceptors;
        
        MethodInterceptorsPair(final Method method) {
            this.method = method;
        }
        
        void addAll(final List<MethodInterceptor> interceptors) {
            if (this.interceptors == null) {
                this.interceptors = (List<MethodInterceptor>)$Lists.newArrayList();
            }
            this.interceptors.addAll(interceptors);
        }
        
        boolean hasInterceptors() {
            return this.interceptors != null;
        }
    }
    
    private static class IndicesCallbackFilter implements $CallbackFilter
    {
        final Class<?> declaringClass;
        final Map<Method, Integer> indices;
        
        IndicesCallbackFilter(final Class<?> declaringClass, final List<Method> methods) {
            this.declaringClass = declaringClass;
            final Map<Method, Integer> indices = (Map<Method, Integer>)$Maps.newHashMap();
            for (int i = 0; i < methods.size(); ++i) {
                final Method method = methods.get(i);
                indices.put(method, i);
            }
            this.indices = indices;
        }
        
        public int accept(final Method method) {
            return this.indices.get(method);
        }
        
        @Override
        public boolean equals(final Object o) {
            return o instanceof IndicesCallbackFilter && ((IndicesCallbackFilter)o).declaringClass == this.declaringClass;
        }
        
        @Override
        public int hashCode() {
            return this.declaringClass.hashCode();
        }
    }
    
    private static class ProxyConstructor<T> implements ConstructionProxy<T>
    {
        final Class<?> enhanced;
        final InjectionPoint injectionPoint;
        final Constructor<T> constructor;
        final $Callback[] callbacks;
        final $FastConstructor fastConstructor;
        final $ImmutableMap<Method, List<MethodInterceptor>> methodInterceptors;
        
        ProxyConstructor(final $Enhancer enhancer, final InjectionPoint injectionPoint, final $Callback[] callbacks, final $ImmutableMap<Method, List<MethodInterceptor>> methodInterceptors) {
            this.enhanced = (Class<?>)enhancer.createClass();
            this.injectionPoint = injectionPoint;
            this.constructor = (Constructor<T>)injectionPoint.getMember();
            this.callbacks = callbacks;
            this.methodInterceptors = methodInterceptors;
            final $FastClass fastClass = BytecodeGen.newFastClass(this.enhanced, BytecodeGen.Visibility.forMember(this.constructor));
            this.fastConstructor = fastClass.getConstructor(this.constructor.getParameterTypes());
        }
        
        public T newInstance(final Object... arguments) throws InvocationTargetException {
            $Enhancer.registerCallbacks(this.enhanced, this.callbacks);
            try {
                return (T)this.fastConstructor.newInstance(arguments);
            }
            finally {
                $Enhancer.registerCallbacks(this.enhanced, null);
            }
        }
        
        public InjectionPoint getInjectionPoint() {
            return this.injectionPoint;
        }
        
        public Constructor<T> getConstructor() {
            return this.constructor;
        }
        
        public $ImmutableMap<Method, List<MethodInterceptor>> getMethodInterceptors() {
            return this.methodInterceptors;
        }
    }
}
