// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.store.access.conglomerate.LogicalUndo;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.error.StandardException;

public interface Page
{
    public static final int FIRST_SLOT_NUMBER = 0;
    public static final int INVALID_SLOT_NUMBER = -1;
    public static final byte INSERT_INITIAL = 0;
    public static final byte INSERT_DEFAULT = 1;
    public static final byte INSERT_UNDO_WITH_PURGE = 2;
    public static final byte INSERT_CONDITIONAL = 4;
    public static final byte INSERT_OVERFLOW = 8;
    public static final byte INSERT_FOR_SPLIT = 16;
    public static final String DIAG_PAGE_SIZE = "pageSize";
    public static final String DIAG_RESERVED_SPACE = "reserveSpace";
    public static final String DIAG_MINIMUM_REC_SIZE = "minRecSize";
    public static final String DIAG_BYTES_FREE = "bytesFree";
    public static final String DIAG_BYTES_RESERVED = "bytesReserved";
    public static final String DIAG_NUMOVERFLOWED = "numOverFlowed";
    public static final String DIAG_ROWSIZE = "rowSize";
    public static final String DIAG_MINROWSIZE = "minRowSize";
    public static final String DIAG_MAXROWSIZE = "maxRowSize";
    public static final String DIAG_PAGEOVERHEAD = "pageOverhead";
    public static final String DIAG_SLOTTABLE_SIZE = "slotTableSize";
    
    long getPageNumber();
    
    RecordHandle getInvalidRecordHandle();
    
    RecordHandle makeRecordHandle(final int p0) throws StandardException;
    
    RecordHandle getRecordHandle(final int p0);
    
    boolean recordExists(final RecordHandle p0, final boolean p1) throws StandardException;
    
    boolean spaceForInsert() throws StandardException;
    
    boolean spaceForInsert(final Object[] p0, final FormatableBitSet p1, final int p2) throws StandardException;
    
    RecordHandle insert(final Object[] p0, final FormatableBitSet p1, final byte p2, final int p3) throws StandardException;
    
    int moveRecordForCompressAtSlot(final int p0, final Object[] p1, final RecordHandle[] p2, final RecordHandle[] p3) throws StandardException;
    
    int fetchNumFields(final RecordHandle p0) throws StandardException;
    
    int getSlotNumber(final RecordHandle p0) throws StandardException;
    
    RecordHandle getRecordHandleAtSlot(final int p0) throws StandardException;
    
    int getNextSlotNumber(final RecordHandle p0) throws StandardException;
    
    RecordHandle insertAtSlot(final int p0, final Object[] p1, final FormatableBitSet p2, final LogicalUndo p3, final byte p4, final int p5) throws StandardException;
    
    RecordHandle fetchFromSlot(final RecordHandle p0, final int p1, final Object[] p2, final FetchDescriptor p3, final boolean p4) throws StandardException;
    
    RecordHandle fetchFieldFromSlot(final int p0, final int p1, final Object p2) throws StandardException;
    
    boolean isDeletedAtSlot(final int p0) throws StandardException;
    
    RecordHandle updateFieldAtSlot(final int p0, final int p1, final Object p2, final LogicalUndo p3) throws StandardException;
    
    int fetchNumFieldsAtSlot(final int p0) throws StandardException;
    
    RecordHandle deleteAtSlot(final int p0, final boolean p1, final LogicalUndo p2) throws StandardException;
    
    void purgeAtSlot(final int p0, final int p1, final boolean p2) throws StandardException;
    
    void copyAndPurge(final Page p0, final int p1, final int p2, final int p3) throws StandardException;
    
    RecordHandle updateAtSlot(final int p0, final Object[] p1, final FormatableBitSet p2) throws StandardException;
    
    void unlatch();
    
    int recordCount() throws StandardException;
    
    int nonDeletedRecordCount() throws StandardException;
    
    boolean shouldReclaimSpace(final int p0, final int p1) throws StandardException;
    
    void setAuxObject(final AuxObject p0);
    
    AuxObject getAuxObject();
    
    void setRepositionNeeded();
    
    boolean isRepositionNeeded(final long p0);
    
    long getPageVersion();
    
    void setTimeStamp(final PageTimeStamp p0) throws StandardException;
    
    PageTimeStamp currentTimeStamp();
    
    boolean equalTimeStamp(final PageTimeStamp p0) throws StandardException;
    
    boolean isLatched();
}
