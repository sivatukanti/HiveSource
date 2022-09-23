// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import org.apache.htrace.shaded.fasterxml.jackson.databind.util.TokenBuffer;

@JacksonStdImpl
public class TokenBufferDeserializer extends StdScalarDeserializer<TokenBuffer>
{
    private static final long serialVersionUID = 1L;
    
    public TokenBufferDeserializer() {
        super(TokenBuffer.class);
    }
    
    @Override
    public TokenBuffer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return this.createBufferInstance(jp).deserialize(jp, ctxt);
    }
    
    protected TokenBuffer createBufferInstance(final JsonParser jp) {
        return new TokenBuffer(jp);
    }
}
