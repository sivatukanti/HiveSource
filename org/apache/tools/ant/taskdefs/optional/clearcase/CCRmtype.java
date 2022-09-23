// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;

public class CCRmtype extends ClearCase
{
    private String mTypeKind;
    private String mTypeName;
    private String mVOB;
    private String mComment;
    private String mCfile;
    private boolean mRmall;
    private boolean mIgnore;
    public static final String FLAG_IGNORE = "-ignore";
    public static final String FLAG_RMALL = "-rmall";
    public static final String FLAG_FORCE = "-force";
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_NOCOMMENT = "-nc";
    
    public CCRmtype() {
        this.mTypeKind = null;
        this.mTypeName = null;
        this.mVOB = null;
        this.mComment = null;
        this.mCfile = null;
        this.mRmall = false;
        this.mIgnore = false;
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline commandLine = new Commandline();
        int result = 0;
        if (this.getTypeKind() == null) {
            throw new BuildException("Required attribute TypeKind not specified");
        }
        if (this.getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        commandLine.setExecutable(this.getClearToolCommand());
        commandLine.createArgument().setValue("rmtype");
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
        if (this.getIgnore()) {
            cmd.createArgument().setValue("-ignore");
        }
        if (this.getRmAll()) {
            cmd.createArgument().setValue("-rmall");
            cmd.createArgument().setValue("-force");
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
    
    public void setIgnore(final boolean ignore) {
        this.mIgnore = ignore;
    }
    
    public boolean getIgnore() {
        return this.mIgnore;
    }
    
    public void setRmAll(final boolean rmall) {
        this.mRmall = rmall;
    }
    
    public boolean getRmAll() {
        return this.mRmall;
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
    
    public void setTypeKind(final String tk) {
        this.mTypeKind = tk;
    }
    
    public String getTypeKind() {
        return this.mTypeKind;
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
    
    private String getTypeSpecifier() {
        final String tkind = this.getTypeKind();
        final String tname = this.getTypeName();
        String typeSpec = null;
        typeSpec = tkind + ":" + tname;
        if (this.getVOB() != null) {
            typeSpec = typeSpec + "@" + this.getVOB();
        }
        return typeSpec;
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
