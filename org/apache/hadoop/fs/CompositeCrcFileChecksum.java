// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.util.CrcUtil;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
@InterfaceStability.Unstable
public class CompositeCrcFileChecksum extends FileChecksum
{
    public static final int LENGTH = 4;
    private int crc;
    private DataChecksum.Type crcType;
    private int bytesPerCrc;
    
    public CompositeCrcFileChecksum(final int crc, final DataChecksum.Type crcType, final int bytesPerCrc) {
        this.crc = crc;
        this.crcType = crcType;
        this.bytesPerCrc = bytesPerCrc;
    }
    
    @Override
    public String getAlgorithmName() {
        return "COMPOSITE-" + this.crcType.name();
    }
    
    @Override
    public int getLength() {
        return 4;
    }
    
    @Override
    public byte[] getBytes() {
        return CrcUtil.intToBytes(this.crc);
    }
    
    @Override
    public Options.ChecksumOpt getChecksumOpt() {
        return new Options.ChecksumOpt(this.crcType, this.bytesPerCrc);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.crc = in.readInt();
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeInt(this.crc);
    }
    
    @Override
    public String toString() {
        return this.getAlgorithmName() + ":" + String.format("0x%08x", this.crc);
    }
}
