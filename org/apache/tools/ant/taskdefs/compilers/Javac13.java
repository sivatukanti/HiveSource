// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import java.lang.reflect.Method;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.BuildException;

public class Javac13 extends DefaultCompilerAdapter
{
    private static final int MODERN_COMPILER_SUCCESS = 0;
    
    public boolean execute() throws BuildException {
        this.attributes.log("Using modern compiler", 3);
        final Commandline cmd = this.setupModernJavacCommand();
        try {
            final Class c = Class.forName("com.sun.tools.javac.Main");
            final Object compiler = c.newInstance();
            final Method compile = c.getMethod("compile", new String[0].getClass());
            final int result = (int)compile.invoke(compiler, cmd.getArguments());
            return result == 0;
        }
        catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            }
            throw new BuildException("Error starting modern compiler", ex, this.location);
        }
    }
}
