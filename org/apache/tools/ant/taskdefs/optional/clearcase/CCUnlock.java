// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

public class CCUnlock extends ClearCase
{
    private String mComment;
    private String mPname;
    public static final String FLAG_COMMENT = "-comment";
    public static final String FLAG_PNAME = "-pname";
    
    public CCUnlock() {
        this.mComment = null;
        this.mPname = null;
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
        commandLine.createArgument().setValue("unlock");
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
        this.getCommentCommand(cmd);
        if (this.getObjSelect() == null && this.getPname() == null) {
            throw new BuildException("Should select either an element (pname) or an object (objselect)");
        }
        this.getPnameCommand(cmd);
        if (this.getObjSelect() != null) {
            cmd.createArgument().setValue(this.getObjSelect());
        }
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
    
    public void setObjselect(final String objselect) {
        this.setObjSelect(objselect);
    }
    
    public void setObjSel(final String objsel) {
        this.setObjSelect(objsel);
    }
    
    public String getObjselect() {
        return this.getObjSelect();
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
        return this.getObjSelect();
    }
}
