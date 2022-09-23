// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCCheckout extends ClearCase
{
    private boolean mReserved;
    private String mOut;
    private boolean mNdata;
    private String mBranch;
    private boolean mVersion;
    private boolean mNwarn;
    private String mComment;
    private String mCfile;
    private boolean mNotco;
    public static final String FLAG_RESERVED = "-reserved";
    public static final String FLAG_UNRESERVED = "-unreserved";
    public static final String FLAG_OUT = "-out";
    public static final String FLAG_NODATA = "-ndata";
    public static final String FLAG_BRANCH = "-branch";
    public static final String FLAG_VERSION = "-version";
    public static final String FLAG_NOWARN = "-nwarn";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    
    public CCCheckout() {
        this.mReserved = true;
        this.mOut = null;
        this.mNdata = false;
        this.mBranch = null;
        this.mVersion = false;
        this.mNwarn = false;
        this.mComment = null;
        this.mCfile = null;
        this.mNotco = true;
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
        commandLine.createArgument().setValue("checkout");
        this.checkOptions(commandLine);
        if (!this.getNotco() && this.lsCheckout()) {
            this.getProject().log("Already checked out in this view: " + this.getViewPathBasename(), 3);
            return;
        }
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getViewPathBasename(), 3);
        }
        result = this.run(commandLine);
        if (Execute.isFailure(result) && this.getFailOnErr()) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private boolean lsCheckout() {
        final Commandline cmdl = new Commandline();
        cmdl.setExecutable(this.getClearToolCommand());
        cmdl.createArgument().setValue("lsco");
        cmdl.createArgument().setValue("-cview");
        cmdl.createArgument().setValue("-short");
        cmdl.createArgument().setValue("-d");
        cmdl.createArgument().setValue(this.getViewPath());
        final String result = this.runS(cmdl);
        return result != null && result.length() > 0;
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.getReserved()) {
            cmd.createArgument().setValue("-reserved");
        }
        else {
            cmd.createArgument().setValue("-unreserved");
        }
        if (this.getOut() != null) {
            this.getOutCommand(cmd);
        }
        else if (this.getNoData()) {
            cmd.createArgument().setValue("-ndata");
        }
        if (this.getBranch() != null) {
            this.getBranchCommand(cmd);
        }
        else if (this.getVersion()) {
            cmd.createArgument().setValue("-version");
        }
        if (this.getNoWarn()) {
            cmd.createArgument().setValue("-nwarn");
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
        cmd.createArgument().setValue(this.getViewPath());
    }
    
    public void setReserved(final boolean reserved) {
        this.mReserved = reserved;
    }
    
    public boolean getReserved() {
        return this.mReserved;
    }
    
    public void setNotco(final boolean notco) {
        this.mNotco = notco;
    }
    
    public boolean getNotco() {
        return this.mNotco;
    }
    
    public void setOut(final String outf) {
        this.mOut = outf;
    }
    
    public String getOut() {
        return this.mOut;
    }
    
    public void setNoData(final boolean ndata) {
        this.mNdata = ndata;
    }
    
    public boolean getNoData() {
        return this.mNdata;
    }
    
    public void setBranch(final String branch) {
        this.mBranch = branch;
    }
    
    public String getBranch() {
        return this.mBranch;
    }
    
    public void setVersion(final boolean version) {
        this.mVersion = version;
    }
    
    public boolean getVersion() {
        return this.mVersion;
    }
    
    public void setNoWarn(final boolean nwarn) {
        this.mNwarn = nwarn;
    }
    
    public boolean getNoWarn() {
        return this.mNwarn;
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
    
    private void getOutCommand(final Commandline cmd) {
        if (this.getOut() != null) {
            cmd.createArgument().setValue("-out");
            cmd.createArgument().setValue(this.getOut());
        }
    }
    
    private void getBranchCommand(final Commandline cmd) {
        if (this.getBranch() != null) {
            cmd.createArgument().setValue("-branch");
            cmd.createArgument().setValue(this.getBranch());
        }
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
