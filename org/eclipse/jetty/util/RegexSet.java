// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.function.Predicate;
import java.util.AbstractSet;

public class RegexSet extends AbstractSet<String> implements Predicate<String>
{
    private final Set<String> _patterns;
    private final Set<String> _unmodifiable;
    private Pattern _pattern;
    
    public RegexSet() {
        this._patterns = new HashSet<String>();
        this._unmodifiable = Collections.unmodifiableSet((Set<? extends String>)this._patterns);
    }
    
    @Override
    public Iterator<String> iterator() {
        return this._unmodifiable.iterator();
    }
    
    @Override
    public int size() {
        return this._patterns.size();
    }
    
    @Override
    public boolean add(final String pattern) {
        final boolean added = this._patterns.add(pattern);
        if (added) {
            this.updatePattern();
        }
        return added;
    }
    
    @Override
    public boolean remove(final Object pattern) {
        final boolean removed = this._patterns.remove(pattern);
        if (removed) {
            this.updatePattern();
        }
        return removed;
    }
    
    @Override
    public boolean isEmpty() {
        return this._patterns.isEmpty();
    }
    
    @Override
    public void clear() {
        this._patterns.clear();
        this._pattern = null;
    }
    
    private void updatePattern() {
        final StringBuilder builder = new StringBuilder();
        builder.append("^(");
        for (final String pattern : this._patterns) {
            if (builder.length() > 2) {
                builder.append('|');
            }
            builder.append('(');
            builder.append(pattern);
            builder.append(')');
        }
        builder.append(")$");
        this._pattern = Pattern.compile(builder.toString());
    }
    
    @Override
    public boolean test(final String s) {
        return this._pattern != null && this._pattern.matcher(s).matches();
    }
    
    public boolean matches(final String s) {
        return this._pattern != null && this._pattern.matcher(s).matches();
    }
}
