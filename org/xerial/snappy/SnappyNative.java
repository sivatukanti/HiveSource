// 
// Decompiled by Procyon v0.5.36
// 

package org.xerial.snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SnappyNative implements SnappyNativeAPI
{
    public native String nativeLibraryVersion();
    
    public native int rawCompress(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer p3, final int p4) throws IOException;
    
    public native int rawCompress(final Object p0, final int p1, final int p2, final Object p3, final int p4);
    
    public native int rawUncompress(final ByteBuffer p0, final int p1, final int p2, final ByteBuffer p3, final int p4) throws IOException;
    
    public native int rawUncompress(final Object p0, final int p1, final int p2, final Object p3, final int p4) throws IOException;
    
    public native int maxCompressedLength(final int p0);
    
    public native int uncompressedLength(final ByteBuffer p0, final int p1, final int p2) throws IOException;
    
    public native int uncompressedLength(final Object p0, final int p1, final int p2) throws IOException;
    
    public native boolean isValidCompressedBuffer(final ByteBuffer p0, final int p1, final int p2) throws IOException;
    
    public native boolean isValidCompressedBuffer(final Object p0, final int p1, final int p2) throws IOException;
    
    public native void arrayCopy(final Object p0, final int p1, final int p2, final Object p3, final int p4) throws IOException;
    
    public void throw_error(final int errorCode) throws IOException {
        throw new IOException(String.format("%s(%d)", SnappyErrorCode.getErrorMessage(errorCode), errorCode));
    }
}
