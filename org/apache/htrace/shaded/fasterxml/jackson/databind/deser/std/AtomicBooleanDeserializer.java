// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanDeserializer extends StdScalarDeserializer<AtomicBoolean>
{
    private static final long serialVersionUID = 1L;
    
    public AtomicBooleanDeserializer() {
        super(AtomicBoolean.class);
    }
    
    @Override
    public AtomicBoolean deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException {
        return new AtomicBoolean(this._parseBooleanPrimitive(jp, ctxt));
    }
}
