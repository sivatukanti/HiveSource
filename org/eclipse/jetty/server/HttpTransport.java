// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.Callback;
import java.nio.ByteBuffer;
import org.eclipse.jetty.http.MetaData;

public interface HttpTransport
{
    void send(final MetaData.Response p0, final boolean p1, final ByteBuffer p2, final boolean p3, final Callback p4);
    
    boolean isPushSupported();
    
    void push(final MetaData.Request p0);
    
    void onCompleted();
    
    void abort(final Throwable p0);
    
    boolean isOptimizedForDirectBuffers();
}
