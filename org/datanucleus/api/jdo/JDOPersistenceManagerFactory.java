// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.Permission;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.api.jdo.metadata.InterfaceMetadataImpl;
import org.datanucleus.api.jdo.metadata.ClassMetadataImpl;
import javax.jdo.metadata.TypeMetadata;
import org.datanucleus.metadata.InterfaceMetaData;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.PackageMetaData;
import org.datanucleus.metadata.FileMetaData;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.api.jdo.metadata.JDOMetadataImpl;
import javax.jdo.metadata.JDOMetadata;
import javax.jdo.FetchGroup;
import java.util.ArrayList;
import java.util.List;
import org.datanucleus.properties.CorePropertyValidator;
import org.datanucleus.store.query.cache.QueryDatastoreCompilationCache;
import org.datanucleus.query.cache.QueryCompilationCache;
import org.datanucleus.store.query.cache.QueryResultsCache;
import org.datanucleus.store.StoreManager;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;
import java.io.IOException;
import javax.naming.RefAddr;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.exceptions.TransactionIsolationNotSupportedException;
import javax.jdo.JDOUnsupportedOptionException;
import java.lang.reflect.InvocationTargetException;
import javax.jdo.spi.JDOImplHelper;
import javax.jdo.JDOException;
import java.lang.reflect.Method;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.ExecutionContext;
import org.datanucleus.exceptions.TransactionActiveOnCloseException;
import javax.jdo.spi.JDOPermission;
import java.util.Iterator;
import javax.jdo.JDOFatalUserException;
import org.datanucleus.exceptions.NucleusException;
import javax.jdo.JDOUserException;
import org.datanucleus.store.connection.ConnectionResourceType;
import org.datanucleus.metadata.TransactionType;
import java.util.HashSet;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.jdo.PersistenceManager;
import javax.jdo.datastore.DataStoreCache;
import javax.jdo.datastore.Sequence;
import javax.jdo.listener.InstanceLifecycleListener;
import java.util.Map;
import java.util.Set;
import org.datanucleus.NucleusContext;
import java.util.concurrent.ConcurrentHashMap;
import org.datanucleus.util.Localiser;
import java.io.Serializable;
import javax.naming.Referenceable;
import javax.naming.spi.ObjectFactory;
import javax.jdo.PersistenceManagerFactory;

public class JDOPersistenceManagerFactory implements PersistenceManagerFactory, ObjectFactory, Referenceable, Serializable
{
    protected static final Localiser LOCALISER;
    private static ConcurrentHashMap<String, JDOPersistenceManagerFactory> pmfByName;
    protected transient NucleusContext nucleusContext;
    private transient Set<JDOPersistenceManager> pmCache;
    protected transient Map<InstanceLifecycleListener, LifecycleListenerForClass> lifecycleListeners;
    private transient Map<String, Sequence> sequenceByFactoryClass;
    private transient DataStoreCache datastoreCache;
    private transient JDOQueryCache queryCache;
    private transient Set<JDOFetchGroup> jdoFetchGroups;
    private boolean closed;
    private boolean configurable;
    private transient ThreadLocal<PersistenceManager> pmProxyThreadLocal;
    private static final String[] OPTION_ARRAY;
    private Map<String, Object> deserialisationProps;
    
    public static synchronized PersistenceManagerFactory getPersistenceManagerFactory(final Properties overridingProps) {
        final Map overridingMap = new HashMap();
        final Enumeration e = overridingProps.propertyNames();
        while (e.hasMoreElements()) {
            final String param = e.nextElement();
            overridingMap.put(param, overridingProps.getProperty(param));
        }
        final JDOPersistenceManagerFactory pmf = new JDOPersistenceManagerFactory(overridingMap);
        pmf.freezeConfiguration();
        return pmf;
    }
    
    public static synchronized PersistenceManagerFactory getPersistenceManagerFactory(final Map overridingProps) {
        Map overridingMap = null;
        if (overridingProps instanceof Properties) {
            overridingMap = new HashMap();
            final Enumeration e = overridingProps.propertyNames();
            while (e.hasMoreElements()) {
                final String param = e.nextElement();
                overridingMap.put(param, overridingProps.getProperty(param));
            }
        }
        else {
            overridingMap = overridingProps;
        }
        return createPersistenceManagerFactory(overridingMap);
    }
    
    public static synchronized PersistenceManagerFactory getPersistenceManagerFactory(final Map overrides, final Map props) {
        Map propsMap = null;
        if (props instanceof Properties) {
            propsMap = new HashMap();
            final Enumeration e = props.propertyNames();
            while (e.hasMoreElements()) {
                final String param = e.nextElement();
                propsMap.put(param, props.getProperty(param));
            }
        }
        else {
            propsMap = props;
        }
        Map overridesMap = null;
        if (overrides instanceof Properties) {
            overridesMap = new HashMap();
            final Enumeration e2 = overrides.propertyNames();
            while (e2.hasMoreElements()) {
                final String param2 = e2.nextElement();
                overridesMap.put(param2, overrides.getProperty(param2));
            }
        }
        else {
            overridesMap = overrides;
        }
        Map overallMap = null;
        if (propsMap != null) {
            overallMap = new HashMap(propsMap);
        }
        else {
            overallMap = new HashMap();
        }
        if (overridesMap != null) {
            overallMap.putAll(overridesMap);
        }
        return createPersistenceManagerFactory(overallMap);
    }
    
    protected static JDOPersistenceManagerFactory createPersistenceManagerFactory(final Map props) {
        Class pmfClass = null;
        if (props != null && props.containsKey("javax.jdo.PersistenceManagerFactoryClass")) {
            final String pmfClassName = props.get("javax.jdo.PersistenceManagerFactoryClass");
            if (!pmfClassName.equals(JDOPersistenceManagerFactory.class.getName())) {
                try {
                    pmfClass = Class.forName(pmfClassName);
                }
                catch (ClassNotFoundException ex) {}
            }
        }
        JDOPersistenceManagerFactory pmf;
        if (pmfClass != null) {
            pmf = (JDOPersistenceManagerFactory)ClassUtils.newInstance(pmfClass, new Class[] { Map.class }, new Object[] { props });
        }
        else {
            pmf = new JDOPersistenceManagerFactory(props);
        }
        final Boolean singleton = pmf.getConfiguration().getBooleanObjectProperty("datanucleus.singletonPMFForName");
        if (singleton != null && singleton) {
            if (JDOPersistenceManagerFactory.pmfByName == null) {
                JDOPersistenceManagerFactory.pmfByName = new ConcurrentHashMap<String, JDOPersistenceManagerFactory>();
            }
            String name = pmf.getName();
            if (name == null) {
                name = pmf.getPersistenceUnitName();
            }
            if (name != null && JDOPersistenceManagerFactory.pmfByName.containsKey(name)) {
                pmf.close();
                NucleusLogger.PERSISTENCE.warn("Requested PMF of name \"" + name + "\" but already exists and using singleton pattern, so returning existing PMF");
                return JDOPersistenceManagerFactory.pmfByName.get(name);
            }
            JDOPersistenceManagerFactory.pmfByName.putIfAbsent(name, pmf);
        }
        pmf.freezeConfiguration();
        return pmf;
    }
    
    public JDOPersistenceManagerFactory() {
        this(null);
    }
    
    public JDOPersistenceManagerFactory(final PersistenceUnitMetaData pumd, final Map overrideProps) {
        this.pmCache = new HashSet<JDOPersistenceManager>();
        this.datastoreCache = null;
        this.queryCache = null;
        this.jdoFetchGroups = null;
        this.configurable = true;
        this.pmProxyThreadLocal = new InheritableThreadLocal<PersistenceManager>() {
            @Override
            protected PersistenceManager initialValue() {
                return null;
            }
        };
        this.deserialisationProps = null;
        final Map props = new HashMap();
        if (pumd != null && pumd.getProperties() != null) {
            props.putAll(pumd.getProperties());
        }
        if (overrideProps != null) {
            props.putAll(overrideProps);
        }
        if (!props.containsKey("datanucleus.TransactionType") && !props.containsKey("javax.jdo.option.TransactionType")) {
            props.put("datanucleus.TransactionType", TransactionType.RESOURCE_LOCAL.toString());
        }
        else {
            final String transactionType = (props.get("datanucleus.TransactionType") != null) ? props.get("datanucleus.TransactionType") : props.get("javax.jdo.option.TransactionType");
            if (TransactionType.JTA.toString().equalsIgnoreCase(transactionType)) {
                props.put("datanucleus.connection.resourceType", ConnectionResourceType.JTA.toString());
                props.put("datanucleus.connection2.resourceType", ConnectionResourceType.JTA.toString());
            }
        }
        this.nucleusContext = new NucleusContext("JDO", props);
        this.initialiseMetaData(pumd);
        this.processLifecycleListenersFromProperties(props);
    }
    
    public JDOPersistenceManagerFactory(final Map props) {
        this.pmCache = new HashSet<JDOPersistenceManager>();
        this.datastoreCache = null;
        this.queryCache = null;
        this.jdoFetchGroups = null;
        this.configurable = true;
        this.pmProxyThreadLocal = new InheritableThreadLocal<PersistenceManager>() {
            @Override
            protected PersistenceManager initialValue() {
                return null;
            }
        };
        this.deserialisationProps = null;
        Map startupProps = null;
        if (props != null) {
            for (final String startupPropName : NucleusContext.STARTUP_PROPERTIES) {
                if (props.containsKey(startupPropName)) {
                    if (startupProps == null) {
                        startupProps = new HashMap();
                    }
                    startupProps.put(startupPropName, props.get(startupPropName));
                }
            }
        }
        this.nucleusContext = new NucleusContext("JDO", startupProps);
        final Map pmfProps = new HashMap();
        PersistenceUnitMetaData pumd = null;
        if (props != null) {
            String persistenceUnitName = props.get("datanucleus.PersistenceUnitName");
            if (persistenceUnitName == null) {
                persistenceUnitName = props.get("javax.jdo.option.PersistenceUnitName");
            }
            if (persistenceUnitName != null) {
                this.getConfiguration().setProperty("datanucleus.PersistenceUnitName", persistenceUnitName);
                try {
                    pumd = this.nucleusContext.getMetaDataManager().getMetaDataForPersistenceUnit(persistenceUnitName);
                    if (pumd == null) {
                        throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012004", persistenceUnitName));
                    }
                    if (pumd.getProperties() != null) {
                        pmfProps.putAll(pumd.getProperties());
                    }
                }
                catch (NucleusException ne) {
                    throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012005", persistenceUnitName), ne);
                }
            }
        }
        if (props != null) {
            pmfProps.putAll(props);
            if (!pmfProps.containsKey("datanucleus.TransactionType") && !pmfProps.containsKey("javax.jdo.option.TransactionType")) {
                pmfProps.put("datanucleus.TransactionType", TransactionType.RESOURCE_LOCAL.toString());
            }
            else {
                final String transactionType = (pmfProps.get("datanucleus.TransactionType") != null) ? pmfProps.get("datanucleus.TransactionType") : pmfProps.get("javax.jdo.option.TransactionType");
                if (TransactionType.JTA.toString().equalsIgnoreCase(transactionType)) {
                    pmfProps.put("datanucleus.connection.resourceType", ConnectionResourceType.JTA.toString());
                    pmfProps.put("datanucleus.connection2.resourceType", ConnectionResourceType.JTA.toString());
                }
            }
        }
        else {
            pmfProps.put("datanucleus.TransactionType", TransactionType.RESOURCE_LOCAL.toString());
        }
        try {
            final String propsFileProp = "datanucleus.propertiesFile";
            if (pmfProps.containsKey(propsFileProp)) {
                this.getConfiguration().setPropertiesUsingFile(pmfProps.get(propsFileProp));
                pmfProps.remove(propsFileProp);
            }
            this.getConfiguration().setPersistenceProperties(pmfProps);
        }
        catch (IllegalArgumentException iae) {
            throw new JDOFatalUserException("Exception thrown setting persistence properties", iae);
        }
        catch (NucleusException jpe) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
        }
        this.initialiseMetaData(pumd);
        this.processLifecycleListenersFromProperties(props);
    }
    
    public synchronized void close() {
        this.checkJDOPermission(JDOPermission.CLOSE_PERSISTENCE_MANAGER_FACTORY);
        if (this.isClosed()) {
            return;
        }
        this.setIsNotConfigurable();
        synchronized (this.pmCache) {
            final Set<JDOUserException> exceptions = new HashSet<JDOUserException>();
            for (final JDOPersistenceManager pm : this.pmCache) {
                final ExecutionContext om = pm.getExecutionContext();
                if (om.getTransaction().isActive()) {
                    final TransactionActiveOnCloseException tae = new TransactionActiveOnCloseException(om);
                    exceptions.add(new JDOUserException(tae.getMessage(), pm));
                }
            }
            if (!exceptions.isEmpty()) {
                throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012002"), exceptions.toArray(new Throwable[exceptions.size()]));
            }
            for (final JDOPersistenceManager pm : this.pmCache) {
                pm.internalClose();
            }
            this.pmCache.clear();
        }
        if (JDOPersistenceManagerFactory.pmfByName != null) {
            synchronized (JDOPersistenceManagerFactory.pmfByName) {
                final Iterator<Map.Entry<String, JDOPersistenceManagerFactory>> pmfIter = JDOPersistenceManagerFactory.pmfByName.entrySet().iterator();
                while (pmfIter.hasNext()) {
                    final Map.Entry<String, JDOPersistenceManagerFactory> entry = pmfIter.next();
                    if (entry.getValue() == this) {
                        pmfIter.remove();
                        break;
                    }
                }
            }
        }
        if (this.sequenceByFactoryClass != null) {
            this.sequenceByFactoryClass.clear();
            this.sequenceByFactoryClass = null;
        }
        if (this.lifecycleListeners != null) {
            this.lifecycleListeners.clear();
            this.lifecycleListeners = null;
        }
        if (this.datastoreCache != null) {
            this.datastoreCache.evictAll();
            this.datastoreCache = null;
        }
        if (this.queryCache != null) {
            this.queryCache.evictAll();
            this.queryCache = null;
        }
        if (this.jdoFetchGroups != null) {
            this.jdoFetchGroups.clear();
            this.jdoFetchGroups = null;
        }
        this.nucleusContext.close();
        this.closed = true;
    }
    
    public synchronized boolean isClosed() {
        return this.closed;
    }
    
    protected void processLifecycleListenersFromProperties(final Map props) {
        if (props != null) {
            for (final Map.Entry entry : props.entrySet()) {
                final String key = entry.getKey();
                if (key.startsWith("javax.jdo.listener.InstanceLifecycleListener")) {
                    final String listenerClsName = key.substring(45);
                    final String listenerClasses = entry.getValue();
                    final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(null);
                    Class listenerCls = null;
                    try {
                        listenerCls = clr.classForName(listenerClsName);
                    }
                    catch (ClassNotResolvedException cnre) {
                        throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012022", listenerClsName));
                    }
                    InstanceLifecycleListener listener = null;
                    final Method method = ClassUtils.getMethodForClass(listenerCls, "getInstance", null);
                    Label_0216: {
                        if (method != null) {
                            try {
                                listener = (InstanceLifecycleListener)method.invoke(null, new Object[0]);
                                break Label_0216;
                            }
                            catch (Exception e) {
                                throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012021", listenerClsName), e);
                            }
                        }
                        try {
                            listener = listenerCls.newInstance();
                        }
                        catch (Exception e) {
                            throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012020", listenerClsName), e);
                        }
                    }
                    Class[] classes = null;
                    if (!StringUtils.isWhitespace(listenerClasses)) {
                        final String[] classNames = StringUtils.split(listenerClasses, ",");
                        classes = new Class[classNames.length];
                        for (int i = 0; i < classNames.length; ++i) {
                            classes[i] = clr.classForName(classNames[i]);
                        }
                    }
                    this.addInstanceLifecycleListener(listener, classes);
                }
            }
        }
    }
    
    protected void initialiseMetaData(final PersistenceUnitMetaData pumd) {
        this.nucleusContext.getMetaDataManager().setAllowXML(this.getConfiguration().getBooleanProperty("datanucleus.metadata.allowXML"));
        this.nucleusContext.getMetaDataManager().setAllowAnnotations(this.getConfiguration().getBooleanProperty("datanucleus.metadata.allowAnnotations"));
        this.nucleusContext.getMetaDataManager().setValidate(this.getConfiguration().getBooleanProperty("datanucleus.metadata.xml.validate"));
        if (pumd != null) {
            try {
                this.nucleusContext.getMetaDataManager().loadPersistenceUnit(pumd, null);
                if (pumd.getValidationMode() != null) {
                    this.getConfiguration().setProperty("datanucleus.validation.mode", pumd.getValidationMode());
                }
            }
            catch (NucleusException jpe) {
                throw new JDOException(jpe.getMessage(), jpe);
            }
        }
        final boolean allowMetadataLoad = this.nucleusContext.getPersistenceConfiguration().getBooleanProperty("datanucleus.metadata.allowLoadAtRuntime");
        if (!allowMetadataLoad) {
            this.nucleusContext.getMetaDataManager().setAllowMetaDataLoad(false);
        }
    }
    
    protected void freezeConfiguration() {
        if (this.isConfigurable()) {
            Method m = null;
            try {
                m = JDOImplHelper.class.getDeclaredMethod("assertOnlyKnownStandardProperties", Map.class);
                m.invoke(null, this.nucleusContext.getPersistenceConfiguration().getPersistenceProperties());
            }
            catch (InvocationTargetException ite) {
                if (ite.getCause() instanceof JDOException) {
                    throw (JDOException)ite.getCause();
                }
            }
            catch (JDOException jdoe) {
                throw jdoe;
            }
            catch (Exception ex) {}
            synchronized (this) {
                try {
                    this.nucleusContext.initialise();
                    this.datastoreCache = new JDODataStoreCache(this.nucleusContext.getLevel2Cache());
                    this.setIsNotConfigurable();
                }
                catch (TransactionIsolationNotSupportedException inse) {
                    throw new JDOUnsupportedOptionException(inse.getMessage());
                }
                catch (NucleusException jpe) {
                    throw NucleusJDOHelper.getJDOExceptionForNucleusException(jpe);
                }
            }
        }
    }
    
    public PersistenceManager getPersistenceManager() {
        return this.getPersistenceManager(this.getConnectionUserName(), this.getConnectionPassword());
    }
    
    public PersistenceManager getPersistenceManager(final String userName, final String password) {
        this.assertIsOpen();
        this.freezeConfiguration();
        final JDOPersistenceManager pm = this.newPM(this, userName, password);
        if (this.lifecycleListeners != null) {
            for (final LifecycleListenerForClass listener : this.lifecycleListeners.values()) {
                pm.addInstanceLifecycleListener(listener.getListener(), listener.getClasses());
            }
        }
        synchronized (this.pmCache) {
            this.pmCache.add(pm);
        }
        return pm;
    }
    
    protected JDOPersistenceManager newPM(final JDOPersistenceManagerFactory jdoPmf, final String userName, final String password) {
        return new JDOPersistenceManager(jdoPmf, userName, password);
    }
    
    public NucleusContext getNucleusContext() {
        return this.nucleusContext;
    }
    
    protected PersistenceConfiguration getConfiguration() {
        return this.nucleusContext.getPersistenceConfiguration();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj == this || (obj instanceof JDOPersistenceManagerFactory && super.equals(obj));
    }
    
    public Object getObjectInstance(final Object obj, final Name name, final Context ctx, final Hashtable env) throws Exception {
        JDOPersistenceManagerFactory pmf = null;
        if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
            NucleusLogger.PERSISTENCE.debug("Creating PersistenceManagerFactory instance via JNDI with values [object] " + ((obj == null) ? "" : obj.toString()) + " " + "[name] " + ((name == null) ? "" : name.toString()) + " " + "[context] " + ((ctx == null) ? "" : ctx.toString()) + " " + "[env] " + ((env == null) ? "" : env.toString()) + " ");
        }
        if (obj instanceof Reference) {
            final Reference ref = (Reference)obj;
            if (ref.getClassName().equals(JDOClassNameConstants.JDOPersistenceManagerFactory) || ref.getClassName().equals(JDOClassNameConstants.JAVAX_JDO_PersistenceManagerFactory)) {
                final Properties p = new Properties();
                final Enumeration e = ref.getAll();
                while (e.hasMoreElements()) {
                    final StringRefAddr sra = e.nextElement();
                    p.setProperty(sra.getType(), (String)sra.getContent());
                }
                pmf = new JDOPersistenceManagerFactory(p);
                pmf.freezeConfiguration();
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug(JDOPersistenceManagerFactory.LOCALISER.msg("012006", name.toString()));
                }
            }
            else {
                NucleusLogger.PERSISTENCE.warn(JDOPersistenceManagerFactory.LOCALISER.msg("012007", ref.getClassName(), JDOClassNameConstants.JDOPersistenceManagerFactory));
            }
        }
        else {
            NucleusLogger.PERSISTENCE.warn(JDOPersistenceManagerFactory.LOCALISER.msg("012008", obj.getClass().getName()));
        }
        return pmf;
    }
    
    public Reference getReference() {
        Reference rc = null;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            rc = new Reference(JDOClassNameConstants.JAVAX_JDO_PersistenceManagerFactory, JDOClassNameConstants.JDOPersistenceManagerFactory, null);
            final Map p = this.getConfiguration().getPersistenceProperties();
            for (final Map.Entry entry : p.entrySet()) {
                final String key = entry.getKey();
                final Object valueObj = entry.getValue();
                if (valueObj instanceof String) {
                    final String value = (String)valueObj;
                    rc.add(new StringRefAddr(key, value));
                    if (!NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        continue;
                    }
                    NucleusLogger.PERSISTENCE.debug(JDOPersistenceManagerFactory.LOCALISER.msg("012009", key, value));
                }
                else if (valueObj instanceof Long) {
                    final String value = "" + valueObj;
                    rc.add(new StringRefAddr(key, value));
                    if (!NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        continue;
                    }
                    NucleusLogger.PERSISTENCE.debug(JDOPersistenceManagerFactory.LOCALISER.msg("012009", key, value));
                }
                else if (valueObj instanceof Integer) {
                    final String value = "" + valueObj;
                    rc.add(new StringRefAddr(key, value));
                    if (!NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        continue;
                    }
                    NucleusLogger.PERSISTENCE.debug(JDOPersistenceManagerFactory.LOCALISER.msg("012009", key, value));
                }
                else if (valueObj instanceof Boolean) {
                    final String value = valueObj ? "true" : "false";
                    rc.add(new StringRefAddr(key, value));
                    if (!NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                        continue;
                    }
                    NucleusLogger.PERSISTENCE.debug(JDOPersistenceManagerFactory.LOCALISER.msg("012009", key, value));
                }
                else {
                    NucleusLogger.PERSISTENCE.warn(JDOPersistenceManagerFactory.LOCALISER.msg("012010", key));
                }
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled() && p.isEmpty()) {
                NucleusLogger.PERSISTENCE.debug(JDOPersistenceManagerFactory.LOCALISER.msg("012011"));
            }
        }
        catch (IOException ex) {
            NucleusLogger.PERSISTENCE.error(ex.getMessage());
            throw new NucleusException(ex.getMessage(), ex);
        }
        return rc;
    }
    
    public PersistenceManager getPersistenceManagerProxy() {
        return new JDOPersistenceManagerProxy(this);
    }
    
    PersistenceManager getPMProxyDelegate() {
        PersistenceManager pm = this.pmProxyThreadLocal.get();
        if (pm == null) {
            pm = this.getPersistenceManager();
            this.pmProxyThreadLocal.set(pm);
        }
        return pm;
    }
    
    void clearPMProxyDelegate() {
        final PersistenceManagerFactory pmf = this.getPMProxyDelegate().getPersistenceManagerFactory();
        final String txnType = pmf.getTransactionType();
        if (TransactionType.RESOURCE_LOCAL.toString().equalsIgnoreCase(txnType)) {
            this.getPMProxyDelegate().close();
            this.pmProxyThreadLocal.remove();
        }
        else if (TransactionType.JTA.toString().equalsIgnoreCase(txnType)) {}
    }
    
    public Properties getProperties() {
        final Properties props = new Properties();
        props.setProperty("VendorName", "DataNucleus");
        props.setProperty("VersionNumber", this.nucleusContext.getPluginManager().getVersionForBundle("org.datanucleus.api.jdo"));
        props.putAll(this.nucleusContext.getPersistenceConfiguration().getPersistenceProperties());
        return props;
    }
    
    public Collection<String> supportedOptions() {
        final Set options = new HashSet(Arrays.asList(JDOPersistenceManagerFactory.OPTION_ARRAY));
        final StoreManager storeMgr = this.nucleusContext.getStoreManager();
        if (storeMgr != null) {
            final Collection storeMgrOptions = storeMgr.getSupportedOptions();
            if (!storeMgrOptions.contains("NonDurableIdentity")) {
                options.remove("javax.jdo.option.NonDurableIdentity");
            }
            if (!storeMgrOptions.contains("DatastoreIdentity")) {
                options.remove("javax.jdo.option.DatastoreIdentity");
            }
            if (!storeMgrOptions.contains("ApplicationIdentity")) {
                options.remove("javax.jdo.option.ApplicationIdentity");
            }
            if (!storeMgr.supportsQueryLanguage("JDOQL")) {
                options.remove("javax.jdo.query.JDOQL");
            }
            if (!storeMgr.supportsQueryLanguage("SQL")) {
                options.remove("javax.jdo.query.SQL");
            }
            if (storeMgrOptions.contains("TransactionIsolationLevel.read-committed")) {
                options.add("javax.jdo.option.TransactionIsolationLevel.read-committed");
            }
            if (storeMgrOptions.contains("TransactionIsolationLevel.read-uncommitted")) {
                options.add("javax.jdo.option.TransactionIsolationLevel.read-uncommitted");
            }
            if (storeMgrOptions.contains("TransactionIsolationLevel.repeatable-read")) {
                options.add("javax.jdo.option.TransactionIsolationLevel.repeatable-read");
            }
            if (storeMgrOptions.contains("TransactionIsolationLevel.serializable")) {
                options.add("javax.jdo.option.TransactionIsolationLevel.serializable");
            }
            if (storeMgrOptions.contains("TransactionIsolationLevel.snapshot")) {
                options.add("javax.jdo.option.TransactionIsolationLevel.snapshot");
            }
            if (storeMgrOptions.contains("Query.Cancel")) {
                options.add("javax.jdo.option.QueryCancel");
            }
            if (storeMgrOptions.contains("Datastore.Timeout")) {
                options.add("javax.jdo.option.DatastoreTimeout");
            }
        }
        return (Collection<String>)Collections.unmodifiableSet((Set<?>)options);
    }
    
    public void releasePersistenceManager(final JDOPersistenceManager pm) {
        synchronized (this.pmCache) {
            if (this.pmCache.contains(pm)) {
                pm.internalClose();
                this.pmCache.remove(pm);
            }
        }
    }
    
    protected void assertIsOpen() {
        if (this.isClosed()) {
            throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012025"));
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.close();
    }
    
    public DataStoreCache getDataStoreCache() {
        this.freezeConfiguration();
        return this.datastoreCache;
    }
    
    public JDOQueryCache getQueryCache() {
        if (this.queryCache != null) {
            return this.queryCache;
        }
        final QueryResultsCache cache = this.nucleusContext.getStoreManager().getQueryManager().getQueryResultsCache();
        return this.queryCache = new JDOQueryCache(cache);
    }
    
    public QueryCompilationCache getQueryGenericCompilationCache() {
        return this.nucleusContext.getStoreManager().getQueryManager().getQueryCompilationCache();
    }
    
    public QueryDatastoreCompilationCache getQueryDatastoreCompilationCache() {
        return this.nucleusContext.getStoreManager().getQueryManager().getQueryDatastoreCompilationCache();
    }
    
    public void setConnectionUserName(final String userName) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionUserName", userName);
    }
    
    public void setConnectionPassword(final String password) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionPassword", password);
    }
    
    public void setConnectionURL(final String url) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionURL", url);
    }
    
    public void setConnectionDriverName(final String driverName) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionDriverName", driverName);
    }
    
    public void setConnectionFactoryName(final String connectionFactoryName) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionFactoryName", connectionFactoryName);
    }
    
    public void setConnectionFactory(final Object connectionFactory) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionFactory", connectionFactory);
    }
    
    public void setConnectionFactory2Name(final String connectionFactoryName) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionFactory2Name", connectionFactoryName);
    }
    
    public void setConnectionFactory2(final Object connectionFactory) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.ConnectionFactory2", connectionFactory);
    }
    
    public void setMultithreaded(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.Multithreaded", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setOptimistic(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.Optimistic", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setRetainValues(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.RetainValues", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setRestoreValues(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.RestoreValues", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNontransactionalRead(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.NontransactionalRead", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setNontransactionalWrite(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.NontransactionalWrite", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setIgnoreCache(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.IgnoreCache", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setDetachAllOnCommit(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.DetachAllOnCommit", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setCopyOnAttach(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.CopyOnAttach", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setMapping(final String mapping) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.mapping", mapping);
    }
    
    public void setCatalog(final String catalog) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.mapping.Catalog", catalog);
    }
    
    public void setSchema(final String schema) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.mapping.Schema", schema);
    }
    
    public void setDatastoreReadTimeoutMillis(final Integer timeout) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.datastoreReadTimeout", timeout);
    }
    
    public void setDatastoreWriteTimeoutMillis(final Integer timeout) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.datastoreWriteTimeout", timeout);
    }
    
    public void setTransactionType(final String type) {
        this.assertConfigurable();
        final boolean validated = new CorePropertyValidator().validate("datanucleus.TransactionType", type);
        if (validated) {
            this.getConfiguration().setProperty("datanucleus.TransactionType", type);
            return;
        }
        throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012026", "javax.jdo.option.TransactionType", type));
    }
    
    public void setPersistenceUnitName(final String name) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.PersistenceUnitName", name);
    }
    
    public void setPersistenceXmlFilename(final String name) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.persistenceXmlFilename", name);
    }
    
    public void setName(final String name) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.Name", name);
    }
    
    public void setServerTimeZoneID(final String id) {
        this.assertConfigurable();
        final boolean validated = new CorePropertyValidator().validate("datanucleus.ServerTimeZoneID", id);
        if (validated) {
            this.getConfiguration().setProperty("datanucleus.ServerTimeZoneID", id);
            return;
        }
        throw new JDOUserException("Invalid TimeZone ID specified");
    }
    
    public void setReadOnly(final boolean flag) {
        this.assertConfigurable();
        this.getConfiguration().setProperty("datanucleus.readOnlyDatastore", flag ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setTransactionIsolationLevel(final String level) {
        this.assertConfigurable();
        if (this.nucleusContext.getStoreManager() != null && !this.nucleusContext.getStoreManager().getSupportedOptions().contains("TransactionIsolationLevel." + level)) {
            throw new JDOUnsupportedOptionException("Isolation level \"" + level + "\" is not supported for this datastore");
        }
        this.getConfiguration().setProperty("datanucleus.transactionIsolation", (level != null) ? level : "read-committed");
    }
    
    public String getConnectionUserName() {
        return this.getConfiguration().getStringProperty("datanucleus.ConnectionUserName");
    }
    
    public String getConnectionPassword() {
        return this.getConfiguration().getStringProperty("datanucleus.ConnectionPassword");
    }
    
    public String getConnectionURL() {
        return this.getConfiguration().getStringProperty("datanucleus.ConnectionURL");
    }
    
    public String getConnectionDriverName() {
        return this.getConfiguration().getStringProperty("datanucleus.ConnectionDriverName");
    }
    
    public String getConnectionFactoryName() {
        return this.getConfiguration().getStringProperty("datanucleus.ConnectionFactoryName");
    }
    
    public String getConnectionFactory2Name() {
        return this.getConfiguration().getStringProperty("datanucleus.ConnectionFactory2Name");
    }
    
    public Object getConnectionFactory() {
        return this.getConfiguration().getProperty("datanucleus.ConnectionFactory");
    }
    
    public Object getConnectionFactory2() {
        return this.getConfiguration().getProperty("datanucleus.ConnectionFactory2");
    }
    
    public boolean getMultithreaded() {
        return this.getConfiguration().getBooleanProperty("datanucleus.Multithreaded");
    }
    
    public boolean getOptimistic() {
        return this.getConfiguration().getBooleanProperty("datanucleus.Optimistic");
    }
    
    public boolean getRetainValues() {
        return this.getConfiguration().getBooleanProperty("datanucleus.RetainValues");
    }
    
    public boolean getRestoreValues() {
        return this.getConfiguration().getBooleanProperty("datanucleus.RestoreValues");
    }
    
    public boolean getNontransactionalRead() {
        return this.getConfiguration().getBooleanProperty("datanucleus.NontransactionalRead");
    }
    
    public boolean getNontransactionalWrite() {
        return this.getConfiguration().getBooleanProperty("datanucleus.NontransactionalWrite");
    }
    
    public boolean getIgnoreCache() {
        return this.getConfiguration().getBooleanProperty("datanucleus.IgnoreCache");
    }
    
    public boolean getDetachAllOnCommit() {
        return this.getConfiguration().getBooleanProperty("datanucleus.DetachAllOnCommit");
    }
    
    public boolean getCopyOnAttach() {
        return this.getConfiguration().getBooleanProperty("datanucleus.CopyOnAttach");
    }
    
    public String getMapping() {
        return this.getConfiguration().getStringProperty("datanucleus.mapping");
    }
    
    public String getCatalog() {
        return this.getConfiguration().getStringProperty("datanucleus.mapping.Catalog");
    }
    
    public String getSchema() {
        return this.getConfiguration().getStringProperty("datanucleus.mapping.Schema");
    }
    
    public String getName() {
        return this.getConfiguration().getStringProperty("datanucleus.Name");
    }
    
    public String getPersistenceUnitName() {
        return this.getConfiguration().getStringProperty("datanucleus.PersistenceUnitName");
    }
    
    public String getPersistenceXmlFilename() {
        return this.getConfiguration().getStringProperty("datanucleus.persistenceXmlFilename");
    }
    
    public Integer getDatastoreReadTimeoutMillis() {
        return this.getConfiguration().getIntProperty("datanucleus.datastoreReadTimeout");
    }
    
    public Integer getDatastoreWriteTimeoutMillis() {
        return this.getConfiguration().getIntProperty("datanucleus.datastoreWriteTimeout");
    }
    
    public String getServerTimeZoneID() {
        return this.getConfiguration().getStringProperty("datanucleus.ServerTimeZoneID");
    }
    
    public boolean getReadOnly() {
        return this.getConfiguration().getBooleanProperty("datanucleus.readOnlyDatastore");
    }
    
    public String getTransactionType() {
        return this.getConfiguration().getStringProperty("datanucleus.TransactionType");
    }
    
    public String getTransactionIsolationLevel() {
        return this.getConfiguration().getStringProperty("datanucleus.transactionIsolation");
    }
    
    public void setPrimaryClassLoader(final ClassLoader loader) {
        this.getConfiguration().setProperty("datanucleus.primaryClassLoader", loader);
    }
    
    public ClassLoader getPrimaryClassLoader() {
        return (ClassLoader)this.getConfiguration().getProperty("datanucleus.primaryClassLoader");
    }
    
    public void setPersistenceProperties(final Map<String, Object> props) {
        this.assertConfigurable();
        this.getConfiguration().setPersistenceProperties(props);
    }
    
    public Map<String, Object> getPersistenceProperties() {
        return this.getConfiguration().getPersistenceProperties();
    }
    
    protected void assertConfigurable() {
        if (!this.isConfigurable()) {
            throw new JDOUserException(JDOPersistenceManagerFactory.LOCALISER.msg("012023"));
        }
    }
    
    protected boolean isConfigurable() {
        return this.configurable;
    }
    
    protected void setIsNotConfigurable() {
        this.configurable = false;
    }
    
    @Deprecated
    public List<LifecycleListenerForClass> getLifecycleListenerSpecifications() {
        if (this.lifecycleListeners == null) {
            return (List<LifecycleListenerForClass>)Collections.EMPTY_LIST;
        }
        return new ArrayList<LifecycleListenerForClass>(this.lifecycleListeners.values());
    }
    
    public void addInstanceLifecycleListener(final InstanceLifecycleListener listener, Class[] classes) {
        final boolean allowListeners = this.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.allowListenerUpdateAfterInit", false);
        if (!allowListeners) {
            this.assertConfigurable();
        }
        if (listener == null) {
            return;
        }
        classes = LifecycleListenerForClass.canonicaliseClasses(classes);
        if (classes != null && classes.length == 0) {
            return;
        }
        if (this.lifecycleListeners == null) {
            this.lifecycleListeners = new ConcurrentHashMap<InstanceLifecycleListener, LifecycleListenerForClass>(1);
        }
        LifecycleListenerForClass entry;
        if (this.lifecycleListeners.containsKey(listener)) {
            entry = this.lifecycleListeners.get(listener).mergeClasses(classes);
        }
        else {
            entry = new LifecycleListenerForClass(listener, classes);
        }
        this.lifecycleListeners.put(listener, entry);
    }
    
    public void removeInstanceLifecycleListener(final InstanceLifecycleListener listener) {
        final boolean allowListeners = this.getNucleusContext().getPersistenceConfiguration().getBooleanProperty("datanucleus.allowListenerUpdateAfterInit", false);
        if (!allowListeners) {
            this.assertConfigurable();
        }
        if (listener == null || this.lifecycleListeners == null) {
            return;
        }
        this.lifecycleListeners.remove(listener);
    }
    
    public void addSequenceForFactoryClass(final String factoryClassName, final Sequence sequence) {
        if (this.sequenceByFactoryClass == null) {
            this.sequenceByFactoryClass = new HashMap<String, Sequence>();
        }
        this.sequenceByFactoryClass.put(factoryClassName, sequence);
    }
    
    public Sequence getSequenceForFactoryClass(final String factoryClassName) {
        if (this.sequenceByFactoryClass == null) {
            return null;
        }
        return this.sequenceByFactoryClass.get(factoryClassName);
    }
    
    public Set<FetchGroup> getFetchGroups() {
        final Set<JDOFetchGroup> jdoGroups = this.getJDOFetchGroups(false);
        if (jdoGroups != null) {
            synchronized (jdoGroups) {
                if (!jdoGroups.isEmpty()) {
                    return new HashSet<FetchGroup>(jdoGroups);
                }
            }
        }
        return null;
    }
    
    public FetchGroup getFetchGroup(final Class cls, final String name) {
        final Set<JDOFetchGroup> jdoGroups = this.getJDOFetchGroups(false);
        if (jdoGroups != null) {
            synchronized (jdoGroups) {
                for (final JDOFetchGroup jdoFetchGroup : jdoGroups) {
                    if (jdoFetchGroup.getType() == cls && jdoFetchGroup.getName().equals(name)) {
                        return jdoFetchGroup;
                    }
                }
            }
        }
        try {
            org.datanucleus.FetchGroup internalGrp = this.nucleusContext.getInternalFetchGroup(cls, name);
            if (!internalGrp.isUnmodifiable()) {
                return new JDOFetchGroup(internalGrp);
            }
            internalGrp = this.nucleusContext.createInternalFetchGroup(cls, name);
            this.nucleusContext.addInternalFetchGroup(internalGrp);
            final JDOFetchGroup jdoGrp = new JDOFetchGroup(internalGrp);
            return jdoGrp;
        }
        catch (NucleusException ne) {
            throw NucleusJDOHelper.getJDOExceptionForNucleusException(ne);
        }
    }
    
    public void addFetchGroups(final FetchGroup... groups) {
        if (groups == null || groups.length == 0) {
            return;
        }
        final Set<JDOFetchGroup> jdoGroups = this.getJDOFetchGroups(true);
        synchronized (jdoGroups) {
            for (int i = 0; i < groups.length; ++i) {
                final JDOFetchGroup jdoFetchGroup = (JDOFetchGroup)groups[i];
                this.nucleusContext.addInternalFetchGroup(jdoFetchGroup.getInternalFetchGroup());
                jdoGroups.add(jdoFetchGroup);
            }
        }
    }
    
    public void removeFetchGroups(final FetchGroup... groups) {
        if (groups == null || groups.length == 0) {
            return;
        }
        final Set<JDOFetchGroup> jdoGroups = this.getJDOFetchGroups(false);
        if (jdoGroups != null) {
            synchronized (jdoGroups) {
                if (!jdoGroups.isEmpty()) {
                    for (int i = 0; i < groups.length; ++i) {
                        final JDOFetchGroup jdoFetchGroup = (JDOFetchGroup)groups[i];
                        this.nucleusContext.removeInternalFetchGroup(jdoFetchGroup.getInternalFetchGroup());
                        jdoGroups.remove(jdoFetchGroup);
                    }
                }
            }
        }
    }
    
    public void removeAllFetchGroups() {
        final Set<JDOFetchGroup> jdoGroups = this.getJDOFetchGroups(false);
        if (jdoGroups != null) {
            synchronized (jdoGroups) {
                for (final JDOFetchGroup jdoGrp : jdoGroups) {
                    this.nucleusContext.removeInternalFetchGroup(jdoGrp.getInternalFetchGroup());
                }
                jdoGroups.clear();
            }
        }
    }
    
    public JDOMetadata newMetadata() {
        return new JDOMetadataImpl();
    }
    
    public void registerMetadata(final JDOMetadata metadata) {
        final MetaDataManager mmgr = this.nucleusContext.getMetaDataManager();
        final FileMetaData filemd = ((JDOMetadataImpl)metadata).getInternal();
        for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
            final PackageMetaData pmd = filemd.getPackage(i);
            for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                final ClassMetaData cmd = pmd.getClass(j);
                if (mmgr.hasMetaDataForClass(cmd.getFullClassName())) {
                    throw new JDOUserException("Cannot redefine metadata for " + cmd.getFullClassName());
                }
            }
            for (int j = 0; j < pmd.getNoOfInterfaces(); ++j) {
                final InterfaceMetaData imd = pmd.getInterface(j);
                if (mmgr.hasMetaDataForClass(imd.getFullClassName())) {
                    throw new JDOUserException("Cannot redefine metadata for " + imd.getFullClassName());
                }
            }
        }
        mmgr.loadUserMetaData(filemd, null);
    }
    
    public TypeMetadata getMetadata(final String className) {
        final MetaDataManager mmgr = this.nucleusContext.getMetaDataManager();
        final AbstractClassMetaData acmd = mmgr.getMetaDataForClass(className, this.nucleusContext.getClassLoaderResolver(null));
        if (acmd == null) {
            return null;
        }
        if (acmd instanceof ClassMetaData) {
            return new ClassMetadataImpl((ClassMetaData)acmd);
        }
        return new InterfaceMetadataImpl((InterfaceMetaData)acmd);
    }
    
    public Collection<Class> getManagedClasses() {
        this.checkJDOPermission(JDOPermission.GET_METADATA);
        final MetaDataManager mmgr = this.nucleusContext.getMetaDataManager();
        final Collection<String> classNames = mmgr.getClassesWithMetaData();
        final Collection<Class> classes = new HashSet<Class>();
        if (classNames != null) {
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(null);
            final Iterator<String> iter = classNames.iterator();
            while (iter.hasNext()) {
                try {
                    final Class cls = clr.classForName(iter.next());
                    classes.add(cls);
                }
                catch (ClassNotResolvedException cnre) {}
            }
        }
        return classes;
    }
    
    private void checkJDOPermission(final JDOPermission jdoPermission) {
        final SecurityManager secmgr = System.getSecurityManager();
        if (secmgr != null) {
            secmgr.checkPermission(jdoPermission);
        }
    }
    
    private synchronized Set<JDOFetchGroup> getJDOFetchGroups(final boolean createIfNull) {
        if (this.jdoFetchGroups == null && createIfNull) {
            this.jdoFetchGroups = new HashSet<JDOFetchGroup>();
        }
        return this.jdoFetchGroups;
    }
    
    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(this.nucleusContext.getPersistenceConfiguration().getPersistenceProperties());
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.deserialisationProps = (Map<String, Object>)ois.readObject();
    }
    
    private Object readResolve() throws InvalidObjectException {
        JDOPersistenceManagerFactory pmf = null;
        if (JDOPersistenceManagerFactory.pmfByName != null) {
            String name = this.deserialisationProps.get("datanucleus.Name");
            if (name == null) {
                name = this.deserialisationProps.get("datanucleus.PersistenceUnitName");
            }
            pmf = JDOPersistenceManagerFactory.pmfByName.get(name);
            if (pmf != null) {
                return pmf;
            }
        }
        this.configurable = true;
        if (this.pmCache == null) {
            this.pmCache = new HashSet<JDOPersistenceManager>();
        }
        this.nucleusContext = new NucleusContext("JDO", this.deserialisationProps);
        PersistenceUnitMetaData pumd = null;
        if (this.getPersistenceUnitName() != null) {
            pumd = this.nucleusContext.getMetaDataManager().getMetaDataForPersistenceUnit(this.getPersistenceUnitName());
        }
        this.initialiseMetaData(pumd);
        this.processLifecycleListenersFromProperties(this.deserialisationProps);
        this.freezeConfiguration();
        this.deserialisationProps = null;
        return this;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.api.jdo.Localisation", JDOPersistenceManagerFactory.class.getClassLoader());
        OPTION_ARRAY = new String[] { "javax.jdo.option.TransientTransactional", "javax.jdo.option.NontransactionalWrite", "javax.jdo.option.NontransactionalRead", "javax.jdo.option.RetainValues", "javax.jdo.option.Optimistic", "javax.jdo.option.ApplicationIdentity", "javax.jdo.option.DatastoreIdentity", "javax.jdo.option.NonDurableIdentity", "javax.jdo.option.BinaryCompatibility", "javax.jdo.option.GetDataStoreConnection", "javax.jdo.option.GetJDBCConnection", "javax.jdo.option.version.DateTime", "javax.jdo.option.PreDirtyEvent", "javax.jdo.option.ArrayList", "javax.jdo.option.LinkedList", "javax.jdo.option.TreeSet", "javax.jdo.option.TreeMap", "javax.jdo.option.Vector", "javax.jdo.option.List", "javax.jdo.option.Stack", "javax.jdo.option.Map", "javax.jdo.option.HashMap", "javax.jdo.option.Hashtable", "javax.jdo.option.SortedSet", "javax.jdo.option.SortedMap", "javax.jdo.option.Array", "javax.jdo.option.NullCollection", "javax.jdo.option.mapping.HeterogeneousObjectType", "javax.jdo.option.mapping.HeterogeneousInterfaceType", "javax.jdo.option.mapping.JoinedTablePerClass", "javax.jdo.option.mapping.JoinedTablePerConcreteClass", "javax.jdo.option.mapping.NonJoinedTablePerConcreteClass", "javax.jdo.query.SQL", "javax.jdo.query.JDOQL", "javax.jdo.option.UnconstrainedQueryVariables" };
    }
}
