// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.util.ReflectionUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.util.HashMap;
import org.apache.hadoop.yarn.util.UTCClock;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.api.records.ReservationId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.yarn.util.Clock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public abstract class AbstractReservationSystem extends AbstractService implements ReservationSystem
{
    private static final Logger LOG;
    private final ReentrantReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;
    private boolean initialized;
    private final Clock clock;
    private AtomicLong resCounter;
    private Map<String, Plan> plans;
    private Map<ReservationId, String> resQMap;
    private RMContext rmContext;
    private ResourceScheduler scheduler;
    private ScheduledExecutorService scheduledExecutorService;
    protected Configuration conf;
    protected long planStepSize;
    private PlanFollower planFollower;
    
    public AbstractReservationSystem(final String name) {
        super(name);
        this.readWriteLock = new ReentrantReadWriteLock(true);
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
        this.initialized = false;
        this.clock = new UTCClock();
        this.resCounter = new AtomicLong();
        this.plans = new HashMap<String, Plan>();
        this.resQMap = new HashMap<ReservationId, String>();
    }
    
    @Override
    public void setRMContext(final RMContext rmContext) {
        this.writeLock.lock();
        try {
            this.rmContext = rmContext;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public void reinitialize(final Configuration conf, final RMContext rmContext) throws YarnException {
        this.writeLock.lock();
        try {
            if (!this.initialized) {
                this.initialize(conf);
                this.initialized = true;
            }
            else {
                this.initializeNewPlans(conf);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private void initialize(final Configuration conf) throws YarnException {
        AbstractReservationSystem.LOG.info("Initializing Reservation system");
        this.conf = conf;
        this.scheduler = this.rmContext.getScheduler();
        this.planStepSize = conf.getTimeDuration("yarn.resourcemanager.reservation-system.planfollower.time-step", 1000L, TimeUnit.MILLISECONDS);
        if (this.planStepSize < 0L) {
            this.planStepSize = 1000L;
        }
        final Set<String> planQueueNames = this.scheduler.getPlanQueues();
        for (final String planQueueName : planQueueNames) {
            final Plan plan = this.initializePlan(planQueueName);
            this.plans.put(planQueueName, plan);
        }
    }
    
    private void initializeNewPlans(final Configuration conf) {
        AbstractReservationSystem.LOG.info("Refreshing Reservation system");
        this.writeLock.lock();
        try {
            final Set<String> planQueueNames = this.scheduler.getPlanQueues();
            for (final String planQueueName : planQueueNames) {
                if (!this.plans.containsKey(planQueueName)) {
                    final Plan plan = this.initializePlan(planQueueName);
                    this.plans.put(planQueueName, plan);
                }
                else {
                    AbstractReservationSystem.LOG.warn("Plan based on reservation queue {0} already exists.", planQueueName);
                }
            }
            if (this.planFollower != null) {
                this.planFollower.setPlans(this.plans.values());
            }
        }
        catch (YarnException e) {
            AbstractReservationSystem.LOG.warn("Exception while trying to refresh reservable queues", e);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    private PlanFollower createPlanFollower() {
        final String planFollowerPolicyClassName = this.conf.get("yarn.resourcemanager.reservation-system.plan.follower", this.getDefaultPlanFollower());
        if (planFollowerPolicyClassName == null) {
            return null;
        }
        AbstractReservationSystem.LOG.info("Using PlanFollowerPolicy: " + planFollowerPolicyClassName);
        try {
            final Class<?> planFollowerPolicyClazz = this.conf.getClassByName(planFollowerPolicyClassName);
            if (PlanFollower.class.isAssignableFrom(planFollowerPolicyClazz)) {
                return ReflectionUtils.newInstance(planFollowerPolicyClazz, this.conf);
            }
            throw new YarnRuntimeException("Class: " + planFollowerPolicyClassName + " not instance of " + PlanFollower.class.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException("Could not instantiate PlanFollowerPolicy: " + planFollowerPolicyClassName, e);
        }
    }
    
    private String getDefaultPlanFollower() {
        if (this.scheduler instanceof CapacityScheduler) {
            return CapacitySchedulerPlanFollower.class.getName();
        }
        return null;
    }
    
    @Override
    public Plan getPlan(final String planName) {
        this.readLock.lock();
        try {
            return this.plans.get(planName);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public long getPlanFollowerTimeStep() {
        this.readLock.lock();
        try {
            return this.planStepSize;
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void synchronizePlan(final String planName) {
        this.writeLock.lock();
        try {
            final Plan plan = this.plans.get(planName);
            if (plan != null) {
                this.planFollower.synchronizePlan(plan);
            }
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void serviceInit(final Configuration conf) throws Exception {
        final Configuration configuration = new Configuration(conf);
        this.reinitialize(configuration, this.rmContext);
        this.planFollower = this.createPlanFollower();
        if (this.planFollower != null) {
            this.planFollower.init(this.clock, this.scheduler, this.plans.values());
        }
        super.serviceInit(conf);
    }
    
    public void serviceStart() throws Exception {
        if (this.planFollower != null) {
            (this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1)).scheduleWithFixedDelay(this.planFollower, 0L, this.planStepSize, TimeUnit.MILLISECONDS);
        }
        super.serviceStart();
    }
    
    public void serviceStop() {
        if (this.scheduledExecutorService != null && !this.scheduledExecutorService.isShutdown()) {
            this.scheduledExecutorService.shutdown();
        }
        this.plans.clear();
    }
    
    @Override
    public String getQueueForReservation(final ReservationId reservationId) {
        this.readLock.lock();
        try {
            return this.resQMap.get(reservationId);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    @Override
    public void setQueueForReservation(final ReservationId reservationId, final String queueName) {
        this.writeLock.lock();
        try {
            this.resQMap.put(reservationId, queueName);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public ReservationId getNewReservationId() {
        this.writeLock.lock();
        try {
            final ReservationId resId = ReservationId.newInstance(ResourceManager.getClusterTimeStamp(), this.resCounter.incrementAndGet());
            AbstractReservationSystem.LOG.info("Allocated new reservationId: " + resId);
            return resId;
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    @Override
    public Map<String, Plan> getAllPlans() {
        return this.plans;
    }
    
    public static String getDefaultReservationSystem(final ResourceScheduler scheduler) {
        if (scheduler instanceof CapacityScheduler) {
            return CapacityReservationSystem.class.getName();
        }
        return null;
    }
    
    protected abstract Plan initializePlan(final String p0) throws YarnException;
    
    protected abstract Planner getReplanner(final String p0);
    
    protected abstract ReservationAgent getAgent(final String p0);
    
    protected abstract SharingPolicy getAdmissionPolicy(final String p0);
    
    static {
        LOG = LoggerFactory.getLogger(AbstractReservationSystem.class);
    }
}
