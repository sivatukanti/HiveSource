// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.providers;

import java.util.Map;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.eclipse.jetty.deploy.util.FileID;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.deploy.App;
import java.io.File;
import java.io.FilenameFilter;
import org.eclipse.jetty.deploy.ConfigurationManager;

public class ContextProvider extends ScanningAppProvider
{
    private ConfigurationManager _configurationManager;
    
    public ContextProvider() {
        super(new FilenameFilter() {
            public boolean accept(final File dir, final String name) {
                if (!dir.exists()) {
                    return false;
                }
                final String lowername = name.toLowerCase();
                return lowername.endsWith(".xml") && !new File(dir, name).isDirectory();
            }
        });
    }
    
    public ConfigurationManager getConfigurationManager() {
        return this._configurationManager;
    }
    
    public void setConfigurationManager(final ConfigurationManager configurationManager) {
        this._configurationManager = configurationManager;
    }
    
    public ContextHandler createContextHandler(final App app) throws Exception {
        final Resource resource = Resource.newResource(app.getOriginId());
        final File file = resource.getFile();
        if (resource.exists() && FileID.isXmlFile(file)) {
            final XmlConfiguration xmlc = new XmlConfiguration(resource.getURL());
            xmlc.getIdMap().put("Server", this.getDeploymentManager().getServer());
            if (this.getConfigurationManager() != null) {
                xmlc.getProperties().putAll(this.getConfigurationManager().getProperties());
            }
            return (ContextHandler)xmlc.configure();
        }
        throw new IllegalStateException("App resouce does not exist " + resource);
    }
}
