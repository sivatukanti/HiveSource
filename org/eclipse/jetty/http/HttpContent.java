// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http;

import java.nio.channels.ReadableByteChannel;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.jetty.util.resource.Resource;
import java.nio.ByteBuffer;

public interface HttpContent
{
    HttpField getContentType();
    
    String getContentTypeValue();
    
    String getCharacterEncoding();
    
    MimeTypes.Type getMimeType();
    
    HttpField getContentEncoding();
    
    String getContentEncodingValue();
    
    HttpField getContentLength();
    
    long getContentLengthValue();
    
    HttpField getLastModified();
    
    String getLastModifiedValue();
    
    HttpField getETag();
    
    String getETagValue();
    
    ByteBuffer getIndirectBuffer();
    
    ByteBuffer getDirectBuffer();
    
    Resource getResource();
    
    InputStream getInputStream() throws IOException;
    
    ReadableByteChannel getReadableByteChannel() throws IOException;
    
    void release();
    
    HttpContent getGzipContent();
    
    public interface Factory
    {
        HttpContent getContent(final String p0, final int p1) throws IOException;
    }
}
