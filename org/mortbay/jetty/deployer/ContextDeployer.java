// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.deployer;

import org.mortbay.xml.XmlConfiguration;
import java.io.FilenameFilter;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.Handler;
import org.mortbay.log.Log;
import java.io.File;
import java.util.HashMap;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import java.util.Map;
import org.mortbay.resource.Resource;
import org.mortbay.util.Scanner;
import org.mortbay.component.AbstractLifeCycle;

public class ContextDeployer extends AbstractLifeCycle
{
    public static final String NAME = "ConfiguredDeployer";
    private int _scanInterval;
    private Scanner _scanner;
    private ScannerListener _scannerListener;
    private Resource _configurationDir;
    private Map _currentDeployments;
    private ContextHandlerCollection _contexts;
    private ConfigurationManager _configMgr;
    private boolean _recursive;
    
    public ContextDeployer() throws Exception {
        this._scanInterval = 10;
        this._currentDeployments = new HashMap();
        this._recursive = false;
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
    
    public void setConfigurationDir(final String dir) throws Exception {
        this.setConfigurationDir(Resource.newResource(dir));
    }
    
    public void setConfigurationDir(final File file) throws Exception {
        this.setConfigurationDir(Resource.newResource(file.toURL()));
    }
    
    public void setConfigurationDir(final Resource resource) {
        if (this.isStarted() || this.isStarting()) {
            throw new IllegalStateException("Cannot change hot deploy dir after deployer start");
        }
        this._configurationDir = resource;
    }
    
    public void setDirectory(final String directory) throws Exception {
        this.setConfigurationDir(directory);
    }
    
    public String getDirectory() {
        return this.getConfigurationDir().getName();
    }
    
    public Resource getConfigurationDir() {
        return this._configurationDir;
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
    
    private void deploy(final String filename) throws Exception {
        final ContextHandler context = this.createContext(filename);
        Log.info("Deploy " + filename + " -> " + context);
        this._contexts.addHandler(context);
        this._currentDeployments.put(filename, context);
        if (this._contexts.isStarted()) {
            context.start();
        }
    }
    
    private void undeploy(final String filename) throws Exception {
        final ContextHandler context = this._currentDeployments.get(filename);
        Log.info("Undeploy " + filename + " -> " + context);
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
    
    protected void doStart() throws Exception {
        if (this._configurationDir == null) {
            throw new IllegalStateException("No configuraition dir specified");
        }
        if (this._contexts == null) {
            throw new IllegalStateException("No context handler collection specified for deployer");
        }
        this._scanner.setScanDir(this._configurationDir.getFile());
        this._scanner.setScanInterval(this.getScanInterval());
        this._scanner.setRecursive(this._recursive);
        this._scanner.setFilenameFilter(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                try {
                    return name.endsWith(".xml");
                }
                catch (Exception e) {
                    Log.warn(e);
                    return false;
                }
            }
        });
        this._scannerListener = new ScannerListener();
        this._scanner.addListener(this._scannerListener);
        this._scanner.scan();
        this._scanner.start();
        this._contexts.getServer().getContainer().addBean(this._scanner);
    }
    
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
        final HashMap properties = new HashMap();
        properties.put("Server", this._contexts.getServer());
        if (this._configMgr != null) {
            properties.putAll(this._configMgr.getProperties());
        }
        xmlConfiguration.setProperties(properties);
        final ContextHandler context = (ContextHandler)xmlConfiguration.configure();
        return context;
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
        
        public String toString() {
            return "ContextDeployer$Scanner";
        }
    }
}
