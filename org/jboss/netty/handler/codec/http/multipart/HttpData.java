// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.http.multipart;

import java.nio.charset.Charset;
import java.io.InputStream;
import java.io.File;
import org.jboss.netty.buffer.ChannelBuffer;
import java.io.IOException;

public interface HttpData extends InterfaceHttpData
{
    void setMaxSize(final long p0);
    
    void checkSize(final long p0) throws IOException;
    
    void setContent(final ChannelBuffer p0) throws IOException;
    
    void addContent(final ChannelBuffer p0, final boolean p1) throws IOException;
    
    void setContent(final File p0) throws IOException;
    
    void setContent(final InputStream p0) throws IOException;
    
    boolean isCompleted();
    
    long length();
    
    void delete();
    
    byte[] get() throws IOException;
    
    ChannelBuffer getChannelBuffer() throws IOException;
    
    ChannelBuffer getChunk(final int p0) throws IOException;
    
    String getString() throws IOException;
    
    String getString(final Charset p0) throws IOException;
    
    void setCharset(final Charset p0);
    
    Charset getCharset();
    
    boolean renameTo(final File p0) throws IOException;
    
    boolean isInMemory();
    
    File getFile() throws IOException;
}
