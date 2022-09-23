// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.core.header.LinkHeader;
import com.sun.jersey.spi.HeaderDelegateProvider;

public class LinkHeaderProvider implements HeaderDelegateProvider<LinkHeader>
{
    @Override
    public boolean supports(final Class<?> type) {
        return LinkHeader.class.isAssignableFrom(type);
    }
    
    @Override
    public LinkHeader fromString(final String value) throws IllegalArgumentException {
        return LinkHeader.valueOf(value);
    }
    
    @Override
    public String toString(final LinkHeader value) {
        return value.toString();
    }
}
