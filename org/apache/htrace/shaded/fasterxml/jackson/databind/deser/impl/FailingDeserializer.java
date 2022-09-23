// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.deser.impl;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class FailingDeserializer extends StdDeserializer<Object>
{
    private static final long serialVersionUID = 1L;
    protected final String _message;
    
    public FailingDeserializer(final String m) {
        super(Object.class);
        this._message = m;
    }
    
    @Override
    public Object deserialize(final JsonParser jp, final DeserializationContext ctxt) throws JsonMappingException {
        throw ctxt.mappingException(this._message);
    }
}
