// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.component.AbstractLifeCycle;

public abstract class AbstractLeakPreventer extends AbstractLifeCycle
{
    protected static final Logger LOG;
    
    public abstract void prevent(final ClassLoader p0);
    
    @Override
    protected void doStart() throws Exception {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            this.prevent(this.getClass().getClassLoader());
            super.doStart();
        }
        finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }
    
    static {
        LOG = Log.getLogger(AbstractLeakPreventer.class);
    }
}
