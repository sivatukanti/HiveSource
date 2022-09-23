// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import java.lang.reflect.Field;
import com.google.inject.internal.util.$Lists;
import java.util.Iterator;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.ConfigurationException;
import java.util.Set;
import com.google.inject.spi.InjectionPoint;
import java.util.List;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeListenerBinding;
import com.google.inject.internal.util.$ImmutableList;

final class MembersInjectorStore
{
    private final InjectorImpl injector;
    private final $ImmutableList<TypeListenerBinding> typeListenerBindings;
    private final FailableCache<TypeLiteral<?>, MembersInjectorImpl<?>> cache;
    
    MembersInjectorStore(final InjectorImpl injector, final List<TypeListenerBinding> typeListenerBindings) {
        this.cache = new FailableCache<TypeLiteral<?>, MembersInjectorImpl<?>>() {
            @Override
            protected MembersInjectorImpl<?> create(final TypeLiteral<?> type, final Errors errors) throws ErrorsException {
                return MembersInjectorStore.this.createWithListeners(type, errors);
            }
        };
        this.injector = injector;
        this.typeListenerBindings = $ImmutableList.copyOf((Iterable<? extends TypeListenerBinding>)typeListenerBindings);
    }
    
    public boolean hasTypeListeners() {
        return !this.typeListenerBindings.isEmpty();
    }
    
    public <T> MembersInjectorImpl<T> get(final TypeLiteral<T> key, final Errors errors) throws ErrorsException {
        return (MembersInjectorImpl<T>)this.cache.get(key, errors);
    }
    
    boolean remove(final TypeLiteral<?> type) {
        return this.cache.remove(type);
    }
    
    private <T> MembersInjectorImpl<T> createWithListeners(final TypeLiteral<T> type, final Errors errors) throws ErrorsException {
        final int numErrorsBefore = errors.size();
        Set<InjectionPoint> injectionPoints;
        try {
            injectionPoints = InjectionPoint.forInstanceMethodsAndFields(type);
        }
        catch (ConfigurationException e) {
            errors.merge(e.getErrorMessages());
            injectionPoints = e.getPartialValue();
        }
        final $ImmutableList<SingleMemberInjector> injectors = this.getInjectors(injectionPoints, errors);
        errors.throwIfNewErrors(numErrorsBefore);
        final EncounterImpl<T> encounter = new EncounterImpl<T>(errors, this.injector.lookups);
        for (final TypeListenerBinding typeListener : this.typeListenerBindings) {
            if (typeListener.getTypeMatcher().matches(type)) {
                try {
                    typeListener.getListener().hear(type, encounter);
                }
                catch (RuntimeException e2) {
                    errors.errorNotifyingTypeListener(typeListener, type, e2);
                }
            }
        }
        encounter.invalidate();
        errors.throwIfNewErrors(numErrorsBefore);
        return new MembersInjectorImpl<T>(this.injector, type, encounter, injectors);
    }
    
    $ImmutableList<SingleMemberInjector> getInjectors(final Set<InjectionPoint> injectionPoints, final Errors errors) {
        final List<SingleMemberInjector> injectors = (List<SingleMemberInjector>)$Lists.newArrayList();
        for (final InjectionPoint injectionPoint : injectionPoints) {
            try {
                final Errors errorsForMember = injectionPoint.isOptional() ? new Errors(injectionPoint) : errors.withSource(injectionPoint);
                final SingleMemberInjector injector = (injectionPoint.getMember() instanceof Field) ? new SingleFieldInjector(this.injector, injectionPoint, errorsForMember) : new SingleMethodInjector(this.injector, injectionPoint, errorsForMember);
                injectors.add(injector);
            }
            catch (ErrorsException ex) {}
        }
        return $ImmutableList.copyOf((Iterable<? extends SingleMemberInjector>)injectors);
    }
}
