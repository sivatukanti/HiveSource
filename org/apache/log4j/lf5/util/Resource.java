// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.lf5.util;

import java.net.URL;
import java.io.InputStreamReader;
import java.io.InputStream;

public class Resource
{
    protected String _name;
    
    public Resource() {
    }
    
    public Resource(final String name) {
        this._name = name;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public String getName() {
        return this._name;
    }
    
    public InputStream getInputStream() {
        final InputStream in = ResourceUtils.getResourceAsStream(this, this);
        return in;
    }
    
    public InputStreamReader getInputStreamReader() {
        final InputStream in = ResourceUtils.getResourceAsStream(this, this);
        if (in == null) {
            return null;
        }
        final InputStreamReader reader = new InputStreamReader(in);
        return reader;
    }
    
    public URL getURL() {
        return ResourceUtils.getResourceAsURL(this, this);
    }
}
