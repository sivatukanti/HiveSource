// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class IncludeExcludeSet<P, T> implements Predicate<T>
{
    private final Set<P> _includes;
    private final Predicate<T> _includePredicate;
    private final Set<P> _excludes;
    private final Predicate<T> _excludePredicate;
    
    public IncludeExcludeSet() {
        this(HashSet.class);
    }
    
    public <SET extends Set<P>> IncludeExcludeSet(final Class<SET> setClass) {
        try {
            this._includes = setClass.newInstance();
            this._excludes = setClass.newInstance();
            if (this._includes instanceof Predicate) {
                this._includePredicate = (Predicate<T>)(Predicate)this._includes;
            }
            else {
                this._includePredicate = new SetContainsPredicate<T>((Set<T>)this._includes);
            }
            if (this._excludes instanceof Predicate) {
                this._excludePredicate = (Predicate<T>)(Predicate)this._excludes;
            }
            else {
                this._excludePredicate = new SetContainsPredicate<T>((Set<T>)this._excludes);
            }
        }
        catch (InstantiationException | IllegalAccessException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            throw new RuntimeException(e);
        }
    }
    
    public <SET extends Set<P>> IncludeExcludeSet(final Set<P> includeSet, final Predicate<T> includePredicate, final Set<P> excludeSet, final Predicate<T> excludePredicate) {
        Objects.requireNonNull(includeSet, "Include Set");
        Objects.requireNonNull(includePredicate, "Include Predicate");
        Objects.requireNonNull(excludeSet, "Exclude Set");
        Objects.requireNonNull(excludePredicate, "Exclude Predicate");
        this._includes = includeSet;
        this._includePredicate = includePredicate;
        this._excludes = excludeSet;
        this._excludePredicate = excludePredicate;
    }
    
    public void include(final P element) {
        this._includes.add(element);
    }
    
    public void include(final P... element) {
        for (final P e : element) {
            this._includes.add(e);
        }
    }
    
    public void exclude(final P element) {
        this._excludes.add(element);
    }
    
    public void exclude(final P... element) {
        for (final P e : element) {
            this._excludes.add(e);
        }
    }
    
    public boolean matches(final T t) {
        return this.test(t);
    }
    
    @Override
    public boolean test(final T t) {
        return (this._includes.isEmpty() || this._includePredicate.test(t)) && !this._excludePredicate.test(t);
    }
    
    public int size() {
        return this._includes.size() + this._excludes.size();
    }
    
    public Set<P> getIncluded() {
        return this._includes;
    }
    
    public Set<P> getExcluded() {
        return this._excludes;
    }
    
    public void clear() {
        this._includes.clear();
        this._excludes.clear();
    }
    
    @Override
    public String toString() {
        return String.format("%s@%x{i=%s,ip=%s,e=%s,ep=%s}", this.getClass().getSimpleName(), this.hashCode(), this._includes, this._includePredicate, this._excludes, this._excludePredicate);
    }
    
    public boolean isEmpty() {
        return this._includes.isEmpty() && this._excludes.isEmpty();
    }
    
    private static class SetContainsPredicate<T> implements Predicate<T>
    {
        private final Set<T> set;
        
        public SetContainsPredicate(final Set<T> set) {
            this.set = set;
        }
        
        @Override
        public boolean test(final T item) {
            return this.set.contains(item);
        }
    }
}
