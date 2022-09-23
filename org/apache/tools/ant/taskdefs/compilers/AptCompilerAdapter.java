// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import java.lang.reflect.Method;
import org.apache.tools.ant.BuildException;
import java.util.Enumeration;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Apt;

public class AptCompilerAdapter extends DefaultCompilerAdapter
{
    private static final int APT_COMPILER_SUCCESS = 0;
    public static final String APT_ENTRY_POINT = "com.sun.tools.apt.Main";
    public static final String APT_METHOD_NAME = "process";
    
    protected Apt getApt() {
        return (Apt)this.getJavac();
    }
    
    static void setAptCommandlineSwitches(final Apt apt, final Commandline cmd) {
        if (!apt.isCompile()) {
            cmd.createArgument().setValue("-nocompile");
        }
        final String factory = apt.getFactory();
        if (factory != null) {
            cmd.createArgument().setValue("-factory");
            cmd.createArgument().setValue(factory);
        }
        final Path factoryPath = apt.getFactoryPath();
        if (factoryPath != null) {
            cmd.createArgument().setValue("-factorypath");
            cmd.createArgument().setPath(factoryPath);
        }
        final File preprocessDir = apt.getPreprocessDir();
        if (preprocessDir != null) {
            cmd.createArgument().setValue("-s");
            cmd.createArgument().setFile(preprocessDir);
        }
        final Vector options = apt.getOptions();
        final Enumeration elements = options.elements();
        StringBuffer arg = null;
        while (elements.hasMoreElements()) {
            final Apt.Option opt = elements.nextElement();
            arg = new StringBuffer();
            arg.append("-A").append(opt.getName());
            if (opt.getValue() != null) {
                arg.append("=").append(opt.getValue());
            }
            cmd.createArgument().setValue(arg.toString());
        }
    }
    
    protected void setAptCommandlineSwitches(final Commandline cmd) {
        final Apt apt = this.getApt();
        setAptCommandlineSwitches(apt, cmd);
    }
    
    public boolean execute() throws BuildException {
        this.attributes.log("Using apt compiler", 3);
        final Commandline cmd = this.setupModernJavacCommand();
        this.setAptCommandlineSwitches(cmd);
        try {
            final Class c = Class.forName("com.sun.tools.apt.Main");
            final Object compiler = c.newInstance();
            final Method compile = c.getMethod("process", new String[0].getClass());
            final int result = (int)compile.invoke(compiler, cmd.getArguments());
            return result == 0;
        }
        catch (BuildException be) {
            throw be;
        }
        catch (Exception ex) {
            throw new BuildException("Error starting apt compiler", ex, this.location);
        }
    }
}
