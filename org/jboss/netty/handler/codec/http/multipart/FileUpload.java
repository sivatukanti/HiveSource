// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

public interface FileUpload extends HttpData
{
    String getFilename();
    
    void setFilename(final String p0);
    
    void setContentType(final String p0);
    
    String getContentType();
    
    void setContentTransferEncoding(final String p0);
    
    String getContentTransferEncoding();
}
