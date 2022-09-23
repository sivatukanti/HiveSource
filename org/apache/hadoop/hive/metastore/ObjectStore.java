// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.net.InetAddress;
import java.util.Collections;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.model.MNotificationNextId;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.model.MNotificationLog;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.NotificationEventRequest;
import org.apache.hadoop.hive.metastore.api.ResourceType;
import org.apache.hadoop.hive.metastore.api.ResourceUri;
import org.apache.hadoop.hive.metastore.model.MResourceUri;
import org.apache.hadoop.hive.metastore.api.FunctionType;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.model.MFunction;
import javax.jdo.JDODataStoreException;
import org.datanucleus.store.rdbms.exceptions.MissingTableException;
import org.apache.hadoop.hive.metastore.model.MVersionTable;
import javax.jdo.identity.IntIdentity;
import org.apache.hadoop.hive.metastore.model.MMasterKey;
import org.apache.hadoop.hive.metastore.model.MDelegationToken;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.model.MPartitionColumnStatistics;
import org.apache.hadoop.hive.metastore.model.MTableColumnStatistics;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.hadoop.hive.metastore.api.UnknownPartitionException;
import org.apache.hadoop.hive.metastore.api.InvalidPartitionException;
import org.apache.hadoop.hive.metastore.api.UnknownTableException;
import org.apache.hadoop.hive.metastore.model.MPartitionEvent;
import org.apache.hadoop.hive.metastore.api.PartitionEventType;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.HiveObjectType;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.model.MGlobalPrivilege;
import org.apache.hadoop.hive.metastore.model.MRoleMap;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.model.MRole;
import org.apache.hadoop.hive.metastore.model.MIndex;
import org.apache.hadoop.hive.metastore.api.Index;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.TokenSource;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.CharStream;
import org.apache.hadoop.hive.metastore.parser.FilterLexer;
import org.apache.hadoop.hive.metastore.parser.FilterParser;
import java.util.HashSet;
import org.apache.hadoop.hive.common.ObjectPair;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory;
import org.apache.hadoop.hive.serde2.typeinfo.PrimitiveTypeInfo;
import java.util.LinkedList;
import org.apache.hadoop.hive.metastore.parser.ExpressionTree;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.common.FileUtils;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.model.MPartition;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.Set;
import java.util.HashMap;
import org.apache.hadoop.hive.metastore.model.MStringList;
import org.apache.hadoop.hive.metastore.api.SkewedInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.model.MStorageDescriptor;
import org.apache.hadoop.hive.metastore.model.MColumnDescriptor;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.model.MSerDeInfo;
import org.apache.hadoop.hive.metastore.model.MOrder;
import org.apache.hadoop.hive.metastore.api.Order;
import com.google.common.collect.Lists;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.model.MPartitionColumnPrivilege;
import org.apache.hadoop.hive.metastore.model.MPartitionPrivilege;
import org.apache.hadoop.hive.metastore.model.MTableColumnPrivilege;
import org.apache.hadoop.hive.metastore.model.MTablePrivilege;
import org.apache.hadoop.hive.metastore.api.PrivilegeGrantInfo;
import org.apache.hadoop.hive.metastore.api.PrincipalPrivilegeSet;
import org.apache.hadoop.hive.metastore.model.MTable;
import org.apache.hadoop.hive.metastore.api.Table;
import javax.jdo.JDOObjectNotFoundException;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.model.MFieldSchema;
import org.apache.hadoop.hive.metastore.model.MType;
import org.apache.hadoop.hive.metastore.api.Type;
import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.model.MDBPrivilege;
import java.util.List;
import javax.jdo.Query;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hive.common.util.HiveStringUtils;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.model.MDatabase;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;
import javax.jdo.datastore.DataStoreCache;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import javax.jdo.JDOHelper;
import java.util.Iterator;
import java.io.IOException;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.conf.HiveConf;
import java.util.regex.Pattern;
import javax.jdo.Transaction;
import org.apache.hadoop.conf.Configuration;
import javax.jdo.PersistenceManager;
import java.util.Map;
import org.apache.commons.logging.Log;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import javax.jdo.PersistenceManagerFactory;
import java.util.Properties;
import org.apache.hadoop.conf.Configurable;

public class ObjectStore implements RawStore, Configurable
{
    private static Properties prop;
    private static PersistenceManagerFactory pmf;
    private static Lock pmfPropLock;
    private static final AtomicBoolean isSchemaVerified;
    private static final Log LOG;
    private static final Map<String, Class> PINCLASSMAP;
    private static final String HOSTNAME;
    private static final String USER;
    private boolean isInitialized;
    private PersistenceManager pm;
    private MetaStoreDirectSql directSql;
    private PartitionExpressionProxy expressionProxy;
    private Configuration hiveConf;
    int openTrasactionCalls;
    private Transaction currentTransaction;
    private TXN_STATUS transactionStatus;
    private Pattern partitionValidationPattern;
    private ClassLoader classLoader;
    private static final int stackLimit = 5;
    
    public ObjectStore() {
        this.isInitialized = false;
        this.pm = null;
        this.directSql = null;
        this.expressionProxy = null;
        this.openTrasactionCalls = 0;
        this.currentTransaction = null;
        this.transactionStatus = TXN_STATUS.NO_STATE;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        if (this.classLoader == null) {
            this.classLoader = ObjectStore.class.getClassLoader();
        }
    }
    
    @Override
    public Configuration getConf() {
        return this.hiveConf;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        ObjectStore.pmfPropLock.lock();
        try {
            this.isInitialized = false;
            this.hiveConf = conf;
            final Properties propsFromConf = getDataSourceProps(conf);
            final boolean propsChanged = !propsFromConf.equals(ObjectStore.prop);
            if (propsChanged) {
                ObjectStore.pmf = null;
                ObjectStore.prop = null;
            }
            assert !this.isActiveTransaction();
            this.shutdown();
            this.pm = null;
            this.directSql = null;
            this.expressionProxy = null;
            this.openTrasactionCalls = 0;
            this.currentTransaction = null;
            this.transactionStatus = TXN_STATUS.NO_STATE;
            this.initialize(propsFromConf);
            final String partitionValidationRegex = this.hiveConf.get(HiveConf.ConfVars.METASTORE_PARTITION_NAME_WHITELIST_PATTERN.name());
            if (partitionValidationRegex != null && partitionValidationRegex.equals("")) {
                this.partitionValidationPattern = Pattern.compile(partitionValidationRegex);
            }
            else {
                this.partitionValidationPattern = null;
            }
            if (!this.isInitialized) {
                throw new RuntimeException("Unable to create persistence manager. Check dss.log for details");
            }
            ObjectStore.LOG.info("Initialized ObjectStore");
        }
        finally {
            ObjectStore.pmfPropLock.unlock();
        }
    }
    
    private void initialize(final Properties dsProps) {
        ObjectStore.LOG.info("ObjectStore, initialize called");
        ObjectStore.prop = dsProps;
        this.pm = this.getPersistenceManager();
        this.isInitialized = (this.pm != null);
        if (this.isInitialized) {
            this.expressionProxy = createExpressionProxy(this.hiveConf);
            this.directSql = new MetaStoreDirectSql(this.pm, this.hiveConf);
        }
        ObjectStore.LOG.debug("RawStore: " + this + ", with PersistenceManager: " + this.pm + " created in the thread with id: " + Thread.currentThread().getId());
    }
    
    private static PartitionExpressionProxy createExpressionProxy(final Configuration conf) {
        final String className = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_EXPRESSION_PROXY_CLASS);
        try {
            final Class<? extends PartitionExpressionProxy> clazz = (Class<? extends PartitionExpressionProxy>)MetaStoreUtils.getClass(className);
            return MetaStoreUtils.newInstance(clazz, new Class[0], new Object[0]);
        }
        catch (MetaException e) {
            ObjectStore.LOG.error("Error loading PartitionExpressionProxy", e);
            throw new RuntimeException("Error loading PartitionExpressionProxy: " + e.getMessage());
        }
    }
    
    private static Properties getDataSourceProps(final Configuration conf) {
        final Properties prop = new Properties();
        for (final Map.Entry<String, String> e : conf) {
            if (e.getKey().contains("datanucleus") || e.getKey().contains("jdo")) {
                final Object prevVal = prop.setProperty(e.getKey(), conf.get(e.getKey()));
                if (!ObjectStore.LOG.isDebugEnabled() || e.getKey().equals(HiveConf.ConfVars.METASTOREPWD.varname)) {
                    continue;
                }
                ObjectStore.LOG.debug("Overriding " + e.getKey() + " value " + prevVal + " from  jpox.properties with " + e.getValue());
            }
        }
        try {
            final String passwd = ShimLoader.getHadoopShims().getPassword(conf, HiveConf.ConfVars.METASTOREPWD.varname);
            if (passwd != null && !passwd.isEmpty()) {
                prop.setProperty(HiveConf.ConfVars.METASTOREPWD.varname, passwd);
            }
        }
        catch (IOException err) {
            throw new RuntimeException("Error getting metastore password: " + err.getMessage(), err);
        }
        if (ObjectStore.LOG.isDebugEnabled()) {
            for (final Map.Entry<Object, Object> e2 : prop.entrySet()) {
                if (!e2.getKey().equals(HiveConf.ConfVars.METASTOREPWD.varname)) {
                    ObjectStore.LOG.debug(e2.getKey() + " = " + e2.getValue());
                }
            }
        }
        return prop;
    }
    
    private static synchronized PersistenceManagerFactory getPMF() {
        if (ObjectStore.pmf == null) {
            ObjectStore.pmf = JDOHelper.getPersistenceManagerFactory(ObjectStore.prop);
            final DataStoreCache dsc = ObjectStore.pmf.getDataStoreCache();
            if (dsc != null) {
                final HiveConf conf = new HiveConf(ObjectStore.class);
                String objTypes = HiveConf.getVar(conf, HiveConf.ConfVars.METASTORE_CACHE_PINOBJTYPES);
                ObjectStore.LOG.info("Setting MetaStore object pin classes with hive.metastore.cache.pinobjtypes=\"" + objTypes + "\"");
                if (objTypes != null && objTypes.length() > 0) {
                    objTypes = objTypes.toLowerCase();
                    final String[] split;
                    final String[] typeTokens = split = objTypes.split(",");
                    for (String type : split) {
                        type = type.trim();
                        if (ObjectStore.PINCLASSMAP.containsKey(type)) {
                            dsc.pinAll(true, ObjectStore.PINCLASSMAP.get(type));
                        }
                        else {
                            ObjectStore.LOG.warn(type + " is not one of the pinnable object types: " + StringUtils.join(ObjectStore.PINCLASSMAP.keySet(), " "));
                        }
                    }
                }
            }
            else {
                ObjectStore.LOG.warn("PersistenceManagerFactory returned null DataStoreCache object. Unable to initialize object pin types defined by hive.metastore.cache.pinobjtypes");
            }
        }
        return ObjectStore.pmf;
    }
    
    @InterfaceAudience.LimitedPrivate({ "HCATALOG" })
    @InterfaceStability.Evolving
    public PersistenceManager getPersistenceManager() {
        return getPMF().getPersistenceManager();
    }
    
    @Override
    public void shutdown() {
        if (this.pm != null) {
            ObjectStore.LOG.debug("RawStore: " + this + ", with PersistenceManager: " + this.pm + " will be shutdown");
            this.pm.close();
        }
    }
    
    @Override
    public boolean openTransaction() {
        ++this.openTrasactionCalls;
        if (this.openTrasactionCalls == 1) {
            (this.currentTransaction = this.pm.currentTransaction()).begin();
            this.transactionStatus = TXN_STATUS.OPEN;
        }
        else if (this.currentTransaction == null || !this.currentTransaction.isActive()) {
            throw new RuntimeException("openTransaction called in an interior transaction scope, but currentTransaction is not active.");
        }
        final boolean result = this.currentTransaction.isActive();
        this.debugLog("Open transaction: count = " + this.openTrasactionCalls + ", isActive = " + result);
        return result;
    }
    
    @Override
    public boolean commitTransaction() {
        if (TXN_STATUS.ROLLBACK == this.transactionStatus) {
            this.debugLog("Commit transaction: rollback");
            return false;
        }
        if (this.openTrasactionCalls <= 0) {
            final RuntimeException e = new RuntimeException("commitTransaction was called but openTransactionCalls = " + this.openTrasactionCalls + ". This probably indicates that there are unbalanced " + "calls to openTransaction/commitTransaction");
            ObjectStore.LOG.error(e);
            throw e;
        }
        if (!this.currentTransaction.isActive()) {
            final RuntimeException e = new RuntimeException("commitTransaction was called but openTransactionCalls = " + this.openTrasactionCalls + ". This probably indicates that there are unbalanced " + "calls to openTransaction/commitTransaction");
            ObjectStore.LOG.error(e);
            throw e;
        }
        --this.openTrasactionCalls;
        this.debugLog("Commit transaction: count = " + this.openTrasactionCalls + ", isactive " + this.currentTransaction.isActive());
        if (this.openTrasactionCalls == 0 && this.currentTransaction.isActive()) {
            this.transactionStatus = TXN_STATUS.COMMITED;
            this.currentTransaction.commit();
        }
        return true;
    }
    
    public boolean isActiveTransaction() {
        return this.currentTransaction != null && this.currentTransaction.isActive();
    }
    
    @Override
    public void rollbackTransaction() {
        if (this.openTrasactionCalls < 1) {
            this.debugLog("rolling back transaction: no open transactions: " + this.openTrasactionCalls);
            return;
        }
        this.debugLog("Rollback transaction, isActive: " + this.currentTransaction.isActive());
        try {
            if (this.currentTransaction.isActive() && this.transactionStatus != TXN_STATUS.ROLLBACK) {
                this.currentTransaction.rollback();
            }
        }
        finally {
            this.openTrasactionCalls = 0;
            this.transactionStatus = TXN_STATUS.ROLLBACK;
            this.pm.evictAll();
        }
    }
    
    @Override
    public void createDatabase(final Database db) throws InvalidObjectException, MetaException {
        boolean commited = false;
        final MDatabase mdb = new MDatabase();
        mdb.setName(db.getName().toLowerCase());
        mdb.setLocationUri(db.getLocationUri());
        mdb.setDescription(db.getDescription());
        mdb.setParameters(db.getParameters());
        mdb.setOwnerName(db.getOwnerName());
        final PrincipalType ownerType = db.getOwnerType();
        mdb.setOwnerType((null == ownerType) ? PrincipalType.USER.name() : ownerType.name());
        try {
            this.openTransaction();
            this.pm.makePersistent(mdb);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    private MDatabase getMDatabase(String name) throws NoSuchObjectException {
        MDatabase mdb = null;
        boolean commited = false;
        try {
            this.openTransaction();
            name = HiveStringUtils.normalizeIdentifier(name);
            final Query query = this.pm.newQuery(MDatabase.class, "name == dbname");
            query.declareParameters("java.lang.String dbname");
            query.setUnique(true);
            mdb = (MDatabase)query.execute(name);
            this.pm.retrieve(mdb);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        if (mdb == null) {
            throw new NoSuchObjectException("There is no database named " + name);
        }
        return mdb;
    }
    
    @Override
    public Database getDatabase(final String name) throws NoSuchObjectException {
        MetaException ex = null;
        Database db = null;
        try {
            db = this.getDatabaseInternal(name);
        }
        catch (MetaException e) {
            ex = e;
        }
        if (db == null) {
            ObjectStore.LOG.warn("Failed to get database " + name + ", returning NoSuchObjectException", ex);
            throw new NoSuchObjectException(name + ((ex == null) ? "" : (": " + ex.getMessage())));
        }
        return db;
    }
    
    public Database getDatabaseInternal(final String name) throws MetaException, NoSuchObjectException {
        return new GetDbHelper(name, null, true, true) {
            @Override
            protected Database getSqlResult(final GetHelper<Database> ctx) throws MetaException {
                return ObjectStore.this.directSql.getDatabase(this.dbName);
            }
            
            @Override
            protected Database getJdoResult(final GetHelper<Database> ctx) throws MetaException, NoSuchObjectException {
                return ObjectStore.this.getJDODatabase(this.dbName);
            }
        }.run(false);
    }
    
    public Database getJDODatabase(final String name) throws NoSuchObjectException {
        MDatabase mdb = null;
        boolean commited = false;
        try {
            this.openTransaction();
            mdb = this.getMDatabase(name);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        final Database db = new Database();
        db.setName(mdb.getName());
        db.setDescription(mdb.getDescription());
        db.setLocationUri(mdb.getLocationUri());
        db.setParameters(this.convertMap(mdb.getParameters()));
        db.setOwnerName(mdb.getOwnerName());
        final String type = mdb.getOwnerType();
        db.setOwnerType((null == type || type.trim().isEmpty()) ? null : PrincipalType.valueOf(type));
        return db;
    }
    
    @Override
    public boolean alterDatabase(final String dbName, final Database db) throws MetaException, NoSuchObjectException {
        MDatabase mdb = null;
        boolean committed = false;
        try {
            mdb = this.getMDatabase(dbName);
            mdb.setParameters(db.getParameters());
            mdb.setOwnerName(db.getOwnerName());
            if (db.getOwnerType() != null) {
                mdb.setOwnerType(db.getOwnerType().name());
            }
            this.openTransaction();
            this.pm.makePersistent(mdb);
            committed = this.commitTransaction();
            if (!committed) {
                this.rollbackTransaction();
                return false;
            }
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean dropDatabase(String dbname) throws NoSuchObjectException, MetaException {
        boolean success = false;
        ObjectStore.LOG.info("Dropping database " + dbname + " along with all tables");
        dbname = HiveStringUtils.normalizeIdentifier(dbname);
        try {
            this.openTransaction();
            final MDatabase db = this.getMDatabase(dbname);
            this.pm.retrieve(db);
            if (db != null) {
                final List<MDBPrivilege> dbGrants = this.listDatabaseGrants(dbname);
                if (dbGrants != null && dbGrants.size() > 0) {
                    this.pm.deletePersistentAll(dbGrants);
                }
                this.pm.deletePersistent(db);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public List<String> getDatabases(final String pattern) throws MetaException {
        boolean commited = false;
        List<String> databases = null;
        try {
            this.openTransaction();
            final String[] subpatterns = pattern.trim().split("\\|");
            String query = "select name from org.apache.hadoop.hive.metastore.model.MDatabase where (";
            boolean first = true;
            for (String subpattern : subpatterns) {
                subpattern = "(?i)" + subpattern.replaceAll("\\*", ".*");
                if (!first) {
                    query += " || ";
                }
                query = query + " name.matches(\"" + subpattern + "\")";
                first = false;
            }
            query += ")";
            final Query q = this.pm.newQuery(query);
            q.setResult("name");
            q.setOrdering("name ascending");
            final Collection names = (Collection)q.execute();
            databases = new ArrayList<String>();
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                databases.add(i.next());
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return databases;
    }
    
    @Override
    public List<String> getAllDatabases() throws MetaException {
        return this.getDatabases(".*");
    }
    
    private MType getMType(final Type type) {
        final List<MFieldSchema> fields = new ArrayList<MFieldSchema>();
        if (type.getFields() != null) {
            for (final FieldSchema field : type.getFields()) {
                fields.add(new MFieldSchema(field.getName(), field.getType(), field.getComment()));
            }
        }
        return new MType(type.getName(), type.getType1(), type.getType2(), fields);
    }
    
    private Type getType(final MType mtype) {
        final List<FieldSchema> fields = new ArrayList<FieldSchema>();
        if (mtype.getFields() != null) {
            for (final MFieldSchema field : mtype.getFields()) {
                fields.add(new FieldSchema(field.getName(), field.getType(), field.getComment()));
            }
        }
        final Type ret = new Type();
        ret.setName(mtype.getName());
        ret.setType1(mtype.getType1());
        ret.setType2(mtype.getType2());
        ret.setFields(fields);
        return ret;
    }
    
    @Override
    public boolean createType(final Type type) {
        boolean success = false;
        final MType mtype = this.getMType(type);
        boolean commited = false;
        try {
            this.openTransaction();
            this.pm.makePersistent(mtype);
            commited = this.commitTransaction();
            success = true;
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public Type getType(final String typeName) {
        Type type = null;
        boolean commited = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MType.class, "name == typeName");
            query.declareParameters("java.lang.String typeName");
            query.setUnique(true);
            final MType mtype = (MType)query.execute(typeName.trim());
            this.pm.retrieve(type);
            if (mtype != null) {
                type = this.getType(mtype);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return type;
    }
    
    @Override
    public boolean dropType(final String typeName) {
        boolean success = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MType.class, "name == typeName");
            query.declareParameters("java.lang.String typeName");
            query.setUnique(true);
            final MType type = (MType)query.execute(typeName.trim());
            this.pm.retrieve(type);
            if (type != null) {
                this.pm.deletePersistent(type);
            }
            success = this.commitTransaction();
        }
        catch (JDOObjectNotFoundException e) {
            success = this.commitTransaction();
            ObjectStore.LOG.debug("type not found " + typeName, e);
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public void createTable(final Table tbl) throws InvalidObjectException, MetaException {
        boolean commited = false;
        try {
            this.openTransaction();
            final MTable mtbl = this.convertToMTable(tbl);
            this.pm.makePersistent(mtbl);
            final PrincipalPrivilegeSet principalPrivs = tbl.getPrivileges();
            final List<Object> toPersistPrivObjs = new ArrayList<Object>();
            if (principalPrivs != null) {
                final int now = (int)(System.currentTimeMillis() / 1000L);
                final Map<String, List<PrivilegeGrantInfo>> userPrivs = principalPrivs.getUserPrivileges();
                this.putPersistentPrivObjects(mtbl, toPersistPrivObjs, now, userPrivs, PrincipalType.USER);
                final Map<String, List<PrivilegeGrantInfo>> groupPrivs = principalPrivs.getGroupPrivileges();
                this.putPersistentPrivObjects(mtbl, toPersistPrivObjs, now, groupPrivs, PrincipalType.GROUP);
                final Map<String, List<PrivilegeGrantInfo>> rolePrivs = principalPrivs.getRolePrivileges();
                this.putPersistentPrivObjects(mtbl, toPersistPrivObjs, now, rolePrivs, PrincipalType.ROLE);
            }
            this.pm.makePersistentAll(toPersistPrivObjs);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    private void putPersistentPrivObjects(final MTable mtbl, final List<Object> toPersistPrivObjs, final int now, final Map<String, List<PrivilegeGrantInfo>> privMap, final PrincipalType type) {
        if (privMap != null) {
            for (final Map.Entry<String, List<PrivilegeGrantInfo>> entry : privMap.entrySet()) {
                final String principalName = entry.getKey();
                final List<PrivilegeGrantInfo> privs = entry.getValue();
                for (int i = 0; i < privs.size(); ++i) {
                    final PrivilegeGrantInfo priv = privs.get(i);
                    if (priv != null) {
                        final MTablePrivilege mTblSec = new MTablePrivilege(principalName, type.toString(), mtbl, priv.getPrivilege(), now, priv.getGrantor(), priv.getGrantorType().toString(), priv.isGrantOption());
                        toPersistPrivObjs.add(mTblSec);
                    }
                }
            }
        }
    }
    
    @Override
    public boolean dropTable(final String dbName, final String tableName) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException {
        boolean success = false;
        try {
            this.openTransaction();
            final MTable tbl = this.getMTable(dbName, tableName);
            this.pm.retrieve(tbl);
            if (tbl != null) {
                final List<MTablePrivilege> tabGrants = this.listAllTableGrants(dbName, tableName);
                if (tabGrants != null && tabGrants.size() > 0) {
                    this.pm.deletePersistentAll(tabGrants);
                }
                final List<MTableColumnPrivilege> tblColGrants = this.listTableAllColumnGrants(dbName, tableName);
                if (tblColGrants != null && tblColGrants.size() > 0) {
                    this.pm.deletePersistentAll(tblColGrants);
                }
                final List<MPartitionPrivilege> partGrants = this.listTableAllPartitionGrants(dbName, tableName);
                if (partGrants != null && partGrants.size() > 0) {
                    this.pm.deletePersistentAll(partGrants);
                }
                final List<MPartitionColumnPrivilege> partColGrants = this.listTableAllPartitionColumnGrants(dbName, tableName);
                if (partColGrants != null && partColGrants.size() > 0) {
                    this.pm.deletePersistentAll(partColGrants);
                }
                try {
                    this.deleteTableColumnStatistics(dbName, tableName, null);
                }
                catch (NoSuchObjectException e) {
                    ObjectStore.LOG.info("Found no table level column statistics associated with db " + dbName + " table " + tableName + " record to delete");
                }
                this.preDropStorageDescriptor(tbl.getSd());
                this.pm.deletePersistentAll(tbl);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public Table getTable(final String dbName, final String tableName) throws MetaException {
        boolean commited = false;
        Table tbl = null;
        try {
            this.openTransaction();
            tbl = this.convertToTable(this.getMTable(dbName, tableName));
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return tbl;
    }
    
    @Override
    public List<String> getTables(String dbName, final String pattern) throws MetaException {
        boolean commited = false;
        List<String> tbls = null;
        try {
            this.openTransaction();
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            final String[] subpatterns = pattern.trim().split("\\|");
            String query = "select tableName from org.apache.hadoop.hive.metastore.model.MTable where database.name == dbName && (";
            boolean first = true;
            for (String subpattern : subpatterns) {
                subpattern = "(?i)" + subpattern.replaceAll("\\*", ".*");
                if (!first) {
                    query += " || ";
                }
                query = query + " tableName.matches(\"" + subpattern + "\")";
                first = false;
            }
            query += ")";
            final Query q = this.pm.newQuery(query);
            q.declareParameters("java.lang.String dbName");
            q.setResult("tableName");
            q.setOrdering("tableName ascending");
            final Collection names = (Collection)q.execute(dbName);
            tbls = new ArrayList<String>();
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                tbls.add(i.next());
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return tbls;
    }
    
    @Override
    public List<String> getAllTables(final String dbName) throws MetaException {
        return this.getTables(dbName, ".*");
    }
    
    private MTable getMTable(String db, String table) {
        MTable mtbl = null;
        boolean commited = false;
        try {
            this.openTransaction();
            db = HiveStringUtils.normalizeIdentifier(db);
            table = HiveStringUtils.normalizeIdentifier(table);
            final Query query = this.pm.newQuery(MTable.class, "tableName == table && database.name == db");
            query.declareParameters("java.lang.String table, java.lang.String db");
            query.setUnique(true);
            mtbl = (MTable)query.execute(table, db);
            this.pm.retrieve(mtbl);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return mtbl;
    }
    
    @Override
    public List<Table> getTableObjectsByName(String db, final List<String> tbl_names) throws MetaException, UnknownDBException {
        final List<Table> tables = new ArrayList<Table>();
        boolean committed = false;
        try {
            this.openTransaction();
            db = HiveStringUtils.normalizeIdentifier(db);
            final Query dbExistsQuery = this.pm.newQuery(MDatabase.class, "name == db");
            dbExistsQuery.declareParameters("java.lang.String db");
            dbExistsQuery.setUnique(true);
            dbExistsQuery.setResult("name");
            final String dbNameIfExists = (String)dbExistsQuery.execute(db);
            if (dbNameIfExists == null || dbNameIfExists.isEmpty()) {
                throw new UnknownDBException("Could not find database " + db);
            }
            final List<String> lowered_tbl_names = new ArrayList<String>();
            for (final String t : tbl_names) {
                lowered_tbl_names.add(HiveStringUtils.normalizeIdentifier(t));
            }
            final Query query = this.pm.newQuery(MTable.class);
            query.setFilter("database.name == db && tbl_names.contains(tableName)");
            query.declareParameters("java.lang.String db, java.util.Collection tbl_names");
            final Collection mtables = (Collection)query.execute(db, lowered_tbl_names);
            final Iterator iter = mtables.iterator();
            while (iter.hasNext()) {
                tables.add(this.convertToTable(iter.next()));
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        return tables;
    }
    
    private <T> List<T> convertList(final List<T> dnList) {
        return (List<T>)((dnList == null) ? null : Lists.newArrayList((Iterable<?>)dnList));
    }
    
    private Map<String, String> convertMap(final Map<String, String> dnMap) {
        return MetaStoreUtils.trimMapNulls(dnMap, HiveConf.getBoolVar(this.getConf(), HiveConf.ConfVars.METASTORE_ORM_RETRIEVE_MAPNULLS_AS_EMPTY_STRINGS));
    }
    
    private Table convertToTable(final MTable mtbl) throws MetaException {
        if (mtbl == null) {
            return null;
        }
        String tableType = mtbl.getTableType();
        if (tableType == null) {
            if (mtbl.getViewOriginalText() != null) {
                tableType = TableType.VIRTUAL_VIEW.toString();
            }
            else if ("TRUE".equals(mtbl.getParameters().get("EXTERNAL"))) {
                tableType = TableType.EXTERNAL_TABLE.toString();
            }
            else {
                tableType = TableType.MANAGED_TABLE.toString();
            }
        }
        return new Table(mtbl.getTableName(), mtbl.getDatabase().getName(), mtbl.getOwner(), mtbl.getCreateTime(), mtbl.getLastAccessTime(), mtbl.getRetention(), this.convertToStorageDescriptor(mtbl.getSd()), this.convertToFieldSchemas(mtbl.getPartitionKeys()), this.convertMap(mtbl.getParameters()), mtbl.getViewOriginalText(), mtbl.getViewExpandedText(), tableType);
    }
    
    private MTable convertToMTable(final Table tbl) throws InvalidObjectException, MetaException {
        if (tbl == null) {
            return null;
        }
        MDatabase mdb = null;
        try {
            mdb = this.getMDatabase(tbl.getDbName());
        }
        catch (NoSuchObjectException e) {
            ObjectStore.LOG.error(org.apache.hadoop.util.StringUtils.stringifyException(e));
            throw new InvalidObjectException("Database " + tbl.getDbName() + " doesn't exist.");
        }
        String tableType = tbl.getTableType();
        final boolean isExternal = "TRUE".equals(tbl.getParameters().get("EXTERNAL"));
        if (TableType.MANAGED_TABLE.toString().equals(tableType) && isExternal) {
            tableType = TableType.EXTERNAL_TABLE.toString();
        }
        if (TableType.EXTERNAL_TABLE.toString().equals(tableType) && !isExternal) {
            tableType = TableType.MANAGED_TABLE.toString();
        }
        return new MTable(HiveStringUtils.normalizeIdentifier(tbl.getTableName()), mdb, this.convertToMStorageDescriptor(tbl.getSd()), tbl.getOwner(), tbl.getCreateTime(), tbl.getLastAccessTime(), tbl.getRetention(), this.convertToMFieldSchemas(tbl.getPartitionKeys()), tbl.getParameters(), tbl.getViewOriginalText(), tbl.getViewExpandedText(), tableType);
    }
    
    private List<MFieldSchema> convertToMFieldSchemas(final List<FieldSchema> keys) {
        List<MFieldSchema> mkeys = null;
        if (keys != null) {
            mkeys = new ArrayList<MFieldSchema>(keys.size());
            for (final FieldSchema part : keys) {
                mkeys.add(new MFieldSchema(HiveStringUtils.normalizeIdentifier(part.getName()), part.getType(), part.getComment()));
            }
        }
        return mkeys;
    }
    
    private List<FieldSchema> convertToFieldSchemas(final List<MFieldSchema> mkeys) {
        List<FieldSchema> keys = null;
        if (mkeys != null) {
            keys = new ArrayList<FieldSchema>(mkeys.size());
            for (final MFieldSchema part : mkeys) {
                keys.add(new FieldSchema(part.getName(), part.getType(), part.getComment()));
            }
        }
        return keys;
    }
    
    private List<MOrder> convertToMOrders(final List<Order> keys) {
        List<MOrder> mkeys = null;
        if (keys != null) {
            mkeys = new ArrayList<MOrder>(keys.size());
            for (final Order part : keys) {
                mkeys.add(new MOrder(HiveStringUtils.normalizeIdentifier(part.getCol()), part.getOrder()));
            }
        }
        return mkeys;
    }
    
    private List<Order> convertToOrders(final List<MOrder> mkeys) {
        List<Order> keys = null;
        if (mkeys != null) {
            keys = new ArrayList<Order>(mkeys.size());
            for (final MOrder part : mkeys) {
                keys.add(new Order(part.getCol(), part.getOrder()));
            }
        }
        return keys;
    }
    
    private SerDeInfo convertToSerDeInfo(final MSerDeInfo ms) throws MetaException {
        if (ms == null) {
            throw new MetaException("Invalid SerDeInfo object");
        }
        return new SerDeInfo(ms.getName(), ms.getSerializationLib(), this.convertMap(ms.getParameters()));
    }
    
    private MSerDeInfo convertToMSerDeInfo(final SerDeInfo ms) throws MetaException {
        if (ms == null) {
            throw new MetaException("Invalid SerDeInfo object");
        }
        return new MSerDeInfo(ms.getName(), ms.getSerializationLib(), ms.getParameters());
    }
    
    private MColumnDescriptor createNewMColumnDescriptor(final List<MFieldSchema> cols) {
        if (cols == null) {
            return null;
        }
        return new MColumnDescriptor(cols);
    }
    
    private StorageDescriptor convertToStorageDescriptor(final MStorageDescriptor msd, final boolean noFS) throws MetaException {
        if (msd == null) {
            return null;
        }
        final List<MFieldSchema> mFieldSchemas = (msd.getCD() == null) ? null : msd.getCD().getCols();
        final StorageDescriptor sd = new StorageDescriptor(noFS ? null : this.convertToFieldSchemas(mFieldSchemas), msd.getLocation(), msd.getInputFormat(), msd.getOutputFormat(), msd.isCompressed(), msd.getNumBuckets(), this.convertToSerDeInfo(msd.getSerDeInfo()), this.convertList(msd.getBucketCols()), this.convertToOrders(msd.getSortCols()), this.convertMap(msd.getParameters()));
        final SkewedInfo skewedInfo = new SkewedInfo(this.convertList(msd.getSkewedColNames()), this.convertToSkewedValues(msd.getSkewedColValues()), this.covertToSkewedMap(msd.getSkewedColValueLocationMaps()));
        sd.setSkewedInfo(skewedInfo);
        sd.setStoredAsSubDirectories(msd.isStoredAsSubDirectories());
        return sd;
    }
    
    private StorageDescriptor convertToStorageDescriptor(final MStorageDescriptor msd) throws MetaException {
        return this.convertToStorageDescriptor(msd, false);
    }
    
    private List<List<String>> convertToSkewedValues(final List<MStringList> mLists) {
        List<List<String>> lists = null;
        if (mLists != null) {
            lists = new ArrayList<List<String>>(mLists.size());
            for (final MStringList element : mLists) {
                lists.add(new ArrayList<String>(element.getInternalList()));
            }
        }
        return lists;
    }
    
    private List<MStringList> convertToMStringLists(final List<List<String>> mLists) {
        List<MStringList> lists = null;
        if (null != mLists) {
            lists = new ArrayList<MStringList>();
            for (final List<String> mList : mLists) {
                lists.add(new MStringList(mList));
            }
        }
        return lists;
    }
    
    private Map<List<String>, String> covertToSkewedMap(final Map<MStringList, String> mMap) {
        Map<List<String>, String> map = null;
        if (mMap != null) {
            map = new HashMap<List<String>, String>(mMap.size());
            final Set<MStringList> keys = mMap.keySet();
            for (final MStringList key : keys) {
                map.put(new ArrayList<String>(key.getInternalList()), mMap.get(key));
            }
        }
        return map;
    }
    
    private Map<MStringList, String> covertToMapMStringList(final Map<List<String>, String> mMap) {
        Map<MStringList, String> map = null;
        if (mMap != null) {
            map = new HashMap<MStringList, String>(mMap.size());
            final Set<List<String>> keys = mMap.keySet();
            for (final List<String> key : keys) {
                map.put(new MStringList(key), mMap.get(key));
            }
        }
        return map;
    }
    
    private MStorageDescriptor convertToMStorageDescriptor(final StorageDescriptor sd) throws MetaException {
        if (sd == null) {
            return null;
        }
        final MColumnDescriptor mcd = this.createNewMColumnDescriptor(this.convertToMFieldSchemas(sd.getCols()));
        return this.convertToMStorageDescriptor(sd, mcd);
    }
    
    private MStorageDescriptor convertToMStorageDescriptor(final StorageDescriptor sd, final MColumnDescriptor mcd) throws MetaException {
        if (sd == null) {
            return null;
        }
        return new MStorageDescriptor(mcd, sd.getLocation(), sd.getInputFormat(), sd.getOutputFormat(), sd.isCompressed(), sd.getNumBuckets(), this.convertToMSerDeInfo(sd.getSerdeInfo()), sd.getBucketCols(), this.convertToMOrders(sd.getSortCols()), sd.getParameters(), (null == sd.getSkewedInfo()) ? null : sd.getSkewedInfo().getSkewedColNames(), this.convertToMStringLists((null == sd.getSkewedInfo()) ? null : sd.getSkewedInfo().getSkewedColValues()), this.covertToMapMStringList((null == sd.getSkewedInfo()) ? null : sd.getSkewedInfo().getSkewedColValueLocationMaps()), sd.isStoredAsSubDirectories());
    }
    
    @Override
    public boolean addPartitions(final String dbName, final String tblName, final List<Partition> parts) throws InvalidObjectException, MetaException {
        boolean success = false;
        this.openTransaction();
        try {
            List<MTablePrivilege> tabGrants = null;
            List<MTableColumnPrivilege> tabColumnGrants = null;
            final MTable table = this.getMTable(dbName, tblName);
            if ("TRUE".equalsIgnoreCase(table.getParameters().get("PARTITION_LEVEL_PRIVILEGE"))) {
                tabGrants = this.listAllTableGrants(dbName, tblName);
                tabColumnGrants = this.listTableAllColumnGrants(dbName, tblName);
            }
            final List<Object> toPersist = new ArrayList<Object>();
            for (final Partition part : parts) {
                if (!part.getTableName().equals(tblName) || !part.getDbName().equals(dbName)) {
                    throw new MetaException("Partition does not belong to target table " + dbName + "." + tblName + ": " + part);
                }
                final MPartition mpart = this.convertToMPart(part, true);
                toPersist.add(mpart);
                final int now = (int)(System.currentTimeMillis() / 1000L);
                if (tabGrants != null) {
                    for (final MTablePrivilege tab : tabGrants) {
                        toPersist.add(new MPartitionPrivilege(tab.getPrincipalName(), tab.getPrincipalType(), mpart, tab.getPrivilege(), now, tab.getGrantor(), tab.getGrantorType(), tab.getGrantOption()));
                    }
                }
                if (tabColumnGrants == null) {
                    continue;
                }
                for (final MTableColumnPrivilege col : tabColumnGrants) {
                    toPersist.add(new MPartitionColumnPrivilege(col.getPrincipalName(), col.getPrincipalType(), mpart, col.getColumnName(), col.getPrivilege(), now, col.getGrantor(), col.getGrantorType(), col.getGrantOption()));
                }
            }
            if (toPersist.size() > 0) {
                this.pm.makePersistentAll(toPersist);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    private boolean isValidPartition(final Partition part, final boolean ifNotExists) throws MetaException {
        MetaStoreUtils.validatePartitionNameCharacters(part.getValues(), this.partitionValidationPattern);
        final boolean doesExist = this.doesPartitionExist(part.getDbName(), part.getTableName(), part.getValues());
        if (doesExist && !ifNotExists) {
            throw new MetaException("Partition already exists: " + part);
        }
        return !doesExist;
    }
    
    @Override
    public boolean addPartitions(final String dbName, final String tblName, final PartitionSpecProxy partitionSpec, final boolean ifNotExists) throws InvalidObjectException, MetaException {
        boolean success = false;
        this.openTransaction();
        try {
            List<MTablePrivilege> tabGrants = null;
            List<MTableColumnPrivilege> tabColumnGrants = null;
            final MTable table = this.getMTable(dbName, tblName);
            if ("TRUE".equalsIgnoreCase(table.getParameters().get("PARTITION_LEVEL_PRIVILEGE"))) {
                tabGrants = this.listAllTableGrants(dbName, tblName);
                tabColumnGrants = this.listTableAllColumnGrants(dbName, tblName);
            }
            if (!partitionSpec.getTableName().equals(tblName) || !partitionSpec.getDbName().equals(dbName)) {
                throw new MetaException("Partition does not belong to target table " + dbName + "." + tblName + ": " + partitionSpec);
            }
            final PartitionSpecProxy.PartitionIterator iterator = partitionSpec.getPartitionIterator();
            final int now = (int)(System.currentTimeMillis() / 1000L);
            while (iterator.hasNext()) {
                final Partition part = iterator.next();
                if (this.isValidPartition(part, ifNotExists)) {
                    final MPartition mpart = this.convertToMPart(part, true);
                    this.pm.makePersistent(mpart);
                    if (tabGrants != null) {
                        for (final MTablePrivilege tab : tabGrants) {
                            this.pm.makePersistent(new MPartitionPrivilege(tab.getPrincipalName(), tab.getPrincipalType(), mpart, tab.getPrivilege(), now, tab.getGrantor(), tab.getGrantorType(), tab.getGrantOption()));
                        }
                    }
                    if (tabColumnGrants == null) {
                        continue;
                    }
                    for (final MTableColumnPrivilege col : tabColumnGrants) {
                        this.pm.makePersistent(new MPartitionColumnPrivilege(col.getPrincipalName(), col.getPrincipalType(), mpart, col.getColumnName(), col.getPrivilege(), now, col.getGrantor(), col.getGrantorType(), col.getGrantOption()));
                    }
                }
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public boolean addPartition(final Partition part) throws InvalidObjectException, MetaException {
        boolean success = false;
        boolean commited = false;
        try {
            final MTable table = this.getMTable(part.getDbName(), part.getTableName());
            List<MTablePrivilege> tabGrants = null;
            List<MTableColumnPrivilege> tabColumnGrants = null;
            if ("TRUE".equalsIgnoreCase(table.getParameters().get("PARTITION_LEVEL_PRIVILEGE"))) {
                tabGrants = this.listAllTableGrants(part.getDbName(), part.getTableName());
                tabColumnGrants = this.listTableAllColumnGrants(part.getDbName(), part.getTableName());
            }
            this.openTransaction();
            final MPartition mpart = this.convertToMPart(part, true);
            this.pm.makePersistent(mpart);
            final int now = (int)(System.currentTimeMillis() / 1000L);
            final List<Object> toPersist = new ArrayList<Object>();
            if (tabGrants != null) {
                for (final MTablePrivilege tab : tabGrants) {
                    final MPartitionPrivilege partGrant = new MPartitionPrivilege(tab.getPrincipalName(), tab.getPrincipalType(), mpart, tab.getPrivilege(), now, tab.getGrantor(), tab.getGrantorType(), tab.getGrantOption());
                    toPersist.add(partGrant);
                }
            }
            if (tabColumnGrants != null) {
                for (final MTableColumnPrivilege col : tabColumnGrants) {
                    final MPartitionColumnPrivilege partColumn = new MPartitionColumnPrivilege(col.getPrincipalName(), col.getPrincipalType(), mpart, col.getColumnName(), col.getPrivilege(), now, col.getGrantor(), col.getGrantorType(), col.getGrantOption());
                    toPersist.add(partColumn);
                }
                if (toPersist.size() > 0) {
                    this.pm.makePersistentAll(toPersist);
                }
            }
            commited = this.commitTransaction();
            success = true;
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public Partition getPartition(final String dbName, final String tableName, final List<String> part_vals) throws NoSuchObjectException, MetaException {
        this.openTransaction();
        final Partition part = this.convertToPart(this.getMPartition(dbName, tableName, part_vals));
        this.commitTransaction();
        if (part == null) {
            throw new NoSuchObjectException("partition values=" + part_vals.toString());
        }
        part.setValues(part_vals);
        return part;
    }
    
    private MPartition getMPartition(String dbName, String tableName, final List<String> part_vals) throws MetaException {
        MPartition mpart = null;
        boolean commited = false;
        try {
            this.openTransaction();
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            tableName = HiveStringUtils.normalizeIdentifier(tableName);
            final MTable mtbl = this.getMTable(dbName, tableName);
            if (mtbl == null) {
                commited = this.commitTransaction();
                return null;
            }
            final String name = Warehouse.makePartName(this.convertToFieldSchemas(mtbl.getPartitionKeys()), part_vals);
            final Query query = this.pm.newQuery(MPartition.class, "table.tableName == t1 && table.database.name == t2 && partitionName == t3");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3");
            query.setUnique(true);
            mpart = (MPartition)query.execute(tableName, dbName, name);
            this.pm.retrieve(mpart);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return mpart;
    }
    
    private MPartition convertToMPart(final Partition part, final boolean useTableCD) throws InvalidObjectException, MetaException {
        if (part == null) {
            return null;
        }
        final MTable mt = this.getMTable(part.getDbName(), part.getTableName());
        if (mt == null) {
            throw new InvalidObjectException("Partition doesn't have a valid table or database name");
        }
        MStorageDescriptor msd;
        if (useTableCD && mt.getSd() != null && mt.getSd().getCD() != null && mt.getSd().getCD().getCols() != null && part.getSd() != null && this.convertToFieldSchemas(mt.getSd().getCD().getCols()).equals(part.getSd().getCols())) {
            msd = this.convertToMStorageDescriptor(part.getSd(), mt.getSd().getCD());
        }
        else {
            msd = this.convertToMStorageDescriptor(part.getSd());
        }
        return new MPartition(Warehouse.makePartName(this.convertToFieldSchemas(mt.getPartitionKeys()), part.getValues()), mt, part.getValues(), part.getCreateTime(), part.getLastAccessTime(), msd, part.getParameters());
    }
    
    private Partition convertToPart(final MPartition mpart) throws MetaException {
        if (mpart == null) {
            return null;
        }
        return new Partition(this.convertList(mpart.getValues()), mpart.getTable().getDatabase().getName(), mpart.getTable().getTableName(), mpart.getCreateTime(), mpart.getLastAccessTime(), this.convertToStorageDescriptor(mpart.getSd()), this.convertMap(mpart.getParameters()));
    }
    
    private Partition convertToPart(final String dbName, final String tblName, final MPartition mpart) throws MetaException {
        if (mpart == null) {
            return null;
        }
        return new Partition(this.convertList(mpart.getValues()), dbName, tblName, mpart.getCreateTime(), mpart.getLastAccessTime(), this.convertToStorageDescriptor(mpart.getSd(), false), this.convertMap(mpart.getParameters()));
    }
    
    @Override
    public boolean dropPartition(final String dbName, final String tableName, final List<String> part_vals) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException {
        boolean success = false;
        try {
            this.openTransaction();
            final MPartition part = this.getMPartition(dbName, tableName, part_vals);
            this.dropPartitionCommon(part);
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public void dropPartitions(final String dbName, final String tblName, final List<String> partNames) throws MetaException, NoSuchObjectException {
        if (partNames.isEmpty()) {
            return;
        }
        boolean success = false;
        this.openTransaction();
        try {
            this.dropPartitionGrantsNoTxn(dbName, tblName, partNames);
            this.dropPartitionAllColumnGrantsNoTxn(dbName, tblName, partNames);
            this.dropPartitionColumnStatisticsNoTxn(dbName, tblName, partNames);
            for (final MColumnDescriptor mcd : this.detachCdsFromSdsNoTxn(dbName, tblName, partNames)) {
                this.removeUnusedColumnDescriptor(mcd);
            }
            this.dropPartitionsNoTxn(dbName, tblName, partNames);
            if (!(success = this.commitTransaction())) {
                throw new MetaException("Failed to drop partitions");
            }
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private boolean dropPartitionCommon(final MPartition part) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        boolean success = false;
        try {
            this.openTransaction();
            if (part != null) {
                final List<MFieldSchema> schemas = part.getTable().getPartitionKeys();
                final List<String> colNames = new ArrayList<String>();
                for (final MFieldSchema col : schemas) {
                    colNames.add(col.getName());
                }
                final String partName = FileUtils.makePartName(colNames, part.getValues());
                final List<MPartitionPrivilege> partGrants = this.listPartitionGrants(part.getTable().getDatabase().getName(), part.getTable().getTableName(), Lists.newArrayList(partName));
                if (partGrants != null && partGrants.size() > 0) {
                    this.pm.deletePersistentAll(partGrants);
                }
                final List<MPartitionColumnPrivilege> partColumnGrants = this.listPartitionAllColumnGrants(part.getTable().getDatabase().getName(), part.getTable().getTableName(), Lists.newArrayList(partName));
                if (partColumnGrants != null && partColumnGrants.size() > 0) {
                    this.pm.deletePersistentAll(partColumnGrants);
                }
                final String dbName = part.getTable().getDatabase().getName();
                final String tableName = part.getTable().getTableName();
                try {
                    this.deletePartitionColumnStatistics(dbName, tableName, partName, part.getValues(), null);
                }
                catch (NoSuchObjectException e) {
                    ObjectStore.LOG.info("No column statistics records found to delete");
                }
                this.preDropStorageDescriptor(part.getSd());
                this.pm.deletePersistent(part);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public List<Partition> getPartitions(final String dbName, final String tableName, final int maxParts) throws MetaException, NoSuchObjectException {
        return this.getPartitionsInternal(dbName, tableName, maxParts, true, true);
    }
    
    protected List<Partition> getPartitionsInternal(final String dbName, final String tblName, final int maxParts, final boolean allowSql, final boolean allowJdo) throws MetaException, NoSuchObjectException {
        return ((GetHelper<List<Partition>>)new GetListHelper<Partition>(dbName, tblName, allowSql, allowJdo) {
            @Override
            protected List<Partition> getSqlResult(final GetHelper<List<Partition>> ctx) throws MetaException {
                final Integer max = (maxParts < 0) ? null : Integer.valueOf(maxParts);
                return ObjectStore.this.directSql.getPartitions(this.dbName, this.tblName, max);
            }
            
            @Override
            protected List<Partition> getJdoResult(final GetHelper<List<Partition>> ctx) throws MetaException, NoSuchObjectException {
                return ObjectStore.this.convertToParts(ObjectStore.this.listMPartitions(this.dbName, this.tblName, maxParts));
            }
        }).run(false);
    }
    
    @Override
    public List<Partition> getPartitionsWithAuth(final String dbName, final String tblName, final short max, final String userName, final List<String> groupNames) throws MetaException, NoSuchObjectException, InvalidObjectException {
        boolean success = false;
        try {
            this.openTransaction();
            final List<MPartition> mparts = this.listMPartitions(dbName, tblName, max);
            final List<Partition> parts = new ArrayList<Partition>(mparts.size());
            if (mparts != null && mparts.size() > 0) {
                for (final MPartition mpart : mparts) {
                    final MTable mtbl = mpart.getTable();
                    final Partition part = this.convertToPart(mpart);
                    parts.add(part);
                    if ("TRUE".equalsIgnoreCase(mtbl.getParameters().get("PARTITION_LEVEL_PRIVILEGE"))) {
                        final String partName = Warehouse.makePartName(this.convertToFieldSchemas(mtbl.getPartitionKeys()), part.getValues());
                        final PrincipalPrivilegeSet partAuth = this.getPartitionPrivilegeSet(dbName, tblName, partName, userName, groupNames);
                        part.setPrivileges(partAuth);
                    }
                }
            }
            success = this.commitTransaction();
            return parts;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public Partition getPartitionWithAuth(final String dbName, final String tblName, final List<String> partVals, final String user_name, final List<String> group_names) throws NoSuchObjectException, MetaException, InvalidObjectException {
        boolean success = false;
        try {
            this.openTransaction();
            final MPartition mpart = this.getMPartition(dbName, tblName, partVals);
            if (mpart == null) {
                this.commitTransaction();
                throw new NoSuchObjectException("partition values=" + partVals.toString());
            }
            Partition part = null;
            final MTable mtbl = mpart.getTable();
            part = this.convertToPart(mpart);
            if ("TRUE".equalsIgnoreCase(mtbl.getParameters().get("PARTITION_LEVEL_PRIVILEGE"))) {
                final String partName = Warehouse.makePartName(this.convertToFieldSchemas(mtbl.getPartitionKeys()), partVals);
                final PrincipalPrivilegeSet partAuth = this.getPartitionPrivilegeSet(dbName, tblName, partName, user_name, group_names);
                part.setPrivileges(partAuth);
            }
            success = this.commitTransaction();
            return part;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<Partition> convertToParts(final List<MPartition> mparts) throws MetaException {
        return this.convertToParts(mparts, null);
    }
    
    private List<Partition> convertToParts(final List<MPartition> src, List<Partition> dest) throws MetaException {
        if (src == null) {
            return dest;
        }
        if (dest == null) {
            dest = new ArrayList<Partition>(src.size());
        }
        for (final MPartition mp : src) {
            dest.add(this.convertToPart(mp));
            Deadline.checkTimeout();
        }
        return dest;
    }
    
    private List<Partition> convertToParts(final String dbName, final String tblName, final List<MPartition> mparts) throws MetaException {
        final List<Partition> parts = new ArrayList<Partition>(mparts.size());
        for (final MPartition mp : mparts) {
            parts.add(this.convertToPart(dbName, tblName, mp));
            Deadline.checkTimeout();
        }
        return parts;
    }
    
    @Override
    public List<String> listPartitionNames(final String dbName, final String tableName, final short max) throws MetaException {
        List<String> pns = null;
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing getPartitionNames");
            pns = this.getPartitionNamesNoTxn(dbName, tableName, max);
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return pns;
    }
    
    private List<String> getPartitionNamesNoTxn(String dbName, String tableName, final short max) {
        final List<String> pns = new ArrayList<String>();
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        final Query q = this.pm.newQuery("select partitionName from org.apache.hadoop.hive.metastore.model.MPartition where table.database.name == t1 && table.tableName == t2 order by partitionName asc");
        q.declareParameters("java.lang.String t1, java.lang.String t2");
        q.setResult("partitionName");
        if (max > 0) {
            q.setRange(0L, max);
        }
        final Collection names = (Collection)q.execute(dbName, tableName);
        final Iterator i = names.iterator();
        while (i.hasNext()) {
            pns.add(i.next());
        }
        return pns;
    }
    
    private Collection getPartitionPsQueryResults(String dbName, String tableName, final List<String> part_vals, final short max_parts, final String resultsCol) throws MetaException, NoSuchObjectException {
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        final Table table = this.getTable(dbName, tableName);
        if (table == null) {
            throw new NoSuchObjectException(dbName + "." + tableName + " table not found");
        }
        List<FieldSchema> partCols = table.getPartitionKeys();
        final int numPartKeys = partCols.size();
        if (part_vals.size() > numPartKeys) {
            throw new MetaException("Incorrect number of partition values");
        }
        partCols = partCols.subList(0, part_vals.size());
        String partNameMatcher = Warehouse.makePartName(partCols, part_vals, ".*");
        if (part_vals.size() < numPartKeys) {
            partNameMatcher += ".*";
        }
        final Query q = this.pm.newQuery(MPartition.class);
        final StringBuilder queryFilter = new StringBuilder("table.database.name == dbName");
        queryFilter.append(" && table.tableName == tableName");
        queryFilter.append(" && partitionName.matches(partialRegex)");
        q.setFilter(queryFilter.toString());
        q.declareParameters("java.lang.String dbName, java.lang.String tableName, java.lang.String partialRegex");
        if (max_parts >= 0) {
            q.setRange(0L, max_parts);
        }
        if (resultsCol != null && !resultsCol.isEmpty()) {
            q.setResult(resultsCol);
        }
        return (Collection)q.execute(dbName, tableName, partNameMatcher);
    }
    
    @Override
    public List<Partition> listPartitionsPsWithAuth(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts, final String userName, final List<String> groupNames) throws MetaException, InvalidObjectException, NoSuchObjectException {
        final List<Partition> partitions = new ArrayList<Partition>();
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("executing listPartitionNamesPsWithAuth");
            final Collection parts = this.getPartitionPsQueryResults(db_name, tbl_name, part_vals, max_parts, null);
            final MTable mtbl = this.getMTable(db_name, tbl_name);
            for (final Object o : parts) {
                final Partition part = this.convertToPart((MPartition)o);
                if (null != userName && null != groupNames && "TRUE".equalsIgnoreCase(mtbl.getParameters().get("PARTITION_LEVEL_PRIVILEGE"))) {
                    final String partName = Warehouse.makePartName(this.convertToFieldSchemas(mtbl.getPartitionKeys()), part.getValues());
                    final PrincipalPrivilegeSet partAuth = this.getPartitionPrivilegeSet(db_name, tbl_name, partName, userName, groupNames);
                    part.setPrivileges(partAuth);
                }
                partitions.add(part);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return partitions;
    }
    
    @Override
    public List<String> listPartitionNamesPs(final String dbName, final String tableName, final List<String> part_vals, final short max_parts) throws MetaException, NoSuchObjectException {
        final List<String> partitionNames = new ArrayList<String>();
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPartitionNamesPs");
            final Collection names = this.getPartitionPsQueryResults(dbName, tableName, part_vals, max_parts, "partitionName");
            for (final Object o : names) {
                partitionNames.add((String)o);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return partitionNames;
    }
    
    private List<MPartition> listMPartitions(String dbName, String tableName, final int max) {
        boolean success = false;
        List<MPartition> mparts = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listMPartitions");
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            tableName = HiveStringUtils.normalizeIdentifier(tableName);
            final Query query = this.pm.newQuery(MPartition.class, "table.tableName == t1 && table.database.name == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            query.setOrdering("partitionName ascending");
            if (max > 0) {
                query.setRange(0L, max);
            }
            mparts = (List<MPartition>)query.execute(tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listMPartitions");
            this.pm.retrieveAll(mparts);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMPartitions " + mparts);
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mparts;
    }
    
    @Override
    public List<Partition> getPartitionsByNames(final String dbName, final String tblName, final List<String> partNames) throws MetaException, NoSuchObjectException {
        return this.getPartitionsByNamesInternal(dbName, tblName, partNames, true, true);
    }
    
    protected List<Partition> getPartitionsByNamesInternal(final String dbName, final String tblName, final List<String> partNames, final boolean allowSql, final boolean allowJdo) throws MetaException, NoSuchObjectException {
        return ((GetHelper<List<Partition>>)new GetListHelper<Partition>(dbName, tblName, allowSql, allowJdo) {
            @Override
            protected List<Partition> getSqlResult(final GetHelper<List<Partition>> ctx) throws MetaException {
                return ObjectStore.this.directSql.getPartitionsViaSqlFilter(this.dbName, this.tblName, partNames);
            }
            
            @Override
            protected List<Partition> getJdoResult(final GetHelper<List<Partition>> ctx) throws MetaException, NoSuchObjectException {
                return ObjectStore.this.getPartitionsViaOrmFilter(this.dbName, this.tblName, partNames);
            }
        }).run(false);
    }
    
    @Override
    public boolean getPartitionsByExpr(final String dbName, final String tblName, final byte[] expr, final String defaultPartitionName, final short maxParts, final List<Partition> result) throws TException {
        return this.getPartitionsByExprInternal(dbName, tblName, expr, defaultPartitionName, maxParts, result, true, true);
    }
    
    protected boolean getPartitionsByExprInternal(final String dbName, final String tblName, final byte[] expr, final String defaultPartitionName, final short maxParts, final List<Partition> result, final boolean allowSql, final boolean allowJdo) throws TException {
        assert result != null;
        String filter = null;
        try {
            filter = this.expressionProxy.convertExprToFilter(expr);
        }
        catch (MetaException ex) {
            throw new IMetaStoreClient.IncompatibleMetastoreException(ex.getMessage());
        }
        final ExpressionTree exprTree = this.makeExpressionTree(filter);
        final AtomicBoolean hasUnknownPartitions = new AtomicBoolean(false);
        result.addAll(((GetHelper<Collection<? extends Partition>>)new GetListHelper<Partition>(dbName, tblName, allowSql, allowJdo) {
            @Override
            protected List<Partition> getSqlResult(final GetHelper<List<Partition>> ctx) throws MetaException {
                List<Partition> result = null;
                if (exprTree != null) {
                    result = ObjectStore.this.directSql.getPartitionsViaSqlFilter(ctx.getTable(), exprTree, null);
                }
                if (result == null) {
                    final List<String> partNames = new LinkedList<String>();
                    hasUnknownPartitions.set(ObjectStore.this.getPartitionNamesPrunedByExprNoTxn(ctx.getTable(), expr, defaultPartitionName, maxParts, partNames));
                    result = ObjectStore.this.directSql.getPartitionsViaSqlFilter(this.dbName, this.tblName, partNames);
                }
                return result;
            }
            
            @Override
            protected List<Partition> getJdoResult(final GetHelper<List<Partition>> ctx) throws MetaException, NoSuchObjectException {
                List<Partition> result = null;
                if (exprTree != null) {
                    result = ObjectStore.this.getPartitionsViaOrmFilter(ctx.getTable(), exprTree, maxParts, false);
                }
                if (result == null) {
                    final List<String> partNames = new ArrayList<String>();
                    hasUnknownPartitions.set(ObjectStore.this.getPartitionNamesPrunedByExprNoTxn(ctx.getTable(), expr, defaultPartitionName, maxParts, partNames));
                    result = ObjectStore.this.getPartitionsViaOrmFilter(this.dbName, this.tblName, partNames);
                }
                return result;
            }
        }).run(true));
        return hasUnknownPartitions.get();
    }
    
    private ExpressionTree makeExpressionTree(final String filter) throws MetaException {
        if (filter == null || filter.isEmpty()) {
            return ExpressionTree.EMPTY_TREE;
        }
        ObjectStore.LOG.debug("Filter specified is " + filter);
        ExpressionTree tree = null;
        try {
            tree = this.getFilterParser(filter).tree;
        }
        catch (MetaException ex) {
            ObjectStore.LOG.info("Unable to make the expression tree from expression string [" + filter + "]" + ex.getMessage());
        }
        if (tree == null) {
            return null;
        }
        final LikeChecker lc = new LikeChecker();
        tree.accept(lc);
        return lc.hasLike() ? null : tree;
    }
    
    private boolean getPartitionNamesPrunedByExprNoTxn(final Table table, final byte[] expr, String defaultPartName, final short maxParts, final List<String> result) throws MetaException {
        result.addAll(this.getPartitionNamesNoTxn(table.getDbName(), table.getTableName(), maxParts));
        final List<String> columnNames = new ArrayList<String>();
        final List<PrimitiveTypeInfo> typeInfos = new ArrayList<PrimitiveTypeInfo>();
        for (final FieldSchema fs : table.getPartitionKeys()) {
            columnNames.add(fs.getName());
            typeInfos.add(TypeInfoFactory.getPrimitiveTypeInfo(fs.getType()));
        }
        if (defaultPartName == null || defaultPartName.isEmpty()) {
            defaultPartName = HiveConf.getVar(this.getConf(), HiveConf.ConfVars.DEFAULTPARTITIONNAME);
        }
        return this.expressionProxy.filterPartitionsByExpr(columnNames, typeInfos, expr, defaultPartName, result);
    }
    
    private List<Partition> getPartitionsViaOrmFilter(final Table table, final ExpressionTree tree, final short maxParts, final boolean isValidatedFilter) throws MetaException {
        final Map<String, Object> params = new HashMap<String, Object>();
        final String jdoFilter = this.makeQueryFilterString(table.getDbName(), table, tree, params, isValidatedFilter);
        if (jdoFilter != null) {
            final Query query = this.pm.newQuery(MPartition.class, jdoFilter);
            if (maxParts >= 0) {
                query.setRange(0L, maxParts);
            }
            final String parameterDeclaration = this.makeParameterDeclarationStringObj(params);
            query.declareParameters(parameterDeclaration);
            query.setOrdering("partitionName ascending");
            final List<MPartition> mparts = (List<MPartition>)query.executeWithMap(params);
            ObjectStore.LOG.debug("Done executing query for getPartitionsViaOrmFilter");
            this.pm.retrieveAll(mparts);
            ObjectStore.LOG.debug("Done retrieving all objects for getPartitionsViaOrmFilter");
            final List<Partition> results = this.convertToParts(mparts);
            query.closeAll();
            return results;
        }
        assert !isValidatedFilter;
        return null;
    }
    
    private List<Partition> getPartitionsViaOrmFilter(final String dbName, final String tblName, final List<String> partNames) throws MetaException {
        if (partNames.isEmpty()) {
            return new ArrayList<Partition>();
        }
        final Out<Query> query = new Out<Query>();
        List<MPartition> mparts = null;
        try {
            mparts = this.getMPartitionsViaOrmFilter(dbName, tblName, partNames, query);
            return this.convertToParts(dbName, tblName, mparts);
        }
        finally {
            if (query.val != null) {
                query.val.closeAll();
            }
        }
    }
    
    private void dropPartitionsNoTxn(final String dbName, final String tblName, final List<String> partNames) {
        final ObjectPair<Query, Map<String, String>> queryWithParams = this.getPartQueryWithParams(dbName, tblName, partNames);
        final Query query = queryWithParams.getFirst();
        query.setClass(MPartition.class);
        final long deleted = query.deletePersistentAll(queryWithParams.getSecond());
        ObjectStore.LOG.debug("Deleted " + deleted + " partition from store");
        query.closeAll();
    }
    
    private HashSet<MColumnDescriptor> detachCdsFromSdsNoTxn(final String dbName, final String tblName, final List<String> partNames) {
        final ObjectPair<Query, Map<String, String>> queryWithParams = this.getPartQueryWithParams(dbName, tblName, partNames);
        final Query query = queryWithParams.getFirst();
        query.setClass(MPartition.class);
        query.setResult("sd");
        final List<MStorageDescriptor> sds = (List<MStorageDescriptor>)query.executeWithMap(queryWithParams.getSecond());
        final HashSet<MColumnDescriptor> candidateCds = new HashSet<MColumnDescriptor>();
        for (final MStorageDescriptor sd : sds) {
            if (sd != null && sd.getCD() != null) {
                candidateCds.add(sd.getCD());
                sd.setCD(null);
            }
        }
        return candidateCds;
    }
    
    private List<MPartition> getMPartitionsViaOrmFilter(final String dbName, final String tblName, final List<String> partNames, final Out<Query> out) {
        final ObjectPair<Query, Map<String, String>> queryWithParams = this.getPartQueryWithParams(dbName, tblName, partNames);
        final Query first = queryWithParams.getFirst();
        out.val = first;
        final Query query = first;
        query.setResultClass(MPartition.class);
        query.setClass(MPartition.class);
        query.setOrdering("partitionName ascending");
        final List<MPartition> result = (List<MPartition>)query.executeWithMap(queryWithParams.getSecond());
        return result;
    }
    
    private ObjectPair<Query, Map<String, String>> getPartQueryWithParams(final String dbName, final String tblName, final List<String> partNames) {
        final StringBuilder sb = new StringBuilder("table.tableName == t1 && table.database.name == t2 && (");
        int n = 0;
        final Map<String, String> params = new HashMap<String, String>();
        final Iterator<String> itr = partNames.iterator();
        while (itr.hasNext()) {
            final String pn = "p" + n;
            ++n;
            final String part = itr.next();
            params.put(pn, part);
            sb.append("partitionName == ").append(pn);
            sb.append(" || ");
        }
        sb.setLength(sb.length() - 4);
        sb.append(')');
        final Query query = this.pm.newQuery();
        query.setFilter(sb.toString());
        ObjectStore.LOG.debug(" JDOQL filter is " + sb.toString());
        params.put("t1", HiveStringUtils.normalizeIdentifier(tblName));
        params.put("t2", HiveStringUtils.normalizeIdentifier(dbName));
        query.declareParameters(this.makeParameterDeclarationString(params));
        return new ObjectPair<Query, Map<String, String>>(query, params);
    }
    
    @Override
    public List<Partition> getPartitionsByFilter(final String dbName, final String tblName, final String filter, final short maxParts) throws MetaException, NoSuchObjectException {
        return this.getPartitionsByFilterInternal(dbName, tblName, filter, maxParts, true, true);
    }
    
    protected List<Partition> getPartitionsByFilterInternal(final String dbName, final String tblName, final String filter, final short maxParts, final boolean allowSql, final boolean allowJdo) throws MetaException, NoSuchObjectException {
        final ExpressionTree tree = (filter != null && !filter.isEmpty()) ? this.getFilterParser(filter).tree : ExpressionTree.EMPTY_TREE;
        return ((GetHelper<List<Partition>>)new GetListHelper<Partition>(dbName, tblName, allowSql, allowJdo) {
            @Override
            protected List<Partition> getSqlResult(final GetHelper<List<Partition>> ctx) throws MetaException {
                final List<Partition> parts = ObjectStore.this.directSql.getPartitionsViaSqlFilter(ctx.getTable(), tree, (maxParts < 0) ? null : Integer.valueOf(maxParts));
                if (parts == null) {
                    ctx.disableDirectSql();
                }
                return parts;
            }
            
            @Override
            protected List<Partition> getJdoResult(final GetHelper<List<Partition>> ctx) throws MetaException, NoSuchObjectException {
                return ObjectStore.this.getPartitionsViaOrmFilter(ctx.getTable(), tree, maxParts, true);
            }
        }).run(true);
    }
    
    private MTable ensureGetMTable(final String dbName, final String tblName) throws NoSuchObjectException, MetaException {
        final MTable mtable = this.getMTable(dbName, tblName);
        if (mtable == null) {
            throw new NoSuchObjectException("Specified database/table does not exist : " + dbName + "." + tblName);
        }
        return mtable;
    }
    
    private Table ensureGetTable(final String dbName, final String tblName) throws NoSuchObjectException, MetaException {
        return this.convertToTable(this.ensureGetMTable(dbName, tblName));
    }
    
    private FilterParser getFilterParser(final String filter) throws MetaException {
        final FilterLexer lexer = new FilterLexer(new ExpressionTree.ANTLRNoCaseStringStream(filter));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final FilterParser parser = new FilterParser(tokens);
        try {
            parser.filter();
        }
        catch (RecognitionException re) {
            throw new MetaException("Error parsing partition filter; lexer error: " + lexer.errorMsg + "; exception " + re);
        }
        if (lexer.errorMsg != null) {
            throw new MetaException("Error parsing partition filter : " + lexer.errorMsg);
        }
        return parser;
    }
    
    private String makeQueryFilterString(final String dbName, final MTable mtable, final String filter, final Map<String, Object> params) throws MetaException {
        final ExpressionTree tree = (filter != null && !filter.isEmpty()) ? this.getFilterParser(filter).tree : ExpressionTree.EMPTY_TREE;
        return this.makeQueryFilterString(dbName, this.convertToTable(mtable), tree, params, true);
    }
    
    private String makeQueryFilterString(final String dbName, final Table table, final ExpressionTree tree, final Map<String, Object> params, final boolean isValidatedFilter) throws MetaException {
        assert tree != null;
        final ExpressionTree.FilterBuilder queryBuilder = new ExpressionTree.FilterBuilder(isValidatedFilter);
        if (table != null) {
            queryBuilder.append("table.tableName == t1 && table.database.name == t2");
            params.put("t1", table.getTableName());
            params.put("t2", table.getDbName());
        }
        else {
            queryBuilder.append("database.name == dbName");
            params.put("dbName", dbName);
        }
        tree.generateJDOFilterFragment(this.getConf(), table, params, queryBuilder);
        if (!queryBuilder.hasError()) {
            final String jdoFilter = queryBuilder.getFilter();
            ObjectStore.LOG.debug("jdoFilter = " + jdoFilter);
            return jdoFilter;
        }
        assert !isValidatedFilter;
        ObjectStore.LOG.info("JDO filter pushdown cannot be used: " + queryBuilder.getErrorMessage());
        return null;
    }
    
    private String makeParameterDeclarationString(final Map<String, String> params) {
        final StringBuilder paramDecl = new StringBuilder();
        for (final String key : params.keySet()) {
            paramDecl.append(", java.lang.String " + key);
        }
        return paramDecl.toString();
    }
    
    private String makeParameterDeclarationStringObj(final Map<String, Object> params) {
        final StringBuilder paramDecl = new StringBuilder();
        for (final Map.Entry<String, Object> entry : params.entrySet()) {
            paramDecl.append(", ");
            paramDecl.append(entry.getValue().getClass().getName());
            paramDecl.append(" ");
            paramDecl.append(entry.getKey());
        }
        return paramDecl.toString();
    }
    
    @Override
    public List<String> listTableNamesByFilter(String dbName, final String filter, final short maxTables) throws MetaException {
        boolean success = false;
        List<String> tableNames = new ArrayList<String>();
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listTableNamesByFilter");
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            final Map<String, Object> params = new HashMap<String, Object>();
            final String queryFilterString = this.makeQueryFilterString(dbName, null, filter, params);
            final Query query = this.pm.newQuery(MTable.class);
            query.declareImports("import java.lang.String");
            query.setResult("tableName");
            query.setResultClass(String.class);
            if (maxTables >= 0) {
                query.setRange(0L, maxTables);
            }
            ObjectStore.LOG.debug("filter specified is " + filter + "," + " JDOQL filter is " + queryFilterString);
            for (final Map.Entry<String, Object> entry : params.entrySet()) {
                ObjectStore.LOG.debug("key: " + entry.getKey() + " value: " + entry.getValue() + " class: " + entry.getValue().getClass().getName());
            }
            final String parameterDeclaration = this.makeParameterDeclarationStringObj(params);
            query.declareParameters(parameterDeclaration);
            query.setFilter(queryFilterString);
            final Collection names = (Collection)query.executeWithMap(params);
            final Set<String> tableNamesSet = new HashSet<String>();
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                tableNamesSet.add(i.next());
            }
            tableNames = new ArrayList<String>(tableNamesSet);
            ObjectStore.LOG.debug("Done executing query for listTableNamesByFilter");
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listTableNamesByFilter");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return tableNames;
    }
    
    @Override
    public List<String> listPartitionNamesByFilter(String dbName, String tableName, final String filter, final short maxParts) throws MetaException {
        boolean success = false;
        List<String> partNames = new ArrayList<String>();
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listMPartitionNamesByFilter");
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            tableName = HiveStringUtils.normalizeIdentifier(tableName);
            final MTable mtable = this.getMTable(dbName, tableName);
            if (mtable == null) {
                return partNames;
            }
            final Map<String, Object> params = new HashMap<String, Object>();
            final String queryFilterString = this.makeQueryFilterString(dbName, mtable, filter, params);
            final Query query = this.pm.newQuery("select partitionName from org.apache.hadoop.hive.metastore.model.MPartition where " + queryFilterString);
            if (maxParts >= 0) {
                query.setRange(0L, maxParts);
            }
            ObjectStore.LOG.debug("Filter specified is " + filter + "," + " JDOQL filter is " + queryFilterString);
            ObjectStore.LOG.debug("Parms is " + params);
            final String parameterDeclaration = this.makeParameterDeclarationStringObj(params);
            query.declareParameters(parameterDeclaration);
            query.setOrdering("partitionName ascending");
            query.setResult("partitionName");
            final Collection names = (Collection)query.executeWithMap(params);
            partNames = new ArrayList<String>();
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                partNames.add(i.next());
            }
            ObjectStore.LOG.debug("Done executing query for listMPartitionNamesByFilter");
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMPartitionNamesByFilter");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return partNames;
    }
    
    @Override
    public void alterTable(String dbname, String name, final Table newTable) throws InvalidObjectException, MetaException {
        boolean success = false;
        try {
            this.openTransaction();
            name = HiveStringUtils.normalizeIdentifier(name);
            dbname = HiveStringUtils.normalizeIdentifier(dbname);
            final MTable newt = this.convertToMTable(newTable);
            if (newt == null) {
                throw new InvalidObjectException("new table is invalid");
            }
            final MTable oldt = this.getMTable(dbname, name);
            if (oldt == null) {
                throw new MetaException("table " + dbname + "." + name + " doesn't exist");
            }
            oldt.setDatabase(newt.getDatabase());
            oldt.setTableName(HiveStringUtils.normalizeIdentifier(newt.getTableName()));
            oldt.setParameters(newt.getParameters());
            oldt.setOwner(newt.getOwner());
            this.copyMSD(newt.getSd(), oldt.getSd());
            oldt.setRetention(newt.getRetention());
            oldt.setPartitionKeys(newt.getPartitionKeys());
            oldt.setTableType(newt.getTableType());
            oldt.setLastAccessTime(newt.getLastAccessTime());
            oldt.setViewOriginalText(newt.getViewOriginalText());
            oldt.setViewExpandedText(newt.getViewExpandedText());
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public void alterIndex(String dbname, String baseTblName, String name, final Index newIndex) throws InvalidObjectException, MetaException {
        boolean success = false;
        try {
            this.openTransaction();
            name = HiveStringUtils.normalizeIdentifier(name);
            baseTblName = HiveStringUtils.normalizeIdentifier(baseTblName);
            dbname = HiveStringUtils.normalizeIdentifier(dbname);
            final MIndex newi = this.convertToMIndex(newIndex);
            if (newi == null) {
                throw new InvalidObjectException("new index is invalid");
            }
            final MIndex oldi = this.getMIndex(dbname, baseTblName, name);
            if (oldi == null) {
                throw new MetaException("index " + name + " doesn't exist");
            }
            oldi.setParameters(newi.getParameters());
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private void alterPartitionNoTxn(String dbname, String name, final List<String> part_vals, final Partition newPart) throws InvalidObjectException, MetaException {
        name = HiveStringUtils.normalizeIdentifier(name);
        dbname = HiveStringUtils.normalizeIdentifier(dbname);
        final MPartition oldp = this.getMPartition(dbname, name, part_vals);
        final MPartition newp = this.convertToMPart(newPart, false);
        if (oldp == null || newp == null) {
            throw new InvalidObjectException("partition does not exist.");
        }
        oldp.setValues(newp.getValues());
        oldp.setPartitionName(newp.getPartitionName());
        oldp.setParameters(newPart.getParameters());
        if (!TableType.VIRTUAL_VIEW.name().equals(oldp.getTable().getTableType())) {
            this.copyMSD(newp.getSd(), oldp.getSd());
        }
        if (newp.getCreateTime() != oldp.getCreateTime()) {
            oldp.setCreateTime(newp.getCreateTime());
        }
        if (newp.getLastAccessTime() != oldp.getLastAccessTime()) {
            oldp.setLastAccessTime(newp.getLastAccessTime());
        }
    }
    
    @Override
    public void alterPartition(final String dbname, final String name, final List<String> part_vals, final Partition newPart) throws InvalidObjectException, MetaException {
        boolean success = false;
        Exception e = null;
        try {
            this.openTransaction();
            this.alterPartitionNoTxn(dbname, name, part_vals, newPart);
            success = this.commitTransaction();
        }
        catch (Exception exception) {
            e = exception;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
                final MetaException metaException = new MetaException("The transaction for alter partition did not commit successfully.");
                if (e != null) {
                    metaException.initCause(e);
                }
                throw metaException;
            }
        }
    }
    
    @Override
    public void alterPartitions(final String dbname, final String name, final List<List<String>> part_vals, final List<Partition> newParts) throws InvalidObjectException, MetaException {
        boolean success = false;
        Exception e = null;
        try {
            this.openTransaction();
            final Iterator<List<String>> part_val_itr = part_vals.iterator();
            for (final Partition tmpPart : newParts) {
                final List<String> tmpPartVals = part_val_itr.next();
                this.alterPartitionNoTxn(dbname, name, tmpPartVals, tmpPart);
            }
            success = this.commitTransaction();
        }
        catch (Exception exception) {
            e = exception;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
                final MetaException metaException = new MetaException("The transaction for alter partition did not commit successfully.");
                if (e != null) {
                    metaException.initCause(e);
                }
                throw metaException;
            }
        }
    }
    
    private void copyMSD(final MStorageDescriptor newSd, final MStorageDescriptor oldSd) {
        oldSd.setLocation(newSd.getLocation());
        final MColumnDescriptor oldCD = oldSd.getCD();
        if (oldSd == null || oldSd.getCD() == null || oldSd.getCD().getCols() == null || newSd == null || newSd.getCD() == null || newSd.getCD().getCols() == null || !this.convertToFieldSchemas(newSd.getCD().getCols()).equals(this.convertToFieldSchemas(oldSd.getCD().getCols()))) {
            oldSd.setCD(newSd.getCD());
        }
        this.removeUnusedColumnDescriptor(oldCD);
        oldSd.setBucketCols(newSd.getBucketCols());
        oldSd.setCompressed(newSd.isCompressed());
        oldSd.setInputFormat(newSd.getInputFormat());
        oldSd.setOutputFormat(newSd.getOutputFormat());
        oldSd.setNumBuckets(newSd.getNumBuckets());
        oldSd.getSerDeInfo().setName(newSd.getSerDeInfo().getName());
        oldSd.getSerDeInfo().setSerializationLib(newSd.getSerDeInfo().getSerializationLib());
        oldSd.getSerDeInfo().setParameters(newSd.getSerDeInfo().getParameters());
        oldSd.setSkewedColNames(newSd.getSkewedColNames());
        oldSd.setSkewedColValues(newSd.getSkewedColValues());
        oldSd.setSkewedColValueLocationMaps(newSd.getSkewedColValueLocationMaps());
        oldSd.setSortCols(newSd.getSortCols());
        oldSd.setParameters(newSd.getParameters());
        oldSd.setStoredAsSubDirectories(newSd.isStoredAsSubDirectories());
    }
    
    private void removeUnusedColumnDescriptor(final MColumnDescriptor oldCD) {
        if (oldCD == null) {
            return;
        }
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("execute removeUnusedColumnDescriptor");
            final List<MStorageDescriptor> referencedSDs = this.listStorageDescriptorsWithCD(oldCD, 1L);
            if (referencedSDs != null && referencedSDs.isEmpty()) {
                this.pm.retrieve(oldCD);
                this.pm.deletePersistent(oldCD);
            }
            success = this.commitTransaction();
            ObjectStore.LOG.debug("successfully deleted a CD in removeUnusedColumnDescriptor");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private void preDropStorageDescriptor(final MStorageDescriptor msd) {
        if (msd == null || msd.getCD() == null) {
            return;
        }
        final MColumnDescriptor mcd = msd.getCD();
        msd.setCD(null);
        this.removeUnusedColumnDescriptor(mcd);
    }
    
    private List<MStorageDescriptor> listStorageDescriptorsWithCD(final MColumnDescriptor oldCD, final long maxSDs) {
        boolean success = false;
        List<MStorageDescriptor> sds = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listStorageDescriptorsWithCD");
            final Query query = this.pm.newQuery(MStorageDescriptor.class, "this.cd == inCD");
            query.declareParameters("MColumnDescriptor inCD");
            if (maxSDs >= 0L) {
                query.setRange(0L, maxSDs);
            }
            sds = (List<MStorageDescriptor>)query.execute(oldCD);
            ObjectStore.LOG.debug("Done executing query for listStorageDescriptorsWithCD");
            this.pm.retrieveAll(sds);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listStorageDescriptorsWithCD");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return sds;
    }
    
    @Override
    public boolean addIndex(final Index index) throws InvalidObjectException, MetaException {
        boolean commited = false;
        try {
            this.openTransaction();
            final MIndex idx = this.convertToMIndex(index);
            this.pm.makePersistent(idx);
            commited = this.commitTransaction();
            return true;
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
                return false;
            }
        }
    }
    
    private MIndex convertToMIndex(final Index index) throws InvalidObjectException, MetaException {
        final StorageDescriptor sd = index.getSd();
        if (sd == null) {
            throw new InvalidObjectException("Storage descriptor is not defined for index.");
        }
        final MStorageDescriptor msd = this.convertToMStorageDescriptor(sd);
        final MTable origTable = this.getMTable(index.getDbName(), index.getOrigTableName());
        if (origTable == null) {
            throw new InvalidObjectException("Original table does not exist for the given index.");
        }
        final String[] qualified = MetaStoreUtils.getQualifiedName(index.getDbName(), index.getIndexTableName());
        final MTable indexTable = this.getMTable(qualified[0], qualified[1]);
        if (indexTable == null) {
            throw new InvalidObjectException("Underlying index table does not exist for the given index.");
        }
        return new MIndex(HiveStringUtils.normalizeIdentifier(index.getIndexName()), origTable, index.getCreateTime(), index.getLastAccessTime(), index.getParameters(), indexTable, msd, index.getIndexHandlerClass(), index.isDeferredRebuild());
    }
    
    @Override
    public boolean dropIndex(final String dbName, final String origTableName, final String indexName) throws MetaException {
        boolean success = false;
        try {
            this.openTransaction();
            final MIndex index = this.getMIndex(dbName, origTableName, indexName);
            if (index != null) {
                this.pm.deletePersistent(index);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    private MIndex getMIndex(String dbName, String originalTblName, final String indexName) throws MetaException {
        MIndex midx = null;
        boolean commited = false;
        try {
            this.openTransaction();
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            originalTblName = HiveStringUtils.normalizeIdentifier(originalTblName);
            final MTable mtbl = this.getMTable(dbName, originalTblName);
            if (mtbl == null) {
                commited = this.commitTransaction();
                return null;
            }
            final Query query = this.pm.newQuery(MIndex.class, "origTable.tableName == t1 && origTable.database.name == t2 && indexName == t3");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3");
            query.setUnique(true);
            midx = (MIndex)query.execute(originalTblName, dbName, HiveStringUtils.normalizeIdentifier(indexName));
            this.pm.retrieve(midx);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return midx;
    }
    
    @Override
    public Index getIndex(final String dbName, final String origTableName, final String indexName) throws MetaException {
        this.openTransaction();
        final MIndex mIndex = this.getMIndex(dbName, origTableName, indexName);
        final Index ret = this.convertToIndex(mIndex);
        this.commitTransaction();
        return ret;
    }
    
    private Index convertToIndex(final MIndex mIndex) throws MetaException {
        if (mIndex == null) {
            return null;
        }
        final MTable origTable = mIndex.getOrigTable();
        final MTable indexTable = mIndex.getIndexTable();
        return new Index(mIndex.getIndexName(), mIndex.getIndexHandlerClass(), origTable.getDatabase().getName(), origTable.getTableName(), mIndex.getCreateTime(), mIndex.getLastAccessTime(), indexTable.getTableName(), this.convertToStorageDescriptor(mIndex.getSd()), mIndex.getParameters(), mIndex.getDeferredRebuild());
    }
    
    @Override
    public List<Index> getIndexes(final String dbName, final String origTableName, final int max) throws MetaException {
        boolean success = false;
        try {
            this.openTransaction();
            final List<MIndex> mIndexList = this.listMIndexes(dbName, origTableName, max);
            final List<Index> indexes = new ArrayList<Index>(mIndexList.size());
            for (final MIndex midx : mIndexList) {
                indexes.add(this.convertToIndex(midx));
            }
            success = this.commitTransaction();
            return indexes;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<MIndex> listMIndexes(String dbName, String origTableName, final int max) {
        boolean success = false;
        List<MIndex> mindexes = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listMIndexes");
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            origTableName = HiveStringUtils.normalizeIdentifier(origTableName);
            final Query query = this.pm.newQuery(MIndex.class, "origTable.tableName == t1 && origTable.database.name == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mindexes = (List<MIndex>)query.execute(origTableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listMIndexes");
            this.pm.retrieveAll(mindexes);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMIndexes");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mindexes;
    }
    
    @Override
    public List<String> listIndexNames(String dbName, String origTableName, final short max) throws MetaException {
        final List<String> pns = new ArrayList<String>();
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listIndexNames");
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            origTableName = HiveStringUtils.normalizeIdentifier(origTableName);
            final Query q = this.pm.newQuery("select indexName from org.apache.hadoop.hive.metastore.model.MIndex where origTable.database.name == t1 && origTable.tableName == t2 order by indexName asc");
            q.declareParameters("java.lang.String t1, java.lang.String t2");
            q.setResult("indexName");
            final Collection names = (Collection)q.execute(dbName, origTableName);
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                pns.add(i.next());
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return pns;
    }
    
    @Override
    public boolean addRole(final String roleName, final String ownerName) throws InvalidObjectException, MetaException, NoSuchObjectException {
        boolean success = false;
        boolean commited = false;
        try {
            this.openTransaction();
            final MRole nameCheck = this.getMRole(roleName);
            if (nameCheck != null) {
                throw new InvalidObjectException("Role " + roleName + " already exists.");
            }
            final int now = (int)(System.currentTimeMillis() / 1000L);
            final MRole mRole = new MRole(roleName, now, ownerName);
            this.pm.makePersistent(mRole);
            commited = this.commitTransaction();
            success = true;
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    @Override
    public boolean grantRole(final Role role, final String userName, final PrincipalType principalType, final String grantor, final PrincipalType grantorType, final boolean grantOption) throws MetaException, NoSuchObjectException, InvalidObjectException {
        boolean success = false;
        boolean commited = false;
        try {
            this.openTransaction();
            MRoleMap roleMap = null;
            try {
                roleMap = this.getMSecurityUserRoleMap(userName, principalType, role.getRoleName());
            }
            catch (Exception ex) {}
            if (roleMap != null) {
                throw new InvalidObjectException("Principal " + userName + " already has the role " + role.getRoleName());
            }
            if (principalType == PrincipalType.ROLE) {
                this.validateRole(userName);
            }
            final MRole mRole = this.getMRole(role.getRoleName());
            final long now = System.currentTimeMillis() / 1000L;
            final MRoleMap roleMember = new MRoleMap(userName, principalType.toString(), mRole, (int)now, grantor, grantorType.toString(), grantOption);
            this.pm.makePersistent(roleMember);
            commited = this.commitTransaction();
            success = true;
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    private void validateRole(final String roleName) throws NoSuchObjectException {
        final MRole granteeRole = this.getMRole(roleName);
        if (granteeRole == null) {
            throw new NoSuchObjectException("Role " + roleName + " does not exist");
        }
    }
    
    @Override
    public boolean revokeRole(final Role role, final String userName, final PrincipalType principalType, final boolean grantOption) throws MetaException, NoSuchObjectException {
        boolean success = false;
        try {
            this.openTransaction();
            final MRoleMap roleMember = this.getMSecurityUserRoleMap(userName, principalType, role.getRoleName());
            if (grantOption) {
                if (!roleMember.getGrantOption()) {
                    throw new MetaException("User " + userName + " does not have grant option with role " + role.getRoleName());
                }
                roleMember.setGrantOption(false);
            }
            else {
                this.pm.deletePersistent(roleMember);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    private MRoleMap getMSecurityUserRoleMap(final String userName, final PrincipalType principalType, final String roleName) {
        MRoleMap mRoleMember = null;
        boolean commited = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MRoleMap.class, "principalName == t1 && principalType == t2 && role.roleName == t3");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3");
            query.setUnique(true);
            mRoleMember = (MRoleMap)query.executeWithArray(userName, principalType.toString(), roleName);
            this.pm.retrieve(mRoleMember);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return mRoleMember;
    }
    
    @Override
    public boolean removeRole(final String roleName) throws MetaException, NoSuchObjectException {
        boolean success = false;
        try {
            this.openTransaction();
            final MRole mRol = this.getMRole(roleName);
            this.pm.retrieve(mRol);
            if (mRol != null) {
                final List<MRoleMap> roleMap = this.listRoleMembers(mRol.getRoleName());
                if (roleMap.size() > 0) {
                    this.pm.deletePersistentAll(roleMap);
                }
                final List<MRoleMap> roleMember = this.listMSecurityPrincipalMembershipRole(mRol.getRoleName(), PrincipalType.ROLE);
                if (roleMember.size() > 0) {
                    this.pm.deletePersistentAll(roleMember);
                }
                final List<MGlobalPrivilege> userGrants = this.listPrincipalGlobalGrants(mRol.getRoleName(), PrincipalType.ROLE);
                if (userGrants.size() > 0) {
                    this.pm.deletePersistentAll(userGrants);
                }
                final List<MDBPrivilege> dbGrants = this.listPrincipalAllDBGrant(mRol.getRoleName(), PrincipalType.ROLE);
                if (dbGrants.size() > 0) {
                    this.pm.deletePersistentAll(dbGrants);
                }
                final List<MTablePrivilege> tabPartGrants = this.listPrincipalAllTableGrants(mRol.getRoleName(), PrincipalType.ROLE);
                if (tabPartGrants.size() > 0) {
                    this.pm.deletePersistentAll(tabPartGrants);
                }
                final List<MPartitionPrivilege> partGrants = this.listPrincipalAllPartitionGrants(mRol.getRoleName(), PrincipalType.ROLE);
                if (partGrants.size() > 0) {
                    this.pm.deletePersistentAll(partGrants);
                }
                final List<MTableColumnPrivilege> tblColumnGrants = this.listPrincipalAllTableColumnGrants(mRol.getRoleName(), PrincipalType.ROLE);
                if (tblColumnGrants.size() > 0) {
                    this.pm.deletePersistentAll(tblColumnGrants);
                }
                final List<MPartitionColumnPrivilege> partColumnGrants = this.listPrincipalAllPartitionColumnGrants(mRol.getRoleName(), PrincipalType.ROLE);
                if (partColumnGrants.size() > 0) {
                    this.pm.deletePersistentAll(partColumnGrants);
                }
                this.pm.deletePersistent(mRol);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return success;
    }
    
    private Set<String> listAllRolesInHierarchy(final String userName, final List<String> groupNames) {
        final List<MRoleMap> ret = new ArrayList<MRoleMap>();
        if (userName != null) {
            ret.addAll(this.listRoles(userName, PrincipalType.USER));
        }
        if (groupNames != null) {
            for (final String groupName : groupNames) {
                ret.addAll(this.listRoles(groupName, PrincipalType.GROUP));
            }
        }
        final Set<String> roleNames = new HashSet<String>();
        this.getAllRoleAncestors(roleNames, ret);
        return roleNames;
    }
    
    private void getAllRoleAncestors(final Set<String> processedRoleNames, final List<MRoleMap> parentRoles) {
        for (final MRoleMap parentRole : parentRoles) {
            final String parentRoleName = parentRole.getRole().getRoleName();
            if (!processedRoleNames.contains(parentRoleName)) {
                final List<MRoleMap> nextParentRoles = this.listRoles(parentRoleName, PrincipalType.ROLE);
                processedRoleNames.add(parentRoleName);
                this.getAllRoleAncestors(processedRoleNames, nextParentRoles);
            }
        }
    }
    
    @Override
    public List<MRoleMap> listRoles(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        List<MRoleMap> mRoleMember = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listRoles");
            final Query query = this.pm.newQuery(MRoleMap.class, "principalName == t1 && principalType == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            query.setUnique(false);
            mRoleMember = (List<MRoleMap>)query.executeWithArray(principalName, principalType.toString());
            ObjectStore.LOG.debug("Done executing query for listMSecurityUserRoleMap");
            this.pm.retrieveAll(mRoleMember);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMSecurityUserRoleMap");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        if (principalType == PrincipalType.USER) {
            if (mRoleMember == null) {
                mRoleMember = new ArrayList<MRoleMap>();
            }
            else {
                mRoleMember = new ArrayList<MRoleMap>(mRoleMember);
            }
            final MRole publicRole = new MRole("public", 0, "public");
            mRoleMember.add(new MRoleMap(principalName, principalType.toString(), publicRole, 0, null, null, false));
        }
        return mRoleMember;
    }
    
    private List<MRoleMap> listMSecurityPrincipalMembershipRole(final String roleName, final PrincipalType principalType) {
        boolean success = false;
        List<MRoleMap> mRoleMemebership = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listMSecurityPrincipalMembershipRole");
            final Query query = this.pm.newQuery(MRoleMap.class, "principalName == t1 && principalType == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mRoleMemebership = (List<MRoleMap>)query.execute(roleName, principalType.toString());
            ObjectStore.LOG.debug("Done executing query for listMSecurityPrincipalMembershipRole");
            this.pm.retrieveAll(mRoleMemebership);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMSecurityPrincipalMembershipRole");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mRoleMemebership;
    }
    
    @Override
    public Role getRole(final String roleName) throws NoSuchObjectException {
        final MRole mRole = this.getMRole(roleName);
        if (mRole == null) {
            throw new NoSuchObjectException(roleName + " role can not be found.");
        }
        final Role ret = new Role(mRole.getRoleName(), mRole.getCreateTime(), mRole.getOwnerName());
        return ret;
    }
    
    private MRole getMRole(final String roleName) {
        MRole mrole = null;
        boolean commited = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MRole.class, "roleName == t1");
            query.declareParameters("java.lang.String t1");
            query.setUnique(true);
            mrole = (MRole)query.execute(roleName);
            this.pm.retrieve(mrole);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return mrole;
    }
    
    @Override
    public List<String> listRoleNames() {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listAllRoleNames");
            final Query query = this.pm.newQuery("select roleName from org.apache.hadoop.hive.metastore.model.MRole");
            query.setResult("roleName");
            final Collection names = (Collection)query.execute();
            final List<String> roleNames = new ArrayList<String>();
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                roleNames.add(i.next());
            }
            success = this.commitTransaction();
            return roleNames;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public PrincipalPrivilegeSet getUserPrivilegeSet(final String userName, final List<String> groupNames) throws InvalidObjectException, MetaException {
        boolean commited = false;
        final PrincipalPrivilegeSet ret = new PrincipalPrivilegeSet();
        try {
            this.openTransaction();
            if (userName != null) {
                final List<MGlobalPrivilege> user = this.listPrincipalGlobalGrants(userName, PrincipalType.USER);
                if (user.size() > 0) {
                    final Map<String, List<PrivilegeGrantInfo>> userPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                    final List<PrivilegeGrantInfo> grantInfos = new ArrayList<PrivilegeGrantInfo>(user.size());
                    for (int i = 0; i < user.size(); ++i) {
                        final MGlobalPrivilege item = user.get(i);
                        grantInfos.add(new PrivilegeGrantInfo(item.getPrivilege(), item.getCreateTime(), item.getGrantor(), this.getPrincipalTypeFromStr(item.getGrantorType()), item.getGrantOption()));
                    }
                    userPriv.put(userName, grantInfos);
                    ret.setUserPrivileges(userPriv);
                }
            }
            if (groupNames != null && groupNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> groupPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String groupName : groupNames) {
                    final List<MGlobalPrivilege> group = this.listPrincipalGlobalGrants(groupName, PrincipalType.GROUP);
                    if (group.size() > 0) {
                        final List<PrivilegeGrantInfo> grantInfos2 = new ArrayList<PrivilegeGrantInfo>(group.size());
                        for (int j = 0; j < group.size(); ++j) {
                            final MGlobalPrivilege item2 = group.get(j);
                            grantInfos2.add(new PrivilegeGrantInfo(item2.getPrivilege(), item2.getCreateTime(), item2.getGrantor(), this.getPrincipalTypeFromStr(item2.getGrantorType()), item2.getGrantOption()));
                        }
                        groupPriv.put(groupName, grantInfos2);
                    }
                }
                ret.setGroupPrivileges(groupPriv);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    public List<PrivilegeGrantInfo> getDBPrivilege(String dbName, final String principalName, final PrincipalType principalType) throws InvalidObjectException, MetaException {
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        if (principalName != null) {
            final List<MDBPrivilege> userNameDbPriv = this.listPrincipalDBGrants(principalName, principalType, dbName);
            if (userNameDbPriv != null && userNameDbPriv.size() > 0) {
                final List<PrivilegeGrantInfo> grantInfos = new ArrayList<PrivilegeGrantInfo>(userNameDbPriv.size());
                for (int i = 0; i < userNameDbPriv.size(); ++i) {
                    final MDBPrivilege item = userNameDbPriv.get(i);
                    grantInfos.add(new PrivilegeGrantInfo(item.getPrivilege(), item.getCreateTime(), item.getGrantor(), this.getPrincipalTypeFromStr(item.getGrantorType()), item.getGrantOption()));
                }
                return grantInfos;
            }
        }
        return new ArrayList<PrivilegeGrantInfo>(0);
    }
    
    @Override
    public PrincipalPrivilegeSet getDBPrivilegeSet(String dbName, final String userName, final List<String> groupNames) throws InvalidObjectException, MetaException {
        boolean commited = false;
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        final PrincipalPrivilegeSet ret = new PrincipalPrivilegeSet();
        try {
            this.openTransaction();
            if (userName != null) {
                final Map<String, List<PrivilegeGrantInfo>> dbUserPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                dbUserPriv.put(userName, this.getDBPrivilege(dbName, userName, PrincipalType.USER));
                ret.setUserPrivileges(dbUserPriv);
            }
            if (groupNames != null && groupNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> dbGroupPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String groupName : groupNames) {
                    dbGroupPriv.put(groupName, this.getDBPrivilege(dbName, groupName, PrincipalType.GROUP));
                }
                ret.setGroupPrivileges(dbGroupPriv);
            }
            final Set<String> roleNames = this.listAllRolesInHierarchy(userName, groupNames);
            if (roleNames != null && roleNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> dbRolePriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String roleName : roleNames) {
                    dbRolePriv.put(roleName, this.getDBPrivilege(dbName, roleName, PrincipalType.ROLE));
                }
                ret.setRolePrivileges(dbRolePriv);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    @Override
    public PrincipalPrivilegeSet getPartitionPrivilegeSet(String dbName, String tableName, final String partition, final String userName, final List<String> groupNames) throws InvalidObjectException, MetaException {
        boolean commited = false;
        final PrincipalPrivilegeSet ret = new PrincipalPrivilegeSet();
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        try {
            this.openTransaction();
            if (userName != null) {
                final Map<String, List<PrivilegeGrantInfo>> partUserPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                partUserPriv.put(userName, this.getPartitionPrivilege(dbName, tableName, partition, userName, PrincipalType.USER));
                ret.setUserPrivileges(partUserPriv);
            }
            if (groupNames != null && groupNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> partGroupPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String groupName : groupNames) {
                    partGroupPriv.put(groupName, this.getPartitionPrivilege(dbName, tableName, partition, groupName, PrincipalType.GROUP));
                }
                ret.setGroupPrivileges(partGroupPriv);
            }
            final Set<String> roleNames = this.listAllRolesInHierarchy(userName, groupNames);
            if (roleNames != null && roleNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> partRolePriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String roleName : roleNames) {
                    partRolePriv.put(roleName, this.getPartitionPrivilege(dbName, tableName, partition, roleName, PrincipalType.ROLE));
                }
                ret.setRolePrivileges(partRolePriv);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    @Override
    public PrincipalPrivilegeSet getTablePrivilegeSet(String dbName, String tableName, final String userName, final List<String> groupNames) throws InvalidObjectException, MetaException {
        boolean commited = false;
        final PrincipalPrivilegeSet ret = new PrincipalPrivilegeSet();
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        try {
            this.openTransaction();
            if (userName != null) {
                final Map<String, List<PrivilegeGrantInfo>> tableUserPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                tableUserPriv.put(userName, this.getTablePrivilege(dbName, tableName, userName, PrincipalType.USER));
                ret.setUserPrivileges(tableUserPriv);
            }
            if (groupNames != null && groupNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> tableGroupPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String groupName : groupNames) {
                    tableGroupPriv.put(groupName, this.getTablePrivilege(dbName, tableName, groupName, PrincipalType.GROUP));
                }
                ret.setGroupPrivileges(tableGroupPriv);
            }
            final Set<String> roleNames = this.listAllRolesInHierarchy(userName, groupNames);
            if (roleNames != null && roleNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> tableRolePriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String roleName : roleNames) {
                    tableRolePriv.put(roleName, this.getTablePrivilege(dbName, tableName, roleName, PrincipalType.ROLE));
                }
                ret.setRolePrivileges(tableRolePriv);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    @Override
    public PrincipalPrivilegeSet getColumnPrivilegeSet(String dbName, String tableName, final String partitionName, String columnName, final String userName, final List<String> groupNames) throws InvalidObjectException, MetaException {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        columnName = HiveStringUtils.normalizeIdentifier(columnName);
        boolean commited = false;
        final PrincipalPrivilegeSet ret = new PrincipalPrivilegeSet();
        try {
            this.openTransaction();
            if (userName != null) {
                final Map<String, List<PrivilegeGrantInfo>> columnUserPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                columnUserPriv.put(userName, this.getColumnPrivilege(dbName, tableName, columnName, partitionName, userName, PrincipalType.USER));
                ret.setUserPrivileges(columnUserPriv);
            }
            if (groupNames != null && groupNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> columnGroupPriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String groupName : groupNames) {
                    columnGroupPriv.put(groupName, this.getColumnPrivilege(dbName, tableName, columnName, partitionName, groupName, PrincipalType.GROUP));
                }
                ret.setGroupPrivileges(columnGroupPriv);
            }
            final Set<String> roleNames = this.listAllRolesInHierarchy(userName, groupNames);
            if (roleNames != null && roleNames.size() > 0) {
                final Map<String, List<PrivilegeGrantInfo>> columnRolePriv = new HashMap<String, List<PrivilegeGrantInfo>>();
                for (final String roleName : roleNames) {
                    columnRolePriv.put(roleName, this.getColumnPrivilege(dbName, tableName, columnName, partitionName, roleName, PrincipalType.ROLE));
                }
                ret.setRolePrivileges(columnRolePriv);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    private List<PrivilegeGrantInfo> getPartitionPrivilege(String dbName, String tableName, final String partName, final String principalName, final PrincipalType principalType) {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        if (principalName != null) {
            final List<MPartitionPrivilege> userNameTabPartPriv = this.listPrincipalPartitionGrants(principalName, principalType, dbName, tableName, partName);
            if (userNameTabPartPriv != null && userNameTabPartPriv.size() > 0) {
                final List<PrivilegeGrantInfo> grantInfos = new ArrayList<PrivilegeGrantInfo>(userNameTabPartPriv.size());
                for (int i = 0; i < userNameTabPartPriv.size(); ++i) {
                    final MPartitionPrivilege item = userNameTabPartPriv.get(i);
                    grantInfos.add(new PrivilegeGrantInfo(item.getPrivilege(), item.getCreateTime(), item.getGrantor(), this.getPrincipalTypeFromStr(item.getGrantorType()), item.getGrantOption()));
                }
                return grantInfos;
            }
        }
        return new ArrayList<PrivilegeGrantInfo>(0);
    }
    
    private PrincipalType getPrincipalTypeFromStr(final String str) {
        return (str == null) ? null : PrincipalType.valueOf(str);
    }
    
    private List<PrivilegeGrantInfo> getTablePrivilege(String dbName, String tableName, final String principalName, final PrincipalType principalType) {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        if (principalName != null) {
            final List<MTablePrivilege> userNameTabPartPriv = this.listAllTableGrants(principalName, principalType, dbName, tableName);
            if (userNameTabPartPriv != null && userNameTabPartPriv.size() > 0) {
                final List<PrivilegeGrantInfo> grantInfos = new ArrayList<PrivilegeGrantInfo>(userNameTabPartPriv.size());
                for (int i = 0; i < userNameTabPartPriv.size(); ++i) {
                    final MTablePrivilege item = userNameTabPartPriv.get(i);
                    grantInfos.add(new PrivilegeGrantInfo(item.getPrivilege(), item.getCreateTime(), item.getGrantor(), this.getPrincipalTypeFromStr(item.getGrantorType()), item.getGrantOption()));
                }
                return grantInfos;
            }
        }
        return new ArrayList<PrivilegeGrantInfo>(0);
    }
    
    private List<PrivilegeGrantInfo> getColumnPrivilege(String dbName, String tableName, String columnName, final String partitionName, final String principalName, final PrincipalType principalType) {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        columnName = HiveStringUtils.normalizeIdentifier(columnName);
        if (partitionName == null) {
            final List<MTableColumnPrivilege> userNameColumnPriv = this.listPrincipalTableColumnGrants(principalName, principalType, dbName, tableName, columnName);
            if (userNameColumnPriv != null && userNameColumnPriv.size() > 0) {
                final List<PrivilegeGrantInfo> grantInfos = new ArrayList<PrivilegeGrantInfo>(userNameColumnPriv.size());
                for (int i = 0; i < userNameColumnPriv.size(); ++i) {
                    final MTableColumnPrivilege item = userNameColumnPriv.get(i);
                    grantInfos.add(new PrivilegeGrantInfo(item.getPrivilege(), item.getCreateTime(), item.getGrantor(), this.getPrincipalTypeFromStr(item.getGrantorType()), item.getGrantOption()));
                }
                return grantInfos;
            }
        }
        else {
            final List<MPartitionColumnPrivilege> userNameColumnPriv2 = this.listPrincipalPartitionColumnGrants(principalName, principalType, dbName, tableName, partitionName, columnName);
            if (userNameColumnPriv2 != null && userNameColumnPriv2.size() > 0) {
                final List<PrivilegeGrantInfo> grantInfos = new ArrayList<PrivilegeGrantInfo>(userNameColumnPriv2.size());
                for (int i = 0; i < userNameColumnPriv2.size(); ++i) {
                    final MPartitionColumnPrivilege item2 = userNameColumnPriv2.get(i);
                    grantInfos.add(new PrivilegeGrantInfo(item2.getPrivilege(), item2.getCreateTime(), item2.getGrantor(), this.getPrincipalTypeFromStr(item2.getGrantorType()), item2.getGrantOption()));
                }
                return grantInfos;
            }
        }
        return new ArrayList<PrivilegeGrantInfo>(0);
    }
    
    @Override
    public boolean grantPrivileges(final PrivilegeBag privileges) throws InvalidObjectException, MetaException, NoSuchObjectException {
        boolean committed = false;
        final int now = (int)(System.currentTimeMillis() / 1000L);
        try {
            this.openTransaction();
            final List<Object> persistentObjs = new ArrayList<Object>();
            final List<HiveObjectPrivilege> privilegeList = privileges.getPrivileges();
            if (privilegeList != null && privilegeList.size() > 0) {
                final Iterator<HiveObjectPrivilege> privIter = privilegeList.iterator();
                final Set<String> privSet = new HashSet<String>();
                while (privIter.hasNext()) {
                    final HiveObjectPrivilege privDef = privIter.next();
                    final HiveObjectRef hiveObject = privDef.getHiveObject();
                    final String privilegeStr = privDef.getGrantInfo().getPrivilege();
                    final String[] privs = privilegeStr.split(",");
                    final String userName = privDef.getPrincipalName();
                    final PrincipalType principalType = privDef.getPrincipalType();
                    final String grantor = privDef.getGrantInfo().getGrantor();
                    final String grantorType = privDef.getGrantInfo().getGrantorType().toString();
                    final boolean grantOption = privDef.getGrantInfo().isGrantOption();
                    privSet.clear();
                    if (principalType == PrincipalType.ROLE) {
                        this.validateRole(userName);
                    }
                    if (hiveObject.getObjectType() == HiveObjectType.GLOBAL) {
                        final List<MGlobalPrivilege> globalPrivs = this.listPrincipalGlobalGrants(userName, principalType);
                        if (globalPrivs != null) {
                            for (final MGlobalPrivilege priv : globalPrivs) {
                                if (priv.getGrantor().equalsIgnoreCase(grantor)) {
                                    privSet.add(priv.getPrivilege());
                                }
                            }
                        }
                        for (final String privilege : privs) {
                            if (privSet.contains(privilege)) {
                                throw new InvalidObjectException(privilege + " is already granted by " + grantor);
                            }
                            final MGlobalPrivilege mGlobalPrivs = new MGlobalPrivilege(userName, principalType.toString(), privilege, now, grantor, grantorType, grantOption);
                            persistentObjs.add(mGlobalPrivs);
                        }
                    }
                    else if (hiveObject.getObjectType() == HiveObjectType.DATABASE) {
                        final MDatabase dbObj = this.getMDatabase(hiveObject.getDbName());
                        if (dbObj == null) {
                            continue;
                        }
                        final List<MDBPrivilege> dbPrivs = this.listPrincipalDBGrants(userName, principalType, hiveObject.getDbName());
                        if (dbPrivs != null) {
                            for (final MDBPrivilege priv2 : dbPrivs) {
                                if (priv2.getGrantor().equalsIgnoreCase(grantor)) {
                                    privSet.add(priv2.getPrivilege());
                                }
                            }
                        }
                        for (final String privilege2 : privs) {
                            if (privSet.contains(privilege2)) {
                                throw new InvalidObjectException(privilege2 + " is already granted on database " + hiveObject.getDbName() + " by " + grantor);
                            }
                            final MDBPrivilege mDb = new MDBPrivilege(userName, principalType.toString(), dbObj, privilege2, now, grantor, grantorType, grantOption);
                            persistentObjs.add(mDb);
                        }
                    }
                    else if (hiveObject.getObjectType() == HiveObjectType.TABLE) {
                        final MTable tblObj = this.getMTable(hiveObject.getDbName(), hiveObject.getObjectName());
                        if (tblObj == null) {
                            continue;
                        }
                        final List<MTablePrivilege> tablePrivs = this.listAllTableGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName());
                        if (tablePrivs != null) {
                            for (final MTablePrivilege priv3 : tablePrivs) {
                                if (priv3.getGrantor() != null && priv3.getGrantor().equalsIgnoreCase(grantor)) {
                                    privSet.add(priv3.getPrivilege());
                                }
                            }
                        }
                        for (final String privilege2 : privs) {
                            if (privSet.contains(privilege2)) {
                                throw new InvalidObjectException(privilege2 + " is already granted on table [" + hiveObject.getDbName() + "," + hiveObject.getObjectName() + "] by " + grantor);
                            }
                            final MTablePrivilege mTab = new MTablePrivilege(userName, principalType.toString(), tblObj, privilege2, now, grantor, grantorType, grantOption);
                            persistentObjs.add(mTab);
                        }
                    }
                    else if (hiveObject.getObjectType() == HiveObjectType.PARTITION) {
                        final MPartition partObj = this.getMPartition(hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getPartValues());
                        String partName = null;
                        if (partObj == null) {
                            continue;
                        }
                        partName = partObj.getPartitionName();
                        final List<MPartitionPrivilege> partPrivs = this.listPrincipalPartitionGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), partObj.getPartitionName());
                        if (partPrivs != null) {
                            for (final MPartitionPrivilege priv4 : partPrivs) {
                                if (priv4.getGrantor().equalsIgnoreCase(grantor)) {
                                    privSet.add(priv4.getPrivilege());
                                }
                            }
                        }
                        for (final String privilege3 : privs) {
                            if (privSet.contains(privilege3)) {
                                throw new InvalidObjectException(privilege3 + " is already granted on partition [" + hiveObject.getDbName() + "," + hiveObject.getObjectName() + "," + partName + "] by " + grantor);
                            }
                            final MPartitionPrivilege mTab2 = new MPartitionPrivilege(userName, principalType.toString(), partObj, privilege3, now, grantor, grantorType, grantOption);
                            persistentObjs.add(mTab2);
                        }
                    }
                    else {
                        if (hiveObject.getObjectType() != HiveObjectType.COLUMN) {
                            continue;
                        }
                        final MTable tblObj = this.getMTable(hiveObject.getDbName(), hiveObject.getObjectName());
                        if (tblObj == null) {
                            continue;
                        }
                        if (hiveObject.getPartValues() != null) {
                            MPartition partObj2 = null;
                            List<MPartitionColumnPrivilege> colPrivs = null;
                            partObj2 = this.getMPartition(hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getPartValues());
                            if (partObj2 == null) {
                                continue;
                            }
                            colPrivs = this.listPrincipalPartitionColumnGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), partObj2.getPartitionName(), hiveObject.getColumnName());
                            if (colPrivs != null) {
                                for (final MPartitionColumnPrivilege priv5 : colPrivs) {
                                    if (priv5.getGrantor().equalsIgnoreCase(grantor)) {
                                        privSet.add(priv5.getPrivilege());
                                    }
                                }
                            }
                            for (final String privilege3 : privs) {
                                if (privSet.contains(privilege3)) {
                                    throw new InvalidObjectException(privilege3 + " is already granted on column " + hiveObject.getColumnName() + " [" + hiveObject.getDbName() + "," + hiveObject.getObjectName() + "," + partObj2.getPartitionName() + "] by " + grantor);
                                }
                                final MPartitionColumnPrivilege mCol = new MPartitionColumnPrivilege(userName, principalType.toString(), partObj2, hiveObject.getColumnName(), privilege3, now, grantor, grantorType, grantOption);
                                persistentObjs.add(mCol);
                            }
                        }
                        else {
                            List<MTableColumnPrivilege> colPrivs2 = null;
                            colPrivs2 = this.listPrincipalTableColumnGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getColumnName());
                            if (colPrivs2 != null) {
                                for (final MTableColumnPrivilege priv6 : colPrivs2) {
                                    if (priv6.getGrantor().equalsIgnoreCase(grantor)) {
                                        privSet.add(priv6.getPrivilege());
                                    }
                                }
                            }
                            for (final String privilege2 : privs) {
                                if (privSet.contains(privilege2)) {
                                    throw new InvalidObjectException(privilege2 + " is already granted on column " + hiveObject.getColumnName() + " [" + hiveObject.getDbName() + "," + hiveObject.getObjectName() + "] by " + grantor);
                                }
                                final MTableColumnPrivilege mCol2 = new MTableColumnPrivilege(userName, principalType.toString(), tblObj, hiveObject.getColumnName(), privilege2, now, grantor, grantorType, grantOption);
                                persistentObjs.add(mCol2);
                            }
                        }
                    }
                }
            }
            if (persistentObjs.size() > 0) {
                this.pm.makePersistentAll(persistentObjs);
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        return committed;
    }
    
    @Override
    public boolean revokePrivileges(final PrivilegeBag privileges, final boolean grantOption) throws InvalidObjectException, MetaException, NoSuchObjectException {
        boolean committed = false;
        try {
            this.openTransaction();
            final List<Object> persistentObjs = new ArrayList<Object>();
            final List<HiveObjectPrivilege> privilegeList = privileges.getPrivileges();
            if (privilegeList != null && privilegeList.size() > 0) {
                for (final HiveObjectPrivilege privDef : privilegeList) {
                    final HiveObjectRef hiveObject = privDef.getHiveObject();
                    final String privilegeStr = privDef.getGrantInfo().getPrivilege();
                    if (privilegeStr != null) {
                        if (privilegeStr.trim().equals("")) {
                            continue;
                        }
                        final String[] privs = privilegeStr.split(",");
                        final String userName = privDef.getPrincipalName();
                        final PrincipalType principalType = privDef.getPrincipalType();
                        if (hiveObject.getObjectType() == HiveObjectType.GLOBAL) {
                            final List<MGlobalPrivilege> mSecUser = this.listPrincipalGlobalGrants(userName, principalType);
                            boolean found = false;
                            if (mSecUser == null) {
                                continue;
                            }
                            for (final String privilege : privs) {
                                for (final MGlobalPrivilege userGrant : mSecUser) {
                                    final String userGrantPrivs = userGrant.getPrivilege();
                                    if (privilege.equals(userGrantPrivs)) {
                                        found = true;
                                        if (grantOption) {
                                            if (!userGrant.getGrantOption()) {
                                                throw new MetaException("User " + userName + " does not have grant option with privilege " + privilege);
                                            }
                                            userGrant.setGrantOption(false);
                                        }
                                        persistentObjs.add(userGrant);
                                        break;
                                    }
                                }
                                if (!found) {
                                    throw new InvalidObjectException("No user grant found for privileges " + privilege);
                                }
                            }
                        }
                        else if (hiveObject.getObjectType() == HiveObjectType.DATABASE) {
                            final MDatabase dbObj = this.getMDatabase(hiveObject.getDbName());
                            if (dbObj == null) {
                                continue;
                            }
                            final String db = hiveObject.getDbName();
                            boolean found2 = false;
                            final List<MDBPrivilege> dbGrants = this.listPrincipalDBGrants(userName, principalType, db);
                            for (final String privilege2 : privs) {
                                for (final MDBPrivilege dbGrant : dbGrants) {
                                    final String dbGrantPriv = dbGrant.getPrivilege();
                                    if (privilege2.equals(dbGrantPriv)) {
                                        found2 = true;
                                        if (grantOption) {
                                            if (!dbGrant.getGrantOption()) {
                                                throw new MetaException("User " + userName + " does not have grant option with privilege " + privilege2);
                                            }
                                            dbGrant.setGrantOption(false);
                                        }
                                        persistentObjs.add(dbGrant);
                                        break;
                                    }
                                }
                                if (!found2) {
                                    throw new InvalidObjectException("No database grant found for privileges " + privilege2 + " on database " + db);
                                }
                            }
                        }
                        else if (hiveObject.getObjectType() == HiveObjectType.TABLE) {
                            boolean found3 = false;
                            final List<MTablePrivilege> tableGrants = this.listAllTableGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName());
                            for (final String privilege : privs) {
                                for (final MTablePrivilege tabGrant : tableGrants) {
                                    final String tableGrantPriv = tabGrant.getPrivilege();
                                    if (privilege.equalsIgnoreCase(tableGrantPriv)) {
                                        found3 = true;
                                        if (grantOption) {
                                            if (!tabGrant.getGrantOption()) {
                                                throw new MetaException("User " + userName + " does not have grant option with privilege " + privilege);
                                            }
                                            tabGrant.setGrantOption(false);
                                        }
                                        persistentObjs.add(tabGrant);
                                        break;
                                    }
                                }
                                if (!found3) {
                                    throw new InvalidObjectException("No grant (" + privilege + ") found " + " on table " + hiveObject.getObjectName() + ", database is " + hiveObject.getDbName());
                                }
                            }
                        }
                        else if (hiveObject.getObjectType() == HiveObjectType.PARTITION) {
                            boolean found3 = false;
                            final Table tabObj = this.getTable(hiveObject.getDbName(), hiveObject.getObjectName());
                            String partName = null;
                            if (hiveObject.getPartValues() != null) {
                                partName = Warehouse.makePartName(tabObj.getPartitionKeys(), hiveObject.getPartValues());
                            }
                            final List<MPartitionPrivilege> partitionGrants = this.listPrincipalPartitionGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), partName);
                            for (final String privilege2 : privs) {
                                for (final MPartitionPrivilege partGrant : partitionGrants) {
                                    final String partPriv = partGrant.getPrivilege();
                                    if (partPriv.equalsIgnoreCase(privilege2)) {
                                        found3 = true;
                                        if (grantOption) {
                                            if (!partGrant.getGrantOption()) {
                                                throw new MetaException("User " + userName + " does not have grant option with privilege " + privilege2);
                                            }
                                            partGrant.setGrantOption(false);
                                        }
                                        persistentObjs.add(partGrant);
                                        break;
                                    }
                                }
                                if (!found3) {
                                    throw new InvalidObjectException("No grant (" + privilege2 + ") found " + " on table " + tabObj.getTableName() + ", partition is " + partName + ", database is " + tabObj.getDbName());
                                }
                            }
                        }
                        else {
                            if (hiveObject.getObjectType() != HiveObjectType.COLUMN) {
                                continue;
                            }
                            final Table tabObj2 = this.getTable(hiveObject.getDbName(), hiveObject.getObjectName());
                            String partName2 = null;
                            if (hiveObject.getPartValues() != null) {
                                partName2 = Warehouse.makePartName(tabObj2.getPartitionKeys(), hiveObject.getPartValues());
                            }
                            if (partName2 != null) {
                                final List<MPartitionColumnPrivilege> mSecCol = this.listPrincipalPartitionColumnGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), partName2, hiveObject.getColumnName());
                                boolean found4 = false;
                                if (mSecCol == null) {
                                    continue;
                                }
                                for (final String privilege2 : privs) {
                                    for (final MPartitionColumnPrivilege col : mSecCol) {
                                        final String colPriv = col.getPrivilege();
                                        if (colPriv.equalsIgnoreCase(privilege2)) {
                                            found4 = true;
                                            if (grantOption) {
                                                if (!col.getGrantOption()) {
                                                    throw new MetaException("User " + userName + " does not have grant option with privilege " + privilege2);
                                                }
                                                col.setGrantOption(false);
                                            }
                                            persistentObjs.add(col);
                                            break;
                                        }
                                    }
                                    if (!found4) {
                                        throw new InvalidObjectException("No grant (" + privilege2 + ") found " + " on table " + tabObj2.getTableName() + ", partition is " + partName2 + ", column name = " + hiveObject.getColumnName() + ", database is " + tabObj2.getDbName());
                                    }
                                }
                            }
                            else {
                                final List<MTableColumnPrivilege> mSecCol2 = this.listPrincipalTableColumnGrants(userName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getColumnName());
                                boolean found4 = false;
                                if (mSecCol2 == null) {
                                    continue;
                                }
                                for (final String privilege2 : privs) {
                                    for (final MTableColumnPrivilege col2 : mSecCol2) {
                                        final String colPriv = col2.getPrivilege();
                                        if (colPriv.equalsIgnoreCase(privilege2)) {
                                            found4 = true;
                                            if (grantOption) {
                                                if (!col2.getGrantOption()) {
                                                    throw new MetaException("User " + userName + " does not have grant option with privilege " + privilege2);
                                                }
                                                col2.setGrantOption(false);
                                            }
                                            persistentObjs.add(col2);
                                            break;
                                        }
                                    }
                                    if (!found4) {
                                        throw new InvalidObjectException("No grant (" + privilege2 + ") found " + " on table " + tabObj2.getTableName() + ", column name = " + hiveObject.getColumnName() + ", database is " + tabObj2.getDbName());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (persistentObjs.size() > 0) {
                if (!grantOption) {
                    this.pm.deletePersistentAll(persistentObjs);
                }
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        return committed;
    }
    
    @Override
    public List<MRoleMap> listRoleMembers(final String roleName) {
        boolean success = false;
        List<MRoleMap> mRoleMemeberList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listMSecurityUserRoleMember");
            final Query query = this.pm.newQuery(MRoleMap.class, "role.roleName == t1");
            query.declareParameters("java.lang.String t1");
            query.setUnique(false);
            mRoleMemeberList = (List<MRoleMap>)query.execute(roleName);
            ObjectStore.LOG.debug("Done executing query for listMSecurityUserRoleMember");
            this.pm.retrieveAll(mRoleMemeberList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMSecurityUserRoleMember");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mRoleMemeberList;
    }
    
    @Override
    public List<MGlobalPrivilege> listPrincipalGlobalGrants(final String principalName, final PrincipalType principalType) {
        boolean commited = false;
        List<MGlobalPrivilege> userNameDbPriv = null;
        try {
            this.openTransaction();
            if (principalName != null) {
                final Query query = this.pm.newQuery(MGlobalPrivilege.class, "principalName == t1 && principalType == t2 ");
                query.declareParameters("java.lang.String t1, java.lang.String t2");
                userNameDbPriv = (List<MGlobalPrivilege>)query.executeWithArray(principalName, principalType.toString());
                this.pm.retrieveAll(userNameDbPriv);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return userNameDbPriv;
    }
    
    @Override
    public List<HiveObjectPrivilege> listGlobalGrantsAll() {
        boolean commited = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MGlobalPrivilege.class);
            final List<MGlobalPrivilege> userNameDbPriv = (List<MGlobalPrivilege>)query.execute();
            this.pm.retrieveAll(userNameDbPriv);
            commited = this.commitTransaction();
            return this.convertGlobal(userNameDbPriv);
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<HiveObjectPrivilege> convertGlobal(final List<MGlobalPrivilege> privs) {
        final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
        for (final MGlobalPrivilege priv : privs) {
            final String pname = priv.getPrincipalName();
            final PrincipalType ptype = PrincipalType.valueOf(priv.getPrincipalType());
            final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.GLOBAL, null, null, null, null);
            final PrivilegeGrantInfo grantor = new PrivilegeGrantInfo(priv.getPrivilege(), priv.getCreateTime(), priv.getGrantor(), PrincipalType.valueOf(priv.getGrantorType()), priv.getGrantOption());
            result.add(new HiveObjectPrivilege(objectRef, pname, ptype, grantor));
        }
        return result;
    }
    
    @Override
    public List<MDBPrivilege> listPrincipalDBGrants(final String principalName, final PrincipalType principalType, String dbName) {
        boolean success = false;
        List<MDBPrivilege> mSecurityDBList = null;
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalDBGrants");
            final Query query = this.pm.newQuery(MDBPrivilege.class, "principalName == t1 && principalType == t2 && database.name == t3");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3");
            mSecurityDBList = (List<MDBPrivilege>)query.executeWithArray(principalName, principalType.toString(), dbName);
            ObjectStore.LOG.debug("Done executing query for listPrincipalDBGrants");
            this.pm.retrieveAll(mSecurityDBList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalDBGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityDBList;
    }
    
    @Override
    public List<HiveObjectPrivilege> listPrincipalDBGrantsAll(final String principalName, final PrincipalType principalType) {
        return this.convertDB(this.listPrincipalAllDBGrant(principalName, principalType));
    }
    
    @Override
    public List<HiveObjectPrivilege> listDBGrantsAll(final String dbName) {
        return this.convertDB(this.listDatabaseGrants(dbName));
    }
    
    private List<HiveObjectPrivilege> convertDB(final List<MDBPrivilege> privs) {
        final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
        for (final MDBPrivilege priv : privs) {
            final String pname = priv.getPrincipalName();
            final PrincipalType ptype = PrincipalType.valueOf(priv.getPrincipalType());
            final String database = priv.getDatabase().getName();
            final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.DATABASE, database, null, null, null);
            final PrivilegeGrantInfo grantor = new PrivilegeGrantInfo(priv.getPrivilege(), priv.getCreateTime(), priv.getGrantor(), PrincipalType.valueOf(priv.getGrantorType()), priv.getGrantOption());
            result.add(new HiveObjectPrivilege(objectRef, pname, ptype, grantor));
        }
        return result;
    }
    
    private List<MDBPrivilege> listPrincipalAllDBGrant(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        List<MDBPrivilege> mSecurityDBList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalAllDBGrant");
            if (principalName != null && principalType != null) {
                final Query query = this.pm.newQuery(MDBPrivilege.class, "principalName == t1 && principalType == t2");
                query.declareParameters("java.lang.String t1, java.lang.String t2");
                mSecurityDBList = (List<MDBPrivilege>)query.execute(principalName, principalType.toString());
            }
            else {
                final Query query = this.pm.newQuery(MDBPrivilege.class);
                mSecurityDBList = (List<MDBPrivilege>)query.execute();
            }
            ObjectStore.LOG.debug("Done executing query for listPrincipalAllDBGrant");
            this.pm.retrieveAll(mSecurityDBList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllDBGrant");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityDBList;
    }
    
    public List<MTablePrivilege> listAllTableGrants(String dbName, String tableName) {
        boolean success = false;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        List<MTablePrivilege> mSecurityTabList = null;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listAllTableGrants");
            final String queryStr = "table.tableName == t1 && table.database.name == t2";
            final Query query = this.pm.newQuery(MTablePrivilege.class, queryStr);
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityTabList = (List<MTablePrivilege>)query.executeWithArray(tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listAllTableGrants");
            this.pm.retrieveAll(mSecurityTabList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listAllTableGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabList;
    }
    
    public List<MPartitionPrivilege> listTableAllPartitionGrants(String dbName, String tableName) {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        boolean success = false;
        List<MPartitionPrivilege> mSecurityTabPartList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listTableAllPartitionGrants");
            final String queryStr = "partition.table.tableName == t1 && partition.table.database.name == t2";
            final Query query = this.pm.newQuery(MPartitionPrivilege.class, queryStr);
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityTabPartList = (List<MPartitionPrivilege>)query.executeWithArray(tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listTableAllPartitionGrants");
            this.pm.retrieveAll(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listTableAllPartitionGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabPartList;
    }
    
    public List<MTableColumnPrivilege> listTableAllColumnGrants(String dbName, String tableName) {
        boolean success = false;
        List<MTableColumnPrivilege> mTblColPrivilegeList = null;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listTableAllColumnGrants");
            final String queryStr = "table.tableName == t1 && table.database.name == t2";
            final Query query = this.pm.newQuery(MTableColumnPrivilege.class, queryStr);
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mTblColPrivilegeList = (List<MTableColumnPrivilege>)query.executeWithArray(tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listTableAllColumnGrants");
            this.pm.retrieveAll(mTblColPrivilegeList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listTableAllColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mTblColPrivilegeList;
    }
    
    public List<MPartitionColumnPrivilege> listTableAllPartitionColumnGrants(String dbName, String tableName) {
        boolean success = false;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        List<MPartitionColumnPrivilege> mSecurityColList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listTableAllPartitionColumnGrants");
            final String queryStr = "partition.table.tableName == t1 && partition.table.database.name == t2";
            final Query query = this.pm.newQuery(MPartitionColumnPrivilege.class, queryStr);
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityColList = (List<MPartitionColumnPrivilege>)query.executeWithArray(tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listTableAllPartitionColumnGrants");
            this.pm.retrieveAll(mSecurityColList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listTableAllPartitionColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityColList;
    }
    
    public List<MPartitionColumnPrivilege> listPartitionAllColumnGrants(String dbName, String tableName, final List<String> partNames) {
        boolean success = false;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        List<MPartitionColumnPrivilege> mSecurityColList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPartitionAllColumnGrants");
            mSecurityColList = this.queryByPartitionNames(dbName, tableName, partNames, MPartitionColumnPrivilege.class, "partition.table.tableName", "partition.table.database.name", "partition.partitionName");
            ObjectStore.LOG.debug("Done executing query for listPartitionAllColumnGrants");
            this.pm.retrieveAll(mSecurityColList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPartitionAllColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityColList;
    }
    
    public void dropPartitionAllColumnGrantsNoTxn(final String dbName, final String tableName, final List<String> partNames) {
        final ObjectPair<Query, Object[]> queryWithParams = this.makeQueryByPartitionNames(dbName, tableName, partNames, MPartitionColumnPrivilege.class, "partition.table.tableName", "partition.table.database.name", "partition.partitionName");
        queryWithParams.getFirst().deletePersistentAll((Object[])queryWithParams.getSecond());
    }
    
    private List<MDBPrivilege> listDatabaseGrants(String dbName) {
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listDatabaseGrants");
            final Query query = this.pm.newQuery(MDBPrivilege.class, "database.name == t1");
            query.declareParameters("java.lang.String t1");
            final List<MDBPrivilege> mSecurityDBList = (List<MDBPrivilege>)query.executeWithArray(dbName);
            ObjectStore.LOG.debug("Done executing query for listDatabaseGrants");
            this.pm.retrieveAll(mSecurityDBList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listDatabaseGrants");
            return mSecurityDBList;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<MPartitionPrivilege> listPartitionGrants(String dbName, String tableName, final List<String> partNames) {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        boolean success = false;
        List<MPartitionPrivilege> mSecurityTabPartList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPartitionGrants");
            mSecurityTabPartList = this.queryByPartitionNames(dbName, tableName, partNames, MPartitionPrivilege.class, "partition.table.tableName", "partition.table.database.name", "partition.partitionName");
            ObjectStore.LOG.debug("Done executing query for listPartitionGrants");
            this.pm.retrieveAll(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPartitionGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabPartList;
    }
    
    private void dropPartitionGrantsNoTxn(final String dbName, final String tableName, final List<String> partNames) {
        final ObjectPair<Query, Object[]> queryWithParams = this.makeQueryByPartitionNames(dbName, tableName, partNames, MPartitionPrivilege.class, "partition.table.tableName", "partition.table.database.name", "partition.partitionName");
        queryWithParams.getFirst().deletePersistentAll((Object[])queryWithParams.getSecond());
    }
    
    private <T> List<T> queryByPartitionNames(final String dbName, final String tableName, final List<String> partNames, final Class<T> clazz, final String tbCol, final String dbCol, final String partCol) {
        final ObjectPair<Query, Object[]> queryAndParams = this.makeQueryByPartitionNames(dbName, tableName, partNames, clazz, tbCol, dbCol, partCol);
        return (List<T>)queryAndParams.getFirst().executeWithArray((Object[])queryAndParams.getSecond());
    }
    
    private ObjectPair<Query, Object[]> makeQueryByPartitionNames(final String dbName, final String tableName, final List<String> partNames, final Class<?> clazz, final String tbCol, final String dbCol, final String partCol) {
        String queryStr = tbCol + " == t1 && " + dbCol + " == t2";
        String paramStr = "java.lang.String t1, java.lang.String t2";
        final Object[] params = new Object[2 + partNames.size()];
        params[0] = HiveStringUtils.normalizeIdentifier(tableName);
        params[1] = HiveStringUtils.normalizeIdentifier(dbName);
        int index = 0;
        for (final String partName : partNames) {
            params[index + 2] = partName;
            queryStr = queryStr + ((index == 0) ? " && (" : " || ") + partCol + " == p" + index;
            paramStr = paramStr + ", java.lang.String p" + index;
            ++index;
        }
        queryStr += ")";
        final Query query = this.pm.newQuery(clazz, queryStr);
        query.declareParameters(paramStr);
        return new ObjectPair<Query, Object[]>(query, params);
    }
    
    @Override
    public List<MTablePrivilege> listAllTableGrants(final String principalName, final PrincipalType principalType, String dbName, String tableName) {
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        boolean success = false;
        List<MTablePrivilege> mSecurityTabPartList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listAllTableGrants");
            final Query query = this.pm.newQuery(MTablePrivilege.class, "principalName == t1 && principalType == t2 && table.tableName == t3 && table.database.name == t4");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3, java.lang.String t4");
            mSecurityTabPartList = (List<MTablePrivilege>)query.executeWithArray(principalName, principalType.toString(), tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listAllTableGrants");
            this.pm.retrieveAll(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listAllTableGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabPartList;
    }
    
    @Override
    public List<MPartitionPrivilege> listPrincipalPartitionGrants(final String principalName, final PrincipalType principalType, String dbName, String tableName, final String partName) {
        boolean success = false;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        List<MPartitionPrivilege> mSecurityTabPartList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listMSecurityPrincipalPartitionGrant");
            final Query query = this.pm.newQuery(MPartitionPrivilege.class, "principalName == t1 && principalType == t2 && partition.table.tableName == t3 && partition.table.database.name == t4 && partition.partitionName == t5");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3, java.lang.String t4, java.lang.String t5");
            mSecurityTabPartList = (List<MPartitionPrivilege>)query.executeWithArray(principalName, principalType.toString(), tableName, dbName, partName);
            ObjectStore.LOG.debug("Done executing query for listMSecurityPrincipalPartitionGrant");
            this.pm.retrieveAll(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listMSecurityPrincipalPartitionGrant");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabPartList;
    }
    
    @Override
    public List<MTableColumnPrivilege> listPrincipalTableColumnGrants(final String principalName, final PrincipalType principalType, String dbName, String tableName, String columnName) {
        boolean success = false;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        columnName = HiveStringUtils.normalizeIdentifier(columnName);
        List<MTableColumnPrivilege> mSecurityColList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalTableColumnGrants");
            final String queryStr = "principalName == t1 && principalType == t2 && table.tableName == t3 && table.database.name == t4 &&  columnName == t5 ";
            final Query query = this.pm.newQuery(MTableColumnPrivilege.class, queryStr);
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3, java.lang.String t4, java.lang.String t5");
            mSecurityColList = (List<MTableColumnPrivilege>)query.executeWithArray(principalName, principalType.toString(), tableName, dbName, columnName);
            ObjectStore.LOG.debug("Done executing query for listPrincipalTableColumnGrants");
            this.pm.retrieveAll(mSecurityColList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalTableColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityColList;
    }
    
    @Override
    public List<MPartitionColumnPrivilege> listPrincipalPartitionColumnGrants(final String principalName, final PrincipalType principalType, String dbName, String tableName, final String partitionName, String columnName) {
        boolean success = false;
        tableName = HiveStringUtils.normalizeIdentifier(tableName);
        dbName = HiveStringUtils.normalizeIdentifier(dbName);
        columnName = HiveStringUtils.normalizeIdentifier(columnName);
        List<MPartitionColumnPrivilege> mSecurityColList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalPartitionColumnGrants");
            final Query query = this.pm.newQuery(MPartitionColumnPrivilege.class, "principalName == t1 && principalType == t2 && partition.table.tableName == t3 && partition.table.database.name == t4 && partition.partitionName == t5 && columnName == t6");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3, java.lang.String t4, java.lang.String t5, java.lang.String t6");
            mSecurityColList = (List<MPartitionColumnPrivilege>)query.executeWithArray(principalName, principalType.toString(), tableName, dbName, partitionName, columnName);
            ObjectStore.LOG.debug("Done executing query for listPrincipalPartitionColumnGrants");
            this.pm.retrieveAll(mSecurityColList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalPartitionColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityColList;
    }
    
    @Override
    public List<HiveObjectPrivilege> listPrincipalPartitionColumnGrantsAll(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalPartitionColumnGrantsAll");
            List<MPartitionColumnPrivilege> mSecurityTabPartList;
            if (principalName != null && principalType != null) {
                final Query query = this.pm.newQuery(MPartitionColumnPrivilege.class, "principalName == t1 && principalType == t2");
                query.declareParameters("java.lang.String t1, java.lang.String t2");
                mSecurityTabPartList = (List<MPartitionColumnPrivilege>)query.executeWithArray(principalName, principalType.toString());
            }
            else {
                final Query query = this.pm.newQuery(MPartitionColumnPrivilege.class);
                mSecurityTabPartList = (List<MPartitionColumnPrivilege>)query.execute();
            }
            ObjectStore.LOG.debug("Done executing query for listPrincipalPartitionColumnGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertPartCols(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalPartitionColumnGrantsAll");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public List<HiveObjectPrivilege> listPartitionColumnGrantsAll(final String dbName, final String tableName, final String partitionName, final String columnName) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPartitionColumnGrantsAll");
            final Query query = this.pm.newQuery(MPartitionColumnPrivilege.class, "partition.table.tableName == t3 && partition.table.database.name == t4 && partition.partitionName == t5 && columnName == t6");
            query.declareParameters("java.lang.String t3, java.lang.String t4, java.lang.String t5, java.lang.String t6");
            final List<MPartitionColumnPrivilege> mSecurityTabPartList = (List<MPartitionColumnPrivilege>)query.executeWithArray(tableName, dbName, partitionName, columnName);
            ObjectStore.LOG.debug("Done executing query for listPartitionColumnGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertPartCols(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPartitionColumnGrantsAll");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<HiveObjectPrivilege> convertPartCols(final List<MPartitionColumnPrivilege> privs) {
        final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
        for (final MPartitionColumnPrivilege priv : privs) {
            final String pname = priv.getPrincipalName();
            final PrincipalType ptype = PrincipalType.valueOf(priv.getPrincipalType());
            final MPartition mpartition = priv.getPartition();
            final MTable mtable = mpartition.getTable();
            final MDatabase mdatabase = mtable.getDatabase();
            final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.COLUMN, mdatabase.getName(), mtable.getTableName(), mpartition.getValues(), priv.getColumnName());
            final PrivilegeGrantInfo grantor = new PrivilegeGrantInfo(priv.getPrivilege(), priv.getCreateTime(), priv.getGrantor(), PrincipalType.valueOf(priv.getGrantorType()), priv.getGrantOption());
            result.add(new HiveObjectPrivilege(objectRef, pname, ptype, grantor));
        }
        return result;
    }
    
    private List<MTablePrivilege> listPrincipalAllTableGrants(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        List<MTablePrivilege> mSecurityTabPartList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalAllTableGrants");
            final Query query = this.pm.newQuery(MTablePrivilege.class, "principalName == t1 && principalType == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityTabPartList = (List<MTablePrivilege>)query.execute(principalName, principalType.toString());
            ObjectStore.LOG.debug("Done executing query for listPrincipalAllTableGrants");
            this.pm.retrieveAll(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllTableGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabPartList;
    }
    
    @Override
    public List<HiveObjectPrivilege> listPrincipalTableGrantsAll(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalAllTableGrants");
            List<MTablePrivilege> mSecurityTabPartList;
            if (principalName != null && principalType != null) {
                final Query query = this.pm.newQuery(MTablePrivilege.class, "principalName == t1 && principalType == t2");
                query.declareParameters("java.lang.String t1, java.lang.String t2");
                mSecurityTabPartList = (List<MTablePrivilege>)query.execute(principalName, principalType.toString());
            }
            else {
                final Query query = this.pm.newQuery(MTablePrivilege.class);
                mSecurityTabPartList = (List<MTablePrivilege>)query.execute();
            }
            ObjectStore.LOG.debug("Done executing query for listPrincipalAllTableGrants");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertTable(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllTableGrants");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public List<HiveObjectPrivilege> listTableGrantsAll(final String dbName, final String tableName) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listTableGrantsAll");
            final Query query = this.pm.newQuery(MTablePrivilege.class, "table.tableName == t1 && table.database.name == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            final List<MTablePrivilege> mSecurityTabPartList = (List<MTablePrivilege>)query.executeWithArray(tableName, dbName);
            ObjectStore.LOG.debug("Done executing query for listTableGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertTable(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllTableGrants");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<HiveObjectPrivilege> convertTable(final List<MTablePrivilege> privs) {
        final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
        for (final MTablePrivilege priv : privs) {
            final String pname = priv.getPrincipalName();
            final PrincipalType ptype = PrincipalType.valueOf(priv.getPrincipalType());
            final String table = priv.getTable().getTableName();
            final String database = priv.getTable().getDatabase().getName();
            final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.TABLE, database, table, null, null);
            final PrivilegeGrantInfo grantor = new PrivilegeGrantInfo(priv.getPrivilege(), priv.getCreateTime(), priv.getGrantor(), PrincipalType.valueOf(priv.getGrantorType()), priv.getGrantOption());
            result.add(new HiveObjectPrivilege(objectRef, pname, ptype, grantor));
        }
        return result;
    }
    
    private List<MPartitionPrivilege> listPrincipalAllPartitionGrants(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        List<MPartitionPrivilege> mSecurityTabPartList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalAllPartitionGrants");
            final Query query = this.pm.newQuery(MPartitionPrivilege.class, "principalName == t1 && principalType == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityTabPartList = (List<MPartitionPrivilege>)query.execute(principalName, principalType.toString());
            ObjectStore.LOG.debug("Done executing query for listPrincipalAllPartitionGrants");
            this.pm.retrieveAll(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllPartitionGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityTabPartList;
    }
    
    @Override
    public List<HiveObjectPrivilege> listPrincipalPartitionGrantsAll(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalPartitionGrantsAll");
            List<MPartitionPrivilege> mSecurityTabPartList;
            if (principalName != null && principalType != null) {
                final Query query = this.pm.newQuery(MPartitionPrivilege.class, "principalName == t1 && principalType == t2");
                query.declareParameters("java.lang.String t1, java.lang.String t2");
                mSecurityTabPartList = (List<MPartitionPrivilege>)query.execute(principalName, principalType.toString());
            }
            else {
                final Query query = this.pm.newQuery(MPartitionPrivilege.class);
                mSecurityTabPartList = (List<MPartitionPrivilege>)query.execute();
            }
            ObjectStore.LOG.debug("Done executing query for listPrincipalPartitionGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertPartition(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalPartitionGrantsAll");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public List<HiveObjectPrivilege> listPartitionGrantsAll(final String dbName, final String tableName, final String partitionName) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalPartitionGrantsAll");
            final Query query = this.pm.newQuery(MPartitionPrivilege.class, "partition.table.tableName == t3 && partition.table.database.name == t4 && partition.partitionName == t5");
            query.declareParameters("java.lang.String t3, java.lang.String t4, java.lang.String t5");
            final List<MPartitionPrivilege> mSecurityTabPartList = (List<MPartitionPrivilege>)query.executeWithArray(tableName, dbName, partitionName);
            ObjectStore.LOG.debug("Done executing query for listPrincipalPartitionGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertPartition(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalPartitionGrantsAll");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<HiveObjectPrivilege> convertPartition(final List<MPartitionPrivilege> privs) {
        final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
        for (final MPartitionPrivilege priv : privs) {
            final String pname = priv.getPrincipalName();
            final PrincipalType ptype = PrincipalType.valueOf(priv.getPrincipalType());
            final MPartition mpartition = priv.getPartition();
            final MTable mtable = mpartition.getTable();
            final MDatabase mdatabase = mtable.getDatabase();
            final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.PARTITION, mdatabase.getName(), mtable.getTableName(), mpartition.getValues(), null);
            final PrivilegeGrantInfo grantor = new PrivilegeGrantInfo(priv.getPrivilege(), priv.getCreateTime(), priv.getGrantor(), PrincipalType.valueOf(priv.getGrantorType()), priv.getGrantOption());
            result.add(new HiveObjectPrivilege(objectRef, pname, ptype, grantor));
        }
        return result;
    }
    
    private List<MTableColumnPrivilege> listPrincipalAllTableColumnGrants(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        List<MTableColumnPrivilege> mSecurityColumnList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalAllTableColumnGrants");
            final Query query = this.pm.newQuery(MTableColumnPrivilege.class, "principalName == t1 && principalType == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityColumnList = (List<MTableColumnPrivilege>)query.execute(principalName, principalType.toString());
            ObjectStore.LOG.debug("Done executing query for listPrincipalAllTableColumnGrants");
            this.pm.retrieveAll(mSecurityColumnList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllTableColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityColumnList;
    }
    
    @Override
    public List<HiveObjectPrivilege> listPrincipalTableColumnGrantsAll(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalTableColumnGrantsAll");
            List<MTableColumnPrivilege> mSecurityTabPartList;
            if (principalName != null && principalType != null) {
                final Query query = this.pm.newQuery(MTableColumnPrivilege.class, "principalName == t1 && principalType == t2");
                query.declareParameters("java.lang.String t1, java.lang.String t2");
                mSecurityTabPartList = (List<MTableColumnPrivilege>)query.execute(principalName, principalType.toString());
            }
            else {
                final Query query = this.pm.newQuery(MTableColumnPrivilege.class);
                mSecurityTabPartList = (List<MTableColumnPrivilege>)query.execute();
            }
            ObjectStore.LOG.debug("Done executing query for listPrincipalTableColumnGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertTableCols(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalTableColumnGrantsAll");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public List<HiveObjectPrivilege> listTableColumnGrantsAll(final String dbName, final String tableName, final String columnName) {
        boolean success = false;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalTableColumnGrantsAll");
            final Query query = this.pm.newQuery(MTableColumnPrivilege.class, "table.tableName == t3 && table.database.name == t4 &&  columnName == t5");
            query.declareParameters("java.lang.String t3, java.lang.String t4, java.lang.String t5");
            final List<MTableColumnPrivilege> mSecurityTabPartList = (List<MTableColumnPrivilege>)query.executeWithArray(tableName, dbName, columnName);
            ObjectStore.LOG.debug("Done executing query for listPrincipalTableColumnGrantsAll");
            this.pm.retrieveAll(mSecurityTabPartList);
            final List<HiveObjectPrivilege> result = this.convertTableCols(mSecurityTabPartList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalTableColumnGrantsAll");
            return result;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<HiveObjectPrivilege> convertTableCols(final List<MTableColumnPrivilege> privs) {
        final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
        for (final MTableColumnPrivilege priv : privs) {
            final String pname = priv.getPrincipalName();
            final PrincipalType ptype = PrincipalType.valueOf(priv.getPrincipalType());
            final MTable mtable = priv.getTable();
            final MDatabase mdatabase = mtable.getDatabase();
            final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.COLUMN, mdatabase.getName(), mtable.getTableName(), null, priv.getColumnName());
            final PrivilegeGrantInfo grantor = new PrivilegeGrantInfo(priv.getPrivilege(), priv.getCreateTime(), priv.getGrantor(), PrincipalType.valueOf(priv.getGrantorType()), priv.getGrantOption());
            result.add(new HiveObjectPrivilege(objectRef, pname, ptype, grantor));
        }
        return result;
    }
    
    private List<MPartitionColumnPrivilege> listPrincipalAllPartitionColumnGrants(final String principalName, final PrincipalType principalType) {
        boolean success = false;
        List<MPartitionColumnPrivilege> mSecurityColumnList = null;
        try {
            this.openTransaction();
            ObjectStore.LOG.debug("Executing listPrincipalAllTableColumnGrants");
            final Query query = this.pm.newQuery(MPartitionColumnPrivilege.class, "principalName == t1 && principalType == t2");
            query.declareParameters("java.lang.String t1, java.lang.String t2");
            mSecurityColumnList = (List<MPartitionColumnPrivilege>)query.execute(principalName, principalType.toString());
            ObjectStore.LOG.debug("Done executing query for listPrincipalAllTableColumnGrants");
            this.pm.retrieveAll(mSecurityColumnList);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done retrieving all objects for listPrincipalAllTableColumnGrants");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return mSecurityColumnList;
    }
    
    @Override
    public boolean isPartitionMarkedForEvent(final String dbName, final String tblName, final Map<String, String> partName, final PartitionEventType evtType) throws UnknownTableException, MetaException, InvalidPartitionException, UnknownPartitionException {
        boolean success = false;
        ObjectStore.LOG.debug("Begin Executing isPartitionMarkedForEvent");
        Collection<MPartitionEvent> partEvents;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MPartitionEvent.class, "dbName == t1 && tblName == t2 && partName == t3 && eventType == t4");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3, int t4");
            final Table tbl = this.getTable(dbName, tblName);
            if (null == tbl) {
                throw new UnknownTableException("Table: " + tblName + " is not found.");
            }
            partEvents = (Collection<MPartitionEvent>)query.executeWithArray(dbName, tblName, this.getPartitionStr(tbl, partName), evtType.getValue());
            this.pm.retrieveAll(partEvents);
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done executing isPartitionMarkedForEvent");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return partEvents != null && !partEvents.isEmpty();
    }
    
    @Override
    public Table markPartitionForEvent(final String dbName, final String tblName, final Map<String, String> partName, final PartitionEventType evtType) throws MetaException, UnknownTableException, InvalidPartitionException, UnknownPartitionException {
        ObjectStore.LOG.debug("Begin executing markPartitionForEvent");
        boolean success = false;
        Table tbl = null;
        try {
            this.openTransaction();
            tbl = this.getTable(dbName, tblName);
            if (null == tbl) {
                throw new UnknownTableException("Table: " + tblName + " is not found.");
            }
            this.pm.makePersistent(new MPartitionEvent(dbName, tblName, this.getPartitionStr(tbl, partName), evtType.getValue()));
            success = this.commitTransaction();
            ObjectStore.LOG.debug("Done executing markPartitionForEvent");
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        return tbl;
    }
    
    private String getPartitionStr(final Table tbl, final Map<String, String> partName) throws InvalidPartitionException {
        if (tbl.getPartitionKeysSize() != partName.size()) {
            throw new InvalidPartitionException("Number of partition columns in table: " + tbl.getPartitionKeysSize() + " doesn't match with number of supplied partition values: " + partName.size());
        }
        final List<String> storedVals = new ArrayList<String>(tbl.getPartitionKeysSize());
        for (final FieldSchema partKey : tbl.getPartitionKeys()) {
            final String partVal = partName.get(partKey.getName());
            if (null == partVal) {
                throw new InvalidPartitionException("No value found for partition column: " + partKey.getName());
            }
            storedVals.add(partVal);
        }
        return StringUtils.join(storedVals, ',');
    }
    
    public Collection<?> executeJDOQLSelect(final String query) {
        boolean committed = false;
        Collection<?> result = null;
        try {
            this.openTransaction();
            final Query q = this.pm.newQuery(query);
            result = (Collection<?>)q.execute();
            committed = this.commitTransaction();
            if (committed) {
                return result;
            }
            return null;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    public long executeJDOQLUpdate(final String query) {
        boolean committed = false;
        long numUpdated = 0L;
        try {
            this.openTransaction();
            final Query q = this.pm.newQuery(query);
            numUpdated = (long)q.execute();
            committed = this.commitTransaction();
            if (committed) {
                return numUpdated;
            }
            return -1L;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    public Set<String> listFSRoots() {
        boolean committed = false;
        final Set<String> fsRoots = new HashSet<String>();
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MDatabase.class);
            final List<MDatabase> mDBs = (List<MDatabase>)query.execute();
            this.pm.retrieveAll(mDBs);
            for (final MDatabase mDB : mDBs) {
                fsRoots.add(mDB.getLocationUri());
            }
            committed = this.commitTransaction();
            if (committed) {
                return fsRoots;
            }
            return null;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    private boolean shouldUpdateURI(final URI onDiskUri, final URI inputUri) {
        final String onDiskHost = onDiskUri.getHost();
        final String inputHost = inputUri.getHost();
        final int onDiskPort = onDiskUri.getPort();
        final int inputPort = inputUri.getPort();
        final String onDiskScheme = onDiskUri.getScheme();
        final String inputScheme = inputUri.getScheme();
        if (inputPort != -1 && inputPort != onDiskPort) {
            return false;
        }
        if (inputScheme != null) {
            if (onDiskScheme == null) {
                return false;
            }
            if (!inputScheme.equalsIgnoreCase(onDiskScheme)) {
                return false;
            }
        }
        return onDiskHost != null && inputHost.equalsIgnoreCase(onDiskHost);
    }
    
    public UpdateMDatabaseURIRetVal updateMDatabaseURI(final URI oldLoc, final URI newLoc, final boolean dryRun) {
        boolean committed = false;
        final Map<String, String> updateLocations = new HashMap<String, String>();
        final List<String> badRecords = new ArrayList<String>();
        UpdateMDatabaseURIRetVal retVal = null;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MDatabase.class);
            final List<MDatabase> mDBs = (List<MDatabase>)query.execute();
            this.pm.retrieveAll(mDBs);
            for (final MDatabase mDB : mDBs) {
                URI locationURI = null;
                final String location = mDB.getLocationUri();
                try {
                    locationURI = new URI(location);
                }
                catch (URISyntaxException e) {
                    badRecords.add(location);
                }
                catch (NullPointerException e2) {
                    badRecords.add(location);
                }
                if (locationURI == null) {
                    badRecords.add(location);
                }
                else {
                    if (!this.shouldUpdateURI(locationURI, oldLoc)) {
                        continue;
                    }
                    final String dbLoc = mDB.getLocationUri().replaceAll(oldLoc.toString(), newLoc.toString());
                    updateLocations.put(locationURI.toString(), dbLoc);
                    if (dryRun) {
                        continue;
                    }
                    mDB.setLocationUri(dbLoc);
                }
            }
            committed = this.commitTransaction();
            if (committed) {
                retVal = new UpdateMDatabaseURIRetVal(badRecords, updateLocations);
            }
            return retVal;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    private void updatePropURIHelper(final URI oldLoc, final URI newLoc, final String tblPropKey, final boolean isDryRun, final List<String> badRecords, final Map<String, String> updateLocations, final Map<String, String> parameters) {
        URI tablePropLocationURI = null;
        if (parameters.containsKey(tblPropKey)) {
            final String tablePropLocation = parameters.get(tblPropKey);
            try {
                tablePropLocationURI = new URI(tablePropLocation);
            }
            catch (URISyntaxException e) {
                badRecords.add(tablePropLocation);
            }
            catch (NullPointerException e2) {
                badRecords.add(tablePropLocation);
            }
            if (tablePropLocationURI == null) {
                badRecords.add(tablePropLocation);
            }
            else if (this.shouldUpdateURI(tablePropLocationURI, oldLoc)) {
                final String tblPropLoc = parameters.get(tblPropKey).replaceAll(oldLoc.toString(), newLoc.toString());
                updateLocations.put(tablePropLocationURI.toString(), tblPropLoc);
                if (!isDryRun) {
                    parameters.put(tblPropKey, tblPropLoc);
                }
            }
        }
    }
    
    public UpdatePropURIRetVal updateTblPropURI(final URI oldLoc, final URI newLoc, final String tblPropKey, final boolean isDryRun) {
        boolean committed = false;
        final Map<String, String> updateLocations = new HashMap<String, String>();
        final List<String> badRecords = new ArrayList<String>();
        UpdatePropURIRetVal retVal = null;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MTable.class);
            final List<MTable> mTbls = (List<MTable>)query.execute();
            this.pm.retrieveAll(mTbls);
            for (final MTable mTbl : mTbls) {
                this.updatePropURIHelper(oldLoc, newLoc, tblPropKey, isDryRun, badRecords, updateLocations, mTbl.getParameters());
            }
            committed = this.commitTransaction();
            if (committed) {
                retVal = new UpdatePropURIRetVal(badRecords, updateLocations);
            }
            return retVal;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Deprecated
    public UpdatePropURIRetVal updateMStorageDescriptorTblPropURI(final URI oldLoc, final URI newLoc, final String tblPropKey, final boolean isDryRun) {
        boolean committed = false;
        final Map<String, String> updateLocations = new HashMap<String, String>();
        final List<String> badRecords = new ArrayList<String>();
        UpdatePropURIRetVal retVal = null;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MStorageDescriptor.class);
            final List<MStorageDescriptor> mSDSs = (List<MStorageDescriptor>)query.execute();
            this.pm.retrieveAll(mSDSs);
            for (final MStorageDescriptor mSDS : mSDSs) {
                this.updatePropURIHelper(oldLoc, newLoc, tblPropKey, isDryRun, badRecords, updateLocations, mSDS.getParameters());
            }
            committed = this.commitTransaction();
            if (committed) {
                retVal = new UpdatePropURIRetVal(badRecords, updateLocations);
            }
            return retVal;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    public UpdateMStorageDescriptorTblURIRetVal updateMStorageDescriptorTblURI(final URI oldLoc, final URI newLoc, final boolean isDryRun) {
        boolean committed = false;
        final Map<String, String> updateLocations = new HashMap<String, String>();
        final List<String> badRecords = new ArrayList<String>();
        UpdateMStorageDescriptorTblURIRetVal retVal = null;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MStorageDescriptor.class);
            final List<MStorageDescriptor> mSDSs = (List<MStorageDescriptor>)query.execute();
            this.pm.retrieveAll(mSDSs);
            for (final MStorageDescriptor mSDS : mSDSs) {
                URI locationURI = null;
                final String location = mSDS.getLocation();
                try {
                    locationURI = new URI(location);
                }
                catch (URISyntaxException e) {
                    badRecords.add(location);
                }
                catch (NullPointerException e2) {
                    badRecords.add(location);
                }
                if (locationURI == null) {
                    badRecords.add(location);
                }
                else {
                    if (!this.shouldUpdateURI(locationURI, oldLoc)) {
                        continue;
                    }
                    final String tblLoc = mSDS.getLocation().replaceAll(oldLoc.toString(), newLoc.toString());
                    updateLocations.put(locationURI.toString(), tblLoc);
                    if (isDryRun) {
                        continue;
                    }
                    mSDS.setLocation(tblLoc);
                }
            }
            committed = this.commitTransaction();
            if (committed) {
                retVal = new UpdateMStorageDescriptorTblURIRetVal(badRecords, updateLocations);
            }
            return retVal;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    public UpdateSerdeURIRetVal updateSerdeURI(final URI oldLoc, final URI newLoc, final String serdeProp, final boolean isDryRun) {
        boolean committed = false;
        final Map<String, String> updateLocations = new HashMap<String, String>();
        final List<String> badRecords = new ArrayList<String>();
        UpdateSerdeURIRetVal retVal = null;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MSerDeInfo.class);
            final List<MSerDeInfo> mSerdes = (List<MSerDeInfo>)query.execute();
            this.pm.retrieveAll(mSerdes);
            for (final MSerDeInfo mSerde : mSerdes) {
                if (mSerde.getParameters().containsKey(serdeProp)) {
                    final String schemaLoc = mSerde.getParameters().get(serdeProp);
                    URI schemaLocURI = null;
                    try {
                        schemaLocURI = new URI(schemaLoc);
                    }
                    catch (URISyntaxException e) {
                        badRecords.add(schemaLoc);
                    }
                    catch (NullPointerException e2) {
                        badRecords.add(schemaLoc);
                    }
                    if (schemaLocURI == null) {
                        badRecords.add(schemaLoc);
                    }
                    else {
                        if (!this.shouldUpdateURI(schemaLocURI, oldLoc)) {
                            continue;
                        }
                        final String newSchemaLoc = schemaLoc.replaceAll(oldLoc.toString(), newLoc.toString());
                        updateLocations.put(schemaLocURI.toString(), newSchemaLoc);
                        if (isDryRun) {
                            continue;
                        }
                        mSerde.getParameters().put(serdeProp, newSchemaLoc);
                    }
                }
            }
            committed = this.commitTransaction();
            if (committed) {
                retVal = new UpdateSerdeURIRetVal(badRecords, updateLocations);
            }
            return retVal;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    private void writeMTableColumnStatistics(final Table table, final MTableColumnStatistics mStatsObj) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        final String dbName = mStatsObj.getDbName();
        final String tableName = mStatsObj.getTableName();
        final String colName = mStatsObj.getColName();
        ObjectStore.LOG.info("Updating table level column statistics for db=" + dbName + " tableName=" + tableName + " colName=" + colName);
        this.validateTableCols(table, Lists.newArrayList(colName));
        final List<MTableColumnStatistics> oldStats = this.getMTableColumnStatistics(table, Lists.newArrayList(colName));
        if (!oldStats.isEmpty()) {
            assert oldStats.size() == 1;
            StatObjectConverter.setFieldsIntoOldStats(mStatsObj, oldStats.get(0));
        }
        else {
            this.pm.makePersistent(mStatsObj);
        }
    }
    
    private void writeMPartitionColumnStatistics(final Table table, final Partition partition, final MPartitionColumnStatistics mStatsObj) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        final String dbName = mStatsObj.getDbName();
        final String tableName = mStatsObj.getTableName();
        final String partName = mStatsObj.getPartitionName();
        final String colName = mStatsObj.getColName();
        ObjectStore.LOG.info("Updating partition level column statistics for db=" + dbName + " tableName=" + tableName + " partName=" + partName + " colName=" + colName);
        boolean foundCol = false;
        final List<FieldSchema> colList = partition.getSd().getCols();
        for (final FieldSchema col : colList) {
            if (col.getName().equals(mStatsObj.getColName().trim())) {
                foundCol = true;
                break;
            }
        }
        if (!foundCol) {
            throw new NoSuchObjectException("Column " + colName + " for which stats gathering is requested doesn't exist.");
        }
        final List<MPartitionColumnStatistics> oldStats = this.getMPartitionColumnStatistics(table, Lists.newArrayList(partName), Lists.newArrayList(colName));
        if (!oldStats.isEmpty()) {
            assert oldStats.size() == 1;
            StatObjectConverter.setFieldsIntoOldStats(mStatsObj, oldStats.get(0));
        }
        else {
            this.pm.makePersistent(mStatsObj);
        }
    }
    
    @Override
    public boolean updateTableColumnStatistics(final ColumnStatistics colStats) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        boolean committed = false;
        this.openTransaction();
        try {
            final List<ColumnStatisticsObj> statsObjs = colStats.getStatsObj();
            final ColumnStatisticsDesc statsDesc = colStats.getStatsDesc();
            final Table table = this.ensureGetTable(statsDesc.getDbName(), statsDesc.getTableName());
            for (final ColumnStatisticsObj statsObj : statsObjs) {
                final MTableColumnStatistics mStatsObj = StatObjectConverter.convertToMTableColumnStatistics(this.ensureGetMTable(statsDesc.getDbName(), statsDesc.getTableName()), statsDesc, statsObj);
                this.writeMTableColumnStatistics(table, mStatsObj);
            }
            committed = this.commitTransaction();
            return committed;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public boolean updatePartitionColumnStatistics(final ColumnStatistics colStats, final List<String> partVals) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        boolean committed = false;
        try {
            this.openTransaction();
            final List<ColumnStatisticsObj> statsObjs = colStats.getStatsObj();
            final ColumnStatisticsDesc statsDesc = colStats.getStatsDesc();
            final Table table = this.ensureGetTable(statsDesc.getDbName(), statsDesc.getTableName());
            final Partition partition = this.convertToPart(this.getMPartition(statsDesc.getDbName(), statsDesc.getTableName(), partVals));
            for (final ColumnStatisticsObj statsObj : statsObjs) {
                final MPartition mPartition = this.getMPartition(statsDesc.getDbName(), statsDesc.getTableName(), partVals);
                if (partition == null) {
                    throw new NoSuchObjectException("Partition for which stats is gathered doesn't exist.");
                }
                final MPartitionColumnStatistics mStatsObj = StatObjectConverter.convertToMPartitionColumnStatistics(mPartition, statsDesc, statsObj);
                this.writeMPartitionColumnStatistics(table, partition, mStatsObj);
            }
            committed = this.commitTransaction();
            return committed;
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    private List<MTableColumnStatistics> getMTableColumnStatistics(final Table table, final List<String> colNames) throws MetaException {
        boolean committed = false;
        this.openTransaction();
        try {
            List<MTableColumnStatistics> result = null;
            this.validateTableCols(table, colNames);
            final Query query = this.pm.newQuery(MTableColumnStatistics.class);
            String filter = "tableName == t1 && dbName == t2 && (";
            String paramStr = "java.lang.String t1, java.lang.String t2";
            final Object[] params = new Object[colNames.size() + 2];
            params[0] = table.getTableName();
            params[1] = table.getDbName();
            for (int i = 0; i < colNames.size(); ++i) {
                filter = filter + ((i == 0) ? "" : " || ") + "colName == c" + i;
                paramStr = paramStr + ", java.lang.String c" + i;
                params[i + 2] = colNames.get(i);
            }
            filter += ")";
            query.setFilter(filter);
            query.declareParameters(paramStr);
            result = (List<MTableColumnStatistics>)query.executeWithArray(params);
            this.pm.retrieveAll(result);
            if (result.size() > colNames.size()) {
                throw new MetaException("Unexpected " + result.size() + " statistics for " + colNames.size() + " columns");
            }
            committed = this.commitTransaction();
            return result;
        }
        catch (Exception ex) {
            ObjectStore.LOG.error("Error retrieving statistics via jdo", ex);
            if (ex instanceof MetaException) {
                throw (MetaException)ex;
            }
            throw new MetaException(ex.getMessage());
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
                return (List<MTableColumnStatistics>)Lists.newArrayList();
            }
        }
    }
    
    private void validateTableCols(final Table table, final List<String> colNames) throws MetaException {
        final List<FieldSchema> colList = table.getSd().getCols();
        for (final String colName : colNames) {
            boolean foundCol = false;
            for (final FieldSchema mCol : colList) {
                if (mCol.getName().equals(colName.trim())) {
                    foundCol = true;
                    break;
                }
            }
            if (!foundCol) {
                throw new MetaException("Column " + colName + " doesn't exist.");
            }
        }
    }
    
    @Override
    public ColumnStatistics getTableColumnStatistics(final String dbName, final String tableName, final List<String> colNames) throws MetaException, NoSuchObjectException {
        return this.getTableColumnStatisticsInternal(dbName, tableName, colNames, true, true);
    }
    
    protected ColumnStatistics getTableColumnStatisticsInternal(final String dbName, final String tableName, final List<String> colNames, final boolean allowSql, final boolean allowJdo) throws MetaException, NoSuchObjectException {
        return new GetStatHelper(HiveStringUtils.normalizeIdentifier(dbName), HiveStringUtils.normalizeIdentifier(tableName), allowSql, allowJdo) {
            @Override
            protected ColumnStatistics getSqlResult(final GetHelper<ColumnStatistics> ctx) throws MetaException {
                return ObjectStore.this.directSql.getTableStats(this.dbName, this.tblName, colNames);
            }
            
            @Override
            protected ColumnStatistics getJdoResult(final GetHelper<ColumnStatistics> ctx) throws MetaException, NoSuchObjectException {
                final List<MTableColumnStatistics> mStats = ObjectStore.this.getMTableColumnStatistics(this.getTable(), colNames);
                if (mStats.isEmpty()) {
                    return null;
                }
                final ColumnStatisticsDesc desc = StatObjectConverter.getTableColumnStatisticsDesc(mStats.get(0));
                final List<ColumnStatisticsObj> statObjs = new ArrayList<ColumnStatisticsObj>(mStats.size());
                for (final MTableColumnStatistics mStat : mStats) {
                    if (desc.getLastAnalyzed() > mStat.getLastAnalyzed()) {
                        desc.setLastAnalyzed(mStat.getLastAnalyzed());
                    }
                    statObjs.add(StatObjectConverter.getTableColumnStatisticsObj(mStat));
                    Deadline.checkTimeout();
                }
                return new ColumnStatistics(desc, statObjs);
            }
        }.run(true);
    }
    
    @Override
    public List<ColumnStatistics> getPartitionColumnStatistics(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames) throws MetaException, NoSuchObjectException {
        return this.getPartitionColumnStatisticsInternal(dbName, tableName, partNames, colNames, true, true);
    }
    
    protected List<ColumnStatistics> getPartitionColumnStatisticsInternal(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames, final boolean allowSql, final boolean allowJdo) throws MetaException, NoSuchObjectException {
        return ((GetHelper<List<ColumnStatistics>>)new GetListHelper<ColumnStatistics>(dbName, tableName, allowSql, allowJdo) {
            @Override
            protected List<ColumnStatistics> getSqlResult(final GetHelper<List<ColumnStatistics>> ctx) throws MetaException {
                return ObjectStore.this.directSql.getPartitionStats(this.dbName, this.tblName, partNames, colNames);
            }
            
            @Override
            protected List<ColumnStatistics> getJdoResult(final GetHelper<List<ColumnStatistics>> ctx) throws MetaException, NoSuchObjectException {
                final List<MPartitionColumnStatistics> mStats = ObjectStore.this.getMPartitionColumnStatistics(this.getTable(), partNames, colNames);
                final List<ColumnStatistics> result = new ArrayList<ColumnStatistics>(Math.min(mStats.size(), partNames.size()));
                String lastPartName = null;
                List<ColumnStatisticsObj> curList = null;
                ColumnStatisticsDesc csd = null;
                for (int i = 0; i <= mStats.size(); ++i) {
                    final boolean isLast = i == mStats.size();
                    final MPartitionColumnStatistics mStatsObj = isLast ? null : mStats.get(i);
                    final String partName = isLast ? null : mStatsObj.getPartitionName();
                    if (isLast || !partName.equals(lastPartName)) {
                        if (i != 0) {
                            result.add(new ColumnStatistics(csd, curList));
                        }
                        if (isLast) {
                            continue;
                        }
                        csd = StatObjectConverter.getPartitionColumnStatisticsDesc(mStatsObj);
                        curList = new ArrayList<ColumnStatisticsObj>(colNames.size());
                    }
                    curList.add(StatObjectConverter.getPartitionColumnStatisticsObj(mStatsObj));
                    lastPartName = partName;
                    Deadline.checkTimeout();
                }
                return result;
            }
        }).run(true);
    }
    
    @Override
    public AggrStats get_aggr_stats_for(final String dbName, final String tblName, final List<String> partNames, final List<String> colNames) throws MetaException, NoSuchObjectException {
        final boolean useDensityFunctionForNDVEstimation = HiveConf.getBoolVar(this.getConf(), HiveConf.ConfVars.HIVE_METASTORE_STATS_NDV_DENSITY_FUNCTION);
        return new GetHelper<AggrStats>(dbName, tblName, true, false) {
            @Override
            protected AggrStats getSqlResult(final GetHelper<AggrStats> ctx) throws MetaException {
                return ObjectStore.this.directSql.aggrColStatsForPartitions(this.dbName, this.tblName, partNames, colNames, useDensityFunctionForNDVEstimation);
            }
            
            @Override
            protected AggrStats getJdoResult(final GetHelper<AggrStats> ctx) throws MetaException, NoSuchObjectException {
                throw new MetaException("Jdo path is not implemented for stats aggr.");
            }
            
            @Override
            protected String describeResult() {
                return null;
            }
        }.run(true);
    }
    
    private List<MPartitionColumnStatistics> getMPartitionColumnStatistics(final Table table, final List<String> partNames, final List<String> colNames) throws NoSuchObjectException, MetaException {
        boolean committed = false;
        final MPartitionColumnStatistics mStatsObj = null;
        try {
            this.openTransaction();
            this.validateTableCols(table, colNames);
            final boolean foundCol = false;
            final Query query = this.pm.newQuery(MPartitionColumnStatistics.class);
            String paramStr = "java.lang.String t1, java.lang.String t2";
            String filter = "tableName == t1 && dbName == t2 && (";
            final Object[] params = new Object[colNames.size() + partNames.size() + 2];
            int i = 0;
            params[i++] = table.getTableName();
            params[i++] = table.getDbName();
            int firstI = i;
            for (final String s : partNames) {
                filter = filter + ((i == firstI) ? "" : " || ") + "partitionName == p" + i;
                paramStr = paramStr + ", java.lang.String p" + i;
                params[i++] = s;
            }
            filter += ") && (";
            firstI = i;
            for (final String s : colNames) {
                filter = filter + ((i == firstI) ? "" : " || ") + "colName == c" + i;
                paramStr = paramStr + ", java.lang.String c" + i;
                params[i++] = s;
            }
            filter += ")";
            query.setFilter(filter);
            query.declareParameters(paramStr);
            query.setOrdering("partitionName ascending");
            final List<MPartitionColumnStatistics> result = (List<MPartitionColumnStatistics>)query.executeWithArray(params);
            this.pm.retrieveAll(result);
            committed = this.commitTransaction();
            return result;
        }
        catch (Exception ex) {
            ObjectStore.LOG.error("Error retrieving statistics via jdo", ex);
            if (ex instanceof MetaException) {
                throw (MetaException)ex;
            }
            throw new MetaException(ex.getMessage());
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
                return (List<MPartitionColumnStatistics>)Lists.newArrayList();
            }
        }
    }
    
    private void dropPartitionColumnStatisticsNoTxn(final String dbName, final String tableName, final List<String> partNames) throws MetaException {
        final ObjectPair<Query, Object[]> queryWithParams = this.makeQueryByPartitionNames(dbName, tableName, partNames, MPartitionColumnStatistics.class, "tableName", "dbName", "partition.partitionName");
        queryWithParams.getFirst().deletePersistentAll((Object[])queryWithParams.getSecond());
    }
    
    @Override
    public boolean deletePartitionColumnStatistics(String dbName, final String tableName, final String partName, final List<String> partVals, final String colName) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        boolean ret = false;
        if (dbName == null) {
            dbName = "default";
        }
        if (tableName == null) {
            throw new InvalidInputException("Table name is null.");
        }
        try {
            this.openTransaction();
            final MTable mTable = this.getMTable(dbName, tableName);
            if (mTable == null) {
                throw new NoSuchObjectException("Table " + tableName + "  for which stats deletion is requested doesn't exist");
            }
            final MPartition mPartition = this.getMPartition(dbName, tableName, partVals);
            if (mPartition == null) {
                throw new NoSuchObjectException("Partition " + partName + " for which stats deletion is requested doesn't exist");
            }
            final Query query = this.pm.newQuery(MPartitionColumnStatistics.class);
            String filter;
            String parameters;
            if (colName != null) {
                filter = "partition.partitionName == t1 && dbName == t2 && tableName == t3 && colName == t4";
                parameters = "java.lang.String t1, java.lang.String t2, java.lang.String t3, java.lang.String t4";
            }
            else {
                filter = "partition.partitionName == t1 && dbName == t2 && tableName == t3";
                parameters = "java.lang.String t1, java.lang.String t2, java.lang.String t3";
            }
            query.setFilter(filter);
            query.declareParameters(parameters);
            if (colName != null) {
                query.setUnique(true);
                final MPartitionColumnStatistics mStatsObj = (MPartitionColumnStatistics)query.executeWithArray(partName.trim(), HiveStringUtils.normalizeIdentifier(dbName), HiveStringUtils.normalizeIdentifier(tableName), HiveStringUtils.normalizeIdentifier(colName));
                this.pm.retrieve(mStatsObj);
                if (mStatsObj == null) {
                    throw new NoSuchObjectException("Column stats doesn't exist for db=" + dbName + " table=" + tableName + " partition=" + partName + " col=" + colName);
                }
                this.pm.deletePersistent(mStatsObj);
            }
            else {
                final List<MPartitionColumnStatistics> mStatsObjColl = (List<MPartitionColumnStatistics>)query.execute(partName.trim(), HiveStringUtils.normalizeIdentifier(dbName), HiveStringUtils.normalizeIdentifier(tableName));
                this.pm.retrieveAll(mStatsObjColl);
                if (mStatsObjColl == null) {
                    throw new NoSuchObjectException("Column stats doesn't exist for db=" + dbName + " table=" + tableName + " partition" + partName);
                }
                this.pm.deletePersistentAll(mStatsObjColl);
            }
            ret = this.commitTransaction();
        }
        catch (NoSuchObjectException e) {
            this.rollbackTransaction();
            throw e;
        }
        finally {
            if (!ret) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    @Override
    public boolean deleteTableColumnStatistics(String dbName, final String tableName, final String colName) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
        boolean ret = false;
        if (dbName == null) {
            dbName = "default";
        }
        if (tableName == null) {
            throw new InvalidInputException("Table name is null.");
        }
        try {
            this.openTransaction();
            final MTable mTable = this.getMTable(dbName, tableName);
            if (mTable == null) {
                throw new NoSuchObjectException("Table " + tableName + "  for which stats deletion is requested doesn't exist");
            }
            final Query query = this.pm.newQuery(MTableColumnStatistics.class);
            String filter;
            String parameters;
            if (colName != null) {
                filter = "table.tableName == t1 && dbName == t2 && colName == t3";
                parameters = "java.lang.String t1, java.lang.String t2, java.lang.String t3";
            }
            else {
                filter = "table.tableName == t1 && dbName == t2";
                parameters = "java.lang.String t1, java.lang.String t2";
            }
            query.setFilter(filter);
            query.declareParameters(parameters);
            if (colName != null) {
                query.setUnique(true);
                final MTableColumnStatistics mStatsObj = (MTableColumnStatistics)query.execute(HiveStringUtils.normalizeIdentifier(tableName), HiveStringUtils.normalizeIdentifier(dbName), HiveStringUtils.normalizeIdentifier(colName));
                this.pm.retrieve(mStatsObj);
                if (mStatsObj == null) {
                    throw new NoSuchObjectException("Column stats doesn't exist for db=" + dbName + " table=" + tableName + " col=" + colName);
                }
                this.pm.deletePersistent(mStatsObj);
            }
            else {
                final List<MTableColumnStatistics> mStatsObjColl = (List<MTableColumnStatistics>)query.execute(HiveStringUtils.normalizeIdentifier(tableName), HiveStringUtils.normalizeIdentifier(dbName));
                this.pm.retrieveAll(mStatsObjColl);
                if (mStatsObjColl == null) {
                    throw new NoSuchObjectException("Column stats doesn't exist for db=" + dbName + " table=" + tableName);
                }
                this.pm.deletePersistentAll(mStatsObjColl);
            }
            ret = this.commitTransaction();
        }
        catch (NoSuchObjectException e) {
            this.rollbackTransaction();
            throw e;
        }
        finally {
            if (!ret) {
                this.rollbackTransaction();
            }
        }
        return ret;
    }
    
    @Override
    public long cleanupEvents() {
        boolean commited = false;
        ObjectStore.LOG.debug("Begin executing cleanupEvents");
        final Long expiryTime = HiveConf.getTimeVar(this.getConf(), HiveConf.ConfVars.METASTORE_EVENT_EXPIRY_DURATION, TimeUnit.MILLISECONDS);
        final Long curTime = System.currentTimeMillis();
        long delCnt;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MPartitionEvent.class, "curTime - eventTime > expiryTime");
            query.declareParameters("java.lang.Long curTime, java.lang.Long expiryTime");
            delCnt = query.deletePersistentAll(curTime, expiryTime);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
            ObjectStore.LOG.debug("Done executing cleanupEvents");
        }
        return delCnt;
    }
    
    private MDelegationToken getTokenFrom(final String tokenId) {
        final Query query = this.pm.newQuery(MDelegationToken.class, "tokenIdentifier == tokenId");
        query.declareParameters("java.lang.String tokenId");
        query.setUnique(true);
        return (MDelegationToken)query.execute(tokenId);
    }
    
    @Override
    public boolean addToken(final String tokenId, final String delegationToken) {
        ObjectStore.LOG.debug("Begin executing addToken");
        boolean committed = false;
        MDelegationToken token;
        try {
            this.openTransaction();
            token = this.getTokenFrom(tokenId);
            if (token == null) {
                this.pm.makePersistent(new MDelegationToken(tokenId, delegationToken));
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing addToken with status : " + committed);
        return committed && token == null;
    }
    
    @Override
    public boolean removeToken(final String tokenId) {
        ObjectStore.LOG.debug("Begin executing removeToken");
        boolean committed = false;
        MDelegationToken token;
        try {
            this.openTransaction();
            token = this.getTokenFrom(tokenId);
            if (null != token) {
                this.pm.deletePersistent(token);
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing removeToken with status : " + committed);
        return committed && token != null;
    }
    
    @Override
    public String getToken(final String tokenId) {
        ObjectStore.LOG.debug("Begin executing getToken");
        boolean committed = false;
        MDelegationToken token;
        try {
            this.openTransaction();
            token = this.getTokenFrom(tokenId);
            if (null != token) {
                this.pm.retrieve(token);
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing getToken with status : " + committed);
        return (null == token) ? null : token.getTokenStr();
    }
    
    @Override
    public List<String> getAllTokenIdentifiers() {
        ObjectStore.LOG.debug("Begin executing getAllTokenIdentifiers");
        boolean committed = false;
        List<MDelegationToken> tokens;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MDelegationToken.class);
            tokens = (List<MDelegationToken>)query.execute();
            this.pm.retrieveAll(tokens);
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing getAllTokenIdentifers with status : " + committed);
        final List<String> tokenIdents = new ArrayList<String>(tokens.size());
        for (final MDelegationToken token : tokens) {
            tokenIdents.add(token.getTokenIdentifier());
        }
        return tokenIdents;
    }
    
    @Override
    public int addMasterKey(final String key) throws MetaException {
        ObjectStore.LOG.debug("Begin executing addMasterKey");
        boolean committed = false;
        final MMasterKey masterKey = new MMasterKey(key);
        try {
            this.openTransaction();
            this.pm.makePersistent(masterKey);
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing addMasterKey with status : " + committed);
        if (committed) {
            return ((IntIdentity)this.pm.getObjectId(masterKey)).getKey();
        }
        throw new MetaException("Failed to add master key.");
    }
    
    @Override
    public void updateMasterKey(final Integer id, final String key) throws NoSuchObjectException, MetaException {
        ObjectStore.LOG.debug("Begin executing updateMasterKey");
        boolean committed = false;
        MMasterKey masterKey;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MMasterKey.class, "keyId == id");
            query.declareParameters("java.lang.Integer id");
            query.setUnique(true);
            masterKey = (MMasterKey)query.execute(id);
            if (null != masterKey) {
                masterKey.setMasterKey(key);
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing updateMasterKey with status : " + committed);
        if (null == masterKey) {
            throw new NoSuchObjectException("No key found with keyId: " + id);
        }
        if (!committed) {
            throw new MetaException("Though key is found, failed to update it. " + id);
        }
    }
    
    @Override
    public boolean removeMasterKey(final Integer id) {
        ObjectStore.LOG.debug("Begin executing removeMasterKey");
        boolean success = false;
        MMasterKey masterKey;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MMasterKey.class, "keyId == id");
            query.declareParameters("java.lang.Integer id");
            query.setUnique(true);
            masterKey = (MMasterKey)query.execute(id);
            if (null != masterKey) {
                this.pm.deletePersistent(masterKey);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing removeMasterKey with status : " + success);
        return null != masterKey && success;
    }
    
    @Override
    public String[] getMasterKeys() {
        ObjectStore.LOG.debug("Begin executing getMasterKeys");
        boolean committed = false;
        List<MMasterKey> keys;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MMasterKey.class);
            keys = (List<MMasterKey>)query.execute();
            this.pm.retrieveAll(keys);
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        ObjectStore.LOG.debug("Done executing getMasterKeys with status : " + committed);
        final String[] masterKeys = new String[keys.size()];
        for (int i = 0; i < keys.size(); ++i) {
            masterKeys[i] = keys.get(i).getMasterKey();
        }
        return masterKeys;
    }
    
    @Override
    public void verifySchema() throws MetaException {
        if (ObjectStore.isSchemaVerified.get()) {
            return;
        }
        this.checkSchema();
    }
    
    public static void setSchemaVerified(final boolean val) {
        ObjectStore.isSchemaVerified.set(val);
    }
    
    private synchronized void checkSchema() throws MetaException {
        if (ObjectStore.isSchemaVerified.get()) {
            return;
        }
        final boolean strictValidation = HiveConf.getBoolVar(this.getConf(), HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION);
        final String schemaVer = this.getMetaStoreSchemaVersion();
        if (schemaVer == null) {
            if (strictValidation) {
                throw new MetaException("Version information not found in metastore. ");
            }
            ObjectStore.LOG.warn("Version information not found in metastore. " + HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION.toString() + " is not enabled so recording the schema version " + MetaStoreSchemaInfo.getHiveSchemaVersion());
            this.setMetaStoreSchemaVersion(MetaStoreSchemaInfo.getHiveSchemaVersion(), "Set by MetaStore " + ObjectStore.USER + "@" + ObjectStore.HOSTNAME);
        }
        else if (schemaVer.equalsIgnoreCase(MetaStoreSchemaInfo.getHiveSchemaVersion())) {
            ObjectStore.LOG.debug("Found expected HMS version of " + schemaVer);
        }
        else {
            if (strictValidation) {
                throw new MetaException("Hive Schema version " + MetaStoreSchemaInfo.getHiveSchemaVersion() + " does not match metastore's schema version " + schemaVer + " Metastore is not upgraded or corrupt");
            }
            ObjectStore.LOG.error("Version information found in metastore differs " + schemaVer + " from expected schema version " + MetaStoreSchemaInfo.getHiveSchemaVersion() + ". Schema verififcation is disabled " + HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION + " so setting version.");
            this.setMetaStoreSchemaVersion(MetaStoreSchemaInfo.getHiveSchemaVersion(), "Set by MetaStore " + ObjectStore.USER + "@" + ObjectStore.HOSTNAME);
        }
        ObjectStore.isSchemaVerified.set(true);
    }
    
    @Override
    public String getMetaStoreSchemaVersion() throws MetaException {
        MVersionTable mSchemaVer;
        try {
            mSchemaVer = this.getMSchemaVersion();
        }
        catch (NoSuchObjectException e) {
            return null;
        }
        return mSchemaVer.getSchemaVersion();
    }
    
    private MVersionTable getMSchemaVersion() throws NoSuchObjectException, MetaException {
        boolean committed = false;
        List<MVersionTable> mVerTables = new ArrayList<MVersionTable>();
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MVersionTable.class);
            try {
                mVerTables = (List<MVersionTable>)query.execute();
                this.pm.retrieveAll(mVerTables);
            }
            catch (JDODataStoreException e) {
                if (e.getCause() instanceof MissingTableException) {
                    throw new MetaException("Version table not found. The metastore is not upgraded to " + MetaStoreSchemaInfo.getHiveSchemaVersion());
                }
                throw e;
            }
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
        if (mVerTables.isEmpty()) {
            throw new NoSuchObjectException("No matching version found");
        }
        if (mVerTables.size() > 1) {
            String msg = "Metastore contains multiple versions (" + mVerTables.size() + ") ";
            for (final MVersionTable version : mVerTables) {
                msg = msg + "[ version = " + version.getSchemaVersion() + ", comment = " + version.getVersionComment() + " ] ";
            }
            throw new MetaException(msg.trim());
        }
        return mVerTables.get(0);
    }
    
    @Override
    public void setMetaStoreSchemaVersion(final String schemaVersion, final String comment) throws MetaException {
        boolean commited = false;
        final boolean recordVersion = HiveConf.getBoolVar(this.getConf(), HiveConf.ConfVars.METASTORE_SCHEMA_VERIFICATION_RECORD_VERSION);
        if (!recordVersion) {
            ObjectStore.LOG.warn("setMetaStoreSchemaVersion called but recording version is disabled: version = " + schemaVersion + ", comment = " + comment);
            return;
        }
        MVersionTable mSchemaVer;
        try {
            mSchemaVer = this.getMSchemaVersion();
        }
        catch (NoSuchObjectException e) {
            mSchemaVer = new MVersionTable();
        }
        mSchemaVer.setSchemaVersion(schemaVersion);
        mSchemaVer.setVersionComment(comment);
        try {
            this.openTransaction();
            this.pm.makePersistent(mSchemaVer);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public boolean doesPartitionExist(String dbName, String tableName, final List<String> partVals) throws MetaException {
        boolean success = false;
        try {
            this.openTransaction();
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            tableName = HiveStringUtils.normalizeIdentifier(tableName);
            final MTable mtbl = this.getMTable(dbName, tableName);
            if (mtbl == null) {
                success = this.commitTransaction();
                return false;
            }
            final Query query = this.pm.newQuery("select partitionName from org.apache.hadoop.hive.metastore.model.MPartition where table.tableName == t1 && table.database.name == t2 && partitionName == t3");
            query.declareParameters("java.lang.String t1, java.lang.String t2, java.lang.String t3");
            query.setUnique(true);
            query.setResult("partitionName");
            final String name = Warehouse.makePartName(this.convertToFieldSchemas(mtbl.getPartitionKeys()), partVals);
            final String result = (String)query.execute(tableName, dbName, name);
            success = this.commitTransaction();
            return result != null;
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private void debugLog(final String message) {
        if (ObjectStore.LOG.isDebugEnabled()) {
            ObjectStore.LOG.debug(message + this.getCallStack());
        }
    }
    
    private String getCallStack() {
        final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        final int thislimit = Math.min(5, stackTrace.length);
        final StringBuilder sb = new StringBuilder();
        sb.append(" at:");
        for (int i = 4; i < thislimit; ++i) {
            sb.append("\n\t");
            sb.append(stackTrace[i].toString());
        }
        return sb.toString();
    }
    
    private Function convertToFunction(final MFunction mfunc) {
        if (mfunc == null) {
            return null;
        }
        final Function func = new Function(mfunc.getFunctionName(), mfunc.getDatabase().getName(), mfunc.getClassName(), mfunc.getOwnerName(), PrincipalType.valueOf(mfunc.getOwnerType()), mfunc.getCreateTime(), FunctionType.findByValue(mfunc.getFunctionType()), this.convertToResourceUriList(mfunc.getResourceUris()));
        return func;
    }
    
    private MFunction convertToMFunction(final Function func) throws InvalidObjectException {
        if (func == null) {
            return null;
        }
        MDatabase mdb = null;
        try {
            mdb = this.getMDatabase(func.getDbName());
        }
        catch (NoSuchObjectException e) {
            ObjectStore.LOG.error(org.apache.hadoop.util.StringUtils.stringifyException(e));
            throw new InvalidObjectException("Database " + func.getDbName() + " doesn't exist.");
        }
        final MFunction mfunc = new MFunction(func.getFunctionName(), mdb, func.getClassName(), func.getOwnerName(), func.getOwnerType().name(), func.getCreateTime(), func.getFunctionType().getValue(), this.convertToMResourceUriList(func.getResourceUris()));
        return mfunc;
    }
    
    private List<ResourceUri> convertToResourceUriList(final List<MResourceUri> mresourceUriList) {
        List<ResourceUri> resourceUriList = null;
        if (mresourceUriList != null) {
            resourceUriList = new ArrayList<ResourceUri>(mresourceUriList.size());
            for (final MResourceUri mres : mresourceUriList) {
                resourceUriList.add(new ResourceUri(ResourceType.findByValue(mres.getResourceType()), mres.getUri()));
            }
        }
        return resourceUriList;
    }
    
    private List<MResourceUri> convertToMResourceUriList(final List<ResourceUri> resourceUriList) {
        List<MResourceUri> mresourceUriList = null;
        if (resourceUriList != null) {
            mresourceUriList = new ArrayList<MResourceUri>(resourceUriList.size());
            for (final ResourceUri res : resourceUriList) {
                mresourceUriList.add(new MResourceUri(res.getResourceType().getValue(), res.getUri()));
            }
        }
        return mresourceUriList;
    }
    
    @Override
    public void createFunction(final Function func) throws InvalidObjectException, MetaException {
        boolean committed = false;
        try {
            this.openTransaction();
            final MFunction mfunc = this.convertToMFunction(func);
            this.pm.makePersistent(mfunc);
            committed = this.commitTransaction();
        }
        finally {
            if (!committed) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public void alterFunction(String dbName, String funcName, final Function newFunction) throws InvalidObjectException, MetaException {
        boolean success = false;
        try {
            this.openTransaction();
            funcName = HiveStringUtils.normalizeIdentifier(funcName);
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            final MFunction newf = this.convertToMFunction(newFunction);
            if (newf == null) {
                throw new InvalidObjectException("new function is invalid");
            }
            final MFunction oldf = this.getMFunction(dbName, funcName);
            if (oldf == null) {
                throw new MetaException("function " + funcName + " doesn't exist");
            }
            oldf.setFunctionName(HiveStringUtils.normalizeIdentifier(newf.getFunctionName()));
            oldf.setDatabase(newf.getDatabase());
            oldf.setOwnerName(newf.getOwnerName());
            oldf.setOwnerType(newf.getOwnerType());
            oldf.setClassName(newf.getClassName());
            oldf.setFunctionType(newf.getFunctionType());
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public void dropFunction(final String dbName, final String funcName) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException {
        boolean success = false;
        try {
            this.openTransaction();
            final MFunction mfunc = this.getMFunction(dbName, funcName);
            this.pm.retrieve(mfunc);
            if (mfunc != null) {
                this.pm.deletePersistentAll(mfunc);
            }
            success = this.commitTransaction();
        }
        finally {
            if (!success) {
                this.rollbackTransaction();
            }
        }
    }
    
    private MFunction getMFunction(String db, String function) {
        MFunction mfunc = null;
        boolean commited = false;
        try {
            this.openTransaction();
            db = HiveStringUtils.normalizeIdentifier(db);
            function = HiveStringUtils.normalizeIdentifier(function);
            final Query query = this.pm.newQuery(MFunction.class, "functionName == function && database.name == db");
            query.declareParameters("java.lang.String function, java.lang.String db");
            query.setUnique(true);
            mfunc = (MFunction)query.execute(function, db);
            this.pm.retrieve(mfunc);
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return mfunc;
    }
    
    @Override
    public Function getFunction(final String dbName, final String funcName) throws MetaException {
        boolean commited = false;
        Function func = null;
        try {
            this.openTransaction();
            func = this.convertToFunction(this.getMFunction(dbName, funcName));
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return func;
    }
    
    @Override
    public List<String> getFunctions(String dbName, final String pattern) throws MetaException {
        boolean commited = false;
        List<String> funcs = null;
        try {
            this.openTransaction();
            dbName = HiveStringUtils.normalizeIdentifier(dbName);
            final String[] subpatterns = pattern.trim().split("\\|");
            String query = "select functionName from org.apache.hadoop.hive.metastore.model.MFunction where database.name == dbName && (";
            boolean first = true;
            for (String subpattern : subpatterns) {
                subpattern = "(?i)" + subpattern.replaceAll("\\*", ".*");
                if (!first) {
                    query += " || ";
                }
                query = query + " functionName.matches(\"" + subpattern + "\")";
                first = false;
            }
            query += ")";
            final Query q = this.pm.newQuery(query);
            q.declareParameters("java.lang.String dbName");
            q.setResult("functionName");
            q.setOrdering("functionName ascending");
            final Collection names = (Collection)q.execute(dbName);
            funcs = new ArrayList<String>();
            final Iterator i = names.iterator();
            while (i.hasNext()) {
                funcs.add(i.next());
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
        return funcs;
    }
    
    @Override
    public NotificationEventResponse getNextNotification(final NotificationEventRequest rqst) {
        boolean commited = false;
        try {
            this.openTransaction();
            final long lastEvent = rqst.getLastEvent();
            final Query query = this.pm.newQuery(MNotificationLog.class, "eventId > lastEvent");
            query.declareParameters("java.lang.Long lastEvent");
            query.setOrdering("eventId ascending");
            final Collection<MNotificationLog> events = (Collection<MNotificationLog>)query.execute(lastEvent);
            commited = this.commitTransaction();
            if (events == null) {
                return null;
            }
            final Iterator<MNotificationLog> i = events.iterator();
            final NotificationEventResponse result = new NotificationEventResponse();
            result.setEvents(new ArrayList<NotificationEvent>());
            final int maxEvents = (rqst.getMaxEvents() > 0) ? rqst.getMaxEvents() : Integer.MAX_VALUE;
            int numEvents = 0;
            while (i.hasNext() && numEvents++ < maxEvents) {
                result.addToEvents(this.translateDbToThrift(i.next()));
            }
            return result;
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
                return null;
            }
        }
    }
    
    @Override
    public void addNotificationEvent(final NotificationEvent entry) {
        boolean commited = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MNotificationNextId.class);
            final Collection<MNotificationNextId> ids = (Collection<MNotificationNextId>)query.execute();
            MNotificationNextId id = null;
            boolean needToPersistId;
            if (ids == null || ids.size() == 0) {
                id = new MNotificationNextId(1L);
                needToPersistId = true;
            }
            else {
                id = ids.iterator().next();
                needToPersistId = false;
            }
            entry.setEventId(id.getNextEventId());
            id.incrementEventId();
            if (needToPersistId) {
                this.pm.makePersistent(id);
            }
            this.pm.makePersistent(this.translateThriftToDb(entry));
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public void cleanNotificationEvents(final int olderThan) {
        boolean commited = false;
        try {
            this.openTransaction();
            final long tmp = System.currentTimeMillis() / 1000L - olderThan;
            final int tooOld = (tmp > 2147483647L) ? 0 : ((int)tmp);
            final Query query = this.pm.newQuery(MNotificationLog.class, "eventTime < tooOld");
            query.declareParameters("java.lang.Integer tooOld");
            final Collection<MNotificationLog> toBeRemoved = (Collection<MNotificationLog>)query.execute(tooOld);
            if (toBeRemoved != null && toBeRemoved.size() > 0) {
                this.pm.deletePersistentAll(toBeRemoved);
            }
            commited = this.commitTransaction();
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    @Override
    public CurrentNotificationEventId getCurrentNotificationEventId() {
        boolean commited = false;
        try {
            this.openTransaction();
            final Query query = this.pm.newQuery(MNotificationNextId.class);
            final Collection<MNotificationNextId> ids = (Collection<MNotificationNextId>)query.execute();
            long id = 0L;
            if (ids != null && ids.size() > 0) {
                id = ids.iterator().next().getNextEventId() - 1L;
            }
            commited = this.commitTransaction();
            return new CurrentNotificationEventId(id);
        }
        finally {
            if (!commited) {
                this.rollbackTransaction();
            }
        }
    }
    
    private MNotificationLog translateThriftToDb(final NotificationEvent entry) {
        final MNotificationLog dbEntry = new MNotificationLog();
        dbEntry.setEventId(entry.getEventId());
        dbEntry.setEventTime(entry.getEventTime());
        dbEntry.setEventType(entry.getEventType());
        dbEntry.setDbName(entry.getDbName());
        dbEntry.setTableName(entry.getTableName());
        dbEntry.setMessage(entry.getMessage());
        return dbEntry;
    }
    
    private NotificationEvent translateDbToThrift(final MNotificationLog dbEvent) {
        final NotificationEvent event = new NotificationEvent();
        event.setEventId(dbEvent.getEventId());
        event.setEventTime(dbEvent.getEventTime());
        event.setEventType(dbEvent.getEventType());
        event.setDbName(dbEvent.getDbName());
        event.setTableName(dbEvent.getTableName());
        event.setMessage(dbEvent.getMessage());
        return event;
    }
    
    static {
        ObjectStore.prop = null;
        ObjectStore.pmf = null;
        ObjectStore.pmfPropLock = new ReentrantLock();
        isSchemaVerified = new AtomicBoolean(false);
        LOG = LogFactory.getLog(ObjectStore.class.getName());
        final Map<String, Class> map = new HashMap<String, Class>();
        map.put("table", MTable.class);
        map.put("storagedescriptor", MStorageDescriptor.class);
        map.put("serdeinfo", MSerDeInfo.class);
        map.put("partition", MPartition.class);
        map.put("database", MDatabase.class);
        map.put("type", MType.class);
        map.put("fieldschema", MFieldSchema.class);
        map.put("order", MOrder.class);
        PINCLASSMAP = Collections.unmodifiableMap((Map<? extends String, ? extends Class>)map);
        String hostname = "UNKNOWN";
        try {
            final InetAddress clientAddr = InetAddress.getLocalHost();
            hostname = clientAddr.getHostAddress();
        }
        catch (IOException ex) {}
        HOSTNAME = hostname;
        final String user = System.getenv("USER");
        if (user == null) {
            USER = "UNKNOWN";
        }
        else {
            USER = user;
        }
    }
    
    private enum TXN_STATUS
    {
        NO_STATE, 
        OPEN, 
        COMMITED, 
        ROLLBACK;
    }
    
    private class LikeChecker extends ExpressionTree.TreeVisitor
    {
        private boolean hasLike;
        
        public boolean hasLike() {
            return this.hasLike;
        }
        
        @Override
        protected boolean shouldStop() {
            return this.hasLike;
        }
        
        @Override
        protected void visit(final ExpressionTree.LeafNode node) throws MetaException {
            this.hasLike = (this.hasLike || node.operator == ExpressionTree.Operator.LIKE);
        }
    }
    
    private static class Out<T>
    {
        public T val;
    }
    
    private abstract class GetHelper<T>
    {
        private final boolean isInTxn;
        private final boolean doTrace;
        private final boolean allowJdo;
        private boolean doUseDirectSql;
        private long start;
        private Table table;
        protected final String dbName;
        protected final String tblName;
        private boolean success;
        protected T results;
        
        public GetHelper(final String dbName, final String tblName, final boolean allowSql, final boolean allowJdo) throws MetaException {
            this.success = false;
            this.results = null;
            assert allowSql || allowJdo;
            this.allowJdo = allowJdo;
            this.dbName = HiveStringUtils.normalizeIdentifier(dbName);
            if (tblName != null) {
                this.tblName = HiveStringUtils.normalizeIdentifier(tblName);
            }
            else {
                this.tblName = null;
                this.table = null;
            }
            this.doTrace = ObjectStore.LOG.isDebugEnabled();
            this.isInTxn = ObjectStore.this.isActiveTransaction();
            final boolean isConfigEnabled = HiveConf.getBoolVar(ObjectStore.this.getConf(), HiveConf.ConfVars.METASTORE_TRY_DIRECT_SQL) && (HiveConf.getBoolVar(ObjectStore.this.getConf(), HiveConf.ConfVars.METASTORE_TRY_DIRECT_SQL_DDL) || !this.isInTxn);
            if (!allowJdo && isConfigEnabled && !ObjectStore.this.directSql.isCompatibleDatastore()) {
                throw new MetaException("SQL is not operational");
            }
            this.doUseDirectSql = (allowSql && isConfigEnabled && ObjectStore.this.directSql.isCompatibleDatastore());
        }
        
        protected abstract String describeResult();
        
        protected abstract T getSqlResult(final GetHelper<T> p0) throws MetaException;
        
        protected abstract T getJdoResult(final GetHelper<T> p0) throws MetaException, NoSuchObjectException;
        
        public T run(final boolean initTable) throws MetaException, NoSuchObjectException {
            try {
                this.start(initTable);
                if (this.doUseDirectSql) {
                    try {
                        this.setResult(this.getSqlResult(this));
                    }
                    catch (Exception ex) {
                        this.handleDirectSqlError(ex);
                    }
                }
                if (!this.doUseDirectSql) {
                    this.setResult(this.getJdoResult(this));
                }
                return this.commit();
            }
            catch (NoSuchObjectException ex2) {
                throw ex2;
            }
            catch (MetaException ex3) {
                throw ex3;
            }
            catch (Exception ex) {
                ObjectStore.LOG.error("", ex);
                throw new MetaException(ex.getMessage());
            }
            finally {
                this.close();
            }
        }
        
        private void start(final boolean initTable) throws MetaException, NoSuchObjectException {
            this.start = (this.doTrace ? System.nanoTime() : 0L);
            ObjectStore.this.openTransaction();
            if (initTable && this.tblName != null) {
                this.table = ObjectStore.this.ensureGetTable(this.dbName, this.tblName);
            }
        }
        
        private boolean setResult(final T results) {
            this.results = results;
            return this.results != null;
        }
        
        private void handleDirectSqlError(final Exception ex) throws MetaException, NoSuchObjectException {
            ObjectStore.LOG.warn("Direct SQL failed" + (this.allowJdo ? ", falling back to ORM" : ""), ex);
            if (this.allowJdo) {
                if (!this.isInTxn) {
                    ObjectStore.this.rollbackTransaction();
                    this.start = (this.doTrace ? System.nanoTime() : 0L);
                    ObjectStore.this.openTransaction();
                    if (this.table != null) {
                        this.table = ObjectStore.this.ensureGetTable(this.dbName, this.tblName);
                    }
                }
                else {
                    this.start = (this.doTrace ? System.nanoTime() : 0L);
                }
                this.doUseDirectSql = false;
                return;
            }
            if (ex instanceof MetaException) {
                throw (MetaException)ex;
            }
            throw new MetaException(ex.getMessage());
        }
        
        public void disableDirectSql() {
            this.doUseDirectSql = false;
        }
        
        private T commit() {
            this.success = ObjectStore.this.commitTransaction();
            if (this.doTrace) {
                ObjectStore.LOG.debug(this.describeResult() + " retrieved using " + (this.doUseDirectSql ? "SQL" : "ORM") + " in " + (System.nanoTime() - this.start) / 1000000.0 + "ms");
            }
            return this.results;
        }
        
        private void close() {
            if (!this.success) {
                ObjectStore.this.rollbackTransaction();
            }
        }
        
        public Table getTable() {
            return this.table;
        }
    }
    
    private abstract class GetListHelper<T> extends GetHelper<List<T>>
    {
        public GetListHelper(final String dbName, final String tblName, final boolean allowSql, final boolean allowJdo) throws MetaException {
            super(dbName, tblName, allowSql, allowJdo);
        }
        
        @Override
        protected String describeResult() {
            return ((List)this.results).size() + " entries";
        }
    }
    
    private abstract class GetDbHelper extends GetHelper<Database>
    {
        public GetDbHelper(final String dbName, final String tblName, final boolean allowSql, final boolean allowJdo) throws MetaException {
            super(dbName, null, allowSql, allowJdo);
        }
        
        @Override
        protected String describeResult() {
            return "db details for db " + this.dbName;
        }
    }
    
    private abstract class GetStatHelper extends GetHelper<ColumnStatistics>
    {
        public GetStatHelper(final String dbName, final String tblName, final boolean allowSql, final boolean allowJdo) throws MetaException {
            super(dbName, tblName, allowSql, allowJdo);
        }
        
        @Override
        protected String describeResult() {
            return "statistics for " + ((this.results == null) ? 0 : ((ColumnStatistics)this.results).getStatsObjSize()) + " columns";
        }
    }
    
    public class UpdateMDatabaseURIRetVal
    {
        private List<String> badRecords;
        private Map<String, String> updateLocations;
        
        UpdateMDatabaseURIRetVal(final List<String> badRecords, final Map<String, String> updateLocations) {
            this.badRecords = badRecords;
            this.updateLocations = updateLocations;
        }
        
        public List<String> getBadRecords() {
            return this.badRecords;
        }
        
        public void setBadRecords(final List<String> badRecords) {
            this.badRecords = badRecords;
        }
        
        public Map<String, String> getUpdateLocations() {
            return this.updateLocations;
        }
        
        public void setUpdateLocations(final Map<String, String> updateLocations) {
            this.updateLocations = updateLocations;
        }
    }
    
    public class UpdatePropURIRetVal
    {
        private List<String> badRecords;
        private Map<String, String> updateLocations;
        
        UpdatePropURIRetVal(final List<String> badRecords, final Map<String, String> updateLocations) {
            this.badRecords = badRecords;
            this.updateLocations = updateLocations;
        }
        
        public List<String> getBadRecords() {
            return this.badRecords;
        }
        
        public void setBadRecords(final List<String> badRecords) {
            this.badRecords = badRecords;
        }
        
        public Map<String, String> getUpdateLocations() {
            return this.updateLocations;
        }
        
        public void setUpdateLocations(final Map<String, String> updateLocations) {
            this.updateLocations = updateLocations;
        }
    }
    
    public class UpdateMStorageDescriptorTblURIRetVal
    {
        private List<String> badRecords;
        private Map<String, String> updateLocations;
        
        UpdateMStorageDescriptorTblURIRetVal(final List<String> badRecords, final Map<String, String> updateLocations) {
            this.badRecords = badRecords;
            this.updateLocations = updateLocations;
        }
        
        public List<String> getBadRecords() {
            return this.badRecords;
        }
        
        public void setBadRecords(final List<String> badRecords) {
            this.badRecords = badRecords;
        }
        
        public Map<String, String> getUpdateLocations() {
            return this.updateLocations;
        }
        
        public void setUpdateLocations(final Map<String, String> updateLocations) {
            this.updateLocations = updateLocations;
        }
    }
    
    public class UpdateSerdeURIRetVal
    {
        private List<String> badRecords;
        private Map<String, String> updateLocations;
        
        UpdateSerdeURIRetVal(final List<String> badRecords, final Map<String, String> updateLocations) {
            this.badRecords = badRecords;
            this.updateLocations = updateLocations;
        }
        
        public List<String> getBadRecords() {
            return this.badRecords;
        }
        
        public void setBadRecords(final List<String> badRecords) {
            this.badRecords = badRecords;
        }
        
        public Map<String, String> getUpdateLocations() {
            return this.updateLocations;
        }
        
        public void setUpdateLocations(final Map<String, String> updateLocations) {
            this.updateLocations = updateLocations;
        }
    }
}
