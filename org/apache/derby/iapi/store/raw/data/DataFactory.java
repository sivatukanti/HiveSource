// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.data;

import org.apache.derby.io.StorageFactory;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import java.io.File;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.StreamContainerHandle;
import org.apache.derby.iapi.store.access.RowSource;
import java.util.Properties;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.ContainerKey;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.store.raw.Corruptable;

public interface DataFactory extends Corruptable
{
    public static final String MODULE = "org.apache.derby.iapi.store.raw.data.DataFactory";
    public static final String TEMP_SEGMENT_NAME = "tmp";
    public static final String DB_LOCKFILE_NAME = "db.lck";
    public static final String DB_EX_LOCKFILE_NAME = "dbex.lck";
    
    boolean isReadOnly();
    
    ContainerHandle openContainer(final RawTransaction p0, final ContainerKey p1, final LockingPolicy p2, final int p3) throws StandardException;
    
    RawContainerHandle openDroppedContainer(final RawTransaction p0, final ContainerKey p1, final LockingPolicy p2, final int p3) throws StandardException;
    
    long addContainer(final RawTransaction p0, final long p1, final long p2, final int p3, final Properties p4, final int p5) throws StandardException;
    
    long addAndLoadStreamContainer(final RawTransaction p0, final long p1, final Properties p2, final RowSource p3) throws StandardException;
    
    StreamContainerHandle openStreamContainer(final RawTransaction p0, final long p1, final long p2, final boolean p3) throws StandardException;
    
    void dropStreamContainer(final RawTransaction p0, final long p1, final long p2) throws StandardException;
    
    void reCreateContainerForRedoRecovery(final RawTransaction p0, final long p1, final long p2, final ByteArray p3) throws StandardException;
    
    void dropContainer(final RawTransaction p0, final ContainerKey p1) throws StandardException;
    
    void checkpoint() throws StandardException;
    
    void idle() throws StandardException;
    
    UUID getIdentifier();
    
    void setRawStoreFactory(final RawStoreFactory p0, final boolean p1, final Properties p2) throws StandardException;
    
    void createFinished() throws StandardException;
    
    FileResource getFileHandler();
    
    void removeStubsOK();
    
    int reclaimSpace(final Serviceable p0, final ContextManager p1) throws StandardException;
    
    void postRecovery() throws StandardException;
    
    void setupCacheCleaner(final DaemonService p0);
    
    int encrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4, final boolean p5) throws StandardException;
    
    int decrypt(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4) throws StandardException;
    
    void decryptAllContainers(final RawTransaction p0) throws StandardException;
    
    void encryptAllContainers(final RawTransaction p0) throws StandardException;
    
    void removeOldVersionOfContainers() throws StandardException;
    
    void setDatabaseEncrypted(final boolean p0);
    
    int getEncryptionBlockSize();
    
    void freezePersistentStore() throws StandardException;
    
    void unfreezePersistentStore();
    
    void writeInProgress() throws StandardException;
    
    void writeFinished();
    
    void backupDataFiles(final Transaction p0, final File p1) throws StandardException;
    
    long getMaxContainerId() throws StandardException;
    
    void removeDroppedContainerFileStubs(final LogInstant p0) throws StandardException;
    
    StorageFactory getStorageFactory();
    
    void stop();
    
    boolean databaseEncrypted();
}
