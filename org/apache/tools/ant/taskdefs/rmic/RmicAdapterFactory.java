// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Task;

public final class RmicAdapterFactory
{
    public static final String ERROR_UNKNOWN_COMPILER = "Class not found: ";
    public static final String ERROR_NOT_RMIC_ADAPTER = "Class of unexpected Type: ";
    public static final String DEFAULT_COMPILER = "default";
    
    private RmicAdapterFactory() {
    }
    
    public static RmicAdapter getRmic(final String rmicType, final Task task) throws BuildException {
        return getRmic(rmicType, task, null);
    }
    
    public static RmicAdapter getRmic(String rmicType, final Task task, final Path classpath) throws BuildException {
        if ("default".equalsIgnoreCase(rmicType) || rmicType.length() == 0) {
            rmicType = (KaffeRmic.isAvailable() ? "kaffe" : "sun");
        }
        if ("sun".equalsIgnoreCase(rmicType)) {
            return new SunRmic();
        }
        if ("kaffe".equalsIgnoreCase(rmicType)) {
            return new KaffeRmic();
        }
        if ("weblogic".equalsIgnoreCase(rmicType)) {
            return new WLRmic();
        }
        if ("forking".equalsIgnoreCase(rmicType)) {
            return new ForkingSunRmic();
        }
        if ("xnew".equalsIgnoreCase(rmicType)) {
            return new XNewRmic();
        }
        return resolveClassName(rmicType, task.getProject().createClassLoader(classpath));
    }
    
    private static RmicAdapter resolveClassName(final String className, final ClassLoader loader) throws BuildException {
        return (RmicAdapter)ClasspathUtils.newInstance(className, (loader != null) ? loader : RmicAdapterFactory.class.getClassLoader(), RmicAdapter.class);
    }
}
