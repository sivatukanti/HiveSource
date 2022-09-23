// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.iapi.services.io.FileUtil;
import org.apache.derby.iapi.util.InterruptStatus;
import java.security.CodeSource;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.util.Matchable;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.daemon.Serviceable;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.store.raw.StreamContainerHandle;
import org.apache.derby.iapi.store.access.RowSource;
import org.apache.derby.iapi.store.raw.Page;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.store.raw.Loggable;
import org.apache.derby.iapi.store.raw.data.RawContainerHandle;
import org.apache.derby.iapi.store.raw.ContainerHandle;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import org.apache.derby.iapi.store.raw.xact.RawTransaction;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.services.daemon.DaemonService;
import org.apache.derby.iapi.services.monitor.PersistentService;
import org.apache.derby.iapi.services.cache.CacheFactory;
import org.apache.derby.iapi.services.i18n.MessageService;
import java.util.Date;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import java.io.File;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.store.raw.ContainerKey;
import java.util.Hashtable;
import org.apache.derby.iapi.store.access.FileResource;
import org.apache.derby.iapi.services.stream.HeaderPrintWriter;
import org.apache.derby.io.StorageFile;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.io.WritableStorageFactory;
import org.apache.derby.io.StorageFactory;
import java.security.PrivilegedExceptionAction;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.store.raw.data.DataFactory;

public class BaseDataFileFactory implements DataFactory, CacheableFactory, ModuleControl, ModuleSupportable, PrivilegedExceptionAction
{
    StorageFactory storageFactory;
    WritableStorageFactory writableStorageFactory;
    private long nextContainerId;
    private boolean databaseEncrypted;
    private CacheManager pageCache;
    private CacheManager containerCache;
    private LogFactory logFactory;
    private ProductVersionHolder jbmsVersion;
    private String jvmVersion;
    private String osInfo;
    private String jarCPath;
    private RawStoreFactory rawStoreFactory;
    private String dataDirectory;
    private boolean throwDBlckException;
    private UUID identifier;
    private final Object freezeSemaphore;
    private boolean isFrozen;
    private int writersInProgress;
    private boolean removeStubsOK;
    private boolean isCorrupt;
    private boolean inCreateNoLog;
    private StorageRandomAccessFile fileLockOnDB;
    private StorageFile exFileLock;
    private HeaderPrintWriter istream;
    private static final String LINE = "----------------------------------------------------------------";
    boolean dataNotSyncedAtAllocation;
    boolean dataNotSyncedAtCheckpoint;
    private PageActions loggablePageActions;
    private AllocationActions loggableAllocActions;
    private boolean readOnly;
    private boolean supportsRandomAccess;
    private FileResource fileHandler;
    private Hashtable droppedTableStubInfo;
    private Hashtable postRecoveryRemovedFiles;
    private int actionCode;
    private static final int REMOVE_TEMP_DIRECTORY_ACTION = 2;
    private static final int GET_CONTAINER_PATH_ACTION = 3;
    private static final int GET_ALTERNATE_CONTAINER_PATH_ACTION = 4;
    private static final int FIND_MAX_CONTAINER_ID_ACTION = 5;
    private static final int DELETE_IF_EXISTS_ACTION = 6;
    private static final int GET_PATH_ACTION = 7;
    private static final int POST_RECOVERY_REMOVE_ACTION = 8;
    private static final int REMOVE_STUBS_ACTION = 9;
    private static final int BOOT_ACTION = 10;
    private static final int GET_LOCK_ON_DB_ACTION = 11;
    private static final int RELEASE_LOCK_ON_DB_ACTION = 12;
    private static final int RESTORE_DATA_DIRECTORY_ACTION = 13;
    private static final int GET_CONTAINER_NAMES_ACTION = 14;
    private ContainerKey containerId;
    private boolean stub;
    private StorageFile actionFile;
    private UUID myUUID;
    private UUIDFactory uuidFactory;
    private String databaseDirectory;
    private File backupRoot;
    private String[] bfilelist;
    
    public BaseDataFileFactory() {
        this.nextContainerId = System.currentTimeMillis();
        this.freezeSemaphore = new Object();
        this.dataNotSyncedAtAllocation = true;
        this.dataNotSyncedAtCheckpoint = false;
    }
    
    public boolean canSupport(final Properties properties) {
        final String property = properties.getProperty("derby.__rt.serviceType");
        return property != null && this.handleServiceType(property) && properties.getProperty("derby.__rt.serviceDirectory") != null;
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        this.jbmsVersion = Monitor.getMonitor().getEngineVersion();
        this.jvmVersion = buildJvmVersion();
        this.osInfo = buildOSinfo();
        this.jarCPath = jarClassPath(this.getClass());
        this.dataDirectory = properties.getProperty("derby.__rt.serviceDirectory");
        final UUIDFactory uuidFactory = Monitor.getMonitor().getUUIDFactory();
        this.identifier = uuidFactory.createUUID();
        final PersistentService serviceType = Monitor.getMonitor().getServiceType(this);
        try {
            this.storageFactory = serviceType.getStorageFactoryInstance(true, this.dataDirectory, properties.getProperty("derby.storage.tempDirectory", PropertyUtil.getSystemProperty("derby.storage.tempDirectory")), this.identifier.toANSIidentifier());
        }
        catch (IOException ex) {
            if (b) {
                throw StandardException.newException("XBM0H.D", ex, this.dataDirectory);
            }
            throw StandardException.newException("XJ004.C", ex, this.dataDirectory);
        }
        if (this.storageFactory instanceof WritableStorageFactory) {
            this.writableStorageFactory = (WritableStorageFactory)this.storageFactory;
        }
        this.actionCode = 10;
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
        }
        catch (PrivilegedActionException ex3) {}
        final String property = properties.getProperty("derby.database.forceDatabaseLock", PropertyUtil.getSystemProperty("derby.database.forceDatabaseLock"));
        this.throwDBlckException = Boolean.valueOf((property != null) ? property.trim() : property);
        if (!this.isReadOnly()) {
            this.getJBMSLockOnDB(this.identifier, uuidFactory, this.dataDirectory);
        }
        String s = properties.getProperty("createFrom");
        if (s == null) {
            s = properties.getProperty("restoreFrom");
        }
        if (s == null) {
            s = properties.getProperty("rollForwardRecoveryFrom");
        }
        if (s != null) {
            try {
                this.databaseEncrypted = Boolean.valueOf(properties.getProperty("dataEncryption"));
                this.restoreDataDirectory(s);
            }
            catch (StandardException ex2) {
                this.releaseJBMSLockOnDB();
                throw ex2;
            }
        }
        this.logMsg("----------------------------------------------------------------");
        final String s2 = this.isReadOnly() ? "D000" : "D001";
        final boolean booleanValue = Boolean.valueOf(properties.getProperty("derby.stream.error.logBootTrace", PropertyUtil.getSystemProperty("derby.stream.error.logBootTrace")));
        this.logMsg(new Date() + MessageService.getTextMessage(s2, this.jbmsVersion, this.identifier, this.dataDirectory, this.getClass().getClassLoader(), this.jarCPath));
        this.logMsg(this.jvmVersion);
        this.logMsg(this.osInfo);
        this.logMsg("derby.system.home=" + PropertyUtil.getSystemProperty("derby.system.home"));
        final String systemProperty = PropertyUtil.getSystemProperty("derby.stream.error.file");
        if (systemProperty != null) {
            this.logMsg("derby.stream.error.file=" + systemProperty);
        }
        final String systemProperty2 = PropertyUtil.getSystemProperty("derby.stream.error.method");
        if (systemProperty2 != null) {
            this.logMsg("derby.stream.error.method=" + systemProperty2);
        }
        final String systemProperty3 = PropertyUtil.getSystemProperty("derby.stream.error.field");
        if (systemProperty3 != null) {
            this.logMsg("derby.stream.error.field=" + systemProperty3);
        }
        if (booleanValue) {
            Monitor.logThrowable(new Throwable("boot trace"));
        }
        final CacheFactory cacheFactory = (CacheFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.cache.CacheFactory");
        final int intParameter = this.getIntParameter("derby.storage.pageCacheSize", null, 1000, 40, Integer.MAX_VALUE);
        this.pageCache = cacheFactory.newCacheManager(this, "PageCache", intParameter / 2, intParameter);
        final int intParameter2 = this.getIntParameter("derby.storage.fileCacheSize", null, 100, 2, Integer.MAX_VALUE);
        this.containerCache = cacheFactory.newCacheManager(this, "ContainerCache", intParameter2 / 2, intParameter2);
        if (b) {
            final String property2 = properties.getProperty("derby.__rt.storage.createWithNoLog");
            this.inCreateNoLog = (property2 != null && Boolean.valueOf(property2));
        }
        this.droppedTableStubInfo = new Hashtable();
        if ("test".equalsIgnoreCase(PropertyUtil.getSystemProperty("derby.system.durability"))) {
            this.dataNotSyncedAtCheckpoint = true;
            Monitor.logMessage(MessageService.getTextMessage("D013", "derby.system.durability", "test"));
        }
        this.fileHandler = new RFResource(this);
    }
    
    public void stop() {
        boolean b = false;
        if (this.rawStoreFactory != null) {
            final DaemonService daemon = this.rawStoreFactory.getDaemon();
            if (daemon != null) {
                daemon.stop();
            }
        }
        final boolean systemBoolean = PropertyUtil.getSystemBoolean("derby.stream.error.logBootTrace");
        this.logMsg("----------------------------------------------------------------");
        this.logMsg(new Date() + MessageService.getTextMessage("D002", this.getIdentifier(), this.getRootDirectory(), this.getClass().getClassLoader()));
        if (systemBoolean) {
            Monitor.logThrowable(new Throwable("shutdown trace"));
        }
        if (!this.isCorrupt) {
            try {
                if (this.pageCache != null && this.containerCache != null) {
                    this.pageCache.shutdown();
                    this.containerCache.shutdown();
                    b = true;
                }
            }
            catch (StandardException ex) {
                ex.printStackTrace(this.istream.getPrintWriter());
            }
        }
        this.removeTempDirectory();
        if (this.isReadOnly()) {
            if (this.storageFactory != null) {
                this.storageFactory.shutdown();
            }
            return;
        }
        if (this.removeStubsOK && b) {
            this.removeStubs();
        }
        this.releaseJBMSLockOnDB();
        if (this.writableStorageFactory != null) {
            this.writableStorageFactory.shutdown();
        }
    }
    
    public Cacheable newCacheable(final CacheManager cacheManager) {
        if (cacheManager == this.pageCache) {
            final StoredPage storedPage = new StoredPage();
            storedPage.setFactory(this);
            return storedPage;
        }
        return this.newContainerObject();
    }
    
    public void createFinished() throws StandardException {
        if (!this.inCreateNoLog) {
            throw StandardException.newException("XSDG5.D");
        }
        this.checkpoint();
        this.inCreateNoLog = false;
    }
    
    public ContainerHandle openContainer(final RawTransaction rawTransaction, final ContainerKey containerKey, final LockingPolicy lockingPolicy, final int n) throws StandardException {
        return this.openContainer(rawTransaction, containerKey, lockingPolicy, n, false);
    }
    
    public RawContainerHandle openDroppedContainer(final RawTransaction rawTransaction, final ContainerKey containerKey, final LockingPolicy lockingPolicy, int n) throws StandardException {
        n |= 0x400;
        return this.openContainer(rawTransaction, containerKey, lockingPolicy, n, true);
    }
    
    private RawContainerHandle openContainer(final RawTransaction rawTransaction, final ContainerKey containerKey, LockingPolicy lockingPolicy, int n, final boolean b) throws StandardException {
        final boolean b2 = (n & 0x80) == 0x0;
        if ((n & 0x40) != 0x0) {
            final BaseContainerHandle baseContainerHandle = new BaseContainerHandle(this.getIdentifier(), rawTransaction, containerKey, lockingPolicy, n);
            if (baseContainerHandle.useContainer(true, b2)) {
                return baseContainerHandle;
            }
            return null;
        }
        else {
            final FileContainer fileContainer = (FileContainer)this.containerCache.find(containerKey);
            if (fileContainer == null) {
                return null;
            }
            if (containerKey.getSegmentId() == -1L) {
                if ((n & 0x800) == 0x800) {
                    n |= 0x1;
                }
                else {
                    n |= 0x101;
                }
                lockingPolicy = rawTransaction.newLockingPolicy(0, 0, true);
            }
            else {
                if (this.inCreateNoLog) {
                    n |= 0x3;
                }
                else if (this.logFactory.logArchived() || this.logFactory.inReplicationMasterMode()) {
                    n &= 0xFFFFFFFC;
                }
                else if (((n & 0x1) == 0x1 || (n & 0x2) == 0x2) && !rawTransaction.blockBackup(false)) {
                    n &= 0xFFFFFFFC;
                }
                if ((n & 0x1) == 0x1 && (n & 0x2) == 0x0) {
                    n |= 0x200;
                }
            }
            PageActions loggablePageActions = null;
            AllocationActions loggableAllocationActions = null;
            if ((n & 0x4) == 0x4) {
                if ((n & 0x1) == 0x0) {
                    loggablePageActions = this.getLoggablePageActions();
                    loggableAllocationActions = this.getLoggableAllocationActions();
                }
                else {
                    loggablePageActions = new DirectActions();
                    loggableAllocationActions = new DirectAllocActions();
                }
            }
            final BaseContainerHandle baseContainerHandle2 = new BaseContainerHandle(this.getIdentifier(), rawTransaction, loggablePageActions, loggableAllocationActions, lockingPolicy, fileContainer, n);
            try {
                if (!baseContainerHandle2.useContainer(b, b2)) {
                    this.containerCache.release(fileContainer);
                    return null;
                }
            }
            catch (StandardException ex) {
                this.containerCache.release(fileContainer);
                throw ex;
            }
            return baseContainerHandle2;
        }
    }
    
    public long addContainer(final RawTransaction rawTransaction, final long n, final long n2, int n3, final Properties properties, final int n4) throws StandardException {
        final long n5 = (n2 != 0L) ? n2 : this.getNextId();
        final ContainerKey containerKey = new ContainerKey(n, n5);
        final boolean b = n == -1L;
        ContainerHandle openContainer = null;
        LockingPolicy lockingPolicy = null;
        if (!b) {
            if (this.isReadOnly()) {
                throw StandardException.newException("40XD1");
            }
            lockingPolicy = rawTransaction.newLockingPolicy(2, 5, true);
            openContainer = rawTransaction.openContainer(containerKey, lockingPolicy, 68);
        }
        final FileContainer fileContainer = (FileContainer)this.containerCache.create(containerKey, properties);
        ContainerHandle openContainer2 = null;
        Page addPage = null;
        try {
            if (b && (n4 & 0x2) == 0x2) {
                n3 |= 0x800;
            }
            openContainer2 = rawTransaction.openContainer(containerKey, null, 0x4 | n3);
            if (!b) {
                final RawContainerHandle rawContainerHandle = (RawContainerHandle)openContainer2;
                final ContainerOperation containerOperation = new ContainerOperation(rawContainerHandle, (byte)1);
                rawContainerHandle.preDirty(true);
                try {
                    rawTransaction.logAndDo(containerOperation);
                    this.flush(rawTransaction.getLastLogInstant());
                }
                finally {
                    rawContainerHandle.preDirty(false);
                }
            }
            addPage = openContainer2.addPage();
        }
        finally {
            if (addPage != null) {
                addPage.unlatch();
            }
            this.containerCache.release(fileContainer);
            if (openContainer2 != null) {
                openContainer2.close();
            }
            if (!b) {
                lockingPolicy.unlockContainer(rawTransaction, openContainer);
            }
        }
        return n5;
    }
    
    public long addAndLoadStreamContainer(final RawTransaction rawTransaction, final long n, final Properties properties, final RowSource rowSource) throws StandardException {
        final long nextId = this.getNextId();
        new StreamFileContainer(new ContainerKey(n, nextId), this, properties).load(rowSource);
        return nextId;
    }
    
    public StreamContainerHandle openStreamContainer(final RawTransaction rawTransaction, final long n, final long n2, final boolean b) throws StandardException {
        final StreamFileContainer open = new StreamFileContainer(new ContainerKey(n, n2), this).open(false);
        if (open == null) {
            return null;
        }
        final StreamFileContainerHandle streamFileContainerHandle = new StreamFileContainerHandle(this.getIdentifier(), rawTransaction, open, b);
        if (streamFileContainerHandle.useContainer()) {
            return streamFileContainerHandle;
        }
        return null;
    }
    
    public void dropStreamContainer(final RawTransaction rawTransaction, final long n, final long n2) throws StandardException {
        final boolean b = n == -1L;
        StreamContainerHandle openStreamContainer = null;
        try {
            rawTransaction.notifyObservers(new ContainerKey(n, n2));
            openStreamContainer = rawTransaction.openStreamContainer(n, n2, false);
            if (b && openStreamContainer != null) {
                openStreamContainer.removeContainer();
            }
        }
        finally {
            if (openStreamContainer != null) {
                openStreamContainer.close();
            }
        }
    }
    
    public void reCreateContainerForRedoRecovery(final RawTransaction rawTransaction, final long n, final long n2, final ByteArray byteArray) throws StandardException {
        this.containerCache.release(this.containerCache.create(new ContainerKey(n, n2), byteArray));
    }
    
    public void dropContainer(final RawTransaction rawTransaction, final ContainerKey containerKey) throws StandardException {
        final boolean b = containerKey.getSegmentId() == -1L;
        LockingPolicy lockingPolicy = null;
        if (!b) {
            if (this.isReadOnly()) {
                throw StandardException.newException("40XD1");
            }
            lockingPolicy = rawTransaction.newLockingPolicy(2, 5, true);
        }
        rawTransaction.notifyObservers(containerKey);
        final RawContainerHandle rawContainerHandle = (RawContainerHandle)rawTransaction.openContainer(containerKey, lockingPolicy, 4);
        try {
            if (rawContainerHandle == null || rawContainerHandle.getContainerStatus() != 1) {
                if (b) {
                    if (rawContainerHandle != null) {
                        rawContainerHandle.removeContainer(null);
                    }
                    return;
                }
                throw StandardException.newException("40XD2", containerKey);
            }
            else if (b) {
                rawContainerHandle.dropContainer(null, true);
                rawContainerHandle.removeContainer(null);
            }
            else {
                final ContainerOperation containerOperation = new ContainerOperation(rawContainerHandle, (byte)2);
                rawContainerHandle.preDirty(true);
                try {
                    rawTransaction.logAndDo(containerOperation);
                }
                finally {
                    rawContainerHandle.preDirty(false);
                }
                rawTransaction.addPostCommitWork(new ReclaimSpace(1, containerKey, this, true));
            }
        }
        finally {
            if (rawContainerHandle != null) {
                rawContainerHandle.close();
            }
        }
    }
    
    public void checkpoint() throws StandardException {
        this.pageCache.cleanAll();
        this.containerCache.cleanAll();
    }
    
    public void idle() throws StandardException {
        this.pageCache.ageOut();
        this.containerCache.ageOut();
    }
    
    public void setRawStoreFactory(final RawStoreFactory rawStoreFactory, final boolean b, final Properties properties) throws StandardException {
        this.rawStoreFactory = rawStoreFactory;
        this.bootLogFactory(b, properties);
    }
    
    public UUID getIdentifier() {
        return this.identifier;
    }
    
    public int reclaimSpace(final Serviceable serviceable, final ContextManager contextManager) throws StandardException {
        if (serviceable == null) {
            return 1;
        }
        return ReclaimSpaceHelper.reclaimSpace(this, (RawTransaction)this.rawStoreFactory.findUserTransaction(contextManager, "SystemTransaction"), (ReclaimSpace)serviceable);
    }
    
    public StandardException markCorrupt(final StandardException ex) {
        final boolean b = !this.isCorrupt;
        this.isCorrupt = true;
        if (this.getLogFactory() != null) {
            this.getLogFactory().markCorrupt(ex);
        }
        if (b) {
            if (this.pageCache != null) {
                this.pageCache.discard(null);
            }
            if (this.containerCache != null) {
                this.containerCache.discard(null);
            }
            this.pageCache = null;
            this.containerCache = null;
            this.releaseJBMSLockOnDB();
        }
        return ex;
    }
    
    public FileResource getFileHandler() {
        return this.fileHandler;
    }
    
    public void removeStubsOK() {
        this.removeStubsOK = true;
    }
    
    public int getIntParameter(final String key, final Properties properties, final int n, final int n2, final int n3) {
        String s = null;
        if (properties != null) {
            s = properties.getProperty(key);
        }
        if (s == null) {
            s = PropertyUtil.getSystemProperty(key);
        }
        if (s != null) {
            try {
                final int int1 = Integer.parseInt(s);
                if (int1 >= n2 && int1 <= n3) {
                    return int1;
                }
            }
            catch (NumberFormatException ex) {}
        }
        return n;
    }
    
    CacheManager getContainerCache() {
        return this.containerCache;
    }
    
    CacheManager getPageCache() {
        return this.pageCache;
    }
    
    void flush(final LogInstant logInstant) throws StandardException {
        this.getLogFactory().flush(logInstant);
    }
    
    LogFactory getLogFactory() {
        return this.logFactory;
    }
    
    RawStoreFactory getRawStoreFactory() {
        return this.rawStoreFactory;
    }
    
    public String getRootDirectory() {
        return this.dataDirectory;
    }
    
    Cacheable newContainerObject() {
        if (this.supportsRandomAccess) {
            return this.newRAFContainer(this);
        }
        return new InputStreamContainer(this);
    }
    
    protected Cacheable newRAFContainer(final BaseDataFileFactory baseDataFileFactory) {
        return new RAFContainer(baseDataFileFactory);
    }
    
    private PageActions getLoggablePageActions() throws StandardException {
        if (this.loggablePageActions == null) {
            this.loggablePageActions = new LoggableActions();
        }
        return this.loggablePageActions;
    }
    
    private AllocationActions getLoggableAllocationActions() {
        if (this.loggableAllocActions == null) {
            this.loggableAllocActions = new LoggableAllocActions();
        }
        return this.loggableAllocActions;
    }
    
    private synchronized void removeTempDirectory() {
        if (this.storageFactory != null) {
            this.actionCode = 2;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
            }
            catch (PrivilegedActionException ex) {}
        }
    }
    
    public StorageFile getContainerPath(final ContainerKey containerKey, final boolean b) {
        return this.getContainerPath(containerKey, b, 3);
    }
    
    private synchronized StorageFile getContainerPath(final ContainerKey containerId, final boolean stub, final int actionCode) {
        this.actionCode = actionCode;
        try {
            this.containerId = containerId;
            this.stub = stub;
            try {
                return AccessController.doPrivileged((PrivilegedExceptionAction<StorageFile>)this);
            }
            catch (PrivilegedActionException ex) {
                return null;
            }
        }
        finally {
            this.containerId = null;
        }
    }
    
    public StorageFile getAlternateContainerPath(final ContainerKey containerKey, final boolean b) {
        return this.getContainerPath(containerKey, b, 4);
    }
    
    private synchronized void removeStubs() {
        if (this.storageFactory != null) {
            this.actionCode = 9;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
            }
            catch (PrivilegedActionException ex) {}
        }
    }
    
    public void stubFileToRemoveAfterCheckPoint(final StorageFile storageFile, final LogInstant key, final Object o) {
        if (this.droppedTableStubInfo != null) {
            this.droppedTableStubInfo.put(key, new Object[] { storageFile, o });
        }
    }
    
    public void removeDroppedContainerFileStubs(final LogInstant logInstant) throws StandardException {
        if (this.droppedTableStubInfo != null) {
            synchronized (this.droppedTableStubInfo) {
                final Enumeration<LogInstant> keys = this.droppedTableStubInfo.keys();
                while (keys.hasMoreElements()) {
                    final LogInstant logInstant2 = keys.nextElement();
                    if (logInstant2.lessThan(logInstant)) {
                        final Object[] array = this.droppedTableStubInfo.get(logInstant2);
                        final Cacheable cached = this.containerCache.findCached(array[1]);
                        if (cached != null) {
                            this.containerCache.remove(cached);
                        }
                        synchronized (this) {
                            this.actionFile = (StorageFile)array[0];
                            this.actionCode = 6;
                            try {
                                if (AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this) == null) {
                                    continue;
                                }
                                this.droppedTableStubInfo.remove(logInstant2);
                            }
                            catch (PrivilegedActionException ex) {}
                        }
                    }
                }
            }
        }
    }
    
    private synchronized long findMaxContainerId() {
        this.actionCode = 5;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Long>)this);
        }
        catch (PrivilegedActionException ex) {
            return 0L;
        }
    }
    
    private void bootLogFactory(final boolean b, final Properties properties) throws StandardException {
        if (this.isReadOnly()) {
            properties.put("derby.__rt.storage.log", "readonly");
        }
        this.logFactory = (LogFactory)Monitor.bootServiceModule(b, this, this.rawStoreFactory.getLogFactoryModule(), properties);
    }
    
    private boolean handleServiceType(final String s) {
        try {
            final PersistentService serviceProvider = Monitor.getMonitor().getServiceProvider(s);
            return serviceProvider != null && serviceProvider.hasStorageFactory();
        }
        catch (StandardException ex) {
            return false;
        }
    }
    
    private void getJBMSLockOnDB(final UUID myUUID, final UUIDFactory uuidFactory, final String databaseDirectory) throws StandardException {
        if (this.fileLockOnDB != null) {
            return;
        }
        if (this.isReadOnly()) {
            return;
        }
        synchronized (this) {
            this.actionCode = 11;
            this.myUUID = myUUID;
            this.uuidFactory = uuidFactory;
            this.databaseDirectory = databaseDirectory;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
            }
            catch (PrivilegedActionException ex) {
                throw (StandardException)ex.getException();
            }
            finally {
                this.myUUID = null;
                this.uuidFactory = null;
                this.databaseDirectory = null;
            }
        }
    }
    
    private void privGetJBMSLockOnDB() throws StandardException {
        boolean b = false;
        Object utf = null;
        final StorageFile storageFile = this.storageFactory.newStorageFile("db.lck");
        try {
            if (storageFile.exists()) {
                b = true;
                this.fileLockOnDB = storageFile.getRandomAccessFile("rw");
                try {
                    utf = this.fileLockOnDB.readUTF();
                }
                catch (IOException ex) {
                    b = false;
                }
                this.fileLockOnDB.close();
                this.fileLockOnDB = null;
                if (!storageFile.delete()) {
                    throw StandardException.newException("XSDB6.D", this.databaseDirectory);
                }
            }
            this.fileLockOnDB = storageFile.getRandomAccessFile("rw");
            storageFile.limitAccessToOwner();
            this.fileLockOnDB.writeUTF(this.myUUID.toString());
            this.fileLockOnDB.sync();
            this.fileLockOnDB.seek(0L);
            if (!this.uuidFactory.recreateUUID(this.fileLockOnDB.readUTF()).equals(this.myUUID)) {
                throw StandardException.newException("XSDB6.D", this.databaseDirectory);
            }
        }
        catch (IOException ex2) {
            this.readOnly = true;
            try {
                if (this.fileLockOnDB != null) {
                    this.fileLockOnDB.close();
                }
            }
            catch (IOException ex3) {}
            this.fileLockOnDB = null;
            return;
        }
        if (storageFile.delete()) {
            final Object[] array = { this.myUUID, this.databaseDirectory, utf };
            int exclusiveFileLock = 0;
            if (!this.throwDBlckException) {
                this.exFileLock = this.storageFactory.newStorageFile("dbex.lck");
                exclusiveFileLock = this.exFileLock.getExclusiveFileLock();
            }
            if (exclusiveFileLock == 0 && b && !this.throwDBlckException) {
                final String completeMessage = MessageService.getCompleteMessage("XSDB7.D", array);
                this.logMsg(completeMessage);
                System.err.println(completeMessage);
            }
            try {
                if (this.fileLockOnDB != null) {
                    this.fileLockOnDB.close();
                }
                this.fileLockOnDB = storageFile.getRandomAccessFile("rw");
                storageFile.limitAccessToOwner();
                this.fileLockOnDB.writeUTF(this.myUUID.toString());
                this.fileLockOnDB.sync();
                this.fileLockOnDB.close();
            }
            catch (IOException ex4) {
                try {
                    this.fileLockOnDB.close();
                }
                catch (IOException ex5) {}
            }
            finally {
                this.fileLockOnDB = null;
            }
            if (b && this.throwDBlckException) {
                throw StandardException.newException("XSDB8.D", array);
            }
            if (exclusiveFileLock == 2) {
                throw StandardException.newException("XSDB6.D", this.databaseDirectory);
            }
        }
    }
    
    private void releaseJBMSLockOnDB() {
        if (this.isReadOnly()) {
            return;
        }
        synchronized (this) {
            this.actionCode = 12;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
            }
            catch (PrivilegedActionException ex) {}
            finally {
                this.fileLockOnDB = null;
            }
        }
    }
    
    private void privReleaseJBMSLockOnDB() throws IOException {
        if (this.fileLockOnDB != null) {
            this.fileLockOnDB.close();
        }
        if (this.storageFactory != null) {
            this.storageFactory.newStorageFile("db.lck").delete();
        }
        if (this.exFileLock != null) {
            this.exFileLock.releaseExclusiveFileLock();
        }
    }
    
    private void logMsg(final String s) {
        if (this.istream == null) {
            this.istream = Monitor.getStream();
        }
        this.istream.println(s);
    }
    
    public final boolean databaseEncrypted() {
        return this.databaseEncrypted;
    }
    
    public void setDatabaseEncrypted(final boolean databaseEncrypted) {
        this.databaseEncrypted = databaseEncrypted;
    }
    
    public int encrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3, final boolean b) throws StandardException {
        return this.rawStoreFactory.encrypt(array, n, n2, array2, n3, b);
    }
    
    public int decrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws StandardException {
        return this.rawStoreFactory.decrypt(array, n, n2, array2, n3);
    }
    
    public void decryptAllContainers(final RawTransaction rawTransaction) throws StandardException {
        new EncryptOrDecryptData(this).decryptAllContainers(rawTransaction);
    }
    
    public void encryptAllContainers(final RawTransaction rawTransaction) throws StandardException {
        new EncryptOrDecryptData(this).encryptAllContainers(rawTransaction);
    }
    
    public void removeOldVersionOfContainers() throws StandardException {
        new EncryptOrDecryptData(this).removeOldVersionOfContainers();
    }
    
    private static String jarClassPath(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                CodeSource codeSource;
                try {
                    codeSource = clazz.getProtectionDomain().getCodeSource();
                }
                catch (SecurityException ex) {
                    return ex.getMessage();
                }
                if (codeSource == null || codeSource.getLocation() == null) {
                    return null;
                }
                return codeSource.getLocation().toString();
            }
        });
    }
    
    private static String buildOSinfo() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                String s = "";
                try {
                    final String systemProperty = PropertyUtil.getSystemProperty("os.name");
                    if (systemProperty != null) {
                        s = "os.name=" + systemProperty + "\n";
                    }
                    final String systemProperty2;
                    if ((systemProperty2 = PropertyUtil.getSystemProperty("os.arch")) != null) {
                        s = s + "os.arch=" + systemProperty2 + "\n";
                    }
                    final String systemProperty3;
                    if ((systemProperty3 = PropertyUtil.getSystemProperty("os.version")) != null) {
                        s = s + "os.version=" + systemProperty3;
                    }
                }
                catch (SecurityException ex) {
                    return ex.getMessage();
                }
                return s;
            }
        });
    }
    
    private static String buildJvmVersion() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                String str = "";
                try {
                    final String systemProperty = PropertyUtil.getSystemProperty("java.vendor");
                    if (systemProperty != null) {
                        str = "java.vendor=" + systemProperty;
                    }
                    final String systemProperty2;
                    if ((systemProperty2 = PropertyUtil.getSystemProperty("java.runtime.version")) != null) {
                        str = str + "\njava.runtime.version=" + systemProperty2;
                    }
                    final String systemProperty3;
                    if ((systemProperty3 = PropertyUtil.getSystemProperty("java.fullversion")) != null) {
                        str = str + "\njava.fullversion=" + systemProperty3;
                    }
                    final String systemProperty4;
                    if ((systemProperty4 = PropertyUtil.getSystemProperty("user.dir")) != null) {
                        str = str + "\nuser.dir=" + systemProperty4;
                    }
                }
                catch (SecurityException ex) {
                    return ex.getMessage();
                }
                return str;
            }
        });
    }
    
    public int getEncryptionBlockSize() {
        return this.rawStoreFactory.getEncryptionBlockSize();
    }
    
    public String getVersionedName(final String s, final long i) {
        return s.concat(".G".concat(Long.toString(i)));
    }
    
    public long getMaxContainerId() throws StandardException {
        return this.findMaxContainerId();
    }
    
    synchronized long getNextId() {
        return this.nextContainerId++;
    }
    
    int random() {
        return this.databaseEncrypted ? this.rawStoreFactory.random() : 0;
    }
    
    void fileToRemove(final StorageFile storageFile, final boolean b) {
        if (this.postRecoveryRemovedFiles == null) {
            this.postRecoveryRemovedFiles = new Hashtable();
        }
        String s = null;
        synchronized (this) {
            this.actionCode = 7;
            this.actionFile = storageFile;
            try {
                s = AccessController.doPrivileged((PrivilegedExceptionAction<String>)this);
            }
            catch (PrivilegedActionException ex) {}
            finally {
                this.actionFile = null;
            }
        }
        if (b) {
            this.postRecoveryRemovedFiles.put(s, storageFile);
        }
        else {
            this.postRecoveryRemovedFiles.remove(s);
        }
    }
    
    public void postRecovery() throws StandardException {
        if (this.rawStoreFactory.getDaemon() == null) {
            return;
        }
        if (this.postRecoveryRemovedFiles != null) {
            synchronized (this) {
                this.actionCode = 8;
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
                }
                catch (PrivilegedActionException ex) {}
            }
            this.postRecoveryRemovedFiles = null;
        }
    }
    
    public void setupCacheCleaner(final DaemonService daemonService) {
        this.containerCache.useDaemonService(daemonService);
        this.pageCache.useDaemonService(daemonService);
    }
    
    public void freezePersistentStore() throws StandardException {
        synchronized (this.freezeSemaphore) {
            if (this.isFrozen) {
                throw StandardException.newException("XSRS0.S");
            }
            this.isFrozen = true;
            try {
                while (this.writersInProgress > 0) {
                    try {
                        this.freezeSemaphore.wait();
                    }
                    catch (InterruptedException ex2) {
                        InterruptStatus.setInterrupted();
                    }
                }
            }
            catch (RuntimeException ex) {
                this.isFrozen = false;
                this.freezeSemaphore.notifyAll();
                throw ex;
            }
        }
    }
    
    public void unfreezePersistentStore() {
        synchronized (this.freezeSemaphore) {
            this.isFrozen = false;
            this.freezeSemaphore.notifyAll();
        }
    }
    
    public void writeInProgress() throws StandardException {
        synchronized (this.freezeSemaphore) {
            while (this.isFrozen) {
                try {
                    this.freezeSemaphore.wait();
                }
                catch (InterruptedException ex) {
                    InterruptStatus.setInterrupted();
                }
            }
            ++this.writersInProgress;
        }
    }
    
    public void writeFinished() {
        synchronized (this.freezeSemaphore) {
            --this.writersInProgress;
            this.freezeSemaphore.notifyAll();
        }
    }
    
    public void backupDataFiles(final Transaction transaction, final File file) throws StandardException {
        final String[] containerNames = this.getContainerNames();
        if (containerNames != null) {
            final LockingPolicy lockingPolicy = transaction.newLockingPolicy(0, 0, false);
            final long n = 0L;
            for (int i = containerNames.length - 1; i >= 0; --i) {
                long long1;
                try {
                    long1 = Long.parseLong(containerNames[i].substring(1, containerNames[i].length() - 4), 16);
                }
                catch (Throwable t) {
                    continue;
                }
                final RawContainerHandle openDroppedContainer = this.openDroppedContainer((RawTransaction)transaction, new ContainerKey(n, long1), lockingPolicy, 8);
                if (openDroppedContainer != null) {
                    openDroppedContainer.backupContainer(file.getPath());
                    openDroppedContainer.close();
                }
            }
        }
    }
    
    synchronized String[] getContainerNames() {
        this.actionCode = 14;
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String[]>)this);
        }
        catch (PrivilegedActionException ex) {
            return null;
        }
    }
    
    private void restoreDataDirectory(final String pathname) throws StandardException {
        final File file = new File(pathname);
        final String[] bfilelist = AccessController.doPrivileged((PrivilegedAction<String[]>)new PrivilegedAction() {
            public Object run() {
                return file.list();
            }
        });
        if (bfilelist == null) {
            throw StandardException.newException("XSDG6.D", file);
        }
        boolean b = false;
        for (int i = 0; i < bfilelist.length; ++i) {
            if (bfilelist[i].startsWith("seg")) {
                final File file2 = new File(file, bfilelist[i]);
                if (AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
                    public Object run() {
                        return file2.exists();
                    }
                }) && AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
                    public Object run() {
                        return file2.isDirectory();
                    }
                })) {
                    b = true;
                    break;
                }
            }
        }
        if (!b) {
            throw StandardException.newException("XSDG6.D", file);
        }
        synchronized (this) {
            this.actionCode = 13;
            this.backupRoot = file;
            this.bfilelist = bfilelist;
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)this);
            }
            catch (PrivilegedActionException ex) {
                throw (StandardException)ex.getException();
            }
            finally {
                this.backupRoot = null;
                this.bfilelist = null;
            }
        }
    }
    
    private void privRestoreDataDirectory() throws StandardException {
        final String[] list = this.storageFactory.newStorageFile(null).list();
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                if (list[i].startsWith("seg")) {
                    final StorageFile storageFile = this.storageFactory.newStorageFile(list[i]);
                    if (!storageFile.deleteAll()) {
                        throw StandardException.newException("XSDG7.D", storageFile);
                    }
                }
            }
        }
        for (int j = 0; j < this.bfilelist.length; ++j) {
            if (this.bfilelist[j].startsWith("seg")) {
                final StorageFile storageFile2 = this.storageFactory.newStorageFile(this.bfilelist[j]);
                final File file = new File(this.backupRoot, this.bfilelist[j]);
                if (!FileUtil.copyDirectory(this.writableStorageFactory, file, storageFile2)) {
                    throw StandardException.newException("XSDG8.D", file, storageFile2);
                }
            }
            else if (this.databaseEncrypted && this.bfilelist[j].startsWith("verifyKey.dat")) {
                final File file2 = new File(this.backupRoot, this.bfilelist[j]);
                final StorageFile storageFile3 = this.storageFactory.newStorageFile(this.bfilelist[j]);
                if (!FileUtil.copyFile(this.writableStorageFactory, file2, storageFile3)) {
                    throw StandardException.newException("XSDG8.D", this.bfilelist[j], storageFile3);
                }
            }
        }
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    public StorageFactory getStorageFactory() {
        return this.storageFactory;
    }
    
    public final Object run() throws IOException, StandardException {
        switch (this.actionCode) {
            case 10: {
                this.readOnly = this.storageFactory.isReadOnlyDatabase();
                this.supportsRandomAccess = this.storageFactory.supportsRandomAccess();
                return null;
            }
            case 2: {
                final StorageFile tempDir = this.storageFactory.getTempDir();
                if (tempDir != null) {
                    tempDir.deleteAll();
                }
                return null;
            }
            case 3:
            case 4: {
                final StringBuffer sb = new StringBuffer("seg");
                sb.append(this.containerId.getSegmentId());
                sb.append(this.storageFactory.getSeparator());
                if (this.actionCode == 3) {
                    sb.append(this.stub ? 'd' : 'c');
                    sb.append(Long.toHexString(this.containerId.getContainerId()));
                    sb.append(".dat");
                }
                else {
                    sb.append(this.stub ? 'D' : 'C');
                    sb.append(Long.toHexString(this.containerId.getContainerId()));
                    sb.append(".DAT");
                }
                return this.storageFactory.newStorageFile(sb.toString());
            }
            case 9: {
                final char separator = this.storageFactory.getSeparator();
                final StorageFile storageFile = this.storageFactory.newStorageFile(null);
                final String[] list = storageFile.list();
                for (int i = list.length - 1; i >= 0; --i) {
                    if (list[i].startsWith("seg")) {
                        final StorageFile storageFile2 = this.storageFactory.newStorageFile(storageFile, list[i]);
                        if (storageFile2.exists() && storageFile2.isDirectory()) {
                            final String[] list2 = storageFile2.list();
                            for (int j = list2.length - 1; j >= 0; --j) {
                                if (list2[j].startsWith("D") || list2[j].startsWith("d")) {
                                    this.storageFactory.newStorageFile(storageFile, list[i] + separator + list2[j]).delete();
                                }
                            }
                        }
                    }
                }
                break;
            }
            case 5: {
                long n = 1L;
                final StorageFile storageFile3 = this.storageFactory.newStorageFile("seg0");
                if (storageFile3.exists() && storageFile3.isDirectory()) {
                    final String[] list3 = storageFile3.list();
                    for (int k = list3.length - 1; k >= 0; --k) {
                        try {
                            final long long1 = Long.parseLong(list3[k].substring(1, list3[k].length() - 4), 16);
                            if (long1 > n) {
                                n = long1;
                            }
                        }
                        catch (Throwable t) {}
                    }
                }
                return ReuseFactory.getLong(n);
            }
            case 6: {
                final boolean b = this.actionFile.exists() && this.actionFile.delete();
                this.actionFile = null;
                return b ? this : null;
            }
            case 7: {
                final String path = this.actionFile.getPath();
                this.actionFile = null;
                return path;
            }
            case 8: {
                final Enumeration<StorageFile> elements = (Enumeration<StorageFile>)this.postRecoveryRemovedFiles.elements();
                while (elements.hasMoreElements()) {
                    final StorageFile storageFile4 = elements.nextElement();
                    if (storageFile4.exists()) {
                        storageFile4.delete();
                    }
                }
                return null;
            }
            case 11: {
                this.privGetJBMSLockOnDB();
                return null;
            }
            case 12: {
                this.privReleaseJBMSLockOnDB();
                return null;
            }
            case 13: {
                this.privRestoreDataDirectory();
                return null;
            }
            case 14: {
                final StorageFile storageFile5 = this.storageFactory.newStorageFile("seg0");
                if (storageFile5.exists() && storageFile5.isDirectory()) {
                    return storageFile5.list();
                }
                return null;
            }
        }
        return null;
    }
}
