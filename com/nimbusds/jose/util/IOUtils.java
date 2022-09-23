// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.io.InputStream;

public class IOUtils
{
    public static String readInputStreamToString(final InputStream stream, final Charset charset) throws IOException {
        final char[] buffer = new char[1024];
        final StringBuilder out = new StringBuilder();
        final Reader in = new InputStreamReader(stream, charset);
        while (true) {
            final int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0) {
                break;
            }
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
    
    public static String readFileToString(final File file, final Charset charset) throws IOException {
        return readInputStreamToString(new FileInputStream(file), charset);
    }
    
    private IOUtils() {
    }
}
