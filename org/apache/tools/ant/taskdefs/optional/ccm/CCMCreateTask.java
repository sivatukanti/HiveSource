// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ccm;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;

public class CCMCreateTask extends Continuus implements ExecuteStreamHandler
{
    private String comment;
    private String platform;
    private String resolver;
    private String release;
    private String subSystem;
    private String task;
    public static final String FLAG_COMMENT = "/synopsis";
    public static final String FLAG_PLATFORM = "/plat";
    public static final String FLAG_RESOLVER = "/resolver";
    public static final String FLAG_RELEASE = "/release";
    public static final String FLAG_SUBSYSTEM = "/subsystem";
    public static final String FLAG_TASK = "/task";
    
    public CCMCreateTask() {
        this.comment = null;
        this.platform = null;
        this.resolver = null;
        this.release = null;
        this.subSystem = null;
        this.task = null;
        this.setCcmAction("create_task");
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        int result = 0;
        commandLine.setExecutable(this.getCcmCommand());
        commandLine.createArgument().setValue(this.getCcmAction());
        this.checkOptions(commandLine);
        result = this.run(commandLine, this);
        if (Execute.isFailure(result)) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
        final Commandline commandLine2 = new Commandline();
        commandLine2.setExecutable(this.getCcmCommand());
        commandLine2.createArgument().setValue("default_task");
        commandLine2.createArgument().setValue(this.getTask());
        this.log(commandLine.describeCommand(), 4);
        result = this.run(commandLine2);
        if (result != 0) {
            final String msg2 = "Failed executing: " + commandLine2.toString();
            throw new BuildException(msg2, this.getLocation());
        }
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.getComment() != null) {
            cmd.createArgument().setValue("/synopsis");
            cmd.createArgument().setValue("\"" + this.getComment() + "\"");
        }
        if (this.getPlatform() != null) {
            cmd.createArgument().setValue("/plat");
            cmd.createArgument().setValue(this.getPlatform());
        }
        if (this.getResolver() != null) {
            cmd.createArgument().setValue("/resolver");
            cmd.createArgument().setValue(this.getResolver());
        }
        if (this.getSubSystem() != null) {
            cmd.createArgument().setValue("/subsystem");
            cmd.createArgument().setValue("\"" + this.getSubSystem() + "\"");
        }
        if (this.getRelease() != null) {
            cmd.createArgument().setValue("/release");
            cmd.createArgument().setValue(this.getRelease());
        }
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(final String v) {
        this.comment = v;
    }
    
    public String getPlatform() {
        return this.platform;
    }
    
    public void setPlatform(final String v) {
        this.platform = v;
    }
    
    public String getResolver() {
        return this.resolver;
    }
    
    public void setResolver(final String v) {
        this.resolver = v;
    }
    
    public String getRelease() {
        return this.release;
    }
    
    public void setRelease(final String v) {
        this.release = v;
    }
    
    public String getSubSystem() {
        return this.subSystem;
    }
    
    public void setSubSystem(final String v) {
        this.subSystem = v;
    }
    
    public String getTask() {
        return this.task;
    }
    
    public void setTask(final String v) {
        this.task = v;
    }
    
    public void start() throws IOException {
    }
    
    public void stop() {
    }
    
    public void setProcessInputStream(final OutputStream param1) throws IOException {
    }
    
    public void setProcessErrorStream(final InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final String s = reader.readLine();
        if (s != null) {
            this.log("err " + s, 4);
        }
    }
    
    public void setProcessOutputStream(final InputStream is) throws IOException {
        String buffer = "";
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            buffer = reader.readLine();
            if (buffer != null) {
                this.log("buffer:" + buffer, 4);
                String taskstring = buffer.substring(buffer.indexOf(32)).trim();
                taskstring = taskstring.substring(0, taskstring.lastIndexOf(32)).trim();
                this.setTask(taskstring);
                this.log("task is " + this.getTask(), 4);
            }
        }
        catch (NullPointerException npe) {
            this.log("error procession stream , null pointer exception", 0);
            npe.printStackTrace();
            throw new BuildException(npe.getClass().getName());
        }
        catch (Exception e) {
            this.log("error procession stream " + e.getMessage(), 0);
            throw new BuildException(e.getMessage());
        }
    }
}
