// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.io;

import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;

public abstract class Stax2ReferentialSource extends Stax2Source
{
    protected Stax2ReferentialSource() {
    }
    
    @Override
    public abstract URL getReference();
    
    @Override
    public abstract Reader constructReader() throws IOException;
    
    @Override
    public abstract InputStream constructInputStream() throws IOException;
    
    @Override
    public String getSystemId() {
        String s = super.getSystemId();
        if (s == null) {
            s = this.getReference().toExternalForm();
        }
        return s;
    }
}
