// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.writer;

import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonNode;
import java.math.BigInteger;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonParser;
import java.math.BigDecimal;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonGenerationException;
import java.io.IOException;
import org.codehaus.jackson.JsonGenerator;

public class JacksonStringMergingGenerator extends JsonGenerator
{
    JsonGenerator generator;
    boolean isClosed;
    String previousString;
    
    private JacksonStringMergingGenerator() {
    }
    
    private JacksonStringMergingGenerator(final JsonGenerator generator) {
        this.generator = generator;
    }
    
    public static JacksonStringMergingGenerator createGenerator(final JsonGenerator g) {
        return new JacksonStringMergingGenerator(g);
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
    public void writeStartArray() throws IOException, JsonGenerationException {
        this.generator.writeStartArray();
    }
    
    @Override
    public void writeEndArray() throws IOException, JsonGenerationException {
        this.flushPreviousString();
        this.generator.writeEndArray();
    }
    
    @Override
    public void writeStartObject() throws IOException, JsonGenerationException {
        this.generator.writeStartObject();
    }
    
    @Override
    public void writeEndObject() throws IOException, JsonGenerationException {
        this.flushPreviousString();
        this.generator.writeEndObject();
    }
    
    @Override
    public void writeFieldName(final String name) throws IOException, JsonGenerationException {
        this.flushPreviousString();
        this.generator.writeFieldName(name);
    }
    
    @Override
    public void writeString(final String s) throws IOException, JsonGenerationException {
        this.generator.writeString(s);
    }
    
    public void writeStringToMerge(final String s) throws IOException, JsonGenerationException {
        if (this.previousString == null) {
            this.previousString = s;
        }
        else {
            this.previousString += s;
        }
    }
    
    @Override
    public void writeString(final char[] text, final int start, final int length) throws IOException, JsonGenerationException {
        this.generator.writeString(text, start, length);
    }
    
    @Override
    public void writeRawUTF8String(final byte[] bytes, final int start, final int length) throws IOException, JsonGenerationException {
        this.generator.writeRawUTF8String(bytes, start, length);
    }
    
    @Override
    public void writeUTF8String(final byte[] bytes, final int start, final int length) throws IOException, JsonGenerationException {
        this.generator.writeUTF8String(bytes, start, length);
    }
    
    @Override
    public void writeRaw(final String raw) throws IOException, JsonGenerationException {
        this.generator.writeRaw(raw);
    }
    
    @Override
    public void writeRaw(final String raw, final int start, final int length) throws IOException, JsonGenerationException {
        this.generator.writeRaw(raw, start, length);
    }
    
    @Override
    public void writeRaw(final char[] raw, final int start, final int count) throws IOException, JsonGenerationException {
        this.generator.writeRaw(raw, start, count);
    }
    
    @Override
    public void writeRaw(final char c) throws IOException, JsonGenerationException {
        this.generator.writeRaw(c);
    }
    
    @Override
    public void writeBinary(final Base64Variant variant, final byte[] bytes, final int start, final int count) throws IOException, JsonGenerationException {
        this.generator.writeBinary(variant, bytes, start, count);
    }
    
    @Override
    public void writeNumber(final int i) throws IOException, JsonGenerationException {
        this.generator.writeNumber(i);
    }
    
    @Override
    public void writeNumber(final long l) throws IOException, JsonGenerationException {
        this.generator.writeNumber(l);
    }
    
    @Override
    public void writeNumber(final double d) throws IOException, JsonGenerationException {
        this.generator.writeNumber(d);
    }
    
    @Override
    public void writeNumber(final float f) throws IOException, JsonGenerationException {
        this.generator.writeNumber(f);
    }
    
    @Override
    public void writeNumber(final BigDecimal bd) throws IOException, JsonGenerationException {
        this.generator.writeNumber(bd);
    }
    
    @Override
    public void writeNumber(final String number) throws IOException, JsonGenerationException, UnsupportedOperationException {
        this.generator.writeNumber(number);
    }
    
    @Override
    public void writeBoolean(final boolean b) throws IOException, JsonGenerationException {
        this.generator.writeBoolean(b);
    }
    
    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        this.generator.writeNull();
    }
    
    @Override
    public void copyCurrentEvent(final JsonParser parser) throws IOException, JsonProcessingException {
        this.flushPreviousString();
        this.generator.copyCurrentEvent(parser);
    }
    
    @Override
    public void copyCurrentStructure(final JsonParser parser) throws IOException, JsonProcessingException {
        this.flushPreviousString();
        this.generator.copyCurrentStructure(parser);
    }
    
    @Override
    public void flush() throws IOException {
        this.generator.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.generator.close();
        this.isClosed = true;
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
        this.generator.writeRawValue(rawString);
    }
    
    @Override
    public void writeRawValue(final String rawString, final int startIndex, final int length) throws IOException, JsonGenerationException {
        this.generator.writeRawValue(rawString, startIndex, length);
    }
    
    @Override
    public void writeRawValue(final char[] rawChars, final int startIndex, final int length) throws IOException, JsonGenerationException {
        this.generator.writeRawValue(rawChars, startIndex, length);
    }
    
    @Override
    public void writeNumber(final BigInteger number) throws IOException, JsonGenerationException {
        this.generator.writeNumber(number);
    }
    
    @Override
    public void writeObject(final Object o) throws IOException, JsonProcessingException {
        this.generator.writeObject(o);
    }
    
    @Override
    public void writeTree(final JsonNode node) throws IOException, JsonProcessingException {
        this.generator.writeTree(node);
    }
    
    @Override
    public JsonStreamContext getOutputContext() {
        return this.generator.getOutputContext();
    }
    
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
    
    private void flushPreviousString() throws IOException {
        if (this.previousString != null) {
            this.generator.writeString(this.previousString);
            this.previousString = null;
        }
    }
}
