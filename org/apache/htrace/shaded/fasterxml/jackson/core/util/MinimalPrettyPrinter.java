// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.util;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;
import java.io.Serializable;
import org.apache.htrace.shaded.fasterxml.jackson.core.PrettyPrinter;

public class MinimalPrettyPrinter implements PrettyPrinter, Serializable
{
    private static final long serialVersionUID = -562765100295218442L;
    public static final String DEFAULT_ROOT_VALUE_SEPARATOR = " ";
    protected String _rootValueSeparator;
    
    public MinimalPrettyPrinter() {
        this(" ");
    }
    
    public MinimalPrettyPrinter(final String rootValueSeparator) {
        this._rootValueSeparator = " ";
        this._rootValueSeparator = rootValueSeparator;
    }
    
    public void setRootValueSeparator(final String sep) {
        this._rootValueSeparator = sep;
    }
    
    @Override
    public void writeRootValueSeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        if (this._rootValueSeparator != null) {
            jg.writeRaw(this._rootValueSeparator);
        }
    }
    
    @Override
    public void writeStartObject(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw('{');
    }
    
    @Override
    public void beforeObjectEntries(final JsonGenerator jg) throws IOException, JsonGenerationException {
    }
    
    @Override
    public void writeObjectFieldValueSeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(':');
    }
    
    @Override
    public void writeObjectEntrySeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(',');
    }
    
    @Override
    public void writeEndObject(final JsonGenerator jg, final int nrOfEntries) throws IOException, JsonGenerationException {
        jg.writeRaw('}');
    }
    
    @Override
    public void writeStartArray(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw('[');
    }
    
    @Override
    public void beforeArrayValues(final JsonGenerator jg) throws IOException, JsonGenerationException {
    }
    
    @Override
    public void writeArrayValueSeparator(final JsonGenerator jg) throws IOException, JsonGenerationException {
        jg.writeRaw(',');
    }
    
    @Override
    public void writeEndArray(final JsonGenerator jg, final int nrOfValues) throws IOException, JsonGenerationException {
        jg.writeRaw(']');
    }
}
