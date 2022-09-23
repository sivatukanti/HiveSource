// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.AntClassLoader;

public final class SplitClassLoader extends AntClassLoader
{
    private final String[] splitClasses;
    
    public SplitClassLoader(final ClassLoader parent, final Path path, final Project project, final String[] splitClasses) {
        super(parent, project, path, true);
        this.splitClasses = splitClasses;
    }
    
    @Override
    protected synchronized Class loadClass(final String classname, final boolean resolve) throws ClassNotFoundException {
        Class theClass = this.findLoadedClass(classname);
        if (theClass != null) {
            return theClass;
        }
        if (this.isSplit(classname)) {
            theClass = this.findClass(classname);
            if (resolve) {
                this.resolveClass(theClass);
            }
            return theClass;
        }
        return super.loadClass(classname, resolve);
    }
    
    private boolean isSplit(final String classname) {
        final String simplename = classname.substring(classname.lastIndexOf(46) + 1);
        for (int i = 0; i < this.splitClasses.length; ++i) {
            if (simplename.equals(this.splitClasses[i]) || simplename.startsWith(this.splitClasses[i] + '$')) {
                return true;
            }
        }
        return false;
    }
}
