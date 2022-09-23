// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCCheckin extends ClearCase
{
    private String mComment;
    private String mCfile;
    private boolean mNwarn;
    private boolean mPtime;
    private boolean mKeep;
    private boolean mIdentical;
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_NOWARN = "-nwarn";
    public static final String FLAG_PRESERVETIME = "-ptime";
    public static final String FLAG_KEEPCOPY = "-keep";
    public static final String FLAG_IDENTICAL = "-identical";
    
    public CCCheckin() {
        this.mComment = null;
        this.mCfile = null;
        this.mNwarn = false;
        this.mPtime = false;
        this.mKeep = false;
        this.mIdentical = true;
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
        commandLine.createArgument().setValue("checkin");
        this.checkOptions(commandLine);
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
        if (this.getComment() != null) {
            this.getCommentCommand(cmd);
        }
        else if (this.getCommentFile() != null) {
            this.getCommentFileCommand(cmd);
        }
        else {
            cmd.createArgument().setValue("-nc");
        }
        if (this.getNoWarn()) {
            cmd.createArgument().setValue("-nwarn");
        }
        if (this.getPreserveTime()) {
            cmd.createArgument().setValue("-ptime");
        }
        if (this.getKeepCopy()) {
            cmd.createArgument().setValue("-keep");
        }
        if (this.getIdentical()) {
            cmd.createArgument().setValue("-identical");
        }
        cmd.createArgument().setValue(this.getViewPath());
    }
    
    public void setComment(final String comment) {
        this.mComment = comment;
    }
    
    public String getComment() {
        return this.mComment;
    }
    
    public void setCommentFile(final String cfile) {
        this.mCfile = cfile;
    }
    
    public String getCommentFile() {
        return this.mCfile;
    }
    
    public void setNoWarn(final boolean nwarn) {
        this.mNwarn = nwarn;
    }
    
    public boolean getNoWarn() {
        return this.mNwarn;
    }
    
    public void setPreserveTime(final boolean ptime) {
        this.mPtime = ptime;
    }
    
    public boolean getPreserveTime() {
        return this.mPtime;
    }
    
    public void setKeepCopy(final boolean keep) {
        this.mKeep = keep;
    }
    
    public boolean getKeepCopy() {
        return this.mKeep;
    }
    
    public void setIdentical(final boolean identical) {
        this.mIdentical = identical;
    }
    
    public boolean getIdentical() {
        return this.mIdentical;
    }
    
    private void getCommentCommand(final Commandline cmd) {
        if (this.getComment() != null) {
            cmd.createArgument().setValue("-c");
            cmd.createArgument().setValue(this.getComment());
        }
    }
    
    private void getCommentFileCommand(final Commandline cmd) {
        if (this.getCommentFile() != null) {
            cmd.createArgument().setValue("-cfile");
            cmd.createArgument().setValue(this.getCommentFile());
        }
    }
}
