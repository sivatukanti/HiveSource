// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class CCMklbtype extends ClearCase
{
    private String mTypeName;
    private String mVOB;
    private String mComment;
    private String mCfile;
    private boolean mReplace;
    private boolean mGlobal;
    private boolean mOrdinary;
    private boolean mPbranch;
    private boolean mShared;
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_GLOBAL = "-global";
    public static final String FLAG_ORDINARY = "-ordinary";
    public static final String FLAG_PBRANCH = "-pbranch";
    public static final String FLAG_SHARED = "-shared";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    
    public CCMklbtype() {
        this.mTypeName = null;
        this.mVOB = null;
        this.mComment = null;
        this.mCfile = null;
        this.mReplace = false;
        this.mGlobal = false;
        this.mOrdinary = true;
        this.mPbranch = false;
        this.mShared = false;
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        int result = 0;
        if (this.getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("mklbtype");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getTypeSpecifier(), 3);
        }
        result = this.run(commandLine);
        if (Execute.isFailure(result) && this.getFailOnErr()) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.getReplace()) {
            cmd.createArgument().setValue("-replace");
        }
        if (this.getOrdinary()) {
            cmd.createArgument().setValue("-ordinary");
        }
        else if (this.getGlobal()) {
            cmd.createArgument().setValue("-global");
        }
        if (this.getPbranch()) {
            cmd.createArgument().setValue("-pbranch");
        }
        if (this.getShared()) {
            cmd.createArgument().setValue("-shared");
        }
        if (this.getComment() != null) {
            this.getCommentCommand(cmd);
        }
        else if (this.getCommentFile() != null) {
            this.getCommentFileCommand(cmd);
        }
        else {
            cmd.createArgument().setValue("-nc");
        }
        cmd.createArgument().setValue(this.getTypeSpecifier());
    }
    
    public void setTypeName(final String tn) {
        this.mTypeName = tn;
    }
    
    public String getTypeName() {
        return this.mTypeName;
    }
    
    public void setVOB(final String vob) {
        this.mVOB = vob;
    }
    
    public String getVOB() {
        return this.mVOB;
    }
    
    public void setReplace(final boolean repl) {
        this.mReplace = repl;
    }
    
    public boolean getReplace() {
        return this.mReplace;
    }
    
    public void setGlobal(final boolean glob) {
        this.mGlobal = glob;
    }
    
    public boolean getGlobal() {
        return this.mGlobal;
    }
    
    public void setOrdinary(final boolean ordinary) {
        this.mOrdinary = ordinary;
    }
    
    public boolean getOrdinary() {
        return this.mOrdinary;
    }
    
    public void setPbranch(final boolean pbranch) {
        this.mPbranch = pbranch;
    }
    
    public boolean getPbranch() {
        return this.mPbranch;
    }
    
    public void setShared(final boolean shared) {
        this.mShared = shared;
    }
    
    public boolean getShared() {
        return this.mShared;
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
    
    private String getTypeSpecifier() {
        String typenm = null;
        typenm = this.getTypeName();
        if (this.getVOB() != null) {
            typenm = typenm + "@" + this.getVOB();
        }
        return typenm;
    }
}
