// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.impl.provider.header;

import com.sun.jersey.spi.HeaderDelegateProvider;

public class StringProvider implements HeaderDelegateProvider<String>
{
    @Override
    public boolean supports(final Class<?> type) {
        return type == String.class;
    }
    
    @Override
    public String toString(final String header) {
        return header;
    }
    
    @Override
    public String fromString(final String header) {
        return header;
    }
}
