// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

public interface ByteHolder
{
    void write(final int p0) throws IOException;
    
    void write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    long write(final InputStream p0, final long p1) throws IOException;
    
    void clear() throws IOException;
    
    void startReading() throws IOException;
    
    int read() throws IOException;
    
    int read(final byte[] p0, final int p1, final int p2) throws IOException;
    
    int read(final OutputStream p0, final int p1) throws IOException;
    
    int shiftToFront() throws IOException;
    
    int available() throws IOException;
    
    int numBytesSaved() throws IOException;
    
    long skip(final long p0) throws IOException;
    
    boolean writingMode();
    
    ByteHolder cloneEmpty();
}
