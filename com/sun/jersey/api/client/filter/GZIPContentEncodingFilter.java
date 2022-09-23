// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client.filter;

import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import com.sun.jersey.api.client.AbstractClientRequestAdapter;
import java.io.IOException;
import com.sun.jersey.api.client.ClientHandlerException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import com.sun.jersey.api.client.ClientRequestAdapter;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientRequest;

public class GZIPContentEncodingFilter extends ClientFilter
{
    private final boolean compressRequestEntity;
    
    public GZIPContentEncodingFilter() {
        this(true);
    }
    
    public GZIPContentEncodingFilter(final boolean compressRequestEntity) {
        this.compressRequestEntity = compressRequestEntity;
    }
    
    @Override
    public ClientResponse handle(final ClientRequest request) throws ClientHandlerException {
        if (!request.getHeaders().containsKey("Accept-Encoding")) {
            request.getHeaders().add("Accept-Encoding", "gzip");
        }
        if (request.getEntity() != null) {
            final Object o = request.getHeaders().getFirst("Content-Encoding");
            if (o != null && o.equals("gzip")) {
                request.setAdapter(new Adapter(request.getAdapter()));
            }
            else if (this.compressRequestEntity) {
                request.getHeaders().add("Content-Encoding", "gzip");
                request.setAdapter(new Adapter(request.getAdapter()));
            }
        }
        final ClientResponse response = this.getNext().handle(request);
        if (response.hasEntity() && response.getHeaders().containsKey("Content-Encoding")) {
            final String encodings = response.getHeaders().getFirst("Content-Encoding");
            if (encodings.equals("gzip")) {
                response.getHeaders().remove("Content-Encoding");
                try {
                    response.setEntityInputStream(new GZIPInputStream(response.getEntityInputStream()));
                }
                catch (IOException ex) {
                    throw new ClientHandlerException(ex);
                }
            }
        }
        return response;
    }
    
    private static final class Adapter extends AbstractClientRequestAdapter
    {
        Adapter(final ClientRequestAdapter cra) {
            super(cra);
        }
        
        @Override
        public OutputStream adapt(final ClientRequest request, final OutputStream out) throws IOException {
            return new GZIPOutputStream(this.getAdapter().adapt(request, out));
        }
    }
}
