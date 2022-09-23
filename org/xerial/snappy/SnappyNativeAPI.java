// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface SnappyNativeAPI
{
    String nativeLibraryVersion();
    
    int rawCompress(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer p3, final int p4) throws IOException;
    
    int rawCompress(final Object p0, final int p1, final int p2, final Object p3, final int p4);
    
    int rawUncompress(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer p3, final int p4) throws IOException;
    
    int rawUncompress(final Object p0, final int p1, final int p2, final Object p3, final int p4) throws IOException;
    
    int maxCompressedLength(final int p0);
    
    int uncompressedLength(final ByteBuffer p0, final int p1, final int p2) throws IOException;
    
    int uncompressedLength(final Object p0, final int p1, final int p2) throws IOException;
    
    boolean isValidCompressedBuffer(final ByteBuffer p0, final int p1, final int p2) throws IOException;
    
    boolean isValidCompressedBuffer(final Object p0, final int p1, final int p2) throws IOException;
    
    void arrayCopy(final Object p0, final int p1, final int p2, final Object p3, final int p4) throws IOException;
    
    void throw_error(final int p0) throws IOException;
}
