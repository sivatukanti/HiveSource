// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.webapp;

import org.mortbay.resource.Resource;
import org.mortbay.xml.XmlConfiguration;
import org.mortbay.log.Log;

public class JettyWebXmlConfiguration implements Configuration
{
    private WebAppContext _context;
    
    public void setWebAppContext(final WebAppContext context) {
        this._context = context;
    }
    
    public WebAppContext getWebAppContext() {
        return this._context;
    }
    
    public void configureClassLoader() throws Exception {
    }
    
    public void configureDefaults() throws Exception {
    }
    
    public void configureWebApp() throws Exception {
        if (this._context.isStarted()) {
            if (Log.isDebugEnabled()) {
                Log.debug("Cannot configure webapp after it is started");
            }
            return;
        }
        if (Log.isDebugEnabled()) {
            Log.debug("Configuring web-jetty.xml");
        }
        final Resource web_inf = this.getWebAppContext().getWebInf();
        if (web_inf != null && web_inf.isDirectory()) {
            Resource jetty = web_inf.addPath("jetty6-web.xml");
            if (!jetty.exists()) {
                jetty = web_inf.addPath("jetty-web.xml");
            }
            if (!jetty.exists()) {
                jetty = web_inf.addPath("web-jetty.xml");
            }
            if (jetty.exists()) {
                final String[] old_server_classes = this._context.getServerClasses();
                try {
                    this._context.setServerClasses(null);
                    if (Log.isDebugEnabled()) {
                        Log.debug("Configure: " + jetty);
                    }
                    final XmlConfiguration jetty_config = new XmlConfiguration(jetty.getURL());
                    jetty_config.configure(this.getWebAppContext());
                }
                finally {
                    if (this._context.getServerClasses() == null) {
                        this._context.setServerClasses(old_server_classes);
                    }
                }
            }
        }
    }
    
    public void deconfigureWebApp() throws Exception {
    }
}
