// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.BufferCache;

public class HttpVersions
{
    public static final String HTTP_0_9 = "";
    public static final String HTTP_1_0 = "HTTP/1.0";
    public static final String HTTP_1_1 = "HTTP/1.1";
    public static final int HTTP_0_9_ORDINAL = 9;
    public static final int HTTP_1_0_ORDINAL = 10;
    public static final int HTTP_1_1_ORDINAL = 11;
    public static final BufferCache CACHE;
    public static final Buffer HTTP_0_9_BUFFER;
    public static final Buffer HTTP_1_0_BUFFER;
    public static final Buffer HTTP_1_1_BUFFER;
    
    static {
        CACHE = new BufferCache();
        HTTP_0_9_BUFFER = HttpVersions.CACHE.add("", 9);
        HTTP_1_0_BUFFER = HttpVersions.CACHE.add("HTTP/1.0", 10);
        HTTP_1_1_BUFFER = HttpVersions.CACHE.add("HTTP/1.1", 11);
    }
}
