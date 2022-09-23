// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels;

import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.apache.hadoop.yarn.api.records.Resource;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.logging.LogFactory;
import java.util.Collections;
import java.util.HashMap;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import java.util.Iterator;
import org.apache.commons.lang.StringUtils;
import java.util.Collection;
import java.util.HashSet;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.nodelabels.event.UpdateNodeToLabelsMappingsEvent;
import org.apache.hadoop.yarn.nodelabels.event.RemoveClusterNodeLabels;
import org.apache.hadoop.yarn.nodelabels.event.StoreNewClusterNodeLabels;
import org.apache.hadoop.yarn.nodelabels.event.NodeLabelsStoreEventType;
import org.apache.hadoop.yarn.nodelabels.event.NodeLabelsStoreEvent;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.event.Dispatcher;
import java.util.regex.Pattern;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.hadoop.service.AbstractService;

public class CommonNodeLabelsManager extends AbstractService
{
    protected static final Log LOG;
    private static final int MAX_LABEL_LENGTH = 255;
    public static final Set<String> EMPTY_STRING_SET;
    public static final String ANY = "*";
    public static final Set<String> ACCESS_ANY_LABEL_SET;
    private static final Pattern LABEL_PATTERN;
    public static final int WILDCARD_PORT = 0;
    public static final String NO_LABEL = "";
    protected Dispatcher dispatcher;
    protected ConcurrentMap<String, Label> labelCollections;
    protected ConcurrentMap<String, Host> nodeCollections;
    protected final ReentrantReadWriteLock.ReadLock readLock;
    protected final ReentrantReadWriteLock.WriteLock writeLock;
    protected NodeLabelsStore store;
    
    protected void handleStoreEvent(final NodeLabelsStoreEvent event) {
        try {
            switch (event.getType()) {
                case ADD_LABELS: {
                    final StoreNewClusterNodeLabels storeNewClusterNodeLabelsEvent = (StoreNewClusterNodeLabels)event;
                    this.store.storeNewClusterNodeLabels(storeNewClusterNodeLabelsEvent.getLabels());
                    break;
                }
                case REMOVE_LABELS: {
                    final RemoveClusterNodeLabels removeClusterNodeLabelsEvent = (RemoveClusterNodeLabels)event;
                    this.store.removeClusterNodeLabels(removeClusterNodeLabelsEvent.getLabels());
                    break;
                }
                case STORE_NODE_TO_LABELS: {
                    final UpdateNodeToLabelsMappingsEvent updateNodeToLabelsMappingsEvent = (UpdateNodeToLabelsMappingsEvent)event;
                    this.store.updateNodeToLabelsMappings(updateNodeToLabelsMappingsEvent.getNodeToLabels());
                    break;
                }
            }
        }
        catch (IOException e) {
            CommonNodeLabelsManager.LOG.error("Failed to store label modification to storage");
            throw new YarnRuntimeException(e);
        }
    }
    
    public CommonNodeLabelsManager() {
        super(CommonNodeLabelsManager.class.getName());
        this.labelCollections = new ConcurrentHashMap<String, Label>();
        this.nodeCollections = new ConcurrentHashMap<String, Host>();
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();
    }
    
    protected void initDispatcher(final Configuration conf) {
        this.dispatcher = new AsyncDispatcher();
        final AsyncDispatcher asyncDispatcher = (AsyncDispatcher)this.dispatcher;
        asyncDispatcher.init(conf);
        asyncDispatcher.setDrainEventsOnStop();
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.initNodeLabelStore(conf);
        this.labelCollections.put("", new Label());
    }
    
    protected void initNodeLabelStore(final Configuration conf) throws Exception {
        (this.store = new FileSystemNodeLabelsStore(this)).init(conf);
        this.store.recover();
    }
    
    protected void startDispatcher() {
        final AsyncDispatcher asyncDispatcher = (AsyncDispatcher)this.dispatcher;
        asyncDispatcher.start();
    }
    
    @Override
    protected void serviceStart() throws Exception {
        this.initDispatcher(this.getConfig());
        if (null != this.dispatcher) {
            this.dispatcher.register(NodeLabelsStoreEventType.class, new ForwardingEventHandler());
        }
        this.startDispatcher();
    }
    
    protected void stopDispatcher() {
        final AsyncDispatcher asyncDispatcher = (AsyncDispatcher)this.dispatcher;
        asyncDispatcher.stop();
    }
    
    @Override
    protected void serviceStop() throws Exception {
        this.stopDispatcher();
        if (null != this.store) {
            this.store.close();
        }
    }
    
    public void addToCluserNodeLabels(Set<String> labels) throws IOException {
        if (null == labels || labels.isEmpty()) {
            return;
        }
        final Set<String> newLabels = new HashSet<String>();
        labels = this.normalizeLabels(labels);
        for (final String label : labels) {
            this.checkAndThrowLabelName(label);
        }
        for (final String label : labels) {
            if (this.labelCollections.get(label) == null) {
                this.labelCollections.put(label, new Label());
                newLabels.add(label);
            }
        }
        if (null != this.dispatcher && !newLabels.isEmpty()) {
            this.dispatcher.getEventHandler().handle(new StoreNewClusterNodeLabels(newLabels));
        }
        CommonNodeLabelsManager.LOG.info("Add labels: [" + StringUtils.join(labels.iterator(), ",") + "]");
    }
    
    protected void checkAddLabelsToNode(final Map<NodeId, Set<String>> addedLabelsToNode) throws IOException {
        if (null == addedLabelsToNode || addedLabelsToNode.isEmpty()) {
            return;
        }
        final Set<String> knownLabels = this.labelCollections.keySet();
        for (final Map.Entry<NodeId, Set<String>> entry : addedLabelsToNode.entrySet()) {
            if (!knownLabels.containsAll(entry.getValue())) {
                final String msg = "Not all labels being added contained by known label collections, please check, added labels=[" + StringUtils.join(entry.getValue(), ",") + "]";
                CommonNodeLabelsManager.LOG.error(msg);
                throw new IOException(msg);
            }
        }
    }
    
    protected void internalAddLabelsToNode(final Map<NodeId, Set<String>> addedLabelsToNode) throws IOException {
        final Map<NodeId, Set<String>> newNMToLabels = new HashMap<NodeId, Set<String>>();
        for (final Map.Entry<NodeId, Set<String>> entry : addedLabelsToNode.entrySet()) {
            final NodeId nodeId = entry.getKey();
            final Set<String> labels = entry.getValue();
            this.createHostIfNonExisted(nodeId.getHost());
            if (nodeId.getPort() == 0) {
                final Host host = this.nodeCollections.get(nodeId.getHost());
                host.labels.addAll(labels);
                newNMToLabels.put(nodeId, host.labels);
            }
            else {
                this.createNodeIfNonExisted(nodeId);
                final Node nm = this.getNMInNodeSet(nodeId);
                if (nm.labels == null) {
                    nm.labels = new HashSet<String>();
                }
                nm.labels.addAll(labels);
                newNMToLabels.put(nodeId, nm.labels);
            }
        }
        if (null != this.dispatcher) {
            this.dispatcher.getEventHandler().handle(new UpdateNodeToLabelsMappingsEvent(newNMToLabels));
        }
        CommonNodeLabelsManager.LOG.info("addLabelsToNode:");
        for (final Map.Entry<NodeId, Set<String>> entry : newNMToLabels.entrySet()) {
            CommonNodeLabelsManager.LOG.info("  NM=" + entry.getKey() + ", labels=[" + StringUtils.join(entry.getValue().iterator(), ",") + "]");
        }
    }
    
    public void addLabelsToNode(Map<NodeId, Set<String>> addedLabelsToNode) throws IOException {
        addedLabelsToNode = this.normalizeNodeIdToLabels(addedLabelsToNode);
        this.checkAddLabelsToNode(addedLabelsToNode);
        this.internalAddLabelsToNode(addedLabelsToNode);
    }
    
    protected void checkRemoveFromClusterNodeLabels(final Collection<String> labelsToRemove) throws IOException {
        if (null == labelsToRemove || labelsToRemove.isEmpty()) {
            return;
        }
        for (String label : labelsToRemove) {
            label = this.normalizeLabel(label);
            if (label == null || label.isEmpty()) {
                throw new IOException("Label to be removed is null or empty");
            }
            if (!this.labelCollections.containsKey(label)) {
                throw new IOException("Node label=" + label + " to be removed doesn't existed in cluster " + "node labels collection.");
            }
        }
    }
    
    protected void internalRemoveFromClusterNodeLabels(final Collection<String> labelsToRemove) {
        for (final String nodeName : this.nodeCollections.keySet()) {
            final Host host = this.nodeCollections.get(nodeName);
            if (null != host) {
                host.labels.removeAll(labelsToRemove);
                for (final Node nm : host.nms.values()) {
                    if (nm.labels != null) {
                        nm.labels.removeAll(labelsToRemove);
                    }
                }
            }
        }
        for (final String label : labelsToRemove) {
            this.labelCollections.remove(label);
        }
        if (null != this.dispatcher) {
            this.dispatcher.getEventHandler().handle(new RemoveClusterNodeLabels(labelsToRemove));
        }
        CommonNodeLabelsManager.LOG.info("Remove labels: [" + StringUtils.join(labelsToRemove.iterator(), ",") + "]");
    }
    
    public void removeFromClusterNodeLabels(Collection<String> labelsToRemove) throws IOException {
        labelsToRemove = this.normalizeLabels(labelsToRemove);
        this.checkRemoveFromClusterNodeLabels(labelsToRemove);
        this.internalRemoveFromClusterNodeLabels(labelsToRemove);
    }
    
    protected void checkRemoveLabelsFromNode(final Map<NodeId, Set<String>> removeLabelsFromNode) throws IOException {
        final Set<String> knownLabels = this.labelCollections.keySet();
        for (final Map.Entry<NodeId, Set<String>> entry : removeLabelsFromNode.entrySet()) {
            final NodeId nodeId = entry.getKey();
            final Set<String> labels = entry.getValue();
            if (!knownLabels.containsAll(labels)) {
                final String msg = "Not all labels being removed contained by known label collections, please check, removed labels=[" + StringUtils.join(labels, ",") + "]";
                CommonNodeLabelsManager.LOG.error(msg);
                throw new IOException(msg);
            }
            Set<String> originalLabels = null;
            boolean nodeExisted = false;
            if (0 != nodeId.getPort()) {
                final Node nm = this.getNMInNodeSet(nodeId);
                if (nm != null) {
                    originalLabels = nm.labels;
                    nodeExisted = true;
                }
            }
            else {
                final Host host = this.nodeCollections.get(nodeId.getHost());
                if (null != host) {
                    originalLabels = host.labels;
                    nodeExisted = true;
                }
            }
            if (!nodeExisted) {
                final String msg2 = "Try to remove labels from NM=" + nodeId + ", but the NM doesn't existed";
                CommonNodeLabelsManager.LOG.error(msg2);
                throw new IOException(msg2);
            }
            if (labels.isEmpty()) {
                continue;
            }
            if (originalLabels == null || !originalLabels.containsAll(labels)) {
                final String msg2 = "Try to remove labels = [" + StringUtils.join(labels, ",") + "], but not all labels contained by NM=" + nodeId;
                CommonNodeLabelsManager.LOG.error(msg2);
                throw new IOException(msg2);
            }
        }
    }
    
    protected void internalRemoveLabelsFromNode(final Map<NodeId, Set<String>> removeLabelsFromNode) {
        final Map<NodeId, Set<String>> newNMToLabels = new HashMap<NodeId, Set<String>>();
        for (final Map.Entry<NodeId, Set<String>> entry : removeLabelsFromNode.entrySet()) {
            final NodeId nodeId = entry.getKey();
            final Set<String> labels = entry.getValue();
            if (nodeId.getPort() == 0) {
                final Host host = this.nodeCollections.get(nodeId.getHost());
                host.labels.removeAll(labels);
                newNMToLabels.put(nodeId, host.labels);
            }
            else {
                final Node nm = this.getNMInNodeSet(nodeId);
                if (nm.labels == null) {
                    continue;
                }
                nm.labels.removeAll(labels);
                newNMToLabels.put(nodeId, nm.labels);
            }
        }
        if (null != this.dispatcher) {
            this.dispatcher.getEventHandler().handle(new UpdateNodeToLabelsMappingsEvent(newNMToLabels));
        }
        CommonNodeLabelsManager.LOG.info("removeLabelsFromNode:");
        for (final Map.Entry<NodeId, Set<String>> entry : newNMToLabels.entrySet()) {
            CommonNodeLabelsManager.LOG.info("  NM=" + entry.getKey() + ", labels=[" + StringUtils.join(entry.getValue().iterator(), ",") + "]");
        }
    }
    
    public void removeLabelsFromNode(Map<NodeId, Set<String>> removeLabelsFromNode) throws IOException {
        removeLabelsFromNode = this.normalizeNodeIdToLabels(removeLabelsFromNode);
        this.checkRemoveLabelsFromNode(removeLabelsFromNode);
        this.internalRemoveLabelsFromNode(removeLabelsFromNode);
    }
    
    protected void checkReplaceLabelsOnNode(final Map<NodeId, Set<String>> replaceLabelsToNode) throws IOException {
        if (null == replaceLabelsToNode || replaceLabelsToNode.isEmpty()) {
            return;
        }
        final Set<String> knownLabels = this.labelCollections.keySet();
        for (final Map.Entry<NodeId, Set<String>> entry : replaceLabelsToNode.entrySet()) {
            if (!knownLabels.containsAll(entry.getValue())) {
                final String msg = "Not all labels being replaced contained by known label collections, please check, new labels=[" + StringUtils.join(entry.getValue(), ",") + "]";
                CommonNodeLabelsManager.LOG.error(msg);
                throw new IOException(msg);
            }
        }
    }
    
    protected void internalReplaceLabelsOnNode(final Map<NodeId, Set<String>> replaceLabelsToNode) throws IOException {
        final Map<NodeId, Set<String>> newNMToLabels = new HashMap<NodeId, Set<String>>();
        for (final Map.Entry<NodeId, Set<String>> entry : replaceLabelsToNode.entrySet()) {
            final NodeId nodeId = entry.getKey();
            final Set<String> labels = entry.getValue();
            this.createHostIfNonExisted(nodeId.getHost());
            if (nodeId.getPort() == 0) {
                final Host host = this.nodeCollections.get(nodeId.getHost());
                host.labels.clear();
                host.labels.addAll(labels);
                newNMToLabels.put(nodeId, host.labels);
            }
            else {
                this.createNodeIfNonExisted(nodeId);
                final Node nm = this.getNMInNodeSet(nodeId);
                if (nm.labels == null) {
                    nm.labels = new HashSet<String>();
                }
                nm.labels.clear();
                nm.labels.addAll(labels);
                newNMToLabels.put(nodeId, nm.labels);
            }
        }
        if (null != this.dispatcher) {
            this.dispatcher.getEventHandler().handle(new UpdateNodeToLabelsMappingsEvent(newNMToLabels));
        }
        CommonNodeLabelsManager.LOG.info("setLabelsToNode:");
        for (final Map.Entry<NodeId, Set<String>> entry : newNMToLabels.entrySet()) {
            CommonNodeLabelsManager.LOG.info("  NM=" + entry.getKey() + ", labels=[" + StringUtils.join(entry.getValue().iterator(), ",") + "]");
        }
    }
    
    public void replaceLabelsOnNode(Map<NodeId, Set<String>> replaceLabelsToNode) throws IOException {
        replaceLabelsToNode = this.normalizeNodeIdToLabels(replaceLabelsToNode);
        this.checkReplaceLabelsOnNode(replaceLabelsToNode);
        this.internalReplaceLabelsOnNode(replaceLabelsToNode);
    }
    
    public Map<NodeId, Set<String>> getNodeLabels() {
        try {
            this.readLock.lock();
            final Map<NodeId, Set<String>> nodeToLabels = new HashMap<NodeId, Set<String>>();
            for (final Map.Entry<String, Host> entry : this.nodeCollections.entrySet()) {
                final String hostName = entry.getKey();
                final Host host = entry.getValue();
                for (final NodeId nodeId : host.nms.keySet()) {
                    final Set<String> nodeLabels = this.getLabelsByNode(nodeId);
                    if (nodeLabels != null) {
                        if (nodeLabels.isEmpty()) {
                            continue;
                        }
                        nodeToLabels.put(nodeId, nodeLabels);
                    }
                }
                if (!host.labels.isEmpty()) {
                    nodeToLabels.put(NodeId.newInstance(hostName, 0), host.labels);
                }
            }
            return Collections.unmodifiableMap((Map<? extends NodeId, ? extends Set<String>>)nodeToLabels);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    public Set<String> getClusterNodeLabels() {
        try {
            this.readLock.lock();
            final Set<String> labels = new HashSet<String>((Collection<? extends String>)this.labelCollections.keySet());
            labels.remove("");
            return Collections.unmodifiableSet((Set<? extends String>)labels);
        }
        finally {
            this.readLock.unlock();
        }
    }
    
    private void checkAndThrowLabelName(String label) throws IOException {
        if (label == null || label.isEmpty() || label.length() > 255) {
            throw new IOException("label added is empty or exceeds 255 character(s)");
        }
        label = label.trim();
        final boolean match = CommonNodeLabelsManager.LABEL_PATTERN.matcher(label).matches();
        if (!match) {
            throw new IOException("label name should only contains {0-9, a-z, A-Z, -, _} and should not started with {-,_}, now it is=" + label);
        }
    }
    
    protected String normalizeLabel(final String label) {
        if (label != null) {
            return label.trim();
        }
        return "";
    }
    
    private Set<String> normalizeLabels(final Collection<String> labels) {
        final Set<String> newLabels = new HashSet<String>();
        for (final String label : labels) {
            newLabels.add(this.normalizeLabel(label));
        }
        return newLabels;
    }
    
    protected Node getNMInNodeSet(final NodeId nodeId) {
        return this.getNMInNodeSet(nodeId, this.nodeCollections);
    }
    
    protected Node getNMInNodeSet(final NodeId nodeId, final Map<String, Host> map) {
        return this.getNMInNodeSet(nodeId, map, false);
    }
    
    protected Node getNMInNodeSet(final NodeId nodeId, final Map<String, Host> map, final boolean checkRunning) {
        final Host host = map.get(nodeId.getHost());
        if (null == host) {
            return null;
        }
        final Node nm = host.nms.get(nodeId);
        if (null == nm) {
            return null;
        }
        if (checkRunning) {
            return nm.running ? nm : null;
        }
        return nm;
    }
    
    protected Set<String> getLabelsByNode(final NodeId nodeId) {
        return this.getLabelsByNode(nodeId, this.nodeCollections);
    }
    
    protected Set<String> getLabelsByNode(final NodeId nodeId, final Map<String, Host> map) {
        final Host host = map.get(nodeId.getHost());
        if (null == host) {
            return CommonNodeLabelsManager.EMPTY_STRING_SET;
        }
        final Node nm = host.nms.get(nodeId);
        if (null != nm && null != nm.labels) {
            return nm.labels;
        }
        return host.labels;
    }
    
    protected void createNodeIfNonExisted(final NodeId nodeId) throws IOException {
        final Host host = this.nodeCollections.get(nodeId.getHost());
        if (null == host) {
            throw new IOException("Should create host before creating node.");
        }
        final Node nm = host.nms.get(nodeId);
        if (null == nm) {
            host.nms.put(nodeId, new Node());
        }
    }
    
    protected void createHostIfNonExisted(final String hostName) {
        Host host = this.nodeCollections.get(hostName);
        if (null == host) {
            host = new Host();
            this.nodeCollections.put(hostName, host);
        }
    }
    
    protected Map<NodeId, Set<String>> normalizeNodeIdToLabels(final Map<NodeId, Set<String>> nodeIdToLabels) {
        final Map<NodeId, Set<String>> newMap = new HashMap<NodeId, Set<String>>();
        for (final Map.Entry<NodeId, Set<String>> entry : nodeIdToLabels.entrySet()) {
            final NodeId id = entry.getKey();
            final Set<String> labels = entry.getValue();
            newMap.put(id, this.normalizeLabels(labels));
        }
        return newMap;
    }
    
    static {
        LOG = LogFactory.getLog(CommonNodeLabelsManager.class);
        EMPTY_STRING_SET = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(0));
        ACCESS_ANY_LABEL_SET = ImmutableSet.of("*");
        LABEL_PATTERN = Pattern.compile("^[0-9a-zA-Z][0-9a-zA-Z-_]*");
    }
    
    protected static class Label
    {
        public Resource resource;
        
        protected Label() {
            this.resource = Resource.newInstance(0, 0);
        }
    }
    
    protected static class Host
    {
        public Set<String> labels;
        public Map<NodeId, Node> nms;
        
        protected Host() {
            this.labels = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
            this.nms = new ConcurrentHashMap<NodeId, Node>();
        }
        
        public Host copy() {
            final Host c = new Host();
            c.labels = new HashSet<String>(this.labels);
            for (final Map.Entry<NodeId, Node> entry : this.nms.entrySet()) {
                c.nms.put(entry.getKey(), entry.getValue().copy());
            }
            return c;
        }
    }
    
    protected static class Node
    {
        public Set<String> labels;
        public Resource resource;
        public boolean running;
        
        protected Node() {
            this.labels = null;
            this.resource = Resource.newInstance(0, 0);
            this.running = false;
        }
        
        public Node copy() {
            final Node c = new Node();
            if (this.labels != null) {
                (c.labels = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>())).addAll(this.labels);
            }
            else {
                c.labels = null;
            }
            c.resource = Resources.clone(this.resource);
            c.running = this.running;
            return c;
        }
    }
    
    private final class ForwardingEventHandler implements EventHandler<NodeLabelsStoreEvent>
    {
        @Override
        public void handle(final NodeLabelsStoreEvent event) {
            CommonNodeLabelsManager.this.handleStoreEvent(event);
        }
    }
}
