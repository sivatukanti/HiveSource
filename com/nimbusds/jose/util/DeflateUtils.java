// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Deflater;
import java.io.ByteArrayOutputStream;

public class DeflateUtils
{
    private static final boolean NOWRAP = true;
    
    public static byte[] compress(final byte[] bytes) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final DeflaterOutputStream def = new DeflaterOutputStream(out, new Deflater(8, true));
        def.write(bytes);
        def.close();
        return out.toByteArray();
    }
    
    public static byte[] decompress(final byte[] bytes) throws IOException {
        final InflaterInputStream inf = new InflaterInputStream(new ByteArrayInputStream(bytes), new Inflater(true));
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int len;
        while ((len = inf.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        inf.close();
        out.close();
        return out.toByteArray();
    }
    
    private DeflateUtils() {
    }
}
