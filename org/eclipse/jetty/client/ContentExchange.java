// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.io.BufferUtil;
import org.eclipse.jetty.http.HttpHeaders;
import java.io.IOException;
import org.eclipse.jetty.io.Buffer;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.ByteArrayOutputStream;

public class ContentExchange extends CachedExchange
{
    private int _bufferSize;
    private String _encoding;
    private ByteArrayOutputStream _responseContent;
    private File _fileForUpload;
    
    public ContentExchange() {
        super(false);
        this._bufferSize = 4096;
        this._encoding = "utf-8";
    }
    
    public ContentExchange(final boolean cacheFields) {
        super(cacheFields);
        this._bufferSize = 4096;
        this._encoding = "utf-8";
    }
    
    public synchronized String getResponseContent() throws UnsupportedEncodingException {
        if (this._responseContent != null) {
            return this._responseContent.toString(this._encoding);
        }
        return null;
    }
    
    public synchronized byte[] getResponseContentBytes() {
        if (this._responseContent != null) {
            return this._responseContent.toByteArray();
        }
        return null;
    }
    
    @Override
    protected synchronized void onResponseStatus(final Buffer version, final int status, final Buffer reason) throws IOException {
        if (this._responseContent != null) {
            this._responseContent.reset();
        }
        super.onResponseStatus(version, status, reason);
    }
    
    @Override
    protected synchronized void onResponseHeader(final Buffer name, final Buffer value) throws IOException {
        super.onResponseHeader(name, value);
        final int header = HttpHeaders.CACHE.getOrdinal(name);
        switch (header) {
            case 12: {
                this._bufferSize = BufferUtil.toInt(value);
                break;
            }
            case 16: {
                final String mime = StringUtil.asciiToLowerCase(value.toString());
                int i = mime.indexOf("charset=");
                if (i <= 0) {
                    break;
                }
                this._encoding = mime.substring(i + 8);
                i = this._encoding.indexOf(59);
                if (i > 0) {
                    this._encoding = this._encoding.substring(0, i);
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    protected synchronized void onResponseContent(final Buffer content) throws IOException {
        super.onResponseContent(content);
        if (this._responseContent == null) {
            this._responseContent = new ByteArrayOutputStream(this._bufferSize);
        }
        content.writeTo(this._responseContent);
    }
    
    @Override
    protected synchronized void onRetry() throws IOException {
        if (this._fileForUpload != null) {
            this.setRequestContent(null);
            this.setRequestContentSource(this.getInputStream());
        }
        else {
            super.onRetry();
        }
    }
    
    private synchronized InputStream getInputStream() throws IOException {
        return new FileInputStream(this._fileForUpload);
    }
    
    public synchronized File getFileForUpload() {
        return this._fileForUpload;
    }
    
    public synchronized void setFileForUpload(final File fileForUpload) throws IOException {
        this._fileForUpload = fileForUpload;
        this.setRequestContentSource(this.getInputStream());
    }
}
