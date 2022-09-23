// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.util;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class DefaultResourceRetriever extends AbstractRestrictedResourceRetriever implements RestrictedResourceRetriever
{
    public DefaultResourceRetriever() {
        this(0, 0);
    }
    
    public DefaultResourceRetriever(final int connectTimeout, final int readTimeout) {
        this(connectTimeout, readTimeout, 0);
    }
    
    public DefaultResourceRetriever(final int connectTimeout, final int readTimeout, final int sizeLimit) {
        super(connectTimeout, readTimeout, sizeLimit);
    }
    
    @Override
    public Resource retrieveResource(final URL url) throws IOException {
        HttpURLConnection con;
        try {
            con = (HttpURLConnection)url.openConnection();
        }
        catch (ClassCastException e) {
            throw new IOException("Couldn't open HTTP(S) connection: " + e.getMessage(), e);
        }
        con.setConnectTimeout(this.getConnectTimeout());
        con.setReadTimeout(this.getReadTimeout());
        InputStream inputStream = con.getInputStream();
        if (this.getSizeLimit() > 0) {
            inputStream = new BoundedInputStream(inputStream, this.getSizeLimit());
        }
        String content;
        try {
            content = IOUtils.readInputStreamToString(inputStream, Charset.forName("UTF-8"));
        }
        finally {
            inputStream.close();
        }
        inputStream.close();
        final int statusCode = con.getResponseCode();
        final String statusMessage = con.getResponseMessage();
        if (statusCode > 299 || statusCode < 200) {
            throw new IOException("HTTP " + statusCode + ": " + statusMessage);
        }
        return new Resource(content, con.getContentType());
    }
}
