// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.webapp;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.log.Logger;

public abstract class DiscoveredAnnotation
{
    private static final Logger LOG;
    protected WebAppContext _context;
    protected String _className;
    protected Class<?> _clazz;
    protected Resource _resource;
    
    public abstract void apply();
    
    public DiscoveredAnnotation(final WebAppContext context, final String className) {
        this(context, className, null);
    }
    
    public DiscoveredAnnotation(final WebAppContext context, final String className, final Resource resource) {
        this._context = context;
        this._className = className;
        this._resource = resource;
    }
    
    public Resource getResource() {
        return this._resource;
    }
    
    public Class<?> getTargetClass() {
        if (this._clazz != null) {
            return this._clazz;
        }
        this.loadClass();
        return this._clazz;
    }
    
    private void loadClass() {
        if (this._clazz != null) {
            return;
        }
        if (this._className == null) {
            return;
        }
        try {
            this._clazz = (Class<?>)Loader.loadClass(null, this._className);
        }
        catch (Exception e) {
            DiscoveredAnnotation.LOG.warn(e);
        }
    }
    
    static {
        LOG = Log.getLogger(DiscoveredAnnotation.class);
    }
}
