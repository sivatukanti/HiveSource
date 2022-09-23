// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.io.DataInput;
import java.io.EOFException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.CompressedNumber;
import org.apache.derby.iapi.store.raw.PageKey;
import org.apache.derby.iapi.store.raw.RecordHandle;

public final class StoredRecordHeader
{
    private static final byte RECORD_DELETED = 1;
    private static final byte RECORD_OVERFLOW = 2;
    private static final byte RECORD_HAS_FIRST_FIELD = 4;
    private static final byte RECORD_VALID_MASK = 15;
    public static final int MAX_OVERFLOW_ONLY_REC_SIZE = 17;
    protected int id;
    private byte status;
    protected int numberFields;
    protected RecordHandle handle;
    private OverflowInfo overflow;
    
    public StoredRecordHeader() {
    }
    
    public StoredRecordHeader(final int id, final int numberFields) {
        this.setId(id);
        this.setNumberFields(numberFields);
    }
    
    public StoredRecordHeader(final byte[] array, final int n) {
        this.read(array, n);
    }
    
    public StoredRecordHeader(final StoredRecordHeader storedRecordHeader) {
        this.status = storedRecordHeader.status;
        this.id = storedRecordHeader.id;
        this.numberFields = storedRecordHeader.numberFields;
        this.handle = null;
        if (storedRecordHeader.overflow != null) {
            this.overflow = new OverflowInfo(storedRecordHeader.overflow);
        }
    }
    
    protected RecordHandle getHandle(final PageKey pageKey, final int n) {
        if (this.handle == null) {
            this.handle = new RecordId(pageKey, this.id, n);
        }
        return this.handle;
    }
    
    public final int getId() {
        return this.id;
    }
    
    public int getNumberFields() {
        return this.numberFields;
    }
    
    public long getOverflowPage() {
        return (this.overflow == null) ? 0L : this.overflow.overflowPage;
    }
    
    public int getOverflowId() {
        return (this.overflow == null) ? 0 : this.overflow.overflowId;
    }
    
    public int getFirstField() {
        return (this.overflow == null) ? 0 : this.overflow.firstField;
    }
    
    public final boolean hasOverflow() {
        return (this.status & 0x2) == 0x2;
    }
    
    protected final boolean hasFirstField() {
        return (this.status & 0x4) == 0x4;
    }
    
    public final boolean isDeleted() {
        return (this.status & 0x1) == 0x1;
    }
    
    public int size() {
        final int n = (this.id <= 63) ? 2 : ((this.id <= 16383) ? 3 : 5);
        int n2;
        if ((this.status & 0x6) == 0x0) {
            n2 = n + ((this.numberFields <= 63) ? 1 : ((this.numberFields <= 16383) ? 2 : 4));
        }
        else if ((this.status & 0x2) == 0x0) {
            n2 = n + CompressedNumber.sizeInt(this.numberFields) + CompressedNumber.sizeInt(this.overflow.firstField);
        }
        else {
            n2 = n + CompressedNumber.sizeLong(this.overflow.overflowPage) + CompressedNumber.sizeInt(this.overflow.overflowId);
            if (this.hasFirstField()) {
                n2 = n2 + CompressedNumber.sizeInt(this.overflow.firstField) + CompressedNumber.sizeInt(this.numberFields);
            }
        }
        return n2;
    }
    
    public int setDeleted(final boolean b) {
        int n = 0;
        if (b) {
            if (!this.isDeleted()) {
                n = 1;
                this.status |= 0x1;
            }
        }
        else if (this.isDeleted()) {
            n = -1;
            this.status &= 0xFFFFFFFE;
        }
        return n;
    }
    
    public void setFirstField(final int n) {
        if (this.overflow == null) {
            this.overflow = new OverflowInfo();
        }
        this.overflow.firstField = n;
        this.status |= 0x4;
    }
    
    public final void setId(final int id) {
        this.id = id;
    }
    
    public void setOverflowDetails(final RecordHandle recordHandle) {
        if (this.overflow == null) {
            this.overflow = new OverflowInfo();
        }
        this.overflow.overflowPage = recordHandle.getPageNumber();
        this.overflow.overflowId = recordHandle.getId();
    }
    
    public void setOverflowFields(final StoredRecordHeader storedRecordHeader) {
        if (this.overflow == null) {
            this.overflow = new OverflowInfo();
        }
        this.status = (byte)(storedRecordHeader.status | 0x2);
        this.id = storedRecordHeader.id;
        this.numberFields = storedRecordHeader.numberFields;
        this.overflow.firstField = storedRecordHeader.overflow.firstField;
        this.handle = null;
    }
    
    public final void setNumberFields(final int numberFields) {
        this.numberFields = numberFields;
    }
    
    public int write(final OutputStream outputStream) throws IOException {
        final int n = 1;
        outputStream.write(this.status);
        int n2 = n + CompressedNumber.writeInt(outputStream, this.id);
        if (this.hasOverflow()) {
            n2 = n2 + CompressedNumber.writeLong(outputStream, this.overflow.overflowPage) + CompressedNumber.writeInt(outputStream, this.overflow.overflowId);
        }
        if (this.hasFirstField()) {
            n2 += CompressedNumber.writeInt(outputStream, this.overflow.firstField);
        }
        if (!this.hasOverflow() || this.hasFirstField()) {
            n2 += CompressedNumber.writeInt(outputStream, this.numberFields);
        }
        return n2;
    }
    
    public void read(final ObjectInput objectInput) throws IOException {
        final int read = objectInput.read();
        if (read < 0) {
            throw new EOFException();
        }
        this.status = (byte)read;
        this.id = CompressedNumber.readInt(objectInput);
        if (this.hasOverflow() || this.hasFirstField()) {
            this.overflow = new OverflowInfo();
        }
        else {
            this.overflow = null;
        }
        if (this.hasOverflow()) {
            this.overflow.overflowPage = CompressedNumber.readLong(objectInput);
            this.overflow.overflowId = CompressedNumber.readInt(objectInput);
        }
        if (this.hasFirstField()) {
            this.overflow.firstField = CompressedNumber.readInt(objectInput);
        }
        if (!this.hasOverflow() || this.hasFirstField()) {
            this.numberFields = CompressedNumber.readInt(objectInput);
        }
        else {
            this.numberFields = 0;
        }
        this.handle = null;
    }
    
    private int readOverFlowPage(final byte[] array, int n) {
        final byte b = array[n++];
        if ((b & 0xFFFFFFC0) == 0x0) {
            this.overflow.overflowPage = (b << 8 | (array[n] & 0xFF));
            return 2;
        }
        if ((b & 0x80) == 0x0) {
            this.overflow.overflowPage = ((b & 0x3F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF));
            return 4;
        }
        this.overflow.overflowPage = ((long)(b & 0x7F) << 56 | (long)(array[n++] & 0xFF) << 48 | (long)(array[n++] & 0xFF) << 40 | (long)(array[n++] & 0xFF) << 32 | (long)(array[n++] & 0xFF) << 24 | (long)(array[n++] & 0xFF) << 16 | (long)(array[n++] & 0xFF) << 8 | (long)(array[n] & 0xFF));
        return 8;
    }
    
    private int readOverFlowId(final byte[] array, int n) {
        final byte b = array[n++];
        if ((b & 0xFFFFFFC0) == 0x0) {
            this.overflow.overflowId = b;
            return 1;
        }
        if ((b & 0x80) == 0x0) {
            this.overflow.overflowId = ((b & 0x3F) << 8 | (array[n] & 0xFF));
            return 2;
        }
        this.overflow.overflowId = ((b & 0x7F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF));
        return 4;
    }
    
    private int readFirstField(final byte[] array, int n) {
        final byte b = array[n++];
        if ((b & 0xFFFFFFC0) == 0x0) {
            this.overflow.firstField = b;
            return 1;
        }
        if ((b & 0x80) == 0x0) {
            this.overflow.firstField = ((b & 0x3F) << 8 | (array[n] & 0xFF));
            return 2;
        }
        this.overflow.firstField = ((b & 0x7F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF));
        return 4;
    }
    
    private void readNumberFields(final byte[] array, int n) {
        final byte numberFields = array[n++];
        if ((numberFields & 0xFFFFFFC0) == 0x0) {
            this.numberFields = numberFields;
        }
        else if ((numberFields & 0x80) == 0x0) {
            this.numberFields = ((numberFields & 0x3F) << 8 | (array[n] & 0xFF));
        }
        else {
            this.numberFields = ((numberFields & 0x7F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n] & 0xFF));
        }
    }
    
    private void read(final byte[] array, int n) {
        this.status = array[n++];
        final byte id = array[n++];
        if ((id & 0xFFFFFFC0) == 0x0) {
            this.id = id;
        }
        else if ((id & 0x80) == 0x0) {
            this.id = ((id & 0x3F) << 8 | (array[n++] & 0xFF));
        }
        else {
            this.id = ((id & 0x7F) << 24 | (array[n++] & 0xFF) << 16 | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF));
        }
        if ((this.status & 0x6) == 0x0) {
            this.overflow = null;
            this.readNumberFields(array, n);
        }
        else if ((this.status & 0x2) == 0x0) {
            this.overflow = new OverflowInfo();
            n += this.readFirstField(array, n);
            this.readNumberFields(array, n);
        }
        else {
            this.overflow = new OverflowInfo();
            n += this.readOverFlowPage(array, n);
            n += this.readOverFlowId(array, n);
            if (this.hasFirstField()) {
                n += this.readFirstField(array, n);
                this.readNumberFields(array, n);
            }
            else {
                this.numberFields = 0;
            }
        }
        this.handle = null;
    }
    
    public static final int getStoredSizeRecordId(final int n) {
        return CompressedNumber.sizeInt(n);
    }
    
    public String toString() {
        return null;
    }
    
    private static class OverflowInfo
    {
        private int overflowId;
        private long overflowPage;
        private int firstField;
        
        private OverflowInfo() {
        }
        
        private OverflowInfo(final OverflowInfo overflowInfo) {
            this.overflowId = overflowInfo.overflowId;
            this.overflowPage = overflowInfo.overflowPage;
            this.firstField = overflowInfo.firstField;
        }
    }
}
