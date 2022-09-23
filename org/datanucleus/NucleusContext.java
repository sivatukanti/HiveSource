// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import java.util.HashSet;
import org.datanucleus.identity.OID;
import org.datanucleus.cache.NullLevel2Cache;
import org.datanucleus.validation.BeanValidatorHandler;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import org.datanucleus.state.CallbackHandler;
import org.datanucleus.transaction.jta.JTASyncRegistryUnavailableException;
import org.datanucleus.transaction.NucleusTransactionException;
import org.datanucleus.transaction.jta.TransactionManagerFinder;
import org.datanucleus.enhancer.jdo.JDOImplementationCreator;
import org.datanucleus.metadata.TransactionType;
import java.util.TimeZone;
import org.datanucleus.plugin.ConfigurationElement;
import org.datanucleus.plugin.Extension;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.TransactionIsolationNotSupportedException;
import org.datanucleus.exceptions.NucleusException;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URI;
import java.io.File;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.store.schema.SchemaScriptAwareStoreManager;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import java.util.TreeSet;
import org.datanucleus.store.schema.SchemaTool;
import org.datanucleus.store.query.Query;
import org.datanucleus.metadata.QueryMetaData;
import org.datanucleus.metadata.QueryLanguage;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Iterator;
import org.datanucleus.store.exceptions.DatastoreInitialisationException;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.store.StoreData;
import org.datanucleus.util.StringUtils;
import java.util.Collection;
import org.datanucleus.state.ObjectProviderFactoryImpl;
import org.datanucleus.store.federation.FederatedStoreManager;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.api.ApiAdapterFactory;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.datanucleus.state.ObjectProviderFactory;
import java.util.List;
import org.datanucleus.identity.IdentityKeyTranslator;
import org.datanucleus.identity.IdentityStringTranslator;
import java.util.Random;
import org.datanucleus.management.FactoryStatistics;
import org.datanucleus.management.jmx.ManagementManager;
import java.util.Map;
import org.datanucleus.transaction.jta.JTASyncRegistry;
import org.datanucleus.transaction.TransactionManager;
import org.datanucleus.cache.Level2Cache;
import org.datanucleus.store.autostart.AutoStartMechanism;
import org.datanucleus.store.StoreManager;
import org.datanucleus.store.types.TypeManager;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.plugin.PluginManager;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class NucleusContext implements Serializable
{
    protected static final Localiser LOCALISER;
    private final ContextType type;
    private final PersistenceConfiguration config;
    private final PluginManager pluginManager;
    private MetaDataManager metaDataManager;
    private final String classLoaderResolverClassName;
    private final ApiAdapter apiAdapter;
    private TypeManager typeManager;
    private transient StoreManager storeMgr;
    private transient AutoStartMechanism starter;
    private boolean jca;
    private Level2Cache cache;
    private transient TransactionManager txManager;
    private transient javax.transaction.TransactionManager jtaTxManager;
    private transient JTASyncRegistry jtaSyncRegistry;
    private transient Map<String, ClassLoaderResolver> classLoaderResolverMap;
    private transient ManagementManager jmxManager;
    private transient FactoryStatistics statistics;
    public static final Random random;
    private Class datastoreIdentityClass;
    private IdentityStringTranslator idStringTranslator;
    private boolean idStringTranslatorInit;
    private IdentityKeyTranslator idKeyTranslator;
    private boolean idKeyTranslatorInit;
    private ImplementationCreator implCreator;
    private List<ExecutionContext.LifecycleListener> executionContextListeners;
    private transient FetchGroupManager fetchGrpMgr;
    private transient Object validatorFactory;
    private transient boolean validatorFactoryInit;
    private ExecutionContextPool ecPool;
    private ObjectProviderFactory opFactory;
    public static final Set<String> STARTUP_PROPERTIES;
    
    public NucleusContext(final String apiName, final Map startupProps) {
        this(apiName, ContextType.PERSISTENCE, startupProps);
    }
    
    public NucleusContext(final String apiName, final Map startupProps, final PluginManager pluginMgr) {
        this(apiName, ContextType.PERSISTENCE, startupProps, pluginMgr);
    }
    
    public NucleusContext(final String apiName, final ContextType type, final Map startupProps) {
        this(apiName, type, startupProps, null);
    }
    
    public NucleusContext(final String apiName, final ContextType type, final Map startupProps, final PluginManager pluginMgr) {
        this.metaDataManager = null;
        this.storeMgr = null;
        this.starter = null;
        this.jca = false;
        this.txManager = null;
        this.jtaTxManager = null;
        this.jtaSyncRegistry = null;
        this.classLoaderResolverMap = new HashMap<String, ClassLoaderResolver>();
        this.jmxManager = null;
        this.statistics = null;
        this.datastoreIdentityClass = null;
        this.idStringTranslator = null;
        this.idStringTranslatorInit = false;
        this.idKeyTranslator = null;
        this.idKeyTranslatorInit = false;
        this.executionContextListeners = new ArrayList<ExecutionContext.LifecycleListener>();
        this.validatorFactory = null;
        this.validatorFactoryInit = false;
        this.ecPool = null;
        this.opFactory = null;
        this.type = type;
        if (pluginMgr != null) {
            this.pluginManager = pluginMgr;
        }
        else {
            this.pluginManager = PluginManager.createPluginManager(startupProps, this.getClass().getClassLoader());
        }
        this.config = new PersistenceConfiguration(this);
        if (startupProps != null && !startupProps.isEmpty()) {
            this.config.setPersistenceProperties(startupProps);
        }
        final String clrName = this.config.getStringProperty("datanucleus.classLoaderResolverName");
        this.classLoaderResolverClassName = this.pluginManager.getAttributeValueForExtension("org.datanucleus.classloader_resolver", "name", clrName, "class-name");
        if (this.classLoaderResolverClassName == null) {
            throw new NucleusUserException(NucleusContext.LOCALISER.msg("001001", clrName)).setFatal();
        }
        if (apiName != null) {
            this.apiAdapter = ApiAdapterFactory.getInstance().getApiAdapter(apiName, this.pluginManager);
            this.config.setDefaultProperties(this.apiAdapter.getDefaultFactoryProperties());
        }
        else {
            this.apiAdapter = null;
        }
    }
    
    public synchronized void initialise() {
        if (this.type == ContextType.PERSISTENCE) {
            final ClassLoaderResolver clr = this.getClassLoaderResolver(null);
            clr.registerUserClassLoader((ClassLoader)this.config.getProperty("datanucleus.primaryClassLoader"));
            boolean generateSchema = false;
            boolean generateScripts = false;
            String generateModeStr = this.config.getStringProperty("datanucleus.generateSchema.database.mode");
            if (generateModeStr == null || generateModeStr.equalsIgnoreCase("none")) {
                generateModeStr = this.config.getStringProperty("datanucleus.generateSchema.scripts.mode");
                generateScripts = true;
            }
            if (generateModeStr != null && !generateModeStr.equalsIgnoreCase("none")) {
                generateSchema = true;
                if (!this.config.getBooleanProperty("datanucleus.autoCreateSchema")) {
                    this.config.setProperty("datanucleus.autoCreateSchema", "true");
                }
                if (!this.config.getBooleanProperty("datanucleus.autoCreateTables")) {
                    this.config.setProperty("datanucleus.autoCreateTables", "true");
                }
                if (!this.config.getBooleanProperty("datanucleus.autoCreateColumns")) {
                    this.config.setProperty("datanucleus.autoCreateColumns", "true");
                }
                if (!this.config.getBooleanProperty("datanucleus.autoCreateConstraints")) {
                    this.config.setProperty("datanucleus.autoCreateConstraints", "true");
                }
                if (!this.config.getBooleanProperty("datanucleus.fixedDatastore")) {
                    this.config.setProperty("datanucleus.fixedDatastore", "false");
                }
                if (!this.config.getBooleanProperty("datanucleus.readOnlyDatastore")) {
                    this.config.setProperty("datanucleus.readOnlyDatastore", "false");
                }
            }
            final Set<String> propNamesWithDatastore = this.config.getPropertyNamesWithPrefix("datanucleus.datastore.");
            if (propNamesWithDatastore == null) {
                NucleusLogger.DATASTORE.debug("Creating StoreManager for datastore");
                final Map<String, Object> datastoreProps = this.config.getDatastoreProperties();
                this.storeMgr = createStoreManagerForProperties(this.config.getPersistenceProperties(), datastoreProps, clr, this);
                final String transactionIsolation = this.config.getStringProperty("datanucleus.transactionIsolation");
                if (transactionIsolation != null) {
                    final String reqdIsolation = getTransactionIsolationForStoreManager(this.storeMgr, transactionIsolation);
                    if (!transactionIsolation.equalsIgnoreCase(reqdIsolation)) {
                        this.config.setProperty("datanucleus.transactionIsolation", reqdIsolation);
                    }
                }
            }
            else {
                NucleusLogger.DATASTORE.debug("Creating FederatedStoreManager to handle federation of primary StoreManager and " + propNamesWithDatastore.size() + " secondary datastores");
                this.storeMgr = new FederatedStoreManager(clr, this);
            }
            NucleusLogger.DATASTORE.debug("StoreManager now created");
            if (this.config.getStringProperty("datanucleus.autoStartMechanism") != null) {
                this.initialiseAutoStart(clr);
            }
            if (generateSchema) {
                this.initialiseSchema(generateModeStr, generateScripts);
            }
            if (this.config.getStringProperty("datanucleus.PersistenceUnitName") != null && this.config.getBooleanProperty("datanucleus.persistenceUnitLoadClasses")) {
                final Collection<String> loadedClasses = this.getMetaDataManager().getClassesWithMetaData();
                this.storeMgr.addClasses(loadedClasses.toArray(new String[loadedClasses.size()]), clr);
            }
            if (this.config.getBooleanProperty("datanucleus.query.compileNamedQueriesAtStartup")) {
                this.initialiseNamedQueries(clr);
            }
            if (this.ecPool == null) {
                this.ecPool = new ExecutionContextPool(this);
            }
            if (this.opFactory == null) {
                this.opFactory = new ObjectProviderFactoryImpl(this);
            }
        }
        this.logConfiguration();
    }
    
    protected void initialiseAutoStart(final ClassLoaderResolver clr) throws DatastoreInitialisationException {
        final String autoStartMechanism = this.config.getStringProperty("datanucleus.autoStartMechanism");
        final String autoStarterClassName = this.getPluginManager().getAttributeValueForExtension("org.datanucleus.autostart", "name", autoStartMechanism, "class-name");
        if (autoStarterClassName != null) {
            final String mode = this.config.getStringProperty("datanucleus.autoStartMechanismMode");
            final Class[] argsClass = { ClassConstants.STORE_MANAGER, ClassConstants.CLASS_LOADER_RESOLVER };
            final Object[] args = { this.storeMgr, clr };
            try {
                this.starter = (AutoStartMechanism)this.getPluginManager().createExecutableExtension("org.datanucleus.autostart", "name", autoStartMechanism, "class-name", argsClass, args);
                if (mode.equalsIgnoreCase("None")) {
                    this.starter.setMode(AutoStartMechanism.Mode.NONE);
                }
                else if (mode.equalsIgnoreCase("Checked")) {
                    this.starter.setMode(AutoStartMechanism.Mode.CHECKED);
                }
                else if (mode.equalsIgnoreCase("Quiet")) {
                    this.starter.setMode(AutoStartMechanism.Mode.QUIET);
                }
                else if (mode.equalsIgnoreCase("Ignored")) {
                    this.starter.setMode(AutoStartMechanism.Mode.IGNORED);
                }
            }
            catch (Exception e) {
                NucleusLogger.PERSISTENCE.error(StringUtils.getStringFromStackTrace(e));
            }
        }
        if (this.starter == null) {
            return;
        }
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug(NucleusContext.LOCALISER.msg("034005", autoStartMechanism));
        }
        boolean illegalState = false;
        try {
            if (!this.starter.isOpen()) {
                this.starter.open();
            }
            final Collection existingData = this.starter.getAllClassData();
            if (existingData != null && existingData.size() > 0) {
                final List classesNeedingAdding = new ArrayList();
                for (final StoreData data : existingData) {
                    if (data.isFCO()) {
                        Class classFound = null;
                        try {
                            classFound = clr.classForName(data.getName());
                        }
                        catch (ClassNotResolvedException cnre) {
                            if (data.getInterfaceName() != null) {
                                try {
                                    this.getImplementationCreator().newInstance(clr.classForName(data.getInterfaceName()), clr);
                                    classFound = clr.classForName(data.getName());
                                }
                                catch (ClassNotResolvedException ex) {}
                            }
                        }
                        if (classFound != null) {
                            NucleusLogger.PERSISTENCE.info(NucleusContext.LOCALISER.msg("032003", data.getName()));
                            classesNeedingAdding.add(data.getName());
                            if (data.getMetaData() != null) {
                                continue;
                            }
                            final AbstractClassMetaData acmd = this.getMetaDataManager().getMetaDataForClass(classFound, clr);
                            if (acmd != null) {
                                data.setMetaData(acmd);
                            }
                            else {
                                final String msg = NucleusContext.LOCALISER.msg("034004", data.getName());
                                if (this.starter.getMode() == AutoStartMechanism.Mode.CHECKED) {
                                    NucleusLogger.PERSISTENCE.error(msg);
                                    throw new DatastoreInitialisationException(msg);
                                }
                                if (this.starter.getMode() == AutoStartMechanism.Mode.IGNORED) {
                                    NucleusLogger.PERSISTENCE.warn(msg);
                                }
                                else {
                                    if (this.starter.getMode() != AutoStartMechanism.Mode.QUIET) {
                                        continue;
                                    }
                                    NucleusLogger.PERSISTENCE.warn(msg);
                                    NucleusLogger.PERSISTENCE.warn(NucleusContext.LOCALISER.msg("034001", data.getName()));
                                    this.starter.deleteClass(data.getName());
                                }
                            }
                        }
                        else {
                            final String msg2 = NucleusContext.LOCALISER.msg("034000", data.getName());
                            if (this.starter.getMode() == AutoStartMechanism.Mode.CHECKED) {
                                NucleusLogger.PERSISTENCE.error(msg2);
                                throw new DatastoreInitialisationException(msg2);
                            }
                            if (this.starter.getMode() == AutoStartMechanism.Mode.IGNORED) {
                                NucleusLogger.PERSISTENCE.warn(msg2);
                            }
                            else {
                                if (this.starter.getMode() != AutoStartMechanism.Mode.QUIET) {
                                    continue;
                                }
                                NucleusLogger.PERSISTENCE.warn(msg2);
                                NucleusLogger.PERSISTENCE.warn(NucleusContext.LOCALISER.msg("034001", data.getName()));
                                this.starter.deleteClass(data.getName());
                            }
                        }
                    }
                }
                final String[] classesToLoad = new String[classesNeedingAdding.size()];
                final Iterator classesNeedingAddingIter = classesNeedingAdding.iterator();
                int n = 0;
                while (classesNeedingAddingIter.hasNext()) {
                    classesToLoad[n++] = classesNeedingAddingIter.next();
                }
                try {
                    this.storeMgr.addClasses(classesToLoad, clr);
                }
                catch (Exception e2) {
                    NucleusLogger.PERSISTENCE.warn(NucleusContext.LOCALISER.msg("034002", e2));
                    illegalState = true;
                }
            }
        }
        finally {
            if (this.starter.isOpen()) {
                this.starter.close();
            }
            if (illegalState) {
                NucleusLogger.PERSISTENCE.warn(NucleusContext.LOCALISER.msg("034003"));
                this.starter = null;
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(NucleusContext.LOCALISER.msg("034006", autoStartMechanism));
            }
        }
    }
    
    public AutoStartMechanism getAutoStartMechanism() {
        return this.starter;
    }
    
    protected void initialiseNamedQueries(final ClassLoaderResolver clr) {
        final MetaDataManager mmgr = this.getMetaDataManager();
        final Set<String> queryNames = mmgr.getNamedQueryNames();
        if (queryNames != null) {
            final ExecutionContext ec = this.getExecutionContext(null, null);
            for (final String queryName : queryNames) {
                final QueryMetaData qmd = mmgr.getMetaDataForQuery(null, clr, queryName);
                if (qmd.getLanguage().equals(QueryLanguage.JPQL.toString()) || qmd.getLanguage().equals(QueryLanguage.JDOQL.toString())) {
                    if (NucleusLogger.QUERY.isDebugEnabled()) {
                        NucleusLogger.QUERY.debug(NucleusContext.LOCALISER.msg("008017", queryName, qmd.getQuery()));
                    }
                    final Query q = this.storeMgr.getQueryManager().newQuery(qmd.getLanguage().toString(), ec, qmd.getQuery());
                    q.compile();
                    q.closeAll();
                }
            }
            ec.close();
        }
    }
    
    protected void initialiseSchema(final String generateModeStr, final boolean generateScripts) {
        SchemaTool.Mode mode = null;
        if (generateModeStr.equalsIgnoreCase("create")) {
            mode = SchemaTool.Mode.CREATE;
        }
        else if (generateModeStr.equalsIgnoreCase("drop")) {
            mode = SchemaTool.Mode.DELETE;
        }
        else if (generateModeStr.equalsIgnoreCase("drop-and-create")) {
            mode = SchemaTool.Mode.DELETE_CREATE;
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            if (mode == SchemaTool.Mode.CREATE) {
                NucleusLogger.DATASTORE_SCHEMA.debug(NucleusContext.LOCALISER.msg("014000"));
            }
            else if (mode == SchemaTool.Mode.DELETE) {
                NucleusLogger.DATASTORE_SCHEMA.debug(NucleusContext.LOCALISER.msg("014001"));
            }
            else if (mode == SchemaTool.Mode.DELETE_CREATE) {
                NucleusLogger.DATASTORE_SCHEMA.debug(NucleusContext.LOCALISER.msg("014045"));
            }
        }
        Set<String> schemaClassNames = null;
        final MetaDataManager metaDataMgr = this.getMetaDataManager();
        final FileMetaData[] filemds = metaDataMgr.getFileMetaData();
        schemaClassNames = new TreeSet<String>();
        if (filemds == null) {
            throw new NucleusUserException("No classes to process in generateSchema");
        }
        for (int i = 0; i < filemds.length; ++i) {
            for (int j = 0; j < filemds[i].getNoOfPackages(); ++j) {
                for (int k = 0; k < filemds[i].getPackage(j).getNoOfClasses(); ++k) {
                    final String className = filemds[i].getPackage(j).getClass(k).getFullClassName();
                    if (!schemaClassNames.contains(className)) {
                        schemaClassNames.add(className);
                    }
                }
            }
        }
        final StoreManager storeMgr = this.getStoreManager();
        if (storeMgr instanceof SchemaAwareStoreManager) {
            final SchemaAwareStoreManager schemaStoreMgr = (SchemaAwareStoreManager)storeMgr;
            final SchemaTool schemaTool = new SchemaTool();
            if (mode == SchemaTool.Mode.CREATE) {
                final String createScript = this.config.getStringProperty("datanucleus.generateSchema.scripts.create.source");
                if (!StringUtils.isWhitespace(createScript)) {
                    final String scriptContent = this.getDatastoreScriptForResourceName(createScript);
                    NucleusLogger.DATASTORE_SCHEMA.debug(">> createScript=" + scriptContent);
                    if (storeMgr instanceof SchemaScriptAwareStoreManager && !StringUtils.isWhitespace(scriptContent)) {
                        ((SchemaScriptAwareStoreManager)storeMgr).executeScript(scriptContent);
                    }
                }
                if (generateScripts) {
                    schemaTool.setDdlFile(this.config.getStringProperty("datanucleus.generateSchema.scripts.create.target"));
                }
                schemaTool.createSchema(schemaStoreMgr, schemaClassNames);
            }
            else if (mode == SchemaTool.Mode.DELETE) {
                final String dropScript = this.config.getStringProperty("datanucleus.generateSchema.scripts.drop.source");
                if (!StringUtils.isWhitespace(dropScript)) {
                    final String scriptContent = this.getDatastoreScriptForResourceName(dropScript);
                    NucleusLogger.DATASTORE_SCHEMA.debug(">> dropScript=" + scriptContent);
                    if (storeMgr instanceof SchemaScriptAwareStoreManager && !StringUtils.isWhitespace(scriptContent)) {
                        ((SchemaScriptAwareStoreManager)storeMgr).executeScript(scriptContent);
                    }
                }
                if (generateScripts) {
                    schemaTool.setDdlFile(this.config.getStringProperty("datanucleus.generateSchema.scripts.drop.target"));
                }
                schemaTool.deleteSchema(schemaStoreMgr, schemaClassNames);
            }
            else if (mode == SchemaTool.Mode.DELETE_CREATE) {
                final String dropScript = this.config.getStringProperty("datanucleus.generateSchema.scripts.drop.source");
                if (!StringUtils.isWhitespace(dropScript)) {
                    final String scriptContent = this.getDatastoreScriptForResourceName(dropScript);
                    NucleusLogger.DATASTORE_SCHEMA.debug(">> dropScript=" + scriptContent);
                    if (storeMgr instanceof SchemaScriptAwareStoreManager && !StringUtils.isWhitespace(scriptContent)) {
                        ((SchemaScriptAwareStoreManager)storeMgr).executeScript(scriptContent);
                    }
                }
                if (generateScripts) {
                    schemaTool.setDdlFile(this.config.getStringProperty("datanucleus.generateSchema.scripts.drop.target"));
                }
                schemaTool.deleteSchema(schemaStoreMgr, schemaClassNames);
                final String createScript2 = this.config.getStringProperty("datanucleus.generateSchema.scripts.create.source");
                if (!StringUtils.isWhitespace(createScript2)) {
                    final String scriptContent2 = this.getDatastoreScriptForResourceName(createScript2);
                    NucleusLogger.DATASTORE_SCHEMA.debug(">> dropScript=" + scriptContent2);
                    if (storeMgr instanceof SchemaScriptAwareStoreManager && !StringUtils.isWhitespace(scriptContent2)) {
                        ((SchemaScriptAwareStoreManager)storeMgr).executeScript(scriptContent2);
                    }
                }
                if (generateScripts) {
                    schemaTool.setDdlFile(this.config.getStringProperty("datanucleus.generateSchema.scripts.create.target"));
                }
                schemaTool.createSchema(schemaStoreMgr, schemaClassNames);
            }
            final String loadScript = this.config.getStringProperty("datanucleus.generateSchema.scripts.load");
            if (!StringUtils.isWhitespace(loadScript)) {
                final String scriptContent = this.getDatastoreScriptForResourceName(loadScript);
                NucleusLogger.DATASTORE_SCHEMA.debug(">> loadScript=" + scriptContent);
                if (storeMgr instanceof SchemaScriptAwareStoreManager && !StringUtils.isWhitespace(scriptContent)) {
                    ((SchemaScriptAwareStoreManager)storeMgr).executeScript(scriptContent);
                }
            }
        }
        else if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(NucleusContext.LOCALISER.msg("008016", StringUtils.toJVMIDString(storeMgr)));
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(NucleusContext.LOCALISER.msg("014043"));
        }
    }
    
    private String getDatastoreScriptForResourceName(final String scriptResourceName) {
        if (StringUtils.isWhitespace(scriptResourceName)) {
            return null;
        }
        File file = new File(scriptResourceName);
        if (!file.exists()) {
            try {
                final URI uri = new URI(scriptResourceName);
                file = new File(uri);
            }
            catch (Exception ex) {}
        }
        if (file != null && file.exists()) {
            FileInputStream fis = null;
            try {
                final StringBuffer str = new StringBuffer();
                fis = new FileInputStream(file);
                int content;
                while ((content = fis.read()) != -1) {
                    str.append((char)content);
                }
                return str.toString();
            }
            catch (Exception e) {}
            finally {
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (IOException ex2) {}
                }
            }
        }
        NucleusLogger.DATASTORE_SCHEMA.info("Datastore script " + scriptResourceName + " was not a valid script; has to be a filename, or a URL string");
        return null;
    }
    
    public synchronized void close() {
        if (this.opFactory != null) {
            this.opFactory.close();
            this.opFactory = null;
        }
        if (this.ecPool != null) {
            this.ecPool.cleanUp();
            this.ecPool = null;
        }
        if (this.fetchGrpMgr != null) {
            this.fetchGrpMgr.clearFetchGroups();
        }
        if (this.storeMgr != null) {
            this.storeMgr.close();
            this.storeMgr = null;
        }
        if (this.metaDataManager != null) {
            this.metaDataManager.close();
            this.metaDataManager = null;
        }
        if (this.statistics != null) {
            if (this.jmxManager != null) {
                this.jmxManager.deregisterMBean(this.statistics.getRegisteredName());
            }
            this.statistics = null;
        }
        if (this.jmxManager != null) {
            this.jmxManager.close();
            this.jmxManager = null;
        }
        if (this.cache != null) {
            this.cache.close();
            NucleusLogger.CACHE.debug(NucleusContext.LOCALISER.msg("004009"));
        }
        this.classLoaderResolverMap.clear();
        this.classLoaderResolverMap = null;
        this.datastoreIdentityClass = null;
    }
    
    public ExecutionContextPool getExecutionContextPool() {
        if (this.ecPool == null) {
            this.initialise();
        }
        return this.ecPool;
    }
    
    public ContextType getType() {
        return this.type;
    }
    
    public ApiAdapter getApiAdapter() {
        return this.apiAdapter;
    }
    
    public String getApiName() {
        return (this.apiAdapter != null) ? this.apiAdapter.getName() : null;
    }
    
    public PersistenceConfiguration getPersistenceConfiguration() {
        return this.config;
    }
    
    public PluginManager getPluginManager() {
        return this.pluginManager;
    }
    
    public synchronized MetaDataManager getMetaDataManager() {
        if (this.metaDataManager == null) {
            final String apiName = this.getApiName();
            try {
                this.metaDataManager = (MetaDataManager)this.getPluginManager().createExecutableExtension("org.datanucleus.metadata_manager", new String[] { "name" }, new String[] { apiName }, "class", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this });
            }
            catch (Exception e) {
                throw new NucleusException(NucleusContext.LOCALISER.msg("008010", apiName, e.getMessage()), e);
            }
            if (this.metaDataManager == null) {
                throw new NucleusException(NucleusContext.LOCALISER.msg("008009", apiName));
            }
        }
        return this.metaDataManager;
    }
    
    public TypeManager getTypeManager() {
        if (this.typeManager == null) {
            this.typeManager = new TypeManager(this);
        }
        return this.typeManager;
    }
    
    public ObjectProviderFactory getObjectProviderFactory() {
        if (this.opFactory == null) {
            this.initialise();
        }
        return this.opFactory;
    }
    
    public ExecutionContext getExecutionContext(final Object owner, final Map<String, Object> options) {
        return this.ecPool.checkOut(owner, options);
    }
    
    public ClassLoaderResolver getClassLoaderResolver(final ClassLoader primaryLoader) {
        String key;
        final String resolverName = key = this.config.getStringProperty("datanucleus.classLoaderResolverName");
        if (primaryLoader != null) {
            key = key + ":[" + StringUtils.toJVMIDString(primaryLoader) + "]";
        }
        if (this.classLoaderResolverMap == null) {
            this.classLoaderResolverMap = new HashMap<String, ClassLoaderResolver>();
        }
        ClassLoaderResolver clr = this.classLoaderResolverMap.get(key);
        if (clr != null) {
            return clr;
        }
        try {
            clr = (ClassLoaderResolver)this.pluginManager.createExecutableExtension("org.datanucleus.classloader_resolver", "name", resolverName, "class-name", new Class[] { ClassLoader.class }, new Object[] { primaryLoader });
            clr.registerUserClassLoader((ClassLoader)this.config.getProperty("datanucleus.primaryClassLoader"));
        }
        catch (ClassNotFoundException cnfe) {
            throw new NucleusUserException(NucleusContext.LOCALISER.msg("001002", this.classLoaderResolverClassName), cnfe).setFatal();
        }
        catch (Exception e) {
            throw new NucleusUserException(NucleusContext.LOCALISER.msg("001003", this.classLoaderResolverClassName), e).setFatal();
        }
        this.classLoaderResolverMap.put(key, clr);
        return clr;
    }
    
    public static String getTransactionIsolationForStoreManager(final StoreManager storeMgr, final String transactionIsolation) {
        if (transactionIsolation != null) {
            final Collection srmOptions = storeMgr.getSupportedOptions();
            if (!srmOptions.contains("TransactionIsolationLevel." + transactionIsolation)) {
                if (transactionIsolation.equals("read-uncommitted")) {
                    if (srmOptions.contains("TransactionIsolationLevel.read-committed")) {
                        return "read-committed";
                    }
                    if (srmOptions.contains("TransactionIsolationLevel.repeatable-read")) {
                        return "repeatable-read";
                    }
                    if (srmOptions.contains("TransactionIsolationLevel.serializable")) {
                        return "serializable";
                    }
                }
                else if (transactionIsolation.equals("read-committed")) {
                    if (srmOptions.contains("TransactionIsolationLevel.repeatable-read")) {
                        return "repeatable-read";
                    }
                    if (srmOptions.contains("TransactionIsolationLevel.serializable")) {
                        return "serializable";
                    }
                }
                else {
                    if (!transactionIsolation.equals("repeatable-read")) {
                        throw new TransactionIsolationNotSupportedException(transactionIsolation);
                    }
                    if (srmOptions.contains("TransactionIsolationLevel.serializable")) {
                        return "serializable";
                    }
                }
            }
        }
        return transactionIsolation;
    }
    
    public static StoreManager createStoreManagerForProperties(final Map<String, Object> props, final Map<String, Object> datastoreProps, final ClassLoaderResolver clr, final NucleusContext nucCtx) {
        final Extension[] exts = nucCtx.getPluginManager().getExtensionPoint("org.datanucleus.store_manager").getExtensions();
        final Class[] ctrArgTypes = { ClassConstants.CLASS_LOADER_RESOLVER, ClassConstants.NUCLEUS_CONTEXT, Map.class };
        final Object[] ctrArgs = { clr, nucCtx, datastoreProps };
        StoreManager storeMgr = null;
        final String storeManagerType = props.get("datanucleus.storeManagerType".toLowerCase());
        if (storeManagerType != null) {
            for (int e = 0; storeMgr == null && e < exts.length; ++e) {
                final ConfigurationElement[] confElm = exts[e].getConfigurationElements();
                for (int c = 0; storeMgr == null && c < confElm.length; ++c) {
                    final String key = confElm[c].getAttribute("key");
                    if (key.equalsIgnoreCase(storeManagerType)) {
                        try {
                            storeMgr = (StoreManager)nucCtx.getPluginManager().createExecutableExtension("org.datanucleus.store_manager", "key", storeManagerType, "class-name", ctrArgTypes, ctrArgs);
                        }
                        catch (InvocationTargetException ex) {
                            final Throwable t = ex.getTargetException();
                            if (t instanceof RuntimeException) {
                                throw (RuntimeException)t;
                            }
                            if (t instanceof Error) {
                                throw (Error)t;
                            }
                            throw new NucleusException(t.getMessage(), t).setFatal();
                        }
                        catch (Exception ex2) {
                            throw new NucleusException(ex2.getMessage(), ex2).setFatal();
                        }
                    }
                }
            }
            if (storeMgr == null) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("008004", storeManagerType)).setFatal();
            }
        }
        if (storeMgr == null) {
            String url = props.get("datanucleus.connectionurl");
            if (url != null) {
                final int idx = url.indexOf(58);
                if (idx > -1) {
                    url = url.substring(0, idx);
                }
            }
            for (int e2 = 0; storeMgr == null && e2 < exts.length; ++e2) {
                final ConfigurationElement[] confElm2 = exts[e2].getConfigurationElements();
                for (int c2 = 0; storeMgr == null && c2 < confElm2.length; ++c2) {
                    final String urlKey = confElm2[c2].getAttribute("url-key");
                    if (url != null) {
                        if (!urlKey.equalsIgnoreCase(url)) {
                            continue;
                        }
                    }
                    try {
                        storeMgr = (StoreManager)nucCtx.getPluginManager().createExecutableExtension("org.datanucleus.store_manager", "url-key", (url == null) ? urlKey : url, "class-name", ctrArgTypes, ctrArgs);
                    }
                    catch (InvocationTargetException ex3) {
                        final Throwable t2 = ex3.getTargetException();
                        if (t2 instanceof RuntimeException) {
                            throw (RuntimeException)t2;
                        }
                        if (t2 instanceof Error) {
                            throw (Error)t2;
                        }
                        throw new NucleusException(t2.getMessage(), t2).setFatal();
                    }
                    catch (Exception ex4) {
                        throw new NucleusException(ex4.getMessage(), ex4).setFatal();
                    }
                }
            }
            if (storeMgr == null) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("008004", url)).setFatal();
            }
        }
        return storeMgr;
    }
    
    protected void logConfiguration() {
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug("================= NucleusContext ===============");
            NucleusLogger.PERSISTENCE.debug(NucleusContext.LOCALISER.msg("008000", this.pluginManager.getVersionForBundle("org.datanucleus"), System.getProperty("java.version"), System.getProperty("os.name")));
            NucleusLogger.PERSISTENCE.debug("Persistence API : " + this.getApiName());
            if (this.config.hasPropertyNotNull("datanucleus.PersistenceUnitName")) {
                NucleusLogger.PERSISTENCE.debug("Persistence-Unit : " + this.config.getStringProperty("datanucleus.PersistenceUnitName"));
            }
            NucleusLogger.PERSISTENCE.debug("Plugin Registry : " + this.pluginManager.getRegistryClassName());
            final Object primCL = this.config.getProperty("datanucleus.primaryClassLoader");
            NucleusLogger.PERSISTENCE.debug("ClassLoading : " + this.config.getStringProperty("datanucleus.classLoaderResolverName") + ((primCL != null) ? ("primary=" + primCL) : ""));
            if (this.type == ContextType.PERSISTENCE) {
                String timeZoneID = this.config.getStringProperty("datanucleus.ServerTimeZoneID");
                if (timeZoneID == null) {
                    timeZoneID = TimeZone.getDefault().getID();
                }
                NucleusLogger.PERSISTENCE.debug("Persistence : " + (this.config.getBooleanProperty("datanucleus.Multithreaded") ? "pm-multithreaded" : "pm-singlethreaded") + (this.config.getBooleanProperty("datanucleus.RetainValues") ? ", retain-values" : "") + (this.config.getBooleanProperty("datanucleus.RestoreValues") ? ", restore-values" : "") + (this.config.getBooleanProperty("datanucleus.NontransactionalRead") ? ", nontransactional-read" : "") + (this.config.getBooleanProperty("datanucleus.NontransactionalWrite") ? ", nontransactional-write" : "") + (this.config.getBooleanProperty("datanucleus.persistenceByReachabilityAtCommit") ? ", reachability-at-commit" : "") + (this.config.getBooleanProperty("datanucleus.DetachAllOnCommit") ? ", detach-all-on-commit" : "") + (this.config.getBooleanProperty("datanucleus.DetachAllOnRollback") ? ", detach-all-on-rollback" : "") + (this.config.getBooleanProperty("datanucleus.DetachOnClose") ? ", detach-on-close" : "") + (this.config.getBooleanProperty("datanucleus.CopyOnAttach") ? ", copy-on-attach" : "") + (this.config.getBooleanProperty("datanucleus.manageRelationships") ? (this.config.getBooleanProperty("datanucleus.manageRelationshipsChecks") ? ", managed-relations(checked)" : ", managed-relations(unchecked)") : "") + ", deletion-policy=" + this.config.getStringProperty("datanucleus.deletionPolicy") + (this.config.getBooleanProperty("datanucleus.IgnoreCache") ? ", ignoreCache" : "") + ", serverTimeZone=" + timeZoneID);
                String txnType = "RESOURCE_LOCAL";
                if (TransactionType.JTA.toString().equalsIgnoreCase(this.config.getStringProperty("datanucleus.TransactionType"))) {
                    if (this.isJcaMode()) {
                        txnType = "JTA (via JCA adapter)";
                    }
                    else {
                        txnType = "JTA";
                    }
                }
                final String autoStartMechanism = this.config.getStringProperty("datanucleus.autoStartMechanism");
                if (autoStartMechanism != null) {
                    final String autoStartClassNames = this.config.getStringProperty("datanucleus.autoStartClassNames");
                    NucleusLogger.PERSISTENCE.debug("AutoStart : mechanism=" + autoStartMechanism + ", mode=" + this.config.getStringProperty("datanucleus.autoStartMechanismMode") + ((autoStartClassNames != null) ? (", classes=" + autoStartClassNames) : ""));
                }
                NucleusLogger.PERSISTENCE.debug("Transactions : type=" + txnType + ", mode=" + (this.config.getBooleanProperty("datanucleus.Optimistic") ? "optimistic" : "datastore") + ", isolation=" + this.config.getStringProperty("datanucleus.transactionIsolation"));
                NucleusLogger.PERSISTENCE.debug("ValueGeneration : txn-isolation=" + this.config.getStringProperty("datanucleus.valuegeneration.transactionIsolation") + " connection=" + (this.config.getStringProperty("datanucleus.valuegeneration.transactionAttribute").equalsIgnoreCase("New") ? "New" : "Existing"));
                NucleusLogger.PERSISTENCE.debug("Cache : Level1 (" + this.config.getStringProperty("datanucleus.cache.level1.type") + ")" + ", Level2 (" + this.config.getStringProperty("datanucleus.cache.level2.type") + ", mode=" + this.config.getStringProperty("datanucleus.cache.level2.mode") + ")" + ", QueryResults (" + this.config.getStringProperty("datanucleus.cache.queryResults.type") + ")" + (this.config.getBooleanProperty("datanucleus.cache.collections") ? ", Collections/Maps " : ""));
            }
            NucleusLogger.PERSISTENCE.debug("================================================");
        }
    }
    
    public synchronized Class getDatastoreIdentityClass() {
        if (this.datastoreIdentityClass == null) {
            final String dsidName = this.config.getStringProperty("datanucleus.datastoreIdentityType");
            final String datastoreIdentityClassName = this.pluginManager.getAttributeValueForExtension("org.datanucleus.store_datastoreidentity", "name", dsidName, "class-name");
            if (datastoreIdentityClassName == null) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("002001", dsidName)).setFatal();
            }
            final ClassLoaderResolver clr = this.getClassLoaderResolver(null);
            try {
                this.datastoreIdentityClass = clr.classForName(datastoreIdentityClassName, ClassConstants.NUCLEUS_CONTEXT_LOADER);
            }
            catch (ClassNotResolvedException cnre) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("002002", dsidName, datastoreIdentityClassName)).setFatal();
            }
        }
        return this.datastoreIdentityClass;
    }
    
    public synchronized IdentityStringTranslator getIdentityStringTranslator() {
        if (this.idStringTranslatorInit) {
            return this.idStringTranslator;
        }
        this.idStringTranslatorInit = true;
        final String translatorType = this.config.getStringProperty("datanucleus.identityStringTranslatorType");
        if (translatorType != null) {
            try {
                return this.idStringTranslator = (IdentityStringTranslator)this.pluginManager.createExecutableExtension("org.datanucleus.identity_string_translator", "name", translatorType, "class-name", null, null);
            }
            catch (Exception e) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("002001", translatorType)).setFatal();
            }
        }
        return null;
    }
    
    public synchronized IdentityKeyTranslator getIdentityKeyTranslator() {
        if (this.idKeyTranslatorInit) {
            return this.idKeyTranslator;
        }
        this.idKeyTranslatorInit = true;
        final String translatorType = this.config.getStringProperty("datanucleus.identityKeyTranslatorType");
        if (translatorType != null) {
            try {
                return this.idKeyTranslator = (IdentityKeyTranslator)this.pluginManager.createExecutableExtension("org.datanucleus.identity_key_translator", "name", translatorType, "class-name", null, null);
            }
            catch (Exception e) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("002001", translatorType)).setFatal();
            }
        }
        return null;
    }
    
    public boolean statisticsEnabled() {
        return this.config.getBooleanProperty("datanucleus.enableStatistics") || this.getJMXManager() != null;
    }
    
    public synchronized ManagementManager getJMXManager() {
        if (this.jmxManager == null && this.config.getStringProperty("datanucleus.jmxType") != null) {
            this.jmxManager = new ManagementManager(this);
        }
        return this.jmxManager;
    }
    
    public synchronized FactoryStatistics getStatistics() {
        if (this.statistics == null && this.statisticsEnabled()) {
            String name = null;
            if (this.getJMXManager() != null) {
                name = this.jmxManager.getDomainName() + ":InstanceName=" + this.jmxManager.getInstanceName() + ",Type=" + FactoryStatistics.class.getName() + ",Name=Factory" + NucleusContext.random.nextInt();
            }
            this.statistics = new FactoryStatistics(name);
            if (this.jmxManager != null) {
                this.jmxManager.registerMBean(this.statistics, name);
            }
        }
        return this.statistics;
    }
    
    public synchronized ImplementationCreator getImplementationCreator() {
        if (this.implCreator == null) {
            final boolean useImplCreator = this.config.getBooleanProperty("datanucleus.useImplementationCreator");
            if (useImplCreator) {
                this.implCreator = new JDOImplementationCreator(this.getMetaDataManager());
            }
        }
        return this.implCreator;
    }
    
    public synchronized TransactionManager getTransactionManager() {
        if (this.txManager == null) {
            this.txManager = new TransactionManager();
        }
        return this.txManager;
    }
    
    public synchronized javax.transaction.TransactionManager getJtaTransactionManager() {
        if (this.jtaTxManager == null) {
            this.jtaTxManager = new TransactionManagerFinder(this).getTransactionManager(this.getClassLoaderResolver((ClassLoader)this.config.getProperty("datanucleus.primaryClassLoader")));
            if (this.jtaTxManager == null) {
                throw new NucleusTransactionException(NucleusContext.LOCALISER.msg("015030"));
            }
        }
        return this.jtaTxManager;
    }
    
    public JTASyncRegistry getJtaSyncRegistry() {
        if (this.jtaSyncRegistry == null) {
            try {
                this.jtaSyncRegistry = new JTASyncRegistry();
            }
            catch (JTASyncRegistryUnavailableException jsrue) {
                NucleusLogger.TRANSACTION.debug("JTA TransactionSynchronizationRegistry not found at JNDI java:comp/TransactionSynchronizationRegistry so using Transaction to register synchronisation");
                this.jtaSyncRegistry = null;
            }
        }
        return this.jtaSyncRegistry;
    }
    
    public boolean isStoreManagerInitialised() {
        return this.storeMgr != null;
    }
    
    public StoreManager getStoreManager() {
        if (this.storeMgr == null) {
            this.initialise();
        }
        return this.storeMgr;
    }
    
    public CallbackHandler getValidationHandler(final ExecutionContext ec) {
        if (this.validatorFactoryInit && this.validatorFactory == null) {
            return null;
        }
        if (this.config.hasPropertyNotNull("datanucleus.validation.mode") && this.config.getStringProperty("datanucleus.validation.mode").equalsIgnoreCase("none")) {
            this.validatorFactoryInit = true;
            return null;
        }
        try {
            ec.getClassLoaderResolver().classForName("javax.validation.Validation");
        }
        catch (ClassNotResolvedException cnre) {
            this.validatorFactoryInit = true;
            return null;
        }
        try {
            if (this.validatorFactory == null) {
                this.validatorFactoryInit = true;
                if (this.config.hasPropertyNotNull("datanucleus.validation.factory")) {
                    this.validatorFactory = this.config.getProperty("datanucleus.validation.factory");
                }
                else {
                    this.validatorFactory = Validation.buildDefaultValidatorFactory();
                }
            }
            return new BeanValidatorHandler(ec, (ValidatorFactory)this.validatorFactory);
        }
        catch (Throwable ex) {
            if (this.config.hasPropertyNotNull("datanucleus.validation.mode") && this.config.getStringProperty("datanucleus.validation.mode").equalsIgnoreCase("callback")) {
                throw ec.getApiAdapter().getUserExceptionForException(ex.getMessage(), (Exception)ex);
            }
            NucleusLogger.GENERAL.warn("Unable to create validator handler", ex);
            return null;
        }
    }
    
    public boolean hasLevel2Cache() {
        this.getLevel2Cache();
        return !(this.cache instanceof NullLevel2Cache);
    }
    
    public Level2Cache getLevel2Cache() {
        if (this.cache == null) {
            final String level2Type = this.config.getStringProperty("datanucleus.cache.level2.type");
            final String level2ClassName = this.pluginManager.getAttributeValueForExtension("org.datanucleus.cache_level2", "name", level2Type, "class-name");
            if (level2ClassName == null) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("004000", level2Type)).setFatal();
            }
            try {
                this.cache = (Level2Cache)this.pluginManager.createExecutableExtension("org.datanucleus.cache_level2", "name", level2Type, "class-name", new Class[] { ClassConstants.NUCLEUS_CONTEXT }, new Object[] { this });
                if (NucleusLogger.CACHE.isDebugEnabled()) {
                    NucleusLogger.CACHE.debug(NucleusContext.LOCALISER.msg("004002", level2Type));
                }
            }
            catch (Exception e) {
                throw new NucleusUserException(NucleusContext.LOCALISER.msg("004001", level2Type, level2ClassName), e).setFatal();
            }
        }
        return this.cache;
    }
    
    public ExecutionContext.LifecycleListener[] getExecutionContextListeners() {
        return this.executionContextListeners.toArray(new ExecutionContext.LifecycleListener[this.executionContextListeners.size()]);
    }
    
    public void addExecutionContextListener(final ExecutionContext.LifecycleListener listener) {
        this.executionContextListeners.add(listener);
    }
    
    public void removeExecutionContextListener(final ExecutionContext.LifecycleListener listener) {
        this.executionContextListeners.remove(listener);
    }
    
    public synchronized void setJcaMode(final boolean jca) {
        this.jca = jca;
    }
    
    public boolean isJcaMode() {
        return this.jca;
    }
    
    public synchronized FetchGroupManager getFetchGroupManager() {
        if (this.fetchGrpMgr == null) {
            this.fetchGrpMgr = new FetchGroupManager(this);
        }
        return this.fetchGrpMgr;
    }
    
    public void addInternalFetchGroup(final FetchGroup grp) {
        this.getFetchGroupManager().addFetchGroup(grp);
    }
    
    public void removeInternalFetchGroup(final FetchGroup grp) {
        this.getFetchGroupManager().removeFetchGroup(grp);
    }
    
    public FetchGroup createInternalFetchGroup(final Class cls, final String name) {
        if (!cls.isInterface() && !this.getApiAdapter().isPersistable(cls)) {
            throw new NucleusUserException("Cannot create FetchGroup for " + cls + " since it is not persistable");
        }
        if (cls.isInterface() && !this.getMetaDataManager().isPersistentInterface(cls.getName())) {
            throw new NucleusUserException("Cannot create FetchGroup for " + cls + " since it is not persistable");
        }
        return this.getFetchGroupManager().createFetchGroup(cls, name);
    }
    
    public FetchGroup getInternalFetchGroup(final Class cls, final String name) {
        if (!cls.isInterface() && !this.getApiAdapter().isPersistable(cls)) {
            throw new NucleusUserException("Cannot create FetchGroup for " + cls + " since it is not persistable");
        }
        this.getMetaDataManager().getMetaDataForClass(cls, this.getClassLoaderResolver(cls.getClassLoader()));
        if (cls.isInterface() && !this.getMetaDataManager().isPersistentInterface(cls.getName())) {
            throw new NucleusUserException("Cannot create FetchGroup for " + cls + " since it is not persistable");
        }
        return this.getFetchGroupManager().getFetchGroup(cls, name);
    }
    
    public Set<FetchGroup> getFetchGroupsWithName(final String name) {
        return this.getFetchGroupManager().getFetchGroupsWithName(name);
    }
    
    public boolean isClassWithIdentityCacheable(final Object id) {
        if (id == null) {
            return false;
        }
        AbstractClassMetaData cmd = null;
        if (id instanceof OID) {
            cmd = this.getMetaDataManager().getMetaDataForClass(((OID)id).getPcClass(), this.getClassLoaderResolver(id.getClass().getClassLoader()));
        }
        else if (this.getApiAdapter().isSingleFieldIdentity(id)) {
            cmd = this.getMetaDataManager().getMetaDataForClass(this.getApiAdapter().getTargetClassNameForSingleFieldIdentity(id), this.getClassLoaderResolver(id.getClass().getClassLoader()));
        }
        else {
            final Collection<AbstractClassMetaData> cmds = this.getMetaDataManager().getClassMetaDataWithApplicationId(id.getClass().getName());
            if (cmds != null && !cmds.isEmpty()) {
                cmd = cmds.iterator().next();
            }
        }
        return this.isClassCacheable(cmd);
    }
    
    public boolean isClassCacheable(final AbstractClassMetaData cmd) {
        final String cacheMode = this.config.getStringProperty("datanucleus.cache.level2.mode");
        if (cacheMode.equalsIgnoreCase("ALL")) {
            return true;
        }
        if (cacheMode.equalsIgnoreCase("NONE")) {
            return false;
        }
        if (cacheMode.equalsIgnoreCase("ENABLE_SELECTIVE")) {
            return cmd == null || (cmd.isCacheable() != null && cmd.isCacheable());
        }
        if (cacheMode.equalsIgnoreCase("DISABLE_SELECTIVE")) {
            return cmd == null || cmd.isCacheable() == null || cmd.isCacheable();
        }
        if (cmd == null) {
            return true;
        }
        final Boolean cacheableFlag = cmd.isCacheable();
        return cacheableFlag == null || cacheableFlag;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        random = new Random();
        (STARTUP_PROPERTIES = new HashSet<String>()).add("datanucleus.plugin.pluginRegistryClassName");
        NucleusContext.STARTUP_PROPERTIES.add("datanucleus.plugin.pluginRegistryBundleCheck");
        NucleusContext.STARTUP_PROPERTIES.add("datanucleus.plugin.allowUserBundles");
        NucleusContext.STARTUP_PROPERTIES.add("datanucleus.plugin.validatePlugins");
        NucleusContext.STARTUP_PROPERTIES.add("datanucleus.classLoaderResolverName");
        NucleusContext.STARTUP_PROPERTIES.add("datanucleus.persistenceXmlFilename");
        NucleusContext.STARTUP_PROPERTIES.add("datanucleus.primaryClassLoader");
    }
    
    public enum ContextType
    {
        PERSISTENCE, 
        ENHANCEMENT;
    }
}
