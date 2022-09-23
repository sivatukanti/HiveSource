// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

public interface HttpFieldPreEncoder
{
    HttpVersion getHttpVersion();
    
    byte[] getEncodedField(final HttpHeader p0, final String p1, final String p2);
}
