// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client.security;

import java.io.IOException;
import org.eclipse.jetty.client.HttpExchange;

public interface Authentication
{
    void setCredentials(final HttpExchange p0) throws IOException;
}
