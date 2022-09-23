// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonschema;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.Type;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;

public interface SchemaAware
{
    JsonNode getSchema(final SerializerProvider p0, final Type p1) throws JsonMappingException;
    
    JsonNode getSchema(final SerializerProvider p0, final Type p1, final boolean p2) throws JsonMappingException;
}
