// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.uri.rules;

import java.util.Iterator;

public interface UriRules<R>
{
    Iterator<R> match(final CharSequence p0, final UriMatchResultContext p1);
}
