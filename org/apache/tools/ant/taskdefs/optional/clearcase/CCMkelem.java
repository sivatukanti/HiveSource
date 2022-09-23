// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCMkelem extends ClearCase
{
    private String mComment;
    private String mCfile;
    private boolean mNwarn;
    private boolean mPtime;
    private boolean mNoco;
    private boolean mCheckin;
    private boolean mMaster;
    private String mEltype;
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_NOWARN = "-nwarn";
    public static final String FLAG_PRESERVETIME = "-ptime";
    public static final String FLAG_NOCHECKOUT = "-nco";
    public static final String FLAG_CHECKIN = "-ci";
    public static final String FLAG_MASTER = "-master";
    public static final String FLAG_ELTYPE = "-eltype";
    
    public CCMkelem() {
        this.mComment = null;
        this.mCfile = null;
        this.mNwarn = false;
        this.mPtime = false;
        this.mNoco = false;
        this.mCheckin = false;
        this.mMaster = false;
        this.mEltype = null;
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
        commandLine.createArgument().setValue("mkelem");
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
        if (this.getNoCheckout() && this.getCheckin()) {
            throw new BuildException("Should choose either [nocheckout | checkin]");
        }
        if (this.getNoCheckout()) {
            cmd.createArgument().setValue("-nco");
        }
        if (this.getCheckin()) {
            cmd.createArgument().setValue("-ci");
            if (this.getPreserveTime()) {
                cmd.createArgument().setValue("-ptime");
            }
        }
        if (this.getMaster()) {
            cmd.createArgument().setValue("-master");
        }
        if (this.getEltype() != null) {
            this.getEltypeCommand(cmd);
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
    
    public void setNoCheckout(final boolean co) {
        this.mNoco = co;
    }
    
    public boolean getNoCheckout() {
        return this.mNoco;
    }
    
    public void setCheckin(final boolean ci) {
        this.mCheckin = ci;
    }
    
    public boolean getCheckin() {
        return this.mCheckin;
    }
    
    public void setMaster(final boolean master) {
        this.mMaster = master;
    }
    
    public boolean getMaster() {
        return this.mMaster;
    }
    
    public void setEltype(final String eltype) {
        this.mEltype = eltype;
    }
    
    public String getEltype() {
        return this.mEltype;
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
    
    private void getEltypeCommand(final Commandline cmd) {
        if (this.getEltype() != null) {
            cmd.createArgument().setValue("-eltype");
            cmd.createArgument().setValue(this.getEltype());
        }
    }
}
