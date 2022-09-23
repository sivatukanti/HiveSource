// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;

public class HostMap<TYPE> extends HashMap<String, TYPE>
{
    public HostMap() {
        super(11);
    }
    
    public HostMap(final int capacity) {
        super(capacity);
    }
    
    @Override
    public TYPE put(final String host, final TYPE object) throws IllegalArgumentException {
        return super.put(host, object);
    }
    
    @Override
    public TYPE get(final Object key) {
        return super.get(key);
    }
    
    public Object getLazyMatches(final String host) {
        if (host == null) {
            return LazyList.getList(super.entrySet());
        }
        int idx = 0;
        String domain = host.trim();
        final HashSet<String> domains = new HashSet<String>();
        do {
            domains.add(domain);
            if ((idx = domain.indexOf(46)) > 0) {
                domain = domain.substring(idx + 1);
            }
        } while (idx > 0);
        Object entries = null;
        for (final Map.Entry<String, TYPE> entry : super.entrySet()) {
            if (domains.contains(entry.getKey())) {
                entries = LazyList.add(entries, entry);
            }
        }
        return entries;
    }
}
