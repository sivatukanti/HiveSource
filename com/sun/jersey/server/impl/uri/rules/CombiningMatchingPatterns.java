// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri.rules;

import java.util.NoSuchElementException;
import java.util.Iterator;
import com.sun.jersey.spi.uri.rules.UriMatchResultContext;
import java.util.List;
import com.sun.jersey.spi.uri.rules.UriRules;

public class CombiningMatchingPatterns<R> implements UriRules<R>
{
    private final List<UriRules<R>> rs;
    
    public CombiningMatchingPatterns(final List<UriRules<R>> rs) {
        this.rs = rs;
    }
    
    @Override
    public Iterator<R> match(final CharSequence path, final UriMatchResultContext resultContext) {
        return new XInterator(path, resultContext);
    }
    
    private final class XInterator implements Iterator<R>
    {
        private final CharSequence path;
        private final UriMatchResultContext resultContext;
        private Iterator<R> ruleIterator;
        private Iterator<UriRules<R>> rulesIterator;
        private R r;
        
        XInterator(final CharSequence path, final UriMatchResultContext resultContext) {
            this.path = path;
            this.resultContext = resultContext;
            this.rulesIterator = CombiningMatchingPatterns.this.rs.iterator();
            this.ruleIterator = this.rulesIterator.next().match(path, resultContext);
        }
        
        @Override
        public boolean hasNext() {
            if (this.r != null) {
                return true;
            }
            if (this.ruleIterator.hasNext()) {
                this.r = this.ruleIterator.next();
                return true;
            }
            while (this.rulesIterator.hasNext()) {
                this.ruleIterator = this.rulesIterator.next().match(this.path, this.resultContext);
                if (this.ruleIterator.hasNext()) {
                    this.r = this.ruleIterator.next();
                    return true;
                }
            }
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
