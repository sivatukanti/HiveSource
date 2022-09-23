// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler;

import org.apache.hadoop.metrics2.lib.Interns;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.yarn.server.utils.BuilderUtils;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.metrics2.MetricsCollector;
import java.util.ArrayList;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import org.apache.hadoop.metrics2.MetricsSystem;
import org.apache.hadoop.metrics2.lib.MetricsRegistry;
import com.google.common.base.Splitter;
import org.apache.hadoop.metrics2.MetricsInfo;
import org.slf4j.Logger;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.metrics2.lib.MutableCounterLong;
import org.apache.hadoop.metrics2.lib.MutableGaugeInt;
import org.apache.hadoop.metrics2.annotation.Metric;
import org.apache.hadoop.metrics2.lib.MutableCounterInt;
import org.apache.hadoop.metrics2.annotation.Metrics;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.metrics2.MetricsSource;

@InterfaceAudience.Private
@Metrics(context = "yarn")
public class QueueMetrics implements MetricsSource
{
    @Metric({ "# of apps submitted" })
    MutableCounterInt appsSubmitted;
    @Metric({ "# of running apps" })
    MutableGaugeInt appsRunning;
    @Metric({ "# of pending apps" })
    MutableGaugeInt appsPending;
    @Metric({ "# of apps completed" })
    MutableCounterInt appsCompleted;
    @Metric({ "# of apps killed" })
    MutableCounterInt appsKilled;
    @Metric({ "# of apps failed" })
    MutableCounterInt appsFailed;
    @Metric({ "Allocated memory in MB" })
    MutableGaugeInt allocatedMB;
    @Metric({ "Allocated CPU in virtual cores" })
    MutableGaugeInt allocatedVCores;
    @Metric({ "# of allocated containers" })
    MutableGaugeInt allocatedContainers;
    @Metric({ "Aggregate # of allocated containers" })
    MutableCounterLong aggregateContainersAllocated;
    @Metric({ "Aggregate # of released containers" })
    MutableCounterLong aggregateContainersReleased;
    @Metric({ "Available memory in MB" })
    MutableGaugeInt availableMB;
    @Metric({ "Available CPU in virtual cores" })
    MutableGaugeInt availableVCores;
    @Metric({ "Pending memory allocation in MB" })
    MutableGaugeInt pendingMB;
    @Metric({ "Pending CPU allocation in virtual cores" })
    MutableGaugeInt pendingVCores;
    @Metric({ "# of pending containers" })
    MutableGaugeInt pendingContainers;
    @Metric({ "# of reserved memory in MB" })
    MutableGaugeInt reservedMB;
    @Metric({ "Reserved CPU in virtual cores" })
    MutableGaugeInt reservedVCores;
    @Metric({ "# of reserved containers" })
    MutableGaugeInt reservedContainers;
    @Metric({ "# of active users" })
    MutableGaugeInt activeUsers;
    @Metric({ "# of active applications" })
    MutableGaugeInt activeApplications;
    private final MutableGaugeInt[] runningTime;
    private TimeBucketMetrics<ApplicationId> runBuckets;
    static final Logger LOG;
    static final MetricsInfo RECORD_INFO;
    protected static final MetricsInfo QUEUE_INFO;
    static final MetricsInfo USER_INFO;
    static final Splitter Q_SPLITTER;
    final MetricsRegistry registry;
    final String queueName;
    final QueueMetrics parent;
    final MetricsSystem metricsSystem;
    private final Map<String, QueueMetrics> users;
    private final Configuration conf;
    protected static final Map<String, QueueMetrics> queueMetrics;
    
    protected QueueMetrics(final MetricsSystem ms, final String queueName, final Queue parent, final boolean enableUserMetrics, final Configuration conf) {
        this.registry = new MetricsRegistry(QueueMetrics.RECORD_INFO);
        this.queueName = queueName;
        this.parent = ((parent != null) ? parent.getMetrics() : null);
        this.users = (enableUserMetrics ? new HashMap<String, QueueMetrics>() : null);
        this.metricsSystem = ms;
        this.conf = conf;
        this.runningTime = this.buildBuckets(conf);
    }
    
    protected QueueMetrics tag(final MetricsInfo info, final String value) {
        this.registry.tag(info, value);
        return this;
    }
    
    protected static StringBuilder sourceName(final String queueName) {
        final StringBuilder sb = new StringBuilder(QueueMetrics.RECORD_INFO.name());
        int i = 0;
        for (final String node : QueueMetrics.Q_SPLITTER.split(queueName)) {
            sb.append(",q").append(i++).append('=').append(node);
        }
        return sb;
    }
    
    public static synchronized QueueMetrics forQueue(final String queueName, final Queue parent, final boolean enableUserMetrics, final Configuration conf) {
        return forQueue(DefaultMetricsSystem.instance(), queueName, parent, enableUserMetrics, conf);
    }
    
    @InterfaceAudience.Private
    public static synchronized void clearQueueMetrics() {
        QueueMetrics.queueMetrics.clear();
    }
    
    public static synchronized QueueMetrics forQueue(final MetricsSystem ms, final String queueName, final Queue parent, final boolean enableUserMetrics, final Configuration conf) {
        QueueMetrics metrics = QueueMetrics.queueMetrics.get(queueName);
        if (metrics == null) {
            metrics = new QueueMetrics(ms, queueName, parent, enableUserMetrics, conf).tag(QueueMetrics.QUEUE_INFO, queueName);
            if (ms != null) {
                metrics = ms.register(sourceName(queueName).toString(), "Metrics for queue: " + queueName, metrics);
            }
            QueueMetrics.queueMetrics.put(queueName, metrics);
        }
        return metrics;
    }
    
    public synchronized QueueMetrics getUserMetrics(final String userName) {
        if (this.users == null) {
            return null;
        }
        QueueMetrics metrics = this.users.get(userName);
        if (metrics == null) {
            metrics = new QueueMetrics(this.metricsSystem, this.queueName, null, false, this.conf);
            this.users.put(userName, metrics);
            this.metricsSystem.register(sourceName(this.queueName).append(",user=").append(userName).toString(), "Metrics for user '" + userName + "' in queue '" + this.queueName + "'", metrics.tag(QueueMetrics.QUEUE_INFO, this.queueName).tag(QueueMetrics.USER_INFO, userName));
        }
        return metrics;
    }
    
    private ArrayList<Integer> parseInts(final String value) {
        final ArrayList<Integer> result = new ArrayList<Integer>();
        for (final String s : value.split(",")) {
            result.add(Integer.parseInt(s.trim()));
        }
        return result;
    }
    
    private MutableGaugeInt[] buildBuckets(final Configuration conf) {
        final ArrayList<Integer> buckets = this.parseInts(conf.get("yarn.resourcemanager.metrics.runtime.buckets", "60,300,1440"));
        final MutableGaugeInt[] result = new MutableGaugeInt[buckets.size() + 1];
        result[0] = this.registry.newGauge("running_0", "", 0);
        final long[] cuts = new long[buckets.size()];
        for (int i = 0; i < buckets.size(); ++i) {
            result[i + 1] = this.registry.newGauge("running_" + buckets.get(i), "", 0);
            cuts[i] = buckets.get(i) * 1000L * 60L;
        }
        this.runBuckets = new TimeBucketMetrics<ApplicationId>(cuts);
        return result;
    }
    
    private void updateRunningTime() {
        final int[] counts = this.runBuckets.getBucketCounts(System.currentTimeMillis());
        for (int i = 0; i < counts.length; ++i) {
            this.runningTime[i].set(counts[i]);
        }
    }
    
    @Override
    public void getMetrics(final MetricsCollector collector, final boolean all) {
        this.updateRunningTime();
        this.registry.snapshot(collector.addRecord(this.registry.info()), all);
    }
    
    public void submitApp(final String user) {
        this.appsSubmitted.incr();
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.submitApp(user);
        }
        if (this.parent != null) {
            this.parent.submitApp(user);
        }
    }
    
    public void submitAppAttempt(final String user) {
        this.appsPending.incr();
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.submitAppAttempt(user);
        }
        if (this.parent != null) {
            this.parent.submitAppAttempt(user);
        }
    }
    
    public void runAppAttempt(final ApplicationId appId, final String user) {
        this.runBuckets.add(appId, System.currentTimeMillis());
        this.appsRunning.incr();
        this.appsPending.decr();
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.runAppAttempt(appId, user);
        }
        if (this.parent != null) {
            this.parent.runAppAttempt(appId, user);
        }
    }
    
    public void finishAppAttempt(final ApplicationId appId, final boolean isPending, final String user) {
        this.runBuckets.remove(appId);
        if (isPending) {
            this.appsPending.decr();
        }
        else {
            this.appsRunning.decr();
        }
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.finishAppAttempt(appId, isPending, user);
        }
        if (this.parent != null) {
            this.parent.finishAppAttempt(appId, isPending, user);
        }
    }
    
    public void finishApp(final String user, final RMAppState rmAppFinalState) {
        switch (rmAppFinalState) {
            case KILLED: {
                this.appsKilled.incr();
                break;
            }
            case FAILED: {
                this.appsFailed.incr();
                break;
            }
            default: {
                this.appsCompleted.incr();
                break;
            }
        }
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.finishApp(user, rmAppFinalState);
        }
        if (this.parent != null) {
            this.parent.finishApp(user, rmAppFinalState);
        }
    }
    
    public void moveAppFrom(final AppSchedulingInfo app) {
        if (app.isPending()) {
            this.appsPending.decr();
        }
        else {
            this.appsRunning.decr();
        }
        final QueueMetrics userMetrics = this.getUserMetrics(app.getUser());
        if (userMetrics != null) {
            userMetrics.moveAppFrom(app);
        }
        if (this.parent != null) {
            this.parent.moveAppFrom(app);
        }
    }
    
    public void moveAppTo(final AppSchedulingInfo app) {
        if (app.isPending()) {
            this.appsPending.incr();
        }
        else {
            this.appsRunning.incr();
        }
        final QueueMetrics userMetrics = this.getUserMetrics(app.getUser());
        if (userMetrics != null) {
            userMetrics.moveAppTo(app);
        }
        if (this.parent != null) {
            this.parent.moveAppTo(app);
        }
    }
    
    public void setAvailableResourcesToQueue(final Resource limit) {
        this.availableMB.set(limit.getMemory());
        this.availableVCores.set(limit.getVirtualCores());
    }
    
    public void setAvailableResourcesToUser(final String user, final Resource limit) {
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.setAvailableResourcesToQueue(limit);
        }
    }
    
    public void incrPendingResources(final String user, final int containers, final Resource res) {
        this._incrPendingResources(containers, res);
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.incrPendingResources(user, containers, res);
        }
        if (this.parent != null) {
            this.parent.incrPendingResources(user, containers, res);
        }
    }
    
    private void _incrPendingResources(final int containers, final Resource res) {
        this.pendingContainers.incr(containers);
        this.pendingMB.incr(res.getMemory() * containers);
        this.pendingVCores.incr(res.getVirtualCores() * containers);
    }
    
    public void decrPendingResources(final String user, final int containers, final Resource res) {
        this._decrPendingResources(containers, res);
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.decrPendingResources(user, containers, res);
        }
        if (this.parent != null) {
            this.parent.decrPendingResources(user, containers, res);
        }
    }
    
    private void _decrPendingResources(final int containers, final Resource res) {
        this.pendingContainers.decr(containers);
        this.pendingMB.decr(res.getMemory() * containers);
        this.pendingVCores.decr(res.getVirtualCores() * containers);
    }
    
    public void allocateResources(final String user, final int containers, final Resource res, final boolean decrPending) {
        this.allocatedContainers.incr(containers);
        this.aggregateContainersAllocated.incr(containers);
        this.allocatedMB.incr(res.getMemory() * containers);
        this.allocatedVCores.incr(res.getVirtualCores() * containers);
        if (decrPending) {
            this._decrPendingResources(containers, res);
        }
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.allocateResources(user, containers, res, decrPending);
        }
        if (this.parent != null) {
            this.parent.allocateResources(user, containers, res, decrPending);
        }
    }
    
    public void releaseResources(final String user, final int containers, final Resource res) {
        this.allocatedContainers.decr(containers);
        this.aggregateContainersReleased.incr(containers);
        this.allocatedMB.decr(res.getMemory() * containers);
        this.allocatedVCores.decr(res.getVirtualCores() * containers);
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.releaseResources(user, containers, res);
        }
        if (this.parent != null) {
            this.parent.releaseResources(user, containers, res);
        }
    }
    
    public void reserveResource(final String user, final Resource res) {
        this.reservedContainers.incr();
        this.reservedMB.incr(res.getMemory());
        this.reservedVCores.incr(res.getVirtualCores());
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.reserveResource(user, res);
        }
        if (this.parent != null) {
            this.parent.reserveResource(user, res);
        }
    }
    
    public void unreserveResource(final String user, final Resource res) {
        this.reservedContainers.decr();
        this.reservedMB.decr(res.getMemory());
        this.reservedVCores.decr(res.getVirtualCores());
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.unreserveResource(user, res);
        }
        if (this.parent != null) {
            this.parent.unreserveResource(user, res);
        }
    }
    
    public void incrActiveUsers() {
        this.activeUsers.incr();
    }
    
    public void decrActiveUsers() {
        this.activeUsers.decr();
    }
    
    public void activateApp(final String user) {
        this.activeApplications.incr();
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.activateApp(user);
        }
        if (this.parent != null) {
            this.parent.activateApp(user);
        }
    }
    
    public void deactivateApp(final String user) {
        this.activeApplications.decr();
        final QueueMetrics userMetrics = this.getUserMetrics(user);
        if (userMetrics != null) {
            userMetrics.deactivateApp(user);
        }
        if (this.parent != null) {
            this.parent.deactivateApp(user);
        }
    }
    
    public int getAppsSubmitted() {
        return this.appsSubmitted.value();
    }
    
    public int getAppsRunning() {
        return this.appsRunning.value();
    }
    
    public int getAppsPending() {
        return this.appsPending.value();
    }
    
    public int getAppsCompleted() {
        return this.appsCompleted.value();
    }
    
    public int getAppsKilled() {
        return this.appsKilled.value();
    }
    
    public int getAppsFailed() {
        return this.appsFailed.value();
    }
    
    public Resource getAllocatedResources() {
        return BuilderUtils.newResource(this.allocatedMB.value(), this.allocatedVCores.value());
    }
    
    public int getAllocatedMB() {
        return this.allocatedMB.value();
    }
    
    public int getAllocatedVirtualCores() {
        return this.allocatedVCores.value();
    }
    
    public int getAllocatedContainers() {
        return this.allocatedContainers.value();
    }
    
    public int getAvailableMB() {
        return this.availableMB.value();
    }
    
    public int getAvailableVirtualCores() {
        return this.availableVCores.value();
    }
    
    public int getPendingMB() {
        return this.pendingMB.value();
    }
    
    public int getPendingVirtualCores() {
        return this.pendingVCores.value();
    }
    
    public int getPendingContainers() {
        return this.pendingContainers.value();
    }
    
    public int getReservedMB() {
        return this.reservedMB.value();
    }
    
    public int getReservedVirtualCores() {
        return this.reservedVCores.value();
    }
    
    public int getReservedContainers() {
        return this.reservedContainers.value();
    }
    
    public int getActiveUsers() {
        return this.activeUsers.value();
    }
    
    public int getActiveApps() {
        return this.activeApplications.value();
    }
    
    public MetricsSystem getMetricsSystem() {
        return this.metricsSystem;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QueueMetrics.class);
        RECORD_INFO = Interns.info("QueueMetrics", "Metrics for the resource scheduler");
        QUEUE_INFO = Interns.info("Queue", "Metrics by queue");
        USER_INFO = Interns.info("User", "Metrics by user");
        Q_SPLITTER = Splitter.on('.').omitEmptyStrings().trimResults();
        queueMetrics = new HashMap<String, QueueMetrics>();
    }
}
