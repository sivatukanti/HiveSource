// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import javax.imageio.ImageIO;

public class AppContextLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        if (AppContextLeakPreventer.LOG.isDebugEnabled()) {
            AppContextLeakPreventer.LOG.debug("Pinning classloader for AppContext.getContext() with " + loader, new Object[0]);
        }
        ImageIO.getUseCache();
    }
}
