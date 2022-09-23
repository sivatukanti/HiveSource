// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import java.util.Vector;
import java.io.File;

public class CCMCheck extends Continuus
{
    private File file;
    private String comment;
    private String task;
    protected Vector filesets;
    public static final String FLAG_COMMENT = "/comment";
    public static final String FLAG_TASK = "/task";
    
    public CCMCheck() {
        this.file = null;
        this.comment = null;
        this.task = null;
        this.filesets = new Vector();
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void setFile(final File v) {
        this.log("working file " + v, 3);
        this.file = v;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setComment(final String v) {
        this.comment = v;
    }
    
    public String getTask() {
        return this.task;
    }
    
    public void setTask(final String v) {
        this.task = v;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.addElement(set);
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.file == null && this.filesets.size() == 0) {
            throw new BuildException("Specify at least one source - a file or a fileset.");
        }
        if (this.file != null && this.file.exists() && this.file.isDirectory()) {
            throw new BuildException("CCMCheck cannot be generated for directories");
        }
        if (this.file != null && this.filesets.size() > 0) {
            throw new BuildException("Choose between file and fileset !");
        }
        if (this.getFile() != null) {
            this.doit();
            return;
        }
        for (int sizeofFileSet = this.filesets.size(), i = 0; i < sizeofFileSet; ++i) {
            final FileSet fs = this.filesets.elementAt(i);
            final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            final String[] srcFiles = ds.getIncludedFiles();
            for (int j = 0; j < srcFiles.length; ++j) {
                final File src = new File(fs.getDir(this.getProject()), srcFiles[j]);
                this.setFile(src);
                this.doit();
            }
        }
    }
    
    private void doit() {
        final Commandline commandLine = new Commandline();
        commandLine.setExecutable(this.getCcmCommand());
        commandLine.createArgument().setValue(this.getCcmAction());
        this.checkOptions(commandLine);
        final int result = this.run(commandLine);
        if (Execute.isFailure(result)) {
            final String msg = "Failed executing: " + commandLine.toString();
            throw new BuildException(msg, this.getLocation());
        }
    }
    
    private void checkOptions(final Commandline cmd) {
        if (this.getComment() != null) {
            cmd.createArgument().setValue("/comment");
            cmd.createArgument().setValue(this.getComment());
        }
        if (this.getTask() != null) {
            cmd.createArgument().setValue("/task");
            cmd.createArgument().setValue(this.getTask());
        }
        if (this.getFile() != null) {
            cmd.createArgument().setValue(this.file.getAbsolutePath());
        }
    }
}
