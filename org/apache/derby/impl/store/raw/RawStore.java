// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw;

import java.io.Writer;
import org.apache.derby.iapi.services.io.FileUtil;
import org.apache.derby.iapi.util.ReuseFactory;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.util.Date;
import java.io.Serializable;
import org.apache.derby.iapi.services.crypto.CipherFactoryBuilder;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.store.raw.ScanHandle;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.monitor.PersistentService;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import java.io.OutputStreamWriter;
import java.io.IOException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.services.i18n.MessageService;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.derby.iapi.store.replication.master.MasterFactory;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.locks.CompatibilitySpace;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.locks.LockFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.io.WritableStorageFactory;
import org.apache.derby.impl.services.monitor.UpdateServiceProperties;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.daemon.DaemonFactory;
import java.util.Properties;
import java.io.File;
import org.apache.derby.io.StorageFile;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.services.crypto.CipherFactory;
import org.apache.derby.iapi.services.crypto.CipherProvider;
import java.security.SecureRandom;
import org.apache.derby.io.StorageFactory;
import org.apache.derby.iapi.store.replication.slave.SlaveFactory;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.xact.TransactionFactory;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.store.raw.RawStoreFactory;

public final class RawStore implements RawStoreFactory, ModuleControl, ModuleSupportable, PrivilegedExceptionAction
{
    private static final String BACKUP_HISTORY = "BACKUP.HISTORY";
    protected TransactionFactory xactFactory;
    protected DataFactory dataFactory;
    protected LogFactory logFactory;
    private SlaveFactory slaveFactory;
    private StorageFactory storageFactory;
    private SecureRandom random;
    private boolean isEncryptedDatabase;
    private CipherProvider encryptionEngine;
    private CipherProvider decryptionEngine;
    private CipherProvider newEncryptionEngine;
    private CipherProvider newDecryptionEngine;
    private CipherFactory currentCipherFactory;
    private CipherFactory newCipherFactory;
    private int counter_encrypt;
    private int counter_decrypt;
    private int encryptionBlockSize;
    protected DaemonService rawStoreDaemon;
    private int actionCode;
    private static final int FILE_WRITER_ACTION = 1;
    private StorageFile actionStorageFile;
    private StorageFile actionToStorageFile;
    private boolean actionAppend;
    private static final int REGULAR_FILE_EXISTS_ACTION = 2;
    private File actionRegularFile;
    private static final int STORAGE_FILE_EXISTS_ACTION = 3;
    private static final int REGULAR_FILE_DELETE_ACTION = 4;
    private static final int REGULAR_FILE_MKDIRS_ACTION = 5;
    private static final int REGULAR_FILE_IS_DIRECTORY_ACTION = 6;
    private static final int REGULAR_FILE_REMOVE_DIRECTORY_ACTION = 7;
    private static final int REGULAR_FILE_RENAME_TO_ACTION = 8;
    private File actionRegularFile2;
    private static final int COPY_STORAGE_DIRECTORY_TO_REGULAR_ACTION = 9;
    private byte[] actionBuffer;
    private String[] actionFilter;
    private boolean actionCopySubDirs;
    private static final int COPY_REGULAR_DIRECTORY_TO_STORAGE_ACTION = 10;
    private static final int COPY_REGULAR_FILE_TO_STORAGE_ACTION = 11;
    private static final int REGULAR_FILE_LIST_DIRECTORY_ACTION = 12;
    private static final int STORAGE_FILE_LIST_DIRECTORY_ACTION = 13;
    private static final int COPY_STORAGE_FILE_TO_REGULAR_ACTION = 14;
    private static final int REGULAR_FILE_GET_CANONICALPATH_ACTION = 15;
    private static final int STORAGE_FILE_GET_CANONICALPATH_ACTION = 16;
    private static final int COPY_STORAGE_FILE_TO_STORAGE_ACTION = 17;
    private static final int STORAGE_FILE_DELETE_ACTION = 18;
    private static final int README_FILE_OUTPUTSTREAM_WRITER_ACTION = 19;
    public static final String TEST_REENCRYPT_CRASH_BEFORE_COMMT;
    public static final String TEST_REENCRYPT_CRASH_AFTER_COMMT;
    public static final String TEST_REENCRYPT_CRASH_AFTER_SWITCH_TO_NEWKEY;
    public static final String TEST_REENCRYPT_CRASH_AFTER_CHECKPOINT;
    public static final String TEST_REENCRYPT_CRASH_AFTER_RECOVERY_UNDO_LOGFILE_DELETE;
    public static final String TEST_REENCRYPT_CRASH_AFTER_RECOVERY_UNDO_REVERTING_KEY;
    public static final String TEST_REENCRYPT_CRASH_BEFORE_RECOVERY_FINAL_CLEANUP;
    
    public RawStore() {
        this.newCipherFactory = null;
        this.encryptionBlockSize = 8;
    }
    
    public boolean canSupport(final Properties properties) {
        return true;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        boolean b2 = false;
        boolean b3 = false;
        final String property = properties.getProperty("replication.slave.mode");
        if (property != null && property.equals("slavemode")) {
            b3 = true;
        }
        this.rawStoreDaemon = ((DaemonFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.daemon.DaemonFactory")).createNewDaemon("rawStoreDaemon");
        this.xactFactory = (TransactionFactory)Monitor.bootServiceModule(b, this, this.getTransactionFactoryModule(), properties);
        this.dataFactory = (DataFactory)Monitor.bootServiceModule(b, this, this.getDataFactoryModule(), properties);
        this.storageFactory = this.dataFactory.getStorageFactory();
        String s = null;
        if (properties != null) {
            s = properties.getProperty("createFrom");
            if (s == null) {
                s = properties.getProperty("restoreFrom");
            }
            if (s == null) {
                s = properties.getProperty("rollForwardRecoveryFrom");
            }
        }
        if (b) {
            b2 = this.setupEncryptionEngines(b, properties);
        }
        this.dataFactory.setRawStoreFactory(this, b, properties);
        this.xactFactory.setRawStoreFactory(this);
        if (properties instanceof UpdateServiceProperties && this.storageFactory instanceof WritableStorageFactory) {
            ((UpdateServiceProperties)properties).setStorageFactory((WritableStorageFactory)this.storageFactory);
        }
        this.logFactory = (LogFactory)Monitor.findServiceModule(this, this.getLogFactoryModule());
        if (s != null) {
            this.restoreRemainingFromBackup(s);
        }
        final String property2 = properties.getProperty("logDevice");
        if (property2 != null) {
            if (!this.isReadOnly() && (b || !property2.equals(this.logFactory.getCanonicalLogPath()) || s != null)) {
                properties.put("logDevice", this.logFactory.getCanonicalLogPath());
                properties.put("derby.storage.logDeviceWhenBackedUp", this.logFactory.getCanonicalLogPath());
            }
        }
        else if (s != null && this.logFactory.getCanonicalLogPath() != null) {
            properties.put("logDevice", this.logFactory.getCanonicalLogPath());
        }
        else {
            properties.remove("derby.storage.logDeviceWhenBackedUp");
        }
        if (s != null) {
            ((UpdateServiceProperties)properties).saveServiceProperties();
        }
        if (!b) {
            if (properties.getProperty("derby.storage.databaseEncryptionStatus") != null) {
                this.handleIncompleteDbCryptoOperation(properties);
            }
            b2 = this.setupEncryptionEngines(b, properties);
        }
        if (this.isEncryptedDatabase) {
            this.logFactory.setDatabaseEncrypted(true, false);
            this.dataFactory.setDatabaseEncrypted(true);
        }
        this.logFactory.setRawStoreFactory(this);
        if (b3) {
            (this.slaveFactory = (SlaveFactory)Monitor.bootServiceModule(b, this, this.getSlaveFactoryModule(), properties)).startSlave(this, this.logFactory);
        }
        this.logFactory.recover(this.dataFactory, this.xactFactory);
        if (b2) {
            this.applyBulkCryptoOperation(properties, this.newCipherFactory);
        }
    }
    
    public void stop() {
        if (this.rawStoreDaemon != null) {
            this.rawStoreDaemon.stop();
        }
        if (this.logFactory == null) {
            return;
        }
        try {
            if (this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, false) && this.dataFactory != null) {
                this.dataFactory.removeStubsOK();
            }
        }
        catch (StandardException ex) {
            this.markCorrupt(ex);
        }
    }
    
    public boolean isReadOnly() {
        return this.dataFactory.isReadOnly();
    }
    
    public LockFactory getLockFactory() {
        return this.xactFactory.getLockFactory();
    }
    
    public TransactionFactory getXactFactory() {
        return this.xactFactory;
    }
    
    public Object getXAResourceManager() throws StandardException {
        return this.xactFactory.getXAResourceManager();
    }
    
    public Transaction startGlobalTransaction(final ContextManager contextManager, final int n, final byte[] array, final byte[] array2) throws StandardException {
        return this.xactFactory.startGlobalTransaction(this, contextManager, n, array, array2);
    }
    
    public Transaction startTransaction(final ContextManager contextManager, final String s) throws StandardException {
        return this.xactFactory.startTransaction(this, contextManager, s);
    }
    
    public Transaction startNestedReadOnlyUserTransaction(final CompatibilitySpace compatibilitySpace, final ContextManager contextManager, final String s) throws StandardException {
        return this.xactFactory.startNestedReadOnlyUserTransaction(this, compatibilitySpace, contextManager, s);
    }
    
    public Transaction startNestedUpdateUserTransaction(final ContextManager contextManager, final String s, final boolean b) throws StandardException {
        return this.xactFactory.startNestedUpdateUserTransaction(this, contextManager, s, b);
    }
    
    public Transaction findUserTransaction(final ContextManager contextManager, final String s) throws StandardException {
        return this.xactFactory.findUserTransaction(this, contextManager, s);
    }
    
    public Transaction startInternalTransaction(final ContextManager contextManager) throws StandardException {
        return this.xactFactory.startInternalTransaction(this, contextManager);
    }
    
    public void checkpoint() throws StandardException {
        this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
    }
    
    public void startReplicationMaster(final String s, final String s2, final int n, final String value) throws StandardException {
        if (this.isReadOnly()) {
            throw StandardException.newException("XRE00");
        }
        if (this.xactFactory.findUserTransaction(this, ContextService.getFactory().getCurrentContextManager(), "UserTransaction").isBlockingBackup()) {
            throw StandardException.newException("XRE23");
        }
        final Properties properties = new Properties();
        properties.setProperty("derby.__rt.replication.master.mode", value);
        ((MasterFactory)Monitor.bootServiceModule(true, this, this.getMasterFactoryModule(), properties)).startMaster(this, this.dataFactory, this.logFactory, s2, n, s);
    }
    
    public void stopReplicationMaster() throws StandardException {
        if (this.isReadOnly()) {
            throw StandardException.newException("XRE00");
        }
        MasterFactory masterFactory;
        try {
            masterFactory = (MasterFactory)Monitor.findServiceModule(this, this.getMasterFactoryModule());
        }
        catch (StandardException ex) {
            throw StandardException.newException("XRE07");
        }
        masterFactory.stopMaster();
    }
    
    public void failover(final String s) throws StandardException {
        if (this.isReadOnly()) {
            throw StandardException.newException("XRE00");
        }
        MasterFactory masterFactory;
        try {
            masterFactory = (MasterFactory)Monitor.findServiceModule(this, this.getMasterFactoryModule());
        }
        catch (StandardException ex) {
            throw StandardException.newException("XRE07");
        }
        masterFactory.startFailover();
    }
    
    public void freeze() throws StandardException {
        this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
        this.dataFactory.freezePersistentStore();
        this.logFactory.freezePersistentStore();
    }
    
    public void unfreeze() throws StandardException {
        this.logFactory.unfreezePersistentStore();
        this.dataFactory.unfreezePersistentStore();
    }
    
    public void backup(String s, final boolean b) throws StandardException {
        if (s == null || s.equals("")) {
            throw StandardException.newException("XSRS6.S", (Object)null);
        }
        String file = null;
        try {
            file = new URL(s).getFile();
        }
        catch (MalformedURLException ex) {}
        if (file != null) {
            s = file;
        }
        final RawTransaction userTransaction = this.xactFactory.findUserTransaction(this, ContextService.getFactory().getCurrentContextManager(), "UserTransaction");
        try {
            if (userTransaction.isBlockingBackup()) {
                throw StandardException.newException("XSRSB.S");
            }
            if (!this.xactFactory.blockBackupBlockingOperations(b)) {
                throw StandardException.newException("XSRSA.S");
            }
            this.backup(userTransaction, new File(s));
        }
        finally {
            this.xactFactory.unblockBackupBlockingOperations();
        }
    }
    
    public synchronized void backup(final Transaction transaction, final File parent) throws StandardException {
        if (!this.privExists(parent)) {
            if (!this.privMkdirs(parent)) {
                throw StandardException.newException("XSRS6.S", parent);
            }
        }
        else {
            if (!this.privIsDirectory(parent)) {
                throw StandardException.newException("XSRS1.S", parent);
            }
            if (this.privExists(new File(parent, "service.properties"))) {
                throw StandardException.newException("XSRSC.S", parent);
            }
        }
        boolean b = true;
        boolean b2 = false;
        boolean b3 = false;
        File file = null;
        File parent2 = null;
        OutputStreamWriter privFileWriter = null;
        StorageFile storageFile = null;
        File file2 = null;
        final LogInstant firstUnflushedInstant = this.logFactory.getFirstUnflushedInstant();
        try {
            this.storageFactory.newStorageFile(null);
            final String canonicalName = this.storageFactory.getCanonicalName();
            final String substring = canonicalName.substring(canonicalName.lastIndexOf(this.storageFactory.getSeparator()) + 1);
            privFileWriter = this.privFileWriter(this.storageFactory.newStorageFile("BACKUP.HISTORY"), true);
            parent2 = new File(parent, substring);
            this.logHistory(privFileWriter, MessageService.getTextMessage("D004", canonicalName, this.getFilePath(parent2)));
            if (this.privExists(parent2)) {
                file = new File(parent, substring + ".OLD");
                if (this.privExists(file)) {
                    if (this.privIsDirectory(file)) {
                        this.privRemoveDirectory(file);
                    }
                    else {
                        this.privDelete(file);
                    }
                }
                if (!this.privRenameTo(parent2, file)) {
                    b3 = true;
                    throw StandardException.newException("XSRS4.S", parent2, file);
                }
                this.logHistory(privFileWriter, MessageService.getTextMessage("D005", this.getFilePath(parent2), this.getFilePath(file)));
                b2 = true;
            }
            if (!this.privMkdirs(parent2)) {
                throw StandardException.newException("XSRS6.S", parent2);
            }
            storageFile = this.storageFactory.newStorageFile("BACKUP.HISTORY");
            file2 = new File(parent2, "BACKUP.HISTORY");
            if (!this.privCopyFile(storageFile, file2)) {
                throw StandardException.newException("XSRS5.S", storageFile, file2);
            }
            final StorageFile storageFile2 = this.storageFactory.newStorageFile("jar");
            if (this.privExists(storageFile2)) {
                final String[] privList = this.privList(storageFile2);
                final File file3 = new File(parent2, "jar");
                if (!this.privMkdirs(file3)) {
                    throw StandardException.newException("XSRS6.S", file3);
                }
                if (((LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext")).getDataDictionary().checkVersion(210, null)) {
                    for (int i = 0; i < privList.length; ++i) {
                        final StorageFile storageFile3 = this.storageFactory.newStorageFile(storageFile2, privList[i]);
                        final File file4 = new File(file3, privList[i]);
                        if (!this.privIsDirectory(new File(storageFile3.getPath()))) {
                            if (!this.privCopyFile(storageFile3, file4)) {
                                throw StandardException.newException("XSRS5.S", storageFile3, file4);
                            }
                        }
                    }
                }
                else {
                    for (int j = 0; j < privList.length; ++j) {
                        final StorageFile storageFile4 = this.storageFactory.newStorageFile(storageFile2, privList[j]);
                        final File file5 = new File(file3, privList[j]);
                        if (!this.privCopyDirectory(storageFile4, file5, null, null, false)) {
                            throw StandardException.newException("XSRS5.S", storageFile4, file5);
                        }
                    }
                }
            }
            final StorageFile logDirectory = this.logFactory.getLogDirectory();
            try {
                final String serviceName = Monitor.getMonitor().getServiceName(this);
                final PersistentService serviceType = Monitor.getMonitor().getServiceType(this);
                final Properties serviceProperties = serviceType.getServiceProperties(serviceType.getCanonicalServiceName(serviceName), null);
                if (!logDirectory.equals(this.storageFactory.newStorageFile("log"))) {
                    serviceProperties.remove("logDevice");
                    this.logHistory(privFileWriter, MessageService.getTextMessage("D007"));
                }
                serviceType.saveServiceProperties(parent2.getPath(), serviceProperties);
            }
            catch (StandardException obj) {
                this.logHistory(privFileWriter, MessageService.getTextMessage("D008") + obj);
                return;
            }
            final StorageFile storageFile5 = this.storageFactory.newStorageFile("verifyKey.dat");
            if (this.privExists(storageFile5)) {
                final File file6 = new File(parent2, "verifyKey.dat");
                if (!this.privCopyFile(storageFile5, file6)) {
                    throw StandardException.newException("XSRS5.S", storageFile5, file6);
                }
            }
            final File file7 = new File(parent2, "log");
            if (this.privExists(file7)) {
                this.privRemoveDirectory(file7);
            }
            if (!this.privMkdirs(file7)) {
                throw StandardException.newException("XSRS6.S", file7);
            }
            this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
            this.logFactory.startLogBackup(file7);
            final File file8 = new File(parent2, "seg0");
            if (!this.privMkdirs(file8)) {
                throw StandardException.newException("XSRS6.S", file8);
            }
            this.dataFactory.backupDataFiles(transaction, file8);
            this.logHistory(privFileWriter, MessageService.getTextMessage("D006", this.getFilePath(file8)));
            this.logFactory.endLogBackup(file7);
            this.logHistory(privFileWriter, MessageService.getTextMessage("D009", this.getFilePath(logDirectory), this.getFilePath(file7)));
            b = false;
        }
        catch (IOException ex) {
            throw StandardException.newException("XSRS7.S", ex);
        }
        finally {
            try {
                if (b) {
                    this.logFactory.abortLogBackup();
                    if (!b3) {
                        this.privRemoveDirectory(parent2);
                    }
                    if (b2) {
                        this.privRenameTo(file, parent2);
                    }
                    this.logHistory(privFileWriter, MessageService.getTextMessage("D010"));
                }
                else {
                    if (b2 && this.privExists(file)) {
                        this.privRemoveDirectory(file);
                        this.logHistory(privFileWriter, MessageService.getTextMessage("D011", this.getFilePath(file)));
                    }
                    this.logHistory(privFileWriter, MessageService.getTextMessage("D012", firstUnflushedInstant));
                    if (!this.privCopyFile(storageFile, file2)) {
                        throw StandardException.newException("XSRS5.S", storageFile, file2);
                    }
                }
                privFileWriter.close();
            }
            catch (IOException ex2) {
                try {
                    privFileWriter.close();
                }
                catch (IOException ex3) {}
                throw StandardException.newException("XSRS7.S", ex2);
            }
        }
    }
    
    public void backupAndEnableLogArchiveMode(final String s, final boolean b, final boolean b2) throws StandardException {
        boolean b3 = false;
        try {
            if (!this.logFactory.logArchived()) {
                this.logFactory.enableLogArchiveMode();
                b3 = true;
            }
            this.backup(s, b2);
            if (b) {
                this.logFactory.deleteOnlineArchivedLogFiles();
            }
        }
        catch (Throwable t) {
            if (b3) {
                this.logFactory.disableLogArchiveMode();
            }
            throw StandardException.plainWrapException(t);
        }
    }
    
    public void disableLogArchiveMode(final boolean b) throws StandardException {
        this.logFactory.disableLogArchiveMode();
        if (b) {
            this.logFactory.deleteOnlineArchivedLogFiles();
        }
    }
    
    private void restoreRemainingFromBackup(final String s) throws StandardException {
        final File file = new File(s, "jar");
        final StorageFile storageFile = this.storageFactory.newStorageFile("jar");
        if (!this.privExists(storageFile) && this.privExists(file) && !this.privCopyDirectory(file, storageFile)) {
            throw StandardException.newException("XBM0Z.D", file, storageFile);
        }
        final StorageFile storageFile2 = this.storageFactory.newStorageFile("BACKUP.HISTORY");
        final File file2 = new File(s, "BACKUP.HISTORY");
        if (this.privExists(file2) && !this.privExists(storageFile2) && !this.privCopyFile(file2, storageFile2)) {
            throw StandardException.newException("XSRS5.S", file2, storageFile2);
        }
    }
    
    public void idle() throws StandardException {
        this.dataFactory.idle();
    }
    
    public TransactionInfo[] getTransactionInfo() {
        return this.xactFactory.getTransactionInfo();
    }
    
    public ScanHandle openFlushedScan(final DatabaseInstant databaseInstant, final int n) throws StandardException {
        return this.logFactory.openFlushedScan(databaseInstant, n);
    }
    
    public DaemonService getDaemon() {
        return this.rawStoreDaemon;
    }
    
    public void createFinished() throws StandardException {
        this.xactFactory.createFinished();
        this.dataFactory.createFinished();
    }
    
    public void getRawStoreProperties(final PersistentSet set) throws StandardException {
        this.logFactory.getLogFactoryProperties(set);
    }
    
    public void freezePersistentStore() throws StandardException {
        this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
        this.logFactory.freezePersistentStore();
    }
    
    public void unfreezePersistentStore() throws StandardException {
        this.logFactory.unfreezePersistentStore();
    }
    
    private boolean setupEncryptionEngines(final boolean b, final Properties properties) throws StandardException {
        final boolean true = isTrue(properties, "decryptDatabase");
        boolean true2 = isTrue(properties, "dataEncryption");
        boolean b2 = false;
        if (!b) {
            final String serviceName = Monitor.getMonitor().getServiceName(this);
            final PersistentService serviceType = Monitor.getMonitor().getServiceType(this);
            this.isEncryptedDatabase = isTrue(serviceType.getServiceProperties(serviceType.getCanonicalServiceName(serviceName), null), "dataEncryption");
            if (this.isEncryptedDatabase) {
                b2 = (true2 = (isSet(properties, "newBootPassword") || isSet(properties, "newEncryptionKey")));
            }
            else if (true2 && true) {
                throw StandardException.newException("XJ048.C", "decryptDatabase, dataEncryption");
            }
            if ((true2 || true) && this.isReadOnly()) {
                throw StandardException.newException("XBCXQ.S");
            }
        }
        if (this.isEncryptedDatabase || true2) {
            final boolean b3 = b || (true2 && !b2);
            final CipherFactoryBuilder cipherFactoryBuilder = (CipherFactoryBuilder)Monitor.startSystemModule("org.apache.derby.iapi.services.crypto.CipherFactoryBuilder");
            (this.currentCipherFactory = cipherFactoryBuilder.createCipherFactory(b3, properties, false)).verifyKey(b3, this.storageFactory, properties);
            this.encryptionEngine = this.currentCipherFactory.createNewCipher(1);
            if (b3) {
                this.encryptionBlockSize = this.encryptionEngine.getEncryptionBlockSize();
                if (b) {
                    properties.put("derby.encryptionBlockSize", String.valueOf(this.encryptionBlockSize));
                }
            }
            else if (isSet(properties, "derby.encryptionBlockSize")) {
                this.encryptionBlockSize = Integer.parseInt(properties.getProperty("derby.encryptionBlockSize"));
            }
            else {
                this.encryptionBlockSize = this.encryptionEngine.getEncryptionBlockSize();
            }
            this.decryptionEngine = this.currentCipherFactory.createNewCipher(2);
            this.random = this.currentCipherFactory.getSecureRandom();
            if (true2) {
                if (b2) {
                    this.newCipherFactory = cipherFactoryBuilder.createCipherFactory(b3, properties, true);
                    this.newDecryptionEngine = this.newCipherFactory.createNewCipher(2);
                    this.newEncryptionEngine = this.newCipherFactory.createNewCipher(1);
                }
                else {
                    this.newDecryptionEngine = this.decryptionEngine;
                    this.newEncryptionEngine = this.encryptionEngine;
                }
            }
            if (b) {
                this.currentCipherFactory.saveProperties(properties);
                this.isEncryptedDatabase = true;
            }
        }
        return !b && (true2 || (this.isEncryptedDatabase && true));
    }
    
    public int encrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final boolean b) throws StandardException {
        if (this.encryptionEngine == null && this.newEncryptionEngine == null) {
            throw StandardException.newException("XSAI3.S");
        }
        ++this.counter_encrypt;
        if (b) {
            return this.newEncryptionEngine.encrypt(array, n, n2, array2, n3);
        }
        return this.encryptionEngine.encrypt(array, n, n2, array2, n3);
    }
    
    public int decrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws StandardException {
        if (!this.isEncryptedDatabase || this.decryptionEngine == null) {
            throw StandardException.newException("XSAI3.S");
        }
        ++this.counter_decrypt;
        return this.decryptionEngine.decrypt(array, n, n2, array2, n3);
    }
    
    public int getEncryptionBlockSize() {
        return this.encryptionBlockSize;
    }
    
    public int random() {
        return this.isEncryptedDatabase ? this.random.nextInt() : 0;
    }
    
    public Serializable changeBootPassword(final Properties properties, final Serializable s) throws StandardException {
        if (this.isReadOnly()) {
            throw StandardException.newException("XBCX9.S");
        }
        if (!this.isEncryptedDatabase) {
            throw StandardException.newException("XBCX8.S");
        }
        if (s == null) {
            throw StandardException.newException("XBCX5.S");
        }
        if (!(s instanceof String)) {
            throw StandardException.newException("XBCX6.S");
        }
        final String s2 = (String)s;
        return this.currentCipherFactory.changeBootPassword((String)s, properties, this.encryptionEngine);
    }
    
    private void crashOnDebugFlag(final String s, final boolean b) throws StandardException {
    }
    
    private void applyBulkCryptoOperation(final Properties properties, final CipherFactory currentCipherFactory) throws StandardException {
        final boolean b = this.isEncryptedDatabase && isTrue(properties, "decryptDatabase");
        final boolean b2 = this.isEncryptedDatabase && (isSet(properties, "newBootPassword") || isSet(properties, "newEncryptionKey"));
        this.cryptoOperationAllowed(b2, b);
        final boolean set = isSet(properties, "encryptionKey");
        this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
        final RawTransaction startTransaction = this.xactFactory.startTransaction(this, ContextService.getFactory().getCurrentContextManager(), "UserTransaction");
        try {
            if (b) {
                this.dataFactory.decryptAllContainers(startTransaction);
            }
            else {
                this.dataFactory.encryptAllContainers(startTransaction);
            }
            if (!this.logFactory.isCheckpointInLastLogFile()) {
                this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
            }
            if (b) {
                this.isEncryptedDatabase = false;
                this.logFactory.setDatabaseEncrypted(false, true);
                this.dataFactory.setDatabaseEncrypted(false);
            }
            else {
                this.logFactory.setDatabaseEncrypted(true, true);
                if (b2) {
                    this.decryptionEngine = this.newDecryptionEngine;
                    this.encryptionEngine = this.newEncryptionEngine;
                    this.currentCipherFactory = currentCipherFactory;
                }
                else {
                    this.isEncryptedDatabase = true;
                    this.dataFactory.setDatabaseEncrypted(true);
                }
            }
            this.logFactory.startNewLogFile();
            properties.put("derby.storage.databaseEncryptionStatus", String.valueOf(1));
            if (b2) {
                if (set) {
                    final StorageFile storageFile = this.storageFactory.newStorageFile("verifyKey.dat");
                    final StorageFile storageFile2 = this.storageFactory.newStorageFile("verifyOldKey.dat");
                    if (!this.privCopyFile(storageFile, storageFile2)) {
                        throw StandardException.newException("XSRS5.S", storageFile, storageFile2);
                    }
                    this.currentCipherFactory.verifyKey(b2, this.storageFactory, properties);
                }
                else {
                    final String property = properties.getProperty("encryptedBootPassword");
                    if (property != null) {
                        properties.put("OldEncryptedBootPassword", property);
                    }
                }
            }
            else if (b) {
                properties.put("dataEncryption", "false");
            }
            else {
                properties.put("derby.encryptionBlockSize", String.valueOf(this.encryptionBlockSize));
            }
            this.currentCipherFactory.saveProperties(properties);
            startTransaction.commit();
            this.logFactory.checkpoint(this, this.dataFactory, this.xactFactory, true);
            properties.put("derby.storage.databaseEncryptionStatus", String.valueOf(3));
            this.dataFactory.removeOldVersionOfContainers();
            if (b) {
                this.removeCryptoProperties(properties);
            }
            else if (b2) {
                if (set) {
                    final StorageFile storageFile3 = this.storageFactory.newStorageFile("verifyOldKey.dat");
                    if (!this.privDelete(storageFile3)) {
                        throw StandardException.newException("XBM0R.D", storageFile3);
                    }
                }
                else {
                    properties.remove("OldEncryptedBootPassword");
                }
            }
            properties.remove("derby.storage.databaseEncryptionStatus");
            startTransaction.close();
        }
        catch (StandardException ex) {
            throw StandardException.newException("XBCXU.S", ex, ex.getMessage());
        }
        finally {
            this.newDecryptionEngine = null;
            this.newEncryptionEngine = null;
        }
    }
    
    public void handleIncompleteDbCryptoOperation(final Properties properties) throws StandardException {
        int int1 = 0;
        final String property = properties.getProperty("derby.storage.databaseEncryptionStatus");
        if (property != null) {
            int1 = Integer.parseInt(property);
        }
        boolean b = false;
        final boolean b2 = isSet(properties, "dataEncryption") && !isTrue(properties, "dataEncryption");
        if (int1 == 1) {
            if (this.logFactory.isCheckpointInLastLogFile()) {
                int1 = 3;
            }
            else {
                int1 = 2;
                properties.put("derby.storage.databaseEncryptionStatus", String.valueOf(int1));
            }
        }
        if (int1 == 2) {
            this.logFactory.deleteLogFileAfterCheckpointLogFile();
            final StorageFile storageFile = this.storageFactory.newStorageFile("verifyKey.dat");
            if (this.privExists(storageFile)) {
                final StorageFile storageFile2 = this.storageFactory.newStorageFile("verifyOldKey.dat");
                if (this.privExists(storageFile2)) {
                    if (!this.privCopyFile(storageFile2, storageFile)) {
                        throw StandardException.newException("XSRS5.S", storageFile2, storageFile);
                    }
                    b = true;
                }
                else if (!b2 && !this.privDelete(storageFile)) {
                    throw StandardException.newException("XBM0R.D", storageFile);
                }
            }
            else {
                final String property2 = properties.getProperty("OldEncryptedBootPassword");
                if (property2 != null) {
                    properties.put("encryptedBootPassword", property2);
                    b = true;
                }
            }
            if (!b2 && !b) {
                this.removeCryptoProperties(properties);
            }
        }
        if (int1 == 3) {
            this.dataFactory.removeOldVersionOfContainers();
        }
        final StorageFile storageFile3 = this.storageFactory.newStorageFile("verifyOldKey.dat");
        if (this.privExists(storageFile3)) {
            if (!this.privDelete(storageFile3)) {
                throw StandardException.newException("XBM0R.D", storageFile3);
            }
        }
        else {
            properties.remove("OldEncryptedBootPassword");
        }
        if (b2) {
            if (int1 == 2) {
                properties.setProperty("dataEncryption", "true");
            }
            else {
                this.removeCryptoProperties(properties);
            }
        }
        properties.remove("derby.storage.databaseEncryptionStatus");
    }
    
    private void cryptoOperationAllowed(final boolean b, final boolean b2) throws StandardException {
        String s;
        if (b2) {
            s = "decryptDatabase attribute";
        }
        else if (b) {
            s = "newBootPassword/newEncryptionKey attribute";
        }
        else {
            s = "dataEncryption attribute on an existing database";
        }
        this.logFactory.checkVersion(10, b2 ? 10 : 2, s);
        if (this.xactFactory.hasPreparedXact()) {
            throw StandardException.newException("XBCXO.S");
        }
        if (this.logFactory.logArchived()) {
            throw StandardException.newException("XBCXS.S");
        }
    }
    
    public StandardException markCorrupt(final StandardException ex) {
        this.logFactory.markCorrupt(ex);
        this.dataFactory.markCorrupt(ex);
        this.xactFactory.markCorrupt(ex);
        return ex;
    }
    
    public String getTransactionFactoryModule() {
        return "org.apache.derby.iapi.store.raw.xact.TransactionFactory";
    }
    
    public String getSlaveFactoryModule() {
        return "org.apache.derby.iapi.store.replication.slave.SlaveFactory";
    }
    
    public String getMasterFactoryModule() {
        return "org.apache.derby.iapi.store.replication.master.MasterFactory";
    }
    
    public String getDataFactoryModule() {
        return "org.apache.derby.iapi.store.raw.data.DataFactory";
    }
    
    public String getLogFactoryModule() {
        return "org.apache.derby.iapi.store.raw.log.LogFactory";
    }
    
    private void logHistory(final OutputStreamWriter outputStreamWriter, final String str) throws IOException {
        outputStreamWriter.write(new Date().toString() + ":" + str + "\n");
        outputStreamWriter.flush();
    }
    
    private String getFilePath(final StorageFile storageFile) {
        final String privGetCanonicalPath = this.privGetCanonicalPath(storageFile);
        if (privGetCanonicalPath != null) {
            return privGetCanonicalPath;
        }
        return storageFile.getPath();
    }
    
    private String getFilePath(final File file) {
        final String privGetCanonicalPath = this.privGetCanonicalPath(file);
        if (privGetCanonicalPath != null) {
            return privGetCanonicalPath;
        }
        return file.getPath();
    }
    
    protected boolean privCopyDirectory(final StorageFile storageFile, final File file) throws StandardException {
        return this.privCopyDirectory(storageFile, file, null, null, true);
    }
    
    protected boolean privCopyDirectory(final File file, final StorageFile storageFile) {
        return this.privCopyDirectory(file, storageFile, null, null);
    }
    
    public long getMaxContainerId() throws StandardException {
        return this.dataFactory.getMaxContainerId();
    }
    
    public boolean checkVersion(final int n, final int n2, final String s) throws StandardException {
        return this.logFactory.checkVersion(n, n2, s);
    }
    
    private void removeCryptoProperties(final Properties properties) {
        properties.remove("dataEncryption");
        properties.remove("log_encrypt_algorithm_version");
        properties.remove("data_encrypt_algorithm_version");
        properties.remove("derby.encryptionBlockSize");
        properties.remove("encryptionKeyLength");
        properties.remove("encryptionProvider");
        properties.remove("encryptionAlgorithm");
        properties.remove("encryptedBootPassword");
    }
    
    private synchronized OutputStreamWriter privFileWriter(final StorageFile actionStorageFile, final boolean actionAppend) throws IOException {
        this.actionCode = 1;
        this.actionStorageFile = actionStorageFile;
        this.actionAppend = actionAppend;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<OutputStreamWriter>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized boolean privExists(final File actionRegularFile) {
        this.actionCode = 2;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privExists(final StorageFile actionStorageFile) {
        this.actionCode = 3;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized OutputStreamWriter privGetOutputStreamWriter(final StorageFile actionStorageFile) throws IOException {
        this.actionCode = 19;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<OutputStreamWriter>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getException();
        }
    }
    
    private synchronized boolean privDelete(final File actionRegularFile) {
        this.actionCode = 4;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privDelete(final StorageFile actionStorageFile) {
        this.actionCode = 18;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized boolean privMkdirs(final File actionRegularFile) {
        this.actionCode = 5;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privIsDirectory(final File actionRegularFile) {
        this.actionCode = 6;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privRemoveDirectory(final File actionRegularFile) {
        this.actionCode = 7;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privRenameTo(final File actionRegularFile, final File actionRegularFile2) {
        this.actionCode = 8;
        this.actionRegularFile = actionRegularFile;
        this.actionRegularFile2 = actionRegularFile2;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionRegularFile = null;
            this.actionRegularFile2 = null;
        }
    }
    
    private synchronized boolean privCopyDirectory(final StorageFile actionStorageFile, final File actionRegularFile, final byte[] actionBuffer, final String[] actionFilter, final boolean actionCopySubDirs) throws StandardException {
        this.actionCode = 9;
        this.actionStorageFile = actionStorageFile;
        this.actionRegularFile = actionRegularFile;
        this.actionBuffer = actionBuffer;
        this.actionFilter = actionFilter;
        this.actionCopySubDirs = actionCopySubDirs;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getCause();
        }
        finally {
            this.actionStorageFile = null;
            this.actionRegularFile = null;
            this.actionBuffer = null;
            this.actionFilter = null;
        }
    }
    
    private synchronized boolean privCopyDirectory(final File actionRegularFile, final StorageFile actionStorageFile, final byte[] actionBuffer, final String[] actionFilter) {
        this.actionCode = 10;
        this.actionStorageFile = actionStorageFile;
        this.actionRegularFile = actionRegularFile;
        this.actionBuffer = actionBuffer;
        this.actionFilter = actionFilter;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
            this.actionRegularFile = null;
            this.actionBuffer = null;
            this.actionFilter = null;
        }
    }
    
    private synchronized boolean privCopyFile(final File actionRegularFile, final StorageFile actionStorageFile) {
        this.actionCode = 11;
        this.actionStorageFile = actionStorageFile;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privCopyFile(final StorageFile actionStorageFile, final File actionRegularFile) throws StandardException {
        this.actionCode = 14;
        this.actionStorageFile = actionStorageFile;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            throw (StandardException)ex.getCause();
        }
        finally {
            this.actionStorageFile = null;
            this.actionRegularFile = null;
        }
    }
    
    private synchronized boolean privCopyFile(final StorageFile actionStorageFile, final StorageFile actionToStorageFile) {
        this.actionCode = 17;
        this.actionStorageFile = actionStorageFile;
        this.actionToStorageFile = actionToStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Boolean>)this);
        }
        catch (PrivilegedActionException ex) {
            return false;
        }
        finally {
            this.actionStorageFile = null;
            this.actionToStorageFile = null;
        }
    }
    
    private synchronized String[] privList(final StorageFile actionStorageFile) {
        this.actionCode = 13;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)this);
        }
        catch (PrivilegedActionException ex) {
            return null;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized String privGetCanonicalPath(final StorageFile actionStorageFile) {
        this.actionCode = 16;
        this.actionStorageFile = actionStorageFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String>)this);
        }
        catch (PrivilegedActionException ex) {
            return null;
        }
        catch (SecurityException ex2) {
            return null;
        }
        finally {
            this.actionStorageFile = null;
        }
    }
    
    private synchronized String privGetCanonicalPath(final File actionRegularFile) {
        this.actionCode = 15;
        this.actionRegularFile = actionRegularFile;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String>)this);
        }
        catch (PrivilegedActionException ex) {
            return null;
        }
        catch (SecurityException ex2) {
            return null;
        }
        finally {
            this.actionRegularFile = null;
        }
    }
    
    public final Object run() throws IOException, StandardException {
        switch (this.actionCode) {
            case 1: {
                return new OutputStreamWriter(this.actionStorageFile.getOutputStream(this.actionAppend));
            }
            case 2: {
                return ReuseFactory.getBoolean(this.actionRegularFile.exists());
            }
            case 3: {
                return ReuseFactory.getBoolean(this.actionStorageFile.exists());
            }
            case 4: {
                return ReuseFactory.getBoolean(this.actionRegularFile.delete());
            }
            case 18: {
                return ReuseFactory.getBoolean(this.actionStorageFile.delete());
            }
            case 5: {
                final boolean mkdirs = this.actionRegularFile.mkdirs();
                FileUtil.limitAccessToOwner(this.actionRegularFile);
                return ReuseFactory.getBoolean(mkdirs);
            }
            case 6: {
                return ReuseFactory.getBoolean(this.actionRegularFile.isDirectory());
            }
            case 7: {
                return ReuseFactory.getBoolean(FileUtil.removeDirectory(this.actionRegularFile));
            }
            case 8: {
                return ReuseFactory.getBoolean(this.actionRegularFile.renameTo(this.actionRegularFile2));
            }
            case 9: {
                return ReuseFactory.getBoolean(FileUtil.copyDirectory(this.storageFactory, this.actionStorageFile, this.actionRegularFile, this.actionBuffer, this.actionFilter, this.actionCopySubDirs));
            }
            case 10: {
                return ReuseFactory.getBoolean(FileUtil.copyDirectory((WritableStorageFactory)this.storageFactory, this.actionRegularFile, this.actionStorageFile, this.actionBuffer, this.actionFilter));
            }
            case 11: {
                return ReuseFactory.getBoolean(FileUtil.copyFile((WritableStorageFactory)this.storageFactory, this.actionRegularFile, this.actionStorageFile));
            }
            case 12: {
                return this.actionRegularFile.list();
            }
            case 13: {
                return this.actionStorageFile.list();
            }
            case 14: {
                return ReuseFactory.getBoolean(FileUtil.copyFile(this.storageFactory, this.actionStorageFile, this.actionRegularFile));
            }
            case 17: {
                return ReuseFactory.getBoolean(FileUtil.copyFile((WritableStorageFactory)this.storageFactory, this.actionStorageFile, this.actionToStorageFile));
            }
            case 15: {
                return this.actionRegularFile.getCanonicalPath();
            }
            case 16: {
                return this.actionStorageFile.getCanonicalPath();
            }
            case 19: {
                return new OutputStreamWriter(this.actionStorageFile.getOutputStream(), "UTF8");
            }
            default: {
                return null;
            }
        }
    }
    
    private static boolean isSet(final Properties properties, final String key) {
        return properties.getProperty(key) != null;
    }
    
    private static boolean isTrue(final Properties properties, final String key) {
        return Boolean.valueOf(properties.getProperty(key));
    }
    
    public void createDataWarningFile() throws StandardException {
        final StorageFile storageFile = this.storageFactory.newStorageFile("seg0", "README_DO_NOT_TOUCH_FILES.txt");
        Writer privGetOutputStreamWriter = null;
        if (!this.privExists(storageFile)) {
            try {
                privGetOutputStreamWriter = this.privGetOutputStreamWriter(storageFile);
                privGetOutputStreamWriter.write(MessageService.getTextMessage("M007"));
            }
            catch (IOException ex) {}
            finally {
                if (privGetOutputStreamWriter != null) {
                    try {
                        ((OutputStreamWriter)privGetOutputStreamWriter).close();
                    }
                    catch (IOException ex2) {}
                }
            }
        }
    }
    
    static {
        TEST_REENCRYPT_CRASH_BEFORE_COMMT = null;
        TEST_REENCRYPT_CRASH_AFTER_COMMT = null;
        TEST_REENCRYPT_CRASH_AFTER_SWITCH_TO_NEWKEY = null;
        TEST_REENCRYPT_CRASH_AFTER_CHECKPOINT = null;
        TEST_REENCRYPT_CRASH_AFTER_RECOVERY_UNDO_LOGFILE_DELETE = null;
        TEST_REENCRYPT_CRASH_AFTER_RECOVERY_UNDO_REVERTING_KEY = null;
        TEST_REENCRYPT_CRASH_BEFORE_RECOVERY_FINAL_CLEANUP = null;
    }
}
