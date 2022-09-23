// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ejb;

import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.Task;

public class BorlandGenerateClient extends Task
{
    static final String JAVA_MODE = "java";
    static final String FORK_MODE = "fork";
    boolean debug;
    File ejbjarfile;
    File clientjarfile;
    Path classpath;
    String mode;
    int version;
    
    public BorlandGenerateClient() {
        this.debug = false;
        this.ejbjarfile = null;
        this.clientjarfile = null;
        this.mode = "fork";
        this.version = 4;
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public void setMode(final String s) {
        this.mode = s;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setEjbjar(final File ejbfile) {
        this.ejbjarfile = ejbfile;
    }
    
    public void setClientjar(final File clientjar) {
        this.clientjarfile = clientjar;
    }
    
    public void setClasspath(final Path classpath) {
        if (this.classpath == null) {
            this.classpath = classpath;
        }
        else {
            this.classpath.append(classpath);
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.ejbjarfile == null || this.ejbjarfile.isDirectory()) {
            throw new BuildException("invalid ejb jar file.");
        }
        if (this.clientjarfile == null || this.clientjarfile.isDirectory()) {
            this.log("invalid or missing client jar file.", 3);
            final String ejbjarname = this.ejbjarfile.getAbsolutePath();
            String clientname = ejbjarname.substring(0, ejbjarname.lastIndexOf("."));
            clientname += "client.jar";
            this.clientjarfile = new File(clientname);
        }
        if (this.mode == null) {
            this.log("mode is null default mode  is java");
            this.setMode("java");
        }
        if (this.version != 5 && this.version != 4) {
            throw new BuildException("version " + this.version + " is not supported");
        }
        this.log("client jar file is " + this.clientjarfile);
        if (this.mode.equalsIgnoreCase("fork")) {
            this.executeFork();
        }
        else {
            this.executeJava();
        }
    }
    
    protected void executeJava() throws BuildException {
        try {
            if (this.version == 5) {
                throw new BuildException("java mode is supported only for previous version <=4");
            }
            this.log("mode : java");
            Java execTask = null;
            execTask = new Java(this);
            execTask.setDir(new File("."));
            execTask.setClassname("com.inprise.server.commandline.EJBUtilities");
            execTask.setClasspath(this.classpath.concatSystemClasspath());
            execTask.setFork(true);
            execTask.createArg().setValue("generateclient");
            if (this.debug) {
                execTask.createArg().setValue("-trace");
            }
            execTask.createArg().setValue("-short");
            execTask.createArg().setValue("-jarfile");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-single");
            execTask.createArg().setValue("-clientjarfile");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            this.log("Calling EJBUtilities", 3);
            execTask.execute();
        }
        catch (Exception e) {
            final String msg = "Exception while calling generateclient Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    protected void executeFork() throws BuildException {
        if (this.version == 4) {
            this.executeForkV4();
        }
        if (this.version == 5) {
            this.executeForkV5();
        }
    }
    
    protected void executeForkV4() throws BuildException {
        try {
            this.log("mode : fork 4", 4);
            final ExecTask execTask = new ExecTask(this);
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            execTask.createArg().setValue("generateclient");
            if (this.debug) {
                execTask.createArg().setValue("-trace");
            }
            execTask.createArg().setValue("-short");
            execTask.createArg().setValue("-jarfile");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-single");
            execTask.createArg().setValue("-clientjarfile");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            this.log("Calling iastool", 3);
            execTask.execute();
        }
        catch (Exception e) {
            final String msg = "Exception while calling generateclient Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
    
    protected void executeForkV5() throws BuildException {
        try {
            this.log("mode : fork 5", 4);
            final ExecTask execTask = new ExecTask(this);
            execTask.setDir(new File("."));
            execTask.setExecutable("iastool");
            if (this.debug) {
                execTask.createArg().setValue("-debug");
            }
            execTask.createArg().setValue("-genclient");
            execTask.createArg().setValue("-jars");
            execTask.createArg().setValue(this.ejbjarfile.getAbsolutePath());
            execTask.createArg().setValue("-target");
            execTask.createArg().setValue(this.clientjarfile.getAbsolutePath());
            execTask.createArg().setValue("-cp");
            execTask.createArg().setValue(this.classpath.toString());
            this.log("Calling iastool", 3);
            execTask.execute();
        }
        catch (Exception e) {
            final String msg = "Exception while calling generateclient Details: " + e.toString();
            throw new BuildException(msg, e);
        }
    }
}
