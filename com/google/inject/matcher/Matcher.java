// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.matcher;

public interface Matcher<T>
{
    boolean matches(final T p0);
    
    Matcher<T> and(final Matcher<? super T> p0);
    
    Matcher<T> or(final Matcher<? super T> p0);
}
