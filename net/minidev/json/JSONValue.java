// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import net.minidev.json.reader.JsonWriterI;
import net.minidev.json.writer.FakeMapper;
import net.minidev.json.parser.ParseException;
import java.io.IOException;
import net.minidev.json.writer.CompessorMapper;
import net.minidev.json.writer.UpdaterMapper;
import java.io.Reader;
import net.minidev.json.writer.JsonReaderI;
import net.minidev.json.parser.JSONParser;
import java.io.InputStream;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.reader.JsonWriter;

public class JSONValue
{
    public static JSONStyle COMPRESSION;
    public static final JsonWriter defaultWriter;
    public static final JsonReader defaultReader;
    
    static {
        JSONValue.COMPRESSION = JSONStyle.NO_COMPRESS;
        defaultWriter = new JsonWriter();
        defaultReader = new JsonReader();
    }
    
    public static Object parse(final InputStream in) {
        try {
            return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Object parse(final byte[] in) {
        try {
            return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final InputStream in, final Class<T> mapTo) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Object parse(final Reader in) {
        try {
            return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final byte[] in, final Class<T> mapTo) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final Reader in, final Class<T> mapTo) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final Reader in, final T toUpdate) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, new UpdaterMapper<T>(JSONValue.defaultReader, toUpdate));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    protected static <T> T parse(final Reader in, final JsonReaderI<T> mapper) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, mapper);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final String in, final Class<T> mapTo) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final InputStream in, final T toUpdate) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, new UpdaterMapper<T>(JSONValue.defaultReader, toUpdate));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static <T> T parse(final String in, final T toUpdate) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, new UpdaterMapper<T>(JSONValue.defaultReader, toUpdate));
        }
        catch (Exception e) {
            return null;
        }
    }
    
    protected static <T> T parse(final byte[] in, final JsonReaderI<T> mapper) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, mapper);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    protected static <T> T parse(final String in, final JsonReaderI<T> mapper) {
        try {
            final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            return p.parse(in, mapper);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Object parse(final String s) {
        try {
            return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(s);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Object parseKeepingOrder(final Reader in) {
        try {
            return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in, JSONValue.defaultReader.DEFAULT_ORDERED);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static Object parseKeepingOrder(final String in) {
        try {
            return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in, JSONValue.defaultReader.DEFAULT_ORDERED);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static String compress(final String input, final JSONStyle style) {
        try {
            final StringBuilder sb = new StringBuilder();
            new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(input, (JsonReaderI<Object>)new CompessorMapper(JSONValue.defaultReader, sb, style));
            return sb.toString();
        }
        catch (Exception e) {
            return input;
        }
    }
    
    public static String compress(final String input) {
        return compress(input, JSONStyle.MAX_COMPRESS);
    }
    
    public static String uncompress(final String input) {
        return compress(input, JSONStyle.NO_COMPRESS);
    }
    
    public static Object parseWithException(final byte[] in) throws IOException, ParseException {
        return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public static Object parseWithException(final InputStream in) throws IOException, ParseException {
        return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public static Object parseWithException(final Reader in) throws IOException, ParseException {
        return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public static Object parseWithException(final String s) throws ParseException {
        return new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(s, JSONValue.defaultReader.DEFAULT);
    }
    
    public static <T> T parseWithException(final String in, final Class<T> mapTo) throws ParseException {
        final JSONParser p = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        return p.parse(in, (JsonReaderI<T>)JSONValue.defaultReader.getMapper((Class<T>)mapTo));
    }
    
    public static Object parseStrict(final Reader in) throws IOException, ParseException {
        return new JSONParser(656).parse(in, JSONValue.defaultReader.DEFAULT);
    }
    
    public static Object parseStrict(final String s) throws ParseException {
        return new JSONParser(656).parse(s, JSONValue.defaultReader.DEFAULT);
    }
    
    public static boolean isValidJsonStrict(final Reader in) throws IOException {
        try {
            new JSONParser(656).parse(in, FakeMapper.DEFAULT);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }
    
    public static boolean isValidJsonStrict(final String s) {
        try {
            new JSONParser(656).parse(s, FakeMapper.DEFAULT);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }
    
    public static boolean isValidJson(final Reader in) throws IOException {
        try {
            new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(in, FakeMapper.DEFAULT);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }
    
    public static boolean isValidJson(final String s) {
        try {
            new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE).parse(s, FakeMapper.DEFAULT);
            return true;
        }
        catch (ParseException e) {
            return false;
        }
    }
    
    public static void writeJSONString(final Object value, final Appendable out) throws IOException {
        writeJSONString(value, out, JSONValue.COMPRESSION);
    }
    
    public static <T> void remapField(final Class<T> type, final String jsonFieldName, final String javaFieldName) {
        JSONValue.defaultReader.remapField(type, jsonFieldName, javaFieldName);
        JSONValue.defaultWriter.remapField(type, javaFieldName, jsonFieldName);
    }
    
    public static <T> void registerWriter(final Class<?> cls, final JsonWriterI<T> writer) {
        JSONValue.defaultWriter.registerWriter(writer, cls);
    }
    
    public static <T> void registerReader(final Class<T> type, final JsonReaderI<T> mapper) {
        JSONValue.defaultReader.registerReader(type, mapper);
    }
    
    public static void writeJSONString(final Object value, final Appendable out, final JSONStyle compression) throws IOException {
        if (value == null) {
            out.append("null");
            return;
        }
        final Class<?> clz = value.getClass();
        JsonWriterI w = JSONValue.defaultWriter.getWrite(clz);
        if (w == null) {
            if (clz.isArray()) {
                w = JsonWriter.arrayWriter;
            }
            else {
                w = JSONValue.defaultWriter.getWriterByInterface(value.getClass());
                if (w == null) {
                    w = JsonWriter.beansWriterASM;
                }
            }
            JSONValue.defaultWriter.registerWriter((JsonWriterI<Object>)w, clz);
        }
        w.writeJSONString(value, out, compression);
    }
    
    public static String toJSONString(final Object value) {
        return toJSONString(value, JSONValue.COMPRESSION);
    }
    
    public static String toJSONString(final Object value, final JSONStyle compression) {
        final StringBuilder sb = new StringBuilder();
        try {
            writeJSONString(value, sb, compression);
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    public static String escape(final String s) {
        return escape(s, JSONValue.COMPRESSION);
    }
    
    public static String escape(final String s, final JSONStyle compression) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        compression.escape(s, sb);
        return sb.toString();
    }
    
    public static void escape(final String s, final Appendable ap) {
        escape(s, ap, JSONValue.COMPRESSION);
    }
    
    public static void escape(final String s, final Appendable ap, final JSONStyle compression) {
        if (s == null) {
            return;
        }
        compression.escape(s, ap);
    }
}
