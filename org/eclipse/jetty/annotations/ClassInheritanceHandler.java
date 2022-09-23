// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.annotations;

import org.eclipse.jetty.util.log.Log;
import java.util.List;
import org.eclipse.jetty.util.MultiMap;
import org.eclipse.jetty.util.log.Logger;

public class ClassInheritanceHandler implements AnnotationParser.ClassHandler
{
    private static final Logger LOG;
    MultiMap _inheritanceMap;
    
    public ClassInheritanceHandler() {
        this._inheritanceMap = new MultiMap();
    }
    
    public void handle(final String className, final int version, final int access, final String signature, final String superName, final String[] interfaces) {
        try {
            for (int i = 0; interfaces != null && i < interfaces.length; ++i) {
                this._inheritanceMap.add((Object)interfaces[i], (Object)className);
            }
            if (!"java.lang.Object".equals(superName)) {
                this._inheritanceMap.add((Object)superName, (Object)className);
            }
        }
        catch (Exception e) {
            ClassInheritanceHandler.LOG.warn(e);
        }
    }
    
    public List getClassNamesExtendingOrImplementing(final String className) {
        return this._inheritanceMap.getValues((Object)className);
    }
    
    public MultiMap getMap() {
        return this._inheritanceMap;
    }
    
    static {
        LOG = Log.getLogger(ClassInheritanceHandler.class);
    }
}
