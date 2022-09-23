// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning;

import com.sun.jersey.core.util.Closing;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class FilesScanner implements Scanner
{
    private final File[] files;
    
    public FilesScanner(final File[] files) {
        this.files = files;
    }
    
    @Override
    public void scan(final ScannerListener cfl) {
        for (final File f : this.files) {
            this.scan(f, cfl);
        }
    }
    
    private void scan(final File f, final ScannerListener cfl) {
        if (f.isDirectory()) {
            this.scanDir(f, cfl);
        }
        else {
            if (!f.getName().endsWith(".jar")) {
                if (!f.getName().endsWith(".zip")) {
                    return;
                }
            }
            try {
                JarFileScanner.scan(f, "", cfl);
            }
            catch (IOException ex) {
                throw new ScannerException("IO error when scanning jar file " + f, ex);
            }
        }
    }
    
    private void scanDir(final File root, final ScannerListener cfl) {
        for (final File child : root.listFiles()) {
            Label_0185: {
                if (child.isDirectory()) {
                    this.scanDir(child, cfl);
                }
                else {
                    if (child.getName().endsWith(".jar")) {
                        try {
                            JarFileScanner.scan(child, "", cfl);
                            break Label_0185;
                        }
                        catch (IOException ex) {
                            throw new ScannerException("IO error when scanning jar file " + child, ex);
                        }
                    }
                    if (cfl.onAccept(child.getName())) {
                        try {
                            new Closing(new BufferedInputStream(new FileInputStream(child))).f(new Closing.Closure() {
                                @Override
                                public void f(final InputStream in) throws IOException {
                                    cfl.onProcess(child.getName(), in);
                                }
                            });
                        }
                        catch (IOException ex) {
                            throw new ScannerException("IO error when scanning file " + child, ex);
                        }
                    }
                }
            }
        }
    }
}
