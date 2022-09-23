// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.util;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.OutputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonStreamContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParseException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.core.FormatSchema;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;

public class JsonParserDelegate extends JsonParser
{
    protected JsonParser delegate;
    
    public JsonParserDelegate(final JsonParser d) {
        this.delegate = d;
    }
    
    @Override
    public void setCodec(final ObjectCodec c) {
        this.delegate.setCodec(c);
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this.delegate.getCodec();
    }
    
    @Override
    public JsonParser enable(final Feature f) {
        this.delegate.enable(f);
        return this;
    }
    
    @Override
    public JsonParser disable(final Feature f) {
        this.delegate.disable(f);
        return this;
    }
    
    @Override
    public boolean isEnabled(final Feature f) {
        return this.delegate.isEnabled(f);
    }
    
    @Override
    public int getFeatureMask() {
        return this.delegate.getFeatureMask();
    }
    
    @Override
    public JsonParser setFeatureMask(final int mask) {
        this.delegate.setFeatureMask(mask);
        return this;
    }
    
    @Override
    public FormatSchema getSchema() {
        return this.delegate.getSchema();
    }
    
    @Override
    public void setSchema(final FormatSchema schema) {
        this.delegate.setSchema(schema);
    }
    
    @Override
    public boolean canUseSchema(final FormatSchema schema) {
        return this.delegate.canUseSchema(schema);
    }
    
    @Override
    public Version version() {
        return this.delegate.version();
    }
    
    @Override
    public Object getInputSource() {
        return this.delegate.getInputSource();
    }
    
    @Override
    public boolean requiresCustomCodec() {
        return this.delegate.requiresCustomCodec();
    }
    
    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
    
    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }
    
    @Override
    public JsonToken getCurrentToken() {
        return this.delegate.getCurrentToken();
    }
    
    @Override
    public int getCurrentTokenId() {
        return this.delegate.getCurrentTokenId();
    }
    
    @Override
    public boolean hasCurrentToken() {
        return this.delegate.hasCurrentToken();
    }
    
    @Override
    public String getCurrentName() throws IOException, JsonParseException {
        return this.delegate.getCurrentName();
    }
    
    @Override
    public JsonLocation getCurrentLocation() {
        return this.delegate.getCurrentLocation();
    }
    
    @Override
    public JsonStreamContext getParsingContext() {
        return this.delegate.getParsingContext();
    }
    
    @Override
    public boolean isExpectedStartArrayToken() {
        return this.delegate.isExpectedStartArrayToken();
    }
    
    @Override
    public void clearCurrentToken() {
        this.delegate.clearCurrentToken();
    }
    
    @Override
    public JsonToken getLastClearedToken() {
        return this.delegate.getLastClearedToken();
    }
    
    @Override
    public void overrideCurrentName(final String name) {
        this.delegate.overrideCurrentName(name);
    }
    
    @Override
    public String getText() throws IOException, JsonParseException {
        return this.delegate.getText();
    }
    
    @Override
    public boolean hasTextCharacters() {
        return this.delegate.hasTextCharacters();
    }
    
    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return this.delegate.getTextCharacters();
    }
    
    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return this.delegate.getTextLength();
    }
    
    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return this.delegate.getTextOffset();
    }
    
    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return this.delegate.getBigIntegerValue();
    }
    
    @Override
    public boolean getBooleanValue() throws IOException, JsonParseException {
        return this.delegate.getBooleanValue();
    }
    
    @Override
    public byte getByteValue() throws IOException, JsonParseException {
        return this.delegate.getByteValue();
    }
    
    @Override
    public short getShortValue() throws IOException, JsonParseException {
        return this.delegate.getShortValue();
    }
    
    @Override
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        return this.delegate.getDecimalValue();
    }
    
    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return this.delegate.getDoubleValue();
    }
    
    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return this.delegate.getFloatValue();
    }
    
    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return this.delegate.getIntValue();
    }
    
    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return this.delegate.getLongValue();
    }
    
    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
        return this.delegate.getNumberType();
    }
    
    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        return this.delegate.getNumberValue();
    }
    
    @Override
    public int getValueAsInt() throws IOException, JsonParseException {
        return this.delegate.getValueAsInt();
    }
    
    @Override
    public int getValueAsInt(final int defaultValue) throws IOException, JsonParseException {
        return this.delegate.getValueAsInt(defaultValue);
    }
    
    @Override
    public long getValueAsLong() throws IOException, JsonParseException {
        return this.delegate.getValueAsLong();
    }
    
    @Override
    public long getValueAsLong(final long defaultValue) throws IOException, JsonParseException {
        return this.delegate.getValueAsLong(defaultValue);
    }
    
    @Override
    public double getValueAsDouble() throws IOException, JsonParseException {
        return this.delegate.getValueAsDouble();
    }
    
    @Override
    public double getValueAsDouble(final double defaultValue) throws IOException, JsonParseException {
        return this.delegate.getValueAsDouble(defaultValue);
    }
    
    @Override
    public boolean getValueAsBoolean() throws IOException, JsonParseException {
        return this.delegate.getValueAsBoolean();
    }
    
    @Override
    public boolean getValueAsBoolean(final boolean defaultValue) throws IOException, JsonParseException {
        return this.delegate.getValueAsBoolean(defaultValue);
    }
    
    @Override
    public String getValueAsString() throws IOException, JsonParseException {
        return this.delegate.getValueAsString();
    }
    
    @Override
    public String getValueAsString(final String defaultValue) throws IOException, JsonParseException {
        return this.delegate.getValueAsString(defaultValue);
    }
    
    @Override
    public Object getEmbeddedObject() throws IOException, JsonParseException {
        return this.delegate.getEmbeddedObject();
    }
    
    @Override
    public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException, JsonParseException {
        return this.delegate.getBinaryValue(b64variant);
    }
    
    @Override
    public int readBinaryValue(final Base64Variant b64variant, final OutputStream out) throws IOException, JsonParseException {
        return this.delegate.readBinaryValue(b64variant, out);
    }
    
    @Override
    public JsonLocation getTokenLocation() {
        return this.delegate.getTokenLocation();
    }
    
    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        return this.delegate.nextToken();
    }
    
    @Override
    public JsonToken nextValue() throws IOException, JsonParseException {
        return this.delegate.nextValue();
    }
    
    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
        this.delegate.skipChildren();
        return this;
    }
    
    @Override
    public boolean canReadObjectId() {
        return this.delegate.canReadObjectId();
    }
    
    @Override
    public boolean canReadTypeId() {
        return this.delegate.canReadTypeId();
    }
    
    @Override
    public Object getObjectId() throws IOException, JsonGenerationException {
        return this.delegate.getObjectId();
    }
    
    @Override
    public Object getTypeId() throws IOException, JsonGenerationException {
        return this.delegate.getTypeId();
    }
}
