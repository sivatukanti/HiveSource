// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning.uri;

import java.net.MalformedURLException;
import java.io.FileInputStream;
import com.sun.jersey.api.uri.UriComponent;
import java.net.URL;
import com.sun.jersey.core.spi.scanning.ScannerException;
import java.io.IOException;
import com.sun.jersey.core.spi.scanning.JarFileScanner;
import java.io.InputStream;
import com.sun.jersey.core.util.Closing;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class JarZipSchemeScanner implements UriSchemeScanner
{
    @Override
    public Set<String> getSchemes() {
        return new HashSet<String>(Arrays.asList("jar", "zip"));
    }
    
    @Override
    public void scan(final URI u, final ScannerListener cfl) {
        final String ssp = u.getRawSchemeSpecificPart();
        final String jarUrlString = ssp.substring(0, ssp.lastIndexOf(33));
        final String parent = ssp.substring(ssp.lastIndexOf(33) + 2);
        try {
            this.closing(jarUrlString).f(new Closing.Closure() {
                @Override
                public void f(final InputStream in) throws IOException {
                    JarFileScanner.scan(in, parent, cfl);
                }
            });
        }
        catch (IOException ex) {
            throw new ScannerException("IO error when scanning jar " + u, ex);
        }
    }
    
    protected Closing closing(final String jarUrlString) throws IOException {
        try {
            return new Closing(new URL(jarUrlString).openStream());
        }
        catch (MalformedURLException ex) {
            return new Closing(new FileInputStream(UriComponent.decode(jarUrlString, UriComponent.Type.PATH)));
        }
    }
}
