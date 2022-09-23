// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import java.io.IOException;
import java.io.InputStream;
import com.sun.jersey.core.util.Closing;
import java.io.BufferedInputStream;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class BundleSchemeScanner implements UriSchemeScanner
{
    @Override
    public Set<String> getSchemes() {
        return new HashSet<String>(Arrays.asList("bundle"));
    }
    
    @Override
    public void scan(final URI u, final ScannerListener sl) throws ScannerException {
        if (sl.onAccept(u.getPath())) {
            try {
                new Closing(new BufferedInputStream(u.toURL().openStream())).f(new Closing.Closure() {
                    @Override
                    public void f(final InputStream in) throws IOException {
                        sl.onProcess(u.getPath(), in);
                    }
                });
            }
            catch (IOException ex) {
                throw new ScannerException("IO error when scanning bundle class " + u, ex);
            }
        }
    }
}
