// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import org.w3c.dom.NodeList;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.util.HashMap;
import org.w3c.dom.Element;
import java.io.IOException;
import java.util.Set;
import org.apache.hadoop.security.Groups;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public abstract class QueuePlacementRule
{
    protected boolean create;
    
    public QueuePlacementRule initialize(final boolean create, final Map<String, String> args) {
        this.create = create;
        return this;
    }
    
    public String assignAppToQueue(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) throws IOException {
        final String queue = this.getQueueForApp(requestedQueue, user, groups, configuredQueues);
        if (this.create || configuredQueues.get(FSQueueType.LEAF).contains(queue) || configuredQueues.get(FSQueueType.PARENT).contains(queue)) {
            return queue;
        }
        return "";
    }
    
    public void initializeFromXml(final Element el) throws AllocationConfigurationException {
        boolean create = true;
        final NamedNodeMap attributes = el.getAttributes();
        final Map<String, String> args = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); ++i) {
            final Node node = attributes.item(i);
            final String key = node.getNodeName();
            final String value = node.getNodeValue();
            if (key.equals("create")) {
                create = Boolean.parseBoolean(value);
            }
            else {
                args.put(key, value);
            }
        }
        this.initialize(create, args);
    }
    
    public abstract boolean isTerminal();
    
    protected abstract String getQueueForApp(final String p0, final String p1, final Groups p2, final Map<FSQueueType, Set<String>> p3) throws IOException;
    
    public static class User extends QueuePlacementRule
    {
        @Override
        protected String getQueueForApp(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) {
            return "root." + user;
        }
        
        @Override
        public boolean isTerminal() {
            return this.create;
        }
    }
    
    public static class PrimaryGroup extends QueuePlacementRule
    {
        @Override
        protected String getQueueForApp(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) throws IOException {
            return "root." + groups.getGroups(user).get(0);
        }
        
        @Override
        public boolean isTerminal() {
            return this.create;
        }
    }
    
    public static class SecondaryGroupExistingQueue extends QueuePlacementRule
    {
        @Override
        protected String getQueueForApp(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) throws IOException {
            final List<String> groupNames = groups.getGroups(user);
            for (int i = 1; i < groupNames.size(); ++i) {
                final String group = groupNames.get(i);
                if (configuredQueues.get(FSQueueType.LEAF).contains("root." + group) || configuredQueues.get(FSQueueType.PARENT).contains("root." + group)) {
                    return "root." + groupNames.get(i);
                }
            }
            return "";
        }
        
        @Override
        public boolean isTerminal() {
            return false;
        }
    }
    
    public static class NestedUserQueue extends QueuePlacementRule
    {
        @VisibleForTesting
        QueuePlacementRule nestedRule;
        
        @Override
        public void initializeFromXml(final Element el) throws AllocationConfigurationException {
            final NodeList elements = el.getChildNodes();
            for (int i = 0; i < elements.getLength(); ++i) {
                final Node node = elements.item(i);
                if (node instanceof Element) {
                    final Element element = (Element)node;
                    if ("rule".equals(element.getTagName())) {
                        final QueuePlacementRule rule = QueuePlacementPolicy.createAndInitializeRule(node);
                        if (rule == null) {
                            throw new AllocationConfigurationException("Unable to create nested rule in nestedUserQueue rule");
                        }
                        this.nestedRule = rule;
                        break;
                    }
                }
            }
            if (this.nestedRule == null) {
                throw new AllocationConfigurationException("No nested rule specified in <nestedUserQueue> rule");
            }
            super.initializeFromXml(el);
        }
        
        @Override
        protected String getQueueForApp(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) throws IOException {
            String queueName = this.nestedRule.assignAppToQueue(requestedQueue, user, groups, configuredQueues);
            if (queueName == null || queueName.length() == 0) {
                return queueName;
            }
            if (!queueName.startsWith("root.")) {
                queueName = "root." + queueName;
            }
            if (configuredQueues.get(FSQueueType.LEAF).contains(queueName)) {
                return "";
            }
            return queueName + "." + user;
        }
        
        @Override
        public boolean isTerminal() {
            return false;
        }
    }
    
    public static class Specified extends QueuePlacementRule
    {
        @Override
        protected String getQueueForApp(String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) {
            if (requestedQueue.equals("default")) {
                return "";
            }
            if (!requestedQueue.startsWith("root.")) {
                requestedQueue = "root." + requestedQueue;
            }
            return requestedQueue;
        }
        
        @Override
        public boolean isTerminal() {
            return false;
        }
    }
    
    public static class Default extends QueuePlacementRule
    {
        @VisibleForTesting
        String defaultQueueName;
        
        @Override
        public QueuePlacementRule initialize(final boolean create, final Map<String, String> args) {
            if (this.defaultQueueName == null) {
                this.defaultQueueName = "root.default";
            }
            return super.initialize(create, args);
        }
        
        @Override
        public void initializeFromXml(final Element el) throws AllocationConfigurationException {
            this.defaultQueueName = el.getAttribute("queue");
            if (this.defaultQueueName != null && !this.defaultQueueName.isEmpty()) {
                if (!this.defaultQueueName.startsWith("root.")) {
                    this.defaultQueueName = "root." + this.defaultQueueName;
                }
            }
            else {
                this.defaultQueueName = "root.default";
            }
            super.initializeFromXml(el);
        }
        
        @Override
        protected String getQueueForApp(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) {
            return this.defaultQueueName;
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
    }
    
    public static class Reject extends QueuePlacementRule
    {
        @Override
        public String assignAppToQueue(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) {
            return null;
        }
        
        @Override
        protected String getQueueForApp(final String requestedQueue, final String user, final Groups groups, final Map<FSQueueType, Set<String>> configuredQueues) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
    }
}
