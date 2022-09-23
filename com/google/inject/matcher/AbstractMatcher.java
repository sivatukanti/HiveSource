// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.matcher;

import java.io.Serializable;

public abstract class AbstractMatcher<T> implements Matcher<T>
{
    public Matcher<T> and(final Matcher<? super T> other) {
        return new AndMatcher<T>(this, other);
    }
    
    public Matcher<T> or(final Matcher<? super T> other) {
        return new OrMatcher<T>(this, other);
    }
    
    private static class AndMatcher<T> extends AbstractMatcher<T> implements Serializable
    {
        private final Matcher<? super T> a;
        private final Matcher<? super T> b;
        private static final long serialVersionUID = 0L;
        
        public AndMatcher(final Matcher<? super T> a, final Matcher<? super T> b) {
            this.a = a;
            this.b = b;
        }
        
        public boolean matches(final T t) {
            return this.a.matches((Object)t) && this.b.matches((Object)t);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof AndMatcher && ((AndMatcher)other).a.equals(this.a) && ((AndMatcher)other).b.equals(this.b);
        }
        
        @Override
        public int hashCode() {
            return 41 * (this.a.hashCode() ^ this.b.hashCode());
        }
        
        @Override
        public String toString() {
            return "and(" + this.a + ", " + this.b + ")";
        }
    }
    
    private static class OrMatcher<T> extends AbstractMatcher<T> implements Serializable
    {
        private final Matcher<? super T> a;
        private final Matcher<? super T> b;
        private static final long serialVersionUID = 0L;
        
        public OrMatcher(final Matcher<? super T> a, final Matcher<? super T> b) {
            this.a = a;
            this.b = b;
        }
        
        public boolean matches(final T t) {
            return this.a.matches((Object)t) || this.b.matches((Object)t);
        }
        
        @Override
        public boolean equals(final Object other) {
            return other instanceof OrMatcher && ((OrMatcher)other).a.equals(this.a) && ((OrMatcher)other).b.equals(this.b);
        }
        
        @Override
        public int hashCode() {
            return 37 * (this.a.hashCode() ^ this.b.hashCode());
        }
        
        @Override
        public String toString() {
            return "or(" + this.a + ", " + this.b + ")";
        }
    }
}
