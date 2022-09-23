// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.fs.ChecksumException;
import java.nio.ByteBuffer;

class NativeCrc32
{
    private static final boolean isSparc;
    public static final int CHECKSUM_CRC32 = 1;
    public static final int CHECKSUM_CRC32C = 2;
    
    public static boolean isAvailable() {
        return !NativeCrc32.isSparc && NativeCodeLoader.isNativeCodeLoaded();
    }
    
    public static void verifyChunkedSums(final int bytesPerSum, final int checksumType, final ByteBuffer sums, final ByteBuffer data, final String fileName, final long basePos) throws ChecksumException {
        nativeComputeChunkedSums(bytesPerSum, checksumType, sums, sums.position(), data, data.position(), data.remaining(), fileName, basePos, true);
    }
    
    public static void verifyChunkedSumsByteArray(final int bytesPerSum, final int checksumType, final byte[] sums, final int sumsOffset, final byte[] data, final int dataOffset, final int dataLength, final String fileName, final long basePos) throws ChecksumException {
        nativeComputeChunkedSumsByteArray(bytesPerSum, checksumType, sums, sumsOffset, data, dataOffset, dataLength, fileName, basePos, true);
    }
    
    public static void calculateChunkedSums(final int bytesPerSum, final int checksumType, final ByteBuffer sums, final ByteBuffer data) {
        nativeComputeChunkedSums(bytesPerSum, checksumType, sums, sums.position(), data, data.position(), data.remaining(), "", 0L, false);
    }
    
    public static void calculateChunkedSumsByteArray(final int bytesPerSum, final int checksumType, final byte[] sums, final int sumsOffset, final byte[] data, final int dataOffset, final int dataLength) {
        nativeComputeChunkedSumsByteArray(bytesPerSum, checksumType, sums, sumsOffset, data, dataOffset, dataLength, "", 0L, false);
    }
    
    @Deprecated
    @VisibleForTesting
    static native void nativeVerifyChunkedSums(final int p0, final int p1, final ByteBuffer p2, final int p3, final ByteBuffer p4, final int p5, final int p6, final String p7, final long p8) throws ChecksumException;
    
    private static native void nativeComputeChunkedSums(final int p0, final int p1, final ByteBuffer p2, final int p3, final ByteBuffer p4, final int p5, final int p6, final String p7, final long p8, final boolean p9);
    
    private static native void nativeComputeChunkedSumsByteArray(final int p0, final int p1, final byte[] p2, final int p3, final byte[] p4, final int p5, final int p6, final String p7, final long p8, final boolean p9);
    
    static {
        isSparc = System.getProperty("os.arch").toLowerCase().startsWith("sparc");
    }
}
