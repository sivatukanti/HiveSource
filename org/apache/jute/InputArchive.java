// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.io.IOException;

public interface InputArchive
{
    byte readByte(final String p0) throws IOException;
    
    boolean readBool(final String p0) throws IOException;
    
    int readInt(final String p0) throws IOException;
    
    long readLong(final String p0) throws IOException;
    
    float readFloat(final String p0) throws IOException;
    
    double readDouble(final String p0) throws IOException;
    
    String readString(final String p0) throws IOException;
    
    byte[] readBuffer(final String p0) throws IOException;
    
    void readRecord(final Record p0, final String p1) throws IOException;
    
    void startRecord(final String p0) throws IOException;
    
    void endRecord(final String p0) throws IOException;
    
    Index startVector(final String p0) throws IOException;
    
    void endVector(final String p0) throws IOException;
    
    Index startMap(final String p0) throws IOException;
    
    void endMap(final String p0) throws IOException;
}
