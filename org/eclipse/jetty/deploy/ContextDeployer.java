// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.xml.XmlConfiguration;
import java.io.FilenameFilter;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.Handler;
import java.io.File;
import java.util.HashMap;
import org.eclipse.jetty.util.AttributesMap;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import java.util.Map;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

@Deprecated
public class ContextDeployer extends AbstractLifeCycle
{
    private static final Logger LOG;
    private int _scanInterval;
    private Scanner _scanner;
    private ScannerListener _scannerListener;
    private Resource _contextsDir;
    private Map _currentDeployments;
    private ContextHandlerCollection _contexts;
    private ConfigurationManager _configMgr;
    private boolean _recursive;
    private AttributesMap _contextAttributes;
    
    public ContextDeployer() {
        this._scanInterval = 10;
        this._currentDeployments = new HashMap();
        this._recursive = false;
        this._contextAttributes = new AttributesMap();
        ContextDeployer.LOG.warn("ContextDeployer is deprecated. Use ContextProvider", new Object[0]);
        this._scanner = new Scanner();
    }
    
    public ContextHandlerCollection getContexts() {
        return this._contexts;
    }
    
    public void setContexts(final ContextHandlerCollection contexts) {
        if (this.isStarted() || this.isStarting()) {
            throw new IllegalStateException("Cannot set Contexts after deployer start");
        }
        this._contexts = contexts;
    }
    
    public void setScanInterval(final int seconds) {
        if (this.isStarted() || this.isStarting()) {
            throw new IllegalStateException("Cannot change scan interval after deployer start");
        }
        this._scanInterval = seconds;
    }
    
    public int getScanInterval() {
        return this._scanInterval;
    }
    
    public void setContextsDir(final String dir) {
        try {
            this._contextsDir = Resource.newResource(dir);
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public String getContextsDir() {
        return (this._contextsDir == null) ? null : this._contextsDir.toString();
    }
    
    @Deprecated
    public void setConfigurationDir(final String dir) throws Exception {
        this.setConfigurationDir(Resource.newResource(dir));
    }
    
    @Deprecated
    public void setConfigurationDir(final File file) throws Exception {
        this.setConfigurationDir(Resource.newResource(Resource.toURL(file)));
    }
    
    @Deprecated
    public void setConfigurationDir(final Resource resource) {
        if (this.isStarted() || this.isStarting()) {
            throw new IllegalStateException("Cannot change hot deploy dir after deployer start");
        }
        this._contextsDir = resource;
    }
    
    @Deprecated
    public void setDirectory(final String directory) throws Exception {
        this.setConfigurationDir(directory);
    }
    
    @Deprecated
    public String getDirectory() {
        return this.getConfigurationDir().getName();
    }
    
    @Deprecated
    public Resource getConfigurationDir() {
        return this._contextsDir;
    }
    
    public void setConfigurationManager(final ConfigurationManager configMgr) {
        this._configMgr = configMgr;
    }
    
    public ConfigurationManager getConfigurationManager() {
        return this._configMgr;
    }
    
    public void setRecursive(final boolean recursive) {
        this._recursive = recursive;
    }
    
    public boolean getRecursive() {
        return this._recursive;
    }
    
    public boolean isRecursive() {
        return this._recursive;
    }
    
    public void setAttribute(final String name, final Object value) {
        this._contextAttributes.setAttribute(name, value);
    }
    
    public Object getAttribute(final String name) {
        return this._contextAttributes.getAttribute(name);
    }
    
    public void removeAttribute(final String name) {
        this._contextAttributes.removeAttribute(name);
    }
    
    private void deploy(final String filename) throws Exception {
        final ContextHandler context = this.createContext(filename);
        ContextDeployer.LOG.info("Deploy " + filename + " -> " + context, new Object[0]);
        this._contexts.addHandler(context);
        this._currentDeployments.put(filename, context);
        if (this._contexts.isStarted()) {
            context.start();
        }
    }
    
    private void undeploy(final String filename) throws Exception {
        final ContextHandler context = this._currentDeployments.get(filename);
        ContextDeployer.LOG.info("Undeploy " + filename + " -> " + context, new Object[0]);
        if (context == null) {
            return;
        }
        context.stop();
        this._contexts.removeHandler(context);
        this._currentDeployments.remove(filename);
    }
    
    private void redeploy(final String filename) throws Exception {
        this.undeploy(filename);
        this.deploy(filename);
    }
    
    @Override
    protected void doStart() throws Exception {
        if (this._contextsDir == null) {
            throw new IllegalStateException("No configuration dir specified");
        }
        if (this._contexts == null) {
            throw new IllegalStateException("No context handler collection specified for deployer");
        }
        this._scanner.setScanDir(this._contextsDir.getFile());
        this._scanner.setScanInterval(this.getScanInterval());
        this._scanner.setRecursive(this._recursive);
        this._scanner.setFilenameFilter(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                try {
                    return name.endsWith(".xml");
                }
                catch (Exception e) {
                    ContextDeployer.LOG.warn(e);
                    return false;
                }
            }
        });
        this._scannerListener = new ScannerListener();
        this._scanner.addListener(this._scannerListener);
        this._scanner.scan();
        this._scanner.start();
        this._contexts.getServer().getContainer().addBean((Object)this._scanner);
    }
    
    @Override
    protected void doStop() throws Exception {
        this._scanner.removeListener(this._scannerListener);
        this._scanner.stop();
    }
    
    private ContextHandler createContext(final String filename) throws Exception {
        final Resource resource = Resource.newResource(filename);
        if (!resource.exists()) {
            return null;
        }
        final XmlConfiguration xmlConfiguration = new XmlConfiguration(resource.getURL());
        xmlConfiguration.getIdMap().put("Server", this._contexts.getServer());
        if (this._configMgr != null) {
            xmlConfiguration.getProperties().putAll(this._configMgr.getProperties());
        }
        final ContextHandler context = (ContextHandler)xmlConfiguration.configure();
        if (this._contextAttributes != null && this._contextAttributes.size() > 0) {
            final AttributesMap attributes = new AttributesMap(this._contextAttributes);
            attributes.addAll(context.getAttributes());
            context.setAttributes(attributes);
        }
        return context;
    }
    
    static {
        LOG = Log.getLogger(ContextDeployer.class);
    }
    
    protected class ScannerListener implements Scanner.DiscreteListener
    {
        public void fileAdded(final String filename) throws Exception {
            ContextDeployer.this.deploy(filename);
        }
        
        public void fileChanged(final String filename) throws Exception {
            ContextDeployer.this.redeploy(filename);
        }
        
        public void fileRemoved(final String filename) throws Exception {
            ContextDeployer.this.undeploy(filename);
        }
        
        @Override
        public String toString() {
            return "ContextDeployer$Scanner";
        }
    }
}
