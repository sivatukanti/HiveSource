// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.OutputStream;
import java.io.IOException;
import java.io.Writer;
import javax.xml.transform.Result;

public abstract class Stax2Result implements Result
{
    protected String mSystemId;
    protected String mPublicId;
    protected String mEncoding;
    
    protected Stax2Result() {
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
    
    public abstract Writer constructWriter() throws IOException;
    
    public abstract OutputStream constructOutputStream() throws IOException;
}
