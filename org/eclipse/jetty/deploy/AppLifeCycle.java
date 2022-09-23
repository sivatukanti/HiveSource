// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.deploy.graph.Node;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.deploy.graph.Graph;

public class AppLifeCycle extends Graph
{
    private static final Logger LOG;
    private static final String ALL_NODES = "*";
    public static final String UNDEPLOYED = "undeployed";
    public static final String DEPLOYING = "deploying";
    public static final String DEPLOYED = "deployed";
    public static final String STARTING = "starting";
    public static final String STARTED = "started";
    public static final String STOPPING = "stopping";
    public static final String UNDEPLOYING = "undeploying";
    private Map<String, List<Binding>> lifecyclebindings;
    
    public AppLifeCycle() {
        this.lifecyclebindings = new HashMap<String, List<Binding>>();
        this.addEdge("undeployed", "deploying");
        this.addEdge("deploying", "deployed");
        this.addEdge("deployed", "starting");
        this.addEdge("starting", "started");
        this.addEdge("started", "stopping");
        this.addEdge("stopping", "deployed");
        this.addEdge("deployed", "undeploying");
        this.addEdge("undeploying", "undeployed");
    }
    
    public void addBinding(final Binding binding) {
        for (final String nodeName : binding.getBindingTargets()) {
            List<Binding> bindings = this.lifecyclebindings.get(nodeName);
            if (bindings == null) {
                bindings = new ArrayList<Binding>();
            }
            bindings.add(binding);
            this.lifecyclebindings.put(nodeName, bindings);
        }
    }
    
    public void removeBinding(final Binding binding) {
        for (final String nodeName : binding.getBindingTargets()) {
            final List<Binding> bindings = this.lifecyclebindings.get(nodeName);
            if (bindings != null) {
                bindings.remove(binding);
            }
        }
    }
    
    public Set<Binding> getBindings() {
        final Set<Binding> boundset = new HashSet<Binding>();
        for (final List<Binding> bindings : this.lifecyclebindings.values()) {
            boundset.addAll(bindings);
        }
        return boundset;
    }
    
    public Set<Binding> getBindings(final Node node) {
        return this.getBindings(node.getName());
    }
    
    public Set<Binding> getBindings(final String nodeName) {
        final Set<Binding> boundset = new HashSet<Binding>();
        List<Binding> bindings = this.lifecyclebindings.get(nodeName);
        if (bindings != null) {
            boundset.addAll(bindings);
        }
        bindings = this.lifecyclebindings.get("*");
        if (bindings != null) {
            boundset.addAll(bindings);
        }
        return boundset;
    }
    
    public void runBindings(final Node node, final App app, final DeploymentManager deploymentManager) throws Throwable {
        for (final Binding binding : this.getBindings(node)) {
            if (AppLifeCycle.LOG.isDebugEnabled()) {
                AppLifeCycle.LOG.debug("Calling " + binding.getClass().getName() + " for " + app, new Object[0]);
            }
            binding.processBinding(node, app);
        }
    }
    
    static {
        LOG = Log.getLogger(AppLifeCycle.class);
    }
    
    public interface Binding
    {
        String[] getBindingTargets();
        
        void processBinding(final Node p0, final App p1) throws Exception;
    }
}
