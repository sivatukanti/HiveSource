// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.inject;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.inject.Injectable;

public abstract class AbstractHttpContextInjectable<T> implements Injectable<T>
{
    @Override
    public T getValue() {
        throw new IllegalStateException();
    }
    
    public abstract T getValue(final HttpContext p0);
    
    public static List<AbstractHttpContextInjectable> transform(final List<Injectable> l) {
        final List<AbstractHttpContextInjectable> al = new ArrayList<AbstractHttpContextInjectable>(l.size());
        for (final Injectable i : l) {
            al.add(transform(i));
        }
        return al;
    }
    
    public static AbstractHttpContextInjectable transform(final Injectable i) {
        if (i == null) {
            return null;
        }
        if (i instanceof AbstractHttpContextInjectable) {
            return (AbstractHttpContextInjectable)i;
        }
        return new AbstractHttpContextInjectable() {
            @Override
            public Object getValue(final HttpContext c) {
                return i.getValue();
            }
        };
    }
}
