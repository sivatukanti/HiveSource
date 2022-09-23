// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import net.jcip.annotations.Immutable;

@Immutable
public class Resource
{
    private final String content;
    private final String contentType;
    
    public Resource(final String content, final String contentType) {
        if (content == null) {
            throw new IllegalArgumentException("The resource content must not be null");
        }
        this.content = content;
        this.contentType = contentType;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public String getContentType() {
        return this.contentType;
    }
}
