// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.client.urlconnection;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public interface HttpURLConnectionFactory
{
    HttpURLConnection getHttpURLConnection(final URL p0) throws IOException;
}
