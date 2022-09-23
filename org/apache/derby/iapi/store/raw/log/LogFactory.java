// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.log;

import org.apache.derby.iapi.store.replication.master.MasterFactory;
import java.io.File;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.store.raw.ScanHandle;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.raw.xact.TransactionFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.Corruptable;

public interface LogFactory extends Corruptable
{
    public static final String RUNTIME_ATTRIBUTES = "derby.__rt.storage.log";
    public static final String RT_READONLY = "readonly";
    public static final String LOG_DIRECTORY_NAME = "log";
    public static final String MODULE = "org.apache.derby.iapi.store.raw.log.LogFactory";
    
    Logger getLogger();
    
    void createDataWarningFile() throws StandardException;
    
    void setRawStoreFactory(final RawStoreFactory p0);
    
    void recover(final DataFactory p0, final TransactionFactory p1) throws StandardException;
    
    boolean checkpoint(final RawStoreFactory p0, final DataFactory p1, final TransactionFactory p2, final boolean p3) throws StandardException;
    
    void flush(final LogInstant p0) throws StandardException;
    
    LogScan openForwardsFlushedScan(final LogInstant p0) throws StandardException;
    
    ScanHandle openFlushedScan(final DatabaseInstant p0, final int p1) throws StandardException;
    
    LogScan openForwardsScan(final LogInstant p0, final LogInstant p1) throws StandardException;
    
    LogInstant getFirstUnflushedInstant();
    
    long getFirstUnflushedInstantAsLong();
    
    void freezePersistentStore() throws StandardException;
    
    void unfreezePersistentStore() throws StandardException;
    
    boolean logArchived();
    
    boolean inReplicationMasterMode();
    
    void getLogFactoryProperties(final PersistentSet p0) throws StandardException;
    
    StorageFile getLogDirectory() throws StandardException;
    
    String getCanonicalLogPath();
    
    void enableLogArchiveMode() throws StandardException;
    
    void disableLogArchiveMode() throws StandardException;
    
    void deleteOnlineArchivedLogFiles();
    
    boolean inRFR();
    
    void checkpointInRFR(final LogInstant p0, final long p1, final long p2, final DataFactory p3) throws StandardException;
    
    void startLogBackup(final File p0) throws StandardException;
    
    void endLogBackup(final File p0) throws StandardException;
    
    void abortLogBackup();
    
    void setDatabaseEncrypted(final boolean p0, final boolean p1) throws StandardException;
    
    void startNewLogFile() throws StandardException;
    
    boolean isCheckpointInLastLogFile() throws StandardException;
    
    void deleteLogFileAfterCheckpointLogFile() throws StandardException;
    
    boolean checkVersion(final int p0, final int p1, final String p2) throws StandardException;
    
    void startReplicationMasterRole(final MasterFactory p0) throws StandardException;
    
    void stopReplicationMasterRole();
}
