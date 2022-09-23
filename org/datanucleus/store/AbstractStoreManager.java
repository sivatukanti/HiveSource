// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.state.JDOStateManager;
import java.util.Locale;
import java.util.Collections;
import java.util.HashSet;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.store.valuegenerator.AbstractDatastoreGenerator;
import org.datanucleus.metadata.MetaDataUtils;
import java.util.Properties;
import org.datanucleus.store.valuegenerator.ValueGenerator;
import org.datanucleus.metadata.TableGeneratorMetaData;
import org.datanucleus.util.TypeConversionHelper;
import org.datanucleus.store.valuegenerator.ValueGenerationConnectionProvider;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.IdentityMetaData;
import org.datanucleus.metadata.IdentityStrategy;
import java.util.Collection;
import org.datanucleus.identity.SCOID;
import org.datanucleus.store.exceptions.NoExtentException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.identity.OID;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.ClassMetaData;
import java.io.PrintStream;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.autostart.AutoStartMechanism;
import java.util.Date;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.store.schema.naming.NamingCase;
import org.datanucleus.store.schema.naming.DN2NamingFactory;
import org.datanucleus.store.schema.naming.JPANamingFactory;
import org.datanucleus.flush.FlushNonReferential;
import org.datanucleus.store.encryption.ConnectionEncryptionProvider;
import org.datanucleus.Transaction;
import java.util.HashMap;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.connection.ConnectionManagerImpl;
import java.util.Iterator;
import org.datanucleus.store.connection.ConnectionFactory;
import org.datanucleus.ExecutionContext;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.connection.ConnectionManager;
import org.datanucleus.store.schema.naming.NamingFactory;
import org.datanucleus.store.schema.StoreSchemaHandler;
import org.datanucleus.store.query.QueryManager;
import org.datanucleus.flush.FlushProcess;
import org.datanucleus.store.valuegenerator.ValueGenerationManager;
import org.datanucleus.NucleusContext;
import org.datanucleus.util.Localiser;
import org.datanucleus.properties.PropertyStore;

public abstract class AbstractStoreManager extends PropertyStore implements StoreManager
{
    protected static final Localiser LOCALISER;
    protected final String storeManagerKey;
    protected final boolean readOnlyDatastore;
    protected final boolean fixedDatastore;
    protected final boolean autoCreateTables;
    protected final boolean autoCreateColumns;
    protected final boolean autoCreateConstraints;
    protected final boolean autoCreateWarnOnError;
    protected final boolean validateTables;
    protected final boolean validateColumns;
    protected final boolean validateConstraints;
    protected final NucleusContext nucleusContext;
    private ValueGenerationManager valueGenerationMgr;
    protected StoreDataManager storeDataMgr;
    protected StorePersistenceHandler persistenceHandler;
    protected FlushProcess flushProcess;
    private QueryManager queryMgr;
    protected StoreSchemaHandler schemaHandler;
    protected NamingFactory namingFactory;
    protected ConnectionManager connectionMgr;
    protected String primaryConnectionFactoryName;
    protected String secondaryConnectionFactoryName;
    
    protected AbstractStoreManager(final String key, final ClassLoaderResolver clr, final NucleusContext nucleusContext, final Map<String, Object> props) {
        this.storeDataMgr = new StoreDataManager();
        this.persistenceHandler = null;
        this.flushProcess = null;
        this.queryMgr = null;
        this.schemaHandler = null;
        this.namingFactory = null;
        this.storeManagerKey = key;
        this.nucleusContext = nucleusContext;
        if (props != null) {
            for (final Map.Entry<String, Object> entry : props.entrySet()) {
                this.setPropertyInternal(entry.getKey(), entry.getValue());
            }
        }
        this.readOnlyDatastore = this.getBooleanProperty("datanucleus.readOnlyDatastore");
        this.fixedDatastore = this.getBooleanProperty("datanucleus.fixedDatastore");
        if (this.readOnlyDatastore || this.fixedDatastore) {
            this.autoCreateTables = false;
            this.autoCreateColumns = false;
            this.autoCreateConstraints = false;
        }
        else {
            final boolean autoCreateSchema = this.getBooleanProperty("datanucleus.autoCreateSchema");
            if (autoCreateSchema) {
                this.autoCreateTables = true;
                this.autoCreateColumns = true;
                this.autoCreateConstraints = true;
            }
            else {
                this.autoCreateTables = this.getBooleanProperty("datanucleus.autoCreateTables");
                this.autoCreateColumns = this.getBooleanProperty("datanucleus.autoCreateColumns");
                this.autoCreateConstraints = this.getBooleanProperty("datanucleus.autoCreateConstraints");
            }
        }
        this.autoCreateWarnOnError = this.getBooleanProperty("datanucleus.autoCreateWarnOnError");
        final boolean validateSchema = this.getBooleanProperty("datanucleus.validateSchema");
        if (validateSchema) {
            this.validateTables = true;
            this.validateColumns = true;
            this.validateConstraints = true;
        }
        else {
            if (!(this.validateTables = this.getBooleanProperty("datanucleus.validateTables"))) {
                this.validateColumns = false;
            }
            else {
                this.validateColumns = this.getBooleanProperty("datanucleus.validateColumns");
            }
            this.validateConstraints = this.getBooleanProperty("datanucleus.validateConstraints");
        }
        this.registerConnectionMgr();
        this.registerConnectionFactory();
        nucleusContext.addExecutionContextListener(new ExecutionContext.LifecycleListener() {
            @Override
            public void preClose(final ExecutionContext ec) {
                ConnectionFactory connFactory = AbstractStoreManager.this.connectionMgr.lookupConnectionFactory(AbstractStoreManager.this.primaryConnectionFactoryName);
                AbstractStoreManager.this.connectionMgr.closeAllConnections(connFactory, ec);
                connFactory = AbstractStoreManager.this.connectionMgr.lookupConnectionFactory(AbstractStoreManager.this.secondaryConnectionFactoryName);
                AbstractStoreManager.this.connectionMgr.closeAllConnections(connFactory, ec);
            }
        });
    }
    
    protected void registerConnectionMgr() {
        this.connectionMgr = new ConnectionManagerImpl(this.nucleusContext);
    }
    
    protected void registerConnectionFactory() {
        final String datastoreName = this.getStringProperty("DATA_FEDERATION_DATASTORE_NAME");
        ConfigurationElement cfElem = this.nucleusContext.getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_connectionfactory", new String[] { "datastore", "transactional" }, new String[] { this.storeManagerKey, "true" });
        if (cfElem != null) {
            this.primaryConnectionFactoryName = cfElem.getAttribute("name");
            if (datastoreName != null) {
                this.primaryConnectionFactoryName = this.primaryConnectionFactoryName + "-" + datastoreName;
            }
            Label_0244: {
                try {
                    final ConnectionFactory cf = (ConnectionFactory)this.nucleusContext.getPluginManager().createExecutableExtension("org.datanucleus.store_connectionfactory", new String[] { "datastore", "transactional" }, new String[] { this.storeManagerKey, "true" }, "class-name", new Class[] { StoreManager.class, String.class }, new Object[] { this, "tx" });
                    this.connectionMgr.registerConnectionFactory(this.primaryConnectionFactoryName, cf);
                    if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                        NucleusLogger.CONNECTION.debug(AbstractStoreManager.LOCALISER.msg("032018", this.primaryConnectionFactoryName));
                    }
                    break Label_0244;
                }
                catch (Exception e) {
                    throw new NucleusException("Error creating transactional connection factory", e).setFatal();
                }
                throw new NucleusException("Error creating transactional connection factory. No connection factory plugin defined");
            }
            cfElem = this.nucleusContext.getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_connectionfactory", new String[] { "datastore", "transactional" }, new String[] { this.storeManagerKey, "false" });
            if (cfElem != null) {
                this.secondaryConnectionFactoryName = cfElem.getAttribute("name");
                if (datastoreName != null) {
                    this.secondaryConnectionFactoryName = this.secondaryConnectionFactoryName + "-" + datastoreName;
                }
                try {
                    final ConnectionFactory cf = (ConnectionFactory)this.nucleusContext.getPluginManager().createExecutableExtension("org.datanucleus.store_connectionfactory", new String[] { "datastore", "transactional" }, new String[] { this.storeManagerKey, "false" }, "class-name", new Class[] { StoreManager.class, String.class }, new Object[] { this, "nontx" });
                    if (NucleusLogger.CONNECTION.isDebugEnabled()) {
                        NucleusLogger.CONNECTION.debug(AbstractStoreManager.LOCALISER.msg("032019", this.secondaryConnectionFactoryName));
                    }
                    this.connectionMgr.registerConnectionFactory(this.secondaryConnectionFactoryName, cf);
                }
                catch (Exception e) {
                    throw new NucleusException("Error creating nontransactional connection factory", e).setFatal();
                }
            }
            return;
        }
        throw new NucleusException("Error creating transactional connection factory. No connection factory plugin defined");
    }
    
    @Override
    public synchronized void close() {
        if (this.primaryConnectionFactoryName != null) {
            final ConnectionFactory cf = this.connectionMgr.lookupConnectionFactory(this.primaryConnectionFactoryName);
            if (cf != null) {
                cf.close();
            }
        }
        if (this.secondaryConnectionFactoryName != null) {
            final ConnectionFactory cf = this.connectionMgr.lookupConnectionFactory(this.secondaryConnectionFactoryName);
            if (cf != null) {
                cf.close();
            }
        }
        if (this.valueGenerationMgr != null) {
            this.valueGenerationMgr.clear();
        }
        this.storeDataMgr.clear();
        if (this.persistenceHandler != null) {
            this.persistenceHandler.close();
            this.persistenceHandler = null;
        }
        if (this.queryMgr != null) {
            this.queryMgr.close();
            this.queryMgr = null;
        }
    }
    
    @Override
    public ConnectionManager getConnectionManager() {
        return this.connectionMgr;
    }
    
    @Override
    public ManagedConnection getConnection(final ExecutionContext ec) {
        return this.getConnection(ec, null);
    }
    
    @Override
    public ManagedConnection getConnection(final ExecutionContext ec, final Map options) {
        ConnectionFactory connFactory;
        if (ec.getTransaction().isActive()) {
            connFactory = this.connectionMgr.lookupConnectionFactory(this.primaryConnectionFactoryName);
        }
        else if (this.secondaryConnectionFactoryName != null) {
            connFactory = this.connectionMgr.lookupConnectionFactory(this.secondaryConnectionFactoryName);
        }
        else {
            connFactory = this.connectionMgr.lookupConnectionFactory(this.primaryConnectionFactoryName);
        }
        return connFactory.getConnection(ec, ec.getTransaction(), options);
    }
    
    public ManagedConnection getConnection(final int isolation_level) {
        ConnectionFactory connFactory = null;
        if (this.secondaryConnectionFactoryName != null) {
            connFactory = this.connectionMgr.lookupConnectionFactory(this.secondaryConnectionFactoryName);
        }
        else {
            connFactory = this.connectionMgr.lookupConnectionFactory(this.primaryConnectionFactoryName);
        }
        Map options = null;
        if (isolation_level >= 0) {
            options = new HashMap();
            options.put("transaction.isolation", isolation_level);
        }
        return connFactory.getConnection(null, null, options);
    }
    
    @Override
    public String getConnectionDriverName() {
        return this.getStringProperty("datanucleus.ConnectionDriverName");
    }
    
    @Override
    public String getConnectionURL() {
        return this.getStringProperty("datanucleus.ConnectionURL");
    }
    
    @Override
    public String getConnectionUserName() {
        return this.getStringProperty("datanucleus.ConnectionUserName");
    }
    
    @Override
    public String getConnectionPassword() {
        String password = this.getStringProperty("datanucleus.ConnectionPassword");
        if (password != null) {
            final String decrypterName = this.getStringProperty("datanucleus.ConnectionPasswordDecrypter");
            if (decrypterName != null) {
                final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(null);
                try {
                    final Class decrypterCls = clr.classForName(decrypterName);
                    final ConnectionEncryptionProvider decrypter = decrypterCls.newInstance();
                    password = decrypter.decrypt(password);
                }
                catch (Exception e) {
                    NucleusLogger.DATASTORE.warn("Error invoking decrypter class " + decrypterName, e);
                }
            }
        }
        return password;
    }
    
    @Override
    public Object getConnectionFactory() {
        return this.getProperty("datanucleus.ConnectionFactory");
    }
    
    @Override
    public String getConnectionFactoryName() {
        return this.getStringProperty("datanucleus.ConnectionFactoryName");
    }
    
    @Override
    public Object getConnectionFactory2() {
        return this.getProperty("datanucleus.ConnectionFactory2");
    }
    
    @Override
    public String getConnectionFactory2Name() {
        return this.getStringProperty("datanucleus.ConnectionFactory2Name");
    }
    
    @Override
    public boolean isAutoCreateTables() {
        return this.autoCreateTables;
    }
    
    @Override
    public boolean isAutoCreateColumns() {
        return this.autoCreateColumns;
    }
    
    @Override
    public boolean isAutoCreateConstraints() {
        return this.autoCreateConstraints;
    }
    
    public boolean isValidateTables() {
        return this.validateTables;
    }
    
    public boolean isValidateColumns() {
        return this.validateColumns;
    }
    
    public boolean isValidateConstraints() {
        return this.validateConstraints;
    }
    
    @Override
    public boolean isJdbcStore() {
        return false;
    }
    
    @Override
    public StorePersistenceHandler getPersistenceHandler() {
        return this.persistenceHandler;
    }
    
    @Override
    public FlushProcess getFlushProcess() {
        if (this.flushProcess == null) {
            this.flushProcess = new FlushNonReferential();
        }
        return this.flushProcess;
    }
    
    @Override
    public QueryManager getQueryManager() {
        if (this.queryMgr == null) {
            this.queryMgr = new QueryManager(this.nucleusContext, this);
        }
        return this.queryMgr;
    }
    
    @Override
    public StoreSchemaHandler getSchemaHandler() {
        return this.schemaHandler;
    }
    
    @Override
    public NamingFactory getNamingFactory() {
        if (this.namingFactory == null) {
            if (this.nucleusContext.getApiName().equalsIgnoreCase("JPA")) {
                this.namingFactory = new JPANamingFactory(this.nucleusContext);
            }
            else {
                this.namingFactory = new DN2NamingFactory(this.nucleusContext);
            }
            final String identifierCase = this.getStringProperty("datanucleus.identifier.case");
            if (identifierCase != null) {
                if (identifierCase.equalsIgnoreCase("lowercase")) {
                    this.namingFactory.setNamingCase(NamingCase.LOWER_CASE);
                }
                else if (identifierCase.equalsIgnoreCase("UPPERCASE")) {
                    this.namingFactory.setNamingCase(NamingCase.UPPER_CASE);
                }
                else {
                    this.namingFactory.setNamingCase(NamingCase.MIXED_CASE);
                }
            }
        }
        return this.namingFactory;
    }
    
    @Override
    public NucleusSequence getNucleusSequence(final ExecutionContext ec, final SequenceMetaData seqmd) {
        return new NucleusSequenceImpl(ec, this, seqmd);
    }
    
    @Override
    public NucleusConnection getNucleusConnection(final ExecutionContext ec) {
        final ConnectionFactory cf = this.connectionMgr.lookupConnectionFactory(this.primaryConnectionFactoryName);
        final boolean enlisted = ec.getTransaction().isActive();
        final ManagedConnection mc = cf.getConnection(enlisted ? ec : null, enlisted ? ec.getTransaction() : null, null);
        mc.lock();
        final Runnable closeRunnable = new Runnable() {
            @Override
            public void run() {
                mc.unlock();
                if (!enlisted) {
                    mc.close();
                }
            }
        };
        return new NucleusConnectionImpl(mc.getConnection(), closeRunnable);
    }
    
    @Override
    public ValueGenerationManager getValueGenerationManager() {
        if (this.valueGenerationMgr == null) {
            this.valueGenerationMgr = new ValueGenerationManager();
        }
        return this.valueGenerationMgr;
    }
    
    @Override
    public ApiAdapter getApiAdapter() {
        return this.nucleusContext.getApiAdapter();
    }
    
    @Override
    public String getStoreManagerKey() {
        return this.storeManagerKey;
    }
    
    @Override
    public String getQueryCacheKey() {
        return this.getStoreManagerKey();
    }
    
    @Override
    public NucleusContext getNucleusContext() {
        return this.nucleusContext;
    }
    
    public MetaDataManager getMetaDataManager() {
        return this.nucleusContext.getMetaDataManager();
    }
    
    @Override
    public Date getDatastoreDate() {
        return new Date();
    }
    
    protected void registerStoreData(final StoreData data) {
        this.storeDataMgr.registerStoreData(data);
        if (this.nucleusContext.getAutoStartMechanism() != null) {
            this.nucleusContext.getAutoStartMechanism().addClass(data);
        }
    }
    
    protected void deregisterAllStoreData() {
        this.storeDataMgr.clear();
        final AutoStartMechanism starter = this.nucleusContext.getAutoStartMechanism();
        if (starter != null) {
            try {
                if (!starter.isOpen()) {
                    starter.open();
                }
                starter.deleteAllClasses();
            }
            finally {
                if (starter.isOpen()) {
                    starter.close();
                }
            }
        }
    }
    
    protected void logConfiguration() {
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug("======================= Datastore =========================");
            NucleusLogger.DATASTORE.debug("StoreManager : \"" + this.storeManagerKey + "\" (" + this.getClass().getName() + ")");
            NucleusLogger.DATASTORE.debug("Datastore : " + (this.readOnlyDatastore ? "read-only" : "read-write") + (this.fixedDatastore ? ", fixed" : "") + (this.getBooleanProperty("datanucleus.SerializeRead") ? ", useLocking" : ""));
            StringBuffer autoCreateOptions = null;
            if (this.autoCreateTables || this.autoCreateColumns || this.autoCreateConstraints) {
                autoCreateOptions = new StringBuffer();
                boolean first = true;
                if (this.autoCreateTables) {
                    if (!first) {
                        autoCreateOptions.append(",");
                    }
                    autoCreateOptions.append("Tables");
                    first = false;
                }
                if (this.autoCreateColumns) {
                    if (!first) {
                        autoCreateOptions.append(",");
                    }
                    autoCreateOptions.append("Columns");
                    first = false;
                }
                if (this.autoCreateConstraints) {
                    if (!first) {
                        autoCreateOptions.append(",");
                    }
                    autoCreateOptions.append("Constraints");
                    first = false;
                }
            }
            StringBuffer validateOptions = null;
            if (this.validateTables || this.validateColumns || this.validateConstraints) {
                validateOptions = new StringBuffer();
                boolean first2 = true;
                if (this.validateTables) {
                    validateOptions.append("Tables");
                    first2 = false;
                }
                if (this.validateColumns) {
                    if (!first2) {
                        validateOptions.append(",");
                    }
                    validateOptions.append("Columns");
                    first2 = false;
                }
                if (this.validateConstraints) {
                    if (!first2) {
                        validateOptions.append(",");
                    }
                    validateOptions.append("Constraints");
                    first2 = false;
                }
            }
            NucleusLogger.DATASTORE.debug("Schema Control : AutoCreate(" + ((autoCreateOptions != null) ? autoCreateOptions.toString() : "None") + ")" + ", Validate(" + ((validateOptions != null) ? validateOptions.toString() : "None") + ")");
            final String[] queryLanguages = this.nucleusContext.getPluginManager().getAttributeValuesForExtension("org.datanucleus.store_query_query", "datastore", this.storeManagerKey, "name");
            NucleusLogger.DATASTORE.debug("Query Languages : " + ((queryLanguages != null) ? StringUtils.objectArrayToString(queryLanguages) : "none"));
            NucleusLogger.DATASTORE.debug("Queries : Timeout=" + this.getIntProperty("datanucleus.datastoreReadTimeout"));
            NucleusLogger.DATASTORE.debug("===========================================================");
        }
    }
    
    @Override
    public void printInformation(final String category, final PrintStream ps) throws Exception {
        if (category.equalsIgnoreCase("DATASTORE")) {
            ps.println(AbstractStoreManager.LOCALISER.msg("032020", this.storeManagerKey, this.getConnectionURL(), this.readOnlyDatastore ? "read-only" : "read-write", this.fixedDatastore ? ", fixed" : ""));
        }
    }
    
    @Override
    public boolean managesClass(final String className) {
        return this.storeDataMgr.managesClass(className);
    }
    
    @Override
    public void addClass(final String className, final ClassLoaderResolver clr) {
        this.addClasses(new String[] { className }, clr);
    }
    
    @Override
    public void addClasses(final String[] classNames, final ClassLoaderResolver clr) {
        if (classNames == null) {
            return;
        }
        final String[] filteredClassNames = this.getNucleusContext().getTypeManager().filterOutSupportedSecondClassNames(classNames);
        for (final ClassMetaData cmd : this.getMetaDataManager().getReferencedClasses(filteredClassNames, clr)) {
            if (cmd.getPersistenceModifier() == ClassPersistenceModifier.PERSISTENCE_CAPABLE && !this.storeDataMgr.managesClass(cmd.getFullClassName())) {
                this.registerStoreData(this.newStoreData(cmd, clr));
            }
        }
    }
    
    protected StoreData newStoreData(final ClassMetaData cmd, final ClassLoaderResolver clr) {
        return new StoreData(cmd.getFullClassName(), cmd, 1, null);
    }
    
    @Override
    public void removeAllClasses(final ClassLoaderResolver clr) {
    }
    
    @Override
    public String manageClassForIdentity(final Object id, final ClassLoaderResolver clr) {
        String className = null;
        if (id instanceof OID) {
            className = ((OID)id).getPcClass();
            final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(className, clr);
            if (cmd.getIdentityType() != IdentityType.DATASTORE) {
                throw new NucleusUserException(AbstractStoreManager.LOCALISER.msg("038001", id, cmd.getFullClassName()));
            }
        }
        else {
            if (!this.getApiAdapter().isSingleFieldIdentity(id)) {
                throw new NucleusException("StoreManager.manageClassForIdentity called for id=" + id + " yet should only be called for datastore-identity/SingleFieldIdentity");
            }
            className = this.getApiAdapter().getTargetClassNameForSingleFieldIdentity(id);
            final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(className, clr);
            if (cmd.getIdentityType() != IdentityType.APPLICATION || !cmd.getObjectidClass().equals(id.getClass().getName())) {
                throw new NucleusUserException(AbstractStoreManager.LOCALISER.msg("038001", id, cmd.getFullClassName()));
            }
        }
        if (!this.managesClass(className)) {
            this.addClass(className, clr);
        }
        return className;
    }
    
    @Override
    public Extent getExtent(final ExecutionContext ec, final Class c, final boolean subclasses) {
        final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(c, ec.getClassLoaderResolver());
        if (!cmd.isRequiresExtent()) {
            throw new NoExtentException(c.getName());
        }
        if (!this.managesClass(c.getName())) {
            this.addClass(c.getName(), ec.getClassLoaderResolver());
        }
        return new DefaultCandidateExtent(ec, c, subclasses, cmd);
    }
    
    @Override
    public boolean supportsQueryLanguage(final String language) {
        if (language == null) {
            return false;
        }
        final String name = this.getNucleusContext().getPluginManager().getAttributeValueForExtension("org.datanucleus.store_query_query", new String[] { "name", "datastore" }, new String[] { language, this.storeManagerKey }, "name");
        return name != null;
    }
    
    @Override
    public boolean supportsValueStrategy(final String strategy) {
        ConfigurationElement elem = this.nucleusContext.getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "unique" }, new String[] { strategy, "true" });
        if (elem != null) {
            return true;
        }
        elem = this.nucleusContext.getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "datastore" }, new String[] { strategy, this.storeManagerKey });
        return elem != null;
    }
    
    @Override
    public String getClassNameForObjectID(final Object id, final ClassLoaderResolver clr, final ExecutionContext ec) {
        if (id == null) {
            return null;
        }
        if (id instanceof SCOID) {
            return ((SCOID)id).getSCOClass();
        }
        if (id instanceof OID) {
            return ((OID)id).getPcClass();
        }
        if (this.getApiAdapter().isSingleFieldIdentity(id)) {
            return this.getApiAdapter().getTargetClassNameForSingleFieldIdentity(id);
        }
        final Collection<AbstractClassMetaData> cmds = this.getMetaDataManager().getClassMetaDataWithApplicationId(id.getClass().getName());
        if (cmds != null) {
            final Iterator<AbstractClassMetaData> iter = cmds.iterator();
            if (iter.hasNext()) {
                final AbstractClassMetaData cmd = iter.next();
                return cmd.getFullClassName();
            }
        }
        return null;
    }
    
    @Override
    public boolean isStrategyDatastoreAttributed(final AbstractClassMetaData cmd, final int absFieldNumber) {
        if (absFieldNumber < 0) {
            if (cmd.isEmbeddedOnly()) {
                return false;
            }
            final IdentityMetaData idmd = cmd.getBaseIdentityMetaData();
            if (idmd == null) {
                final String strategy = this.getStrategyForNative(cmd, absFieldNumber);
                if (strategy.equalsIgnoreCase("identity")) {
                    return true;
                }
            }
            else {
                final IdentityStrategy idStrategy = idmd.getValueStrategy();
                if (idStrategy == IdentityStrategy.IDENTITY) {
                    return true;
                }
                if (idStrategy == IdentityStrategy.NATIVE) {
                    final String strategy2 = this.getStrategyForNative(cmd, absFieldNumber);
                    if (strategy2.equalsIgnoreCase("identity")) {
                        return true;
                    }
                }
            }
        }
        else {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(absFieldNumber);
            if (mmd.getValueStrategy() == null) {
                return false;
            }
            if (mmd.getValueStrategy() == IdentityStrategy.IDENTITY) {
                return true;
            }
            if (mmd.getValueStrategy() == IdentityStrategy.NATIVE) {
                final String strategy = this.getStrategyForNative(cmd, absFieldNumber);
                if (strategy.equalsIgnoreCase("identity")) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Object getStrategyValue(final ExecutionContext ec, final AbstractClassMetaData cmd, final int absoluteFieldNumber) {
        AbstractMemberMetaData mmd = null;
        String fieldName = null;
        IdentityStrategy strategy = null;
        String sequence = null;
        String valueGeneratorName = null;
        TableGeneratorMetaData tableGeneratorMetaData = null;
        SequenceMetaData sequenceMetaData = null;
        if (absoluteFieldNumber >= 0) {
            mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(absoluteFieldNumber);
            fieldName = mmd.getFullFieldName();
            strategy = mmd.getValueStrategy();
            sequence = mmd.getSequence();
            valueGeneratorName = mmd.getValueGeneratorName();
        }
        else {
            fieldName = cmd.getFullClassName() + " (datastore id)";
            strategy = cmd.getIdentityMetaData().getValueStrategy();
            sequence = cmd.getIdentityMetaData().getSequence();
            valueGeneratorName = cmd.getIdentityMetaData().getValueGeneratorName();
        }
        String strategyName = strategy.toString();
        if (strategy.equals(IdentityStrategy.CUSTOM)) {
            strategyName = strategy.getCustomName();
        }
        else if (strategy.equals(IdentityStrategy.NATIVE)) {
            strategyName = this.getStrategyForNative(cmd, absoluteFieldNumber);
            strategy = IdentityStrategy.getIdentityStrategy(strategyName);
        }
        if (valueGeneratorName != null) {
            if (strategy == IdentityStrategy.INCREMENT) {
                tableGeneratorMetaData = this.getMetaDataManager().getMetaDataForTableGenerator(ec.getClassLoaderResolver(), valueGeneratorName);
                if (tableGeneratorMetaData == null) {
                    throw new NucleusUserException(AbstractStoreManager.LOCALISER.msg("038005", fieldName, valueGeneratorName));
                }
            }
            else if (strategy == IdentityStrategy.SEQUENCE) {
                sequenceMetaData = this.getMetaDataManager().getMetaDataForSequence(ec.getClassLoaderResolver(), valueGeneratorName);
                if (sequenceMetaData == null) {
                    throw new NucleusUserException(AbstractStoreManager.LOCALISER.msg("038006", fieldName, valueGeneratorName));
                }
            }
        }
        else if (strategy == IdentityStrategy.SEQUENCE && sequence != null) {
            sequenceMetaData = this.getMetaDataManager().getMetaDataForSequence(ec.getClassLoaderResolver(), sequence);
            if (sequenceMetaData == null) {
                NucleusLogger.VALUEGENERATION.warn("Field " + fieldName + " has been specified to use sequence " + sequence + " but there is no <sequence> specified in the MetaData. " + "Falling back to use a sequence in the datastore with this name directly.");
            }
        }
        String generatorName = null;
        String generatorNameKeyInManager = null;
        ConfigurationElement elem = this.nucleusContext.getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "unique" }, new String[] { strategyName, "true" });
        if (elem != null) {
            generatorName = (generatorNameKeyInManager = elem.getAttribute("name"));
        }
        else {
            elem = this.nucleusContext.getPluginManager().getConfigurationElementForExtension("org.datanucleus.store_valuegenerator", new String[] { "name", "datastore" }, new String[] { strategyName, this.storeManagerKey });
            if (elem != null) {
                generatorName = elem.getAttribute("name");
            }
        }
        if (generatorNameKeyInManager == null) {
            if (absoluteFieldNumber >= 0) {
                generatorNameKeyInManager = mmd.getFullFieldName();
            }
            else {
                generatorNameKeyInManager = cmd.getBaseAbstractClassMetaData().getFullClassName();
            }
        }
        ValueGenerator generator = null;
        synchronized (this) {
            generator = this.getValueGenerationManager().getValueGenerator(generatorNameKeyInManager);
            if (generator == null) {
                if (generatorName == null) {
                    throw new NucleusUserException(AbstractStoreManager.LOCALISER.msg("038004", strategy));
                }
                final Properties props = this.getPropertiesForGenerator(cmd, absoluteFieldNumber, ec, sequenceMetaData, tableGeneratorMetaData);
                Class cls = null;
                if (elem != null) {
                    cls = this.nucleusContext.getPluginManager().loadClass(elem.getExtension().getPlugin().getSymbolicName(), elem.getAttribute("class-name"));
                }
                if (cls == null) {
                    throw new NucleusException("Cannot create Value Generator for strategy " + generatorName);
                }
                generator = this.getValueGenerationManager().createValueGenerator(generatorNameKeyInManager, cls, props, this, null);
            }
        }
        Object oid = this.getStrategyValueForGenerator(generator, ec);
        if (mmd != null) {
            try {
                final Object convertedValue = TypeConversionHelper.convertTo(oid, mmd.getType());
                if (convertedValue == null) {
                    throw new NucleusException(AbstractStoreManager.LOCALISER.msg("038003", mmd.getFullFieldName(), oid)).setFatal();
                }
                oid = convertedValue;
            }
            catch (NumberFormatException nfe) {
                throw new NucleusUserException("Value strategy created value=" + oid + " type=" + oid.getClass().getName() + " but field is of type " + mmd.getTypeName() + ". Use a different strategy or change the type of the field " + mmd.getFullFieldName());
            }
        }
        if (NucleusLogger.VALUEGENERATION.isDebugEnabled()) {
            NucleusLogger.VALUEGENERATION.debug(AbstractStoreManager.LOCALISER.msg("038002", fieldName, strategy, generator.getClass().getName(), oid));
        }
        return oid;
    }
    
    protected String getStrategyForNative(final AbstractClassMetaData cmd, final int absFieldNumber) {
        if (absFieldNumber >= 0) {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(absFieldNumber);
            final Class type = mmd.getType();
            if (String.class.isAssignableFrom(type)) {
                return "uuid-hex";
            }
            if (type != Long.class && type != Integer.class && type != Short.class && type != Long.TYPE && type != Integer.TYPE && type != Short.TYPE) {
                throw new NucleusUserException("This datastore provider doesn't support native strategy for field of type " + type.getName());
            }
            if (this.supportsValueStrategy("identity")) {
                return "identity";
            }
            if (this.supportsValueStrategy("sequence") && mmd.getSequence() != null) {
                return "sequence";
            }
            if (this.supportsValueStrategy("increment")) {
                return "increment";
            }
            throw new NucleusUserException("This datastore provider doesn't support numeric native strategy for member " + mmd.getFullFieldName());
        }
        else {
            final IdentityMetaData idmd = cmd.getBaseIdentityMetaData();
            if (idmd != null && idmd.getColumnMetaData() != null) {
                final String jdbcType = idmd.getColumnMetaData().getJdbcType();
                if (MetaDataUtils.isJdbcTypeString(jdbcType)) {
                    return "uuid-hex";
                }
            }
            if (this.supportsValueStrategy("identity")) {
                return "identity";
            }
            if (this.supportsValueStrategy("sequence") && idmd.getSequence() != null) {
                return "sequence";
            }
            if (this.supportsValueStrategy("increment")) {
                return "increment";
            }
            throw new NucleusUserException("This datastore provider doesn't support numeric native strategy for class " + cmd.getFullClassName());
        }
    }
    
    protected Object getStrategyValueForGenerator(final ValueGenerator generator, final ExecutionContext ec) {
        Object oid = null;
        synchronized (generator) {
            if (generator instanceof AbstractDatastoreGenerator) {
                final ValueGenerationConnectionProvider connProvider = new ValueGenerationConnectionProvider() {
                    ManagedConnection mconn;
                    
                    @Override
                    public ManagedConnection retrieveConnection() {
                        return this.mconn = AbstractStoreManager.this.getConnection(ec);
                    }
                    
                    @Override
                    public void releaseConnection() {
                        this.mconn.release();
                        this.mconn = null;
                    }
                };
                ((AbstractDatastoreGenerator)generator).setConnectionProvider(connProvider);
            }
            oid = generator.next();
        }
        return oid;
    }
    
    protected Properties getPropertiesForGenerator(final AbstractClassMetaData cmd, final int absoluteFieldNumber, final ExecutionContext ec, final SequenceMetaData seqmd, final TableGeneratorMetaData tablegenmd) {
        final Properties properties = new Properties();
        AbstractMemberMetaData mmd = null;
        IdentityStrategy strategy = null;
        String sequence = null;
        ExtensionMetaData[] extensions = null;
        if (absoluteFieldNumber >= 0) {
            mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(absoluteFieldNumber);
            strategy = mmd.getValueStrategy();
            sequence = mmd.getSequence();
            extensions = mmd.getExtensions();
        }
        else {
            final IdentityMetaData idmd = cmd.getBaseIdentityMetaData();
            strategy = idmd.getValueStrategy();
            sequence = idmd.getSequence();
            extensions = idmd.getExtensions();
        }
        properties.setProperty("class-name", cmd.getFullClassName());
        properties.put("root-class-name", cmd.getBaseAbstractClassMetaData().getFullClassName());
        if (mmd != null) {
            properties.setProperty("field-name", mmd.getFullFieldName());
        }
        if (sequence != null) {
            properties.setProperty("sequence-name", sequence);
        }
        if (extensions != null) {
            for (int i = 0; i < extensions.length; ++i) {
                properties.put(extensions[i].getKey(), extensions[i].getValue());
            }
        }
        if (strategy.equals(IdentityStrategy.NATIVE)) {
            final String realStrategyName = this.getStrategyForNative(cmd, absoluteFieldNumber);
            strategy = IdentityStrategy.getIdentityStrategy(realStrategyName);
        }
        if (strategy == IdentityStrategy.INCREMENT && tablegenmd != null) {
            properties.put("key-initial-value", "" + tablegenmd.getInitialValue());
            properties.put("key-cache-size", "" + tablegenmd.getAllocationSize());
            if (tablegenmd.getTableName() != null) {
                properties.put("sequence-table-name", tablegenmd.getTableName());
            }
            if (tablegenmd.getCatalogName() != null) {
                properties.put("sequence-catalog-name", tablegenmd.getCatalogName());
            }
            if (tablegenmd.getSchemaName() != null) {
                properties.put("sequence-schema-name", tablegenmd.getSchemaName());
            }
            if (tablegenmd.getPKColumnName() != null) {
                properties.put("sequence-name-column-name", tablegenmd.getPKColumnName());
            }
            if (tablegenmd.getPKColumnName() != null) {
                properties.put("sequence-nextval-column-name", tablegenmd.getValueColumnName());
            }
            if (tablegenmd.getPKColumnValue() != null) {
                properties.put("sequence-name", tablegenmd.getPKColumnValue());
            }
        }
        else if (strategy == IdentityStrategy.INCREMENT && tablegenmd == null) {
            if (!properties.containsKey("key-cache-size")) {
                final int allocSize = this.getIntProperty("datanucleus.valuegeneration.increment.allocationSize");
                properties.put("key-cache-size", "" + allocSize);
            }
        }
        else if (strategy == IdentityStrategy.SEQUENCE && seqmd != null && seqmd.getDatastoreSequence() != null) {
            if (seqmd.getInitialValue() >= 0) {
                properties.put("key-initial-value", "" + seqmd.getInitialValue());
            }
            if (seqmd.getAllocationSize() > 0) {
                properties.put("key-cache-size", "" + seqmd.getAllocationSize());
            }
            else {
                final int allocSize = this.getIntProperty("datanucleus.valuegeneration.sequence.allocationSize");
                properties.put("key-cache-size", "" + allocSize);
            }
            properties.put("sequence-name", "" + seqmd.getDatastoreSequence());
            final ExtensionMetaData[] seqExtensions = seqmd.getExtensions();
            if (seqExtensions != null) {
                for (int j = 0; j < seqExtensions.length; ++j) {
                    properties.put(seqExtensions[j].getKey(), seqExtensions[j].getValue());
                }
            }
        }
        return properties;
    }
    
    @Override
    public Collection<String> getSubClassesForClass(final String className, final boolean includeDescendents, final ClassLoaderResolver clr) {
        final HashSet subclasses = new HashSet();
        final String[] subclassNames = this.getMetaDataManager().getSubclassesForClass(className, includeDescendents);
        if (subclassNames != null) {
            for (int i = 0; i < subclassNames.length; ++i) {
                if (!this.storeDataMgr.managesClass(subclassNames[i])) {
                    this.addClass(subclassNames[i], clr);
                }
                subclasses.add(subclassNames[i]);
            }
        }
        return (Collection<String>)subclasses;
    }
    
    @Override
    public Collection<String> getSupportedOptions() {
        return (Collection<String>)Collections.EMPTY_SET;
    }
    
    @Override
    public boolean hasProperty(final String name) {
        return this.properties.containsKey(name.toLowerCase(Locale.ENGLISH)) || this.nucleusContext.getPersistenceConfiguration().hasProperty(name);
    }
    
    @Override
    public Object getProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getProperty(name);
        }
        return this.nucleusContext.getPersistenceConfiguration().getProperty(name);
    }
    
    @Override
    public int getIntProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getIntProperty(name);
        }
        return this.nucleusContext.getPersistenceConfiguration().getIntProperty(name);
    }
    
    @Override
    public String getStringProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getStringProperty(name);
        }
        return this.nucleusContext.getPersistenceConfiguration().getStringProperty(name);
    }
    
    @Override
    public boolean getBooleanProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getBooleanProperty(name);
        }
        return this.nucleusContext.getPersistenceConfiguration().getBooleanProperty(name);
    }
    
    @Override
    public boolean getBooleanProperty(final String name, final boolean resultIfNotSet) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getBooleanProperty(name, resultIfNotSet);
        }
        return this.nucleusContext.getPersistenceConfiguration().getBooleanProperty(name, resultIfNotSet);
    }
    
    @Override
    public Boolean getBooleanObjectProperty(final String name) {
        if (this.properties.containsKey(name.toLowerCase(Locale.ENGLISH))) {
            return super.getBooleanObjectProperty(name);
        }
        return this.nucleusContext.getPersistenceConfiguration().getBooleanObjectProperty(name);
    }
    
    @Override
    public void transactionStarted(final ExecutionContext ec) {
    }
    
    @Override
    public void transactionCommitted(final ExecutionContext ec) {
    }
    
    @Override
    public void transactionRolledBack(final ExecutionContext ec) {
    }
    
    public boolean usesBackedSCOWrappers() {
        return false;
    }
    
    @Override
    public boolean useBackedSCOWrapperForMember(final AbstractMemberMetaData mmd, final ExecutionContext ec) {
        return this.usesBackedSCOWrappers();
    }
    
    @Override
    public String getDefaultObjectProviderClassName() {
        return JDOStateManager.class.getName();
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
