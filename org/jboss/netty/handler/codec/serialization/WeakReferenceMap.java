// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.serialization;

import java.util.Collection;
import java.util.Set;
import java.lang.ref.WeakReference;
import java.lang.ref.Reference;
import java.util.Map;

public class WeakReferenceMap<K, V> extends ReferenceMap<K, V>
{
    public WeakReferenceMap(final Map<K, Reference<V>> delegate) {
        super(delegate);
    }
    
    @Override
    Reference<V> fold(final V value) {
        return new WeakReference<V>(value);
    }
}
