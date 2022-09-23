// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.exceptions.TransactionNotActiveException;
import org.datanucleus.exceptions.ObjectDetachedException;
import org.datanucleus.exceptions.ClassNotDetachableException;
import org.datanucleus.exceptions.ClassNotPersistableException;
import org.datanucleus.store.query.Query;
import org.datanucleus.store.Extent;
import org.datanucleus.cache.Level2Cache;
import org.datanucleus.identity.IdentityReference;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.cache.L2CachePopulateFieldManager;
import org.datanucleus.exceptions.RollbackStateTransitionException;
import org.datanucleus.state.DetachState;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.flush.Operation;
import org.datanucleus.flush.FlushProcess;
import org.datanucleus.exceptions.NucleusOptimisticException;
import org.datanucleus.state.LifeCycleState;
import org.datanucleus.state.RelationshipManagerImpl;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Constructor;
import org.datanucleus.identity.IdentityKeyTranslator;
import org.datanucleus.exceptions.NoPersistenceInformationException;
import org.datanucleus.identity.OIDFactory;
import org.datanucleus.identity.DatastoreUniqueOID;
import org.datanucleus.identity.IdentityStringTranslator;
import org.datanucleus.identity.OID;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.identity.SCOID;
import java.lang.reflect.Modifier;
import org.datanucleus.state.FetchPlanState;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.exceptions.NucleusFatalUserException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.PersistenceBatchType;
import org.datanucleus.exceptions.CommitStateTransitionException;
import java.util.Collection;
import org.datanucleus.cache.CachedPC;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.FieldValues;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.state.LockManagerImpl;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.StoreManager;
import java.util.HashMap;
import org.datanucleus.exceptions.TransactionActiveOnCloseException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.management.jmx.ManagementManager;
import java.util.Iterator;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.TransactionType;
import java.util.Locale;
import java.util.HashSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import org.datanucleus.util.WeakValueMap;
import org.datanucleus.management.ManagerStatistics;
import org.datanucleus.state.RelationshipManager;
import java.util.concurrent.locks.Lock;
import org.datanucleus.state.LockManager;
import java.util.BitSet;
import java.util.Set;
import org.datanucleus.flush.OperationQueue;
import java.util.List;
import java.util.Map;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.properties.BasePropertyStore;
import org.datanucleus.cache.Level1Cache;
import org.datanucleus.state.CallbackHandler;
import org.datanucleus.util.Localiser;

public class ExecutionContextImpl implements ExecutionContext, TransactionEventListener
{
    protected static final Localiser LOCALISER;
    NucleusContext nucCtx;
    private Object owner;
    private boolean closed;
    private FetchPlan fetchPlan;
    private ClassLoaderResolver clr;
    private CallbackHandler callbacks;
    protected Level1Cache cache;
    private BasePropertyStore properties;
    private Object objectLookingForOP;
    private ObjectProvider foundOP;
    private Transaction tx;
    private Map<Object, ObjectProvider> enlistedOPCache;
    private List<ObjectProvider> dirtyOPs;
    private List<ObjectProvider> indirectDirtyOPs;
    private OperationQueue operationQueue;
    private Set<ObjectProvider> nontxProcessedOPs;
    protected boolean l2CacheEnabled;
    private Set l2CacheTxIds;
    private Map<Object, BitSet> l2CacheTxFieldsToUpdateById;
    private int flushing;
    private boolean runningDetachAllOnTxnEnd;
    private FetchGroupManager fetchGrpMgr;
    private LockManager lockMgr;
    protected Lock lock;
    private boolean runningManageRelations;
    Map<ObjectProvider, RelationshipManager> managedRelationDetails;
    Map<ObjectProvider, Object> opAttachDetachObjectReferenceMap;
    Map<ObjectProvider, List<EmbeddedOwnerRelation>> opEmbeddedInfoByOwner;
    Map<ObjectProvider, List<EmbeddedOwnerRelation>> opEmbeddedInfoByEmbedded;
    protected Map<ObjectProvider, Map<?, ?>> opAssociatedValuesMapByOP;
    private boolean runningPBRAtCommit;
    private Set reachabilityPersistedIds;
    private Set reachabilityDeletedIds;
    private Set reachabilityFlushedNewIds;
    private Set reachabilityEnlistedIds;
    ManagerStatistics statistics;
    private Set<ExecutionContextListener> ecListeners;
    private ThreadLocal contextInfoThreadLocal;
    private ObjectProvider[] detachAllOnTxnEndOPs;
    
    public ExecutionContextImpl(final NucleusContext ctx, final Object owner, final Map<String, Object> options) {
        this.clr = null;
        this.properties = new BasePropertyStore();
        this.objectLookingForOP = null;
        this.foundOP = null;
        this.enlistedOPCache = (Map<Object, ObjectProvider>)new WeakValueMap();
        this.dirtyOPs = new ArrayList<ObjectProvider>();
        this.indirectDirtyOPs = new ArrayList<ObjectProvider>();
        this.operationQueue = null;
        this.nontxProcessedOPs = null;
        this.l2CacheEnabled = false;
        this.l2CacheTxIds = null;
        this.l2CacheTxFieldsToUpdateById = null;
        this.flushing = 0;
        this.runningDetachAllOnTxnEnd = false;
        this.lockMgr = null;
        this.runningManageRelations = false;
        this.managedRelationDetails = null;
        this.opAttachDetachObjectReferenceMap = null;
        this.opEmbeddedInfoByOwner = null;
        this.opEmbeddedInfoByEmbedded = null;
        this.opAssociatedValuesMapByOP = null;
        this.runningPBRAtCommit = false;
        this.reachabilityPersistedIds = null;
        this.reachabilityDeletedIds = null;
        this.reachabilityFlushedNewIds = null;
        this.reachabilityEnlistedIds = null;
        this.statistics = null;
        this.ecListeners = null;
        this.detachAllOnTxnEndOPs = null;
        this.nucCtx = ctx;
        if (ctx.getPersistenceConfiguration().getBooleanProperty("datanucleus.Multithreaded")) {
            this.lock = new ReentrantLock();
        }
        this.initialise(owner, options);
        this.initialiseLevel1Cache();
        if (this.getReachabilityAtCommit()) {
            this.reachabilityPersistedIds = new HashSet();
            this.reachabilityDeletedIds = new HashSet();
            this.reachabilityFlushedNewIds = new HashSet();
            this.reachabilityEnlistedIds = new HashSet();
        }
        this.setLevel2Cache(true);
    }
    
    @Override
    public void initialise(final Object owner, final Map<String, Object> options) {
        this.owner = owner;
        this.closed = false;
        final ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
        this.clr = this.nucCtx.getClassLoaderResolver(contextLoader);
        try {
            final ImplementationCreator ic = this.nucCtx.getImplementationCreator();
            if (ic != null) {
                this.clr.setRuntimeClassLoader(ic.getClassLoader());
            }
        }
        catch (Exception ex) {}
        final PersistenceConfiguration conf = this.nucCtx.getPersistenceConfiguration();
        for (final Map.Entry<String, Object> entry : conf.getManagerOverrideableProperties().entrySet()) {
            this.properties.setProperty(entry.getKey().toLowerCase(Locale.ENGLISH), entry.getValue());
        }
        this.fetchPlan = new FetchPlan(this, this.clr).setMaxFetchDepth(conf.getIntProperty("datanucleus.maxFetchDepth"));
        if (TransactionType.JTA.toString().equalsIgnoreCase(conf.getStringProperty("datanucleus.TransactionType"))) {
            if (this.getNucleusContext().isJcaMode()) {
                this.tx = new JTAJCATransactionImpl(this);
            }
            else {
                boolean autoJoin = true;
                if (options != null && options.containsKey("jta_autojoin")) {
                    autoJoin = Boolean.valueOf(options.get("jta_autojoin"));
                }
                this.tx = new JTATransactionImpl(this, autoJoin);
            }
        }
        else {
            this.tx = new TransactionImpl(this);
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010000", this, this.nucCtx.getStoreManager(), this.tx));
        }
        if (this.nucCtx.statisticsEnabled()) {
            String name = null;
            if (this.nucCtx.getJMXManager() != null) {
                final ManagementManager mgmtMgr = this.nucCtx.getJMXManager();
                name = mgmtMgr.getDomainName() + ":InstanceName=" + mgmtMgr.getInstanceName() + ",Type=" + ManagerStatistics.class.getName() + ",Name=Manager" + NucleusContext.random.nextLong();
            }
            this.statistics = new ManagerStatistics(name, this.nucCtx.getStatistics());
            if (this.nucCtx.getJMXManager() != null) {
                this.nucCtx.getJMXManager().registerMBean(this.statistics, name);
            }
        }
        this.contextInfoThreadLocal = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return new ThreadContextInfo();
            }
        };
    }
    
    @Override
    public void close() {
        if (this.closed) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010002"));
        }
        if (this.tx.getIsActive()) {
            throw new TransactionActiveOnCloseException(this);
        }
        if (!this.dirtyOPs.isEmpty() && this.tx.getNontransactionalWrite()) {
            if (this.isNonTxAtomic()) {
                this.processNontransactionalUpdate();
            }
            else {
                try {
                    this.tx.begin();
                    this.tx.commit();
                }
                finally {
                    if (this.tx.isActive()) {
                        this.tx.rollback();
                    }
                }
            }
        }
        if (this.getDetachOnClose()) {
            this.performDetachOnClose();
        }
        final LifecycleListener[] listener = this.nucCtx.getExecutionContextListeners();
        for (int i = 0; i < listener.length; ++i) {
            listener[i].preClose(this);
        }
        this.disconnectObjectProvidersFromCache();
        this.disconnectLifecycleListener();
        if (this.ecListeners != null) {
            for (final ExecutionContextListener lstr : this.ecListeners) {
                lstr.executionContextClosing(this);
            }
            this.ecListeners.clear();
            this.ecListeners = null;
        }
        this.fetchPlan.clearGroups().addGroup("default");
        if (this.statistics != null) {
            if (this.nucCtx.getJMXManager() != null) {
                this.nucCtx.getJMXManager().deregisterMBean(this.statistics.getRegisteredName());
            }
            this.statistics = null;
        }
        this.cache.clear();
        this.enlistedOPCache.clear();
        this.dirtyOPs.clear();
        this.indirectDirtyOPs.clear();
        if (this.nontxProcessedOPs != null) {
            this.nontxProcessedOPs.clear();
            this.nontxProcessedOPs = null;
        }
        if (this.managedRelationDetails != null) {
            this.managedRelationDetails.clear();
            this.managedRelationDetails = null;
        }
        if (this.l2CacheTxIds != null) {
            this.l2CacheTxIds.clear();
        }
        if (this.l2CacheTxFieldsToUpdateById != null) {
            this.l2CacheTxFieldsToUpdateById.clear();
        }
        if (this.getReachabilityAtCommit()) {
            this.reachabilityPersistedIds.clear();
            this.reachabilityDeletedIds.clear();
            this.reachabilityFlushedNewIds.clear();
            this.reachabilityEnlistedIds.clear();
        }
        if (this.opEmbeddedInfoByOwner != null) {
            this.opEmbeddedInfoByOwner.clear();
            this.opEmbeddedInfoByOwner = null;
        }
        if (this.opEmbeddedInfoByEmbedded != null) {
            this.opEmbeddedInfoByEmbedded.clear();
            this.opEmbeddedInfoByEmbedded = null;
        }
        if (this.opAssociatedValuesMapByOP != null) {
            this.opAssociatedValuesMapByOP.clear();
            this.opAssociatedValuesMapByOP = null;
        }
        this.closed = true;
        this.tx.close();
        this.tx = null;
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010001", this));
        }
        this.nucCtx.getExecutionContextPool().checkIn(this);
    }
    
    protected void setLevel2Cache(final boolean flag) {
        if (flag && this.nucCtx.hasLevel2Cache() && !this.l2CacheEnabled) {
            this.l2CacheTxIds = new HashSet();
            this.l2CacheTxFieldsToUpdateById = new HashMap<Object, BitSet>();
            this.l2CacheEnabled = true;
        }
        else if (!flag && this.l2CacheEnabled) {
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug("Disabling L2 caching for " + this);
            }
            this.l2CacheTxIds.clear();
            this.l2CacheTxIds = null;
            this.l2CacheTxFieldsToUpdateById.clear();
            this.l2CacheTxFieldsToUpdateById = null;
            this.l2CacheEnabled = false;
        }
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    protected ThreadContextInfo acquireThreadContextInfo() {
        final ThreadContextInfo threadContextInfo;
        final ThreadContextInfo threadInfo = threadContextInfo = this.contextInfoThreadLocal.get();
        ++threadContextInfo.referenceCounter;
        return threadInfo;
    }
    
    protected ThreadContextInfo getThreadContextInfo() {
        return this.contextInfoThreadLocal.get();
    }
    
    protected void releaseThreadContextInfo() {
        final ThreadContextInfo threadContextInfo;
        final ThreadContextInfo threadInfo = threadContextInfo = this.contextInfoThreadLocal.get();
        final int referenceCounter = threadContextInfo.referenceCounter - 1;
        threadContextInfo.referenceCounter = referenceCounter;
        if (referenceCounter <= 0) {
            threadInfo.referenceCounter = 0;
            if (threadInfo.attachedOwnerByObject != null) {
                threadInfo.attachedOwnerByObject.clear();
            }
            threadInfo.attachedOwnerByObject = null;
            if (threadInfo.attachedPCById != null) {
                threadInfo.attachedPCById.clear();
            }
            threadInfo.attachedPCById = null;
            this.contextInfoThreadLocal.remove();
        }
    }
    
    @Override
    public void transactionStarted() {
        this.getStoreManager().transactionStarted(this);
        this.postBegin();
    }
    
    @Override
    public void transactionPreFlush() {
    }
    
    @Override
    public void transactionFlushed() {
    }
    
    @Override
    public void transactionPreCommit() {
        this.preCommit();
    }
    
    @Override
    public void transactionCommitted() {
        this.getStoreManager().transactionCommitted(this);
        this.postCommit();
    }
    
    @Override
    public void transactionPreRollBack() {
        this.preRollback();
    }
    
    @Override
    public void transactionRolledBack() {
        this.getStoreManager().transactionRolledBack(this);
        this.postRollback();
    }
    
    @Override
    public void transactionEnded() {
    }
    
    @Override
    public ManagerStatistics getStatistics() {
        return this.statistics;
    }
    
    protected void initialiseLevel1Cache() {
        final String level1Type = this.nucCtx.getPersistenceConfiguration().getStringProperty("datanucleus.cache.level1.type");
        if (level1Type != null && level1Type.equalsIgnoreCase("none")) {
            return;
        }
        final String level1ClassName = this.getNucleusContext().getPluginManager().getAttributeValueForExtension("org.datanucleus.cache_level1", "name", level1Type, "class-name");
        if (level1ClassName == null) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("003001", level1Type)).setFatal();
        }
        try {
            this.cache = (Level1Cache)this.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.cache_level1", "name", level1Type, "class-name", null, null);
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003003", level1Type));
            }
        }
        catch (Exception e) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("003002", level1Type, level1ClassName), e).setFatal();
        }
    }
    
    @Override
    public ClassLoaderResolver getClassLoaderResolver() {
        return this.clr;
    }
    
    @Override
    public StoreManager getStoreManager() {
        return this.getNucleusContext().getStoreManager();
    }
    
    @Override
    public ApiAdapter getApiAdapter() {
        return this.getNucleusContext().getApiAdapter();
    }
    
    @Override
    public TypeManager getTypeManager() {
        return this.getNucleusContext().getTypeManager();
    }
    
    @Override
    public LockManager getLockManager() {
        if (this.lockMgr == null) {
            this.lockMgr = new LockManagerImpl();
        }
        return this.lockMgr;
    }
    
    @Override
    public FetchPlan getFetchPlan() {
        this.assertIsOpen();
        return this.fetchPlan;
    }
    
    @Override
    public Object getOwner() {
        return this.owner;
    }
    
    @Override
    public NucleusContext getNucleusContext() {
        return this.nucCtx;
    }
    
    @Override
    public MetaDataManager getMetaDataManager() {
        return this.getNucleusContext().getMetaDataManager();
    }
    
    @Override
    public void setProperties(final Map props) {
        if (props == null) {
            return;
        }
        for (final Map.Entry entry : props.entrySet()) {
            if (entry.getKey() instanceof String) {
                this.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }
    
    @Override
    public void setProperty(final String name, final Object value) {
        if (this.properties.hasProperty(name.toLowerCase(Locale.ENGLISH))) {
            final String intName = this.getNucleusContext().getPersistenceConfiguration().getInternalNameForProperty(name);
            this.getNucleusContext().getPersistenceConfiguration().validatePropertyValue(intName, value);
            this.properties.setProperty(intName.toLowerCase(Locale.ENGLISH), value);
        }
        else if (name.equalsIgnoreCase("datanucleus.cache.level2.type")) {
            if ("none".equalsIgnoreCase((String)value)) {
                this.setLevel2Cache(false);
            }
        }
        else {
            final String intName = this.getNucleusContext().getPersistenceConfiguration().getInternalNameForProperty(name);
            if (intName != null && !intName.equalsIgnoreCase(name)) {
                this.getNucleusContext().getPersistenceConfiguration().validatePropertyValue(intName, value);
                this.properties.setProperty(intName.toLowerCase(Locale.ENGLISH), value);
            }
            else {
                NucleusLogger.PERSISTENCE.warn("Attempt to set property \"" + name + "\" on PM/EM yet this is not supported. Ignored");
            }
        }
        if (name.equalsIgnoreCase("datanucleus.SerializeRead")) {
            this.tx.setSerializeRead(this.getBooleanProperty("datanucleus.SerializeRead"));
        }
    }
    
    @Override
    public Map<String, Object> getProperties() {
        final Map<String, Object> props = new HashMap<String, Object>();
        for (final Map.Entry<String, Object> entry : this.properties.getProperties().entrySet()) {
            final String propName = this.nucCtx.getPersistenceConfiguration().getCaseSensitiveNameForPropertyName(entry.getKey());
            props.put(propName, entry.getValue());
        }
        return props;
    }
    
    @Override
    public Boolean getBooleanProperty(final String name) {
        if (this.properties.hasProperty(name.toLowerCase(Locale.ENGLISH))) {
            this.assertIsOpen();
            final String intName = this.getNucleusContext().getPersistenceConfiguration().getInternalNameForProperty(name);
            return this.properties.getBooleanProperty(intName);
        }
        return null;
    }
    
    @Override
    public Integer getIntProperty(final String name) {
        if (this.properties.hasProperty(name.toLowerCase(Locale.ENGLISH))) {
            this.assertIsOpen();
            final String intName = this.getNucleusContext().getPersistenceConfiguration().getInternalNameForProperty(name);
            return this.properties.getIntProperty(intName);
        }
        return null;
    }
    
    @Override
    public String getStringProperty(final String name) {
        if (this.properties.hasProperty(name.toLowerCase(Locale.ENGLISH))) {
            this.assertIsOpen();
            final String intName = this.getNucleusContext().getPersistenceConfiguration().getInternalNameForProperty(name);
            return this.properties.getStringProperty(intName);
        }
        return null;
    }
    
    @Override
    public Object getProperty(final String name) {
        if (this.properties.hasProperty(name.toLowerCase(Locale.ENGLISH))) {
            this.assertIsOpen();
            final String intName = this.getNucleusContext().getPersistenceConfiguration().getInternalNameForProperty(name);
            return this.properties.getProperty(intName.toLowerCase(Locale.ENGLISH));
        }
        return null;
    }
    
    @Override
    public Set<String> getSupportedProperties() {
        return this.nucCtx.getPersistenceConfiguration().getManagedOverrideablePropertyNames();
    }
    
    @Override
    public Integer getDatastoreReadTimeoutMillis() {
        return this.properties.getIntProperty("datanucleus.datastoreReadTimeout".toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public Integer getDatastoreWriteTimeoutMillis() {
        return this.properties.getIntProperty("datanucleus.datastoreWriteTimeout".toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public boolean getMultithreaded() {
        return false;
    }
    
    protected boolean getDetachOnClose() {
        return this.properties.getBooleanProperty("datanucleus.DetachOnClose".toLowerCase(Locale.ENGLISH));
    }
    
    protected boolean getDetachAllOnCommit() {
        return this.properties.getBooleanProperty("datanucleus.DetachAllOnCommit".toLowerCase(Locale.ENGLISH));
    }
    
    protected boolean getDetachAllOnRollback() {
        return this.properties.getBooleanProperty("datanucleus.DetachAllOnRollback".toLowerCase(Locale.ENGLISH));
    }
    
    protected boolean getReachabilityAtCommit() {
        return this.properties.getBooleanProperty("datanucleus.persistenceByReachabilityAtCommit".toLowerCase(Locale.ENGLISH));
    }
    
    public boolean getCopyOnAttach() {
        return this.properties.getBooleanProperty("datanucleus.CopyOnAttach".toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public boolean getIgnoreCache() {
        return this.properties.getBooleanProperty("datanucleus.IgnoreCache".toLowerCase(Locale.ENGLISH));
    }
    
    @Override
    public boolean isDelayDatastoreOperationsEnabled() {
        if (this.isFlushing() || this.tx.isCommitting()) {
            return false;
        }
        final String flushModeString = (String)this.getProperty("datanucleus.flush.mode");
        if (flushModeString != null) {
            return !flushModeString.equalsIgnoreCase("AUTO");
        }
        if (this.tx.isActive()) {
            return this.tx.getOptimistic();
        }
        return !this.isNonTxAtomic();
    }
    
    @Override
    public boolean isInserting(final Object pc) {
        final ObjectProvider op = this.findObjectProvider(pc);
        return op != null && op.isInserting();
    }
    
    @Override
    public Transaction getTransaction() {
        this.assertIsOpen();
        return this.tx;
    }
    
    @Override
    public void enlistInTransaction(final ObjectProvider op) {
        this.assertActiveTransaction();
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(ExecutionContextImpl.LOCALISER.msg("015017", StringUtils.toJVMIDString(op.getObject()), op.getInternalObjectId().toString()));
        }
        if (this.getReachabilityAtCommit() && this.tx.isActive()) {
            if (this.getApiAdapter().isNew(op.getObject())) {
                this.reachabilityFlushedNewIds.add(op.getInternalObjectId());
            }
            else if (this.getApiAdapter().isPersistent(op.getObject()) && !this.getApiAdapter().isDeleted(op.getObject()) && !this.reachabilityFlushedNewIds.contains(op.getInternalObjectId())) {
                this.reachabilityPersistedIds.add(op.getInternalObjectId());
            }
            if (!this.runningPBRAtCommit) {
                this.reachabilityEnlistedIds.add(op.getInternalObjectId());
            }
        }
        this.enlistedOPCache.put(op.getInternalObjectId(), op);
    }
    
    @Override
    public void evictFromTransaction(final ObjectProvider op) {
        if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(ExecutionContextImpl.LOCALISER.msg("015019", StringUtils.toJVMIDString(op.getObject()), IdentityUtils.getIdentityAsString(this.getApiAdapter(), op.getInternalObjectId())));
        }
        if (this.enlistedOPCache.remove(op.getInternalObjectId()) == null && NucleusLogger.TRANSACTION.isDebugEnabled()) {
            NucleusLogger.TRANSACTION.debug(ExecutionContextImpl.LOCALISER.msg("010023", StringUtils.toJVMIDString(op.getObject()), IdentityUtils.getIdentityAsString(this.getApiAdapter(), op.getInternalObjectId())));
        }
    }
    
    @Override
    public boolean isEnlistedInTransaction(final Object id) {
        return this.getReachabilityAtCommit() && this.tx.isActive() && id != null && this.reachabilityEnlistedIds.contains(id);
    }
    
    @Override
    public Object getAttachedObjectForId(final Object id) {
        ObjectProvider op = this.enlistedOPCache.get(id);
        if (op != null) {
            return op.getObject();
        }
        if (this.cache != null) {
            op = ((Map<K, ObjectProvider>)this.cache).get(id);
            if (op != null) {
                return op.getObject();
            }
        }
        return null;
    }
    
    @Override
    public void addObjectProvider(final ObjectProvider op) {
        this.putObjectIntoLevel1Cache(op);
    }
    
    @Override
    public void removeObjectProvider(final ObjectProvider op) {
        this.removeObjectFromLevel1Cache(op.getInternalObjectId());
        this.enlistedOPCache.remove(op.getInternalObjectId());
        if (this.opEmbeddedInfoByEmbedded != null) {
            final List<EmbeddedOwnerRelation> embRels = this.opEmbeddedInfoByEmbedded.get(op);
            if (embRels != null) {
                for (final EmbeddedOwnerRelation rel : embRels) {
                    this.opEmbeddedInfoByOwner.remove(rel.getOwnerOP());
                }
                this.opEmbeddedInfoByEmbedded.remove(op);
            }
        }
        if (this.opEmbeddedInfoByOwner != null) {
            final List<EmbeddedOwnerRelation> embRels = this.opEmbeddedInfoByOwner.get(op);
            if (embRels != null) {
                for (final EmbeddedOwnerRelation rel : embRels) {
                    this.opEmbeddedInfoByEmbedded.remove(rel.getEmbeddedOP());
                }
                this.opEmbeddedInfoByOwner.remove(op);
            }
        }
        if (this.opAssociatedValuesMapByOP != null) {
            this.opAssociatedValuesMapByOP.remove(op);
        }
    }
    
    @Override
    public ObjectProvider findObjectProvider(final Object pc) {
        ObjectProvider op = null;
        final Object previousLookingFor = this.objectLookingForOP;
        final ObjectProvider previousFound = this.foundOP;
        try {
            this.objectLookingForOP = pc;
            this.foundOP = null;
            final ExecutionContext ec = this.getApiAdapter().getExecutionContext(pc);
            if (ec != null && this != ec) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(pc)));
            }
            op = this.foundOP;
        }
        finally {
            this.objectLookingForOP = previousLookingFor;
            this.foundOP = previousFound;
        }
        return op;
    }
    
    @Override
    public ObjectProvider findObjectProvider(final Object pc, final boolean persist) {
        ObjectProvider op = this.findObjectProvider(pc);
        if (op == null && persist) {
            final int objectType = 0;
            final Object object2 = this.persistObjectInternal(pc, null, null, -1, objectType);
            op = this.findObjectProvider(object2);
        }
        else if (op == null) {
            return null;
        }
        return op;
    }
    
    @Override
    public ObjectProvider findObjectProviderForEmbedded(final Object value, final ObjectProvider owner, final AbstractMemberMetaData mmd) {
        ObjectProvider embeddedOP = this.findObjectProvider(value);
        if (embeddedOP == null) {
            embeddedOP = this.newObjectProviderForEmbedded(value, false, owner, owner.getClassMetaData().getMetaDataForMember(mmd.getName()).getAbsoluteFieldNumber());
        }
        if (embeddedOP.getEmbeddedOwners() == null || embeddedOP.getEmbeddedOwners().length == 0) {
            final int absoluteFieldNumber = owner.getClassMetaData().getMetaDataForMember(mmd.getName()).getAbsoluteFieldNumber();
            this.registerEmbeddedRelation(owner, absoluteFieldNumber, embeddedOP);
            embeddedOP.setPcObjectType((short)1);
        }
        return embeddedOP;
    }
    
    @Override
    public ObjectProvider findObjectProviderOfOwnerForAttachingObject(final Object pc) {
        final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
        try {
            if (threadInfo.attachedOwnerByObject == null) {
                return null;
            }
            return threadInfo.attachedOwnerByObject.get(pc);
        }
        finally {
            this.releaseThreadContextInfo();
        }
    }
    
    @Override
    public ObjectProvider newObjectProviderForHollow(final Class pcClass, final Object id) {
        return this.nucCtx.getObjectProviderFactory().newForHollow(this, pcClass, id);
    }
    
    @Override
    public ObjectProvider newObjectProviderForHollowPreConstructed(final Object id, final Object pc) {
        return this.nucCtx.getObjectProviderFactory().newForHollowPreConstructed(this, id, pc);
    }
    
    @Override
    public ObjectProvider newObjectProviderForHollowPopulated(final Class pcClass, final Object id, final FieldValues fv) {
        return this.nucCtx.getObjectProviderFactory().newForHollow(this, pcClass, id, fv);
    }
    
    @Override
    public ObjectProvider newObjectProviderForPersistentClean(final Object id, final Object pc) {
        return this.nucCtx.getObjectProviderFactory().newForPersistentClean(this, id, pc);
    }
    
    @Override
    @Deprecated
    public ObjectProvider newObjectProviderForHollowPopulatedAppId(final Class pcClass, final FieldValues fv) {
        return this.nucCtx.getObjectProviderFactory().newForHollowPopulatedAppId(this, pcClass, fv);
    }
    
    @Override
    public ObjectProvider newObjectProviderForEmbedded(final Object pc, final boolean copyPc, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        return this.nucCtx.getObjectProviderFactory().newForEmbedded(this, pc, copyPc, ownerOP, ownerFieldNumber);
    }
    
    @Override
    public ObjectProvider newObjectProviderForEmbedded(final AbstractClassMetaData cmd, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        return this.nucCtx.getObjectProviderFactory().newForEmbedded(this, cmd, ownerOP, ownerFieldNumber);
    }
    
    @Override
    public ObjectProvider newObjectProviderForPersistentNew(final Object pc, final FieldValues preInsertChanges) {
        return this.nucCtx.getObjectProviderFactory().newForPersistentNew(this, pc, preInsertChanges);
    }
    
    @Override
    public ObjectProvider newObjectProviderForTransactionalTransient(final Object pc) {
        return this.nucCtx.getObjectProviderFactory().newForTransactionalTransient(this, pc);
    }
    
    @Override
    public ObjectProvider newObjectProviderForDetached(final Object pc, final Object id, final Object version) {
        return this.nucCtx.getObjectProviderFactory().newForDetached(this, pc, id, version);
    }
    
    @Override
    public ObjectProvider newObjectProviderForPNewToBeDeleted(final Object pc) {
        return this.nucCtx.getObjectProviderFactory().newForPNewToBeDeleted(this, pc);
    }
    
    protected ObjectProvider newObjectProviderForCachedPC(final Object id, final CachedPC cachedPC) {
        return this.nucCtx.getObjectProviderFactory().newForCachedPC(this, id, cachedPC);
    }
    
    @Override
    public void hereIsObjectProvider(final ObjectProvider op, final Object pc) {
        if (this.objectLookingForOP == pc) {
            this.foundOP = op;
        }
    }
    
    protected void disconnectObjectProvidersFromCache() {
        if (this.cache != null) {
            final Collection<ObjectProvider> cachedOPsClone = new HashSet<ObjectProvider>(((Map<K, ? extends ObjectProvider>)this.cache).values());
            for (final ObjectProvider op : cachedOPsClone) {
                if (op != null) {
                    op.disconnect();
                }
            }
            this.cache.clear();
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003011"));
            }
        }
    }
    
    private boolean isNonTxAtomic() {
        return this.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.nontx.atomic");
    }
    
    @Override
    public void processNontransactionalUpdate() {
        if (this.tx.isActive() || !this.tx.getNontransactionalWrite() || !this.isNonTxAtomic()) {
            return;
        }
        final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
        if (threadInfo.nontxPersistDelete) {
            return;
        }
        this.processNontransactionalAtomicChanges();
    }
    
    protected void processNontransactionalAtomicChanges() {
        if (this.tx.isActive() || !this.tx.getNontransactionalWrite()) {
            return;
        }
        if (!this.isNonTxAtomic()) {
            return;
        }
        if (!this.dirtyOPs.isEmpty()) {
            for (final ObjectProvider op : this.dirtyOPs) {
                this.enlistedOPCache.put(op.getInternalObjectId(), op);
            }
            this.flushInternal(true);
            if (this.l2CacheEnabled) {
                this.performLevel2CacheUpdateAtCommit();
            }
            if (this.getDetachAllOnCommit()) {
                this.performDetachAllOnTxnEndPreparation();
                this.performDetachAllOnTxnEnd();
            }
            List failures = null;
            try {
                final ApiAdapter api = this.getApiAdapter();
                final ObjectProvider[] ops = this.enlistedOPCache.values().toArray(new ObjectProvider[this.enlistedOPCache.size()]);
                for (int i = 0; i < ops.length; ++i) {
                    try {
                        if (ops[i] != null && ops[i].getObject() != null && api.isPersistent(ops[i].getObject()) && api.isDirty(ops[i].getObject())) {
                            ops[i].postCommit(this.getTransaction());
                        }
                        else {
                            NucleusLogger.PERSISTENCE.debug(">> Atomic nontransactional processing : Not performing postCommit on " + ops[i]);
                        }
                    }
                    catch (RuntimeException e) {
                        if (failures == null) {
                            failures = new ArrayList();
                        }
                        failures.add(e);
                    }
                }
            }
            finally {
                this.resetTransactionalVariables();
            }
            if (failures != null && !failures.isEmpty()) {
                throw new CommitStateTransitionException(failures.toArray(new Exception[failures.size()]));
            }
        }
        if (this.nontxProcessedOPs != null && !this.nontxProcessedOPs.isEmpty()) {
            for (final ObjectProvider op : this.nontxProcessedOPs) {
                if (op != null && op.getLifecycleState() != null && op.getLifecycleState().isDeleted()) {
                    this.removeObjectFromLevel1Cache(op.getInternalObjectId());
                    this.removeObjectFromLevel2Cache(op.getInternalObjectId());
                }
            }
            this.nontxProcessedOPs.clear();
        }
    }
    
    @Override
    public void evictObject(final Object obj) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            this.assertNotDetached(obj);
            final ObjectProvider op = this.findObjectProvider(obj);
            if (op == null) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(obj)));
            }
            op.evict();
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void evictObjects(final Class cls, final boolean subclasses) {
        if (this.cache != null) {
            try {
                if (this.getMultithreaded()) {
                    this.lock.lock();
                }
                final ArrayList<ObjectProvider> opsToEvict = new ArrayList<ObjectProvider>();
                opsToEvict.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
                for (final ObjectProvider op : opsToEvict) {
                    final Object pc = op.getObject();
                    boolean evict = false;
                    if (!subclasses && pc.getClass() == cls) {
                        evict = true;
                    }
                    else if (subclasses && cls.isAssignableFrom(pc.getClass())) {
                        evict = true;
                    }
                    if (evict) {
                        op.evict();
                        this.removeObjectFromLevel1Cache(this.getApiAdapter().getIdForObject(pc));
                    }
                }
            }
            finally {
                if (this.getMultithreaded()) {
                    this.lock.unlock();
                }
            }
        }
    }
    
    @Override
    public void evictAllObjects() {
        if (this.cache != null) {
            final ArrayList<ObjectProvider> opsToEvict = new ArrayList<ObjectProvider>();
            opsToEvict.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
            for (final ObjectProvider op : opsToEvict) {
                final Object pc = op.getObject();
                op.evict();
                this.removeObjectFromLevel1Cache(this.getApiAdapter().getIdForObject(pc));
            }
        }
    }
    
    @Override
    public void refreshObject(final Object obj) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            this.assertNotDetached(obj);
            final ObjectProvider op = this.findObjectProvider(obj);
            if (op == null) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(obj)));
            }
            if (this.getApiAdapter().isPersistent(obj) && op.isWaitingToBeFlushedToDatastore()) {
                return;
            }
            op.refresh();
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void refreshAllObjects() {
        final Set<ObjectProvider> toRefresh = new HashSet<ObjectProvider>();
        toRefresh.addAll(this.enlistedOPCache.values());
        toRefresh.addAll(this.dirtyOPs);
        toRefresh.addAll(this.indirectDirtyOPs);
        if (!this.tx.isActive() && this.cache != null) {
            toRefresh.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
        }
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            List failures = null;
            for (final ObjectProvider op : toRefresh) {
                try {
                    op.refresh();
                }
                catch (RuntimeException e) {
                    if (failures == null) {
                        failures = new ArrayList();
                    }
                    failures.add(e);
                }
            }
            if (failures != null && !failures.isEmpty()) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010037"), failures.toArray(new Exception[failures.size()]));
            }
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
    }
    
    @Override
    public void retrieveObject(final Object obj, final boolean fgOnly) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            this.assertNotDetached(obj);
            final ObjectProvider op = this.findObjectProvider(obj);
            if (op == null) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(obj)));
            }
            op.retrieve(fgOnly);
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public Object persistObject(final Object obj, final boolean merging) {
        if (obj == null) {
            return null;
        }
        final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
        try {
            boolean allowMergeOfTransient = this.nucCtx.getPersistenceConfiguration().getBooleanProperty("datanucleus.allowAttachOfTransient", false);
            if (this.getBooleanProperty("datanucleus.allowAttachOfTransient") != null) {
                allowMergeOfTransient = this.getBooleanProperty("datanucleus.allowAttachOfTransient");
            }
            if (merging && allowMergeOfTransient) {
                threadInfo.merging = true;
            }
            if (threadInfo.attachedOwnerByObject == null) {
                threadInfo.attachedOwnerByObject = new HashMap<Object, ObjectProvider>();
            }
            if (threadInfo.attachedPCById == null) {
                threadInfo.attachedPCById = new HashMap();
            }
            if (this.tx.isActive()) {
                return this.persistObjectWork(obj);
            }
            threadInfo.nontxPersistDelete = true;
            boolean success = true;
            final Set cachedIds = (this.cache != null) ? new HashSet(this.cache.keySet()) : null;
            try {
                return this.persistObjectWork(obj);
            }
            catch (RuntimeException re) {
                success = false;
                if (this.cache != null) {
                    final Iterator cacheIter = ((Map<Object, V>)this.cache).keySet().iterator();
                    while (cacheIter.hasNext()) {
                        final Object id = cacheIter.next();
                        if (!cachedIds.contains(id)) {
                            cacheIter.remove();
                        }
                    }
                }
                throw re;
            }
            finally {
                threadInfo.nontxPersistDelete = false;
                if (success) {
                    this.processNontransactionalAtomicChanges();
                }
            }
        }
        finally {
            this.releaseThreadContextInfo();
        }
    }
    
    @Override
    public Object[] persistObjects(final Object[] objs) {
        if (objs == null) {
            return null;
        }
        final Object[] persistedObjs = new Object[objs.length];
        final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
        try {
            if (threadInfo.attachedOwnerByObject == null) {
                threadInfo.attachedOwnerByObject = new HashMap<Object, ObjectProvider>();
            }
            if (threadInfo.attachedPCById == null) {
                threadInfo.attachedPCById = new HashMap();
            }
            if (!this.tx.isActive()) {
                threadInfo.nontxPersistDelete = true;
            }
            try {
                this.getStoreManager().getPersistenceHandler().batchStart(this, PersistenceBatchType.PERSIST);
                ArrayList<RuntimeException> failures = null;
                for (int i = 0; i < objs.length; ++i) {
                    try {
                        if (objs[i] != null) {
                            persistedObjs[i] = this.persistObjectWork(objs[i]);
                        }
                    }
                    catch (RuntimeException e) {
                        if (failures == null) {
                            failures = new ArrayList<RuntimeException>();
                        }
                        failures.add(e);
                    }
                }
                if (failures != null && !failures.isEmpty()) {
                    final RuntimeException e2 = failures.get(0);
                    if (e2 instanceof NucleusException && ((NucleusException)e2).isFatal()) {
                        throw new NucleusFatalUserException(ExecutionContextImpl.LOCALISER.msg("010039"), failures.toArray(new Exception[failures.size()]));
                    }
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010039"), failures.toArray(new Exception[failures.size()]));
                }
            }
            finally {
                this.getStoreManager().getPersistenceHandler().batchEnd(this, PersistenceBatchType.PERSIST);
                if (!this.tx.isActive()) {
                    threadInfo.nontxPersistDelete = false;
                    this.processNontransactionalAtomicChanges();
                }
            }
        }
        finally {
            this.releaseThreadContextInfo();
        }
        return persistedObjs;
    }
    
    private Object persistObjectWork(final Object obj) {
        final boolean detached = this.getApiAdapter().isDetached(obj);
        final Object persistedPc = this.persistObjectInternal(obj, null, null, -1, 0);
        final ObjectProvider op = this.findObjectProvider(persistedPc);
        if (op != null) {
            if (this.indirectDirtyOPs.contains(op)) {
                this.dirtyOPs.add(op);
                this.indirectDirtyOPs.remove(op);
            }
            else if (!this.dirtyOPs.contains(op)) {
                this.dirtyOPs.add(op);
                if (this.l2CacheTxIds != null && this.nucCtx.isClassCacheable(op.getClassMetaData())) {
                    this.l2CacheTxIds.add(op.getInternalObjectId());
                }
            }
            if (this.getReachabilityAtCommit() && this.tx.isActive() && (detached || this.getApiAdapter().isNew(persistedPc))) {
                this.reachabilityPersistedIds.add(op.getInternalObjectId());
            }
        }
        return persistedPc;
    }
    
    @Override
    public Object persistObjectInternal(final Object obj, final FieldValues preInsertChanges, final ObjectProvider ownerOP, final int ownerFieldNum, final int objectType) {
        if (obj == null) {
            return null;
        }
        final ApiAdapter api = this.getApiAdapter();
        Object id = null;
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            final ExecutionContext ec = api.getExecutionContext(obj);
            if (ec != null && ec != this) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", obj));
            }
            boolean cacheable = false;
            Object persistedPc = obj;
            if (api.isDetached(obj)) {
                this.assertDetachable(obj);
                if (this.getCopyOnAttach()) {
                    persistedPc = this.attachObjectCopy(ownerOP, obj, api.getIdForObject(obj) == null);
                }
                else {
                    this.attachObject(ownerOP, obj, api.getIdForObject(obj) == null);
                    persistedPc = obj;
                }
            }
            else if (api.isTransactional(obj) && !api.isPersistent(obj)) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010015", StringUtils.toJVMIDString(obj)));
                }
                final ObjectProvider op = this.findObjectProvider(obj);
                if (op == null) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(obj)));
                }
                op.makePersistentTransactionalTransient();
            }
            else if (!api.isPersistent(obj)) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010015", StringUtils.toJVMIDString(obj)));
                }
                boolean merged = false;
                final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
                try {
                    if (threadInfo.merging) {
                        final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(obj.getClass(), this.clr);
                        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                            final Object transientId = api.getNewApplicationIdentityObjectId(obj, cmd);
                            final Object existingObj = this.findObject(transientId, true, true, cmd.getFullClassName());
                            final ObjectProvider existingOP = this.findObjectProvider(existingObj);
                            existingOP.attach(obj);
                            id = transientId;
                            merged = true;
                            persistedPc = existingObj;
                        }
                        cacheable = this.nucCtx.isClassCacheable(cmd);
                    }
                }
                catch (NucleusObjectNotFoundException onfe) {}
                finally {
                    this.releaseThreadContextInfo();
                }
                if (!merged) {
                    ObjectProvider op2 = this.findObjectProvider(obj);
                    if (op2 == null) {
                        if ((objectType == 2 || objectType == 3 || objectType == 4 || objectType == 1) && ownerOP != null) {
                            op2 = this.newObjectProviderForEmbedded(obj, false, ownerOP, ownerFieldNum);
                            op2.setPcObjectType((short)objectType);
                            op2.makePersistent();
                            id = op2.getInternalObjectId();
                        }
                        else {
                            op2 = this.newObjectProviderForPersistentNew(obj, preInsertChanges);
                            op2.makePersistent();
                            id = op2.getInternalObjectId();
                        }
                    }
                    else if (op2.getReferencedPC() == null) {
                        op2.makePersistent();
                        id = op2.getInternalObjectId();
                    }
                    else {
                        persistedPc = op2.getReferencedPC();
                    }
                    if (op2 != null) {
                        cacheable = this.nucCtx.isClassCacheable(op2.getClassMetaData());
                    }
                }
            }
            else if (api.isPersistent(obj) && api.getIdForObject(obj) == null) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010015", StringUtils.toJVMIDString(obj)));
                }
                final ObjectProvider op = this.findObjectProvider(obj);
                op.makePersistent();
                id = op.getInternalObjectId();
                cacheable = this.nucCtx.isClassCacheable(op.getClassMetaData());
            }
            else if (api.isDeleted(obj)) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010015", StringUtils.toJVMIDString(obj)));
                }
                final ObjectProvider op = this.findObjectProvider(obj);
                op.makePersistent();
                id = op.getInternalObjectId();
                cacheable = this.nucCtx.isClassCacheable(op.getClassMetaData());
            }
            else if (api.isPersistent(obj) && api.isTransactional(obj) && api.isDirty(obj) && this.isDelayDatastoreOperationsEnabled()) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010015", StringUtils.toJVMIDString(obj)));
                }
                final ObjectProvider op = this.findObjectProvider(obj);
                op.makePersistent();
                id = op.getInternalObjectId();
                cacheable = this.nucCtx.isClassCacheable(op.getClassMetaData());
            }
            if (id != null && this.l2CacheTxIds != null && cacheable) {
                this.l2CacheTxIds.add(id);
            }
            return persistedPc;
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public Object persistObjectInternal(final Object pc, final ObjectProvider ownerOP, final int ownerFieldNum, final int objectType) {
        if (ownerOP != null) {
            final ObjectProvider op = this.findObjectProvider(ownerOP.getObject());
            return this.persistObjectInternal(pc, null, op, ownerFieldNum, objectType);
        }
        return this.persistObjectInternal(pc, null, null, ownerFieldNum, objectType);
    }
    
    @Override
    public Object persistObjectInternal(final Object pc, final FieldValues preInsertChanges, final int objectType) {
        return this.persistObjectInternal(pc, preInsertChanges, null, -1, objectType);
    }
    
    @Override
    public void deleteObjects(final Object[] objs) {
        if (objs == null) {
            return;
        }
        final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
        try {
            if (!this.tx.isActive()) {
                threadInfo.nontxPersistDelete = true;
            }
            this.getStoreManager().getPersistenceHandler().batchStart(this, PersistenceBatchType.DELETE);
            ArrayList<RuntimeException> failures = null;
            for (int i = 0; i < objs.length; ++i) {
                try {
                    if (objs[i] != null) {
                        this.deleteObjectWork(objs[i]);
                    }
                }
                catch (RuntimeException e) {
                    if (failures == null) {
                        failures = new ArrayList<RuntimeException>();
                    }
                    failures.add(e);
                }
            }
            if (failures != null && !failures.isEmpty()) {
                final RuntimeException e2 = failures.get(0);
                if (e2 instanceof NucleusException && ((NucleusException)e2).isFatal()) {
                    throw new NucleusFatalUserException(ExecutionContextImpl.LOCALISER.msg("010040"), failures.toArray(new Exception[failures.size()]));
                }
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010040"), failures.toArray(new Exception[failures.size()]));
            }
        }
        finally {
            this.getStoreManager().getPersistenceHandler().batchEnd(this, PersistenceBatchType.DELETE);
            if (!this.tx.isActive()) {
                threadInfo.nontxPersistDelete = false;
                this.processNontransactionalAtomicChanges();
            }
            this.releaseThreadContextInfo();
        }
    }
    
    @Override
    public void deleteObject(final Object obj) {
        if (obj == null) {
            return;
        }
        final ThreadContextInfo threadInfo = this.acquireThreadContextInfo();
        try {
            if (!this.tx.isActive()) {
                threadInfo.nontxPersistDelete = true;
            }
            this.deleteObjectWork(obj);
        }
        finally {
            if (!this.tx.isActive()) {
                threadInfo.nontxPersistDelete = false;
                this.processNontransactionalAtomicChanges();
            }
            this.releaseThreadContextInfo();
        }
    }
    
    void deleteObjectWork(final Object obj) {
        ObjectProvider op = this.findObjectProvider(obj);
        if (op == null && this.getApiAdapter().isDetached(obj)) {
            final Object attachedObj = this.findObject(this.getApiAdapter().getIdForObject(obj), true, false, obj.getClass().getName());
            op = this.findObjectProvider(attachedObj);
        }
        if (op != null) {
            if (this.indirectDirtyOPs.contains(op)) {
                this.indirectDirtyOPs.remove(op);
                this.dirtyOPs.add(op);
            }
            else if (!this.dirtyOPs.contains(op)) {
                this.dirtyOPs.add(op);
                if (this.l2CacheTxIds != null && this.nucCtx.isClassCacheable(op.getClassMetaData())) {
                    this.l2CacheTxIds.add(op.getInternalObjectId());
                }
            }
        }
        this.deleteObjectInternal(obj);
        if (this.getReachabilityAtCommit() && this.tx.isActive() && op != null && this.getApiAdapter().isDeleted(obj)) {
            this.reachabilityDeletedIds.add(op.getInternalObjectId());
        }
    }
    
    @Override
    public void deleteObjectInternal(final Object obj) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            Object pc = obj;
            if (this.getApiAdapter().isDetached(obj)) {
                pc = this.findObject(this.getApiAdapter().getIdForObject(obj), true, true, null);
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010019", StringUtils.toJVMIDString(pc)));
            }
            if (this.getApiAdapter().getName().equals("JDO")) {
                if (!this.getApiAdapter().isPersistent(pc) && !this.getApiAdapter().isTransactional(pc)) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010020"));
                }
                if (!this.getApiAdapter().isPersistent(pc) && this.getApiAdapter().isTransactional(pc)) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010021", this.getApiAdapter().getIdForObject(obj)));
                }
            }
            ObjectProvider op = this.findObjectProvider(pc);
            if (op == null) {
                if (!this.getApiAdapter().allowDeleteOfNonPersistentObject()) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(pc)));
                }
                op = this.newObjectProviderForPNewToBeDeleted(pc);
            }
            if (this.l2CacheTxIds != null && this.nucCtx.isClassCacheable(op.getClassMetaData())) {
                this.l2CacheTxIds.add(op.getInternalObjectId());
            }
            op.deletePersistent();
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void makeObjectTransient(final Object obj, final FetchPlanState state) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            this.assertNotDetached(obj);
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010022", StringUtils.toJVMIDString(obj)));
            }
            if (this.getApiAdapter().isPersistent(obj)) {
                final ObjectProvider op = this.findObjectProvider(obj);
                op.makeTransient(state);
            }
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void makeObjectTransactional(final Object obj) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            this.assertNotDetached(obj);
            if (this.getApiAdapter().isPersistent(obj)) {
                this.assertActiveTransaction();
            }
            ObjectProvider op = this.findObjectProvider(obj);
            if (op == null) {
                op = this.newObjectProviderForTransactionalTransient(obj);
            }
            op.makeTransactional();
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void makeObjectNontransactional(final Object obj) {
        if (obj == null) {
            return;
        }
        try {
            this.clr.setPrimary(obj.getClass().getClassLoader());
            this.assertClassPersistable(obj.getClass());
            if (!this.getApiAdapter().isPersistent(obj) && this.getApiAdapter().isTransactional(obj) && this.getApiAdapter().isDirty(obj)) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010024"));
            }
            final ObjectProvider op = this.findObjectProvider(obj);
            op.makeNontransactional();
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void attachObject(final ObjectProvider ownerOP, final Object pc, final boolean sco) {
        this.assertClassPersistable(pc.getClass());
        final Map attachedOwnerByObject = this.getThreadContextInfo().attachedOwnerByObject;
        if (attachedOwnerByObject != null) {
            attachedOwnerByObject.put(pc, ownerOP);
        }
        final ApiAdapter api = this.getApiAdapter();
        final Object id = api.getIdForObject(pc);
        if (id != null && this.isInserting(pc)) {
            return;
        }
        if (id == null && !sco) {
            this.persistObjectInternal(pc, null, null, -1, 0);
            return;
        }
        if (api.isDetached(pc)) {
            if (this.cache != null) {
                final ObjectProvider l1CachedOP = ((Map<K, ObjectProvider>)this.cache).get(id);
                if (l1CachedOP != null && l1CachedOP.getObject() != pc) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010017", StringUtils.toJVMIDString(pc)));
                }
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010016", StringUtils.toJVMIDString(pc)));
            }
            final ObjectProvider op = this.newObjectProviderForDetached(pc, id, api.getVersionForObject(pc));
            op.attach(sco);
        }
    }
    
    @Override
    public Object attachObjectCopy(final ObjectProvider ownerOP, final Object pc, final boolean sco) {
        this.assertClassPersistable(pc.getClass());
        this.assertDetachable(pc);
        final Map attachedOwnerByObject = this.getThreadContextInfo().attachedOwnerByObject;
        if (attachedOwnerByObject != null) {
            attachedOwnerByObject.put(pc, ownerOP);
        }
        final ApiAdapter api = this.getApiAdapter();
        final Object id = api.getIdForObject(pc);
        if (id != null && this.isInserting(pc)) {
            return pc;
        }
        if (id == null && !sco) {
            return this.persistObjectInternal(pc, null, null, -1, 0);
        }
        if (api.isPersistent(pc)) {
            return pc;
        }
        Object pcTarget = null;
        if (sco) {
            final boolean detached = this.getApiAdapter().isDetached(pc);
            final ObjectProvider targetOP = this.newObjectProviderForEmbedded(pc, true, null, -1);
            pcTarget = targetOP.getObject();
            if (detached) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010018", StringUtils.toJVMIDString(pc), StringUtils.toJVMIDString(pcTarget)));
                }
                targetOP.attachCopy(pc, sco);
            }
        }
        else {
            final boolean detached = this.getApiAdapter().isDetached(pc);
            pcTarget = this.findObject(id, false, false, pc.getClass().getName());
            if (detached) {
                Object obj = null;
                final Map attachedPCById = this.getThreadContextInfo().attachedPCById;
                if (attachedPCById != null) {
                    obj = attachedPCById.get(this.getApiAdapter().getIdForObject(pc));
                }
                if (obj != null) {
                    pcTarget = obj;
                }
                else {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010018", StringUtils.toJVMIDString(pc), StringUtils.toJVMIDString(pcTarget)));
                    }
                    pcTarget = this.findObjectProvider(pcTarget).attachCopy(pc, sco);
                    if (attachedPCById != null) {
                        attachedPCById.put(this.getApiAdapter().getIdForObject(pc), pcTarget);
                    }
                }
            }
        }
        return pcTarget;
    }
    
    @Override
    public void detachObject(final Object obj, final FetchPlanState state) {
        if (this.getApiAdapter().isDetached(obj)) {
            return;
        }
        if (!this.getApiAdapter().isPersistent(obj)) {
            if (this.runningDetachAllOnTxnEnd && !this.getMetaDataManager().getMetaDataForClass(obj.getClass(), this.clr).isDetachable()) {
                return;
            }
            if (this.tx.isActive()) {
                this.persistObjectInternal(obj, null, null, -1, 0);
            }
        }
        final ObjectProvider op = this.findObjectProvider(obj);
        if (op == null) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(obj)));
        }
        op.detach(state);
        if (this.dirtyOPs.contains(op) || this.indirectDirtyOPs.contains(op)) {
            NucleusLogger.GENERAL.info(ExecutionContextImpl.LOCALISER.msg("010047", StringUtils.toJVMIDString(obj)));
            this.clearDirty(op);
        }
    }
    
    @Override
    public Object detachObjectCopy(final Object pc, final FetchPlanState state) {
        Object thePC = pc;
        try {
            this.clr.setPrimary(pc.getClass().getClassLoader());
            if (!this.getApiAdapter().isPersistent(pc) && !this.getApiAdapter().isDetached(pc)) {
                if (!this.tx.isActive()) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010014"));
                }
                thePC = this.persistObjectInternal(pc, null, null, -1, 0);
            }
            if (this.getApiAdapter().isDetached(thePC)) {
                thePC = this.findObject(this.getApiAdapter().getIdForObject(thePC), false, true, null);
            }
            final ObjectProvider op = this.findObjectProvider(thePC);
            if (op == null) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010007", this.getApiAdapter().getIdForObject(thePC)));
            }
            return op.detachCopy(state);
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public void detachAll() {
        final Collection<ObjectProvider> opsToDetach = new HashSet<ObjectProvider>();
        opsToDetach.addAll(this.enlistedOPCache.values());
        if (this.cache != null) {
            opsToDetach.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
        }
        final FetchPlanState fps = new FetchPlanState();
        final Iterator<ObjectProvider> iter = opsToDetach.iterator();
        while (iter.hasNext()) {
            iter.next().detach(fps);
        }
    }
    
    @Override
    public Object getAttachDetachReferencedObject(final ObjectProvider op) {
        if (this.opAttachDetachObjectReferenceMap == null) {
            return null;
        }
        return this.opAttachDetachObjectReferenceMap.get(op);
    }
    
    @Override
    public void setAttachDetachReferencedObject(final ObjectProvider op, final Object obj) {
        if (obj != null) {
            if (this.opAttachDetachObjectReferenceMap == null) {
                this.opAttachDetachObjectReferenceMap = new HashMap<ObjectProvider, Object>();
            }
            this.opAttachDetachObjectReferenceMap.put(op, obj);
        }
        else if (this.opAttachDetachObjectReferenceMap != null) {
            this.opAttachDetachObjectReferenceMap.remove(op);
        }
    }
    
    @Override
    public Object newInstance(final Class cls) {
        if (this.getApiAdapter().isPersistable(cls) && !Modifier.isAbstract(cls.getModifiers())) {
            try {
                return cls.newInstance();
            }
            catch (IllegalAccessException iae) {
                throw new NucleusUserException(iae.toString(), iae);
            }
            catch (InstantiationException ie) {
                throw new NucleusUserException(ie.toString(), ie);
            }
        }
        this.assertHasImplementationCreator();
        return this.getNucleusContext().getImplementationCreator().newInstance(cls, this.clr);
    }
    
    @Override
    public boolean exists(final Object obj) {
        if (obj == null) {
            return false;
        }
        final Object id = this.getApiAdapter().getIdForObject(obj);
        if (id == null) {
            return false;
        }
        try {
            this.findObject(id, true, false, obj.getClass().getName());
        }
        catch (NucleusObjectNotFoundException onfe) {
            return false;
        }
        return true;
    }
    
    @Override
    public Set getManagedObjects() {
        if (!this.tx.isActive()) {
            return null;
        }
        final Set objs = new HashSet();
        for (final ObjectProvider op : this.enlistedOPCache.values()) {
            objs.add(op.getObject());
        }
        return objs;
    }
    
    @Override
    public Set getManagedObjects(final Class[] classes) {
        if (!this.tx.isActive()) {
            return null;
        }
        final Set objs = new HashSet();
        for (final ObjectProvider op : this.enlistedOPCache.values()) {
            for (int i = 0; i < classes.length; ++i) {
                if (classes[i] == op.getObject().getClass()) {
                    objs.add(op.getObject());
                    break;
                }
            }
        }
        return objs;
    }
    
    @Override
    public Set getManagedObjects(final String[] states) {
        if (!this.tx.isActive()) {
            return null;
        }
        final Set objs = new HashSet();
        for (final ObjectProvider op : this.enlistedOPCache.values()) {
            for (int i = 0; i < states.length; ++i) {
                if (this.getApiAdapter().getObjectState(op.getObject()).equals(states[i])) {
                    objs.add(op.getObject());
                    break;
                }
            }
        }
        return objs;
    }
    
    @Override
    public Set getManagedObjects(final String[] states, final Class[] classes) {
        if (!this.tx.isActive()) {
            return null;
        }
        final Set objs = new HashSet();
        for (final ObjectProvider op : this.enlistedOPCache.values()) {
            boolean matches = false;
            for (int i = 0; i < states.length; ++i) {
                if (this.getApiAdapter().getObjectState(op.getObject()).equals(states[i])) {
                    final int j = 0;
                    while (i < classes.length) {
                        if (classes[j] == op.getObject().getClass()) {
                            matches = true;
                            objs.add(op.getObject());
                            break;
                        }
                        ++i;
                    }
                }
                if (matches) {
                    break;
                }
            }
        }
        return objs;
    }
    
    @Override
    public Object findObject(Object id, final FieldValues fv, Class cls, final boolean ignoreCache, final boolean checkInheritance) {
        this.assertIsOpen();
        boolean createdHollow = false;
        Object pc = null;
        ObjectProvider op = null;
        if (!ignoreCache) {
            pc = this.getObjectFromCache(id);
        }
        if (pc == null) {
            pc = this.getStoreManager().getPersistenceHandler().findObject(this, id);
        }
        if (pc == null) {
            String className = (cls != null) ? cls.getName() : null;
            if (!(id instanceof SCOID)) {
                final ClassDetailsForId details = this.getClassDetailsForId(id, className, checkInheritance);
                if (details.className != null && cls != null && !cls.getName().equals(details.className)) {
                    cls = this.clr.classForName(details.className);
                }
                className = details.className;
                id = details.id;
                if (details.pc != null) {
                    pc = details.pc;
                    op = this.findObjectProvider(pc);
                }
            }
            if (pc == null) {
                if (cls == null) {
                    try {
                        cls = this.clr.classForName(className, id.getClass().getClassLoader());
                    }
                    catch (ClassNotResolvedException e) {
                        final String msg = ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id));
                        NucleusLogger.PERSISTENCE.warn(msg);
                        throw new NucleusUserException(msg, e);
                    }
                }
                createdHollow = true;
                op = this.newObjectProviderForHollowPopulated(cls, id, fv);
                pc = op.getObject();
                this.putObjectIntoLevel1Cache(op);
                this.putObjectIntoLevel2Cache(op, false);
            }
        }
        if (pc != null && fv != null && !createdHollow) {
            if (op == null) {
                op = this.findObjectProvider(pc);
            }
            if (op != null) {
                fv.fetchNonLoadedFields(op);
            }
        }
        return pc;
    }
    
    @Override
    public Object[] findObjects(final Object[] identities, final boolean validate) {
        if (identities == null) {
            return null;
        }
        if (identities.length == 1) {
            return new Object[] { this.findObject(identities[0], validate, validate, null) };
        }
        for (int i = 0; i < identities.length; ++i) {
            if (identities[i] == null) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010044"));
            }
        }
        final Object[] ids = new Object[identities.length];
        for (int j = 0; j < identities.length; ++j) {
            if (this.getNucleusContext().getIdentityStringTranslator() != null && identities[j] instanceof String) {
                final IdentityStringTranslator translator = this.getNucleusContext().getIdentityStringTranslator();
                ids[j] = translator.getIdentity(this, (String)identities[j]);
            }
            else {
                ids[j] = identities[j];
            }
        }
        final Map pcById = new HashMap(identities.length);
        final List idsToFind = new ArrayList();
        final ApiAdapter api = this.getApiAdapter();
        for (int k = 0; k < ids.length; ++k) {
            final Object pc = this.getObjectFromLevel1Cache(ids[k]);
            if (pc != null) {
                if (ids[k] instanceof SCOID && api.isPersistent(pc) && !api.isNew(pc) && !api.isDeleted(pc) && !api.isTransactional(pc)) {
                    throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010005"));
                }
                pcById.put(ids[k], pc);
            }
            else {
                idsToFind.add(ids[k]);
            }
        }
        if (!idsToFind.isEmpty() && this.l2CacheEnabled) {
            final Map pcsById = this.getObjectsFromLevel2Cache(idsToFind);
            if (!pcsById.isEmpty()) {
                for (final Map.Entry entry : pcsById.entrySet()) {
                    pcById.put(entry.getKey(), entry.getValue());
                    idsToFind.remove(entry.getKey());
                }
            }
        }
        final boolean performValidationWhenCached = this.nucCtx.getPersistenceConfiguration().getBooleanProperty("datanucleus.findObject.validateWhenCached");
        final List<ObjectProvider> opsToValidate = new ArrayList<ObjectProvider>();
        if (validate && performValidationWhenCached) {
            final Collection pcValues = pcById.values();
            for (final Object pc2 : pcValues) {
                if (api.isTransactional(pc2)) {
                    continue;
                }
                final ObjectProvider op = this.findObjectProvider(pc2);
                opsToValidate.add(op);
            }
        }
        Object[] foundPcs = null;
        if (!idsToFind.isEmpty()) {
            foundPcs = this.getStoreManager().getPersistenceHandler().findObjects(this, idsToFind.toArray());
        }
        int foundPcIdx = 0;
        for (final Object idOrig : idsToFind) {
            Object id = idOrig;
            Object pc3 = foundPcs[foundPcIdx++];
            ObjectProvider op2 = null;
            if (pc3 != null) {
                op2 = this.findObjectProvider(pc3);
                this.putObjectIntoLevel1Cache(op2);
            }
            else {
                final ClassDetailsForId details = this.getClassDetailsForId(id, null, validate);
                final String className = details.className;
                id = details.id;
                if (details.pc != null) {
                    pc3 = details.pc;
                    op2 = this.findObjectProvider(pc3);
                    if (performValidationWhenCached && validate && !api.isTransactional(pc3)) {
                        opsToValidate.add(op2);
                    }
                }
                else {
                    try {
                        final Class pcClass = this.clr.classForName(className, (id instanceof OID) ? null : id.getClass().getClassLoader());
                        if (Modifier.isAbstract(pcClass.getModifiers())) {
                            throw new NucleusObjectNotFoundException(ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), className));
                        }
                        op2 = this.newObjectProviderForHollow(pcClass, id);
                        pc3 = op2.getObject();
                        if (!validate) {
                            op2.markForInheritanceValidation();
                        }
                        this.putObjectIntoLevel1Cache(op2);
                    }
                    catch (ClassNotResolvedException e) {
                        NucleusLogger.PERSISTENCE.warn(ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)));
                        throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)), e);
                    }
                    if (validate) {
                        opsToValidate.add(op2);
                    }
                }
            }
            pcById.put(idOrig, pc3);
        }
        if (!opsToValidate.isEmpty()) {
            try {
                this.getStoreManager().getPersistenceHandler().locateObjects(opsToValidate.toArray(new ObjectProvider[opsToValidate.size()]));
            }
            catch (NucleusObjectNotFoundException nonfe) {
                final NucleusObjectNotFoundException[] nonfes = (NucleusObjectNotFoundException[])nonfe.getNestedExceptions();
                if (nonfes != null) {
                    for (int l = 0; l < nonfes.length; ++l) {
                        final Object missingId = nonfes[l].getFailedObject();
                        this.removeObjectFromLevel1Cache(missingId);
                    }
                }
                throw nonfe;
            }
        }
        final Object[] objs = new Object[ids.length];
        for (int m = 0; m < ids.length; ++m) {
            final Object id2 = ids[m];
            objs[m] = pcById.get(id2);
        }
        return objs;
    }
    
    private ClassDetailsForId getClassDetailsForId(Object id, final String objectClassName, final boolean checkInheritance) {
        final ApiAdapter api = this.getApiAdapter();
        String className = null;
        String originalClassName = null;
        boolean checkedClassName = false;
        if (id instanceof SCOID) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010006"));
        }
        if (id instanceof DatastoreUniqueOID) {
            throw new NucleusObjectNotFoundException(ExecutionContextImpl.LOCALISER.msg("010026"), id);
        }
        if (objectClassName != null) {
            originalClassName = objectClassName;
        }
        else if (api.isDatastoreIdentity(id) || api.isSingleFieldIdentity(id)) {
            originalClassName = this.getStoreManager().manageClassForIdentity(id, this.clr);
        }
        else {
            originalClassName = this.getStoreManager().getClassNameForObjectID(id, this.clr, this);
            checkedClassName = true;
        }
        Object pc = null;
        if (checkInheritance) {
            className = (checkedClassName ? originalClassName : this.getStoreManager().getClassNameForObjectID(id, this.clr, this));
            if (className == null) {
                throw new NucleusObjectNotFoundException(ExecutionContextImpl.LOCALISER.msg("010026"), id);
            }
            if (!checkedClassName && (api.isDatastoreIdentity(id) || api.isSingleFieldIdentity(id))) {
                final String[] subclasses = this.getMetaDataManager().getSubclassesForClass(className, true);
                if (subclasses != null) {
                    for (int i = 0; i < subclasses.length; ++i) {
                        Object oid = null;
                        if (api.isDatastoreIdentity(id)) {
                            oid = OIDFactory.getInstance(this.nucCtx, subclasses[i], ((OID)id).getKeyValue());
                        }
                        else if (api.isSingleFieldIdentity(id)) {
                            oid = api.getNewSingleFieldIdentity(id.getClass(), this.getClassLoaderResolver().classForName(subclasses[i]), api.getTargetKeyForSingleFieldIdentity(id));
                        }
                        pc = this.getObjectFromCache(oid);
                        if (pc != null) {
                            className = subclasses[i];
                            break;
                        }
                    }
                }
            }
            if (pc == null && originalClassName != null && !originalClassName.equals(className)) {
                if (api.isDatastoreIdentity(id)) {
                    id = OIDFactory.getInstance(this.getNucleusContext(), className, ((OID)id).getKeyValue());
                    pc = this.getObjectFromCache(id);
                }
                else if (api.isSingleFieldIdentity(id)) {
                    id = api.getNewSingleFieldIdentity(id.getClass(), this.clr.classForName(className), api.getTargetKeyForSingleFieldIdentity(id));
                    pc = this.getObjectFromCache(id);
                }
            }
        }
        else {
            className = originalClassName;
        }
        return new ClassDetailsForId(id, className, pc);
    }
    
    @Override
    public Object findObject(Object id, final boolean validate, final boolean checkInheritance, final String objectClassName) {
        if (id == null) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010044"));
        }
        final IdentityStringTranslator translator = this.getNucleusContext().getIdentityStringTranslator();
        if (translator != null && id instanceof String) {
            id = translator.getIdentity(this, (String)id);
        }
        final ApiAdapter api = this.getApiAdapter();
        boolean fromCache = false;
        Object pc = this.getObjectFromCache(id);
        ObjectProvider op = null;
        if (pc != null) {
            fromCache = true;
            if (id instanceof SCOID && api.isPersistent(pc) && !api.isNew(pc) && !api.isDeleted(pc) && !api.isTransactional(pc)) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010005"));
            }
            if (api.isTransactional(pc)) {
                return pc;
            }
            op = this.findObjectProvider(pc);
        }
        else {
            pc = this.getStoreManager().getPersistenceHandler().findObject(this, id);
            if (pc != null) {
                op = this.findObjectProvider(pc);
                this.putObjectIntoLevel1Cache(op);
                this.putObjectIntoLevel2Cache(op, false);
            }
            else {
                final ClassDetailsForId details = this.getClassDetailsForId(id, objectClassName, checkInheritance);
                final String className = details.className;
                id = details.id;
                if (details.pc != null) {
                    pc = details.pc;
                    op = this.findObjectProvider(pc);
                    fromCache = true;
                }
                else {
                    try {
                        final Class pcClass = this.clr.classForName(className, (id instanceof OID) ? null : id.getClass().getClassLoader());
                        if (Modifier.isAbstract(pcClass.getModifiers())) {
                            throw new NucleusObjectNotFoundException(ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), className));
                        }
                        op = this.newObjectProviderForHollow(pcClass, id);
                        pc = op.getObject();
                        if (!checkInheritance && !validate) {
                            op.markForInheritanceValidation();
                        }
                        this.putObjectIntoLevel1Cache(op);
                    }
                    catch (ClassNotResolvedException e) {
                        NucleusLogger.PERSISTENCE.warn(ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)));
                        throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010027", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)), e);
                    }
                }
            }
        }
        final boolean performValidationWhenCached = this.nucCtx.getPersistenceConfiguration().getBooleanProperty("datanucleus.findObject.validateWhenCached");
        if (validate && (!fromCache || performValidationWhenCached)) {
            if (op != null && !fromCache) {
                this.putObjectIntoLevel1Cache(op);
            }
            try {
                op.validate();
                if (op.getObject() != pc) {
                    fromCache = false;
                    pc = op.getObject();
                    this.putObjectIntoLevel1Cache(op);
                }
            }
            catch (NucleusObjectNotFoundException onfe) {
                this.removeObjectFromLevel1Cache(op.getInternalObjectId());
                throw onfe;
            }
        }
        if (!fromCache) {
            this.putObjectIntoLevel2Cache(op, false);
        }
        return pc;
    }
    
    @Override
    public Object newObjectId(final Class pcClass, Object key) {
        if (pcClass == null) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010028"));
        }
        this.assertClassPersistable(pcClass);
        final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(pcClass, this.clr);
        if (cmd == null) {
            throw new NoPersistenceInformationException(pcClass.getName());
        }
        if (!this.getStoreManager().managesClass(cmd.getFullClassName())) {
            this.getStoreManager().addClass(cmd.getFullClassName(), this.clr);
        }
        final IdentityKeyTranslator translator = this.getNucleusContext().getIdentityKeyTranslator();
        if (translator != null) {
            key = translator.getKey(this, pcClass, key);
        }
        Object id = null;
        if (cmd.usesSingleFieldIdentityClass()) {
            final Class idType = this.clr.classForName(cmd.getObjectidClass());
            id = this.getApiAdapter().getNewSingleFieldIdentity(idType, pcClass, key);
        }
        else {
            if (!(key instanceof String)) {
                throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010029", pcClass.getName(), key.getClass().getName()));
            }
            if (cmd.getIdentityType() == IdentityType.APPLICATION) {
                if (Modifier.isAbstract(pcClass.getModifiers()) && cmd.getObjectidClass() != null) {
                    try {
                        final Constructor c = this.clr.classForName(cmd.getObjectidClass()).getDeclaredConstructor(String.class);
                        id = c.newInstance((String)key);
                        return id;
                    }
                    catch (Exception e) {
                        final String msg = ExecutionContextImpl.LOCALISER.msg("010030", cmd.getObjectidClass(), cmd.getFullClassName());
                        NucleusLogger.PERSISTENCE.error(msg);
                        NucleusLogger.PERSISTENCE.error(e);
                        throw new NucleusUserException(msg);
                    }
                }
                this.clr.classForName(pcClass.getName(), true);
                id = this.getApiAdapter().getNewApplicationIdentityObjectId(pcClass, key);
            }
            else {
                id = OIDFactory.getInstance(this.getNucleusContext(), (String)key);
            }
        }
        return id;
    }
    
    @Override
    public Object newObjectId(final String className, final Object pc) {
        final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(className, this.clr);
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            final Object nextIdentifier = this.getStoreManager().getStrategyValue(this, cmd, -1);
            return OIDFactory.getInstance(this.getNucleusContext(), cmd.getFullClassName(), nextIdentifier);
        }
        if (cmd.getIdentityType() == IdentityType.APPLICATION) {
            return this.getApiAdapter().getNewApplicationIdentityObjectId(pc, cmd);
        }
        return new SCOID(className);
    }
    
    @Override
    public void clearDirty(final ObjectProvider op) {
        this.dirtyOPs.remove(op);
        this.indirectDirtyOPs.remove(op);
    }
    
    @Override
    public void clearDirty() {
        this.dirtyOPs.clear();
        this.indirectDirtyOPs.clear();
    }
    
    @Override
    public void markDirty(final ObjectProvider op, final boolean directUpdate) {
        if (this.tx.isCommitting() && !this.tx.isActive()) {
            throw new NucleusException("Cannot change objects when transaction is no longer active.");
        }
        final boolean isInDirty = this.dirtyOPs.contains(op);
        final boolean isInIndirectDirty = this.indirectDirtyOPs.contains(op);
        if (!this.isDelayDatastoreOperationsEnabled() && !isInDirty && !isInIndirectDirty && this.dirtyOPs.size() >= this.getNucleusContext().getPersistenceConfiguration().getIntProperty("datanucleus.datastoreTransactionFlushLimit")) {
            this.flushInternal(false);
        }
        if (directUpdate) {
            if (isInIndirectDirty) {
                this.indirectDirtyOPs.remove(op);
                this.dirtyOPs.add(op);
            }
            else if (!isInDirty) {
                this.dirtyOPs.add(op);
                if (this.l2CacheTxIds != null && this.nucCtx.isClassCacheable(op.getClassMetaData())) {
                    this.l2CacheTxIds.add(op.getInternalObjectId());
                }
            }
        }
        else if (!isInDirty && !isInIndirectDirty) {
            this.indirectDirtyOPs.add(op);
            if (this.l2CacheTxIds != null && this.nucCtx.isClassCacheable(op.getClassMetaData())) {
                this.l2CacheTxIds.add(op.getInternalObjectId());
            }
        }
    }
    
    @Override
    public boolean getManageRelations() {
        return this.properties.getBooleanProperty("datanucleus.manageRelationships");
    }
    
    public boolean getManageRelationsChecks() {
        return this.properties.getBooleanProperty("datanucleus.manageRelationshipsChecks");
    }
    
    @Override
    public RelationshipManager getRelationshipManager(final ObjectProvider op) {
        if (!this.getManageRelations()) {
            return null;
        }
        if (this.managedRelationDetails == null) {
            this.managedRelationDetails = new ConcurrentHashMap<ObjectProvider, RelationshipManager>();
        }
        RelationshipManager relMgr = this.managedRelationDetails.get(op);
        if (relMgr == null) {
            relMgr = new RelationshipManagerImpl(op);
            this.managedRelationDetails.put(op, relMgr);
        }
        return relMgr;
    }
    
    @Override
    public boolean isManagingRelations() {
        return this.runningManageRelations;
    }
    
    protected void performManagedRelationships() {
        if (this.getManageRelations() && this.managedRelationDetails != null && !this.managedRelationDetails.isEmpty()) {
            try {
                this.runningManageRelations = true;
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("013000"));
                }
                if (this.getManageRelationsChecks()) {
                    for (final ObjectProvider op : this.managedRelationDetails.keySet()) {
                        final LifeCycleState lc = op.getLifecycleState();
                        if (lc != null) {
                            if (lc.isDeleted()) {
                                continue;
                            }
                            final RelationshipManager relMgr = this.managedRelationDetails.get(op);
                            relMgr.checkConsistency();
                        }
                    }
                }
                for (final ObjectProvider op : this.managedRelationDetails.keySet()) {
                    final LifeCycleState lc = op.getLifecycleState();
                    if (lc != null) {
                        if (lc.isDeleted()) {
                            continue;
                        }
                        final RelationshipManager relMgr = this.managedRelationDetails.get(op);
                        relMgr.process();
                        relMgr.clearFields();
                    }
                }
                this.managedRelationDetails.clear();
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("013001"));
                }
            }
            finally {
                this.runningManageRelations = false;
            }
        }
    }
    
    @Override
    public List<ObjectProvider> getObjectsToBeFlushed() {
        final List<ObjectProvider> ops = new ArrayList<ObjectProvider>();
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            ops.addAll(this.dirtyOPs);
            ops.addAll(this.indirectDirtyOPs);
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
        return ops;
    }
    
    @Override
    public boolean isFlushing() {
        return this.flushing > 0;
    }
    
    @Override
    public void flush() {
        if (this.tx.isActive()) {
            this.performManagedRelationships();
            this.flushInternal(true);
            if (this.dirtyOPs.size() > 0 || this.indirectDirtyOPs.size() > 0) {
                NucleusLogger.GENERAL.info("Flush pass 1 resulted in " + (this.dirtyOPs.size() + this.indirectDirtyOPs.size()) + " additional objects being made dirty. Performing flush pass 2");
                this.flushInternal(true);
            }
        }
    }
    
    @Override
    public void flushInternal(final boolean flushToDatastore) {
        if (!flushToDatastore && this.dirtyOPs.size() == 0 && this.indirectDirtyOPs.size() == 0) {
            return;
        }
        if (!this.tx.isActive()) {
            if (this.nontxProcessedOPs == null) {
                this.nontxProcessedOPs = new HashSet<ObjectProvider>();
            }
            this.nontxProcessedOPs.addAll(this.dirtyOPs);
            this.nontxProcessedOPs.addAll(this.indirectDirtyOPs);
        }
        ++this.flushing;
        try {
            if (flushToDatastore) {
                this.tx.preFlush();
            }
            final FlushProcess flusher = this.getStoreManager().getFlushProcess();
            final List<NucleusOptimisticException> optimisticFailures = flusher.execute(this, this.dirtyOPs, this.indirectDirtyOPs, this.operationQueue);
            if (flushToDatastore) {
                this.tx.flush();
            }
            if (optimisticFailures != null) {
                throw new NucleusOptimisticException(ExecutionContextImpl.LOCALISER.msg("010031"), optimisticFailures.toArray(new Throwable[optimisticFailures.size()]));
            }
        }
        finally {
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010004"));
            }
            --this.flushing;
        }
    }
    
    @Override
    public OperationQueue getOperationQueue() {
        return this.operationQueue;
    }
    
    @Override
    public void addOperationToQueue(final Operation oper) {
        if (this.operationQueue == null) {
            this.operationQueue = new OperationQueue();
        }
        this.operationQueue.enqueue(oper);
    }
    
    @Override
    public void flushOperationsForBackingStore(final Store backingStore, final ObjectProvider op) {
        if (this.operationQueue != null) {
            this.operationQueue.performAll(backingStore, op);
        }
    }
    
    public void postBegin() {
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            ObjectProvider[] ops = this.dirtyOPs.toArray(new ObjectProvider[this.dirtyOPs.size()]);
            for (int i = 0; i < ops.length; ++i) {
                ops[i].preBegin(this.tx);
            }
            ops = this.indirectDirtyOPs.toArray(new ObjectProvider[this.indirectDirtyOPs.size()]);
            for (int i = 0; i < ops.length; ++i) {
                ops[i].preBegin(this.tx);
            }
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
    }
    
    public void preCommit() {
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            this.flush();
            if (this.getReachabilityAtCommit()) {
                try {
                    this.runningPBRAtCommit = true;
                    this.performReachabilityAtCommit();
                }
                catch (Throwable t) {
                    NucleusLogger.PERSISTENCE.error(t);
                    if (t instanceof NucleusException) {
                        throw (NucleusException)t;
                    }
                    throw new NucleusException("Unexpected error during precommit", t);
                }
                finally {
                    this.runningPBRAtCommit = false;
                }
            }
            if (this.l2CacheEnabled) {
                this.performLevel2CacheUpdateAtCommit();
            }
            if (this.getDetachAllOnCommit()) {
                this.performDetachAllOnTxnEndPreparation();
            }
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
    }
    
    @Override
    public boolean isObjectModifiedInTransaction(final Object id) {
        return this.l2CacheTxIds != null && this.l2CacheTxIds.contains(id);
    }
    
    @Override
    public void markFieldsForUpdateInLevel2Cache(final Object id, final boolean[] fields) {
        if (this.l2CacheTxFieldsToUpdateById == null) {
            return;
        }
        BitSet bits = this.l2CacheTxFieldsToUpdateById.get(id);
        if (bits == null) {
            bits = new BitSet();
            this.l2CacheTxFieldsToUpdateById.put(id, bits);
        }
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i]) {
                bits.set(i);
            }
        }
    }
    
    private void performLevel2CacheUpdateAtCommit() {
        if (this.l2CacheTxIds == null) {
            return;
        }
        final String cacheStoreMode = this.getLevel2CacheStoreMode();
        if (cacheStoreMode.equalsIgnoreCase("bypass")) {
            return;
        }
        Set<ObjectProvider> opsToCache = null;
        Set<Object> idsToRemove = null;
        for (final Object id : this.l2CacheTxIds) {
            final ObjectProvider op = this.enlistedOPCache.get(id);
            if (op == null) {
                if (NucleusLogger.CACHE.isDebugEnabled() && this.nucCtx.getLevel2Cache().containsOid(id)) {
                    NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004014", id));
                }
                if (idsToRemove == null) {
                    idsToRemove = new HashSet<Object>();
                }
                idsToRemove.add(id);
            }
            else {
                final Object obj = op.getObject();
                final Object objID = this.getApiAdapter().getIdForObject(obj);
                if (objID == null) {
                    continue;
                }
                if (this.getApiAdapter().isDeleted(obj)) {
                    if (NucleusLogger.CACHE.isDebugEnabled()) {
                        NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004007", StringUtils.toJVMIDString(obj), op.getInternalObjectId()));
                    }
                    if (idsToRemove == null) {
                        idsToRemove = new HashSet<Object>();
                    }
                    idsToRemove.add(objID);
                }
                else {
                    if (this.getApiAdapter().isDetached(obj)) {
                        continue;
                    }
                    if (opsToCache == null) {
                        opsToCache = new HashSet<ObjectProvider>();
                    }
                    opsToCache.add(op);
                }
            }
        }
        if (idsToRemove != null && !idsToRemove.isEmpty()) {
            this.nucCtx.getLevel2Cache().evictAll(idsToRemove);
        }
        if (opsToCache != null && !opsToCache.isEmpty()) {
            this.putObjectsIntoLevel2Cache(opsToCache);
        }
        this.l2CacheTxIds.clear();
        this.l2CacheTxFieldsToUpdateById.clear();
    }
    
    private void performReachabilityAtCommit() {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010032"));
        }
        if (this.reachabilityPersistedIds.size() > 0 && this.reachabilityFlushedNewIds.size() > 0) {
            final Set currentReachables = new HashSet();
            final Object[] ids = this.reachabilityPersistedIds.toArray();
            final Set objectNotFound = new HashSet();
            for (int i = 0; i < ids.length; ++i) {
                if (!this.reachabilityDeletedIds.contains(ids[i])) {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug("Performing reachability algorithm on object with id \"" + ids[i] + "\"");
                    }
                    try {
                        final ObjectProvider op = this.findObjectProvider(this.findObject(ids[i], true, true, null));
                        op.runReachability(currentReachables);
                    }
                    catch (NucleusObjectNotFoundException ex) {
                        objectNotFound.add(ids[i]);
                    }
                }
            }
            this.reachabilityFlushedNewIds.removeAll(currentReachables);
            final Object[] nonReachableIds = this.reachabilityFlushedNewIds.toArray();
            if (nonReachableIds != null && nonReachableIds.length > 0) {
                for (int j = 0; j < nonReachableIds.length; ++j) {
                    if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010033", nonReachableIds[j]));
                    }
                    try {
                        if (!objectNotFound.contains(nonReachableIds[j])) {
                            final ObjectProvider op2 = this.findObjectProvider(this.findObject(nonReachableIds[j], true, true, null));
                            op2.nullifyFields();
                        }
                    }
                    catch (NucleusObjectNotFoundException ex2) {}
                }
                for (int j = 0; j < nonReachableIds.length; ++j) {
                    try {
                        if (!objectNotFound.contains(nonReachableIds[j])) {
                            final ObjectProvider op2 = this.findObjectProvider(this.findObject(nonReachableIds[j], true, true, null));
                            op2.deletePersistent();
                        }
                    }
                    catch (NucleusObjectNotFoundException ex3) {}
                }
            }
            this.flushInternal(true);
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010034"));
        }
    }
    
    private void performDetachAllOnTxnEndPreparation() {
        final Collection<ObjectProvider> ops = new ArrayList<ObjectProvider>();
        final Collection roots = this.fetchPlan.getDetachmentRoots();
        final Class[] rootClasses = this.fetchPlan.getDetachmentRootClasses();
        if (roots != null && roots.size() > 0) {
            for (final Object obj : roots) {
                ops.add(this.findObjectProvider(obj));
            }
        }
        else if (rootClasses != null && rootClasses.length > 0) {
            final ObjectProvider[] txOPs = this.enlistedOPCache.values().toArray(new ObjectProvider[this.enlistedOPCache.size()]);
            for (int i = 0; i < txOPs.length; ++i) {
                for (int j = 0; j < rootClasses.length; ++j) {
                    if (txOPs[i].getObject().getClass() == rootClasses[j]) {
                        ops.add(txOPs[i]);
                        break;
                    }
                }
            }
        }
        else if (this.cache != null) {
            ops.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
        }
        final Iterator<ObjectProvider> opsIter = ops.iterator();
        while (opsIter.hasNext()) {
            final ObjectProvider op = opsIter.next();
            final Object pc = op.getObject();
            if (pc != null && !this.getApiAdapter().isDetached(pc) && !this.getApiAdapter().isDeleted(pc)) {
                final FetchPlanState state = new FetchPlanState();
                try {
                    op.loadFieldsInFetchPlan(state);
                }
                catch (NucleusObjectNotFoundException onfe) {
                    NucleusLogger.PERSISTENCE.warn(ExecutionContextImpl.LOCALISER.msg("010013", StringUtils.toJVMIDString(pc), op.getInternalObjectId()));
                    opsIter.remove();
                }
            }
        }
        this.detachAllOnTxnEndOPs = ops.toArray(new ObjectProvider[ops.size()]);
    }
    
    private void performDetachAllOnTxnEnd() {
        try {
            this.runningDetachAllOnTxnEnd = true;
            if (this.detachAllOnTxnEndOPs != null) {
                final ObjectProvider[] opsToDetach = this.detachAllOnTxnEndOPs;
                final DetachState state = new DetachState(this.getApiAdapter());
                for (int i = 0; i < opsToDetach.length; ++i) {
                    final Object pc = opsToDetach[i].getObject();
                    if (pc != null) {
                        opsToDetach[i].detach(state);
                    }
                }
            }
        }
        finally {
            this.detachAllOnTxnEndOPs = null;
            this.runningDetachAllOnTxnEnd = false;
        }
    }
    
    @Override
    public boolean isRunningDetachAllOnCommit() {
        return this.runningDetachAllOnTxnEnd;
    }
    
    private void performDetachOnClose() {
        if (this.cache != null && this.cache.size() > 0) {
            NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010011"));
            final List<ObjectProvider> toDetach = new ArrayList<ObjectProvider>();
            toDetach.addAll(((Map<K, ? extends ObjectProvider>)this.cache).values());
            if (this.tx.getNontransactionalRead()) {
                this.performDetachOnCloseWork(toDetach);
            }
            else {
                try {
                    this.tx.begin();
                    this.performDetachOnCloseWork(toDetach);
                    this.tx.commit();
                }
                finally {
                    if (this.tx.isActive()) {
                        this.tx.rollback();
                    }
                }
            }
            NucleusLogger.PERSISTENCE.debug(ExecutionContextImpl.LOCALISER.msg("010012"));
        }
    }
    
    private void performDetachOnCloseWork(final List<ObjectProvider> smsToDetach) {
        for (final ObjectProvider op : smsToDetach) {
            if (op != null && op.getObject() != null && !op.getExecutionContext().getApiAdapter().isDeleted(op.getObject()) && op.getExternalObjectId() != null) {
                try {
                    op.detach(new DetachState(this.getApiAdapter()));
                }
                catch (NucleusObjectNotFoundException ex) {}
            }
        }
    }
    
    public void postCommit() {
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            if (this.getDetachAllOnCommit()) {
                this.performDetachAllOnTxnEnd();
            }
            List failures = null;
            try {
                final ApiAdapter api = this.getApiAdapter();
                final ObjectProvider[] ops = this.enlistedOPCache.values().toArray(new ObjectProvider[this.enlistedOPCache.size()]);
                for (int i = 0; i < ops.length; ++i) {
                    try {
                        if (ops[i] != null && ops[i].getObject() != null && (api.isPersistent(ops[i].getObject()) || api.isTransactional(ops[i].getObject()))) {
                            ops[i].postCommit(this.getTransaction());
                            if (this.getDetachAllOnCommit() && api.isDetachable(ops[i].getObject())) {
                                this.removeObjectProvider(ops[i]);
                            }
                        }
                    }
                    catch (RuntimeException e) {
                        if (failures == null) {
                            failures = new ArrayList();
                        }
                        failures.add(e);
                    }
                }
            }
            finally {
                this.resetTransactionalVariables();
            }
            if (failures != null && !failures.isEmpty()) {
                throw new CommitStateTransitionException(failures.toArray(new Exception[failures.size()]));
            }
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
    }
    
    public void preRollback() {
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            ArrayList failures = null;
            try {
                final Collection<ObjectProvider> ops = this.enlistedOPCache.values();
                for (final ObjectProvider op : ops) {
                    try {
                        op.preRollback(this.getTransaction());
                    }
                    catch (RuntimeException e) {
                        if (failures == null) {
                            failures = new ArrayList();
                        }
                        failures.add(e);
                    }
                }
                this.clearDirty();
            }
            finally {
                this.resetTransactionalVariables();
            }
            if (failures != null && !failures.isEmpty()) {
                throw new RollbackStateTransitionException(failures.toArray(new Exception[failures.size()]));
            }
            if (this.getDetachAllOnRollback()) {
                this.performDetachAllOnTxnEndPreparation();
            }
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
    }
    
    public void postRollback() {
        try {
            if (this.getMultithreaded()) {
                this.lock.lock();
            }
            if (this.getDetachAllOnRollback()) {
                this.performDetachAllOnTxnEnd();
            }
        }
        finally {
            if (this.getMultithreaded()) {
                this.lock.unlock();
            }
        }
    }
    
    private void resetTransactionalVariables() {
        if (this.getReachabilityAtCommit()) {
            this.reachabilityEnlistedIds.clear();
            this.reachabilityPersistedIds.clear();
            this.reachabilityDeletedIds.clear();
            this.reachabilityFlushedNewIds.clear();
        }
        this.enlistedOPCache.clear();
        this.dirtyOPs.clear();
        this.indirectDirtyOPs.clear();
        this.fetchPlan.resetDetachmentRoots();
        if (this.getManageRelations() && this.managedRelationDetails != null) {
            this.managedRelationDetails.clear();
        }
        if (this.l2CacheTxIds != null) {
            this.l2CacheTxIds.clear();
        }
        if (this.l2CacheTxFieldsToUpdateById != null) {
            this.l2CacheTxFieldsToUpdateById.clear();
        }
        if (this.operationQueue != null) {
            if (!this.operationQueue.getOperations().isEmpty()) {
                NucleusLogger.PERSISTENCE.warn("Queue of operations for flushing is not empty! Ignoring unprocessed operations. Generate a testcase and report this. See the log for full details of unflushed ops");
                this.operationQueue.log();
            }
            this.operationQueue.clear();
        }
        this.opAttachDetachObjectReferenceMap = null;
    }
    
    protected String getLevel2CacheRetrieveMode() {
        String setting = (String)this.getProperty("datanucleus.cache.level2.retrieveMode");
        if (setting == null) {
            setting = this.nucCtx.getPersistenceConfiguration().getStringProperty("datanucleus.cache.level2.retrieveMode");
        }
        return setting;
    }
    
    protected String getLevel2CacheStoreMode() {
        String setting = (String)this.getProperty("datanucleus.cache.level2.storeMode");
        if (setting == null) {
            setting = this.nucCtx.getPersistenceConfiguration().getStringProperty("datanucleus.cache.level2.storeMode");
        }
        return setting;
    }
    
    @Override
    public void putObjectIntoLevel1Cache(final ObjectProvider op) {
        if (this.cache != null) {
            final Object id = op.getInternalObjectId();
            if (id == null || op.getObject() == null) {
                NucleusLogger.CACHE.warn(ExecutionContextImpl.LOCALISER.msg("003006"));
                return;
            }
            final Object oldOP = this.cache.put(id, op);
            if (NucleusLogger.CACHE.isDebugEnabled() && oldOP == null) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003004", StringUtils.toJVMIDString(op.getObject()), IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), StringUtils.booleanArrayToString(op.getLoadedFields())));
            }
        }
    }
    
    protected void putObjectIntoLevel2Cache(final ObjectProvider op, final boolean updateIfPresent) {
        if (op.getInternalObjectId() == null || !this.nucCtx.isClassCacheable(op.getClassMetaData())) {
            return;
        }
        final String storeMode = this.getLevel2CacheStoreMode();
        if (storeMode.equalsIgnoreCase("bypass")) {
            return;
        }
        if (this.l2CacheTxIds != null && !this.l2CacheTxIds.contains(op.getInternalObjectId())) {
            this.putObjectIntoLevel2CacheInternal(op, updateIfPresent);
        }
    }
    
    protected CachedPC getL2CacheableObject(final ObjectProvider op, final CachedPC currentCachedPC) {
        CachedPC cachedPC = null;
        int[] fieldsToUpdate = null;
        if (currentCachedPC != null) {
            cachedPC = currentCachedPC.getCopy();
            cachedPC.setVersion(op.getTransactionalVersion());
            final BitSet fieldsToUpdateBitSet = this.l2CacheTxFieldsToUpdateById.get(op.getInternalObjectId());
            if (fieldsToUpdateBitSet != null) {
                int num = 0;
                for (int i = 0; i < fieldsToUpdateBitSet.length(); ++i) {
                    if (fieldsToUpdateBitSet.get(i)) {
                        ++num;
                    }
                }
                fieldsToUpdate = new int[num];
                int j = 0;
                for (int k = 0; k < fieldsToUpdateBitSet.length(); ++k) {
                    if (fieldsToUpdateBitSet.get(k)) {
                        fieldsToUpdate[j++] = k;
                    }
                }
            }
            if (fieldsToUpdate == null || fieldsToUpdate.length == 0) {
                return null;
            }
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                final int[] loadedFieldNums = cachedPC.getLoadedFieldNumbers();
                final String fieldNames = (loadedFieldNums == null || loadedFieldNums.length == 0) ? "" : StringUtils.intArrayToString(loadedFieldNums);
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004015", StringUtils.toJVMIDString(op.getObject()), op.getInternalObjectId(), fieldNames, cachedPC.getVersion(), StringUtils.intArrayToString(fieldsToUpdate)));
            }
        }
        else {
            final int[] loadedFieldNumbers = op.getLoadedFieldNumbers();
            if (loadedFieldNumbers == null || loadedFieldNumbers.length == 0) {
                return null;
            }
            cachedPC = new CachedPC(op.getObject().getClass(), op.getLoadedFields(), op.getTransactionalVersion());
            fieldsToUpdate = loadedFieldNumbers;
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                final int[] loadedFieldNums = cachedPC.getLoadedFieldNumbers();
                final String fieldNames = (loadedFieldNums == null || loadedFieldNums.length == 0) ? "" : StringUtils.intArrayToString(loadedFieldNums);
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004003", StringUtils.toJVMIDString(op.getObject()), op.getInternalObjectId(), fieldNames, cachedPC.getVersion()));
            }
        }
        op.provideFields(fieldsToUpdate, new L2CachePopulateFieldManager(op, cachedPC));
        return cachedPC;
    }
    
    protected void putObjectsIntoLevel2Cache(final Set<ObjectProvider> ops) {
        final int batchSize = this.nucCtx.getPersistenceConfiguration().getIntProperty("datanucleus.cache.level2.batchSize");
        final Level2Cache l2Cache = this.nucCtx.getLevel2Cache();
        final Map<Object, CachedPC> dataToUpdate = new HashMap<Object, CachedPC>();
        for (final ObjectProvider op : ops) {
            final Object id = op.getInternalObjectId();
            final CachedPC currentCachedPC = l2Cache.get(id);
            final CachedPC cachedPC = this.getL2CacheableObject(op, currentCachedPC);
            if (cachedPC != null && id != null && !(id instanceof IdentityReference)) {
                dataToUpdate.put(id, cachedPC);
                if (dataToUpdate.size() != batchSize) {
                    continue;
                }
                l2Cache.putAll(dataToUpdate);
                dataToUpdate.clear();
            }
        }
        if (!dataToUpdate.isEmpty()) {
            l2Cache.putAll(dataToUpdate);
            dataToUpdate.clear();
        }
    }
    
    protected void putObjectIntoLevel2CacheInternal(final ObjectProvider op, final boolean updateIfPresent) {
        final Object id = op.getInternalObjectId();
        if (id == null || id instanceof IdentityReference) {
            return;
        }
        final Level2Cache l2Cache = this.nucCtx.getLevel2Cache();
        if (!updateIfPresent && l2Cache.containsOid(id)) {
            return;
        }
        final CachedPC currentCachedPC = l2Cache.get(id);
        final CachedPC cachedPC = this.getL2CacheableObject(op, currentCachedPC);
        if (cachedPC != null) {
            l2Cache.put(id, cachedPC);
        }
    }
    
    @Override
    public void removeObjectFromLevel1Cache(final Object id) {
        if (id != null && this.cache != null) {
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003009", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), String.valueOf(this.cache.size())));
            }
            final Object pcRemoved = ((Map<K, Object>)this.cache).remove(id);
            if (pcRemoved == null && NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003010", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)));
            }
        }
    }
    
    @Override
    public void removeObjectFromLevel2Cache(final Object id) {
        if (id != null) {
            final Level2Cache l2Cache = this.nucCtx.getLevel2Cache();
            if (l2Cache.containsOid(id)) {
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004016", id));
                }
                l2Cache.evict(id);
            }
        }
    }
    
    @Override
    public boolean hasIdentityInCache(final Object id) {
        if (this.cache != null && this.cache.containsKey(id)) {
            return true;
        }
        if (this.l2CacheEnabled) {
            final Level2Cache l2Cache = this.nucCtx.getLevel2Cache();
            if (l2Cache.containsOid(id)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object getObjectFromCache(final Object id) {
        final Object pc = this.getObjectFromLevel1Cache(id);
        if (pc != null) {
            return pc;
        }
        return this.getObjectFromLevel2Cache(id);
    }
    
    @Override
    public Object[] getObjectsFromCache(final Object[] ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        final Object[] objs = new Object[ids.length];
        final Collection idsNotFound = new HashSet();
        for (int i = 0; i < ids.length; ++i) {
            objs[i] = this.getObjectFromLevel1Cache(ids[i]);
            if (objs[i] == null) {
                idsNotFound.add(ids[i]);
            }
        }
        if (idsNotFound.size() > 0) {
            final Map l2ObjsById = this.getObjectsFromLevel2Cache(idsNotFound);
            for (int j = 0; j < ids.length; ++j) {
                if (objs[j] == null) {
                    objs[j] = l2ObjsById.get(ids[j]);
                }
            }
        }
        return objs;
    }
    
    public Object getObjectFromLevel1Cache(final Object id) {
        Object pc = null;
        ObjectProvider op = null;
        if (this.cache != null) {
            op = ((Map<K, ObjectProvider>)this.cache).get(id);
            if (op != null) {
                pc = op.getObject();
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003008", StringUtils.toJVMIDString(pc), IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), StringUtils.booleanArrayToString(op.getLoadedFields()), "" + this.cache.size()));
                }
                op.resetDetachState();
                return pc;
            }
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003007", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), "" + this.cache.size()));
            }
        }
        return null;
    }
    
    protected Object getObjectFromLevel2Cache(final Object id) {
        Object pc = null;
        if (this.l2CacheEnabled) {
            if (!this.nucCtx.isClassWithIdentityCacheable(id)) {
                return null;
            }
            final String cacheRetrieveMode = this.getLevel2CacheRetrieveMode();
            if (cacheRetrieveMode.equalsIgnoreCase("bypass")) {
                return null;
            }
            final Level2Cache l2Cache = this.nucCtx.getLevel2Cache();
            final CachedPC cachedPC = l2Cache.get(id);
            if (cachedPC != null) {
                final ObjectProvider op = this.newObjectProviderForCachedPC(id, cachedPC);
                pc = op.getObject();
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004006", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), StringUtils.intArrayToString(cachedPC.getLoadedFieldNumbers()), cachedPC.getVersion(), StringUtils.toJVMIDString(pc)));
                }
                if (this.tx.isActive() && this.tx.getOptimistic()) {
                    op.makeNontransactional();
                }
                else if (!this.tx.isActive() && this.getApiAdapter().isTransactional(pc)) {
                    op.makeNontransactional();
                }
                return pc;
            }
            if (NucleusLogger.CACHE.isDebugEnabled()) {
                NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004005", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)));
            }
        }
        return null;
    }
    
    protected Map getObjectsFromLevel2Cache(final Collection ids) {
        if (this.l2CacheEnabled) {
            final Level2Cache l2Cache = this.nucCtx.getLevel2Cache();
            final Map<Object, CachedPC> cachedPCs = l2Cache.getAll(ids);
            final Map pcsById = new HashMap(cachedPCs.size());
            for (final Map.Entry<Object, CachedPC> entry : cachedPCs.entrySet()) {
                final Object id = entry.getKey();
                final CachedPC cachedPC = entry.getValue();
                if (cachedPC != null) {
                    final ObjectProvider op = this.newObjectProviderForCachedPC(id, cachedPC);
                    final Object pc = op.getObject();
                    if (NucleusLogger.CACHE.isDebugEnabled()) {
                        NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004006", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id), StringUtils.intArrayToString(cachedPC.getLoadedFieldNumbers()), cachedPC.getVersion(), StringUtils.toJVMIDString(pc)));
                    }
                    if (this.tx.isActive() && this.tx.getOptimistic()) {
                        op.makeNontransactional();
                    }
                    else if (!this.tx.isActive() && this.getApiAdapter().isTransactional(pc)) {
                        op.makeNontransactional();
                    }
                    pcsById.put(id, pc);
                }
                else {
                    if (!NucleusLogger.CACHE.isDebugEnabled()) {
                        continue;
                    }
                    NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("004005", IdentityUtils.getIdentityAsString(this.getApiAdapter(), id)));
                }
            }
            return pcsById;
        }
        return null;
    }
    
    @Override
    public void replaceObjectId(final Object pc, final Object oldID, final Object newID) {
        if (pc == null || this.getApiAdapter().getIdForObject(pc) == null) {
            NucleusLogger.CACHE.warn(ExecutionContextImpl.LOCALISER.msg("003006"));
            return;
        }
        final ObjectProvider op = this.findObjectProvider(pc);
        if (this.cache != null) {
            final Object o = ((Map<K, Object>)this.cache).get(oldID);
            if (o != null) {
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(ExecutionContextImpl.LOCALISER.msg("003012", StringUtils.toJVMIDString(pc), IdentityUtils.getIdentityAsString(this.getApiAdapter(), oldID), IdentityUtils.getIdentityAsString(this.getApiAdapter(), newID)));
                }
                this.cache.remove(oldID);
            }
            if (op != null) {
                this.putObjectIntoLevel1Cache(op);
            }
        }
        if (this.enlistedOPCache.get(oldID) != null && op != null) {
            this.enlistedOPCache.remove(oldID);
            this.enlistedOPCache.put(newID, op);
            if (NucleusLogger.TRANSACTION.isDebugEnabled()) {
                NucleusLogger.TRANSACTION.debug(ExecutionContextImpl.LOCALISER.msg("015018", StringUtils.toJVMIDString(pc), IdentityUtils.getIdentityAsString(this.getApiAdapter(), oldID), IdentityUtils.getIdentityAsString(this.getApiAdapter(), newID)));
            }
        }
        if (this.l2CacheTxIds != null && this.l2CacheTxIds.contains(oldID)) {
            this.l2CacheTxIds.remove(oldID);
            this.l2CacheTxIds.add(newID);
        }
        if (this.getReachabilityAtCommit() && this.tx.isActive()) {
            if (this.reachabilityEnlistedIds.remove(oldID)) {
                this.reachabilityEnlistedIds.add(newID);
            }
            if (this.reachabilityFlushedNewIds.remove(oldID)) {
                this.reachabilityFlushedNewIds.add(newID);
            }
            if (this.reachabilityPersistedIds.remove(oldID)) {
                this.reachabilityPersistedIds.add(newID);
            }
            if (this.reachabilityDeletedIds.remove(oldID)) {
                this.reachabilityDeletedIds.add(newID);
            }
        }
    }
    
    @Override
    public boolean getSerializeReadForClass(final String className) {
        if (this.tx.isActive() && this.tx.getSerializeRead() != null) {
            return this.tx.getSerializeRead();
        }
        if (this.getProperty("datanucleus.SerializeRead") != null) {
            return this.properties.getBooleanProperty("datanucleus.SerializeRead");
        }
        if (className != null) {
            final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(className, this.clr);
            if (cmd != null) {
                return cmd.isSerializeRead();
            }
        }
        return false;
    }
    
    @Override
    public Extent getExtent(final Class pcClass, final boolean subclasses) {
        try {
            this.clr.setPrimary(pcClass.getClassLoader());
            this.assertClassPersistable(pcClass);
            return this.getStoreManager().getExtent(this, pcClass, subclasses);
        }
        finally {
            this.clr.unsetPrimary();
        }
    }
    
    @Override
    public Query newQuery() {
        final Query q = this.getStoreManager().getQueryManager().newQuery("JDOQL", this, null);
        if (this.ecListeners == null) {
            this.ecListeners = new HashSet<ExecutionContextListener>();
        }
        this.ecListeners.add(q);
        return q;
    }
    
    public void removeAllInstanceLifecycleListeners() {
        if (this.callbacks != null) {
            this.callbacks.close();
        }
    }
    
    @Override
    public CallbackHandler getCallbackHandler() {
        if (this.callbacks != null) {
            return this.callbacks;
        }
        if (!this.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.allowCallbacks")) {
            return this.callbacks = new NullCallbackHandler();
        }
        final String callbackHandlerClassName = this.getNucleusContext().getPluginManager().getAttributeValueForExtension("org.datanucleus.callbackhandler", "name", this.getNucleusContext().getApiName(), "class-name");
        if (callbackHandlerClassName != null) {
            try {
                return this.callbacks = (CallbackHandler)this.getNucleusContext().getPluginManager().createExecutableExtension("org.datanucleus.callbackhandler", "name", this.getNucleusContext().getApiName(), "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this.getNucleusContext() });
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.error(ExecutionContextImpl.LOCALISER.msg("025000", callbackHandlerClassName, e));
            }
        }
        return null;
    }
    
    @Override
    public void addListener(final Object listener, final Class[] classes) {
        if (listener == null) {
            return;
        }
        this.getCallbackHandler().addListener(listener, classes);
    }
    
    @Override
    public void removeListener(final Object listener) {
        if (listener != null) {
            this.getCallbackHandler().removeListener(listener);
        }
    }
    
    @Override
    public void disconnectLifecycleListener() {
        if (this.callbacks != null) {
            this.callbacks.close();
        }
    }
    
    protected void assertIsOpen() {
        if (this.isClosed()) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010002")).setFatal();
        }
    }
    
    @Override
    public void assertClassPersistable(final Class cls) {
        if (cls != null && !this.getNucleusContext().getApiAdapter().isPersistable(cls) && !cls.isInterface()) {
            throw new ClassNotPersistableException(cls.getName());
        }
        if (!this.hasPersistenceInformationForClass(cls)) {
            throw new NoPersistenceInformationException(cls.getName());
        }
    }
    
    protected void assertDetachable(final Object object) {
        if (object != null && !this.getApiAdapter().isDetachable(object)) {
            throw new ClassNotDetachableException(object.getClass().getName());
        }
    }
    
    protected void assertNotDetached(final Object object) {
        if (object != null && this.getApiAdapter().isDetached(object)) {
            throw new ObjectDetachedException(object.getClass().getName());
        }
    }
    
    protected void assertActiveTransaction() {
        if (!this.tx.isActive()) {
            throw new TransactionNotActiveException();
        }
    }
    
    protected void assertHasImplementationCreator() {
        if (this.getNucleusContext().getImplementationCreator() == null) {
            throw new NucleusUserException(ExecutionContextImpl.LOCALISER.msg("010035"));
        }
    }
    
    @Override
    public boolean hasPersistenceInformationForClass(final Class cls) {
        if (cls == null) {
            return false;
        }
        if (this.getMetaDataManager().getMetaDataForClass(cls, this.clr) != null) {
            return true;
        }
        if (cls.isInterface()) {
            try {
                this.newInstance(cls);
            }
            catch (RuntimeException ex) {
                NucleusLogger.PERSISTENCE.warn(ex);
            }
            return this.getMetaDataManager().getMetaDataForClass(cls, this.clr) != null;
        }
        return false;
    }
    
    protected FetchGroupManager getFetchGroupManager() {
        if (this.fetchGrpMgr == null) {
            this.fetchGrpMgr = new FetchGroupManager(this.getNucleusContext());
        }
        return this.fetchGrpMgr;
    }
    
    @Override
    public void addInternalFetchGroup(final FetchGroup grp) {
        this.getFetchGroupManager().addFetchGroup(grp);
    }
    
    protected void removeInternalFetchGroup(final FetchGroup grp) {
        if (this.fetchGrpMgr == null) {
            return;
        }
        this.getFetchGroupManager().removeFetchGroup(grp);
    }
    
    @Override
    public FetchGroup getInternalFetchGroup(final Class cls, final String name) {
        if (!cls.isInterface() && !this.getNucleusContext().getApiAdapter().isPersistable(cls)) {
            throw new NucleusUserException("Cannot create FetchGroup for " + cls + " since it is not persistable");
        }
        if (cls.isInterface() && !this.getNucleusContext().getMetaDataManager().isPersistentInterface(cls.getName())) {
            throw new NucleusUserException("Cannot create FetchGroup for " + cls + " since it is not persistable");
        }
        if (this.fetchGrpMgr == null) {
            return null;
        }
        return this.getFetchGroupManager().getFetchGroup(cls, name);
    }
    
    @Override
    public Set getFetchGroupsWithName(final String name) {
        if (this.fetchGrpMgr == null) {
            return null;
        }
        return this.getFetchGroupManager().getFetchGroupsWithName(name);
    }
    
    @Override
    public Lock getLock() {
        return this.lock;
    }
    
    @Override
    public EmbeddedOwnerRelation registerEmbeddedRelation(final ObjectProvider ownerOP, final int ownerFieldNum, final ObjectProvider embOP) {
        final EmbeddedOwnerRelation relation = new EmbeddedOwnerRelation(ownerOP, ownerFieldNum, embOP);
        if (this.opEmbeddedInfoByEmbedded == null) {
            this.opEmbeddedInfoByEmbedded = new HashMap<ObjectProvider, List<EmbeddedOwnerRelation>>();
        }
        List<EmbeddedOwnerRelation> relations = this.opEmbeddedInfoByEmbedded.get(embOP);
        if (relations == null) {
            relations = new ArrayList<EmbeddedOwnerRelation>();
        }
        relations.add(relation);
        this.opEmbeddedInfoByEmbedded.put(embOP, relations);
        if (this.opEmbeddedInfoByOwner == null) {
            this.opEmbeddedInfoByOwner = new HashMap<ObjectProvider, List<EmbeddedOwnerRelation>>();
        }
        relations = this.opEmbeddedInfoByOwner.get(ownerOP);
        if (relations == null) {
            relations = new ArrayList<EmbeddedOwnerRelation>();
        }
        relations.add(relation);
        this.opEmbeddedInfoByOwner.put(ownerOP, relations);
        return relation;
    }
    
    @Override
    public void deregisterEmbeddedRelation(final EmbeddedOwnerRelation rel) {
        if (this.opEmbeddedInfoByEmbedded != null) {
            final List<EmbeddedOwnerRelation> ownerRels = this.opEmbeddedInfoByEmbedded.get(rel.getEmbeddedOP());
            ownerRels.remove(rel);
            if (ownerRels.size() == 0) {
                this.opEmbeddedInfoByEmbedded.remove(rel.getEmbeddedOP());
                if (this.opEmbeddedInfoByEmbedded.size() == 0) {
                    this.opEmbeddedInfoByEmbedded = null;
                }
            }
        }
        if (this.opEmbeddedInfoByOwner != null) {
            final List<EmbeddedOwnerRelation> embRels = this.opEmbeddedInfoByOwner.get(rel.getOwnerOP());
            embRels.remove(rel);
            if (embRels.size() == 0) {
                this.opEmbeddedInfoByOwner.remove(rel.getOwnerOP());
                if (this.opEmbeddedInfoByOwner.size() == 0) {
                    this.opEmbeddedInfoByOwner = null;
                }
            }
        }
    }
    
    @Override
    public void removeEmbeddedOwnerRelation(final ObjectProvider ownerOP, final int ownerFieldNum, final ObjectProvider embOP) {
        if (this.opEmbeddedInfoByOwner != null) {
            final List<EmbeddedOwnerRelation> ownerRels = this.opEmbeddedInfoByOwner.get(ownerOP);
            EmbeddedOwnerRelation rel = null;
            for (final EmbeddedOwnerRelation ownerRel : ownerRels) {
                if (ownerRel.getEmbeddedOP() == embOP && ownerRel.getOwnerFieldNum() == ownerFieldNum) {
                    rel = ownerRel;
                    break;
                }
            }
            if (rel != null) {
                this.deregisterEmbeddedRelation(rel);
            }
        }
    }
    
    @Override
    public List<EmbeddedOwnerRelation> getOwnerInformationForEmbedded(final ObjectProvider embOP) {
        if (this.opEmbeddedInfoByEmbedded == null) {
            return null;
        }
        return this.opEmbeddedInfoByEmbedded.get(embOP);
    }
    
    @Override
    public List<EmbeddedOwnerRelation> getEmbeddedInformationForOwner(final ObjectProvider ownerOP) {
        if (this.opEmbeddedInfoByOwner == null) {
            return null;
        }
        return this.opEmbeddedInfoByOwner.get(ownerOP);
    }
    
    @Override
    public void setObjectProviderAssociatedValue(final ObjectProvider op, final Object key, final Object value) {
        Map opMap = null;
        if (this.opAssociatedValuesMapByOP == null) {
            this.opAssociatedValuesMapByOP = new HashMap<ObjectProvider, Map<?, ?>>();
            opMap = new HashMap();
            this.opAssociatedValuesMapByOP.put(op, opMap);
        }
        else {
            opMap = this.opAssociatedValuesMapByOP.get(op);
            if (opMap == null) {
                opMap = new HashMap();
                this.opAssociatedValuesMapByOP.put(op, opMap);
            }
        }
        opMap.put(key, value);
    }
    
    @Override
    public Object getObjectProviderAssociatedValue(final ObjectProvider op, final Object key) {
        if (this.opAssociatedValuesMapByOP == null) {
            return null;
        }
        final Map opMap = this.opAssociatedValuesMapByOP.get(op);
        return (opMap == null) ? null : opMap.get(key);
    }
    
    @Override
    public void removeObjectProviderAssociatedValue(final ObjectProvider op, final Object key) {
        if (this.opAssociatedValuesMapByOP != null) {
            final Map opMap = this.opAssociatedValuesMapByOP.get(op);
            if (opMap != null) {
                opMap.remove(key);
            }
        }
    }
    
    @Override
    public boolean containsObjectProviderAssociatedValue(final ObjectProvider op, final Object key) {
        return this.opAssociatedValuesMapByOP != null && this.opAssociatedValuesMapByOP.containsKey(op) && this.opAssociatedValuesMapByOP.get(op).containsKey(key);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
    
    static class ThreadContextInfo
    {
        int referenceCounter;
        Map<Object, ObjectProvider> attachedOwnerByObject;
        Map attachedPCById;
        boolean merging;
        boolean nontxPersistDelete;
        
        ThreadContextInfo() {
            this.referenceCounter = 0;
            this.attachedOwnerByObject = null;
            this.attachedPCById = null;
            this.merging = false;
            this.nontxPersistDelete = false;
        }
    }
    
    private static class ClassDetailsForId
    {
        Object id;
        String className;
        Object pc;
        
        public ClassDetailsForId(final Object id, final String className, final Object pc) {
            this.id = id;
            this.className = className;
            this.pc = pc;
        }
    }
}
