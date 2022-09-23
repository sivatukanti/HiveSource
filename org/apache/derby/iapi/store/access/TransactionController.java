// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.io.Storable;
import java.util.Properties;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PersistentSet;

public interface TransactionController extends PersistentSet
{
    public static final int MODE_RECORD = 6;
    public static final int MODE_TABLE = 7;
    public static final int ISOLATION_NOLOCK = 0;
    public static final int ISOLATION_READ_UNCOMMITTED = 1;
    public static final int ISOLATION_READ_COMMITTED = 2;
    public static final int ISOLATION_READ_COMMITTED_NOHOLDLOCK = 3;
    public static final int ISOLATION_REPEATABLE_READ = 4;
    public static final int ISOLATION_SERIALIZABLE = 5;
    public static final int OPENMODE_USE_UPDATE_LOCKS = 4096;
    public static final int OPENMODE_SECONDARY_LOCKED = 8192;
    public static final int OPENMODE_BASEROW_INSERT_LOCKED = 16384;
    public static final int OPENMODE_FORUPDATE = 4;
    public static final int OPENMODE_FOR_LOCK_ONLY = 64;
    public static final int OPENMODE_LOCK_NOWAIT = 128;
    public static final int OPEN_CONGLOMERATE = 1;
    public static final int OPEN_SCAN = 2;
    public static final int OPEN_CREATED_SORTS = 3;
    public static final int OPEN_SORT = 4;
    public static final int OPEN_TOTAL = 5;
    public static final byte IS_DEFAULT = 0;
    public static final byte IS_TEMPORARY = 1;
    public static final byte IS_KEPT = 2;
    public static final int RELEASE_LOCKS = 1;
    public static final int KEEP_LOCKS = 2;
    public static final int READONLY_TRANSACTION_INITIALIZATION = 4;
    
    AccessFactory getAccessManager();
    
    boolean conglomerateExists(final long p0) throws StandardException;
    
    long createConglomerate(final String p0, final DataValueDescriptor[] p1, final ColumnOrdering[] p2, final int[] p3, final Properties p4, final int p5) throws StandardException;
    
    long createAndLoadConglomerate(final String p0, final DataValueDescriptor[] p1, final ColumnOrdering[] p2, final int[] p3, final Properties p4, final int p5, final RowLocationRetRowSource p6, final long[] p7) throws StandardException;
    
    long recreateAndLoadConglomerate(final String p0, final boolean p1, final DataValueDescriptor[] p2, final ColumnOrdering[] p3, final int[] p4, final Properties p5, final int p6, final long p7, final RowLocationRetRowSource p8, final long[] p9) throws StandardException;
    
    void addColumnToConglomerate(final long p0, final int p1, final Storable p2, final int p3) throws StandardException;
    
    void dropConglomerate(final long p0) throws StandardException;
    
    long findConglomid(final long p0) throws StandardException;
    
    long findContainerid(final long p0) throws StandardException;
    
    TransactionController startNestedUserTransaction(final boolean p0, final boolean p1) throws StandardException;
    
    Properties getUserCreateConglomPropList();
    
    ConglomerateController openConglomerate(final long p0, final boolean p1, final int p2, final int p3, final int p4) throws StandardException;
    
    ConglomerateController openCompiledConglomerate(final boolean p0, final int p1, final int p2, final int p3, final StaticCompiledOpenConglomInfo p4, final DynamicCompiledOpenConglomInfo p5) throws StandardException;
    
    BackingStoreHashtable createBackingStoreHashtableFromScan(final long p0, final int p1, final int p2, final int p3, final FormatableBitSet p4, final DataValueDescriptor[] p5, final int p6, final Qualifier[][] p7, final DataValueDescriptor[] p8, final int p9, final long p10, final int[] p11, final boolean p12, final long p13, final long p14, final int p15, final float p16, final boolean p17, final boolean p18, final boolean p19) throws StandardException;
    
    ScanController openScan(final long p0, final boolean p1, final int p2, final int p3, final int p4, final FormatableBitSet p5, final DataValueDescriptor[] p6, final int p7, final Qualifier[][] p8, final DataValueDescriptor[] p9, final int p10) throws StandardException;
    
    ScanController openCompiledScan(final boolean p0, final int p1, final int p2, final int p3, final FormatableBitSet p4, final DataValueDescriptor[] p5, final int p6, final Qualifier[][] p7, final DataValueDescriptor[] p8, final int p9, final StaticCompiledOpenConglomInfo p10, final DynamicCompiledOpenConglomInfo p11) throws StandardException;
    
    GroupFetchScanController openGroupFetchScan(final long p0, final boolean p1, final int p2, final int p3, final int p4, final FormatableBitSet p5, final DataValueDescriptor[] p6, final int p7, final Qualifier[][] p8, final DataValueDescriptor[] p9, final int p10) throws StandardException;
    
    GroupFetchScanController defragmentConglomerate(final long p0, final boolean p1, final boolean p2, final int p3, final int p4, final int p5) throws StandardException;
    
    void purgeConglomerate(final long p0) throws StandardException;
    
    void compressConglomerate(final long p0) throws StandardException;
    
    boolean fetchMaxOnBtree(final long p0, final int p1, final int p2, final int p3, final FormatableBitSet p4, final DataValueDescriptor[] p5) throws StandardException;
    
    StoreCostController openStoreCost(final long p0) throws StandardException;
    
    int countOpens(final int p0) throws StandardException;
    
    String debugOpened() throws StandardException;
    
    FileResource getFileHandler();
    
    CompatibilitySpace getLockSpace();
    
    void setNoLockWait(final boolean p0);
    
    StaticCompiledOpenConglomInfo getStaticCompiledConglomInfo(final long p0) throws StandardException;
    
    DynamicCompiledOpenConglomInfo getDynamicCompiledConglomInfo(final long p0) throws StandardException;
    
    void logAndDo(final Loggable p0) throws StandardException;
    
    long createSort(final Properties p0, final DataValueDescriptor[] p1, final ColumnOrdering[] p2, final SortObserver p3, final boolean p4, final long p5, final int p6) throws StandardException;
    
    void dropSort(final long p0) throws StandardException;
    
    SortController openSort(final long p0) throws StandardException;
    
    SortCostController openSortCostController() throws StandardException;
    
    RowLocationRetRowSource openSortRowSource(final long p0) throws StandardException;
    
    ScanController openSortScan(final long p0, final boolean p1) throws StandardException;
    
    boolean anyoneBlocked();
    
    void abort() throws StandardException;
    
    void commit() throws StandardException;
    
    DatabaseInstant commitNoSync(final int p0) throws StandardException;
    
    void destroy();
    
    ContextManager getContextManager();
    
    String getTransactionIdString();
    
    String getActiveStateTxIdString();
    
    boolean isIdle();
    
    boolean isGlobal();
    
    boolean isPristine();
    
    int releaseSavePoint(final String p0, final Object p1) throws StandardException;
    
    int rollbackToSavePoint(final String p0, final boolean p1, final Object p2) throws StandardException;
    
    int setSavePoint(final String p0, final Object p1) throws StandardException;
    
    Object createXATransactionFromLocalTransaction(final int p0, final byte[] p1, final byte[] p2) throws StandardException;
}
