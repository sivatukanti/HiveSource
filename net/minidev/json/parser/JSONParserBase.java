// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import java.math.BigInteger;
import java.io.IOException;
import net.minidev.json.writer.JsonReaderI;
import java.math.BigDecimal;
import net.minidev.json.writer.JsonReader;

abstract class JSONParserBase
{
    protected char c;
    JsonReader base;
    public static final byte EOI = 26;
    protected static final char MAX_STOP = '~';
    private String lastKey;
    protected static boolean[] stopAll;
    protected static boolean[] stopArray;
    protected static boolean[] stopKey;
    protected static boolean[] stopValue;
    protected static boolean[] stopX;
    protected final MSB sb;
    protected Object xo;
    protected String xs;
    protected int pos;
    protected final boolean acceptLeadinZero;
    protected final boolean acceptNaN;
    protected final boolean acceptNonQuote;
    protected final boolean acceptSimpleQuote;
    protected final boolean acceptUselessComma;
    protected final boolean checkTaillingData;
    protected final boolean checkTaillingSpace;
    protected final boolean ignoreControlChar;
    protected final boolean useHiPrecisionFloat;
    protected final boolean useIntegerStorage;
    protected final boolean reject127;
    
    static {
        JSONParserBase.stopAll = new boolean[126];
        JSONParserBase.stopArray = new boolean[126];
        JSONParserBase.stopKey = new boolean[126];
        JSONParserBase.stopValue = new boolean[126];
        JSONParserBase.stopX = new boolean[126];
        JSONParserBase.stopKey[58] = (JSONParserBase.stopKey[26] = true);
        final boolean[] stopValue = JSONParserBase.stopValue;
        final int n = 44;
        final boolean[] stopValue2 = JSONParserBase.stopValue;
        final int n2 = 125;
        final boolean[] stopValue3 = JSONParserBase.stopValue;
        final int n3 = 26;
        final boolean b = true;
        stopValue3[n3] = b;
        stopValue[n] = (stopValue2[n2] = b);
        final boolean[] stopArray = JSONParserBase.stopArray;
        final int n4 = 44;
        final boolean[] stopArray2 = JSONParserBase.stopArray;
        final int n5 = 93;
        final boolean[] stopArray3 = JSONParserBase.stopArray;
        final int n6 = 26;
        final boolean b2 = true;
        stopArray3[n6] = b2;
        stopArray[n4] = (stopArray2[n5] = b2);
        JSONParserBase.stopX[26] = true;
        JSONParserBase.stopAll[44] = (JSONParserBase.stopAll[58] = true);
        final boolean[] stopAll = JSONParserBase.stopAll;
        final int n7 = 93;
        final boolean[] stopAll2 = JSONParserBase.stopAll;
        final int n8 = 125;
        final boolean[] stopAll3 = JSONParserBase.stopAll;
        final int n9 = 26;
        final boolean b3 = true;
        stopAll3[n9] = b3;
        stopAll[n7] = (stopAll2[n8] = b3);
    }
    
    public JSONParserBase(final int permissiveMode) {
        this.sb = new MSB(15);
        this.acceptNaN = ((permissiveMode & 0x4) > 0);
        this.acceptNonQuote = ((permissiveMode & 0x2) > 0);
        this.acceptSimpleQuote = ((permissiveMode & 0x1) > 0);
        this.ignoreControlChar = ((permissiveMode & 0x8) > 0);
        this.useIntegerStorage = ((permissiveMode & 0x10) > 0);
        this.acceptLeadinZero = ((permissiveMode & 0x20) > 0);
        this.acceptUselessComma = ((permissiveMode & 0x40) > 0);
        this.useHiPrecisionFloat = ((permissiveMode & 0x80) > 0);
        this.checkTaillingData = ((permissiveMode & 0x300) != 0x300);
        this.checkTaillingSpace = ((permissiveMode & 0x200) == 0x0);
        this.reject127 = ((permissiveMode & 0x400) > 0);
    }
    
    public void checkControleChar() throws ParseException {
        if (this.ignoreControlChar) {
            return;
        }
        for (int l = this.xs.length(), i = 0; i < l; ++i) {
            final char c = this.xs.charAt(i);
            if (c >= '\0') {
                if (c <= '\u001f') {
                    throw new ParseException(this.pos + i, 0, c);
                }
                if (c == '\u007f' && this.reject127) {
                    throw new ParseException(this.pos + i, 0, c);
                }
            }
        }
    }
    
    public void checkLeadinZero() throws ParseException {
        final int len = this.xs.length();
        if (len == 1) {
            return;
        }
        if (len == 2) {
            if (this.xs.equals("00")) {
                throw new ParseException(this.pos, 6, this.xs);
            }
        }
        else {
            final char c1 = this.xs.charAt(0);
            final char c2 = this.xs.charAt(1);
            if (c1 == '-') {
                final char c3 = this.xs.charAt(2);
                if (c2 == '0' && c3 >= '0' && c3 <= '9') {
                    throw new ParseException(this.pos, 6, this.xs);
                }
            }
            else if (c1 == '0' && c2 >= '0' && c2 <= '9') {
                throw new ParseException(this.pos, 6, this.xs);
            }
        }
    }
    
    protected Number extractFloat() throws ParseException {
        if (!this.acceptLeadinZero) {
            this.checkLeadinZero();
        }
        if (!this.useHiPrecisionFloat) {
            return Float.parseFloat(this.xs);
        }
        if (this.xs.length() > 18) {
            return new BigDecimal(this.xs);
        }
        return Double.parseDouble(this.xs);
    }
    
    protected <T> T parse(final JsonReaderI<T> mapper) throws ParseException {
        this.pos = -1;
        T result;
        try {
            this.read();
            result = (T)this.readFirst((JsonReaderI<Object>)mapper);
            if (this.checkTaillingData) {
                if (!this.checkTaillingSpace) {
                    this.skipSpace();
                }
                if (this.c != '\u001a') {
                    throw new ParseException(this.pos - 1, 1, this.c);
                }
            }
        }
        catch (IOException e) {
            throw new ParseException(this.pos, e);
        }
        this.xs = null;
        this.xo = null;
        return result;
    }
    
    protected Number parseNumber(final String s) throws ParseException {
        int p = 0;
        final int l = s.length();
        int max = 19;
        boolean neg;
        if (s.charAt(0) == '-') {
            ++p;
            ++max;
            neg = true;
            if (!this.acceptLeadinZero && l >= 3 && s.charAt(1) == '0') {
                throw new ParseException(this.pos, 6, s);
            }
        }
        else {
            neg = false;
            if (!this.acceptLeadinZero && l >= 2 && s.charAt(0) == '0') {
                throw new ParseException(this.pos, 6, s);
            }
        }
        boolean mustCheck;
        if (l < max) {
            max = l;
            mustCheck = false;
        }
        else {
            if (l > max) {
                return new BigInteger(s, 10);
            }
            max = l - 1;
            mustCheck = true;
        }
        long r;
        for (r = 0L; p < max; r = r * 10L + ('0' - s.charAt(p++))) {}
        if (mustCheck) {
            boolean isBig;
            if (r > -922337203685477580L) {
                isBig = false;
            }
            else if (r < -922337203685477580L) {
                isBig = true;
            }
            else if (neg) {
                isBig = (s.charAt(p) > '8');
            }
            else {
                isBig = (s.charAt(p) > '7');
            }
            if (isBig) {
                return new BigInteger(s, 10);
            }
            r = r * 10L + ('0' - s.charAt(p));
        }
        if (neg) {
            if (this.useIntegerStorage && r >= -2147483648L) {
                return (int)r;
            }
            return r;
        }
        else {
            r = -r;
            if (this.useIntegerStorage && r <= 2147483647L) {
                return (int)r;
            }
            return r;
        }
    }
    
    protected abstract void read() throws IOException;
    
    protected <T> T readArray(final JsonReaderI<T> mapper) throws ParseException, IOException {
        final Object current = mapper.createArray();
        if (this.c != '[') {
            throw new RuntimeException("Internal Error");
        }
        this.read();
        boolean needData = false;
        if (this.c == ',' && !this.acceptUselessComma) {
            throw new ParseException(this.pos, 0, this.c);
        }
        while (true) {
            switch (this.c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.read();
                    continue;
                }
                case ']': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, this.c);
                    }
                    this.read();
                    return mapper.convert(current);
                }
                case ':':
                case '}': {
                    throw new ParseException(this.pos, 0, this.c);
                }
                case ',': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, this.c);
                    }
                    this.read();
                    needData = true;
                    continue;
                }
                case '\u001a': {
                    throw new ParseException(this.pos - 1, 3, "EOF");
                }
                default: {
                    mapper.addValue(current, this.readMain(mapper, JSONParserBase.stopArray));
                    needData = false;
                    continue;
                }
            }
        }
    }
    
    protected <T> T readFirst(final JsonReaderI<T> mapper) throws ParseException, IOException {
        while (true) {
            switch (this.c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.read();
                    continue;
                }
                case ':':
                case ']':
                case '}': {
                    throw new ParseException(this.pos, 0, this.c);
                }
                case '{': {
                    return (T)this.readObject((JsonReaderI<Object>)mapper);
                }
                case '[': {
                    return (T)this.readArray((JsonReaderI<Object>)mapper);
                }
                case '\"':
                case '\'': {
                    this.readString();
                    return mapper.convert(this.xs);
                }
                case 'n': {
                    this.readNQString(JSONParserBase.stopX);
                    if ("null".equals(this.xs)) {
                        return null;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case 'f': {
                    this.readNQString(JSONParserBase.stopX);
                    if ("false".equals(this.xs)) {
                        return mapper.convert(Boolean.FALSE);
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case 't': {
                    this.readNQString(JSONParserBase.stopX);
                    if ("true".equals(this.xs)) {
                        return mapper.convert(Boolean.TRUE);
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case 'N': {
                    this.readNQString(JSONParserBase.stopX);
                    if (!this.acceptNaN) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    if ("NaN".equals(this.xs)) {
                        return mapper.convert(Float.NaN);
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    this.xo = this.readNumber(JSONParserBase.stopX);
                    return mapper.convert(this.xo);
                }
                default: {
                    this.readNQString(JSONParserBase.stopX);
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return mapper.convert(this.xs);
                }
            }
        }
    }
    
    protected Object readMain(final JsonReaderI<?> mapper, final boolean[] stop) throws ParseException, IOException {
        while (true) {
            switch (this.c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    this.read();
                    continue;
                }
                case ':':
                case ']':
                case '}': {
                    throw new ParseException(this.pos, 0, this.c);
                }
                case '{': {
                    return this.readObject(mapper.startObject(this.lastKey));
                }
                case '[': {
                    return this.readArray(mapper.startArray(this.lastKey));
                }
                case '\"':
                case '\'': {
                    this.readString();
                    return this.xs;
                }
                case 'n': {
                    this.readNQString(stop);
                    if ("null".equals(this.xs)) {
                        return null;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case 'f': {
                    this.readNQString(stop);
                    if ("false".equals(this.xs)) {
                        return Boolean.FALSE;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case 't': {
                    this.readNQString(stop);
                    if ("true".equals(this.xs)) {
                        return Boolean.TRUE;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case 'N': {
                    this.readNQString(stop);
                    if (!this.acceptNaN) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    if ("NaN".equals(this.xs)) {
                        return Float.NaN;
                    }
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': {
                    return this.readNumber(stop);
                }
                default: {
                    this.readNQString(stop);
                    if (!this.acceptNonQuote) {
                        throw new ParseException(this.pos, 1, this.xs);
                    }
                    return this.xs;
                }
            }
        }
    }
    
    protected abstract void readNoEnd() throws ParseException, IOException;
    
    protected abstract void readNQString(final boolean[] p0) throws IOException;
    
    protected abstract Object readNumber(final boolean[] p0) throws ParseException, IOException;
    
    protected <T> T readObject(final JsonReaderI<T> mapper) throws ParseException, IOException {
        if (this.c != '{') {
            throw new RuntimeException("Internal Error");
        }
        final Object current = mapper.createObject();
        boolean needData = false;
        boolean acceptData = true;
        while (true) {
            this.read();
            switch (this.c) {
                case '\t':
                case '\n':
                case '\r':
                case ' ': {
                    continue;
                }
                case ':':
                case '[':
                case ']':
                case '{': {
                    throw new ParseException(this.pos, 0, this.c);
                }
                case '}': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, this.c);
                    }
                    this.read();
                    return mapper.convert(current);
                }
                case ',': {
                    if (needData && !this.acceptUselessComma) {
                        throw new ParseException(this.pos, 0, this.c);
                    }
                    needData = (acceptData = true);
                    continue;
                }
                default: {
                    if (this.c == '\"' || this.c == '\'') {
                        this.readString();
                    }
                    else {
                        this.readNQString(JSONParserBase.stopKey);
                        if (!this.acceptNonQuote) {
                            throw new ParseException(this.pos, 1, this.xs);
                        }
                    }
                    final String key = this.xs;
                    if (!acceptData) {
                        throw new ParseException(this.pos, 1, key);
                    }
                    this.skipSpace();
                    if (this.c != ':') {
                        if (this.c == '\u001a') {
                            throw new ParseException(this.pos - 1, 3, null);
                        }
                        throw new ParseException(this.pos - 1, 0, this.c);
                    }
                    else {
                        this.readNoEnd();
                        this.lastKey = key;
                        final Object value = this.readMain(mapper, JSONParserBase.stopValue);
                        mapper.setValue(current, key, value);
                        this.lastKey = null;
                        this.skipSpace();
                        if (this.c == '}') {
                            this.read();
                            return mapper.convert(current);
                        }
                        if (this.c == '\u001a') {
                            throw new ParseException(this.pos - 1, 3, null);
                        }
                        if (this.c == ',') {
                            needData = (acceptData = true);
                            continue;
                        }
                        throw new ParseException(this.pos - 1, 1, this.c);
                    }
                    break;
                }
            }
        }
    }
    
    abstract void readS() throws IOException;
    
    protected abstract void readString() throws ParseException, IOException;
    
    protected void readString2() throws ParseException, IOException {
        final char sep = this.c;
        while (true) {
            this.read();
            switch (this.c) {
                case '\u001a': {
                    throw new ParseException(this.pos - 1, 3, null);
                }
                case '\"':
                case '\'': {
                    if (sep == this.c) {
                        this.read();
                        this.xs = this.sb.toString();
                        return;
                    }
                    this.sb.append(this.c);
                    continue;
                }
                case '\\': {
                    this.read();
                    switch (this.c) {
                        case 't': {
                            this.sb.append('\t');
                            continue;
                        }
                        case 'n': {
                            this.sb.append('\n');
                            continue;
                        }
                        case 'r': {
                            this.sb.append('\r');
                            continue;
                        }
                        case 'f': {
                            this.sb.append('\f');
                            continue;
                        }
                        case 'b': {
                            this.sb.append('\b');
                            continue;
                        }
                        case '\\': {
                            this.sb.append('\\');
                            continue;
                        }
                        case '/': {
                            this.sb.append('/');
                            continue;
                        }
                        case '\'': {
                            this.sb.append('\'');
                            continue;
                        }
                        case '\"': {
                            this.sb.append('\"');
                            continue;
                        }
                        case 'u': {
                            this.sb.append(this.readUnicode(4));
                            continue;
                        }
                        case 'x': {
                            this.sb.append(this.readUnicode(2));
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                    break;
                }
                case '\0':
                case '\u0001':
                case '\u0002':
                case '\u0003':
                case '\u0004':
                case '\u0005':
                case '\u0006':
                case '\u0007':
                case '\b':
                case '\t':
                case '\n':
                case '\u000b':
                case '\f':
                case '\r':
                case '\u000e':
                case '\u000f':
                case '\u0010':
                case '\u0011':
                case '\u0012':
                case '\u0013':
                case '\u0014':
                case '\u0015':
                case '\u0016':
                case '\u0017':
                case '\u0018':
                case '\u0019':
                case '\u001b':
                case '\u001c':
                case '\u001d':
                case '\u001e':
                case '\u001f': {
                    if (this.ignoreControlChar) {
                        continue;
                    }
                    throw new ParseException(this.pos, 0, this.c);
                }
                case '\u007f': {
                    if (this.ignoreControlChar) {
                        continue;
                    }
                    if (this.reject127) {
                        throw new ParseException(this.pos, 0, this.c);
                    }
                    break;
                }
            }
            this.sb.append(this.c);
        }
    }
    
    protected char readUnicode(final int totalChars) throws ParseException, IOException {
        int value = 0;
        for (int i = 0; i < totalChars; ++i) {
            value *= 16;
            this.read();
            if (this.c <= '9' && this.c >= '0') {
                value += this.c - '0';
            }
            else if (this.c <= 'F' && this.c >= 'A') {
                value += this.c - 'A' + 10;
            }
            else if (this.c >= 'a' && this.c <= 'f') {
                value += this.c - 'a' + 10;
            }
            else {
                if (this.c == '\u001a') {
                    throw new ParseException(this.pos, 3, "EOF");
                }
                throw new ParseException(this.pos, 4, this.c);
            }
        }
        return (char)value;
    }
    
    protected void skipDigits() throws IOException {
        while (this.c >= '0' && this.c <= '9') {
            this.readS();
        }
    }
    
    protected void skipNQString(final boolean[] stop) throws IOException {
        while (this.c != '\u001a' && (this.c < '\0' || this.c >= '~' || !stop[this.c])) {
            this.readS();
        }
    }
    
    protected void skipSpace() throws IOException {
        while (this.c <= ' ' && this.c != '\u001a') {
            this.readS();
        }
    }
    
    public static class MSB
    {
        char[] b;
        int p;
        
        public MSB(final int size) {
            this.b = new char[size];
            this.p = -1;
        }
        
        public void append(final char c) {
            ++this.p;
            if (this.b.length <= this.p) {
                final char[] t = new char[this.b.length * 2 + 1];
                System.arraycopy(this.b, 0, t, 0, this.b.length);
                this.b = t;
            }
            this.b[this.p] = c;
        }
        
        public void append(final int c) {
            ++this.p;
            if (this.b.length <= this.p) {
                final char[] t = new char[this.b.length * 2 + 1];
                System.arraycopy(this.b, 0, t, 0, this.b.length);
                this.b = t;
            }
            this.b[this.p] = (char)c;
        }
        
        @Override
        public String toString() {
            return new String(this.b, 0, this.p + 1);
        }
        
        public void clear() {
            this.p = -1;
        }
    }
}
