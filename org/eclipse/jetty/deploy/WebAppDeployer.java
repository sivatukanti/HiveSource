// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.Attributes;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.AttributesMap;
import java.util.ArrayList;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public class WebAppDeployer extends AbstractLifeCycle
{
    private static final Logger LOG;
    private HandlerCollection _contexts;
    private String _webAppDir;
    private String _defaultsDescriptor;
    private String[] _configurationClasses;
    private boolean _extract;
    private boolean _parentLoaderPriority;
    private boolean _allowDuplicates;
    private ArrayList _deployed;
    private AttributesMap _contextAttributes;
    
    public WebAppDeployer() {
        this._contextAttributes = new AttributesMap();
        WebAppDeployer.LOG.warn("WebAppDeployer is deprecated. Use WebAppProvider", new Object[0]);
    }
    
    public String[] getConfigurationClasses() {
        return this._configurationClasses;
    }
    
    public void setConfigurationClasses(final String[] configurationClasses) {
        this._configurationClasses = configurationClasses;
    }
    
    public HandlerCollection getContexts() {
        return this._contexts;
    }
    
    public void setContexts(final HandlerCollection contexts) {
        this._contexts = contexts;
    }
    
    public String getDefaultsDescriptor() {
        return this._defaultsDescriptor;
    }
    
    public void setDefaultsDescriptor(final String defaultsDescriptor) {
        this._defaultsDescriptor = defaultsDescriptor;
    }
    
    public boolean isExtract() {
        return this._extract;
    }
    
    public void setExtract(final boolean extract) {
        this._extract = extract;
    }
    
    public boolean isParentLoaderPriority() {
        return this._parentLoaderPriority;
    }
    
    public void setParentLoaderPriority(final boolean parentPriorityClassLoading) {
        this._parentLoaderPriority = parentPriorityClassLoading;
    }
    
    public String getWebAppDir() {
        return this._webAppDir;
    }
    
    public void setWebAppDir(final String dir) {
        this._webAppDir = dir;
    }
    
    public boolean getAllowDuplicates() {
        return this._allowDuplicates;
    }
    
    public void setAllowDuplicates(final boolean allowDuplicates) {
        this._allowDuplicates = allowDuplicates;
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
    
    public void doStart() throws Exception {
        this._deployed = new ArrayList();
        this.scan();
    }
    
    public void scan() throws Exception {
        if (this._contexts == null) {
            throw new IllegalArgumentException("No HandlerContainer");
        }
        final Resource r = Resource.newResource(this._webAppDir);
        if (!r.exists()) {
            throw new IllegalArgumentException("No such webapps resource " + r);
        }
        if (!r.isDirectory()) {
            throw new IllegalArgumentException("Not directory webapps resource " + r);
        }
        final String[] files = r.list();
    Label_0694:
        for (int f = 0; files != null && f < files.length; ++f) {
            String context = files[f];
            if (!context.equalsIgnoreCase("CVS/") && !context.equalsIgnoreCase("CVS")) {
                if (!context.startsWith(".")) {
                    final Resource app = r.addPath(r.encode(context));
                    if (context.toLowerCase().endsWith(".war") || context.toLowerCase().endsWith(".jar")) {
                        context = context.substring(0, context.length() - 4);
                        final Resource unpacked = r.addPath(context);
                        if (unpacked != null && unpacked.exists() && unpacked.isDirectory()) {
                            continue;
                        }
                    }
                    else if (!app.isDirectory()) {
                        continue;
                    }
                    if (context.equalsIgnoreCase("root") || context.equalsIgnoreCase("root/")) {
                        context = "/";
                    }
                    else {
                        context = "/" + context;
                    }
                    if (context.endsWith("/") && context.length() > 0) {
                        context = context.substring(0, context.length() - 1);
                    }
                    if (!this._allowDuplicates) {
                        final Handler[] installed = this._contexts.getChildHandlersByClass(ContextHandler.class);
                        for (int i = 0; i < installed.length; ++i) {
                            final ContextHandler c = (ContextHandler)installed[i];
                            if (context.equals(c.getContextPath())) {
                                continue Label_0694;
                            }
                            try {
                                String path = null;
                                if (c instanceof WebAppContext) {
                                    path = Resource.newResource(((WebAppContext)c).getWar()).getFile().getCanonicalPath();
                                }
                                else if (c.getBaseResource() != null) {
                                    path = c.getBaseResource().getFile().getCanonicalPath();
                                }
                                if (path != null && path.equals(app.getFile().getCanonicalPath())) {
                                    WebAppDeployer.LOG.debug("Already deployed: {}", path);
                                    continue Label_0694;
                                }
                            }
                            catch (Exception e) {
                                WebAppDeployer.LOG.ignore(e);
                            }
                        }
                    }
                    WebAppContext wah = null;
                    Label_0577: {
                        if (this._contexts instanceof ContextHandlerCollection && WebAppContext.class.isAssignableFrom(((ContextHandlerCollection)this._contexts).getContextClass())) {
                            try {
                                wah = (WebAppContext)((ContextHandlerCollection)this._contexts).getContextClass().newInstance();
                                break Label_0577;
                            }
                            catch (Exception e2) {
                                throw new Error(e2);
                            }
                        }
                        wah = new WebAppContext();
                    }
                    wah.setContextPath(context);
                    if (this._configurationClasses != null) {
                        wah.setConfigurationClasses(this._configurationClasses);
                    }
                    if (this._defaultsDescriptor != null) {
                        wah.setDefaultsDescriptor(this._defaultsDescriptor);
                    }
                    wah.setExtractWAR(this._extract);
                    wah.setWar(app.toString());
                    wah.setParentLoaderPriority(this._parentLoaderPriority);
                    wah.setAttributes(new AttributesMap(this._contextAttributes));
                    this._contexts.addHandler(wah);
                    this._deployed.add(wah);
                    if (this._contexts.isStarted()) {
                        wah.start();
                    }
                }
            }
        }
    }
    
    public void doStop() throws Exception {
        int i = this._deployed.size();
        while (i-- > 0) {
            final ContextHandler wac = this._deployed.get(i);
            wac.stop();
        }
    }
    
    static {
        LOG = Log.getLogger(WebAppDeployer.class);
    }
}
