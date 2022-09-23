// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro;

import java.util.Iterator;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Map;
import java.io.IOException;
import java.util.HashMap;

public class SchemaNormalization
{
    static final long EMPTY64 = -4513414715797952619L;
    
    private SchemaNormalization() {
    }
    
    public static String toParsingForm(final Schema s) {
        try {
            final Map<String, String> env = new HashMap<String, String>();
            return build(env, s, new StringBuilder()).toString();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] fingerprint(final String fpName, final byte[] data) throws NoSuchAlgorithmException {
        if (fpName.equals("CRC-64-AVRO")) {
            long fp = fingerprint64(data);
            final byte[] result = new byte[8];
            for (int i = 0; i < 8; ++i) {
                result[i] = (byte)fp;
                fp >>= 8;
            }
            return result;
        }
        final MessageDigest md = MessageDigest.getInstance(fpName);
        return md.digest(data);
    }
    
    public static long fingerprint64(final byte[] data) {
        long result = -4513414715797952619L;
        for (final byte b : data) {
            result = (result >>> 8 ^ FP64.FP_TABLE[(int)(result ^ (long)b) & 0xFF]);
        }
        return result;
    }
    
    public static byte[] parsingFingerprint(final String fpName, final Schema s) throws NoSuchAlgorithmException {
        try {
            return fingerprint(fpName, toParsingForm(s).getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static long parsingFingerprint64(final Schema s) {
        try {
            return fingerprint64(toParsingForm(s).getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static Appendable build(final Map<String, String> env, final Schema s, final Appendable o) throws IOException {
        boolean firstTime = true;
        final Schema.Type st = s.getType();
        switch (st) {
            default: {
                return o.append('\"').append(st.getName()).append('\"');
            }
            case UNION: {
                o.append('[');
                for (final Schema b : s.getTypes()) {
                    if (!firstTime) {
                        o.append(',');
                    }
                    else {
                        firstTime = false;
                    }
                    build(env, b, o);
                }
                return o.append(']');
            }
            case ARRAY:
            case MAP: {
                o.append("{\"type\":\"").append(st.getName()).append("\"");
                if (st == Schema.Type.ARRAY) {
                    build(env, s.getElementType(), o.append(",\"items\":"));
                }
                else {
                    build(env, s.getValueType(), o.append(",\"values\":"));
                }
                return o.append("}");
            }
            case ENUM:
            case FIXED:
            case RECORD: {
                final String name = s.getFullName();
                if (env.get(name) != null) {
                    return o.append(env.get(name));
                }
                final String qname = "\"" + name + "\"";
                env.put(name, qname);
                o.append("{\"name\":").append(qname);
                o.append(",\"type\":\"").append(st.getName()).append("\"");
                if (st == Schema.Type.ENUM) {
                    o.append(",\"symbols\":[");
                    for (final String enumSymbol : s.getEnumSymbols()) {
                        if (!firstTime) {
                            o.append(',');
                        }
                        else {
                            firstTime = false;
                        }
                        o.append('\"').append(enumSymbol).append('\"');
                    }
                    o.append("]");
                }
                else if (st == Schema.Type.FIXED) {
                    o.append(",\"size\":").append(Integer.toString(s.getFixedSize()));
                }
                else {
                    o.append(",\"fields\":[");
                    for (final Schema.Field f : s.getFields()) {
                        if (!firstTime) {
                            o.append(',');
                        }
                        else {
                            firstTime = false;
                        }
                        o.append("{\"name\":\"").append(f.name()).append("\"");
                        build(env, f.schema(), o.append(",\"type\":")).append("}");
                    }
                    o.append("]");
                }
                return o.append("}");
            }
        }
    }
    
    private static class FP64
    {
        private static final long[] FP_TABLE;
        
        static {
            FP_TABLE = new long[256];
            for (int i = 0; i < 256; ++i) {
                long fp = i;
                for (int j = 0; j < 8; ++j) {
                    final long mask = -(fp & 0x1L);
                    fp = (fp >>> 1 ^ (0xC15D213AA4D7A795L & mask));
                }
                FP64.FP_TABLE[i] = fp;
            }
        }
    }
}
