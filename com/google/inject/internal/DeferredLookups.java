// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.Provider;
import com.google.inject.Key;
import com.google.inject.internal.util.$Lists;
import com.google.inject.spi.Element;
import java.util.List;

final class DeferredLookups implements Lookups
{
    private final InjectorImpl injector;
    private final List<Element> lookups;
    
    DeferredLookups(final InjectorImpl injector) {
        this.lookups = (List<Element>)$Lists.newArrayList();
        this.injector = injector;
    }
    
    void initialize(final Errors errors) {
        this.injector.lookups = this.injector;
        new LookupProcessor(errors).process(this.injector, this.lookups);
    }
    
    public <T> Provider<T> getProvider(final Key<T> key) {
        final ProviderLookup<T> lookup = new ProviderLookup<T>(key, key);
        this.lookups.add(lookup);
        return lookup.getProvider();
    }
    
    public <T> MembersInjector<T> getMembersInjector(final TypeLiteral<T> type) {
        final MembersInjectorLookup<T> lookup = new MembersInjectorLookup<T>(type, type);
        this.lookups.add(lookup);
        return lookup.getMembersInjector();
    }
}
