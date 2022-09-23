// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp.compilers;

import org.apache.tools.ant.AntClassLoader;
import java.io.File;
import org.apache.tools.ant.taskdefs.optional.jsp.JspC;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.jsp.JspMangler;

public class JasperC extends DefaultJspCompilerAdapter
{
    JspMangler mangler;
    
    public JasperC(final JspMangler mangler) {
        this.mangler = mangler;
    }
    
    public boolean execute() throws BuildException {
        this.getJspc().log("Using jasper compiler", 3);
        final CommandlineJava cmd = this.setupJasperCommand();
        try {
            final Java java = new Java(this.owner);
            final Path p = this.getClasspath();
            if (this.getJspc().getClasspath() != null) {
                this.getProject().log("using user supplied classpath: " + p, 4);
            }
            else {
                this.getProject().log("using system classpath: " + p, 4);
            }
            java.setClasspath(p);
            java.setDir(this.getProject().getBaseDir());
            java.setClassname("org.apache.jasper.JspC");
            final String[] args = cmd.getJavaCommand().getArguments();
            for (int i = 0; i < args.length; ++i) {
                java.createArg().setValue(args[i]);
            }
            java.setFailonerror(this.getJspc().getFailonerror());
            java.setFork(true);
            java.setTaskName("jasperc");
            java.execute();
            return true;
        }
        catch (Exception ex) {
            if (ex instanceof BuildException) {
                throw (BuildException)ex;
            }
            throw new BuildException("Error running jsp compiler: ", ex, this.getJspc().getLocation());
        }
        finally {
            this.getJspc().deleteEmptyJavaFiles();
        }
    }
    
    private CommandlineJava setupJasperCommand() {
        final CommandlineJava cmd = new CommandlineJava();
        final JspC jspc = this.getJspc();
        this.addArg(cmd, "-d", jspc.getDestdir());
        this.addArg(cmd, "-p", jspc.getPackage());
        if (!this.isTomcat5x()) {
            this.addArg(cmd, "-v" + jspc.getVerbose());
        }
        else {
            this.getProject().log("this task doesn't support Tomcat 5.x properly, please use the Tomcat provided jspc task instead");
        }
        this.addArg(cmd, "-uriroot", jspc.getUriroot());
        this.addArg(cmd, "-uribase", jspc.getUribase());
        this.addArg(cmd, "-ieplugin", jspc.getIeplugin());
        this.addArg(cmd, "-webinc", jspc.getWebinc());
        this.addArg(cmd, "-webxml", jspc.getWebxml());
        this.addArg(cmd, "-die9");
        if (jspc.isMapped()) {
            this.addArg(cmd, "-mapped");
        }
        if (jspc.getWebApp() != null) {
            final File dir = jspc.getWebApp().getDirectory();
            this.addArg(cmd, "-webapp", dir);
        }
        this.logAndAddFilesToCompile(this.getJspc(), this.getJspc().getCompileList(), cmd);
        return cmd;
    }
    
    public JspMangler createMangler() {
        return this.mangler;
    }
    
    private Path getClasspath() {
        Path p = this.getJspc().getClasspath();
        if (p == null) {
            p = new Path(this.getProject());
            return p.concatSystemClasspath("only");
        }
        return p.concatSystemClasspath("ignore");
    }
    
    private boolean isTomcat5x() {
        AntClassLoader l = null;
        try {
            l = this.getProject().createClassLoader(this.getClasspath());
            l.loadClass("org.apache.jasper.tagplugins.jstl.If");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
        finally {
            if (l != null) {
                l.cleanup();
            }
        }
    }
}
