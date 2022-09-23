// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.server.utils.Lock;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.util.resource.ResourceCalculator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.logging.Log;

class CSQueueUtils
{
    private static final Log LOG;
    static final float EPSILON = 1.0E-4f;
    
    public static void checkMaxCapacity(final String queueName, final float capacity, final float maximumCapacity) {
        if (maximumCapacity < 0.0f || maximumCapacity > 1.0f) {
            throw new IllegalArgumentException("Illegal value  of maximumCapacity " + maximumCapacity + " used in call to setMaxCapacity for queue " + queueName);
        }
    }
    
    public static void checkAbsoluteCapacity(final String queueName, final float absCapacity, final float absMaxCapacity) {
        if (absMaxCapacity < absCapacity - 1.0E-4f) {
            throw new IllegalArgumentException("Illegal call to setMaxCapacity. Queue '" + queueName + "' has " + "an absolute capacity (" + absCapacity + ") greater than " + "its absolute maximumCapacity (" + absMaxCapacity + ")");
        }
    }
    
    public static void checkAbsoluteCapacitiesByLabel(final String queueName, final Map<String, Float> absCapacities, final Map<String, Float> absMaximumCapacities) {
        for (final Map.Entry<String, Float> entry : absCapacities.entrySet()) {
            final String label = entry.getKey();
            final float absCapacity = entry.getValue();
            final float absMaxCapacity = absMaximumCapacities.get(label);
            if (absMaxCapacity < absCapacity - 1.0E-4f) {
                throw new IllegalArgumentException("Illegal call to setMaxCapacity. Queue '" + queueName + "' has " + "an absolute capacity (" + absCapacity + ") greater than " + "its absolute maximumCapacity (" + absMaxCapacity + ") of label=" + label);
            }
        }
    }
    
    public static float computeAbsoluteMaximumCapacity(final float maximumCapacity, final CSQueue parent) {
        final float parentAbsMaxCapacity = (parent == null) ? 1.0f : parent.getAbsoluteMaximumCapacity();
        return parentAbsMaxCapacity * maximumCapacity;
    }
    
    public static Map<String, Float> computeAbsoluteCapacityByNodeLabels(final Map<String, Float> nodeLabelToCapacities, final CSQueue parent) {
        if (parent == null) {
            return nodeLabelToCapacities;
        }
        final Map<String, Float> absoluteCapacityByNodeLabels = new HashMap<String, Float>();
        for (final Map.Entry<String, Float> entry : nodeLabelToCapacities.entrySet()) {
            final String label = entry.getKey();
            final float capacity = entry.getValue();
            absoluteCapacityByNodeLabels.put(label, capacity * parent.getAbsoluteCapacityByNodeLabel(label));
        }
        return absoluteCapacityByNodeLabels;
    }
    
    public static Map<String, Float> computeAbsoluteMaxCapacityByNodeLabels(final Map<String, Float> maximumNodeLabelToCapacities, final CSQueue parent) {
        if (parent == null) {
            return maximumNodeLabelToCapacities;
        }
        final Map<String, Float> absoluteMaxCapacityByNodeLabels = new HashMap<String, Float>();
        for (final Map.Entry<String, Float> entry : maximumNodeLabelToCapacities.entrySet()) {
            final String label = entry.getKey();
            final float maxCapacity = entry.getValue();
            absoluteMaxCapacityByNodeLabels.put(label, maxCapacity * parent.getAbsoluteMaximumCapacityByNodeLabel(label));
        }
        return absoluteMaxCapacityByNodeLabels;
    }
    
    public static int computeMaxActiveApplications(final ResourceCalculator calculator, final Resource clusterResource, final Resource minimumAllocation, final float maxAMResourcePercent, final float absoluteMaxCapacity) {
        return Math.max((int)Math.ceil(Resources.ratio(calculator, clusterResource, minimumAllocation) * maxAMResourcePercent * absoluteMaxCapacity), 1);
    }
    
    public static int computeMaxActiveApplicationsPerUser(final int maxActiveApplications, final int userLimit, final float userLimitFactor) {
        return Math.max((int)Math.ceil(maxActiveApplications * (userLimit / 100.0f) * userLimitFactor), 1);
    }
    
    @Lock({ CSQueue.class })
    public static void updateQueueStatistics(final ResourceCalculator calculator, final CSQueue childQueue, final CSQueue parentQueue, final Resource clusterResource, final Resource minimumAllocation) {
        Resource queueLimit = Resources.none();
        final Resource usedResources = childQueue.getUsedResources();
        float absoluteUsedCapacity = 0.0f;
        float usedCapacity = 0.0f;
        if (Resources.greaterThan(calculator, clusterResource, clusterResource, Resources.none())) {
            queueLimit = Resources.multiply(clusterResource, childQueue.getAbsoluteCapacity());
            absoluteUsedCapacity = Resources.divide(calculator, clusterResource, usedResources, clusterResource);
            usedCapacity = (Resources.equals(queueLimit, Resources.none()) ? 0.0f : Resources.divide(calculator, clusterResource, usedResources, queueLimit));
        }
        childQueue.setUsedCapacity(usedCapacity);
        childQueue.setAbsoluteUsedCapacity(absoluteUsedCapacity);
        final Resource available = Resources.subtract(queueLimit, usedResources);
        childQueue.getMetrics().setAvailableResourcesToQueue(Resources.max(calculator, clusterResource, available, Resources.none()));
    }
    
    public static float getAbsoluteMaxAvailCapacity(final ResourceCalculator resourceCalculator, final Resource clusterResource, final CSQueue queue) {
        final CSQueue parent = queue.getParent();
        if (parent == null) {
            return queue.getAbsoluteMaximumCapacity();
        }
        final float parentMaxAvail = getAbsoluteMaxAvailCapacity(resourceCalculator, clusterResource, parent);
        final Resource parentResource = Resources.multiply(clusterResource, parentMaxAvail);
        if (Resources.isInvalidDivisor(resourceCalculator, parentResource)) {
            return 0.0f;
        }
        final float siblingUsedCapacity = Resources.ratio(resourceCalculator, Resources.subtract(parent.getUsedResources(), queue.getUsedResources()), parentResource);
        final float maxAvail = Math.min(queue.getMaximumCapacity(), 1.0f - siblingUsedCapacity);
        float absoluteMaxAvail = maxAvail * parentMaxAvail;
        if (CSQueueUtils.LOG.isDebugEnabled()) {
            CSQueueUtils.LOG.debug("qpath " + queue.getQueuePath());
            CSQueueUtils.LOG.debug("parentMaxAvail " + parentMaxAvail);
            CSQueueUtils.LOG.debug("siblingUsedCapacity " + siblingUsedCapacity);
            CSQueueUtils.LOG.debug("getAbsoluteMaximumCapacity " + queue.getAbsoluteMaximumCapacity());
            CSQueueUtils.LOG.debug("maxAvail " + maxAvail);
            CSQueueUtils.LOG.debug("absoluteMaxAvail " + absoluteMaxAvail);
        }
        if (absoluteMaxAvail < 0.0f) {
            absoluteMaxAvail = 0.0f;
        }
        else if (absoluteMaxAvail > 1.0f) {
            absoluteMaxAvail = 1.0f;
        }
        return absoluteMaxAvail;
    }
    
    static {
        LOG = LogFactory.getLog(CSQueueUtils.class);
    }
}
