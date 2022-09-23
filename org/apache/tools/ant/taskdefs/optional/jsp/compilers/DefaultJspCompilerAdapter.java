// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.Project;
import java.io.File;
import java.util.Enumeration;
import org.apache.tools.ant.types.CommandlineJava;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;

public abstract class DefaultJspCompilerAdapter implements JspCompilerAdapter
{
    private static String lSep;
    protected JspC owner;
    
    protected void logAndAddFilesToCompile(final JspC jspc, final Vector compileList, final CommandlineJava cmd) {
        jspc.log("Compilation " + cmd.describeJavaCommand(), 3);
        final StringBuffer niceSourceList = new StringBuffer("File");
        if (compileList.size() != 1) {
            niceSourceList.append("s");
        }
        niceSourceList.append(" to be compiled:");
        niceSourceList.append(DefaultJspCompilerAdapter.lSep);
        final Enumeration e = compileList.elements();
        while (e.hasMoreElements()) {
            final String arg = e.nextElement();
            cmd.createArgument().setValue(arg);
            niceSourceList.append("    ");
            niceSourceList.append(arg);
            niceSourceList.append(DefaultJspCompilerAdapter.lSep);
        }
        jspc.log(niceSourceList.toString(), 3);
    }
    
    public void setJspc(final JspC owner) {
        this.owner = owner;
    }
    
    public JspC getJspc() {
        return this.owner;
    }
    
    protected void addArg(final CommandlineJava cmd, final String argument) {
        if (argument != null && argument.length() != 0) {
            cmd.createArgument().setValue(argument);
        }
    }
    
    protected void addArg(final CommandlineJava cmd, final String argument, final String value) {
        if (value != null) {
            cmd.createArgument().setValue(argument);
            cmd.createArgument().setValue(value);
        }
    }
    
    protected void addArg(final CommandlineJava cmd, final String argument, final File file) {
        if (file != null) {
            cmd.createArgument().setValue(argument);
            cmd.createArgument().setFile(file);
        }
    }
    
    public boolean implementsOwnDependencyChecking() {
        return false;
    }
    
    public Project getProject() {
        return this.getJspc().getProject();
    }
    
    static {
        DefaultJspCompilerAdapter.lSep = System.getProperty("line.separator");
    }
}
