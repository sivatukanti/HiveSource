// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.api.client;

import java.io.IOException;
import java.io.OutputStream;

public interface ClientRequestAdapter
{
    OutputStream adapt(final ClientRequest p0, final OutputStream p1) throws IOException;
}
