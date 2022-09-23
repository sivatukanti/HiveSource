// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access;

import org.apache.derby.iapi.services.daemon.Serviceable;
import java.io.Serializable;
import org.apache.derby.iapi.store.access.TransactionInfo;
import org.apache.derby.iapi.services.property.PropertyUtil;
import java.util.Dictionary;
import org.apache.derby.iapi.store.raw.data.DataFactory;
import org.apache.derby.iapi.store.raw.log.LogFactory;
import org.apache.derby.iapi.store.raw.Transaction;
import org.apache.derby.iapi.services.property.PersistentSet;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.services.locks.LockFactory;
import java.util.Enumeration;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.services.cache.Cacheable;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.store.access.conglomerate.TransactionManager;
import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.services.cache.CacheFactory;
import org.apache.derby.iapi.store.access.TransactionController;
import org.apache.derby.iapi.store.access.conglomerate.MethodFactory;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.cache.CacheManager;
import org.apache.derby.iapi.store.access.conglomerate.ConglomerateFactory;
import org.apache.derby.iapi.services.property.PropertyFactory;
import org.apache.derby.iapi.store.raw.LockingPolicy;
import java.util.Properties;
import java.util.Hashtable;
import org.apache.derby.iapi.store.raw.RawStoreFactory;
import org.apache.derby.iapi.services.property.PropertySetCallback;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.services.cache.CacheableFactory;
import org.apache.derby.iapi.store.access.AccessFactory;

public abstract class RAMAccessManager implements AccessFactory, CacheableFactory, ModuleControl, PropertySetCallback
{
    private RawStoreFactory rawstore;
    private Hashtable implhash;
    private Hashtable formathash;
    private Properties serviceProperties;
    LockingPolicy system_default_locking_policy;
    private PropertyConglomerate xactProperties;
    private PropertyFactory pf;
    protected LockingPolicy[] table_level_policy;
    protected LockingPolicy[] record_level_policy;
    protected ConglomerateFactory[] conglom_map;
    private CacheManager conglom_cache;
    private long conglom_nextid;
    
    public RAMAccessManager() {
        this.conglom_nextid = 0L;
        this.implhash = new Hashtable();
        this.formathash = new Hashtable();
    }
    
    protected LockingPolicy getDefaultLockingPolicy() {
        return this.system_default_locking_policy;
    }
    
    RawStoreFactory getRawStore() {
        return this.rawstore;
    }
    
    PropertyConglomerate getTransactionalProperties() {
        return this.xactProperties;
    }
    
    private void boot_load_conglom_map() throws StandardException {
        this.conglom_map = new ConglomerateFactory[2];
        final MethodFactory methodFactoryByImpl = this.findMethodFactoryByImpl("heap");
        if (methodFactoryByImpl == null || !(methodFactoryByImpl instanceof ConglomerateFactory)) {
            throw StandardException.newException("XSAM3.S", "heap");
        }
        this.conglom_map[0] = (ConglomerateFactory)methodFactoryByImpl;
        final MethodFactory methodFactoryByImpl2 = this.findMethodFactoryByImpl("BTREE");
        if (methodFactoryByImpl2 == null || !(methodFactoryByImpl2 instanceof ConglomerateFactory)) {
            throw StandardException.newException("XSAM3.S", "BTREE");
        }
        this.conglom_map[1] = (ConglomerateFactory)methodFactoryByImpl2;
    }
    
    protected abstract int getSystemLockLevel();
    
    protected abstract void bootLookupSystemLockLevel(final TransactionController p0) throws StandardException;
    
    protected long getNextConglomId(final int n) throws StandardException {
        final long n2;
        synchronized (this.conglom_cache) {
            if (this.conglom_nextid == 0L) {
                this.conglom_nextid = (this.rawstore.getMaxContainerId() >> 4) + 1L;
            }
            n2 = this.conglom_nextid++;
        }
        return n2 << 4 | (long)n;
    }
    
    ConglomerateFactory getFactoryFromConglomId(final long value) throws StandardException {
        try {
            return this.conglom_map[(int)(0xFL & value)];
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw StandardException.newException("XSAI2.S", new Long(value));
        }
    }
    
    private void conglomCacheInit() throws StandardException {
        this.conglom_cache = ((CacheFactory)Monitor.startSystemModule("org.apache.derby.iapi.services.cache.CacheFactory")).newCacheManager(this, "ConglomerateDirectoryCache", 200, 300);
    }
    
    Conglomerate conglomCacheFind(final TransactionManager transactionManager, final long value) throws StandardException {
        Conglomerate conglom = null;
        final CacheableConglomerate cacheableConglomerate = (CacheableConglomerate)this.conglom_cache.find(new Long(value));
        if (cacheableConglomerate != null) {
            conglom = cacheableConglomerate.getConglom();
            this.conglom_cache.release(cacheableConglomerate);
        }
        return conglom;
    }
    
    protected void conglomCacheInvalidate() throws StandardException {
        this.conglom_cache.ageOut();
    }
    
    void conglomCacheAddEntry(final long value, final Conglomerate conglomerate) throws StandardException {
        this.conglom_cache.release(this.conglom_cache.create(new Long(value), conglomerate));
    }
    
    void conglomCacheRemoveEntry(final long value) throws StandardException {
        final CacheableConglomerate cacheableConglomerate = (CacheableConglomerate)this.conglom_cache.findCached(new Long(value));
        if (cacheableConglomerate != null) {
            this.conglom_cache.remove(cacheableConglomerate);
        }
    }
    
    RAMTransactionContext getCurrentTransactionContext() {
        RAMTransactionContext ramTransactionContext = (RAMTransactionContext)ContextService.getContext("RAMInternalContext");
        if (ramTransactionContext == null) {
            ramTransactionContext = (RAMTransactionContext)ContextService.getContext("RAMChildContext");
        }
        if (ramTransactionContext == null) {
            ramTransactionContext = (RAMTransactionContext)ContextService.getContext("RAMTransactionContext");
        }
        return ramTransactionContext;
    }
    
    public void createFinished() throws StandardException {
        this.rawstore.createFinished();
    }
    
    public MethodFactory findMethodFactoryByFormat(final UUID key) {
        final MethodFactory methodFactory = this.formathash.get(key);
        if (methodFactory != null) {
            return methodFactory;
        }
        final Enumeration<MethodFactory> elements = this.formathash.elements();
        while (elements.hasMoreElements()) {
            final MethodFactory methodFactory2 = elements.nextElement();
            if (methodFactory2.supportsFormat(key)) {
                return methodFactory2;
            }
        }
        return null;
    }
    
    public MethodFactory findMethodFactoryByImpl(final String s) throws StandardException {
        final MethodFactory methodFactory = this.implhash.get(s);
        if (methodFactory != null) {
            return methodFactory;
        }
        final Enumeration<MethodFactory> elements = this.implhash.elements();
        while (elements.hasMoreElements()) {
            final MethodFactory methodFactory2 = elements.nextElement();
            if (methodFactory2.supportsImplementation(s)) {
                return methodFactory2;
            }
        }
        MethodFactory methodFactory3 = null;
        final Properties properties = new Properties(this.serviceProperties);
        properties.put("derby.access.Conglomerate.type", s);
        try {
            methodFactory3 = (MethodFactory)Monitor.bootServiceModule(false, this, "org.apache.derby.iapi.store.access.conglomerate.MethodFactory", s, properties);
        }
        catch (StandardException ex) {
            if (!ex.getMessageId().equals("XBM02.D")) {
                throw ex;
            }
        }
        if (methodFactory3 != null) {
            this.registerAccessMethod(methodFactory3);
            return methodFactory3;
        }
        return null;
    }
    
    public LockFactory getLockFactory() {
        return this.rawstore.getLockFactory();
    }
    
    public TransactionController getTransaction(final ContextManager contextManager) throws StandardException {
        return this.getAndNameTransaction(contextManager, "UserTransaction");
    }
    
    public TransactionController getAndNameTransaction(final ContextManager contextManager, final String s) throws StandardException {
        if (contextManager == null) {
            return null;
        }
        final RAMTransactionContext ramTransactionContext = (RAMTransactionContext)contextManager.getContext("RAMTransactionContext");
        if (ramTransactionContext == null) {
            final Transaction userTransaction = this.rawstore.findUserTransaction(contextManager, s);
            final RAMTransaction transaction = new RAMTransactionContext(contextManager, "RAMTransactionContext", new RAMTransaction(this, userTransaction, null), false).getTransaction();
            if (this.xactProperties != null) {
                userTransaction.setup(transaction);
                transaction.commit();
            }
            userTransaction.setDefaultLockingPolicy(this.system_default_locking_policy);
            transaction.commit();
            return transaction;
        }
        return ramTransactionContext.getTransaction();
    }
    
    public Object startXATransaction(final ContextManager contextManager, final int n, final byte[] array, final byte[] array2) throws StandardException {
        RAMTransaction ramTransaction = null;
        if (contextManager == null) {
            return null;
        }
        if (contextManager.getContext("RAMTransactionContext") == null) {
            final Transaction startGlobalTransaction = this.rawstore.startGlobalTransaction(contextManager, n, array, array2);
            ramTransaction = new RAMTransaction(this, startGlobalTransaction, null);
            final RAMTransactionContext ramTransactionContext = new RAMTransactionContext(contextManager, "RAMTransactionContext", ramTransaction, false);
            if (this.xactProperties != null) {
                startGlobalTransaction.setup(ramTransaction);
                ramTransaction.commitNoSync(5);
            }
            startGlobalTransaction.setDefaultLockingPolicy(this.system_default_locking_policy);
            ramTransaction.commitNoSync(5);
        }
        return ramTransaction;
    }
    
    public Object getXAResourceManager() throws StandardException {
        return this.rawstore.getXAResourceManager();
    }
    
    public void registerAccessMethod(final MethodFactory methodFactory) {
        this.implhash.put(methodFactory.primaryImplementationType(), methodFactory);
        this.formathash.put(methodFactory.primaryFormat(), methodFactory);
    }
    
    public boolean isReadOnly() {
        return this.rawstore.isReadOnly();
    }
    
    public void createReadMeFiles() throws StandardException {
        this.rawstore.createDataWarningFile();
        ((LogFactory)Monitor.findServiceModule(this, this.rawstore.getLogFactoryModule())).createDataWarningFile();
        Monitor.getMonitor().getServiceType(this.rawstore).createDataWarningFile(((DataFactory)Monitor.findServiceModule(this, this.rawstore.getDataFactoryModule())).getStorageFactory());
    }
    
    private void addPropertySetNotification(final PropertySetCallback propertySetCallback, final TransactionController transactionController) {
        this.pf.addPropertySetNotification(propertySetCallback);
        final Hashtable hashtable = new Hashtable();
        try {
            this.xactProperties.getProperties(transactionController, hashtable, false, false);
        }
        catch (StandardException ex) {
            return;
        }
        propertySetCallback.init(PropertyUtil.isDBOnly(hashtable), hashtable);
    }
    
    public TransactionInfo[] getTransactionInfo() {
        return this.rawstore.getTransactionInfo();
    }
    
    public void startReplicationMaster(final String s, final String s2, final int n, final String s3) throws StandardException {
        this.rawstore.startReplicationMaster(s, s2, n, s3);
    }
    
    public void failover(final String s) throws StandardException {
        this.rawstore.failover(s);
    }
    
    public void stopReplicationMaster() throws StandardException {
        this.rawstore.stopReplicationMaster();
    }
    
    public void freeze() throws StandardException {
        this.rawstore.freeze();
    }
    
    public void unfreeze() throws StandardException {
        this.rawstore.unfreeze();
    }
    
    public void backup(final String s, final boolean b) throws StandardException {
        this.rawstore.backup(s, b);
    }
    
    public void backupAndEnableLogArchiveMode(final String s, final boolean b, final boolean b2) throws StandardException {
        this.rawstore.backupAndEnableLogArchiveMode(s, b, b2);
    }
    
    public void disableLogArchiveMode(final boolean b) throws StandardException {
        this.rawstore.disableLogArchiveMode(b);
    }
    
    public void checkpoint() throws StandardException {
        this.rawstore.checkpoint();
    }
    
    public void waitForPostCommitToFinishWork() {
        this.rawstore.getDaemon().waitUntilQueueIsEmpty();
    }
    
    public void boot(final boolean b, final Properties serviceProperties) throws StandardException {
        this.serviceProperties = serviceProperties;
        this.boot_load_conglom_map();
        if (b) {
            this.conglom_nextid = 1L;
        }
        this.rawstore = (RawStoreFactory)Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.store.raw.RawStoreFactory", this.serviceProperties);
        Monitor.bootServiceModule(b, this, "org.apache.derby.iapi.services.property.PropertyFactory", serviceProperties);
        this.conglomCacheInit();
        final RAMTransaction ramTransaction = (RAMTransaction)this.getAndNameTransaction(ContextService.getFactory().getCurrentContextManager(), "UserTransaction");
        this.system_default_locking_policy = ramTransaction.getRawStoreXact().newLockingPolicy(2, 5, true);
        (this.table_level_policy = new LockingPolicy[6])[0] = ramTransaction.getRawStoreXact().newLockingPolicy(2, 0, true);
        this.table_level_policy[1] = ramTransaction.getRawStoreXact().newLockingPolicy(2, 1, true);
        this.table_level_policy[2] = ramTransaction.getRawStoreXact().newLockingPolicy(2, 2, true);
        this.table_level_policy[3] = ramTransaction.getRawStoreXact().newLockingPolicy(2, 3, true);
        this.table_level_policy[4] = ramTransaction.getRawStoreXact().newLockingPolicy(2, 4, true);
        this.table_level_policy[5] = ramTransaction.getRawStoreXact().newLockingPolicy(2, 5, true);
        (this.record_level_policy = new LockingPolicy[6])[0] = ramTransaction.getRawStoreXact().newLockingPolicy(1, 0, true);
        this.record_level_policy[1] = ramTransaction.getRawStoreXact().newLockingPolicy(1, 1, true);
        this.record_level_policy[2] = ramTransaction.getRawStoreXact().newLockingPolicy(1, 2, true);
        this.record_level_policy[3] = ramTransaction.getRawStoreXact().newLockingPolicy(1, 3, true);
        this.record_level_policy[4] = ramTransaction.getRawStoreXact().newLockingPolicy(1, 4, true);
        this.record_level_policy[5] = ramTransaction.getRawStoreXact().newLockingPolicy(1, 5, true);
        ramTransaction.commit();
        this.pf = (PropertyFactory)Monitor.findServiceModule(this, "org.apache.derby.iapi.services.property.PropertyFactory");
        this.xactProperties = new PropertyConglomerate(ramTransaction, b, serviceProperties, this.pf);
        if (b) {
            this.rawstore.createDataWarningFile();
        }
        this.rawstore.getRawStoreProperties(ramTransaction);
        this.bootLookupSystemLockLevel(ramTransaction);
        this.system_default_locking_policy = ramTransaction.getRawStoreXact().newLockingPolicy((this.getSystemLockLevel() == 7) ? 2 : 1, 5, true);
        this.addPropertySetNotification(this.getLockFactory(), ramTransaction);
        this.addPropertySetNotification(this, ramTransaction);
        ramTransaction.commit();
        ramTransaction.destroy();
    }
    
    public void stop() {
    }
    
    public void init(final boolean b, final Dictionary dictionary) {
    }
    
    public boolean validate(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        if (s.equals("encryptionAlgorithm")) {
            throw StandardException.newException("XBCXD.S");
        }
        if (s.equals("encryptionProvider")) {
            throw StandardException.newException("XBCXE.S");
        }
        return true;
    }
    
    public Serviceable apply(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        return null;
    }
    
    public Serializable map(final String s, final Serializable s2, final Dictionary dictionary) throws StandardException {
        return null;
    }
    
    public Cacheable newCacheable(final CacheManager cacheManager) {
        return new CacheableConglomerate(this);
    }
}
