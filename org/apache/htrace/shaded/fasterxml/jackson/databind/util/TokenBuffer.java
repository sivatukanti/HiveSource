// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind.util;

import java.util.TreeMap;
import java.io.OutputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParseException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonLocation;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.ByteArrayBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.core.json.JsonReadContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.base.ParserMinimalBase;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonStreamContext;
import java.io.InputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import org.apache.htrace.shaded.fasterxml.jackson.core.TreeNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.databind.DeserializationContext;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.databind.cfg.PackageVersion;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.core.json.DupDetector;
import org.apache.htrace.shaded.fasterxml.jackson.core.json.JsonWriteContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;

public class TokenBuffer extends JsonGenerator
{
    protected static final int DEFAULT_GENERATOR_FEATURES;
    protected ObjectCodec _objectCodec;
    protected int _generatorFeatures;
    protected boolean _closed;
    protected boolean _hasNativeTypeIds;
    protected boolean _hasNativeObjectIds;
    protected boolean _mayHaveNativeIds;
    protected Segment _first;
    protected Segment _last;
    protected int _appendAt;
    protected Object _typeId;
    protected Object _objectId;
    protected boolean _hasNativeId;
    protected JsonWriteContext _writeContext;
    
    @Deprecated
    public TokenBuffer(final ObjectCodec codec) {
        this(codec, false);
    }
    
    public TokenBuffer(final ObjectCodec codec, final boolean hasNativeIds) {
        this._hasNativeId = false;
        this._objectCodec = codec;
        this._generatorFeatures = TokenBuffer.DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        final Segment segment = new Segment();
        this._last = segment;
        this._first = segment;
        this._appendAt = 0;
        this._hasNativeTypeIds = hasNativeIds;
        this._hasNativeObjectIds = hasNativeIds;
        this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
    }
    
    public TokenBuffer(final JsonParser jp) {
        this._hasNativeId = false;
        this._objectCodec = jp.getCodec();
        this._generatorFeatures = TokenBuffer.DEFAULT_GENERATOR_FEATURES;
        this._writeContext = JsonWriteContext.createRootContext(null);
        final Segment segment = new Segment();
        this._last = segment;
        this._first = segment;
        this._appendAt = 0;
        this._hasNativeTypeIds = jp.canReadTypeId();
        this._hasNativeObjectIds = jp.canReadObjectId();
        this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
    }
    
    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }
    
    public JsonParser asParser() {
        return this.asParser(this._objectCodec);
    }
    
    public JsonParser asParser(final ObjectCodec codec) {
        return new Parser(this._first, codec, this._hasNativeTypeIds, this._hasNativeObjectIds);
    }
    
    public JsonParser asParser(final JsonParser src) {
        final Parser p = new Parser(this._first, src.getCodec(), this._hasNativeTypeIds, this._hasNativeObjectIds);
        p.setLocation(src.getTokenLocation());
        return p;
    }
    
    public JsonToken firstToken() {
        if (this._first != null) {
            return this._first.type(0);
        }
        return null;
    }
    
    public TokenBuffer append(final TokenBuffer other) throws IOException, JsonGenerationException {
        if (!this._hasNativeTypeIds) {
            this._hasNativeTypeIds = other.canWriteTypeId();
        }
        if (!this._hasNativeObjectIds) {
            this._hasNativeObjectIds = other.canWriteObjectId();
        }
        this._mayHaveNativeIds = (this._hasNativeTypeIds | this._hasNativeObjectIds);
        final JsonParser jp = other.asParser();
        while (jp.nextToken() != null) {
            this.copyCurrentStructure(jp);
        }
        return this;
    }
    
    public void serialize(final JsonGenerator jgen) throws IOException, JsonGenerationException {
        Segment segment = this._first;
        int ptr = -1;
        final boolean checkIds = this._mayHaveNativeIds;
        boolean hasIds = checkIds && segment.hasIds();
        while (true) {
            if (++ptr >= 16) {
                ptr = 0;
                segment = segment.next();
                if (segment == null) {
                    break;
                }
                hasIds = (checkIds && segment.hasIds());
            }
            final JsonToken t = segment.type(ptr);
            if (t == null) {
                break;
            }
            if (hasIds) {
                Object id = segment.findObjectId(ptr);
                if (id != null) {
                    jgen.writeObjectId(id);
                }
                id = segment.findTypeId(ptr);
                if (id != null) {
                    jgen.writeTypeId(id);
                }
            }
            switch (t) {
                case START_OBJECT: {
                    jgen.writeStartObject();
                    continue;
                }
                case END_OBJECT: {
                    jgen.writeEndObject();
                    continue;
                }
                case START_ARRAY: {
                    jgen.writeStartArray();
                    continue;
                }
                case END_ARRAY: {
                    jgen.writeEndArray();
                    continue;
                }
                case FIELD_NAME: {
                    final Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        jgen.writeFieldName((SerializableString)ob);
                    }
                    else {
                        jgen.writeFieldName((String)ob);
                    }
                    continue;
                }
                case VALUE_STRING: {
                    final Object ob = segment.get(ptr);
                    if (ob instanceof SerializableString) {
                        jgen.writeString((SerializableString)ob);
                    }
                    else {
                        jgen.writeString((String)ob);
                    }
                    continue;
                }
                case VALUE_NUMBER_INT: {
                    final Object n = segment.get(ptr);
                    if (n instanceof Integer) {
                        jgen.writeNumber((int)n);
                    }
                    else if (n instanceof BigInteger) {
                        jgen.writeNumber((BigInteger)n);
                    }
                    else if (n instanceof Long) {
                        jgen.writeNumber((long)n);
                    }
                    else if (n instanceof Short) {
                        jgen.writeNumber((short)n);
                    }
                    else {
                        jgen.writeNumber(((Number)n).intValue());
                    }
                    continue;
                }
                case VALUE_NUMBER_FLOAT: {
                    final Object n = segment.get(ptr);
                    if (n instanceof Double) {
                        jgen.writeNumber((double)n);
                    }
                    else if (n instanceof BigDecimal) {
                        jgen.writeNumber((BigDecimal)n);
                    }
                    else if (n instanceof Float) {
                        jgen.writeNumber((float)n);
                    }
                    else if (n == null) {
                        jgen.writeNull();
                    }
                    else {
                        if (!(n instanceof String)) {
                            throw new JsonGenerationException("Unrecognized value type for VALUE_NUMBER_FLOAT: " + n.getClass().getName() + ", can not serialize");
                        }
                        jgen.writeNumber((String)n);
                    }
                    continue;
                }
                case VALUE_TRUE: {
                    jgen.writeBoolean(true);
                    continue;
                }
                case VALUE_FALSE: {
                    jgen.writeBoolean(false);
                    continue;
                }
                case VALUE_NULL: {
                    jgen.writeNull();
                    continue;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    jgen.writeObject(segment.get(ptr));
                    continue;
                }
                default: {
                    throw new RuntimeException("Internal error: should never end up through this code path");
                }
            }
        }
    }
    
    public TokenBuffer deserialize(final JsonParser jp, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        this.copyCurrentStructure(jp);
        return this;
    }
    
    @Override
    public String toString() {
        final int MAX_COUNT = 100;
        final StringBuilder sb = new StringBuilder();
        sb.append("[TokenBuffer: ");
        final JsonParser jp = this.asParser();
        int count = 0;
        final boolean hasNativeIds = this._hasNativeTypeIds || this._hasNativeObjectIds;
        while (true) {
            try {
                final JsonToken t = jp.nextToken();
                if (t == null) {
                    break;
                }
                if (hasNativeIds) {
                    this._appendNativeIds(sb);
                }
                if (count < 100) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(t.toString());
                    if (t == JsonToken.FIELD_NAME) {
                        sb.append('(');
                        sb.append(jp.getCurrentName());
                        sb.append(')');
                    }
                }
            }
            catch (IOException ioe) {
                throw new IllegalStateException(ioe);
            }
            ++count;
        }
        if (count >= 100) {
            sb.append(" ... (truncated ").append(count - 100).append(" entries)");
        }
        sb.append(']');
        return sb.toString();
    }
    
    private final void _appendNativeIds(final StringBuilder sb) {
        final Object objectId = this._last.findObjectId(this._appendAt - 1);
        if (objectId != null) {
            sb.append("[objectId=").append(String.valueOf(objectId)).append(']');
        }
        final Object typeId = this._last.findTypeId(this._appendAt - 1);
        if (typeId != null) {
            sb.append("[typeId=").append(String.valueOf(typeId)).append(']');
        }
    }
    
    @Override
    public JsonGenerator enable(final Feature f) {
        this._generatorFeatures |= f.getMask();
        return this;
    }
    
    @Override
    public JsonGenerator disable(final Feature f) {
        this._generatorFeatures &= ~f.getMask();
        return this;
    }
    
    @Override
    public boolean isEnabled(final Feature f) {
        return (this._generatorFeatures & f.getMask()) != 0x0;
    }
    
    @Override
    public int getFeatureMask() {
        return this._generatorFeatures;
    }
    
    @Override
    public JsonGenerator setFeatureMask(final int mask) {
        this._generatorFeatures = mask;
        return this;
    }
    
    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        return this;
    }
    
    @Override
    public JsonGenerator setCodec(final ObjectCodec oc) {
        this._objectCodec = oc;
        return this;
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }
    
    @Override
    public final JsonWriteContext getOutputContext() {
        return this._writeContext;
    }
    
    @Override
    public boolean canWriteBinaryNatively() {
        return true;
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    @Override
    public void close() throws IOException {
        this._closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return this._closed;
    }
    
    @Override
    public final void writeStartArray() throws IOException, JsonGenerationException {
        this._append(JsonToken.START_ARRAY);
        this._writeContext = this._writeContext.createChildArrayContext();
    }
    
    @Override
    public final void writeEndArray() throws IOException, JsonGenerationException {
        this._append(JsonToken.END_ARRAY);
        final JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }
    
    @Override
    public final void writeStartObject() throws IOException, JsonGenerationException {
        this._append(JsonToken.START_OBJECT);
        this._writeContext = this._writeContext.createChildObjectContext();
    }
    
    @Override
    public final void writeEndObject() throws IOException, JsonGenerationException {
        this._append(JsonToken.END_OBJECT);
        final JsonWriteContext c = this._writeContext.getParent();
        if (c != null) {
            this._writeContext = c;
        }
    }
    
    @Override
    public final void writeFieldName(final String name) throws IOException, JsonGenerationException {
        this._append(JsonToken.FIELD_NAME, name);
        this._writeContext.writeFieldName(name);
    }
    
    @Override
    public void writeFieldName(final SerializableString name) throws IOException, JsonGenerationException {
        this._append(JsonToken.FIELD_NAME, name);
        this._writeContext.writeFieldName(name.getValue());
    }
    
    @Override
    public void writeString(final String text) throws IOException, JsonGenerationException {
        if (text == null) {
            this.writeNull();
        }
        else {
            this._append(JsonToken.VALUE_STRING, text);
        }
    }
    
    @Override
    public void writeString(final char[] text, final int offset, final int len) throws IOException, JsonGenerationException {
        this.writeString(new String(text, offset, len));
    }
    
    @Override
    public void writeString(final SerializableString text) throws IOException, JsonGenerationException {
        if (text == null) {
            this.writeNull();
        }
        else {
            this._append(JsonToken.VALUE_STRING, text);
        }
    }
    
    @Override
    public void writeRawUTF8String(final byte[] text, final int offset, final int length) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeUTF8String(final byte[] text, final int offset, final int length) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final String text) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final String text, final int offset, final int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final SerializableString text) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final char[] text, final int offset, final int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRaw(final char c) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRawValue(final String text) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRawValue(final String text, final int offset, final int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeRawValue(final char[] text, final int offset, final int len) throws IOException, JsonGenerationException {
        this._reportUnsupportedOperation();
    }
    
    @Override
    public void writeNumber(final short i) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_INT, i);
    }
    
    @Override
    public void writeNumber(final int i) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_INT, i);
    }
    
    @Override
    public void writeNumber(final long l) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_INT, l);
    }
    
    @Override
    public void writeNumber(final double d) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_FLOAT, d);
    }
    
    @Override
    public void writeNumber(final float f) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_FLOAT, f);
    }
    
    @Override
    public void writeNumber(final BigDecimal dec) throws IOException, JsonGenerationException {
        if (dec == null) {
            this.writeNull();
        }
        else {
            this._append(JsonToken.VALUE_NUMBER_FLOAT, dec);
        }
    }
    
    @Override
    public void writeNumber(final BigInteger v) throws IOException, JsonGenerationException {
        if (v == null) {
            this.writeNull();
        }
        else {
            this._append(JsonToken.VALUE_NUMBER_INT, v);
        }
    }
    
    @Override
    public void writeNumber(final String encodedValue) throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NUMBER_FLOAT, encodedValue);
    }
    
    @Override
    public void writeBoolean(final boolean state) throws IOException, JsonGenerationException {
        this._append(state ? JsonToken.VALUE_TRUE : JsonToken.VALUE_FALSE);
    }
    
    @Override
    public void writeNull() throws IOException, JsonGenerationException {
        this._append(JsonToken.VALUE_NULL);
    }
    
    @Override
    public void writeObject(final Object value) throws IOException {
        if (value == null) {
            this.writeNull();
            return;
        }
        final Class<?> raw = value.getClass();
        if (raw == byte[].class) {
            this._append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
            return;
        }
        if (this._objectCodec == null) {
            this._append(JsonToken.VALUE_EMBEDDED_OBJECT, value);
        }
        else {
            this._objectCodec.writeValue(this, value);
        }
    }
    
    @Override
    public void writeTree(final TreeNode node) throws IOException {
        if (node == null) {
            this.writeNull();
            return;
        }
        if (this._objectCodec == null) {
            this._append(JsonToken.VALUE_EMBEDDED_OBJECT, node);
        }
        else {
            this._objectCodec.writeTree(this, node);
        }
    }
    
    @Override
    public void writeBinary(final Base64Variant b64variant, final byte[] data, final int offset, final int len) throws IOException, JsonGenerationException {
        final byte[] copy = new byte[len];
        System.arraycopy(data, offset, copy, 0, len);
        this.writeObject(copy);
    }
    
    @Override
    public int writeBinary(final Base64Variant b64variant, final InputStream data, final int dataLength) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean canWriteTypeId() {
        return this._hasNativeTypeIds;
    }
    
    @Override
    public boolean canWriteObjectId() {
        return this._hasNativeObjectIds;
    }
    
    @Override
    public void writeTypeId(final Object id) {
        this._typeId = id;
        this._hasNativeId = true;
    }
    
    @Override
    public void writeObjectId(final Object id) {
        this._objectId = id;
        this._hasNativeId = true;
    }
    
    @Override
    public void copyCurrentEvent(final JsonParser jp) throws IOException, JsonProcessingException {
        if (this._mayHaveNativeIds) {
            this._checkNativeIds(jp);
        }
        Label_0346: {
            switch (jp.getCurrentToken()) {
                case START_OBJECT: {
                    this.writeStartObject();
                    break;
                }
                case END_OBJECT: {
                    this.writeEndObject();
                    break;
                }
                case START_ARRAY: {
                    this.writeStartArray();
                    break;
                }
                case END_ARRAY: {
                    this.writeEndArray();
                    break;
                }
                case FIELD_NAME: {
                    this.writeFieldName(jp.getCurrentName());
                    break;
                }
                case VALUE_STRING: {
                    if (jp.hasTextCharacters()) {
                        this.writeString(jp.getTextCharacters(), jp.getTextOffset(), jp.getTextLength());
                        break;
                    }
                    this.writeString(jp.getText());
                    break;
                }
                case VALUE_NUMBER_INT: {
                    switch (jp.getNumberType()) {
                        case INT: {
                            this.writeNumber(jp.getIntValue());
                            break Label_0346;
                        }
                        case BIG_INTEGER: {
                            this.writeNumber(jp.getBigIntegerValue());
                            break Label_0346;
                        }
                        default: {
                            this.writeNumber(jp.getLongValue());
                            break Label_0346;
                        }
                    }
                    break;
                }
                case VALUE_NUMBER_FLOAT: {
                    switch (jp.getNumberType()) {
                        case BIG_DECIMAL: {
                            this.writeNumber(jp.getDecimalValue());
                            break Label_0346;
                        }
                        case FLOAT: {
                            this.writeNumber(jp.getFloatValue());
                            break Label_0346;
                        }
                        default: {
                            this.writeNumber(jp.getDoubleValue());
                            break Label_0346;
                        }
                    }
                    break;
                }
                case VALUE_TRUE: {
                    this.writeBoolean(true);
                    break;
                }
                case VALUE_FALSE: {
                    this.writeBoolean(false);
                    break;
                }
                case VALUE_NULL: {
                    this.writeNull();
                    break;
                }
                case VALUE_EMBEDDED_OBJECT: {
                    this.writeObject(jp.getEmbeddedObject());
                    break;
                }
                default: {
                    throw new RuntimeException("Internal error: should never end up through this code path");
                }
            }
        }
    }
    
    @Override
    public void copyCurrentStructure(final JsonParser jp) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.FIELD_NAME) {
            if (this._mayHaveNativeIds) {
                this._checkNativeIds(jp);
            }
            this.writeFieldName(jp.getCurrentName());
            t = jp.nextToken();
        }
        if (this._mayHaveNativeIds) {
            this._checkNativeIds(jp);
        }
        switch (t) {
            case START_ARRAY: {
                this.writeStartArray();
                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    this.copyCurrentStructure(jp);
                }
                this.writeEndArray();
                break;
            }
            case START_OBJECT: {
                this.writeStartObject();
                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    this.copyCurrentStructure(jp);
                }
                this.writeEndObject();
                break;
            }
            default: {
                this.copyCurrentEvent(jp);
                break;
            }
        }
    }
    
    private final void _checkNativeIds(final JsonParser jp) throws IOException, JsonProcessingException {
        final Object typeId = jp.getTypeId();
        this._typeId = typeId;
        if (typeId != null) {
            this._hasNativeId = true;
        }
        if ((this._objectId = jp.getObjectId()) != null) {
            this._hasNativeId = true;
        }
    }
    
    protected final void _append(final JsonToken type) {
        final Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, this._objectId, this._typeId) : this._last.append(this._appendAt, type);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    protected final void _append(final JsonToken type, final Object value) {
        final Segment next = this._hasNativeId ? this._last.append(this._appendAt, type, value, this._objectId, this._typeId) : this._last.append(this._appendAt, type, value);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    protected final void _appendRaw(final int rawType, final Object value) {
        final Segment next = this._hasNativeId ? this._last.appendRaw(this._appendAt, rawType, value, this._objectId, this._typeId) : this._last.appendRaw(this._appendAt, rawType, value);
        if (next == null) {
            ++this._appendAt;
        }
        else {
            this._last = next;
            this._appendAt = 1;
        }
    }
    
    @Override
    protected void _reportUnsupportedOperation() {
        throw new UnsupportedOperationException("Called operation not supported for TokenBuffer");
    }
    
    static {
        DEFAULT_GENERATOR_FEATURES = Feature.collectDefaults();
    }
    
    protected static final class Parser extends ParserMinimalBase
    {
        protected ObjectCodec _codec;
        protected final boolean _hasNativeTypeIds;
        protected final boolean _hasNativeObjectIds;
        protected final boolean _hasNativeIds;
        protected Segment _segment;
        protected int _segmentPtr;
        protected JsonReadContext _parsingContext;
        protected boolean _closed;
        protected transient ByteArrayBuilder _byteBuilder;
        protected JsonLocation _location;
        
        @Deprecated
        protected Parser(final Segment firstSeg, final ObjectCodec codec) {
            this(firstSeg, codec, false, false);
        }
        
        public Parser(final Segment firstSeg, final ObjectCodec codec, final boolean hasNativeTypeIds, final boolean hasNativeObjectIds) {
            super(0);
            this._location = null;
            this._segment = firstSeg;
            this._segmentPtr = -1;
            this._codec = codec;
            this._parsingContext = JsonReadContext.createRootContext(null);
            this._hasNativeTypeIds = hasNativeTypeIds;
            this._hasNativeObjectIds = hasNativeObjectIds;
            this._hasNativeIds = (hasNativeTypeIds | hasNativeObjectIds);
        }
        
        public void setLocation(final JsonLocation l) {
            this._location = l;
        }
        
        @Override
        public ObjectCodec getCodec() {
            return this._codec;
        }
        
        @Override
        public void setCodec(final ObjectCodec c) {
            this._codec = c;
        }
        
        @Override
        public Version version() {
            return PackageVersion.VERSION;
        }
        
        public JsonToken peekNextToken() throws IOException, JsonParseException {
            if (this._closed) {
                return null;
            }
            Segment seg = this._segment;
            int ptr = this._segmentPtr + 1;
            if (ptr >= 16) {
                ptr = 0;
                seg = ((seg == null) ? null : seg.next());
            }
            return (seg == null) ? null : seg.type(ptr);
        }
        
        @Override
        public void close() throws IOException {
            if (!this._closed) {
                this._closed = true;
            }
        }
        
        @Override
        public JsonToken nextToken() throws IOException, JsonParseException {
            if (this._closed || this._segment == null) {
                return null;
            }
            if (++this._segmentPtr >= 16) {
                this._segmentPtr = 0;
                this._segment = this._segment.next();
                if (this._segment == null) {
                    return null;
                }
            }
            this._currToken = this._segment.type(this._segmentPtr);
            if (this._currToken == JsonToken.FIELD_NAME) {
                final Object ob = this._currentObject();
                final String name = (String)((ob instanceof String) ? ob : ob.toString());
                this._parsingContext.setCurrentName(name);
            }
            else if (this._currToken == JsonToken.START_OBJECT) {
                this._parsingContext = this._parsingContext.createChildObjectContext(-1, -1);
            }
            else if (this._currToken == JsonToken.START_ARRAY) {
                this._parsingContext = this._parsingContext.createChildArrayContext(-1, -1);
            }
            else if (this._currToken == JsonToken.END_OBJECT || this._currToken == JsonToken.END_ARRAY) {
                this._parsingContext = this._parsingContext.getParent();
                if (this._parsingContext == null) {
                    this._parsingContext = JsonReadContext.createRootContext(null);
                }
            }
            return this._currToken;
        }
        
        @Override
        public boolean isClosed() {
            return this._closed;
        }
        
        @Override
        public JsonStreamContext getParsingContext() {
            return this._parsingContext;
        }
        
        @Override
        public JsonLocation getTokenLocation() {
            return this.getCurrentLocation();
        }
        
        @Override
        public JsonLocation getCurrentLocation() {
            return (this._location == null) ? JsonLocation.NA : this._location;
        }
        
        @Override
        public String getCurrentName() {
            return this._parsingContext.getCurrentName();
        }
        
        @Override
        public void overrideCurrentName(final String name) {
            JsonReadContext ctxt = this._parsingContext;
            if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
                ctxt = ctxt.getParent();
            }
            try {
                ctxt.setCurrentName(name);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public String getText() {
            if (this._currToken == JsonToken.VALUE_STRING || this._currToken == JsonToken.FIELD_NAME) {
                final Object ob = this._currentObject();
                if (ob instanceof String) {
                    return (String)ob;
                }
                return (ob == null) ? null : ob.toString();
            }
            else {
                if (this._currToken == null) {
                    return null;
                }
                switch (this._currToken) {
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT: {
                        final Object ob = this._currentObject();
                        return (ob == null) ? null : ob.toString();
                    }
                    default: {
                        return this._currToken.asString();
                    }
                }
            }
        }
        
        @Override
        public char[] getTextCharacters() {
            final String str = this.getText();
            return (char[])((str == null) ? null : str.toCharArray());
        }
        
        @Override
        public int getTextLength() {
            final String str = this.getText();
            return (str == null) ? 0 : str.length();
        }
        
        @Override
        public int getTextOffset() {
            return 0;
        }
        
        @Override
        public boolean hasTextCharacters() {
            return false;
        }
        
        @Override
        public BigInteger getBigIntegerValue() throws IOException, JsonParseException {
            final Number n = this.getNumberValue();
            if (n instanceof BigInteger) {
                return (BigInteger)n;
            }
            if (this.getNumberType() == NumberType.BIG_DECIMAL) {
                return ((BigDecimal)n).toBigInteger();
            }
            return BigInteger.valueOf(n.longValue());
        }
        
        @Override
        public BigDecimal getDecimalValue() throws IOException, JsonParseException {
            final Number n = this.getNumberValue();
            if (n instanceof BigDecimal) {
                return (BigDecimal)n;
            }
            switch (this.getNumberType()) {
                case INT:
                case LONG: {
                    return BigDecimal.valueOf(n.longValue());
                }
                case BIG_INTEGER: {
                    return new BigDecimal((BigInteger)n);
                }
                default: {
                    return BigDecimal.valueOf(n.doubleValue());
                }
            }
        }
        
        @Override
        public double getDoubleValue() throws IOException, JsonParseException {
            return this.getNumberValue().doubleValue();
        }
        
        @Override
        public float getFloatValue() throws IOException, JsonParseException {
            return this.getNumberValue().floatValue();
        }
        
        @Override
        public int getIntValue() throws IOException, JsonParseException {
            if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
                return ((Number)this._currentObject()).intValue();
            }
            return this.getNumberValue().intValue();
        }
        
        @Override
        public long getLongValue() throws IOException, JsonParseException {
            return this.getNumberValue().longValue();
        }
        
        @Override
        public NumberType getNumberType() throws IOException, JsonParseException {
            final Number n = this.getNumberValue();
            if (n instanceof Integer) {
                return NumberType.INT;
            }
            if (n instanceof Long) {
                return NumberType.LONG;
            }
            if (n instanceof Double) {
                return NumberType.DOUBLE;
            }
            if (n instanceof BigDecimal) {
                return NumberType.BIG_DECIMAL;
            }
            if (n instanceof BigInteger) {
                return NumberType.BIG_INTEGER;
            }
            if (n instanceof Float) {
                return NumberType.FLOAT;
            }
            if (n instanceof Short) {
                return NumberType.INT;
            }
            return null;
        }
        
        @Override
        public final Number getNumberValue() throws IOException, JsonParseException {
            this._checkIsNumber();
            final Object value = this._currentObject();
            if (value instanceof Number) {
                return (Number)value;
            }
            if (value instanceof String) {
                final String str = (String)value;
                if (str.indexOf(46) >= 0) {
                    return Double.parseDouble(str);
                }
                return Long.parseLong(str);
            }
            else {
                if (value == null) {
                    return null;
                }
                throw new IllegalStateException("Internal error: entry should be a Number, but is of type " + value.getClass().getName());
            }
        }
        
        @Override
        public Object getEmbeddedObject() {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                return this._currentObject();
            }
            return null;
        }
        
        @Override
        public byte[] getBinaryValue(final Base64Variant b64variant) throws IOException, JsonParseException {
            if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
                final Object ob = this._currentObject();
                if (ob instanceof byte[]) {
                    return (byte[])ob;
                }
            }
            if (this._currToken != JsonToken.VALUE_STRING) {
                throw this._constructError("Current token (" + this._currToken + ") not VALUE_STRING (or VALUE_EMBEDDED_OBJECT with byte[]), can not access as binary");
            }
            final String str = this.getText();
            if (str == null) {
                return null;
            }
            ByteArrayBuilder builder = this._byteBuilder;
            if (builder == null) {
                builder = (this._byteBuilder = new ByteArrayBuilder(100));
            }
            else {
                this._byteBuilder.reset();
            }
            this._decodeBase64(str, builder, b64variant);
            return builder.toByteArray();
        }
        
        @Override
        public int readBinaryValue(final Base64Variant b64variant, final OutputStream out) throws IOException, JsonParseException {
            final byte[] data = this.getBinaryValue(b64variant);
            if (data != null) {
                out.write(data, 0, data.length);
                return data.length;
            }
            return 0;
        }
        
        @Override
        public boolean canReadObjectId() {
            return this._hasNativeObjectIds;
        }
        
        @Override
        public boolean canReadTypeId() {
            return this._hasNativeTypeIds;
        }
        
        @Override
        public Object getTypeId() {
            return this._segment.findTypeId(this._segmentPtr);
        }
        
        @Override
        public Object getObjectId() {
            return this._segment.findObjectId(this._segmentPtr);
        }
        
        protected final Object _currentObject() {
            return this._segment.get(this._segmentPtr);
        }
        
        protected final void _checkIsNumber() throws JsonParseException {
            if (this._currToken == null || !this._currToken.isNumeric()) {
                throw this._constructError("Current token (" + this._currToken + ") not numeric, can not use numeric value accessors");
            }
        }
        
        @Override
        protected void _handleEOF() throws JsonParseException {
            this._throwInternal();
        }
    }
    
    protected static final class Segment
    {
        public static final int TOKENS_PER_SEGMENT = 16;
        private static final JsonToken[] TOKEN_TYPES_BY_INDEX;
        protected Segment _next;
        protected long _tokenTypes;
        protected final Object[] _tokens;
        protected TreeMap<Integer, Object> _nativeIds;
        
        public Segment() {
            this._tokens = new Object[16];
        }
        
        public JsonToken type(final int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            final int ix = (int)l & 0xF;
            return Segment.TOKEN_TYPES_BY_INDEX[ix];
        }
        
        public int rawType(final int index) {
            long l = this._tokenTypes;
            if (index > 0) {
                l >>= index << 2;
            }
            final int ix = (int)l & 0xF;
            return ix;
        }
        
        public Object get(final int index) {
            return this._tokens[index];
        }
        
        public Segment next() {
            return this._next;
        }
        
        public boolean hasIds() {
            return this._nativeIds != null;
        }
        
        public Segment append(final int index, final JsonToken tokenType) {
            if (index < 16) {
                this.set(index, tokenType);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType);
            return this._next;
        }
        
        public Segment append(final int index, final JsonToken tokenType, final Object objectId, final Object typeId) {
            if (index < 16) {
                this.set(index, tokenType, objectId, typeId);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType, objectId, typeId);
            return this._next;
        }
        
        public Segment append(final int index, final JsonToken tokenType, final Object value) {
            if (index < 16) {
                this.set(index, tokenType, value);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType, value);
            return this._next;
        }
        
        public Segment append(final int index, final JsonToken tokenType, final Object value, final Object objectId, final Object typeId) {
            if (index < 16) {
                this.set(index, tokenType, value, objectId, typeId);
                return null;
            }
            (this._next = new Segment()).set(0, tokenType, value, objectId, typeId);
            return this._next;
        }
        
        public Segment appendRaw(final int index, final int rawTokenType, final Object value) {
            if (index < 16) {
                this.set(index, rawTokenType, value);
                return null;
            }
            (this._next = new Segment()).set(0, rawTokenType, value);
            return this._next;
        }
        
        public Segment appendRaw(final int index, final int rawTokenType, final Object value, final Object objectId, final Object typeId) {
            if (index < 16) {
                this.set(index, rawTokenType, value, objectId, typeId);
                return null;
            }
            (this._next = new Segment()).set(0, rawTokenType, value, objectId, typeId);
            return this._next;
        }
        
        private void set(final int index, final JsonToken tokenType) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }
        
        private void set(final int index, final JsonToken tokenType, final Object objectId, final Object typeId) {
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }
        
        private void set(final int index, final JsonToken tokenType, final Object value) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }
        
        private void set(final int index, final JsonToken tokenType, final Object value, final Object objectId, final Object typeId) {
            this._tokens[index] = value;
            long typeCode = tokenType.ordinal();
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }
        
        private void set(final int index, final int rawTokenType, final Object value) {
            this._tokens[index] = value;
            long typeCode = rawTokenType;
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
        }
        
        private void set(final int index, final int rawTokenType, final Object value, final Object objectId, final Object typeId) {
            this._tokens[index] = value;
            long typeCode = rawTokenType;
            if (index > 0) {
                typeCode <<= index << 2;
            }
            this._tokenTypes |= typeCode;
            this.assignNativeIds(index, objectId, typeId);
        }
        
        private final void assignNativeIds(final int index, final Object objectId, final Object typeId) {
            if (this._nativeIds == null) {
                this._nativeIds = new TreeMap<Integer, Object>();
            }
            if (objectId != null) {
                this._nativeIds.put(this._objectIdIndex(index), objectId);
            }
            if (typeId != null) {
                this._nativeIds.put(this._typeIdIndex(index), typeId);
            }
        }
        
        public Object findObjectId(final int index) {
            return (this._nativeIds == null) ? null : this._nativeIds.get(this._objectIdIndex(index));
        }
        
        public Object findTypeId(final int index) {
            return (this._nativeIds == null) ? null : this._nativeIds.get(this._typeIdIndex(index));
        }
        
        private final int _typeIdIndex(final int i) {
            return i + i;
        }
        
        private final int _objectIdIndex(final int i) {
            return i + i + 1;
        }
        
        static {
            TOKEN_TYPES_BY_INDEX = new JsonToken[16];
            final JsonToken[] t = JsonToken.values();
            System.arraycopy(t, 1, Segment.TOKEN_TYPES_BY_INDEX, 1, Math.min(15, t.length - 1));
        }
    }
}
