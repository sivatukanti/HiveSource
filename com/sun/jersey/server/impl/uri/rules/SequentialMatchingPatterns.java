// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import java.util.List;
import com.sun.jersey.spi.uri.rules.UriRules;

public final class SequentialMatchingPatterns<R> implements UriRules<R>
{
    private final List<PatternRulePair<R>> rules;
    
    public SequentialMatchingPatterns(final List<PatternRulePair<R>> rules) {
        this.rules = rules;
    }
    
    @Override
    public Iterator<R> match(final CharSequence path, final UriMatchResultContext resultContext) {
        return new XInterator(path, resultContext);
    }
    
    private final class XInterator implements Iterator<R>
    {
        private final CharSequence path;
        private final UriMatchResultContext resultContext;
        private final Iterator<PatternRulePair<R>> i;
        private R r;
        
        XInterator(final CharSequence path, final UriMatchResultContext resultContext) {
            this.path = path;
            this.resultContext = resultContext;
            this.i = SequentialMatchingPatterns.this.rules.iterator();
        }
        
        @Override
        public boolean hasNext() {
            if (this.r != null) {
                return true;
            }
            while (this.i.hasNext()) {
                final PatternRulePair<R> prp = this.i.next();
                final MatchResult mr = prp.p.match(this.path);
                if (mr != null) {
                    this.resultContext.setMatchResult(mr);
                    this.r = prp.r;
                    return true;
                }
            }
            this.r = null;
            return false;
        }
        
        @Override
        public R next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            final R _r = this.r;
            this.r = null;
            return _r;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
