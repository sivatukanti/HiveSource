// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning;

import java.io.IOException;
import java.io.InputStream;

public interface ScannerListener
{
    boolean onAccept(final String p0);
    
    void onProcess(final String p0, final InputStream p1) throws IOException;
}
