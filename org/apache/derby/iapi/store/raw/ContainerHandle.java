// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.store.access.SpaceInfo;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;

public interface ContainerHandle
{
    public static final int DEFAULT_PAGESIZE = -1;
    public static final int DEFAULT_SPARESPACE = -1;
    public static final int DEFAULT_ASSIGN_ID = 0;
    public static final int MODE_DEFAULT = 0;
    public static final int MODE_UNLOGGED = 1;
    public static final int MODE_CREATE_UNLOGGED = 2;
    public static final int MODE_FORUPDATE = 4;
    public static final int MODE_READONLY = 8;
    public static final int MODE_TRUNCATE_ON_COMMIT = 16;
    public static final int MODE_DROP_ON_COMMIT = 32;
    public static final int MODE_OPEN_FOR_LOCK_ONLY = 64;
    public static final int MODE_LOCK_NOWAIT = 128;
    public static final int MODE_TRUNCATE_ON_ROLLBACK = 256;
    public static final int MODE_FLUSH_ON_COMMIT = 512;
    public static final int MODE_NO_ACTIONS_ON_COMMIT = 1024;
    public static final int MODE_TEMP_IS_KEPT = 2048;
    public static final int MODE_USE_UPDATE_LOCKS = 4096;
    public static final int MODE_SECONDARY_LOCKED = 8192;
    public static final int MODE_BASEROW_INSERT_LOCKED = 16384;
    public static final int TEMPORARY_SEGMENT = -1;
    public static final long FIRST_PAGE_NUMBER = 1L;
    public static final long INVALID_PAGE_NUMBER = -1L;
    public static final int ADD_PAGE_DEFAULT = 1;
    public static final int ADD_PAGE_BULK = 2;
    public static final int GET_PAGE_UNFILLED = 1;
    
    ContainerKey getId();
    
    Object getUniqueId();
    
    boolean isReadOnly();
    
    Page addPage() throws StandardException;
    
    void compressContainer() throws StandardException;
    
    long getReusableRecordIdSequenceNumber() throws StandardException;
    
    Page addPage(final int p0) throws StandardException;
    
    void preAllocate(final int p0);
    
    void removePage(final Page p0) throws StandardException;
    
    Page getPage(final long p0) throws StandardException;
    
    Page getPageNoWait(final long p0) throws StandardException;
    
    Page getUserPageNoWait(final long p0) throws StandardException;
    
    Page getUserPageWait(final long p0) throws StandardException;
    
    Page getFirstPage() throws StandardException;
    
    Page getNextPage(final long p0) throws StandardException;
    
    Page getPageForInsert(final int p0) throws StandardException;
    
    Page getPageForCompress(final int p0, final long p1) throws StandardException;
    
    void getContainerProperties(final Properties p0) throws StandardException;
    
    void close();
    
    long getEstimatedRowCount(final int p0) throws StandardException;
    
    void setEstimatedRowCount(final long p0, final int p1) throws StandardException;
    
    long getEstimatedPageCount(final int p0) throws StandardException;
    
    void flushContainer() throws StandardException;
    
    LockingPolicy getLockingPolicy();
    
    void setLockingPolicy(final LockingPolicy p0);
    
    RecordHandle makeRecordHandle(final long p0, final int p1) throws StandardException;
    
    void compactRecord(final RecordHandle p0) throws StandardException;
    
    boolean isTemporaryContainer() throws StandardException;
    
    SpaceInfo getSpaceInfo() throws StandardException;
    
    void backupContainer(final String p0) throws StandardException;
}
