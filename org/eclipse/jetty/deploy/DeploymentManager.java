// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.deploy.graph.Path;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.deploy.bindings.StandardUndeployer;
import org.eclipse.jetty.deploy.bindings.StandardStopper;
import org.eclipse.jetty.deploy.bindings.StandardStarter;
import org.eclipse.jetty.deploy.bindings.StandardDeployer;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.deploy.graph.Edge;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.ArrayList;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.util.AttributesMap;
import java.util.Queue;
import java.util.List;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AggregateLifeCycle;

public class DeploymentManager extends AggregateLifeCycle
{
    private static final Logger LOG;
    private final List<AppProvider> _providers;
    private final AppLifeCycle _lifecycle;
    private final Queue<AppEntry> _apps;
    private AttributesMap _contextAttributes;
    private ContextHandlerCollection _contexts;
    private boolean _useStandardBindings;
    private String _defaultLifeCycleGoal;
    
    public DeploymentManager() {
        this._providers = new ArrayList<AppProvider>();
        this._lifecycle = new AppLifeCycle();
        this._apps = new ConcurrentLinkedQueue<AppEntry>();
        this._contextAttributes = new AttributesMap();
        this._useStandardBindings = true;
        this._defaultLifeCycleGoal = "started";
    }
    
    public void addApp(final App app) {
        DeploymentManager.LOG.info("Deployable added: " + app.getOriginId(), new Object[0]);
        final AppEntry entry = new AppEntry();
        entry.app = app;
        entry.setLifeCycleNode(this._lifecycle.getNodeByName("undeployed"));
        this._apps.add(entry);
        if (this.isRunning() && this._defaultLifeCycleGoal != null) {
            this.requestAppGoal(entry, this._defaultLifeCycleGoal);
        }
    }
    
    public void setAppProviders(final Collection<AppProvider> providers) {
        if (this.isRunning()) {
            throw new IllegalStateException();
        }
        this._providers.clear();
        this.removeBeans();
        for (final AppProvider provider : providers) {
            if (this._providers.add(provider)) {
                this.addBean(provider);
            }
        }
    }
    
    public Collection<AppProvider> getAppProviders() {
        return (Collection<AppProvider>)Collections.unmodifiableList((List<?>)this._providers);
    }
    
    public void addAppProvider(final AppProvider provider) {
        if (this.isRunning()) {
            throw new IllegalStateException();
        }
        final List<AppProvider> old = new ArrayList<AppProvider>(this._providers);
        if (this._providers.add(provider) && this.getServer() != null) {
            this.getServer().getContainer().update((Object)this, (Object)null, (Object)provider, "provider");
        }
        this.addBean(provider);
    }
    
    public void setLifeCycleBindings(final Collection<AppLifeCycle.Binding> bindings) {
        if (this.isRunning()) {
            throw new IllegalStateException();
        }
        for (final AppLifeCycle.Binding b : this._lifecycle.getBindings()) {
            this._lifecycle.removeBinding(b);
        }
        for (final AppLifeCycle.Binding b : bindings) {
            this._lifecycle.addBinding(b);
        }
    }
    
    public Collection<AppLifeCycle.Binding> getLifeCycleBindings() {
        return (Collection<AppLifeCycle.Binding>)Collections.unmodifiableSet((Set<?>)this._lifecycle.getBindings());
    }
    
    public void addLifeCycleBinding(final AppLifeCycle.Binding binding) {
        this._lifecycle.addBinding(binding);
    }
    
    public void insertLifeCycleNode(final String existingFromNodeName, final String existingToNodeName, final String insertedNodeName) {
        final Node fromNode = this._lifecycle.getNodeByName(existingFromNodeName);
        final Node toNode = this._lifecycle.getNodeByName(existingToNodeName);
        final Edge edge = new Edge(fromNode, toNode);
        this._lifecycle.insertNode(edge, insertedNodeName);
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this._useStandardBindings) {
            DeploymentManager.LOG.debug("DeploymentManager using standard bindings", new Object[0]);
            this.addLifeCycleBinding(new StandardDeployer());
            this.addLifeCycleBinding(new StandardStarter());
            this.addLifeCycleBinding(new StandardStopper());
            this.addLifeCycleBinding(new StandardUndeployer());
        }
        for (final AppProvider provider : this._providers) {
            this.startAppProvider(provider);
        }
        super.doStart();
    }
    
    @Override
    protected void doStop() throws Exception {
        for (final AppProvider provider : this._providers) {
            try {
                provider.stop();
            }
            catch (Exception e) {
                DeploymentManager.LOG.warn("Unable to start AppProvider", e);
            }
        }
        super.doStop();
    }
    
    private AppEntry findAppByOriginId(final String originId) {
        if (originId == null) {
            return null;
        }
        for (final AppEntry entry : this._apps) {
            if (originId.equals(entry.app.getOriginId())) {
                return entry;
            }
        }
        return null;
    }
    
    public App getAppByOriginId(final String originId) {
        final AppEntry entry = this.findAppByOriginId(originId);
        if (entry == null) {
            return null;
        }
        return entry.app;
    }
    
    public Collection<AppEntry> getAppEntries() {
        return this._apps;
    }
    
    public Collection<App> getApps() {
        final List<App> ret = new ArrayList<App>();
        for (final AppEntry entry : this._apps) {
            ret.add(entry.app);
        }
        return ret;
    }
    
    public Collection<App> getApps(final Node node) {
        final List<App> ret = new ArrayList<App>();
        for (final AppEntry entry : this._apps) {
            if (entry.lifecyleNode == node) {
                ret.add(entry.app);
            }
        }
        return ret;
    }
    
    public List<App> getAppsWithSameContext(final App app) {
        final List<App> ret = new ArrayList<App>();
        if (app == null) {
            return ret;
        }
        final String contextId = app.getContextPath();
        if (contextId == null) {
            return ret;
        }
        for (final AppEntry entry : this._apps) {
            if (entry.app.equals(app)) {
                continue;
            }
            if (!contextId.equals(entry.app.getContextPath())) {
                continue;
            }
            ret.add(entry.app);
        }
        return ret;
    }
    
    public Object getContextAttribute(final String name) {
        return this._contextAttributes.getAttribute(name);
    }
    
    public AttributesMap getContextAttributes() {
        return this._contextAttributes;
    }
    
    public ContextHandlerCollection getContexts() {
        return this._contexts;
    }
    
    public String getDefaultLifeCycleGoal() {
        return this._defaultLifeCycleGoal;
    }
    
    public AppLifeCycle getLifeCycle() {
        return this._lifecycle;
    }
    
    public Server getServer() {
        if (this._contexts == null) {
            return null;
        }
        return this._contexts.getServer();
    }
    
    public void removeApp(final App app) {
        final Iterator<AppEntry> it = this._apps.iterator();
        while (it.hasNext()) {
            final AppEntry entry = it.next();
            if (entry.app.equals(app)) {
                if (!"undeployed".equals(entry.lifecyleNode.getName())) {
                    this.requestAppGoal(entry.app, "undeployed");
                }
                it.remove();
                DeploymentManager.LOG.info("Deployable removed: " + entry.app, new Object[0]);
            }
        }
    }
    
    public void removeAppProvider(final AppProvider provider) {
        if (this._providers.remove(provider)) {
            this.removeBean(provider);
            if (this.getServer() != null) {
                this.getServer().getContainer().update((Object)this, (Object)provider, (Object)null, "provider");
            }
        }
        try {
            provider.stop();
        }
        catch (Exception e) {
            DeploymentManager.LOG.warn("Unable to stop Provider", e);
        }
    }
    
    public void removeContextAttribute(final String name) {
        this._contextAttributes.removeAttribute(name);
    }
    
    public void requestAppGoal(final App app, final String nodeName) {
        final AppEntry appentry = this.findAppByOriginId(app.getOriginId());
        if (appentry == null) {
            throw new IllegalStateException("App not being tracked by Deployment Manager: " + app);
        }
        this.requestAppGoal(appentry, nodeName);
    }
    
    private void requestAppGoal(final AppEntry appentry, final String nodeName) {
        final Node destinationNode = this._lifecycle.getNodeByName(nodeName);
        if (destinationNode == null) {
            throw new IllegalStateException("Node not present in Deployment Manager: " + nodeName);
        }
        final Path path = this._lifecycle.getPath(appentry.lifecyleNode, destinationNode);
        if (path.isEmpty()) {
            return;
        }
        try {
            final Iterator<Node> it = path.getNodes().iterator();
            if (it.hasNext()) {
                it.next();
                while (it.hasNext()) {
                    final Node node = it.next();
                    DeploymentManager.LOG.debug("Executing Node {}", node);
                    this._lifecycle.runBindings(node, appentry.app, this);
                    appentry.setLifeCycleNode(node);
                }
            }
        }
        catch (Throwable t) {
            DeploymentManager.LOG.warn("Unable to reach node goal: " + nodeName, t);
        }
    }
    
    public void requestAppGoal(final String appId, final String nodeName) {
        final AppEntry appentry = this.findAppByOriginId(appId);
        if (appentry == null) {
            throw new IllegalStateException("App not being tracked by Deployment Manager: " + appId);
        }
        this.requestAppGoal(appentry, nodeName);
    }
    
    public void setContextAttribute(final String name, final Object value) {
        this._contextAttributes.setAttribute(name, value);
    }
    
    public void setContextAttributes(final AttributesMap contextAttributes) {
        this._contextAttributes = contextAttributes;
    }
    
    public void setContexts(final ContextHandlerCollection contexts) {
        this._contexts = contexts;
    }
    
    public void setDefaultLifeCycleGoal(final String defaultLifeCycleState) {
        this._defaultLifeCycleGoal = defaultLifeCycleState;
    }
    
    private void startAppProvider(final AppProvider provider) {
        try {
            provider.setDeploymentManager(this);
            provider.start();
        }
        catch (Exception e) {
            DeploymentManager.LOG.warn("Unable to start AppProvider", e);
        }
    }
    
    public void undeployAll() {
        DeploymentManager.LOG.info("Undeploy All", new Object[0]);
        for (final AppEntry appentry : this._apps) {
            this.requestAppGoal(appentry, "undeployed");
        }
    }
    
    public boolean isUseStandardBindings() {
        return this._useStandardBindings;
    }
    
    public void setUseStandardBindings(final boolean useStandardBindings) {
        this._useStandardBindings = useStandardBindings;
    }
    
    public Collection<Node> getNodes() {
        return this._lifecycle.getNodes();
    }
    
    public Collection<App> getApps(final String nodeName) {
        return this.getApps(this._lifecycle.getNodeByName(nodeName));
    }
    
    static {
        LOG = Log.getLogger(DeploymentManager.class);
    }
    
    public class AppEntry
    {
        private int version;
        private App app;
        private Node lifecyleNode;
        private Map<Node, Long> stateTimestamps;
        
        public AppEntry() {
            this.stateTimestamps = new HashMap<Node, Long>();
        }
        
        public App getApp() {
            return this.app;
        }
        
        public Node getLifecyleNode() {
            return this.lifecyleNode;
        }
        
        public Map<Node, Long> getStateTimestamps() {
            return this.stateTimestamps;
        }
        
        public int getVersion() {
            return this.version;
        }
        
        void setLifeCycleNode(final Node node) {
            this.lifecyleNode = node;
            this.stateTimestamps.put(node, System.currentTimeMillis());
        }
    }
}
