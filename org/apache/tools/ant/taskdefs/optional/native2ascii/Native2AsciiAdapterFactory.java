// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.native2ascii;

import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.JavaEnvUtils;

public class Native2AsciiAdapterFactory
{
    public static String getDefault() {
        if (JavaEnvUtils.isKaffe() || JavaEnvUtils.isClasspathBased()) {
            return "kaffe";
        }
        return "sun";
    }
    
    public static Native2AsciiAdapter getAdapter(final String choice, final ProjectComponent log) throws BuildException {
        return getAdapter(choice, log, null);
    }
    
    public static Native2AsciiAdapter getAdapter(final String choice, final ProjectComponent log, final Path classpath) throws BuildException {
        if (((JavaEnvUtils.isKaffe() || JavaEnvUtils.isClasspathBased()) && choice == null) || "kaffe".equals(choice)) {
            return new KaffeNative2Ascii();
        }
        if ("sun".equals(choice)) {
            return new SunNative2Ascii();
        }
        if (choice != null) {
            return resolveClassName(choice, log.getProject().createClassLoader(classpath));
        }
        return new SunNative2Ascii();
    }
    
    private static Native2AsciiAdapter resolveClassName(final String className, final ClassLoader loader) throws BuildException {
        return (Native2AsciiAdapter)ClasspathUtils.newInstance(className, (loader != null) ? loader : Native2AsciiAdapterFactory.class.getClassLoader(), Native2AsciiAdapter.class);
    }
}
