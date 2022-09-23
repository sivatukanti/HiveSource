// 
// Decompiled by Procyon v0.5.36
// 

package org.tukaani.xz.index;

import java.io.IOException;
import java.util.Iterator;
import org.tukaani.xz.common.EncoderUtil;
import java.util.zip.Checksum;
import java.util.zip.CheckedOutputStream;
import java.util.zip.CRC32;
import java.io.OutputStream;
import org.tukaani.xz.XZIOException;
import java.util.ArrayList;

public class IndexEncoder extends IndexBase
{
    private final ArrayList records;
    
    public IndexEncoder() {
        super(new XZIOException("XZ Stream or its Index has grown too big"));
        this.records = new ArrayList();
    }
    
    public void add(final long n, final long n2) throws XZIOException {
        super.add(n, n2);
        this.records.add(new IndexRecord(n, n2));
    }
    
    public void encode(final OutputStream out) throws IOException {
        final CRC32 cksum = new CRC32();
        final CheckedOutputStream checkedOutputStream = new CheckedOutputStream(out, cksum);
        checkedOutputStream.write(0);
        EncoderUtil.encodeVLI(checkedOutputStream, this.recordCount);
        for (final IndexRecord indexRecord : this.records) {
            EncoderUtil.encodeVLI(checkedOutputStream, indexRecord.unpadded);
            EncoderUtil.encodeVLI(checkedOutputStream, indexRecord.uncompressed);
        }
        for (int i = this.getIndexPaddingSize(); i > 0; --i) {
            checkedOutputStream.write(0);
        }
        final long value = cksum.getValue();
        for (int j = 0; j < 4; ++j) {
            out.write((byte)(value >>> j * 8));
        }
    }
}
