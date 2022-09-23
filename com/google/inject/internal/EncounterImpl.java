// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.Provider;
import com.google.inject.Key;
import com.google.inject.spi.Message;
import com.google.inject.matcher.Matchers;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Preconditions;
import org.aopalliance.intercept.MethodInterceptor;
import java.lang.reflect.Method;
import com.google.inject.matcher.Matcher;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.spi.InjectionListener;
import com.google.inject.MembersInjector;
import java.util.List;
import com.google.inject.spi.TypeEncounter;

final class EncounterImpl<T> implements TypeEncounter<T>
{
    private final Errors errors;
    private final Lookups lookups;
    private List<MembersInjector<? super T>> membersInjectors;
    private List<InjectionListener<? super T>> injectionListeners;
    private List<MethodAspect> aspects;
    private boolean valid;
    
    EncounterImpl(final Errors errors, final Lookups lookups) {
        this.valid = true;
        this.errors = errors;
        this.lookups = lookups;
    }
    
    void invalidate() {
        this.valid = false;
    }
    
    $ImmutableList<MethodAspect> getAspects() {
        return (this.aspects == null) ? $ImmutableList.of() : $ImmutableList.copyOf((Iterable<? extends MethodAspect>)this.aspects);
    }
    
    public void bindInterceptor(final Matcher<? super Method> methodMatcher, final MethodInterceptor... interceptors) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        if (this.aspects == null) {
            this.aspects = (List<MethodAspect>)$Lists.newArrayList();
        }
        this.aspects.add(new MethodAspect(Matchers.any(), methodMatcher, interceptors));
    }
    
    $ImmutableList<MembersInjector<? super T>> getMembersInjectors() {
        return (this.membersInjectors == null) ? $ImmutableList.of() : $ImmutableList.copyOf((Iterable<? extends MembersInjector<? super T>>)this.membersInjectors);
    }
    
    $ImmutableList<InjectionListener<? super T>> getInjectionListeners() {
        return (this.injectionListeners == null) ? $ImmutableList.of() : $ImmutableList.copyOf((Iterable<? extends InjectionListener<? super T>>)this.injectionListeners);
    }
    
    public void register(final MembersInjector<? super T> membersInjector) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        if (this.membersInjectors == null) {
            this.membersInjectors = (List<MembersInjector<? super T>>)$Lists.newArrayList();
        }
        this.membersInjectors.add(membersInjector);
    }
    
    public void register(final InjectionListener<? super T> injectionListener) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        if (this.injectionListeners == null) {
            this.injectionListeners = (List<InjectionListener<? super T>>)$Lists.newArrayList();
        }
        this.injectionListeners.add(injectionListener);
    }
    
    public void addError(final String message, final Object... arguments) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        this.errors.addMessage(message, arguments);
    }
    
    public void addError(final Throwable t) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        this.errors.errorInUserCode(t, "An exception was caught and reported. Message: %s", t.getMessage());
    }
    
    public void addError(final Message message) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        this.errors.addMessage(message);
    }
    
    public <T> Provider<T> getProvider(final Key<T> key) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        return this.lookups.getProvider(key);
    }
    
    public <T> Provider<T> getProvider(final Class<T> type) {
        return this.getProvider((Key<T>)Key.get((Class<T>)type));
    }
    
    public <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> typeLiteral) {
        $Preconditions.checkState(this.valid, (Object)"Encounters may not be used after hear() returns.");
        return this.lookups.getMembersInjector(typeLiteral);
    }
    
    public <T> MembersInjector<T> getMembersInjector(final Class<T> type) {
        return this.getMembersInjector((TypeLiteral<T>)TypeLiteral.get((Class<T>)type));
    }
}
