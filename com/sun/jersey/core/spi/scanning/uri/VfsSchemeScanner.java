// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning.uri;

import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.JarFileScanner;
import java.io.IOException;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.ws.rs.core.UriBuilder;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class VfsSchemeScanner implements UriSchemeScanner
{
    @Override
    public Set<String> getSchemes() {
        return new HashSet<String>(Arrays.asList("vfsfile", "vfszip", "vfs"));
    }
    
    @Override
    public void scan(final URI u, final ScannerListener sl) {
        if (!u.getScheme().equalsIgnoreCase("vfszip")) {
            new FileSchemeScanner().scan(UriBuilder.fromUri(u).scheme("file").build(new Object[0]), sl);
        }
        else {
            final String su = u.toString();
            final int webInfIndex = su.indexOf("/WEB-INF/classes");
            if (webInfIndex != -1) {
                final String war = su.substring(0, webInfIndex);
                final String path = su.substring(webInfIndex + 1);
                final int warParentIndex = war.lastIndexOf(47);
                final String warParent = su.substring(0, warParentIndex);
                if (warParent.endsWith(".ear")) {
                    final String warName = su.substring(warParentIndex + 1, war.length());
                    try {
                        JarFileScanner.scan(new URL(warParent.replace("vfszip", "file")).openStream(), "", new ScannerListener() {
                            @Override
                            public boolean onAccept(final String name) {
                                return name.equals(warName);
                            }
                            
                            @Override
                            public void onProcess(final String name, InputStream in) throws IOException {
                                in = new FilterInputStream(in) {
                                    @Override
                                    public void close() throws IOException {
                                    }
                                };
                                try {
                                    JarFileScanner.scan(in, path, sl);
                                }
                                catch (IOException ex) {
                                    throw new ScannerException("IO error when scanning war " + u, ex);
                                }
                            }
                        });
                    }
                    catch (IOException ex) {
                        throw new ScannerException("IO error when scanning war " + u, ex);
                    }
                }
                else {
                    try {
                        JarFileScanner.scan(new URL(war.replace("vfszip", "file")).openStream(), path, sl);
                    }
                    catch (IOException ex2) {
                        throw new ScannerException("IO error when scanning war " + u, ex2);
                    }
                }
            }
            else {
                try {
                    JarFileScanner.scan(new URL(su).openStream(), "", sl);
                }
                catch (IOException ex3) {
                    throw new ScannerException("IO error when scanning jar " + u, ex3);
                }
            }
        }
    }
}
