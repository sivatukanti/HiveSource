// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.Writer;
import java.io.Reader;
import java.io.InputStream;
import java.sql.SQLException;
import java.io.IOException;

interface InternalClob
{
    long getCharLength() throws IOException, SQLException;
    
    long getCharLengthIfKnown();
    
    InputStream getRawByteStream() throws IOException, SQLException;
    
    Reader getReader(final long p0) throws IOException, SQLException;
    
    Reader getInternalReader(final long p0) throws IOException, SQLException;
    
    long getUpdateCount();
    
    Writer getWriter(final long p0) throws IOException, SQLException;
    
    long insertString(final String p0, final long p1) throws IOException, SQLException;
    
    boolean isReleased();
    
    boolean isWritable();
    
    void release() throws IOException, SQLException;
    
    void truncate(final long p0) throws IOException, SQLException;
}
