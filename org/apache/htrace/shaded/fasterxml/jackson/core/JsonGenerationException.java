// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core;

public class JsonGenerationException extends JsonProcessingException
{
    private static final long serialVersionUID = 123L;
    
    public JsonGenerationException(final Throwable rootCause) {
        super(rootCause);
    }
    
    public JsonGenerationException(final String msg) {
        super(msg, (JsonLocation)null);
    }
    
    public JsonGenerationException(final String msg, final Throwable rootCause) {
        super(msg, null, rootCause);
    }
}
