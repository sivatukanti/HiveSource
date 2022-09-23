// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

import java.awt.Toolkit;

public class AWTLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        if (AWTLeakPreventer.LOG.isDebugEnabled()) {
            AWTLeakPreventer.LOG.debug("Pinning classloader for java.awt.EventQueue using " + loader, new Object[0]);
        }
        Toolkit.getDefaultToolkit();
    }
}
