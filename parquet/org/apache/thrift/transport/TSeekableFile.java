// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift.transport;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface TSeekableFile
{
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    void close() throws IOException;
    
    long length() throws IOException;
    
    void seek(final long p0) throws IOException;
}
