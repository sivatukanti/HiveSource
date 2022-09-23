// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.ensemble.exhibitor;

import java.io.InputStream;
import java.io.Closeable;
import org.apache.curator.utils.CloseableUtils;
import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URI;

public class DefaultExhibitorRestClient implements ExhibitorRestClient
{
    private final boolean useSsl;
    
    public DefaultExhibitorRestClient() {
        this(false);
    }
    
    public DefaultExhibitorRestClient(final boolean useSsl) {
        this.useSsl = useSsl;
    }
    
    @Override
    public String getRaw(final String hostname, final int port, final String uriPath, final String mimeType) throws Exception {
        final URI uri = new URI(this.useSsl ? "https" : "http", null, hostname, port, uriPath, null, null);
        final HttpURLConnection connection = (HttpURLConnection)uri.toURL().openConnection();
        connection.addRequestProperty("Accept", mimeType);
        final StringBuilder str = new StringBuilder();
        final InputStream in = new BufferedInputStream(connection.getInputStream());
        try {
            while (true) {
                final int b = in.read();
                if (b < 0) {
                    break;
                }
                str.append((char)(b & 0xFF));
            }
        }
        finally {
            CloseableUtils.closeQuietly(in);
        }
        return str.toString();
    }
}
