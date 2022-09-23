// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.taskdefs.optional.jsp.Jasper41Mangler;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;
import org.apache.tools.ant.taskdefs.optional.jsp.JspNameMangler;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Task;

public final class JspCompilerAdapterFactory
{
    private JspCompilerAdapterFactory() {
    }
    
    public static JspCompilerAdapter getCompiler(final String compilerType, final Task task) throws BuildException {
        return getCompiler(compilerType, task, task.getProject().createClassLoader(null));
    }
    
    public static JspCompilerAdapter getCompiler(final String compilerType, final Task task, final AntClassLoader loader) throws BuildException {
        if (compilerType.equalsIgnoreCase("jasper")) {
            return new JasperC(new JspNameMangler());
        }
        if (compilerType.equalsIgnoreCase("jasper41")) {
            return new JasperC(new Jasper41Mangler());
        }
        return resolveClassName(compilerType, loader);
    }
    
    private static JspCompilerAdapter resolveClassName(final String className, final AntClassLoader classloader) throws BuildException {
        try {
            final Class c = classloader.findClass(className);
            final Object o = c.newInstance();
            return (JspCompilerAdapter)o;
        }
        catch (ClassNotFoundException cnfe) {
            throw new BuildException(className + " can't be found.", cnfe);
        }
        catch (ClassCastException cce) {
            throw new BuildException(className + " isn't the classname of " + "a compiler adapter.", cce);
        }
        catch (Throwable t) {
            throw new BuildException(className + " caused an interesting " + "exception.", t);
        }
    }
}
