// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public interface JsonFormatVisitorWrapper extends JsonFormatVisitorWithSerializerProvider
{
    JsonObjectFormatVisitor expectObjectFormat(final JavaType p0) throws JsonMappingException;
    
    JsonArrayFormatVisitor expectArrayFormat(final JavaType p0) throws JsonMappingException;
    
    JsonStringFormatVisitor expectStringFormat(final JavaType p0) throws JsonMappingException;
    
    JsonNumberFormatVisitor expectNumberFormat(final JavaType p0) throws JsonMappingException;
    
    JsonIntegerFormatVisitor expectIntegerFormat(final JavaType p0) throws JsonMappingException;
    
    JsonBooleanFormatVisitor expectBooleanFormat(final JavaType p0) throws JsonMappingException;
    
    JsonNullFormatVisitor expectNullFormat(final JavaType p0) throws JsonMappingException;
    
    JsonAnyFormatVisitor expectAnyFormat(final JavaType p0) throws JsonMappingException;
    
    JsonMapFormatVisitor expectMapFormat(final JavaType p0) throws JsonMappingException;
}
