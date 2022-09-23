// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.v2.util;

import java.io.IOException;
import java.io.InputStreamReader;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.activation.DataHandler;
import java.io.InputStream;
import java.io.Reader;
import javax.activation.DataSource;
import javax.xml.transform.stream.StreamSource;

public final class DataSourceSource extends StreamSource
{
    private final DataSource source;
    private final String charset;
    private Reader r;
    private InputStream is;
    
    public DataSourceSource(final DataHandler dh) throws MimeTypeParseException {
        this(dh.getDataSource());
    }
    
    public DataSourceSource(final DataSource source) throws MimeTypeParseException {
        this.source = source;
        final String ct = source.getContentType();
        if (ct == null) {
            this.charset = null;
        }
        else {
            final MimeType mimeType = new MimeType(ct);
            this.charset = mimeType.getParameter("charset");
        }
    }
    
    @Override
    public void setReader(final Reader reader) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void setInputStream(final InputStream inputStream) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Reader getReader() {
        try {
            if (this.charset == null) {
                return null;
            }
            if (this.r == null) {
                this.r = new InputStreamReader(this.source.getInputStream(), this.charset);
            }
            return this.r;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public InputStream getInputStream() {
        try {
            if (this.charset != null) {
                return null;
            }
            if (this.is == null) {
                this.is = this.source.getInputStream();
            }
            return this.is;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public DataSource getDataSource() {
        return this.source;
    }
}
