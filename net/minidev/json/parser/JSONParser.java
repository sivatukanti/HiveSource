// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.parser;

import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import net.minidev.json.JSONValue;
import net.minidev.json.writer.JsonReaderI;

public class JSONParser
{
    public static final int ACCEPT_SIMPLE_QUOTE = 1;
    public static final int ACCEPT_NON_QUOTE = 2;
    public static final int ACCEPT_NAN = 4;
    public static final int IGNORE_CONTROL_CHAR = 8;
    public static final int USE_INTEGER_STORAGE = 16;
    public static final int ACCEPT_LEADING_ZERO = 32;
    public static final int ACCEPT_USELESS_COMMA = 64;
    public static final int USE_HI_PRECISION_FLOAT = 128;
    public static final int ACCEPT_TAILLING_DATA = 256;
    public static final int ACCEPT_TAILLING_SPACE = 512;
    public static final int REJECT_127_CHAR = 1024;
    public static final int MODE_PERMISSIVE = -1;
    public static final int MODE_RFC4627 = 656;
    public static final int MODE_JSON_SIMPLE = 1984;
    public static final int MODE_STRICTEST = 1168;
    public static int DEFAULT_PERMISSIVE_MODE;
    private int mode;
    private JSONParserInputStream pBinStream;
    private JSONParserByteArray pBytes;
    private JSONParserReader pStream;
    private JSONParserString pString;
    
    static {
        JSONParser.DEFAULT_PERMISSIVE_MODE = ((System.getProperty("JSON_SMART_SIMPLE") != null) ? 1984 : -1);
    }
    
    private JSONParserReader getPStream() {
        if (this.pStream == null) {
            this.pStream = new JSONParserReader(this.mode);
        }
        return this.pStream;
    }
    
    private JSONParserInputStream getPBinStream() {
        if (this.pBinStream == null) {
            this.pBinStream = new JSONParserInputStream(this.mode);
        }
        return this.pBinStream;
    }
    
    private JSONParserString getPString() {
        if (this.pString == null) {
            this.pString = new JSONParserString(this.mode);
        }
        return this.pString;
    }
    
    private JSONParserByteArray getPBytes() {
        if (this.pBytes == null) {
            this.pBytes = new JSONParserByteArray(this.mode);
        }
        return this.pBytes;
    }
    
    @Deprecated
    public JSONParser() {
        this.mode = JSONParser.DEFAULT_PERMISSIVE_MODE;
    }
    
    public JSONParser(final int permissifMode) {
        this.mode = permissifMode;
    }
    
    public Object parse(final byte[] in) throws ParseException {
        return this.getPBytes().parse(in);
    }
    
    public <T> T parse(final byte[] in, final JsonReaderI<T> mapper) throws ParseException {
        return this.getPBytes().parse(in, mapper);
    }
    
    public <T> T parse(final byte[] in, final Class<T> mapTo) throws ParseException {
        return this.getPBytes().parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
    }
    
    public Object parse(final InputStream in) throws ParseException, UnsupportedEncodingException {
        return this.getPBinStream().parse(in);
    }
    
    public <T> T parse(final InputStream in, final JsonReaderI<T> mapper) throws ParseException, UnsupportedEncodingException {
        return this.getPBinStream().parse(in, mapper);
    }
    
    public <T> T parse(final InputStream in, final Class<T> mapTo) throws ParseException, UnsupportedEncodingException {
        return this.getPBinStream().parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
    }
    
    public Object parse(final Reader in) throws ParseException {
        return this.getPStream().parse(in);
    }
    
    public <T> T parse(final Reader in, final JsonReaderI<T> mapper) throws ParseException {
        return this.getPStream().parse(in, mapper);
    }
    
    public <T> T parse(final Reader in, final Class<T> mapTo) throws ParseException {
        return this.getPStream().parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
    }
    
    public Object parse(final String in) throws ParseException {
        return this.getPString().parse(in);
    }
    
    public <T> T parse(final String in, final JsonReaderI<T> mapper) throws ParseException {
        return this.getPString().parse(in, mapper);
    }
    
    public <T> T parse(final String in, final Class<T> mapTo) throws ParseException {
        return this.getPString().parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
    }
}
