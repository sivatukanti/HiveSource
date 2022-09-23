// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.Buffer;

public class HttpSchemes
{
    public static final String HTTP = "http";
    public static final String HTTPS = "https";
    public static final Buffer HTTP_BUFFER;
    public static final Buffer HTTPS_BUFFER;
    
    static {
        HTTP_BUFFER = new ByteArrayBuffer("http");
        HTTPS_BUFFER = new ByteArrayBuffer("https");
    }
}
