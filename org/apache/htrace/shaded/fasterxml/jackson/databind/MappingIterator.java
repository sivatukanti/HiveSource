// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.core.FormatSchema;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import java.io.Closeable;
import java.util.Iterator;

public class MappingIterator<T> implements Iterator<T>, Closeable
{
    protected static final MappingIterator<?> EMPTY_ITERATOR;
    protected final JavaType _type;
    protected final DeserializationContext _context;
    protected final JsonDeserializer<T> _deserializer;
    protected JsonParser _parser;
    protected final boolean _closeParser;
    protected boolean _hasNextChecked;
    protected final T _updatedValue;
    
    @Deprecated
    protected MappingIterator(final JavaType type, final JsonParser jp, final DeserializationContext ctxt, final JsonDeserializer<?> deser) {
        this(type, jp, ctxt, deser, true, null);
    }
    
    protected MappingIterator(final JavaType type, final JsonParser jp, final DeserializationContext ctxt, final JsonDeserializer<?> deser, final boolean managedParser, final Object valueToUpdate) {
        this._type = type;
        this._parser = jp;
        this._context = ctxt;
        this._deserializer = (JsonDeserializer<T>)deser;
        this._closeParser = managedParser;
        if (valueToUpdate == null) {
            this._updatedValue = null;
        }
        else {
            this._updatedValue = (T)valueToUpdate;
        }
        if (managedParser && jp != null && jp.getCurrentToken() == JsonToken.START_ARRAY) {
            jp.clearCurrentToken();
        }
    }
    
    protected static <T> MappingIterator<T> emptyIterator() {
        return (MappingIterator<T>)MappingIterator.EMPTY_ITERATOR;
    }
    
    @Override
    public boolean hasNext() {
        try {
            return this.hasNextValue();
        }
        catch (JsonMappingException e) {
            throw new RuntimeJsonMappingException(e.getMessage(), e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public T next() {
        try {
            return this.nextValue();
        }
        catch (JsonMappingException e) {
            throw new RuntimeJsonMappingException(e.getMessage(), e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void close() throws IOException {
        if (this._parser != null) {
            this._parser.close();
        }
    }
    
    public boolean hasNextValue() throws IOException {
        if (this._parser == null) {
            return false;
        }
        if (!this._hasNextChecked) {
            JsonToken t = this._parser.getCurrentToken();
            this._hasNextChecked = true;
            if (t == null) {
                t = this._parser.nextToken();
                if (t == null || t == JsonToken.END_ARRAY) {
                    final JsonParser jp = this._parser;
                    this._parser = null;
                    if (this._closeParser) {
                        jp.close();
                    }
                    return false;
                }
            }
        }
        return true;
    }
    
    public T nextValue() throws IOException {
        if (!this._hasNextChecked && !this.hasNextValue()) {
            throw new NoSuchElementException();
        }
        if (this._parser == null) {
            throw new NoSuchElementException();
        }
        this._hasNextChecked = false;
        T result;
        if (this._updatedValue == null) {
            result = this._deserializer.deserialize(this._parser, this._context);
        }
        else {
            this._deserializer.deserialize(this._parser, this._context, this._updatedValue);
            result = this._updatedValue;
        }
        this._parser.clearCurrentToken();
        return result;
    }
    
    public List<T> readAll() throws IOException {
        return this.readAll(new ArrayList<T>());
    }
    
    public List<T> readAll(final List<T> resultList) throws IOException {
        while (this.hasNextValue()) {
            resultList.add(this.nextValue());
        }
        return resultList;
    }
    
    public JsonParser getParser() {
        return this._parser;
    }
    
    public FormatSchema getParserSchema() {
        return this._parser.getSchema();
    }
    
    public JsonLocation getCurrentLocation() {
        return this._parser.getCurrentLocation();
    }
    
    static {
        EMPTY_ITERATOR = new MappingIterator<Object>(null, null, null, null, false, null);
    }
}
