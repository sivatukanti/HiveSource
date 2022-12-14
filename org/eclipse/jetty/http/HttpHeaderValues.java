// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.BufferCache;

public class HttpHeaderValues extends BufferCache
{
    public static final String CLOSE = "close";
    public static final String CHUNKED = "chunked";
    public static final String GZIP = "gzip";
    public static final String IDENTITY = "identity";
    public static final String KEEP_ALIVE = "keep-alive";
    public static final String CONTINUE = "100-continue";
    public static final String PROCESSING = "102-processing";
    public static final String TE = "TE";
    public static final String BYTES = "bytes";
    public static final String NO_CACHE = "no-cache";
    public static final String UPGRADE = "Upgrade";
    public static final int CLOSE_ORDINAL = 1;
    public static final int CHUNKED_ORDINAL = 2;
    public static final int GZIP_ORDINAL = 3;
    public static final int IDENTITY_ORDINAL = 4;
    public static final int KEEP_ALIVE_ORDINAL = 5;
    public static final int CONTINUE_ORDINAL = 6;
    public static final int PROCESSING_ORDINAL = 7;
    public static final int TE_ORDINAL = 8;
    public static final int BYTES_ORDINAL = 9;
    public static final int NO_CACHE_ORDINAL = 10;
    public static final int UPGRADE_ORDINAL = 11;
    public static final HttpHeaderValues CACHE;
    public static final Buffer CLOSE_BUFFER;
    public static final Buffer CHUNKED_BUFFER;
    public static final Buffer GZIP_BUFFER;
    public static final Buffer IDENTITY_BUFFER;
    public static final Buffer KEEP_ALIVE_BUFFER;
    public static final Buffer CONTINUE_BUFFER;
    public static final Buffer PROCESSING_BUFFER;
    public static final Buffer TE_BUFFER;
    public static final Buffer BYTES_BUFFER;
    public static final Buffer NO_CACHE_BUFFER;
    public static final Buffer UPGRADE_BUFFER;
    
    public static boolean hasKnownValues(final int httpHeaderOrdinal) {
        switch (httpHeaderOrdinal) {
            case 1:
            case 5:
            case 10: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    static {
        CACHE = new HttpHeaderValues();
        CLOSE_BUFFER = HttpHeaderValues.CACHE.add("close", 1);
        CHUNKED_BUFFER = HttpHeaderValues.CACHE.add("chunked", 2);
        GZIP_BUFFER = HttpHeaderValues.CACHE.add("gzip", 3);
        IDENTITY_BUFFER = HttpHeaderValues.CACHE.add("identity", 4);
        KEEP_ALIVE_BUFFER = HttpHeaderValues.CACHE.add("keep-alive", 5);
        CONTINUE_BUFFER = HttpHeaderValues.CACHE.add("100-continue", 6);
        PROCESSING_BUFFER = HttpHeaderValues.CACHE.add("102-processing", 7);
        TE_BUFFER = HttpHeaderValues.CACHE.add("TE", 8);
        BYTES_BUFFER = HttpHeaderValues.CACHE.add("bytes", 9);
        NO_CACHE_BUFFER = HttpHeaderValues.CACHE.add("no-cache", 10);
        UPGRADE_BUFFER = HttpHeaderValues.CACHE.add("Upgrade", 11);
    }
}
