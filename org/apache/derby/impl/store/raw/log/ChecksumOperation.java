// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.services.io.FormatIdUtil;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.raw.Transaction;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.derby.iapi.store.raw.Loggable;

public class ChecksumOperation implements Loggable
{
    private byte checksumAlgo;
    private long checksumValue;
    private int dataLength;
    private Checksum checksum;
    public static final byte CRC32_ALGORITHM = 1;
    private static final int formatLength;
    
    public void init() {
        this.checksumAlgo = 1;
        this.initializeChecksumAlgo();
        this.dataLength = 0;
    }
    
    protected void update(final byte[] array, final int n, final int n2) {
        this.checksum.update(array, n, n2);
        this.dataLength += n2;
    }
    
    protected void reset() {
        this.checksum.reset();
        this.dataLength = 0;
    }
    
    private void initializeChecksumAlgo() {
        if (this.checksumAlgo == 1) {
            this.checksum = new CRC32();
        }
    }
    
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.checksumValue = this.checksum.getValue();
        objectOutput.writeByte(this.checksumAlgo);
        objectOutput.writeInt(this.dataLength);
        objectOutput.writeLong(this.checksumValue);
    }
    
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.checksumAlgo = (byte)objectInput.readUnsignedByte();
        this.dataLength = objectInput.readInt();
        this.checksumValue = objectInput.readLong();
        this.initializeChecksumAlgo();
    }
    
    public int getStoredSize() {
        return ChecksumOperation.formatLength + 1 + 4 + 8;
    }
    
    public int getTypeFormatId() {
        return 453;
    }
    
    public void doMe(final Transaction transaction, final LogInstant logInstant, final LimitObjectInput limitObjectInput) throws StandardException {
    }
    
    public ByteArray getPreparedLog() {
        return null;
    }
    
    public boolean needsRedo(final Transaction transaction) {
        return false;
    }
    
    public void releaseResource(final Transaction transaction) {
    }
    
    public int group() {
        return 2304;
    }
    
    protected int getDataLength() {
        return this.dataLength;
    }
    
    protected boolean isChecksumValid(final byte[] array, final int n, final int n2) {
        this.checksum.reset();
        this.checksum.update(array, n, n2);
        return this.checksum.getValue() == this.checksumValue;
    }
    
    public String toString() {
        return null;
    }
    
    static {
        formatLength = FormatIdUtil.getFormatIdByteLength(453);
    }
}
