// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.deploy.bindings;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.graph.Node;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.deploy.AppLifeCycle;

public class GlobalWebappConfigBinding implements AppLifeCycle.Binding
{
    private static final Logger LOG;
    private String _jettyXml;
    
    public String getJettyXml() {
        return this._jettyXml;
    }
    
    public void setJettyXml(final String jettyXml) {
        this._jettyXml = jettyXml;
    }
    
    public String[] getBindingTargets() {
        return new String[] { "deploying" };
    }
    
    public void processBinding(final Node node, final App app) throws Exception {
        final ContextHandler handler = app.getContextHandler();
        if (handler == null) {
            throw new NullPointerException("No Handler created for App: " + app);
        }
        if (handler instanceof WebAppContext) {
            final WebAppContext context = (WebAppContext)handler;
            if (GlobalWebappConfigBinding.LOG.isDebugEnabled()) {
                GlobalWebappConfigBinding.LOG.debug("Binding: Configuring webapp context with global settings from: " + this._jettyXml, new Object[0]);
            }
            if (this._jettyXml == null) {
                GlobalWebappConfigBinding.LOG.warn("Binding: global context binding is enabled but no jetty-web.xml file has been registered", new Object[0]);
            }
            final Resource globalContextSettings = Resource.newResource(this._jettyXml);
            if (globalContextSettings.exists()) {
                final XmlConfiguration jettyXmlConfig = new XmlConfiguration(globalContextSettings.getInputStream());
                jettyXmlConfig.configure(context);
            }
            else {
                GlobalWebappConfigBinding.LOG.info("Binding: Unable to locate global webapp context settings: " + this._jettyXml, new Object[0]);
            }
        }
    }
    
    static {
        LOG = Log.getLogger(GlobalWebappConfigBinding.class);
    }
}
