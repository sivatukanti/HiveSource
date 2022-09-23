// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import java.util.Collection;
import com.sun.jersey.spi.uri.rules.UriRules;

public final class AtomicMatchingPatterns<R> implements UriRules<R>
{
    private final Collection<PatternRulePair<R>> rules;
    
    public AtomicMatchingPatterns(final Collection<PatternRulePair<R>> rules) {
        this.rules = rules;
    }
    
    @Override
    public Iterator<R> match(final CharSequence path, final UriMatchResultContext resultContext) {
        if (resultContext.isTracingEnabled()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("match path \"").append(path).append("\" -> ");
            boolean first = true;
            for (final PatternRulePair<R> prp : this.rules) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append("\"").append(prp.p.toString()).append("\"");
                first = false;
            }
            resultContext.trace(sb.toString());
        }
        for (final PatternRulePair<R> prp2 : this.rules) {
            final MatchResult mr = prp2.p.match(path);
            if (mr != null) {
                resultContext.setMatchResult(mr);
                return new SingleEntryIterator<R>(prp2.r);
            }
        }
        return new EmptyIterator<R>();
    }
    
    private static final class SingleEntryIterator<T> implements Iterator<T>
    {
        private T t;
        
        SingleEntryIterator(final T t) {
            this.t = t;
        }
        
        @Override
        public boolean hasNext() {
            return this.t != null;
        }
        
        @Override
        public T next() {
            if (this.hasNext()) {
                final T _t = this.t;
                this.t = null;
                return _t;
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private static final class EmptyIterator<T> implements Iterator<T>
    {
        @Override
        public boolean hasNext() {
            return false;
        }
        
        @Override
        public T next() {
            throw new NoSuchElementException();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
