// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler.gzip;

import java.util.zip.Deflater;
import org.eclipse.jetty.server.Request;

public interface GzipFactory
{
    Deflater getDeflater(final Request p0, final long p1);
    
    boolean isMimeTypeGzipable(final String p0);
    
    void recycle(final Deflater p0);
}
