// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Set;
import java.util.Collection;

public final class $Collections2
{
    private $Collections2() {
    }
    
    static <E> Collection<E> toCollection(final Iterable<E> iterable) {
        return (Collection<E>)((iterable instanceof Collection) ? ((Collection)iterable) : $Lists.newArrayList((Iterable<?>)iterable));
    }
    
    static boolean setEquals(final Set<?> thisSet, @$Nullable final Object object) {
        if (object == thisSet) {
            return true;
        }
        if (object instanceof Set) {
            final Set<?> thatSet = (Set<?>)object;
            return thisSet.size() == thatSet.size() && thisSet.containsAll(thatSet);
        }
        return false;
    }
}
