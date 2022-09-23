// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.$Lists;
import java.util.Iterator;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.spi.InjectionPoint;
import java.util.Set;
import com.google.inject.internal.util.$Maps;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

final class Initializer
{
    private final Thread creatingThread;
    private final CountDownLatch ready;
    private final Map<Object, InjectableReference<?>> pendingInjection;
    
    Initializer() {
        this.creatingThread = Thread.currentThread();
        this.ready = new CountDownLatch(1);
        this.pendingInjection = (Map<Object, InjectableReference<?>>)$Maps.newIdentityHashMap();
    }
    
     <T> Initializable<T> requestInjection(final InjectorImpl injector, final T instance, final Object source, final Set<InjectionPoint> injectionPoints) {
        $Preconditions.checkNotNull(source);
        if (instance == null || (injectionPoints.isEmpty() && !injector.membersInjectorStore.hasTypeListeners())) {
            return Initializables.of(instance);
        }
        final InjectableReference<T> initializable = new InjectableReference<T>(injector, instance, source);
        this.pendingInjection.put(instance, initializable);
        return initializable;
    }
    
    void validateOustandingInjections(final Errors errors) {
        for (final InjectableReference<?> reference : this.pendingInjection.values()) {
            try {
                reference.validate(errors);
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
    }
    
    void injectAll(final Errors errors) {
        for (final InjectableReference<?> reference : $Lists.newArrayList((Iterable<?>)this.pendingInjection.values())) {
            try {
                reference.get(errors);
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
        if (!this.pendingInjection.isEmpty()) {
            throw new AssertionError((Object)("Failed to satisfy " + this.pendingInjection));
        }
        this.ready.countDown();
    }
    
    private class InjectableReference<T> implements Initializable<T>
    {
        private final InjectorImpl injector;
        private final T instance;
        private final Object source;
        private MembersInjectorImpl<T> membersInjector;
        
        public InjectableReference(final InjectorImpl injector, final T instance, final Object source) {
            this.injector = injector;
            this.instance = $Preconditions.checkNotNull(instance, (Object)"instance");
            this.source = $Preconditions.checkNotNull(source, (Object)"source");
        }
        
        public void validate(final Errors errors) throws ErrorsException {
            final TypeLiteral<T> type = TypeLiteral.get(this.instance.getClass());
            this.membersInjector = this.injector.membersInjectorStore.get(type, errors.withSource(this.source));
        }
        
        public T get(final Errors errors) throws ErrorsException {
            if (Initializer.this.ready.getCount() == 0L) {
                return this.instance;
            }
            if (Thread.currentThread() != Initializer.this.creatingThread) {
                try {
                    Initializer.this.ready.await();
                    return this.instance;
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Initializer.this.pendingInjection.remove(this.instance) != null) {
                this.membersInjector.injectAndNotify(this.instance, errors.withSource(this.source), this.injector.options.stage == Stage.TOOL);
            }
            return this.instance;
        }
        
        @Override
        public String toString() {
            return this.instance.toString();
        }
    }
}
