// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.tools.ant.types.Commandline;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.OutputStream;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;

public class SunRmic extends DefaultRmicAdapter
{
    public static final String RMIC_CLASSNAME = "sun.rmi.rmic.Main";
    public static final String COMPILER_NAME = "sun";
    public static final String RMIC_EXECUTABLE = "rmic";
    public static final String ERROR_NO_RMIC_ON_CLASSPATH = "Cannot use SUN rmic, as it is not available.  A common solution is to set the environment variable JAVA_HOME";
    public static final String ERROR_RMIC_FAILED = "Error starting SUN rmic: ";
    
    public boolean execute() throws BuildException {
        this.getRmic().log("Using SUN rmic compiler", 3);
        final Commandline cmd = this.setupRmicCommand();
        final LogOutputStream logstr = new LogOutputStream(this.getRmic(), 1);
        try {
            final Class c = Class.forName("sun.rmi.rmic.Main");
            final Constructor cons = c.getConstructor(OutputStream.class, String.class);
            final Object rmic = cons.newInstance(logstr, "rmic");
            final Method doRmic = c.getMethod("compile", String[].class);
            final Boolean ok = (Boolean)doRmic.invoke(rmic, cmd.getArguments());
            return ok;
        }
        catch (ClassNotFoundException ex2) {
            throw new BuildException("Cannot use SUN rmic, as it is not available.  A common solution is to set the environment variable JAVA_HOME", this.getRmic().getLocation());
        }
        catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            }
            throw new BuildException("Error starting SUN rmic: ", ex, this.getRmic().getLocation());
        }
        finally {
            try {
                logstr.close();
            }
            catch (IOException e) {
                throw new BuildException(e);
            }
        }
    }
    
    @Override
    protected String[] preprocessCompilerArgs(final String[] compilerArgs) {
        return this.filterJvmCompilerArgs(compilerArgs);
    }
}
