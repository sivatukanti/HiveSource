// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map;

public interface ResolvableDeserializer
{
    void resolve(final DeserializationConfig p0, final DeserializerProvider p1) throws JsonMappingException;
}
