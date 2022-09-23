// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.util;

import org.apache.htrace.shaded.fasterxml.jackson.core.JsonStreamContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;
import org.apache.htrace.shaded.fasterxml.jackson.core.TreeNode;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.InputStream;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.SerializableString;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.CharacterEscapes;
import org.apache.htrace.shaded.fasterxml.jackson.core.PrettyPrinter;
import org.apache.htrace.shaded.fasterxml.jackson.core.Version;
import org.apache.htrace.shaded.fasterxml.jackson.core.FormatSchema;
import org.apache.htrace.shaded.fasterxml.jackson.core.ObjectCodec;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonGenerator;

public class JsonGeneratorDelegate extends JsonGenerator
{
    protected JsonGenerator delegate;
    protected boolean delegateCopyMethods;
    
    public JsonGeneratorDelegate(final JsonGenerator d) {
        this(d, true);
    }
    
    public JsonGeneratorDelegate(final JsonGenerator d, final boolean delegateCopyMethods) {
        this.delegate = d;
        this.delegateCopyMethods = delegateCopyMethods;
    }
    
    public JsonGenerator getDelegate() {
        return this.delegate;
    }
    
    @Override
    public ObjectCodec getCodec() {
        return this.delegate.getCodec();
    }
    
    @Override
    public JsonGenerator setCodec(final ObjectCodec oc) {
        this.delegate.setCodec(oc);
        return this;
    }
    
    @Override
    public void setSchema(final FormatSchema schema) {
        this.delegate.setSchema(schema);
    }
    
    @Override
    public FormatSchema getSchema() {
        return this.delegate.getSchema();
    }
    
    @Override
    public Version version() {
        return this.delegate.version();
    }
    
    @Override
    public Object getOutputTarget() {
        return this.delegate.getOutputTarget();
    }
    
    @Override
    public boolean canUseSchema(final FormatSchema schema) {
        return this.delegate.canUseSchema(schema);
    }
    
    @Override
    public boolean canWriteTypeId() {
        return this.delegate.canWriteTypeId();
    }
    
    @Override
    public boolean canWriteObjectId() {
        return this.delegate.canWriteObjectId();
    }
    
    @Override
    public boolean canWriteBinaryNatively() {
        return this.delegate.canWriteBinaryNatively();
    }
    
    @Override
    public boolean canOmitFields() {
        return this.delegate.canOmitFields();
    }
    
    @Override
    public JsonGenerator enable(final Feature f) {
        this.delegate.enable(f);
        return this;
    }
    
    @Override
    public JsonGenerator disable(final Feature f) {
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
    public JsonGenerator setFeatureMask(final int mask) {
        this.delegate.setFeatureMask(mask);
        return this;
    }
    
    @Override
    public JsonGenerator setPrettyPrinter(final PrettyPrinter pp) {
        this.delegate.setPrettyPrinter(pp);
        return this;
    }
    
    @Override
    public PrettyPrinter getPrettyPrinter() {
        return this.delegate.getPrettyPrinter();
    }
    
    @Override
    public JsonGenerator useDefaultPrettyPrinter() {
        this.delegate.useDefaultPrettyPrinter();
        return this;
    }
    
    @Override
    public JsonGenerator setHighestNonEscapedChar(final int charCode) {
        this.delegate.setHighestNonEscapedChar(charCode);
        return this;
    }
    
    @Override
    public int getHighestEscapedChar() {
        return this.delegate.getHighestEscapedChar();
    }
    
    @Override
    public CharacterEscapes getCharacterEscapes() {
        return this.delegate.getCharacterEscapes();
    }
    
    @Override
    public JsonGenerator setCharacterEscapes(final CharacterEscapes esc) {
        this.delegate.setCharacterEscapes(esc);
        return this;
    }
    
    @Override
    public JsonGenerator setRootValueSeparator(final SerializableString sep) {
        this.delegate.setRootValueSeparator(sep);
        return this;
    }
    
    @Override
    public void writeStartArray() throws IOException {
        this.delegate.writeStartArray();
    }
    
    @Override
    public void writeStartArray(final int size) throws IOException {
        this.delegate.writeStartArray(size);
    }
    
    @Override
    public void writeEndArray() throws IOException {
        this.delegate.writeEndArray();
    }
    
    @Override
    public void writeStartObject() throws IOException {
        this.delegate.writeStartObject();
    }
    
    @Override
    public void writeEndObject() throws IOException {
        this.delegate.writeEndObject();
    }
    
    @Override
    public void writeFieldName(final String name) throws IOException {
        this.delegate.writeFieldName(name);
    }
    
    @Override
    public void writeFieldName(final SerializableString name) throws IOException {
        this.delegate.writeFieldName(name);
    }
    
    @Override
    public void writeString(final String text) throws IOException {
        this.delegate.writeString(text);
    }
    
    @Override
    public void writeString(final char[] text, final int offset, final int len) throws IOException {
        this.delegate.writeString(text, offset, len);
    }
    
    @Override
    public void writeString(final SerializableString text) throws IOException {
        this.delegate.writeString(text);
    }
    
    @Override
    public void writeRawUTF8String(final byte[] text, final int offset, final int length) throws IOException {
        this.delegate.writeRawUTF8String(text, offset, length);
    }
    
    @Override
    public void writeUTF8String(final byte[] text, final int offset, final int length) throws IOException {
        this.delegate.writeUTF8String(text, offset, length);
    }
    
    @Override
    public void writeRaw(final String text) throws IOException {
        this.delegate.writeRaw(text);
    }
    
    @Override
    public void writeRaw(final String text, final int offset, final int len) throws IOException {
        this.delegate.writeRaw(text, offset, len);
    }
    
    @Override
    public void writeRaw(final SerializableString raw) throws IOException {
        this.delegate.writeRaw(raw);
    }
    
    @Override
    public void writeRaw(final char[] text, final int offset, final int len) throws IOException {
        this.delegate.writeRaw(text, offset, len);
    }
    
    @Override
    public void writeRaw(final char c) throws IOException {
        this.delegate.writeRaw(c);
    }
    
    @Override
    public void writeRawValue(final String text) throws IOException {
        this.delegate.writeRawValue(text);
    }
    
    @Override
    public void writeRawValue(final String text, final int offset, final int len) throws IOException {
        this.delegate.writeRawValue(text, offset, len);
    }
    
    @Override
    public void writeRawValue(final char[] text, final int offset, final int len) throws IOException {
        this.delegate.writeRawValue(text, offset, len);
    }
    
    @Override
    public void writeBinary(final Base64Variant b64variant, final byte[] data, final int offset, final int len) throws IOException {
        this.delegate.writeBinary(b64variant, data, offset, len);
    }
    
    @Override
    public int writeBinary(final Base64Variant b64variant, final InputStream data, final int dataLength) throws IOException {
        return this.delegate.writeBinary(b64variant, data, dataLength);
    }
    
    @Override
    public void writeNumber(final short v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final int v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final long v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final BigInteger v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final double v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final float v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final BigDecimal v) throws IOException {
        this.delegate.writeNumber(v);
    }
    
    @Override
    public void writeNumber(final String encodedValue) throws IOException, UnsupportedOperationException {
        this.delegate.writeNumber(encodedValue);
    }
    
    @Override
    public void writeBoolean(final boolean state) throws IOException {
        this.delegate.writeBoolean(state);
    }
    
    @Override
    public void writeNull() throws IOException {
        this.delegate.writeNull();
    }
    
    @Override
    public void writeOmittedField(final String fieldName) throws IOException {
        this.delegate.writeOmittedField(fieldName);
    }
    
    @Override
    public void writeObjectId(final Object id) throws IOException {
        this.delegate.writeObjectId(id);
    }
    
    @Override
    public void writeObjectRef(final Object id) throws IOException {
        this.delegate.writeObjectRef(id);
    }
    
    @Override
    public void writeTypeId(final Object id) throws IOException {
        this.delegate.writeTypeId(id);
    }
    
    @Override
    public void writeObject(final Object pojo) throws IOException, JsonProcessingException {
        if (this.delegateCopyMethods) {
            this.delegate.writeObject(pojo);
            return;
        }
        if (pojo == null) {
            this.writeNull();
        }
        else {
            if (this.getCodec() != null) {
                this.getCodec().writeValue(this, pojo);
                return;
            }
            this._writeSimpleObject(pojo);
        }
    }
    
    @Override
    public void writeTree(final TreeNode rootNode) throws IOException {
        if (this.delegateCopyMethods) {
            this.delegate.writeTree(rootNode);
            return;
        }
        if (rootNode == null) {
            this.writeNull();
        }
        else {
            if (this.getCodec() == null) {
                throw new IllegalStateException("No ObjectCodec defined");
            }
            this.getCodec().writeValue(this, rootNode);
        }
    }
    
    @Override
    public void copyCurrentEvent(final JsonParser jp) throws IOException {
        if (this.delegateCopyMethods) {
            this.delegate.copyCurrentEvent(jp);
        }
        else {
            super.copyCurrentEvent(jp);
        }
    }
    
    @Override
    public void copyCurrentStructure(final JsonParser jp) throws IOException {
        if (this.delegateCopyMethods) {
            this.delegate.copyCurrentStructure(jp);
        }
        else {
            super.copyCurrentStructure(jp);
        }
    }
    
    @Override
    public JsonStreamContext getOutputContext() {
        return this.delegate.getOutputContext();
    }
    
    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
    
    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }
}
