// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.container;

import java.io.IOException;
import java.io.OutputStream;

public interface ContainerResponseWriter
{
    OutputStream writeStatusAndHeaders(final long p0, final ContainerResponse p1) throws IOException;
    
    void finish() throws IOException;
}
