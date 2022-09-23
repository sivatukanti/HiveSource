// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map;

public interface ContextualKeyDeserializer
{
    KeyDeserializer createContextual(final DeserializationConfig p0, final BeanProperty p1) throws JsonMappingException;
}
