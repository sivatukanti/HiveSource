// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerPreemptEvent;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent;
import org.apache.hadoop.yarn.event.Event;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.hadoop.service.AbstractService;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingMonitor;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerPreemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingEditPolicy;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.PreemptableResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.amlauncher.AMLauncherEventType;
import org.apache.hadoop.metrics2.source.JvmMetrics;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.NullRMStateStore;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.ContainerAllocationExpirer;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStoreFactory;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.YarnUncaughtExceptionHandler;
import org.apache.hadoop.classification.InterfaceAudience;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.SecurityUtil;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.ha.HAServiceProtocol;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.QueueMetrics;
import org.apache.hadoop.yarn.server.resourcemanager.webapp.RMWebApp;
import javax.servlet.http.HttpServlet;
import org.apache.hadoop.yarn.server.webproxy.WebAppProxyServlet;
import org.apache.hadoop.yarn.api.ApplicationClientProtocol;
import org.apache.hadoop.yarn.webapp.WebApps;
import org.apache.hadoop.http.lib.StaticUserWebFilter;
import org.apache.hadoop.security.token.delegation.AbstractDelegationTokenSecretManager;
import org.apache.hadoop.yarn.server.security.http.RMAuthenticationFilter;
import org.apache.hadoop.yarn.server.resourcemanager.security.RMAuthenticationHandler;
import org.apache.hadoop.util.StringUtils;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.security.http.RMAuthenticationFilterInitializer;
import org.apache.hadoop.security.AuthenticationFilterInitializer;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher;
import org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.security.DelegationTokenRenewer;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.MemoryRMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.nodelabels.RMNodeLabelsManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.AMLivelinessMonitor;
import org.apache.hadoop.yarn.server.resourcemanager.amlauncher.ApplicationMasterLauncher;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.AbstractReservationSystem;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.RMStateStore;
import java.io.InputStream;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import org.apache.hadoop.service.Service;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.conf.HAUtil;
import org.apache.hadoop.security.authorize.ProxyUsers;
import org.apache.hadoop.security.Groups;
import org.apache.hadoop.yarn.conf.ConfigurationProviderFactory;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.ConfigurationProvider;
import org.apache.hadoop.yarn.server.webproxy.AppReportFetcher;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.server.resourcemanager.security.QueueACLsManager;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystem;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.event.Dispatcher;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.recovery.Recoverable;
import org.apache.hadoop.service.CompositeService;

public class ResourceManager extends CompositeService implements Recoverable
{
    public static final int SHUTDOWN_HOOK_PRIORITY = 30;
    private static final Log LOG;
    private static long clusterTimeStamp;
    @VisibleForTesting
    protected RMContextImpl rmContext;
    private Dispatcher rmDispatcher;
    @VisibleForTesting
    protected AdminService adminService;
    protected RMActiveServices activeServices;
    protected RMSecretManagerService rmSecretManagerService;
    protected ResourceScheduler scheduler;
    protected ReservationSystem reservationSystem;
    private ClientRMService clientRM;
    protected ApplicationMasterService masterService;
    protected NMLivelinessMonitor nmLivelinessMonitor;
    protected NodesListManager nodesListManager;
    protected RMAppManager rmAppManager;
    protected ApplicationACLsManager applicationACLsManager;
    protected QueueACLsManager queueACLsManager;
    private WebApp webApp;
    private AppReportFetcher fetcher;
    protected ResourceTrackerService resourceTracker;
    @VisibleForTesting
    protected String webAppAddress;
    private ConfigurationProvider configurationProvider;
    private Configuration conf;
    private UserGroupInformation rmLoginUGI;
    
    public ResourceManager() {
        super("ResourceManager");
        this.fetcher = null;
        this.configurationProvider = null;
    }
    
    public RMContext getRMContext() {
        return this.rmContext;
    }
    
    public static long getClusterTimeStamp() {
        return ResourceManager.clusterTimeStamp;
    }
    
    @VisibleForTesting
    protected static void setClusterTimeStamp(final long timestamp) {
        ResourceManager.clusterTimeStamp = timestamp;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.conf = conf;
        this.rmContext = new RMContextImpl();
        (this.configurationProvider = ConfigurationProviderFactory.getConfigurationProvider(conf)).init(this.conf);
        this.rmContext.setConfigurationProvider(this.configurationProvider);
        final InputStream coreSiteXMLInputStream = this.configurationProvider.getConfigurationInputStream(this.conf, "core-site.xml");
        if (coreSiteXMLInputStream != null) {
            this.conf.addResource(coreSiteXMLInputStream);
        }
        Groups.getUserToGroupsMappingServiceWithLoadedConfiguration(this.conf).refresh();
        RMServerUtils.processRMProxyUsersConf(conf);
        ProxyUsers.refreshSuperUserGroupsConfiguration(this.conf);
        final InputStream yarnSiteXMLInputStream = this.configurationProvider.getConfigurationInputStream(this.conf, "yarn-site.xml");
        if (yarnSiteXMLInputStream != null) {
            this.conf.addResource(yarnSiteXMLInputStream);
        }
        validateConfigs(this.conf);
        this.rmContext.setHAEnabled(HAUtil.isHAEnabled(this.conf));
        if (this.rmContext.isHAEnabled()) {
            HAUtil.verifyAndSetConfiguration(this.conf);
        }
        this.rmLoginUGI = UserGroupInformation.getCurrentUser();
        try {
            this.doSecureLogin();
        }
        catch (IOException ie) {
            throw new YarnRuntimeException("Failed to login", ie);
        }
        this.addIfService(this.rmDispatcher = this.setupDispatcher());
        this.rmContext.setDispatcher(this.rmDispatcher);
        this.addService(this.adminService = this.createAdminService());
        this.rmContext.setRMAdminService(this.adminService);
        this.createAndInitActiveServices();
        this.webAppAddress = WebAppUtils.getWebAppBindURL(this.conf, "yarn.resourcemanager.bind-host", WebAppUtils.getRMWebAppURLWithoutScheme(this.conf));
        super.serviceInit(this.conf);
    }
    
    protected QueueACLsManager createQueueACLsManager(final ResourceScheduler scheduler, final Configuration conf) {
        return new QueueACLsManager(scheduler, conf);
    }
    
    @VisibleForTesting
    protected void setRMStateStore(final RMStateStore rmStore) {
        rmStore.setRMDispatcher(this.rmDispatcher);
        rmStore.setResourceManager(this);
        this.rmContext.setStateStore(rmStore);
    }
    
    protected EventHandler<SchedulerEvent> createSchedulerEventDispatcher() {
        return new SchedulerEventDispatcher(this.scheduler);
    }
    
    protected Dispatcher createDispatcher() {
        return new AsyncDispatcher();
    }
    
    protected ResourceScheduler createScheduler() {
        final String schedulerClassName = this.conf.get("yarn.resourcemanager.scheduler.class", "org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler");
        ResourceManager.LOG.info("Using Scheduler: " + schedulerClassName);
        try {
            final Class<?> schedulerClazz = Class.forName(schedulerClassName);
            if (ResourceScheduler.class.isAssignableFrom(schedulerClazz)) {
                return ReflectionUtils.newInstance(schedulerClazz, this.conf);
            }
            throw new YarnRuntimeException("Class: " + schedulerClassName + " not instance of " + ResourceScheduler.class.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException("Could not instantiate Scheduler: " + schedulerClassName, e);
        }
    }
    
    protected ReservationSystem createReservationSystem() {
        final String reservationClassName = this.conf.get("yarn.resourcemanager.reservation-system.class", AbstractReservationSystem.getDefaultReservationSystem(this.scheduler));
        if (reservationClassName == null) {
            return null;
        }
        ResourceManager.LOG.info("Using ReservationSystem: " + reservationClassName);
        try {
            final Class<?> reservationClazz = Class.forName(reservationClassName);
            if (ReservationSystem.class.isAssignableFrom(reservationClazz)) {
                return ReflectionUtils.newInstance(reservationClazz, this.conf);
            }
            throw new YarnRuntimeException("Class: " + reservationClassName + " not instance of " + ReservationSystem.class.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException("Could not instantiate ReservationSystem: " + reservationClassName, e);
        }
    }
    
    protected ApplicationMasterLauncher createAMLauncher() {
        return new ApplicationMasterLauncher(this.rmContext);
    }
    
    private NMLivelinessMonitor createNMLivelinessMonitor() {
        return new NMLivelinessMonitor(this.rmContext.getDispatcher());
    }
    
    protected AMLivelinessMonitor createAMLivelinessMonitor() {
        return new AMLivelinessMonitor(this.rmDispatcher);
    }
    
    protected RMNodeLabelsManager createNodeLabelManager() throws InstantiationException, IllegalAccessException {
        final Class<? extends RMNodeLabelsManager> nlmCls = this.conf.getClass("yarn.node-labels.manager-class", MemoryRMNodeLabelsManager.class, RMNodeLabelsManager.class);
        return (RMNodeLabelsManager)nlmCls.newInstance();
    }
    
    protected DelegationTokenRenewer createDelegationTokenRenewer() {
        return new DelegationTokenRenewer();
    }
    
    protected RMAppManager createRMAppManager() {
        return new RMAppManager(this.rmContext, this.scheduler, this.masterService, this.applicationACLsManager, this.conf);
    }
    
    protected RMApplicationHistoryWriter createRMApplicationHistoryWriter() {
        return new RMApplicationHistoryWriter();
    }
    
    protected SystemMetricsPublisher createSystemMetricsPublisher() {
        return new SystemMetricsPublisher();
    }
    
    protected static void validateConfigs(final Configuration conf) {
        final int globalMaxAppAttempts = conf.getInt("yarn.resourcemanager.am.max-attempts", 2);
        if (globalMaxAppAttempts <= 0) {
            throw new YarnRuntimeException("Invalid global max attempts configuration, yarn.resourcemanager.am.max-attempts=" + globalMaxAppAttempts + ", it should be a positive integer.");
        }
        final long expireIntvl = conf.getLong("yarn.nm.liveness-monitor.expiry-interval-ms", 600000L);
        final long heartbeatIntvl = conf.getLong("yarn.resourcemanager.nodemanagers.heartbeat-interval-ms", 1000L);
        if (expireIntvl < heartbeatIntvl) {
            throw new YarnRuntimeException("Nodemanager expiry interval should be no less than heartbeat interval, yarn.nm.liveness-monitor.expiry-interval-ms=" + expireIntvl + ", " + "yarn.resourcemanager.nodemanagers.heartbeat-interval-ms" + "=" + heartbeatIntvl);
        }
    }
    
    public void handleTransitionToStandBy() {
        if (this.rmContext.isHAEnabled()) {
            try {
                ResourceManager.LOG.info("Transitioning RM to Standby mode");
                this.transitionToStandby(true);
                this.adminService.resetLeaderElection();
            }
            catch (Exception e) {
                ResourceManager.LOG.fatal("Failed to transition RM to Standby mode.");
                ExitUtil.terminate(1, e);
            }
        }
    }
    
    protected void startWepApp() {
        final Configuration conf = this.getConfig();
        final boolean useYarnAuthenticationFilter = conf.getBoolean("yarn.resourcemanager.webapp.delegation-token-auth-filter.enabled", true);
        final String authPrefix = "hadoop.http.authentication.";
        final String authTypeKey = authPrefix + "type";
        final String filterInitializerConfKey = "hadoop.http.filter.initializers";
        String actualInitializers = "";
        final Class<?>[] initializersClasses = conf.getClasses(filterInitializerConfKey, (Class<?>[])new Class[0]);
        boolean hasHadoopAuthFilterInitializer = false;
        boolean hasRMAuthFilterInitializer = false;
        if (initializersClasses != null) {
            for (final Class<?> initializer : initializersClasses) {
                if (initializer.getName().equals(AuthenticationFilterInitializer.class.getName())) {
                    hasHadoopAuthFilterInitializer = true;
                }
                if (initializer.getName().equals(RMAuthenticationFilterInitializer.class.getName())) {
                    hasRMAuthFilterInitializer = true;
                }
            }
            if (UserGroupInformation.isSecurityEnabled() && useYarnAuthenticationFilter && hasHadoopAuthFilterInitializer && conf.get(authTypeKey, "").equals("kerberos")) {
                final ArrayList<String> target = new ArrayList<String>();
                for (final Class<?> filterInitializer : initializersClasses) {
                    if (filterInitializer.getName().equals(AuthenticationFilterInitializer.class.getName())) {
                        if (!hasRMAuthFilterInitializer) {
                            target.add(RMAuthenticationFilterInitializer.class.getName());
                        }
                    }
                    else {
                        target.add(filterInitializer.getName());
                    }
                }
                actualInitializers = StringUtils.join(",", target);
                ResourceManager.LOG.info("Using RM authentication filter(kerberos/delegation-token) for RM webapp authentication");
                RMAuthenticationHandler.setSecretManager(this.getClientRMService().rmDTSecretManager);
                RMAuthenticationFilter.setDelegationTokenSecretManager(this.getClientRMService().rmDTSecretManager);
                final String yarnAuthKey = authPrefix + "yarn.resourcemanager.authentication-handler";
                conf.setStrings(yarnAuthKey, RMAuthenticationHandler.class.getName());
                conf.set(filterInitializerConfKey, actualInitializers);
            }
        }
        final String initializers = conf.get(filterInitializerConfKey);
        if (!UserGroupInformation.isSecurityEnabled()) {
            if (initializersClasses == null || initializersClasses.length == 0) {
                conf.set(filterInitializerConfKey, RMAuthenticationFilterInitializer.class.getName());
                conf.set(authTypeKey, "simple");
            }
            else if (initializers.equals(StaticUserWebFilter.class.getName())) {
                conf.set(filterInitializerConfKey, RMAuthenticationFilterInitializer.class.getName() + "," + initializers);
                conf.set(authTypeKey, "simple");
            }
        }
        final WebApps.Builder<ApplicationMasterService> builder = WebApps.$for("cluster", ApplicationMasterService.class, this.masterService, "ws").with(conf).withHttpSpnegoPrincipalKey("yarn.resourcemanager.webapp.spnego-principal").withHttpSpnegoKeytabKey("yarn.resourcemanager.webapp.spnego-keytab-file").at(this.webAppAddress);
        final String proxyHostAndPort = WebAppUtils.getProxyHostAndPort(conf);
        if (WebAppUtils.getResolvedRMWebAppURLWithoutScheme(conf).equals(proxyHostAndPort)) {
            if (HAUtil.isHAEnabled(conf)) {
                this.fetcher = new AppReportFetcher(conf);
            }
            else {
                this.fetcher = new AppReportFetcher(conf, this.getClientRMService());
            }
            builder.withServlet("proxy", "/proxy/*", WebAppProxyServlet.class);
            builder.withAttribute("AppUrlFetcher", this.fetcher);
            final String[] proxyParts = proxyHostAndPort.split(":");
            builder.withAttribute("proxyHost", proxyParts[0]);
        }
        this.webApp = builder.start(new RMWebApp(this));
    }
    
    protected void createAndInitActiveServices() throws Exception {
        (this.activeServices = new RMActiveServices(this)).init(this.conf);
    }
    
    void startActiveServices() throws Exception {
        if (this.activeServices != null) {
            ResourceManager.clusterTimeStamp = System.currentTimeMillis();
            this.activeServices.start();
        }
    }
    
    void stopActiveServices() throws Exception {
        if (this.activeServices != null) {
            this.activeServices.stop();
            this.activeServices = null;
            this.rmContext.getRMNodes().clear();
            this.rmContext.getInactiveRMNodes().clear();
            this.rmContext.getRMApps().clear();
            ClusterMetrics.destroy();
            QueueMetrics.clearQueueMetrics();
        }
    }
    
    @VisibleForTesting
    protected boolean areActiveServicesRunning() {
        return this.activeServices != null && this.activeServices.isInState(Service.STATE.STARTED);
    }
    
    synchronized void transitionToActive() throws Exception {
        if (this.rmContext.getHAServiceState() == HAServiceProtocol.HAServiceState.ACTIVE) {
            ResourceManager.LOG.info("Already in active state");
            return;
        }
        ResourceManager.LOG.info("Transitioning to active state");
        this.rmLoginUGI.doAs((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try {
                    ResourceManager.this.startActiveServices();
                    return null;
                }
                catch (Exception e) {
                    ResourceManager.this.resetDispatcher();
                    ResourceManager.this.createAndInitActiveServices();
                    throw e;
                }
            }
        });
        this.rmContext.setHAServiceState(HAServiceProtocol.HAServiceState.ACTIVE);
        ResourceManager.LOG.info("Transitioned to active state");
    }
    
    synchronized void transitionToStandby(final boolean initialize) throws Exception {
        if (this.rmContext.getHAServiceState() == HAServiceProtocol.HAServiceState.STANDBY) {
            ResourceManager.LOG.info("Already in standby state");
            return;
        }
        ResourceManager.LOG.info("Transitioning to standby state");
        if (this.rmContext.getHAServiceState() == HAServiceProtocol.HAServiceState.ACTIVE) {
            this.stopActiveServices();
            if (initialize) {
                this.resetDispatcher();
                this.createAndInitActiveServices();
            }
        }
        this.rmContext.setHAServiceState(HAServiceProtocol.HAServiceState.STANDBY);
        ResourceManager.LOG.info("Transitioned to standby state");
    }
    
    @Override
    protected void serviceStart() throws Exception {
        if (this.rmContext.isHAEnabled()) {
            this.transitionToStandby(true);
        }
        else {
            this.transitionToActive();
        }
        this.startWepApp();
        if (this.getConfig().getBoolean("yarn.is.minicluster", false)) {
            final int port = this.webApp.port();
            WebAppUtils.setRMWebAppPort(this.conf, port);
        }
        super.serviceStart();
    }
    
    protected void doSecureLogin() throws IOException {
        final InetSocketAddress socAddr = getBindAddress(this.conf);
        SecurityUtil.login(this.conf, "yarn.resourcemanager.keytab", "yarn.resourcemanager.principal", socAddr.getHostName());
        if (UserGroupInformation.isSecurityEnabled()) {
            this.rmLoginUGI = UserGroupInformation.getLoginUser();
        }
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.webApp != null) {
            this.webApp.stop();
        }
        if (this.fetcher != null) {
            this.fetcher.stop();
        }
        if (this.configurationProvider != null) {
            this.configurationProvider.close();
        }
        super.serviceStop();
        this.transitionToStandby(false);
        this.rmContext.setHAServiceState(HAServiceProtocol.HAServiceState.STOPPING);
    }
    
    protected ResourceTrackerService createResourceTrackerService() {
        return new ResourceTrackerService(this.rmContext, this.nodesListManager, this.nmLivelinessMonitor, this.rmContext.getContainerTokenSecretManager(), this.rmContext.getNMTokenSecretManager());
    }
    
    protected ClientRMService createClientRMService() {
        return new ClientRMService(this.rmContext, this.scheduler, this.rmAppManager, this.applicationACLsManager, this.queueACLsManager, this.rmContext.getRMDelegationTokenSecretManager());
    }
    
    protected ApplicationMasterService createApplicationMasterService() {
        return new ApplicationMasterService(this.rmContext, this.scheduler);
    }
    
    protected AdminService createAdminService() {
        return new AdminService(this, this.rmContext);
    }
    
    protected RMSecretManagerService createRMSecretManagerService() {
        return new RMSecretManagerService(this.conf, this.rmContext);
    }
    
    @InterfaceAudience.Private
    public ClientRMService getClientRMService() {
        return this.clientRM;
    }
    
    @InterfaceAudience.Private
    public ResourceScheduler getResourceScheduler() {
        return this.scheduler;
    }
    
    @InterfaceAudience.Private
    public ResourceTrackerService getResourceTrackerService() {
        return this.resourceTracker;
    }
    
    @InterfaceAudience.Private
    public ApplicationMasterService getApplicationMasterService() {
        return this.masterService;
    }
    
    @InterfaceAudience.Private
    public ApplicationACLsManager getApplicationACLsManager() {
        return this.applicationACLsManager;
    }
    
    @InterfaceAudience.Private
    public QueueACLsManager getQueueACLsManager() {
        return this.queueACLsManager;
    }
    
    @InterfaceAudience.Private
    WebApp getWebapp() {
        return this.webApp;
    }
    
    @Override
    public void recover(final RMStateStore.RMState state) throws Exception {
        this.rmContext.getRMDelegationTokenSecretManager().recover(state);
        this.rmContext.getAMRMTokenSecretManager().recover(state);
        this.rmAppManager.recover(state);
        this.setSchedulerRecoveryStartAndWaitTime(state, this.conf);
    }
    
    public static void main(final String[] argv) {
        Thread.setDefaultUncaughtExceptionHandler(new YarnUncaughtExceptionHandler());
        StringUtils.startupShutdownMessage(ResourceManager.class, argv, ResourceManager.LOG);
        try {
            final Configuration conf = new YarnConfiguration();
            if (argv.length == 1 && argv[0].equals("-format-state-store")) {
                deleteRMStateStore(conf);
            }
            else {
                final ResourceManager resourceManager = new ResourceManager();
                ShutdownHookManager.get().addShutdownHook(new CompositeServiceShutdownHook(resourceManager), 30);
                resourceManager.init(conf);
                resourceManager.start();
            }
        }
        catch (Throwable t) {
            ResourceManager.LOG.fatal("Error starting ResourceManager", t);
            System.exit(-1);
        }
    }
    
    private Dispatcher setupDispatcher() {
        final Dispatcher dispatcher = this.createDispatcher();
        dispatcher.register(RMFatalEventType.class, new RMFatalEventDispatcher());
        return dispatcher;
    }
    
    private void resetDispatcher() {
        final Dispatcher dispatcher = this.setupDispatcher();
        ((Service)dispatcher).init(this.conf);
        ((Service)dispatcher).start();
        this.removeService((Service)this.rmDispatcher);
        ((Service)this.rmDispatcher).stop();
        this.addIfService(this.rmDispatcher = dispatcher);
        this.rmContext.setDispatcher(this.rmDispatcher);
    }
    
    private void setSchedulerRecoveryStartAndWaitTime(final RMStateStore.RMState state, final Configuration conf) {
        if (!state.getApplicationState().isEmpty()) {
            final long waitTime = conf.getLong("yarn.resourcemanager.work-preserving-recovery.scheduling-wait-ms", 10000L);
            this.rmContext.setSchedulerRecoveryStartAndWaitTime(waitTime);
        }
    }
    
    public static InetSocketAddress getBindAddress(final Configuration conf) {
        return conf.getSocketAddr("yarn.resourcemanager.address", "0.0.0.0:8032", 8032);
    }
    
    private static void deleteRMStateStore(final Configuration conf) throws Exception {
        final RMStateStore rmStore = RMStateStoreFactory.getStore(conf);
        rmStore.init(conf);
        rmStore.start();
        try {
            ResourceManager.LOG.info("Deleting ResourceManager state store...");
            rmStore.deleteStore();
            ResourceManager.LOG.info("State store deleted");
        }
        finally {
            rmStore.stop();
        }
    }
    
    static {
        LOG = LogFactory.getLog(ResourceManager.class);
        ResourceManager.clusterTimeStamp = System.currentTimeMillis();
    }
    
    @InterfaceAudience.Private
    public class RMActiveServices extends CompositeService
    {
        private DelegationTokenRenewer delegationTokenRenewer;
        private EventHandler<SchedulerEvent> schedulerDispatcher;
        private ApplicationMasterLauncher applicationMasterLauncher;
        private ContainerAllocationExpirer containerAllocationExpirer;
        private ResourceManager rm;
        private boolean recoveryEnabled;
        
        RMActiveServices(final ResourceManager rm) {
            super("RMActiveServices");
            this.rm = rm;
        }
        
        @Override
        protected void serviceInit(final Configuration configuration) throws Exception {
            ResourceManager.this.conf.setBoolean("yarn.dispatcher.exit-on-error", true);
            this.addService(ResourceManager.this.rmSecretManagerService = ResourceManager.this.createRMSecretManagerService());
            this.addService(this.containerAllocationExpirer = new ContainerAllocationExpirer(ResourceManager.this.rmDispatcher));
            ResourceManager.this.rmContext.setContainerAllocationExpirer(this.containerAllocationExpirer);
            final AMLivelinessMonitor amLivelinessMonitor = ResourceManager.this.createAMLivelinessMonitor();
            this.addService(amLivelinessMonitor);
            ResourceManager.this.rmContext.setAMLivelinessMonitor(amLivelinessMonitor);
            final AMLivelinessMonitor amFinishingMonitor = ResourceManager.this.createAMLivelinessMonitor();
            this.addService(amFinishingMonitor);
            ResourceManager.this.rmContext.setAMFinishingMonitor(amFinishingMonitor);
            final RMNodeLabelsManager nlm = ResourceManager.this.createNodeLabelManager();
            this.addService(nlm);
            ResourceManager.this.rmContext.setNodeLabelManager(nlm);
            final boolean isRecoveryEnabled = ResourceManager.this.conf.getBoolean("yarn.resourcemanager.recovery.enabled", false);
            RMStateStore rmStore = null;
            if (isRecoveryEnabled) {
                this.recoveryEnabled = true;
                rmStore = RMStateStoreFactory.getStore(ResourceManager.this.conf);
                final boolean isWorkPreservingRecoveryEnabled = ResourceManager.this.conf.getBoolean("yarn.resourcemanager.work-preserving-recovery.enabled", false);
                ResourceManager.this.rmContext.setWorkPreservingRecoveryEnabled(isWorkPreservingRecoveryEnabled);
            }
            else {
                this.recoveryEnabled = false;
                rmStore = new NullRMStateStore();
            }
            try {
                rmStore.init(ResourceManager.this.conf);
                rmStore.setRMDispatcher(ResourceManager.this.rmDispatcher);
                rmStore.setResourceManager(this.rm);
            }
            catch (Exception e) {
                ResourceManager.LOG.error("Failed to init state store", e);
                throw e;
            }
            ResourceManager.this.rmContext.setStateStore(rmStore);
            if (UserGroupInformation.isSecurityEnabled()) {
                this.delegationTokenRenewer = ResourceManager.this.createDelegationTokenRenewer();
                ResourceManager.this.rmContext.setDelegationTokenRenewer(this.delegationTokenRenewer);
            }
            final RMApplicationHistoryWriter rmApplicationHistoryWriter = ResourceManager.this.createRMApplicationHistoryWriter();
            this.addService(rmApplicationHistoryWriter);
            ResourceManager.this.rmContext.setRMApplicationHistoryWriter(rmApplicationHistoryWriter);
            final SystemMetricsPublisher systemMetricsPublisher = ResourceManager.this.createSystemMetricsPublisher();
            this.addService(systemMetricsPublisher);
            ResourceManager.this.rmContext.setSystemMetricsPublisher(systemMetricsPublisher);
            ResourceManager.this.nodesListManager = new NodesListManager(ResourceManager.this.rmContext);
            ResourceManager.this.rmDispatcher.register(NodesListManagerEventType.class, ResourceManager.this.nodesListManager);
            this.addService(ResourceManager.this.nodesListManager);
            ResourceManager.this.rmContext.setNodesListManager(ResourceManager.this.nodesListManager);
            (ResourceManager.this.scheduler = ResourceManager.this.createScheduler()).setRMContext(ResourceManager.this.rmContext);
            this.addIfService(ResourceManager.this.scheduler);
            ResourceManager.this.rmContext.setScheduler(ResourceManager.this.scheduler);
            this.addIfService(this.schedulerDispatcher = ResourceManager.this.createSchedulerEventDispatcher());
            ResourceManager.this.rmDispatcher.register(SchedulerEventType.class, this.schedulerDispatcher);
            ResourceManager.this.rmDispatcher.register(RMAppEventType.class, new ApplicationEventDispatcher(ResourceManager.this.rmContext));
            ResourceManager.this.rmDispatcher.register(RMAppAttemptEventType.class, new ApplicationAttemptEventDispatcher(ResourceManager.this.rmContext));
            ResourceManager.this.rmDispatcher.register(RMNodeEventType.class, new NodeEventDispatcher(ResourceManager.this.rmContext));
            this.addService(ResourceManager.this.nmLivelinessMonitor = ResourceManager.this.createNMLivelinessMonitor());
            this.addService(ResourceManager.this.resourceTracker = ResourceManager.this.createResourceTrackerService());
            ResourceManager.this.rmContext.setResourceTrackerService(ResourceManager.this.resourceTracker);
            DefaultMetricsSystem.initialize("ResourceManager");
            JvmMetrics.initSingleton("ResourceManager", null);
            if (ResourceManager.this.conf.getBoolean("yarn.resourcemanager.reservation-system.enable", false)) {
                ResourceManager.this.reservationSystem = ResourceManager.this.createReservationSystem();
                if (ResourceManager.this.reservationSystem != null) {
                    ResourceManager.this.reservationSystem.setRMContext(ResourceManager.this.rmContext);
                    this.addIfService(ResourceManager.this.reservationSystem);
                    ResourceManager.this.rmContext.setReservationSystem(ResourceManager.this.reservationSystem);
                    ResourceManager.LOG.info("Initialized Reservation system");
                }
            }
            this.createPolicyMonitors();
            this.addService(ResourceManager.this.masterService = ResourceManager.this.createApplicationMasterService());
            ResourceManager.this.rmContext.setApplicationMasterService(ResourceManager.this.masterService);
            ResourceManager.this.applicationACLsManager = new ApplicationACLsManager(ResourceManager.this.conf);
            ResourceManager.this.queueACLsManager = ResourceManager.this.createQueueACLsManager(ResourceManager.this.scheduler, ResourceManager.this.conf);
            ResourceManager.this.rmAppManager = ResourceManager.this.createRMAppManager();
            ResourceManager.this.rmDispatcher.register(RMAppManagerEventType.class, ResourceManager.this.rmAppManager);
            ResourceManager.this.clientRM = ResourceManager.this.createClientRMService();
            this.addService(ResourceManager.this.clientRM);
            ResourceManager.this.rmContext.setClientRMService(ResourceManager.this.clientRM);
            this.applicationMasterLauncher = ResourceManager.this.createAMLauncher();
            ResourceManager.this.rmDispatcher.register(AMLauncherEventType.class, this.applicationMasterLauncher);
            this.addService(this.applicationMasterLauncher);
            if (UserGroupInformation.isSecurityEnabled()) {
                this.addService(this.delegationTokenRenewer);
                this.delegationTokenRenewer.setRMContext(ResourceManager.this.rmContext);
            }
            new RMNMInfo(ResourceManager.this.rmContext, ResourceManager.this.scheduler);
            super.serviceInit(ResourceManager.this.conf);
        }
        
        @Override
        protected void serviceStart() throws Exception {
            final RMStateStore rmStore = ResourceManager.this.rmContext.getStateStore();
            rmStore.start();
            if (this.recoveryEnabled) {
                try {
                    rmStore.checkVersion();
                    if (ResourceManager.this.rmContext.isWorkPreservingRecoveryEnabled()) {
                        ResourceManager.this.rmContext.setEpoch(rmStore.getAndIncrementEpoch());
                    }
                    final RMStateStore.RMState state = rmStore.loadState();
                    ResourceManager.this.recover(state);
                }
                catch (Exception e) {
                    ResourceManager.LOG.error("Failed to load/recover state", e);
                    throw e;
                }
            }
            super.serviceStart();
        }
        
        @Override
        protected void serviceStop() throws Exception {
            DefaultMetricsSystem.shutdown();
            if (ResourceManager.this.rmContext != null) {
                final RMStateStore store = ResourceManager.this.rmContext.getStateStore();
                try {
                    store.close();
                }
                catch (Exception e) {
                    ResourceManager.LOG.error("Error closing store.", e);
                }
            }
            super.serviceStop();
        }
        
        protected void createPolicyMonitors() {
            if (ResourceManager.this.scheduler instanceof PreemptableResourceScheduler && ResourceManager.this.conf.getBoolean("yarn.resourcemanager.scheduler.monitor.enable", false)) {
                ResourceManager.LOG.info("Loading policy monitors");
                final List<SchedulingEditPolicy> policies = ResourceManager.this.conf.getInstances("yarn.resourcemanager.scheduler.monitor.policies", SchedulingEditPolicy.class);
                if (policies.size() > 0) {
                    ResourceManager.this.rmDispatcher.register(ContainerPreemptEventType.class, new RMContainerPreemptEventDispatcher((PreemptableResourceScheduler)ResourceManager.this.scheduler));
                    for (final SchedulingEditPolicy policy : policies) {
                        ResourceManager.LOG.info("LOADING SchedulingEditPolicy:" + policy.getPolicyName());
                        final SchedulingMonitor mon = new SchedulingMonitor(ResourceManager.this.rmContext, policy);
                        this.addService(mon);
                    }
                }
                else {
                    ResourceManager.LOG.warn("Policy monitors configured (yarn.resourcemanager.scheduler.monitor.enable) but none specified (yarn.resourcemanager.scheduler.monitor.policies)");
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    public static class SchedulerEventDispatcher extends AbstractService implements EventHandler<SchedulerEvent>
    {
        private final ResourceScheduler scheduler;
        private final BlockingQueue<SchedulerEvent> eventQueue;
        private final Thread eventProcessor;
        private volatile boolean stopped;
        private boolean shouldExitOnError;
        
        public SchedulerEventDispatcher(final ResourceScheduler scheduler) {
            super(SchedulerEventDispatcher.class.getName());
            this.eventQueue = new LinkedBlockingQueue<SchedulerEvent>();
            this.stopped = false;
            this.shouldExitOnError = false;
            this.scheduler = scheduler;
            (this.eventProcessor = new Thread(new EventProcessor())).setName("ResourceManager Event Processor");
        }
        
        @Override
        protected void serviceInit(final Configuration conf) throws Exception {
            this.shouldExitOnError = conf.getBoolean("yarn.dispatcher.exit-on-error", false);
            super.serviceInit(conf);
        }
        
        @Override
        protected void serviceStart() throws Exception {
            this.eventProcessor.start();
            super.serviceStart();
        }
        
        @Override
        protected void serviceStop() throws Exception {
            this.stopped = true;
            this.eventProcessor.interrupt();
            try {
                this.eventProcessor.join();
            }
            catch (InterruptedException e) {
                throw new YarnRuntimeException(e);
            }
            super.serviceStop();
        }
        
        @Override
        public void handle(final SchedulerEvent event) {
            try {
                final int qSize = this.eventQueue.size();
                if (qSize != 0 && qSize % 1000 == 0) {
                    ResourceManager.LOG.info("Size of scheduler event-queue is " + qSize);
                }
                final int remCapacity = this.eventQueue.remainingCapacity();
                if (remCapacity < 1000) {
                    ResourceManager.LOG.info("Very low remaining capacity on scheduler event queue: " + remCapacity);
                }
                this.eventQueue.put(event);
            }
            catch (InterruptedException e) {
                ResourceManager.LOG.info("Interrupted. Trying to exit gracefully.");
            }
        }
        
        private final class EventProcessor implements Runnable
        {
            @Override
            public void run() {
                while (!SchedulerEventDispatcher.this.stopped && !Thread.currentThread().isInterrupted()) {
                    SchedulerEvent event;
                    try {
                        event = SchedulerEventDispatcher.this.eventQueue.take();
                    }
                    catch (InterruptedException e) {
                        ResourceManager.LOG.error("Returning, interrupted : " + e);
                        return;
                    }
                    try {
                        SchedulerEventDispatcher.this.scheduler.handle(event);
                    }
                    catch (Throwable t) {
                        if (SchedulerEventDispatcher.this.stopped) {
                            ResourceManager.LOG.warn("Exception during shutdown: ", t);
                            break;
                        }
                        ResourceManager.LOG.fatal("Error in handling event type " + ((AbstractEvent<Object>)event).getType() + " to the scheduler", t);
                        if (!SchedulerEventDispatcher.this.shouldExitOnError || ShutdownHookManager.get().isShutdownInProgress()) {
                            continue;
                        }
                        ResourceManager.LOG.info("Exiting, bbye..");
                        System.exit(-1);
                    }
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    public static class RMFatalEventDispatcher implements EventHandler<RMFatalEvent>
    {
        @Override
        public void handle(final RMFatalEvent event) {
            ResourceManager.LOG.fatal("Received a " + RMFatalEvent.class.getName() + " of type " + event.getType().name() + ". Cause:\n" + event.getCause());
            ExitUtil.terminate(1, event.getCause());
        }
    }
    
    @InterfaceAudience.Private
    public static final class ApplicationEventDispatcher implements EventHandler<RMAppEvent>
    {
        private final RMContext rmContext;
        
        public ApplicationEventDispatcher(final RMContext rmContext) {
            this.rmContext = rmContext;
        }
        
        @Override
        public void handle(final RMAppEvent event) {
            final ApplicationId appID = event.getApplicationId();
            final RMApp rmApp = this.rmContext.getRMApps().get(appID);
            if (rmApp != null) {
                try {
                    rmApp.handle(event);
                }
                catch (Throwable t) {
                    ResourceManager.LOG.error("Error in handling event type " + ((AbstractEvent<Object>)event).getType() + " for application " + appID, t);
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    public static final class RMContainerPreemptEventDispatcher implements EventHandler<ContainerPreemptEvent>
    {
        private final PreemptableResourceScheduler scheduler;
        
        public RMContainerPreemptEventDispatcher(final PreemptableResourceScheduler scheduler) {
            this.scheduler = scheduler;
        }
        
        @Override
        public void handle(final ContainerPreemptEvent event) {
            final ApplicationAttemptId aid = event.getAppId();
            final RMContainer container = event.getContainer();
            switch (event.getType()) {
                case DROP_RESERVATION: {
                    this.scheduler.dropContainerReservation(container);
                    break;
                }
                case PREEMPT_CONTAINER: {
                    this.scheduler.preemptContainer(aid, container);
                    break;
                }
                case KILL_CONTAINER: {
                    this.scheduler.killContainer(container);
                    break;
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    public static final class ApplicationAttemptEventDispatcher implements EventHandler<RMAppAttemptEvent>
    {
        private final RMContext rmContext;
        
        public ApplicationAttemptEventDispatcher(final RMContext rmContext) {
            this.rmContext = rmContext;
        }
        
        @Override
        public void handle(final RMAppAttemptEvent event) {
            final ApplicationAttemptId appAttemptID = event.getApplicationAttemptId();
            final ApplicationId appAttemptId = appAttemptID.getApplicationId();
            final RMApp rmApp = this.rmContext.getRMApps().get(appAttemptId);
            if (rmApp != null) {
                final RMAppAttempt rmAppAttempt = rmApp.getRMAppAttempt(appAttemptID);
                if (rmAppAttempt != null) {
                    try {
                        rmAppAttempt.handle(event);
                    }
                    catch (Throwable t) {
                        ResourceManager.LOG.error("Error in handling event type " + ((AbstractEvent<Object>)event).getType() + " for applicationAttempt " + appAttemptId, t);
                    }
                }
            }
        }
    }
    
    @InterfaceAudience.Private
    public static final class NodeEventDispatcher implements EventHandler<RMNodeEvent>
    {
        private final RMContext rmContext;
        
        public NodeEventDispatcher(final RMContext rmContext) {
            this.rmContext = rmContext;
        }
        
        @Override
        public void handle(final RMNodeEvent event) {
            final NodeId nodeId = event.getNodeId();
            final RMNode node = this.rmContext.getRMNodes().get(nodeId);
            if (node != null) {
                try {
                    ((EventHandler)node).handle(event);
                }
                catch (Throwable t) {
                    ResourceManager.LOG.error("Error in handling event type " + ((AbstractEvent<Object>)event).getType() + " for node " + nodeId, t);
                }
            }
        }
    }
}
