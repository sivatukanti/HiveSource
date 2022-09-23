// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.log;

import org.apache.derby.iapi.store.replication.master.MasterFactory;
import java.io.File;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.store.raw.log.LogScan;
import org.apache.derby.iapi.store.raw.ScanHandle;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.catalog.UUID;
import java.util.Properties;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.services.io.Formatable;
import org.apache.derby.iapi.store.raw.xact.TransactionFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.raw.log.Logger;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.store.raw.log.LogFactory;

public class ReadOnly implements LogFactory, ModuleSupportable
{
    private String logArchiveDirectory;
    
    public ReadOnly() {
        this.logArchiveDirectory = null;
    }
    
    public Logger getLogger() {
        return null;
    }
    
    public void createDataWarningFile() throws StandardException {
    }
    
    public void setRawStoreFactory(final RawStoreFactory rawStoreFactory) {
    }
    
    public void recover(final DataFactory dataFactory, final TransactionFactory transactionFactory) throws StandardException {
        if (transactionFactory != null) {
            transactionFactory.useTransactionTable(null);
        }
    }
    
    public boolean checkpoint(final RawStoreFactory rawStoreFactory, final DataFactory dataFactory, final TransactionFactory transactionFactory, final boolean b) {
        return true;
    }
    
    public StandardException markCorrupt(final StandardException ex) {
        return ex;
    }
    
    public void flush(final LogInstant logInstant) throws StandardException {
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("derby.__rt.storage.log");
        return property != null && property.equals("readonly");
    }
    
    public LogInstant setTruncationLWM(final UUID uuid, final LogInstant logInstant, final RawStoreFactory rawStoreFactory, final TransactionFactory transactionFactory) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public void setTruncationLWM(final UUID uuid, final LogInstant logInstant) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public void removeTruncationLWM(final UUID uuid, final RawStoreFactory rawStoreFactory, final TransactionFactory transactionFactory) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public LogInstant getTruncationLWM(final UUID uuid) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public void removeTruncationLWM(final UUID uuid) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public ScanHandle openFlushedScan(final DatabaseInstant databaseInstant, final int n) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public LogScan openForwardsScan(final LogInstant logInstant, final LogInstant logInstant2) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public LogInstant getFirstUnflushedInstant() {
        return null;
    }
    
    public long getFirstUnflushedInstantAsLong() {
        return 0L;
    }
    
    public LogScan openForwardsFlushedScan(final LogInstant logInstant) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public void freezePersistentStore() throws StandardException {
    }
    
    public void unfreezePersistentStore() throws StandardException {
    }
    
    public boolean logArchived() {
        return this.logArchiveDirectory != null;
    }
    
    public void getLogFactoryProperties(final PersistentSet set) {
    }
    
    public StorageFile getLogDirectory() {
        return null;
    }
    
    public String getCanonicalLogPath() {
        return null;
    }
    
    public void enableLogArchiveMode() {
    }
    
    public void disableLogArchiveMode() {
    }
    
    public void deleteOnlineArchivedLogFiles() {
    }
    
    public boolean inRFR() {
        return false;
    }
    
    public void checkpointInRFR(final LogInstant logInstant, final long n, final long n2, final DataFactory dataFactory) throws StandardException {
    }
    
    public void startLogBackup(final File file) throws StandardException {
    }
    
    public void endLogBackup(final File file) throws StandardException {
    }
    
    public void abortLogBackup() {
    }
    
    public void setDatabaseEncrypted(final boolean b, final boolean b2) {
    }
    
    public void startNewLogFile() throws StandardException {
    }
    
    public boolean isCheckpointInLastLogFile() throws StandardException {
        return false;
    }
    
    public void deleteLogFileAfterCheckpointLogFile() throws StandardException {
    }
    
    public boolean checkVersion(final int n, final int n2, final String s) throws StandardException {
        throw StandardException.newException("XSAI3.S");
    }
    
    public void startReplicationMasterRole(final MasterFactory masterFactory) throws StandardException {
        throw StandardException.newException("XRE00");
    }
    
    public boolean inReplicationMasterMode() {
        return false;
    }
    
    public void stopReplicationMasterRole() {
    }
}
