// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerNode;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FSSchedulerNode extends SchedulerNode
{
    private static final Log LOG;
    private FSAppAttempt reservedAppSchedulable;
    
    public FSSchedulerNode(final RMNode node, final boolean usePortForNodeName) {
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
                throw new IllegalStateException("Trying to reserve container " + container + " for application " + application.getApplicationId() + " when currently" + " reserved container " + reservedContainer + " on node " + this);
            }
            FSSchedulerNode.LOG.info("Updated reserved container " + container.getContainer().getId() + " on node " + this + " for application " + application);
        }
        else {
            FSSchedulerNode.LOG.info("Reserved container " + container.getContainer().getId() + " on node " + this + " for application " + application);
        }
        this.setReservedContainer(container);
        this.reservedAppSchedulable = (FSAppAttempt)application;
    }
    
    @Override
    public synchronized void unreserveResource(final SchedulerApplicationAttempt application) {
        final ApplicationAttemptId reservedApplication = this.getReservedContainer().getContainer().getId().getApplicationAttemptId();
        if (!reservedApplication.equals(application.getApplicationAttemptId())) {
            throw new IllegalStateException("Trying to unreserve  for application " + application.getApplicationId() + " when currently reserved " + " for application " + reservedApplication.getApplicationId() + " on node " + this);
        }
        this.setReservedContainer(null);
        this.reservedAppSchedulable = null;
    }
    
    public synchronized FSAppAttempt getReservedAppSchedulable() {
        return this.reservedAppSchedulable;
    }
    
    static {
        LOG = LogFactory.getLog(FSSchedulerNode.class);
    }
}
