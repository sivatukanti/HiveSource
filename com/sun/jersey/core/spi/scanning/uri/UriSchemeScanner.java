// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.net.URI;
import java.util.Set;

public interface UriSchemeScanner
{
    Set<String> getSchemes();
    
    void scan(final URI p0, final ScannerListener p1) throws ScannerException;
}
