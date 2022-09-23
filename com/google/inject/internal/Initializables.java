// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal;

final class Initializables
{
    static <T> Initializable<T> of(final T instance) {
        return new Initializable<T>() {
            public T get(final Errors errors) throws ErrorsException {
                return instance;
            }
            
            @Override
            public String toString() {
                return String.valueOf(instance);
            }
        };
    }
}
