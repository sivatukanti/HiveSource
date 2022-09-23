// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose;

import java.util.Collection;
import net.jcip.annotations.Immutable;
import java.util.LinkedHashSet;

@Immutable
class AlgorithmFamily<T extends Algorithm> extends LinkedHashSet<T>
{
    private static final long serialVersionUID = 1L;
    
    public AlgorithmFamily(final T... algs) {
        for (final T alg : algs) {
            super.add(alg);
        }
    }
    
    @Override
    public boolean add(final T alg) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> algs) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
}
