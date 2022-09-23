// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import java.io.IOException;
import com.sun.jersey.core.util.Closing;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class FileSchemeScanner implements UriSchemeScanner
{
    @Override
    public Set<String> getSchemes() {
        return Collections.singleton("file");
    }
    
    @Override
    public void scan(final URI u, final ScannerListener cfl) {
        final File f = new File(u.getPath());
        if (f.isDirectory()) {
            this.scanDirectory(f, cfl);
        }
    }
    
    private void scanDirectory(final File root, final ScannerListener cfl) {
        for (final File child : root.listFiles()) {
            if (child.isDirectory()) {
                this.scanDirectory(child, cfl);
            }
            else if (cfl.onAccept(child.getName())) {
                try {
                    new Closing(new BufferedInputStream(new FileInputStream(child))).f(new Closing.Closure() {
                        @Override
                        public void f(final InputStream in) throws IOException {
                            cfl.onProcess(child.getName(), in);
                        }
                    });
                }
                catch (IOException ex) {
                    throw new ScannerException("IO error when scanning jar file " + child, ex);
                }
            }
        }
    }
}
