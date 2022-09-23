// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCMkbl extends ClearCase
{
    private String mComment;
    private String mCfile;
    private String mBaselineRootName;
    private boolean mNwarn;
    private boolean mIdentical;
    private boolean mFull;
    private boolean mNlabel;
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_IDENTICAL = "-identical";
    public static final String FLAG_INCREMENTAL = "-incremental";
    public static final String FLAG_FULL = "-full";
    public static final String FLAG_NLABEL = "-nlabel";
    
    public CCMkbl() {
        this.mComment = null;
        this.mCfile = null;
        this.mBaselineRootName = null;
        this.mNwarn = false;
        this.mIdentical = true;
        this.mFull = false;
        this.mNlabel = false;
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
        commandLine.createArgument().setValue("mkbl");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getBaselineRootName(), 3);
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
        if (this.getIdentical()) {
            cmd.createArgument().setValue("-identical");
        }
        if (this.getFull()) {
            cmd.createArgument().setValue("-full");
        }
        else {
            cmd.createArgument().setValue("-incremental");
        }
        if (this.getNlabel()) {
            cmd.createArgument().setValue("-nlabel");
        }
        cmd.createArgument().setValue(this.getBaselineRootName());
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
    
    public void setBaselineRootName(final String baselineRootName) {
        this.mBaselineRootName = baselineRootName;
    }
    
    public String getBaselineRootName() {
        return this.mBaselineRootName;
    }
    
    public void setNoWarn(final boolean nwarn) {
        this.mNwarn = nwarn;
    }
    
    public boolean getNoWarn() {
        return this.mNwarn;
    }
    
    public void setIdentical(final boolean identical) {
        this.mIdentical = identical;
    }
    
    public boolean getIdentical() {
        return this.mIdentical;
    }
    
    public void setFull(final boolean full) {
        this.mFull = full;
    }
    
    public boolean getFull() {
        return this.mFull;
    }
    
    public void setNlabel(final boolean nlabel) {
        this.mNlabel = nlabel;
    }
    
    public boolean getNlabel() {
        return this.mNlabel;
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
