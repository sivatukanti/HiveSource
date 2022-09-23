// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.io.MD5Hash;

public class MD5MD5CRC32CastagnoliFileChecksum extends MD5MD5CRC32FileChecksum
{
    public MD5MD5CRC32CastagnoliFileChecksum() {
        this(0, 0L, null);
    }
    
    public MD5MD5CRC32CastagnoliFileChecksum(final int bytesPerCRC, final long crcPerBlock, final MD5Hash md5) {
        super(bytesPerCRC, crcPerBlock, md5);
    }
    
    @Override
    public DataChecksum.Type getCrcType() {
        return DataChecksum.Type.CRC32C;
    }
}
