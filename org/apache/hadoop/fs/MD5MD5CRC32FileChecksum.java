// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.DataOutput;
import java.io.DataInput;
import org.apache.hadoop.io.WritableUtils;
import org.apache.hadoop.io.Writable;
import java.io.IOException;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.io.MD5Hash;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Unstable
public class MD5MD5CRC32FileChecksum extends FileChecksum
{
    public static final int LENGTH = 28;
    private int bytesPerCRC;
    private long crcPerBlock;
    private MD5Hash md5;
    
    public MD5MD5CRC32FileChecksum() {
        this(0, 0L, null);
    }
    
    public MD5MD5CRC32FileChecksum(final int bytesPerCRC, final long crcPerBlock, final MD5Hash md5) {
        this.bytesPerCRC = bytesPerCRC;
        this.crcPerBlock = crcPerBlock;
        this.md5 = md5;
    }
    
    @Override
    public String getAlgorithmName() {
        return "MD5-of-" + this.crcPerBlock + "MD5-of-" + this.bytesPerCRC + this.getCrcType().name();
    }
    
    public static DataChecksum.Type getCrcTypeFromAlgorithmName(final String algorithm) throws IOException {
        if (algorithm.endsWith(DataChecksum.Type.CRC32.name())) {
            return DataChecksum.Type.CRC32;
        }
        if (algorithm.endsWith(DataChecksum.Type.CRC32C.name())) {
            return DataChecksum.Type.CRC32C;
        }
        throw new IOException("Unknown checksum type in " + algorithm);
    }
    
    @Override
    public int getLength() {
        return 28;
    }
    
    @Override
    public byte[] getBytes() {
        return WritableUtils.toByteArray(this);
    }
    
    public DataChecksum.Type getCrcType() {
        return DataChecksum.Type.CRC32;
    }
    
    @Override
    public Options.ChecksumOpt getChecksumOpt() {
        return new Options.ChecksumOpt(this.getCrcType(), this.bytesPerCRC);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.bytesPerCRC = in.readInt();
        this.crcPerBlock = in.readLong();
        this.md5 = MD5Hash.read(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.bytesPerCRC);
        out.writeLong(this.crcPerBlock);
        this.md5.write(out);
    }
    
    @Override
    public String toString() {
        return this.getAlgorithmName() + ":" + this.md5;
    }
}
