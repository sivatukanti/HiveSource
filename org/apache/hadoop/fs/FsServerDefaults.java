// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.io.WritableFactories;
import org.apache.hadoop.io.WritableFactory;
import java.io.DataInput;
import java.io.IOException;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataOutput;
import org.apache.hadoop.util.DataChecksum;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.io.Writable;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class FsServerDefaults implements Writable
{
    private long blockSize;
    private int bytesPerChecksum;
    private int writePacketSize;
    private short replication;
    private int fileBufferSize;
    private boolean encryptDataTransfer;
    private long trashInterval;
    private DataChecksum.Type checksumType;
    private String keyProviderUri;
    private byte storagepolicyId;
    
    public FsServerDefaults() {
    }
    
    public FsServerDefaults(final long blockSize, final int bytesPerChecksum, final int writePacketSize, final short replication, final int fileBufferSize, final boolean encryptDataTransfer, final long trashInterval, final DataChecksum.Type checksumType) {
        this(blockSize, bytesPerChecksum, writePacketSize, replication, fileBufferSize, encryptDataTransfer, trashInterval, checksumType, null, (byte)0);
    }
    
    public FsServerDefaults(final long blockSize, final int bytesPerChecksum, final int writePacketSize, final short replication, final int fileBufferSize, final boolean encryptDataTransfer, final long trashInterval, final DataChecksum.Type checksumType, final String keyProviderUri) {
        this(blockSize, bytesPerChecksum, writePacketSize, replication, fileBufferSize, encryptDataTransfer, trashInterval, checksumType, keyProviderUri, (byte)0);
    }
    
    public FsServerDefaults(final long blockSize, final int bytesPerChecksum, final int writePacketSize, final short replication, final int fileBufferSize, final boolean encryptDataTransfer, final long trashInterval, final DataChecksum.Type checksumType, final String keyProviderUri, final byte storagepolicy) {
        this.blockSize = blockSize;
        this.bytesPerChecksum = bytesPerChecksum;
        this.writePacketSize = writePacketSize;
        this.replication = replication;
        this.fileBufferSize = fileBufferSize;
        this.encryptDataTransfer = encryptDataTransfer;
        this.trashInterval = trashInterval;
        this.checksumType = checksumType;
        this.keyProviderUri = keyProviderUri;
        this.storagepolicyId = storagepolicy;
    }
    
    public long getBlockSize() {
        return this.blockSize;
    }
    
    public int getBytesPerChecksum() {
        return this.bytesPerChecksum;
    }
    
    public int getWritePacketSize() {
        return this.writePacketSize;
    }
    
    public short getReplication() {
        return this.replication;
    }
    
    public int getFileBufferSize() {
        return this.fileBufferSize;
    }
    
    public boolean getEncryptDataTransfer() {
        return this.encryptDataTransfer;
    }
    
    public long getTrashInterval() {
        return this.trashInterval;
    }
    
    public DataChecksum.Type getChecksumType() {
        return this.checksumType;
    }
    
    public String getKeyProviderUri() {
        return this.keyProviderUri;
    }
    
    public byte getDefaultStoragePolicyId() {
        return this.storagepolicyId;
    }
    
    @InterfaceAudience.Private
    @Override
    public void write(final DataOutput out) throws IOException {
        out.writeLong(this.blockSize);
        out.writeInt(this.bytesPerChecksum);
        out.writeInt(this.writePacketSize);
        out.writeShort(this.replication);
        out.writeInt(this.fileBufferSize);
        WritableUtils.writeEnum(out, this.checksumType);
        out.writeByte(this.storagepolicyId);
    }
    
    @InterfaceAudience.Private
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.blockSize = in.readLong();
        this.bytesPerChecksum = in.readInt();
        this.writePacketSize = in.readInt();
        this.replication = in.readShort();
        this.fileBufferSize = in.readInt();
        this.checksumType = WritableUtils.readEnum(in, DataChecksum.Type.class);
        this.storagepolicyId = in.readByte();
    }
    
    static {
        WritableFactories.setFactory(FsServerDefaults.class, new WritableFactory() {
            @Override
            public Writable newInstance() {
                return new FsServerDefaults();
            }
        });
    }
}
