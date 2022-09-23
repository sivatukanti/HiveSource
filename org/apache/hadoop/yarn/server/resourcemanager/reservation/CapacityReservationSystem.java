// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.reservation;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CSQueue;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "yarn" })
@InterfaceStability.Unstable
public class CapacityReservationSystem extends AbstractReservationSystem
{
    private static final Logger LOG;
    private CapacityScheduler capScheduler;
    
    public CapacityReservationSystem() {
        super(CapacityReservationSystem.class.getName());
    }
    
    @Override
    public void reinitialize(final Configuration conf, final RMContext rmContext) throws YarnException {
        final ResourceScheduler scheduler = rmContext.getScheduler();
        if (!(scheduler instanceof CapacityScheduler)) {
            throw new YarnRuntimeException("Class " + scheduler.getClass().getCanonicalName() + " not instance of " + CapacityScheduler.class.getCanonicalName());
        }
        this.capScheduler = (CapacityScheduler)scheduler;
        super.reinitialize(this.conf = conf, rmContext);
    }
    
    @Override
    protected Plan initializePlan(final String planQueueName) throws YarnException {
        final SharingPolicy adPolicy = this.getAdmissionPolicy(planQueueName);
        final String planQueuePath = this.capScheduler.getQueue(planQueueName).getQueuePath();
        adPolicy.init(planQueuePath, this.capScheduler.getConfiguration());
        final CSQueue planQueue = this.capScheduler.getQueue(planQueueName);
        final Resource minAllocation = this.capScheduler.getMinimumResourceCapability();
        final ResourceCalculator rescCalc = this.capScheduler.getResourceCalculator();
        final Resource totCap = rescCalc.multiplyAndNormalizeDown(this.capScheduler.getClusterResource(), planQueue.getAbsoluteCapacity(), minAllocation);
        final Plan plan = new InMemoryPlan(this.capScheduler.getRootQueueMetrics(), adPolicy, this.getAgent(planQueuePath), totCap, this.planStepSize, rescCalc, minAllocation, this.capScheduler.getMaximumResourceCapability(), planQueueName, this.getReplanner(planQueuePath), this.capScheduler.getConfiguration().getMoveOnExpiry(planQueuePath));
        CapacityReservationSystem.LOG.info("Intialized plan {0} based on reservable queue {1}", plan.toString(), planQueueName);
        return plan;
    }
    
    @Override
    protected Planner getReplanner(final String planQueueName) {
        final CapacitySchedulerConfiguration capSchedulerConfig = this.capScheduler.getConfiguration();
        final String plannerClassName = capSchedulerConfig.getReplanner(planQueueName);
        CapacityReservationSystem.LOG.info("Using Replanner: " + plannerClassName + " for queue: " + planQueueName);
        try {
            final Class<?> plannerClazz = capSchedulerConfig.getClassByName(plannerClassName);
            if (Planner.class.isAssignableFrom(plannerClazz)) {
                final Planner planner = ReflectionUtils.newInstance(plannerClazz, this.conf);
                planner.init(planQueueName, capSchedulerConfig);
                return planner;
            }
            throw new YarnRuntimeException("Class: " + plannerClazz + " not instance of " + Planner.class.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException("Could not instantiate Planner: " + plannerClassName + " for queue: " + planQueueName, e);
        }
    }
    
    @Override
    protected ReservationAgent getAgent(final String queueName) {
        final CapacitySchedulerConfiguration capSchedulerConfig = this.capScheduler.getConfiguration();
        final String agentClassName = capSchedulerConfig.getReservationAgent(queueName);
        CapacityReservationSystem.LOG.info("Using Agent: " + agentClassName + " for queue: " + queueName);
        try {
            final Class<?> agentClazz = capSchedulerConfig.getClassByName(agentClassName);
            if (ReservationAgent.class.isAssignableFrom(agentClazz)) {
                return ReflectionUtils.newInstance(agentClazz, this.conf);
            }
            throw new YarnRuntimeException("Class: " + agentClassName + " not instance of " + ReservationAgent.class.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException("Could not instantiate Agent: " + agentClassName + " for queue: " + queueName, e);
        }
    }
    
    @Override
    protected SharingPolicy getAdmissionPolicy(final String queueName) {
        final CapacitySchedulerConfiguration capSchedulerConfig = this.capScheduler.getConfiguration();
        final String admissionPolicyClassName = capSchedulerConfig.getReservationAdmissionPolicy(queueName);
        CapacityReservationSystem.LOG.info("Using AdmissionPolicy: " + admissionPolicyClassName + " for queue: " + queueName);
        try {
            final Class<?> admissionPolicyClazz = capSchedulerConfig.getClassByName(admissionPolicyClassName);
            if (SharingPolicy.class.isAssignableFrom(admissionPolicyClazz)) {
                return ReflectionUtils.newInstance(admissionPolicyClazz, this.conf);
            }
            throw new YarnRuntimeException("Class: " + admissionPolicyClassName + " not instance of " + SharingPolicy.class.getCanonicalName());
        }
        catch (ClassNotFoundException e) {
            throw new YarnRuntimeException("Could not instantiate AdmissionPolicy: " + admissionPolicyClassName + " for queue: " + queueName, e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(CapacityReservationSystem.class);
    }
}
