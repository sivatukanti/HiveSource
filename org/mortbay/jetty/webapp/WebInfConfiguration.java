// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.webapp;

import org.mortbay.resource.Resource;
import org.mortbay.log.Log;

public class WebInfConfiguration implements Configuration
{
    protected WebAppContext _context;
    
    public void setWebAppContext(final WebAppContext context) {
        this._context = context;
    }
    
    public WebAppContext getWebAppContext() {
        return this._context;
    }
    
    public void configureClassLoader() throws Exception {
        if (this._context.isStarted()) {
            if (Log.isDebugEnabled()) {
                Log.debug("Cannot configure webapp after it is started");
            }
            return;
        }
        final Resource web_inf = this._context.getWebInf();
        if (web_inf != null && web_inf.isDirectory() && this._context.getClassLoader() instanceof WebAppClassLoader) {
            final Resource classes = web_inf.addPath("classes/");
            if (classes.exists()) {
                ((WebAppClassLoader)this._context.getClassLoader()).addClassPath(classes.toString());
            }
            final Resource lib = web_inf.addPath("lib/");
            if (lib.exists() || lib.isDirectory()) {
                ((WebAppClassLoader)this._context.getClassLoader()).addJars(lib);
            }
        }
    }
    
    public void configureDefaults() throws Exception {
    }
    
    public void configureWebApp() throws Exception {
    }
    
    public void deconfigureWebApp() throws Exception {
    }
}
