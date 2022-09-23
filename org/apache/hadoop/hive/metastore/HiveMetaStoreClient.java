// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.lang.reflect.Method;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.UnknownPartitionException;
import org.apache.hadoop.hive.metastore.api.InvalidPartitionException;
import org.apache.hadoop.hive.metastore.api.PartitionEventType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import org.apache.hadoop.hive.metastore.api.FireEventResponse;
import org.apache.hadoop.hive.metastore.api.FireEventRequest;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.api.NotificationEvent;
import org.apache.hadoop.hive.metastore.api.NotificationEventRequest;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.AddDynamicPartitions;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.CompactionRequest;
import org.apache.hadoop.hive.metastore.api.CompactionType;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeRequest;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeResponse;
import org.apache.hadoop.hive.metastore.api.HeartbeatRequest;
import org.apache.hadoop.hive.metastore.api.ShowLocksRequest;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponse;
import org.apache.hadoop.hive.metastore.api.TxnOpenException;
import org.apache.hadoop.hive.metastore.api.UnlockRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchLockException;
import org.apache.hadoop.hive.metastore.api.CheckLockRequest;
import org.apache.hadoop.hive.metastore.api.LockResponse;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsInfoResponse;
import org.apache.hadoop.hive.metastore.api.TxnAbortedException;
import org.apache.hadoop.hive.metastore.api.CommitTxnRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchTxnException;
import org.apache.hadoop.hive.metastore.api.AbortTxnRequest;
import org.apache.hadoop.hive.metastore.api.OpenTxnRequest;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.hadoop.hive.metastore.api.OpenTxnsResponse;
import org.apache.hadoop.hive.metastore.txn.TxnHandler;
import org.apache.hadoop.hive.common.ValidTxnList;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.GrantRevokePrivilegeResponse;
import org.apache.hadoop.hive.metastore.api.GrantRevokePrivilegeRequest;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalResponse;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalRequest;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleResponse;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleRequest;
import org.apache.hadoop.hive.metastore.api.Role;
import org.apache.hadoop.hive.metastore.api.GrantRevokeRoleResponse;
import org.apache.hadoop.hive.metastore.api.GrantRevokeType;
import org.apache.hadoop.hive.metastore.api.GrantRevokeRoleRequest;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.PrincipalPrivilegeSet;
import org.apache.hadoop.hive.metastore.api.Function;
import org.apache.hadoop.hive.metastore.api.ConfigValSecurityException;
import org.apache.hadoop.hive.metastore.api.PartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.TableStatsRequest;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.SetPartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.api.PartitionsByExprResult;
import java.util.Collection;
import org.apache.thrift.TApplicationException;
import org.apache.hadoop.hive.metastore.api.PartitionsByExprRequest;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import org.apache.hadoop.hive.metastore.api.UnknownTableException;
import org.apache.hadoop.hive.metastore.api.DropPartitionsRequest;
import org.apache.hadoop.hive.metastore.api.DropPartitionsExpr;
import org.apache.hadoop.hive.metastore.api.RequestPartsSpec;
import org.apache.hadoop.hive.common.ObjectPair;
import java.util.Iterator;
import org.apache.hadoop.hive.metastore.api.Type;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.api.AddPartitionsResult;
import org.apache.hadoop.hive.metastore.api.AddPartitionsRequest;
import java.util.ArrayList;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import java.util.HashMap;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.thrift.protocol.TProtocol;
import org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge;
import org.apache.hadoop.util.StringUtils;
import javax.security.auth.login.LoginException;
import java.util.Arrays;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TFramedTransport;
import java.io.IOException;
import org.apache.hadoop.hive.shims.Utils;
import org.apache.hadoop.hive.shims.ShimLoader;
import org.apache.thrift.transport.TSocket;
import org.apache.hadoop.hive.metastore.api.Partition;
import java.util.List;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.api.EnvironmentContext;
import org.apache.hadoop.hive.metastore.api.Table;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConfUtil;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.commons.logging.Log;
import java.util.Map;
import org.apache.hadoop.hive.conf.HiveConf;
import java.net.URI;
import org.apache.thrift.transport.TTransport;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class HiveMetaStoreClient implements IMetaStoreClient
{
    ThriftHiveMetastore.Iface client;
    private TTransport transport;
    private boolean isConnected;
    private URI[] metastoreUris;
    private final HiveMetaHookLoader hookLoader;
    protected final HiveConf conf;
    private String tokenStrForm;
    private final boolean localMetaStore;
    private final MetaStoreFilterHook filterHook;
    private Map<String, String> currentMetaVars;
    private int retries;
    private long retryDelaySeconds;
    protected static final Log LOG;
    
    public HiveMetaStoreClient(final HiveConf conf) throws MetaException {
        this(conf, null);
    }
    
    public HiveMetaStoreClient(HiveConf conf, final HiveMetaHookLoader hookLoader) throws MetaException {
        this.client = null;
        this.transport = null;
        this.isConnected = false;
        this.retries = 5;
        this.retryDelaySeconds = 0L;
        this.hookLoader = hookLoader;
        if (conf == null) {
            conf = new HiveConf(HiveMetaStoreClient.class);
        }
        this.conf = conf;
        this.filterHook = this.loadFilterHooks();
        final String msUri = conf.getVar(HiveConf.ConfVars.METASTOREURIS);
        this.localMetaStore = HiveConfUtil.isEmbeddedMetaStore(msUri);
        if (this.localMetaStore) {
            this.client = HiveMetaStore.newRetryingHMSHandler("hive client", conf, true);
            this.isConnected = true;
            this.snapshotActiveConf();
            return;
        }
        this.retries = HiveConf.getIntVar(conf, HiveConf.ConfVars.METASTORETHRIFTCONNECTIONRETRIES);
        this.retryDelaySeconds = conf.getTimeVar(HiveConf.ConfVars.METASTORE_CLIENT_CONNECT_RETRY_DELAY, TimeUnit.SECONDS);
        if (conf.getVar(HiveConf.ConfVars.METASTOREURIS) != null) {
            final String[] metastoreUrisString = conf.getVar(HiveConf.ConfVars.METASTOREURIS).split(",");
            this.metastoreUris = new URI[metastoreUrisString.length];
            try {
                int i = 0;
                for (final String s : metastoreUrisString) {
                    final URI tmpUri = new URI(s);
                    if (tmpUri.getScheme() == null) {
                        throw new IllegalArgumentException("URI: " + s + " does not have a scheme");
                    }
                    this.metastoreUris[i++] = tmpUri;
                }
            }
            catch (IllegalArgumentException e) {
                throw e;
            }
            catch (Exception e2) {
                MetaStoreUtils.logAndThrowMetaException(e2);
            }
            this.open();
            return;
        }
        HiveMetaStoreClient.LOG.error("NOT getting uris from conf");
        throw new MetaException("MetaStoreURIs not found in conf file");
    }
    
    private MetaStoreFilterHook loadFilterHooks() throws IllegalStateException {
        final Class<? extends MetaStoreFilterHook> authProviderClass = this.conf.getClass(HiveConf.ConfVars.METASTORE_FILTER_HOOK.varname, DefaultMetaStoreFilterHookImpl.class, MetaStoreFilterHook.class);
        final String msg = "Unable to create instance of " + authProviderClass.getName() + ": ";
        try {
            final Constructor<? extends MetaStoreFilterHook> constructor = authProviderClass.getConstructor(HiveConf.class);
            return (MetaStoreFilterHook)constructor.newInstance(this.conf);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalStateException(msg + e.getMessage(), e);
        }
        catch (SecurityException e2) {
            throw new IllegalStateException(msg + e2.getMessage(), e2);
        }
        catch (InstantiationException e3) {
            throw new IllegalStateException(msg + e3.getMessage(), e3);
        }
        catch (IllegalAccessException e4) {
            throw new IllegalStateException(msg + e4.getMessage(), e4);
        }
        catch (IllegalArgumentException e5) {
            throw new IllegalStateException(msg + e5.getMessage(), e5);
        }
        catch (InvocationTargetException e6) {
            throw new IllegalStateException(msg + e6.getMessage(), e6);
        }
    }
    
    private void promoteRandomMetaStoreURI() {
        if (this.metastoreUris.length <= 1) {
            return;
        }
        final Random rng = new Random();
        final int index = rng.nextInt(this.metastoreUris.length - 1) + 1;
        final URI tmp = this.metastoreUris[0];
        this.metastoreUris[0] = this.metastoreUris[index];
        this.metastoreUris[index] = tmp;
    }
    
    @Override
    public boolean isCompatibleWith(final HiveConf conf) {
        if (this.currentMetaVars == null) {
            return false;
        }
        boolean compatible = true;
        for (final HiveConf.ConfVars oneVar : HiveConf.metaVars) {
            final String oldVar = this.currentMetaVars.get(oneVar.varname);
            final String newVar = conf.get(oneVar.varname, "");
            Label_0156: {
                if (oldVar != null) {
                    if (oneVar.isCaseSensitive()) {
                        if (oldVar.equals(newVar)) {
                            break Label_0156;
                        }
                    }
                    else if (oldVar.equalsIgnoreCase(newVar)) {
                        break Label_0156;
                    }
                }
                HiveMetaStoreClient.LOG.info("Mestastore configuration " + oneVar.varname + " changed from " + oldVar + " to " + newVar);
                compatible = false;
            }
        }
        return compatible;
    }
    
    @Override
    public void setHiveAddedJars(final String addedJars) {
        HiveConf.setVar(this.conf, HiveConf.ConfVars.HIVEADDEDJARS, addedJars);
    }
    
    @Override
    public void reconnect() throws MetaException {
        if (this.localMetaStore) {
            throw new MetaException("For direct MetaStore DB connections, we don't support retries at the client level.");
        }
        this.close();
        this.promoteRandomMetaStoreURI();
        this.open();
    }
    
    @Override
    public void alter_table(final String dbname, final String tbl_name, final Table new_tbl) throws InvalidOperationException, MetaException, TException {
        this.alter_table(dbname, tbl_name, new_tbl, null);
    }
    
    @Override
    public void alter_table(final String dbname, final String tbl_name, final Table new_tbl, final boolean cascade) throws InvalidOperationException, MetaException, TException {
        this.client.alter_table_with_cascade(dbname, tbl_name, new_tbl, cascade);
    }
    
    public void alter_table(final String dbname, final String tbl_name, final Table new_tbl, final EnvironmentContext envContext) throws InvalidOperationException, MetaException, TException {
        this.client.alter_table_with_environment_context(dbname, tbl_name, new_tbl, envContext);
    }
    
    @Override
    public void renamePartition(final String dbname, final String name, final List<String> part_vals, final Partition newPart) throws InvalidOperationException, MetaException, TException {
        this.client.rename_partition(dbname, name, part_vals, newPart);
    }
    
    private void open() throws MetaException {
        this.isConnected = false;
        TTransportException tte = null;
        final boolean useSasl = this.conf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_SASL);
        final boolean useFramedTransport = this.conf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_FRAMED_TRANSPORT);
        final boolean useCompactProtocol = this.conf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_COMPACT_PROTOCOL);
        final int clientSocketTimeout = (int)this.conf.getTimeVar(HiveConf.ConfVars.METASTORE_CLIENT_SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
        for (int attempt = 0; !this.isConnected && attempt < this.retries; ++attempt) {
            for (final URI store : this.metastoreUris) {
                HiveMetaStoreClient.LOG.info("Trying to connect to metastore with URI " + store);
                try {
                    this.transport = new TSocket(store.getHost(), store.getPort(), clientSocketTimeout);
                    Label_0320: {
                        if (useSasl) {
                            try {
                                final HadoopThriftAuthBridge.Client authBridge = ShimLoader.getHadoopThriftAuthBridge().createClient();
                                final String tokenSig = this.conf.get("hive.metastore.token.signature");
                                this.tokenStrForm = Utils.getTokenStrForm(tokenSig);
                                if (this.tokenStrForm != null) {
                                    this.transport = authBridge.createClientTransport(null, store.getHost(), "DIGEST", this.tokenStrForm, this.transport, MetaStoreUtils.getMetaStoreSaslProperties(this.conf));
                                }
                                else {
                                    final String principalConfig = this.conf.getVar(HiveConf.ConfVars.METASTORE_KERBEROS_PRINCIPAL);
                                    this.transport = authBridge.createClientTransport(principalConfig, store.getHost(), "KERBEROS", null, this.transport, MetaStoreUtils.getMetaStoreSaslProperties(this.conf));
                                }
                                break Label_0320;
                            }
                            catch (IOException ioe) {
                                HiveMetaStoreClient.LOG.error("Couldn't create client transport", ioe);
                                throw new MetaException(ioe.toString());
                            }
                        }
                        if (useFramedTransport) {
                            this.transport = new TFramedTransport(this.transport);
                        }
                    }
                    TProtocol protocol;
                    if (useCompactProtocol) {
                        protocol = new TCompactProtocol(this.transport);
                    }
                    else {
                        protocol = new TBinaryProtocol(this.transport);
                    }
                    this.client = new ThriftHiveMetastore.Client(protocol);
                    try {
                        this.transport.open();
                        this.isConnected = true;
                    }
                    catch (TTransportException e) {
                        tte = e;
                        if (HiveMetaStoreClient.LOG.isDebugEnabled()) {
                            HiveMetaStoreClient.LOG.warn("Failed to connect to the MetaStore Server...", e);
                        }
                        else {
                            HiveMetaStoreClient.LOG.warn("Failed to connect to the MetaStore Server...");
                        }
                    }
                    if (this.isConnected && !useSasl && this.conf.getBoolVar(HiveConf.ConfVars.METASTORE_EXECUTE_SET_UGI)) {
                        try {
                            final UserGroupInformation ugi = Utils.getUGI();
                            this.client.set_ugi(ugi.getUserName(), Arrays.asList(ugi.getGroupNames()));
                        }
                        catch (LoginException e2) {
                            HiveMetaStoreClient.LOG.warn("Failed to do login. set_ugi() is not successful, Continuing without it.", e2);
                        }
                        catch (IOException e3) {
                            HiveMetaStoreClient.LOG.warn("Failed to find ugi of client set_ugi() is not successful, Continuing without it.", e3);
                        }
                        catch (TException e4) {
                            HiveMetaStoreClient.LOG.warn("set_ugi() not successful, Likely cause: new client talking to old server. Continuing without it.", e4);
                        }
                    }
                }
                catch (MetaException e5) {
                    HiveMetaStoreClient.LOG.error("Unable to connect to metastore with URI " + store + " in attempt " + attempt, e5);
                }
                if (this.isConnected) {
                    break;
                }
            }
            if (!this.isConnected && this.retryDelaySeconds > 0L) {
                try {
                    HiveMetaStoreClient.LOG.info("Waiting " + this.retryDelaySeconds + " seconds before next connection attempt.");
                    Thread.sleep(this.retryDelaySeconds * 1000L);
                }
                catch (InterruptedException ex) {}
            }
        }
        if (!this.isConnected) {
            throw new MetaException("Could not connect to meta store using any of the URIs provided. Most recent failure: " + StringUtils.stringifyException(tte));
        }
        this.snapshotActiveConf();
        HiveMetaStoreClient.LOG.info("Connected to metastore.");
    }
    
    private void snapshotActiveConf() {
        this.currentMetaVars = new HashMap<String, String>(HiveConf.metaVars.length);
        for (final HiveConf.ConfVars oneVar : HiveConf.metaVars) {
            this.currentMetaVars.put(oneVar.varname, this.conf.get(oneVar.varname, ""));
        }
    }
    
    @Override
    public String getTokenStrForm() throws IOException {
        return this.tokenStrForm;
    }
    
    @Override
    public void close() {
        this.isConnected = false;
        this.currentMetaVars = null;
        try {
            if (null != this.client) {
                this.client.shutdown();
            }
        }
        catch (TException e) {
            HiveMetaStoreClient.LOG.debug("Unable to shutdown metastore client. Will try closing transport directly.", e);
        }
        if (this.transport != null && this.transport.isOpen()) {
            this.transport.close();
        }
    }
    
    @Override
    public void setMetaConf(final String key, final String value) throws TException {
        this.client.setMetaConf(key, value);
    }
    
    @Override
    public String getMetaConf(final String key) throws TException {
        return this.client.getMetaConf(key);
    }
    
    @Override
    public Partition add_partition(final Partition new_part) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.add_partition(new_part, null);
    }
    
    public Partition add_partition(final Partition new_part, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.deepCopy(this.client.add_partition_with_environment_context(new_part, envContext));
    }
    
    @Override
    public int add_partitions(final List<Partition> new_parts) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.client.add_partitions(new_parts);
    }
    
    @Override
    public List<Partition> add_partitions(final List<Partition> parts, final boolean ifNotExists, final boolean needResults) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        if (parts.isEmpty()) {
            return needResults ? new ArrayList<Partition>() : null;
        }
        final Partition part = parts.get(0);
        final AddPartitionsRequest req = new AddPartitionsRequest(part.getDbName(), part.getTableName(), parts, ifNotExists);
        req.setNeedResult(needResults);
        final AddPartitionsResult result = this.client.add_partitions_req(req);
        return needResults ? this.filterHook.filterPartitions(result.getPartitions()) : null;
    }
    
    @Override
    public int add_partitions_pspec(final PartitionSpecProxy partitionSpec) throws TException {
        return this.client.add_partitions_pspec(partitionSpec.toPartitionSpec());
    }
    
    @Override
    public Partition appendPartition(final String db_name, final String table_name, final List<String> part_vals) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.appendPartition(db_name, table_name, part_vals, null);
    }
    
    public Partition appendPartition(final String db_name, final String table_name, final List<String> part_vals, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.deepCopy(this.client.append_partition_with_environment_context(db_name, table_name, part_vals, envContext));
    }
    
    @Override
    public Partition appendPartition(final String dbName, final String tableName, final String partName) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.appendPartition(dbName, tableName, partName, null);
    }
    
    public Partition appendPartition(final String dbName, final String tableName, final String partName, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.deepCopy(this.client.append_partition_by_name_with_environment_context(dbName, tableName, partName, envContext));
    }
    
    @Override
    public Partition exchange_partition(final Map<String, String> partitionSpecs, final String sourceDb, final String sourceTable, final String destDb, final String destinationTableName) throws MetaException, NoSuchObjectException, InvalidObjectException, TException {
        return this.client.exchange_partition(partitionSpecs, sourceDb, sourceTable, destDb, destinationTableName);
    }
    
    @Override
    public void validatePartitionNameCharacters(final List<String> partVals) throws TException, MetaException {
        this.client.partition_name_has_valid_characters(partVals, true);
    }
    
    @Override
    public void createDatabase(final Database db) throws AlreadyExistsException, InvalidObjectException, MetaException, TException {
        this.client.create_database(db);
    }
    
    @Override
    public void createTable(final Table tbl) throws AlreadyExistsException, InvalidObjectException, MetaException, NoSuchObjectException, TException {
        this.createTable(tbl, null);
    }
    
    public void createTable(final Table tbl, final EnvironmentContext envContext) throws AlreadyExistsException, InvalidObjectException, MetaException, NoSuchObjectException, TException {
        final HiveMetaHook hook = this.getHook(tbl);
        if (hook != null) {
            hook.preCreateTable(tbl);
        }
        boolean success = false;
        try {
            this.create_table_with_environment_context(tbl, envContext);
            if (hook != null) {
                hook.commitCreateTable(tbl);
            }
            success = true;
        }
        finally {
            if (!success && hook != null) {
                hook.rollbackCreateTable(tbl);
            }
        }
    }
    
    public boolean createType(final Type type) throws AlreadyExistsException, InvalidObjectException, MetaException, TException {
        return this.client.create_type(type);
    }
    
    @Override
    public void dropDatabase(final String name) throws NoSuchObjectException, InvalidOperationException, MetaException, TException {
        this.dropDatabase(name, true, false, false);
    }
    
    @Override
    public void dropDatabase(final String name, final boolean deleteData, final boolean ignoreUnknownDb) throws NoSuchObjectException, InvalidOperationException, MetaException, TException {
        this.dropDatabase(name, deleteData, ignoreUnknownDb, false);
    }
    
    @Override
    public void dropDatabase(final String name, final boolean deleteData, final boolean ignoreUnknownDb, final boolean cascade) throws NoSuchObjectException, InvalidOperationException, MetaException, TException {
        try {
            this.getDatabase(name);
        }
        catch (NoSuchObjectException e) {
            if (!ignoreUnknownDb) {
                throw e;
            }
            return;
        }
        if (cascade) {
            final List<String> tableList = this.getAllTables(name);
            for (final String table : tableList) {
                try {
                    this.dropTable(name, table, deleteData, true);
                }
                catch (UnsupportedOperationException ex) {}
            }
        }
        this.client.drop_database(name, deleteData, cascade);
    }
    
    public boolean dropPartition(final String db_name, final String tbl_name, final List<String> part_vals) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartition(db_name, tbl_name, part_vals, true, null);
    }
    
    public boolean dropPartition(final String db_name, final String tbl_name, final List<String> part_vals, final EnvironmentContext env_context) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartition(db_name, tbl_name, part_vals, true, env_context);
    }
    
    @Override
    public boolean dropPartition(final String dbName, final String tableName, final String partName, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartition(dbName, tableName, partName, deleteData, null);
    }
    
    private static EnvironmentContext getEnvironmentContextWithIfPurgeSet() {
        final Map<String, String> warehouseOptions = new HashMap<String, String>();
        warehouseOptions.put("ifPurge", "TRUE");
        return new EnvironmentContext(warehouseOptions);
    }
    
    public boolean dropPartition(final String dbName, final String tableName, final String partName, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException, TException {
        return this.client.drop_partition_by_name_with_environment_context(dbName, tableName, partName, deleteData, envContext);
    }
    
    @Override
    public boolean dropPartition(final String db_name, final String tbl_name, final List<String> part_vals, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartition(db_name, tbl_name, part_vals, deleteData, null);
    }
    
    @Override
    public boolean dropPartition(final String db_name, final String tbl_name, final List<String> part_vals, final PartitionDropOptions options) throws TException {
        return this.dropPartition(db_name, tbl_name, part_vals, options.deleteData, options.purgeData ? getEnvironmentContextWithIfPurgeSet() : null);
    }
    
    public boolean dropPartition(final String db_name, final String tbl_name, final List<String> part_vals, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException, TException {
        return this.client.drop_partition_with_environment_context(db_name, tbl_name, part_vals, deleteData, envContext);
    }
    
    @Override
    public List<Partition> dropPartitions(final String dbName, final String tblName, final List<ObjectPair<Integer, byte[]>> partExprs, final PartitionDropOptions options) throws TException {
        final RequestPartsSpec rps = new RequestPartsSpec();
        final List<DropPartitionsExpr> exprs = new ArrayList<DropPartitionsExpr>(partExprs.size());
        for (final ObjectPair<Integer, byte[]> partExpr : partExprs) {
            final DropPartitionsExpr dpe = new DropPartitionsExpr();
            dpe.setExpr(partExpr.getSecond());
            dpe.setPartArchiveLevel(partExpr.getFirst());
            exprs.add(dpe);
        }
        rps.setExprs(exprs);
        final DropPartitionsRequest req = new DropPartitionsRequest(dbName, tblName, rps);
        req.setDeleteData(options.deleteData);
        req.setIgnoreProtection(options.ignoreProtection);
        req.setNeedResult(options.returnResults);
        req.setIfExists(options.ifExists);
        if (options.purgeData) {
            HiveMetaStoreClient.LOG.info("Dropped partitions will be purged!");
            req.setEnvironmentContext(getEnvironmentContextWithIfPurgeSet());
        }
        return this.client.drop_partitions_req(req).getPartitions();
    }
    
    @Override
    public List<Partition> dropPartitions(final String dbName, final String tblName, final List<ObjectPair<Integer, byte[]>> partExprs, final boolean deleteData, final boolean ignoreProtection, final boolean ifExists, final boolean needResult) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartitions(dbName, tblName, partExprs, PartitionDropOptions.instance().deleteData(deleteData).ignoreProtection(ignoreProtection).ifExists(ifExists).returnResults(needResult));
    }
    
    @Override
    public List<Partition> dropPartitions(final String dbName, final String tblName, final List<ObjectPair<Integer, byte[]>> partExprs, final boolean deleteData, final boolean ignoreProtection, final boolean ifExists) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartitions(dbName, tblName, partExprs, PartitionDropOptions.instance().deleteData(deleteData).ignoreProtection(ignoreProtection).ifExists(ifExists));
    }
    
    @Override
    public void dropTable(final String dbname, final String name, final boolean deleteData, final boolean ignoreUnknownTab) throws MetaException, TException, NoSuchObjectException, UnsupportedOperationException {
        this.dropTable(dbname, name, deleteData, ignoreUnknownTab, null);
    }
    
    @Override
    public void dropTable(final String dbname, final String name, final boolean deleteData, final boolean ignoreUnknownTab, final boolean ifPurge) throws MetaException, TException, NoSuchObjectException, UnsupportedOperationException {
        EnvironmentContext envContext = null;
        if (ifPurge) {
            Map<String, String> warehouseOptions = null;
            warehouseOptions = new HashMap<String, String>();
            warehouseOptions.put("ifPurge", "TRUE");
            envContext = new EnvironmentContext(warehouseOptions);
        }
        this.dropTable(dbname, name, deleteData, ignoreUnknownTab, envContext);
    }
    
    @Deprecated
    @Override
    public void dropTable(final String tableName, final boolean deleteData) throws MetaException, UnknownTableException, TException, NoSuchObjectException {
        this.dropTable("default", tableName, deleteData, false, null);
    }
    
    @Override
    public void dropTable(final String dbname, final String name) throws NoSuchObjectException, MetaException, TException {
        this.dropTable(dbname, name, true, true, null);
    }
    
    public void dropTable(final String dbname, final String name, final boolean deleteData, final boolean ignoreUnknownTab, final EnvironmentContext envContext) throws MetaException, TException, NoSuchObjectException, UnsupportedOperationException {
        Table tbl;
        try {
            tbl = this.getTable(dbname, name);
        }
        catch (NoSuchObjectException e) {
            if (!ignoreUnknownTab) {
                throw e;
            }
            return;
        }
        if (MetaStoreUtils.isIndexTable(tbl)) {
            throw new UnsupportedOperationException("Cannot drop index tables");
        }
        final HiveMetaHook hook = this.getHook(tbl);
        if (hook != null) {
            hook.preDropTable(tbl);
        }
        boolean success = false;
        try {
            this.drop_table_with_environment_context(dbname, name, deleteData, envContext);
            if (hook != null) {
                hook.commitDropTable(tbl, deleteData);
            }
            success = true;
        }
        catch (NoSuchObjectException e2) {
            if (!ignoreUnknownTab) {
                throw e2;
            }
        }
        finally {
            if (!success && hook != null) {
                hook.rollbackDropTable(tbl);
            }
        }
    }
    
    public boolean dropType(final String type) throws NoSuchObjectException, MetaException, TException {
        return this.client.drop_type(type);
    }
    
    public Map<String, Type> getTypeAll(final String name) throws MetaException, TException {
        Map<String, Type> result = null;
        final Map<String, Type> fromClient = this.client.get_type_all(name);
        if (fromClient != null) {
            result = new LinkedHashMap<String, Type>();
            for (final String key : fromClient.keySet()) {
                result.put(key, this.deepCopy(fromClient.get(key)));
            }
        }
        return result;
    }
    
    @Override
    public List<String> getDatabases(final String databasePattern) throws MetaException {
        try {
            return this.filterHook.filterDatabases(this.client.get_databases(databasePattern));
        }
        catch (Exception e) {
            MetaStoreUtils.logAndThrowMetaException(e);
            return null;
        }
    }
    
    @Override
    public List<String> getAllDatabases() throws MetaException {
        try {
            return this.filterHook.filterDatabases(this.client.get_all_databases());
        }
        catch (Exception e) {
            MetaStoreUtils.logAndThrowMetaException(e);
            return null;
        }
    }
    
    @Override
    public List<Partition> listPartitions(final String db_name, final String tbl_name, final short max_parts) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopyPartitions(this.filterHook.filterPartitions(this.client.get_partitions(db_name, tbl_name, max_parts)));
    }
    
    @Override
    public PartitionSpecProxy listPartitionSpecs(final String dbName, final String tableName, final int maxParts) throws TException {
        return PartitionSpecProxy.Factory.get(this.filterHook.filterPartitionSpecs(this.client.get_partitions_pspec(dbName, tableName, maxParts)));
    }
    
    @Override
    public List<Partition> listPartitions(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopyPartitions(this.filterHook.filterPartitions(this.client.get_partitions_ps(db_name, tbl_name, part_vals, max_parts)));
    }
    
    @Override
    public List<Partition> listPartitionsWithAuthInfo(final String db_name, final String tbl_name, final short max_parts, final String user_name, final List<String> group_names) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopyPartitions(this.filterHook.filterPartitions(this.client.get_partitions_with_auth(db_name, tbl_name, max_parts, user_name, group_names)));
    }
    
    @Override
    public List<Partition> listPartitionsWithAuthInfo(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts, final String user_name, final List<String> group_names) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopyPartitions(this.filterHook.filterPartitions(this.client.get_partitions_ps_with_auth(db_name, tbl_name, part_vals, max_parts, user_name, group_names)));
    }
    
    @Override
    public List<Partition> listPartitionsByFilter(final String db_name, final String tbl_name, final String filter, final short max_parts) throws MetaException, NoSuchObjectException, TException {
        return this.deepCopyPartitions(this.filterHook.filterPartitions(this.client.get_partitions_by_filter(db_name, tbl_name, filter, max_parts)));
    }
    
    @Override
    public PartitionSpecProxy listPartitionSpecsByFilter(final String db_name, final String tbl_name, final String filter, final int max_parts) throws MetaException, NoSuchObjectException, TException {
        return PartitionSpecProxy.Factory.get(this.filterHook.filterPartitionSpecs(this.client.get_part_specs_by_filter(db_name, tbl_name, filter, max_parts)));
    }
    
    @Override
    public boolean listPartitionsByExpr(final String db_name, final String tbl_name, final byte[] expr, final String default_partition_name, final short max_parts, final List<Partition> result) throws TException {
        assert result != null;
        final PartitionsByExprRequest req = new PartitionsByExprRequest(db_name, tbl_name, ByteBuffer.wrap(expr));
        if (default_partition_name != null) {
            req.setDefaultPartitionName(default_partition_name);
        }
        if (max_parts >= 0) {
            req.setMaxParts(max_parts);
        }
        PartitionsByExprResult r = null;
        try {
            r = this.client.get_partitions_by_expr(req);
        }
        catch (TApplicationException te) {
            if (te.getType() != 1 && te.getType() != 3) {
                throw te;
            }
            throw new IncompatibleMetastoreException("Metastore doesn't support listPartitionsByExpr: " + te.getMessage());
        }
        r.setPartitions(this.filterHook.filterPartitions(r.getPartitions()));
        this.deepCopyPartitions(r.getPartitions(), result);
        return !r.isSetHasUnknownPartitions() || r.isHasUnknownPartitions();
    }
    
    @Override
    public Database getDatabase(final String name) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopy(this.filterHook.filterDatabase(this.client.get_database(name)));
    }
    
    @Override
    public Partition getPartition(final String db_name, final String tbl_name, final List<String> part_vals) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopy(this.filterHook.filterPartition(this.client.get_partition(db_name, tbl_name, part_vals)));
    }
    
    @Override
    public List<Partition> getPartitionsByNames(final String db_name, final String tbl_name, final List<String> part_names) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopyPartitions(this.filterHook.filterPartitions(this.client.get_partitions_by_names(db_name, tbl_name, part_names)));
    }
    
    @Override
    public Partition getPartitionWithAuthInfo(final String db_name, final String tbl_name, final List<String> part_vals, final String user_name, final List<String> group_names) throws MetaException, UnknownTableException, NoSuchObjectException, TException {
        return this.deepCopy(this.filterHook.filterPartition(this.client.get_partition_with_auth(db_name, tbl_name, part_vals, user_name, group_names)));
    }
    
    @Override
    public Table getTable(final String dbname, final String name) throws MetaException, TException, NoSuchObjectException {
        return this.deepCopy(this.filterHook.filterTable(this.client.get_table(dbname, name)));
    }
    
    @Deprecated
    @Override
    public Table getTable(final String tableName) throws MetaException, TException, NoSuchObjectException {
        return this.filterHook.filterTable(this.getTable("default", tableName));
    }
    
    @Override
    public List<Table> getTableObjectsByName(final String dbName, final List<String> tableNames) throws MetaException, InvalidOperationException, UnknownDBException, TException {
        return this.deepCopyTables(this.filterHook.filterTables(this.client.get_table_objects_by_name(dbName, tableNames)));
    }
    
    @Override
    public List<String> listTableNamesByFilter(final String dbName, final String filter, final short maxTables) throws MetaException, TException, InvalidOperationException, UnknownDBException {
        return this.filterHook.filterTableNames(dbName, this.client.get_table_names_by_filter(dbName, filter, maxTables));
    }
    
    public Type getType(final String name) throws NoSuchObjectException, MetaException, TException {
        return this.deepCopy(this.client.get_type(name));
    }
    
    @Override
    public List<String> getTables(final String dbname, final String tablePattern) throws MetaException {
        try {
            return this.filterHook.filterTableNames(dbname, this.client.get_tables(dbname, tablePattern));
        }
        catch (Exception e) {
            MetaStoreUtils.logAndThrowMetaException(e);
            return null;
        }
    }
    
    @Override
    public List<String> getAllTables(final String dbname) throws MetaException {
        try {
            return this.filterHook.filterTableNames(dbname, this.client.get_all_tables(dbname));
        }
        catch (Exception e) {
            MetaStoreUtils.logAndThrowMetaException(e);
            return null;
        }
    }
    
    @Override
    public boolean tableExists(final String databaseName, final String tableName) throws MetaException, TException, UnknownDBException {
        try {
            return this.filterHook.filterTable(this.client.get_table(databaseName, tableName)) != null;
        }
        catch (NoSuchObjectException e) {
            return false;
        }
    }
    
    @Deprecated
    @Override
    public boolean tableExists(final String tableName) throws MetaException, TException, UnknownDBException {
        return this.tableExists("default", tableName);
    }
    
    @Override
    public List<String> listPartitionNames(final String dbName, final String tblName, final short max) throws MetaException, TException {
        return this.filterHook.filterPartitionNames(dbName, tblName, this.client.get_partition_names(dbName, tblName, max));
    }
    
    @Override
    public List<String> listPartitionNames(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts) throws MetaException, TException, NoSuchObjectException {
        return this.filterHook.filterPartitionNames(db_name, tbl_name, this.client.get_partition_names_ps(db_name, tbl_name, part_vals, max_parts));
    }
    
    @Override
    public void alter_partition(final String dbName, final String tblName, final Partition newPart) throws InvalidOperationException, MetaException, TException {
        this.client.alter_partition(dbName, tblName, newPart);
    }
    
    @Override
    public void alter_partitions(final String dbName, final String tblName, final List<Partition> newParts) throws InvalidOperationException, MetaException, TException {
        this.client.alter_partitions(dbName, tblName, newParts);
    }
    
    @Override
    public void alterDatabase(final String dbName, final Database db) throws MetaException, NoSuchObjectException, TException {
        this.client.alter_database(dbName, db);
    }
    
    @Override
    public List<FieldSchema> getFields(final String db, final String tableName) throws MetaException, TException, UnknownTableException, UnknownDBException {
        return this.deepCopyFieldSchemas(this.client.get_fields(db, tableName));
    }
    
    @Override
    public void createIndex(final Index index, final Table indexTable) throws AlreadyExistsException, InvalidObjectException, MetaException, NoSuchObjectException, TException {
        this.client.add_index(index, indexTable);
    }
    
    @Override
    public void alter_index(final String dbname, final String base_tbl_name, final String idx_name, final Index new_idx) throws InvalidOperationException, MetaException, TException {
        this.client.alter_index(dbname, base_tbl_name, idx_name, new_idx);
    }
    
    @Override
    public Index getIndex(final String dbName, final String tblName, final String indexName) throws MetaException, UnknownTableException, NoSuchObjectException, TException {
        return this.deepCopy(this.filterHook.filterIndex(this.client.get_index_by_name(dbName, tblName, indexName)));
    }
    
    @Override
    public List<String> listIndexNames(final String dbName, final String tblName, final short max) throws MetaException, TException {
        return this.filterHook.filterIndexNames(dbName, tblName, this.client.get_index_names(dbName, tblName, max));
    }
    
    @Override
    public List<Index> listIndexes(final String dbName, final String tblName, final short max) throws NoSuchObjectException, MetaException, TException {
        return this.filterHook.filterIndexes(this.client.get_indexes(dbName, tblName, max));
    }
    
    @Override
    public boolean updateTableColumnStatistics(final ColumnStatistics statsObj) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
        return this.client.update_table_column_statistics(statsObj);
    }
    
    @Override
    public boolean updatePartitionColumnStatistics(final ColumnStatistics statsObj) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
        return this.client.update_partition_column_statistics(statsObj);
    }
    
    @Override
    public boolean setPartitionColumnStatistics(final SetPartitionsStatsRequest request) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
        return this.client.set_aggr_stats_for(request);
    }
    
    @Override
    public List<ColumnStatisticsObj> getTableColumnStatistics(final String dbName, final String tableName, final List<String> colNames) throws NoSuchObjectException, MetaException, TException, InvalidInputException, InvalidObjectException {
        return this.client.get_table_statistics_req(new TableStatsRequest(dbName, tableName, colNames)).getTableStats();
    }
    
    @Override
    public Map<String, List<ColumnStatisticsObj>> getPartitionColumnStatistics(final String dbName, final String tableName, final List<String> partNames, final List<String> colNames) throws NoSuchObjectException, MetaException, TException {
        return this.client.get_partitions_statistics_req(new PartitionsStatsRequest(dbName, tableName, colNames, partNames)).getPartStats();
    }
    
    @Override
    public boolean deletePartitionColumnStatistics(final String dbName, final String tableName, final String partName, final String colName) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
        return this.client.delete_partition_column_statistics(dbName, tableName, partName, colName);
    }
    
    @Override
    public boolean deleteTableColumnStatistics(final String dbName, final String tableName, final String colName) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
        return this.client.delete_table_column_statistics(dbName, tableName, colName);
    }
    
    @Override
    public List<FieldSchema> getSchema(final String db, final String tableName) throws MetaException, TException, UnknownTableException, UnknownDBException {
        EnvironmentContext envCxt = null;
        final String addedJars = this.conf.getVar(HiveConf.ConfVars.HIVEADDEDJARS);
        if (org.apache.commons.lang.StringUtils.isNotBlank(addedJars)) {
            final Map<String, String> props = new HashMap<String, String>();
            props.put("hive.added.jars.path", addedJars);
            envCxt = new EnvironmentContext(props);
        }
        return this.deepCopyFieldSchemas(this.client.get_schema_with_environment_context(db, tableName, envCxt));
    }
    
    @Override
    public String getConfigValue(final String name, final String defaultValue) throws TException, ConfigValSecurityException {
        return this.client.get_config_value(name, defaultValue);
    }
    
    @Override
    public Partition getPartition(final String db, final String tableName, final String partName) throws MetaException, TException, UnknownTableException, NoSuchObjectException {
        return this.deepCopy(this.filterHook.filterPartition(this.client.get_partition_by_name(db, tableName, partName)));
    }
    
    public Partition appendPartitionByName(final String dbName, final String tableName, final String partName) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.appendPartitionByName(dbName, tableName, partName, null);
    }
    
    public Partition appendPartitionByName(final String dbName, final String tableName, final String partName, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
        return this.deepCopy(this.client.append_partition_by_name_with_environment_context(dbName, tableName, partName, envContext));
    }
    
    public boolean dropPartitionByName(final String dbName, final String tableName, final String partName, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
        return this.dropPartitionByName(dbName, tableName, partName, deleteData, null);
    }
    
    public boolean dropPartitionByName(final String dbName, final String tableName, final String partName, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException, TException {
        return this.client.drop_partition_by_name_with_environment_context(dbName, tableName, partName, deleteData, envContext);
    }
    
    private HiveMetaHook getHook(final Table tbl) throws MetaException {
        if (this.hookLoader == null) {
            return null;
        }
        return this.hookLoader.getHook(tbl);
    }
    
    @Override
    public List<String> partitionNameToVals(final String name) throws MetaException, TException {
        return this.client.partition_name_to_vals(name);
    }
    
    @Override
    public Map<String, String> partitionNameToSpec(final String name) throws MetaException, TException {
        return this.client.partition_name_to_spec(name);
    }
    
    private Partition deepCopy(final Partition partition) {
        Partition copy = null;
        if (partition != null) {
            copy = new Partition(partition);
        }
        return copy;
    }
    
    private Database deepCopy(final Database database) {
        Database copy = null;
        if (database != null) {
            copy = new Database(database);
        }
        return copy;
    }
    
    protected Table deepCopy(final Table table) {
        Table copy = null;
        if (table != null) {
            copy = new Table(table);
        }
        return copy;
    }
    
    private Index deepCopy(final Index index) {
        Index copy = null;
        if (index != null) {
            copy = new Index(index);
        }
        return copy;
    }
    
    private Type deepCopy(final Type type) {
        Type copy = null;
        if (type != null) {
            copy = new Type(type);
        }
        return copy;
    }
    
    private FieldSchema deepCopy(final FieldSchema schema) {
        FieldSchema copy = null;
        if (schema != null) {
            copy = new FieldSchema(schema);
        }
        return copy;
    }
    
    private Function deepCopy(final Function func) {
        Function copy = null;
        if (func != null) {
            copy = new Function(func);
        }
        return copy;
    }
    
    protected PrincipalPrivilegeSet deepCopy(final PrincipalPrivilegeSet pps) {
        PrincipalPrivilegeSet copy = null;
        if (pps != null) {
            copy = new PrincipalPrivilegeSet(pps);
        }
        return copy;
    }
    
    private List<Partition> deepCopyPartitions(final List<Partition> partitions) {
        return this.deepCopyPartitions(partitions, null);
    }
    
    private List<Partition> deepCopyPartitions(final Collection<Partition> src, List<Partition> dest) {
        if (src == null) {
            return dest;
        }
        if (dest == null) {
            dest = new ArrayList<Partition>(src.size());
        }
        for (final Partition part : src) {
            dest.add(this.deepCopy(part));
        }
        return dest;
    }
    
    private List<Table> deepCopyTables(final List<Table> tables) {
        List<Table> copy = null;
        if (tables != null) {
            copy = new ArrayList<Table>();
            for (final Table tab : tables) {
                copy.add(this.deepCopy(tab));
            }
        }
        return copy;
    }
    
    protected List<FieldSchema> deepCopyFieldSchemas(final List<FieldSchema> schemas) {
        List<FieldSchema> copy = null;
        if (schemas != null) {
            copy = new ArrayList<FieldSchema>();
            for (final FieldSchema schema : schemas) {
                copy.add(this.deepCopy(schema));
            }
        }
        return copy;
    }
    
    @Override
    public boolean dropIndex(final String dbName, final String tblName, final String name, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
        return this.client.drop_index_by_name(dbName, tblName, name, deleteData);
    }
    
    @Override
    public boolean grant_role(final String roleName, final String userName, final PrincipalType principalType, final String grantor, final PrincipalType grantorType, final boolean grantOption) throws MetaException, TException {
        final GrantRevokeRoleRequest req = new GrantRevokeRoleRequest();
        req.setRequestType(GrantRevokeType.GRANT);
        req.setRoleName(roleName);
        req.setPrincipalName(userName);
        req.setPrincipalType(principalType);
        req.setGrantor(grantor);
        req.setGrantorType(grantorType);
        req.setGrantOption(grantOption);
        final GrantRevokeRoleResponse res = this.client.grant_revoke_role(req);
        if (!res.isSetSuccess()) {
            throw new MetaException("GrantRevokeResponse missing success field");
        }
        return res.isSuccess();
    }
    
    @Override
    public boolean create_role(final Role role) throws MetaException, TException {
        return this.client.create_role(role);
    }
    
    @Override
    public boolean drop_role(final String roleName) throws MetaException, TException {
        return this.client.drop_role(roleName);
    }
    
    @Override
    public List<Role> list_roles(final String principalName, final PrincipalType principalType) throws MetaException, TException {
        return this.client.list_roles(principalName, principalType);
    }
    
    @Override
    public List<String> listRoleNames() throws MetaException, TException {
        return this.client.get_role_names();
    }
    
    @Override
    public GetPrincipalsInRoleResponse get_principals_in_role(final GetPrincipalsInRoleRequest req) throws MetaException, TException {
        return this.client.get_principals_in_role(req);
    }
    
    @Override
    public GetRoleGrantsForPrincipalResponse get_role_grants_for_principal(final GetRoleGrantsForPrincipalRequest getRolePrincReq) throws MetaException, TException {
        return this.client.get_role_grants_for_principal(getRolePrincReq);
    }
    
    @Override
    public boolean grant_privileges(final PrivilegeBag privileges) throws MetaException, TException {
        final GrantRevokePrivilegeRequest req = new GrantRevokePrivilegeRequest();
        req.setRequestType(GrantRevokeType.GRANT);
        req.setPrivileges(privileges);
        final GrantRevokePrivilegeResponse res = this.client.grant_revoke_privileges(req);
        if (!res.isSetSuccess()) {
            throw new MetaException("GrantRevokePrivilegeResponse missing success field");
        }
        return res.isSuccess();
    }
    
    @Override
    public boolean revoke_role(final String roleName, final String userName, final PrincipalType principalType, final boolean grantOption) throws MetaException, TException {
        final GrantRevokeRoleRequest req = new GrantRevokeRoleRequest();
        req.setRequestType(GrantRevokeType.REVOKE);
        req.setRoleName(roleName);
        req.setPrincipalName(userName);
        req.setPrincipalType(principalType);
        req.setGrantOption(grantOption);
        final GrantRevokeRoleResponse res = this.client.grant_revoke_role(req);
        if (!res.isSetSuccess()) {
            throw new MetaException("GrantRevokeResponse missing success field");
        }
        return res.isSuccess();
    }
    
    @Override
    public boolean revoke_privileges(final PrivilegeBag privileges, final boolean grantOption) throws MetaException, TException {
        final GrantRevokePrivilegeRequest req = new GrantRevokePrivilegeRequest();
        req.setRequestType(GrantRevokeType.REVOKE);
        req.setPrivileges(privileges);
        req.setRevokeGrantOption(grantOption);
        final GrantRevokePrivilegeResponse res = this.client.grant_revoke_privileges(req);
        if (!res.isSetSuccess()) {
            throw new MetaException("GrantRevokePrivilegeResponse missing success field");
        }
        return res.isSuccess();
    }
    
    @Override
    public PrincipalPrivilegeSet get_privilege_set(final HiveObjectRef hiveObject, final String userName, final List<String> groupNames) throws MetaException, TException {
        return this.client.get_privilege_set(hiveObject, userName, groupNames);
    }
    
    @Override
    public List<HiveObjectPrivilege> list_privileges(final String principalName, final PrincipalType principalType, final HiveObjectRef hiveObject) throws MetaException, TException {
        return this.client.list_privileges(principalName, principalType, hiveObject);
    }
    
    public String getDelegationToken(final String renewerKerberosPrincipalName) throws MetaException, TException, IOException {
        final String owner = this.conf.getUser();
        return this.getDelegationToken(owner, renewerKerberosPrincipalName);
    }
    
    @Override
    public String getDelegationToken(final String owner, final String renewerKerberosPrincipalName) throws MetaException, TException {
        if (this.localMetaStore) {
            return null;
        }
        return this.client.get_delegation_token(owner, renewerKerberosPrincipalName);
    }
    
    @Override
    public long renewDelegationToken(final String tokenStrForm) throws MetaException, TException {
        if (this.localMetaStore) {
            return 0L;
        }
        return this.client.renew_delegation_token(tokenStrForm);
    }
    
    @Override
    public void cancelDelegationToken(final String tokenStrForm) throws MetaException, TException {
        if (this.localMetaStore) {
            return;
        }
        this.client.cancel_delegation_token(tokenStrForm);
    }
    
    @Override
    public ValidTxnList getValidTxns() throws TException {
        return TxnHandler.createValidReadTxnList(this.client.get_open_txns(), 0L);
    }
    
    @Override
    public ValidTxnList getValidTxns(final long currentTxn) throws TException {
        return TxnHandler.createValidReadTxnList(this.client.get_open_txns(), currentTxn);
    }
    
    @Override
    public long openTxn(final String user) throws TException {
        final OpenTxnsResponse txns = this.openTxns(user, 1);
        return txns.getTxn_ids().get(0);
    }
    
    @Override
    public OpenTxnsResponse openTxns(final String user, final int numTxns) throws TException {
        String hostname = null;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException e) {
            HiveMetaStoreClient.LOG.error("Unable to resolve my host name " + e.getMessage());
            throw new RuntimeException(e);
        }
        return this.client.open_txns(new OpenTxnRequest(numTxns, user, hostname));
    }
    
    @Override
    public void rollbackTxn(final long txnid) throws NoSuchTxnException, TException {
        this.client.abort_txn(new AbortTxnRequest(txnid));
    }
    
    @Override
    public void commitTxn(final long txnid) throws NoSuchTxnException, TxnAbortedException, TException {
        this.client.commit_txn(new CommitTxnRequest(txnid));
    }
    
    @Override
    public GetOpenTxnsInfoResponse showTxns() throws TException {
        return this.client.get_open_txns_info();
    }
    
    @Override
    public LockResponse lock(final LockRequest request) throws NoSuchTxnException, TxnAbortedException, TException {
        return this.client.lock(request);
    }
    
    @Override
    public LockResponse checkLock(final long lockid) throws NoSuchTxnException, TxnAbortedException, NoSuchLockException, TException {
        return this.client.check_lock(new CheckLockRequest(lockid));
    }
    
    @Override
    public void unlock(final long lockid) throws NoSuchLockException, TxnOpenException, TException {
        this.client.unlock(new UnlockRequest(lockid));
    }
    
    @Override
    public ShowLocksResponse showLocks() throws TException {
        return this.client.show_locks(new ShowLocksRequest());
    }
    
    @Override
    public void heartbeat(final long txnid, final long lockid) throws NoSuchLockException, NoSuchTxnException, TxnAbortedException, TException {
        final HeartbeatRequest hb = new HeartbeatRequest();
        hb.setLockid(lockid);
        hb.setTxnid(txnid);
        this.client.heartbeat(hb);
    }
    
    @Override
    public HeartbeatTxnRangeResponse heartbeatTxnRange(final long min, final long max) throws NoSuchTxnException, TxnAbortedException, TException {
        final HeartbeatTxnRangeRequest rqst = new HeartbeatTxnRangeRequest(min, max);
        return this.client.heartbeat_txn_range(rqst);
    }
    
    @Override
    public void compact(final String dbname, final String tableName, final String partitionName, final CompactionType type) throws TException {
        final CompactionRequest cr = new CompactionRequest();
        if (dbname == null) {
            cr.setDbname("default");
        }
        else {
            cr.setDbname(dbname);
        }
        cr.setTablename(tableName);
        if (partitionName != null) {
            cr.setPartitionname(partitionName);
        }
        cr.setType(type);
        this.client.compact(cr);
    }
    
    @Override
    public ShowCompactResponse showCompactions() throws TException {
        return this.client.show_compact(new ShowCompactRequest());
    }
    
    @Override
    public void addDynamicPartitions(final long txnId, final String dbName, final String tableName, final List<String> partNames) throws TException {
        this.client.add_dynamic_partitions(new AddDynamicPartitions(txnId, dbName, tableName, partNames));
    }
    
    @InterfaceAudience.LimitedPrivate({ "HCatalog" })
    @Override
    public NotificationEventResponse getNextNotification(final long lastEventId, final int maxEvents, final NotificationFilter filter) throws TException {
        final NotificationEventRequest rqst = new NotificationEventRequest(lastEventId);
        rqst.setMaxEvents(maxEvents);
        final NotificationEventResponse rsp = this.client.get_next_notification(rqst);
        HiveMetaStoreClient.LOG.debug("Got back " + rsp.getEventsSize() + " events");
        if (filter == null) {
            return rsp;
        }
        final NotificationEventResponse filtered = new NotificationEventResponse();
        if (rsp != null && rsp.getEvents() != null) {
            for (final NotificationEvent e : rsp.getEvents()) {
                if (filter.accept(e)) {
                    filtered.addToEvents(e);
                }
            }
        }
        return filtered;
    }
    
    @InterfaceAudience.LimitedPrivate({ "HCatalog" })
    @Override
    public CurrentNotificationEventId getCurrentNotificationEventId() throws TException {
        return this.client.get_current_notificationEventId();
    }
    
    @InterfaceAudience.LimitedPrivate({ "Apache Hive, HCatalog" })
    @Override
    public FireEventResponse fireListenerEvent(final FireEventRequest rqst) throws TException {
        return this.client.fire_listener_event(rqst);
    }
    
    public static IMetaStoreClient newSynchronizedClient(final IMetaStoreClient client) {
        return (IMetaStoreClient)Proxy.newProxyInstance(HiveMetaStoreClient.class.getClassLoader(), new Class[] { IMetaStoreClient.class }, new SynchronizedHandler(client));
    }
    
    @Override
    public void markPartitionForEvent(final String db_name, final String tbl_name, final Map<String, String> partKVs, final PartitionEventType eventType) throws MetaException, TException, NoSuchObjectException, UnknownDBException, UnknownTableException, InvalidPartitionException, UnknownPartitionException {
        assert db_name != null;
        assert tbl_name != null;
        assert partKVs != null;
        this.client.markPartitionForEvent(db_name, tbl_name, partKVs, eventType);
    }
    
    @Override
    public boolean isPartitionMarkedForEvent(final String db_name, final String tbl_name, final Map<String, String> partKVs, final PartitionEventType eventType) throws MetaException, NoSuchObjectException, UnknownTableException, UnknownDBException, TException, InvalidPartitionException, UnknownPartitionException {
        assert db_name != null;
        assert tbl_name != null;
        assert partKVs != null;
        return this.client.isPartitionMarkedForEvent(db_name, tbl_name, partKVs, eventType);
    }
    
    @Override
    public void createFunction(final Function func) throws InvalidObjectException, MetaException, TException {
        this.client.create_function(func);
    }
    
    @Override
    public void alterFunction(final String dbName, final String funcName, final Function newFunction) throws InvalidObjectException, MetaException, TException {
        this.client.alter_function(dbName, funcName, newFunction);
    }
    
    @Override
    public void dropFunction(final String dbName, final String funcName) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException, TException {
        this.client.drop_function(dbName, funcName);
    }
    
    @Override
    public Function getFunction(final String dbName, final String funcName) throws MetaException, TException {
        return this.deepCopy(this.client.get_function(dbName, funcName));
    }
    
    @Override
    public List<String> getFunctions(final String dbName, final String pattern) throws MetaException, TException {
        return this.client.get_functions(dbName, pattern);
    }
    
    protected void create_table_with_environment_context(final Table tbl, final EnvironmentContext envContext) throws AlreadyExistsException, InvalidObjectException, MetaException, NoSuchObjectException, TException {
        this.client.create_table_with_environment_context(tbl, envContext);
    }
    
    protected void drop_table_with_environment_context(final String dbname, final String name, final boolean deleteData, final EnvironmentContext envContext) throws MetaException, TException, NoSuchObjectException, UnsupportedOperationException {
        this.client.drop_table_with_environment_context(dbname, name, deleteData, envContext);
    }
    
    @Override
    public AggrStats getAggrColStatsFor(final String dbName, final String tblName, final List<String> colNames, final List<String> partNames) throws NoSuchObjectException, MetaException, TException {
        if (colNames.isEmpty() || partNames.isEmpty()) {
            HiveMetaStoreClient.LOG.debug("Columns is empty or partNames is empty : Short-circuiting stats eval on client side.");
            return new AggrStats(new ArrayList<ColumnStatisticsObj>(), 0L);
        }
        final PartitionsStatsRequest req = new PartitionsStatsRequest(dbName, tblName, colNames, partNames);
        return this.client.get_aggr_stats_for(req);
    }
    
    static {
        LOG = LogFactory.getLog("hive.metastore");
    }
    
    private static class SynchronizedHandler implements InvocationHandler
    {
        private final IMetaStoreClient client;
        private static final Object lock;
        
        SynchronizedHandler(final IMetaStoreClient client) {
            this.client = client;
        }
        
        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            try {
                synchronized (SynchronizedHandler.lock) {
                    return method.invoke(this.client, args);
                }
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
        
        static {
            lock = SynchronizedHandler.class;
        }
    }
}
