// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.LeafQueue;
import org.apache.hadoop.yarn.server.resourcemanager.resource.Priority;
import java.util.Comparator;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp;
import java.util.NavigableSet;
import java.util.HashSet;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerPreemptEventType;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import org.apache.hadoop.yarn.util.resource.Resources;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.PreemptableResourceScheduler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.util.SystemClock;
import java.util.HashMap;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import java.util.Map;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ContainerPreemptEvent;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.monitor.SchedulingEditPolicy;

public class ProportionalCapacityPreemptionPolicy implements SchedulingEditPolicy
{
    private static final Log LOG;
    public static final String OBSERVE_ONLY = "yarn.resourcemanager.monitor.capacity.preemption.observe_only";
    public static final String MONITORING_INTERVAL = "yarn.resourcemanager.monitor.capacity.preemption.monitoring_interval";
    public static final String WAIT_TIME_BEFORE_KILL = "yarn.resourcemanager.monitor.capacity.preemption.max_wait_before_kill";
    public static final String TOTAL_PREEMPTION_PER_ROUND = "yarn.resourcemanager.monitor.capacity.preemption.total_preemption_per_round";
    public static final String MAX_IGNORED_OVER_CAPACITY = "yarn.resourcemanager.monitor.capacity.preemption.max_ignored_over_capacity";
    public static final String NATURAL_TERMINATION_FACTOR = "yarn.resourcemanager.monitor.capacity.preemption.natural_termination_factor";
    public EventHandler<ContainerPreemptEvent> dispatcher;
    private final Clock clock;
    private double maxIgnoredOverCapacity;
    private long maxWaitTime;
    private CapacityScheduler scheduler;
    private long monitoringInterval;
    private final Map<RMContainer, Long> preempted;
    private ResourceCalculator rc;
    private float percentageClusterPreemptionAllowed;
    private double naturalTerminationFactor;
    private boolean observeOnly;
    
    public ProportionalCapacityPreemptionPolicy() {
        this.preempted = new HashMap<RMContainer, Long>();
        this.clock = new SystemClock();
    }
    
    public ProportionalCapacityPreemptionPolicy(final Configuration config, final EventHandler<ContainerPreemptEvent> dispatcher, final CapacityScheduler scheduler) {
        this(config, dispatcher, scheduler, new SystemClock());
    }
    
    public ProportionalCapacityPreemptionPolicy(final Configuration config, final EventHandler<ContainerPreemptEvent> dispatcher, final CapacityScheduler scheduler, final Clock clock) {
        this.preempted = new HashMap<RMContainer, Long>();
        this.init(config, dispatcher, scheduler);
        this.clock = clock;
    }
    
    @Override
    public void init(final Configuration config, final EventHandler<ContainerPreemptEvent> disp, final PreemptableResourceScheduler sched) {
        ProportionalCapacityPreemptionPolicy.LOG.info("Preemption monitor:" + this.getClass().getCanonicalName());
        assert null == this.scheduler : "Unexpected duplicate call to init";
        if (!(sched instanceof CapacityScheduler)) {
            throw new YarnRuntimeException("Class " + sched.getClass().getCanonicalName() + " not instance of " + CapacityScheduler.class.getCanonicalName());
        }
        this.dispatcher = disp;
        this.scheduler = (CapacityScheduler)sched;
        this.maxIgnoredOverCapacity = config.getDouble("yarn.resourcemanager.monitor.capacity.preemption.max_ignored_over_capacity", 0.1);
        this.naturalTerminationFactor = config.getDouble("yarn.resourcemanager.monitor.capacity.preemption.natural_termination_factor", 0.2);
        this.maxWaitTime = config.getLong("yarn.resourcemanager.monitor.capacity.preemption.max_wait_before_kill", 15000L);
        this.monitoringInterval = config.getLong("yarn.resourcemanager.monitor.capacity.preemption.monitoring_interval", 3000L);
        this.percentageClusterPreemptionAllowed = config.getFloat("yarn.resourcemanager.monitor.capacity.preemption.total_preemption_per_round", 0.1f);
        this.observeOnly = config.getBoolean("yarn.resourcemanager.monitor.capacity.preemption.observe_only", false);
        this.rc = this.scheduler.getResourceCalculator();
    }
    
    @VisibleForTesting
    public ResourceCalculator getResourceCalculator() {
        return this.rc;
    }
    
    @Override
    public void editSchedule() {
        final CSQueue root = this.scheduler.getRootQueue();
        final Resource clusterResources = Resources.clone(this.scheduler.getClusterResource());
        this.containerBasedPreemptOrKill(root, clusterResources);
    }
    
    private void containerBasedPreemptOrKill(final CSQueue root, final Resource clusterResources) {
        final TempQueue tRoot;
        synchronized (this.scheduler) {
            tRoot = this.cloneQueues(root, clusterResources);
        }
        tRoot.idealAssigned = tRoot.guaranteed;
        final Resource totalPreemptionAllowed = Resources.multiply(clusterResources, this.percentageClusterPreemptionAllowed);
        final List<TempQueue> queues = this.recursivelyComputeIdealAssignment(tRoot, totalPreemptionAllowed);
        final Map<ApplicationAttemptId, Set<RMContainer>> toPreempt = this.getContainersToPreempt(queues, clusterResources);
        if (ProportionalCapacityPreemptionPolicy.LOG.isDebugEnabled()) {
            this.logToCSV(queues);
        }
        if (this.observeOnly) {
            return;
        }
        for (final Map.Entry<ApplicationAttemptId, Set<RMContainer>> e : toPreempt.entrySet()) {
            for (final RMContainer container : e.getValue()) {
                if (this.preempted.get(container) != null && this.preempted.get(container) + this.maxWaitTime < this.clock.getTime()) {
                    this.dispatcher.handle(new ContainerPreemptEvent(e.getKey(), container, ContainerPreemptEventType.KILL_CONTAINER));
                    this.preempted.remove(container);
                }
                else {
                    this.dispatcher.handle(new ContainerPreemptEvent(e.getKey(), container, ContainerPreemptEventType.PREEMPT_CONTAINER));
                    if (this.preempted.get(container) != null) {
                        continue;
                    }
                    this.preempted.put(container, this.clock.getTime());
                }
            }
        }
        final Iterator<RMContainer> i = this.preempted.keySet().iterator();
        while (i.hasNext()) {
            final RMContainer id = i.next();
            if (this.preempted.get(id) + 2L * this.maxWaitTime < this.clock.getTime()) {
                i.remove();
            }
        }
    }
    
    private List<TempQueue> recursivelyComputeIdealAssignment(final TempQueue root, final Resource totalPreemptionAllowed) {
        final List<TempQueue> leafs = new ArrayList<TempQueue>();
        if (root.getChildren() != null && root.getChildren().size() > 0) {
            this.computeIdealResourceDistribution(this.rc, root.getChildren(), totalPreemptionAllowed, root.idealAssigned);
            for (final TempQueue t : root.getChildren()) {
                leafs.addAll(this.recursivelyComputeIdealAssignment(t, totalPreemptionAllowed));
            }
            return leafs;
        }
        return Collections.singletonList(root);
    }
    
    private void computeIdealResourceDistribution(final ResourceCalculator rc, final List<TempQueue> queues, final Resource totalPreemptionAllowed, final Resource tot_guarant) {
        final List<TempQueue> qAlloc = new ArrayList<TempQueue>(queues);
        final Resource unassigned = Resources.clone(tot_guarant);
        final Set<TempQueue> nonZeroGuarQueues = new HashSet<TempQueue>();
        final Set<TempQueue> zeroGuarQueues = new HashSet<TempQueue>();
        for (final TempQueue q : qAlloc) {
            if (Resources.greaterThan(rc, tot_guarant, q.guaranteed, Resources.none())) {
                nonZeroGuarQueues.add(q);
            }
            else {
                zeroGuarQueues.add(q);
            }
        }
        this.computeFixpointAllocation(rc, tot_guarant, nonZeroGuarQueues, unassigned, false);
        if (!zeroGuarQueues.isEmpty() && Resources.greaterThan(rc, tot_guarant, unassigned, Resources.none())) {
            this.computeFixpointAllocation(rc, tot_guarant, zeroGuarQueues, unassigned, true);
        }
        final Resource totPreemptionNeeded = Resource.newInstance(0, 0);
        for (final TempQueue t : queues) {
            if (Resources.greaterThan(rc, tot_guarant, t.current, t.idealAssigned)) {
                Resources.addTo(totPreemptionNeeded, Resources.subtract(t.current, t.idealAssigned));
            }
        }
        float scalingFactor = 1.0f;
        if (Resources.greaterThan(rc, tot_guarant, totPreemptionNeeded, totalPreemptionAllowed)) {
            scalingFactor = Resources.divide(rc, tot_guarant, totalPreemptionAllowed, totPreemptionNeeded);
        }
        for (final TempQueue t2 : queues) {
            t2.assignPreemption(scalingFactor, rc, tot_guarant);
        }
        if (ProportionalCapacityPreemptionPolicy.LOG.isDebugEnabled()) {
            final long time = this.clock.getTime();
            for (final TempQueue t3 : queues) {
                ProportionalCapacityPreemptionPolicy.LOG.debug(time + ": " + t3);
            }
        }
    }
    
    private void computeFixpointAllocation(final ResourceCalculator rc, final Resource tot_guarant, final Collection<TempQueue> qAlloc, final Resource unassigned, final boolean ignoreGuarantee) {
        while (!qAlloc.isEmpty() && Resources.greaterThan(rc, tot_guarant, unassigned, Resources.none())) {
            final Resource wQassigned = Resource.newInstance(0, 0);
            this.resetCapacity(rc, unassigned, qAlloc, ignoreGuarantee);
            final Iterator<TempQueue> i = qAlloc.iterator();
            while (i.hasNext()) {
                final TempQueue sub = i.next();
                final Resource wQavail = Resources.multiply(unassigned, sub.normalizedGuarantee);
                final Resource wQidle = sub.offer(wQavail, rc, tot_guarant);
                final Resource wQdone = Resources.subtract(wQavail, wQidle);
                if (!Resources.greaterThan(rc, tot_guarant, wQdone, Resources.none())) {
                    i.remove();
                }
                Resources.addTo(wQassigned, wQdone);
            }
            Resources.subtractFrom(unassigned, wQassigned);
        }
    }
    
    private void resetCapacity(final ResourceCalculator rc, final Resource clusterResource, final Collection<TempQueue> queues, final boolean ignoreGuar) {
        final Resource activeCap = Resource.newInstance(0, 0);
        if (ignoreGuar) {
            for (final TempQueue q : queues) {
                q.normalizedGuarantee = 1.0f / queues.size();
            }
        }
        else {
            for (final TempQueue q : queues) {
                Resources.addTo(activeCap, q.guaranteed);
            }
            for (final TempQueue q : queues) {
                q.normalizedGuarantee = Resources.divide(rc, clusterResource, q.guaranteed, activeCap);
            }
        }
    }
    
    private Map<ApplicationAttemptId, Set<RMContainer>> getContainersToPreempt(final List<TempQueue> queues, final Resource clusterResource) {
        final Map<ApplicationAttemptId, Set<RMContainer>> preemptMap = new HashMap<ApplicationAttemptId, Set<RMContainer>>();
        final List<RMContainer> skippedAMContainerlist = new ArrayList<RMContainer>();
        for (final TempQueue qT : queues) {
            if (Resources.greaterThan(this.rc, clusterResource, qT.current, Resources.multiply(qT.guaranteed, 1.0 + this.maxIgnoredOverCapacity))) {
                final Resource resToObtain = Resources.multiply(qT.toBePreempted, this.naturalTerminationFactor);
                final Resource skippedAMSize = Resource.newInstance(0, 0);
                synchronized (qT.leafQueue) {
                    final NavigableSet<FiCaSchedulerApp> ns = (NavigableSet<FiCaSchedulerApp>)(NavigableSet)qT.leafQueue.getApplications();
                    final Iterator<FiCaSchedulerApp> desc = ns.descendingIterator();
                    qT.actuallyPreempted = Resources.clone(resToObtain);
                    while (desc.hasNext()) {
                        final FiCaSchedulerApp fc = desc.next();
                        if (Resources.lessThanOrEqual(this.rc, clusterResource, resToObtain, Resources.none())) {
                            break;
                        }
                        preemptMap.put(fc.getApplicationAttemptId(), this.preemptFrom(fc, clusterResource, resToObtain, skippedAMContainerlist, skippedAMSize));
                    }
                    final Resource maxAMCapacityForThisQueue = Resources.multiply(Resources.multiply(clusterResource, qT.leafQueue.getAbsoluteCapacity()), qT.leafQueue.getMaxAMResourcePerQueuePercent());
                    this.preemptAMContainers(clusterResource, preemptMap, skippedAMContainerlist, resToObtain, skippedAMSize, maxAMCapacityForThisQueue);
                }
            }
        }
        return preemptMap;
    }
    
    private void preemptAMContainers(final Resource clusterResource, final Map<ApplicationAttemptId, Set<RMContainer>> preemptMap, final List<RMContainer> skippedAMContainerlist, final Resource resToObtain, final Resource skippedAMSize, final Resource maxAMCapacityForThisQueue) {
        for (final RMContainer c : skippedAMContainerlist) {
            if (Resources.lessThanOrEqual(this.rc, clusterResource, resToObtain, Resources.none())) {
                break;
            }
            if (Resources.lessThanOrEqual(this.rc, clusterResource, skippedAMSize, maxAMCapacityForThisQueue)) {
                break;
            }
            Set<RMContainer> contToPrempt = preemptMap.get(c.getApplicationAttemptId());
            if (null == contToPrempt) {
                contToPrempt = new HashSet<RMContainer>();
                preemptMap.put(c.getApplicationAttemptId(), contToPrempt);
            }
            contToPrempt.add(c);
            Resources.subtractFrom(resToObtain, c.getContainer().getResource());
            Resources.subtractFrom(skippedAMSize, c.getContainer().getResource());
        }
        skippedAMContainerlist.clear();
    }
    
    private Set<RMContainer> preemptFrom(final FiCaSchedulerApp app, final Resource clusterResource, final Resource rsrcPreempt, final List<RMContainer> skippedAMContainerlist, final Resource skippedAMSize) {
        final Set<RMContainer> ret = new HashSet<RMContainer>();
        final ApplicationAttemptId appId = app.getApplicationAttemptId();
        final List<RMContainer> reservations = new ArrayList<RMContainer>(app.getReservedContainers());
        for (final RMContainer c : reservations) {
            if (Resources.lessThanOrEqual(this.rc, clusterResource, rsrcPreempt, Resources.none())) {
                return ret;
            }
            if (!this.observeOnly) {
                this.dispatcher.handle(new ContainerPreemptEvent(appId, c, ContainerPreemptEventType.DROP_RESERVATION));
            }
            Resources.subtractFrom(rsrcPreempt, c.getContainer().getResource());
        }
        final List<RMContainer> containers = new ArrayList<RMContainer>(app.getLiveContainers());
        sortContainers(containers);
        for (final RMContainer c2 : containers) {
            if (Resources.lessThanOrEqual(this.rc, clusterResource, rsrcPreempt, Resources.none())) {
                return ret;
            }
            if (c2.isAMContainer()) {
                skippedAMContainerlist.add(c2);
                Resources.addTo(skippedAMSize, c2.getContainer().getResource());
            }
            else {
                ret.add(c2);
                Resources.subtractFrom(rsrcPreempt, c2.getContainer().getResource());
            }
        }
        return ret;
    }
    
    @VisibleForTesting
    static void sortContainers(final List<RMContainer> containers) {
        Collections.sort(containers, new Comparator<RMContainer>() {
            @Override
            public int compare(final RMContainer a, final RMContainer b) {
                final Comparator<org.apache.hadoop.yarn.api.records.Priority> c = new Priority.Comparator();
                final int priorityComp = c.compare(b.getContainer().getPriority(), a.getContainer().getPriority());
                if (priorityComp != 0) {
                    return priorityComp;
                }
                return b.getContainerId().compareTo(a.getContainerId());
            }
        });
    }
    
    @Override
    public long getMonitoringInterval() {
        return this.monitoringInterval;
    }
    
    @Override
    public String getPolicyName() {
        return "ProportionalCapacityPreemptionPolicy";
    }
    
    private TempQueue cloneQueues(final CSQueue root, final Resource clusterResources) {
        TempQueue ret;
        synchronized (root) {
            final String queueName = root.getQueueName();
            final float absUsed = root.getAbsoluteUsedCapacity();
            final float absCap = root.getAbsoluteCapacity();
            final float absMaxCap = root.getAbsoluteMaximumCapacity();
            final Resource current = Resources.multiply(clusterResources, absUsed);
            final Resource guaranteed = Resources.multiply(clusterResources, absCap);
            final Resource maxCapacity = Resources.multiply(clusterResources, absMaxCap);
            if (root instanceof LeafQueue) {
                final LeafQueue l = (LeafQueue)root;
                final Resource pending = l.getTotalResourcePending();
                ret = new TempQueue(queueName, current, pending, guaranteed, maxCapacity);
                ret.setLeafQueue(l);
            }
            else {
                final Resource pending2 = Resource.newInstance(0, 0);
                ret = new TempQueue(root.getQueueName(), current, pending2, guaranteed, maxCapacity);
                for (final CSQueue c : root.getChildQueues()) {
                    ret.addChild(this.cloneQueues(c, clusterResources));
                }
            }
        }
        return ret;
    }
    
    private void logToCSV(final List<TempQueue> unorderedqueues) {
        final List<TempQueue> queues = new ArrayList<TempQueue>(unorderedqueues);
        Collections.sort(queues, new Comparator<TempQueue>() {
            @Override
            public int compare(final TempQueue o1, final TempQueue o2) {
                return o1.queueName.compareTo(o2.queueName);
            }
        });
        final String queueState = " QUEUESTATE: " + this.clock.getTime();
        final StringBuilder sb = new StringBuilder();
        sb.append(queueState);
        for (final TempQueue tq : queues) {
            sb.append(", ");
            tq.appendLogString(sb);
        }
        ProportionalCapacityPreemptionPolicy.LOG.debug(sb.toString());
    }
    
    static {
        LOG = LogFactory.getLog(ProportionalCapacityPreemptionPolicy.class);
    }
    
    static class TempQueue
    {
        final String queueName;
        final Resource current;
        final Resource pending;
        final Resource guaranteed;
        final Resource maxCapacity;
        Resource idealAssigned;
        Resource toBePreempted;
        Resource actuallyPreempted;
        double normalizedGuarantee;
        final ArrayList<TempQueue> children;
        LeafQueue leafQueue;
        
        TempQueue(final String queueName, final Resource current, final Resource pending, final Resource guaranteed, final Resource maxCapacity) {
            this.queueName = queueName;
            this.current = current;
            this.pending = pending;
            this.guaranteed = guaranteed;
            this.maxCapacity = maxCapacity;
            this.idealAssigned = Resource.newInstance(0, 0);
            this.actuallyPreempted = Resource.newInstance(0, 0);
            this.toBePreempted = Resource.newInstance(0, 0);
            this.normalizedGuarantee = Double.NaN;
            this.children = new ArrayList<TempQueue>();
        }
        
        public void setLeafQueue(final LeafQueue l) {
            assert this.children.size() == 0;
            this.leafQueue = l;
        }
        
        public void addChild(final TempQueue q) {
            assert this.leafQueue == null;
            this.children.add(q);
            Resources.addTo(this.pending, q.pending);
        }
        
        public void addChildren(final ArrayList<TempQueue> queues) {
            assert this.leafQueue == null;
            this.children.addAll(queues);
        }
        
        public ArrayList<TempQueue> getChildren() {
            return this.children;
        }
        
        Resource offer(final Resource avail, final ResourceCalculator rc, final Resource clusterResource) {
            final Resource accepted = Resources.min(rc, clusterResource, Resources.subtract(this.maxCapacity, this.idealAssigned), Resources.min(rc, clusterResource, avail, Resources.subtract(Resources.add(this.current, this.pending), this.idealAssigned)));
            final Resource remain = Resources.subtract(avail, accepted);
            Resources.addTo(this.idealAssigned, accepted);
            return remain;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(" NAME: " + this.queueName).append(" CUR: ").append(this.current).append(" PEN: ").append(this.pending).append(" GAR: ").append(this.guaranteed).append(" NORM: ").append(this.normalizedGuarantee).append(" IDEAL_ASSIGNED: ").append(this.idealAssigned).append(" IDEAL_PREEMPT: ").append(this.toBePreempted).append(" ACTUAL_PREEMPT: ").append(this.actuallyPreempted).append("\n");
            return sb.toString();
        }
        
        public void assignPreemption(final float scalingFactor, final ResourceCalculator rc, final Resource clusterResource) {
            if (Resources.greaterThan(rc, clusterResource, this.current, this.idealAssigned)) {
                this.toBePreempted = Resources.multiply(Resources.subtract(this.current, this.idealAssigned), scalingFactor);
            }
            else {
                this.toBePreempted = Resource.newInstance(0, 0);
            }
        }
        
        void appendLogString(final StringBuilder sb) {
            sb.append(this.queueName).append(", ").append(this.current.getMemory()).append(", ").append(this.current.getVirtualCores()).append(", ").append(this.pending.getMemory()).append(", ").append(this.pending.getVirtualCores()).append(", ").append(this.guaranteed.getMemory()).append(", ").append(this.guaranteed.getVirtualCores()).append(", ").append(this.idealAssigned.getMemory()).append(", ").append(this.idealAssigned.getVirtualCores()).append(", ").append(this.toBePreempted.getMemory()).append(", ").append(this.toBePreempted.getVirtualCores()).append(", ").append(this.actuallyPreempted.getMemory()).append(", ").append(this.actuallyPreempted.getVirtualCores());
        }
    }
}
