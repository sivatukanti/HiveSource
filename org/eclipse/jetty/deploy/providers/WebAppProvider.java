// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.providers;

import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.deploy.util.FileID;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.deploy.App;
import java.io.IOException;
import java.net.MalformedURLException;
import org.eclipse.jetty.util.resource.Resource;
import java.io.FilenameFilter;
import java.io.File;

public class WebAppProvider extends ScanningAppProvider
{
    private boolean _extractWars;
    private boolean _parentLoaderPriority;
    private String _defaultsDescriptor;
    private Filter _filter;
    private File _tempDirectory;
    private String[] _configurationClasses;
    
    public WebAppProvider() {
        super(new Filter());
        this._extractWars = false;
        this._parentLoaderPriority = false;
        this._filter = (Filter)this._filenameFilter;
        this.setScanInterval(0);
    }
    
    public boolean isExtractWars() {
        return this._extractWars;
    }
    
    public void setExtractWars(final boolean extractWars) {
        this._extractWars = extractWars;
    }
    
    public boolean isParentLoaderPriority() {
        return this._parentLoaderPriority;
    }
    
    public void setParentLoaderPriority(final boolean parentLoaderPriority) {
        this._parentLoaderPriority = parentLoaderPriority;
    }
    
    public String getDefaultsDescriptor() {
        return this._defaultsDescriptor;
    }
    
    public void setDefaultsDescriptor(final String defaultsDescriptor) {
        this._defaultsDescriptor = defaultsDescriptor;
    }
    
    public String getContextXmlDir() {
        return (this._filter._contexts == null) ? null : this._filter._contexts.toString();
    }
    
    public void setContextXmlDir(final String contextsDir) {
        try {
            this._filter._contexts = Resource.newResource(contextsDir).getFile();
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
    }
    
    public void setConfigurationClasses(final String[] configurations) {
        this._configurationClasses = (String[])((configurations == null) ? null : ((String[])configurations.clone()));
    }
    
    public String[] getConfigurationClasses() {
        return this._configurationClasses;
    }
    
    public void setTempDir(final File directory) {
        this._tempDirectory = directory;
    }
    
    public File getTempDir() {
        return this._tempDirectory;
    }
    
    public ContextHandler createContextHandler(final App app) throws Exception {
        final Resource resource = Resource.newResource(app.getOriginId());
        final File file = resource.getFile();
        if (!resource.exists()) {
            throw new IllegalStateException("App resouce does not exist " + resource);
        }
        String context = file.getName();
        if (!file.isDirectory()) {
            if (!FileID.isWebArchiveFile(file)) {
                throw new IllegalStateException("unable to create ContextHandler for " + app);
            }
            context = context.substring(0, context.length() - 4);
        }
        if (context.endsWith("/") && context.length() > 0) {
            context = context.substring(0, context.length() - 1);
        }
        final WebAppContext wah = new WebAppContext();
        wah.setDisplayName(context);
        if (context.equalsIgnoreCase("root")) {
            context = "/";
        }
        else if (context.toLowerCase().startsWith("root-")) {
            final int dash = context.toLowerCase().indexOf(45);
            final String virtual = context.substring(dash + 1);
            wah.setVirtualHosts(new String[] { virtual });
            context = "/";
        }
        if (context.charAt(0) != '/') {
            context = "/" + context;
        }
        wah.setContextPath(context);
        wah.setWar(file.getAbsolutePath());
        if (this._defaultsDescriptor != null) {
            wah.setDefaultsDescriptor(this._defaultsDescriptor);
        }
        wah.setExtractWAR(this._extractWars);
        wah.setParentLoaderPriority(this._parentLoaderPriority);
        if (this._configurationClasses != null) {
            wah.setConfigurationClasses(this._configurationClasses);
        }
        if (this._tempDirectory != null) {
            wah.setAttribute("org.eclipse.jetty.webapp.basetempdir", this._tempDirectory);
        }
        return wah;
    }
    
    public static class Filter implements FilenameFilter
    {
        private File _contexts;
        
        public boolean accept(final File dir, final String name) {
            if (!dir.exists()) {
                return false;
            }
            final String lowername = name.toLowerCase();
            final File file = new File(dir, name);
            if (!file.isDirectory() && !lowername.endsWith(".war")) {
                return false;
            }
            if (file.isDirectory() && (new File(dir, name + ".war").exists() || new File(dir, name + ".WAR").exists())) {
                return false;
            }
            if (this._contexts != null) {
                String context = name;
                if (!file.isDirectory()) {
                    context = context.substring(0, context.length() - 4);
                }
                if (new File(this._contexts, context + ".xml").exists() || new File(this._contexts, context + ".XML").exists()) {
                    return false;
                }
            }
            return true;
        }
    }
}
