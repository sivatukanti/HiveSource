// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;

public class Stax2FileSource extends Stax2ReferentialSource
{
    final File mFile;
    
    public Stax2FileSource(final File mFile) {
        this.mFile = mFile;
    }
    
    @Override
    public URL getReference() {
        try {
            return this.mFile.toURL();
        }
        catch (MalformedURLException obj) {
            throw new IllegalArgumentException("(was " + obj.getClass() + ") Could not convert File '" + this.mFile.getPath() + "' to URL: " + obj);
        }
    }
    
    @Override
    public Reader constructReader() throws IOException {
        final String encoding = this.getEncoding();
        if (encoding != null && encoding.length() > 0) {
            return new InputStreamReader(this.constructInputStream(), encoding);
        }
        return new FileReader(this.mFile);
    }
    
    @Override
    public InputStream constructInputStream() throws IOException {
        return new FileInputStream(this.mFile);
    }
    
    public File getFile() {
        return this.mFile;
    }
}
