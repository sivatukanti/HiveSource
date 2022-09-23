// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.preventers;

public class Java2DLeakPreventer extends AbstractLeakPreventer
{
    @Override
    public void prevent(final ClassLoader loader) {
        try {
            Class.forName("sun.java2d.Disposer", true, loader);
        }
        catch (ClassNotFoundException e) {
            Java2DLeakPreventer.LOG.ignore(e);
        }
    }
}
