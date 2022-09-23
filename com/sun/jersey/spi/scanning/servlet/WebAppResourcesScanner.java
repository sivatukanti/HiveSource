// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.scanning.servlet;

import java.util.Iterator;
import java.util.Set;
import com.sun.jersey.core.spi.scanning.ScannerException;
import java.io.IOException;
import com.sun.jersey.core.spi.scanning.JarFileScanner;
import java.io.InputStream;
import com.sun.jersey.core.util.Closing;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import javax.servlet.ServletContext;
import com.sun.jersey.core.spi.scanning.Scanner;

public class WebAppResourcesScanner implements Scanner
{
    private final String[] paths;
    private final ServletContext sc;
    
    public WebAppResourcesScanner(final String[] paths, final ServletContext sc) {
        this.paths = paths;
        this.sc = sc;
    }
    
    @Override
    public void scan(final ScannerListener cfl) {
        for (final String path : this.paths) {
            this.scan(path, cfl);
        }
    }
    
    private void scan(final String root, final ScannerListener cfl) {
        final Set<String> resourcePaths = this.sc.getResourcePaths(root);
        if (resourcePaths == null) {
            return;
        }
        for (final String resourcePath : resourcePaths) {
            if (resourcePath.endsWith("/")) {
                this.scan(resourcePath, cfl);
            }
            else {
                if (resourcePath.endsWith(".jar")) {
                    try {
                        new Closing(this.sc.getResourceAsStream(resourcePath)).f(new Closing.Closure() {
                            @Override
                            public void f(final InputStream in) throws IOException {
                                JarFileScanner.scan(in, "", cfl);
                            }
                        });
                        continue;
                    }
                    catch (IOException ex) {
                        throw new ScannerException("IO error scanning jar " + resourcePath, ex);
                    }
                }
                if (!cfl.onAccept(resourcePath)) {
                    continue;
                }
                try {
                    new Closing(this.sc.getResourceAsStream(resourcePath)).f(new Closing.Closure() {
                        @Override
                        public void f(final InputStream in) throws IOException {
                            cfl.onProcess(resourcePath, in);
                        }
                    });
                }
                catch (IOException ex) {
                    throw new ScannerException("IO error scanning resource " + resourcePath, ex);
                }
            }
        }
    }
}
