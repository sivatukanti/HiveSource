// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class ANTLRFileStream extends ANTLRStringStream
{
    protected String fileName;
    
    public ANTLRFileStream(final String fileName) throws IOException {
        this(fileName, null);
    }
    
    public ANTLRFileStream(final String fileName, final String encoding) throws IOException {
        this.load(this.fileName = fileName, encoding);
    }
    
    public void load(final String fileName, final String encoding) throws IOException {
        if (fileName == null) {
            return;
        }
        final File f = new File(fileName);
        final int size = (int)f.length();
        final FileInputStream fis = new FileInputStream(fileName);
        InputStreamReader isr;
        if (encoding != null) {
            isr = new InputStreamReader(fis, encoding);
        }
        else {
            isr = new InputStreamReader(fis);
        }
        try {
            this.data = new char[size];
            super.n = isr.read(this.data);
        }
        finally {
            isr.close();
        }
    }
    
    public String getSourceName() {
        return this.fileName;
    }
}
