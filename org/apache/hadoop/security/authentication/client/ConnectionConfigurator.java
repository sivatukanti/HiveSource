// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security.authentication.client;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface ConnectionConfigurator
{
    HttpURLConnection configure(final HttpURLConnection p0) throws IOException;
}
