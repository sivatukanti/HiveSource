// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import java.lang.reflect.Method;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.BuildException;

public class WLRmic extends DefaultRmicAdapter
{
    public static final String WLRMIC_CLASSNAME = "weblogic.rmic";
    public static final String COMPILER_NAME = "weblogic";
    public static final String ERROR_NO_WLRMIC_ON_CLASSPATH = "Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option";
    public static final String ERROR_WLRMIC_FAILED = "Error starting WebLogic rmic: ";
    public static final String WL_RMI_STUB_SUFFIX = "_WLStub";
    public static final String WL_RMI_SKEL_SUFFIX = "_WLSkel";
    public static final String UNSUPPORTED_STUB_OPTION = "Unsupported stub option: ";
    
    public boolean execute() throws BuildException {
        this.getRmic().log("Using WebLogic rmic", 3);
        final Commandline cmd = this.setupRmicCommand(new String[] { "-noexit" });
        AntClassLoader loader = null;
        try {
            Class c = null;
            if (this.getRmic().getClasspath() == null) {
                c = Class.forName("weblogic.rmic");
            }
            else {
                loader = this.getRmic().getProject().createClassLoader(this.getRmic().getClasspath());
                c = Class.forName("weblogic.rmic", true, loader);
            }
            final Method doRmic = c.getMethod("main", String[].class);
            doRmic.invoke(null, cmd.getArguments());
            return true;
        }
        catch (ClassNotFoundException ex2) {
            throw new BuildException("Cannot use WebLogic rmic, as it is not available. Add it to Ant's classpath with the -lib option", this.getRmic().getLocation());
        }
        catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            }
            throw new BuildException("Error starting WebLogic rmic: ", ex, this.getRmic().getLocation());
        }
        finally {
            if (loader != null) {
                loader.cleanup();
            }
        }
    }
    
    public String getStubClassSuffix() {
        return "_WLStub";
    }
    
    public String getSkelClassSuffix() {
        return "_WLSkel";
    }
    
    @Override
    protected String[] preprocessCompilerArgs(final String[] compilerArgs) {
        return this.filterJvmCompilerArgs(compilerArgs);
    }
    
    @Override
    protected String addStubVersionOptions() {
        final String stubVersion = this.getRmic().getStubVersion();
        if (null != stubVersion) {
            this.getRmic().log("Unsupported stub option: " + stubVersion, 1);
        }
        return null;
    }
}
