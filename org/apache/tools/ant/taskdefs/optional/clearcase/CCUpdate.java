// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCUpdate extends ClearCase
{
    private boolean mGraphical;
    private boolean mOverwrite;
    private boolean mRename;
    private boolean mCtime;
    private boolean mPtime;
    private String mLog;
    public static final String FLAG_GRAPHICAL = "-graphical";
    public static final String FLAG_LOG = "-log";
    public static final String FLAG_OVERWRITE = "-overwrite";
    public static final String FLAG_NOVERWRITE = "-noverwrite";
    public static final String FLAG_RENAME = "-rename";
    public static final String FLAG_CURRENTTIME = "-ctime";
    public static final String FLAG_PRESERVETIME = "-ptime";
    
    public CCUpdate() {
        this.mGraphical = false;
        this.mOverwrite = false;
        this.mRename = false;
        this.mCtime = false;
        this.mPtime = false;
        this.mLog = null;
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        final Project aProj = this.getProject();
        int result = 0;
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("update");
        this.checkOptions(commandLine);
        this.getProject().log(commandLine.toString(), 4);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        result = this.run(commandLine);
        if (Execute.isFailure(result) && this.getFailOnErr()) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.getGraphical()) {
            cmd.createArgument().setValue("-graphical");
        }
        else {
            if (this.getOverwrite()) {
                cmd.createArgument().setValue("-overwrite");
            }
            else if (this.getRename()) {
                cmd.createArgument().setValue("-rename");
            }
            else {
                cmd.createArgument().setValue("-noverwrite");
            }
            if (this.getCurrentTime()) {
                cmd.createArgument().setValue("-ctime");
            }
            else if (this.getPreserveTime()) {
                cmd.createArgument().setValue("-ptime");
            }
            this.getLogCommand(cmd);
        }
        cmd.createArgument().setValue(this.getViewPath());
    }
    
    public void setGraphical(final boolean graphical) {
        this.mGraphical = graphical;
    }
    
    public boolean getGraphical() {
        return this.mGraphical;
    }
    
    public void setOverwrite(final boolean ow) {
        this.mOverwrite = ow;
    }
    
    public boolean getOverwrite() {
        return this.mOverwrite;
    }
    
    public void setRename(final boolean ren) {
        this.mRename = ren;
    }
    
    public boolean getRename() {
        return this.mRename;
    }
    
    public void setCurrentTime(final boolean ct) {
        this.mCtime = ct;
    }
    
    public boolean getCurrentTime() {
        return this.mCtime;
    }
    
    public void setPreserveTime(final boolean pt) {
        this.mPtime = pt;
    }
    
    public boolean getPreserveTime() {
        return this.mPtime;
    }
    
    public void setLog(final String log) {
        this.mLog = log;
    }
    
    public String getLog() {
        return this.mLog;
    }
    
    private void getLogCommand(final Commandline cmd) {
        if (this.getLog() == null) {
            return;
        }
        cmd.createArgument().setValue("-log");
        cmd.createArgument().setValue(this.getLog());
    }
}
