// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager;

import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.event.Event;
import com.google.common.annotations.VisibleForTesting;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppNodeUpdateEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import java.util.Collection;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.util.Iterator;
import java.io.IOException;
import org.apache.hadoop.yarn.exceptions.YarnException;
import java.util.Map;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.HostsFileReader;
import org.apache.commons.logging.Log;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.service.AbstractService;

public class NodesListManager extends AbstractService implements EventHandler<NodesListManagerEvent>
{
    private static final Log LOG;
    private HostsFileReader hostsReader;
    private Configuration conf;
    private Set<RMNode> unusableRMNodesConcurrentSet;
    private final RMContext rmContext;
    private String includesFile;
    private String excludesFile;
    
    public NodesListManager(final RMContext rmContext) {
        super(NodesListManager.class.getName());
        this.unusableRMNodesConcurrentSet = Collections.newSetFromMap(new ConcurrentHashMap<RMNode, Boolean>());
        this.rmContext = rmContext;
    }
    
    @Override
    protected void serviceInit(final Configuration conf) throws Exception {
        this.conf = conf;
        try {
            this.includesFile = conf.get("yarn.resourcemanager.nodes.include-path", "");
            this.excludesFile = conf.get("yarn.resourcemanager.nodes.exclude-path", "");
            this.hostsReader = this.createHostsFileReader(this.includesFile, this.excludesFile);
            this.setDecomissionedNMsMetrics();
            this.printConfiguredHosts();
        }
        catch (YarnException ex) {
            this.disableHostsFileReader(ex);
        }
        catch (IOException ioe) {
            this.disableHostsFileReader(ioe);
        }
        super.serviceInit(conf);
    }
    
    private void printConfiguredHosts() {
        if (!NodesListManager.LOG.isDebugEnabled()) {
            return;
        }
        NodesListManager.LOG.debug("hostsReader: in=" + this.conf.get("yarn.resourcemanager.nodes.include-path", "") + " out=" + this.conf.get("yarn.resourcemanager.nodes.exclude-path", ""));
        for (final String include : this.hostsReader.getHosts()) {
            NodesListManager.LOG.debug("include: " + include);
        }
        for (final String exclude : this.hostsReader.getExcludedHosts()) {
            NodesListManager.LOG.debug("exclude: " + exclude);
        }
    }
    
    public void refreshNodes(Configuration yarnConf) throws IOException, YarnException {
        synchronized (this.hostsReader) {
            if (null == yarnConf) {
                yarnConf = new YarnConfiguration();
            }
            this.includesFile = yarnConf.get("yarn.resourcemanager.nodes.include-path", "");
            this.excludesFile = yarnConf.get("yarn.resourcemanager.nodes.exclude-path", "");
            this.hostsReader.updateFileNames(this.includesFile, this.excludesFile);
            this.hostsReader.refresh(this.includesFile.isEmpty() ? null : this.rmContext.getConfigurationProvider().getConfigurationInputStream(this.conf, this.includesFile), this.excludesFile.isEmpty() ? null : this.rmContext.getConfigurationProvider().getConfigurationInputStream(this.conf, this.excludesFile));
            this.printConfiguredHosts();
        }
    }
    
    private void setDecomissionedNMsMetrics() {
        final Set<String> excludeList = this.hostsReader.getExcludedHosts();
        ClusterMetrics.getMetrics().setDecommisionedNMs(excludeList.size());
    }
    
    public boolean isValidNode(final String hostName) {
        synchronized (this.hostsReader) {
            final Set<String> hostsList = this.hostsReader.getHosts();
            final Set<String> excludeList = this.hostsReader.getExcludedHosts();
            final String ip = NetUtils.normalizeHostName(hostName);
            return (hostsList.isEmpty() || hostsList.contains(hostName) || hostsList.contains(ip)) && !excludeList.contains(hostName) && !excludeList.contains(ip);
        }
    }
    
    public int getUnusableNodes(final Collection<RMNode> unUsableNodes) {
        unUsableNodes.addAll(this.unusableRMNodesConcurrentSet);
        return this.unusableRMNodesConcurrentSet.size();
    }
    
    @Override
    public void handle(final NodesListManagerEvent event) {
        final RMNode eventNode = event.getNode();
        switch (event.getType()) {
            case NODE_UNUSABLE: {
                NodesListManager.LOG.debug(eventNode + " reported unusable");
                this.unusableRMNodesConcurrentSet.add(eventNode);
                for (final RMApp app : this.rmContext.getRMApps().values()) {
                    this.rmContext.getDispatcher().getEventHandler().handle(new RMAppNodeUpdateEvent(app.getApplicationId(), eventNode, RMAppNodeUpdateEvent.RMAppNodeUpdateType.NODE_UNUSABLE));
                }
                break;
            }
            case NODE_USABLE: {
                if (this.unusableRMNodesConcurrentSet.contains(eventNode)) {
                    NodesListManager.LOG.debug(eventNode + " reported usable");
                    this.unusableRMNodesConcurrentSet.remove(eventNode);
                }
                for (final RMApp app : this.rmContext.getRMApps().values()) {
                    this.rmContext.getDispatcher().getEventHandler().handle(new RMAppNodeUpdateEvent(app.getApplicationId(), eventNode, RMAppNodeUpdateEvent.RMAppNodeUpdateType.NODE_USABLE));
                }
                break;
            }
            default: {
                NodesListManager.LOG.error("Ignoring invalid eventtype " + ((AbstractEvent<Object>)event).getType());
                break;
            }
        }
    }
    
    private void disableHostsFileReader(final Exception ex) {
        NodesListManager.LOG.warn("Failed to init hostsReader, disabling", ex);
        try {
            this.includesFile = this.conf.get("");
            this.excludesFile = this.conf.get("");
            this.hostsReader = this.createHostsFileReader(this.includesFile, this.excludesFile);
            this.setDecomissionedNMsMetrics();
        }
        catch (IOException ioe2) {
            this.hostsReader = null;
            throw new YarnRuntimeException(ioe2);
        }
        catch (YarnException e) {
            this.hostsReader = null;
            throw new YarnRuntimeException(e);
        }
    }
    
    @VisibleForTesting
    public HostsFileReader getHostsReader() {
        return this.hostsReader;
    }
    
    private HostsFileReader createHostsFileReader(final String includesFile, final String excludesFile) throws IOException, YarnException {
        final HostsFileReader hostsReader = new HostsFileReader(includesFile, (includesFile == null || includesFile.isEmpty()) ? null : this.rmContext.getConfigurationProvider().getConfigurationInputStream(this.conf, includesFile), excludesFile, (excludesFile == null || excludesFile.isEmpty()) ? null : this.rmContext.getConfigurationProvider().getConfigurationInputStream(this.conf, excludesFile));
        return hostsReader;
    }
    
    static {
        LOG = LogFactory.getLog(NodesListManager.class);
    }
}
