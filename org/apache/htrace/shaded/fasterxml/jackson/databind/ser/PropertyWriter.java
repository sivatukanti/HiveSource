// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.ser;

import org.apache.htrace.shaded.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import org.apache.htrace.shaded.fasterxml.jackson.databind.PropertyName;

public abstract class PropertyWriter
{
    public abstract String getName();
    
    public abstract PropertyName getFullName();
    
    public abstract void serializeAsField(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void serializeAsOmittedField(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void serializeAsElement(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void serializeAsPlaceholder(final Object p0, final JsonGenerator p1, final SerializerProvider p2) throws Exception;
    
    public abstract void depositSchemaProperty(final JsonObjectFormatVisitor p0) throws JsonMappingException;
    
    @Deprecated
    public abstract void depositSchemaProperty(final ObjectNode p0, final SerializerProvider p1) throws JsonMappingException;
}
