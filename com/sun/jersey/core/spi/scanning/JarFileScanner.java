// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.core.spi.scanning;

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.IOException;
import java.io.InputStream;
import com.sun.jersey.core.util.Closing;
import java.io.FileInputStream;
import java.io.File;

public final class JarFileScanner
{
    public static void scan(final File f, final String parent, final ScannerListener sl) throws IOException {
        new Closing(new FileInputStream(f)).f(new Closing.Closure() {
            @Override
            public void f(final InputStream in) throws IOException {
                JarFileScanner.scan(in, parent, sl);
            }
        });
    }
    
    public static void scan(final InputStream in, final String parent, final ScannerListener sl) throws IOException {
        JarInputStream jarIn = null;
        try {
            jarIn = new JarInputStream(in);
            for (JarEntry e = jarIn.getNextJarEntry(); e != null; e = jarIn.getNextJarEntry()) {
                if (!e.isDirectory() && e.getName().startsWith(parent) && sl.onAccept(e.getName())) {
                    sl.onProcess(e.getName(), jarIn);
                }
                jarIn.closeEntry();
            }
        }
        finally {
            if (jarIn != null) {
                jarIn.close();
            }
        }
    }
}
