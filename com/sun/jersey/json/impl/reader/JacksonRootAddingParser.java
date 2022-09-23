// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.json.impl.reader;

import org.codehaus.jackson.ObjectCodec;
import java.math.BigDecimal;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.Base64Variant;
import java.math.BigInteger;
import org.codehaus.jackson.JsonParseException;
import java.io.IOException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.JsonParser;

public class JacksonRootAddingParser extends JsonParser
{
    String rootName;
    JsonParser parser;
    State state;
    boolean isClosed;
    
    public static JsonParser createRootAddingParser(final JsonParser parser, final String rootName) {
        return new JacksonRootAddingParser(parser, rootName);
    }
    
    private JacksonRootAddingParser() {
        this.isClosed = false;
    }
    
    private JacksonRootAddingParser(final JsonParser parser, final String rootName) {
        this.isClosed = false;
        this.parser = parser;
        this.state = State.START;
        this.rootName = rootName;
    }
    
    @Override
    public void enableFeature(final Feature feature) {
        this.parser.enableFeature(feature);
    }
    
    @Override
    public void disableFeature(final Feature feature) {
        this.parser.disableFeature(feature);
    }
    
    @Override
    public void setFeature(final Feature feature, final boolean isSet) {
        this.parser.setFeature(feature, isSet);
    }
    
    @Override
    public JsonToken nextValue() throws IOException, JsonParseException {
        JsonToken result;
        for (result = this.nextToken(); !result.isScalarValue(); result = this.nextToken()) {}
        return result;
    }
    
    @Override
    public boolean isClosed() {
        return this.isClosed;
    }
    
    @Override
    public byte getByteValue() throws IOException, JsonParseException {
        return this.parser.getByteValue();
    }
    
    @Override
    public short getShortValue() throws IOException, JsonParseException {
        return this.parser.getShortValue();
    }
    
    @Override
    public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
        return this.parser.getBigIntegerValue();
    }
    
    @Override
    public float getFloatValue() throws IOException, JsonParseException {
        return this.parser.getFloatValue();
    }
    
    @Override
    public byte[] getBinaryValue(final Base64Variant base64Variant) throws IOException, JsonParseException {
        return this.parser.getBinaryValue(base64Variant);
    }
    
    @Override
    public <T> T readValueAs(final Class<T> type) throws IOException, JsonProcessingException {
        return this.parser.readValueAs(type);
    }
    
    @Override
    public <T> T readValueAs(final TypeReference<?> typeRef) throws IOException, JsonProcessingException {
        return this.parser.readValueAs(typeRef);
    }
    
    @Override
    public JsonNode readValueAsTree() throws IOException, JsonProcessingException {
        return this.parser.readValueAsTree();
    }
    
    @Override
    public JsonStreamContext getParsingContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public JsonToken nextToken() throws IOException, JsonParseException {
        switch (this.state) {
            case START: {
                this.state = State.AFTER_SO;
                return this._currToken = JsonToken.START_OBJECT;
            }
            case AFTER_SO: {
                this.state = State.AFTER_FN;
                return this._currToken = JsonToken.FIELD_NAME;
            }
            case AFTER_FN: {
                this.state = State.INNER;
            }
            case INNER: {
                this._currToken = this.parser.nextToken();
                if (this._currToken == null) {
                    this.state = State.END;
                    this._currToken = JsonToken.END_OBJECT;
                }
                return this._currToken;
            }
            default: {
                return this._currToken = null;
            }
        }
    }
    
    @Override
    public JsonParser skipChildren() throws IOException, JsonParseException {
        return this.parser.skipChildren();
    }
    
    @Override
    public String getCurrentName() throws IOException, JsonParseException {
        switch (this.state) {
            case START: {
                return null;
            }
            case AFTER_SO: {
                return null;
            }
            case AFTER_FN: {
                return this.rootName;
            }
            case INNER: {
                return this.parser.getCurrentName();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public void close() throws IOException {
        this.parser.close();
    }
    
    @Override
    public JsonLocation getTokenLocation() {
        return this.parser.getTokenLocation();
    }
    
    @Override
    public JsonLocation getCurrentLocation() {
        return this.parser.getCurrentLocation();
    }
    
    @Override
    public String getText() throws IOException, JsonParseException {
        return this.parser.getText();
    }
    
    @Override
    public char[] getTextCharacters() throws IOException, JsonParseException {
        return this.parser.getTextCharacters();
    }
    
    @Override
    public int getTextLength() throws IOException, JsonParseException {
        return this.parser.getTextLength();
    }
    
    @Override
    public int getTextOffset() throws IOException, JsonParseException {
        return this.parser.getTextOffset();
    }
    
    @Override
    public Number getNumberValue() throws IOException, JsonParseException {
        return this.parser.getNumberValue();
    }
    
    @Override
    public NumberType getNumberType() throws IOException, JsonParseException {
        return this.parser.getNumberType();
    }
    
    @Override
    public int getIntValue() throws IOException, JsonParseException {
        return this.parser.getIntValue();
    }
    
    @Override
    public long getLongValue() throws IOException, JsonParseException {
        return this.parser.getLongValue();
    }
    
    @Override
    public double getDoubleValue() throws IOException, JsonParseException {
        return this.parser.getDoubleValue();
    }
    
    @Override
    public BigDecimal getDecimalValue() throws IOException, JsonParseException {
        return this.parser.getDecimalValue();
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this.parser.getCodec();
    }
    
    @Override
    public void setCodec(final ObjectCodec c) {
        this.parser.setCodec(c);
    }
    
    enum State
    {
        START, 
        AFTER_SO, 
        AFTER_FN, 
        INNER, 
        END;
    }
}
