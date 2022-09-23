// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandle;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.fs.ChecksumException;
import java.nio.ByteBuffer;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.util.zip.Checksum;

@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class DataChecksum implements Checksum
{
    public static final int CHECKSUM_NULL = 0;
    public static final int CHECKSUM_CRC32 = 1;
    public static final int CHECKSUM_CRC32C = 2;
    public static final int CHECKSUM_DEFAULT = 3;
    public static final int CHECKSUM_MIXED = 4;
    private static final Logger LOG;
    private static volatile boolean useJava9Crc32C;
    private final Type type;
    private final Checksum summer;
    private final int bytesPerChecksum;
    private int inSum;
    public static final int SIZE_OF_INTEGER = 4;
    
    public static Checksum newCrc32() {
        return new CRC32();
    }
    
    static Checksum newCrc32C() {
        try {
            return DataChecksum.useJava9Crc32C ? Java9Crc32CFactory.createChecksum() : new PureJavaCrc32C();
        }
        catch (ExceptionInInitializerError | RuntimeException exceptionInInitializerError) {
            final Throwable t;
            final Throwable e = t;
            DataChecksum.LOG.error("CRC32C creation failed, switching to PureJavaCrc32C", e);
            DataChecksum.useJava9Crc32C = false;
            return new PureJavaCrc32C();
        }
    }
    
    public static int getCrcPolynomialForType(final Type type) throws IOException {
        switch (type) {
            case CRC32: {
                return -306674912;
            }
            case CRC32C: {
                return -2097792136;
            }
            default: {
                throw new IOException("No CRC polynomial could be associated with type: " + type);
            }
        }
    }
    
    public static DataChecksum newDataChecksum(final Type type, final int bytesPerChecksum) {
        if (bytesPerChecksum <= 0) {
            return null;
        }
        switch (type) {
            case NULL: {
                return new DataChecksum(type, new ChecksumNull(), bytesPerChecksum);
            }
            case CRC32: {
                return new DataChecksum(type, newCrc32(), bytesPerChecksum);
            }
            case CRC32C: {
                return new DataChecksum(type, newCrc32C(), bytesPerChecksum);
            }
            default: {
                return null;
            }
        }
    }
    
    public static DataChecksum newDataChecksum(final byte[] bytes, final int offset) {
        if (offset < 0 || bytes.length < offset + getChecksumHeaderSize()) {
            return null;
        }
        final int bytesPerChecksum = (bytes[offset + 1] & 0xFF) << 24 | (bytes[offset + 2] & 0xFF) << 16 | (bytes[offset + 3] & 0xFF) << 8 | (bytes[offset + 4] & 0xFF);
        return newDataChecksum(Type.valueOf(bytes[offset]), bytesPerChecksum);
    }
    
    public static DataChecksum newDataChecksum(final DataInputStream in) throws IOException {
        final int type = in.readByte();
        final int bpc = in.readInt();
        final DataChecksum summer = newDataChecksum(Type.valueOf(type), bpc);
        if (summer == null) {
            throw new InvalidChecksumSizeException("Could not create DataChecksum of type " + type + " with bytesPerChecksum " + bpc);
        }
        return summer;
    }
    
    public void writeHeader(final DataOutputStream out) throws IOException {
        out.writeByte(this.type.id);
        out.writeInt(this.bytesPerChecksum);
    }
    
    public byte[] getHeader() {
        final byte[] header = new byte[getChecksumHeaderSize()];
        header[0] = (byte)(this.type.id & 0xFF);
        header[1] = (byte)(this.bytesPerChecksum >>> 24 & 0xFF);
        header[2] = (byte)(this.bytesPerChecksum >>> 16 & 0xFF);
        header[3] = (byte)(this.bytesPerChecksum >>> 8 & 0xFF);
        header[4] = (byte)(this.bytesPerChecksum & 0xFF);
        return header;
    }
    
    public int writeValue(final DataOutputStream out, final boolean reset) throws IOException {
        if (this.type.size <= 0) {
            return 0;
        }
        if (this.type.size == 4) {
            out.writeInt((int)this.summer.getValue());
            if (reset) {
                this.reset();
            }
            return this.type.size;
        }
        throw new IOException("Unknown Checksum " + this.type);
    }
    
    public int writeValue(final byte[] buf, final int offset, final boolean reset) throws IOException {
        if (this.type.size <= 0) {
            return 0;
        }
        if (this.type.size == 4) {
            final int checksum = (int)this.summer.getValue();
            buf[offset + 0] = (byte)(checksum >>> 24 & 0xFF);
            buf[offset + 1] = (byte)(checksum >>> 16 & 0xFF);
            buf[offset + 2] = (byte)(checksum >>> 8 & 0xFF);
            buf[offset + 3] = (byte)(checksum & 0xFF);
            if (reset) {
                this.reset();
            }
            return this.type.size;
        }
        throw new IOException("Unknown Checksum " + this.type);
    }
    
    public boolean compare(final byte[] buf, final int offset) {
        if (this.type.size == 4) {
            final int checksum = (buf[offset + 0] & 0xFF) << 24 | (buf[offset + 1] & 0xFF) << 16 | (buf[offset + 2] & 0xFF) << 8 | (buf[offset + 3] & 0xFF);
            return checksum == (int)this.summer.getValue();
        }
        return this.type.size == 0;
    }
    
    private DataChecksum(final Type type, final Checksum checksum, final int chunkSize) {
        this.inSum = 0;
        this.type = type;
        this.summer = checksum;
        this.bytesPerChecksum = chunkSize;
    }
    
    public Type getChecksumType() {
        return this.type;
    }
    
    public int getChecksumSize() {
        return this.type.size;
    }
    
    public int getChecksumSize(final int dataSize) {
        return ((dataSize - 1) / this.getBytesPerChecksum() + 1) * this.getChecksumSize();
    }
    
    public int getBytesPerChecksum() {
        return this.bytesPerChecksum;
    }
    
    public int getNumBytesInSum() {
        return this.inSum;
    }
    
    public static int getChecksumHeaderSize() {
        return 5;
    }
    
    @Override
    public long getValue() {
        return this.summer.getValue();
    }
    
    @Override
    public void reset() {
        this.summer.reset();
        this.inSum = 0;
    }
    
    @Override
    public void update(final byte[] b, final int off, final int len) {
        if (len > 0) {
            this.summer.update(b, off, len);
            this.inSum += len;
        }
    }
    
    @Override
    public void update(final int b) {
        this.summer.update(b);
        ++this.inSum;
    }
    
    public void verifyChunkedSums(final ByteBuffer data, final ByteBuffer checksums, final String fileName, final long basePos) throws ChecksumException {
        if (this.type.size == 0) {
            return;
        }
        if (data.hasArray() && checksums.hasArray()) {
            final int dataOffset = data.arrayOffset() + data.position();
            final int crcsOffset = checksums.arrayOffset() + checksums.position();
            if (NativeCrc32.isAvailable()) {
                NativeCrc32.verifyChunkedSumsByteArray(this.bytesPerChecksum, this.type.id, checksums.array(), crcsOffset, data.array(), dataOffset, data.remaining(), fileName, basePos);
            }
            else {
                verifyChunked(this.type, this.summer, data.array(), dataOffset, data.remaining(), this.bytesPerChecksum, checksums.array(), crcsOffset, fileName, basePos);
            }
            return;
        }
        if (NativeCrc32.isAvailable() && data.isDirect()) {
            NativeCrc32.verifyChunkedSums(this.bytesPerChecksum, this.type.id, checksums, data, fileName, basePos);
        }
        else {
            verifyChunked(this.type, this.summer, data, this.bytesPerChecksum, checksums, fileName, basePos);
        }
    }
    
    static void verifyChunked(final Type type, final Checksum algorithm, final ByteBuffer data, final int bytesPerCrc, final ByteBuffer crcs, final String filename, final long basePos) throws ChecksumException {
        final byte[] bytes = new byte[bytesPerCrc];
        final int dataOffset = data.position();
        final int dataLength = data.remaining();
        data.mark();
        crcs.mark();
        try {
            int i = 0;
            for (int n = dataLength - bytesPerCrc + 1; i < n; i += bytesPerCrc) {
                data.get(bytes);
                algorithm.reset();
                algorithm.update(bytes, 0, bytesPerCrc);
                final int computed = (int)algorithm.getValue();
                final int expected = crcs.getInt();
                if (computed != expected) {
                    final long errPos = basePos + data.position() - dataOffset - bytesPerCrc;
                    throwChecksumException(type, algorithm, filename, errPos, expected, computed);
                }
            }
            final int remainder = dataLength - i;
            if (remainder > 0) {
                data.get(bytes, 0, remainder);
                algorithm.reset();
                algorithm.update(bytes, 0, remainder);
                final int computed = (int)algorithm.getValue();
                final int expected = crcs.getInt();
                if (computed != expected) {
                    final long errPos = basePos + data.position() - dataOffset - remainder;
                    throwChecksumException(type, algorithm, filename, errPos, expected, computed);
                }
            }
        }
        finally {
            data.reset();
            crcs.reset();
        }
    }
    
    static void verifyChunked(final Type type, final Checksum algorithm, final byte[] data, final int dataOffset, final int dataLength, final int bytesPerCrc, final byte[] crcs, final int crcsOffset, final String filename, final long basePos) throws ChecksumException {
        final int dataEnd = dataOffset + dataLength;
        int i = dataOffset;
        int j = crcsOffset;
        for (int n = dataEnd - bytesPerCrc + 1; i < n; i += bytesPerCrc, j += 4) {
            algorithm.reset();
            algorithm.update(data, i, bytesPerCrc);
            final int computed = (int)algorithm.getValue();
            final int expected = (crcs[j] << 24) + (crcs[j + 1] << 24 >>> 8) + ((crcs[j + 2] << 24 >>> 16) + (crcs[j + 3] << 24 >>> 24));
            if (computed != expected) {
                final long errPos = basePos + i - dataOffset;
                throwChecksumException(type, algorithm, filename, errPos, expected, computed);
            }
        }
        final int remainder = dataEnd - i;
        if (remainder > 0) {
            algorithm.reset();
            algorithm.update(data, i, remainder);
            final int computed = (int)algorithm.getValue();
            final int expected = (crcs[j] << 24) + (crcs[j + 1] << 24 >>> 8) + ((crcs[j + 2] << 24 >>> 16) + (crcs[j + 3] << 24 >>> 24));
            if (computed != expected) {
                final long errPos = basePos + i - dataOffset;
                throwChecksumException(type, algorithm, filename, errPos, expected, computed);
            }
        }
    }
    
    private static void throwChecksumException(final Type type, final Checksum algorithm, final String filename, final long errPos, final int expected, final int computed) throws ChecksumException {
        throw new ChecksumException("Checksum " + type + " not matched for file " + filename + " at position " + errPos + String.format(": expected=%X but computed=%X", expected, computed) + ", algorithm=" + algorithm.getClass().getSimpleName(), errPos);
    }
    
    public void calculateChunkedSums(final ByteBuffer data, final ByteBuffer checksums) {
        if (this.type.size == 0) {
            return;
        }
        if (data.hasArray() && checksums.hasArray()) {
            this.calculateChunkedSums(data.array(), data.arrayOffset() + data.position(), data.remaining(), checksums.array(), checksums.arrayOffset() + checksums.position());
            return;
        }
        if (NativeCrc32.isAvailable()) {
            NativeCrc32.calculateChunkedSums(this.bytesPerChecksum, this.type.id, checksums, data);
            return;
        }
        data.mark();
        checksums.mark();
        try {
            final byte[] buf = new byte[this.bytesPerChecksum];
            while (data.remaining() > 0) {
                final int n = Math.min(data.remaining(), this.bytesPerChecksum);
                data.get(buf, 0, n);
                this.summer.reset();
                this.summer.update(buf, 0, n);
                checksums.putInt((int)this.summer.getValue());
            }
        }
        finally {
            data.reset();
            checksums.reset();
        }
    }
    
    public void calculateChunkedSums(final byte[] data, int dataOffset, final int dataLength, final byte[] sums, int sumsOffset) {
        if (this.type.size == 0) {
            return;
        }
        if (NativeCrc32.isAvailable()) {
            NativeCrc32.calculateChunkedSumsByteArray(this.bytesPerChecksum, this.type.id, sums, sumsOffset, data, dataOffset, dataLength);
            return;
        }
        int n;
        long calculated;
        for (int remaining = dataLength; remaining > 0; remaining -= n, calculated = this.summer.getValue(), sums[sumsOffset++] = (byte)(calculated >> 24), sums[sumsOffset++] = (byte)(calculated >> 16), sums[sumsOffset++] = (byte)(calculated >> 8), sums[sumsOffset++] = (byte)calculated) {
            n = Math.min(remaining, this.bytesPerChecksum);
            this.summer.reset();
            this.summer.update(data, dataOffset, n);
            dataOffset += n;
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof DataChecksum)) {
            return false;
        }
        final DataChecksum o = (DataChecksum)other;
        return o.bytesPerChecksum == this.bytesPerChecksum && o.type == this.type;
    }
    
    @Override
    public int hashCode() {
        return (this.type.id + 31) * this.bytesPerChecksum;
    }
    
    @Override
    public String toString() {
        return "DataChecksum(type=" + this.type + ", chunkSize=" + this.bytesPerChecksum + ")";
    }
    
    static {
        LOG = LoggerFactory.getLogger(DataChecksum.class);
        DataChecksum.useJava9Crc32C = Shell.isJavaVersionAtLeast(9);
    }
    
    public enum Type
    {
        NULL(0, 0), 
        CRC32(1, 4), 
        CRC32C(2, 4), 
        DEFAULT(3, 0), 
        MIXED(4, 0);
        
        public final int id;
        public final int size;
        
        private Type(final int id, final int size) {
            this.id = id;
            this.size = size;
        }
        
        public static Type valueOf(final int id) {
            if (id < 0 || id >= values().length) {
                throw new IllegalArgumentException("id=" + id + " out of range [0, " + values().length + ")");
            }
            return values()[id];
        }
    }
    
    static class ChecksumNull implements Checksum
    {
        public ChecksumNull() {
        }
        
        @Override
        public long getValue() {
            return 0L;
        }
        
        @Override
        public void reset() {
        }
        
        @Override
        public void update(final byte[] b, final int off, final int len) {
        }
        
        @Override
        public void update(final int b) {
        }
    }
    
    private static class Java9Crc32CFactory
    {
        private static final MethodHandle NEW_CRC32C_MH;
        
        public static Checksum createChecksum() {
            try {
                return Java9Crc32CFactory.NEW_CRC32C_MH.invoke();
            }
            catch (Throwable t) {
                throw (t instanceof RuntimeException) ? t : new RuntimeException(t);
            }
        }
        
        static {
            MethodHandle newCRC32C = null;
            try {
                newCRC32C = MethodHandles.publicLookup().findConstructor(Class.forName("java.util.zip.CRC32C"), MethodType.methodType(Void.TYPE));
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
            NEW_CRC32C_MH = newCRC32C;
        }
    }
}
