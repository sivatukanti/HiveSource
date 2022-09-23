// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.client;

import org.apache.http.client.HttpResponseException;
import org.apache.http.HttpResponse;
import java.io.IOException;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpEntity;
import org.apache.http.annotation.Immutable;

@Immutable
public class BasicResponseHandler extends AbstractResponseHandler<String>
{
    @Override
    public String handleEntity(final HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity);
    }
    
    @Override
    public String handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        return super.handleResponse(response);
    }
}
