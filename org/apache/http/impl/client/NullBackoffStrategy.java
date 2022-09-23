// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.ConnectionBackoffStrategy;

public class NullBackoffStrategy implements ConnectionBackoffStrategy
{
    @Override
    public boolean shouldBackoff(final Throwable t) {
        return false;
    }
    
    @Override
    public boolean shouldBackoff(final HttpResponse resp) {
        return false;
    }
}
