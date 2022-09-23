// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import org.eclipse.jetty.io.Buffer;
import java.io.IOException;

public interface HttpEventListener
{
    void onRequestCommitted() throws IOException;
    
    void onRequestComplete() throws IOException;
    
    void onResponseStatus(final Buffer p0, final int p1, final Buffer p2) throws IOException;
    
    void onResponseHeader(final Buffer p0, final Buffer p1) throws IOException;
    
    void onResponseHeaderComplete() throws IOException;
    
    void onResponseContent(final Buffer p0) throws IOException;
    
    void onResponseComplete() throws IOException;
    
    void onConnectionFailed(final Throwable p0);
    
    void onException(final Throwable p0);
    
    void onExpire();
    
    void onRetry();
}
