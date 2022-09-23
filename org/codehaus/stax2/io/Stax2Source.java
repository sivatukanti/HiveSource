// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import javax.xml.transform.Source;

public abstract class Stax2Source implements Source
{
    protected String mSystemId;
    protected String mPublicId;
    protected String mEncoding;
    
    protected Stax2Source() {
    }
    
    public String getSystemId() {
        return this.mSystemId;
    }
    
    public void setSystemId(final String mSystemId) {
        this.mSystemId = mSystemId;
    }
    
    public String getPublicId() {
        return this.mPublicId;
    }
    
    public void setPublicId(final String mPublicId) {
        this.mPublicId = mPublicId;
    }
    
    public String getEncoding() {
        return this.mEncoding;
    }
    
    public void setEncoding(final String mEncoding) {
        this.mEncoding = mEncoding;
    }
    
    public abstract URL getReference();
    
    public abstract Reader constructReader() throws IOException;
    
    public abstract InputStream constructInputStream() throws IOException;
}
