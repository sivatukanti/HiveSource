// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.IOException;
import java.io.InputStream;
import org.mortbay.resource.Resource;
import org.mortbay.io.Buffer;

public interface HttpContent
{
    Buffer getContentType();
    
    Buffer getLastModified();
    
    Buffer getBuffer();
    
    Resource getResource();
    
    long getContentLength();
    
    InputStream getInputStream() throws IOException;
    
    void release();
}
