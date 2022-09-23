// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.jute;

import java.util.TreeMap;
import java.util.List;
import java.io.IOException;

public interface OutputArchive
{
    void writeByte(final byte p0, final String p1) throws IOException;
    
    void writeBool(final boolean p0, final String p1) throws IOException;
    
    void writeInt(final int p0, final String p1) throws IOException;
    
    void writeLong(final long p0, final String p1) throws IOException;
    
    void writeFloat(final float p0, final String p1) throws IOException;
    
    void writeDouble(final double p0, final String p1) throws IOException;
    
    void writeString(final String p0, final String p1) throws IOException;
    
    void writeBuffer(final byte[] p0, final String p1) throws IOException;
    
    void writeRecord(final Record p0, final String p1) throws IOException;
    
    void startRecord(final Record p0, final String p1) throws IOException;
    
    void endRecord(final Record p0, final String p1) throws IOException;
    
    void startVector(final List p0, final String p1) throws IOException;
    
    void endVector(final List p0, final String p1) throws IOException;
    
    void startMap(final TreeMap p0, final String p1) throws IOException;
    
    void endMap(final TreeMap p0, final String p1) throws IOException;
}
