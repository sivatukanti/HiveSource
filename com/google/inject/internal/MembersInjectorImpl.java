// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.InjectionPoint;
import com.google.inject.internal.util.$ImmutableSet;
import java.util.Iterator;
import com.google.inject.spi.InjectionListener;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.TypeLiteral;
import com.google.inject.MembersInjector;

final class MembersInjectorImpl<T> implements MembersInjector<T>
{
    private final TypeLiteral<T> typeLiteral;
    private final InjectorImpl injector;
    private final $ImmutableList<SingleMemberInjector> memberInjectors;
    private final $ImmutableList<MembersInjector<? super T>> userMembersInjectors;
    private final $ImmutableList<InjectionListener<? super T>> injectionListeners;
    private final $ImmutableList<MethodAspect> addedAspects;
    
    MembersInjectorImpl(final InjectorImpl injector, final TypeLiteral<T> typeLiteral, final EncounterImpl<T> encounter, final $ImmutableList<SingleMemberInjector> memberInjectors) {
        this.injector = injector;
        this.typeLiteral = typeLiteral;
        this.memberInjectors = memberInjectors;
        this.userMembersInjectors = encounter.getMembersInjectors();
        this.injectionListeners = encounter.getInjectionListeners();
        this.addedAspects = encounter.getAspects();
    }
    
    public $ImmutableList<SingleMemberInjector> getMemberInjectors() {
        return this.memberInjectors;
    }
    
    public void injectMembers(final T instance) {
        final Errors errors = new Errors(this.typeLiteral);
        try {
            this.injectAndNotify(instance, errors, false);
        }
        catch (ErrorsException e) {
            errors.merge(e.getErrors());
        }
        errors.throwProvisionExceptionIfErrorsExist();
    }
    
    void injectAndNotify(final T instance, final Errors errors, final boolean toolableOnly) throws ErrorsException {
        if (instance == null) {
            return;
        }
        this.injector.callInContext((ContextualCallable<Object>)new ContextualCallable<Void>() {
            public Void call(final InternalContext context) throws ErrorsException {
                MembersInjectorImpl.this.injectMembers(instance, errors, context, toolableOnly);
                return null;
            }
        });
        if (!toolableOnly) {
            this.notifyListeners(instance, errors);
        }
    }
    
    void notifyListeners(final T instance, final Errors errors) throws ErrorsException {
        final int numErrorsBefore = errors.size();
        for (final InjectionListener<? super T> injectionListener : this.injectionListeners) {
            try {
                injectionListener.afterInjection((Object)instance);
            }
            catch (RuntimeException e) {
                errors.errorNotifyingInjectionListener(injectionListener, this.typeLiteral, e);
            }
        }
        errors.throwIfNewErrors(numErrorsBefore);
    }
    
    void injectMembers(final T t, final Errors errors, final InternalContext context, final boolean toolableOnly) {
        for (int i = 0, size = this.memberInjectors.size(); i < size; ++i) {
            final SingleMemberInjector injector = this.memberInjectors.get(i);
            if (!toolableOnly || injector.getInjectionPoint().isToolable()) {
                injector.inject(errors, context, t);
            }
        }
        if (!toolableOnly) {
            for (int i = 0, size = this.userMembersInjectors.size(); i < size; ++i) {
                final MembersInjector<? super T> userMembersInjector = this.userMembersInjectors.get(i);
                try {
                    userMembersInjector.injectMembers((Object)t);
                }
                catch (RuntimeException e) {
                    errors.errorInUserInjector(userMembersInjector, this.typeLiteral, e);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "MembersInjector<" + this.typeLiteral + ">";
    }
    
    public $ImmutableSet<InjectionPoint> getInjectionPoints() {
        final $ImmutableSet.Builder<InjectionPoint> builder = $ImmutableSet.builder();
        for (final SingleMemberInjector memberInjector : this.memberInjectors) {
            builder.add(memberInjector.getInjectionPoint());
        }
        return builder.build();
    }
    
    public $ImmutableList<MethodAspect> getAddedAspects() {
        return this.addedAspects;
    }
}
