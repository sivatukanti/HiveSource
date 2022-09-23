// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;

public interface PropertyFilter
{
    void serializeAsField(final Object p0, final JsonGenerator p1, final SerializerProvider p2, final PropertyWriter p3) throws Exception;
    
    void serializeAsElement(final Object p0, final JsonGenerator p1, final SerializerProvider p2, final PropertyWriter p3) throws Exception;
    
    @Deprecated
    void depositSchemaProperty(final PropertyWriter p0, final ObjectNode p1, final SerializerProvider p2) throws JsonMappingException;
    
    void depositSchemaProperty(final PropertyWriter p0, final JsonObjectFormatVisitor p1, final SerializerProvider p2) throws JsonMappingException;
}
