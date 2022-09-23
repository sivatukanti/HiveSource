// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.io.bio;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class StringEndPoint extends StreamEndPoint
{
    String _encoding;
    ByteArrayInputStream _bin;
    ByteArrayOutputStream _bout;
    
    public StringEndPoint() {
        super(null, null);
        this._encoding = "UTF-8";
        this._bin = new ByteArrayInputStream(new byte[0]);
        this._bout = new ByteArrayOutputStream();
        this._in = this._bin;
        this._out = this._bout;
    }
    
    public StringEndPoint(final String encoding) {
        this();
        if (encoding != null) {
            this._encoding = encoding;
        }
    }
    
    public void setInput(final String s) {
        try {
            final byte[] bytes = s.getBytes(this._encoding);
            this._bin = new ByteArrayInputStream(bytes);
            this._in = this._bin;
            this._bout = new ByteArrayOutputStream();
            this._out = this._bout;
            this._ishut = false;
            this._oshut = false;
        }
        catch (Exception e) {
            throw new IllegalStateException(e.toString());
        }
    }
    
    public String getOutput() {
        try {
            final String s = new String(this._bout.toByteArray(), this._encoding);
            this._bout.reset();
            return s;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException(this._encoding + ": " + e.toString());
        }
    }
    
    public boolean hasMore() {
        return this._bin.available() > 0;
    }
}
