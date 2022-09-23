// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.javah;

import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.JavaEnvUtils;

public class JavahAdapterFactory
{
    public static String getDefault() {
        if (JavaEnvUtils.isKaffe()) {
            return "kaffeh";
        }
        if (JavaEnvUtils.isGij()) {
            return "gcjh";
        }
        return "sun";
    }
    
    public static JavahAdapter getAdapter(final String choice, final ProjectComponent log) throws BuildException {
        return getAdapter(choice, log, null);
    }
    
    public static JavahAdapter getAdapter(final String choice, final ProjectComponent log, final Path classpath) throws BuildException {
        if ((JavaEnvUtils.isKaffe() && choice == null) || "kaffeh".equals(choice)) {
            return new Kaffeh();
        }
        if ((JavaEnvUtils.isGij() && choice == null) || "gcjh".equals(choice)) {
            return new Gcjh();
        }
        if ("sun".equals(choice)) {
            return new SunJavah();
        }
        if (choice != null) {
            return resolveClassName(choice, log.getProject().createClassLoader(classpath));
        }
        return new SunJavah();
    }
    
    private static JavahAdapter resolveClassName(final String className, final ClassLoader loader) throws BuildException {
        return (JavahAdapter)ClasspathUtils.newInstance(className, (loader != null) ? loader : JavahAdapterFactory.class.getClassLoader(), JavahAdapter.class);
    }
}
