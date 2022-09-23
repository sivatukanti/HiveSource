// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.Provider;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.MembersInjector;
import com.google.inject.spi.MembersInjectorLookup;

final class LookupProcessor extends AbstractProcessor
{
    LookupProcessor(final Errors errors) {
        super(errors);
    }
    
    @Override
    public <T> Boolean visit(final MembersInjectorLookup<T> lookup) {
        try {
            final MembersInjector<T> membersInjector = this.injector.membersInjectorStore.get(lookup.getType(), this.errors);
            lookup.initializeDelegate(membersInjector);
        }
        catch (ErrorsException e) {
            this.errors.merge(e.getErrors());
        }
        return true;
    }
    
    @Override
    public <T> Boolean visit(final ProviderLookup<T> lookup) {
        try {
            final Provider<T> provider = this.injector.getProviderOrThrow(lookup.getKey(), this.errors);
            lookup.initializeDelegate(provider);
        }
        catch (ErrorsException e) {
            this.errors.merge(e.getErrors());
        }
        return true;
    }
}
