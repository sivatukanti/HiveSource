// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.core.base;

import org.apache.htrace.shaded.fasterxml.jackson.core.util.VersionUtil;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonProcessingException;
import org.apache.htrace.shaded.fasterxml.jackson.core.util.ByteArrayBuilder;
import org.apache.htrace.shaded.fasterxml.jackson.core.io.NumberInput;
import org.apache.htrace.shaded.fasterxml.jackson.core.Base64Variant;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonStreamContext;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonToken;
import org.apache.htrace.shaded.fasterxml.jackson.core.JsonParser;

public abstract class ParserMinimalBase extends JsonParser
{
    protected static final int INT_TAB = 9;
    protected static final int INT_LF = 10;
    protected static final int INT_CR = 13;
    protected static final int INT_SPACE = 32;
    protected static final int INT_LBRACKET = 91;
    protected static final int INT_RBRACKET = 93;
    protected static final int INT_LCURLY = 123;
    protected static final int INT_RCURLY = 125;
    protected static final int INT_QUOTE = 34;
    protected static final int INT_BACKSLASH = 92;
    protected static final int INT_SLASH = 47;
    protected static final int INT_COLON = 58;
    protected static final int INT_COMMA = 44;
    protected static final int INT_HASH = 35;
    protected static final int INT_PERIOD = 46;
    protected static final int INT_e = 101;
    protected static final int INT_E = 69;
    protected JsonToken _currToken;
    protected JsonToken _lastClearedToken;
    
    protected ParserMinimalBase() {
    }
    
    protected ParserMinimalBase(final int features) {
        super(features);
    }
    
    @Override
    public abstract JsonToken nextToken() throws IOException, JsonParseException;
    
    @Override
    public JsonToken getCurrentToken() {
        return this._currToken;
    }
    
    @Override
    public final int getCurrentTokenId() {
        final JsonToken t = this._currToken;
        return (t == null) ? 0 : t.id();
    }
    
    @Override
    public boolean hasCurrentToken() {
        return this._currToken != null;
    }
    
    @Override
    public JsonToken nextValue() throws IOException {
        JsonToken t = this.nextToken();
        if (t == JsonToken.FIELD_NAME) {
            t = this.nextToken();
        }
        return t;
    }
    
    @Override
    public JsonParser skipChildren() throws IOException {
        if (this._currToken != JsonToken.START_OBJECT && this._currToken != JsonToken.START_ARRAY) {
            return this;
        }
        int open = 1;
        while (true) {
            final JsonToken t = this.nextToken();
            if (t == null) {
                this._handleEOF();
                return this;
            }
            if (t.isStructStart()) {
                ++open;
            }
            else {
                if (t.isStructEnd() && --open == 0) {
                    return this;
                }
                continue;
            }
        }
    }
    
    protected abstract void _handleEOF() throws JsonParseException;
    
    @Override
    public abstract String getCurrentName() throws IOException;
    
    @Override
    public abstract void close() throws IOException;
    
    @Override
    public abstract boolean isClosed();
    
    @Override
    public abstract JsonStreamContext getParsingContext();
    
    @Override
    public void clearCurrentToken() {
        if (this._currToken != null) {
            this._lastClearedToken = this._currToken;
            this._currToken = null;
        }
    }
    
    @Override
    public JsonToken getLastClearedToken() {
        return this._lastClearedToken;
    }
    
    @Override
    public abstract void overrideCurrentName(final String p0);
    
    @Override
    public abstract String getText() throws IOException;
    
    @Override
    public abstract char[] getTextCharacters() throws IOException;
    
    @Override
    public abstract boolean hasTextCharacters();
    
    @Override
    public abstract int getTextLength() throws IOException;
    
    @Override
    public abstract int getTextOffset() throws IOException;
    
    @Override
    public abstract byte[] getBinaryValue(final Base64Variant p0) throws IOException;
    
    @Override
    public boolean getValueAsBoolean(final boolean defaultValue) throws IOException {
        final JsonToken t = this._currToken;
        if (t != null) {
            switch (t.id()) {
                case 6: {
                    final String str = this.getText().trim();
                    if ("true".equals(str)) {
                        return true;
                    }
                    if ("false".equals(str)) {
                        return false;
                    }
                    if (this._hasTextualNull(str)) {
                        return false;
                    }
                    break;
                }
                case 7: {
                    return this.getIntValue() != 0;
                }
                case 9: {
                    return true;
                }
                case 10:
                case 11: {
                    return false;
                }
                case 12: {
                    final Object value = this.getEmbeddedObject();
                    if (value instanceof Boolean) {
                        return (boolean)value;
                    }
                    break;
                }
            }
        }
        return defaultValue;
    }
    
    @Override
    public int getValueAsInt(final int defaultValue) throws IOException {
        final JsonToken t = this._currToken;
        if (t != null) {
            switch (t.id()) {
                case 6: {
                    final String str = this.getText();
                    if (this._hasTextualNull(str)) {
                        return 0;
                    }
                    return NumberInput.parseAsInt(str, defaultValue);
                }
                case 7:
                case 8: {
                    return this.getIntValue();
                }
                case 9: {
                    return 1;
                }
                case 10: {
                    return 0;
                }
                case 11: {
                    return 0;
                }
                case 12: {
                    final Object value = this.getEmbeddedObject();
                    if (value instanceof Number) {
                        return ((Number)value).intValue();
                    }
                    break;
                }
            }
        }
        return defaultValue;
    }
    
    @Override
    public long getValueAsLong(final long defaultValue) throws IOException {
        final JsonToken t = this._currToken;
        if (t != null) {
            switch (t.id()) {
                case 6: {
                    final String str = this.getText();
                    if (this._hasTextualNull(str)) {
                        return 0L;
                    }
                    return NumberInput.parseAsLong(str, defaultValue);
                }
                case 7:
                case 8: {
                    return this.getLongValue();
                }
                case 9: {
                    return 1L;
                }
                case 10:
                case 11: {
                    return 0L;
                }
                case 12: {
                    final Object value = this.getEmbeddedObject();
                    if (value instanceof Number) {
                        return ((Number)value).longValue();
                    }
                    break;
                }
            }
        }
        return defaultValue;
    }
    
    @Override
    public double getValueAsDouble(final double defaultValue) throws IOException {
        final JsonToken t = this._currToken;
        if (t != null) {
            switch (t.id()) {
                case 6: {
                    final String str = this.getText();
                    if (this._hasTextualNull(str)) {
                        return 0.0;
                    }
                    return NumberInput.parseAsDouble(str, defaultValue);
                }
                case 7:
                case 8: {
                    return this.getDoubleValue();
                }
                case 9: {
                    return 1.0;
                }
                case 10:
                case 11: {
                    return 0.0;
                }
                case 12: {
                    final Object value = this.getEmbeddedObject();
                    if (value instanceof Number) {
                        return ((Number)value).doubleValue();
                    }
                    break;
                }
            }
        }
        return defaultValue;
    }
    
    @Override
    public String getValueAsString(final String defaultValue) throws IOException {
        if (this._currToken != JsonToken.VALUE_STRING && (this._currToken == null || this._currToken == JsonToken.VALUE_NULL || !this._currToken.isScalarValue())) {
            return defaultValue;
        }
        return this.getText();
    }
    
    protected void _decodeBase64(final String str, final ByteArrayBuilder builder, final Base64Variant b64variant) throws IOException {
        try {
            b64variant.decode(str, builder);
        }
        catch (IllegalArgumentException e) {
            this._reportError(e.getMessage());
        }
    }
    
    @Deprecated
    protected void _reportInvalidBase64(final Base64Variant b64variant, final char ch, final int bindex, final String msg) throws JsonParseException {
        String base;
        if (ch <= ' ') {
            base = "Illegal white space character (code 0x" + Integer.toHexString(ch) + ") as character #" + (bindex + 1) + " of 4-char base64 unit: can only used between units";
        }
        else if (b64variant.usesPaddingChar(ch)) {
            base = "Unexpected padding character ('" + b64variant.getPaddingChar() + "') as character #" + (bindex + 1) + " of 4-char base64 unit: padding only legal as 3rd or 4th character";
        }
        else if (!Character.isDefined(ch) || Character.isISOControl(ch)) {
            base = "Illegal character (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        else {
            base = "Illegal character '" + ch + "' (code 0x" + Integer.toHexString(ch) + ") in base64 content";
        }
        if (msg != null) {
            base = base + ": " + msg;
        }
        throw this._constructError(base);
    }
    
    @Deprecated
    protected void _reportBase64EOF() throws JsonParseException {
        throw this._constructError("Unexpected end-of-String in base64 content");
    }
    
    protected boolean _hasTextualNull(final String value) {
        return "null".equals(value);
    }
    
    protected void _reportUnexpectedChar(final int ch, final String comment) throws JsonParseException {
        if (ch < 0) {
            this._reportInvalidEOF();
        }
        String msg = "Unexpected character (" + _getCharDesc(ch) + ")";
        if (comment != null) {
            msg = msg + ": " + comment;
        }
        this._reportError(msg);
    }
    
    protected void _reportInvalidEOF() throws JsonParseException {
        this._reportInvalidEOF(" in " + this._currToken);
    }
    
    protected void _reportInvalidEOF(final String msg) throws JsonParseException {
        this._reportError("Unexpected end-of-input" + msg);
    }
    
    protected void _reportInvalidEOFInValue() throws JsonParseException {
        this._reportInvalidEOF(" in a value");
    }
    
    protected void _reportMissingRootWS(final int ch) throws JsonParseException {
        this._reportUnexpectedChar(ch, "Expected space separating root-level values");
    }
    
    protected void _throwInvalidSpace(final int i) throws JsonParseException {
        final char c = (char)i;
        final String msg = "Illegal character (" + _getCharDesc(c) + "): only regular white space (\\r, \\n, \\t) is allowed between tokens";
        this._reportError(msg);
    }
    
    protected void _throwUnquotedSpace(final int i, final String ctxtDesc) throws JsonParseException {
        if (!this.isEnabled(Feature.ALLOW_UNQUOTED_CONTROL_CHARS) || i > 32) {
            final char c = (char)i;
            final String msg = "Illegal unquoted character (" + _getCharDesc(c) + "): has to be escaped using backslash to be included in " + ctxtDesc;
            this._reportError(msg);
        }
    }
    
    protected char _handleUnrecognizedCharacterEscape(final char ch) throws JsonProcessingException {
        if (this.isEnabled(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER)) {
            return ch;
        }
        if (ch == '\'' && this.isEnabled(Feature.ALLOW_SINGLE_QUOTES)) {
            return ch;
        }
        this._reportError("Unrecognized character escape " + _getCharDesc(ch));
        return ch;
    }
    
    protected static final String _getCharDesc(final int ch) {
        final char c = (char)ch;
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + ch + ")";
        }
        if (ch > 255) {
            return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
        }
        return "'" + c + "' (code " + ch + ")";
    }
    
    protected final void _reportError(final String msg) throws JsonParseException {
        throw this._constructError(msg);
    }
    
    protected final void _wrapError(final String msg, final Throwable t) throws JsonParseException {
        throw this._constructError(msg, t);
    }
    
    protected final void _throwInternal() {
        VersionUtil.throwInternal();
    }
    
    protected final JsonParseException _constructError(final String msg, final Throwable t) {
        return new JsonParseException(msg, this.getCurrentLocation(), t);
    }
    
    protected static byte[] _asciiBytes(final String str) {
        final byte[] b = new byte[str.length()];
        for (int i = 0, len = str.length(); i < len; ++i) {
            b[i] = (byte)str.charAt(i);
        }
        return b;
    }
    
    protected static String _ascii(final byte[] b) {
        try {
            return new String(b, "US-ASCII");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
