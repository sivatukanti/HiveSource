// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.header;

import com.sun.jersey.core.util.StringKeyStringValueIgnoreCaseMultivaluedMap;

public class InBoundHeaders extends StringKeyStringValueIgnoreCaseMultivaluedMap
{
    public InBoundHeaders() {
    }
    
    public InBoundHeaders(final InBoundHeaders that) {
        super(that);
    }
}
