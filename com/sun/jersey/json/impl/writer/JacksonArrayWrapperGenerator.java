// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonParser;
import java.math.BigDecimal;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import java.math.BigInteger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.ObjectCodec;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;

public class JacksonArrayWrapperGenerator extends JsonGenerator
{
    State state;
    int depth;
    final JsonGenerator generator;
    private boolean isClosed;
    final int arrayDepth;
    
    private JacksonArrayWrapperGenerator() {
        this(null, 0);
    }
    
    private JacksonArrayWrapperGenerator(final JsonGenerator generator) {
        this(generator, 0);
    }
    
    private JacksonArrayWrapperGenerator(final JsonGenerator generator, final int arrayDepth) {
        this.state = State.START;
        this.depth = 0;
        this.isClosed = false;
        this.generator = generator;
        this.arrayDepth = arrayDepth;
    }
    
    public static JsonGenerator createArrayWrapperGenerator(final JsonGenerator g) {
        return new JacksonArrayWrapperGenerator(g);
    }
    
    public static JsonGenerator createArrayWrapperGenerator(final JsonGenerator g, final int arrayDepth) {
        return new JacksonArrayWrapperGenerator(g, arrayDepth);
    }
    
    private void aboutToWriteANonNull() throws IOException {
        if (this.depth == this.arrayDepth) {
            if (this.state == State.START) {
                this.generator.writeStartArray();
                this.state = State.IN_THE_MIDDLE;
            }
            else if (this.state == State.AFTER_NULL) {
                this.generator.writeStartArray();
                this.generator.writeNull();
                this.state = State.IN_THE_MIDDLE;
            }
        }
    }
    
    @Deprecated
    @Override
    public void enableFeature(final Feature feature) {
        this.generator.enableFeature(feature);
    }
    
    @Override
    public JsonGenerator enable(final Feature feature) {
        return this.generator.enable(feature);
    }
    
    @Deprecated
    @Override
    public void disableFeature(final Feature feature) {
        this.generator.disableFeature(feature);
    }
    
    @Override
    public JsonGenerator disable(final Feature feature) {
        return this.generator.disable(feature);
    }
    
    @Override
    public void setFeature(final Feature feature, final boolean enabled) {
        this.generator.setFeature(feature, enabled);
    }
    
    @Deprecated
    @Override
    public boolean isFeatureEnabled(final Feature feature) {
        return this.generator.isFeatureEnabled(feature);
    }
    
    @Override
    public boolean isEnabled(final Feature f) {
        return this.generator.isEnabled(f);
    }
    
    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this.generator.useDefaultPrettyPrinter();
    }
    
    @Override
    public JsonGenerator setCodec(final ObjectCodec codec) {
        return this.generator.setCodec(codec);
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this.generator.getCodec();
    }
    
    @Override
    public void writeRawValue(final String rawString) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRawValue(rawString);
    }
    
    @Override
    public void writeRawValue(final String rawString, final int startIndex, final int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRawValue(rawString, startIndex, length);
    }
    
    @Override
    public void writeRawValue(final char[] rawChars, final int startIndex, final int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(rawChars, startIndex, length);
    }
    
    @Override
    public void writeNumber(final BigInteger number) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(number);
    }
    
    @Override
    public void writeObject(final Object o) throws IOException, JsonProcessingException {
        this.aboutToWriteANonNull();
        this.generator.writeObject(o);
    }
    
    @Override
    public void writeTree(final JsonNode node) throws IOException, JsonProcessingException {
        this.aboutToWriteANonNull();
        this.generator.writeTree(node);
    }
    
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
    
    @Override
    public JsonStreamContext getOutputContext() {
        return this.generator.getOutputContext();
    }
    
    @Override
    public void writeStartArray() throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeStartArray();
    }
    
    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {
        this.generator.writeEndArray();
    }
    
    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        if (this.arrayDepth > 0 && this.depth == this.arrayDepth) {
            this.generator.writeStartArray();
            this.generator.writeStartObject();
        }
        else {
            this.aboutToWriteANonNull();
            this.generator.writeStartObject();
        }
        ++this.depth;
    }
    
    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        if (this.arrayDepth > 0 && this.depth == this.arrayDepth) {
            this.generator.writeEndObject();
            this.generator.writeEndArray();
        }
        else {
            this.generator.writeEndObject();
        }
        --this.depth;
    }
    
    @Override
    public void writeFieldName(final String name) throws IOException, JsonGenerationException {
        this.generator.writeFieldName(name);
    }
    
    @Override
    public void writeString(final String s) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeString(s);
    }
    
    @Override
    public void writeString(final char[] text, final int start, final int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeString(text, start, length);
    }
    
    @Override
    public void writeRawUTF8String(final byte[] bytes, final int start, final int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRawUTF8String(bytes, start, length);
    }
    
    @Override
    public void writeUTF8String(final byte[] bytes, final int start, final int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeUTF8String(bytes, start, length);
    }
    
    @Override
    public void writeRaw(final String raw) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(raw);
    }
    
    @Override
    public void writeRaw(final String raw, final int start, final int length) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(raw, start, length);
    }
    
    @Override
    public void writeRaw(final char[] raw, final int start, final int count) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(raw, start, count);
    }
    
    @Override
    public void writeRaw(final char c) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeRaw(c);
    }
    
    @Override
    public void writeBinary(final Base64Variant variant, final byte[] bytes, final int start, final int count) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeBinary(variant, bytes, start, count);
    }
    
    @Override
    public void writeNumber(final int i) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(i);
    }
    
    @Override
    public void writeNumber(final long l) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(l);
    }
    
    @Override
    public void writeNumber(final double d) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(d);
    }
    
    @Override
    public void writeNumber(final float f) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(f);
    }
    
    @Override
    public void writeNumber(final BigDecimal bd) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(bd);
    }
    
    @Override
    public void writeNumber(final String number) throws IOException, JsonGenerationException, UnsupportedOperationException {
        this.aboutToWriteANonNull();
        this.generator.writeNumber(number);
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException, JsonGenerationException {
        this.aboutToWriteANonNull();
        this.generator.writeBoolean(b);
    }
    
    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        switch (this.state) {
            case START: {
                this.state = State.AFTER_NULL;
                return;
            }
            case AFTER_NULL: {
                this.generator.writeStartArray();
                this.generator.writeNull();
                this.state = State.IN_THE_MIDDLE;
                break;
            }
        }
        this.generator.writeNull();
    }
    
    @Override
    public void copyCurrentEvent(final JsonParser parser) throws IOException, JsonProcessingException {
        if (JsonToken.VALUE_NULL != parser.getCurrentToken()) {
            this.aboutToWriteANonNull();
            this.generator.copyCurrentEvent(parser);
        }
        else {
            this.writeNull();
        }
    }
    
    @Override
    public void copyCurrentStructure(final JsonParser parser) throws IOException, JsonProcessingException {
        this.generator.copyCurrentStructure(parser);
    }
    
    @Override
    public void flush() throws IOException {
        if (this.depth == this.arrayDepth) {
            switch (this.state) {
                case IN_THE_MIDDLE: {
                    this.generator.writeEndArray();
                    break;
                }
                case START:
                case AFTER_NULL: {
                    this.generator.writeStartArray();
                    this.generator.writeEndArray();
                    break;
                }
            }
        }
        this.generator.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.generator.close();
        this.isClosed = true;
    }
    
    private enum State
    {
        START, 
        AFTER_NULL, 
        IN_THE_MIDDLE;
    }
}
