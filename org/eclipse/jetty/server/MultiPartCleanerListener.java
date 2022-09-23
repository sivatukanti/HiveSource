// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class MultiPartCleanerListener implements ServletRequestListener
{
    public static final MultiPartCleanerListener INSTANCE;
    
    protected MultiPartCleanerListener() {
    }
    
    @Override
    public void requestDestroyed(final ServletRequestEvent sre) {
        final MultiPartInputStreamParser mpis = (MultiPartInputStreamParser)sre.getServletRequest().getAttribute("org.eclipse.jetty.multiPartInputStream");
        if (mpis != null) {
            final ContextHandler.Context context = (ContextHandler.Context)sre.getServletRequest().getAttribute("org.eclipse.jetty.multiPartContext");
            if (context == sre.getServletContext()) {
                try {
                    mpis.deleteParts();
                }
                catch (MultiException e) {
                    sre.getServletContext().log("Errors deleting multipart tmp files", e);
                }
            }
        }
    }
    
    @Override
    public void requestInitialized(final ServletRequestEvent sre) {
    }
    
    static {
        INSTANCE = new MultiPartCleanerListener();
    }
}
