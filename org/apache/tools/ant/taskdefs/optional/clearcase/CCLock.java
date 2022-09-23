// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCLock extends ClearCase
{
    private boolean mReplace;
    private boolean mObsolete;
    private String mComment;
    private String mNusers;
    private String mPname;
    private String mObjselect;
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_NUSERS = "-nusers";
    public static final String FLAG_OBSOLETE = "-obsolete";
    public static final String FLAG_COMMENT = "-comment";
    public static final String FLAG_PNAME = "-pname";
    
    public CCLock() {
        this.mReplace = false;
        this.mObsolete = false;
        this.mComment = null;
        this.mNusers = null;
        this.mPname = null;
        this.mObjselect = null;
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
        commandLine.createArgument().setValue("lock");
        this.checkOptions(commandLine);
        if (!this.getFailOnErr()) {
            this.getProject().log("Ignoring any errors that occur for: " + this.getOpType(), 3);
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
        if (this.getObsolete()) {
            cmd.createArgument().setValue("-obsolete");
        }
        else {
            this.getNusersCommand(cmd);
        }
        this.getCommentCommand(cmd);
        if (this.getObjselect() == null && this.getPname() == null) {
            throw new BuildException("Should select either an element (pname) or an object (objselect)");
        }
        this.getPnameCommand(cmd);
        if (this.getObjselect() != null) {
            cmd.createArgument().setValue(this.getObjselect());
        }
    }
    
    public void setReplace(final boolean replace) {
        this.mReplace = replace;
    }
    
    public boolean getReplace() {
        return this.mReplace;
    }
    
    public void setObsolete(final boolean obsolete) {
        this.mObsolete = obsolete;
    }
    
    public boolean getObsolete() {
        return this.mObsolete;
    }
    
    public void setNusers(final String nusers) {
        this.mNusers = nusers;
    }
    
    public String getNusers() {
        return this.mNusers;
    }
    
    public void setComment(final String comment) {
        this.mComment = comment;
    }
    
    public String getComment() {
        return this.mComment;
    }
    
    public void setPname(final String pname) {
        this.mPname = pname;
    }
    
    public String getPname() {
        return this.mPname;
    }
    
    public void setObjSel(final String objsel) {
        this.mObjselect = objsel;
    }
    
    public void setObjselect(final String objselect) {
        this.mObjselect = objselect;
    }
    
    public String getObjselect() {
        return this.mObjselect;
    }
    
    private void getNusersCommand(final Commandline cmd) {
        if (this.getNusers() == null) {
            return;
        }
        cmd.createArgument().setValue("-nusers");
        cmd.createArgument().setValue(this.getNusers());
    }
    
    private void getCommentCommand(final Commandline cmd) {
        if (this.getComment() == null) {
            return;
        }
        cmd.createArgument().setValue("-comment");
        cmd.createArgument().setValue(this.getComment());
    }
    
    private void getPnameCommand(final Commandline cmd) {
        if (this.getPname() == null) {
            return;
        }
        cmd.createArgument().setValue("-pname");
        cmd.createArgument().setValue(this.getPname());
    }
    
    private String getOpType() {
        if (this.getPname() != null) {
            return this.getPname();
        }
        return this.getObjselect();
    }
}
