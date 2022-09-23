// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface ExecuteStreamHandler
{
    void setProcessInputStream(final OutputStream p0) throws IOException;
    
    void setProcessErrorStream(final InputStream p0) throws IOException;
    
    void setProcessOutputStream(final InputStream p0) throws IOException;
    
    void start() throws IOException;
    
    void stop();
}
