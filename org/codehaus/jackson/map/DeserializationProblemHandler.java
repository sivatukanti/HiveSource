// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map;

import org.codehaus.jackson.JsonProcessingException;
import java.io.IOException;

public abstract class DeserializationProblemHandler
{
    public boolean handleUnknownProperty(final DeserializationContext ctxt, final JsonDeserializer<?> deserializer, final Object beanOrClass, final String propertyName) throws IOException, JsonProcessingException {
        return false;
    }
}
