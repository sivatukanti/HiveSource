// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.apache.commons.logging.LogFactory;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Map;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class QueueManager
{
    public static final Log LOG;
    public static final String ROOT_QUEUE = "root";
    private final FairScheduler scheduler;
    private final Collection<FSLeafQueue> leafQueues;
    private final Map<String, FSQueue> queues;
    private FSParentQueue rootQueue;
    
    public QueueManager(final FairScheduler scheduler) {
        this.leafQueues = new CopyOnWriteArrayList<FSLeafQueue>();
        this.queues = new HashMap<String, FSQueue>();
        this.scheduler = scheduler;
    }
    
    public FSParentQueue getRootQueue() {
        return this.rootQueue;
    }
    
    public void initialize(final Configuration conf) throws IOException, SAXException, AllocationConfigurationException, ParserConfigurationException {
        this.rootQueue = new FSParentQueue("root", this.scheduler, null);
        this.queues.put(this.rootQueue.getName(), this.rootQueue);
        this.getLeafQueue("default", true);
    }
    
    public FSLeafQueue getLeafQueue(final String name, final boolean create) {
        final FSQueue queue = this.getQueue(name, create, FSQueueType.LEAF);
        if (queue instanceof FSParentQueue) {
            return null;
        }
        return (FSLeafQueue)queue;
    }
    
    public FSParentQueue getParentQueue(final String name, final boolean create) {
        final FSQueue queue = this.getQueue(name, create, FSQueueType.PARENT);
        if (queue instanceof FSLeafQueue) {
            return null;
        }
        return (FSParentQueue)queue;
    }
    
    private FSQueue getQueue(String name, final boolean create, final FSQueueType queueType) {
        name = this.ensureRootPrefix(name);
        synchronized (this.queues) {
            FSQueue queue = this.queues.get(name);
            if (queue == null && create) {
                queue = this.createQueue(name, queueType);
                if (queue != null) {
                    this.rootQueue.recomputeSteadyShares();
                }
            }
            return queue;
        }
    }
    
    private FSQueue createQueue(final String name, final FSQueueType queueType) {
        final List<String> newQueueNames = new ArrayList<String>();
        newQueueNames.add(name);
        int sepIndex = name.length();
        FSParentQueue parent = null;
        while (sepIndex != -1) {
            sepIndex = name.lastIndexOf(46, sepIndex - 1);
            String curName = null;
            curName = name.substring(0, sepIndex);
            final FSQueue queue = this.queues.get(curName);
            if (queue == null) {
                newQueueNames.add(curName);
            }
            else {
                if (queue instanceof FSParentQueue) {
                    parent = (FSParentQueue)queue;
                    break;
                }
                return null;
            }
        }
        final AllocationConfiguration queueConf = this.scheduler.getAllocationConfiguration();
        FSLeafQueue leafQueue = null;
        for (int i = newQueueNames.size() - 1; i >= 0; --i) {
            final String queueName = newQueueNames.get(i);
            if (i == 0 && queueType != FSQueueType.PARENT) {
                leafQueue = new FSLeafQueue(name, this.scheduler, parent);
                try {
                    leafQueue.setPolicy(queueConf.getDefaultSchedulingPolicy());
                }
                catch (AllocationConfigurationException ex) {
                    QueueManager.LOG.warn("Failed to set default scheduling policy " + queueConf.getDefaultSchedulingPolicy() + " on new leaf queue.", ex);
                }
                parent.addChildQueue(leafQueue);
                this.queues.put(leafQueue.getName(), leafQueue);
                this.leafQueues.add(leafQueue);
                leafQueue.updatePreemptionVariables();
                return leafQueue;
            }
            final FSParentQueue newParent = new FSParentQueue(queueName, this.scheduler, parent);
            try {
                newParent.setPolicy(queueConf.getDefaultSchedulingPolicy());
            }
            catch (AllocationConfigurationException ex2) {
                QueueManager.LOG.warn("Failed to set default scheduling policy " + queueConf.getDefaultSchedulingPolicy() + " on new parent queue.", ex2);
            }
            parent.addChildQueue(newParent);
            this.queues.put(newParent.getName(), newParent);
            newParent.updatePreemptionVariables();
            parent = newParent;
        }
        return parent;
    }
    
    private boolean removeEmptyIncompatibleQueues(String queueToCreate, final FSQueueType queueType) {
        queueToCreate = this.ensureRootPrefix(queueToCreate);
        if (queueToCreate.equals("root") || queueToCreate.startsWith("root.default.")) {
            return false;
        }
        final FSQueue queue = this.queues.get(queueToCreate);
        if (queue == null) {
            int sepIndex;
            String prefixString;
            FSQueue prefixQueue;
            for (sepIndex = queueToCreate.length(), sepIndex = queueToCreate.lastIndexOf(46, sepIndex - 1); sepIndex != -1; sepIndex = queueToCreate.lastIndexOf(46, sepIndex - 1)) {
                prefixString = queueToCreate.substring(0, sepIndex);
                prefixQueue = this.queues.get(prefixString);
                if (prefixQueue != null && prefixQueue instanceof FSLeafQueue) {
                    return this.removeQueueIfEmpty(prefixQueue);
                }
            }
            return true;
        }
        if (queue instanceof FSLeafQueue) {
            return queueType == FSQueueType.LEAF || this.removeQueueIfEmpty(queue);
        }
        return queueType == FSQueueType.PARENT || this.removeQueueIfEmpty(queue);
    }
    
    private boolean removeQueueIfEmpty(final FSQueue queue) {
        if (this.isEmpty(queue)) {
            this.removeQueue(queue);
            return true;
        }
        return false;
    }
    
    private void removeQueue(final FSQueue queue) {
        if (queue instanceof FSLeafQueue) {
            this.leafQueues.remove(queue);
        }
        else {
            final List<FSQueue> childQueues = queue.getChildQueues();
            while (!childQueues.isEmpty()) {
                this.removeQueue(childQueues.get(0));
            }
        }
        this.queues.remove(queue.getName());
        queue.getParent().getChildQueues().remove(queue);
    }
    
    protected boolean isEmpty(final FSQueue queue) {
        if (queue instanceof FSLeafQueue) {
            final FSLeafQueue leafQueue = (FSLeafQueue)queue;
            return queue.getNumRunnableApps() == 0 && leafQueue.getNonRunnableAppSchedulables().isEmpty();
        }
        for (final FSQueue child : queue.getChildQueues()) {
            if (!this.isEmpty(child)) {
                return false;
            }
        }
        return true;
    }
    
    public FSQueue getQueue(String name) {
        name = this.ensureRootPrefix(name);
        synchronized (this.queues) {
            return this.queues.get(name);
        }
    }
    
    public boolean exists(String name) {
        name = this.ensureRootPrefix(name);
        synchronized (this.queues) {
            return this.queues.containsKey(name);
        }
    }
    
    public Collection<FSLeafQueue> getLeafQueues() {
        synchronized (this.queues) {
            return this.leafQueues;
        }
    }
    
    public Collection<FSQueue> getQueues() {
        return this.queues.values();
    }
    
    private String ensureRootPrefix(String name) {
        if (!name.startsWith("root.") && !name.equals("root")) {
            name = "root." + name;
        }
        return name;
    }
    
    public void updateAllocationConfiguration(final AllocationConfiguration queueConf) {
        for (final String name : queueConf.getConfiguredQueues().get(FSQueueType.LEAF)) {
            if (this.removeEmptyIncompatibleQueues(name, FSQueueType.LEAF)) {
                this.getLeafQueue(name, true);
            }
        }
        for (final String name : queueConf.getConfiguredQueues().get(FSQueueType.PARENT)) {
            if (this.removeEmptyIncompatibleQueues(name, FSQueueType.PARENT)) {
                this.getParentQueue(name, true);
            }
        }
        for (final FSQueue queue : this.queues.values()) {
            final FSQueueMetrics queueMetrics = queue.getMetrics();
            queueMetrics.setMinShare(queue.getMinShare());
            queueMetrics.setMaxShare(queue.getMaxShare());
            try {
                final SchedulingPolicy policy = queueConf.getSchedulingPolicy(queue.getName());
                policy.initialize(this.scheduler.getClusterResource());
                queue.setPolicy(policy);
            }
            catch (AllocationConfigurationException ex) {
                QueueManager.LOG.warn("Cannot apply configured scheduling policy to queue " + queue.getName(), ex);
            }
        }
        this.rootQueue.recomputeSteadyShares();
        this.rootQueue.updatePreemptionVariables();
    }
    
    static {
        LOG = LogFactory.getLog(QueueManager.class.getName());
    }
}
