// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;

public interface ResolvableSerializer
{
    void resolve(final SerializerProvider p0) throws JsonMappingException;
}
