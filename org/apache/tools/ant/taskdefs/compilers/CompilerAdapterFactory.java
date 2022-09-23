// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.Task;

public final class CompilerAdapterFactory
{
    private static final String MODERN_COMPILER = "com.sun.tools.javac.Main";
    
    private CompilerAdapterFactory() {
    }
    
    public static CompilerAdapter getCompiler(final String compilerType, final Task task) throws BuildException {
        return getCompiler(compilerType, task, null);
    }
    
    public static CompilerAdapter getCompiler(String compilerType, final Task task, final Path classpath) throws BuildException {
        if (compilerType.equalsIgnoreCase("jikes")) {
            return new Jikes();
        }
        if (compilerType.equalsIgnoreCase("extjavac")) {
            return new JavacExternal();
        }
        if (compilerType.equalsIgnoreCase("classic") || compilerType.equalsIgnoreCase("javac1.1") || compilerType.equalsIgnoreCase("javac1.2")) {
            task.log("This version of java does not support the classic compiler; upgrading to modern", 1);
            compilerType = "modern";
        }
        if (compilerType.equalsIgnoreCase("modern") || compilerType.equalsIgnoreCase("javac1.3") || compilerType.equalsIgnoreCase("javac1.4") || compilerType.equalsIgnoreCase("javac1.5") || compilerType.equalsIgnoreCase("javac1.6") || compilerType.equalsIgnoreCase("javac1.7") || compilerType.equalsIgnoreCase("javac1.8")) {
            if (doesModernCompilerExist()) {
                return new Javac13();
            }
            throw new BuildException("Unable to find a javac compiler;\ncom.sun.tools.javac.Main is not on the classpath.\nPerhaps JAVA_HOME does not point to the JDK.\nIt is currently set to \"" + JavaEnvUtils.getJavaHome() + "\"");
        }
        else {
            if (compilerType.equalsIgnoreCase("jvc") || compilerType.equalsIgnoreCase("microsoft")) {
                return new Jvc();
            }
            if (compilerType.equalsIgnoreCase("kjc")) {
                return new Kjc();
            }
            if (compilerType.equalsIgnoreCase("gcj")) {
                return new Gcj();
            }
            if (compilerType.equalsIgnoreCase("sj") || compilerType.equalsIgnoreCase("symantec")) {
                return new Sj();
            }
            return resolveClassName(compilerType, task.getProject().createClassLoader(classpath));
        }
    }
    
    private static boolean doesModernCompilerExist() {
        try {
            Class.forName("com.sun.tools.javac.Main");
            return true;
        }
        catch (ClassNotFoundException cnfe) {
            try {
                final ClassLoader cl = CompilerAdapterFactory.class.getClassLoader();
                if (cl != null) {
                    cl.loadClass("com.sun.tools.javac.Main");
                    return true;
                }
            }
            catch (ClassNotFoundException ex) {}
            return false;
        }
    }
    
    private static CompilerAdapter resolveClassName(final String className, final ClassLoader loader) throws BuildException {
        return (CompilerAdapter)ClasspathUtils.newInstance(className, (loader != null) ? loader : CompilerAdapterFactory.class.getClassLoader(), CompilerAdapter.class);
    }
}
