// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.io;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;

public interface StorageRandomAccessFile extends DataInput, DataOutput
{
    void close() throws IOException;
    
    long getFilePointer() throws IOException;
    
    long length() throws IOException;
    
    void seek(final long p0) throws IOException;
    
    void setLength(final long p0) throws IOException;
    
    void sync() throws IOException;
    
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
}
