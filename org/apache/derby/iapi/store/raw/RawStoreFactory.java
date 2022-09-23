// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.store.raw.xact.TransactionFactory;
import java.io.Serializable;
import java.util.Properties;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.locks.LockFactory;

public interface RawStoreFactory extends Corruptable
{
    public static final int DERBY_STORE_MINOR_VERSION_1 = 1;
    public static final int DERBY_STORE_MINOR_VERSION_2 = 2;
    public static final int DERBY_STORE_MINOR_VERSION_3 = 3;
    public static final int DERBY_STORE_MINOR_VERSION_4 = 4;
    public static final int DERBY_STORE_MINOR_VERSION_10 = 10;
    public static final int DERBY_STORE_MAJOR_VERSION_10 = 10;
    public static final int PAGE_SIZE_DEFAULT = 4096;
    public static final int PAGE_SIZE_MINIMUM = 1024;
    public static final String PAGE_SIZE_STRING = "2048";
    public static final String PAGE_CACHE_SIZE_PARAMETER = "derby.storage.pageCacheSize";
    public static final int PAGE_CACHE_SIZE_DEFAULT = 1000;
    public static final int PAGE_CACHE_SIZE_MINIMUM = 40;
    public static final int PAGE_CACHE_SIZE_MAXIMUM = Integer.MAX_VALUE;
    public static final String CONTAINER_CACHE_SIZE_PARAMETER = "derby.storage.fileCacheSize";
    public static final int CONTAINER_CACHE_SIZE_DEFAULT = 100;
    public static final int CONTAINER_CACHE_SIZE_MINIMUM = 2;
    public static final int CONTAINER_CACHE_SIZE_MAXIMUM = Integer.MAX_VALUE;
    public static final short MAX_CONTAINER_INITIAL_PAGES = 1000;
    public static final String MINIMUM_RECORD_SIZE_PARAMETER = "derby.storage.minimumRecordSize";
    public static final int MINIMUM_RECORD_SIZE_DEFAULT = 12;
    public static final int MINIMUM_RECORD_SIZE_MINIMUM = 1;
    public static final String PAGE_RESERVED_SPACE_PARAMETER = "derby.storage.pageReservedSpace";
    public static final String PAGE_RESERVED_ZERO_SPACE_STRING = "0";
    public static final String PRE_ALLOCATE_PAGE = "derby.storage.pagePerAllocate";
    public static final String PAGE_REUSABLE_RECORD_ID = "derby.storage.reusableRecordId";
    public static final String STREAM_FILE_BUFFER_SIZE_PARAMETER = "derby.storage.streamFileBufferSize";
    public static final int STREAM_FILE_BUFFER_SIZE_DEFAULT = 16384;
    public static final int STREAM_FILE_BUFFER_SIZE_MINIMUM = 1024;
    public static final int STREAM_FILE_BUFFER_SIZE_MAXIMUM = Integer.MAX_VALUE;
    public static final String CONTAINER_INITIAL_PAGES = "derby.storage.initialPages";
    public static final int ENCRYPTION_ALIGNMENT = 8;
    public static final int DEFAULT_ENCRYPTION_BLOCKSIZE = 8;
    public static final String ENCRYPTION_BLOCKSIZE = "derby.encryptionBlockSize";
    public static final String DATA_ENCRYPT_ALGORITHM_VERSION = "data_encrypt_algorithm_version";
    public static final String LOG_ENCRYPT_ALGORITHM_VERSION = "log_encrypt_algorithm_version";
    public static final String ENCRYPTED_KEY = "encryptedBootPassword";
    public static final String OLD_ENCRYPTED_KEY = "OldEncryptedBootPassword";
    public static final String DB_ENCRYPTION_STATUS = "derby.storage.databaseEncryptionStatus";
    public static final int DB_ENCRYPTION_IN_PROGRESS = 1;
    public static final int DB_ENCRYPTION_IN_UNDO = 2;
    public static final int DB_ENCRYPTION_IN_CLEANUP = 3;
    public static final String CRYPTO_OLD_EXTERNAL_KEY_VERIFY_FILE = "verifyOldKey.dat";
    public static final String KEEP_TRANSACTION_LOG = "derby.storage.keepTransactionLog";
    public static final String PATCH_INITPAGE_RECOVER_ERROR = "derby.storage.patchInitPageRecoverError";
    public static final String MODULE = "org.apache.derby.iapi.store.raw.RawStoreFactory";
    
    boolean isReadOnly();
    
    LockFactory getLockFactory();
    
    Transaction startTransaction(final ContextManager p0, final String p1) throws StandardException;
    
    Transaction startGlobalTransaction(final ContextManager p0, final int p1, final byte[] p2, final byte[] p3) throws StandardException;
    
    Transaction findUserTransaction(final ContextManager p0, final String p1) throws StandardException;
    
    Transaction startInternalTransaction(final ContextManager p0) throws StandardException;
    
    Transaction startNestedReadOnlyUserTransaction(final CompatibilitySpace p0, final ContextManager p1, final String p2) throws StandardException;
    
    Transaction startNestedUpdateUserTransaction(final ContextManager p0, final String p1, final boolean p2) throws StandardException;
    
    TransactionInfo[] getTransactionInfo();
    
    void startReplicationMaster(final String p0, final String p1, final int p2, final String p3) throws StandardException;
    
    void stopReplicationMaster() throws StandardException;
    
    void failover(final String p0) throws StandardException;
    
    void freeze() throws StandardException;
    
    void unfreeze() throws StandardException;
    
    void backup(final String p0, final boolean p1) throws StandardException;
    
    void backupAndEnableLogArchiveMode(final String p0, final boolean p1, final boolean p2) throws StandardException;
    
    void disableLogArchiveMode(final boolean p0) throws StandardException;
    
    void checkpoint() throws StandardException;
    
    void idle() throws StandardException;
    
    ScanHandle openFlushedScan(final DatabaseInstant p0, final int p1) throws StandardException;
    
    DaemonService getDaemon();
    
    String getTransactionFactoryModule();
    
    String getDataFactoryModule();
    
    String getLogFactoryModule();
    
    Object getXAResourceManager() throws StandardException;
    
    void createFinished() throws StandardException;
    
    void getRawStoreProperties(final PersistentSet p0) throws StandardException;
    
    void freezePersistentStore() throws StandardException;
    
    void unfreezePersistentStore() throws StandardException;
    
    int encrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final boolean p5) throws StandardException;
    
    int decrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws StandardException;
    
    int getEncryptionBlockSize();
    
    int random();
    
    Serializable changeBootPassword(final Properties p0, final Serializable p1) throws StandardException;
    
    long getMaxContainerId() throws StandardException;
    
    TransactionFactory getXactFactory();
    
    boolean checkVersion(final int p0, final int p1, final String p2) throws StandardException;
    
    void createDataWarningFile() throws StandardException;
}
