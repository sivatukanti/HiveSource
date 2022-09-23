// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.conf;

import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.api.ApplicationConstants;
import java.util.Collections;
import java.util.Arrays;
import org.apache.hadoop.http.HttpConfig;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.net.NetUtils;
import java.net.InetSocketAddress;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configuration;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class YarnConfiguration extends Configuration
{
    @InterfaceAudience.Private
    public static final String CS_CONFIGURATION_FILE = "capacity-scheduler.xml";
    @InterfaceAudience.Private
    public static final String HADOOP_POLICY_CONFIGURATION_FILE = "hadoop-policy.xml";
    @InterfaceAudience.Private
    public static final String YARN_SITE_CONFIGURATION_FILE = "yarn-site.xml";
    private static final String YARN_DEFAULT_CONFIGURATION_FILE = "yarn-default.xml";
    @InterfaceAudience.Private
    public static final String CORE_SITE_CONFIGURATION_FILE = "core-site.xml";
    @InterfaceAudience.Private
    public static final List<String> RM_CONFIGURATION_FILES;
    @InterfaceStability.Evolving
    public static final int APPLICATION_MAX_TAGS = 10;
    @InterfaceStability.Evolving
    public static final int APPLICATION_MAX_TAG_LENGTH = 100;
    public static final String YARN_PREFIX = "yarn.";
    public static final String DEBUG_NM_DELETE_DELAY_SEC = "yarn.nodemanager.delete.debug-delay-sec";
    public static final String IPC_PREFIX = "yarn.ipc.";
    public static final String IPC_CLIENT_FACTORY_CLASS = "yarn.ipc.client.factory.class";
    public static final String DEFAULT_IPC_CLIENT_FACTORY_CLASS = "org.apache.hadoop.yarn.factories.impl.pb.RpcClientFactoryPBImpl";
    public static final String IPC_SERVER_FACTORY_CLASS = "yarn.ipc.server.factory.class";
    public static final String DEFAULT_IPC_SERVER_FACTORY_CLASS = "org.apache.hadoop.yarn.factories.impl.pb.RpcServerFactoryPBImpl";
    public static final String IPC_RECORD_FACTORY_CLASS = "yarn.ipc.record.factory.class";
    public static final String DEFAULT_IPC_RECORD_FACTORY_CLASS = "org.apache.hadoop.yarn.factories.impl.pb.RecordFactoryPBImpl";
    public static final String IPC_RPC_IMPL = "yarn.ipc.rpc.class";
    public static final String DEFAULT_IPC_RPC_IMPL = "org.apache.hadoop.yarn.ipc.HadoopYarnProtoRPC";
    public static final String RM_PREFIX = "yarn.resourcemanager.";
    public static final String RM_CLUSTER_ID = "yarn.resourcemanager.cluster-id";
    public static final String RM_HOSTNAME = "yarn.resourcemanager.hostname";
    public static final String RM_ADDRESS = "yarn.resourcemanager.address";
    public static final int DEFAULT_RM_PORT = 8032;
    public static final String DEFAULT_RM_ADDRESS = "0.0.0.0:8032";
    public static final String RM_BIND_HOST = "yarn.resourcemanager.bind-host";
    public static final String RM_CLIENT_THREAD_COUNT = "yarn.resourcemanager.client.thread-count";
    public static final int DEFAULT_RM_CLIENT_THREAD_COUNT = 50;
    public static final String RM_PRINCIPAL = "yarn.resourcemanager.principal";
    public static final String RM_SCHEDULER_ADDRESS = "yarn.resourcemanager.scheduler.address";
    public static final int DEFAULT_RM_SCHEDULER_PORT = 8030;
    public static final String DEFAULT_RM_SCHEDULER_ADDRESS = "0.0.0.0:8030";
    public static final String RM_SCHEDULER_MINIMUM_ALLOCATION_MB = "yarn.scheduler.minimum-allocation-mb";
    public static final int DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_MB = 1024;
    public static final String RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES = "yarn.scheduler.minimum-allocation-vcores";
    public static final int DEFAULT_RM_SCHEDULER_MINIMUM_ALLOCATION_VCORES = 1;
    public static final String RM_SCHEDULER_MAXIMUM_ALLOCATION_MB = "yarn.scheduler.maximum-allocation-mb";
    public static final int DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_MB = 8192;
    public static final String RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES = "yarn.scheduler.maximum-allocation-vcores";
    public static final int DEFAULT_RM_SCHEDULER_MAXIMUM_ALLOCATION_VCORES = 4;
    public static final String RM_SCHEDULER_CLIENT_THREAD_COUNT = "yarn.resourcemanager.scheduler.client.thread-count";
    public static final int DEFAULT_RM_SCHEDULER_CLIENT_THREAD_COUNT = 50;
    public static final String RM_SCHEDULER_INCLUDE_PORT_IN_NODE_NAME = "yarn.scheduler.include-port-in-node-name";
    public static final boolean DEFAULT_RM_SCHEDULER_USE_PORT_FOR_NODE_NAME = false;
    public static final String RM_RESERVATION_SYSTEM_ENABLE = "yarn.resourcemanager.reservation-system.enable";
    public static final boolean DEFAULT_RM_RESERVATION_SYSTEM_ENABLE = false;
    public static final String RM_RESERVATION_SYSTEM_CLASS = "yarn.resourcemanager.reservation-system.class";
    public static final String RM_RESERVATION_SYSTEM_PLAN_FOLLOWER = "yarn.resourcemanager.reservation-system.plan.follower";
    public static final String RM_RESERVATION_SYSTEM_PLAN_FOLLOWER_TIME_STEP = "yarn.resourcemanager.reservation-system.planfollower.time-step";
    public static final long DEFAULT_RM_RESERVATION_SYSTEM_PLAN_FOLLOWER_TIME_STEP = 1000L;
    public static final String RM_SCHEDULER_ENABLE_MONITORS = "yarn.resourcemanager.scheduler.monitor.enable";
    public static final boolean DEFAULT_RM_SCHEDULER_ENABLE_MONITORS = false;
    public static final String RM_SCHEDULER_MONITOR_POLICIES = "yarn.resourcemanager.scheduler.monitor.policies";
    public static final String RM_WEBAPP_ADDRESS = "yarn.resourcemanager.webapp.address";
    public static final int DEFAULT_RM_WEBAPP_PORT = 8088;
    public static final String DEFAULT_RM_WEBAPP_ADDRESS = "0.0.0.0:8088";
    public static final String RM_WEBAPP_HTTPS_ADDRESS = "yarn.resourcemanager.webapp.https.address";
    public static final boolean YARN_SSL_CLIENT_HTTPS_NEED_AUTH_DEFAULT = false;
    public static final String YARN_SSL_SERVER_RESOURCE_DEFAULT = "ssl-server.xml";
    public static final int DEFAULT_RM_WEBAPP_HTTPS_PORT = 8090;
    public static final String DEFAULT_RM_WEBAPP_HTTPS_ADDRESS = "0.0.0.0:8090";
    public static final String RM_RESOURCE_TRACKER_ADDRESS = "yarn.resourcemanager.resource-tracker.address";
    public static final int DEFAULT_RM_RESOURCE_TRACKER_PORT = 8031;
    public static final String DEFAULT_RM_RESOURCE_TRACKER_ADDRESS = "0.0.0.0:8031";
    public static final String RM_AM_EXPIRY_INTERVAL_MS = "yarn.am.liveness-monitor.expiry-interval-ms";
    public static final int DEFAULT_RM_AM_EXPIRY_INTERVAL_MS = 600000;
    public static final String RM_NM_EXPIRY_INTERVAL_MS = "yarn.nm.liveness-monitor.expiry-interval-ms";
    public static final int DEFAULT_RM_NM_EXPIRY_INTERVAL_MS = 600000;
    public static final String YARN_ACL_ENABLE = "yarn.acl.enable";
    public static final boolean DEFAULT_YARN_ACL_ENABLE = false;
    public static final String YARN_ADMIN_ACL = "yarn.admin.acl";
    public static final String DEFAULT_YARN_ADMIN_ACL = "*";
    public static final String DEFAULT_YARN_APP_ACL = " ";
    public static final String RM_ADMIN_ADDRESS = "yarn.resourcemanager.admin.address";
    public static final int DEFAULT_RM_ADMIN_PORT = 8033;
    public static final String DEFAULT_RM_ADMIN_ADDRESS = "0.0.0.0:8033";
    public static final String RM_ADMIN_CLIENT_THREAD_COUNT = "yarn.resourcemanager.admin.client.thread-count";
    public static final int DEFAULT_RM_ADMIN_CLIENT_THREAD_COUNT = 1;
    public static final String RM_AM_MAX_ATTEMPTS = "yarn.resourcemanager.am.max-attempts";
    public static final int DEFAULT_RM_AM_MAX_ATTEMPTS = 2;
    public static final String RM_KEYTAB = "yarn.resourcemanager.keytab";
    public static final String RM_WEBAPP_SPNEGO_USER_NAME_KEY = "yarn.resourcemanager.webapp.spnego-principal";
    public static final String RM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY = "yarn.resourcemanager.webapp.spnego-keytab-file";
    public static final String RM_WEBAPP_DELEGATION_TOKEN_AUTH_FILTER = "yarn.resourcemanager.webapp.delegation-token-auth-filter.enabled";
    public static final boolean DEFAULT_RM_WEBAPP_DELEGATION_TOKEN_AUTH_FILTER = true;
    public static final String RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS = "yarn.resourcemanager.rm.container-allocation.expiry-interval-ms";
    public static final int DEFAULT_RM_CONTAINER_ALLOC_EXPIRY_INTERVAL_MS = 600000;
    public static final String RM_NODES_INCLUDE_FILE_PATH = "yarn.resourcemanager.nodes.include-path";
    public static final String DEFAULT_RM_NODES_INCLUDE_FILE_PATH = "";
    public static final String RM_NODES_EXCLUDE_FILE_PATH = "yarn.resourcemanager.nodes.exclude-path";
    public static final String DEFAULT_RM_NODES_EXCLUDE_FILE_PATH = "";
    public static final String RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT = "yarn.resourcemanager.resource-tracker.client.thread-count";
    public static final int DEFAULT_RM_RESOURCE_TRACKER_CLIENT_THREAD_COUNT = 50;
    public static final String RM_SCHEDULER = "yarn.resourcemanager.scheduler.class";
    public static final String DEFAULT_RM_SCHEDULER = "org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler";
    public static final String RM_NM_HEARTBEAT_INTERVAL_MS = "yarn.resourcemanager.nodemanagers.heartbeat-interval-ms";
    public static final long DEFAULT_RM_NM_HEARTBEAT_INTERVAL_MS = 1000L;
    public static final String RM_HISTORY_WRITER_MULTI_THREADED_DISPATCHER_POOL_SIZE = "yarn.resourcemanager.history-writer.multi-threaded-dispatcher.pool-size";
    public static final int DEFAULT_RM_HISTORY_WRITER_MULTI_THREADED_DISPATCHER_POOL_SIZE = 10;
    public static final String RM_SYSTEM_METRICS_PUBLISHER_ENABLED = "yarn.resourcemanager.system-metrics-publisher.enabled";
    public static final boolean DEFAULT_RM_SYSTEM_METRICS_PUBLISHER_ENABLED = false;
    public static final String RM_SYSTEM_METRICS_PUBLISHER_DISPATCHER_POOL_SIZE = "yarn.resourcemanager.system-metrics-publisher.dispatcher.pool-size";
    public static final int DEFAULT_RM_SYSTEM_METRICS_PUBLISHER_DISPATCHER_POOL_SIZE = 10;
    public static final String DELEGATION_KEY_UPDATE_INTERVAL_KEY = "yarn.resourcemanager.delegation.key.update-interval";
    public static final long DELEGATION_KEY_UPDATE_INTERVAL_DEFAULT = 86400000L;
    public static final String DELEGATION_TOKEN_RENEW_INTERVAL_KEY = "yarn.resourcemanager.delegation.token.renew-interval";
    public static final long DELEGATION_TOKEN_RENEW_INTERVAL_DEFAULT = 86400000L;
    public static final String DELEGATION_TOKEN_MAX_LIFETIME_KEY = "yarn.resourcemanager.delegation.token.max-lifetime";
    public static final long DELEGATION_TOKEN_MAX_LIFETIME_DEFAULT = 604800000L;
    public static final String RECOVERY_ENABLED = "yarn.resourcemanager.recovery.enabled";
    public static final boolean DEFAULT_RM_RECOVERY_ENABLED = false;
    @InterfaceAudience.Private
    public static final String RM_WORK_PRESERVING_RECOVERY_ENABLED = "yarn.resourcemanager.work-preserving-recovery.enabled";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_RM_WORK_PRESERVING_RECOVERY_ENABLED = false;
    public static final String RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS = "yarn.resourcemanager.work-preserving-recovery.scheduling-wait-ms";
    public static final long DEFAULT_RM_WORK_PRESERVING_RECOVERY_SCHEDULING_WAIT_MS = 10000L;
    public static final String RM_ZK_PREFIX = "yarn.resourcemanager.zk-";
    public static final String RM_ZK_ADDRESS = "yarn.resourcemanager.zk-address";
    public static final String RM_ZK_NUM_RETRIES = "yarn.resourcemanager.zk-num-retries";
    public static final int DEFAULT_ZK_RM_NUM_RETRIES = 1000;
    public static final String RM_ZK_RETRY_INTERVAL_MS = "yarn.resourcemanager.zk-retry-interval-ms";
    public static final long DEFAULT_RM_ZK_RETRY_INTERVAL_MS = 1000L;
    public static final String RM_ZK_TIMEOUT_MS = "yarn.resourcemanager.zk-timeout-ms";
    public static final int DEFAULT_RM_ZK_TIMEOUT_MS = 10000;
    public static final String RM_ZK_ACL = "yarn.resourcemanager.zk-acl";
    public static final String DEFAULT_RM_ZK_ACL = "world:anyone:rwcda";
    public static final String RM_ZK_AUTH = "yarn.resourcemanager.zk-auth";
    public static final String ZK_STATE_STORE_PREFIX = "yarn.resourcemanager.zk-state-store.";
    public static final String ZK_RM_STATE_STORE_PARENT_PATH = "yarn.resourcemanager.zk-state-store.parent-path";
    public static final String DEFAULT_ZK_RM_STATE_STORE_PARENT_PATH = "/rmstore";
    public static final String ZK_RM_STATE_STORE_ROOT_NODE_ACL = "yarn.resourcemanager.zk-state-store.root-node.acl";
    public static final String RM_HA_PREFIX = "yarn.resourcemanager.ha.";
    public static final String RM_HA_ENABLED = "yarn.resourcemanager.ha.enabled";
    public static final boolean DEFAULT_RM_HA_ENABLED = false;
    public static final String RM_HA_IDS = "yarn.resourcemanager.ha.rm-ids";
    public static final String RM_HA_ID = "yarn.resourcemanager.ha.id";
    public static final String FS_BASED_RM_CONF_STORE = "yarn.resourcemanager.configuration.file-system-based-store";
    public static final String DEFAULT_FS_BASED_RM_CONF_STORE = "/yarn/conf";
    public static final String RM_CONFIGURATION_PROVIDER_CLASS = "yarn.resourcemanager.configuration.provider-class";
    public static final String DEFAULT_RM_CONFIGURATION_PROVIDER_CLASS = "org.apache.hadoop.yarn.LocalConfigurationProvider";
    private static final List<String> RM_SERVICES_ADDRESS_CONF_KEYS_HTTP;
    private static final List<String> RM_SERVICES_ADDRESS_CONF_KEYS_HTTPS;
    public static final String AUTO_FAILOVER_PREFIX = "yarn.resourcemanager.ha.automatic-failover.";
    public static final String AUTO_FAILOVER_ENABLED = "yarn.resourcemanager.ha.automatic-failover.enabled";
    public static final boolean DEFAULT_AUTO_FAILOVER_ENABLED = true;
    public static final String AUTO_FAILOVER_EMBEDDED = "yarn.resourcemanager.ha.automatic-failover.embedded";
    public static final boolean DEFAULT_AUTO_FAILOVER_EMBEDDED = true;
    public static final String AUTO_FAILOVER_ZK_BASE_PATH = "yarn.resourcemanager.ha.automatic-failover.zk-base-path";
    public static final String DEFAULT_AUTO_FAILOVER_ZK_BASE_PATH = "/yarn-leader-election";
    public static final String CLIENT_FAILOVER_PREFIX = "yarn.client.failover-";
    public static final String CLIENT_FAILOVER_PROXY_PROVIDER = "yarn.client.failover-proxy-provider";
    public static final String DEFAULT_CLIENT_FAILOVER_PROXY_PROVIDER = "org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider";
    public static final String CLIENT_FAILOVER_MAX_ATTEMPTS = "yarn.client.failover-max-attempts";
    public static final String CLIENT_FAILOVER_SLEEPTIME_BASE_MS = "yarn.client.failover-sleep-base-ms";
    public static final String CLIENT_FAILOVER_SLEEPTIME_MAX_MS = "yarn.client.failover-sleep-max-ms";
    public static final String CLIENT_FAILOVER_RETRIES = "yarn.client.failover-retries";
    public static final int DEFAULT_CLIENT_FAILOVER_RETRIES = 0;
    public static final String CLIENT_FAILOVER_RETRIES_ON_SOCKET_TIMEOUTS = "yarn.client.failover-retries-on-socket-timeouts";
    public static final int DEFAULT_CLIENT_FAILOVER_RETRIES_ON_SOCKET_TIMEOUTS = 0;
    public static final String RM_STORE = "yarn.resourcemanager.store.class";
    public static final String FS_RM_STATE_STORE_URI = "yarn.resourcemanager.fs.state-store.uri";
    public static final String FS_RM_STATE_STORE_RETRY_POLICY_SPEC = "yarn.resourcemanager.fs.state-store.retry-policy-spec";
    public static final String DEFAULT_FS_RM_STATE_STORE_RETRY_POLICY_SPEC = "2000, 500";
    public static final String RM_MAX_COMPLETED_APPLICATIONS = "yarn.resourcemanager.max-completed-applications";
    public static final int DEFAULT_RM_MAX_COMPLETED_APPLICATIONS = 10000;
    public static final String RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS = "yarn.resourcemanager.state-store.max-completed-applications";
    public static final int DEFAULT_RM_STATE_STORE_MAX_COMPLETED_APPLICATIONS = 10000;
    public static final String DEFAULT_APPLICATION_NAME = "N/A";
    public static final String DEFAULT_APPLICATION_TYPE = "YARN";
    public static final int APPLICATION_TYPE_LENGTH = 20;
    public static final String DEFAULT_QUEUE_NAME = "default";
    public static final String RM_METRICS_RUNTIME_BUCKETS = "yarn.resourcemanager.metrics.runtime.buckets";
    public static final String DEFAULT_RM_METRICS_RUNTIME_BUCKETS = "60,300,1440";
    public static final String RM_AMRM_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS = "yarn.resourcemanager.am-rm-tokens.master-key-rolling-interval-secs";
    public static final long DEFAULT_RM_AMRM_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS = 86400L;
    public static final String RM_CONTAINER_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS = "yarn.resourcemanager.container-tokens.master-key-rolling-interval-secs";
    public static final long DEFAULT_RM_CONTAINER_TOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS = 86400L;
    public static final String RM_NMTOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS = "yarn.resourcemanager.nm-tokens.master-key-rolling-interval-secs";
    public static final long DEFAULT_RM_NMTOKEN_MASTER_KEY_ROLLING_INTERVAL_SECS = 86400L;
    public static final String RM_NODEMANAGER_MINIMUM_VERSION = "yarn.resourcemanager.nodemanager.minimum.version";
    public static final String DEFAULT_RM_NODEMANAGER_MINIMUM_VERSION = "NONE";
    public static final String RM_PROXY_USER_PREFIX = "yarn.resourcemanager.proxyuser.";
    public static final String NM_PREFIX = "yarn.nodemanager.";
    public static final String NM_ADMIN_USER_ENV = "yarn.nodemanager.admin-env";
    public static final String DEFAULT_NM_ADMIN_USER_ENV = "MALLOC_ARENA_MAX=$MALLOC_ARENA_MAX";
    public static final String NM_ENV_WHITELIST = "yarn.nodemanager.env-whitelist";
    public static final String DEFAULT_NM_ENV_WHITELIST;
    public static final String NM_ADDRESS = "yarn.nodemanager.address";
    public static final int DEFAULT_NM_PORT = 0;
    public static final String DEFAULT_NM_ADDRESS = "0.0.0.0:0";
    public static final String NM_BIND_HOST = "yarn.nodemanager.bind-host";
    public static final String NM_CONTAINER_EXECUTOR = "yarn.nodemanager.container-executor.class";
    public static final String NM_CONTAINER_EXECUTOR_SCHED_PRIORITY = "yarn.nodemanager.container-executor.os.sched.priority.adjustment";
    public static final int DEFAULT_NM_CONTAINER_EXECUTOR_SCHED_PRIORITY = 0;
    public static final String NM_CONTAINER_MGR_THREAD_COUNT = "yarn.nodemanager.container-manager.thread-count";
    public static final int DEFAULT_NM_CONTAINER_MGR_THREAD_COUNT = 20;
    public static final String NM_DELETE_THREAD_COUNT = "yarn.nodemanager.delete.thread-count";
    public static final int DEFAULT_NM_DELETE_THREAD_COUNT = 4;
    public static final String NM_KEYTAB = "yarn.nodemanager.keytab";
    public static final String NM_LOCAL_DIRS = "yarn.nodemanager.local-dirs";
    public static final String DEFAULT_NM_LOCAL_DIRS = "/tmp/nm-local-dir";
    public static final String NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY = "yarn.nodemanager.local-cache.max-files-per-directory";
    public static final int DEFAULT_NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY = 8192;
    public static final String NM_LOCALIZER_ADDRESS = "yarn.nodemanager.localizer.address";
    public static final int DEFAULT_NM_LOCALIZER_PORT = 8040;
    public static final String DEFAULT_NM_LOCALIZER_ADDRESS = "0.0.0.0:8040";
    public static final String NM_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS = "yarn.nodemanager.localizer.cache.cleanup.interval-ms";
    public static final long DEFAULT_NM_LOCALIZER_CACHE_CLEANUP_INTERVAL_MS = 600000L;
    public static final String NM_LOCALIZER_CACHE_TARGET_SIZE_MB = "yarn.nodemanager.localizer.cache.target-size-mb";
    public static final long DEFAULT_NM_LOCALIZER_CACHE_TARGET_SIZE_MB = 10240L;
    public static final String NM_LOCALIZER_CLIENT_THREAD_COUNT = "yarn.nodemanager.localizer.client.thread-count";
    public static final int DEFAULT_NM_LOCALIZER_CLIENT_THREAD_COUNT = 5;
    public static final String NM_LOCALIZER_FETCH_THREAD_COUNT = "yarn.nodemanager.localizer.fetch.thread-count";
    public static final int DEFAULT_NM_LOCALIZER_FETCH_THREAD_COUNT = 4;
    public static final String NM_LOG_DIRS = "yarn.nodemanager.log-dirs";
    public static final String DEFAULT_NM_LOG_DIRS = "/tmp/logs";
    public static final String NM_RESOURCEMANAGER_MINIMUM_VERSION = "yarn.nodemanager.resourcemanager.minimum.version";
    public static final String DEFAULT_NM_RESOURCEMANAGER_MINIMUM_VERSION = "NONE";
    public static final String RM_DELAYED_DELEGATION_TOKEN_REMOVAL_INTERVAL_MS = "yarn.resourcemanager.delayed.delegation-token.removal-interval-ms";
    public static final long DEFAULT_RM_DELAYED_DELEGATION_TOKEN_REMOVAL_INTERVAL_MS = 30000L;
    public static final String RM_DELEGATION_TOKEN_RENEWER_THREAD_COUNT = "yarn.resourcemanager.delegation-token-renewer.thread-count";
    public static final int DEFAULT_RM_DELEGATION_TOKEN_RENEWER_THREAD_COUNT = 50;
    public static final String RM_PROXY_USER_PRIVILEGES_ENABLED = "yarn.resourcemanager.proxy-user-privileges.enabled";
    public static boolean DEFAULT_RM_PROXY_USER_PRIVILEGES_ENABLED;
    public static final String LOG_AGGREGATION_ENABLED = "yarn.log-aggregation-enable";
    public static final boolean DEFAULT_LOG_AGGREGATION_ENABLED = false;
    public static final String LOG_AGGREGATION_RETAIN_SECONDS = "yarn.log-aggregation.retain-seconds";
    public static final long DEFAULT_LOG_AGGREGATION_RETAIN_SECONDS = -1L;
    public static final String LOG_AGGREGATION_RETAIN_CHECK_INTERVAL_SECONDS = "yarn.log-aggregation.retain-check-interval-seconds";
    public static final long DEFAULT_LOG_AGGREGATION_RETAIN_CHECK_INTERVAL_SECONDS = -1L;
    public static final String NM_LOG_RETAIN_SECONDS = "yarn.nodemanager.log.retain-seconds";
    public static final long DEFAULT_NM_LOG_RETAIN_SECONDS = 10800L;
    public static final String NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS = "yarn.nodemanager.log-aggregation.roll-monitoring-interval-seconds";
    public static final long DEFAULT_NM_LOG_AGGREGATION_ROLL_MONITORING_INTERVAL_SECONDS = -1L;
    public static final String NM_LOG_DELETION_THREADS_COUNT = "yarn.nodemanager.log.deletion-threads-count";
    public static final int DEFAULT_NM_LOG_DELETE_THREAD_COUNT = 4;
    public static final String NM_REMOTE_APP_LOG_DIR = "yarn.nodemanager.remote-app-log-dir";
    public static final String DEFAULT_NM_REMOTE_APP_LOG_DIR = "/tmp/logs";
    public static final String NM_REMOTE_APP_LOG_DIR_SUFFIX = "yarn.nodemanager.remote-app-log-dir-suffix";
    public static final String DEFAULT_NM_REMOTE_APP_LOG_DIR_SUFFIX = "logs";
    public static final String YARN_LOG_SERVER_URL = "yarn.log.server.url";
    public static final String YARN_TRACKING_URL_GENERATOR = "yarn.tracking.url.generator";
    public static final String NM_PMEM_MB = "yarn.nodemanager.resource.memory-mb";
    public static final int DEFAULT_NM_PMEM_MB = 8192;
    public static final String NM_PMEM_CHECK_ENABLED = "yarn.nodemanager.pmem-check-enabled";
    public static final boolean DEFAULT_NM_PMEM_CHECK_ENABLED = true;
    public static final String NM_VMEM_CHECK_ENABLED = "yarn.nodemanager.vmem-check-enabled";
    public static final boolean DEFAULT_NM_VMEM_CHECK_ENABLED = true;
    public static final String NM_VMEM_PMEM_RATIO = "yarn.nodemanager.vmem-pmem-ratio";
    public static final float DEFAULT_NM_VMEM_PMEM_RATIO = 2.1f;
    public static final String NM_VCORES = "yarn.nodemanager.resource.cpu-vcores";
    public static final int DEFAULT_NM_VCORES = 8;
    public static final String NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT = "yarn.nodemanager.resource.percentage-physical-cpu-limit";
    public static final int DEFAULT_NM_RESOURCE_PERCENTAGE_PHYSICAL_CPU_LIMIT = 100;
    public static final String NM_WEBAPP_ADDRESS = "yarn.nodemanager.webapp.address";
    public static final int DEFAULT_NM_WEBAPP_PORT = 8042;
    public static final String DEFAULT_NM_WEBAPP_ADDRESS = "0.0.0.0:8042";
    public static final String NM_WEBAPP_HTTPS_ADDRESS = "yarn.nodemanager.webapp.https.address";
    public static final int DEFAULT_NM_WEBAPP_HTTPS_PORT = 8044;
    public static final String DEFAULT_NM_WEBAPP_HTTPS_ADDRESS = "0.0.0.0:8044";
    public static final String NM_CONTAINER_MON_INTERVAL_MS = "yarn.nodemanager.container-monitor.interval-ms";
    public static final int DEFAULT_NM_CONTAINER_MON_INTERVAL_MS = 3000;
    public static final String NM_CONTAINER_MON_RESOURCE_CALCULATOR = "yarn.nodemanager.container-monitor.resource-calculator.class";
    public static final String NM_CONTAINER_MON_PROCESS_TREE = "yarn.nodemanager.container-monitor.process-tree.class";
    public static final String PROCFS_USE_SMAPS_BASED_RSS_ENABLED = "yarn.nodemanager..container-monitor.procfs-tree.smaps-based-rss.enabled";
    public static final boolean DEFAULT_PROCFS_USE_SMAPS_BASED_RSS_ENABLED = false;
    private static final String NM_DISK_HEALTH_CHECK_PREFIX = "yarn.nodemanager.disk-health-checker.";
    public static final String NM_DISK_HEALTH_CHECK_ENABLE = "yarn.nodemanager.disk-health-checker.enable";
    public static final String NM_DISK_HEALTH_CHECK_INTERVAL_MS = "yarn.nodemanager.disk-health-checker.interval-ms";
    public static final long DEFAULT_NM_DISK_HEALTH_CHECK_INTERVAL_MS = 120000L;
    public static final String NM_MIN_HEALTHY_DISKS_FRACTION = "yarn.nodemanager.disk-health-checker.min-healthy-disks";
    public static final float DEFAULT_NM_MIN_HEALTHY_DISKS_FRACTION = 0.25f;
    public static final String NM_MAX_PER_DISK_UTILIZATION_PERCENTAGE = "yarn.nodemanager.disk-health-checker.max-disk-utilization-per-disk-percentage";
    public static final float DEFAULT_NM_MAX_PER_DISK_UTILIZATION_PERCENTAGE = 90.0f;
    public static final String NM_MIN_PER_DISK_FREE_SPACE_MB = "yarn.nodemanager.disk-health-checker.min-free-space-per-disk-mb";
    public static final long DEFAULT_NM_MIN_PER_DISK_FREE_SPACE_MB = 0L;
    public static final String NM_HEALTH_CHECK_INTERVAL_MS = "yarn.nodemanager.health-checker.interval-ms";
    public static final long DEFAULT_NM_HEALTH_CHECK_INTERVAL_MS = 600000L;
    public static final String NM_HEALTH_CHECK_SCRIPT_TIMEOUT_MS = "yarn.nodemanager.health-checker.script.timeout-ms";
    public static final long DEFAULT_NM_HEALTH_CHECK_SCRIPT_TIMEOUT_MS = 1200000L;
    public static final String NM_HEALTH_CHECK_SCRIPT_PATH = "yarn.nodemanager.health-checker.script.path";
    public static final String NM_HEALTH_CHECK_SCRIPT_OPTS = "yarn.nodemanager.health-checker.script.opts";
    public static final String NM_DOCKER_CONTAINER_EXECUTOR_IMAGE_NAME = "yarn.nodemanager.docker-container-executor.image-name";
    public static final String NM_DOCKER_CONTAINER_EXECUTOR_EXEC_NAME = "yarn.nodemanager.docker-container-executor.exec-name";
    public static final String NM_DEFAULT_DOCKER_CONTAINER_EXECUTOR_EXEC_NAME = "/usr/bin/docker";
    public static final String NM_LINUX_CONTAINER_EXECUTOR_PATH = "yarn.nodemanager.linux-container-executor.path";
    public static final String NM_LINUX_CONTAINER_GROUP = "yarn.nodemanager.linux-container-executor.group";
    public static final String NM_NONSECURE_MODE_LIMIT_USERS = "yarn.nodemanager.linux-container-executor.nonsecure-mode.limit-users";
    public static final boolean DEFAULT_NM_NONSECURE_MODE_LIMIT_USERS = true;
    public static final String NM_NONSECURE_MODE_LOCAL_USER_KEY = "yarn.nodemanager.linux-container-executor.nonsecure-mode.local-user";
    public static final String DEFAULT_NM_NONSECURE_MODE_LOCAL_USER = "nobody";
    public static final String NM_NONSECURE_MODE_USER_PATTERN_KEY = "yarn.nodemanager.linux-container-executor.nonsecure-mode.user-pattern";
    public static final String DEFAULT_NM_NONSECURE_MODE_USER_PATTERN = "^[_.A-Za-z0-9][-@_.A-Za-z0-9]{0,255}?[$]?$";
    public static final String NM_LINUX_CONTAINER_RESOURCES_HANDLER = "yarn.nodemanager.linux-container-executor.resources-handler.class";
    public static final String NM_LINUX_CONTAINER_CGROUPS_HIERARCHY = "yarn.nodemanager.linux-container-executor.cgroups.hierarchy";
    public static final String NM_LINUX_CONTAINER_CGROUPS_MOUNT = "yarn.nodemanager.linux-container-executor.cgroups.mount";
    public static final String NM_LINUX_CONTAINER_CGROUPS_MOUNT_PATH = "yarn.nodemanager.linux-container-executor.cgroups.mount-path";
    public static final String NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE = "yarn.nodemanager.linux-container-executor.cgroups.strict-resource-usage";
    public static final boolean DEFAULT_NM_LINUX_CONTAINER_CGROUPS_STRICT_RESOURCE_USAGE = false;
    public static final String NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT = "yarn.nodemanager.linux-container-executor.cgroups.delete-timeout-ms";
    public static final long DEFAULT_NM_LINUX_CONTAINER_CGROUPS_DELETE_TIMEOUT = 1000L;
    public static final String NM_WINDOWS_SECURE_CONTAINER_GROUP = "yarn.nodemanager.windows-secure-container-executor.group";
    public static final String NM_LOG_AGG_COMPRESSION_TYPE = "yarn.nodemanager.log-aggregation.compression-type";
    public static final String DEFAULT_NM_LOG_AGG_COMPRESSION_TYPE = "none";
    public static final String NM_PRINCIPAL = "yarn.nodemanager.principal";
    public static final String NM_AUX_SERVICES = "yarn.nodemanager.aux-services";
    public static final String NM_AUX_SERVICE_FMT = "yarn.nodemanager.aux-services.%s.class";
    public static final String NM_USER_HOME_DIR = "yarn.nodemanager.user-home-dir";
    public static final String NM_WEBAPP_SPNEGO_USER_NAME_KEY = "yarn.nodemanager.webapp.spnego-principal";
    public static final String NM_WEBAPP_SPNEGO_KEYTAB_FILE_KEY = "yarn.nodemanager.webapp.spnego-keytab-file";
    public static final String DEFAULT_NM_USER_HOME_DIR = "/home/";
    public static final String NM_RECOVERY_PREFIX = "yarn.nodemanager.recovery.";
    public static final String NM_RECOVERY_ENABLED = "yarn.nodemanager.recovery.enabled";
    public static final boolean DEFAULT_NM_RECOVERY_ENABLED = false;
    public static final String NM_RECOVERY_DIR = "yarn.nodemanager.recovery.dir";
    public static final String PROXY_PREFIX = "yarn.web-proxy.";
    public static final String PROXY_PRINCIPAL = "yarn.web-proxy.principal";
    public static final String PROXY_KEYTAB = "yarn.web-proxy.keytab";
    public static final String PROXY_ADDRESS = "yarn.web-proxy.address";
    public static final int DEFAULT_PROXY_PORT = 9099;
    public static final String DEFAULT_PROXY_ADDRESS = "0.0.0.0:9099";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCETRACKER_PROTOCOL = "security.resourcetracker.protocol.acl";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONCLIENT_PROTOCOL = "security.applicationclient.protocol.acl";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCEMANAGER_ADMINISTRATION_PROTOCOL = "security.resourcemanager-administration.protocol.acl";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONMASTER_PROTOCOL = "security.applicationmaster.protocol.acl";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_CONTAINER_MANAGEMENT_PROTOCOL = "security.containermanagement.protocol.acl";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_RESOURCE_LOCALIZER = "security.resourcelocalizer.protocol.acl";
    public static final String YARN_SECURITY_SERVICE_AUTHORIZATION_APPLICATIONHISTORY_PROTOCOL = "security.applicationhistory.protocol.acl";
    public static final String NM_SLEEP_DELAY_BEFORE_SIGKILL_MS = "yarn.nodemanager.sleep-delay-before-sigkill.ms";
    public static final long DEFAULT_NM_SLEEP_DELAY_BEFORE_SIGKILL_MS = 250L;
    public static final String NM_PROCESS_KILL_WAIT_MS = "yarn.nodemanager.process-kill-wait.ms";
    public static final long DEFAULT_NM_PROCESS_KILL_WAIT_MS = 2000L;
    public static final String RESOURCEMANAGER_CONNECT_MAX_WAIT_MS = "yarn.resourcemanager.connect.max-wait.ms";
    public static final long DEFAULT_RESOURCEMANAGER_CONNECT_MAX_WAIT_MS = 900000L;
    public static final String RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS = "yarn.resourcemanager.connect.retry-interval.ms";
    public static final long DEFAULT_RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS = 30000L;
    public static final String YARN_APPLICATION_CLASSPATH = "yarn.application.classpath";
    @InterfaceAudience.Public
    @InterfaceStability.Unstable
    public static final String[] DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH;
    public static final String[] DEFAULT_YARN_APPLICATION_CLASSPATH;
    public static final String DEFAULT_CONTAINER_TEMP_DIR = "./tmp";
    public static final String IS_MINI_YARN_CLUSTER = "yarn.is.minicluster";
    public static final String YARN_MC_PREFIX = "yarn.minicluster.";
    public static final String YARN_MINICLUSTER_FIXED_PORTS = "yarn.minicluster.fixed.ports";
    public static final boolean DEFAULT_YARN_MINICLUSTER_FIXED_PORTS = false;
    public static final String YARN_MINICLUSTER_USE_RPC = "yarn.minicluster.use-rpc";
    public static final boolean DEFAULT_YARN_MINICLUSTER_USE_RPC = false;
    public static final String YARN_MINICLUSTER_CONTROL_RESOURCE_MONITORING = "yarn.minicluster.control-resource-monitoring";
    public static final boolean DEFAULT_YARN_MINICLUSTER_CONTROL_RESOURCE_MONITORING = false;
    public static final String YARN_APP_CONTAINER_LOG_DIR = "yarn.app.container.log.dir";
    public static final String YARN_APP_CONTAINER_LOG_SIZE = "yarn.app.container.log.filesize";
    public static final String YARN_APP_CONTAINER_LOG_BACKUPS = "yarn.app.container.log.backups";
    public static final String TIMELINE_SERVICE_PREFIX = "yarn.timeline-service.";
    @InterfaceAudience.Private
    public static final String APPLICATION_HISTORY_PREFIX = "yarn.timeline-service.generic-application-history.";
    @InterfaceAudience.Private
    public static final String APPLICATION_HISTORY_ENABLED = "yarn.timeline-service.generic-application-history.enabled";
    @InterfaceAudience.Private
    public static final boolean DEFAULT_APPLICATION_HISTORY_ENABLED = false;
    @InterfaceAudience.Private
    public static final String APPLICATION_HISTORY_STORE = "yarn.timeline-service.generic-application-history.store-class";
    @InterfaceAudience.Private
    public static final String FS_APPLICATION_HISTORY_STORE_URI = "yarn.timeline-service.generic-application-history.fs-history-store.uri";
    @InterfaceAudience.Private
    public static final String FS_APPLICATION_HISTORY_STORE_COMPRESSION_TYPE = "yarn.timeline-service.generic-application-history.fs-history-store.compression-type";
    @InterfaceAudience.Private
    public static final String DEFAULT_FS_APPLICATION_HISTORY_STORE_COMPRESSION_TYPE = "none";
    public static final String TIMELINE_SERVICE_ENABLED = "yarn.timeline-service.enabled";
    public static final boolean DEFAULT_TIMELINE_SERVICE_ENABLED = false;
    public static final String TIMELINE_SERVICE_ADDRESS = "yarn.timeline-service.address";
    public static final int DEFAULT_TIMELINE_SERVICE_PORT = 10200;
    public static final String DEFAULT_TIMELINE_SERVICE_ADDRESS = "0.0.0.0:10200";
    public static final String TIMELINE_SERVICE_BIND_HOST = "yarn.timeline-service.bind-host";
    public static final String TIMELINE_SERVICE_HANDLER_THREAD_COUNT = "yarn.timeline-service.handler-thread-count";
    public static final int DEFAULT_TIMELINE_SERVICE_CLIENT_THREAD_COUNT = 10;
    public static final String TIMELINE_SERVICE_WEBAPP_ADDRESS = "yarn.timeline-service.webapp.address";
    public static final int DEFAULT_TIMELINE_SERVICE_WEBAPP_PORT = 8188;
    public static final String DEFAULT_TIMELINE_SERVICE_WEBAPP_ADDRESS = "0.0.0.0:8188";
    public static final String TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS = "yarn.timeline-service.webapp.https.address";
    public static final int DEFAULT_TIMELINE_SERVICE_WEBAPP_HTTPS_PORT = 8190;
    public static final String DEFAULT_TIMELINE_SERVICE_WEBAPP_HTTPS_ADDRESS = "0.0.0.0:8190";
    public static final String TIMELINE_SERVICE_STORE = "yarn.timeline-service.store-class";
    public static final String TIMELINE_SERVICE_TTL_ENABLE = "yarn.timeline-service.ttl-enable";
    public static final String TIMELINE_SERVICE_TTL_MS = "yarn.timeline-service.ttl-ms";
    public static final long DEFAULT_TIMELINE_SERVICE_TTL_MS = 604800000L;
    public static final String TIMELINE_SERVICE_LEVELDB_PREFIX = "yarn.timeline-service.leveldb-timeline-store.";
    public static final String TIMELINE_SERVICE_LEVELDB_PATH = "yarn.timeline-service.leveldb-timeline-store.path";
    public static final String TIMELINE_SERVICE_LEVELDB_READ_CACHE_SIZE = "yarn.timeline-service.leveldb-timeline-store.read-cache-size";
    public static final long DEFAULT_TIMELINE_SERVICE_LEVELDB_READ_CACHE_SIZE = 104857600L;
    public static final String TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE = "yarn.timeline-service.leveldb-timeline-store.start-time-read-cache-size";
    public static final int DEFAULT_TIMELINE_SERVICE_LEVELDB_START_TIME_READ_CACHE_SIZE = 10000;
    public static final String TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE = "yarn.timeline-service.leveldb-timeline-store.start-time-write-cache-size";
    public static final int DEFAULT_TIMELINE_SERVICE_LEVELDB_START_TIME_WRITE_CACHE_SIZE = 10000;
    public static final String TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS = "yarn.timeline-service.leveldb-timeline-store.ttl-interval-ms";
    public static final long DEFAULT_TIMELINE_SERVICE_LEVELDB_TTL_INTERVAL_MS = 300000L;
    public static final String TIMELINE_SERVICE_PRINCIPAL = "yarn.timeline-service.principal";
    public static final String TIMELINE_SERVICE_KEYTAB = "yarn.timeline-service.keytab";
    public static final String TIMELINE_SERVICE_HTTP_CROSS_ORIGIN_ENABLED = "yarn.timeline-service.http-cross-origin.enabled";
    public static final boolean TIMELINE_SERVICE_HTTP_CROSS_ORIGIN_ENABLED_DEFAULT = false;
    public static final String TIMELINE_SERVICE_CLIENT_PREFIX = "yarn.timeline-service.client.";
    public static final String TIMELINE_SERVICE_CLIENT_MAX_RETRIES = "yarn.timeline-service.client.max-retries";
    public static final int DEFAULT_TIMELINE_SERVICE_CLIENT_MAX_RETRIES = 30;
    public static final String TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS = "yarn.timeline-service.client.retry-interval-ms";
    public static final long DEFAULT_TIMELINE_SERVICE_CLIENT_RETRY_INTERVAL_MS = 1000L;
    @Deprecated
    public static final String YARN_CLIENT_APP_SUBMISSION_POLL_INTERVAL_MS = "yarn.client.app-submission.poll-interval";
    public static final String YARN_CLIENT_APPLICATION_CLIENT_PROTOCOL_POLL_INTERVAL_MS = "yarn.client.application-client-protocol.poll-interval-ms";
    public static final long DEFAULT_YARN_CLIENT_APPLICATION_CLIENT_PROTOCOL_POLL_INTERVAL_MS = 200L;
    public static final String YARN_CLIENT_APPLICATION_CLIENT_PROTOCOL_POLL_TIMEOUT_MS = "yarn.client.application-client-protocol.poll-timeout-ms";
    public static final long DEFAULT_YARN_CLIENT_APPLICATION_CLIENT_PROTOCOL_POLL_TIMEOUT_MS = -1L;
    public static final String NM_CLIENT_ASYNC_THREAD_POOL_MAX_SIZE = "yarn.client.nodemanager-client-async.thread-pool-max-size";
    public static final int DEFAULT_NM_CLIENT_ASYNC_THREAD_POOL_MAX_SIZE = 500;
    public static final String NM_CLIENT_MAX_NM_PROXIES = "yarn.client.max-cached-nodemanagers-proxies";
    public static final int DEFAULT_NM_CLIENT_MAX_NM_PROXIES = 0;
    public static final String CLIENT_NM_CONNECT_MAX_WAIT_MS = "yarn.client.nodemanager-connect.max-wait-ms";
    public static final long DEFAULT_CLIENT_NM_CONNECT_MAX_WAIT_MS = 900000L;
    public static final String CLIENT_NM_CONNECT_RETRY_INTERVAL_MS = "yarn.client.nodemanager-connect.retry-interval-ms";
    public static final long DEFAULT_CLIENT_NM_CONNECT_RETRY_INTERVAL_MS = 10000L;
    public static final String YARN_HTTP_POLICY_KEY = "yarn.http.policy";
    public static final String YARN_HTTP_POLICY_DEFAULT;
    public static final String NODE_LABELS_PREFIX = "yarn.node-labels.";
    public static final String RM_NODE_LABELS_MANAGER_CLASS = "yarn.node-labels.manager-class";
    public static final String FS_NODE_LABELS_STORE_ROOT_DIR = "yarn.node-labels.fs-store.root-dir";
    public static final String FS_NODE_LABELS_STORE_RETRY_POLICY_SPEC = "yarn.node-labels.fs-store.retry-policy-spec";
    public static final String DEFAULT_FS_NODE_LABELS_STORE_RETRY_POLICY_SPEC = "2000, 500";
    
    private static void addDeprecatedKeys() {
        Configuration.addDeprecations(new DeprecationDelta[] { new DeprecationDelta("yarn.client.max-nodemanagers-proxies", "yarn.client.max-cached-nodemanagers-proxies") });
    }
    
    public YarnConfiguration() {
    }
    
    public YarnConfiguration(final Configuration conf) {
        super(conf);
        if (!(conf instanceof YarnConfiguration)) {
            this.reloadConfiguration();
        }
    }
    
    @InterfaceAudience.Private
    public static List<String> getServiceAddressConfKeys(final Configuration conf) {
        return useHttps(conf) ? YarnConfiguration.RM_SERVICES_ADDRESS_CONF_KEYS_HTTPS : YarnConfiguration.RM_SERVICES_ADDRESS_CONF_KEYS_HTTP;
    }
    
    @Override
    public InetSocketAddress getSocketAddr(final String name, final String defaultAddress, final int defaultPort) {
        String address;
        if (HAUtil.isHAEnabled(this) && getServiceAddressConfKeys(this).contains(name)) {
            address = HAUtil.getConfValueForRMInstance(name, defaultAddress, this);
        }
        else {
            address = this.get(name, defaultAddress);
        }
        return NetUtils.createSocketAddr(address, defaultPort, name);
    }
    
    @Override
    public InetSocketAddress updateConnectAddr(final String name, final InetSocketAddress addr) {
        String prefix = name;
        if (HAUtil.isHAEnabled(this)) {
            prefix = HAUtil.addSuffix(prefix, HAUtil.getRMHAId(this));
        }
        return super.updateConnectAddr(prefix, addr);
    }
    
    @InterfaceAudience.Private
    public static int getRMDefaultPortNumber(final String addressPrefix, final Configuration conf) {
        if (addressPrefix.equals("yarn.resourcemanager.address")) {
            return 8032;
        }
        if (addressPrefix.equals("yarn.resourcemanager.scheduler.address")) {
            return 8030;
        }
        if (addressPrefix.equals("yarn.resourcemanager.webapp.address")) {
            return 8088;
        }
        if (addressPrefix.equals("yarn.resourcemanager.webapp.https.address")) {
            return 8090;
        }
        if (addressPrefix.equals("yarn.resourcemanager.resource-tracker.address")) {
            return 8031;
        }
        if (addressPrefix.equals("yarn.resourcemanager.admin.address")) {
            return 8033;
        }
        throw new HadoopIllegalArgumentException("Invalid RM RPC address Prefix: " + addressPrefix + ". The valid value should be one of " + getServiceAddressConfKeys(conf));
    }
    
    public static boolean useHttps(final Configuration conf) {
        return HttpConfig.Policy.HTTPS_ONLY == HttpConfig.Policy.fromString(conf.get("yarn.http.policy", YarnConfiguration.YARN_HTTP_POLICY_DEFAULT));
    }
    
    @InterfaceAudience.Private
    public static String getClusterId(final Configuration conf) {
        final String clusterId = conf.get("yarn.resourcemanager.cluster-id");
        if (clusterId == null) {
            throw new HadoopIllegalArgumentException("Configuration doesn't specify yarn.resourcemanager.cluster-id");
        }
        return clusterId;
    }
    
    static {
        RM_CONFIGURATION_FILES = Collections.unmodifiableList((List<? extends String>)Arrays.asList("capacity-scheduler.xml", "hadoop-policy.xml", "yarn-site.xml", "core-site.xml"));
        addDeprecatedKeys();
        Configuration.addDefaultResource("yarn-default.xml");
        Configuration.addDefaultResource("yarn-site.xml");
        RM_SERVICES_ADDRESS_CONF_KEYS_HTTP = Collections.unmodifiableList((List<? extends String>)Arrays.asList("yarn.resourcemanager.address", "yarn.resourcemanager.scheduler.address", "yarn.resourcemanager.admin.address", "yarn.resourcemanager.resource-tracker.address", "yarn.resourcemanager.webapp.address"));
        RM_SERVICES_ADDRESS_CONF_KEYS_HTTPS = Collections.unmodifiableList((List<? extends String>)Arrays.asList("yarn.resourcemanager.address", "yarn.resourcemanager.scheduler.address", "yarn.resourcemanager.admin.address", "yarn.resourcemanager.resource-tracker.address", "yarn.resourcemanager.webapp.https.address"));
        DEFAULT_NM_ENV_WHITELIST = StringUtils.join(",", Arrays.asList(ApplicationConstants.Environment.JAVA_HOME.key(), ApplicationConstants.Environment.HADOOP_COMMON_HOME.key(), ApplicationConstants.Environment.HADOOP_HDFS_HOME.key(), ApplicationConstants.Environment.HADOOP_CONF_DIR.key(), ApplicationConstants.Environment.HADOOP_YARN_HOME.key()));
        YarnConfiguration.DEFAULT_RM_PROXY_USER_PRIVILEGES_ENABLED = false;
        DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH = new String[] { ApplicationConstants.Environment.HADOOP_CONF_DIR.$$(), ApplicationConstants.Environment.HADOOP_COMMON_HOME.$$() + "/share/hadoop/common/*", ApplicationConstants.Environment.HADOOP_COMMON_HOME.$$() + "/share/hadoop/common/lib/*", ApplicationConstants.Environment.HADOOP_HDFS_HOME.$$() + "/share/hadoop/hdfs/*", ApplicationConstants.Environment.HADOOP_HDFS_HOME.$$() + "/share/hadoop/hdfs/lib/*", ApplicationConstants.Environment.HADOOP_YARN_HOME.$$() + "/share/hadoop/yarn/*", ApplicationConstants.Environment.HADOOP_YARN_HOME.$$() + "/share/hadoop/yarn/lib/*" };
        DEFAULT_YARN_APPLICATION_CLASSPATH = new String[] { ApplicationConstants.Environment.HADOOP_CONF_DIR.$(), ApplicationConstants.Environment.HADOOP_COMMON_HOME.$() + "/share/hadoop/common/*", ApplicationConstants.Environment.HADOOP_COMMON_HOME.$() + "/share/hadoop/common/lib/*", ApplicationConstants.Environment.HADOOP_HDFS_HOME.$() + "/share/hadoop/hdfs/*", ApplicationConstants.Environment.HADOOP_HDFS_HOME.$() + "/share/hadoop/hdfs/lib/*", ApplicationConstants.Environment.HADOOP_YARN_HOME.$() + "/share/hadoop/yarn/*", ApplicationConstants.Environment.HADOOP_YARN_HOME.$() + "/share/hadoop/yarn/lib/*" };
        YARN_HTTP_POLICY_DEFAULT = HttpConfig.Policy.HTTP_ONLY.name();
    }
}
