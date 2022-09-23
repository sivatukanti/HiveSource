// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.index;

import java.io.IOException;
import java.io.DataInputStream;
import java.util.Arrays;
import org.tukaani.xz.common.DecoderUtil;
import java.util.zip.Checksum;
import java.util.zip.CheckedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import org.tukaani.xz.check.CRC32;
import org.tukaani.xz.check.SHA256;
import org.tukaani.xz.XZIOException;
import org.tukaani.xz.CorruptedInputException;
import org.tukaani.xz.check.Check;

public class IndexHash extends IndexBase
{
    private Check hash;
    
    public IndexHash() {
        super(new CorruptedInputException());
        try {
            this.hash = new SHA256();
        }
        catch (NoSuchAlgorithmException ex) {
            this.hash = new CRC32();
        }
    }
    
    public void add(final long n, final long n2) throws XZIOException {
        super.add(n, n2);
        final ByteBuffer allocate = ByteBuffer.allocate(16);
        allocate.putLong(n);
        allocate.putLong(n2);
        this.hash.update(allocate.array());
    }
    
    public void validate(final InputStream in) throws IOException {
        final java.util.zip.CRC32 cksum = new java.util.zip.CRC32();
        cksum.update(0);
        final CheckedInputStream in2 = new CheckedInputStream(in, cksum);
        if (DecoderUtil.decodeVLI(in2) != this.recordCount) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        final IndexHash indexHash = new IndexHash();
        for (long n = 0L; n < this.recordCount; ++n) {
            final long decodeVLI = DecoderUtil.decodeVLI(in2);
            final long decodeVLI2 = DecoderUtil.decodeVLI(in2);
            try {
                indexHash.add(decodeVLI, decodeVLI2);
            }
            catch (XZIOException ex) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
            if (indexHash.blocksSum > this.blocksSum || indexHash.uncompressedSum > this.uncompressedSum || indexHash.indexListSize > this.indexListSize) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
        if (indexHash.blocksSum != this.blocksSum || indexHash.uncompressedSum != this.uncompressedSum || indexHash.indexListSize != this.indexListSize || !Arrays.equals(indexHash.hash.finish(), this.hash.finish())) {
            throw new CorruptedInputException("XZ Index is corrupt");
        }
        final DataInputStream dataInputStream = new DataInputStream(in2);
        for (int i = this.getIndexPaddingSize(); i > 0; --i) {
            if (dataInputStream.readUnsignedByte() != 0) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
        final long value = cksum.getValue();
        for (int j = 0; j < 4; ++j) {
            if ((value >>> j * 8 & 0xFFL) != dataInputStream.readUnsignedByte()) {
                throw new CorruptedInputException("XZ Index is corrupt");
            }
        }
    }
}
