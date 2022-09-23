// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Map;

public class LinkHeaders
{
    private final Map<String, LinkHeader> map;
    
    public LinkHeaders(final MultivaluedMap<String, String> headers) throws IllegalArgumentException {
        final List<String> ls = headers.get("Link");
        if (ls != null) {
            this.map = new HashMap<String, LinkHeader>();
            for (final String l : ls) {
                final LinkHeader lh = LinkHeader.valueOf(l);
                for (final String rel : lh.getRel()) {
                    this.map.put(rel, lh);
                }
            }
        }
        else {
            this.map = Collections.emptyMap();
        }
    }
    
    public LinkHeader getLink(final String rel) throws IllegalArgumentException {
        return this.map.get(rel);
    }
}
