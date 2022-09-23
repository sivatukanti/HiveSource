// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;

@Deprecated
public interface BeanPropertyFilter
{
    void serializeAsField(final Object p0, final JsonGenerator p1, final SerializerProvider p2, final BeanPropertyWriter p3) throws Exception;
    
    @Deprecated
    void depositSchemaProperty(final BeanPropertyWriter p0, final ObjectNode p1, final SerializerProvider p2) throws JsonMappingException;
    
    void depositSchemaProperty(final BeanPropertyWriter p0, final JsonObjectFormatVisitor p1, final SerializerProvider p2) throws JsonMappingException;
}
