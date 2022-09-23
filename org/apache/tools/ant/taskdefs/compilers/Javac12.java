// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.JavaEnvUtils;
import java.io.OutputStream;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogOutputStream;

public class Javac12 extends DefaultCompilerAdapter
{
    protected static final String CLASSIC_COMPILER_CLASSNAME = "sun.tools.javac.Main";
    
    public boolean execute() throws BuildException {
        this.attributes.log("Using classic compiler", 3);
        final Commandline cmd = this.setupJavacCommand(true);
        final OutputStream logstr = new LogOutputStream(this.attributes, 1);
        try {
            final Class c = Class.forName("sun.tools.javac.Main");
            final Constructor cons = c.getConstructor(OutputStream.class, String.class);
            final Object compiler = cons.newInstance(logstr, "javac");
            final Method compile = c.getMethod("compile", String[].class);
            final Boolean ok = (Boolean)compile.invoke(compiler, cmd.getArguments());
            return ok;
        }
        catch (ClassNotFoundException ex2) {
            throw new BuildException("Cannot use classic compiler , as it is not available. \n A common solution is to set the environment variable JAVA_HOME to your jdk directory.\nIt is currently set to \"" + JavaEnvUtils.getJavaHome() + "\"", this.location);
        }
        catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            }
            throw new BuildException("Error starting classic compiler: ", ex, this.location);
        }
        finally {
            FileUtils.close(logstr);
        }
    }
}
