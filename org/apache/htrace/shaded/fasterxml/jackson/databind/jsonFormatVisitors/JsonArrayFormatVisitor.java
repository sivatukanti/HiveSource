// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.jsonFormatVisitors;

import org.apache.htrace.shaded.fasterxml.jackson.databind.SerializerProvider;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JsonMappingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.JavaType;

public interface JsonArrayFormatVisitor extends JsonFormatVisitorWithSerializerProvider
{
    void itemsFormat(final JsonFormatVisitable p0, final JavaType p1) throws JsonMappingException;
    
    void itemsFormat(final JsonFormatTypes p0) throws JsonMappingException;
    
    public static class Base implements JsonArrayFormatVisitor
    {
        protected SerializerProvider _provider;
        
        public Base() {
        }
        
        public Base(final SerializerProvider p) {
            this._provider = p;
        }
        
        @Override
        public SerializerProvider getProvider() {
            return this._provider;
        }
        
        @Override
        public void setProvider(final SerializerProvider p) {
            this._provider = p;
        }
        
        @Override
        public void itemsFormat(final JsonFormatVisitable handler, final JavaType elementType) throws JsonMappingException {
        }
        
        @Override
        public void itemsFormat(final JsonFormatTypes format) throws JsonMappingException {
        }
    }
}
