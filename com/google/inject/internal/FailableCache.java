// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$MapMaker;
import java.util.Map;

public abstract class FailableCache<K, V>
{
    private final Map<K, Object> delegate;
    
    public FailableCache() {
        this.delegate = (Map<K, Object>)new $MapMaker().makeComputingMap(($Function<? super Object, ?>)new $Function<K, Object>() {
            public Object apply(@$Nullable final K key) {
                final Errors errors = new Errors();
                V result = null;
                try {
                    result = FailableCache.this.create(key, errors);
                }
                catch (ErrorsException e) {
                    errors.merge(e.getErrors());
                }
                return errors.hasErrors() ? errors : result;
            }
        });
    }
    
    protected abstract V create(final K p0, final Errors p1) throws ErrorsException;
    
    public V get(final K key, final Errors errors) throws ErrorsException {
        final Object resultOrError = this.delegate.get(key);
        if (resultOrError instanceof Errors) {
            errors.merge((Errors)resultOrError);
            throw errors.toException();
        }
        final V result = (V)resultOrError;
        return result;
    }
    
    boolean remove(final K key) {
        return this.delegate.remove(key) != null;
    }
}
