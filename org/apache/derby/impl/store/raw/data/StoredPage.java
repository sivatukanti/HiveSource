// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.store.access.RowUtil;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.util.StringUtil;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.store.raw.PageTimeStamp;
import org.apache.derby.iapi.services.daemon.Serviceable;
import java.io.ByteArrayInputStream;
import org.apache.derby.iapi.util.ByteArray;
import java.io.ObjectOutput;
import org.apache.derby.iapi.services.io.ErrorObjectInput;
import java.io.EOFException;
import org.apache.derby.iapi.services.io.FormatIdInputStream;
import java.io.DataInput;
import org.apache.derby.iapi.services.io.DataInputUtil;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import java.io.ByteArrayOutputStream;
import org.apache.derby.iapi.services.io.StreamStorable;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import java.io.InputStream;
import org.apache.derby.iapi.services.io.CompressedNumber;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.Page;
import java.io.ObjectInput;
import java.util.Arrays;
import org.apache.derby.iapi.store.raw.RecordHandle;
import org.apache.derby.iapi.store.raw.FetchDescriptor;
import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.cache.Cacheable;
import java.io.DataOutput;
import org.apache.derby.iapi.services.io.FormatIdUtil;
import java.io.IOException;
import org.apache.derby.iapi.store.raw.PageKey;
import java.io.OutputStream;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatIdOutputStream;
import org.apache.derby.iapi.services.io.ArrayOutputStream;
import org.apache.derby.iapi.services.io.ArrayInputStream;
import java.util.zip.CRC32;

public class StoredPage extends CachedPage
{
    public static final int FORMAT_NUMBER = 117;
    protected static final int PAGE_HEADER_OFFSET = 4;
    protected static final int PAGE_HEADER_SIZE = 56;
    protected static final int RECORD_SPACE_OFFSET = 60;
    protected static final int PAGE_VERSION_OFFSET = 6;
    protected static final int SMALL_SLOT_SIZE = 2;
    protected static final int LARGE_SLOT_SIZE = 4;
    protected static final int CHECKSUM_SIZE = 8;
    protected static final int OVERFLOW_POINTER_SIZE = 12;
    protected static final int OVERFLOW_PTR_FIELD_SIZE = 14;
    ByteHolder bh;
    protected static final int COLUMN_NONE = 0;
    protected static final int COLUMN_FIRST = 1;
    protected static final int COLUMN_LONG = 2;
    protected static final int COLUMN_CREATE_NULL = 3;
    private int maxFieldSize;
    private boolean isOverflowPage;
    private int slotsInUse;
    private int nextId;
    private int generation;
    private int prevGeneration;
    private long bipLocation;
    private int deletedRowCount;
    private boolean headerOutOfDate;
    private CRC32 checksum;
    protected int minimumRecordSize;
    private int userRowSize;
    private int slotFieldSize;
    private int slotEntrySize;
    private int slotTableOffsetToFirstEntry;
    private int slotTableOffsetToFirstRecordLengthField;
    private int slotTableOffsetToFirstReservedSpaceField;
    protected int totalSpace;
    protected int freeSpace;
    private int firstFreeByte;
    protected int spareSpace;
    private StoredRecordHeader overflowRecordHeader;
    protected ArrayInputStream rawDataIn;
    protected ArrayOutputStream rawDataOut;
    protected FormatIdOutputStream logicalDataOut;
    
    public int getTypeFormatId() {
        return 117;
    }
    
    public StoredPage() {
        this.bh = null;
        this.freeSpace = Integer.MIN_VALUE;
        this.firstFreeByte = Integer.MIN_VALUE;
    }
    
    private StoredRecordHeader getOverFlowRecordHeader() throws StandardException {
        return (this.overflowRecordHeader != null) ? this.overflowRecordHeader : (this.overflowRecordHeader = new StoredRecordHeader());
    }
    
    protected void initialize() {
        super.initialize();
        if (this.rawDataIn == null) {
            this.rawDataIn = new ArrayInputStream();
            this.checksum = new CRC32();
        }
        if (this.pageData != null) {
            this.rawDataIn.setData(this.pageData);
        }
    }
    
    private void createOutStreams() {
        (this.rawDataOut = new ArrayOutputStream()).setData(this.pageData);
        this.logicalDataOut = new FormatIdOutputStream(this.rawDataOut);
    }
    
    private void setOutputStream(final OutputStream output) {
        if (this.rawDataOut == null) {
            this.createOutStreams();
        }
        this.logicalDataOut.setOutput(output);
    }
    
    private void resetOutputStream() {
        this.logicalDataOut.setOutput(this.rawDataOut);
    }
    
    protected void usePageBuffer(final byte[] pageData) {
        this.pageData = pageData;
        final int length = this.pageData.length;
        if (this.rawDataIn != null) {
            this.rawDataIn.setData(this.pageData);
        }
        this.slotFieldSize = this.calculateSlotFieldSize(length);
        this.slotEntrySize = 3 * this.slotFieldSize;
        this.initSpace();
        this.slotTableOffsetToFirstEntry = length - 8 - this.slotEntrySize;
        this.slotTableOffsetToFirstRecordLengthField = this.slotTableOffsetToFirstEntry + this.slotFieldSize;
        this.slotTableOffsetToFirstReservedSpaceField = this.slotTableOffsetToFirstEntry + 2 * this.slotFieldSize;
        if (this.rawDataOut != null) {
            this.rawDataOut.setData(this.pageData);
        }
    }
    
    private int calculateSlotFieldSize(final int n) {
        if (n < 65536) {
            return 2;
        }
        return 4;
    }
    
    protected void createPage(final PageKey pageKey, final PageCreationArgs pageCreationArgs) throws StandardException {
        this.spareSpace = pageCreationArgs.spareSpace;
        this.minimumRecordSize = pageCreationArgs.minimumRecordSize;
        this.setPageArray(pageCreationArgs.pageSize);
        this.cleanPage();
        this.setPageVersion(0L);
        this.nextId = 6;
        this.generation = 0;
        this.prevGeneration = 0;
        this.bipLocation = 0L;
        this.createOutStreams();
    }
    
    protected void initFromData(final FileContainer fileContainer, final PageKey pageKey) throws StandardException {
        if (fileContainer != null) {
            this.spareSpace = fileContainer.getSpareSpace();
            this.minimumRecordSize = fileContainer.getMinimumRecordSize();
        }
        try {
            this.validateChecksum(pageKey);
        }
        catch (StandardException ex) {
            if (ex.getMessageId().equals("XSDG2.D")) {
                final int pageSize = this.getPageSize();
                final byte[] pageData = this.pageData;
                this.pageData = null;
                this.setPageArray(pageSize);
                try {
                    fileContainer.readPage(pageKey.getPageNumber(), this.pageData);
                }
                catch (IOException ex2) {
                    throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex2, pageKey));
                }
                try {
                    this.validateChecksum(pageKey);
                }
                catch (StandardException ex4) {
                    throw this.dataFactory.markCorrupt(ex);
                }
                throw StandardException.newException("XSDFD.S", ex, pageKey, pagedataToHexDump(pageData), pagedataToHexDump(pageData));
            }
            throw ex;
        }
        try {
            this.readPageHeader();
            this.initSlotTable(pageKey);
        }
        catch (IOException ex3) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex3, pageKey));
        }
    }
    
    protected void validateChecksum(final PageKey pageKey) throws StandardException {
        long long1;
        try {
            this.rawDataIn.setPosition(this.getPageSize() - 8);
            long1 = this.rawDataIn.readLong();
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, pageKey));
        }
        this.checksum.reset();
        this.checksum.update(this.pageData, 0, this.getPageSize() - 8);
        if (long1 != this.checksum.getValue()) {
            final CRC32 checksum = new CRC32();
            checksum.reset();
            checksum.update(this.pageData, 0, this.getPageSize() - 8);
            if (long1 != checksum.getValue()) {
                throw StandardException.newException("XSDG2.D", pageKey, new Long(this.checksum.getValue()), new Long(long1), pagedataToHexDump(this.pageData));
            }
            this.checksum = checksum;
        }
    }
    
    protected void updateChecksum() throws IOException {
        this.checksum.reset();
        this.checksum.update(this.pageData, 0, this.getPageSize() - 8);
        this.rawDataOut.setPosition(this.getPageSize() - 8);
        this.logicalDataOut.writeLong(this.checksum.getValue());
    }
    
    protected void writePage(final PageKey pageKey) throws StandardException {
        try {
            if (this.headerOutOfDate) {
                this.updatePageHeader();
            }
            else {
                this.updatePageVersion();
            }
            this.updateChecksum();
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, pageKey));
        }
    }
    
    protected void writeFormatId(final PageKey pageKey) throws StandardException {
        try {
            if (this.rawDataOut == null) {
                this.createOutStreams();
            }
            this.rawDataOut.setPosition(0);
            FormatIdUtil.writeFormatIdInteger(this.logicalDataOut, this.getTypeFormatId());
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, pageKey));
        }
    }
    
    protected void releaseExclusive() {
        super.releaseExclusive();
        this.pageCache.release(this);
    }
    
    public int getTotalSpace(final int n) throws StandardException {
        try {
            this.rawDataIn.setPosition(this.getSlotOffset(n) + this.slotFieldSize);
            return (this.slotFieldSize == 2) ? (this.rawDataIn.readUnsignedShort() + this.rawDataIn.readUnsignedShort()) : (this.rawDataIn.readInt() + this.rawDataIn.readInt());
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
        }
    }
    
    public boolean spaceForInsert() throws StandardException {
        return this.slotsInUse == 0 || (this.allowInsert() && (this.totalSpace - this.freeSpace) / this.slotsInUse <= this.freeSpace);
    }
    
    public boolean spaceForInsert(final Object[] array, final FormatableBitSet set, final int n) throws StandardException {
        if (this.slotsInUse == 0) {
            return true;
        }
        if (!this.allowInsert()) {
            return false;
        }
        final DynamicByteArrayOutputStream dynamicByteArrayOutputStream = new DynamicByteArrayOutputStream();
        try {
            this.logRow(0, true, this.nextId, array, set, dynamicByteArrayOutputStream, 0, (byte)1, -1, -1, n);
        }
        catch (NoSpaceOnPage noSpaceOnPage) {
            return false;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
        return true;
    }
    
    private boolean spaceForInsert(final Object[] array, final FormatableBitSet set, final int n, final int n2, final int n3) throws StandardException {
        if (!this.spaceForInsert() || this.freeSpace < n) {
            return false;
        }
        final DynamicByteArrayOutputStream dynamicByteArrayOutputStream = new DynamicByteArrayOutputStream();
        try {
            this.logRow(0, true, this.nextId, array, set, dynamicByteArrayOutputStream, n2, (byte)1, -1, -1, n3);
        }
        catch (NoSpaceOnPage noSpaceOnPage) {
            return false;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
        return true;
    }
    
    public boolean unfilled() {
        return this.allowInsert() && this.freeSpace > this.getPageSize() / 2;
    }
    
    public boolean allowInsert() {
        if (this.slotsInUse == 0) {
            return true;
        }
        final int n = this.freeSpace - this.slotEntrySize;
        return n >= this.minimumRecordSize && n >= 17 && n * 100 / this.totalSpace >= this.spareSpace;
    }
    
    public boolean spaceForCopy(final int n, final int[] array) {
        int n2 = this.slotEntrySize * n;
        for (int i = 0; i < n; ++i) {
            if (array[i] > 0) {
                n2 += ((array[i] >= this.minimumRecordSize) ? array[i] : this.minimumRecordSize);
            }
        }
        return this.freeSpace - n2 >= 0;
    }
    
    protected boolean spaceForCopy(int n, final int n2) {
        n = n - StoredRecordHeader.getStoredSizeRecordId(n2) + StoredRecordHeader.getStoredSizeRecordId(this.nextId);
        return this.freeSpace - (this.slotEntrySize + ((n >= this.minimumRecordSize) ? n : this.minimumRecordSize)) >= 0;
    }
    
    protected boolean restoreRecordFromSlot(final int n, final Object[] array, final FetchDescriptor fetchDescriptor, final RecordHandle recordHandle, StoredRecordHeader restoreLongRecordFromSlot, final boolean b) throws StandardException {
        try {
            final int n2 = this.getRecordOffset(n) + restoreLongRecordFromSlot.size();
            final ArrayInputStream rawDataIn = this.rawDataIn;
            rawDataIn.setPosition(n2);
            if (!restoreLongRecordFromSlot.hasOverflow()) {
                if (b && fetchDescriptor != null && fetchDescriptor.getQualifierList() != null) {
                    fetchDescriptor.reset();
                    if (!this.qualifyRecordFromSlot(array, n2, fetchDescriptor, restoreLongRecordFromSlot, recordHandle)) {
                        return false;
                    }
                    rawDataIn.setPosition(n2);
                }
                if (fetchDescriptor != null) {
                    this.readRecordFromArray(array, (fetchDescriptor.getValidColumns() == null) ? (array.length - 1) : fetchDescriptor.getMaxFetchColumnId(), fetchDescriptor.getValidColumnsArray(), fetchDescriptor.getMaterializedColumns(), rawDataIn, restoreLongRecordFromSlot, recordHandle);
                }
                else {
                    this.readRecordFromArray(array, array.length - 1, null, null, rawDataIn, restoreLongRecordFromSlot, recordHandle);
                }
                return true;
            }
            if (fetchDescriptor != null) {
                if (fetchDescriptor.getQualifierList() != null) {
                    fetchDescriptor.reset();
                }
                this.readRecordFromArray(array, (fetchDescriptor.getValidColumns() == null) ? (array.length - 1) : fetchDescriptor.getMaxFetchColumnId(), fetchDescriptor.getValidColumnsArray(), fetchDescriptor.getMaterializedColumns(), rawDataIn, restoreLongRecordFromSlot, recordHandle);
            }
            else {
                this.readRecordFromArray(array, array.length - 1, null, null, rawDataIn, restoreLongRecordFromSlot, recordHandle);
            }
            while (restoreLongRecordFromSlot != null) {
                final StoredPage overflowPage = this.getOverflowPage(restoreLongRecordFromSlot.getOverflowPage());
                restoreLongRecordFromSlot = overflowPage.restoreLongRecordFromSlot(array, fetchDescriptor, recordHandle, restoreLongRecordFromSlot);
                overflowPage.unlatch();
            }
            return fetchDescriptor == null || fetchDescriptor.getQualifierList() == null || this.qualifyRecordFromRow(array, fetchDescriptor.getQualifierList());
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
        }
    }
    
    private StoredRecordHeader restoreLongRecordFromSlot(final Object[] array, final FetchDescriptor fetchDescriptor, final RecordHandle recordHandle, final StoredRecordHeader storedRecordHeader) throws StandardException {
        final int recordById = this.findRecordById(storedRecordHeader.getOverflowId(), 0);
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(recordById);
        try {
            final int position = this.getRecordOffset(recordById) + headerAtSlot.size();
            final ArrayInputStream rawDataIn = this.rawDataIn;
            rawDataIn.setPosition(position);
            if (fetchDescriptor != null) {
                if (fetchDescriptor.getQualifierList() != null) {
                    fetchDescriptor.reset();
                }
                this.readRecordFromArray(array, (fetchDescriptor.getValidColumns() == null) ? (array.length - 1) : fetchDescriptor.getMaxFetchColumnId(), fetchDescriptor.getValidColumnsArray(), fetchDescriptor.getMaterializedColumns(), rawDataIn, headerAtSlot, recordHandle);
            }
            else {
                this.readRecordFromArray(array, array.length - 1, null, null, rawDataIn, headerAtSlot, recordHandle);
            }
            return headerAtSlot.hasOverflow() ? headerAtSlot : null;
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
        }
    }
    
    public int newRecordId() {
        return this.nextId;
    }
    
    public int newRecordIdAndBump() {
        this.headerOutOfDate = true;
        return this.nextId++;
    }
    
    protected int newRecordId(final int n) {
        return n + 1;
    }
    
    public boolean isOverflowPage() {
        return this.isOverflowPage;
    }
    
    public final int getPageSize() {
        return this.pageData.length;
    }
    
    protected final void clearSection(final int fromIndex, final int n) {
        Arrays.fill(this.pageData, fromIndex, fromIndex + n, (byte)0);
    }
    
    protected int getMaxFreeSpace() {
        return this.getPageSize() - 60 - 8;
    }
    
    protected int getCurrentFreeSpace() {
        return this.freeSpace;
    }
    
    private void readPageHeader() throws IOException {
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(4);
        this.isOverflowPage = rawDataIn.readBoolean();
        this.setPageStatus(rawDataIn.readByte());
        this.setPageVersion(rawDataIn.readLong());
        this.slotsInUse = rawDataIn.readUnsignedShort();
        this.nextId = rawDataIn.readInt();
        this.generation = rawDataIn.readInt();
        this.prevGeneration = rawDataIn.readInt();
        this.bipLocation = rawDataIn.readLong();
        this.deletedRowCount = rawDataIn.readUnsignedShort() - 1;
        final long n = rawDataIn.readUnsignedShort();
        final long n2 = rawDataIn.readInt();
        rawDataIn.readLong();
        rawDataIn.readLong();
    }
    
    private void updatePageHeader() throws IOException {
        this.rawDataOut.setPosition(4);
        this.logicalDataOut.writeBoolean(this.isOverflowPage);
        this.logicalDataOut.writeByte(this.getPageStatus());
        this.logicalDataOut.writeLong(this.getPageVersion());
        this.logicalDataOut.writeShort(this.slotsInUse);
        this.logicalDataOut.writeInt(this.nextId);
        this.logicalDataOut.writeInt(this.generation);
        this.logicalDataOut.writeInt(this.prevGeneration);
        this.logicalDataOut.writeLong(this.bipLocation);
        this.logicalDataOut.writeShort(this.deletedRowCount + 1);
        this.logicalDataOut.writeShort(0);
        this.logicalDataOut.writeInt(this.dataFactory.random());
        this.logicalDataOut.writeLong(0L);
        this.logicalDataOut.writeLong(0L);
        this.headerOutOfDate = false;
    }
    
    private void updatePageVersion() throws IOException {
        this.rawDataOut.setPosition(6);
        this.logicalDataOut.writeLong(this.getPageVersion());
    }
    
    private int getSlotOffset(final int n) {
        return this.slotTableOffsetToFirstEntry - n * this.slotEntrySize;
    }
    
    private int getRecordOffset(final int n) {
        final byte[] pageData = this.pageData;
        int n2 = this.slotTableOffsetToFirstEntry - n * this.slotEntrySize;
        return (this.slotFieldSize == 2) ? ((pageData[n2++] & 0xFF) << 8 | (pageData[n2] & 0xFF)) : ((pageData[n2++] & 0xFF) << 24 | (pageData[n2++] & 0xFF) << 16 | (pageData[n2++] & 0xFF) << 8 | (pageData[n2] & 0xFF));
    }
    
    private void setRecordOffset(final int n, final int n2) throws IOException {
        this.rawDataOut.setPosition(this.getSlotOffset(n));
        if (this.slotFieldSize == 2) {
            this.logicalDataOut.writeShort(n2);
        }
        else {
            this.logicalDataOut.writeInt(n2);
        }
    }
    
    protected int getRecordPortionLength(final int n) throws IOException {
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(this.slotTableOffsetToFirstRecordLengthField - n * this.slotEntrySize);
        return (this.slotFieldSize == 2) ? rawDataIn.readUnsignedShort() : rawDataIn.readInt();
    }
    
    public int getReservedCount(final int n) throws IOException {
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(this.slotTableOffsetToFirstReservedSpaceField - n * this.slotEntrySize);
        return (this.slotFieldSize == 2) ? rawDataIn.readUnsignedShort() : rawDataIn.readInt();
    }
    
    private void updateRecordPortionLength(final int n, final int n2, final int n3) throws IOException {
        this.rawDataOut.setPosition(this.slotTableOffsetToFirstRecordLengthField - n * this.slotEntrySize);
        if (this.slotFieldSize == 2) {
            this.logicalDataOut.writeShort(this.getRecordPortionLength(n) + n2);
        }
        else {
            this.logicalDataOut.writeInt(this.getRecordPortionLength(n) + n2);
        }
        if (n3 != 0) {
            if (this.slotFieldSize == 2) {
                this.logicalDataOut.writeShort(this.getReservedCount(n) + n3);
            }
            else {
                this.logicalDataOut.writeInt(this.getReservedCount(n) + n3);
            }
        }
    }
    
    private void initSlotTable(final PageKey pageKey) throws StandardException {
        final int slotsInUse = this.slotsInUse;
        this.initializeHeaders(slotsInUse);
        this.clearAllSpace();
        this.freeSpace -= slotsInUse * this.slotEntrySize;
        int n = -1;
        int n2 = -1;
        try {
            for (int i = 0; i < slotsInUse; ++i) {
                final int recordOffset = this.getRecordOffset(i);
                if (recordOffset < 60 || recordOffset >= this.getPageSize() - 8) {
                    throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", pageKey));
                }
                if (recordOffset > n2) {
                    n2 = recordOffset;
                    n = i;
                }
            }
            this.bumpRecordCount(slotsInUse);
            if (n != -1) {
                this.firstFreeByte = n2 + this.getTotalSpace(n);
                this.freeSpace -= this.firstFreeByte - 60;
            }
            if (this.deletedRowCount == -1) {
                int deletedRowCount = 0;
                for (int slotsInUse2 = this.slotsInUse, j = 0; j < slotsInUse2; ++j) {
                    if (this.isDeletedOnPage(j)) {
                        ++deletedRowCount;
                    }
                }
                this.deletedRowCount = deletedRowCount;
            }
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, pageKey));
        }
    }
    
    private void setSlotEntry(final int n, final int n2, final int n3, final int n4) throws IOException {
        this.rawDataOut.setPosition(this.getSlotOffset(n));
        if (this.slotFieldSize == 2) {
            this.logicalDataOut.writeShort(n2);
            this.logicalDataOut.writeShort(n3);
            this.logicalDataOut.writeShort(n4);
        }
        else {
            this.logicalDataOut.writeInt(n2);
            this.logicalDataOut.writeInt(n3);
            this.logicalDataOut.writeInt(n4);
        }
    }
    
    private void addSlotEntry(final int n, final int n2, final int n3, final int n4) throws IOException {
        if (n < this.slotsInUse) {
            final int slotOffset = this.getSlotOffset(this.slotsInUse - 1);
            System.arraycopy(this.pageData, slotOffset, this.pageData, this.getSlotOffset(this.slotsInUse), this.getSlotOffset(n) + this.slotEntrySize - slotOffset);
        }
        else {
            this.getSlotOffset(n);
        }
        this.freeSpace -= this.slotEntrySize;
        ++this.slotsInUse;
        this.headerOutOfDate = true;
        this.setSlotEntry(n, n2, n3, n4);
    }
    
    private void removeSlotEntry(final int n) throws IOException {
        final int slotOffset = this.getSlotOffset(this.slotsInUse - 1);
        final int slotOffset2 = this.getSlotOffset(this.slotsInUse - 2);
        if (n != this.slotsInUse - 1) {
            System.arraycopy(this.pageData, slotOffset, this.pageData, slotOffset2, this.getSlotOffset(n) - slotOffset);
        }
        this.clearSection(slotOffset, this.slotEntrySize);
        this.freeSpace += this.slotEntrySize;
        --this.slotsInUse;
        this.headerOutOfDate = true;
    }
    
    public StoredRecordHeader recordHeaderOnDemand(final int n) {
        final StoredRecordHeader storedRecordHeader = new StoredRecordHeader(this.pageData, this.getRecordOffset(n));
        this.setHeaderAtSlot(n, storedRecordHeader);
        return storedRecordHeader;
    }
    
    public boolean entireRecordOnPage(final int n) throws StandardException {
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        if (headerAtSlot.hasOverflow()) {
            return false;
        }
        try {
            final int recordOffset = this.getRecordOffset(n);
            final int numberFields = headerAtSlot.getNumberFields();
            final ArrayInputStream rawDataIn = this.rawDataIn;
            rawDataIn.setPosition(recordOffset + headerAtSlot.size());
            for (int i = 0; i < numberFields; ++i) {
                final int status = StoredFieldHeader.readStatus(rawDataIn);
                if (StoredFieldHeader.isOverflow(status)) {
                    return false;
                }
                final int fieldDataLength = StoredFieldHeader.readFieldDataLength(rawDataIn, status, this.slotFieldSize);
                if (fieldDataLength != 0) {
                    rawDataIn.setPosition(rawDataIn.getPosition() + fieldDataLength);
                }
            }
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
        }
        return true;
    }
    
    protected void purgeOverflowAtSlot(final int n, final RecordHandle recordHandle, final boolean b) throws StandardException {
        if (n < 0 || n >= this.slotsInUse) {
            throw StandardException.newException("XSDA1.S");
        }
        this.owner.getActionSet().actionPurge(this.owner.getTransaction(), this, n, 1, new int[] { this.getHeaderAtSlot(n).getId() }, b);
    }
    
    private void purgeOneColumnChain(long pageNumber, int id) throws StandardException {
        StoredPage overflowPage = null;
        boolean b = false;
        try {
            while (pageNumber != -1L) {
                overflowPage = this.getOverflowPage(pageNumber);
                b = false;
                if (overflowPage == null) {
                    break;
                }
                final RecordHandle nextColumnPiece = overflowPage.getNextColumnPiece(0);
                if (overflowPage.recordCount() == 1) {
                    b = true;
                    this.owner.removePage(overflowPage);
                }
                else {
                    overflowPage.unlatch();
                    overflowPage = null;
                }
                if (nextColumnPiece != null) {
                    pageNumber = nextColumnPiece.getPageNumber();
                    id = nextColumnPiece.getId();
                }
                else {
                    pageNumber = -1L;
                }
            }
        }
        finally {
            if (!b && overflowPage != null) {
                overflowPage.unlatch();
            }
        }
    }
    
    private void purgeColumnChains(final RawTransaction rawTransaction, final int n, final RecordHandle recordHandle) throws StandardException {
        try {
            final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
            final int numberFields = headerAtSlot.getNumberFields();
            final ArrayInputStream rawDataIn = this.rawDataIn;
            rawDataIn.setPosition(this.getRecordOffset(n) + headerAtSlot.size());
            for (int i = 0; i < numberFields; ++i) {
                final int status = StoredFieldHeader.readStatus(rawDataIn);
                final int fieldDataLength = StoredFieldHeader.readFieldDataLength(rawDataIn, status, this.slotFieldSize);
                if (!StoredFieldHeader.isOverflow(status)) {
                    if (fieldDataLength != 0) {
                        rawDataIn.setPosition(rawDataIn.getPosition() + fieldDataLength);
                    }
                }
                else {
                    this.purgeOneColumnChain(CompressedNumber.readLong((InputStream)rawDataIn), CompressedNumber.readInt((InputStream)rawDataIn));
                }
            }
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
        }
    }
    
    protected void purgeRowPieces(final RawTransaction rawTransaction, final int n, final RecordHandle recordHandle, final boolean b) throws StandardException {
        this.purgeColumnChains(rawTransaction, n, recordHandle);
        StoredRecordHeader storedRecordHeader = this.getHeaderAtSlot(n);
        while (storedRecordHeader.hasOverflow()) {
            StoredPage overflowPage = this.getOverflowPage(storedRecordHeader.getOverflowPage());
            if (overflowPage == null) {
                break;
            }
            try {
                final int overflowSlot = getOverflowSlot(overflowPage, storedRecordHeader);
                overflowPage.purgeColumnChains(rawTransaction, overflowSlot, recordHandle);
                storedRecordHeader = overflowPage.getHeaderAtSlot(overflowSlot);
                if (overflowSlot == 0 && overflowPage.recordCount() == 1) {
                    try {
                        this.owner.removePage(overflowPage);
                    }
                    finally {
                        overflowPage = null;
                    }
                }
                else {
                    overflowPage.purgeOverflowAtSlot(overflowSlot, recordHandle, b);
                    overflowPage.unlatch();
                    overflowPage = null;
                }
            }
            finally {
                if (overflowPage != null) {
                    overflowPage.unlatch();
                }
            }
        }
    }
    
    void removeOrphanedColumnChain(final ReclaimSpace reclaimSpace, final ContainerHandle containerHandle) throws StandardException {
        final StoredPage storedPage = (StoredPage)containerHandle.getPageNoWait(reclaimSpace.getColumnPageId());
        if (storedPage == null) {
            return;
        }
        final boolean equalTimeStamp = storedPage.equalTimeStamp(reclaimSpace.getPageTimeStamp());
        storedPage.unlatch();
        if (!equalTimeStamp) {
            return;
        }
        final RecordHandle headRowHandle = reclaimSpace.getHeadRowHandle();
        final int recordById = this.findRecordById(headRowHandle.getId(), headRowHandle.getSlotNumberHint());
        if (recordById >= 0) {
            StoredPage overflowPage = this;
            try {
                int columnId;
                StoredRecordHeader storedRecordHeader;
                for (columnId = reclaimSpace.getColumnId(), storedRecordHeader = this.getHeaderAtSlot(recordById); storedRecordHeader.getNumberFields() + storedRecordHeader.getFirstField() <= columnId; storedRecordHeader = overflowPage.getHeaderAtSlot(getOverflowSlot(overflowPage, storedRecordHeader))) {
                    if (overflowPage != this) {
                        overflowPage.unlatch();
                        overflowPage = null;
                    }
                    if (!storedRecordHeader.hasOverflow()) {
                        break;
                    }
                    overflowPage = this.getOverflowPage(storedRecordHeader.getOverflowPage());
                }
                if (storedRecordHeader.getNumberFields() + storedRecordHeader.getFirstField() > columnId && !overflowPage.isColumnOrphaned(storedRecordHeader, columnId, reclaimSpace.getColumnPageId(), reclaimSpace.getColumnRecordId())) {
                    if (overflowPage != this) {
                        overflowPage.unlatch();
                        overflowPage = null;
                    }
                    return;
                }
            }
            catch (IOException ex) {
                throw StandardException.newException("XSDA4.S", ex);
            }
            finally {
                if (overflowPage != this && overflowPage != null) {
                    overflowPage.unlatch();
                }
            }
        }
        this.purgeOneColumnChain(reclaimSpace.getColumnPageId(), reclaimSpace.getColumnRecordId());
    }
    
    private boolean isColumnOrphaned(final StoredRecordHeader storedRecordHeader, final int n, final long n2, final long n3) throws StandardException, IOException {
        final int recordById = this.findRecordById(storedRecordHeader.getId(), 0);
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(this.getRecordOffset(recordById) + storedRecordHeader.size());
        for (int i = storedRecordHeader.getFirstField(); i < n; ++i) {
            this.skipField(rawDataIn);
        }
        final int status = StoredFieldHeader.readStatus(rawDataIn);
        StoredFieldHeader.readFieldDataLength(rawDataIn, status, this.slotFieldSize);
        if (StoredFieldHeader.isOverflow(status)) {
            final long long1 = CompressedNumber.readLong((InputStream)rawDataIn);
            final int int1 = CompressedNumber.readInt((InputStream)rawDataIn);
            if (long1 == n2 && int1 == n3) {
                return false;
            }
        }
        return true;
    }
    
    private RecordHandle getNextColumnPiece(final int n) throws StandardException {
        try {
            final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
            if (headerAtSlot.getNumberFields() != 2) {
                return null;
            }
            final ArrayInputStream rawDataIn = this.rawDataIn;
            rawDataIn.setPosition(this.getRecordOffset(n) + headerAtSlot.size());
            this.skipField(rawDataIn);
            StoredFieldHeader.readFieldDataLength(rawDataIn, StoredFieldHeader.readStatus(rawDataIn), this.slotFieldSize);
            return this.owner.makeRecordHandle(CompressedNumber.readLong((InputStream)rawDataIn), CompressedNumber.readInt((InputStream)rawDataIn));
        }
        catch (IOException ex) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
        }
    }
    
    private void initSpace() {
        this.totalSpace = this.getMaxFreeSpace();
        this.maxFieldSize = this.totalSpace - this.slotEntrySize - 16 - 12;
    }
    
    private void clearAllSpace() {
        this.freeSpace = this.totalSpace;
        this.firstFreeByte = this.getPageSize() - this.totalSpace - 8;
    }
    
    private void compressPage(final int n, final int n2) throws IOException {
        final int n3 = n2 + 1 - n;
        if (n2 + 1 != this.firstFreeByte) {
            System.arraycopy(this.pageData, n2 + 1, this.pageData, n, this.firstFreeByte - n2 - 1);
            for (int i = 0; i < this.slotsInUse; ++i) {
                final int recordOffset = this.getRecordOffset(i);
                if (recordOffset >= n2 + 1) {
                    this.setRecordOffset(i, recordOffset - n3);
                }
            }
        }
        this.freeSpace += n3;
        this.clearSection(this.firstFreeByte -= n3, n3);
    }
    
    protected void expandPage(final int n, final int n2) throws IOException {
        final int n3 = this.firstFreeByte - n;
        if (n3 > 0) {
            System.arraycopy(this.pageData, n, this.pageData, n + n2, n3);
            for (int i = 0; i < this.slotsInUse; ++i) {
                final int recordOffset = this.getRecordOffset(i);
                if (recordOffset >= n) {
                    this.setRecordOffset(i, recordOffset + n2);
                }
            }
        }
        this.freeSpace -= n2;
        this.firstFreeByte += n2;
    }
    
    private void shrinkPage(final int n, final int n2) throws IOException {
        final int n3 = this.firstFreeByte - n;
        if (n3 > 0) {
            System.arraycopy(this.pageData, n, this.pageData, n - n2, n3);
            for (int i = 0; i < this.slotsInUse; ++i) {
                final int recordOffset = this.getRecordOffset(i);
                if (recordOffset >= n) {
                    this.setRecordOffset(i, recordOffset - n2);
                }
            }
        }
        this.freeSpace += n2;
        this.firstFreeByte -= n2;
    }
    
    public int getRecordLength(final int n) throws IOException {
        return this.getRecordPortionLength(n);
    }
    
    protected boolean getIsOverflow(final int n) throws IOException {
        return this.getHeaderAtSlot(n).hasOverflow();
    }
    
    public int logRow(final int n, final boolean b, final int id, final Object[] array, final FormatableBitSet set, final DynamicByteArrayOutputStream outputStream, int firstField, final byte b2, final int n2, final int n3, final int n4) throws StandardException, IOException {
        if (!b && n2 != -1 && n3 == -1) {
            return n2;
        }
        final int freeSpace = this.freeSpace;
        this.setOutputStream(outputStream);
        int beginPosition = outputStream.getPosition();
        this.userRowSize = 0;
        boolean b3 = false;
        int n5;
        if (n2 != -1) {
            n5 = n3;
            beginPosition = outputStream.getBeginPosition();
        }
        else {
            if (!b) {
                n5 = freeSpace + this.getTotalSpace(n);
            }
            else {
                n5 = freeSpace - this.slotEntrySize;
                if (firstField == 0) {
                    b3 = true;
                }
            }
            if (n5 <= 0) {
                throw new NoSpaceOnPage(this.isOverflowPage());
            }
        }
        try {
            if (array == null) {
                return this.logOverflowRecord(n, n5, outputStream);
            }
            int numberFields = 0;
            StoredRecordHeader storedRecordHeader;
            if (b) {
                storedRecordHeader = new StoredRecordHeader();
            }
            else {
                storedRecordHeader = new StoredRecordHeader(this.getHeaderAtSlot(n));
                firstField = storedRecordHeader.getFirstField();
            }
            if (set == null) {
                numberFields = array.length - firstField;
            }
            else {
                for (int i = set.getLength() - 1; i >= firstField; --i) {
                    if (set.isSet(i)) {
                        numberFields = i + 1 - firstField;
                        break;
                    }
                }
            }
            int numberFields2 = -1;
            if (b) {
                storedRecordHeader.setId(id);
                storedRecordHeader.setNumberFields(numberFields);
            }
            else {
                numberFields2 = storedRecordHeader.getNumberFields();
                if (numberFields > numberFields2) {
                    if (storedRecordHeader.hasOverflow()) {
                        numberFields = numberFields2;
                    }
                    else {
                        storedRecordHeader.setNumberFields(numberFields);
                    }
                }
                else if (numberFields < numberFields2) {
                    if (set == null) {
                        storedRecordHeader.setNumberFields(numberFields);
                    }
                    else {
                        numberFields = numberFields2;
                    }
                }
            }
            final int n6 = firstField + numberFields;
            if (n2 >= n6) {
                return -1;
            }
            if ((b2 & 0x1) != 0x1) {
                storedRecordHeader.setFirstField(firstField);
            }
            int n7;
            if ((n7 = n2) == -1) {
                n5 -= storedRecordHeader.write(this.logicalDataOut);
                if (n5 < 0) {
                    throw new NoSpaceOnPage(this.isOverflowPage());
                }
                n7 = firstField;
            }
            boolean b4 = false;
            final int n8 = (set == null) ? 0 : set.getLength();
            if (set != null && !b && set != null && n7 < firstField + numberFields2) {
                this.rawDataIn.setPosition(this.getFieldOffset(n, n7));
                b4 = true;
            }
            int n9 = 0;
            int position = outputStream.getPosition();
            int n10 = firstField;
            if (n5 > 12) {
                position = -1;
            }
            int n11 = 1;
            for (int j = n7; j < n6; ++j) {
                Object o = null;
                boolean b5 = false;
                if (set == null || (n8 > j && set.isSet(j))) {
                    if (j < array.length) {
                        o = array[j];
                    }
                }
                else if (!b) {
                    b5 = true;
                }
                if (n5 > 12) {
                    position = outputStream.getPosition();
                    n10 = j;
                }
                final int n12 = n5;
                if (b5) {
                    if (j < firstField + numberFields2) {
                        final int position2 = this.rawDataIn.getPosition();
                        this.skipField(this.rawDataIn);
                        final int n13 = this.rawDataIn.getPosition() - position2;
                        if (n13 <= n5) {
                            this.logColumn(null, 0, outputStream, Integer.MAX_VALUE, 0, n4);
                            n5 -= n13;
                        }
                    }
                    else {
                        n5 = this.logColumn(null, 0, outputStream, n5, 3, n4);
                    }
                }
                else {
                    if (b4 && j < firstField + numberFields2) {
                        this.skipField(this.rawDataIn);
                    }
                    try {
                        if (o == null) {
                            n5 = this.logColumn(null, 0, outputStream, n5, n11, n4);
                        }
                        else {
                            n5 = this.logColumn(array, j, outputStream, n5, n11, n4);
                        }
                    }
                    catch (LongColumnException ex) {
                        if ((b2 & 0x1) == 0x1) {
                            if (ex.getColumn() instanceof InputStream && array[j] instanceof StreamStorable && (array[j] instanceof InputStream || ((StreamStorable)array[j]).returnStream() != null)) {
                                ((StreamStorable)array[j]).setStream((InputStream)ex.getColumn());
                            }
                            throw new NoSpaceOnPage(this.isOverflowPage());
                        }
                        if ((n5 >= 14 && j == n6 - 1) || (n5 >= 28 && j < n6 - 1)) {
                            outputStream.setBeginPosition(beginPosition);
                            ex.setExceptionInfo(outputStream, j, n5);
                            throw ex;
                        }
                    }
                }
                n9 += n12 - n5;
                final boolean b6 = n4 != 100 && this.isLong(n9, n4);
                int n14;
                if (n12 == n5 || b6) {
                    if ((b2 & 0x1) == 0x1) {
                        throw new NoSpaceOnPage(this.isOverflowPage());
                    }
                    if (b6) {
                        outputStream.setPosition(outputStream.getPosition() - n9);
                    }
                    n14 = j;
                }
                else {
                    n14 = n6;
                }
                if ((n12 == n5 || (b2 & 0x10) == 0x10) && n5 < 12) {
                    if (j == firstField || position < 0) {
                        throw new NoSpaceOnPage(this.isOverflowPage());
                    }
                    outputStream.setPosition(position);
                    n14 = n10;
                }
                if (n14 < n6) {
                    final int numberFields3 = n14 - firstField;
                    final int size = storedRecordHeader.size();
                    storedRecordHeader.setNumberFields(numberFields3);
                    final int size2 = storedRecordHeader.size();
                    final int position3 = outputStream.getPosition();
                    if (size > size2) {
                        final int n15 = size - size2;
                        outputStream.setBeginPosition(beginPosition + n15);
                        outputStream.setPosition(beginPosition + n15);
                    }
                    else if (size2 > size) {
                        outputStream.setPosition(beginPosition);
                    }
                    else {
                        outputStream.setBeginPosition(beginPosition);
                        outputStream.setPosition(beginPosition);
                    }
                    storedRecordHeader.write(this.logicalDataOut);
                    outputStream.setPosition(position3);
                    if (!b && set != null) {
                        this.handleIncompleteLogRow(n, n14, set, outputStream);
                    }
                    return n14;
                }
                n11 = 0;
            }
            outputStream.setBeginPosition(beginPosition);
            firstField = -1;
            if (b3 && n5 < this.minimumRecordSize - this.userRowSize) {
                throw new NoSpaceOnPage(this.isOverflowPage());
            }
        }
        finally {
            this.resetOutputStream();
        }
        return firstField;
    }
    
    private void handleIncompleteLogRow(final int n, final int n2, final FormatableBitSet set, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream) throws StandardException {
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        final int n3 = headerAtSlot.getFirstField() + headerAtSlot.getNumberFields();
        boolean b = false;
        final int size = set.size();
        for (int i = n2; i < n3; ++i) {
            if (size <= i || !set.get(i)) {
                b = true;
                break;
            }
        }
        if (!b) {
            return;
        }
        final Object[] column = new Object[n3 - n2];
        ByteArrayOutputStream byteArrayOutputStream = null;
        for (int j = n2; j < n3; ++j) {
            if (size <= j || !set.get(j)) {
                try {
                    if (byteArrayOutputStream == null) {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                    }
                    else {
                        byteArrayOutputStream.reset();
                    }
                    this.logField(n, j, byteArrayOutputStream);
                    column[j - n2] = new RawField(byteArrayOutputStream.toByteArray());
                }
                catch (IOException ex) {
                    throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", ex, this.getPageId()));
                }
            }
        }
        final LongColumnException ex2 = new LongColumnException();
        ex2.setExceptionInfo(dynamicByteArrayOutputStream, n2, -1);
        ex2.setColumn(column);
        throw ex2;
    }
    
    public void restoreRecordFromStream(final LimitObjectInput limitObjectInput, final Object[] array) throws StandardException, IOException {
        final StoredRecordHeader storedRecordHeader = new StoredRecordHeader();
        storedRecordHeader.read(limitObjectInput);
        this.readRecordFromStream(array, array.length - 1, null, null, limitObjectInput, storedRecordHeader, null);
    }
    
    private boolean qualifyRecordFromRow(final Object[] array, final Qualifier[][] array2) throws StandardException {
        boolean b = true;
        for (int i = 0; i < array2[0].length; ++i) {
            final Qualifier qualifier = array2[0][i];
            b = ((DataValueDescriptor)array[qualifier.getColumnId()]).compare(qualifier.getOperator(), qualifier.getOrderable(), qualifier.getOrderedNulls(), qualifier.getUnknownRV());
            if (qualifier.negateCompareResult()) {
                b = !b;
            }
            if (!b) {
                return false;
            }
        }
        for (int j = 1; j < array2.length; ++j) {
            b = false;
            for (int k = 0; k < array2[j].length; ++k) {
                final Qualifier qualifier2 = array2[j][k];
                qualifier2.getColumnId();
                b = ((DataValueDescriptor)array[qualifier2.getColumnId()]).compare(qualifier2.getOperator(), qualifier2.getOrderable(), qualifier2.getOrderedNulls(), qualifier2.getUnknownRV());
                if (qualifier2.negateCompareResult()) {
                    b = !b;
                }
                if (b) {
                    break;
                }
            }
            if (!b) {
                break;
            }
        }
        return b;
    }
    
    private final void readOneColumnFromPage(final Object[] array, final int i, int n, final StoredRecordHeader storedRecordHeader, final RecordHandle recordHandle) throws StandardException, IOException {
        ErrorObjectInput errorObjectInput = null;
        final ArrayInputStream rawDataIn = this.rawDataIn;
        try {
            final Object o = array[i];
            if (i <= storedRecordHeader.getNumberFields() - 1) {
                for (int j = i; j > 0; --j) {
                    n += StoredFieldHeader.readTotalFieldLength(this.pageData, n);
                }
                final int status = StoredFieldHeader.readStatus(this.pageData, n);
                final int fieldLengthAndSetStreamPosition = StoredFieldHeader.readFieldLengthAndSetStreamPosition(this.pageData, n + 1, status, this.slotFieldSize, rawDataIn);
                if (!StoredFieldHeader.isNonexistent(status)) {
                    final boolean overflow = StoredFieldHeader.isOverflow(status);
                    InputStream inputStream = null;
                    if (overflow) {
                        inputStream = new OverflowInputStream(new MemByteHolder(this.pageData.length), this.owner, CompressedNumber.readLong((InputStream)rawDataIn), CompressedNumber.readInt((InputStream)rawDataIn), recordHandle);
                    }
                    if (o instanceof DataValueDescriptor) {
                        final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)o;
                        if (StoredFieldHeader.isNull(status)) {
                            dataValueDescriptor.restoreToNull();
                        }
                        else if (!overflow) {
                            rawDataIn.setLimit(fieldLengthAndSetStreamPosition);
                            errorObjectInput = rawDataIn;
                            dataValueDescriptor.readExternalFromArray(rawDataIn);
                            errorObjectInput = null;
                            final int clearLimit = rawDataIn.clearLimit();
                            if (clearLimit != 0) {
                                DataInputUtil.skipFully(rawDataIn, clearLimit);
                            }
                        }
                        else {
                            final FormatIdInputStream stream = new FormatIdInputStream(inputStream);
                            if (dataValueDescriptor instanceof StreamStorable) {
                                ((StreamStorable)dataValueDescriptor).setStream(stream);
                            }
                            else {
                                errorObjectInput = stream;
                                dataValueDescriptor.readExternal(stream);
                                errorObjectInput = null;
                            }
                        }
                    }
                    else {
                        if (StoredFieldHeader.isNull(status)) {
                            throw StandardException.newException("XSDA6.S", Integer.toString(i));
                        }
                        rawDataIn.setLimit(fieldLengthAndSetStreamPosition);
                        errorObjectInput = rawDataIn;
                        array[i] = rawDataIn.readObject();
                        errorObjectInput = null;
                        final int clearLimit2 = rawDataIn.clearLimit();
                        if (clearLimit2 != 0) {
                            DataInputUtil.skipFully(rawDataIn, clearLimit2);
                        }
                    }
                }
                else if (o instanceof DataValueDescriptor) {
                    ((DataValueDescriptor)o).restoreToNull();
                }
                else {
                    array[i] = null;
                }
            }
            else if (o instanceof DataValueDescriptor) {
                ((DataValueDescriptor)o).restoreToNull();
            }
            else {
                array[i] = null;
            }
        }
        catch (IOException ex) {
            if (errorObjectInput == null) {
                throw ex;
            }
            rawDataIn.clearLimit();
            if (ex instanceof EOFException) {
                throw StandardException.newException("XSDA7.S", ex, errorObjectInput.getErrorInfo());
            }
            final Exception nestedException = errorObjectInput.getNestedException();
            if (nestedException != null) {
                if (nestedException instanceof InstantiationException) {
                    throw StandardException.newException("XSDAM.S", nestedException, errorObjectInput.getErrorInfo());
                }
                if (nestedException instanceof IllegalAccessException) {
                    throw StandardException.newException("XSDAN.S", nestedException, errorObjectInput.getErrorInfo());
                }
                if (nestedException instanceof StandardException) {
                    throw (StandardException)nestedException;
                }
            }
            throw StandardException.newException("XSDA8.S", ex, errorObjectInput.getErrorInfo());
        }
        catch (ClassNotFoundException ex2) {
            rawDataIn.clearLimit();
            throw StandardException.newException("XSDA9.S", ex2, errorObjectInput.getErrorInfo());
        }
        catch (LinkageError linkageError) {
            if (errorObjectInput != null) {
                rawDataIn.clearLimit();
                throw StandardException.newException("XSDA8.S", linkageError, errorObjectInput.getErrorInfo());
            }
            throw linkageError;
        }
    }
    
    private final boolean qualifyRecordFromSlot(final Object[] array, final int n, final FetchDescriptor fetchDescriptor, final StoredRecordHeader storedRecordHeader, final RecordHandle recordHandle) throws StandardException, IOException {
        boolean b = true;
        final Qualifier[][] qualifierList = fetchDescriptor.getQualifierList();
        final int[] materializedColumns = fetchDescriptor.getMaterializedColumns();
        for (int i = 0; i < qualifierList[0].length; ++i) {
            final Qualifier qualifier = qualifierList[0][i];
            final int columnId = qualifier.getColumnId();
            if (materializedColumns[columnId] == 0) {
                this.readOneColumnFromPage(array, columnId, n, storedRecordHeader, recordHandle);
                materializedColumns[columnId] = n;
            }
            b = ((DataValueDescriptor)array[columnId]).compare(qualifier.getOperator(), qualifier.getOrderable(), qualifier.getOrderedNulls(), qualifier.getUnknownRV());
            if (qualifier.negateCompareResult()) {
                b = !b;
            }
            if (!b) {
                return false;
            }
        }
        for (int j = 1; j < qualifierList.length; ++j) {
            b = false;
            for (int k = 0; k < qualifierList[j].length; ++k) {
                final Qualifier qualifier2 = qualifierList[j][k];
                final int columnId2 = qualifier2.getColumnId();
                if (materializedColumns[columnId2] == 0) {
                    this.readOneColumnFromPage(array, columnId2, n, storedRecordHeader, recordHandle);
                    materializedColumns[columnId2] = n;
                }
                b = ((DataValueDescriptor)array[columnId2]).compare(qualifier2.getOperator(), qualifier2.getOrderable(), qualifier2.getOrderedNulls(), qualifier2.getUnknownRV());
                if (qualifier2.negateCompareResult()) {
                    b = !b;
                }
                if (b) {
                    break;
                }
            }
            if (!b) {
                break;
            }
        }
        return b;
    }
    
    private final boolean readRecordFromStream(final Object[] array, final int n, final int[] array2, final int[] array3, final LimitObjectInput limitObjectInput, final StoredRecordHeader storedRecordHeader, final RecordHandle recordHandle) throws StandardException, IOException {
        ErrorObjectInput errorObjectInput = null;
        try {
            final int numberFields = storedRecordHeader.getNumberFields();
            final int firstField = storedRecordHeader.getFirstField();
            if (firstField > n) {
                return true;
            }
            final int n2 = numberFields + firstField;
            final int n3 = (array2 == null) ? 0 : array2.length;
            for (int i = firstField; i <= n; ++i) {
                if ((array2 != null && (n3 <= i || array2[i] == 0)) || (array3 != null && array3[i] != 0)) {
                    if (i < n2) {
                        this.skipField(limitObjectInput);
                    }
                }
                else if (i >= n2) {
                    final Object o = array[i];
                    if (o instanceof DataValueDescriptor) {
                        ((DataValueDescriptor)o).restoreToNull();
                    }
                    else {
                        array[i] = null;
                    }
                }
                else {
                    final int status = StoredFieldHeader.readStatus(limitObjectInput);
                    final int fieldDataLength = StoredFieldHeader.readFieldDataLength(limitObjectInput, status, this.slotFieldSize);
                    final Object o2 = array[i];
                    InputStream inputStream = null;
                    if (StoredFieldHeader.isNonexistent(status)) {
                        if (o2 instanceof DataValueDescriptor) {
                            ((DataValueDescriptor)o2).restoreToNull();
                        }
                        else {
                            array[i] = null;
                        }
                    }
                    else {
                        final boolean overflow = StoredFieldHeader.isOverflow(status);
                        if (overflow) {
                            inputStream = new OverflowInputStream(new MemByteHolder(this.pageData.length), this.owner, CompressedNumber.readLong((InputStream)limitObjectInput), CompressedNumber.readInt((InputStream)limitObjectInput), recordHandle);
                        }
                        if (o2 instanceof DataValueDescriptor) {
                            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)o2;
                            if (StoredFieldHeader.isNull(status)) {
                                dataValueDescriptor.restoreToNull();
                            }
                            else if (!overflow) {
                                limitObjectInput.setLimit(fieldDataLength);
                                errorObjectInput = limitObjectInput;
                                dataValueDescriptor.readExternal(limitObjectInput);
                                errorObjectInput = null;
                                final int clearLimit = limitObjectInput.clearLimit();
                                if (clearLimit != 0) {
                                    DataInputUtil.skipFully(limitObjectInput, clearLimit);
                                }
                            }
                            else {
                                final FormatIdInputStream stream = new FormatIdInputStream(inputStream);
                                boolean b = true;
                                if (!(dataValueDescriptor instanceof StreamStorable)) {
                                    b = false;
                                }
                                if (b) {
                                    ((StreamStorable)dataValueDescriptor).setStream(stream);
                                }
                                else {
                                    errorObjectInput = stream;
                                    dataValueDescriptor.readExternal(stream);
                                    errorObjectInput = null;
                                }
                            }
                        }
                        else {
                            if (StoredFieldHeader.isNull(status)) {
                                throw StandardException.newException("XSDA6.S", Integer.toString(i));
                            }
                            limitObjectInput.setLimit(fieldDataLength);
                            errorObjectInput = limitObjectInput;
                            array[i] = limitObjectInput.readObject();
                            errorObjectInput = null;
                            final int clearLimit2 = limitObjectInput.clearLimit();
                            if (clearLimit2 != 0) {
                                DataInputUtil.skipFully(limitObjectInput, clearLimit2);
                            }
                        }
                    }
                }
            }
            return numberFields + firstField > n;
        }
        catch (IOException ex) {
            if (errorObjectInput == null) {
                throw ex;
            }
            limitObjectInput.clearLimit();
            if (ex instanceof EOFException) {
                throw StandardException.newException("XSDA7.S", ex, errorObjectInput.getErrorInfo());
            }
            final Exception nestedException = errorObjectInput.getNestedException();
            if (nestedException != null) {
                if (nestedException instanceof InstantiationException) {
                    throw StandardException.newException("XSDAM.S", nestedException, errorObjectInput.getErrorInfo());
                }
                if (nestedException instanceof IllegalAccessException) {
                    throw StandardException.newException("XSDAN.S", nestedException, errorObjectInput.getErrorInfo());
                }
                if (nestedException instanceof StandardException) {
                    throw (StandardException)nestedException;
                }
            }
            throw StandardException.newException("XSDA8.S", ex, errorObjectInput.getErrorInfo());
        }
        catch (ClassNotFoundException ex2) {
            limitObjectInput.clearLimit();
            throw StandardException.newException("XSDA9.S", ex2, errorObjectInput.getErrorInfo());
        }
        catch (LinkageError linkageError) {
            if (errorObjectInput != null) {
                limitObjectInput.clearLimit();
                throw StandardException.newException("XSDA8.S", linkageError, errorObjectInput.getErrorInfo());
            }
            throw linkageError;
        }
    }
    
    private final boolean readRecordFromArray(final Object[] array, final int n, final int[] array2, final int[] array3, final ArrayInputStream arrayInputStream, final StoredRecordHeader storedRecordHeader, final RecordHandle recordHandle) throws StandardException, IOException {
        ErrorObjectInput errorObjectInput = null;
        try {
            final int numberFields = storedRecordHeader.getNumberFields();
            final int firstField = storedRecordHeader.getFirstField();
            if (firstField > n) {
                return true;
            }
            final int n2 = numberFields + firstField;
            final int n3 = (array2 == null) ? 0 : array2.length;
            int n4 = arrayInputStream.getPosition();
            for (int i = firstField; i <= n; ++i) {
                if ((array2 != null && (n3 <= i || array2[i] == 0)) || (array3 != null && array3[i] != 0)) {
                    if (i < n2) {
                        n4 += StoredFieldHeader.readTotalFieldLength(this.pageData, n4);
                    }
                }
                else if (i < n2) {
                    final int status = StoredFieldHeader.readStatus(this.pageData, n4);
                    final int fieldLengthAndSetStreamPosition = StoredFieldHeader.readFieldLengthAndSetStreamPosition(this.pageData, n4 + 1, status, this.slotFieldSize, arrayInputStream);
                    final Object o = array[i];
                    InputStream inputStream = null;
                    if ((status & 0x5) != 0x5) {
                        final boolean b = (status & 0x2) != 0x0;
                        if (b) {
                            inputStream = new OverflowInputStream(new MemByteHolder(this.pageData.length), this.owner, CompressedNumber.readLong((InputStream)arrayInputStream), CompressedNumber.readInt((InputStream)arrayInputStream), recordHandle);
                        }
                        if (o instanceof DataValueDescriptor) {
                            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)o;
                            if ((status & 0x1) == 0x0) {
                                if (!b) {
                                    arrayInputStream.setLimit(fieldLengthAndSetStreamPosition);
                                    errorObjectInput = arrayInputStream;
                                    dataValueDescriptor.readExternalFromArray(arrayInputStream);
                                    errorObjectInput = null;
                                    final int clearLimit = arrayInputStream.clearLimit();
                                    if (clearLimit != 0) {
                                        DataInputUtil.skipFully(arrayInputStream, clearLimit);
                                    }
                                }
                                else {
                                    final FormatIdInputStream stream = new FormatIdInputStream(inputStream);
                                    boolean b2 = true;
                                    if (!(dataValueDescriptor instanceof StreamStorable)) {
                                        b2 = false;
                                    }
                                    if (b2) {
                                        ((StreamStorable)dataValueDescriptor).setStream(stream);
                                    }
                                    else {
                                        errorObjectInput = stream;
                                        dataValueDescriptor.readExternal(stream);
                                        errorObjectInput = null;
                                    }
                                }
                            }
                            else {
                                dataValueDescriptor.restoreToNull();
                            }
                        }
                        else {
                            if (StoredFieldHeader.isNull(status)) {
                                throw StandardException.newException("XSDA6.S", Integer.toString(i));
                            }
                            arrayInputStream.setLimit(fieldLengthAndSetStreamPosition);
                            errorObjectInput = arrayInputStream;
                            array[i] = arrayInputStream.readObject();
                            errorObjectInput = null;
                            final int clearLimit2 = arrayInputStream.clearLimit();
                            if (clearLimit2 != 0) {
                                DataInputUtil.skipFully(arrayInputStream, clearLimit2);
                            }
                        }
                    }
                    else if (o instanceof DataValueDescriptor) {
                        ((DataValueDescriptor)o).restoreToNull();
                    }
                    else {
                        array[i] = null;
                    }
                    n4 = arrayInputStream.getPosition();
                }
                else {
                    final Object o2 = array[i];
                    if (o2 instanceof DataValueDescriptor) {
                        ((DataValueDescriptor)o2).restoreToNull();
                    }
                    else {
                        array[i] = null;
                    }
                }
            }
            return numberFields + firstField > n;
        }
        catch (IOException ex) {
            if (errorObjectInput == null) {
                throw ex;
            }
            arrayInputStream.clearLimit();
            if (ex instanceof EOFException) {
                throw StandardException.newException("XSDA7.S", ex, errorObjectInput.getErrorInfo());
            }
            final Exception nestedException = errorObjectInput.getNestedException();
            if (nestedException != null) {
                if (nestedException instanceof InstantiationException) {
                    throw StandardException.newException("XSDAM.S", nestedException, errorObjectInput.getErrorInfo());
                }
                if (nestedException instanceof IllegalAccessException) {
                    throw StandardException.newException("XSDAN.S", nestedException, errorObjectInput.getErrorInfo());
                }
                if (nestedException instanceof StandardException) {
                    throw (StandardException)nestedException;
                }
            }
            throw StandardException.newException("XSDA8.S", ex, errorObjectInput.getErrorInfo());
        }
        catch (ClassNotFoundException ex2) {
            arrayInputStream.clearLimit();
            throw StandardException.newException("XSDA9.S", ex2, errorObjectInput.getErrorInfo());
        }
        catch (LinkageError linkageError) {
            if (errorObjectInput != null) {
                arrayInputStream.clearLimit();
                throw StandardException.newException("XSDA8.S", linkageError, errorObjectInput.getErrorInfo());
            }
            throw linkageError;
        }
    }
    
    public void restorePortionLongColumn(final OverflowInputStream overflowInputStream) throws StandardException, IOException {
        final int recordById = this.findRecordById(overflowInputStream.getOverflowId(), 0);
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(recordById);
        final int recordOffset = this.getRecordOffset(recordById);
        final int numberFields = headerAtSlot.getNumberFields();
        this.rawDataIn.setPosition(recordOffset + headerAtSlot.size());
        final int fieldDataLength = StoredFieldHeader.readFieldDataLength(this.rawDataIn, StoredFieldHeader.readStatus(this.rawDataIn), this.slotFieldSize);
        final ByteHolder byteHolder = overflowInputStream.getByteHolder();
        byteHolder.write(this.rawDataIn, fieldDataLength);
        overflowInputStream.setByteHolder(byteHolder);
        if (numberFields == 1) {
            overflowInputStream.setOverflowPage(-1L);
            overflowInputStream.setOverflowId(-1);
        }
        else {
            StoredFieldHeader.readFieldDataLength(this.rawDataIn, StoredFieldHeader.readStatus(this.rawDataIn), this.slotFieldSize);
            final long long1 = CompressedNumber.readLong((InputStream)this.rawDataIn);
            final int int1 = CompressedNumber.readInt((InputStream)this.rawDataIn);
            overflowInputStream.setOverflowPage(long1);
            overflowInputStream.setOverflowId(int1);
        }
    }
    
    public void logColumn(final int n, final int n2, final Object o, final DynamicByteArrayOutputStream outputStream, final int n3) throws StandardException, IOException {
        final int freeSpace = this.freeSpace;
        int position = -1;
        final int n4 = freeSpace + this.getReservedCount(n);
        this.rawDataIn.setPosition(this.getFieldOffset(n, n2));
        final int status = StoredFieldHeader.readStatus(this.rawDataIn);
        final int fieldDataLength = StoredFieldHeader.readFieldDataLength(this.rawDataIn, status, this.slotFieldSize);
        final int n5 = n4 + (StoredFieldHeader.size(status, fieldDataLength, this.slotFieldSize) + fieldDataLength);
        try {
            this.setOutputStream(outputStream);
            position = this.rawDataOut.getPosition();
            if (n5 == this.logColumn(new Object[] { o }, 0, outputStream, n5, 0, n3)) {
                throw new NoSpaceOnPage(this.isOverflowPage());
            }
        }
        finally {
            this.rawDataOut.setPosition(position);
            this.resetOutputStream();
        }
    }
    
    public int logLongColumn(final int n, final int n2, final Object o, final DynamicByteArrayOutputStream outputStream) throws StandardException, IOException {
        final int n3 = this.freeSpace - this.slotEntrySize;
        if (n3 <= 0) {
            throw new NoSpaceOnPage(this.isOverflowPage());
        }
        this.setOutputStream(outputStream);
        outputStream.getPosition();
        try {
            final int n4 = n3 - new StoredRecordHeader(n2, 1).write(this.logicalDataOut);
            if (n4 < 0) {
                throw new NoSpaceOnPage(this.isOverflowPage());
            }
            return this.logColumn(new Object[] { o }, 0, outputStream, n4, 2, 100);
        }
        finally {
            this.resetOutputStream();
        }
    }
    
    private int logColumn(final Object[] array, final int n, final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, int n2, final int n3, final int n4) throws StandardException, IOException {
        Object returnStream = (array != null) ? array[n] : null;
        if (returnStream instanceof RawField) {
            final byte[] data = ((RawField)returnStream).getData();
            if (data.length <= n2) {
                dynamicByteArrayOutputStream.write(data);
                n2 -= data.length;
            }
            return n2;
        }
        boolean b = true;
        int nonexistent = StoredFieldHeader.setFixed(StoredFieldHeader.setInitial(), true);
        final int position = dynamicByteArrayOutputStream.getPosition();
        int position2 = 0;
        int length = 0;
        if (returnStream instanceof StreamStorable) {
            final StreamStorable streamStorable = (StreamStorable)returnStream;
            if (streamStorable.returnStream() != null) {
                returnStream = streamStorable.returnStream();
            }
        }
        if (returnStream == null && n3 != 3) {
            nonexistent = StoredFieldHeader.setNonexistent(nonexistent);
            StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
        }
        else if (returnStream instanceof InputStream) {
            int numBytesSaved = 0;
            final int maxDataLength = this.getMaxDataLength(n2, n4);
            RememberBytesInputStream stream;
            if (returnStream instanceof RememberBytesInputStream) {
                stream = (RememberBytesInputStream)returnStream;
                numBytesSaved = stream.numBytesSaved();
            }
            else {
                stream = new RememberBytesInputStream((InputStream)returnStream, new MemByteHolder(this.maxFieldSize + 1));
                if (array[n] instanceof StreamStorable) {
                    ((StreamStorable)array[n]).setStream(stream);
                }
                returnStream = stream;
            }
            if (numBytesSaved < maxDataLength + 1) {
                numBytesSaved += (int)stream.fillBuf(maxDataLength + 1 - numBytesSaved);
            }
            if (numBytesSaved <= maxDataLength) {
                length = numBytesSaved;
                nonexistent = StoredFieldHeader.setFixed(nonexistent, true);
                StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
                stream.putBuf(this.logicalDataOut, length);
            }
            else if (n3 == 2) {
                b = false;
                length = maxDataLength - 12 - 2;
                nonexistent = StoredFieldHeader.setFixed(nonexistent, true);
                StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
                stream.putBuf(this.logicalDataOut, length);
                stream.available();
                stream.shiftToFront();
            }
            else {
                final int n5 = this.maxFieldSize - numBytesSaved + 1;
                if (n5 > 0) {
                    numBytesSaved += (int)stream.fillBuf(n5);
                }
                length = numBytesSaved;
                returnStream = stream;
            }
        }
        else if (n3 == 3) {
            nonexistent = StoredFieldHeader.setNull(nonexistent, true);
            StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
        }
        else if (returnStream instanceof DataValueDescriptor) {
            final DataValueDescriptor dataValueDescriptor = (DataValueDescriptor)returnStream;
            final boolean b2 = n3 == 3 || dataValueDescriptor.isNull();
            if (b2) {
                nonexistent = StoredFieldHeader.setNull(nonexistent, true);
            }
            final int write = StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
            if (!b2) {
                try {
                    position2 = dynamicByteArrayOutputStream.getPosition();
                    dataValueDescriptor.writeExternal(this.logicalDataOut);
                }
                catch (IOException ex) {
                    if (this.logicalDataOut != null) {
                        final Exception nestedException = this.logicalDataOut.getNestedException();
                        if (nestedException != null && nestedException instanceof StandardException) {
                            throw (StandardException)nestedException;
                        }
                    }
                    throw StandardException.newException("XSDAJ.S", ex);
                }
                length = dynamicByteArrayOutputStream.getPosition() - position - write;
            }
        }
        else if (returnStream instanceof RecordHandle) {
            final RecordHandle recordHandle = (RecordHandle)returnStream;
            nonexistent = StoredFieldHeader.setOverflow(nonexistent, true);
            StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
            length = length + CompressedNumber.writeLong(dynamicByteArrayOutputStream, recordHandle.getPageNumber()) + CompressedNumber.writeInt(dynamicByteArrayOutputStream, recordHandle.getId());
        }
        else {
            final int write2 = StoredFieldHeader.write(this.logicalDataOut, nonexistent, length, this.slotFieldSize);
            this.logicalDataOut.writeObject(returnStream);
            length = dynamicByteArrayOutputStream.getPosition() - position - write2;
        }
        final int setFixed = StoredFieldHeader.setFixed(nonexistent, false);
        final int n6 = StoredFieldHeader.size(setFixed, length, this.slotFieldSize) + length;
        this.userRowSize += length;
        final boolean long1 = this.isLong(n6, n4);
        if ((n2 < n6 || long1) && n3 != 2) {
            if (long1) {
                if (!(returnStream instanceof InputStream)) {
                    returnStream = new RememberBytesInputStream(new ByteArrayInputStream(new ByteArray(dynamicByteArrayOutputStream.getByteArray(), position2, length).getArray(), position2, length), new MemByteHolder(length + 1));
                }
                dynamicByteArrayOutputStream.setPosition(position);
                final LongColumnException ex2 = new LongColumnException();
                ex2.setColumn(returnStream);
                throw ex2;
            }
            dynamicByteArrayOutputStream.setPosition(position);
            return n2;
        }
        else {
            dynamicByteArrayOutputStream.setPosition(position);
            dynamicByteArrayOutputStream.setPosition(position + length + StoredFieldHeader.write(dynamicByteArrayOutputStream, StoredFieldHeader.setFixed(setFixed, true), length, this.slotFieldSize));
            n2 -= n6;
            if (n3 != 2) {
                return n2;
            }
            if (b) {
                return -1;
            }
            return 1;
        }
    }
    
    private int logOverflowRecord(final int n, final int n2, final DynamicByteArrayOutputStream outputStream) throws StandardException, IOException {
        this.setOutputStream(outputStream);
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        final StoredRecordHeader overFlowRecordHeader = this.getOverFlowRecordHeader();
        overFlowRecordHeader.setOverflowFields(headerAtSlot);
        final int size = headerAtSlot.size();
        final int size2 = overFlowRecordHeader.size();
        if (size < size2 && n2 < size2 - size) {
            throw new NoSpaceOnPage(this.isOverflowPage());
        }
        overFlowRecordHeader.write(this.logicalDataOut);
        this.logRecordDataPortion(n, 0, headerAtSlot, null, this.logicalDataOut, null);
        return -1;
    }
    
    private int logOverflowField(final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, int n, final long n2, final int n3) throws StandardException, IOException {
        final int setOverflow = StoredFieldHeader.setOverflow(StoredFieldHeader.setInitial(), true);
        final int n4;
        n -= (n4 = CompressedNumber.sizeLong(n2) + CompressedNumber.sizeInt(n3)) + StoredFieldHeader.size(setOverflow, n4, this.slotFieldSize);
        if (n < 0) {
            throw new NoSpaceOnPage(this.isOverflowPage());
        }
        StoredFieldHeader.write(this.logicalDataOut, setOverflow, n4, this.slotFieldSize);
        CompressedNumber.writeLong(dynamicByteArrayOutputStream, n2);
        CompressedNumber.writeInt(dynamicByteArrayOutputStream, n3);
        return n;
    }
    
    public void logRecord(final int n, final int n2, final int id, final FormatableBitSet set, final OutputStream outputStream, final RecordHandle recordHandle) throws StandardException, IOException {
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        if (id != headerAtSlot.getId()) {
            final StoredRecordHeader storedRecordHeader = new StoredRecordHeader(headerAtSlot);
            storedRecordHeader.setId(id);
            storedRecordHeader.write(outputStream);
        }
        else {
            headerAtSlot.write(outputStream);
        }
        this.logRecordDataPortion(n, n2, headerAtSlot, set, outputStream, recordHandle);
    }
    
    private void logRecordDataPortion(final int n, final int n2, final StoredRecordHeader storedRecordHeader, final FormatableBitSet set, final OutputStream outputStream, final RecordHandle recordHandle) throws StandardException, IOException {
        int n3 = this.getRecordOffset(n) + storedRecordHeader.size();
        final int firstField = storedRecordHeader.getFirstField();
        final int n4 = firstField + storedRecordHeader.getNumberFields();
        final int n5 = (set == null) ? 0 : set.getLength();
        for (int i = firstField; i < n4; ++i) {
            this.rawDataIn.setPosition(n3);
            final int status = StoredFieldHeader.readStatus(this.rawDataIn);
            final int fieldDataLength = StoredFieldHeader.readFieldDataLength(this.rawDataIn, status, this.slotFieldSize);
            if ((set != null && (n5 <= i || !set.isSet(i))) || ((n2 & 0x2) != 0x0 && !StoredFieldHeader.isOverflow(status))) {
                n3 = n3 + StoredFieldHeader.size(status, fieldDataLength, this.slotFieldSize) + fieldDataLength;
                StoredFieldHeader.write(outputStream, StoredFieldHeader.setNonexistent(StoredFieldHeader.setInitial()), 0, this.slotFieldSize);
            }
            else {
                if ((n2 & 0x1) != 0x0 && recordHandle != null && StoredFieldHeader.isOverflow(status) && !this.owner.isTemporaryContainer()) {
                    final int position = this.rawDataIn.getPosition();
                    final long long1 = CompressedNumber.readLong((InputStream)this.rawDataIn);
                    final int int1 = CompressedNumber.readInt((InputStream)this.rawDataIn);
                    final StoredPage overflowPage = this.getOverflowPage(long1);
                    final PageTimeStamp currentTimeStamp = overflowPage.currentTimeStamp();
                    overflowPage.unlatch();
                    final RawTransaction transaction = this.owner.getTransaction();
                    transaction.addPostCommitWork(new ReclaimSpace(4, recordHandle, i, long1, int1, currentTimeStamp, transaction.getDataFactory(), true));
                    this.rawDataIn.setPosition(position);
                }
                n3 += StoredFieldHeader.write(outputStream, status, fieldDataLength, this.slotFieldSize);
                if (fieldDataLength != 0) {
                    outputStream.write(this.pageData, n3, fieldDataLength);
                    n3 += fieldDataLength;
                }
            }
        }
    }
    
    public void logField(final int n, final int n2, final OutputStream outputStream) throws StandardException, IOException {
        final int fieldOffset = this.getFieldOffset(n, n2);
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(fieldOffset);
        final int status = StoredFieldHeader.readStatus(rawDataIn);
        final int fieldDataLength = StoredFieldHeader.readFieldDataLength(rawDataIn, status, this.slotFieldSize);
        StoredFieldHeader.write(outputStream, status, fieldDataLength, this.slotFieldSize);
        if (fieldDataLength != 0) {
            outputStream.write(this.pageData, rawDataIn.getPosition(), fieldDataLength);
        }
    }
    
    public RecordHandle insertAtSlot(final int n, final Object[] array, final FormatableBitSet set, final LogicalUndo logicalUndo, final byte b, final int n2) throws StandardException {
        try {
            return super.insertAtSlot(n, array, set, logicalUndo, b, n2);
        }
        catch (NoSpaceOnPage noSpaceOnPage) {
            return null;
        }
    }
    
    public RecordHandle updateFieldAtSlot(final int n, final int n2, final Object o, final LogicalUndo logicalUndo) throws StandardException {
        try {
            return super.updateFieldAtSlot(n, n2, o, logicalUndo);
        }
        catch (NoSpaceOnPage noSpaceOnPage) {
            if (this.slotsInUse == 1) {
                throw StandardException.newException("XSDA3.S");
            }
            throw StandardException.newException("XSDA3.S");
        }
    }
    
    public int fetchNumFieldsAtSlot(final int n) throws StandardException {
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        if (!headerAtSlot.hasOverflow()) {
            return super.fetchNumFieldsAtSlot(n);
        }
        final StoredPage overflowPage = this.getOverflowPage(headerAtSlot.getOverflowPage());
        final int fetchNumFieldsAtSlot = overflowPage.fetchNumFieldsAtSlot(getOverflowSlot(overflowPage, headerAtSlot));
        overflowPage.unlatch();
        return fetchNumFieldsAtSlot;
    }
    
    public int moveRecordForCompressAtSlot(final int n, final Object[] array, final RecordHandle[] array2, final RecordHandle[] array3) throws StandardException {
        final long pageNumber = this.getPageNumber();
        try {
            this.fetchFromSlot(null, n, array, null, false);
            final int recordPortionLength = this.getRecordPortionLength(n);
            final int id = this.getHeaderAtSlot(n).getId();
            StoredPage storedPage = (StoredPage)this.owner.getPageForCompress(0, pageNumber);
            if (storedPage != null && (storedPage.getPageNumber() >= this.getPageNumber() || !storedPage.spaceForCopy(recordPortionLength, id))) {
                storedPage.unlatch();
                storedPage = null;
            }
            if (storedPage == null) {
                storedPage = (StoredPage)this.owner.getPageForCompress(1, pageNumber);
                if (storedPage != null && (storedPage.getPageNumber() >= this.getPageNumber() || !storedPage.spaceForCopy(recordPortionLength, id))) {
                    storedPage.unlatch();
                    storedPage = null;
                }
            }
            if (storedPage == null) {
                storedPage = (StoredPage)this.owner.addPage();
                if (storedPage.getPageNumber() >= this.getPageNumber() || !storedPage.spaceForCopy(recordPortionLength, id)) {
                    this.owner.removePage(storedPage);
                    storedPage = null;
                }
            }
            if (storedPage != null) {
                final int recordCount = storedPage.recordCount();
                array2[0] = this.getRecordHandleAtSlot(n);
                this.copyAndPurge(storedPage, n, 1, recordCount);
                array3[0] = storedPage.getRecordHandleAtSlot(recordCount);
                storedPage.unlatch();
                return 1;
            }
            return 0;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
    
    public void logAction(final LogInstant logInstant) throws StandardException {
        if (this.rawDataOut == null) {
            this.createOutStreams();
        }
        if (!this.isActuallyDirty()) {
            if (!this.isOverflowPage() && (this.getPageStatus() & 0x1) != 0x0) {
                this.initialRowCount = this.internalNonDeletedRecordCount();
            }
            else {
                this.initialRowCount = 0;
            }
        }
        this.setDirty();
        this.bumpPageVersion();
        this.updateLastLogInstant(logInstant);
    }
    
    private void cleanPage() {
        this.setDirty();
        this.clearSection(0, this.getPageSize());
        this.slotsInUse = 0;
        this.deletedRowCount = 0;
        this.headerOutOfDate = true;
        this.clearAllSpace();
    }
    
    public void initPage(final LogInstant logInstant, final byte pageStatus, final int nextId, final boolean isOverflowPage, final boolean b) throws StandardException {
        this.logAction(logInstant);
        if (b) {
            this.cleanPage();
            super.cleanPageForReuse();
        }
        this.headerOutOfDate = true;
        this.setPageStatus(pageStatus);
        this.isOverflowPage = isOverflowPage;
        this.nextId = nextId;
    }
    
    public void setPageStatus(final LogInstant logInstant, final byte pageStatus) throws StandardException {
        this.logAction(logInstant);
        this.headerOutOfDate = true;
        this.setPageStatus(pageStatus);
    }
    
    public void setReservedSpace(final LogInstant logInstant, final int n, final int n2) throws StandardException, IOException {
        this.logAction(logInstant);
        this.headerOutOfDate = true;
        final int n3 = n2 - this.getReservedCount(n);
        final int n4 = this.getRecordOffset(n) + this.getTotalSpace(n);
        if (n3 > 0) {
            this.expandPage(n4, n3);
        }
        else {
            this.shrinkPage(n4, -n3);
        }
        this.rawDataOut.setPosition(this.getSlotOffset(n) + 2 * this.slotFieldSize);
        if (this.slotFieldSize == 2) {
            this.logicalDataOut.writeShort(n2);
        }
        else {
            this.logicalDataOut.writeInt(n2);
        }
    }
    
    public void storeRecord(final LogInstant logInstant, final int n, final boolean b, final ObjectInput objectInput) throws StandardException, IOException {
        this.logAction(logInstant);
        if (b) {
            this.storeRecordForInsert(n, objectInput);
        }
        else {
            this.storeRecordForUpdate(n, objectInput);
        }
    }
    
    private void storeRecordForInsert(final int n, final ObjectInput objectInput) throws StandardException, IOException {
        StoredRecordHeader shiftUp = this.shiftUp(n);
        if (shiftUp == null) {
            shiftUp = new StoredRecordHeader();
            this.setHeaderAtSlot(n, shiftUp);
        }
        this.bumpRecordCount(1);
        shiftUp.read(objectInput);
        if (shiftUp.isDeleted()) {
            ++this.deletedRowCount;
            this.headerOutOfDate = true;
        }
        if (this.nextId <= shiftUp.getId()) {
            this.nextId = shiftUp.getId() + 1;
        }
        final int firstFreeByte;
        final int n2 = firstFreeByte = this.firstFreeByte;
        final int numberFields = shiftUp.getNumberFields();
        this.rawDataOut.setPosition(firstFreeByte);
        int position = firstFreeByte + shiftUp.write(this.rawDataOut);
        int n3 = 0;
        for (int i = 0; i < numberFields; ++i) {
            final int status = StoredFieldHeader.readStatus(objectInput);
            final int fieldDataLength = StoredFieldHeader.readFieldDataLength(objectInput, status, this.slotFieldSize);
            final int setFixed = StoredFieldHeader.setFixed(status, false);
            this.rawDataOut.setPosition(position);
            position += StoredFieldHeader.write(this.rawDataOut, setFixed, fieldDataLength, this.slotFieldSize);
            if (fieldDataLength != 0) {
                objectInput.readFully(this.pageData, position, fieldDataLength);
                position += fieldDataLength;
                n3 += fieldDataLength;
            }
        }
        final int n4 = position - this.firstFreeByte;
        this.freeSpace -= n4;
        this.firstFreeByte += n4;
        int n5 = 0;
        if (this.minimumRecordSize > 0 && n3 < this.minimumRecordSize) {
            n5 = this.minimumRecordSize - n3;
            this.freeSpace -= n5;
            this.firstFreeByte += n5;
        }
        if (this.isOverflowPage()) {
            final int n6 = 17 - (n4 + n5);
            if (n6 > 0) {
                this.freeSpace -= n6;
                this.firstFreeByte += n6;
                n5 += n6;
            }
        }
        this.addSlotEntry(n, n2, n4, n5);
        if (this.firstFreeByte > this.getSlotOffset(n) || this.freeSpace < 0) {
            throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", this.getPageId()));
        }
    }
    
    private void storeRecordForUpdate(final int n, final ObjectInput objectInput) throws StandardException, IOException {
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        final StoredRecordHeader storedRecordHeader = new StoredRecordHeader();
        storedRecordHeader.read(objectInput);
        final int numberFields = headerAtSlot.getNumberFields();
        final int numberFields2 = storedRecordHeader.getNumberFields();
        final int firstField = headerAtSlot.getFirstField();
        if (numberFields2 < numberFields) {
            final int n2 = this.getRecordOffset(n) + this.getRecordPortionLength(n) - this.getFieldOffset(n, firstField + numberFields2);
            this.updateRecordPortionLength(n, -n2, n2);
        }
        int recordOffset;
        final int n3 = recordOffset = this.getRecordOffset(n);
        final int n4 = ((numberFields2 < numberFields) ? (numberFields2 - 1) : (numberFields - 1)) + firstField;
        DynamicByteArrayOutputStream dynamicByteArrayOutputStream = null;
        this.rawDataOut.setPosition(recordOffset);
        final int size = headerAtSlot.size();
        final int size2 = storedRecordHeader.size();
        int n5 = size;
        if (n4 < firstField) {
            n5 += this.getReservedCount(n);
        }
        if (n5 >= size2) {
            storedRecordHeader.write(this.rawDataOut);
            recordOffset += size2;
            n5 -= size2;
        }
        else {
            dynamicByteArrayOutputStream = new DynamicByteArrayOutputStream(this.getPageSize());
            storedRecordHeader.write(dynamicByteArrayOutputStream);
        }
        int position = n3 + size;
        int n6 = size2 - size;
        final int n7 = firstField + numberFields;
        for (int n8 = firstField + numberFields2, i = firstField; i < n8; ++i) {
            int n9 = 0;
            if (i < n7) {
                this.rawDataIn.setPosition(position);
                final int status = StoredFieldHeader.readStatus(this.rawDataIn);
                final int fieldDataLength = StoredFieldHeader.readFieldDataLength(this.rawDataIn, status, this.slotFieldSize);
                n9 = StoredFieldHeader.size(status, fieldDataLength, this.slotFieldSize) + fieldDataLength;
            }
            final int status2 = StoredFieldHeader.readStatus(objectInput);
            final int fieldDataLength2 = StoredFieldHeader.readFieldDataLength(objectInput, status2, this.slotFieldSize);
            if (StoredFieldHeader.isNonexistent(status2) && i < n7) {
                if (dynamicByteArrayOutputStream == null || dynamicByteArrayOutputStream.getUsed() == 0) {
                    if (recordOffset != position) {
                        System.arraycopy(this.pageData, position, this.pageData, recordOffset, n9);
                    }
                    recordOffset += n9;
                    if (i == n4) {
                        n5 += this.getReservedCount(n);
                    }
                }
                else {
                    final int position2 = dynamicByteArrayOutputStream.getPosition();
                    dynamicByteArrayOutputStream.setPosition(position2 + n9);
                    System.arraycopy(this.pageData, position, dynamicByteArrayOutputStream.getByteArray(), position2, n9);
                    int n10 = n5 + n9;
                    if (i == n4) {
                        n10 += this.getReservedCount(n);
                    }
                    final int moveSavedDataToPage = this.moveSavedDataToPage(dynamicByteArrayOutputStream, n10, recordOffset);
                    recordOffset += moveSavedDataToPage;
                    n5 = n10 - moveSavedDataToPage;
                }
                position += n9;
            }
            else {
                final int setFixed = StoredFieldHeader.setFixed(status2, false);
                final int size3 = StoredFieldHeader.size(setFixed, fieldDataLength2, this.slotFieldSize);
                final int n11 = size3 + fieldDataLength2;
                n6 += n11 - n9;
                n5 += n9;
                position += n9;
                if (i == n4) {
                    n5 += this.getReservedCount(n);
                }
                if (dynamicByteArrayOutputStream != null && dynamicByteArrayOutputStream.getUsed() != 0) {
                    final int moveSavedDataToPage2 = this.moveSavedDataToPage(dynamicByteArrayOutputStream, n5, recordOffset);
                    recordOffset += moveSavedDataToPage2;
                    n5 -= moveSavedDataToPage2;
                }
                if ((dynamicByteArrayOutputStream == null || dynamicByteArrayOutputStream.getUsed() == 0) && n5 >= size3) {
                    this.rawDataOut.setPosition(recordOffset);
                    recordOffset += StoredFieldHeader.write(this.rawDataOut, setFixed, fieldDataLength2, this.slotFieldSize);
                    n5 -= size3;
                    if (fieldDataLength2 != 0) {
                        final int n12 = (n5 >= fieldDataLength2) ? fieldDataLength2 : n5;
                        if (n12 != 0) {
                            objectInput.readFully(this.pageData, recordOffset, n12);
                            recordOffset += n12;
                            n5 -= n12;
                        }
                        final int n13 = fieldDataLength2 - n12;
                        if (n13 != 0) {
                            if (dynamicByteArrayOutputStream == null) {
                                dynamicByteArrayOutputStream = new DynamicByteArrayOutputStream(n11 * 2);
                            }
                            final int position3 = dynamicByteArrayOutputStream.getPosition();
                            dynamicByteArrayOutputStream.setPosition(position3 + n13);
                            objectInput.readFully(dynamicByteArrayOutputStream.getByteArray(), position3, n13);
                        }
                    }
                }
                else {
                    if (dynamicByteArrayOutputStream == null) {
                        dynamicByteArrayOutputStream = new DynamicByteArrayOutputStream(n11 * 2);
                    }
                    StoredFieldHeader.write(dynamicByteArrayOutputStream, setFixed, fieldDataLength2, this.slotFieldSize);
                    if (fieldDataLength2 != 0) {
                        final int position4 = dynamicByteArrayOutputStream.getPosition();
                        dynamicByteArrayOutputStream.setPosition(position4 + fieldDataLength2);
                        objectInput.readFully(dynamicByteArrayOutputStream.getByteArray(), position4, fieldDataLength2);
                    }
                }
            }
        }
        int n16;
        if (dynamicByteArrayOutputStream != null && dynamicByteArrayOutputStream.getUsed() != 0) {
            final int n14 = n3 + this.getTotalSpace(n);
            final int n15 = dynamicByteArrayOutputStream.getUsed() - (n14 - recordOffset);
            if (n15 > this.freeSpace) {
                throw this.dataFactory.markCorrupt(StandardException.newException("XSDB0.D", this.getPageId()));
            }
            this.expandPage(n14, n15);
            this.moveSavedDataToPage(dynamicByteArrayOutputStream, n5 + n15, recordOffset);
            n16 = -1 * this.getReservedCount(n);
        }
        else {
            n16 = -1 * n6;
        }
        this.updateRecordPortionLength(n, n6, n16);
        this.setHeaderAtSlot(n, storedRecordHeader);
    }
    
    private int moveSavedDataToPage(final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final int n, final int n2) {
        if (n > dynamicByteArrayOutputStream.getUsed() / 2) {
            final int n3 = (n <= dynamicByteArrayOutputStream.getUsed()) ? n : dynamicByteArrayOutputStream.getUsed();
            System.arraycopy(dynamicByteArrayOutputStream.getByteArray(), 0, this.pageData, n2, n3);
            dynamicByteArrayOutputStream.discardLeft(n3);
            return n3;
        }
        return 0;
    }
    
    private void createSpaceForUpdate(final int n, final int n2, final int n3, final int n4) throws StandardException, IOException {
        if (n4 > n3) {
            final int n5 = n4 - n3;
            final int reservedCount = this.getReservedCount(n);
            final int n6 = n5 - reservedCount;
            int n7;
            if (n6 > 0) {
                this.expandPage(this.getRecordOffset(n) + this.getTotalSpace(n), n6);
                n7 = -reservedCount;
            }
            else {
                n7 = -n5;
            }
            this.shiftRemainingData(n, n2, n3, n4);
            this.updateRecordPortionLength(n, n5, n7);
            return;
        }
        final int n8 = n3 - n4;
        if (n8 == 0) {
            return;
        }
        this.clearSection(n2 + n4 + this.shiftRemainingData(n, n2, n3, n4), n8);
        this.updateRecordPortionLength(n, -n8, n8);
    }
    
    public void storeField(final LogInstant logInstant, final int n, final int n2, final ObjectInput objectInput) throws StandardException, IOException {
        this.logAction(logInstant);
        final int fieldOffset = this.getFieldOffset(n, n2);
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(fieldOffset);
        final int status = StoredFieldHeader.readStatus(rawDataIn);
        final int fieldDataLength = StoredFieldHeader.readFieldDataLength(rawDataIn, status, this.slotFieldSize);
        final int status2 = StoredFieldHeader.readStatus(objectInput);
        final int fieldDataLength2 = StoredFieldHeader.readFieldDataLength(objectInput, status2, this.slotFieldSize);
        final int setFixed = StoredFieldHeader.setFixed(status2, false);
        this.createSpaceForUpdate(n, fieldOffset, StoredFieldHeader.size(status, fieldDataLength, this.slotFieldSize) + fieldDataLength, StoredFieldHeader.size(setFixed, fieldDataLength2, this.slotFieldSize) + fieldDataLength2);
        this.rawDataOut.setPosition(fieldOffset);
        final int n3 = fieldOffset + StoredFieldHeader.write(this.rawDataOut, setFixed, fieldDataLength2, this.slotFieldSize);
        if (fieldDataLength2 != 0) {
            objectInput.readFully(this.pageData, n3, fieldDataLength2);
        }
    }
    
    public void reserveSpaceForSlot(final LogInstant logInstant, final int n, final int n2) throws StandardException, IOException {
        this.logAction(logInstant);
        final int n3 = n2 - this.getReservedCount(n);
        if (n3 <= 0) {
            return;
        }
        if (this.freeSpace < n3) {
            throw new NoSpaceOnPage(this.isOverflowPage());
        }
        final int recordOffset = this.getRecordOffset(n);
        this.expandPage(recordOffset + this.getTotalSpace(n), n3);
        this.setSlotEntry(n, recordOffset, this.getRecordPortionLength(n), n2);
    }
    
    public void skipField(final ObjectInput objectInput) throws IOException {
        final int fieldDataLength = StoredFieldHeader.readFieldDataLength(objectInput, StoredFieldHeader.readStatus(objectInput), this.slotFieldSize);
        if (fieldDataLength != 0) {
            DataInputUtil.skipFully(objectInput, fieldDataLength);
        }
    }
    
    public void skipRecord(final ObjectInput objectInput) throws IOException {
        final StoredRecordHeader storedRecordHeader = new StoredRecordHeader();
        storedRecordHeader.read(objectInput);
        for (int i = storedRecordHeader.getNumberFields(); i > 0; --i) {
            this.skipField(objectInput);
        }
    }
    
    private int shiftRemainingData(final int n, final int n2, final int n3, final int n4) throws IOException {
        final int n5 = this.getRecordOffset(n) + this.getRecordPortionLength(n) - (n2 + n3);
        if (n5 != 0) {
            System.arraycopy(this.pageData, n2 + n3, this.pageData, n2 + n4, n5);
        }
        return n5;
    }
    
    public void setDeleteStatus(final LogInstant logInstant, final int n, final boolean b) throws StandardException, IOException {
        this.logAction(logInstant);
        this.deletedRowCount += super.setDeleteStatus(n, b);
        this.headerOutOfDate = true;
        final int recordOffset = this.getRecordOffset(n);
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        this.rawDataOut.setPosition(recordOffset);
        headerAtSlot.write(this.logicalDataOut);
    }
    
    protected int internalDeletedRecordCount() {
        return this.deletedRowCount;
    }
    
    public void purgeRecord(final LogInstant logInstant, final int n, final int n2) throws StandardException, IOException {
        this.logAction(logInstant);
        if (this.getHeaderAtSlot(n).isDeleted()) {
            --this.deletedRowCount;
        }
        final int recordOffset = this.getRecordOffset(n);
        this.compressPage(recordOffset, recordOffset + this.getTotalSpace(n) - 1);
        this.removeSlotEntry(n);
        this.removeAndShiftDown(n);
    }
    
    private int getFieldOffset(final int n, final int n2) throws IOException {
        final int recordOffset = this.getRecordOffset(n);
        final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n);
        final int firstField = headerAtSlot.getFirstField();
        final ArrayInputStream rawDataIn = this.rawDataIn;
        rawDataIn.setPosition(recordOffset + headerAtSlot.size());
        for (int i = firstField; i < n2; ++i) {
            this.skipField(rawDataIn);
        }
        return this.rawDataIn.getPosition();
    }
    
    public PageTimeStamp currentTimeStamp() {
        return new PageVersion(this.getPageNumber(), this.getPageVersion());
    }
    
    public void setTimeStamp(final PageTimeStamp pageTimeStamp) throws StandardException {
        if (pageTimeStamp == null) {
            throw StandardException.newException("XSDAB.S");
        }
        if (!(pageTimeStamp instanceof PageVersion)) {
            throw StandardException.newException("XSDAA.S", pageTimeStamp);
        }
        final PageVersion pageVersion = (PageVersion)pageTimeStamp;
        pageVersion.setPageNumber(this.getPageNumber());
        pageVersion.setPageVersion(this.getPageVersion());
    }
    
    public boolean equalTimeStamp(final PageTimeStamp pageTimeStamp) throws StandardException {
        if (pageTimeStamp == null) {
            return false;
        }
        if (!(pageTimeStamp instanceof PageVersion)) {
            throw StandardException.newException("XSDAA.S", pageTimeStamp);
        }
        final PageVersion pageVersion = (PageVersion)pageTimeStamp;
        if (pageVersion.getPageNumber() != this.getPageNumber()) {
            throw StandardException.newException("XSDAA.S", pageTimeStamp);
        }
        return pageVersion.getPageVersion() == this.getPageVersion();
    }
    
    public String toString() {
        return null;
    }
    
    public String toUncheckedString() {
        return null;
    }
    
    private static String pagedataToHexDump(final byte[] array) {
        return StringUtil.hexDump(array);
    }
    
    private String pageHeaderToString() {
        return null;
    }
    
    String getPageDumpString() {
        return MessageService.getTextMessage("D016", this.getIdentity(), new Boolean(this.isOverflowPage), new Long(this.getPageVersion()), new Integer(this.slotsInUse), new Integer(this.deletedRowCount), new Integer(this.getPageStatus()), new Integer(this.nextId), new Integer(this.firstFreeByte), new Integer(this.freeSpace), new Integer(this.totalSpace), new Integer(this.spareSpace), new Integer(this.minimumRecordSize), new Integer(this.getPageSize()), pagedataToHexDump(this.pageData));
    }
    
    private String recordToString(final int n) {
        return null;
    }
    
    protected StoredPage getOverflowPage(final long n) throws StandardException {
        final StoredPage storedPage = (StoredPage)this.owner.getPage(n);
        if (storedPage == null) {}
        return storedPage;
    }
    
    protected BasePage getNewOverflowPage() throws StandardException {
        final FileContainer fileContainer = (FileContainer)this.containerCache.find(this.identity.getContainerId());
        try {
            return (BasePage)fileContainer.addPage(this.owner, true);
        }
        finally {
            this.containerCache.release(fileContainer);
        }
    }
    
    protected static int getOverflowSlot(final BasePage basePage, final StoredRecordHeader storedRecordHeader) throws StandardException {
        final int recordById = basePage.findRecordById(storedRecordHeader.getOverflowId(), 0);
        if (recordById < 0) {
            throw StandardException.newException("XSDA1.S");
        }
        return recordById;
    }
    
    public BasePage getOverflowPageForInsert(final int n, final Object[] array, final FormatableBitSet set) throws StandardException {
        return this.getOverflowPageForInsert(n, array, set, 0);
    }
    
    public BasePage getOverflowPageForInsert(final int n, final Object[] array, final FormatableBitSet set, final int n2) throws StandardException {
        final long[] array2 = new long[5];
        int n3 = 0;
        long n4 = 0L;
    Label_0110:
        for (int n5 = 0; n5 < this.slotsInUse && n3 < array2.length; ++n5) {
            final StoredRecordHeader headerAtSlot = this.getHeaderAtSlot(n5);
            if (headerAtSlot.hasOverflow()) {
                final long overflowPage = headerAtSlot.getOverflowPage();
                if (n5 == n) {
                    n4 = overflowPage;
                }
                else {
                    for (int i = 0; i < n3; ++i) {
                        if (array2[i] == overflowPage) {
                            continue Label_0110;
                        }
                    }
                    array2[n3++] = overflowPage;
                }
            }
        }
        for (final long n6 : array2) {
            if (n6 != n4) {
                StoredPage overflowPage2 = null;
                final int n7 = 0;
                try {
                    overflowPage2 = this.getOverflowPage(n6);
                    if (overflowPage2.spaceForInsert(array, set, n7, n2, 100)) {
                        return overflowPage2;
                    }
                    overflowPage2.getCurrentFreeSpace();
                    overflowPage2.unlatch();
                    overflowPage2 = null;
                }
                catch (StandardException ex) {
                    if (overflowPage2 != null) {
                        overflowPage2.unlatch();
                    }
                }
            }
        }
        return this.getNewOverflowPage();
    }
    
    protected void updateOverflowed(final RawTransaction rawTransaction, final int n, final Object[] array, final FormatableBitSet set, final StoredRecordHeader storedRecordHeader) throws StandardException {
        StoredPage overflowPage = this.getOverflowPage(storedRecordHeader.getOverflowPage());
        try {
            overflowPage.doUpdateAtSlot(rawTransaction, getOverflowSlot(overflowPage, storedRecordHeader), storedRecordHeader.getOverflowId(), array, set);
            overflowPage.unlatch();
            overflowPage = null;
        }
        finally {
            if (overflowPage != null) {
                overflowPage.unlatch();
            }
        }
    }
    
    public void updateOverflowDetails(final RecordHandle recordHandle, final RecordHandle recordHandle2) throws StandardException {
        final long pageNumber = recordHandle.getPageNumber();
        if (pageNumber == this.getPageNumber()) {
            this.updateOverflowDetails(this, recordHandle, recordHandle2);
            return;
        }
        final StoredPage storedPage = (StoredPage)this.owner.getPage(pageNumber);
        this.updateOverflowDetails(storedPage, recordHandle, recordHandle2);
        storedPage.unlatch();
    }
    
    private void updateOverflowDetails(final StoredPage storedPage, final RecordHandle recordHandle, final RecordHandle overflowDetails) throws StandardException {
        storedPage.getOverFlowRecordHeader().setOverflowDetails(overflowDetails);
        storedPage.doUpdateAtSlot(this.owner.getTransaction(), storedPage.getSlotNumber(recordHandle), recordHandle.getId(), null, null);
    }
    
    public void updateFieldOverflowDetails(final RecordHandle recordHandle, final RecordHandle recordHandle2) throws StandardException {
        final Object[] array = { null, recordHandle2 };
        final FormatableBitSet set = new FormatableBitSet(2);
        set.set(1);
        this.doUpdateAtSlot(this.owner.getTransaction(), this.getSlotNumber(recordHandle), recordHandle.getId(), array, set);
    }
    
    public int appendOverflowFieldHeader(final DynamicByteArrayOutputStream dynamicByteArrayOutputStream, final RecordHandle recordHandle) throws StandardException, IOException {
        final int setOverflow = StoredFieldHeader.setOverflow(StoredFieldHeader.setInitial(), true);
        final long pageNumber = recordHandle.getPageNumber();
        final int id = recordHandle.getId();
        return StoredFieldHeader.write(dynamicByteArrayOutputStream, setOverflow, CompressedNumber.sizeLong(pageNumber) + CompressedNumber.sizeInt(id), this.slotFieldSize) + CompressedNumber.writeLong(dynamicByteArrayOutputStream, pageNumber) + CompressedNumber.writeInt(dynamicByteArrayOutputStream, id);
    }
    
    protected int getSlotsInUse() {
        return this.slotsInUse;
    }
    
    private int getMaxDataLength(final int n, final int n2) {
        final int n3 = this.totalSpace * n2 / 100;
        int n4;
        if (n < 62) {
            n4 = n - 2;
        }
        else if (n < 16380) {
            n4 = n - 3;
        }
        else {
            n4 = n - 5;
        }
        return (n4 > n3) ? n3 : n4;
    }
    
    private boolean isLong(final int n, final int n2) {
        return n > this.maxFieldSize * n2 / 100;
    }
    
    public void doUpdateAtSlot(final RawTransaction rawTransaction, int recordById, int n, final Object[] array, final FormatableBitSet set) throws StandardException {
        final RecordHandle recordHandle = this.isOverflowPage() ? null : this.getRecordHandleAtSlot(recordById);
        if (array == null) {
            this.owner.getActionSet().actionUpdate(rawTransaction, this, recordById, n, array, set, -1, null, -1, recordHandle);
            return;
        }
        int n2 = RowUtil.nextColumn(array, set, 0);
        if (n2 == -1) {
            return;
        }
        boolean b = false;
        StoredPage storedPage = this;
        while (true) {
            final StoredRecordHeader headerAtSlot = storedPage.getHeaderAtSlot(recordById);
            final int firstField = headerAtSlot.getFirstField();
            final int n3 = firstField + headerAtSlot.getNumberFields();
            long n4 = -1L;
            int nextColumn = -1;
            int n5 = -1;
            if (!headerAtSlot.hasOverflow() || (n2 >= firstField && n2 < n3)) {
                int actionUpdate = -1;
                Object[] array2 = null;
                DynamicByteArrayOutputStream logBuffer = null;
                boolean b2;
                do {
                    try {
                        actionUpdate = this.owner.getActionSet().actionUpdate(rawTransaction, storedPage, recordById, n, array, set, nextColumn, logBuffer, n5, recordHandle);
                        b2 = false;
                    }
                    catch (LongColumnException ex) {
                        if (ex.getRealSpaceOnPage() == -1) {
                            logBuffer = ex.getLogBuffer();
                            array2 = (Object[])ex.getColumn();
                            nextColumn = ex.getNextColumn();
                            n5 = -1;
                            b2 = true;
                        }
                        else {
                            logBuffer = new DynamicByteArrayOutputStream(ex.getLogBuffer());
                            final RecordHandle insertLongColumn = this.insertLongColumn(storedPage, ex, (byte)2);
                            final int n6 = 0;
                            int n7;
                            try {
                                n7 = n6 + this.appendOverflowFieldHeader(logBuffer, insertLongColumn);
                            }
                            catch (IOException ex2) {
                                throw StandardException.newException("XSDA4.S", ex2);
                            }
                            nextColumn = ex.getNextColumn() + 1;
                            n5 = ex.getRealSpaceOnPage() - n7;
                            b2 = true;
                        }
                    }
                    catch (NoSpaceOnPage noSpaceOnPage) {
                        throw StandardException.newException("XSDAP.S", noSpaceOnPage, ((PageKey)storedPage.getIdentity()).toString(), this.getPageDumpString(), new Integer(recordById), new Integer(n), set.toString(), new Integer(nextColumn), new Integer(0), recordHandle);
                    }
                } while (b2);
                final int n8 = (set == null) ? 0 : set.getLength();
                if (actionUpdate != -1) {
                    int length = n3;
                    if (!headerAtSlot.hasOverflow()) {
                        if (set == null) {
                            if (array.length > length) {
                                length = array.length;
                            }
                        }
                        else if (n8 > length) {
                            length = n8;
                        }
                    }
                    final Object[] array3 = new Object[length];
                    final FormatableBitSet set2 = new FormatableBitSet(length);
                    for (int i = actionUpdate; i < length; ++i) {
                        if (set == null || (n8 > i && set.isSet(i))) {
                            set2.set(i);
                            array3[i] = RowUtil.getColumn(array, set, i);
                        }
                        else if (i < n3) {
                            set2.set(i);
                            array3[i] = array2[i - actionUpdate];
                        }
                    }
                    final RecordHandle recordHandleAtSlot = storedPage.getRecordHandleAtSlot(recordById);
                    if (headerAtSlot.hasOverflow()) {
                        n4 = headerAtSlot.getOverflowPage();
                        n = headerAtSlot.getOverflowId();
                        n2 = RowUtil.nextColumn(array, set, n3);
                    }
                    else {
                        n2 = -1;
                        n4 = 0L;
                    }
                    if (!b && recordHandle != null && storedPage != null && !this.owner.isTemporaryContainer()) {
                        b = storedPage.checkRowReservedSpace(recordById);
                    }
                    final BasePage overflowPageForInsert = storedPage.getOverflowPageForInsert(recordById, array3, set2, actionUpdate);
                    if (storedPage != this) {
                        storedPage.unlatch();
                        storedPage = null;
                    }
                    byte value = 8;
                    if (n4 != 0L) {
                        value |= 0x10;
                    }
                    final RecordHandle recordHandle2 = (n4 == 0L) ? null : this.owner.makeRecordHandle(n4, n);
                    RecordHandle insertAllowOverflow;
                    try {
                        insertAllowOverflow = overflowPageForInsert.insertAllowOverflow(0, array3, set2, actionUpdate, value, 100, recordHandle2);
                    }
                    catch (NoSpaceOnPage noSpaceOnPage2) {
                        throw StandardException.newException("XSDAP.S", noSpaceOnPage2, ((PageKey)overflowPageForInsert.getIdentity()).toString(), this.getPageDumpString(), new Integer(recordById), new Integer(n), set2.toString(), new Integer(actionUpdate), new Integer(value), recordHandle2);
                    }
                    if (storedPage == this) {
                        this.updateOverflowDetails(this, recordHandleAtSlot, insertAllowOverflow);
                    }
                    else {
                        this.updateOverflowDetails(recordHandleAtSlot, insertAllowOverflow);
                    }
                    overflowPageForInsert.unlatch();
                }
                else {
                    if (!b && recordHandle != null && storedPage != null && !this.owner.isTemporaryContainer()) {
                        b = storedPage.checkRowReservedSpace(recordById);
                    }
                    n2 = (headerAtSlot.hasOverflow() ? RowUtil.nextColumn(array, set, n3) : -1);
                }
                if (n2 == -1) {
                    break;
                }
            }
            if (n4 == -1L) {
                n4 = headerAtSlot.getOverflowPage();
                n = headerAtSlot.getOverflowId();
            }
            if (storedPage != this && storedPage != null) {
                storedPage.unlatch();
            }
            storedPage = (StoredPage)this.owner.getPage(n4);
            recordById = storedPage.findRecordById(n, 0);
        }
        if (storedPage != this && storedPage != null) {
            storedPage.unlatch();
        }
        if (b) {
            final RawTransaction transaction = this.owner.getTransaction();
            transaction.addPostCommitWork(new ReclaimSpace(3, recordHandle, transaction.getDataFactory(), true));
        }
    }
    
    private boolean checkRowReservedSpace(final int n) throws StandardException {
        boolean b = false;
        try {
            final int reservedCount = this.getReservedCount(n);
            final int n2 = 12;
            if (reservedCount > n2) {
                final int n3 = this.getRecordPortionLength(n) + reservedCount;
                if (this.isOverflowPage()) {
                    if (n3 > 17 + n2) {
                        b = true;
                    }
                }
                else if (n3 > this.minimumRecordSize + n2) {
                    b = true;
                }
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
        return b;
    }
    
    protected void compactRecord(final RawTransaction rawTransaction, final int n, final int n2) throws StandardException {
        if (!this.isOverflowPage()) {
            StoredRecordHeader storedRecordHeader = this.getHeaderAtSlot(n);
            while (storedRecordHeader.hasOverflow()) {
                final StoredPage overflowPage = this.getOverflowPage(storedRecordHeader.getOverflowPage());
                try {
                    final int overflowId = storedRecordHeader.getOverflowId();
                    final int overflowSlot = getOverflowSlot(overflowPage, storedRecordHeader);
                    overflowPage.compactRecord(rawTransaction, overflowSlot, overflowId);
                    storedRecordHeader = overflowPage.getHeaderAtSlot(overflowSlot);
                }
                finally {
                    overflowPage.unlatch();
                }
            }
        }
        final int n3 = 12;
        try {
            final int reservedCount = this.getReservedCount(n);
            if (reservedCount > n3) {
                final int recordPortionLength = this.getRecordPortionLength(n);
                int n4 = reservedCount;
                final int n5 = recordPortionLength + reservedCount;
                if (this.isOverflowPage()) {
                    if (n5 > 17 + n3) {
                        if (recordPortionLength >= 17) {
                            n4 = 0;
                        }
                        else {
                            n4 = 17 - recordPortionLength;
                        }
                    }
                }
                else if (n5 > this.minimumRecordSize + n3) {
                    if (recordPortionLength >= this.minimumRecordSize) {
                        n4 = 0;
                    }
                    else {
                        n4 = this.minimumRecordSize - recordPortionLength;
                    }
                }
                if (n4 < reservedCount) {
                    this.owner.getActionSet().actionShrinkReservedSpace(rawTransaction, this, n, n2, n4, reservedCount);
                }
            }
        }
        catch (IOException ex) {
            throw StandardException.newException("XSDA4.S", ex);
        }
    }
}
