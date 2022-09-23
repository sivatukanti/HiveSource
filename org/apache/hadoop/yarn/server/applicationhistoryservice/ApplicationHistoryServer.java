// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.commons.logging.LogFactory;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.yarn.server.applicationhistoryservice.webapp.AHSWebApp;
import org.apache.hadoop.yarn.webapp.WebApps;
import org.apache.hadoop.yarn.webapp.util.WebAppUtils;
import java.util.Collection;
import org.apache.hadoop.security.AuthenticationFilterInitializer;
import java.util.ArrayList;
import org.apache.hadoop.yarn.server.timeline.security.TimelineAuthenticationFilterInitializer;
import org.apache.hadoop.yarn.server.timeline.webapp.CrossOriginFilterInitializer;
import org.apache.hadoop.yarn.server.timeline.security.TimelineAuthenticationFilter;
import org.apache.hadoop.yarn.server.timeline.security.TimelineACLsManager;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.server.timeline.LeveldbTimelineStore;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.util.ShutdownHookManager;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.yarn.YarnUncaughtExceptionHandler;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.metrics2.source.JvmMetrics;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.webapp.WebApp;
import org.apache.hadoop.yarn.server.timeline.TimelineDataManager;
import org.apache.hadoop.yarn.server.timeline.security.TimelineDelegationTokenSecretManagerService;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.apache.hadoop.yarn.server.security.ApplicationACLsManager;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.CompositeService;

public class ApplicationHistoryServer extends CompositeService
{
    public static final int SHUTDOWN_HOOK_PRIORITY = 30;
    private static final Log LOG;
    private ApplicationHistoryClientService ahsClientService;
    private ApplicationACLsManager aclsManager;
    private ApplicationHistoryManager historyManager;
    private TimelineStore timelineStore;
    private TimelineDelegationTokenSecretManagerService secretManagerService;
    private TimelineDataManager timelineDataManager;
    private WebApp webApp;
    
    public ApplicationHistoryServer() {
        super(ApplicationHistoryServer.class.getName());
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.addIfService(this.timelineStore = this.createTimelineStore(conf));
        this.addService(this.secretManagerService = this.createTimelineDelegationTokenSecretManagerService(conf));
        this.addService(this.timelineDataManager = this.createTimelineDataManager(conf));
        this.aclsManager = this.createApplicationACLsManager(conf);
        this.historyManager = this.createApplicationHistoryManager(conf);
        this.addService(this.ahsClientService = this.createApplicationHistoryClientService(this.historyManager));
        this.addService((Service)this.historyManager);
        DefaultMetricsSystem.initialize("ApplicationHistoryServer");
        JvmMetrics.initSingleton("ApplicationHistoryServer", null);
        super.serviceInit(conf);
    }
    
    @Override
    protected void serviceStart() throws Exception {
        try {
            this.doSecureLogin(this.getConfig());
        }
        catch (IOException ie) {
            throw new YarnRuntimeException("Failed to login", ie);
        }
        this.startWebApp();
        super.serviceStart();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        if (this.webApp != null) {
            this.webApp.stop();
        }
        DefaultMetricsSystem.shutdown();
        super.serviceStop();
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    ApplicationHistoryClientService getClientService() {
        return this.ahsClientService;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    public TimelineStore getTimelineStore() {
        return this.timelineStore;
    }
    
    @InterfaceAudience.Private
    @VisibleForTesting
    ApplicationHistoryManager getApplicationHistoryManager() {
        return this.historyManager;
    }
    
    static ApplicationHistoryServer launchAppHistoryServer(final String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new YarnUncaughtExceptionHandler());
        StringUtils.startupShutdownMessage(ApplicationHistoryServer.class, args, ApplicationHistoryServer.LOG);
        ApplicationHistoryServer appHistoryServer = null;
        try {
            appHistoryServer = new ApplicationHistoryServer();
            ShutdownHookManager.get().addShutdownHook(new CompositeServiceShutdownHook(appHistoryServer), 30);
            final YarnConfiguration conf = new YarnConfiguration();
            appHistoryServer.init(conf);
            appHistoryServer.start();
        }
        catch (Throwable t) {
            ApplicationHistoryServer.LOG.fatal("Error starting ApplicationHistoryServer", t);
            ExitUtil.terminate(-1, "Error starting ApplicationHistoryServer");
        }
        return appHistoryServer;
    }
    
    public static void main(final String[] args) {
        launchAppHistoryServer(args);
    }
    
    private ApplicationHistoryClientService createApplicationHistoryClientService(final ApplicationHistoryManager historyManager) {
        return new ApplicationHistoryClientService(historyManager);
    }
    
    private ApplicationACLsManager createApplicationACLsManager(final Configuration conf) {
        return new ApplicationACLsManager(conf);
    }
    
    private ApplicationHistoryManager createApplicationHistoryManager(final Configuration conf) {
        if (conf.get("yarn.timeline-service.generic-application-history.store-class") == null || conf.get("yarn.timeline-service.generic-application-history.store-class").length() == 0 || conf.get("yarn.timeline-service.generic-application-history.store-class").equals(NullApplicationHistoryStore.class.getName())) {
            return new ApplicationHistoryManagerOnTimelineStore(this.timelineDataManager, this.aclsManager);
        }
        ApplicationHistoryServer.LOG.warn("The filesystem based application history store is deprecated.");
        return new ApplicationHistoryManagerImpl();
    }
    
    private TimelineStore createTimelineStore(final Configuration conf) {
        return ReflectionUtils.newInstance(conf.getClass("yarn.timeline-service.store-class", LeveldbTimelineStore.class, TimelineStore.class), conf);
    }
    
    private TimelineDelegationTokenSecretManagerService createTimelineDelegationTokenSecretManagerService(final Configuration conf) {
        return new TimelineDelegationTokenSecretManagerService();
    }
    
    private TimelineDataManager createTimelineDataManager(final Configuration conf) {
        return new TimelineDataManager(this.timelineStore, new TimelineACLsManager(conf));
    }
    
    private void startWebApp() {
        final Configuration conf = this.getConfig();
        TimelineAuthenticationFilter.setTimelineDelegationTokenSecretManager(this.secretManagerService.getTimelineDelegationTokenSecretManager());
        String initializers = conf.get("hadoop.http.filter.initializers");
        boolean modifiedInitializers = false;
        initializers = ((initializers == null || initializers.length() == 0) ? "" : initializers);
        if (!initializers.contains(CrossOriginFilterInitializer.class.getName()) && conf.getBoolean("yarn.timeline-service.http-cross-origin.enabled", false)) {
            initializers = CrossOriginFilterInitializer.class.getName() + "," + initializers;
            modifiedInitializers = true;
        }
        if (!initializers.contains(TimelineAuthenticationFilterInitializer.class.getName())) {
            initializers = TimelineAuthenticationFilterInitializer.class.getName() + "," + initializers;
            modifiedInitializers = true;
        }
        final String[] parts = initializers.split(",");
        final ArrayList<String> target = new ArrayList<String>();
        for (String filterInitializer : parts) {
            filterInitializer = filterInitializer.trim();
            if (filterInitializer.equals(AuthenticationFilterInitializer.class.getName())) {
                modifiedInitializers = true;
            }
            else {
                target.add(filterInitializer);
            }
        }
        final String actualInitializers = org.apache.commons.lang.StringUtils.join(target, ",");
        if (modifiedInitializers) {
            conf.set("hadoop.http.filter.initializers", actualInitializers);
        }
        final String bindAddress = WebAppUtils.getWebAppBindURL(conf, "yarn.timeline-service.bind-host", WebAppUtils.getAHSWebAppURLWithoutScheme(conf));
        ApplicationHistoryServer.LOG.info("Instantiating AHSWebApp at " + bindAddress);
        try {
            this.webApp = WebApps.$for("applicationhistory", ApplicationHistoryClientService.class, this.ahsClientService, "ws").with(conf).at(bindAddress).start(new AHSWebApp(this.timelineDataManager, this.historyManager));
        }
        catch (Exception e) {
            final String msg = "AHSWebApp failed to start.";
            ApplicationHistoryServer.LOG.error(msg, e);
            throw new YarnRuntimeException(msg, e);
        }
    }
    
    private void doSecureLogin(final Configuration conf) throws IOException {
        final InetSocketAddress socAddr = getBindAddress(conf);
        SecurityUtil.login(conf, "yarn.timeline-service.keytab", "yarn.timeline-service.principal", socAddr.getHostName());
    }
    
    private static InetSocketAddress getBindAddress(final Configuration conf) {
        return conf.getSocketAddr("yarn.timeline-service.address", "0.0.0.0:10200", 10200);
    }
    
    static {
        LOG = LogFactory.getLog(ApplicationHistoryServer.class);
    }
}
