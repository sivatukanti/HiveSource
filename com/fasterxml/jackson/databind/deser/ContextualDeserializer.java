// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;

public interface ContextualDeserializer
{
    JsonDeserializer<?> createContextual(final DeserializationContext p0, final BeanProperty p1) throws JsonMappingException;
}
