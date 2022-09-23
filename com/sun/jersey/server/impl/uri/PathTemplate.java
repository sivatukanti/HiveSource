// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.uri;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.api.uri.UriTemplateParser;
import com.sun.jersey.api.uri.UriTemplate;

public final class PathTemplate extends UriTemplate
{
    public PathTemplate(final String path) {
        super(new PathTemplateParser(prefixWithSlash(path)));
    }
    
    private static String prefixWithSlash(final String path) {
        return path.startsWith("/") ? path : ("/" + path);
    }
    
    private static final class PathTemplateParser extends UriTemplateParser
    {
        public PathTemplateParser(final String path) {
            super(path);
        }
        
        @Override
        protected String encodeLiteralCharacters(final String literalCharacters) {
            return UriComponent.contextualEncode(literalCharacters, UriComponent.Type.PATH);
        }
    }
}
