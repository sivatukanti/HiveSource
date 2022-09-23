// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.io;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.Base64Variant;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.ObjectCodec;
import java.util.ArrayList;
import java.util.List;
import org.apache.avro.AvroTypeException;
import java.nio.ByteBuffer;
import org.apache.avro.util.Utf8;
import org.codehaus.jackson.JsonToken;
import java.io.EOFException;
import org.apache.avro.io.parsing.JsonGrammarGenerator;
import org.apache.avro.Schema;
import java.io.IOException;
import java.io.InputStream;
import org.apache.avro.io.parsing.Symbol;
import java.util.Stack;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.apache.avro.io.parsing.Parser;

public class JsonDecoder extends ParsingDecoder implements Parser.ActionHandler
{
    private JsonParser in;
    private static JsonFactory jsonFactory;
    Stack<ReorderBuffer> reorderBuffers;
    ReorderBuffer currentReorderBuffer;
    static final String CHARSET = "ISO-8859-1";
    
    private JsonDecoder(final Symbol root, final InputStream in) throws IOException {
        super(root);
        this.reorderBuffers = new Stack<ReorderBuffer>();
        this.configure(in);
    }
    
    private JsonDecoder(final Symbol root, final String in) throws IOException {
        super(root);
        this.reorderBuffers = new Stack<ReorderBuffer>();
        this.configure(in);
    }
    
    JsonDecoder(final Schema schema, final InputStream in) throws IOException {
        this(getSymbol(schema), in);
    }
    
    JsonDecoder(final Schema schema, final String in) throws IOException {
        this(getSymbol(schema), in);
    }
    
    private static Symbol getSymbol(final Schema schema) {
        if (null == schema) {
            throw new NullPointerException("Schema cannot be null!");
        }
        return new JsonGrammarGenerator().generate(schema);
    }
    
    public JsonDecoder configure(final InputStream in) throws IOException {
        if (null == in) {
            throw new NullPointerException("InputStream to read from cannot be null!");
        }
        this.parser.reset();
        (this.in = JsonDecoder.jsonFactory.createJsonParser(in)).nextToken();
        return this;
    }
    
    public JsonDecoder configure(final String in) throws IOException {
        if (null == in) {
            throw new NullPointerException("String to read from cannot be null!");
        }
        this.parser.reset();
        (this.in = new JsonFactory().createJsonParser(in)).nextToken();
        return this;
    }
    
    private void advance(final Symbol symbol) throws IOException {
        this.parser.processTrailingImplicitActions();
        if (this.in.getCurrentToken() == null && this.parser.depth() == 1) {
            throw new EOFException();
        }
        this.parser.advance(symbol);
    }
    
    @Override
    public void readNull() throws IOException {
        this.advance(Symbol.NULL);
        if (this.in.getCurrentToken() == JsonToken.VALUE_NULL) {
            this.in.nextToken();
            return;
        }
        throw this.error("null");
    }
    
    @Override
    public boolean readBoolean() throws IOException {
        this.advance(Symbol.BOOLEAN);
        final JsonToken t = this.in.getCurrentToken();
        if (t == JsonToken.VALUE_TRUE || t == JsonToken.VALUE_FALSE) {
            this.in.nextToken();
            return t == JsonToken.VALUE_TRUE;
        }
        throw this.error("boolean");
    }
    
    @Override
    public int readInt() throws IOException {
        this.advance(Symbol.INT);
        if (this.in.getCurrentToken().isNumeric()) {
            final int result = this.in.getIntValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("int");
    }
    
    @Override
    public long readLong() throws IOException {
        this.advance(Symbol.LONG);
        if (this.in.getCurrentToken().isNumeric()) {
            final long result = this.in.getLongValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("long");
    }
    
    @Override
    public float readFloat() throws IOException {
        this.advance(Symbol.FLOAT);
        if (this.in.getCurrentToken().isNumeric()) {
            final float result = this.in.getFloatValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("float");
    }
    
    @Override
    public double readDouble() throws IOException {
        this.advance(Symbol.DOUBLE);
        if (this.in.getCurrentToken().isNumeric()) {
            final double result = this.in.getDoubleValue();
            this.in.nextToken();
            return result;
        }
        throw this.error("double");
    }
    
    @Override
    public Utf8 readString(final Utf8 old) throws IOException {
        return new Utf8(this.readString());
    }
    
    @Override
    public String readString() throws IOException {
        this.advance(Symbol.STRING);
        if (this.parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            this.parser.advance(Symbol.MAP_KEY_MARKER);
            if (this.in.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw this.error("map-key");
            }
        }
        else if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("string");
        }
        final String result = this.in.getText();
        this.in.nextToken();
        return result;
    }
    
    @Override
    public void skipString() throws IOException {
        this.advance(Symbol.STRING);
        if (this.parser.topSymbol() == Symbol.MAP_KEY_MARKER) {
            this.parser.advance(Symbol.MAP_KEY_MARKER);
            if (this.in.getCurrentToken() != JsonToken.FIELD_NAME) {
                throw this.error("map-key");
            }
        }
        else if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("string");
        }
        this.in.nextToken();
    }
    
    @Override
    public ByteBuffer readBytes(final ByteBuffer old) throws IOException {
        this.advance(Symbol.BYTES);
        if (this.in.getCurrentToken() == JsonToken.VALUE_STRING) {
            final byte[] result = this.readByteArray();
            this.in.nextToken();
            return ByteBuffer.wrap(result);
        }
        throw this.error("bytes");
    }
    
    private byte[] readByteArray() throws IOException {
        final byte[] result = this.in.getText().getBytes("ISO-8859-1");
        return result;
    }
    
    @Override
    public void skipBytes() throws IOException {
        this.advance(Symbol.BYTES);
        if (this.in.getCurrentToken() == JsonToken.VALUE_STRING) {
            this.in.nextToken();
            return;
        }
        throw this.error("bytes");
    }
    
    private void checkFixed(final int size) throws IOException {
        this.advance(Symbol.FIXED);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        if (size != top.size) {
            throw new AvroTypeException("Incorrect length for fixed binary: expected " + top.size + " but received " + size + " bytes.");
        }
    }
    
    @Override
    public void readFixed(final byte[] bytes, final int start, final int len) throws IOException {
        this.checkFixed(len);
        if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("fixed");
        }
        final byte[] result = this.readByteArray();
        this.in.nextToken();
        if (result.length != len) {
            throw new AvroTypeException("Expected fixed length " + len + ", but got" + result.length);
        }
        System.arraycopy(result, 0, bytes, start, len);
    }
    
    @Override
    public void skipFixed(final int length) throws IOException {
        this.checkFixed(length);
        this.doSkipFixed(length);
    }
    
    private void doSkipFixed(final int length) throws IOException {
        if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("fixed");
        }
        final byte[] result = this.readByteArray();
        this.in.nextToken();
        if (result.length != length) {
            throw new AvroTypeException("Expected fixed length " + length + ", but got" + result.length);
        }
    }
    
    @Override
    protected void skipFixed() throws IOException {
        this.advance(Symbol.FIXED);
        final Symbol.IntCheckAction top = (Symbol.IntCheckAction)this.parser.popSymbol();
        this.doSkipFixed(top.size);
    }
    
    @Override
    public int readEnum() throws IOException {
        this.advance(Symbol.ENUM);
        final Symbol.EnumLabelsAction top = (Symbol.EnumLabelsAction)this.parser.popSymbol();
        if (this.in.getCurrentToken() != JsonToken.VALUE_STRING) {
            throw this.error("fixed");
        }
        this.in.getText();
        final int n = top.findLabel(this.in.getText());
        if (n >= 0) {
            this.in.nextToken();
            return n;
        }
        throw new AvroTypeException("Unknown symbol in enum " + this.in.getText());
    }
    
    @Override
    public long readArrayStart() throws IOException {
        this.advance(Symbol.ARRAY_START);
        if (this.in.getCurrentToken() == JsonToken.START_ARRAY) {
            this.in.nextToken();
            return this.doArrayNext();
        }
        throw this.error("array-start");
    }
    
    @Override
    public long arrayNext() throws IOException {
        this.advance(Symbol.ITEM_END);
        return this.doArrayNext();
    }
    
    private long doArrayNext() throws IOException {
        if (this.in.getCurrentToken() == JsonToken.END_ARRAY) {
            this.parser.advance(Symbol.ARRAY_END);
            this.in.nextToken();
            return 0L;
        }
        return 1L;
    }
    
    @Override
    public long skipArray() throws IOException {
        this.advance(Symbol.ARRAY_START);
        if (this.in.getCurrentToken() == JsonToken.START_ARRAY) {
            this.in.skipChildren();
            this.in.nextToken();
            this.advance(Symbol.ARRAY_END);
            return 0L;
        }
        throw this.error("array-start");
    }
    
    @Override
    public long readMapStart() throws IOException {
        this.advance(Symbol.MAP_START);
        if (this.in.getCurrentToken() == JsonToken.START_OBJECT) {
            this.in.nextToken();
            return this.doMapNext();
        }
        throw this.error("map-start");
    }
    
    @Override
    public long mapNext() throws IOException {
        this.advance(Symbol.ITEM_END);
        return this.doMapNext();
    }
    
    private long doMapNext() throws IOException {
        if (this.in.getCurrentToken() == JsonToken.END_OBJECT) {
            this.in.nextToken();
            this.advance(Symbol.MAP_END);
            return 0L;
        }
        return 1L;
    }
    
    @Override
    public long skipMap() throws IOException {
        this.advance(Symbol.MAP_START);
        if (this.in.getCurrentToken() == JsonToken.START_OBJECT) {
            this.in.skipChildren();
            this.in.nextToken();
            this.advance(Symbol.MAP_END);
            return 0L;
        }
        throw this.error("map-start");
    }
    
    @Override
    public int readIndex() throws IOException {
        this.advance(Symbol.UNION);
        final Symbol.Alternative a = (Symbol.Alternative)this.parser.popSymbol();
        String label;
        if (this.in.getCurrentToken() == JsonToken.VALUE_NULL) {
            label = "null";
        }
        else {
            if (this.in.getCurrentToken() != JsonToken.START_OBJECT || this.in.nextToken() != JsonToken.FIELD_NAME) {
                throw this.error("start-union");
            }
            label = this.in.getText();
            this.in.nextToken();
            this.parser.pushSymbol(Symbol.UNION_END);
        }
        final int n = a.findLabel(label);
        if (n < 0) {
            throw new AvroTypeException("Unknown union branch " + label);
        }
        this.parser.pushSymbol(a.getSymbol(n));
        return n;
    }
    
    @Override
    public Symbol doAction(final Symbol input, final Symbol top) throws IOException {
        if (top instanceof Symbol.FieldAdjustAction) {
            final Symbol.FieldAdjustAction fa = (Symbol.FieldAdjustAction)top;
            final String name = fa.fname;
            if (this.currentReorderBuffer != null) {
                final List<JsonElement> node = this.currentReorderBuffer.savedFields.get(name);
                if (node != null) {
                    this.currentReorderBuffer.savedFields.remove(name);
                    this.currentReorderBuffer.origParser = this.in;
                    this.in = this.makeParser(node);
                    return null;
                }
            }
            if (this.in.getCurrentToken() == JsonToken.FIELD_NAME) {
                do {
                    final String fn = this.in.getText();
                    this.in.nextToken();
                    if (name.equals(fn)) {
                        return null;
                    }
                    if (this.currentReorderBuffer == null) {
                        this.currentReorderBuffer = new ReorderBuffer();
                    }
                    this.currentReorderBuffer.savedFields.put(fn, getVaueAsTree(this.in));
                } while (this.in.getCurrentToken() == JsonToken.FIELD_NAME);
                throw new AvroTypeException("Expected field name not found: " + fa.fname);
            }
        }
        else if (top == Symbol.FIELD_END) {
            if (this.currentReorderBuffer != null && this.currentReorderBuffer.origParser != null) {
                this.in = this.currentReorderBuffer.origParser;
                this.currentReorderBuffer.origParser = null;
            }
        }
        else if (top == Symbol.RECORD_START) {
            if (this.in.getCurrentToken() != JsonToken.START_OBJECT) {
                throw this.error("record-start");
            }
            this.in.nextToken();
            this.reorderBuffers.push(this.currentReorderBuffer);
            this.currentReorderBuffer = null;
        }
        else {
            if (top != Symbol.RECORD_END && top != Symbol.UNION_END) {
                throw new AvroTypeException("Unknown action symbol " + top);
            }
            if (this.in.getCurrentToken() != JsonToken.END_OBJECT) {
                throw this.error((top == Symbol.RECORD_END) ? "record-end" : "union-end");
            }
            this.in.nextToken();
            if (top == Symbol.RECORD_END) {
                if (this.currentReorderBuffer != null && !this.currentReorderBuffer.savedFields.isEmpty()) {
                    throw this.error("Unknown fields: " + this.currentReorderBuffer.savedFields.keySet());
                }
                this.currentReorderBuffer = this.reorderBuffers.pop();
            }
        }
        return null;
    }
    
    private static List<JsonElement> getVaueAsTree(final JsonParser in) throws IOException {
        int level = 0;
        final List<JsonElement> result = new ArrayList<JsonElement>();
        do {
            final JsonToken t = in.getCurrentToken();
            switch (t) {
                case START_OBJECT:
                case START_ARRAY: {
                    ++level;
                    result.add(new JsonElement(t));
                    break;
                }
                case END_OBJECT:
                case END_ARRAY: {
                    --level;
                    result.add(new JsonElement(t));
                    break;
                }
                case FIELD_NAME:
                case VALUE_STRING:
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                case VALUE_TRUE:
                case VALUE_FALSE:
                case VALUE_NULL: {
                    result.add(new JsonElement(t, in.getText()));
                    break;
                }
            }
            in.nextToken();
        } while (level != 0);
        result.add(new JsonElement(null));
        return result;
    }
    
    private JsonParser makeParser(final List<JsonElement> elements) throws IOException {
        return new JsonParser() {
            int pos = 0;
            
            @Override
            public ObjectCodec getCodec() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void setCodec(final ObjectCodec c) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void close() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public JsonToken nextToken() throws IOException {
                ++this.pos;
                return elements.get(this.pos).token;
            }
            
            @Override
            public JsonParser skipChildren() throws IOException {
                int level = 0;
                do {
                    switch (elements.get(this.pos++).token) {
                        default: {
                            continue;
                        }
                        case START_OBJECT:
                        case START_ARRAY: {
                            ++level;
                            continue;
                        }
                        case END_OBJECT:
                        case END_ARRAY: {
                            --level;
                            continue;
                        }
                    }
                } while (level > 0);
                return this;
            }
            
            @Override
            public boolean isClosed() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String getCurrentName() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public JsonStreamContext getParsingContext() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public JsonLocation getTokenLocation() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public JsonLocation getCurrentLocation() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public String getText() throws IOException {
                return elements.get(this.pos).value;
            }
            
            @Override
            public char[] getTextCharacters() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public int getTextLength() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public int getTextOffset() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public Number getNumberValue() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public NumberType getNumberType() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public int getIntValue() throws IOException {
                return Integer.parseInt(this.getText());
            }
            
            @Override
            public long getLongValue() throws IOException {
                return Long.parseLong(this.getText());
            }
            
            @Override
            public BigInteger getBigIntegerValue() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public float getFloatValue() throws IOException {
                return Float.parseFloat(this.getText());
            }
            
            @Override
            public double getDoubleValue() throws IOException {
                return Double.parseDouble(this.getText());
            }
            
            @Override
            public BigDecimal getDecimalValue() throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public JsonToken getCurrentToken() {
                return elements.get(this.pos).token;
            }
        };
    }
    
    private AvroTypeException error(final String type) {
        return new AvroTypeException("Expected " + type + ". Got " + this.in.getCurrentToken());
    }
    
    static {
        JsonDecoder.jsonFactory = new JsonFactory();
    }
    
    private static class ReorderBuffer
    {
        public Map<String, List<JsonElement>> savedFields;
        public JsonParser origParser;
        
        private ReorderBuffer() {
            this.savedFields = new HashMap<String, List<JsonElement>>();
            this.origParser = null;
        }
    }
    
    private static class JsonElement
    {
        public final JsonToken token;
        public final String value;
        
        public JsonElement(final JsonToken t, final String value) {
            this.token = t;
            this.value = value;
        }
        
        public JsonElement(final JsonToken t) {
            this(t, null);
        }
    }
}
