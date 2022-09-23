// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;

public class FiCaSchedulerNode extends SchedulerNode
{
    private static final Log LOG;
    
    public FiCaSchedulerNode(final RMNode node, final boolean usePortForNodeName) {
        super(node, usePortForNodeName);
    }
    
    @Override
    public synchronized void reserveResource(final SchedulerApplicationAttempt application, final Priority priority, final RMContainer container) {
        final RMContainer reservedContainer = this.getReservedContainer();
        if (reservedContainer != null) {
            if (!container.getContainer().getNodeId().equals(this.getNodeID())) {
                throw new IllegalStateException("Trying to reserve container " + container + " on node " + container.getReservedNode() + " when currently" + " reserved resource " + reservedContainer + " on node " + reservedContainer.getReservedNode());
            }
            if (!reservedContainer.getContainer().getId().getApplicationAttemptId().equals(container.getContainer().getId().getApplicationAttemptId())) {
                throw new IllegalStateException("Trying to reserve container " + container + " for application " + application.getApplicationAttemptId() + " when currently" + " reserved container " + reservedContainer + " on node " + this);
            }
            if (FiCaSchedulerNode.LOG.isDebugEnabled()) {
                FiCaSchedulerNode.LOG.debug("Updated reserved container " + container.getContainer().getId() + " on node " + this + " for application attempt " + application.getApplicationAttemptId());
            }
        }
        else if (FiCaSchedulerNode.LOG.isDebugEnabled()) {
            FiCaSchedulerNode.LOG.debug("Reserved container " + container.getContainer().getId() + " on node " + this + " for application attempt " + application.getApplicationAttemptId());
        }
        this.setReservedContainer(container);
    }
    
    @Override
    public synchronized void unreserveResource(final SchedulerApplicationAttempt application) {
        if (this.getReservedContainer() != null && this.getReservedContainer().getContainer() != null && this.getReservedContainer().getContainer().getId() != null && this.getReservedContainer().getContainer().getId().getApplicationAttemptId() != null) {
            final ApplicationAttemptId reservedApplication = this.getReservedContainer().getContainer().getId().getApplicationAttemptId();
            if (!reservedApplication.equals(application.getApplicationAttemptId())) {
                throw new IllegalStateException("Trying to unreserve  for application " + application.getApplicationAttemptId() + " when currently reserved " + " for application " + reservedApplication.getApplicationId() + " on node " + this);
            }
        }
        this.setReservedContainer(null);
    }
    
    static {
        LOG = LogFactory.getLog(FiCaSchedulerNode.class);
    }
}
