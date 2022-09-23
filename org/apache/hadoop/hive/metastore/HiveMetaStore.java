// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.thrift.TUnion;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionBuilder;
import org.apache.hadoop.hive.common.cli.CommonCliOptions;
import org.apache.hadoop.hive.metastore.events.InsertEvent;
import org.apache.hadoop.hive.metastore.api.FireEventRequestData;
import org.apache.hadoop.hive.metastore.api.FireEventResponse;
import org.apache.hadoop.hive.metastore.api.FireEventRequest;
import org.apache.hadoop.hive.metastore.api.CurrentNotificationEventId;
import org.apache.hadoop.hive.metastore.api.NotificationEventResponse;
import org.apache.hadoop.hive.metastore.api.NotificationEventRequest;
import org.apache.hadoop.hive.metastore.api.SetPartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.AggrStats;
import org.apache.hadoop.hive.metastore.api.RolePrincipalGrant;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalResponse;
import org.apache.hadoop.hive.metastore.api.GetRoleGrantsForPrincipalRequest;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleResponse;
import org.apache.hadoop.hive.metastore.api.GetPrincipalsInRoleRequest;
import org.apache.hadoop.hive.metastore.api.AddDynamicPartitions;
import org.apache.hadoop.hive.metastore.api.ShowCompactResponse;
import org.apache.hadoop.hive.metastore.api.ShowCompactRequest;
import org.apache.hadoop.hive.metastore.api.CompactionRequest;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeResponse;
import org.apache.hadoop.hive.metastore.api.HeartbeatTxnRangeRequest;
import org.apache.hadoop.hive.metastore.api.HeartbeatRequest;
import org.apache.hadoop.hive.metastore.api.ShowLocksResponse;
import org.apache.hadoop.hive.metastore.api.ShowLocksRequest;
import org.apache.hadoop.hive.metastore.api.TxnOpenException;
import org.apache.hadoop.hive.metastore.api.UnlockRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchLockException;
import org.apache.hadoop.hive.metastore.api.CheckLockRequest;
import org.apache.hadoop.hive.metastore.api.LockResponse;
import org.apache.hadoop.hive.metastore.api.LockRequest;
import org.apache.hadoop.hive.metastore.api.TxnAbortedException;
import org.apache.hadoop.hive.metastore.api.CommitTxnRequest;
import org.apache.hadoop.hive.metastore.api.NoSuchTxnException;
import org.apache.hadoop.hive.metastore.api.AbortTxnRequest;
import org.apache.hadoop.hive.metastore.api.OpenTxnsResponse;
import org.apache.hadoop.hive.metastore.api.OpenTxnRequest;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsInfoResponse;
import org.apache.hadoop.hive.metastore.api.GetOpenTxnsResponse;
import org.apache.hadoop.hive.metastore.api.InvalidPartitionException;
import org.apache.hadoop.hive.metastore.api.UnknownPartitionException;
import org.apache.hadoop.hive.metastore.events.LoadPartitionDoneEvent;
import org.apache.hadoop.hive.metastore.events.PreLoadPartitionDoneEvent;
import org.apache.hadoop.hive.metastore.api.PartitionEventType;
import org.apache.hadoop.hive.metastore.model.MGlobalPrivilege;
import org.apache.hadoop.hive.metastore.model.MTablePrivilege;
import org.apache.hadoop.hive.metastore.model.MPartitionPrivilege;
import org.apache.hadoop.hive.metastore.model.MDBPrivilege;
import org.apache.hadoop.hive.metastore.model.MPartitionColumnPrivilege;
import org.apache.hadoop.hive.metastore.model.MTableColumnPrivilege;
import java.util.Collections;
import org.apache.hadoop.hive.metastore.api.GrantRevokePrivilegeResponse;
import org.apache.hadoop.hive.metastore.api.GrantRevokePrivilegeRequest;
import org.apache.hadoop.hive.metastore.api.GrantRevokeRoleResponse;
import org.apache.hadoop.hive.metastore.api.GrantRevokeRoleRequest;
import org.apache.hadoop.hive.metastore.model.MRole;
import org.apache.hadoop.hive.metastore.model.MRoleMap;
import org.apache.hadoop.hive.metastore.events.PreAuthorizationCallEvent;
import org.apache.hadoop.hive.metastore.api.PrincipalPrivilegeSet;
import java.util.LinkedList;
import org.apache.hadoop.hive.metastore.api.PartitionsByExprResult;
import org.apache.hadoop.hive.metastore.api.PartitionsByExprRequest;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsDesc;
import org.apache.hadoop.hive.metastore.api.PartitionsStatsResult;
import org.apache.hadoop.hive.metastore.api.PartitionsStatsRequest;
import org.apache.hadoop.hive.metastore.api.ColumnStatisticsObj;
import org.apache.hadoop.hive.metastore.api.TableStatsResult;
import org.apache.hadoop.hive.metastore.api.TableStatsRequest;
import com.google.common.collect.Lists;
import org.apache.hadoop.hive.metastore.api.ColumnStatistics;
import org.apache.hadoop.hive.metastore.events.DropIndexEvent;
import org.apache.hadoop.hive.metastore.events.PreDropIndexEvent;
import org.apache.hadoop.hive.metastore.events.AddIndexEvent;
import org.apache.hadoop.hive.metastore.events.PreAddIndexEvent;
import java.util.LinkedHashMap;
import org.apache.hadoop.hive.metastore.api.ConfigValSecurityException;
import org.apache.hadoop.hive.serde2.Deserializer;
import org.apache.hadoop.hive.serde2.SerDeException;
import org.apache.hadoop.hive.metastore.api.UnknownTableException;
import org.apache.hadoop.hive.metastore.events.AlterTableEvent;
import org.apache.hadoop.hive.metastore.events.PreAlterTableEvent;
import org.apache.hadoop.hive.metastore.events.AlterIndexEvent;
import org.apache.hadoop.hive.metastore.events.PreAlterIndexEvent;
import org.apache.hadoop.hive.metastore.events.AlterPartitionEvent;
import org.apache.hadoop.hive.metastore.events.PreAlterPartitionEvent;
import org.apache.hadoop.hive.metastore.api.PartitionSpecWithSharedSD;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import org.apache.hadoop.hive.metastore.api.PartitionWithoutSD;
import com.google.common.collect.Multimaps;
import com.google.common.base.Function;
import org.apache.hadoop.hive.metastore.api.PartitionListComposingSpec;
import org.apache.hadoop.hive.metastore.api.RequestPartsSpec;
import org.apache.hadoop.hive.common.FileUtils;
import org.apache.hadoop.hive.metastore.api.DropPartitionsExpr;
import org.apache.hadoop.hive.metastore.api.DropPartitionsResult;
import org.apache.hadoop.hive.metastore.api.DropPartitionsRequest;
import org.apache.hadoop.hive.metastore.events.DropPartitionEvent;
import org.apache.hadoop.hive.metastore.events.PreDropPartitionEvent;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.hadoop.hive.metastore.partition.spec.PartitionSpecProxy;
import org.apache.hadoop.hive.metastore.api.PartitionSpec;
import org.apache.hadoop.hive.metastore.api.AddPartitionsResult;
import org.apache.hadoop.hive.metastore.api.AddPartitionsRequest;
import java.util.HashMap;
import org.apache.hadoop.hive.metastore.events.AddPartitionEvent;
import org.apache.hadoop.hive.metastore.events.PreAddPartitionEvent;
import org.apache.hadoop.hive.metastore.events.PreReadTableEvent;
import org.apache.hadoop.hive.metastore.api.Partition;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.shims.HadoopShims;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hive.metastore.events.DropTableEvent;
import org.apache.hadoop.hive.metastore.api.Index;
import org.apache.hadoop.hive.metastore.events.PreDropTableEvent;
import org.apache.hadoop.hive.metastore.api.SkewedInfo;
import org.apache.hadoop.hive.metastore.events.CreateTableEvent;
import org.apache.hadoop.hive.metastore.events.PreCreateTableEvent;
import org.apache.hadoop.hive.metastore.api.EnvironmentContext;
import org.apache.hadoop.hive.metastore.api.Type;
import org.apache.hadoop.hive.metastore.api.InvalidInputException;
import org.apache.hadoop.hive.metastore.events.DropDatabaseEvent;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.UnknownDBException;
import org.apache.hadoop.hive.metastore.api.InvalidOperationException;
import org.apache.hadoop.hive.metastore.events.PreDropDatabaseEvent;
import java.util.ArrayList;
import org.apache.thrift.TException;
import org.apache.hadoop.hive.metastore.events.PreReadDatabaseEvent;
import org.apache.hadoop.hive.metastore.api.AlreadyExistsException;
import org.apache.hadoop.hive.metastore.events.CreateDatabaseEvent;
import org.apache.hadoop.hive.metastore.events.PreEventContext;
import org.apache.hadoop.hive.metastore.events.PreCreateDatabaseEvent;
import org.apache.hadoop.fs.Path;
import java.util.AbstractMap;
import com.facebook.fb303.fb_status;
import java.util.Collection;
import org.apache.hadoop.hive.metastore.api.Role;
import com.google.common.base.Splitter;
import org.apache.hadoop.hive.metastore.api.HiveObjectPrivilege;
import org.apache.hadoop.hive.metastore.api.PrivilegeGrantInfo;
import org.apache.hadoop.hive.metastore.api.HiveObjectRef;
import org.apache.hadoop.hive.metastore.api.HiveObjectType;
import org.apache.hadoop.hive.metastore.api.PrivilegeBag;
import javax.jdo.JDOException;
import org.apache.hadoop.hive.metastore.api.InvalidObjectException;
import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.PrincipalType;
import org.apache.hadoop.hive.metastore.api.Database;
import org.apache.hadoop.hive.common.classification.InterfaceStability;
import org.apache.hadoop.hive.common.classification.InterfaceAudience;
import org.apache.hadoop.hive.metastore.events.ConfigChangeEvent;
import java.util.TimerTask;
import org.apache.hadoop.hive.metastore.events.EventCleanerTask;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hive.common.metrics.Metrics;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.hive.shims.Utils;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Formatter;
import org.apache.hadoop.hive.metastore.txn.TxnHandler;
import com.facebook.fb303.FacebookBase;
import org.apache.thrift.transport.TTransport;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.LogFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TServerTransport;
import org.apache.hadoop.util.StringUtils;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.hadoop.hive.thrift.TUGIContainingTransport;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.TProcessor;
import org.apache.hadoop.conf.Configuration;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TServerSocket;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.Iterator;
import java.util.Properties;
import org.apache.hadoop.hive.shims.ShimLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Map;
import org.apache.hadoop.hive.common.LogUtils;
import java.io.IOException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.thrift.HadoopThriftAuthBridge;
import java.text.DateFormat;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.metastore.api.ThriftHiveMetastore;

public class HiveMetaStore extends ThriftHiveMetastore
{
    public static final Log LOG;
    private static boolean isMetaStoreRemote;
    @VisibleForTesting
    static boolean TEST_TIMEOUT_ENABLED;
    @VisibleForTesting
    static long TEST_TIMEOUT_VALUE;
    public static final ThreadLocal<DateFormat> PARTITION_DATE_FORMAT;
    private static final int DEFAULT_HIVE_METASTORE_PORT = 9083;
    public static final String ADMIN = "admin";
    public static final String PUBLIC = "public";
    private static HadoopThriftAuthBridge.Server saslServer;
    private static boolean useSasl;
    private static int nextThreadId;
    
    public static IHMSHandler newRetryingHMSHandler(final IHMSHandler baseHandler, final HiveConf hiveConf) throws MetaException {
        return newRetryingHMSHandler(baseHandler, hiveConf, false);
    }
    
    public static IHMSHandler newRetryingHMSHandler(final IHMSHandler baseHandler, final HiveConf hiveConf, final boolean local) throws MetaException {
        return RetryingHMSHandler.getProxy(hiveConf, baseHandler, local);
    }
    
    public static Iface newRetryingHMSHandler(final String name, final HiveConf conf, final boolean local) throws MetaException {
        final HMSHandler baseHandler = new HMSHandler(name, conf, false);
        return RetryingHMSHandler.getProxy(conf, baseHandler, local);
    }
    
    public static void cancelDelegationToken(final String tokenStrForm) throws IOException {
        HiveMetaStore.saslServer.cancelDelegationToken(tokenStrForm);
    }
    
    public static String getDelegationToken(final String owner, final String renewer) throws IOException, InterruptedException {
        return HiveMetaStore.saslServer.getDelegationToken(owner, renewer);
    }
    
    public static boolean isMetaStoreRemote() {
        return HiveMetaStore.isMetaStoreRemote;
    }
    
    public static long renewDelegationToken(final String tokenStrForm) throws IOException {
        return HiveMetaStore.saslServer.renewDelegationToken(tokenStrForm);
    }
    
    public static void main(final String[] args) throws Throwable {
        HiveConf.setLoadMetastoreConfig(true);
        final HiveMetastoreCli cli = new HiveMetastoreCli();
        cli.parse(args);
        final boolean isCliVerbose = cli.isVerbose();
        final Properties hiveconf = cli.addHiveconfToSystemProperties();
        if (System.getProperty("log4j.configuration") == null) {
            try {
                LogUtils.initHiveLog4j();
            }
            catch (LogUtils.LogInitializationException e) {
                HMSHandler.LOG.warn(e.getMessage());
            }
        }
        try {
            final String msg = "Starting hive metastore on port " + cli.port;
            HMSHandler.LOG.info(msg);
            if (cli.isVerbose()) {
                System.err.println(msg);
            }
            final HiveConf conf = new HiveConf(HMSHandler.class);
            for (final Map.Entry<Object, Object> item : hiveconf.entrySet()) {
                conf.set(item.getKey(), item.getValue());
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    final String shutdownMsg = "Shutting down hive metastore.";
                    HMSHandler.LOG.info(shutdownMsg);
                    if (isCliVerbose) {
                        System.err.println(shutdownMsg);
                    }
                }
            });
            final Lock startLock = new ReentrantLock();
            final Condition startCondition = startLock.newCondition();
            final AtomicBoolean startedServing = new AtomicBoolean();
            startMetaStoreThreads(conf, startLock, startCondition, startedServing);
            startMetaStore(cli.port, ShimLoader.getHadoopThriftAuthBridge(), conf, startLock, startCondition, startedServing);
        }
        catch (Throwable t) {
            HMSHandler.LOG.error("Metastore Thrift Server threw an exception...", t);
            throw t;
        }
    }
    
    public static void startMetaStore(final int port, final HadoopThriftAuthBridge bridge) throws Throwable {
        startMetaStore(port, bridge, new HiveConf(HMSHandler.class), null, null, null);
    }
    
    public static void startMetaStore(final int port, final HadoopThriftAuthBridge bridge, final HiveConf conf) throws Throwable {
        startMetaStore(port, bridge, conf, null, null, null);
    }
    
    public static void startMetaStore(final int port, final HadoopThriftAuthBridge bridge, final HiveConf conf, final Lock startLock, final Condition startCondition, final AtomicBoolean startedServing) throws Throwable {
        try {
            HiveMetaStore.isMetaStoreRemote = true;
            final int maxMessageSize = conf.getIntVar(HiveConf.ConfVars.METASTORESERVERMAXMESSAGESIZE);
            final int minWorkerThreads = conf.getIntVar(HiveConf.ConfVars.METASTORESERVERMINTHREADS);
            final int maxWorkerThreads = conf.getIntVar(HiveConf.ConfVars.METASTORESERVERMAXTHREADS);
            final boolean tcpKeepAlive = conf.getBoolVar(HiveConf.ConfVars.METASTORE_TCP_KEEP_ALIVE);
            final boolean useFramedTransport = conf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_FRAMED_TRANSPORT);
            final boolean useCompactProtocol = conf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_COMPACT_PROTOCOL);
            HiveMetaStore.useSasl = conf.getBoolVar(HiveConf.ConfVars.METASTORE_USE_THRIFT_SASL);
            final TServerTransport serverTransport = tcpKeepAlive ? new TServerSocketKeepAlive(port) : new TServerSocket(port);
            TProtocolFactory protocolFactory;
            TProtocolFactory inputProtoFactory;
            if (useCompactProtocol) {
                protocolFactory = new TCompactProtocol.Factory();
                inputProtoFactory = new TCompactProtocol.Factory(maxMessageSize, maxMessageSize);
            }
            else {
                protocolFactory = new TBinaryProtocol.Factory();
                inputProtoFactory = new TBinaryProtocol.Factory(true, true, maxMessageSize, maxMessageSize);
            }
            final HMSHandler baseHandler = new HMSHandler("new db based metaserver", conf, false);
            final IHMSHandler handler = newRetryingHMSHandler(baseHandler, conf);
            TTransportFactory transFactory;
            TProcessor processor;
            if (HiveMetaStore.useSasl) {
                if (useFramedTransport) {
                    throw new HiveMetaException("Framed transport is not supported with SASL enabled.");
                }
                (HiveMetaStore.saslServer = bridge.createServer(conf.getVar(HiveConf.ConfVars.METASTORE_KERBEROS_KEYTAB_FILE), conf.getVar(HiveConf.ConfVars.METASTORE_KERBEROS_PRINCIPAL))).startDelegationTokenSecretManager(conf, baseHandler.getMS(), HadoopThriftAuthBridge.Server.ServerMode.METASTORE);
                transFactory = HiveMetaStore.saslServer.createTransportFactory(MetaStoreUtils.getMetaStoreSaslProperties(conf));
                processor = HiveMetaStore.saslServer.wrapProcessor(new Processor<Object>(handler));
                HiveMetaStore.LOG.info("Starting DB backed MetaStore Server in Secure Mode");
            }
            else if (conf.getBoolVar(HiveConf.ConfVars.METASTORE_EXECUTE_SET_UGI)) {
                TTransportFactory tTransportFactory;
                if (useFramedTransport) {
                    final TFramedTransport.Factory x2;
                    final TUGIContainingTransport.Factory x3;
                    tTransportFactory = new ChainedTTransportFactory((TTransportFactory)x2, (TTransportFactory)x3);
                    x2 = new TFramedTransport.Factory();
                    x3 = new TUGIContainingTransport.Factory();
                }
                else {
                    tTransportFactory = new TUGIContainingTransport.Factory();
                }
                transFactory = tTransportFactory;
                processor = new TUGIBasedProcessor<Object>((Object)handler);
                HiveMetaStore.LOG.info("Starting DB backed MetaStore Server with SetUGI enabled");
            }
            else {
                transFactory = (useFramedTransport ? new TFramedTransport.Factory() : new TTransportFactory());
                processor = new TSetIpAddressProcessor<Object>((Object)handler);
                HiveMetaStore.LOG.info("Starting DB backed MetaStore Server");
            }
            final TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport).processor(processor).transportFactory(transFactory).protocolFactory(protocolFactory).inputProtocolFactory(inputProtoFactory).minWorkerThreads(minWorkerThreads).maxWorkerThreads(maxWorkerThreads);
            final TServer tServer = new TThreadPoolServer(args);
            HMSHandler.LOG.info("Started the new metaserver on port [" + port + "]...");
            HMSHandler.LOG.info("Options.minWorkerThreads = " + minWorkerThreads);
            HMSHandler.LOG.info("Options.maxWorkerThreads = " + maxWorkerThreads);
            HMSHandler.LOG.info("TCP keepalive = " + tcpKeepAlive);
            if (startLock != null) {
                signalOtherThreadsToStart(tServer, startLock, startCondition, startedServing);
            }
            tServer.serve();
        }
        catch (Throwable x) {
            x.printStackTrace();
            HMSHandler.LOG.error(StringUtils.stringifyException(x));
            throw x;
        }
    }
    
    private static void signalOtherThreadsToStart(final TServer server, final Lock startLock, final Condition startCondition, final AtomicBoolean startedServing) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e) {
                        HiveMetaStore.LOG.warn("Signalling thread was interuppted: " + e.getMessage());
                    }
                } while (!server.isServing());
                startLock.lock();
                try {
                    startedServing.set(true);
                    startCondition.signalAll();
                }
                finally {
                    startLock.unlock();
                }
            }
        };
        t.start();
    }
    
    private static void startMetaStoreThreads(final HiveConf conf, final Lock startLock, final Condition startCondition, final AtomicBoolean startedServing) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                startLock.lock();
                ShimLoader.getHadoopShims().startPauseMonitor(conf);
                try {
                    while (!startedServing.get()) {
                        startCondition.await();
                    }
                    startCompactorInitiator(conf);
                    startCompactorWorkers(conf);
                    startCompactorCleaner(conf);
                }
                catch (Throwable e) {
                    HiveMetaStore.LOG.error("Failure when starting the compactor, compactions may not happen, " + StringUtils.stringifyException(e));
                }
                finally {
                    startLock.unlock();
                }
            }
        };
        t.start();
    }
    
    private static void startCompactorInitiator(final HiveConf conf) throws Exception {
        if (HiveConf.getBoolVar(conf, HiveConf.ConfVars.HIVE_COMPACTOR_INITIATOR_ON)) {
            final MetaStoreThread initiator = instantiateThread("org.apache.hadoop.hive.ql.txn.compactor.Initiator");
            initializeAndStartThread(initiator, conf);
        }
    }
    
    private static void startCompactorWorkers(final HiveConf conf) throws Exception {
        for (int numWorkers = HiveConf.getIntVar(conf, HiveConf.ConfVars.HIVE_COMPACTOR_WORKER_THREADS), i = 0; i < numWorkers; ++i) {
            final MetaStoreThread worker = instantiateThread("org.apache.hadoop.hive.ql.txn.compactor.Worker");
            initializeAndStartThread(worker, conf);
        }
    }
    
    private static void startCompactorCleaner(final HiveConf conf) throws Exception {
        if (HiveConf.getBoolVar(conf, HiveConf.ConfVars.HIVE_COMPACTOR_INITIATOR_ON)) {
            final MetaStoreThread cleaner = instantiateThread("org.apache.hadoop.hive.ql.txn.compactor.Cleaner");
            initializeAndStartThread(cleaner, conf);
        }
    }
    
    private static MetaStoreThread instantiateThread(final String classname) throws Exception {
        final Class c = Class.forName(classname);
        final Object o = c.newInstance();
        if (MetaStoreThread.class.isAssignableFrom(o.getClass())) {
            return (MetaStoreThread)o;
        }
        final String s = classname + " is not an instance of MetaStoreThread.";
        HiveMetaStore.LOG.error(s);
        throw new IOException(s);
    }
    
    private static void initializeAndStartThread(final MetaStoreThread thread, final HiveConf conf) throws MetaException {
        HiveMetaStore.LOG.info("Starting metastore thread of type " + thread.getClass().getName());
        thread.setHiveConf(conf);
        thread.setThreadId(HiveMetaStore.nextThreadId++);
        thread.init(new AtomicBoolean(), new AtomicBoolean());
        thread.start();
    }
    
    static {
        LOG = LogFactory.getLog(HiveMetaStore.class);
        HiveMetaStore.isMetaStoreRemote = false;
        HiveMetaStore.TEST_TIMEOUT_ENABLED = false;
        HiveMetaStore.TEST_TIMEOUT_VALUE = -1L;
        PARTITION_DATE_FORMAT = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                final DateFormat val = new SimpleDateFormat("yyyy-MM-dd");
                val.setLenient(false);
                return val;
            }
        };
        HiveMetaStore.nextThreadId = 1000000;
    }
    
    private static final class ChainedTTransportFactory extends TTransportFactory
    {
        private final TTransportFactory parentTransFactory;
        private final TTransportFactory childTransFactory;
        
        private ChainedTTransportFactory(final TTransportFactory parentTransFactory, final TTransportFactory childTransFactory) {
            this.parentTransFactory = parentTransFactory;
            this.childTransFactory = childTransFactory;
        }
        
        @Override
        public TTransport getTransport(final TTransport trans) {
            return this.childTransFactory.getTransport(this.parentTransFactory.getTransport(trans));
        }
    }
    
    public static class HMSHandler extends FacebookBase implements IHMSHandler
    {
        public static final Log LOG;
        private String rawStoreClassName;
        private final HiveConf hiveConf;
        private static String currentUrl;
        private Warehouse wh;
        private static final ThreadLocal<RawStore> threadLocalMS;
        private static final ThreadLocal<TxnHandler> threadLocalTxn;
        private static final ThreadLocal<Configuration> threadLocalConf;
        public static final String AUDIT_FORMAT = "ugi=%s\tip=%s\tcmd=%s\t";
        public static final Log auditLog;
        private static final ThreadLocal<Formatter> auditFormatter;
        private static int nextSerialNum;
        private static ThreadLocal<Integer> threadLocalId;
        private static ThreadLocal<String> threadLocalIpAddress;
        private ClassLoader classLoader;
        private AlterHandler alterHandler;
        private List<MetaStorePreEventListener> preListeners;
        private List<MetaStoreEventListener> listeners;
        private List<MetaStoreEndFunctionListener> endFunctionListeners;
        private List<MetaStoreInitListener> initListeners;
        private Pattern partitionValidationPattern;
        
        public static RawStore getRawStore() {
            return HMSHandler.threadLocalMS.get();
        }
        
        public static void removeRawStore() {
            HMSHandler.threadLocalMS.remove();
        }
        
        private final void logAuditEvent(final String cmd) {
            if (cmd == null) {
                return;
            }
            UserGroupInformation ugi;
            try {
                ugi = Utils.getUGI();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            final Formatter fmt = HMSHandler.auditFormatter.get();
            ((StringBuilder)fmt.out()).setLength(0);
            String address = null;
            if (HiveMetaStore.useSasl) {
                if (HiveMetaStore.saslServer != null && HiveMetaStore.saslServer.getRemoteAddress() != null) {
                    address = String.valueOf(HiveMetaStore.saslServer.getRemoteAddress());
                }
            }
            else {
                address = getIpAddress();
            }
            if (address == null) {
                address = "unknown-ip-addr";
            }
            HMSHandler.auditLog.info(fmt.format("ugi=%s\tip=%s\tcmd=%s\t", ugi.getUserName(), address, cmd).toString());
        }
        
        public static void setIpAddress(final String ipAddress) {
            HMSHandler.threadLocalIpAddress.set(ipAddress);
        }
        
        public static String getIpAddress() {
            return HMSHandler.threadLocalIpAddress.get();
        }
        
        public static Integer get() {
            return HMSHandler.threadLocalId.get();
        }
        
        public HMSHandler(final String name) throws MetaException {
            this(name, new HiveConf(HMSHandler.class), true);
        }
        
        public HMSHandler(final String name, final HiveConf conf) throws MetaException {
            this(name, conf, true);
        }
        
        public HMSHandler(final String name, final HiveConf conf, final boolean init) throws MetaException {
            super(name);
            this.classLoader = Thread.currentThread().getContextClassLoader();
            if (this.classLoader == null) {
                this.classLoader = Configuration.class.getClassLoader();
            }
            this.hiveConf = conf;
            if (init) {
                this.init();
            }
        }
        
        public HiveConf getHiveConf() {
            return this.hiveConf;
        }
        
        @Override
        public void init() throws MetaException {
            this.rawStoreClassName = this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_RAW_STORE_IMPL);
            this.initListeners = MetaStoreUtils.getMetaStoreListeners(MetaStoreInitListener.class, this.hiveConf, this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_INIT_HOOKS));
            for (final MetaStoreInitListener singleInitListener : this.initListeners) {
                final MetaStoreInitContext context = new MetaStoreInitContext();
                singleInitListener.onInit(context);
            }
            final String alterHandlerName = this.hiveConf.get("hive.metastore.alter.impl", HiveAlterHandler.class.getName());
            this.alterHandler = ReflectionUtils.newInstance(MetaStoreUtils.getClass(alterHandlerName), this.hiveConf);
            this.wh = new Warehouse(this.hiveConf);
            synchronized (HMSHandler.class) {
                if (HMSHandler.currentUrl == null || !HMSHandler.currentUrl.equals(MetaStoreInit.getConnectionURL(this.hiveConf))) {
                    this.createDefaultDB();
                    this.createDefaultRoles();
                    this.addAdminUsers();
                    HMSHandler.currentUrl = MetaStoreInit.getConnectionURL(this.hiveConf);
                }
            }
            if (this.hiveConf.getBoolean("hive.metastore.metrics.enabled", false)) {
                try {
                    Metrics.init();
                }
                catch (Exception e) {
                    HMSHandler.LOG.error("error in Metrics init: " + e.getClass().getName() + " " + e.getMessage(), e);
                }
            }
            this.preListeners = MetaStoreUtils.getMetaStoreListeners(MetaStorePreEventListener.class, this.hiveConf, this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_PRE_EVENT_LISTENERS));
            (this.listeners = MetaStoreUtils.getMetaStoreListeners(MetaStoreEventListener.class, this.hiveConf, this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_EVENT_LISTENERS))).add(new SessionPropertiesListener(this.hiveConf));
            this.endFunctionListeners = MetaStoreUtils.getMetaStoreListeners(MetaStoreEndFunctionListener.class, this.hiveConf, this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_END_FUNCTION_LISTENERS));
            final String partitionValidationRegex = this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_PARTITION_NAME_WHITELIST_PATTERN);
            if (partitionValidationRegex != null && !partitionValidationRegex.isEmpty()) {
                this.partitionValidationPattern = Pattern.compile(partitionValidationRegex);
            }
            else {
                this.partitionValidationPattern = null;
            }
            final long cleanFreq = this.hiveConf.getTimeVar(HiveConf.ConfVars.METASTORE_EVENT_CLEAN_FREQ, TimeUnit.MILLISECONDS);
            if (cleanFreq > 0L) {
                final Timer cleaner = new Timer("Metastore Events Cleaner Thread", true);
                cleaner.schedule(new EventCleanerTask(this), cleanFreq, cleanFreq);
            }
        }
        
        private String addPrefix(final String s) {
            return HMSHandler.threadLocalId.get() + ": " + s;
        }
        
        @Override
        public void setConf(final Configuration conf) {
            HMSHandler.threadLocalConf.set(conf);
            final RawStore ms = HMSHandler.threadLocalMS.get();
            if (ms != null) {
                ms.setConf(conf);
            }
        }
        
        @Override
        public Configuration getConf() {
            Configuration conf = HMSHandler.threadLocalConf.get();
            if (conf == null) {
                conf = new Configuration(this.hiveConf);
                HMSHandler.threadLocalConf.set(conf);
            }
            return conf;
        }
        
        public Warehouse getWh() {
            return this.wh;
        }
        
        @Override
        public void setMetaConf(final String key, final String value) throws MetaException {
            final HiveConf.ConfVars confVar = HiveConf.getMetaConf(key);
            if (confVar == null) {
                throw new MetaException("Invalid configuration key " + key);
            }
            final String validate = confVar.validate(value);
            if (validate != null) {
                throw new MetaException("Invalid configuration value " + value + " for key " + key + " by " + validate);
            }
            final Configuration configuration = this.getConf();
            final String oldValue = configuration.get(key);
            configuration.set(key, value);
            for (final MetaStoreEventListener listener : this.listeners) {
                listener.onConfigChange(new ConfigChangeEvent(this, key, oldValue, value));
            }
        }
        
        @Override
        public String getMetaConf(final String key) throws MetaException {
            final HiveConf.ConfVars confVar = HiveConf.getMetaConf(key);
            if (confVar == null) {
                throw new MetaException("Invalid configuration key " + key);
            }
            return this.getConf().get(key);
        }
        
        @InterfaceAudience.LimitedPrivate({ "HCATALOG" })
        @InterfaceStability.Evolving
        public RawStore getMS() throws MetaException {
            RawStore ms = HMSHandler.threadLocalMS.get();
            if (ms == null) {
                ms = this.newRawStore();
                ms.verifySchema();
                HMSHandler.threadLocalMS.set(ms);
                ms = HMSHandler.threadLocalMS.get();
            }
            return ms;
        }
        
        private TxnHandler getTxnHandler() {
            TxnHandler txn = HMSHandler.threadLocalTxn.get();
            if (txn == null) {
                txn = new TxnHandler(this.hiveConf);
                HMSHandler.threadLocalTxn.set(txn);
            }
            return txn;
        }
        
        private RawStore newRawStore() throws MetaException {
            HMSHandler.LOG.info(this.addPrefix("Opening raw store with implemenation class:" + this.rawStoreClassName));
            final Configuration conf = this.getConf();
            return RawStoreProxy.getProxy(this.hiveConf, conf, this.rawStoreClassName, HMSHandler.threadLocalId.get());
        }
        
        private void createDefaultDB_core(final RawStore ms) throws MetaException, InvalidObjectException {
            try {
                ms.getDatabase("default");
            }
            catch (NoSuchObjectException e) {
                final Database db = new Database("default", "Default Hive database", this.wh.getDefaultDatabasePath("default").toString(), null);
                db.setOwnerName("public");
                db.setOwnerType(PrincipalType.ROLE);
                ms.createDatabase(db);
            }
        }
        
        private void createDefaultDB() throws MetaException {
            try {
                this.createDefaultDB_core(this.getMS());
            }
            catch (JDOException e) {
                HMSHandler.LOG.warn("Retrying creating default database after error: " + e.getMessage(), e);
                try {
                    this.createDefaultDB_core(this.getMS());
                }
                catch (InvalidObjectException e2) {
                    throw new MetaException(e2.getMessage());
                }
            }
            catch (InvalidObjectException e3) {
                throw new MetaException(e3.getMessage());
            }
        }
        
        private void createDefaultRoles() throws MetaException {
            try {
                this.createDefaultRoles_core();
            }
            catch (JDOException e) {
                HMSHandler.LOG.warn("Retrying creating default roles after error: " + e.getMessage(), e);
                this.createDefaultRoles_core();
            }
        }
        
        private void createDefaultRoles_core() throws MetaException {
            final RawStore ms = this.getMS();
            try {
                ms.addRole("admin", "admin");
            }
            catch (InvalidObjectException e) {
                HMSHandler.LOG.debug("admin role already exists", e);
            }
            catch (NoSuchObjectException e2) {
                HMSHandler.LOG.warn("Unexpected exception while adding admin roles", e2);
            }
            HMSHandler.LOG.info("Added admin role in metastore");
            try {
                ms.addRole("public", "public");
            }
            catch (InvalidObjectException e) {
                HMSHandler.LOG.debug("public role already exists", e);
            }
            catch (NoSuchObjectException e2) {
                HMSHandler.LOG.warn("Unexpected exception while adding public roles", e2);
            }
            HMSHandler.LOG.info("Added public role in metastore");
            final PrivilegeBag privs = new PrivilegeBag();
            privs.addToPrivileges(new HiveObjectPrivilege(new HiveObjectRef(HiveObjectType.GLOBAL, null, null, null, null), "admin", PrincipalType.ROLE, new PrivilegeGrantInfo("All", 0, "admin", PrincipalType.ROLE, true)));
            try {
                ms.grantPrivileges(privs);
            }
            catch (InvalidObjectException e3) {
                HMSHandler.LOG.debug("Failed while granting global privs to admin", e3);
            }
            catch (NoSuchObjectException e4) {
                HMSHandler.LOG.warn("Failed while granting global privs to admin", e4);
            }
        }
        
        private void addAdminUsers() throws MetaException {
            try {
                this.addAdminUsers_core();
            }
            catch (JDOException e) {
                HMSHandler.LOG.warn("Retrying adding admin users after error: " + e.getMessage(), e);
                this.addAdminUsers_core();
            }
        }
        
        private void addAdminUsers_core() throws MetaException {
            final String userStr = HiveConf.getVar(this.hiveConf, HiveConf.ConfVars.USERS_IN_ADMIN_ROLE, "").trim();
            if (userStr.isEmpty()) {
                HMSHandler.LOG.info("No user is added in admin role, since config is empty");
                return;
            }
            final Iterator<String> users = Splitter.on(",").trimResults().omitEmptyStrings().split(userStr).iterator();
            if (!users.hasNext()) {
                HMSHandler.LOG.info("No user is added in admin role, since config value " + userStr + " is in incorrect format. We accept comma seprated list of users.");
                return;
            }
            final RawStore ms = this.getMS();
            Role adminRole;
            try {
                adminRole = ms.getRole("admin");
            }
            catch (NoSuchObjectException e) {
                HMSHandler.LOG.error("Failed to retrieve just added admin role", e);
                return;
            }
            while (users.hasNext()) {
                final String userName = users.next();
                try {
                    ms.grantRole(adminRole, userName, PrincipalType.USER, "admin", PrincipalType.ROLE, true);
                    HMSHandler.LOG.info("Added " + userName + " to admin role");
                }
                catch (NoSuchObjectException e2) {
                    HMSHandler.LOG.error("Failed to add " + userName + " in admin role", e2);
                }
                catch (InvalidObjectException e3) {
                    HMSHandler.LOG.debug(userName + " already in admin role", e3);
                }
            }
        }
        
        private void logInfo(final String m) {
            HMSHandler.LOG.info(HMSHandler.threadLocalId.get().toString() + ": " + m);
            this.logAuditEvent(m);
        }
        
        private String startFunction(final String function, final String extraLogInfo) {
            this.incrementCounter(function);
            this.logInfo(((getIpAddress() == null) ? "" : ("source:" + getIpAddress() + " ")) + function + extraLogInfo);
            try {
                Metrics.startScope(function);
            }
            catch (IOException e) {
                HMSHandler.LOG.debug("Exception when starting metrics scope" + e.getClass().getName() + " " + e.getMessage(), e);
            }
            return function;
        }
        
        private String startFunction(final String function) {
            return this.startFunction(function, "");
        }
        
        private String startTableFunction(final String function, final String db, final String tbl) {
            return this.startFunction(function, " : db=" + db + " tbl=" + tbl);
        }
        
        private String startMultiTableFunction(final String function, final String db, final List<String> tbls) {
            final String tableNames = org.apache.commons.lang.StringUtils.join(tbls, ",");
            return this.startFunction(function, " : db=" + db + " tbls=" + tableNames);
        }
        
        private String startPartitionFunction(final String function, final String db, final String tbl, final List<String> partVals) {
            return this.startFunction(function, " : db=" + db + " tbl=" + tbl + "[" + org.apache.commons.lang.StringUtils.join(partVals, ",") + "]");
        }
        
        private String startPartitionFunction(final String function, final String db, final String tbl, final Map<String, String> partName) {
            return this.startFunction(function, " : db=" + db + " tbl=" + tbl + "partition=" + partName);
        }
        
        private void endFunction(final String function, final boolean successful, final Exception e) {
            this.endFunction(function, successful, e, null);
        }
        
        private void endFunction(final String function, final boolean successful, final Exception e, final String inputTableName) {
            this.endFunction(function, new MetaStoreEndFunctionContext(successful, e, inputTableName));
        }
        
        private void endFunction(final String function, final MetaStoreEndFunctionContext context) {
            try {
                Metrics.endScope(function);
            }
            catch (IOException e) {
                HMSHandler.LOG.debug("Exception when closing metrics scope" + e);
            }
            for (final MetaStoreEndFunctionListener listener : this.endFunctionListeners) {
                listener.onEndFunction(function, context);
            }
        }
        
        @Override
        public fb_status getStatus() {
            return fb_status.ALIVE;
        }
        
        @Override
        public void shutdown() {
            this.logInfo("Shutting down the object store...");
            final RawStore ms = HMSHandler.threadLocalMS.get();
            if (ms != null) {
                try {
                    ms.shutdown();
                }
                finally {
                    HMSHandler.threadLocalMS.remove();
                }
            }
            this.logInfo("Metastore shutdown complete.");
        }
        
        @Override
        public AbstractMap<String, Long> getCounters() {
            final AbstractMap<String, Long> counters = super.getCounters();
            if (this.endFunctionListeners != null) {
                for (final MetaStoreEndFunctionListener listener : this.endFunctionListeners) {
                    listener.exportCounters(counters);
                }
            }
            return counters;
        }
        
        private void create_database_core(final RawStore ms, final Database db) throws AlreadyExistsException, InvalidObjectException, MetaException {
            if (!MetaStoreUtils.validateName(db.getName())) {
                throw new InvalidObjectException(db.getName() + " is not a valid database name");
            }
            if (null == db.getLocationUri()) {
                db.setLocationUri(this.wh.getDefaultDatabasePath(db.getName()).toString());
            }
            else {
                db.setLocationUri(this.wh.getDnsPath(new Path(db.getLocationUri())).toString());
            }
            final Path dbPath = new Path(db.getLocationUri());
            boolean success = false;
            boolean madeDir = false;
            try {
                this.firePreEvent(new PreCreateDatabaseEvent(db, this));
                if (!this.wh.isDir(dbPath)) {
                    if (!this.wh.mkdirs(dbPath, true)) {
                        throw new MetaException("Unable to create database path " + dbPath + ", failed to create database " + db.getName());
                    }
                    madeDir = true;
                }
                ms.openTransaction();
                ms.createDatabase(db);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                    if (madeDir) {
                        this.wh.deleteDir(dbPath, true);
                    }
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    listener.onCreateDatabase(new CreateDatabaseEvent(db, success, this));
                }
            }
        }
        
        @Override
        public void create_database(final Database db) throws AlreadyExistsException, InvalidObjectException, MetaException {
            this.startFunction("create_database", ": " + db.toString());
            boolean success = false;
            Exception ex = null;
            try {
                try {
                    if (null != this.get_database_core(db.getName())) {
                        throw new AlreadyExistsException("Database " + db.getName() + " already exists");
                    }
                }
                catch (NoSuchObjectException ex2) {}
                if (HiveMetaStore.TEST_TIMEOUT_ENABLED) {
                    try {
                        Thread.sleep(HiveMetaStore.TEST_TIMEOUT_VALUE);
                    }
                    catch (InterruptedException ex3) {}
                    Deadline.checkTimeout();
                }
                this.create_database_core(this.getMS(), db);
                success = true;
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("create_database", success, ex);
            }
        }
        
        @Override
        public Database get_database(final String name) throws NoSuchObjectException, MetaException {
            this.startFunction("get_database", ": " + name);
            Database db = null;
            Exception ex = null;
            try {
                db = this.get_database_core(name);
                this.firePreEvent(new PreReadDatabaseEvent(db, this));
            }
            catch (MetaException e) {
                ex = e;
                throw e;
            }
            catch (NoSuchObjectException e2) {
                ex = e2;
                throw e2;
            }
            finally {
                this.endFunction("get_database", db != null, ex);
            }
            return db;
        }
        
        public Database get_database_core(final String name) throws NoSuchObjectException, MetaException {
            Database db = null;
            try {
                db = this.getMS().getDatabase(name);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (NoSuchObjectException e2) {
                throw e2;
            }
            catch (Exception e3) {
                assert e3 instanceof RuntimeException;
                throw (RuntimeException)e3;
            }
            return db;
        }
        
        @Override
        public void alter_database(final String dbName, final Database db) throws NoSuchObjectException, TException, MetaException {
            this.startFunction("alter_database" + dbName);
            boolean success = false;
            Exception ex = null;
            try {
                this.getMS().alterDatabase(dbName, db);
                success = true;
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("alter_database", success, ex);
            }
        }
        
        private void drop_database_core(final RawStore ms, final String name, final boolean deleteData, final boolean cascade) throws NoSuchObjectException, InvalidOperationException, MetaException, IOException, InvalidObjectException, InvalidInputException {
            boolean success = false;
            Database db = null;
            final List<Path> tablePaths = new ArrayList<Path>();
            List<Path> partitionPaths = new ArrayList<Path>();
            try {
                ms.openTransaction();
                db = ms.getDatabase(name);
                this.firePreEvent(new PreDropDatabaseEvent(db, this));
                final List<String> allTables = this.get_all_tables(db.getName());
                final List<String> allFunctions = this.get_functions(db.getName(), "*");
                if (!cascade) {
                    if (!allTables.isEmpty()) {
                        throw new InvalidOperationException("Database " + db.getName() + " is not empty. One or more tables exist.");
                    }
                    if (!allFunctions.isEmpty()) {
                        throw new InvalidOperationException("Database " + db.getName() + " is not empty. One or more functions exist.");
                    }
                }
                final Path path = new Path(db.getLocationUri()).getParent();
                if (!this.wh.isWritable(path)) {
                    throw new MetaException("Database not dropped since " + path + " is not writable by " + this.hiveConf.getUser());
                }
                final Path databasePath = this.wh.getDnsPath(this.wh.getDatabasePath(db));
                for (final String funcName : allFunctions) {
                    this.drop_function(name, funcName);
                }
                final int tableBatchSize = HiveConf.getIntVar(this.hiveConf, HiveConf.ConfVars.METASTORE_BATCH_RETRIEVE_MAX);
                int startIndex = 0;
                int endIndex = -1;
                while (endIndex < allTables.size() - 1) {
                    startIndex = endIndex + 1;
                    endIndex += tableBatchSize;
                    if (endIndex >= allTables.size()) {
                        endIndex = allTables.size() - 1;
                    }
                    List<Table> tables = null;
                    try {
                        tables = ms.getTableObjectsByName(name, allTables.subList(startIndex, endIndex));
                    }
                    catch (UnknownDBException e) {
                        throw new MetaException(e.getMessage());
                    }
                    if (tables != null && !tables.isEmpty()) {
                        for (final Table table : tables) {
                            Path tablePath = null;
                            if (table.getSd().getLocation() != null && !this.isExternal(table)) {
                                tablePath = this.wh.getDnsPath(new Path(table.getSd().getLocation()));
                                if (!this.wh.isWritable(tablePath.getParent())) {
                                    throw new MetaException("Database metadata not deleted since table: " + table.getTableName() + " has a parent location " + tablePath.getParent() + " which is not writable by " + this.hiveConf.getUser());
                                }
                                if (!this.isSubdirectory(databasePath, tablePath)) {
                                    tablePaths.add(tablePath);
                                }
                            }
                            partitionPaths = this.dropPartitionsAndGetLocations(ms, name, table.getTableName(), tablePath, table.getPartitionKeys(), deleteData && !this.isExternal(table));
                            this.drop_table(name, table.getTableName(), false);
                        }
                    }
                }
                if (ms.dropDatabase(name)) {
                    success = ms.commitTransaction();
                }
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
                else if (deleteData) {
                    this.deletePartitionData(partitionPaths);
                    for (final Path tablePath2 : tablePaths) {
                        this.deleteTableData(tablePath2);
                    }
                    try {
                        this.wh.deleteDir(new Path(db.getLocationUri()), true);
                    }
                    catch (Exception e2) {
                        HMSHandler.LOG.error("Failed to delete database directory: " + db.getLocationUri() + " " + e2.getMessage());
                    }
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    listener.onDropDatabase(new DropDatabaseEvent(db, success, this));
                }
            }
        }
        
        private boolean isSubdirectory(final Path parent, final Path other) {
            return other.toString().startsWith(parent.toString().endsWith("/") ? parent.toString() : (parent.toString() + "/"));
        }
        
        @Override
        public void drop_database(final String dbName, final boolean deleteData, final boolean cascade) throws NoSuchObjectException, InvalidOperationException, MetaException {
            this.startFunction("drop_database", ": " + dbName);
            if ("default".equalsIgnoreCase(dbName)) {
                this.endFunction("drop_database", false, null);
                throw new MetaException("Can not drop default database");
            }
            boolean success = false;
            Exception ex = null;
            try {
                this.drop_database_core(this.getMS(), dbName, deleteData, cascade);
                success = true;
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e2;
                }
                if (e2 instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("drop_database", success, ex);
            }
        }
        
        @Override
        public List<String> get_databases(final String pattern) throws MetaException {
            this.startFunction("get_databases", ": " + pattern);
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getDatabases(pattern);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_databases", ret != null, ex);
            }
            return ret;
        }
        
        @Override
        public List<String> get_all_databases() throws MetaException {
            this.startFunction("get_all_databases");
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getAllDatabases();
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_all_databases", ret != null, ex);
            }
            return ret;
        }
        
        private void create_type_core(final RawStore ms, final Type type) throws AlreadyExistsException, MetaException, InvalidObjectException {
            if (!MetaStoreUtils.validateName(type.getName())) {
                throw new InvalidObjectException("Invalid type name");
            }
            boolean success = false;
            try {
                ms.openTransaction();
                if (this.is_type_exists(ms, type.getName())) {
                    throw new AlreadyExistsException("Type " + type.getName() + " already exists");
                }
                ms.createType(type);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
            }
        }
        
        @Override
        public boolean create_type(final Type type) throws AlreadyExistsException, MetaException, InvalidObjectException {
            this.startFunction("create_type", ": " + type.toString());
            boolean success = false;
            Exception ex = null;
            try {
                this.create_type_core(this.getMS(), type);
                success = true;
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("create_type", success, ex);
            }
            return success;
        }
        
        @Override
        public Type get_type(final String name) throws MetaException, NoSuchObjectException {
            this.startFunction("get_type", ": " + name);
            Type ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getType(name);
                if (null == ret) {
                    throw new NoSuchObjectException("Type \"" + name + "\" not found.");
                }
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_type", ret != null, ex);
            }
            return ret;
        }
        
        private boolean is_type_exists(final RawStore ms, final String typeName) throws MetaException {
            return ms.getType(typeName) != null;
        }
        
        private void drop_type_core(final RawStore ms, final String typeName) throws NoSuchObjectException, MetaException {
            boolean success = false;
            try {
                ms.openTransaction();
                if (!this.is_type_exists(ms, typeName)) {
                    throw new NoSuchObjectException(typeName + " doesn't exist");
                }
                if (!ms.dropType(typeName)) {
                    throw new MetaException("Unable to drop type " + typeName);
                }
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
            }
        }
        
        @Override
        public boolean drop_type(final String name) throws MetaException, NoSuchObjectException {
            this.startFunction("drop_type", ": " + name);
            boolean success = false;
            Exception ex = null;
            try {
                success = this.getMS().dropType(name);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("drop_type", success, ex);
            }
            return success;
        }
        
        @Override
        public Map<String, Type> get_type_all(final String name) throws MetaException {
            this.startFunction("get_type_all", ": " + name);
            this.endFunction("get_type_all", false, null);
            throw new MetaException("Not yet implemented");
        }
        
        private void create_table_core(final RawStore ms, final Table tbl, final EnvironmentContext envContext) throws AlreadyExistsException, MetaException, InvalidObjectException, NoSuchObjectException {
            if (!MetaStoreUtils.validateName(tbl.getTableName())) {
                throw new InvalidObjectException(tbl.getTableName() + " is not a valid object name");
            }
            String validate = MetaStoreUtils.validateTblColumns(tbl.getSd().getCols());
            if (validate != null) {
                throw new InvalidObjectException("Invalid column " + validate);
            }
            if (tbl.getPartitionKeys() != null) {
                validate = MetaStoreUtils.validateTblColumns(tbl.getPartitionKeys());
                if (validate != null) {
                    throw new InvalidObjectException("Invalid partition column " + validate);
                }
            }
            final SkewedInfo skew = tbl.getSd().getSkewedInfo();
            if (skew != null) {
                validate = MetaStoreUtils.validateSkewedColNames(skew.getSkewedColNames());
                if (validate != null) {
                    throw new InvalidObjectException("Invalid skew column " + validate);
                }
                validate = MetaStoreUtils.validateSkewedColNamesSubsetCol(skew.getSkewedColNames(), tbl.getSd().getCols());
                if (validate != null) {
                    throw new InvalidObjectException("Invalid skew column " + validate);
                }
            }
            Path tblPath = null;
            boolean success = false;
            boolean madeDir = false;
            try {
                this.firePreEvent(new PreCreateTableEvent(tbl, this));
                ms.openTransaction();
                final Database db = ms.getDatabase(tbl.getDbName());
                if (db == null) {
                    throw new NoSuchObjectException("The database " + tbl.getDbName() + " does not exist");
                }
                if (this.is_table_exists(ms, tbl.getDbName(), tbl.getTableName())) {
                    throw new AlreadyExistsException("Table " + tbl.getTableName() + " already exists");
                }
                if (!TableType.VIRTUAL_VIEW.toString().equals(tbl.getTableType())) {
                    if (tbl.getSd().getLocation() == null || tbl.getSd().getLocation().isEmpty()) {
                        tblPath = this.wh.getTablePath(ms.getDatabase(tbl.getDbName()), tbl.getTableName());
                    }
                    else {
                        if (!this.isExternal(tbl) && !MetaStoreUtils.isNonNativeTable(tbl)) {
                            HMSHandler.LOG.warn("Location: " + tbl.getSd().getLocation() + " specified for non-external table:" + tbl.getTableName());
                        }
                        tblPath = this.wh.getDnsPath(new Path(tbl.getSd().getLocation()));
                    }
                    tbl.getSd().setLocation(tblPath.toString());
                }
                if (tblPath != null && !this.wh.isDir(tblPath)) {
                    if (!this.wh.mkdirs(tblPath, true)) {
                        throw new MetaException(tblPath + " is not a directory or unable to create one");
                    }
                    madeDir = true;
                }
                if (HiveConf.getBoolVar(this.hiveConf, HiveConf.ConfVars.HIVESTATSAUTOGATHER) && !MetaStoreUtils.isView(tbl)) {
                    if (tbl.getPartitionKeysSize() == 0) {
                        MetaStoreUtils.updateUnpartitionedTableStatsFast(db, tbl, this.wh, madeDir);
                    }
                    else {
                        MetaStoreUtils.updateUnpartitionedTableStatsFast(db, tbl, this.wh, true);
                    }
                }
                final long time = System.currentTimeMillis() / 1000L;
                tbl.setCreateTime((int)time);
                if (tbl.getParameters() == null || tbl.getParameters().get("transient_lastDdlTime") == null) {
                    tbl.putToParameters("transient_lastDdlTime", Long.toString(time));
                }
                ms.createTable(tbl);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                    if (madeDir) {
                        this.wh.deleteDir(tblPath, true);
                    }
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    final CreateTableEvent createTableEvent = new CreateTableEvent(tbl, success, this);
                    createTableEvent.setEnvironmentContext(envContext);
                    listener.onCreateTable(createTableEvent);
                }
            }
        }
        
        @Override
        public void create_table(final Table tbl) throws AlreadyExistsException, MetaException, InvalidObjectException {
            this.create_table_with_environment_context(tbl, null);
        }
        
        @Override
        public void create_table_with_environment_context(final Table tbl, final EnvironmentContext envContext) throws AlreadyExistsException, MetaException, InvalidObjectException {
            this.startFunction("create_table", ": " + tbl.toString());
            boolean success = false;
            Exception ex = null;
            try {
                this.create_table_core(this.getMS(), tbl, envContext);
                success = true;
            }
            catch (NoSuchObjectException e) {
                ex = e;
                throw new InvalidObjectException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e2;
                }
                if (e2 instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("create_table", success, ex, tbl.getTableName());
            }
        }
        
        private boolean is_table_exists(final RawStore ms, final String dbname, final String name) throws MetaException {
            return ms.getTable(dbname, name) != null;
        }
        
        private boolean drop_table_core(final RawStore ms, final String dbname, final String name, final boolean deleteData, final EnvironmentContext envContext, final String indexName) throws NoSuchObjectException, MetaException, IOException, InvalidObjectException, InvalidInputException {
            boolean success = false;
            boolean isExternal = false;
            Path tblPath = null;
            List<Path> partPaths = null;
            Table tbl = null;
            boolean ifPurge = false;
            try {
                ms.openTransaction();
                tbl = this.get_table_core(dbname, name);
                if (tbl == null) {
                    throw new NoSuchObjectException(name + " doesn't exist");
                }
                if (tbl.getSd() == null) {
                    throw new MetaException("Table metadata is corrupted");
                }
                ifPurge = isMustPurge(envContext, tbl);
                this.firePreEvent(new PreDropTableEvent(tbl, deleteData, this));
                final boolean isIndexTable = this.isIndexTable(tbl);
                if (indexName == null && isIndexTable) {
                    throw new RuntimeException("The table " + name + " is an index table. Please do drop index instead.");
                }
                if (!isIndexTable) {
                    try {
                        for (List<Index> indexes = ms.getIndexes(dbname, name, 32767); indexes != null && indexes.size() > 0; indexes = ms.getIndexes(dbname, name, 32767)) {
                            for (final Index idx : indexes) {
                                this.drop_index_by_name(dbname, name, idx.getIndexName(), true);
                            }
                        }
                    }
                    catch (TException e) {
                        throw new MetaException(e.getMessage());
                    }
                }
                isExternal = this.isExternal(tbl);
                if (tbl.getSd().getLocation() != null) {
                    tblPath = new Path(tbl.getSd().getLocation());
                    if (!this.wh.isWritable(tblPath.getParent())) {
                        final String target = (indexName == null) ? "Table" : "Index table";
                        throw new MetaException(target + " metadata not deleted since " + tblPath.getParent() + " is not writable by " + this.hiveConf.getUser());
                    }
                }
                this.checkTrashPurgeCombination(tblPath, dbname + "." + name, ifPurge);
                partPaths = this.dropPartitionsAndGetLocations(ms, dbname, name, tblPath, tbl.getPartitionKeys(), deleteData && !isExternal);
                if (!ms.dropTable(dbname, name)) {
                    final String tableName = dbname + "." + name;
                    throw new MetaException((indexName == null) ? ("Unable to drop table " + tableName) : ("Unable to drop index table " + tableName + " for index " + indexName));
                }
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
                else if (deleteData && !isExternal) {
                    this.deletePartitionData(partPaths, ifPurge);
                    this.deleteTableData(tblPath, ifPurge);
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    final DropTableEvent dropTableEvent = new DropTableEvent(tbl, success, deleteData, this);
                    dropTableEvent.setEnvironmentContext(envContext);
                    listener.onDropTable(dropTableEvent);
                }
            }
            return success;
        }
        
        private void checkTrashPurgeCombination(final Path pathToData, final String objectName, final boolean ifPurge) throws MetaException {
            if (pathToData == null || ifPurge) {
                return;
            }
            boolean trashEnabled = false;
            try {
                trashEnabled = (0.0f < this.hiveConf.getFloat("fs.trash.interval", -1.0f));
            }
            catch (NumberFormatException ex2) {}
            if (trashEnabled) {
                try {
                    final HadoopShims.HdfsEncryptionShim shim = ShimLoader.getHadoopShims().createHdfsEncryptionShim(FileSystem.get(this.hiveConf), this.hiveConf);
                    if (shim.isPathEncrypted(pathToData)) {
                        throw new MetaException("Unable to drop " + objectName + " because it is in an encryption zone" + " and trash is enabled.  Use PURGE option to skip trash.");
                    }
                }
                catch (IOException ex) {
                    final MetaException e = new MetaException(ex.getMessage());
                    e.initCause(ex);
                    throw e;
                }
            }
        }
        
        private void deleteTableData(final Path tablePath) {
            this.deleteTableData(tablePath, false);
        }
        
        private void deleteTableData(final Path tablePath, final boolean ifPurge) {
            if (tablePath != null) {
                try {
                    this.wh.deleteDir(tablePath, true, ifPurge);
                }
                catch (Exception e) {
                    HMSHandler.LOG.error("Failed to delete table directory: " + tablePath + " " + e.getMessage());
                }
            }
        }
        
        private void deletePartitionData(final List<Path> partPaths) {
            this.deletePartitionData(partPaths, false);
        }
        
        private void deletePartitionData(final List<Path> partPaths, final boolean ifPurge) {
            if (partPaths != null && !partPaths.isEmpty()) {
                for (final Path partPath : partPaths) {
                    try {
                        this.wh.deleteDir(partPath, true, ifPurge);
                    }
                    catch (Exception e) {
                        HMSHandler.LOG.error("Failed to delete partition directory: " + partPath + " " + e.getMessage());
                    }
                }
            }
        }
        
        private List<Path> dropPartitionsAndGetLocations(final RawStore ms, final String dbName, final String tableName, final Path tablePath, final List<FieldSchema> partitionKeys, final boolean checkLocation) throws MetaException, IOException, NoSuchObjectException, InvalidObjectException, InvalidInputException {
            final int partitionBatchSize = HiveConf.getIntVar(this.hiveConf, HiveConf.ConfVars.METASTORE_BATCH_RETRIEVE_MAX);
            Path tableDnsPath = null;
            if (tablePath != null) {
                tableDnsPath = this.wh.getDnsPath(tablePath);
            }
            final List<Path> partPaths = new ArrayList<Path>();
            final Table tbl = ms.getTable(dbName, tableName);
            while (true) {
                final List<Partition> partsToDelete = ms.getPartitions(dbName, tableName, partitionBatchSize);
                if (partsToDelete == null || partsToDelete.isEmpty()) {
                    return partPaths;
                }
                final List<String> partNames = new ArrayList<String>();
                for (final Partition part : partsToDelete) {
                    if (checkLocation && part.getSd() != null && part.getSd().getLocation() != null) {
                        final Path partPath = this.wh.getDnsPath(new Path(part.getSd().getLocation()));
                        if (tableDnsPath == null || (partPath != null && !this.isSubdirectory(tableDnsPath, partPath))) {
                            if (!this.wh.isWritable(partPath.getParent())) {
                                throw new MetaException("Table metadata not deleted since the partition " + Warehouse.makePartName(partitionKeys, part.getValues()) + " has parent location " + partPath.getParent() + " which is not writable " + "by " + this.hiveConf.getUser());
                            }
                            partPaths.add(partPath);
                        }
                    }
                    partNames.add(Warehouse.makePartName(tbl.getPartitionKeys(), part.getValues()));
                }
                ms.dropPartitions(dbName, tableName, partNames);
            }
        }
        
        @Override
        public void drop_table(final String dbname, final String name, final boolean deleteData) throws NoSuchObjectException, MetaException {
            this.drop_table_with_environment_context(dbname, name, deleteData, null);
        }
        
        @Override
        public void drop_table_with_environment_context(final String dbname, final String name, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException {
            this.startTableFunction("drop_table", dbname, name);
            boolean success = false;
            Exception ex = null;
            try {
                success = this.drop_table_core(this.getMS(), dbname, name, deleteData, envContext, null);
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("drop_table", success, ex, name);
            }
        }
        
        private boolean isExternal(final Table table) {
            return MetaStoreUtils.isExternalTable(table);
        }
        
        private boolean isIndexTable(final Table table) {
            return MetaStoreUtils.isIndexTable(table);
        }
        
        @Override
        public Table get_table(final String dbname, final String name) throws MetaException, NoSuchObjectException {
            Table t = null;
            this.startTableFunction("get_table", dbname, name);
            Exception ex = null;
            try {
                t = this.get_table_core(dbname, name);
                this.firePreEvent(new PreReadTableEvent(t, this));
            }
            catch (MetaException e) {
                ex = e;
                throw e;
            }
            catch (NoSuchObjectException e2) {
                ex = e2;
                throw e2;
            }
            finally {
                this.endFunction("get_table", t != null, ex, name);
            }
            return t;
        }
        
        public Table get_table_core(final String dbname, final String name) throws MetaException, NoSuchObjectException {
            Table t;
            try {
                t = this.getMS().getTable(dbname, name);
                if (t == null) {
                    throw new NoSuchObjectException(dbname + "." + name + " table not found");
                }
            }
            catch (Exception e) {
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e;
                }
                throw newMetaException(e);
            }
            return t;
        }
        
        @Override
        public List<Table> get_table_objects_by_name(final String dbname, final List<String> names) throws MetaException, InvalidOperationException, UnknownDBException {
            List<Table> tables = null;
            this.startMultiTableFunction("get_multi_table", dbname, names);
            Exception ex = null;
            try {
                if (dbname == null || dbname.isEmpty()) {
                    throw new UnknownDBException("DB name is null or empty");
                }
                if (names == null) {
                    throw new InvalidOperationException(dbname + " cannot find null tables");
                }
                tables = this.getMS().getTableObjectsByName(dbname, names);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e;
                }
                if (e instanceof UnknownDBException) {
                    throw (UnknownDBException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_multi_table", tables != null, ex, org.apache.commons.lang.StringUtils.join(names, ","));
            }
            return tables;
        }
        
        @Override
        public List<String> get_table_names_by_filter(final String dbName, final String filter, final short maxTables) throws MetaException, InvalidOperationException, UnknownDBException {
            List<String> tables = null;
            this.startFunction("get_table_names_by_filter", ": db = " + dbName + ", filter = " + filter);
            Exception ex = null;
            try {
                if (dbName == null || dbName.isEmpty()) {
                    throw new UnknownDBException("DB name is null or empty");
                }
                if (filter == null) {
                    throw new InvalidOperationException(filter + " cannot apply null filter");
                }
                tables = this.getMS().listTableNamesByFilter(dbName, filter, maxTables);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e;
                }
                if (e instanceof UnknownDBException) {
                    throw (UnknownDBException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_table_names_by_filter", tables != null, ex, org.apache.commons.lang.StringUtils.join(tables, ","));
            }
            return tables;
        }
        
        private Partition append_partition_common(final RawStore ms, final String dbName, final String tableName, final List<String> part_vals, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException {
            final Partition part = new Partition();
            boolean success = false;
            boolean madeDir = false;
            Path partLocation = null;
            Table tbl = null;
            try {
                ms.openTransaction();
                part.setDbName(dbName);
                part.setTableName(tableName);
                part.setValues(part_vals);
                MetaStoreUtils.validatePartitionNameCharacters(part_vals, this.partitionValidationPattern);
                tbl = ms.getTable(part.getDbName(), part.getTableName());
                if (tbl == null) {
                    throw new InvalidObjectException("Unable to add partition because table or database do not exist");
                }
                if (tbl.getSd().getLocation() == null) {
                    throw new MetaException("Cannot append a partition to a view");
                }
                this.firePreEvent(new PreAddPartitionEvent(tbl, part, this));
                part.setSd(tbl.getSd());
                partLocation = new Path(tbl.getSd().getLocation(), Warehouse.makePartName(tbl.getPartitionKeys(), part_vals));
                part.getSd().setLocation(partLocation.toString());
                Partition old_part = null;
                try {
                    old_part = ms.getPartition(part.getDbName(), part.getTableName(), part.getValues());
                }
                catch (NoSuchObjectException e) {
                    old_part = null;
                }
                if (old_part != null) {
                    throw new AlreadyExistsException("Partition already exists:" + part);
                }
                if (!this.wh.isDir(partLocation)) {
                    if (!this.wh.mkdirs(partLocation, true)) {
                        throw new MetaException(partLocation + " is not a directory or unable to create one");
                    }
                    madeDir = true;
                }
                final long time = System.currentTimeMillis() / 1000L;
                part.setCreateTime((int)time);
                part.putToParameters("transient_lastDdlTime", Long.toString(time));
                if (HiveConf.getBoolVar(this.hiveConf, HiveConf.ConfVars.HIVESTATSAUTOGATHER) && !MetaStoreUtils.isView(tbl)) {
                    MetaStoreUtils.updatePartitionStatsFast(part, this.wh, madeDir);
                }
                success = ms.addPartition(part);
                if (success) {
                    success = ms.commitTransaction();
                }
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                    if (madeDir) {
                        this.wh.deleteDir(partLocation, true);
                    }
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    final AddPartitionEvent addPartitionEvent = new AddPartitionEvent(tbl, part, success, this);
                    addPartitionEvent.setEnvironmentContext(envContext);
                    listener.onAddPartition(addPartitionEvent);
                }
            }
            return part;
        }
        
        private void firePreEvent(final PreEventContext event) throws MetaException {
            for (final MetaStorePreEventListener listener : this.preListeners) {
                try {
                    listener.onEvent(event);
                }
                catch (NoSuchObjectException e) {
                    throw new MetaException(e.getMessage());
                }
                catch (InvalidOperationException e2) {
                    throw new MetaException(e2.getMessage());
                }
            }
        }
        
        @Override
        public Partition append_partition(final String dbName, final String tableName, final List<String> part_vals) throws InvalidObjectException, AlreadyExistsException, MetaException {
            return this.append_partition_with_environment_context(dbName, tableName, part_vals, null);
        }
        
        @Override
        public Partition append_partition_with_environment_context(final String dbName, final String tableName, final List<String> part_vals, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException {
            this.startPartitionFunction("append_partition", dbName, tableName, part_vals);
            if (HMSHandler.LOG.isDebugEnabled()) {
                for (final String part : part_vals) {
                    HMSHandler.LOG.debug(part);
                }
            }
            Partition ret = null;
            Exception ex = null;
            try {
                ret = this.append_partition_common(this.getMS(), dbName, tableName, part_vals, envContext);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("append_partition", ret != null, ex, tableName);
            }
            return ret;
        }
        
        private List<Partition> add_partitions_core(final RawStore ms, final String dbName, final String tblName, final List<Partition> parts, final boolean ifNotExists) throws MetaException, InvalidObjectException, AlreadyExistsException, TException {
            this.logInfo("add_partitions");
            boolean success = false;
            final Map<PartValEqWrapper, Boolean> addedPartitions = new HashMap<PartValEqWrapper, Boolean>();
            final List<Partition> result = new ArrayList<Partition>();
            List<Partition> existingParts = null;
            Table tbl = null;
            try {
                ms.openTransaction();
                tbl = ms.getTable(dbName, tblName);
                if (tbl == null) {
                    throw new InvalidObjectException("Unable to add partitions because database or table " + dbName + "." + tblName + " does not exist");
                }
                if (!parts.isEmpty()) {
                    this.firePreEvent(new PreAddPartitionEvent(tbl, parts, this));
                }
                for (final Partition part : parts) {
                    if (!part.getTableName().equals(tblName) || !part.getDbName().equals(dbName)) {
                        throw new MetaException("Partition does not belong to target table " + dbName + "." + tblName + ": " + part);
                    }
                    final boolean shouldAdd = this.startAddPartition(ms, part, ifNotExists);
                    if (!shouldAdd) {
                        if (existingParts == null) {
                            existingParts = new ArrayList<Partition>();
                        }
                        existingParts.add(part);
                        HMSHandler.LOG.info("Not adding partition " + part + " as it already exists");
                    }
                    else {
                        final boolean madeDir = this.createLocationForAddedPartition(tbl, part);
                        if (addedPartitions.put(new PartValEqWrapper(part), madeDir) != null) {
                            throw new MetaException("Duplicate partitions in the list: " + part);
                        }
                        this.initializeAddedPartition(tbl, part, madeDir);
                        result.add(part);
                    }
                }
                success = (result.isEmpty() || ms.addPartitions(dbName, tblName, result));
                success = (success && ms.commitTransaction());
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                    for (final Map.Entry<PartValEqWrapper, Boolean> e : addedPartitions.entrySet()) {
                        if (e.getValue()) {
                            this.wh.deleteDir(new Path(e.getKey().partition.getSd().getLocation()), true);
                        }
                    }
                    this.fireMetaStoreAddPartitionEvent(tbl, parts, null, false);
                }
                else {
                    this.fireMetaStoreAddPartitionEvent(tbl, result, null, true);
                    if (existingParts != null) {
                        this.fireMetaStoreAddPartitionEvent(tbl, existingParts, null, false);
                    }
                }
            }
            return result;
        }
        
        @Override
        public AddPartitionsResult add_partitions_req(final AddPartitionsRequest request) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
            final AddPartitionsResult result = new AddPartitionsResult();
            if (request.getParts().isEmpty()) {
                return result;
            }
            try {
                final List<Partition> parts = this.add_partitions_core(this.getMS(), request.getDbName(), request.getTblName(), request.getParts(), request.isIfNotExists());
                if (request.isNeedResult()) {
                    result.setPartitions(parts);
                }
            }
            catch (TException te) {
                throw te;
            }
            catch (Exception e) {
                throw newMetaException(e);
            }
            return result;
        }
        
        @Override
        public int add_partitions(final List<Partition> parts) throws MetaException, InvalidObjectException, AlreadyExistsException {
            this.startFunction("add_partition");
            if (parts.size() == 0) {
                return 0;
            }
            Integer ret = null;
            Exception ex = null;
            try {
                ret = this.add_partitions_core(this.getMS(), parts.get(0).getDbName(), parts.get(0).getTableName(), parts, false).size();
                assert ret == parts.size();
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                throw newMetaException(e);
            }
            finally {
                final String tableName = parts.get(0).getTableName();
                this.endFunction("add_partition", ret != null, ex, tableName);
            }
            return ret;
        }
        
        @Override
        public int add_partitions_pspec(final List<PartitionSpec> partSpecs) throws TException {
            this.logInfo("add_partitions_pspec");
            if (partSpecs.isEmpty()) {
                return 0;
            }
            final String dbName = partSpecs.get(0).getDbName();
            final String tableName = partSpecs.get(0).getTableName();
            return this.add_partitions_pspec_core(this.getMS(), dbName, tableName, partSpecs, false);
        }
        
        private int add_partitions_pspec_core(final RawStore ms, final String dbName, final String tblName, final List<PartitionSpec> partSpecs, final boolean ifNotExists) throws TException {
            boolean success = false;
            final Map<PartValEqWrapperLite, Boolean> addedPartitions = new HashMap<PartValEqWrapperLite, Boolean>();
            final PartitionSpecProxy partitionSpecProxy = PartitionSpecProxy.Factory.get(partSpecs);
            final PartitionSpecProxy.PartitionIterator partitionIterator = partitionSpecProxy.getPartitionIterator();
            Table tbl = null;
            try {
                ms.openTransaction();
                tbl = ms.getTable(dbName, tblName);
                if (tbl == null) {
                    throw new InvalidObjectException("Unable to add partitions because database or table " + dbName + "." + tblName + " does not exist");
                }
                this.firePreEvent(new PreAddPartitionEvent(tbl, partitionSpecProxy, this));
                int nPartitions = 0;
                while (partitionIterator.hasNext()) {
                    final Partition part = partitionIterator.getCurrent();
                    if (!part.getTableName().equals(tblName) || !part.getDbName().equals(dbName)) {
                        throw new MetaException("Partition does not belong to target table " + dbName + "." + tblName + ": " + part);
                    }
                    final boolean shouldAdd = this.startAddPartition(ms, part, ifNotExists);
                    if (!shouldAdd) {
                        HMSHandler.LOG.info("Not adding partition " + part + " as it already exists");
                    }
                    else {
                        final boolean madeDir = this.createLocationForAddedPartition(tbl, part);
                        if (addedPartitions.put(new PartValEqWrapperLite(part), madeDir) != null) {
                            throw new MetaException("Duplicate partitions in the list: " + part);
                        }
                        this.initializeAddedPartition(tbl, partitionIterator, madeDir);
                        ++nPartitions;
                        partitionIterator.next();
                    }
                }
                success = (ms.addPartitions(dbName, tblName, partitionSpecProxy, ifNotExists) && ms.commitTransaction());
                return nPartitions;
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                    for (final Map.Entry<PartValEqWrapperLite, Boolean> e : addedPartitions.entrySet()) {
                        if (e.getValue()) {
                            this.wh.deleteDir(new Path(e.getKey().location), true);
                        }
                    }
                }
                this.fireMetaStoreAddPartitionEvent(tbl, partitionSpecProxy, null, true);
            }
        }
        
        private boolean startAddPartition(final RawStore ms, final Partition part, final boolean ifNotExists) throws MetaException, TException {
            MetaStoreUtils.validatePartitionNameCharacters(part.getValues(), this.partitionValidationPattern);
            final boolean doesExist = ms.doesPartitionExist(part.getDbName(), part.getTableName(), part.getValues());
            if (doesExist && !ifNotExists) {
                throw new AlreadyExistsException("Partition already exists: " + part);
            }
            return !doesExist;
        }
        
        private boolean createLocationForAddedPartition(final Table tbl, final Partition part) throws MetaException {
            Path partLocation = null;
            String partLocationStr = null;
            if (part.getSd() != null) {
                partLocationStr = part.getSd().getLocation();
            }
            if (partLocationStr == null || partLocationStr.isEmpty()) {
                if (tbl.getSd().getLocation() != null) {
                    partLocation = new Path(tbl.getSd().getLocation(), Warehouse.makePartName(tbl.getPartitionKeys(), part.getValues()));
                }
            }
            else {
                if (tbl.getSd().getLocation() == null) {
                    throw new MetaException("Cannot specify location for a view partition");
                }
                partLocation = this.wh.getDnsPath(new Path(partLocationStr));
            }
            boolean result = false;
            if (partLocation != null) {
                part.getSd().setLocation(partLocation.toString());
                if (!this.wh.isDir(partLocation)) {
                    if (!this.wh.mkdirs(partLocation, true)) {
                        throw new MetaException(partLocation + " is not a directory or unable to create one");
                    }
                    result = true;
                }
            }
            return result;
        }
        
        private void initializeAddedPartition(final Table tbl, final Partition part, final boolean madeDir) throws MetaException {
            this.initializeAddedPartition(tbl, new PartitionSpecProxy.SimplePartitionWrapperIterator(part), madeDir);
        }
        
        private void initializeAddedPartition(final Table tbl, final PartitionSpecProxy.PartitionIterator part, final boolean madeDir) throws MetaException {
            if (HiveConf.getBoolVar(this.hiveConf, HiveConf.ConfVars.HIVESTATSAUTOGATHER) && !MetaStoreUtils.isView(tbl)) {
                MetaStoreUtils.updatePartitionStatsFast(part, this.wh, madeDir, false);
            }
            final long time = System.currentTimeMillis() / 1000L;
            part.setCreateTime((int)time);
            if (part.getParameters() == null || part.getParameters().get("transient_lastDdlTime") == null) {
                part.putToParameters("transient_lastDdlTime", Long.toString(time));
            }
            final Map<String, String> tblParams = tbl.getParameters();
            final String inheritProps = this.hiveConf.getVar(HiveConf.ConfVars.METASTORE_PART_INHERIT_TBL_PROPS).trim();
            Set<String> inheritKeys = new HashSet<String>(Arrays.asList(inheritProps.split(",")));
            if (inheritKeys.contains("*")) {
                inheritKeys = tblParams.keySet();
            }
            for (final String key : inheritKeys) {
                final String paramVal = tblParams.get(key);
                if (null != paramVal) {
                    part.putToParameters(key, paramVal);
                }
            }
        }
        
        private Partition add_partition_core(final RawStore ms, final Partition part, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
            boolean success = false;
            Table tbl = null;
            try {
                ms.openTransaction();
                tbl = ms.getTable(part.getDbName(), part.getTableName());
                if (tbl == null) {
                    throw new InvalidObjectException("Unable to add partition because table or database do not exist");
                }
                this.firePreEvent(new PreAddPartitionEvent(tbl, part, this));
                final boolean shouldAdd = this.startAddPartition(ms, part, false);
                assert shouldAdd;
                final boolean madeDir = this.createLocationForAddedPartition(tbl, part);
                try {
                    this.initializeAddedPartition(tbl, part, madeDir);
                    success = ms.addPartition(part);
                }
                finally {
                    if (!success && madeDir) {
                        this.wh.deleteDir(new Path(part.getSd().getLocation()), true);
                    }
                }
                success = (success && ms.commitTransaction());
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
                this.fireMetaStoreAddPartitionEvent(tbl, Arrays.asList(part), envContext, success);
            }
            return part;
        }
        
        private void fireMetaStoreAddPartitionEvent(final Table tbl, final List<Partition> parts, final EnvironmentContext envContext, final boolean success) throws MetaException {
            if (tbl != null && parts != null && !parts.isEmpty()) {
                final AddPartitionEvent addPartitionEvent = new AddPartitionEvent(tbl, parts, success, this);
                addPartitionEvent.setEnvironmentContext(envContext);
                for (final MetaStoreEventListener listener : this.listeners) {
                    listener.onAddPartition(addPartitionEvent);
                }
            }
        }
        
        private void fireMetaStoreAddPartitionEvent(final Table tbl, final PartitionSpecProxy partitionSpec, final EnvironmentContext envContext, final boolean success) throws MetaException {
            if (tbl != null && partitionSpec != null) {
                final AddPartitionEvent addPartitionEvent = new AddPartitionEvent(tbl, partitionSpec, success, this);
                addPartitionEvent.setEnvironmentContext(envContext);
                for (final MetaStoreEventListener listener : this.listeners) {
                    listener.onAddPartition(addPartitionEvent);
                }
            }
        }
        
        @Override
        public Partition add_partition(final Partition part) throws InvalidObjectException, AlreadyExistsException, MetaException {
            return this.add_partition_with_environment_context(part, null);
        }
        
        @Override
        public Partition add_partition_with_environment_context(final Partition part, final EnvironmentContext envContext) throws InvalidObjectException, AlreadyExistsException, MetaException {
            this.startTableFunction("add_partition", part.getDbName(), part.getTableName());
            Partition ret = null;
            Exception ex = null;
            try {
                ret = this.add_partition_core(this.getMS(), part, envContext);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("add_partition", ret != null, ex, (part != null) ? part.getTableName() : null);
            }
            return ret;
        }
        
        @Override
        public Partition exchange_partition(final Map<String, String> partitionSpecs, final String sourceDbName, final String sourceTableName, final String destDbName, final String destTableName) throws MetaException, NoSuchObjectException, InvalidObjectException, InvalidInputException, TException {
            boolean success = false;
            boolean pathCreated = false;
            final RawStore ms = this.getMS();
            ms.openTransaction();
            final Table destinationTable = ms.getTable(destDbName, destTableName);
            final Table sourceTable = ms.getTable(sourceDbName, sourceTableName);
            final List<String> partVals = MetaStoreUtils.getPvals(sourceTable.getPartitionKeys(), partitionSpecs);
            final List<String> partValsPresent = new ArrayList<String>();
            final List<FieldSchema> partitionKeysPresent = new ArrayList<FieldSchema>();
            int i = 0;
            for (final FieldSchema fs : sourceTable.getPartitionKeys()) {
                final String partVal = partVals.get(i);
                if (partVal != null && !partVal.equals("")) {
                    partValsPresent.add(partVal);
                    partitionKeysPresent.add(fs);
                }
                ++i;
            }
            final List<Partition> partitionsToExchange = this.get_partitions_ps(sourceDbName, sourceTableName, partVals, (short)(-1));
            final boolean sameColumns = MetaStoreUtils.compareFieldColumns(sourceTable.getSd().getCols(), destinationTable.getSd().getCols());
            final boolean samePartitions = MetaStoreUtils.compareFieldColumns(sourceTable.getPartitionKeys(), destinationTable.getPartitionKeys());
            if (!sameColumns || !samePartitions) {
                throw new MetaException("The tables have different schemas. Their partitions cannot be exchanged.");
            }
            final Path sourcePath = new Path(sourceTable.getSd().getLocation(), Warehouse.makePartName(partitionKeysPresent, partValsPresent));
            final Path destPath = new Path(destinationTable.getSd().getLocation(), Warehouse.makePartName(partitionKeysPresent, partValsPresent));
            try {
                for (final Partition partition : partitionsToExchange) {
                    final Partition destPartition = new Partition(partition);
                    destPartition.setDbName(destDbName);
                    destPartition.setTableName(destinationTable.getTableName());
                    final Path destPartitionPath = new Path(destinationTable.getSd().getLocation(), Warehouse.makePartName(destinationTable.getPartitionKeys(), partition.getValues()));
                    destPartition.getSd().setLocation(destPartitionPath.toString());
                    ms.addPartition(destPartition);
                    ms.dropPartition(partition.getDbName(), sourceTable.getTableName(), partition.getValues());
                }
                final Path destParentPath = destPath.getParent();
                if (!this.wh.isDir(destParentPath) && !this.wh.mkdirs(destParentPath, true)) {
                    throw new MetaException("Unable to create path " + destParentPath);
                }
                pathCreated = this.wh.renameDir(sourcePath, destPath);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                    if (pathCreated) {
                        this.wh.renameDir(destPath, sourcePath);
                    }
                }
            }
            return new Partition();
        }
        
        private boolean drop_partition_common(final RawStore ms, final String db_name, final String tbl_name, final List<String> part_vals, final boolean deleteData, final EnvironmentContext envContext) throws MetaException, NoSuchObjectException, IOException, InvalidObjectException, InvalidInputException {
            boolean success = false;
            Path partPath = null;
            Table tbl = null;
            Partition part = null;
            boolean isArchived = false;
            Path archiveParentDir = null;
            boolean mustPurge = false;
            try {
                ms.openTransaction();
                part = ms.getPartition(db_name, tbl_name, part_vals);
                tbl = this.get_table_core(db_name, tbl_name);
                this.firePreEvent(new PreDropPartitionEvent(tbl, part, deleteData, this));
                mustPurge = isMustPurge(envContext, tbl);
                if (part == null) {
                    throw new NoSuchObjectException("Partition doesn't exist. " + part_vals);
                }
                isArchived = MetaStoreUtils.isArchived(part);
                if (isArchived) {
                    archiveParentDir = MetaStoreUtils.getOriginalLocation(part);
                    this.verifyIsWritablePath(archiveParentDir);
                    this.checkTrashPurgeCombination(archiveParentDir, db_name + "." + tbl_name + "." + part_vals, mustPurge);
                }
                if (!ms.dropPartition(db_name, tbl_name, part_vals)) {
                    throw new MetaException("Unable to drop partition");
                }
                success = ms.commitTransaction();
                if (part.getSd() != null && part.getSd().getLocation() != null) {
                    partPath = new Path(part.getSd().getLocation());
                    this.verifyIsWritablePath(partPath);
                    this.checkTrashPurgeCombination(partPath, db_name + "." + tbl_name + "." + part_vals, mustPurge);
                }
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
                else if (deleteData && (partPath != null || archiveParentDir != null) && tbl != null && !this.isExternal(tbl)) {
                    if (mustPurge) {
                        HMSHandler.LOG.info("dropPartition() will purge " + partPath + " directly, skipping trash.");
                    }
                    else {
                        HMSHandler.LOG.info("dropPartition() will move " + partPath + " to trash-directory.");
                    }
                    if (isArchived) {
                        assert archiveParentDir != null;
                        this.wh.deleteDir(archiveParentDir, true, mustPurge);
                    }
                    else {
                        assert partPath != null;
                        this.wh.deleteDir(partPath, true, mustPurge);
                        this.deleteParentRecursive(partPath.getParent(), part_vals.size() - 1, mustPurge);
                    }
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    final DropPartitionEvent dropPartitionEvent = new DropPartitionEvent(tbl, part, success, deleteData, this);
                    dropPartitionEvent.setEnvironmentContext(envContext);
                    listener.onDropPartition(dropPartitionEvent);
                }
            }
            return true;
        }
        
        private static boolean isMustPurge(final EnvironmentContext envContext, final Table tbl) {
            return (envContext != null && Boolean.parseBoolean(envContext.getProperties().get("ifPurge"))) || (tbl.isSetParameters() && "true".equalsIgnoreCase(tbl.getParameters().get("auto.purge")));
        }
        
        private void deleteParentRecursive(final Path parent, final int depth, final boolean mustPurge) throws IOException, MetaException {
            if (depth > 0 && parent != null && this.wh.isWritable(parent) && this.wh.isEmpty(parent)) {
                this.wh.deleteDir(parent, true, mustPurge);
                this.deleteParentRecursive(parent.getParent(), depth - 1, mustPurge);
            }
        }
        
        @Override
        public boolean drop_partition(final String db_name, final String tbl_name, final List<String> part_vals, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
            return this.drop_partition_with_environment_context(db_name, tbl_name, part_vals, deleteData, null);
        }
        
        @Override
        public DropPartitionsResult drop_partitions_req(final DropPartitionsRequest request) throws MetaException, NoSuchObjectException, TException {
            final RawStore ms = this.getMS();
            final String dbName = request.getDbName();
            final String tblName = request.getTblName();
            final boolean ifExists = request.isSetIfExists() && request.isIfExists();
            final boolean deleteData = request.isSetDeleteData() && request.isDeleteData();
            final boolean ignoreProtection = request.isSetIgnoreProtection() && request.isIgnoreProtection();
            final boolean needResult = !request.isSetNeedResult() || request.isNeedResult();
            final List<PathAndPartValSize> dirsToDelete = new ArrayList<PathAndPartValSize>();
            final List<Path> archToDelete = new ArrayList<Path>();
            final EnvironmentContext envContext = request.isSetEnvironmentContext() ? request.getEnvironmentContext() : null;
            boolean success = false;
            ms.openTransaction();
            Table tbl = null;
            List<Partition> parts = null;
            boolean mustPurge = false;
            try {
                tbl = this.get_table_core(dbName, tblName);
                mustPurge = isMustPurge(envContext, tbl);
                int minCount = 0;
                final RequestPartsSpec spec = request.getParts();
                List<String> partNames = null;
                if (spec.isSetExprs()) {
                    parts = new ArrayList<Partition>(spec.getExprs().size());
                    for (final DropPartitionsExpr expr : spec.getExprs()) {
                        ++minCount;
                        final List<Partition> result = new ArrayList<Partition>();
                        final boolean hasUnknown = ms.getPartitionsByExpr(dbName, tblName, expr.getExpr(), null, (short)(-1), result);
                        if (hasUnknown) {
                            throw new MetaException("Unexpected unknown partitions to drop");
                        }
                        if (!ignoreProtection && expr.isSetPartArchiveLevel()) {
                            for (final Partition part : parts) {
                                if (MetaStoreUtils.isArchived(part) && MetaStoreUtils.getArchivingLevel(part) < expr.getPartArchiveLevel()) {
                                    throw new MetaException("Cannot drop a subset of partitions  in an archive, partition " + part);
                                }
                            }
                        }
                        parts.addAll(result);
                    }
                }
                else {
                    if (!spec.isSetNames()) {
                        throw new MetaException("Partition spec is not set");
                    }
                    partNames = spec.getNames();
                    minCount = partNames.size();
                    parts = ms.getPartitionsByNames(dbName, tblName, partNames);
                }
                if (parts.size() < minCount && !ifExists) {
                    throw new NoSuchObjectException("Some partitions to drop are missing");
                }
                List<String> colNames = null;
                if (partNames == null) {
                    partNames = new ArrayList<String>(parts.size());
                    colNames = new ArrayList<String>(tbl.getPartitionKeys().size());
                    for (final FieldSchema col : tbl.getPartitionKeys()) {
                        colNames.add(col.getName());
                    }
                }
                for (final Partition part2 : parts) {
                    if (!ignoreProtection && !MetaStoreUtils.canDropPartition(tbl, part2)) {
                        throw new MetaException("Table " + tbl.getTableName() + " Partition " + part2 + " is protected from being dropped");
                    }
                    this.firePreEvent(new PreDropPartitionEvent(tbl, part2, deleteData, this));
                    if (colNames != null) {
                        partNames.add(FileUtils.makePartName(colNames, part2.getValues()));
                    }
                    if (MetaStoreUtils.isArchived(part2)) {
                        final Path archiveParentDir = MetaStoreUtils.getOriginalLocation(part2);
                        this.verifyIsWritablePath(archiveParentDir);
                        this.checkTrashPurgeCombination(archiveParentDir, dbName + "." + tblName + "." + part2.getValues(), mustPurge);
                        archToDelete.add(archiveParentDir);
                    }
                    if (part2.getSd() == null || part2.getSd().getLocation() == null) {
                        continue;
                    }
                    final Path partPath = new Path(part2.getSd().getLocation());
                    this.verifyIsWritablePath(partPath);
                    this.checkTrashPurgeCombination(partPath, dbName + "." + tblName + "." + part2.getValues(), mustPurge);
                    dirsToDelete.add(new PathAndPartValSize(partPath, part2.getValues().size()));
                }
                ms.dropPartitions(dbName, tblName, partNames);
                success = ms.commitTransaction();
                final DropPartitionsResult result2 = new DropPartitionsResult();
                if (needResult) {
                    result2.setPartitions(parts);
                }
                return result2;
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
                else if (deleteData && !this.isExternal(tbl)) {
                    HMSHandler.LOG.info(mustPurge ? "dropPartition() will purge partition-directories directly, skipping trash." : "dropPartition() will move partition-directories to trash-directory.");
                    for (final Path path : archToDelete) {
                        this.wh.deleteDir(path, true, mustPurge);
                    }
                    for (final PathAndPartValSize p : dirsToDelete) {
                        this.wh.deleteDir(p.path, true, mustPurge);
                        try {
                            this.deleteParentRecursive(p.path.getParent(), p.partValSize - 1, mustPurge);
                        }
                        catch (IOException ex) {
                            HMSHandler.LOG.warn("Error from deleteParentRecursive", ex);
                            throw new MetaException("Failed to delete parent: " + ex.getMessage());
                        }
                    }
                }
                if (parts != null) {
                    for (final Partition part3 : parts) {
                        for (final MetaStoreEventListener listener : this.listeners) {
                            final DropPartitionEvent dropPartitionEvent = new DropPartitionEvent(tbl, part3, success, deleteData, this);
                            dropPartitionEvent.setEnvironmentContext(envContext);
                            listener.onDropPartition(dropPartitionEvent);
                        }
                    }
                }
            }
        }
        
        private void verifyIsWritablePath(final Path dir) throws MetaException {
            try {
                if (!this.wh.isWritable(dir.getParent())) {
                    throw new MetaException("Table partition not deleted since " + dir.getParent() + " is not writable by " + this.hiveConf.getUser());
                }
            }
            catch (IOException ex) {
                HMSHandler.LOG.warn("Error from isWritable", ex);
                throw new MetaException("Table partition not deleted since " + dir.getParent() + " access cannot be checked: " + ex.getMessage());
            }
        }
        
        @Override
        public boolean drop_partition_with_environment_context(final String db_name, final String tbl_name, final List<String> part_vals, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException, TException {
            this.startPartitionFunction("drop_partition", db_name, tbl_name, part_vals);
            HMSHandler.LOG.info("Partition values:" + part_vals);
            boolean ret = false;
            Exception ex = null;
            try {
                ret = this.drop_partition_common(this.getMS(), db_name, tbl_name, part_vals, deleteData, envContext);
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("drop_partition", ret, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public Partition get_partition(final String db_name, final String tbl_name, final List<String> part_vals) throws MetaException, NoSuchObjectException {
            this.startPartitionFunction("get_partition", db_name, tbl_name, part_vals);
            Partition ret = null;
            Exception ex = null;
            try {
                this.fireReadTablePreEvent(db_name, tbl_name);
                ret = this.getMS().getPartition(db_name, tbl_name, part_vals);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_partition", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        private void fireReadTablePreEvent(final String dbName, final String tblName) throws MetaException, NoSuchObjectException {
            if (this.preListeners.size() > 0) {
                final Table t = this.getMS().getTable(dbName, tblName);
                if (t == null) {
                    throw new NoSuchObjectException(dbName + "." + tblName + " table not found");
                }
                this.firePreEvent(new PreReadTableEvent(t, this));
            }
        }
        
        @Override
        public Partition get_partition_with_auth(final String db_name, final String tbl_name, final List<String> part_vals, final String user_name, final List<String> group_names) throws MetaException, NoSuchObjectException, TException {
            this.startPartitionFunction("get_partition_with_auth", db_name, tbl_name, part_vals);
            this.fireReadTablePreEvent(db_name, tbl_name);
            Partition ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getPartitionWithAuth(db_name, tbl_name, part_vals, user_name, group_names);
            }
            catch (InvalidObjectException e) {
                ex = e;
                throw new NoSuchObjectException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("get_partition_with_auth", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<Partition> get_partitions(final String db_name, final String tbl_name, final short max_parts) throws NoSuchObjectException, MetaException {
            this.startTableFunction("get_partitions", db_name, tbl_name);
            this.fireReadTablePreEvent(db_name, tbl_name);
            List<Partition> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getPartitions(db_name, tbl_name, max_parts);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_partitions", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<Partition> get_partitions_with_auth(final String dbName, final String tblName, final short maxParts, final String userName, final List<String> groupNames) throws NoSuchObjectException, MetaException, TException {
            this.startTableFunction("get_partitions_with_auth", dbName, tblName);
            List<Partition> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getPartitionsWithAuth(dbName, tblName, maxParts, userName, groupNames);
            }
            catch (InvalidObjectException e) {
                ex = e;
                throw new NoSuchObjectException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("get_partitions_with_auth", ret != null, ex, tblName);
            }
            return ret;
        }
        
        @Override
        public List<PartitionSpec> get_partitions_pspec(final String db_name, final String tbl_name, final int max_parts) throws NoSuchObjectException, MetaException {
            final String dbName = db_name.toLowerCase();
            final String tableName = tbl_name.toLowerCase();
            this.startTableFunction("get_partitions_pspec", dbName, tableName);
            List<PartitionSpec> partitionSpecs = null;
            try {
                final Table table = this.get_table_core(dbName, tableName);
                final List<Partition> partitions = this.get_partitions(dbName, tableName, (short)max_parts);
                if (is_partition_spec_grouping_enabled(table)) {
                    partitionSpecs = this.get_partitionspecs_grouped_by_storage_descriptor(table, partitions);
                }
                else {
                    final PartitionSpec pSpec = new PartitionSpec();
                    pSpec.setPartitionList(new PartitionListComposingSpec(partitions));
                    pSpec.setDbName(dbName);
                    pSpec.setTableName(tableName);
                    pSpec.setRootPath(table.getSd().getLocation());
                    partitionSpecs = Arrays.asList(pSpec);
                }
                return partitionSpecs;
            }
            finally {
                this.endFunction("get_partitions_pspec", partitionSpecs != null && !partitionSpecs.isEmpty(), null, tbl_name);
            }
        }
        
        private List<PartitionSpec> get_partitionspecs_grouped_by_storage_descriptor(final Table table, final List<Partition> partitions) throws NoSuchObjectException, MetaException {
            assert is_partition_spec_grouping_enabled(table);
            final String tablePath = table.getSd().getLocation();
            final ImmutableListMultimap<Boolean, Partition> partitionsWithinTableDirectory = Multimaps.index(partitions, (Function<? super Partition, Boolean>)new Function<Partition, Boolean>() {
                @Override
                public Boolean apply(final Partition input) {
                    return input.getSd().getLocation().startsWith(tablePath);
                }
            });
            final List<PartitionSpec> partSpecs = new ArrayList<PartitionSpec>();
            final Map<StorageDescriptorKey, List<PartitionWithoutSD>> sdToPartList = new HashMap<StorageDescriptorKey, List<PartitionWithoutSD>>();
            if (partitionsWithinTableDirectory.containsKey(true)) {
                final ImmutableList<Partition> partsWithinTableDir = partitionsWithinTableDirectory.get(true);
                for (final Partition partition : partsWithinTableDir) {
                    final PartitionWithoutSD partitionWithoutSD = new PartitionWithoutSD(partition.getValues(), partition.getCreateTime(), partition.getLastAccessTime(), partition.getSd().getLocation().substring(tablePath.length()), partition.getParameters());
                    final StorageDescriptorKey sdKey = new StorageDescriptorKey(partition.getSd());
                    if (!sdToPartList.containsKey(sdKey)) {
                        sdToPartList.put(sdKey, new ArrayList<PartitionWithoutSD>());
                    }
                    sdToPartList.get(sdKey).add(partitionWithoutSD);
                }
                for (final Map.Entry<StorageDescriptorKey, List<PartitionWithoutSD>> entry : sdToPartList.entrySet()) {
                    partSpecs.add(this.getSharedSDPartSpec(table, entry.getKey(), entry.getValue()));
                }
            }
            if (partitionsWithinTableDirectory.containsKey(false)) {
                final List<Partition> partitionsOutsideTableDir = partitionsWithinTableDirectory.get(false);
                if (!partitionsOutsideTableDir.isEmpty()) {
                    final PartitionSpec partListSpec = new PartitionSpec();
                    partListSpec.setDbName(table.getDbName());
                    partListSpec.setTableName(table.getTableName());
                    partListSpec.setPartitionList(new PartitionListComposingSpec(partitionsOutsideTableDir));
                    partSpecs.add(partListSpec);
                }
            }
            return partSpecs;
        }
        
        private PartitionSpec getSharedSDPartSpec(final Table table, final StorageDescriptorKey sdKey, final List<PartitionWithoutSD> partitions) {
            final StorageDescriptor sd = new StorageDescriptor(sdKey.getSd());
            sd.setLocation(table.getSd().getLocation());
            final PartitionSpecWithSharedSD sharedSDPartSpec = new PartitionSpecWithSharedSD(partitions, sd);
            final PartitionSpec ret = new PartitionSpec();
            ret.setRootPath(sd.getLocation());
            ret.setSharedSDPartitionSpec(sharedSDPartSpec);
            ret.setDbName(table.getDbName());
            ret.setTableName(table.getTableName());
            return ret;
        }
        
        private static boolean is_partition_spec_grouping_enabled(final Table table) {
            final Map<String, String> parameters = table.getParameters();
            return parameters.containsKey("hive.hcatalog.partition.spec.grouping.enabled") && parameters.get("hive.hcatalog.partition.spec.grouping.enabled").equalsIgnoreCase("true");
        }
        
        @Override
        public List<String> get_partition_names(final String db_name, final String tbl_name, final short max_parts) throws MetaException, NoSuchObjectException {
            this.startTableFunction("get_partition_names", db_name, tbl_name);
            this.fireReadTablePreEvent(db_name, tbl_name);
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().listPartitionNames(db_name, tbl_name, max_parts);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_partition_names", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public void alter_partition(final String db_name, final String tbl_name, final Partition new_part) throws InvalidOperationException, MetaException, TException {
            this.rename_partition(db_name, tbl_name, null, new_part);
        }
        
        @Override
        public void alter_partition_with_environment_context(final String dbName, final String tableName, final Partition newPartition, final EnvironmentContext envContext) throws InvalidOperationException, MetaException, TException {
            this.rename_partition(dbName, tableName, null, newPartition, envContext);
        }
        
        @Override
        public void rename_partition(final String db_name, final String tbl_name, final List<String> part_vals, final Partition new_part) throws InvalidOperationException, MetaException, TException {
            this.rename_partition(db_name, tbl_name, part_vals, new_part, null);
        }
        
        private void rename_partition(final String db_name, final String tbl_name, final List<String> part_vals, final Partition new_part, final EnvironmentContext envContext) throws InvalidOperationException, MetaException, TException {
            this.startTableFunction("alter_partition", db_name, tbl_name);
            if (HMSHandler.LOG.isInfoEnabled()) {
                HMSHandler.LOG.info("New partition values:" + new_part.getValues());
                if (part_vals != null && part_vals.size() > 0) {
                    HMSHandler.LOG.info("Old Partition values:" + part_vals);
                }
            }
            Partition oldPart = null;
            Exception ex = null;
            try {
                this.firePreEvent(new PreAlterPartitionEvent(db_name, tbl_name, part_vals, new_part, this));
                if (part_vals != null && !part_vals.isEmpty()) {
                    MetaStoreUtils.validatePartitionNameCharacters(new_part.getValues(), this.partitionValidationPattern);
                }
                oldPart = this.alterHandler.alterPartition(this.getMS(), this.wh, db_name, tbl_name, part_vals, new_part);
                Table table = null;
                for (final MetaStoreEventListener listener : this.listeners) {
                    if (table == null) {
                        table = this.getMS().getTable(db_name, tbl_name);
                    }
                    final AlterPartitionEvent alterPartitionEvent = new AlterPartitionEvent(oldPart, new_part, table, true, this);
                    alterPartitionEvent.setEnvironmentContext(envContext);
                    listener.onAlterPartition(alterPartitionEvent);
                }
            }
            catch (InvalidObjectException e) {
                ex = e;
                throw new InvalidOperationException(e.getMessage());
            }
            catch (AlreadyExistsException e2) {
                ex = e2;
                throw new InvalidOperationException(e2.getMessage());
            }
            catch (Exception e3) {
                ex = e3;
                if (e3 instanceof MetaException) {
                    throw (MetaException)e3;
                }
                if (e3 instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e3;
                }
                if (e3 instanceof TException) {
                    throw (TException)e3;
                }
                throw newMetaException(e3);
            }
            finally {
                this.endFunction("alter_partition", oldPart != null, ex, tbl_name);
            }
        }
        
        @Override
        public void alter_partitions(final String db_name, final String tbl_name, final List<Partition> new_parts) throws InvalidOperationException, MetaException, TException {
            this.startTableFunction("alter_partitions", db_name, tbl_name);
            if (HMSHandler.LOG.isInfoEnabled()) {
                for (final Partition tmpPart : new_parts) {
                    HMSHandler.LOG.info("New partition values:" + tmpPart.getValues());
                }
            }
            List<Partition> oldParts = null;
            Exception ex = null;
            try {
                for (final Partition tmpPart2 : new_parts) {
                    this.firePreEvent(new PreAlterPartitionEvent(db_name, tbl_name, null, tmpPart2, this));
                }
                oldParts = this.alterHandler.alterPartitions(this.getMS(), this.wh, db_name, tbl_name, new_parts);
                final Iterator<Partition> olditr = oldParts.iterator();
                Table table = null;
                for (final Partition tmpPart3 : new_parts) {
                    Partition oldTmpPart = null;
                    if (!olditr.hasNext()) {
                        throw new InvalidOperationException("failed to alterpartitions");
                    }
                    oldTmpPart = olditr.next();
                    for (final MetaStoreEventListener listener : this.listeners) {
                        if (table == null) {
                            table = this.getMS().getTable(db_name, tbl_name);
                        }
                        final AlterPartitionEvent alterPartitionEvent = new AlterPartitionEvent(oldTmpPart, tmpPart3, table, true, this);
                        listener.onAlterPartition(alterPartitionEvent);
                    }
                }
            }
            catch (InvalidObjectException e) {
                ex = e;
                throw new InvalidOperationException(e.getMessage());
            }
            catch (AlreadyExistsException e2) {
                ex = e2;
                throw new InvalidOperationException(e2.getMessage());
            }
            catch (Exception e3) {
                ex = e3;
                if (e3 instanceof MetaException) {
                    throw (MetaException)e3;
                }
                if (e3 instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e3;
                }
                if (e3 instanceof TException) {
                    throw (TException)e3;
                }
                throw newMetaException(e3);
            }
            finally {
                this.endFunction("alter_partition", oldParts != null, ex, tbl_name);
            }
        }
        
        @Override
        public void alter_index(final String dbname, final String base_table_name, final String index_name, final Index newIndex) throws InvalidOperationException, MetaException {
            this.startFunction("alter_index", ": db=" + dbname + " base_tbl=" + base_table_name + " idx=" + index_name + " newidx=" + newIndex.getIndexName());
            newIndex.putToParameters("transient_lastDdlTime", Long.toString(System.currentTimeMillis() / 1000L));
            boolean success = false;
            Exception ex = null;
            Index oldIndex = null;
            try {
                oldIndex = this.get_index_by_name(dbname, base_table_name, index_name);
                this.firePreEvent(new PreAlterIndexEvent(oldIndex, newIndex, this));
                this.getMS().alterIndex(dbname, base_table_name, index_name, newIndex);
                success = true;
            }
            catch (InvalidObjectException e) {
                ex = e;
                throw new InvalidOperationException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("alter_index", success, ex, base_table_name);
                for (final MetaStoreEventListener listener : this.listeners) {
                    final AlterIndexEvent alterIndexEvent = new AlterIndexEvent(oldIndex, newIndex, success, this);
                    listener.onAlterIndex(alterIndexEvent);
                }
            }
        }
        
        @Override
        public String getVersion() throws TException {
            this.endFunction(this.startFunction("getVersion"), true, null);
            return "3.0";
        }
        
        @Override
        public void alter_table(final String dbname, final String name, final Table newTable) throws InvalidOperationException, MetaException {
            this.alter_table_core(dbname, name, newTable, null, false);
        }
        
        @Override
        public void alter_table_with_cascade(final String dbname, final String name, final Table newTable, final boolean cascade) throws InvalidOperationException, MetaException {
            this.alter_table_core(dbname, name, newTable, null, cascade);
        }
        
        @Override
        public void alter_table_with_environment_context(final String dbname, final String name, final Table newTable, final EnvironmentContext envContext) throws InvalidOperationException, MetaException {
            this.alter_table_core(dbname, name, newTable, envContext, false);
        }
        
        private void alter_table_core(final String dbname, final String name, final Table newTable, final EnvironmentContext envContext, final boolean cascade) throws InvalidOperationException, MetaException {
            this.startFunction("alter_table", ": db=" + dbname + " tbl=" + name + " newtbl=" + newTable.getTableName());
            if (newTable.getParameters() == null || newTable.getParameters().get("transient_lastDdlTime") == null) {
                newTable.putToParameters("transient_lastDdlTime", Long.toString(System.currentTimeMillis() / 1000L));
            }
            boolean success = false;
            Exception ex = null;
            try {
                final Table oldt = this.get_table_core(dbname, name);
                this.firePreEvent(new PreAlterTableEvent(oldt, newTable, this));
                this.alterHandler.alterTable(this.getMS(), this.wh, dbname, name, newTable, cascade);
                success = true;
                for (final MetaStoreEventListener listener : this.listeners) {
                    final AlterTableEvent alterTableEvent = new AlterTableEvent(oldt, newTable, success, this);
                    alterTableEvent.setEnvironmentContext(envContext);
                    listener.onAlterTable(alterTableEvent);
                }
            }
            catch (NoSuchObjectException e) {
                ex = e;
                throw new InvalidOperationException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof InvalidOperationException) {
                    throw (InvalidOperationException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("alter_table", success, ex, name);
            }
        }
        
        @Override
        public List<String> get_tables(final String dbname, final String pattern) throws MetaException {
            this.startFunction("get_tables", ": db=" + dbname + " pat=" + pattern);
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getTables(dbname, pattern);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_tables", ret != null, ex);
            }
            return ret;
        }
        
        @Override
        public List<String> get_all_tables(final String dbname) throws MetaException {
            this.startFunction("get_all_tables", ": db=" + dbname);
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getAllTables(dbname);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_all_tables", ret != null, ex);
            }
            return ret;
        }
        
        @Override
        public List<FieldSchema> get_fields(final String db, final String tableName) throws MetaException, UnknownTableException, UnknownDBException {
            return this.get_fields_with_environment_context(db, tableName, null);
        }
        
        @Override
        public List<FieldSchema> get_fields_with_environment_context(final String db, final String tableName, final EnvironmentContext envContext) throws MetaException, UnknownTableException, UnknownDBException {
            this.startFunction("get_fields_with_environment_context", ": db=" + db + "tbl=" + tableName);
            final String[] names = tableName.split("\\.");
            final String base_table_name = names[0];
            List<FieldSchema> ret = null;
            Exception ex = null;
            ClassLoader orgHiveLoader = null;
            Configuration curConf = this.hiveConf;
            try {
                Table tbl;
                try {
                    tbl = this.get_table_core(db, base_table_name);
                }
                catch (NoSuchObjectException e) {
                    throw new UnknownTableException(e.getMessage());
                }
                if (null == tbl.getSd().getSerdeInfo().getSerializationLib() || this.hiveConf.getStringCollection(HiveConf.ConfVars.SERDESUSINGMETASTOREFORSCHEMA.varname).contains(tbl.getSd().getSerdeInfo().getSerializationLib())) {
                    ret = tbl.getSd().getCols();
                }
                else {
                    try {
                        if (envContext != null) {
                            final String addedJars = envContext.getProperties().get("hive.added.jars.path");
                            if (org.apache.commons.lang.StringUtils.isNotBlank(addedJars)) {
                                curConf = this.getConf();
                                orgHiveLoader = curConf.getClassLoader();
                                final ClassLoader loader = MetaStoreUtils.addToClassPath(orgHiveLoader, org.apache.commons.lang.StringUtils.split(addedJars, ","));
                                curConf.setClassLoader(loader);
                            }
                        }
                        final Deserializer s = MetaStoreUtils.getDeserializer(curConf, tbl, false);
                        ret = MetaStoreUtils.getFieldsFromDeserializer(tableName, s);
                    }
                    catch (SerDeException e2) {
                        StringUtils.stringifyException(e2);
                        throw new MetaException(e2.getMessage());
                    }
                }
            }
            catch (Exception e3) {
                ex = e3;
                if (e3 instanceof UnknownDBException) {
                    throw (UnknownDBException)e3;
                }
                if (e3 instanceof UnknownTableException) {
                    throw (UnknownTableException)e3;
                }
                if (e3 instanceof MetaException) {
                    throw (MetaException)e3;
                }
                throw newMetaException(e3);
            }
            finally {
                if (orgHiveLoader != null) {
                    curConf.setClassLoader(orgHiveLoader);
                }
                this.endFunction("get_fields_with_environment_context", ret != null, ex, tableName);
            }
            return ret;
        }
        
        @Override
        public List<FieldSchema> get_schema(final String db, final String tableName) throws MetaException, UnknownTableException, UnknownDBException {
            return this.get_schema_with_environment_context(db, tableName, null);
        }
        
        @Override
        public List<FieldSchema> get_schema_with_environment_context(final String db, final String tableName, final EnvironmentContext envContext) throws MetaException, UnknownTableException, UnknownDBException {
            this.startFunction("get_schema_with_environment_context", ": db=" + db + "tbl=" + tableName);
            boolean success = false;
            Exception ex = null;
            try {
                final String[] names = tableName.split("\\.");
                final String base_table_name = names[0];
                Table tbl;
                try {
                    tbl = this.get_table_core(db, base_table_name);
                }
                catch (NoSuchObjectException e) {
                    throw new UnknownTableException(e.getMessage());
                }
                final List<FieldSchema> fieldSchemas = this.get_fields_with_environment_context(db, base_table_name, envContext);
                if (tbl == null || fieldSchemas == null) {
                    throw new UnknownTableException(tableName + " doesn't exist");
                }
                if (tbl.getPartitionKeys() != null) {
                    fieldSchemas.addAll(tbl.getPartitionKeys());
                }
                success = true;
                return fieldSchemas;
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof UnknownDBException) {
                    throw (UnknownDBException)e2;
                }
                if (e2 instanceof UnknownTableException) {
                    throw (UnknownTableException)e2;
                }
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                final MetaException me = new MetaException(e2.toString());
                me.initCause(e2);
                throw me;
            }
            finally {
                this.endFunction("get_schema_with_environment_context", success, ex, tableName);
            }
        }
        
        @Override
        public String getCpuProfile(final int profileDurationInSec) throws TException {
            return "";
        }
        
        @Override
        public String get_config_value(final String name, final String defaultValue) throws TException, ConfigValSecurityException {
            this.startFunction("get_config_value", ": name=" + name + " defaultValue=" + defaultValue);
            boolean success = false;
            Exception ex = null;
            try {
                if (name == null) {
                    success = true;
                    return defaultValue;
                }
                if (!Pattern.matches("(hive|hdfs|mapred).*", name)) {
                    throw new ConfigValSecurityException("For security reasons, the config key " + name + " cannot be accessed");
                }
                String toReturn = defaultValue;
                try {
                    toReturn = this.hiveConf.get(name, defaultValue);
                }
                catch (RuntimeException e) {
                    HMSHandler.LOG.error(HMSHandler.threadLocalId.get().toString() + ": " + "RuntimeException thrown in get_config_value - msg: " + e.getMessage() + " cause: " + e.getCause());
                }
                success = true;
                return toReturn;
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof ConfigValSecurityException) {
                    throw (ConfigValSecurityException)e2;
                }
                if (e2 instanceof TException) {
                    throw (TException)e2;
                }
                final TException te = new TException(e2.toString());
                te.initCause(e2);
                throw te;
            }
            finally {
                this.endFunction("get_config_value", success, ex);
            }
        }
        
        private List<String> getPartValsFromName(final RawStore ms, final String dbName, final String tblName, final String partName) throws MetaException, InvalidObjectException {
            final LinkedHashMap<String, String> hm = Warehouse.makeSpecFromName(partName);
            final Table t = ms.getTable(dbName, tblName);
            if (t == null) {
                throw new InvalidObjectException(dbName + "." + tblName + " table not found");
            }
            final List<String> partVals = new ArrayList<String>();
            for (final FieldSchema field : t.getPartitionKeys()) {
                final String key = field.getName();
                final String val = hm.get(key);
                if (val == null) {
                    throw new InvalidObjectException("incomplete partition name - missing " + key);
                }
                partVals.add(val);
            }
            return partVals;
        }
        
        private Partition get_partition_by_name_core(final RawStore ms, final String db_name, final String tbl_name, final String part_name) throws MetaException, NoSuchObjectException, TException {
            this.fireReadTablePreEvent(db_name, tbl_name);
            List<String> partVals = null;
            try {
                partVals = this.getPartValsFromName(ms, db_name, tbl_name, part_name);
            }
            catch (InvalidObjectException e) {
                throw new NoSuchObjectException(e.getMessage());
            }
            final Partition p = ms.getPartition(db_name, tbl_name, partVals);
            if (p == null) {
                throw new NoSuchObjectException(db_name + "." + tbl_name + " partition (" + part_name + ") not found");
            }
            return p;
        }
        
        @Override
        public Partition get_partition_by_name(final String db_name, final String tbl_name, final String part_name) throws MetaException, NoSuchObjectException, TException {
            this.startFunction("get_partition_by_name", ": db=" + db_name + " tbl=" + tbl_name + " part=" + part_name);
            Partition ret = null;
            Exception ex = null;
            try {
                ret = this.get_partition_by_name_core(this.getMS(), db_name, tbl_name, part_name);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_partition_by_name", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public Partition append_partition_by_name(final String db_name, final String tbl_name, final String part_name) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
            return this.append_partition_by_name_with_environment_context(db_name, tbl_name, part_name, null);
        }
        
        @Override
        public Partition append_partition_by_name_with_environment_context(final String db_name, final String tbl_name, final String part_name, final EnvironmentContext env_context) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
            this.startFunction("append_partition_by_name", ": db=" + db_name + " tbl=" + tbl_name + " part=" + part_name);
            Partition ret = null;
            Exception ex = null;
            try {
                final RawStore ms = this.getMS();
                final List<String> partVals = this.getPartValsFromName(ms, db_name, tbl_name, part_name);
                ret = this.append_partition_common(ms, db_name, tbl_name, partVals, env_context);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof TException) {
                    throw (TException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("append_partition_by_name", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        private boolean drop_partition_by_name_core(final RawStore ms, final String db_name, final String tbl_name, final String part_name, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException, TException, IOException, InvalidObjectException, InvalidInputException {
            List<String> partVals = null;
            try {
                partVals = this.getPartValsFromName(ms, db_name, tbl_name, part_name);
            }
            catch (InvalidObjectException e) {
                throw new NoSuchObjectException(e.getMessage());
            }
            return this.drop_partition_common(ms, db_name, tbl_name, partVals, deleteData, envContext);
        }
        
        @Override
        public boolean drop_partition_by_name(final String db_name, final String tbl_name, final String part_name, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
            return this.drop_partition_by_name_with_environment_context(db_name, tbl_name, part_name, deleteData, null);
        }
        
        @Override
        public boolean drop_partition_by_name_with_environment_context(final String db_name, final String tbl_name, final String part_name, final boolean deleteData, final EnvironmentContext envContext) throws NoSuchObjectException, MetaException, TException {
            this.startFunction("drop_partition_by_name", ": db=" + db_name + " tbl=" + tbl_name + " part=" + part_name);
            boolean ret = false;
            Exception ex = null;
            try {
                ret = this.drop_partition_by_name_core(this.getMS(), db_name, tbl_name, part_name, deleteData, envContext);
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("drop_partition_by_name", ret, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<Partition> get_partitions_ps(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts) throws MetaException, TException, NoSuchObjectException {
            this.startPartitionFunction("get_partitions_ps", db_name, tbl_name, part_vals);
            List<Partition> ret = null;
            Exception ex = null;
            try {
                ret = this.get_partitions_ps_with_auth(db_name, tbl_name, part_vals, max_parts, null, null);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_partitions_ps", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<Partition> get_partitions_ps_with_auth(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts, final String userName, final List<String> groupNames) throws MetaException, TException, NoSuchObjectException {
            this.startPartitionFunction("get_partitions_ps_with_auth", db_name, tbl_name, part_vals);
            this.fireReadTablePreEvent(db_name, tbl_name);
            List<Partition> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().listPartitionsPsWithAuth(db_name, tbl_name, part_vals, max_parts, userName, groupNames);
            }
            catch (InvalidObjectException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("get_partitions_ps_with_auth", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<String> get_partition_names_ps(final String db_name, final String tbl_name, final List<String> part_vals, final short max_parts) throws MetaException, TException, NoSuchObjectException {
            this.startPartitionFunction("get_partitions_names_ps", db_name, tbl_name, part_vals);
            this.fireReadTablePreEvent(db_name, tbl_name);
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().listPartitionNamesPs(db_name, tbl_name, part_vals, max_parts);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_partitions_names_ps", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<String> partition_name_to_vals(final String part_name) throws MetaException, TException {
            if (part_name.length() == 0) {
                return new ArrayList<String>();
            }
            final LinkedHashMap<String, String> map = Warehouse.makeSpecFromName(part_name);
            final List<String> part_vals = new ArrayList<String>();
            part_vals.addAll(map.values());
            return part_vals;
        }
        
        @Override
        public Map<String, String> partition_name_to_spec(final String part_name) throws MetaException, TException {
            if (part_name.length() == 0) {
                return new HashMap<String, String>();
            }
            return Warehouse.makeSpecFromName(part_name);
        }
        
        @Override
        public Index add_index(final Index newIndex, final Table indexTable) throws InvalidObjectException, AlreadyExistsException, MetaException, TException {
            this.startFunction("add_index", ": " + newIndex.toString() + " " + indexTable.toString());
            Index ret = null;
            Exception ex = null;
            try {
                ret = this.add_index_core(this.getMS(), newIndex, indexTable);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof InvalidObjectException) {
                    throw (InvalidObjectException)e;
                }
                if (e instanceof AlreadyExistsException) {
                    throw (AlreadyExistsException)e;
                }
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof TException) {
                    throw (TException)e;
                }
                throw newMetaException(e);
            }
            finally {
                final String tableName = (indexTable != null) ? indexTable.getTableName() : null;
                this.endFunction("add_index", ret != null, ex, tableName);
            }
            return ret;
        }
        
        private Index add_index_core(final RawStore ms, final Index index, final Table indexTable) throws InvalidObjectException, AlreadyExistsException, MetaException {
            boolean success = false;
            boolean indexTableCreated = false;
            final String[] qualified = MetaStoreUtils.getQualifiedName(index.getDbName(), index.getIndexTableName());
            try {
                ms.openTransaction();
                this.firePreEvent(new PreAddIndexEvent(index, this));
                Index old_index = null;
                try {
                    old_index = this.get_index_by_name(index.getDbName(), index.getOrigTableName(), index.getIndexName());
                }
                catch (Exception ex) {}
                if (old_index != null) {
                    throw new AlreadyExistsException("Index already exists:" + index);
                }
                final Table origTbl = ms.getTable(index.getDbName(), index.getOrigTableName());
                if (origTbl == null) {
                    throw new InvalidObjectException("Unable to add index because database or the orginal table do not exist");
                }
                final long time = System.currentTimeMillis() / 1000L;
                Table indexTbl = indexTable;
                if (indexTbl != null) {
                    try {
                        indexTbl = ms.getTable(qualified[0], qualified[1]);
                    }
                    catch (Exception ex2) {}
                    if (indexTbl != null) {
                        throw new InvalidObjectException("Unable to add index because index table already exists");
                    }
                    this.create_table(indexTable);
                    indexTableCreated = true;
                }
                index.setCreateTime((int)time);
                index.putToParameters("transient_lastDdlTime", Long.toString(time));
                ms.addIndex(index);
                success = ms.commitTransaction();
                return index;
            }
            finally {
                if (!success) {
                    if (indexTableCreated) {
                        try {
                            this.drop_table(qualified[0], qualified[1], false);
                        }
                        catch (Exception ex3) {}
                    }
                    ms.rollbackTransaction();
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    final AddIndexEvent addIndexEvent = new AddIndexEvent(index, success, this);
                    listener.onAddIndex(addIndexEvent);
                }
            }
        }
        
        @Override
        public boolean drop_index_by_name(final String dbName, final String tblName, final String indexName, final boolean deleteData) throws NoSuchObjectException, MetaException, TException {
            this.startFunction("drop_index_by_name", ": db=" + dbName + " tbl=" + tblName + " index=" + indexName);
            boolean ret = false;
            Exception ex = null;
            try {
                ret = this.drop_index_by_name_core(this.getMS(), dbName, tblName, indexName, deleteData);
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("drop_index_by_name", ret, ex, tblName);
            }
            return ret;
        }
        
        private boolean drop_index_by_name_core(final RawStore ms, final String dbName, final String tblName, final String indexName, final boolean deleteData) throws NoSuchObjectException, MetaException, TException, IOException, InvalidObjectException, InvalidInputException {
            boolean success = false;
            Index index = null;
            Path tblPath = null;
            List<Path> partPaths = null;
            try {
                ms.openTransaction();
                index = this.get_index_by_name(dbName, tblName, indexName);
                this.firePreEvent(new PreDropIndexEvent(index, this));
                ms.dropIndex(dbName, tblName, indexName);
                final String idxTblName = index.getIndexTableName();
                if (idxTblName != null) {
                    final String[] qualified = MetaStoreUtils.getQualifiedName(index.getDbName(), idxTblName);
                    final Table tbl = this.get_table_core(qualified[0], qualified[1]);
                    if (tbl.getSd() == null) {
                        throw new MetaException("Table metadata is corrupted");
                    }
                    if (tbl.getSd().getLocation() != null) {
                        tblPath = new Path(tbl.getSd().getLocation());
                        if (!this.wh.isWritable(tblPath.getParent())) {
                            throw new MetaException("Index table metadata not deleted since " + tblPath.getParent() + " is not writable by " + this.hiveConf.getUser());
                        }
                    }
                    partPaths = this.dropPartitionsAndGetLocations(ms, qualified[0], qualified[1], tblPath, tbl.getPartitionKeys(), deleteData);
                    if (!ms.dropTable(qualified[0], qualified[1])) {
                        throw new MetaException("Unable to drop underlying data table " + qualified[0] + "." + qualified[1] + " for index " + indexName);
                    }
                }
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
                else if (deleteData && tblPath != null) {
                    this.deletePartitionData(partPaths);
                    this.deleteTableData(tblPath);
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    final DropIndexEvent dropIndexEvent = new DropIndexEvent(index, success, this);
                    listener.onDropIndex(dropIndexEvent);
                }
            }
            return success;
        }
        
        @Override
        public Index get_index_by_name(final String dbName, final String tblName, final String indexName) throws MetaException, NoSuchObjectException, TException {
            this.startFunction("get_index_by_name", ": db=" + dbName + " tbl=" + tblName + " index=" + indexName);
            Index ret = null;
            Exception ex = null;
            try {
                ret = this.get_index_by_name_core(this.getMS(), dbName, tblName, indexName);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_index_by_name", ret != null, ex, tblName);
            }
            return ret;
        }
        
        private Index get_index_by_name_core(final RawStore ms, final String db_name, final String tbl_name, final String index_name) throws MetaException, NoSuchObjectException, TException {
            final Index index = ms.getIndex(db_name, tbl_name, index_name);
            if (index == null) {
                throw new NoSuchObjectException(db_name + "." + tbl_name + " index=" + index_name + " not found");
            }
            return index;
        }
        
        @Override
        public List<String> get_index_names(final String dbName, final String tblName, final short maxIndexes) throws MetaException, TException {
            this.startTableFunction("get_index_names", dbName, tblName);
            List<String> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().listIndexNames(dbName, tblName, maxIndexes);
            }
            catch (Exception e) {
                ex = e;
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                if (e instanceof TException) {
                    throw (TException)e;
                }
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_index_names", ret != null, ex, tblName);
            }
            return ret;
        }
        
        @Override
        public List<Index> get_indexes(final String dbName, final String tblName, final short maxIndexes) throws NoSuchObjectException, MetaException, TException {
            this.startTableFunction("get_indexes", dbName, tblName);
            List<Index> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getIndexes(dbName, tblName, maxIndexes);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_indexes", ret != null, ex, tblName);
            }
            return ret;
        }
        
        private String lowerCaseConvertPartName(final String partName) throws MetaException {
            boolean isFirst = true;
            final Map<String, String> partSpec = Warehouse.makeEscSpecFromName(partName);
            String convertedPartName = new String();
            for (final Map.Entry<String, String> entry : partSpec.entrySet()) {
                final String partColName = entry.getKey();
                final String partColVal = entry.getValue();
                if (!isFirst) {
                    convertedPartName += "/";
                }
                else {
                    isFirst = false;
                }
                convertedPartName = convertedPartName + partColName.toLowerCase() + "=" + partColVal;
            }
            return convertedPartName;
        }
        
        @Override
        public ColumnStatistics get_table_column_statistics(String dbName, String tableName, String colName) throws NoSuchObjectException, MetaException, TException, InvalidInputException, InvalidObjectException {
            dbName = dbName.toLowerCase();
            tableName = tableName.toLowerCase();
            colName = colName.toLowerCase();
            this.startFunction("get_column_statistics_by_table: db=" + dbName + " table=" + tableName + " column=" + colName);
            ColumnStatistics statsObj = null;
            try {
                statsObj = this.getMS().getTableColumnStatistics(dbName, tableName, Lists.newArrayList(colName));
                assert statsObj.getStatsObjSize() <= 1;
                return statsObj;
            }
            finally {
                this.endFunction("get_column_statistics_by_table: ", statsObj != null, null, tableName);
            }
        }
        
        @Override
        public TableStatsResult get_table_statistics_req(final TableStatsRequest request) throws MetaException, NoSuchObjectException, TException {
            final String dbName = request.getDbName().toLowerCase();
            final String tblName = request.getTblName().toLowerCase();
            this.startFunction("get_table_statistics_req: db=" + dbName + " table=" + tblName);
            TableStatsResult result = null;
            final List<String> lowerCaseColNames = new ArrayList<String>(request.getColNames().size());
            for (final String colName : request.getColNames()) {
                lowerCaseColNames.add(colName.toLowerCase());
            }
            try {
                final ColumnStatistics cs = this.getMS().getTableColumnStatistics(dbName, tblName, lowerCaseColNames);
                result = new TableStatsResult((List<ColumnStatisticsObj>)((cs == null) ? Lists.newArrayList() : cs.getStatsObj()));
            }
            finally {
                this.endFunction("get_table_statistics_req: ", result == null, null, tblName);
            }
            return result;
        }
        
        @Override
        public ColumnStatistics get_partition_column_statistics(String dbName, String tableName, final String partName, String colName) throws NoSuchObjectException, MetaException, InvalidInputException, TException, InvalidObjectException {
            dbName = dbName.toLowerCase();
            tableName = tableName.toLowerCase();
            colName = colName.toLowerCase();
            final String convertedPartName = this.lowerCaseConvertPartName(partName);
            this.startFunction("get_column_statistics_by_partition: db=" + dbName + " table=" + tableName + " partition=" + convertedPartName + " column=" + colName);
            ColumnStatistics statsObj = null;
            try {
                final List<ColumnStatistics> list = this.getMS().getPartitionColumnStatistics(dbName, tableName, Lists.newArrayList(convertedPartName), Lists.newArrayList(colName));
                if (list.isEmpty()) {
                    return null;
                }
                if (list.size() != 1) {
                    throw new MetaException(list.size() + " statistics for single column and partition");
                }
                statsObj = list.get(0);
            }
            finally {
                this.endFunction("get_column_statistics_by_partition: ", statsObj != null, null, tableName);
            }
            return statsObj;
        }
        
        @Override
        public PartitionsStatsResult get_partitions_statistics_req(final PartitionsStatsRequest request) throws MetaException, NoSuchObjectException, TException {
            final String dbName = request.getDbName().toLowerCase();
            final String tblName = request.getTblName().toLowerCase();
            this.startFunction("get_partitions_statistics_req: db=" + dbName + " table=" + tblName);
            PartitionsStatsResult result = null;
            final List<String> lowerCaseColNames = new ArrayList<String>(request.getColNames().size());
            for (final String colName : request.getColNames()) {
                lowerCaseColNames.add(colName.toLowerCase());
            }
            final List<String> lowerCasePartNames = new ArrayList<String>(request.getPartNames().size());
            for (final String partName : request.getPartNames()) {
                lowerCasePartNames.add(this.lowerCaseConvertPartName(partName));
            }
            try {
                final List<ColumnStatistics> stats = this.getMS().getPartitionColumnStatistics(dbName, tblName, lowerCasePartNames, lowerCaseColNames);
                final Map<String, List<ColumnStatisticsObj>> map = new HashMap<String, List<ColumnStatisticsObj>>();
                for (final ColumnStatistics stat : stats) {
                    map.put(stat.getStatsDesc().getPartName(), stat.getStatsObj());
                }
                result = new PartitionsStatsResult(map);
            }
            finally {
                this.endFunction("get_partitions_statistics_req: ", result == null, null, tblName);
            }
            return result;
        }
        
        @Override
        public boolean update_table_column_statistics(final ColumnStatistics colStats) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
            String dbName = null;
            String tableName = null;
            String colName = null;
            final ColumnStatisticsDesc statsDesc = colStats.getStatsDesc();
            dbName = statsDesc.getDbName().toLowerCase();
            tableName = statsDesc.getTableName().toLowerCase();
            statsDesc.setDbName(dbName);
            statsDesc.setTableName(tableName);
            final long time = System.currentTimeMillis() / 1000L;
            statsDesc.setLastAnalyzed(time);
            final List<ColumnStatisticsObj> statsObjs = colStats.getStatsObj();
            for (final ColumnStatisticsObj statsObj : statsObjs) {
                colName = statsObj.getColName().toLowerCase();
                statsObj.setColName(colName);
                this.startFunction("write_column_statistics:  db=" + dbName + " table=" + tableName + " column=" + colName);
            }
            colStats.setStatsDesc(statsDesc);
            colStats.setStatsObj(statsObjs);
            boolean ret = false;
            try {
                ret = this.getMS().updateTableColumnStatistics(colStats);
                return ret;
            }
            finally {
                this.endFunction("write_column_statistics: ", ret, null, tableName);
            }
        }
        
        @Override
        public boolean update_partition_column_statistics(final ColumnStatistics colStats) throws NoSuchObjectException, InvalidObjectException, MetaException, TException, InvalidInputException {
            String dbName = null;
            String tableName = null;
            String partName = null;
            String colName = null;
            final ColumnStatisticsDesc statsDesc = colStats.getStatsDesc();
            dbName = statsDesc.getDbName().toLowerCase();
            tableName = statsDesc.getTableName().toLowerCase();
            partName = this.lowerCaseConvertPartName(statsDesc.getPartName());
            statsDesc.setDbName(dbName);
            statsDesc.setTableName(tableName);
            statsDesc.setPartName(partName);
            final long time = System.currentTimeMillis() / 1000L;
            statsDesc.setLastAnalyzed(time);
            final List<ColumnStatisticsObj> statsObjs = colStats.getStatsObj();
            for (final ColumnStatisticsObj statsObj : statsObjs) {
                colName = statsObj.getColName().toLowerCase();
                statsObj.setColName(colName);
                this.startFunction("write_partition_column_statistics:  db=" + dbName + " table=" + tableName + " part=" + partName + "column=" + colName);
            }
            colStats.setStatsDesc(statsDesc);
            colStats.setStatsObj(statsObjs);
            boolean ret = false;
            try {
                final List<String> partVals = this.getPartValsFromName(this.getMS(), dbName, tableName, partName);
                ret = this.getMS().updatePartitionColumnStatistics(colStats, partVals);
                return ret;
            }
            finally {
                this.endFunction("write_partition_column_statistics: ", ret, null, tableName);
            }
        }
        
        @Override
        public boolean delete_partition_column_statistics(String dbName, String tableName, final String partName, String colName) throws NoSuchObjectException, MetaException, InvalidObjectException, TException, InvalidInputException {
            dbName = dbName.toLowerCase();
            tableName = tableName.toLowerCase();
            if (colName != null) {
                colName = colName.toLowerCase();
            }
            final String convertedPartName = this.lowerCaseConvertPartName(partName);
            this.startFunction("delete_column_statistics_by_partition: db=" + dbName + " table=" + tableName + " partition=" + convertedPartName + " column=" + colName);
            boolean ret = false;
            try {
                final List<String> partVals = this.getPartValsFromName(this.getMS(), dbName, tableName, convertedPartName);
                ret = this.getMS().deletePartitionColumnStatistics(dbName, tableName, convertedPartName, partVals, colName);
            }
            finally {
                this.endFunction("delete_column_statistics_by_partition: ", ret, null, tableName);
            }
            return ret;
        }
        
        @Override
        public boolean delete_table_column_statistics(String dbName, String tableName, String colName) throws NoSuchObjectException, MetaException, InvalidObjectException, TException, InvalidInputException {
            dbName = dbName.toLowerCase();
            tableName = tableName.toLowerCase();
            if (colName != null) {
                colName = colName.toLowerCase();
            }
            this.startFunction("delete_column_statistics_by_table: db=" + dbName + " table=" + tableName + " column=" + colName);
            boolean ret = false;
            try {
                ret = this.getMS().deleteTableColumnStatistics(dbName, tableName, colName);
            }
            finally {
                this.endFunction("delete_column_statistics_by_table: ", ret, null, tableName);
            }
            return ret;
        }
        
        @Override
        public List<Partition> get_partitions_by_filter(final String dbName, final String tblName, final String filter, final short maxParts) throws MetaException, NoSuchObjectException, TException {
            this.startTableFunction("get_partitions_by_filter", dbName, tblName);
            this.fireReadTablePreEvent(dbName, tblName);
            List<Partition> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getPartitionsByFilter(dbName, tblName, filter, maxParts);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_partitions_by_filter", ret != null, ex, tblName);
            }
            return ret;
        }
        
        @Override
        public List<PartitionSpec> get_part_specs_by_filter(final String dbName, final String tblName, final String filter, final int maxParts) throws MetaException, NoSuchObjectException, TException {
            this.startTableFunction("get_partitions_by_filter_pspec", dbName, tblName);
            List<PartitionSpec> partitionSpecs = null;
            try {
                final Table table = this.get_table_core(dbName, tblName);
                final List<Partition> partitions = this.get_partitions_by_filter(dbName, tblName, filter, (short)maxParts);
                if (is_partition_spec_grouping_enabled(table)) {
                    partitionSpecs = this.get_partitionspecs_grouped_by_storage_descriptor(table, partitions);
                }
                else {
                    final PartitionSpec pSpec = new PartitionSpec();
                    pSpec.setPartitionList(new PartitionListComposingSpec(partitions));
                    pSpec.setRootPath(table.getSd().getLocation());
                    pSpec.setDbName(dbName);
                    pSpec.setTableName(tblName);
                    partitionSpecs = Arrays.asList(pSpec);
                }
                return partitionSpecs;
            }
            finally {
                this.endFunction("get_partitions_by_filter_pspec", partitionSpecs != null && !partitionSpecs.isEmpty(), null, tblName);
            }
        }
        
        @Override
        public PartitionsByExprResult get_partitions_by_expr(final PartitionsByExprRequest req) throws TException {
            final String dbName = req.getDbName();
            final String tblName = req.getTblName();
            this.startTableFunction("get_partitions_by_expr", dbName, tblName);
            this.fireReadTablePreEvent(dbName, tblName);
            PartitionsByExprResult ret = null;
            Exception ex = null;
            try {
                final List<Partition> partitions = new LinkedList<Partition>();
                final boolean hasUnknownPartitions = this.getMS().getPartitionsByExpr(dbName, tblName, req.getExpr(), req.getDefaultPartitionName(), req.getMaxParts(), partitions);
                ret = new PartitionsByExprResult(partitions, hasUnknownPartitions);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_partitions_by_expr", ret != null, ex, tblName);
            }
            return ret;
        }
        
        private void rethrowException(final Exception e) throws MetaException, NoSuchObjectException, TException {
            if (e instanceof MetaException) {
                throw (MetaException)e;
            }
            if (e instanceof NoSuchObjectException) {
                throw (NoSuchObjectException)e;
            }
            if (e instanceof TException) {
                throw (TException)e;
            }
            throw newMetaException(e);
        }
        
        @Override
        public List<Partition> get_partitions_by_names(final String dbName, final String tblName, final List<String> partNames) throws MetaException, NoSuchObjectException, TException {
            this.startTableFunction("get_partitions_by_names", dbName, tblName);
            this.fireReadTablePreEvent(dbName, tblName);
            List<Partition> ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().getPartitionsByNames(dbName, tblName, partNames);
            }
            catch (Exception e) {
                ex = e;
                this.rethrowException(e);
            }
            finally {
                this.endFunction("get_partitions_by_names", ret != null, ex, tblName);
            }
            return ret;
        }
        
        @Override
        public PrincipalPrivilegeSet get_privilege_set(final HiveObjectRef hiveObject, final String userName, final List<String> groupNames) throws MetaException, TException {
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            if (hiveObject.getObjectType() == HiveObjectType.COLUMN) {
                final String partName = this.getPartName(hiveObject);
                return this.get_column_privilege_set(hiveObject.getDbName(), hiveObject.getObjectName(), partName, hiveObject.getColumnName(), userName, groupNames);
            }
            if (hiveObject.getObjectType() == HiveObjectType.PARTITION) {
                final String partName = this.getPartName(hiveObject);
                return this.get_partition_privilege_set(hiveObject.getDbName(), hiveObject.getObjectName(), partName, userName, groupNames);
            }
            if (hiveObject.getObjectType() == HiveObjectType.DATABASE) {
                return this.get_db_privilege_set(hiveObject.getDbName(), userName, groupNames);
            }
            if (hiveObject.getObjectType() == HiveObjectType.TABLE) {
                return this.get_table_privilege_set(hiveObject.getDbName(), hiveObject.getObjectName(), userName, groupNames);
            }
            if (hiveObject.getObjectType() == HiveObjectType.GLOBAL) {
                return this.get_user_privilege_set(userName, groupNames);
            }
            return null;
        }
        
        private String getPartName(final HiveObjectRef hiveObject) throws MetaException {
            String partName = null;
            final List<String> partValue = hiveObject.getPartValues();
            if (partValue != null && partValue.size() > 0) {
                try {
                    final Table table = this.get_table_core(hiveObject.getDbName(), hiveObject.getObjectName());
                    partName = Warehouse.makePartName(table.getPartitionKeys(), partValue);
                }
                catch (NoSuchObjectException e) {
                    throw new MetaException(e.getMessage());
                }
            }
            return partName;
        }
        
        private PrincipalPrivilegeSet get_column_privilege_set(final String dbName, final String tableName, final String partName, final String columnName, final String userName, final List<String> groupNames) throws MetaException, TException {
            this.incrementCounter("get_column_privilege_set");
            PrincipalPrivilegeSet ret = null;
            try {
                ret = this.getMS().getColumnPrivilegeSet(dbName, tableName, partName, columnName, userName, groupNames);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        private PrincipalPrivilegeSet get_db_privilege_set(final String dbName, final String userName, final List<String> groupNames) throws MetaException, TException {
            this.incrementCounter("get_db_privilege_set");
            PrincipalPrivilegeSet ret = null;
            try {
                ret = this.getMS().getDBPrivilegeSet(dbName, userName, groupNames);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        private PrincipalPrivilegeSet get_partition_privilege_set(final String dbName, final String tableName, final String partName, final String userName, final List<String> groupNames) throws MetaException, TException {
            this.incrementCounter("get_partition_privilege_set");
            PrincipalPrivilegeSet ret = null;
            try {
                ret = this.getMS().getPartitionPrivilegeSet(dbName, tableName, partName, userName, groupNames);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        private PrincipalPrivilegeSet get_table_privilege_set(final String dbName, final String tableName, final String userName, final List<String> groupNames) throws MetaException, TException {
            this.incrementCounter("get_table_privilege_set");
            PrincipalPrivilegeSet ret = null;
            try {
                ret = this.getMS().getTablePrivilegeSet(dbName, tableName, userName, groupNames);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        @Override
        public boolean grant_role(final String roleName, final String principalName, final PrincipalType principalType, final String grantor, final PrincipalType grantorType, final boolean grantOption) throws MetaException, TException {
            this.incrementCounter("add_role_member");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            if ("public".equals(roleName)) {
                throw new MetaException("No user can be added to public. Since all users implictly belong to public role.");
            }
            Boolean ret = null;
            try {
                final RawStore ms = this.getMS();
                final Role role = ms.getRole(roleName);
                if (principalType == PrincipalType.ROLE && this.isNewRoleAParent(principalName, roleName)) {
                    throw new MetaException("Cannot grant role " + principalName + " to " + roleName + " as " + roleName + " already belongs to the role " + principalName + ". (no cycles allowed)");
                }
                ret = ms.grantRole(role, principalName, principalType, grantor, grantorType, grantOption);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        private boolean isNewRoleAParent(final String newRole, final String curRole) throws MetaException {
            if (newRole.equals(curRole)) {
                return true;
            }
            final List<MRoleMap> parentRoleMaps = this.getMS().listRoles(curRole, PrincipalType.ROLE);
            for (final MRoleMap parentRole : parentRoleMaps) {
                if (this.isNewRoleAParent(newRole, parentRole.getRole().getRoleName())) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public List<Role> list_roles(final String principalName, final PrincipalType principalType) throws MetaException, TException {
            this.incrementCounter("list_roles");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            final List<Role> result = new ArrayList<Role>();
            try {
                final List<MRoleMap> roleMaps = this.getMS().listRoles(principalName, principalType);
                if (roleMaps != null) {
                    for (final MRoleMap roleMap : roleMaps) {
                        final MRole mrole = roleMap.getRole();
                        final Role role = new Role(mrole.getRoleName(), mrole.getCreateTime(), mrole.getOwnerName());
                        result.add(role);
                    }
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        @Override
        public boolean create_role(final Role role) throws MetaException, TException {
            this.incrementCounter("create_role");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            if ("public".equals(role.getRoleName())) {
                throw new MetaException("public role implictly exists. It can't be created.");
            }
            Boolean ret = null;
            try {
                ret = this.getMS().addRole(role.getRoleName(), role.getOwnerName());
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        @Override
        public boolean drop_role(final String roleName) throws MetaException, TException {
            this.incrementCounter("drop_role");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            if ("admin".equals(roleName) || "public".equals(roleName)) {
                throw new MetaException("public,admin roles can't be dropped.");
            }
            Boolean ret = null;
            try {
                ret = this.getMS().removeRole(roleName);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        @Override
        public List<String> get_role_names() throws MetaException, TException {
            this.incrementCounter("get_role_names");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            List<String> ret = null;
            try {
                ret = this.getMS().listRoleNames();
                return ret;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        @Override
        public boolean grant_privileges(final PrivilegeBag privileges) throws MetaException, TException {
            this.incrementCounter("grant_privileges");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            Boolean ret = null;
            try {
                ret = this.getMS().grantPrivileges(privileges);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        @Override
        public boolean revoke_role(final String roleName, final String userName, final PrincipalType principalType) throws MetaException, TException {
            return this.revoke_role(roleName, userName, principalType, false);
        }
        
        private boolean revoke_role(final String roleName, final String userName, final PrincipalType principalType, final boolean grantOption) throws MetaException, TException {
            this.incrementCounter("remove_role_member");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            if ("public".equals(roleName)) {
                throw new MetaException("public role can't be revoked.");
            }
            Boolean ret = null;
            try {
                final RawStore ms = this.getMS();
                final Role mRole = ms.getRole(roleName);
                ret = ms.revokeRole(mRole, userName, principalType, grantOption);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        @Override
        public GrantRevokeRoleResponse grant_revoke_role(final GrantRevokeRoleRequest request) throws MetaException, TException {
            final GrantRevokeRoleResponse response = new GrantRevokeRoleResponse();
            boolean grantOption = false;
            if (request.isSetGrantOption()) {
                grantOption = request.isGrantOption();
            }
            switch (request.getRequestType()) {
                case GRANT: {
                    final boolean result = this.grant_role(request.getRoleName(), request.getPrincipalName(), request.getPrincipalType(), request.getGrantor(), request.getGrantorType(), grantOption);
                    response.setSuccess(result);
                    break;
                }
                case REVOKE: {
                    final boolean result = this.revoke_role(request.getRoleName(), request.getPrincipalName(), request.getPrincipalType(), grantOption);
                    response.setSuccess(result);
                    break;
                }
                default: {
                    throw new MetaException("Unknown request type " + request.getRequestType());
                }
            }
            return response;
        }
        
        @Override
        public GrantRevokePrivilegeResponse grant_revoke_privileges(final GrantRevokePrivilegeRequest request) throws MetaException, TException {
            final GrantRevokePrivilegeResponse response = new GrantRevokePrivilegeResponse();
            switch (request.getRequestType()) {
                case GRANT: {
                    final boolean result = this.grant_privileges(request.getPrivileges());
                    response.setSuccess(result);
                    break;
                }
                case REVOKE: {
                    boolean revokeGrantOption = false;
                    if (request.isSetRevokeGrantOption()) {
                        revokeGrantOption = request.isRevokeGrantOption();
                    }
                    final boolean result2 = this.revoke_privileges(request.getPrivileges(), revokeGrantOption);
                    response.setSuccess(result2);
                    break;
                }
                default: {
                    throw new MetaException("Unknown request type " + request.getRequestType());
                }
            }
            return response;
        }
        
        @Override
        public boolean revoke_privileges(final PrivilegeBag privileges) throws MetaException, TException {
            return this.revoke_privileges(privileges, false);
        }
        
        public boolean revoke_privileges(final PrivilegeBag privileges, final boolean grantOption) throws MetaException, TException {
            this.incrementCounter("revoke_privileges");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            Boolean ret = null;
            try {
                ret = this.getMS().revokePrivileges(privileges, grantOption);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        private PrincipalPrivilegeSet get_user_privilege_set(final String userName, final List<String> groupNames) throws MetaException, TException {
            this.incrementCounter("get_user_privilege_set");
            PrincipalPrivilegeSet ret = null;
            try {
                ret = this.getMS().getUserPrivilegeSet(userName, groupNames);
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
            return ret;
        }
        
        @Override
        public List<HiveObjectPrivilege> list_privileges(final String principalName, final PrincipalType principalType, final HiveObjectRef hiveObject) throws MetaException, TException {
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            if (hiveObject.getObjectType() == null) {
                return this.getAllPrivileges(principalName, principalType);
            }
            if (hiveObject.getObjectType() == HiveObjectType.GLOBAL) {
                return this.list_global_privileges(principalName, principalType);
            }
            if (hiveObject.getObjectType() == HiveObjectType.DATABASE) {
                return this.list_db_privileges(principalName, principalType, hiveObject.getDbName());
            }
            if (hiveObject.getObjectType() == HiveObjectType.TABLE) {
                return this.list_table_privileges(principalName, principalType, hiveObject.getDbName(), hiveObject.getObjectName());
            }
            if (hiveObject.getObjectType() == HiveObjectType.PARTITION) {
                return this.list_partition_privileges(principalName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getPartValues());
            }
            if (hiveObject.getObjectType() != HiveObjectType.COLUMN) {
                return null;
            }
            if (hiveObject.getPartValues() == null || hiveObject.getPartValues().isEmpty()) {
                return this.list_table_column_privileges(principalName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getColumnName());
            }
            return this.list_partition_column_privileges(principalName, principalType, hiveObject.getDbName(), hiveObject.getObjectName(), hiveObject.getPartValues(), hiveObject.getColumnName());
        }
        
        private List<HiveObjectPrivilege> getAllPrivileges(final String principalName, final PrincipalType principalType) throws TException {
            final List<HiveObjectPrivilege> privs = new ArrayList<HiveObjectPrivilege>();
            privs.addAll(this.list_global_privileges(principalName, principalType));
            privs.addAll(this.list_db_privileges(principalName, principalType, null));
            privs.addAll(this.list_table_privileges(principalName, principalType, null, null));
            privs.addAll(this.list_partition_privileges(principalName, principalType, null, null, null));
            privs.addAll(this.list_table_column_privileges(principalName, principalType, null, null, null));
            privs.addAll(this.list_partition_column_privileges(principalName, principalType, null, null, null, null));
            return privs;
        }
        
        private List<HiveObjectPrivilege> list_table_column_privileges(final String principalName, final PrincipalType principalType, final String dbName, final String tableName, final String columnName) throws MetaException, TException {
            this.incrementCounter("list_table_column_privileges");
            try {
                if (dbName == null) {
                    return this.getMS().listPrincipalTableColumnGrantsAll(principalName, principalType);
                }
                if (principalName == null) {
                    return this.getMS().listTableColumnGrantsAll(dbName, tableName, columnName);
                }
                final List<MTableColumnPrivilege> mTableCols = this.getMS().listPrincipalTableColumnGrants(principalName, principalType, dbName, tableName, columnName);
                if (mTableCols.isEmpty()) {
                    return Collections.emptyList();
                }
                final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
                for (int i = 0; i < mTableCols.size(); ++i) {
                    final MTableColumnPrivilege sCol = mTableCols.get(i);
                    final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.COLUMN, dbName, tableName, null, sCol.getColumnName());
                    final HiveObjectPrivilege secObj = new HiveObjectPrivilege(objectRef, sCol.getPrincipalName(), principalType, new PrivilegeGrantInfo(sCol.getPrivilege(), sCol.getCreateTime(), sCol.getGrantor(), PrincipalType.valueOf(sCol.getGrantorType()), sCol.getGrantOption()));
                    result.add(secObj);
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        private List<HiveObjectPrivilege> list_partition_column_privileges(final String principalName, final PrincipalType principalType, final String dbName, final String tableName, final List<String> partValues, final String columnName) throws MetaException, TException {
            this.incrementCounter("list_partition_column_privileges");
            try {
                if (dbName == null) {
                    return this.getMS().listPrincipalPartitionColumnGrantsAll(principalName, principalType);
                }
                final Table tbl = this.get_table_core(dbName, tableName);
                final String partName = Warehouse.makePartName(tbl.getPartitionKeys(), partValues);
                if (principalName == null) {
                    return this.getMS().listPartitionColumnGrantsAll(dbName, tableName, partName, columnName);
                }
                final List<MPartitionColumnPrivilege> mPartitionCols = this.getMS().listPrincipalPartitionColumnGrants(principalName, principalType, dbName, tableName, partName, columnName);
                if (mPartitionCols.isEmpty()) {
                    return Collections.emptyList();
                }
                final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
                for (int i = 0; i < mPartitionCols.size(); ++i) {
                    final MPartitionColumnPrivilege sCol = mPartitionCols.get(i);
                    final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.COLUMN, dbName, tableName, partValues, sCol.getColumnName());
                    final HiveObjectPrivilege secObj = new HiveObjectPrivilege(objectRef, sCol.getPrincipalName(), principalType, new PrivilegeGrantInfo(sCol.getPrivilege(), sCol.getCreateTime(), sCol.getGrantor(), PrincipalType.valueOf(sCol.getGrantorType()), sCol.getGrantOption()));
                    result.add(secObj);
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        private List<HiveObjectPrivilege> list_db_privileges(final String principalName, final PrincipalType principalType, final String dbName) throws MetaException, TException {
            this.incrementCounter("list_security_db_grant");
            try {
                if (dbName == null) {
                    return this.getMS().listPrincipalDBGrantsAll(principalName, principalType);
                }
                if (principalName == null) {
                    return this.getMS().listDBGrantsAll(dbName);
                }
                final List<MDBPrivilege> mDbs = this.getMS().listPrincipalDBGrants(principalName, principalType, dbName);
                if (mDbs.isEmpty()) {
                    return Collections.emptyList();
                }
                final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
                for (int i = 0; i < mDbs.size(); ++i) {
                    final MDBPrivilege sDB = mDbs.get(i);
                    final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.DATABASE, dbName, null, null, null);
                    final HiveObjectPrivilege secObj = new HiveObjectPrivilege(objectRef, sDB.getPrincipalName(), principalType, new PrivilegeGrantInfo(sDB.getPrivilege(), sDB.getCreateTime(), sDB.getGrantor(), PrincipalType.valueOf(sDB.getGrantorType()), sDB.getGrantOption()));
                    result.add(secObj);
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        private List<HiveObjectPrivilege> list_partition_privileges(final String principalName, final PrincipalType principalType, final String dbName, final String tableName, final List<String> partValues) throws MetaException, TException {
            this.incrementCounter("list_security_partition_grant");
            try {
                if (dbName == null) {
                    return this.getMS().listPrincipalPartitionGrantsAll(principalName, principalType);
                }
                final Table tbl = this.get_table_core(dbName, tableName);
                final String partName = Warehouse.makePartName(tbl.getPartitionKeys(), partValues);
                if (principalName == null) {
                    return this.getMS().listPartitionGrantsAll(dbName, tableName, partName);
                }
                final List<MPartitionPrivilege> mParts = this.getMS().listPrincipalPartitionGrants(principalName, principalType, dbName, tableName, partName);
                if (mParts.isEmpty()) {
                    return Collections.emptyList();
                }
                final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
                for (int i = 0; i < mParts.size(); ++i) {
                    final MPartitionPrivilege sPart = mParts.get(i);
                    final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.PARTITION, dbName, tableName, partValues, null);
                    final HiveObjectPrivilege secObj = new HiveObjectPrivilege(objectRef, sPart.getPrincipalName(), principalType, new PrivilegeGrantInfo(sPart.getPrivilege(), sPart.getCreateTime(), sPart.getGrantor(), PrincipalType.valueOf(sPart.getGrantorType()), sPart.getGrantOption()));
                    result.add(secObj);
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        private List<HiveObjectPrivilege> list_table_privileges(final String principalName, final PrincipalType principalType, final String dbName, final String tableName) throws MetaException, TException {
            this.incrementCounter("list_security_table_grant");
            try {
                if (dbName == null) {
                    return this.getMS().listPrincipalTableGrantsAll(principalName, principalType);
                }
                if (principalName == null) {
                    return this.getMS().listTableGrantsAll(dbName, tableName);
                }
                final List<MTablePrivilege> mTbls = this.getMS().listAllTableGrants(principalName, principalType, dbName, tableName);
                if (mTbls.isEmpty()) {
                    return Collections.emptyList();
                }
                final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
                for (int i = 0; i < mTbls.size(); ++i) {
                    final MTablePrivilege sTbl = mTbls.get(i);
                    final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.TABLE, dbName, tableName, null, null);
                    final HiveObjectPrivilege secObj = new HiveObjectPrivilege(objectRef, sTbl.getPrincipalName(), principalType, new PrivilegeGrantInfo(sTbl.getPrivilege(), sTbl.getCreateTime(), sTbl.getGrantor(), PrincipalType.valueOf(sTbl.getGrantorType()), sTbl.getGrantOption()));
                    result.add(secObj);
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        private List<HiveObjectPrivilege> list_global_privileges(final String principalName, final PrincipalType principalType) throws MetaException, TException {
            this.incrementCounter("list_security_user_grant");
            try {
                if (principalName == null) {
                    return this.getMS().listGlobalGrantsAll();
                }
                final List<MGlobalPrivilege> mUsers = this.getMS().listPrincipalGlobalGrants(principalName, principalType);
                if (mUsers.isEmpty()) {
                    return Collections.emptyList();
                }
                final List<HiveObjectPrivilege> result = new ArrayList<HiveObjectPrivilege>();
                for (int i = 0; i < mUsers.size(); ++i) {
                    final MGlobalPrivilege sUsr = mUsers.get(i);
                    final HiveObjectRef objectRef = new HiveObjectRef(HiveObjectType.GLOBAL, null, null, null, null);
                    final HiveObjectPrivilege secUser = new HiveObjectPrivilege(objectRef, sUsr.getPrincipalName(), principalType, new PrivilegeGrantInfo(sUsr.getPrivilege(), sUsr.getCreateTime(), sUsr.getGrantor(), PrincipalType.valueOf(sUsr.getGrantorType()), sUsr.getGrantOption()));
                    result.add(secUser);
                }
                return result;
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        
        @Override
        public void cancel_delegation_token(final String token_str_form) throws MetaException, TException {
            this.startFunction("cancel_delegation_token");
            boolean success = false;
            Exception ex = null;
            try {
                HiveMetaStore.cancelDelegationToken(token_str_form);
                success = true;
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof TException) {
                    throw (TException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("cancel_delegation_token", success, ex);
            }
        }
        
        @Override
        public long renew_delegation_token(final String token_str_form) throws MetaException, TException {
            this.startFunction("renew_delegation_token");
            Long ret = null;
            Exception ex = null;
            try {
                ret = HiveMetaStore.renewDelegationToken(token_str_form);
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (Exception e2) {
                ex = e2;
                if (e2 instanceof MetaException) {
                    throw (MetaException)e2;
                }
                if (e2 instanceof TException) {
                    throw (TException)e2;
                }
                throw newMetaException(e2);
            }
            finally {
                this.endFunction("renew_delegation_token", ret != null, ex);
            }
            return ret;
        }
        
        @Override
        public String get_delegation_token(final String token_owner, final String renewer_kerberos_principal_name) throws MetaException, TException {
            this.startFunction("get_delegation_token");
            String ret = null;
            Exception ex = null;
            try {
                ret = HiveMetaStore.getDelegationToken(token_owner, renewer_kerberos_principal_name);
            }
            catch (IOException e) {
                ex = e;
                throw new MetaException(e.getMessage());
            }
            catch (InterruptedException e2) {
                ex = e2;
                throw new MetaException(e2.getMessage());
            }
            catch (Exception e3) {
                ex = e3;
                if (e3 instanceof MetaException) {
                    throw (MetaException)e3;
                }
                if (e3 instanceof TException) {
                    throw (TException)e3;
                }
                throw newMetaException(e3);
            }
            finally {
                this.endFunction("get_delegation_token", ret != null, ex);
            }
            return ret;
        }
        
        @Override
        public void markPartitionForEvent(final String db_name, final String tbl_name, final Map<String, String> partName, final PartitionEventType evtType) throws MetaException, TException, NoSuchObjectException, UnknownDBException, UnknownTableException, InvalidPartitionException, UnknownPartitionException {
            Table tbl = null;
            Exception ex = null;
            try {
                this.startPartitionFunction("markPartitionForEvent", db_name, tbl_name, partName);
                this.firePreEvent(new PreLoadPartitionDoneEvent(db_name, tbl_name, partName, this));
                tbl = this.getMS().markPartitionForEvent(db_name, tbl_name, partName, evtType);
                if (null == tbl) {
                    throw new UnknownTableException("Table: " + tbl_name + " not found.");
                }
                for (final MetaStoreEventListener listener : this.listeners) {
                    listener.onLoadPartitionDone(new LoadPartitionDoneEvent(true, tbl, partName, this));
                }
            }
            catch (Exception original) {
                ex = original;
                HMSHandler.LOG.error(original);
                if (original instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)original;
                }
                if (original instanceof UnknownTableException) {
                    throw (UnknownTableException)original;
                }
                if (original instanceof UnknownDBException) {
                    throw (UnknownDBException)original;
                }
                if (original instanceof UnknownPartitionException) {
                    throw (UnknownPartitionException)original;
                }
                if (original instanceof InvalidPartitionException) {
                    throw (InvalidPartitionException)original;
                }
                if (original instanceof MetaException) {
                    throw (MetaException)original;
                }
                throw newMetaException(original);
            }
            finally {
                this.endFunction("markPartitionForEvent", tbl != null, ex, tbl_name);
            }
        }
        
        @Override
        public boolean isPartitionMarkedForEvent(final String db_name, final String tbl_name, final Map<String, String> partName, final PartitionEventType evtType) throws MetaException, NoSuchObjectException, UnknownDBException, UnknownTableException, TException, UnknownPartitionException, InvalidPartitionException {
            this.startPartitionFunction("isPartitionMarkedForEvent", db_name, tbl_name, partName);
            Boolean ret = null;
            Exception ex = null;
            try {
                ret = this.getMS().isPartitionMarkedForEvent(db_name, tbl_name, partName, evtType);
            }
            catch (Exception original) {
                HMSHandler.LOG.error(original);
                ex = original;
                if (original instanceof NoSuchObjectException) {
                    throw (NoSuchObjectException)original;
                }
                if (original instanceof UnknownTableException) {
                    throw (UnknownTableException)original;
                }
                if (original instanceof UnknownDBException) {
                    throw (UnknownDBException)original;
                }
                if (original instanceof UnknownPartitionException) {
                    throw (UnknownPartitionException)original;
                }
                if (original instanceof InvalidPartitionException) {
                    throw (InvalidPartitionException)original;
                }
                if (original instanceof MetaException) {
                    throw (MetaException)original;
                }
                throw newMetaException(original);
            }
            finally {
                this.endFunction("isPartitionMarkedForEvent", ret != null, ex, tbl_name);
            }
            return ret;
        }
        
        @Override
        public List<String> set_ugi(final String username, final List<String> groupNames) throws MetaException, TException {
            Collections.addAll(groupNames, new String[] { username });
            return groupNames;
        }
        
        @Override
        public boolean partition_name_has_valid_characters(final List<String> part_vals, final boolean throw_exception) throws TException, MetaException {
            this.startFunction("partition_name_has_valid_characters");
            boolean ret = false;
            Exception ex = null;
            try {
                if (throw_exception) {
                    MetaStoreUtils.validatePartitionNameCharacters(part_vals, this.partitionValidationPattern);
                    ret = true;
                }
                else {
                    ret = MetaStoreUtils.partitionNameHasValidCharacters(part_vals, this.partitionValidationPattern);
                }
            }
            catch (Exception e) {
                if (e instanceof MetaException) {
                    throw (MetaException)e;
                }
                ex = e;
                throw newMetaException(e);
            }
            this.endFunction("partition_name_has_valid_characters", true, null);
            return ret;
        }
        
        private static MetaException newMetaException(final Exception e) {
            final MetaException me = new MetaException(e.toString());
            me.initCause(e);
            return me;
        }
        
        private void validateFunctionInfo(final org.apache.hadoop.hive.metastore.api.Function func) throws InvalidObjectException, MetaException {
            if (!MetaStoreUtils.validateName(func.getFunctionName())) {
                throw new InvalidObjectException(func.getFunctionName() + " is not a valid object name");
            }
            final String className = func.getClassName();
            if (className == null) {
                throw new InvalidObjectException("Function class name cannot be null");
            }
        }
        
        @Override
        public void create_function(final org.apache.hadoop.hive.metastore.api.Function func) throws AlreadyExistsException, InvalidObjectException, MetaException, NoSuchObjectException, TException {
            this.validateFunctionInfo(func);
            boolean success = false;
            final RawStore ms = this.getMS();
            try {
                ms.openTransaction();
                final Database db = ms.getDatabase(func.getDbName());
                if (db == null) {
                    throw new NoSuchObjectException("The database " + func.getDbName() + " does not exist");
                }
                final org.apache.hadoop.hive.metastore.api.Function existingFunc = ms.getFunction(func.getDbName(), func.getFunctionName());
                if (existingFunc != null) {
                    throw new AlreadyExistsException("Function " + func.getFunctionName() + " already exists");
                }
                final long time = System.currentTimeMillis() / 1000L;
                func.setCreateTime((int)time);
                ms.createFunction(func);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
            }
        }
        
        @Override
        public void drop_function(final String dbName, final String funcName) throws NoSuchObjectException, MetaException, InvalidObjectException, InvalidInputException {
            boolean success = false;
            org.apache.hadoop.hive.metastore.api.Function func = null;
            final RawStore ms = this.getMS();
            try {
                ms.openTransaction();
                func = ms.getFunction(dbName, funcName);
                if (func == null) {
                    throw new NoSuchObjectException("Function " + funcName + " does not exist");
                }
                ms.dropFunction(dbName, funcName);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
            }
        }
        
        @Override
        public void alter_function(final String dbName, final String funcName, final org.apache.hadoop.hive.metastore.api.Function newFunc) throws InvalidOperationException, MetaException, TException {
            this.validateFunctionInfo(newFunc);
            boolean success = false;
            final RawStore ms = this.getMS();
            try {
                ms.openTransaction();
                ms.alterFunction(dbName, funcName, newFunc);
                success = ms.commitTransaction();
            }
            finally {
                if (!success) {
                    ms.rollbackTransaction();
                }
            }
        }
        
        @Override
        public List<String> get_functions(final String dbName, final String pattern) throws MetaException {
            this.startFunction("get_functions", ": db=" + dbName + " pat=" + pattern);
            final RawStore ms = this.getMS();
            Exception ex = null;
            List<String> funcNames = null;
            try {
                funcNames = ms.getFunctions(dbName, pattern);
            }
            catch (Exception e) {
                ex = e;
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_functions", funcNames != null, ex);
            }
            return funcNames;
        }
        
        @Override
        public org.apache.hadoop.hive.metastore.api.Function get_function(final String dbName, final String funcName) throws MetaException, NoSuchObjectException, TException {
            this.startFunction("get_function", ": " + dbName + "." + funcName);
            final RawStore ms = this.getMS();
            org.apache.hadoop.hive.metastore.api.Function func = null;
            Exception ex = null;
            try {
                func = ms.getFunction(dbName, funcName);
                if (func == null) {
                    throw new NoSuchObjectException("Function " + dbName + "." + funcName + " does not exist");
                }
            }
            catch (Exception e) {
                ex = e;
                throw newMetaException(e);
            }
            finally {
                this.endFunction("get_function", func != null, ex);
            }
            return func;
        }
        
        @Override
        public GetOpenTxnsResponse get_open_txns() throws TException {
            return this.getTxnHandler().getOpenTxns();
        }
        
        @Override
        public GetOpenTxnsInfoResponse get_open_txns_info() throws TException {
            return this.getTxnHandler().getOpenTxnsInfo();
        }
        
        @Override
        public OpenTxnsResponse open_txns(final OpenTxnRequest rqst) throws TException {
            return this.getTxnHandler().openTxns(rqst);
        }
        
        @Override
        public void abort_txn(final AbortTxnRequest rqst) throws NoSuchTxnException, TException {
            this.getTxnHandler().abortTxn(rqst);
        }
        
        @Override
        public void commit_txn(final CommitTxnRequest rqst) throws NoSuchTxnException, TxnAbortedException, TException {
            this.getTxnHandler().commitTxn(rqst);
        }
        
        @Override
        public LockResponse lock(final LockRequest rqst) throws NoSuchTxnException, TxnAbortedException, TException {
            return this.getTxnHandler().lock(rqst);
        }
        
        @Override
        public LockResponse check_lock(final CheckLockRequest rqst) throws NoSuchTxnException, TxnAbortedException, NoSuchLockException, TException {
            return this.getTxnHandler().checkLock(rqst);
        }
        
        @Override
        public void unlock(final UnlockRequest rqst) throws NoSuchLockException, TxnOpenException, TException {
            this.getTxnHandler().unlock(rqst);
        }
        
        @Override
        public ShowLocksResponse show_locks(final ShowLocksRequest rqst) throws TException {
            return this.getTxnHandler().showLocks(rqst);
        }
        
        @Override
        public void heartbeat(final HeartbeatRequest ids) throws NoSuchLockException, NoSuchTxnException, TxnAbortedException, TException {
            this.getTxnHandler().heartbeat(ids);
        }
        
        @Override
        public HeartbeatTxnRangeResponse heartbeat_txn_range(final HeartbeatTxnRangeRequest rqst) throws TException {
            return this.getTxnHandler().heartbeatTxnRange(rqst);
        }
        
        @Override
        public void compact(final CompactionRequest rqst) throws TException {
            this.getTxnHandler().compact(rqst);
        }
        
        @Override
        public ShowCompactResponse show_compact(final ShowCompactRequest rqst) throws TException {
            return this.getTxnHandler().showCompact(rqst);
        }
        
        @Override
        public void add_dynamic_partitions(final AddDynamicPartitions rqst) throws NoSuchTxnException, TxnAbortedException, TException {
            this.getTxnHandler().addDynamicPartitions(rqst);
        }
        
        @Override
        public GetPrincipalsInRoleResponse get_principals_in_role(final GetPrincipalsInRoleRequest request) throws MetaException, TException {
            this.incrementCounter("get_principals_in_role");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            Exception ex = null;
            List<MRoleMap> roleMaps = null;
            try {
                roleMaps = this.getMS().listRoleMembers(request.getRoleName());
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("get_principals_in_role", ex == null, ex);
            }
            return new GetPrincipalsInRoleResponse(this.getRolePrincipalGrants(roleMaps));
        }
        
        @Override
        public GetRoleGrantsForPrincipalResponse get_role_grants_for_principal(final GetRoleGrantsForPrincipalRequest request) throws MetaException, TException {
            this.incrementCounter("get_role_grants_for_principal");
            this.firePreEvent(new PreAuthorizationCallEvent(this));
            Exception ex = null;
            List<MRoleMap> roleMaps = null;
            try {
                roleMaps = this.getMS().listRoles(request.getPrincipal_name(), request.getPrincipal_type());
            }
            catch (MetaException e) {
                throw e;
            }
            catch (Exception e2) {
                ex = e2;
                this.rethrowException(e2);
            }
            finally {
                this.endFunction("get_role_grants_for_principal", ex == null, ex);
            }
            final List<RolePrincipalGrant> roleGrantsList = this.getRolePrincipalGrants(roleMaps);
            return new GetRoleGrantsForPrincipalResponse(roleGrantsList);
        }
        
        private List<RolePrincipalGrant> getRolePrincipalGrants(final List<MRoleMap> roleMaps) {
            final List<RolePrincipalGrant> rolePrinGrantList = new ArrayList<RolePrincipalGrant>();
            if (roleMaps != null) {
                for (final MRoleMap roleMap : roleMaps) {
                    final RolePrincipalGrant rolePrinGrant = new RolePrincipalGrant(roleMap.getRole().getRoleName(), roleMap.getPrincipalName(), PrincipalType.valueOf(roleMap.getPrincipalType()), roleMap.getGrantOption(), roleMap.getAddTime(), roleMap.getGrantor(), (roleMap.getGrantorType() == null) ? null : PrincipalType.valueOf(roleMap.getGrantorType()));
                    rolePrinGrantList.add(rolePrinGrant);
                }
            }
            return rolePrinGrantList;
        }
        
        @Override
        public AggrStats get_aggr_stats_for(final PartitionsStatsRequest request) throws NoSuchObjectException, MetaException, TException {
            final String dbName = request.getDbName().toLowerCase();
            final String tblName = request.getTblName().toLowerCase();
            this.startFunction("get_aggr_stats_for: db=" + request.getDbName() + " table=" + request.getTblName());
            final List<String> lowerCaseColNames = new ArrayList<String>(request.getColNames().size());
            for (final String colName : request.getColNames()) {
                lowerCaseColNames.add(colName.toLowerCase());
            }
            final List<String> lowerCasePartNames = new ArrayList<String>(request.getPartNames().size());
            for (final String partName : request.getPartNames()) {
                lowerCasePartNames.add(this.lowerCaseConvertPartName(partName));
            }
            AggrStats aggrStats = null;
            try {
                aggrStats = new AggrStats(this.getMS().get_aggr_stats_for(dbName, tblName, lowerCasePartNames, lowerCaseColNames));
                return aggrStats;
            }
            finally {
                this.endFunction("get_partitions_statistics_req: ", aggrStats == null, null, request.getTblName());
            }
        }
        
        @Override
        public boolean set_aggr_stats_for(final SetPartitionsStatsRequest request) throws NoSuchObjectException, InvalidObjectException, MetaException, InvalidInputException, TException {
            boolean ret = true;
            for (final ColumnStatistics colStats : request.getColStats()) {
                ret = (ret && this.update_partition_column_statistics(colStats));
            }
            return ret;
        }
        
        @Override
        public NotificationEventResponse get_next_notification(final NotificationEventRequest rqst) throws TException {
            final RawStore ms = this.getMS();
            return ms.getNextNotification(rqst);
        }
        
        @Override
        public CurrentNotificationEventId get_current_notificationEventId() throws TException {
            final RawStore ms = this.getMS();
            return ms.getCurrentNotificationEventId();
        }
        
        @Override
        public FireEventResponse fire_listener_event(final FireEventRequest rqst) throws TException {
            switch (((TUnion<T, FireEventRequestData._Fields>)rqst.getData()).getSetField()) {
                case INSERT_DATA: {
                    final InsertEvent event = new InsertEvent(rqst.getDbName(), rqst.getTableName(), rqst.getPartitionVals(), rqst.getData().getInsertData().getFilesAdded(), rqst.isSuccessful(), this);
                    for (final MetaStoreEventListener listener : this.listeners) {
                        listener.onInsert(event);
                    }
                    return new FireEventResponse();
                }
                default: {
                    throw new TException("Event type " + ((TUnion<T, FireEventRequestData._Fields>)rqst.getData()).getSetField().toString() + " not currently supported.");
                }
            }
        }
        
        static {
            LOG = HiveMetaStore.LOG;
            threadLocalMS = new ThreadLocal<RawStore>() {
                @Override
                protected synchronized RawStore initialValue() {
                    return null;
                }
            };
            threadLocalTxn = new ThreadLocal<TxnHandler>() {
                @Override
                protected synchronized TxnHandler initialValue() {
                    return null;
                }
            };
            threadLocalConf = new ThreadLocal<Configuration>() {
                @Override
                protected synchronized Configuration initialValue() {
                    return null;
                }
            };
            auditLog = LogFactory.getLog(HiveMetaStore.class.getName() + ".audit");
            auditFormatter = new ThreadLocal<Formatter>() {
                @Override
                protected Formatter initialValue() {
                    return new Formatter(new StringBuilder("ugi=%s\tip=%s\tcmd=%s\t".length() * 4));
                }
            };
            HMSHandler.nextSerialNum = 0;
            HMSHandler.threadLocalId = new ThreadLocal<Integer>() {
                @Override
                protected synchronized Integer initialValue() {
                    return new Integer(HMSHandler.nextSerialNum++);
                }
            };
            HMSHandler.threadLocalIpAddress = new ThreadLocal<String>() {
                @Override
                protected synchronized String initialValue() {
                    return null;
                }
            };
        }
        
        private static class PartValEqWrapper
        {
            Partition partition;
            
            public PartValEqWrapper(final Partition partition) {
                this.partition = partition;
            }
            
            @Override
            public int hashCode() {
                return this.partition.isSetValues() ? this.partition.getValues().hashCode() : 0;
            }
            
            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || !(obj instanceof PartValEqWrapper)) {
                    return false;
                }
                final Partition p1 = this.partition;
                final Partition p2 = ((PartValEqWrapper)obj).partition;
                if (!p1.isSetValues() || !p2.isSetValues()) {
                    return p1.isSetValues() == p2.isSetValues();
                }
                if (p1.getValues().size() != p2.getValues().size()) {
                    return false;
                }
                for (int i = 0; i < p1.getValues().size(); ++i) {
                    final String v1 = p1.getValues().get(i);
                    final String v2 = p2.getValues().get(i);
                    if ((v1 == null && v2 != null) || !v1.equals(v2)) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        private static class PartValEqWrapperLite
        {
            List<String> values;
            String location;
            
            public PartValEqWrapperLite(final Partition partition) {
                this.values = (partition.isSetValues() ? partition.getValues() : null);
                this.location = partition.getSd().getLocation();
            }
            
            @Override
            public int hashCode() {
                return (this.values == null) ? 0 : this.values.hashCode();
            }
            
            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || !(obj instanceof PartValEqWrapperLite)) {
                    return false;
                }
                final List<String> lhsValues = this.values;
                final List<String> rhsValues = ((PartValEqWrapperLite)obj).values;
                if (lhsValues == null || rhsValues == null) {
                    return lhsValues == rhsValues;
                }
                if (lhsValues.size() != rhsValues.size()) {
                    return false;
                }
                for (int i = 0; i < lhsValues.size(); ++i) {
                    final String lhsValue = lhsValues.get(i);
                    final String rhsValue = rhsValues.get(i);
                    if ((lhsValue == null && rhsValue != null) || (lhsValue != null && !lhsValue.equals(rhsValue))) {
                        return false;
                    }
                }
                return true;
            }
        }
        
        private static class PathAndPartValSize
        {
            public Path path;
            public int partValSize;
            
            public PathAndPartValSize(final Path path, final int partValSize) {
                this.path = path;
                this.partValSize = partValSize;
            }
        }
        
        private static class StorageDescriptorKey
        {
            private final StorageDescriptor sd;
            
            StorageDescriptorKey(final StorageDescriptor sd) {
                this.sd = sd;
            }
            
            StorageDescriptor getSd() {
                return this.sd;
            }
            
            private String hashCodeKey() {
                return this.sd.getInputFormat() + "\t" + this.sd.getOutputFormat() + "\t" + this.sd.getSerdeInfo().getSerializationLib() + "\t" + this.sd.getCols();
            }
            
            @Override
            public int hashCode() {
                return this.hashCodeKey().hashCode();
            }
            
            @Override
            public boolean equals(final Object rhs) {
                return rhs == this || (rhs instanceof StorageDescriptorKey && this.hashCodeKey().equals(((StorageDescriptorKey)rhs).hashCodeKey()));
            }
        }
    }
    
    public static class HiveMetastoreCli extends CommonCliOptions
    {
        int port;
        
        public HiveMetastoreCli() {
            super("hivemetastore", true);
            this.port = 9083;
            final Options options = this.OPTIONS;
            OptionBuilder.hasArg();
            OptionBuilder.withArgName("port");
            OptionBuilder.withDescription("Hive Metastore port number, default:9083");
            options.addOption(OptionBuilder.create('p'));
        }
        
        @Override
        public void parse(String[] args) {
            super.parse(args);
            args = this.commandLine.getArgs();
            if (args.length > 0) {
                System.err.println("This usage has been deprecated, consider using the new command line syntax (run with -h to see usage information)");
                this.port = new Integer(args[0]);
            }
            if (this.commandLine.hasOption('p')) {
                this.port = Integer.parseInt(this.commandLine.getOptionValue('p'));
            }
            else {
                final String metastorePort = System.getenv("METASTORE_PORT");
                if (metastorePort != null) {
                    this.port = Integer.parseInt(metastorePort);
                }
            }
        }
    }
}
