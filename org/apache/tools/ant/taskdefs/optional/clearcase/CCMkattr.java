// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class CCMkattr extends ClearCase
{
    private boolean mReplace;
    private boolean mRecurse;
    private String mVersion;
    private String mTypeName;
    private String mTypeValue;
    private String mComment;
    private String mCfile;
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_RECURSE = "-recurse";
    public static final String FLAG_VERSION = "-version";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    
    public CCMkattr() {
        this.mReplace = false;
        this.mRecurse = false;
        this.mVersion = null;
        this.mTypeName = null;
        this.mTypeValue = null;
        this.mComment = null;
        this.mCfile = null;
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        final Project aProj = this.getProject();
        int result = 0;
        if (this.getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        if (this.getTypeValue() == null) {
            throw new BuildException("Required attribute TypeValue not specified");
        }
        if (this.getViewPath() == null) {
            this.setViewPath(aProj.getBaseDir().getPath());
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("mkattr");
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
        if (this.getReplace()) {
            cmd.createArgument().setValue("-replace");
        }
        if (this.getRecurse()) {
            cmd.createArgument().setValue("-recurse");
        }
        if (this.getVersion() != null) {
            this.getVersionCommand(cmd);
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
        if (this.getTypeName() != null) {
            this.getTypeCommand(cmd);
        }
        if (this.getTypeValue() != null) {
            this.getTypeValueCommand(cmd);
        }
        cmd.createArgument().setValue(this.getViewPath());
    }
    
    public void setReplace(final boolean replace) {
        this.mReplace = replace;
    }
    
    public boolean getReplace() {
        return this.mReplace;
    }
    
    public void setRecurse(final boolean recurse) {
        this.mRecurse = recurse;
    }
    
    public boolean getRecurse() {
        return this.mRecurse;
    }
    
    public void setVersion(final String version) {
        this.mVersion = version;
    }
    
    public String getVersion() {
        return this.mVersion;
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
    
    public void setTypeName(final String tn) {
        this.mTypeName = tn;
    }
    
    public String getTypeName() {
        return this.mTypeName;
    }
    
    public void setTypeValue(final String tv) {
        this.mTypeValue = tv;
    }
    
    public String getTypeValue() {
        return this.mTypeValue;
    }
    
    private void getVersionCommand(final Commandline cmd) {
        if (this.getVersion() != null) {
            cmd.createArgument().setValue("-version");
            cmd.createArgument().setValue(this.getVersion());
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
    
    private void getTypeCommand(final Commandline cmd) {
        final String typenm = this.getTypeName();
        if (typenm != null) {
            cmd.createArgument().setValue(typenm);
        }
    }
    
    private void getTypeValueCommand(final Commandline cmd) {
        String typevl = this.getTypeValue();
        if (typevl != null) {
            if (Os.isFamily("windows")) {
                typevl = "\\\"" + typevl + "\\\"";
            }
            else {
                typevl = "\"" + typevl + "\"";
            }
            cmd.createArgument().setValue(typevl);
        }
    }
}
