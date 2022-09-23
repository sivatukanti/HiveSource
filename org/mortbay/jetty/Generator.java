// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty;

import java.io.IOException;
import org.mortbay.io.Buffer;

public interface Generator
{
    public static final boolean LAST = true;
    public static final boolean MORE = false;
    
    void addContent(final Buffer p0, final boolean p1) throws IOException;
    
    boolean addContent(final byte p0) throws IOException;
    
    void complete() throws IOException;
    
    void completeHeader(final HttpFields p0, final boolean p1) throws IOException;
    
    long flush() throws IOException;
    
    int getContentBufferSize();
    
    long getContentWritten();
    
    boolean isContentWritten();
    
    void increaseContentBufferSize(final int p0);
    
    boolean isBufferFull();
    
    boolean isCommitted();
    
    boolean isComplete();
    
    boolean isPersistent();
    
    void reset(final boolean p0);
    
    void resetBuffer();
    
    void sendError(final int p0, final String p1, final String p2, final boolean p3) throws IOException;
    
    void setHead(final boolean p0);
    
    void setRequest(final String p0, final String p1);
    
    void setResponse(final int p0, final String p1);
    
    void setSendServerVersion(final boolean p0);
    
    void setVersion(final int p0);
    
    boolean isIdle();
    
    void setContentLength(final long p0);
    
    void setPersistent(final boolean p0);
}
