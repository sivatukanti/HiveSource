// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.BufferCache;

public class Ajp13RequestHeaders extends BufferCache
{
    public static final int MAGIC = 4660;
    public static final String ACCEPT = "accept";
    public static final String ACCEPT_CHARSET = "accept-charset";
    public static final String ACCEPT_ENCODING = "accept-encoding";
    public static final String ACCEPT_LANGUAGE = "accept-language";
    public static final String AUTHORIZATION = "authorization";
    public static final String CONNECTION = "connection";
    public static final String CONTENT_TYPE = "content-type";
    public static final String CONTENT_LENGTH = "content-length";
    public static final String COOKIE = "cookie";
    public static final String COOKIE2 = "cookie2";
    public static final String HOST = "host";
    public static final String PRAGMA = "pragma";
    public static final String REFERER = "referer";
    public static final String USER_AGENT = "user-agent";
    public static final int ACCEPT_ORDINAL = 1;
    public static final int ACCEPT_CHARSET_ORDINAL = 2;
    public static final int ACCEPT_ENCODING_ORDINAL = 3;
    public static final int ACCEPT_LANGUAGE_ORDINAL = 4;
    public static final int AUTHORIZATION_ORDINAL = 5;
    public static final int CONNECTION_ORDINAL = 6;
    public static final int CONTENT_TYPE_ORDINAL = 7;
    public static final int CONTENT_LENGTH_ORDINAL = 8;
    public static final int COOKIE_ORDINAL = 9;
    public static final int COOKIE2_ORDINAL = 10;
    public static final int HOST_ORDINAL = 11;
    public static final int PRAGMA_ORDINAL = 12;
    public static final int REFERER_ORDINAL = 13;
    public static final int USER_AGENT_ORDINAL = 14;
    public static final BufferCache CACHE;
    public static final Buffer ACCEPT_BUFFER;
    public static final Buffer ACCEPT_CHARSET_BUFFER;
    public static final Buffer ACCEPT_ENCODING_BUFFER;
    public static final Buffer ACCEPT_LANGUAGE_BUFFER;
    public static final Buffer AUTHORIZATION_BUFFER;
    public static final Buffer CONNECTION_BUFFER;
    public static final Buffer CONTENT_TYPE_BUFFER;
    public static final Buffer CONTENT_LENGTH_BUFFER;
    public static final Buffer COOKIE_BUFFER;
    public static final Buffer COOKIE2_BUFFER;
    public static final Buffer HOST_BUFFER;
    public static final Buffer PRAGMA_BUFFER;
    public static final Buffer REFERER_BUFFER;
    public static final Buffer USER_AGENT_BUFFER;
    public static final byte CONTEXT_ATTR = 1;
    public static final byte SERVLET_PATH_ATTR = 2;
    public static final byte REMOTE_USER_ATTR = 3;
    public static final byte AUTH_TYPE_ATTR = 4;
    public static final byte QUERY_STRING_ATTR = 5;
    public static final byte JVM_ROUTE_ATTR = 6;
    public static final byte SSL_CERT_ATTR = 7;
    public static final byte SSL_CIPHER_ATTR = 8;
    public static final byte SSL_SESSION_ATTR = 9;
    public static final byte REQUEST_ATTR = 10;
    public static final byte SSL_KEYSIZE_ATTR = 11;
    public static final byte SECRET_ATTR = 12;
    public static final byte STORED_METHOD_ATTR = 13;
    
    static {
        CACHE = new BufferCache();
        ACCEPT_BUFFER = Ajp13RequestHeaders.CACHE.add("accept", 1);
        ACCEPT_CHARSET_BUFFER = Ajp13RequestHeaders.CACHE.add("accept-charset", 2);
        ACCEPT_ENCODING_BUFFER = Ajp13RequestHeaders.CACHE.add("accept-encoding", 3);
        ACCEPT_LANGUAGE_BUFFER = Ajp13RequestHeaders.CACHE.add("accept-language", 4);
        AUTHORIZATION_BUFFER = Ajp13RequestHeaders.CACHE.add("authorization", 5);
        CONNECTION_BUFFER = Ajp13RequestHeaders.CACHE.add("connection", 6);
        CONTENT_TYPE_BUFFER = Ajp13RequestHeaders.CACHE.add("content-type", 7);
        CONTENT_LENGTH_BUFFER = Ajp13RequestHeaders.CACHE.add("content-length", 8);
        COOKIE_BUFFER = Ajp13RequestHeaders.CACHE.add("cookie", 9);
        COOKIE2_BUFFER = Ajp13RequestHeaders.CACHE.add("cookie2", 10);
        HOST_BUFFER = Ajp13RequestHeaders.CACHE.add("host", 11);
        PRAGMA_BUFFER = Ajp13RequestHeaders.CACHE.add("pragma", 12);
        REFERER_BUFFER = Ajp13RequestHeaders.CACHE.add("referer", 13);
        USER_AGENT_BUFFER = Ajp13RequestHeaders.CACHE.add("user-agent", 14);
    }
}
