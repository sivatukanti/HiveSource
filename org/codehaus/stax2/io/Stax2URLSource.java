// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

public class Stax2URLSource extends Stax2ReferentialSource
{
    final URL mURL;
    
    public Stax2URLSource(final URL murl) {
        this.mURL = murl;
    }
    
    @Override
    public URL getReference() {
        return this.mURL;
    }
    
    @Override
    public Reader constructReader() throws IOException {
        final String encoding = this.getEncoding();
        if (encoding != null && encoding.length() > 0) {
            return new InputStreamReader(this.constructInputStream(), encoding);
        }
        return new InputStreamReader(this.constructInputStream());
    }
    
    @Override
    public InputStream constructInputStream() throws IOException {
        if ("file".equals(this.mURL.getProtocol())) {
            return new FileInputStream(this.mURL.getPath());
        }
        return this.mURL.openStream();
    }
}
