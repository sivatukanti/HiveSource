// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import java.lang.ref.SoftReference;

public class BufferRecyclers
{
    protected static final ThreadLocal<SoftReference<BufferRecycler>> _recyclerRef;
    protected static final ThreadLocal<SoftReference<JsonStringEncoder>> _encoderRef;
    
    public static BufferRecycler getBufferRecycler() {
        final SoftReference<BufferRecycler> ref = BufferRecyclers._recyclerRef.get();
        BufferRecycler br = (ref == null) ? null : ref.get();
        if (br == null) {
            br = new BufferRecycler();
            BufferRecyclers._recyclerRef.set(new SoftReference<BufferRecycler>(br));
        }
        return br;
    }
    
    public static JsonStringEncoder getJsonStringEncoder() {
        final SoftReference<JsonStringEncoder> ref = BufferRecyclers._encoderRef.get();
        JsonStringEncoder enc = (ref == null) ? null : ref.get();
        if (enc == null) {
            enc = new JsonStringEncoder();
            BufferRecyclers._encoderRef.set(new SoftReference<JsonStringEncoder>(enc));
        }
        return enc;
    }
    
    public static byte[] encodeAsUTF8(final String text) {
        return getJsonStringEncoder().encodeAsUTF8(text);
    }
    
    public static char[] quoteAsJsonText(final String rawText) {
        return getJsonStringEncoder().quoteAsString(rawText);
    }
    
    public static void quoteAsJsonText(final CharSequence input, final StringBuilder output) {
        getJsonStringEncoder().quoteAsString(input, output);
    }
    
    public static byte[] quoteAsJsonUTF8(final String rawText) {
        return getJsonStringEncoder().quoteAsUTF8(rawText);
    }
    
    static {
        _recyclerRef = new ThreadLocal<SoftReference<BufferRecycler>>();
        _encoderRef = new ThreadLocal<SoftReference<JsonStringEncoder>>();
    }
}
