// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import java.util.ListIterator;
import org.datanucleus.store.rdbms.table.JoinTable;
import org.datanucleus.store.rdbms.table.ViewImpl;
import org.datanucleus.store.rdbms.table.ClassView;
import org.datanucleus.metadata.ClassPersistenceModifier;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.store.autostart.AutoStartMechanism;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.store.StorePersistenceHandler;
import org.datanucleus.store.StoreDataManager;
import java.util.TreeSet;
import java.util.StringTokenizer;
import org.datanucleus.store.rdbms.valuegenerator.SequenceTable;
import org.datanucleus.metadata.MetaData;
import java.io.File;
import org.datanucleus.store.rdbms.autostart.SchemaAutoStarter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.io.FileWriter;
import java.sql.Statement;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import org.datanucleus.store.rdbms.scostore.JoinPersistableRelationStore;
import org.datanucleus.store.rdbms.table.PersistableJoinTable;
import org.datanucleus.store.rdbms.scostore.JoinSetStore;
import org.datanucleus.store.rdbms.scostore.JoinListStore;
import org.datanucleus.store.rdbms.scostore.JoinMapStore;
import org.datanucleus.store.rdbms.scostore.JoinArrayStore;
import org.datanucleus.store.rdbms.table.ArrayTable;
import org.datanucleus.store.rdbms.scostore.FKMapStore;
import org.datanucleus.store.rdbms.scostore.FKSetStore;
import org.datanucleus.store.scostore.SetStore;
import org.datanucleus.store.rdbms.scostore.FKListStore;
import org.datanucleus.store.scostore.ListStore;
import org.datanucleus.store.rdbms.scostore.FKArrayStore;
import org.datanucleus.store.rdbms.query.PersistentClassROF;
import org.datanucleus.store.rdbms.query.ResultObjectFactory;
import org.datanucleus.FetchPlan;
import org.datanucleus.store.rdbms.mapping.datastore.AbstractDatastoreMapping;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.metadata.MapMetaData;
import java.io.PrintStream;
import org.datanucleus.store.rdbms.table.MapTable;
import org.datanucleus.store.rdbms.table.CollectionTable;
import org.datanucleus.store.rdbms.table.ClassTable;
import org.datanucleus.util.MacroString;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaInfo;
import java.util.Collections;
import org.datanucleus.store.rdbms.schema.RDBMSTableInfo;
import org.datanucleus.store.rdbms.schema.RDBMSColumnInfo;
import org.datanucleus.store.rdbms.schema.JDBCTypeInfo;
import org.datanucleus.store.rdbms.exceptions.UnsupportedDataTypeException;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import org.datanucleus.metadata.IdentityMetaData;
import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.util.StringUtils;
import java.util.Properties;
import org.datanucleus.metadata.TableGeneratorMetaData;
import org.datanucleus.transaction.TransactionUtils;
import org.datanucleus.store.valuegenerator.ValueGenerationConnectionProvider;
import org.datanucleus.store.valuegenerator.AbstractDatastoreGenerator;
import org.datanucleus.store.valuegenerator.ValueGenerator;
import org.datanucleus.store.rdbms.fieldmanager.ParameterSetter;
import org.datanucleus.store.rdbms.fieldmanager.ResultSetGetter;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.rdbms.mapping.StatementClassMapping;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.identity.OID;
import java.util.ArrayList;
import org.datanucleus.identity.SCOID;
import org.datanucleus.store.rdbms.table.TableImpl;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Date;
import org.datanucleus.store.rdbms.table.ProbeTable;
import org.datanucleus.store.connection.ConnectionFactory;
import org.datanucleus.store.NucleusConnectionImpl;
import java.sql.SQLException;
import org.datanucleus.store.NucleusConnection;
import org.datanucleus.store.NucleusSequence;
import org.datanucleus.metadata.SequenceMetaData;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.schema.RDBMSTypesInfo;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.util.Collection;
import org.datanucleus.store.scostore.PersistableRelationStore;
import org.datanucleus.store.scostore.ArrayStore;
import org.datanucleus.store.scostore.MapStore;
import org.datanucleus.store.types.SCOUtils;
import java.util.List;
import org.datanucleus.store.scostore.CollectionStore;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.store.types.IncompatibleFieldTypeException;
import org.datanucleus.store.rdbms.mapping.java.PersistableMapping;
import org.datanucleus.store.rdbms.mapping.java.CollectionMapping;
import org.datanucleus.store.rdbms.mapping.java.ArrayMapping;
import org.datanucleus.store.rdbms.mapping.java.MapMapping;
import org.datanucleus.state.ActivityState;
import java.util.HashSet;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.store.rdbms.exceptions.NoTableManagedException;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.StoreData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.state.ReferentialJDOStateManager;
import org.datanucleus.ClassConstants;
import java.util.Iterator;
import org.datanucleus.store.connection.ManagedConnection;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.NucleusException;
import java.util.HashMap;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapterFactory;
import org.datanucleus.exceptions.NucleusDataStoreException;
import java.sql.Connection;
import org.datanucleus.store.rdbms.schema.RDBMSSchemaHandler;
import org.datanucleus.flush.FlushOrdered;
import org.datanucleus.store.StoreManager;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentHashMap;
import org.datanucleus.NucleusContext;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.scostore.Store;
import org.datanucleus.util.MultiMap;
import java.util.Set;
import java.io.Writer;
import java.util.Calendar;
import org.datanucleus.store.rdbms.sql.expression.SQLExpressionFactory;
import java.util.concurrent.locks.ReadWriteLock;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.state.ObjectProvider;
import java.util.Map;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.store.rdbms.mapping.MappedTypeManager;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.util.Localiser;
import org.datanucleus.store.schema.SchemaScriptAwareStoreManager;
import org.datanucleus.store.schema.SchemaAwareStoreManager;
import org.datanucleus.store.BackedSCOStoreManager;
import org.datanucleus.store.AbstractStoreManager;

public class RDBMSStoreManager extends AbstractStoreManager implements BackedSCOStoreManager, SchemaAwareStoreManager, SchemaScriptAwareStoreManager
{
    protected static final Localiser LOCALISER_RDBMS;
    protected DatastoreAdapter dba;
    protected IdentifierFactory identifierFactory;
    protected String catalogName;
    protected String schemaName;
    protected MappedTypeManager mappedTypeMgr;
    protected MappingManager mappingManager;
    protected Map<ObjectProvider, DatastoreClass> insertedDatastoreClassByStateManager;
    protected ReadWriteLock schemaLock;
    private SQLController sqlController;
    protected SQLExpressionFactory expressionFactory;
    private transient Calendar dateTimezoneCalendar;
    private ClassAdder classAdder;
    private Writer ddlWriter;
    private boolean completeDDL;
    private Set<String> writtenDdlStatements;
    private MultiMap schemaCallbacks;
    private Map<String, Store> backingStoreByMemberName;
    
    public RDBMSStoreManager(final ClassLoaderResolver clr, final NucleusContext ctx, final Map<String, Object> props) {
        super("rdbms", clr, ctx, props);
        this.catalogName = null;
        this.schemaName = null;
        this.mappedTypeMgr = null;
        this.insertedDatastoreClassByStateManager = new ConcurrentHashMap<ObjectProvider, DatastoreClass>();
        this.schemaLock = new ReentrantReadWriteLock();
        this.sqlController = null;
        this.dateTimezoneCalendar = null;
        this.classAdder = null;
        this.ddlWriter = null;
        this.completeDDL = false;
        this.writtenDdlStatements = null;
        this.schemaCallbacks = new MultiMap();
        this.backingStoreByMemberName = new ConcurrentHashMap<String, Store>();
        this.mappedTypeMgr = new MappedTypeManager(this.nucleusContext);
        this.persistenceHandler = new RDBMSPersistenceHandler(this);
        this.flushProcess = new FlushOrdered();
        this.schemaHandler = new RDBMSSchemaHandler(this);
        this.expressionFactory = new SQLExpressionFactory(this);
        try {
            final ManagedConnection mc = this.getConnection(-1);
            final Connection conn = (Connection)mc.getConnection();
            if (conn == null) {
                throw new NucleusDataStoreException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050007"));
            }
            try {
                (this.dba = DatastoreAdapterFactory.getInstance().getDatastoreAdapter(clr, conn, this.getStringProperty("datanucleus.rdbms.datastoreAdapterClassName"), ctx.getPluginManager())).initialiseTypes(this.schemaHandler, mc);
                this.dba.removeUnsupportedMappings(this.schemaHandler, mc);
                if (this.hasPropertyNotNull("datanucleus.mapping.Catalog")) {
                    if (!this.dba.supportsOption("CatalogInTableDefinition")) {
                        NucleusLogger.DATASTORE.warn(RDBMSStoreManager.LOCALISER_RDBMS.msg("050002", this.getStringProperty("datanucleus.mapping.Catalog")));
                    }
                    else {
                        this.catalogName = this.getStringProperty("datanucleus.mapping.Catalog");
                    }
                }
                if (this.hasPropertyNotNull("datanucleus.mapping.Schema")) {
                    if (!this.dba.supportsOption("SchemaInTableDefinition")) {
                        NucleusLogger.DATASTORE.warn(RDBMSStoreManager.LOCALISER_RDBMS.msg("050003", this.getStringProperty("datanucleus.mapping.Schema")));
                    }
                    else {
                        this.schemaName = this.getStringProperty("datanucleus.mapping.Schema");
                    }
                }
                this.initialiseIdentifierFactory(ctx);
                if (this.schemaName != null) {
                    final String validSchemaName = this.identifierFactory.getIdentifierInAdapterCase(this.schemaName);
                    if (!validSchemaName.equals(this.schemaName)) {
                        NucleusLogger.DATASTORE_SCHEMA.warn(RDBMSStoreManager.LOCALISER_RDBMS.msg("020192", "schema", this.schemaName, validSchemaName));
                        this.schemaName = validSchemaName;
                    }
                }
                if (this.catalogName != null) {
                    final String validCatalogName = this.identifierFactory.getIdentifierInAdapterCase(this.catalogName);
                    if (!validCatalogName.equals(this.catalogName)) {
                        NucleusLogger.DATASTORE_SCHEMA.warn(RDBMSStoreManager.LOCALISER_RDBMS.msg("020192", "catalog", this.catalogName, validCatalogName));
                        this.catalogName = validCatalogName;
                    }
                }
                this.sqlController = new SQLController(this.dba.supportsOption("StatementBatching"), this.getIntProperty("datanucleus.rdbms.statementBatchLimit"), this.getIntProperty("datanucleus.datastoreReadTimeout"), this.getStringProperty("datanucleus.rdbms.statementLogging"));
                final Map<String, Object> dbaProps = new HashMap<String, Object>();
                final Map<String, Object> omfProps = ctx.getPersistenceConfiguration().getPersistenceProperties();
                for (final Map.Entry<String, Object> entry : omfProps.entrySet()) {
                    final String prop = entry.getKey();
                    if (prop.startsWith("datanucleus.rdbms.adapter.")) {
                        dbaProps.put(prop, entry.getValue());
                    }
                }
                if (dbaProps.size() > 0) {
                    this.dba.setProperties(dbaProps);
                }
                this.initialiseSchema(conn, clr);
                this.logConfiguration();
            }
            catch (Exception e) {
                NucleusLogger.GENERAL.info(">> RDBMSStoreManager.init", e);
                throw e;
            }
            finally {
                mc.release();
            }
        }
        catch (NucleusException ne) {
            NucleusLogger.DATASTORE_SCHEMA.error(RDBMSStoreManager.LOCALISER_RDBMS.msg("050004"), ne);
            throw ne.setFatal();
        }
        catch (Exception e2) {
            final String msg = RDBMSStoreManager.LOCALISER_RDBMS.msg("050004") + ' ' + RDBMSStoreManager.LOCALISER_RDBMS.msg("050006") + ' ' + RDBMSStoreManager.LOCALISER_RDBMS.msg("048000", e2);
            NucleusLogger.DATASTORE_SCHEMA.error(msg, e2);
            throw new NucleusUserException(msg, e2).setFatal();
        }
    }
    
    @Override
    public String getQueryCacheKey() {
        return this.getStoreManagerKey() + "-" + this.getDatastoreAdapter().getVendorID();
    }
    
    protected void initialiseIdentifierFactory(final NucleusContext nucleusContext) {
        if (this.dba == null) {
            throw new NucleusException("DatastoreAdapter not yet created so cannot create IdentifierFactory!");
        }
        final String idFactoryName = this.getStringProperty("datanucleus.identifierFactory");
        final String idFactoryClassName = nucleusContext.getPluginManager().getAttributeValueForExtension("org.datanucleus.store_identifierfactory", "name", idFactoryName, "class-name");
        if (idFactoryClassName == null) {
            throw new NucleusUserException(RDBMSStoreManager.LOCALISER.msg("039003", idFactoryName)).setFatal();
        }
        try {
            final Map props = new HashMap();
            if (this.catalogName != null) {
                props.put("DefaultCatalog", this.catalogName);
            }
            if (this.schemaName != null) {
                props.put("DefaultSchema", this.schemaName);
            }
            String val = this.getStringProperty("datanucleus.identifier.case");
            if (val != null) {
                props.put("RequiredCase", val);
            }
            else {
                props.put("RequiredCase", this.getDefaultIdentifierCase());
            }
            val = this.getStringProperty("datanucleus.identifier.wordSeparator");
            if (val != null) {
                props.put("WordSeparator", val);
            }
            val = this.getStringProperty("datanucleus.identifier.tablePrefix");
            if (val != null) {
                props.put("TablePrefix", val);
            }
            val = this.getStringProperty("datanucleus.identifier.tableSuffix");
            if (val != null) {
                props.put("TableSuffix", val);
            }
            props.put("NamingFactory", this.getNamingFactory());
            final Class[] argTypes = { DatastoreAdapter.class, ClassConstants.CLASS_LOADER_RESOLVER, Map.class };
            final Object[] args = { this.dba, nucleusContext.getClassLoaderResolver(null), props };
            this.identifierFactory = (IdentifierFactory)nucleusContext.getPluginManager().createExecutableExtension("org.datanucleus.store_identifierfactory", "name", idFactoryName, "class-name", argTypes, args);
        }
        catch (ClassNotFoundException cnfe) {
            throw new NucleusUserException(RDBMSStoreManager.LOCALISER.msg("039004", idFactoryName, idFactoryClassName), cnfe).setFatal();
        }
        catch (Exception e) {
            NucleusLogger.PERSISTENCE.error("Exception creating IdentifierFactory", e);
            throw new NucleusException(RDBMSStoreManager.LOCALISER.msg("039005", idFactoryClassName), e).setFatal();
        }
    }
    
    @Override
    public boolean supportsValueStrategy(final String strategy) {
        return (strategy.equalsIgnoreCase("IDENTITY") || super.supportsValueStrategy(strategy)) && (!strategy.equalsIgnoreCase("IDENTITY") || this.dba.supportsOption("IdentityColumns")) && (!strategy.equalsIgnoreCase("SEQUENCE") || this.dba.supportsOption("Sequences"));
    }
    
    public MappedTypeManager getMappedTypeManager() {
        return this.mappedTypeMgr;
    }
    
    public IdentifierFactory getIdentifierFactory() {
        return this.identifierFactory;
    }
    
    public DatastoreAdapter getDatastoreAdapter() {
        return this.dba;
    }
    
    public MappingManager getMappingManager() {
        if (this.mappingManager == null) {
            this.mappingManager = this.dba.getMappingManager(this);
        }
        return this.mappingManager;
    }
    
    @Override
    public String getDefaultObjectProviderClassName() {
        return ReferentialJDOStateManager.class.getName();
    }
    
    public synchronized StoreData[] getStoreDataForDatastoreContainerObject(final DatastoreIdentifier tableIdentifier) {
        return this.storeDataMgr.getStoreDataForProperties("tableId", tableIdentifier, "table-owner", "true");
    }
    
    public Table getTable(final AbstractMemberMetaData mmd) {
        this.schemaLock.readLock().lock();
        try {
            final StoreData sd = this.storeDataMgr.get(mmd);
            if (sd != null && sd instanceof RDBMSStoreData) {
                return ((RDBMSStoreData)sd).getTable();
            }
            return null;
        }
        finally {
            this.schemaLock.readLock().unlock();
        }
    }
    
    public DatastoreClass getDatastoreClass(final String className, final ClassLoaderResolver clr) {
        DatastoreClass ct = null;
        if (className == null) {
            NucleusLogger.PERSISTENCE.error(RDBMSStoreManager.LOCALISER.msg("032015"));
            return null;
        }
        this.schemaLock.readLock().lock();
        try {
            final StoreData sd = this.storeDataMgr.get(className);
            if (sd != null && sd instanceof RDBMSStoreData) {
                ct = (DatastoreClass)((RDBMSStoreData)sd).getTable();
                if (ct != null) {
                    return ct;
                }
            }
        }
        finally {
            this.schemaLock.readLock().unlock();
        }
        boolean toBeAdded = false;
        if (clr != null) {
            final Class cls = clr.classForName(className);
            final ApiAdapter api = this.getApiAdapter();
            if (cls != null && !cls.isInterface() && api.isPersistable(cls)) {
                toBeAdded = true;
            }
        }
        else {
            toBeAdded = true;
        }
        boolean classKnown = false;
        if (toBeAdded) {
            this.addClass(className, clr);
            this.schemaLock.readLock().lock();
            try {
                final StoreData sd2 = this.storeDataMgr.get(className);
                if (sd2 != null && sd2 instanceof RDBMSStoreData) {
                    classKnown = true;
                    ct = (DatastoreClass)((RDBMSStoreData)sd2).getTable();
                }
            }
            finally {
                this.schemaLock.readLock().unlock();
            }
        }
        if (!classKnown && ct == null) {
            throw new NoTableManagedException(className);
        }
        return ct;
    }
    
    public DatastoreClass getDatastoreClass(final DatastoreIdentifier name) {
        this.schemaLock.readLock().lock();
        try {
            for (final StoreData sd : this.storeDataMgr.getManagedStoreData()) {
                if (sd instanceof RDBMSStoreData) {
                    final RDBMSStoreData tsd = (RDBMSStoreData)sd;
                    if (tsd.hasTable() && tsd.getDatastoreIdentifier().equals(name)) {
                        return (DatastoreClass)tsd.getTable();
                    }
                    continue;
                }
            }
            return null;
        }
        finally {
            this.schemaLock.readLock().unlock();
        }
    }
    
    public AbstractClassMetaData[] getClassesManagingTableForClass(final AbstractClassMetaData cmd, final ClassLoaderResolver clr) {
        if (cmd == null) {
            return null;
        }
        if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.COMPLETE_TABLE || cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.NEW_TABLE) {
            return new AbstractClassMetaData[] { cmd };
        }
        if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
            final String[] subclasses = this.getMetaDataManager().getSubclassesForClass(cmd.getFullClassName(), true);
            if (subclasses != null) {
                for (int i = 0; i < subclasses.length; ++i) {
                    if (!this.storeDataMgr.managesClass(subclasses[i])) {
                        this.addClass(subclasses[i], clr);
                    }
                }
            }
            final HashSet managingClasses = new HashSet();
            for (final StoreData data : this.storeDataMgr.getManagedStoreData()) {
                if (data.isFCO() && ((AbstractClassMetaData)data.getMetaData()).getSuperAbstractClassMetaData() != null && ((AbstractClassMetaData)data.getMetaData()).getSuperAbstractClassMetaData().getFullClassName().equals(cmd.getFullClassName())) {
                    final AbstractClassMetaData[] superCmds = this.getClassesManagingTableForClass((AbstractClassMetaData)data.getMetaData(), clr);
                    if (superCmds == null) {
                        continue;
                    }
                    for (int j = 0; j < superCmds.length; ++j) {
                        managingClasses.add(superCmds[j]);
                    }
                }
            }
            final Iterator managingClassesIter = managingClasses.iterator();
            final AbstractClassMetaData[] managingCmds = new AbstractClassMetaData[managingClasses.size()];
            int j = 0;
            while (managingClassesIter.hasNext()) {
                managingCmds[j++] = managingClassesIter.next();
            }
            return managingCmds;
        }
        if (cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
            return this.getClassesManagingTableForClass(cmd.getSuperAbstractClassMetaData(), clr);
        }
        return null;
    }
    
    public boolean isObjectInserted(final ObjectProvider sm, final int fieldNumber) {
        if (sm == null) {
            return false;
        }
        if (!sm.isInserting()) {
            return true;
        }
        final DatastoreClass latestTable = this.insertedDatastoreClassByStateManager.get(sm);
        if (latestTable == null) {
            return false;
        }
        final AbstractMemberMetaData mmd = sm.getClassMetaData().getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
        if (mmd == null) {
            return false;
        }
        String className = mmd.getClassName();
        if (mmd.isPrimaryKey()) {
            className = sm.getObject().getClass().getName();
        }
        for (DatastoreClass datastoreCls = latestTable; datastoreCls != null; datastoreCls = datastoreCls.getSuperDatastoreClass()) {
            if (datastoreCls.managesClass(className)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isObjectInserted(final ObjectProvider sm, final String className) {
        if (sm == null) {
            return false;
        }
        if (!sm.isInserting()) {
            return false;
        }
        final DatastoreClass latestTable = this.insertedDatastoreClassByStateManager.get(sm);
        if (latestTable != null) {
            for (DatastoreClass datastoreCls = latestTable; datastoreCls != null; datastoreCls = datastoreCls.getSuperDatastoreClass()) {
                if (datastoreCls.managesClass(className)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setObjectIsInsertedToLevel(final ObjectProvider sm, final DatastoreClass table) {
        this.insertedDatastoreClassByStateManager.put(sm, table);
        if (table.managesClass(sm.getClassMetaData().getFullClassName())) {
            sm.changeActivityState(ActivityState.INSERTING_CALLBACKS);
            this.insertedDatastoreClassByStateManager.remove(sm);
        }
    }
    
    @Override
    public Store getBackingStoreForField(final ClassLoaderResolver clr, final AbstractMemberMetaData mmd, final Class type) {
        if (mmd == null || mmd.isSerialized()) {
            return null;
        }
        Store store = this.backingStoreByMemberName.get(mmd.getFullFieldName());
        if (store != null) {
            return store;
        }
        synchronized (this.backingStoreByMemberName) {
            store = this.backingStoreByMemberName.get(mmd.getFullFieldName());
            if (store != null) {
                return store;
            }
            if (mmd.getMap() != null) {
                this.assertCompatibleFieldType(mmd, clr, type, MapMapping.class);
                store = this.getBackingStoreForMap(mmd, clr);
            }
            else if (mmd.getArray() != null) {
                this.assertCompatibleFieldType(mmd, clr, type, ArrayMapping.class);
                store = this.getBackingStoreForArray(mmd, clr);
            }
            else if (mmd.getCollection() != null) {
                this.assertCompatibleFieldType(mmd, clr, type, CollectionMapping.class);
                store = this.getBackingStoreForCollection(mmd, clr, type);
            }
            else {
                this.assertCompatibleFieldType(mmd, clr, type, PersistableMapping.class);
                store = this.getBackingStoreForPersistableRelation(mmd, clr, type);
            }
            this.backingStoreByMemberName.put(mmd.getFullFieldName(), store);
            return store;
        }
    }
    
    private void assertCompatibleFieldType(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Class type, final Class expectedMappingType) {
        DatastoreClass ownerTable = this.getDatastoreClass(mmd.getClassName(), clr);
        if (ownerTable == null) {
            final AbstractClassMetaData fieldTypeCmd = this.getMetaDataManager().getMetaDataForClass(mmd.getClassName(), clr);
            final AbstractClassMetaData[] tableOwnerCmds = this.getClassesManagingTableForClass(fieldTypeCmd, clr);
            if (tableOwnerCmds != null && tableOwnerCmds.length == 1) {
                ownerTable = this.getDatastoreClass(tableOwnerCmds[0].getFullClassName(), clr);
            }
        }
        if (ownerTable != null) {
            final JavaTypeMapping m = ownerTable.getMemberMapping(mmd);
            if (!expectedMappingType.isAssignableFrom(m.getClass())) {
                throw new IncompatibleFieldTypeException(mmd.getFullFieldName(), type.getName(), mmd.getTypeName());
            }
        }
    }
    
    private CollectionStore getBackingStoreForCollection(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Class type) {
        CollectionStore store = null;
        final Table datastoreTable = this.getTable(mmd);
        if (type == null) {
            if (datastoreTable == null) {
                if (mmd.getOrderMetaData() != null || List.class.isAssignableFrom(mmd.getType())) {
                    store = this.newFKListStore(mmd, clr);
                }
                else {
                    store = this.newFKSetStore(mmd, clr);
                }
            }
            else if (mmd.getOrderMetaData() != null || List.class.isAssignableFrom(mmd.getType())) {
                store = this.newJoinListStore(mmd, clr, datastoreTable);
            }
            else {
                store = this.newJoinSetStore(mmd, clr, datastoreTable);
            }
        }
        else if (datastoreTable == null) {
            if (SCOUtils.isListBased(type)) {
                store = this.newFKListStore(mmd, clr);
            }
            else {
                store = this.newFKSetStore(mmd, clr);
            }
        }
        else if (SCOUtils.isListBased(type)) {
            store = this.newJoinListStore(mmd, clr, datastoreTable);
        }
        else {
            store = this.newJoinSetStore(mmd, clr, datastoreTable);
        }
        return store;
    }
    
    private MapStore getBackingStoreForMap(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        MapStore store = null;
        final Table datastoreTable = this.getTable(mmd);
        if (datastoreTable == null) {
            store = this.newFKMapStore(mmd, clr);
        }
        else {
            store = this.newJoinMapStore(mmd, clr, datastoreTable);
        }
        return store;
    }
    
    private ArrayStore getBackingStoreForArray(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        final Table datastoreTable = this.getTable(mmd);
        ArrayStore store;
        if (datastoreTable != null) {
            store = this.newJoinArrayStore(mmd, clr, datastoreTable);
        }
        else {
            store = this.newFKArrayStore(mmd, clr);
        }
        return store;
    }
    
    private PersistableRelationStore getBackingStoreForPersistableRelation(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Class type) {
        PersistableRelationStore store = null;
        final Table datastoreTable = this.getTable(mmd);
        store = this.newPersistableRelationStore(mmd, clr, datastoreTable);
        return store;
    }
    
    public String getDefaultIdentifierCase() {
        return "UPPERCASE";
    }
    
    public MultiMap getSchemaCallbacks() {
        return this.schemaCallbacks;
    }
    
    public void addSchemaCallback(final String className, final AbstractMemberMetaData mmd) {
        final Collection coll = this.schemaCallbacks.get(className);
        if (coll == null || !coll.contains(mmd)) {
            this.schemaCallbacks.put(className, mmd);
        }
    }
    
    @Override
    public boolean isJdbcStore() {
        return true;
    }
    
    @Override
    protected void logConfiguration() {
        super.logConfiguration();
        if (NucleusLogger.DATASTORE.isDebugEnabled()) {
            NucleusLogger.DATASTORE.debug("Datastore Adapter : " + this.dba.getClass().getName());
            NucleusLogger.DATASTORE.debug("Datastore : name=\"" + this.dba.getDatastoreProductName() + "\"" + " version=\"" + this.dba.getDatastoreProductVersion() + "\"");
            NucleusLogger.DATASTORE.debug("Datastore Driver : name=\"" + this.dba.getDatastoreDriverName() + "\"" + " version=\"" + this.dba.getDatastoreDriverVersion() + "\"");
            String primaryDS = null;
            if (this.getConnectionFactory() != null) {
                primaryDS = "DataSource[input DataSource]";
            }
            else if (this.getConnectionFactoryName() != null) {
                primaryDS = "JNDI[" + this.getConnectionFactoryName() + "]";
            }
            else {
                primaryDS = "URL[" + this.getConnectionURL() + "]";
            }
            NucleusLogger.DATASTORE.debug("Primary Connection Factory : " + primaryDS);
            String secondaryDS = null;
            if (this.getConnectionFactory2() != null) {
                secondaryDS = "DataSource[input DataSource]";
            }
            else if (this.getConnectionFactory2Name() != null) {
                secondaryDS = "JNDI[" + this.getConnectionFactory2Name() + "]";
            }
            else if (this.getConnectionURL() != null) {
                secondaryDS = "URL[" + this.getConnectionURL() + "]";
            }
            else {
                secondaryDS = primaryDS;
            }
            NucleusLogger.DATASTORE.debug("Secondary Connection Factory : " + secondaryDS);
            if (this.identifierFactory != null) {
                NucleusLogger.DATASTORE.debug("Datastore Identifiers : factory=\"" + this.getStringProperty("datanucleus.identifierFactory") + "\"" + " case=" + this.identifierFactory.getIdentifierCase().toString() + ((this.catalogName != null) ? (" catalog=" + this.catalogName) : "") + ((this.schemaName != null) ? (" schema=" + this.schemaName) : ""));
                NucleusLogger.DATASTORE.debug("Supported Identifier Cases : " + (this.dba.supportsOption("LowerCaseIdentifiers") ? "lowercase " : "") + (this.dba.supportsOption("LowerCaseQuotedIdentifiers") ? "\"lowercase\" " : "") + (this.dba.supportsOption("MixedCaseIdentifiers") ? "MixedCase " : "") + (this.dba.supportsOption("MixedCaseQuotedIdentifiers") ? "\"MixedCase\" " : "") + (this.dba.supportsOption("UpperCaseIdentifiers") ? "UPPERCASE " : "") + (this.dba.supportsOption("UpperCaseQuotedIdentifiers") ? "\"UPPERCASE\" " : "") + (this.dba.supportsOption("MixedCaseSensitiveIdentifiers") ? "MixedCase-Sensitive " : "") + (this.dba.supportsOption("MixedCaseQuotedSensitiveIdentifiers") ? "\"MixedCase-Sensitive\" " : ""));
                NucleusLogger.DATASTORE.debug("Supported Identifier Lengths (max) : Table=" + this.dba.getDatastoreIdentifierMaxLength(IdentifierType.TABLE) + " Column=" + this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN) + " Constraint=" + this.dba.getDatastoreIdentifierMaxLength(IdentifierType.CANDIDATE_KEY) + " Index=" + this.dba.getDatastoreIdentifierMaxLength(IdentifierType.INDEX) + " Delimiter=" + this.dba.getIdentifierQuoteString());
                NucleusLogger.DATASTORE.debug("Support for Identifiers in DDL : catalog=" + this.dba.supportsOption("CatalogInTableDefinition") + " schema=" + this.dba.supportsOption("SchemaInTableDefinition"));
            }
            NucleusLogger.DATASTORE.debug("Datastore : " + (this.getBooleanProperty("datanucleus.rdbms.checkExistTablesOrViews") ? "checkTableViewExistence" : "") + ", rdbmsConstraintCreateMode=" + this.getStringProperty("datanucleus.rdbms.constraintCreateMode") + ", initialiseColumnInfo=" + this.getStringProperty("datanucleus.rdbms.initializeColumnInfo"));
            final int batchLimit = this.getIntProperty("datanucleus.rdbms.statementBatchLimit");
            final boolean supportBatching = this.dba.supportsOption("StatementBatching");
            if (supportBatching) {
                NucleusLogger.DATASTORE.debug("Support Statement Batching : yes (max-batch-size=" + ((batchLimit == -1) ? "UNLIMITED" : ("" + batchLimit)) + ")");
            }
            else {
                NucleusLogger.DATASTORE.debug("Support Statement Batching : no");
            }
            NucleusLogger.DATASTORE.debug("Queries : Results direction=" + this.getStringProperty("datanucleus.rdbms.query.fetchDirection") + ", type=" + this.getStringProperty("datanucleus.rdbms.query.resultSetType") + ", concurrency=" + this.getStringProperty("datanucleus.rdbms.query.resultSetConcurrency"));
            NucleusLogger.DATASTORE.debug("Java-Types : string-default-length=" + this.getIntProperty("datanucleus.rdbms.stringDefaultLength"));
            final RDBMSTypesInfo typesInfo = (RDBMSTypesInfo)this.schemaHandler.getSchemaData(null, "types", null);
            if (typesInfo != null && typesInfo.getNumberOfChildren() > 0) {
                final StringBuffer typeStr = new StringBuffer();
                final Iterator jdbcTypesIter = typesInfo.getChildren().keySet().iterator();
                while (jdbcTypesIter.hasNext()) {
                    final String jdbcTypeStr = jdbcTypesIter.next();
                    int jdbcTypeNumber = 0;
                    try {
                        jdbcTypeNumber = Short.valueOf(jdbcTypeStr);
                    }
                    catch (NumberFormatException ex) {}
                    String typeName = JDBCUtils.getNameForJDBCType(jdbcTypeNumber);
                    if (typeName == null) {
                        typeName = "[id=" + jdbcTypeNumber + "]";
                    }
                    typeStr.append(typeName);
                    if (jdbcTypesIter.hasNext()) {
                        typeStr.append(", ");
                    }
                }
                NucleusLogger.DATASTORE.debug("JDBC-Types : " + (Object)typeStr);
            }
            NucleusLogger.DATASTORE.debug("===========================================================");
        }
    }
    
    @Override
    public void close() {
        this.dba = null;
        super.close();
        this.classAdder = null;
    }
    
    @Override
    public NucleusSequence getNucleusSequence(final ExecutionContext ec, final SequenceMetaData seqmd) {
        return new NucleusSequenceImpl(ec, this, seqmd);
    }
    
    @Override
    public NucleusConnection getNucleusConnection(final ExecutionContext ec) {
        final boolean enlisted = ec.getTransaction().isActive();
        ConnectionFactory cf = null;
        if (enlisted) {
            cf = this.connectionMgr.lookupConnectionFactory(this.primaryConnectionFactoryName);
        }
        else {
            cf = this.connectionMgr.lookupConnectionFactory(this.secondaryConnectionFactoryName);
        }
        final ManagedConnection mc = cf.getConnection(enlisted ? ec : null, ec.getTransaction(), null);
        mc.lock();
        final Runnable closeRunnable = new Runnable() {
            @Override
            public void run() {
                mc.unlock();
                if (!enlisted) {
                    try {
                        ((Connection)mc.getConnection()).close();
                    }
                    catch (SQLException sqle) {
                        throw new NucleusDataStoreException(sqle.getMessage());
                    }
                }
            }
        };
        return new NucleusConnectionImpl(mc.getConnection(), closeRunnable);
    }
    
    public SQLController getSQLController() {
        return this.sqlController;
    }
    
    public SQLExpressionFactory getSQLExpressionFactory() {
        return this.expressionFactory;
    }
    
    private void initialiseSchema(final Connection conn, final ClassLoaderResolver clr) throws Exception {
        if (this.schemaName == null && this.catalogName == null) {
            try {
                try {
                    this.catalogName = this.dba.getCatalogName(conn);
                    this.schemaName = this.dba.getSchemaName(conn);
                }
                catch (UnsupportedOperationException e2) {
                    if (!this.readOnlyDatastore && !this.fixedDatastore) {
                        final ProbeTable pt = new ProbeTable(this);
                        pt.initialize(clr);
                        pt.create(conn);
                        try {
                            final String[] schema_details = pt.findSchemaDetails(conn);
                            if (schema_details != null) {
                                this.catalogName = schema_details[0];
                                this.schemaName = schema_details[1];
                            }
                        }
                        finally {
                            pt.drop(conn);
                        }
                    }
                }
            }
            catch (SQLException e) {
                final String msg = RDBMSStoreManager.LOCALISER_RDBMS.msg("050005", e.getMessage()) + ' ' + RDBMSStoreManager.LOCALISER_RDBMS.msg("050006");
                NucleusLogger.DATASTORE_SCHEMA.warn(msg);
            }
        }
        if (!this.readOnlyDatastore) {
            this.dba.initialiseDatastore(conn);
        }
        final String autoStartMechanismName = this.nucleusContext.getPersistenceConfiguration().getStringProperty("datanucleus.autoStartMechanism");
        if ((this.readOnlyDatastore || this.fixedDatastore) && "SchemaTable".equals(autoStartMechanismName)) {
            this.nucleusContext.getPersistenceConfiguration().setProperty("datanucleus.autoStartMechanism", "None");
        }
    }
    
    private void clearSchemaData() {
        this.deregisterAllStoreData();
        this.schemaHandler.clear();
        final ManagedConnection mc = this.getConnection(-1);
        try {
            this.dba.initialiseTypes(this.schemaHandler, mc);
        }
        finally {
            mc.release();
        }
        ((RDBMSPersistenceHandler)this.persistenceHandler).removeAllRequests();
    }
    
    public String getCatalogName() {
        return this.catalogName;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    @Override
    public Date getDatastoreDate() {
        Date serverDate = null;
        final String dateStmt = this.dba.getDatastoreDateStatement();
        ManagedConnection mconn = null;
        try {
            mconn = this.getConnection(0);
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = this.getSQLController().getStatementForQuery(mconn, dateStmt);
                rs = this.getSQLController().executeStatementQuery(null, mconn, dateStmt, ps);
                if (!rs.next()) {
                    return null;
                }
                final Timestamp time = rs.getTimestamp(1, this.getCalendarForDateTimezone());
                serverDate = new Date(time.getTime());
            }
            catch (SQLException sqle) {
                final String msg = RDBMSStoreManager.LOCALISER_RDBMS.msg("050052", sqle.getMessage());
                NucleusLogger.DATASTORE.warn(msg, sqle);
                throw new NucleusUserException(msg, sqle).setFatal();
            }
            finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    this.getSQLController().closeStatement(mconn, ps);
                }
            }
        }
        catch (SQLException sqle2) {
            final String msg2 = RDBMSStoreManager.LOCALISER_RDBMS.msg("050052", sqle2.getMessage());
            NucleusLogger.DATASTORE.warn(msg2, sqle2);
            throw new NucleusException(msg2, sqle2).setFatal();
        }
        finally {
            mconn.release();
        }
        return serverDate;
    }
    
    @Override
    public void addClasses(final String[] classNames, final ClassLoaderResolver clr) {
        try {
            this.schemaLock.writeLock().lock();
            if (this.classAdder != null) {
                this.classAdder.addClasses(classNames, clr);
                return;
            }
            if (classNames != null && classNames.length > 0) {
                new ClassAdder(classNames, (Writer)null).execute(clr);
            }
        }
        finally {
            this.schemaLock.writeLock().unlock();
        }
    }
    
    @Override
    public void removeAllClasses(final ClassLoaderResolver clr) {
        final DeleteTablesSchemaTransaction deleteTablesTxn = new DeleteTablesSchemaTransaction(this, 2, this.storeDataMgr);
        boolean success = true;
        try {
            deleteTablesTxn.execute(clr);
        }
        catch (NucleusException ne) {
            success = false;
            throw ne;
        }
        finally {
            if (success) {
                this.clearSchemaData();
            }
        }
    }
    
    public Writer getDdlWriter() {
        return this.ddlWriter;
    }
    
    public boolean getCompleteDDL() {
        return this.completeDDL;
    }
    
    public boolean hasWrittenDdlStatement(final String stmt) {
        return this.writtenDdlStatements != null && this.writtenDdlStatements.contains(stmt);
    }
    
    public void addWrittenDdlStatement(final String stmt) {
        if (this.writtenDdlStatements != null) {
            this.writtenDdlStatements.add(stmt);
        }
    }
    
    public void validateTable(final TableImpl table, final ClassLoaderResolver clr) {
        final ValidateTableSchemaTransaction validateTblTxn = new ValidateTableSchemaTransaction(this, 2, table);
        validateTblTxn.execute(clr);
    }
    
    @Override
    public String getClassNameForObjectID(final Object id, final ClassLoaderResolver clr, final ExecutionContext ec) {
        if (id instanceof SCOID) {
            return ((SCOID)id).getSCOClass();
        }
        final ApiAdapter api = this.getApiAdapter();
        final List<AbstractClassMetaData> rootCmds = new ArrayList<AbstractClassMetaData>();
        if (id instanceof OID) {
            final OID oid = (OID)id;
            final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(oid.getPcClass(), clr);
            rootCmds.add(cmd);
            if (cmd.getIdentityType() != IdentityType.DATASTORE) {
                throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050022", id, cmd.getFullClassName()));
            }
        }
        else if (api.isSingleFieldIdentity(id)) {
            final String className = api.getTargetClassNameForSingleFieldIdentity(id);
            final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(className, clr);
            rootCmds.add(cmd);
            if (cmd.getIdentityType() != IdentityType.APPLICATION || !cmd.getObjectidClass().equals(id.getClass().getName())) {
                throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050022", id, cmd.getFullClassName()));
            }
        }
        else {
            final Collection<AbstractClassMetaData> pkCmds = this.getMetaDataManager().getClassMetaDataWithApplicationId(id.getClass().getName());
            if (pkCmds != null && pkCmds.size() > 0) {
                for (final AbstractClassMetaData pkCmd : pkCmds) {
                    AbstractClassMetaData cmdToSwap = null;
                    boolean toAdd = true;
                    for (final AbstractClassMetaData rootCmd : rootCmds) {
                        if (rootCmd.isDescendantOf(pkCmd)) {
                            cmdToSwap = rootCmd;
                            toAdd = false;
                            break;
                        }
                        if (!pkCmd.isDescendantOf(rootCmd)) {
                            continue;
                        }
                        toAdd = false;
                    }
                    if (cmdToSwap != null) {
                        rootCmds.remove(cmdToSwap);
                        rootCmds.add(pkCmd);
                    }
                    else {
                        if (!toAdd) {
                            continue;
                        }
                        rootCmds.add(pkCmd);
                    }
                }
            }
            if (rootCmds.size() == 0) {
                return null;
            }
        }
        final AbstractClassMetaData rootCmd2 = rootCmds.get(0);
        if (ec != null) {
            if (rootCmds.size() != 1) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    final StringBuffer str = new StringBuffer();
                    final Iterator<AbstractClassMetaData> rootCmdIter = rootCmds.iterator();
                    while (rootCmdIter.hasNext()) {
                        final AbstractClassMetaData cmd2 = rootCmdIter.next();
                        str.append(cmd2.getFullClassName());
                        if (rootCmdIter.hasNext()) {
                            str.append(",");
                        }
                    }
                    NucleusLogger.PERSISTENCE.debug("Performing query using UNION on " + str.toString() + " and their subclasses to find the class of " + id);
                }
                return RDBMSStoreHelper.getClassNameForIdUsingUnion(this, ec, id, rootCmds);
            }
            final Collection<String> subclasses = this.getSubClassesForClass(rootCmd2.getFullClassName(), true, clr);
            if (!rootCmd2.isImplementationOfPersistentDefinition() && (subclasses == null || subclasses.isEmpty())) {
                return rootCmd2.getFullClassName();
            }
            if (rootCmd2.hasDiscriminatorStrategy()) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug("Performing query using discriminator on " + rootCmd2.getFullClassName() + " and its subclasses to find the class of " + id);
                }
                return RDBMSStoreHelper.getClassNameForIdUsingDiscriminator(this, ec, id, rootCmd2);
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug("Performing query using UNION on " + rootCmd2.getFullClassName() + " and its subclasses to find the class of " + id);
            }
            return RDBMSStoreHelper.getClassNameForIdUsingUnion(this, ec, id, rootCmds);
        }
        else {
            if (rootCmds.size() > 1) {
                if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                    NucleusLogger.PERSISTENCE.debug("Id \"" + id + "\" has been determined to be the id of class " + rootCmd2.getFullClassName() + " : this is the first of " + rootCmds.size() + " possible" + ", but unable to determine further");
                }
                return rootCmd2.getFullClassName();
            }
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug("Id \"" + id + "\" has been determined to be the id of class " + rootCmd2.getFullClassName() + " : unable to determine if actually of a subclass");
            }
            return rootCmd2.getFullClassName();
        }
    }
    
    public FieldManager getFieldManagerForResultProcessing(final ObjectProvider op, final ResultSet rs, final StatementClassMapping resultMappings) {
        return new ResultSetGetter(this, op, rs, resultMappings);
    }
    
    public FieldManager getFieldManagerForResultProcessing(final ExecutionContext ec, final ResultSet rs, final StatementClassMapping resultMappings, final AbstractClassMetaData cmd) {
        return new ResultSetGetter(this, ec, rs, resultMappings, cmd);
    }
    
    public FieldManager getFieldManagerForStatementGeneration(final ObjectProvider sm, final PreparedStatement ps, final StatementClassMapping stmtMappings) {
        return new ParameterSetter(sm, ps, stmtMappings);
    }
    
    public Object getResultValueAtPosition(final ResultSet rs, final JavaTypeMapping mapping, final int position) {
        try {
            return rs.getObject(position);
        }
        catch (SQLException sqle) {
            throw new NucleusDataStoreException(sqle.getMessage(), sqle);
        }
    }
    
    @Override
    protected Object getStrategyValueForGenerator(final ValueGenerator generator, final ExecutionContext ec) {
        Object oid = null;
        synchronized (generator) {
            if (generator instanceof AbstractDatastoreGenerator) {
                final RDBMSStoreManager thisStoreMgr = this;
                final ValueGenerationConnectionProvider connProvider = new ValueGenerationConnectionProvider() {
                    ManagedConnection mconn;
                    
                    @Override
                    public ManagedConnection retrieveConnection() {
                        if (RDBMSStoreManager.this.getStringProperty("datanucleus.valuegeneration.transactionAttribute").equalsIgnoreCase("UsePM")) {
                            this.mconn = thisStoreMgr.getConnection(ec);
                        }
                        else {
                            final int isolationLevel = TransactionUtils.getTransactionIsolationLevelForName(RDBMSStoreManager.this.getStringProperty("datanucleus.valuegeneration.transactionIsolation"));
                            this.mconn = thisStoreMgr.getConnection(isolationLevel);
                        }
                        return this.mconn;
                    }
                    
                    @Override
                    public void releaseConnection() {
                        try {
                            if (RDBMSStoreManager.this.getStringProperty("datanucleus.valuegeneration.transactionAttribute").equalsIgnoreCase("UsePM")) {
                                this.mconn.release();
                            }
                            else {
                                this.mconn.release();
                            }
                            this.mconn = null;
                        }
                        catch (NucleusException e) {
                            final String msg = RDBMSStoreManager.LOCALISER_RDBMS.msg("050025", e);
                            NucleusLogger.VALUEGENERATION.error(msg);
                            throw new NucleusDataStoreException(msg, e);
                        }
                    }
                };
                ((AbstractDatastoreGenerator)generator).setConnectionProvider(connProvider);
            }
            oid = generator.next();
        }
        return oid;
    }
    
    @Override
    protected Properties getPropertiesForGenerator(final AbstractClassMetaData cmd, final int absoluteFieldNumber, final ExecutionContext ec, final SequenceMetaData seqmd, final TableGeneratorMetaData tablegenmd) {
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
        DatastoreClass tbl = this.getDatastoreClass(cmd.getBaseAbstractClassMetaData().getFullClassName(), ec.getClassLoaderResolver());
        if (tbl == null) {
            tbl = this.getTableForStrategy(cmd, absoluteFieldNumber, ec.getClassLoaderResolver());
        }
        JavaTypeMapping m = null;
        if (mmd != null) {
            m = tbl.getMemberMapping(mmd);
            if (m == null) {
                tbl = this.getTableForStrategy(cmd, absoluteFieldNumber, ec.getClassLoaderResolver());
                m = tbl.getMemberMapping(mmd);
            }
        }
        else {
            m = tbl.getIdMapping();
        }
        final StringBuffer columnsName = new StringBuffer();
        for (int i = 0; i < m.getNumberOfDatastoreMappings(); ++i) {
            if (i > 0) {
                columnsName.append(",");
            }
            columnsName.append(m.getDatastoreMapping(i).getColumn().getIdentifier().toString());
        }
        final Properties properties = new Properties();
        properties.setProperty("class-name", cmd.getFullClassName());
        properties.put("root-class-name", cmd.getBaseAbstractClassMetaData().getFullClassName());
        if (mmd != null) {
            properties.setProperty("field-name", mmd.getFullFieldName());
        }
        if (cmd.getCatalog() != null) {
            properties.setProperty("catalog-name", cmd.getCatalog());
        }
        else if (!StringUtils.isWhitespace(this.catalogName)) {
            properties.setProperty("catalog-name", this.catalogName);
        }
        if (cmd.getSchema() != null) {
            properties.setProperty("schema-name", cmd.getSchema());
        }
        else if (!StringUtils.isWhitespace(this.schemaName)) {
            properties.setProperty("schema-name", this.schemaName);
        }
        properties.setProperty("table-name", tbl.getIdentifier().toString());
        properties.setProperty("column-name", columnsName.toString());
        if (sequence != null) {
            properties.setProperty("sequence-name", sequence);
        }
        if (extensions != null) {
            for (int j = 0; j < extensions.length; ++j) {
                properties.put(extensions[j].getKey(), extensions[j].getValue());
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
            properties.remove("table-name");
            properties.remove("column-name");
        }
        else if (strategy == IdentityStrategy.INCREMENT && tablegenmd == null) {
            if (!properties.containsKey("key-cache-size")) {
                final int allocSize = this.getIntProperty("datanucleus.valuegeneration.increment.allocationSize");
                properties.put("key-cache-size", "" + allocSize);
            }
        }
        else if (strategy == IdentityStrategy.SEQUENCE && seqmd != null) {
            if (StringUtils.isWhitespace(sequence) && seqmd.getName() != null) {
                properties.put("sequence-name", seqmd.getName());
            }
            if (seqmd.getDatastoreSequence() != null) {
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
                    for (int k = 0; k < seqExtensions.length; ++k) {
                        properties.put(seqExtensions[k].getKey(), seqExtensions[k].getValue());
                    }
                }
            }
        }
        return properties;
    }
    
    private DatastoreClass getTableForStrategy(final AbstractClassMetaData cmd, final int fieldNumber, final ClassLoaderResolver clr) {
        DatastoreClass t = this.getDatastoreClass(cmd.getFullClassName(), clr);
        if (t == null && cmd.getInheritanceMetaData().getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
            throw new NucleusUserException(RDBMSStoreManager.LOCALISER.msg("032013", cmd.getFullClassName()));
        }
        if (fieldNumber >= 0) {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(fieldNumber);
            t = t.getBaseDatastoreClassWithMember(mmd);
        }
        else if (t != null) {
            boolean has_superclass = true;
            while (has_superclass) {
                final DatastoreClass supert = t.getSuperDatastoreClass();
                if (supert != null) {
                    t = supert;
                }
                else {
                    has_superclass = false;
                }
            }
        }
        return t;
    }
    
    @Override
    protected String getStrategyForNative(final AbstractClassMetaData cmd, final int absFieldNumber) {
        if (!this.getBooleanProperty("datanucleus.rdbms.useLegacyNativeValueStrategy")) {
            return super.getStrategyForNative(cmd, absFieldNumber);
        }
        String sequence = null;
        if (absFieldNumber >= 0) {
            sequence = cmd.getMetaDataForManagedMemberAtAbsolutePosition(absFieldNumber).getSequence();
        }
        else {
            sequence = cmd.getIdentityMetaData().getSequence();
        }
        if (this.dba.supportsOption("Sequences") && sequence != null) {
            return "sequence";
        }
        return "table-sequence";
    }
    
    public SQLTypeInfo getSQLTypeInfoForJDBCType(final int jdbcType) throws UnsupportedDataTypeException {
        return this.getSQLTypeInfoForJDBCType(jdbcType, "DEFAULT");
    }
    
    public SQLTypeInfo getSQLTypeInfoForJDBCType(final int jdbcType, final String sqlType) throws UnsupportedDataTypeException {
        final RDBMSTypesInfo typesInfo = (RDBMSTypesInfo)this.schemaHandler.getSchemaData(null, "types", null);
        final JDBCTypeInfo jdbcTypeInfo = (JDBCTypeInfo)typesInfo.getChild("" + jdbcType);
        if (jdbcTypeInfo.getNumberOfChildren() == 0) {
            throw new UnsupportedDataTypeException(RDBMSStoreManager.LOCALISER.msg("051005", JDBCUtils.getNameForJDBCType(jdbcType)));
        }
        SQLTypeInfo sqlTypeInfo = (SQLTypeInfo)jdbcTypeInfo.getChild((sqlType != null) ? sqlType : "DEFAULT");
        if (sqlTypeInfo == null && sqlType != null) {
            sqlTypeInfo = (SQLTypeInfo)jdbcTypeInfo.getChild(sqlType.toUpperCase());
            if (sqlTypeInfo == null) {
                sqlTypeInfo = (SQLTypeInfo)jdbcTypeInfo.getChild(sqlType.toLowerCase());
                if (sqlTypeInfo == null) {
                    NucleusLogger.DATASTORE_SCHEMA.warn("Attempt to find JDBC driver 'typeInfo' for jdbc-type=" + JDBCUtils.getNameForJDBCType(jdbcType) + " but sql-type=" + sqlType + " is not found. Using default sql-type for this jdbc-type.");
                    sqlTypeInfo = (SQLTypeInfo)jdbcTypeInfo.getChild("DEFAULT");
                }
            }
        }
        return sqlTypeInfo;
    }
    
    public RDBMSColumnInfo getColumnInfoForColumnName(final Table table, final Connection conn, final DatastoreIdentifier column) throws SQLException {
        final RDBMSColumnInfo colInfo = (RDBMSColumnInfo)this.schemaHandler.getSchemaData(conn, "column", new Object[] { table, column.getIdentifierName() });
        return colInfo;
    }
    
    public List getColumnInfoForTable(final Table table, final Connection conn) throws SQLException {
        final RDBMSTableInfo tableInfo = (RDBMSTableInfo)this.schemaHandler.getSchemaData(conn, "columns", new Object[] { table });
        if (tableInfo == null) {
            return Collections.EMPTY_LIST;
        }
        final List cols = new ArrayList(tableInfo.getNumberOfChildren());
        cols.addAll(tableInfo.getChildren());
        return cols;
    }
    
    public void invalidateColumnInfoForTable(final Table table) {
        final RDBMSSchemaInfo schemaInfo = (RDBMSSchemaInfo)this.schemaHandler.getSchemaData(null, "tables", null);
        if (schemaInfo != null && schemaInfo.getNumberOfChildren() > 0) {
            schemaInfo.getChildren().remove(table.getIdentifier().getFullyQualifiedName(true));
        }
    }
    
    public Collection getManagedTables(final String catalog, final String schema) {
        if (this.storeDataMgr == null) {
            return Collections.EMPTY_SET;
        }
        final Collection tables = new HashSet();
        for (final RDBMSStoreData sd : this.storeDataMgr.getManagedStoreData()) {
            if (sd.getTable() != null) {
                final DatastoreIdentifier identifier = sd.getTable().getIdentifier();
                boolean catalogMatches = true;
                boolean schemaMatches = true;
                if (catalog != null && identifier.getCatalogName() != null && !catalog.equals(identifier.getCatalogName())) {
                    catalogMatches = false;
                }
                if (schema != null && identifier.getSchemaName() != null && !schema.equals(identifier.getSchemaName())) {
                    schemaMatches = false;
                }
                if (!catalogMatches || !schemaMatches) {
                    continue;
                }
                tables.add(sd.getTable());
            }
        }
        return tables;
    }
    
    public void resolveIdentifierMacro(final MacroString.IdentifierMacro im, final ClassLoaderResolver clr) {
        final DatastoreClass ct = this.getDatastoreClass(im.className, clr);
        if (im.fieldName == null) {
            im.value = ct.getIdentifier().toString();
            return;
        }
        JavaTypeMapping m;
        if (im.fieldName.equals("this")) {
            if (!(ct instanceof ClassTable)) {
                throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050034", im.className));
            }
            if (im.subfieldName != null) {
                throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050035", im.className, im.fieldName, im.subfieldName));
            }
            m = ct.getIdMapping();
        }
        else {
            final AbstractMemberMetaData mmd = this.getMetaDataManager().getMetaDataForMember(im.className, im.fieldName, clr);
            m = ct.getMemberMapping(mmd);
            final Table t = this.getTable(mmd);
            if (im.subfieldName == null) {
                if (t != null) {
                    im.value = t.getIdentifier().toString();
                    return;
                }
            }
            else if (t instanceof CollectionTable) {
                final CollectionTable collTable = (CollectionTable)t;
                if (im.subfieldName.equals("owner")) {
                    m = collTable.getOwnerMapping();
                }
                else if (im.subfieldName.equals("element")) {
                    m = collTable.getElementMapping();
                }
                else {
                    if (!im.subfieldName.equals("index")) {
                        throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050036", im.subfieldName, im));
                    }
                    m = collTable.getOrderMapping();
                }
            }
            else {
                if (!(t instanceof MapTable)) {
                    throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050035", im.className, im.fieldName, im.subfieldName));
                }
                final MapTable mt = (MapTable)t;
                if (im.subfieldName.equals("owner")) {
                    m = mt.getOwnerMapping();
                }
                else if (im.subfieldName.equals("key")) {
                    m = mt.getKeyMapping();
                }
                else {
                    if (!im.subfieldName.equals("value")) {
                        throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050037", im.subfieldName, im));
                    }
                    m = mt.getValueMapping();
                }
            }
        }
        im.value = m.getDatastoreMapping(0).getColumn().getIdentifier().toString();
    }
    
    @Override
    public void printInformation(final String category, final PrintStream ps) throws Exception {
        final DatastoreAdapter dba = this.getDatastoreAdapter();
        super.printInformation(category, ps);
        if (category.equalsIgnoreCase("DATASTORE")) {
            ps.println(dba.toString());
            ps.println();
            ps.println("Database TypeInfo");
            final RDBMSTypesInfo typesInfo = (RDBMSTypesInfo)this.schemaHandler.getSchemaData(null, "types", null);
            if (typesInfo != null) {
                for (final String jdbcTypeStr : typesInfo.getChildren().keySet()) {
                    short jdbcTypeNumber = 0;
                    try {
                        jdbcTypeNumber = Short.valueOf(jdbcTypeStr);
                    }
                    catch (NumberFormatException ex) {}
                    final JDBCTypeInfo jdbcType = (JDBCTypeInfo)typesInfo.getChild(jdbcTypeStr);
                    final Collection sqlTypeNames = jdbcType.getChildren().keySet();
                    final String typeStr = "JDBC Type=" + JDBCUtils.getNameForJDBCType(jdbcTypeNumber) + " sqlTypes=" + StringUtils.collectionToString(sqlTypeNames);
                    ps.println(typeStr);
                    final SQLTypeInfo sqlType = (SQLTypeInfo)jdbcType.getChild("DEFAULT");
                    ps.println(sqlType);
                }
            }
            ps.println("");
            ps.println("Database Keywords");
            final Iterator reservedWordsIter = dba.iteratorReservedWords();
            while (reservedWordsIter.hasNext()) {
                final Object words = reservedWordsIter.next();
                ps.println(words);
            }
            ps.println("");
        }
        else if (category.equalsIgnoreCase("SCHEMA")) {
            ps.println(dba.toString());
            ps.println();
            ps.println("TABLES");
            final ManagedConnection mc = this.getConnection(-1);
            try {
                final Connection conn = (Connection)mc.getConnection();
                final RDBMSSchemaInfo schemaInfo = (RDBMSSchemaInfo)this.schemaHandler.getSchemaData(conn, "tables", new Object[] { this.catalogName, this.schemaName });
                if (schemaInfo != null) {
                    for (final RDBMSTableInfo tableInfo : schemaInfo.getChildren().values()) {
                        ps.println(tableInfo);
                        for (final RDBMSColumnInfo colInfo : tableInfo.getChildren()) {
                            ps.println(colInfo);
                        }
                    }
                }
            }
            finally {
                if (mc != null) {
                    mc.release();
                }
            }
            ps.println("");
        }
    }
    
    public Table newJoinDatastoreContainerObject(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        if (mmd.getJoinMetaData() == null) {
            final AbstractMemberMetaData[] relatedMmds = mmd.getRelatedMemberMetaData(clr);
            if (relatedMmds == null || relatedMmds[0].getJoinMetaData() == null) {
                Class element_class;
                if (mmd.hasCollection()) {
                    element_class = clr.classForName(mmd.getCollection().getElementType());
                }
                else if (mmd.hasMap()) {
                    final MapMetaData mapmd = (MapMetaData)mmd.getContainer();
                    if (mmd.getValueMetaData() != null && mmd.getValueMetaData().getMappedBy() != null) {
                        element_class = clr.classForName(mapmd.getKeyType());
                    }
                    else {
                        if (mmd.getKeyMetaData() == null || mmd.getKeyMetaData().getMappedBy() == null) {
                            throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050050", mmd.getFullFieldName()));
                        }
                        element_class = clr.classForName(mapmd.getValueType());
                    }
                }
                else {
                    if (!mmd.hasArray()) {
                        return null;
                    }
                    element_class = clr.classForName(mmd.getTypeName()).getComponentType();
                }
                if (this.getMetaDataManager().getMetaDataForClass(element_class, clr) != null) {
                    return null;
                }
                if (ClassUtils.isReferenceType(element_class)) {
                    return null;
                }
                throw new NucleusUserException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050049", mmd.getFullFieldName(), mmd.toString()));
            }
        }
        final Table joinTable = this.getTable(mmd);
        if (joinTable != null) {
            return joinTable;
        }
        if (this.classAdder == null) {
            throw new IllegalStateException(RDBMSStoreManager.LOCALISER_RDBMS.msg("050016"));
        }
        if (mmd.getType().isArray()) {
            return this.classAdder.addJoinTableForContainer(mmd, clr, 3);
        }
        if (Map.class.isAssignableFrom(mmd.getType())) {
            return this.classAdder.addJoinTableForContainer(mmd, clr, 2);
        }
        if (Collection.class.isAssignableFrom(mmd.getType())) {
            return this.classAdder.addJoinTableForContainer(mmd, clr, 1);
        }
        return this.classAdder.addJoinTableForContainer(mmd, clr, 4);
    }
    
    public void registerTableInitialized(final Table table) {
        if (this.classAdder != null) {
            this.classAdder.tablesRecentlyInitialized.add(table);
        }
    }
    
    @Override
    public Collection getSupportedOptions() {
        final Set set = new HashSet();
        set.add("ORM");
        set.add("NonDurableIdentity");
        set.add("DatastoreIdentity");
        set.add("ApplicationIdentity");
        if (this.dba.supportsOption("TxIsolationReadCommitted")) {
            set.add("TransactionIsolationLevel.read-committed");
        }
        if (this.dba.supportsOption("TxIsolationReadUncommitted")) {
            set.add("TransactionIsolationLevel.read-uncommitted");
        }
        if (this.dba.supportsOption("TxIsolationReadRepeatableRead")) {
            set.add("TransactionIsolationLevel.repeatable-read");
        }
        if (this.dba.supportsOption("TxIsolationSerializable")) {
            set.add("TransactionIsolationLevel.serializable");
        }
        set.add("Query.Cancel");
        set.add("Datastore.Timeout");
        return set;
    }
    
    public boolean insertValuesOnInsert(final DatastoreMapping datastoreMapping) {
        return ((AbstractDatastoreMapping)datastoreMapping).insertValuesOnInsert();
    }
    
    public boolean allowsBatching() {
        return this.dba.supportsOption("StatementBatching") && this.getIntProperty("datanucleus.rdbms.statementBatchLimit") != 0;
    }
    
    public ResultObjectFactory newResultObjectFactory(final AbstractClassMetaData acmd, final StatementClassMapping mappingDefinition, final boolean ignoreCache, final FetchPlan fetchPlan, final Class persistentClass) {
        return new PersistentClassROF(this, acmd, mappingDefinition, ignoreCache, fetchPlan, persistentClass);
    }
    
    protected ArrayStore newFKArrayStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        return new FKArrayStore(mmd, this, clr);
    }
    
    protected ListStore newFKListStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        return new FKListStore(mmd, this, clr);
    }
    
    protected SetStore newFKSetStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        return new FKSetStore(mmd, this, clr);
    }
    
    protected MapStore newFKMapStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr) {
        return new FKMapStore(mmd, this, clr);
    }
    
    protected ArrayStore newJoinArrayStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Table table) {
        return new JoinArrayStore(mmd, (ArrayTable)table, clr);
    }
    
    protected MapStore newJoinMapStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Table table) {
        return new JoinMapStore((MapTable)table, clr);
    }
    
    protected ListStore newJoinListStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Table table) {
        return new JoinListStore(mmd, (CollectionTable)table, clr);
    }
    
    protected SetStore newJoinSetStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Table table) {
        return new JoinSetStore(mmd, (CollectionTable)table, clr);
    }
    
    protected PersistableRelationStore newPersistableRelationStore(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final Table table) {
        return new JoinPersistableRelationStore(mmd, (PersistableJoinTable)table, clr);
    }
    
    @Override
    public boolean usesBackedSCOWrappers() {
        return true;
    }
    
    @Override
    public boolean useBackedSCOWrapperForMember(final AbstractMemberMetaData mmd, final ExecutionContext ec) {
        return true;
    }
    
    public Calendar getCalendarForDateTimezone() {
        if (this.dateTimezoneCalendar == null) {
            final String serverTimeZoneID = this.getStringProperty("datanucleus.ServerTimeZoneID");
            TimeZone tz;
            if (serverTimeZoneID != null) {
                tz = TimeZone.getTimeZone(serverTimeZoneID);
            }
            else {
                tz = TimeZone.getDefault();
            }
            this.dateTimezoneCalendar = new GregorianCalendar(tz);
        }
        return (Calendar)this.dateTimezoneCalendar.clone();
    }
    
    public void createDatabase(final String databaseName, final Properties props) {
        try {
            final String stmtText = this.dba.getCreateDatabaseStatement(this.catalogName, this.schemaName);
            final ManagedConnection mconn = this.getConnection(0);
            final Connection conn = (Connection)mconn.getConnection();
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                NucleusLogger.DATASTORE_SCHEMA.debug("createDatabase executing " + stmtText);
                final boolean success = stmt.execute(stmtText);
                NucleusLogger.DATASTORE_SCHEMA.debug("createDatabase execute returned " + success);
            }
            catch (SQLException sqle) {}
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException ex) {}
                }
                mconn.release();
            }
        }
        catch (UnsupportedOperationException uoe) {}
    }
    
    public void dropDatabase(final String schemaName, final Properties props) {
        try {
            final String stmtText = this.dba.getDropDatabaseStatement(this.catalogName, schemaName);
            final ManagedConnection mconn = this.getConnection(0);
            final Connection conn = (Connection)mconn.getConnection();
            Statement stmt = null;
            try {
                stmt = conn.createStatement();
                NucleusLogger.DATASTORE_SCHEMA.debug("dropDatabase executing " + stmtText);
                final boolean success = stmt.execute(stmtText);
                NucleusLogger.DATASTORE_SCHEMA.debug("dropDatabase execute returned " + success);
            }
            catch (SQLException sqle) {}
            finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    }
                    catch (SQLException ex) {}
                }
                mconn.release();
            }
        }
        catch (UnsupportedOperationException uoe) {}
    }
    
    @Override
    public void createSchema(final Set<String> inputClassNames, final Properties props) {
        final Set<String> classNames = cleanInputClassNames(this.nucleusContext, inputClassNames);
        final String ddlFilename = (props != null) ? props.getProperty("ddlFilename") : null;
        final String completeDdlProp = (props != null) ? props.getProperty("completeDdl") : null;
        final boolean completeDdl = completeDdlProp != null && completeDdlProp.equalsIgnoreCase("true");
        final String autoStartProp = (props != null) ? props.getProperty("autoStartTable") : null;
        final boolean autoStart = autoStartProp != null && autoStartProp.equalsIgnoreCase("true");
        if (classNames.size() > 0) {
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(null);
            FileWriter ddlFileWriter = null;
            try {
                if (ddlFilename != null) {
                    final File ddlFile = StringUtils.getFileForFilename(ddlFilename);
                    if (ddlFile.exists()) {
                        ddlFile.delete();
                    }
                    if (ddlFile.getParentFile() != null && !ddlFile.getParentFile().exists()) {
                        ddlFile.getParentFile().mkdirs();
                    }
                    ddlFile.createNewFile();
                    ddlFileWriter = new FileWriter(ddlFile);
                    final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    ddlFileWriter.write("------------------------------------------------------------------\n");
                    ddlFileWriter.write("-- DataNucleus SchemaTool (ran at " + fmt.format(new Date()) + ")\n");
                    ddlFileWriter.write("------------------------------------------------------------------\n");
                    if (completeDdl) {
                        ddlFileWriter.write("-- Complete schema required for the following classes:-\n");
                    }
                    else {
                        ddlFileWriter.write("-- Schema diff for " + this.getConnectionURL() + " and the following classes:-\n");
                    }
                    final Iterator classNameIter = classNames.iterator();
                    while (classNameIter.hasNext()) {
                        ddlFileWriter.write("--     " + classNameIter.next() + "\n");
                    }
                    ddlFileWriter.write("--\n");
                }
                try {
                    if (ddlFileWriter != null) {
                        this.ddlWriter = ddlFileWriter;
                        this.completeDDL = completeDdl;
                        this.writtenDdlStatements = new HashSet<String>();
                    }
                    new ClassAdder((String[])classNames.toArray(new String[classNames.size()]), (Writer)ddlFileWriter).execute(clr);
                    if (autoStart) {
                        if (ddlFileWriter != null) {
                            try {
                                ddlFileWriter.write("\n");
                                ddlFileWriter.write("------------------------------------------------------------------\n");
                                ddlFileWriter.write("-- Table for SchemaTable auto-starter\n");
                            }
                            catch (IOException ex) {}
                        }
                        new SchemaAutoStarter(this, clr);
                    }
                    if (ddlFileWriter != null) {
                        this.ddlWriter = null;
                        this.completeDDL = false;
                        this.writtenDdlStatements.clear();
                        this.writtenDdlStatements = null;
                    }
                    if (ddlFileWriter != null) {
                        ddlFileWriter.write("\n");
                        ddlFileWriter.write("------------------------------------------------------------------\n");
                        ddlFileWriter.write("-- Sequences and SequenceTables\n");
                    }
                    this.createSchemaSequences(classNames, clr, ddlFileWriter);
                }
                finally {
                    if (ddlFileWriter != null) {
                        ddlFileWriter.close();
                    }
                }
            }
            catch (IOException ex2) {}
            return;
        }
        final String msg = RDBMSStoreManager.LOCALISER.msg(false, "014039");
        NucleusLogger.DATASTORE_SCHEMA.error(msg);
        System.out.println(msg);
        throw new NucleusException(msg);
    }
    
    protected void createSchemaSequences(final Set<String> classNames, final ClassLoaderResolver clr, final FileWriter ddlWriter) {
        if (classNames != null && classNames.size() > 0) {
            final Set<String> seqTablesGenerated = new HashSet<String>();
            final Set<String> sequencesGenerated = new HashSet<String>();
            for (final String className : classNames) {
                final AbstractClassMetaData cmd = this.getMetaDataManager().getMetaDataForClass(className, clr);
                if (cmd.getIdentityMetaData() != null && cmd.getIdentityMetaData().getValueStrategy() != null) {
                    if (cmd.getIdentityMetaData().getValueStrategy() == IdentityStrategy.INCREMENT) {
                        this.addSequenceTableForMetaData(cmd.getIdentityMetaData(), clr, seqTablesGenerated);
                    }
                    else if (cmd.getIdentityMetaData().getValueStrategy() == IdentityStrategy.SEQUENCE) {
                        String seqName = cmd.getIdentityMetaData().getSequence();
                        if (StringUtils.isWhitespace(seqName)) {
                            seqName = cmd.getIdentityMetaData().getValueGeneratorName();
                        }
                        if (!StringUtils.isWhitespace(seqName)) {
                            this.addSequenceForMetaData(cmd.getIdentityMetaData(), seqName, clr, sequencesGenerated, ddlWriter);
                        }
                    }
                }
                final AbstractMemberMetaData[] mmds = cmd.getManagedMembers();
                for (int j = 0; j < mmds.length; ++j) {
                    final IdentityStrategy str = mmds[j].getValueStrategy();
                    if (str == IdentityStrategy.INCREMENT) {
                        this.addSequenceTableForMetaData(mmds[j], clr, seqTablesGenerated);
                    }
                    else if (str == IdentityStrategy.SEQUENCE) {
                        String seqName2 = mmds[j].getSequence();
                        if (StringUtils.isWhitespace(seqName2)) {
                            seqName2 = mmds[j].getValueGeneratorName();
                        }
                        if (!StringUtils.isWhitespace(seqName2)) {
                            this.addSequenceForMetaData(mmds[j], seqName2, clr, sequencesGenerated, ddlWriter);
                        }
                    }
                }
            }
        }
    }
    
    protected void addSequenceTableForMetaData(final MetaData md, final ClassLoaderResolver clr, final Set<String> seqTablesGenerated) {
        String catName = null;
        String schName = null;
        String tableName = "SEQUENCE_TABLE";
        String seqColName = "SEQUENCE_NAME";
        String nextValColName = "NEXT_VAL";
        if (md.hasExtension("sequence-catalog-name")) {
            catName = md.getValueForExtension("sequence-catalog-name");
        }
        if (md.hasExtension("sequence-schema-name")) {
            schName = md.getValueForExtension("sequence-schema-name");
        }
        if (md.hasExtension("sequence-table-name")) {
            tableName = md.getValueForExtension("sequence-table-name");
        }
        if (md.hasExtension("sequence-name-column-name")) {
            seqColName = md.getValueForExtension("sequence-name-column-name");
        }
        if (md.hasExtension("sequence-nextval-column-name")) {
            nextValColName = md.getValueForExtension("sequence-nextval-column-name");
        }
        if (!seqTablesGenerated.contains(tableName)) {
            final ManagedConnection mconn = this.getConnection(0);
            final Connection conn = (Connection)mconn.getConnection();
            try {
                final DatastoreIdentifier tableIdentifier = this.identifierFactory.newTableIdentifier(tableName);
                if (catName != null) {
                    tableIdentifier.setCatalogName(catName);
                }
                if (schName != null) {
                    tableIdentifier.setSchemaName(schName);
                }
                final SequenceTable seqTable = new SequenceTable(tableIdentifier, this, seqColName, nextValColName);
                seqTable.initialize(clr);
                seqTable.exists(conn, true);
            }
            catch (Exception e) {}
            finally {
                mconn.release();
            }
            seqTablesGenerated.add(tableName);
        }
    }
    
    protected void addSequenceForMetaData(MetaData md, final String seq, final ClassLoaderResolver clr, final Set<String> sequencesGenerated, final FileWriter ddlWriter) {
        String seqName = seq;
        Integer min = null;
        Integer max = null;
        Integer start = null;
        Integer increment = null;
        Integer cacheSize = null;
        final SequenceMetaData seqmd = this.getMetaDataManager().getMetaDataForSequence(clr, seq);
        if (seqmd != null) {
            seqName = seqmd.getDatastoreSequence();
            if (seqmd.getAllocationSize() > 0) {
                increment = seqmd.getAllocationSize();
            }
            if (seqmd.getInitialValue() >= 0) {
                start = seqmd.getInitialValue();
            }
            md = seqmd;
        }
        if (md.hasExtension("key-min-value")) {
            min = Integer.valueOf(md.getValueForExtension("key-min-value"));
        }
        if (md.hasExtension("key-max-value")) {
            max = Integer.valueOf(md.getValueForExtension("key-max-value"));
        }
        if (md.hasExtension("key-cache-size")) {
            increment = Integer.valueOf(md.getValueForExtension("key-cache-size"));
        }
        if (md.hasExtension("key-initial-value")) {
            start = Integer.valueOf(md.getValueForExtension("key-initial-value"));
        }
        if (md.hasExtension("key-database-cache-size")) {
            cacheSize = Integer.valueOf(md.getValueForExtension("key-database-cache-size"));
        }
        if (!sequencesGenerated.contains(seqName)) {
            final String stmt = this.getDatastoreAdapter().getSequenceCreateStmt(seqName, min, max, start, increment, cacheSize);
            if (ddlWriter != null) {
                try {
                    ddlWriter.write(stmt + ";\n");
                }
                catch (IOException ioe) {}
            }
            else {
                PreparedStatement ps = null;
                final ManagedConnection mconn = this.getConnection(0);
                try {
                    ps = this.sqlController.getStatementForUpdate(mconn, stmt, false);
                    this.sqlController.executeStatementUpdate(null, mconn, stmt, ps, true);
                }
                catch (SQLException e) {}
                finally {
                    try {
                        if (ps != null) {
                            this.sqlController.closeStatement(mconn, ps);
                        }
                    }
                    catch (SQLException ex) {}
                    mconn.release();
                }
            }
            sequencesGenerated.add(seqName);
        }
    }
    
    @Override
    public void deleteSchema(final Set<String> inputClassNames, final Properties props) {
        final Set<String> classNames = cleanInputClassNames(this.nucleusContext, inputClassNames);
        if (classNames.size() > 0) {
            final String ddlFilename = (props != null) ? props.getProperty("ddlFilename") : null;
            final String completeDdlProp = (props != null) ? props.getProperty("completeDdl") : null;
            final boolean completeDdl = completeDdlProp != null && completeDdlProp.equalsIgnoreCase("true");
            final String autoStartProp = (props != null) ? props.getProperty("autoStartTable") : null;
            final boolean autoStart = autoStartProp != null && autoStartProp.equalsIgnoreCase("true");
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(null);
            FileWriter ddlFileWriter = null;
            try {
                if (ddlFilename != null) {
                    final File ddlFile = StringUtils.getFileForFilename(ddlFilename);
                    if (ddlFile.exists()) {
                        ddlFile.delete();
                    }
                    if (ddlFile.getParentFile() != null && !ddlFile.getParentFile().exists()) {
                        ddlFile.getParentFile().mkdirs();
                    }
                    ddlFile.createNewFile();
                    ddlFileWriter = new FileWriter(ddlFile);
                    final SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    ddlFileWriter.write("------------------------------------------------------------------\n");
                    ddlFileWriter.write("-- DataNucleus SchemaTool (ran at " + fmt.format(new Date()) + ")\n");
                    ddlFileWriter.write("------------------------------------------------------------------\n");
                    ddlFileWriter.write("-- Delete schema required for the following classes:-\n");
                    final Iterator classNameIter = classNames.iterator();
                    while (classNameIter.hasNext()) {
                        ddlFileWriter.write("--     " + classNameIter.next() + "\n");
                    }
                    ddlFileWriter.write("--\n");
                }
                try {
                    if (ddlFileWriter != null) {
                        this.ddlWriter = ddlFileWriter;
                        this.completeDDL = completeDdl;
                        this.writtenDdlStatements = new HashSet<String>();
                    }
                    final String[] classNameArray = classNames.toArray(new String[classNames.size()]);
                    this.addClasses(classNameArray, clr);
                    final DeleteTablesSchemaTransaction deleteTablesTxn = new DeleteTablesSchemaTransaction(this, 2, this.storeDataMgr);
                    deleteTablesTxn.setWriter(this.ddlWriter);
                    boolean success = true;
                    try {
                        deleteTablesTxn.execute(clr);
                    }
                    catch (NucleusException ne) {
                        success = false;
                        throw ne;
                    }
                    finally {
                        if (success) {
                            this.clearSchemaData();
                        }
                    }
                    if (autoStart) {}
                }
                finally {
                    if (ddlFileWriter != null) {
                        this.ddlWriter = null;
                        this.completeDDL = false;
                        this.writtenDdlStatements.clear();
                        this.writtenDdlStatements = null;
                        ddlFileWriter.close();
                    }
                }
            }
            catch (IOException ex) {}
            return;
        }
        final String msg = RDBMSStoreManager.LOCALISER.msg(false, "014039");
        NucleusLogger.DATASTORE_SCHEMA.error(msg);
        System.out.println(msg);
        throw new NucleusException(msg);
    }
    
    @Override
    public void validateSchema(final Set<String> inputClassNames, final Properties props) {
        final Set<String> classNames = cleanInputClassNames(this.nucleusContext, inputClassNames);
        if (classNames != null && classNames.size() > 0) {
            final ClassLoaderResolver clr = this.nucleusContext.getClassLoaderResolver(null);
            final String[] classNameArray = classNames.toArray(new String[classNames.size()]);
            this.addClasses(classNameArray, clr);
            return;
        }
        final String msg = RDBMSStoreManager.LOCALISER.msg(false, "014039");
        NucleusLogger.DATASTORE_SCHEMA.error(msg);
        System.out.println(msg);
        throw new NucleusException(msg);
    }
    
    @Override
    public void executeScript(String script) {
        script = StringUtils.replaceAll(script, "\n", " ");
        script = StringUtils.replaceAll(script, "\t", " ");
        final ManagedConnection mc = this.getConnection(-1);
        try {
            final Connection conn = (Connection)mc.getConnection();
            final Statement stmt = conn.createStatement();
            try {
                final StringTokenizer tokeniser = new StringTokenizer(script, ";");
                while (tokeniser.hasMoreTokens()) {
                    final String token = tokeniser.nextToken().trim();
                    if (!StringUtils.isWhitespace(token)) {
                        NucleusLogger.DATASTORE_NATIVE.debug("Executing script statement : " + token);
                        stmt.execute(token + ";");
                    }
                }
            }
            finally {
                stmt.close();
            }
        }
        catch (SQLException e) {
            NucleusLogger.DATASTORE_NATIVE.error("Exception executing user script", e);
            throw new NucleusUserException("Exception executing user script. See nested exception for details", e);
        }
        finally {
            mc.release();
        }
    }
    
    protected static Set<String> cleanInputClassNames(final NucleusContext ctx, final Set<String> inputClassNames) {
        final Set<String> classNames = new TreeSet<String>();
        if (inputClassNames == null || inputClassNames.size() == 0) {
            final Collection classesWithMetadata = ctx.getMetaDataManager().getClassesWithMetaData();
            classNames.addAll(classesWithMetadata);
        }
        else {
            classNames.addAll(inputClassNames);
        }
        return classNames;
    }
    
    static {
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
    
    private class ClassAdder extends AbstractSchemaTransaction
    {
        public static final int JOIN_TABLE_COLLECTION = 1;
        public static final int JOIN_TABLE_MAP = 2;
        public static final int JOIN_TABLE_ARRAY = 3;
        public static final int JOIN_TABLE_PERSISTABLE = 4;
        private Writer ddlWriter;
        protected final boolean checkExistTablesOrViews;
        private HashSet<RDBMSStoreData> schemaDataAdded;
        private final String[] classNames;
        public List<Table> tablesRecentlyInitialized;
        private int addClassTablesRecursionCounter;
        
        private ClassAdder(final String[] classNames, final Writer writer) {
            super(RDBMSStoreManager.this, RDBMSStoreManager.this.dba.getTransactionIsolationForSchemaCreation());
            this.ddlWriter = null;
            this.schemaDataAdded = new HashSet<RDBMSStoreData>();
            this.tablesRecentlyInitialized = new ArrayList<Table>();
            this.addClassTablesRecursionCounter = 0;
            this.ddlWriter = writer;
            this.classNames = RDBMSStoreManager.this.getNucleusContext().getTypeManager().filterOutSupportedSecondClassNames(classNames);
            this.checkExistTablesOrViews = RDBMSStoreManager.this.getBooleanProperty("datanucleus.rdbms.checkExistTablesOrViews");
        }
        
        @Override
        public String toString() {
            return ClassAdder.LOCALISER_RDBMS.msg("050038", RDBMSStoreManager.this.catalogName, RDBMSStoreManager.this.schemaName);
        }
        
        @Override
        protected void run(final ClassLoaderResolver clr) throws SQLException {
            if (this.classNames.length == 0) {
                return;
            }
            try {
                RDBMSStoreManager.this.schemaLock.writeLock().lock();
                RDBMSStoreManager.this.classAdder = this;
                try {
                    this.addClassTablesAndValidate(this.classNames, clr);
                }
                finally {
                    RDBMSStoreManager.this.classAdder = null;
                }
            }
            finally {
                RDBMSStoreManager.this.schemaLock.writeLock().unlock();
            }
        }
        
        private void addClasses(String[] classNames, final ClassLoaderResolver clr) {
            classNames = RDBMSStoreManager.this.getNucleusContext().getTypeManager().filterOutSupportedSecondClassNames(classNames);
            if (classNames.length == 0) {
                return;
            }
            this.addClassTables(classNames, clr);
        }
        
        private void addClassTables(final String[] classNames, final ClassLoaderResolver clr) {
            ++this.addClassTablesRecursionCounter;
            try {
                final Iterator iter = RDBMSStoreManager.this.getMetaDataManager().getReferencedClasses(classNames, clr).iterator();
                final AutoStartMechanism starter = RDBMSStoreManager.this.nucleusContext.getAutoStartMechanism();
                try {
                    if (starter != null && !starter.isOpen()) {
                        starter.open();
                    }
                    while (iter.hasNext()) {
                        final ClassMetaData cmd = iter.next();
                        this.addClassTable(cmd, clr);
                    }
                    for (final RDBMSStoreData data : new HashSet<RDBMSStoreData>(this.schemaDataAdded)) {
                        if (data.getTable() == null && data.isFCO()) {
                            final AbstractClassMetaData cmd2 = (AbstractClassMetaData)data.getMetaData();
                            final InheritanceMetaData imd = cmd2.getInheritanceMetaData();
                            if (imd.getStrategy() != InheritanceStrategy.SUPERCLASS_TABLE) {
                                continue;
                            }
                            final AbstractClassMetaData[] managingCmds = RDBMSStoreManager.this.getClassesManagingTableForClass(cmd2, clr);
                            DatastoreClass superTable = null;
                            if (managingCmds == null || managingCmds.length != 1) {
                                continue;
                            }
                            RDBMSStoreData superData = (RDBMSStoreData)RDBMSStoreManager.this.storeDataMgr.get(managingCmds[0].getFullClassName());
                            if (superData == null) {
                                this.addClassTables(new String[] { managingCmds[0].getFullClassName() }, clr);
                                superData = (RDBMSStoreData)RDBMSStoreManager.this.storeDataMgr.get(managingCmds[0].getFullClassName());
                            }
                            if (superData == null) {
                                final String msg = ClassAdder.LOCALISER_RDBMS.msg("050013", cmd2.getFullClassName());
                                NucleusLogger.PERSISTENCE.error(msg);
                                throw new NucleusUserException(msg);
                            }
                            superTable = (DatastoreClass)superData.getTable();
                            data.setDatastoreContainerObject(superTable);
                        }
                    }
                }
                finally {
                    if (starter != null && starter.isOpen() && this.addClassTablesRecursionCounter <= 1) {
                        starter.close();
                    }
                }
            }
            finally {
                --this.addClassTablesRecursionCounter;
            }
        }
        
        private void addClassTable(final ClassMetaData cmd, final ClassLoaderResolver clr) {
            if (cmd.getPersistenceModifier() != ClassPersistenceModifier.PERSISTENCE_CAPABLE) {
                return;
            }
            if (cmd.getIdentityType() == IdentityType.NONDURABLE && cmd.hasExtension("requires-table") && cmd.getValueForExtension("requires-table") != null && cmd.getValueForExtension("requires-table").equalsIgnoreCase("false")) {
                return;
            }
            final RDBMSStoreData sd = (RDBMSStoreData)RDBMSStoreManager.this.storeDataMgr.get(cmd.getFullClassName());
            if (sd == null) {
                if (cmd.getIdentityType() == IdentityType.APPLICATION && !cmd.usesSingleFieldIdentityClass()) {
                    final String baseClassWithMetaData = cmd.getBaseAbstractClassMetaData().getFullClassName();
                    final Collection<AbstractClassMetaData> pkCmds = RDBMSStoreManager.this.getMetaDataManager().getClassMetaDataWithApplicationId(cmd.getObjectidClass());
                    if (pkCmds != null && pkCmds.size() > 0) {
                        boolean in_same_tree = false;
                        String sample_class_in_other_tree = null;
                        for (final AbstractClassMetaData pkCmd : pkCmds) {
                            final String otherClassBaseClass = pkCmd.getBaseAbstractClassMetaData().getFullClassName();
                            if (otherClassBaseClass.equals(baseClassWithMetaData)) {
                                in_same_tree = true;
                                break;
                            }
                            sample_class_in_other_tree = pkCmd.getFullClassName();
                        }
                        if (!in_same_tree) {
                            final String error_msg = ClassAdder.LOCALISER_RDBMS.msg("050021", cmd.getFullClassName(), cmd.getObjectidClass(), sample_class_in_other_tree);
                            NucleusLogger.DATASTORE.error(error_msg);
                            throw new NucleusUserException(error_msg);
                        }
                    }
                }
                if (cmd.isEmbeddedOnly()) {
                    NucleusLogger.DATASTORE.info(RDBMSStoreManager.LOCALISER.msg("032012", cmd.getFullClassName()));
                }
                else {
                    final InheritanceMetaData imd = cmd.getInheritanceMetaData();
                    RDBMSStoreData sdNew = null;
                    if (imd.getStrategy() == InheritanceStrategy.SUBCLASS_TABLE) {
                        sdNew = new RDBMSStoreData(cmd, null, false);
                        AbstractStoreManager.this.registerStoreData(sdNew);
                    }
                    else if (imd.getStrategy() == InheritanceStrategy.COMPLETE_TABLE && cmd.isAbstract()) {
                        sdNew = new RDBMSStoreData(cmd, null, false);
                        AbstractStoreManager.this.registerStoreData(sdNew);
                    }
                    else if (imd.getStrategy() == InheritanceStrategy.NEW_TABLE || imd.getStrategy() == InheritanceStrategy.COMPLETE_TABLE) {
                        DatastoreIdentifier tableName = null;
                        final RDBMSStoreData tmpData = (RDBMSStoreData)RDBMSStoreManager.this.storeDataMgr.get(cmd.getFullClassName());
                        if (tmpData != null && tmpData.getDatastoreIdentifier() != null) {
                            tableName = tmpData.getDatastoreIdentifier();
                        }
                        else {
                            tableName = RDBMSStoreManager.this.identifierFactory.newTableIdentifier(cmd);
                        }
                        final StoreData[] existingStoreData = RDBMSStoreManager.this.getStoreDataForDatastoreContainerObject(tableName);
                        if (existingStoreData != null) {
                            String existingClass = null;
                            for (int j = 0; j < existingStoreData.length; ++j) {
                                if (!existingStoreData[j].getName().equals(cmd.getFullClassName())) {
                                    existingClass = existingStoreData[j].getName();
                                    break;
                                }
                            }
                            if (existingClass != null) {
                                final String msg = ClassAdder.LOCALISER_RDBMS.msg("050015", cmd.getFullClassName(), tableName.getIdentifierName(), existingClass);
                                NucleusLogger.DATASTORE.warn(msg);
                            }
                        }
                        DatastoreClass t = null;
                        boolean hasViewDef = false;
                        if (RDBMSStoreManager.this.dba.getVendorID() != null) {
                            hasViewDef = cmd.hasExtension("view-definition-" + RDBMSStoreManager.this.dba.getVendorID());
                        }
                        if (!hasViewDef) {
                            hasViewDef = cmd.hasExtension("view-definition");
                        }
                        if (hasViewDef) {
                            t = new ClassView(tableName, RDBMSStoreManager.this, cmd);
                        }
                        else {
                            t = new ClassTable(tableName, RDBMSStoreManager.this, cmd);
                        }
                        sdNew = new RDBMSStoreData(cmd, t, true);
                        AbstractStoreManager.this.registerStoreData(sdNew);
                        t.preInitialize(clr);
                    }
                    else if (imd.getStrategy() == InheritanceStrategy.SUPERCLASS_TABLE) {
                        final AbstractClassMetaData[] managingCmds = RDBMSStoreManager.this.getClassesManagingTableForClass(cmd, clr);
                        Table superTable = null;
                        if (managingCmds == null || managingCmds.length != 1) {
                            final String msg2 = ClassAdder.LOCALISER_RDBMS.msg("050013", cmd.getFullClassName());
                            NucleusLogger.PERSISTENCE.error(msg2);
                            throw new NucleusUserException(msg2);
                        }
                        final RDBMSStoreData superData = (RDBMSStoreData)RDBMSStoreManager.this.storeDataMgr.get(managingCmds[0].getFullClassName());
                        if (superData != null) {
                            superTable = superData.getTable();
                        }
                        sdNew = new RDBMSStoreData(cmd, superTable, false);
                        AbstractStoreManager.this.registerStoreData(sdNew);
                    }
                    this.schemaDataAdded.add(sdNew);
                }
            }
        }
        
        private void addClassTablesAndValidate(final String[] classNames, final ClassLoaderResolver clr) {
            synchronized (RDBMSStoreManager.this.storeDataMgr) {
                RDBMSStoreManager.this.storeDataMgr.begin();
                boolean completed = false;
                List tablesCreated = null;
                List tableConstraintsCreated = null;
                List viewsCreated = null;
                try {
                    List autoCreateErrors = new ArrayList();
                    this.addClassTables(classNames, clr);
                    final List<Table>[] toValidate = this.initializeClassTables(classNames, clr);
                    if (toValidate[0] != null && toValidate[0].size() > 0) {
                        final List[] result = this.performTablesValidation(toValidate[0], clr);
                        tablesCreated = result[0];
                        tableConstraintsCreated = result[1];
                        autoCreateErrors = result[2];
                    }
                    if (toValidate[1] != null && toValidate[1].size() > 0) {
                        final List[] result = this.performViewsValidation(toValidate[1]);
                        viewsCreated = result[0];
                        autoCreateErrors.addAll(result[1]);
                    }
                    this.verifyErrors(autoCreateErrors);
                    completed = true;
                }
                catch (SQLException sqle) {
                    final String msg = ClassAdder.LOCALISER_RDBMS.msg("050044", sqle);
                    NucleusLogger.DATASTORE_SCHEMA.error(msg);
                    throw new NucleusDataStoreException(msg, sqle);
                }
                catch (Exception e) {
                    if (NucleusException.class.isAssignableFrom(e.getClass())) {
                        throw (NucleusException)e;
                    }
                    NucleusLogger.DATASTORE_SCHEMA.error(ClassAdder.LOCALISER_RDBMS.msg("050044", e));
                    throw new NucleusException(e.toString(), e).setFatal();
                }
                finally {
                    if (!completed) {
                        RDBMSStoreManager.this.storeDataMgr.rollback();
                        this.rollbackSchemaCreation(viewsCreated, tableConstraintsCreated, tablesCreated);
                    }
                    else {
                        RDBMSStoreManager.this.storeDataMgr.commit();
                    }
                    this.schemaDataAdded.clear();
                }
            }
        }
        
        private List<Table>[] initializeClassTables(final String[] classNames, final ClassLoaderResolver clr) {
            final List<Table> tablesToValidate = new ArrayList<Table>();
            final List<Table> viewsToValidate = new ArrayList<Table>();
            this.tablesRecentlyInitialized.clear();
            int numTablesInitializedInit = 0;
            do {
                final RDBMSStoreData[] rdbmsStoreData = RDBMSStoreManager.this.storeDataMgr.getManagedStoreData().toArray(new RDBMSStoreData[RDBMSStoreManager.this.storeDataMgr.size()]);
                numTablesInitializedInit = this.tablesRecentlyInitialized.size();
                for (int i = 0; i < rdbmsStoreData.length; ++i) {
                    final RDBMSStoreData currentStoreData = rdbmsStoreData[i];
                    if (currentStoreData.hasTable()) {
                        final Table t = currentStoreData.getTable();
                        if (t instanceof DatastoreClass) {
                            ((RDBMSPersistenceHandler)RDBMSStoreManager.this.persistenceHandler).removeRequestsForTable((DatastoreClass)t);
                        }
                        if (!t.isInitialized()) {
                            t.initialize(clr);
                        }
                        if (!currentStoreData.isTableOwner() && !((ClassTable)t).managesClass(currentStoreData.getName())) {
                            ((ClassTable)t).manageClass((AbstractClassMetaData)currentStoreData.getMetaData(), clr);
                            if (!tablesToValidate.contains(t)) {
                                tablesToValidate.add(t);
                            }
                        }
                    }
                }
            } while (this.tablesRecentlyInitialized.size() > numTablesInitializedInit);
            for (int j = 0; j < this.tablesRecentlyInitialized.size(); ++j) {
                this.tablesRecentlyInitialized.get(j).postInitialize(clr);
            }
            for (final Table t2 : this.tablesRecentlyInitialized) {
                if (t2 instanceof ViewImpl) {
                    viewsToValidate.add(t2);
                }
                else {
                    if (tablesToValidate.contains(t2)) {
                        continue;
                    }
                    tablesToValidate.add(t2);
                }
            }
            return (List<Table>[])new List[] { tablesToValidate, viewsToValidate };
        }
        
        private List[] performTablesValidation(List<Table> tablesToValidate, final ClassLoaderResolver clr) throws SQLException {
            final List autoCreateErrors = new ArrayList();
            final List<Table> tableConstraintsCreated = new ArrayList<Table>();
            final List<Table> tablesCreated = new ArrayList<Table>();
            if (this.ddlWriter != null) {
                tablesToValidate = this.removeDuplicateTablesFromList(tablesToValidate);
            }
            for (final TableImpl t : tablesToValidate) {
                boolean columnsValidated = false;
                if (this.checkExistTablesOrViews) {
                    if (this.ddlWriter != null) {
                        try {
                            if (t instanceof ClassTable) {
                                this.ddlWriter.write("-- Table " + t.toString() + " for classes " + StringUtils.objectArrayToString(((ClassTable)t).getManagedClasses()) + "\n");
                            }
                            else if (t instanceof JoinTable) {
                                this.ddlWriter.write("-- Table " + t.toString() + " for join relationship\n");
                            }
                        }
                        catch (IOException ioe) {
                            NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + t, ioe);
                        }
                    }
                    if (!tablesCreated.contains(t) && t.exists(this.getCurrentConnection(), RDBMSStoreManager.this.autoCreateTables)) {
                        tablesCreated.add(t);
                        columnsValidated = true;
                    }
                    else if (t.isInitializedModified() || RDBMSStoreManager.this.autoCreateColumns) {
                        t.validateColumns(this.getCurrentConnection(), false, RDBMSStoreManager.this.autoCreateColumns, autoCreateErrors);
                        columnsValidated = true;
                    }
                }
                if (RDBMSStoreManager.this.validateTables && !columnsValidated) {
                    t.validate(this.getCurrentConnection(), RDBMSStoreManager.this.validateColumns, false, autoCreateErrors);
                }
                else if (!columnsValidated) {
                    final String initInfo = RDBMSStoreManager.this.getStringProperty("datanucleus.rdbms.initializeColumnInfo");
                    if (initInfo.equalsIgnoreCase("PK")) {
                        t.initializeColumnInfoForPrimaryKeyColumns(this.getCurrentConnection());
                    }
                    else if (initInfo.equalsIgnoreCase("ALL")) {
                        t.initializeColumnInfoFromDatastore(this.getCurrentConnection());
                    }
                }
                RDBMSStoreManager.this.invalidateColumnInfoForTable(t);
            }
            for (final TableImpl t : tablesToValidate) {
                if (RDBMSStoreManager.this.validateConstraints || RDBMSStoreManager.this.autoCreateConstraints) {
                    if (this.ddlWriter != null) {
                        try {
                            if (t instanceof ClassTable) {
                                this.ddlWriter.write("-- Constraints for table " + t.toString() + " for class(es) " + StringUtils.objectArrayToString(((ClassTable)t).getManagedClasses()) + "\n");
                            }
                            else {
                                this.ddlWriter.write("-- Constraints for table " + t.toString() + "\n");
                            }
                        }
                        catch (IOException ioe2) {
                            NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + t, ioe2);
                        }
                    }
                    if (tablesCreated.contains(t) && !this.hasDuplicateTablesFromList(tablesToValidate)) {
                        if (t.createConstraints(this.getCurrentConnection(), autoCreateErrors, clr)) {
                            tableConstraintsCreated.add(t);
                        }
                    }
                    else if (t.validateConstraints(this.getCurrentConnection(), RDBMSStoreManager.this.autoCreateConstraints, autoCreateErrors, clr)) {
                        tableConstraintsCreated.add(t);
                    }
                    if (this.ddlWriter == null) {
                        continue;
                    }
                    try {
                        this.ddlWriter.write("\n");
                    }
                    catch (IOException ioe2) {
                        NucleusLogger.DATASTORE_SCHEMA.error("error writing DDL into file for table " + t, ioe2);
                    }
                }
            }
            return new List[] { tablesCreated, tableConstraintsCreated, autoCreateErrors };
        }
        
        private List<Table> removeDuplicateTablesFromList(final List<Table> newTables) {
            final List<Table> result = new ArrayList<Table>();
            for (final Table tbl : newTables) {
                if (!result.contains(tbl)) {
                    result.add(tbl);
                }
            }
            return result;
        }
        
        private boolean hasDuplicateTablesFromList(final List<Table> newTables) {
            final Map map = new HashMap();
            for (int i = 0; i < newTables.size(); ++i) {
                final Table t1 = newTables.get(i);
                if (map.containsKey(t1.getIdentifier().getIdentifierName())) {
                    return true;
                }
                map.put(t1.getIdentifier().getIdentifierName(), t1);
            }
            return false;
        }
        
        private List[] performViewsValidation(final List<Table> viewsToValidate) throws SQLException {
            final List<Table> viewsCreated = new ArrayList<Table>();
            final List autoCreateErrors = new ArrayList();
            for (final ViewImpl v : viewsToValidate) {
                if (this.checkExistTablesOrViews && v.exists(this.getCurrentConnection(), RDBMSStoreManager.this.autoCreateTables)) {
                    viewsCreated.add(v);
                }
                if (RDBMSStoreManager.this.validateTables) {
                    v.validate(this.getCurrentConnection(), true, false, autoCreateErrors);
                }
                RDBMSStoreManager.this.invalidateColumnInfoForTable(v);
            }
            return new List[] { viewsCreated, autoCreateErrors };
        }
        
        private void verifyErrors(final List autoCreateErrors) {
            if (autoCreateErrors.size() > 0) {
                for (final Throwable exc : autoCreateErrors) {
                    if (RDBMSStoreManager.this.autoCreateWarnOnError) {
                        NucleusLogger.DATASTORE.warn(ClassAdder.LOCALISER_RDBMS.msg("050044", exc));
                    }
                    else {
                        NucleusLogger.DATASTORE.error(ClassAdder.LOCALISER_RDBMS.msg("050044", exc));
                    }
                }
                if (!RDBMSStoreManager.this.autoCreateWarnOnError) {
                    throw new NucleusDataStoreException(ClassAdder.LOCALISER_RDBMS.msg("050043"), autoCreateErrors.toArray(new Throwable[autoCreateErrors.size()]));
                }
            }
        }
        
        private void rollbackSchemaCreation(final List<Table> viewsCreated, final List<Table> tableConstraintsCreated, final List<Table> tablesCreated) {
            if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
                NucleusLogger.DATASTORE_SCHEMA.debug(ClassAdder.LOCALISER_RDBMS.msg("050040"));
            }
            try {
                if (viewsCreated != null) {
                    final ListIterator li = viewsCreated.listIterator(viewsCreated.size());
                    while (li.hasPrevious()) {
                        li.previous().drop(this.getCurrentConnection());
                    }
                }
                if (tableConstraintsCreated != null) {
                    final ListIterator li = tableConstraintsCreated.listIterator(tableConstraintsCreated.size());
                    while (li.hasPrevious()) {
                        li.previous().dropConstraints(this.getCurrentConnection());
                    }
                }
                if (tablesCreated != null) {
                    final ListIterator li = tablesCreated.listIterator(tablesCreated.size());
                    while (li.hasPrevious()) {
                        li.previous().drop(this.getCurrentConnection());
                    }
                }
            }
            catch (Exception e) {
                NucleusLogger.DATASTORE_SCHEMA.warn(ClassAdder.LOCALISER_RDBMS.msg("050041", e));
            }
            final AutoStartMechanism starter = RDBMSStoreManager.this.nucleusContext.getAutoStartMechanism();
            if (starter != null) {
                try {
                    if (!starter.isOpen()) {
                        starter.open();
                    }
                    for (final RDBMSStoreData sd : this.schemaDataAdded) {
                        starter.deleteClass(sd.getName());
                    }
                }
                finally {
                    if (starter.isOpen()) {
                        starter.close();
                    }
                }
            }
        }
        
        private Table addJoinTableForContainer(final AbstractMemberMetaData mmd, final ClassLoaderResolver clr, final int type) {
            DatastoreIdentifier tableName = null;
            final RDBMSStoreData sd = (RDBMSStoreData)RDBMSStoreManager.this.storeDataMgr.get(mmd);
            if (sd != null && sd.getDatastoreIdentifier() != null) {
                tableName = sd.getDatastoreIdentifier();
            }
            else {
                tableName = RDBMSStoreManager.this.identifierFactory.newTableIdentifier(mmd);
            }
            Table join = null;
            if (type == 1) {
                join = new CollectionTable(tableName, mmd, RDBMSStoreManager.this);
            }
            else if (type == 2) {
                join = new MapTable(tableName, mmd, RDBMSStoreManager.this);
            }
            else if (type == 3) {
                join = new ArrayTable(tableName, mmd, RDBMSStoreManager.this);
            }
            else if (type == 4) {
                join = new PersistableJoinTable(tableName, mmd, RDBMSStoreManager.this);
            }
            final AutoStartMechanism starter = RDBMSStoreManager.this.nucleusContext.getAutoStartMechanism();
            RDBMSStoreData data;
            try {
                if (starter != null && !starter.isOpen()) {
                    starter.open();
                }
                data = new RDBMSStoreData(mmd, join);
                AbstractStoreManager.this.registerStoreData(data);
            }
            finally {
                if (starter != null && starter.isOpen()) {
                    starter.close();
                }
            }
            this.schemaDataAdded.add(data);
            return join;
        }
    }
}
