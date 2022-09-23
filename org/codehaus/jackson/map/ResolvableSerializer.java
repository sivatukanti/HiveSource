// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.jackson.map;

public interface ResolvableSerializer
{
    void resolve(final SerializerProvider p0) throws JsonMappingException;
}
