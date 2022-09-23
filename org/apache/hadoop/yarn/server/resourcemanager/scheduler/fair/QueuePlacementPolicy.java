// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;

import java.util.Collections;
import java.util.HashMap;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.util.ReflectionUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Groups;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class QueuePlacementPolicy
{
    private static final Map<String, Class<? extends QueuePlacementRule>> ruleClasses;
    private final List<QueuePlacementRule> rules;
    private final Map<FSQueueType, Set<String>> configuredQueues;
    private final Groups groups;
    
    public QueuePlacementPolicy(final List<QueuePlacementRule> rules, final Map<FSQueueType, Set<String>> configuredQueues, final Configuration conf) throws AllocationConfigurationException {
        for (int i = 0; i < rules.size() - 1; ++i) {
            if (rules.get(i).isTerminal()) {
                throw new AllocationConfigurationException("Rules after rule " + i + " in queue placement policy can never be reached");
            }
        }
        if (!rules.get(rules.size() - 1).isTerminal()) {
            throw new AllocationConfigurationException("Could get past last queue placement rule without assigning");
        }
        this.rules = rules;
        this.configuredQueues = configuredQueues;
        this.groups = new Groups(conf);
    }
    
    public static QueuePlacementPolicy fromXml(final Element el, final Map<FSQueueType, Set<String>> configuredQueues, final Configuration conf) throws AllocationConfigurationException {
        final List<QueuePlacementRule> rules = new ArrayList<QueuePlacementRule>();
        final NodeList elements = el.getChildNodes();
        for (int i = 0; i < elements.getLength(); ++i) {
            final Node node = elements.item(i);
            if (node instanceof Element) {
                final QueuePlacementRule rule = createAndInitializeRule(node);
                rules.add(rule);
            }
        }
        return new QueuePlacementPolicy(rules, configuredQueues, conf);
    }
    
    public static QueuePlacementRule createAndInitializeRule(final Node node) throws AllocationConfigurationException {
        final Element element = (Element)node;
        final String ruleName = element.getAttribute("name");
        if ("".equals(ruleName)) {
            throw new AllocationConfigurationException("No name provided for a rule element");
        }
        final Class<? extends QueuePlacementRule> clazz = QueuePlacementPolicy.ruleClasses.get(ruleName);
        if (clazz == null) {
            throw new AllocationConfigurationException("No rule class found for " + ruleName);
        }
        final QueuePlacementRule rule = ReflectionUtils.newInstance(clazz, null);
        rule.initializeFromXml(element);
        return rule;
    }
    
    public static QueuePlacementPolicy fromConfiguration(final Configuration conf, final Map<FSQueueType, Set<String>> configuredQueues) {
        final boolean create = conf.getBoolean("yarn.scheduler.fair.allow-undeclared-pools", true);
        final boolean userAsDefaultQueue = conf.getBoolean("yarn.scheduler.fair.user-as-default-queue", true);
        final List<QueuePlacementRule> rules = new ArrayList<QueuePlacementRule>();
        rules.add(new QueuePlacementRule.Specified().initialize(create, null));
        if (userAsDefaultQueue) {
            rules.add(new QueuePlacementRule.User().initialize(create, null));
        }
        if (!userAsDefaultQueue || !create) {
            rules.add(new QueuePlacementRule.Default().initialize(true, null));
        }
        try {
            return new QueuePlacementPolicy(rules, configuredQueues, conf);
        }
        catch (AllocationConfigurationException ex) {
            throw new RuntimeException("Should never hit exception when loadingplacement policy from conf", ex);
        }
    }
    
    public String assignAppToQueue(final String requestedQueue, final String user) throws IOException {
        for (final QueuePlacementRule rule : this.rules) {
            final String queue = rule.assignAppToQueue(requestedQueue, user, this.groups, this.configuredQueues);
            if (queue == null || !queue.isEmpty()) {
                return queue;
            }
        }
        throw new IllegalStateException("Should have applied a rule before reaching here");
    }
    
    public List<QueuePlacementRule> getRules() {
        return this.rules;
    }
    
    static {
        final Map<String, Class<? extends QueuePlacementRule>> map = new HashMap<String, Class<? extends QueuePlacementRule>>();
        map.put("user", QueuePlacementRule.User.class);
        map.put("primaryGroup", QueuePlacementRule.PrimaryGroup.class);
        map.put("secondaryGroupExistingQueue", QueuePlacementRule.SecondaryGroupExistingQueue.class);
        map.put("specified", QueuePlacementRule.Specified.class);
        map.put("nestedUserQueue", QueuePlacementRule.NestedUserQueue.class);
        map.put("default", QueuePlacementRule.Default.class);
        map.put("reject", QueuePlacementRule.Reject.class);
        ruleClasses = Collections.unmodifiableMap((Map<? extends String, ? extends Class<? extends QueuePlacementRule>>)map);
    }
}
