// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.providers;

import org.eclipse.jetty.util.log.Log;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import org.eclipse.jetty.util.Scanner;
import org.eclipse.jetty.util.resource.Resource;
import java.io.FilenameFilter;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.App;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.deploy.AppProvider;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public abstract class ScanningAppProvider extends AbstractLifeCycle implements AppProvider
{
    private static final Logger LOG;
    private Map<String, App> _appMap;
    private DeploymentManager _deploymentManager;
    protected final FilenameFilter _filenameFilter;
    private Resource _monitoredDir;
    private boolean _recursive;
    private int _scanInterval;
    private Scanner _scanner;
    private final Scanner.DiscreteListener _scannerListener;
    
    protected ScanningAppProvider(final FilenameFilter filter) {
        this._appMap = new HashMap<String, App>();
        this._recursive = false;
        this._scanInterval = 10;
        this._scannerListener = new Scanner.DiscreteListener() {
            public void fileAdded(final String filename) throws Exception {
                ScanningAppProvider.this.fileAdded(filename);
            }
            
            public void fileChanged(final String filename) throws Exception {
                ScanningAppProvider.this.fileChanged(filename);
            }
            
            public void fileRemoved(final String filename) throws Exception {
                ScanningAppProvider.this.fileRemoved(filename);
            }
        };
        this._filenameFilter = filter;
    }
    
    protected Map<String, App> getDeployedApps() {
        return this._appMap;
    }
    
    protected App createApp(final String filename) {
        return new App(this._deploymentManager, this, filename);
    }
    
    @Override
    protected void doStart() throws Exception {
        if (ScanningAppProvider.LOG.isDebugEnabled()) {
            ScanningAppProvider.LOG.debug(this.getClass().getSimpleName() + ".doStart()", new Object[0]);
        }
        if (this._monitoredDir == null) {
            throw new IllegalStateException("No configuration dir specified");
        }
        final File scandir = this._monitoredDir.getFile();
        ScanningAppProvider.LOG.info("Deployment monitor " + scandir + " at interval " + this._scanInterval, new Object[0]);
        (this._scanner = new Scanner()).setScanDirs(Collections.singletonList(scandir));
        this._scanner.setScanInterval(this._scanInterval);
        this._scanner.setRecursive(this._recursive);
        this._scanner.setFilenameFilter(this._filenameFilter);
        this._scanner.setReportDirs(true);
        this._scanner.addListener(this._scannerListener);
        this._scanner.start();
    }
    
    @Override
    protected void doStop() throws Exception {
        if (this._scanner != null) {
            this._scanner.stop();
            this._scanner.removeListener(this._scannerListener);
            this._scanner = null;
        }
    }
    
    protected void fileAdded(final String filename) throws Exception {
        if (ScanningAppProvider.LOG.isDebugEnabled()) {
            ScanningAppProvider.LOG.debug("added {}", filename);
        }
        final App app = this.createApp(filename);
        if (app != null) {
            this._appMap.put(filename, app);
            this._deploymentManager.addApp(app);
        }
    }
    
    protected void fileChanged(final String filename) throws Exception {
        if (ScanningAppProvider.LOG.isDebugEnabled()) {
            ScanningAppProvider.LOG.debug("changed {}", filename);
        }
        App app = this._appMap.remove(filename);
        if (app != null) {
            this._deploymentManager.removeApp(app);
        }
        app = this.createApp(filename);
        if (app != null) {
            this._appMap.put(filename, app);
            this._deploymentManager.addApp(app);
        }
    }
    
    protected void fileRemoved(final String filename) throws Exception {
        if (ScanningAppProvider.LOG.isDebugEnabled()) {
            ScanningAppProvider.LOG.debug("removed {}", filename);
        }
        final App app = this._appMap.remove(filename);
        if (app != null) {
            this._deploymentManager.removeApp(app);
        }
    }
    
    public DeploymentManager getDeploymentManager() {
        return this._deploymentManager;
    }
    
    public Resource getMonitoredDirResource() {
        return this._monitoredDir;
    }
    
    public String getMonitoredDirName() {
        return this._monitoredDir.toString();
    }
    
    public int getScanInterval() {
        return this._scanInterval;
    }
    
    public boolean isRecursive() {
        return this._recursive;
    }
    
    public void setDeploymentManager(final DeploymentManager deploymentManager) {
        this._deploymentManager = deploymentManager;
    }
    
    public void setMonitoredDirResource(final Resource contextsDir) {
        this._monitoredDir = contextsDir;
    }
    
    public void addScannerListener(final Scanner.Listener listener) {
        this._scanner.addListener(listener);
    }
    
    @Deprecated
    public void setMonitoredDir(final String dir) {
        this.setMonitoredDirName(dir);
    }
    
    public void setMonitoredDirName(final String dir) {
        try {
            this.setMonitoredDirResource(Resource.newResource(dir));
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    protected void setRecursive(final boolean recursive) {
        this._recursive = recursive;
    }
    
    public void setScanInterval(final int scanInterval) {
        this._scanInterval = scanInterval;
    }
    
    static {
        LOG = Log.getLogger(ScanningAppProvider.class);
    }
}
