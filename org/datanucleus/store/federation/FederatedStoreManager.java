// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.federation;

import org.datanucleus.ClassConstants;
import org.datanucleus.state.ReferentialJDOStateManager;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.store.connection.ConnectionManager;
import java.io.PrintStream;
import org.datanucleus.util.NucleusLogger;
import java.util.Collection;
import org.datanucleus.store.valuegenerator.ValueGenerationManager;
import org.datanucleus.store.schema.StoreSchemaHandler;
import org.datanucleus.store.NucleusSequence;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.store.NucleusConnection;
import org.datanucleus.store.Extent;
import java.util.Date;
import org.datanucleus.ExecutionContext;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.schema.naming.NamingFactory;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.flush.FlushProcess;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.PersistenceConfiguration;
import java.util.HashMap;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.store.StorePersistenceHandler;
import org.datanucleus.NucleusContext;
import java.util.Map;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.StoreManager;

public class FederatedStoreManager implements StoreManager
{
    protected static final Localiser LOCALISER;
    public static final String PROPERTY_DATA_FEDERATION_DATASTORE_NAME = "DATA_FEDERATION_DATASTORE_NAME";
    StoreManager primaryStoreMgr;
    Map<String, StoreManager> secondaryStoreMgrMap;
    final NucleusContext nucleusContext;
    protected StorePersistenceHandler persistenceHandler;
    private QueryManager queryMgr;
    
    public FederatedStoreManager(final ClassLoaderResolver clr, final NucleusContext nucleusContext) {
        this.secondaryStoreMgrMap = null;
        this.persistenceHandler = null;
        this.queryMgr = null;
        this.nucleusContext = nucleusContext;
        final Map<String, Object> datastoreProps = nucleusContext.getPersistenceConfiguration().getDatastoreProperties();
        this.primaryStoreMgr = NucleusContext.createStoreManagerForProperties(nucleusContext.getPersistenceConfiguration().getPersistenceProperties(), datastoreProps, clr, nucleusContext);
        final String transactionIsolation = nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.transactionIsolation");
        if (transactionIsolation != null) {
            final String reqdIsolation = NucleusContext.getTransactionIsolationForStoreManager(this.primaryStoreMgr, transactionIsolation);
            if (!transactionIsolation.equalsIgnoreCase(reqdIsolation)) {
                nucleusContext.getPersistenceConfiguration().setProperty("datanucleus.transactionIsolation", reqdIsolation);
            }
        }
        final Set<String> propNamesWithDatastore = nucleusContext.getPersistenceConfiguration().getPropertyNamesWithPrefix("datanucleus.datastore.");
        if (propNamesWithDatastore != null) {
            this.secondaryStoreMgrMap = new HashMap<String, StoreManager>();
            for (final String datastorePropName : propNamesWithDatastore) {
                final String datastoreName = datastorePropName.substring("datanucleus.datastore.".length());
                final String filename = nucleusContext.getPersistenceConfiguration().getStringProperty(datastorePropName);
                final PersistenceConfiguration datastoreConf = new PersistenceConfiguration(nucleusContext);
                datastoreConf.setPropertiesUsingFile(filename);
                datastoreConf.setProperty("DATA_FEDERATION_DATASTORE_NAME", datastoreName);
                final StoreManager storeMgr = NucleusContext.createStoreManagerForProperties(datastoreConf.getPersistenceProperties(), datastoreConf.getDatastoreProperties(), clr, nucleusContext);
                this.secondaryStoreMgrMap.put(datastoreName, storeMgr);
            }
        }
        this.persistenceHandler = new FederatedPersistenceHandler(this);
    }
    
    @Override
    public NucleusContext getNucleusContext() {
        return this.nucleusContext;
    }
    
    @Override
    public FlushProcess getFlushProcess() {
        return this.primaryStoreMgr.getFlushProcess();
    }
    
    @Override
    public void close() {
        this.primaryStoreMgr.close();
        this.primaryStoreMgr = null;
        if (this.secondaryStoreMgrMap != null) {
            for (final String name : this.secondaryStoreMgrMap.keySet()) {
                final StoreManager secStoreMgr = this.secondaryStoreMgrMap.get(name);
                secStoreMgr.close();
            }
            this.secondaryStoreMgrMap.clear();
            this.secondaryStoreMgrMap = null;
        }
        this.persistenceHandler.close();
        if (this.queryMgr != null) {
            this.queryMgr.close();
            this.queryMgr = null;
        }
    }
    
    public StoreManager getStoreManagerForClass(final AbstractClassMetaData cmd) {
        if (!cmd.hasExtension("datastore")) {
            return this.primaryStoreMgr;
        }
        final String datastoreName = cmd.getValueForExtension("datastore");
        if (this.secondaryStoreMgrMap == null || !this.secondaryStoreMgrMap.containsKey(datastoreName)) {
            throw new NucleusUserException("Class " + cmd.getFullClassName() + " specified to persist to datastore " + datastoreName + " yet not defined");
        }
        return this.secondaryStoreMgrMap.get(datastoreName);
    }
    
    public StoreManager getStoreManagerForClass(final String className, final ClassLoaderResolver clr) {
        final AbstractClassMetaData cmd = this.nucleusContext.getMetaDataManager().getMetaDataForClass(className, clr);
        return this.getStoreManagerForClass(cmd);
    }
    
    @Override
    public void addClass(final String className, final ClassLoaderResolver clr) {
        this.getStoreManagerForClass(className, clr).addClass(className, clr);
    }
    
    @Override
    public void addClasses(final String[] classNames, final ClassLoaderResolver clr) {
        if (classNames != null) {
            for (int i = 0; i < classNames.length; ++i) {
                this.addClass(classNames[i], clr);
            }
        }
    }
    
    @Override
    public NamingFactory getNamingFactory() {
        return this.primaryStoreMgr.getNamingFactory();
    }
    
    @Override
    public ApiAdapter getApiAdapter() {
        return this.nucleusContext.getApiAdapter();
    }
    
    @Override
    public String getClassNameForObjectID(final Object id, final ClassLoaderResolver clr, final ExecutionContext ec) {
        return this.primaryStoreMgr.getClassNameForObjectID(id, clr, ec);
    }
    
    @Override
    public Date getDatastoreDate() {
        return this.primaryStoreMgr.getDatastoreDate();
    }
    
    @Override
    public Extent getExtent(final ExecutionContext ec, final Class c, final boolean subclasses) {
        return this.getStoreManagerForClass(c.getName(), ec.getClassLoaderResolver()).getExtent(ec, c, subclasses);
    }
    
    @Override
    public boolean isJdbcStore() {
        return this.primaryStoreMgr.isJdbcStore();
    }
    
    @Override
    public NucleusConnection getNucleusConnection(final ExecutionContext ec) {
        return this.primaryStoreMgr.getNucleusConnection(ec);
    }
    
    @Override
    public NucleusSequence getNucleusSequence(final ExecutionContext ec, final SequenceMetaData seqmd) {
        return this.primaryStoreMgr.getNucleusSequence(ec, seqmd);
    }
    
    @Override
    public StoreSchemaHandler getSchemaHandler() {
        return this.primaryStoreMgr.getSchemaHandler();
    }
    
    @Override
    public StorePersistenceHandler getPersistenceHandler() {
        return this.persistenceHandler;
    }
    
    @Override
    public QueryManager getQueryManager() {
        if (this.queryMgr == null) {
            this.queryMgr = new FederatedQueryManager(this.nucleusContext, this);
        }
        return this.queryMgr;
    }
    
    @Override
    public ValueGenerationManager getValueGenerationManager() {
        return this.primaryStoreMgr.getValueGenerationManager();
    }
    
    @Override
    public String getStoreManagerKey() {
        return this.primaryStoreMgr.getStoreManagerKey();
    }
    
    @Override
    public String getQueryCacheKey() {
        return this.primaryStoreMgr.getQueryCacheKey();
    }
    
    @Override
    public Object getStrategyValue(final ExecutionContext ec, final AbstractClassMetaData cmd, final int absoluteFieldNumber) {
        return this.getStoreManagerForClass(cmd).getStrategyValue(ec, cmd, absoluteFieldNumber);
    }
    
    @Override
    public Collection<String> getSubClassesForClass(final String className, final boolean includeDescendents, final ClassLoaderResolver clr) {
        return this.getStoreManagerForClass(className, clr).getSubClassesForClass(className, includeDescendents, clr);
    }
    
    @Override
    public boolean isStrategyDatastoreAttributed(final AbstractClassMetaData cmd, final int absFieldNumber) {
        return this.getStoreManagerForClass(cmd).isStrategyDatastoreAttributed(cmd, absFieldNumber);
    }
    
    @Override
    public String manageClassForIdentity(final Object id, final ClassLoaderResolver clr) {
        NucleusLogger.PERSISTENCE.debug(">> TODO Need to allocate manageClassForIdentity(" + id + ") to correct store manager");
        return this.primaryStoreMgr.manageClassForIdentity(id, clr);
    }
    
    @Override
    public boolean managesClass(final String className) {
        return this.primaryStoreMgr.managesClass(className);
    }
    
    @Override
    public void printInformation(final String category, final PrintStream ps) throws Exception {
        this.primaryStoreMgr.printInformation(category, ps);
    }
    
    @Override
    public void removeAllClasses(final ClassLoaderResolver clr) {
        this.primaryStoreMgr.removeAllClasses(clr);
        if (this.secondaryStoreMgrMap != null) {
            final Collection<StoreManager> secStoreMgrs = this.secondaryStoreMgrMap.values();
            for (final StoreManager storeMgr : secStoreMgrs) {
                storeMgr.removeAllClasses(clr);
            }
        }
    }
    
    @Override
    public boolean supportsQueryLanguage(final String language) {
        return this.primaryStoreMgr.supportsQueryLanguage(language);
    }
    
    @Override
    public boolean supportsValueStrategy(final String language) {
        return this.primaryStoreMgr.supportsValueStrategy(language);
    }
    
    @Override
    public Collection getSupportedOptions() {
        return this.primaryStoreMgr.getSupportedOptions();
    }
    
    @Override
    public ConnectionManager getConnectionManager() {
        return this.primaryStoreMgr.getConnectionManager();
    }
    
    @Override
    public ManagedConnection getConnection(final ExecutionContext ec) {
        return this.primaryStoreMgr.getConnection(ec);
    }
    
    @Override
    public ManagedConnection getConnection(final ExecutionContext ec, final Map options) {
        return this.primaryStoreMgr.getConnection(ec, options);
    }
    
    @Override
    public String getConnectionDriverName() {
        return this.primaryStoreMgr.getConnectionDriverName();
    }
    
    @Override
    public String getConnectionURL() {
        return this.primaryStoreMgr.getConnectionURL();
    }
    
    @Override
    public String getConnectionUserName() {
        return this.primaryStoreMgr.getConnectionUserName();
    }
    
    @Override
    public String getConnectionPassword() {
        return this.primaryStoreMgr.getConnectionPassword();
    }
    
    @Override
    public Object getConnectionFactory() {
        return this.primaryStoreMgr.getConnectionFactory();
    }
    
    @Override
    public Object getConnectionFactory2() {
        return this.primaryStoreMgr.getConnectionFactory2();
    }
    
    @Override
    public String getConnectionFactory2Name() {
        return this.primaryStoreMgr.getConnectionFactory2Name();
    }
    
    @Override
    public String getConnectionFactoryName() {
        return this.primaryStoreMgr.getConnectionFactoryName();
    }
    
    @Override
    public Object getProperty(final String name) {
        return this.primaryStoreMgr.getProperty(name);
    }
    
    @Override
    public boolean hasProperty(final String name) {
        return this.primaryStoreMgr.hasProperty(name);
    }
    
    @Override
    public int getIntProperty(final String name) {
        return this.primaryStoreMgr.getIntProperty(name);
    }
    
    @Override
    public boolean getBooleanProperty(final String name) {
        return this.primaryStoreMgr.getBooleanProperty(name);
    }
    
    @Override
    public boolean getBooleanProperty(final String name, final boolean resultIfNotSet) {
        return this.primaryStoreMgr.getBooleanProperty(name, resultIfNotSet);
    }
    
    @Override
    public Boolean getBooleanObjectProperty(final String name) {
        return this.primaryStoreMgr.getBooleanObjectProperty(name);
    }
    
    @Override
    public String getStringProperty(final String name) {
        return this.primaryStoreMgr.getStringProperty(name);
    }
    
    @Override
    public void transactionStarted(final ExecutionContext ec) {
        this.primaryStoreMgr.transactionStarted(ec);
    }
    
    @Override
    public void transactionCommitted(final ExecutionContext ec) {
        this.primaryStoreMgr.transactionCommitted(ec);
    }
    
    @Override
    public void transactionRolledBack(final ExecutionContext ec) {
        this.primaryStoreMgr.transactionRolledBack(ec);
    }
    
    @Override
    public boolean isAutoCreateTables() {
        return this.primaryStoreMgr.isAutoCreateTables();
    }
    
    @Override
    public boolean isAutoCreateConstraints() {
        return this.primaryStoreMgr.isAutoCreateConstraints();
    }
    
    @Override
    public boolean isAutoCreateColumns() {
        return this.primaryStoreMgr.isAutoCreateColumns();
    }
    
    @Override
    public boolean useBackedSCOWrapperForMember(final AbstractMemberMetaData mmd, final ExecutionContext ec) {
        return this.primaryStoreMgr.useBackedSCOWrapperForMember(mmd, ec);
    }
    
    @Override
    public String getDefaultObjectProviderClassName() {
        return ReferentialJDOStateManager.class.getName();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
