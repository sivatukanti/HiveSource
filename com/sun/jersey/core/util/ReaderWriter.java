// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.util;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import javax.ws.rs.core.MediaType;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public final class ReaderWriter
{
    public static final Charset UTF8;
    public static final String BUFFER_SIZE_SYSTEM_PROPERTY = "com.sun.jersey.core.util.ReaderWriter.BufferSize";
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    public static final int BUFFER_SIZE;
    
    private static int getBufferSize() {
        final String v = System.getProperty("com.sun.jersey.core.util.ReaderWriter.BufferSize", Integer.toString(8192));
        try {
            final int i = Integer.valueOf(v);
            if (i <= 0) {
                throw new NumberFormatException();
            }
            return i;
        }
        catch (NumberFormatException ex) {
            return 8192;
        }
    }
    
    public static final void writeTo(final InputStream in, final OutputStream out) throws IOException {
        final byte[] data = new byte[ReaderWriter.BUFFER_SIZE];
        int read;
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }
    
    public static final void writeTo(final Reader in, final Writer out) throws IOException {
        final char[] data = new char[ReaderWriter.BUFFER_SIZE];
        int read;
        while ((read = in.read(data)) != -1) {
            out.write(data, 0, read);
        }
    }
    
    public static final Charset getCharset(final MediaType m) {
        final String name = (m == null) ? null : m.getParameters().get("charset");
        return (name == null) ? ReaderWriter.UTF8 : Charset.forName(name);
    }
    
    public static final String readFromAsString(final InputStream in, final MediaType type) throws IOException {
        return readFromAsString(new InputStreamReader(in, getCharset(type)));
    }
    
    public static final String readFromAsString(final Reader reader) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final char[] c = new char[ReaderWriter.BUFFER_SIZE];
        int l;
        while ((l = reader.read(c)) != -1) {
            sb.append(c, 0, l);
        }
        return sb.toString();
    }
    
    public static final void writeToAsString(final String s, final OutputStream out, final MediaType type) throws IOException {
        final Writer osw = new BufferedWriter(new OutputStreamWriter(out, getCharset(type)));
        osw.write(s);
        osw.flush();
    }
    
    static {
        UTF8 = Charset.forName("UTF-8");
        BUFFER_SIZE = getBufferSize();
    }
}
