// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core;

public class JsonParseException extends JsonProcessingException
{
    private static final long serialVersionUID = 1L;
    
    public JsonParseException(final String msg, final JsonLocation loc) {
        super(msg, loc);
    }
    
    public JsonParseException(final String msg, final JsonLocation loc, final Throwable root) {
        super(msg, loc, root);
    }
}
