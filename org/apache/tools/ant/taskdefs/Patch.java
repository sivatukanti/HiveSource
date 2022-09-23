// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Commandline;
import java.io.File;
import org.apache.tools.ant.Task;

public class Patch extends Task
{
    private File originalFile;
    private File directory;
    private boolean havePatchfile;
    private Commandline cmd;
    private boolean failOnError;
    private static final String PATCH = "patch";
    
    public Patch() {
        this.havePatchfile = false;
        this.cmd = new Commandline();
        this.failOnError = false;
    }
    
    public void setOriginalfile(final File file) {
        this.originalFile = file;
    }
    
    public void setDestfile(final File file) {
        if (file != null) {
            this.cmd.createArgument().setValue("-o");
            this.cmd.createArgument().setFile(file);
        }
    }
    
    public void setPatchfile(final File file) {
        if (!file.exists()) {
            throw new BuildException("patchfile " + file + " doesn't exist", this.getLocation());
        }
        this.cmd.createArgument().setValue("-i");
        this.cmd.createArgument().setFile(file);
        this.havePatchfile = true;
    }
    
    public void setBackups(final boolean backups) {
        if (backups) {
            this.cmd.createArgument().setValue("-b");
        }
    }
    
    public void setIgnorewhitespace(final boolean ignore) {
        if (ignore) {
            this.cmd.createArgument().setValue("-l");
        }
    }
    
    public void setStrip(final int num) throws BuildException {
        if (num < 0) {
            throw new BuildException("strip has to be >= 0", this.getLocation());
        }
        this.cmd.createArgument().setValue("-p" + num);
    }
    
    public void setQuiet(final boolean q) {
        if (q) {
            this.cmd.createArgument().setValue("-s");
        }
    }
    
    public void setReverse(final boolean r) {
        if (r) {
            this.cmd.createArgument().setValue("-R");
        }
    }
    
    public void setDir(final File directory) {
        this.directory = directory;
    }
    
    public void setFailOnError(final boolean value) {
        this.failOnError = value;
    }
    
    @Override
    public void execute() throws BuildException {
        if (!this.havePatchfile) {
            throw new BuildException("patchfile argument is required", this.getLocation());
        }
        final Commandline toExecute = (Commandline)this.cmd.clone();
        toExecute.setExecutable("patch");
        if (this.originalFile != null) {
            toExecute.createArgument().setFile(this.originalFile);
        }
        final Execute exe = new Execute(new LogStreamHandler(this, 2, 1), null);
        exe.setCommandline(toExecute.getCommandline());
        if (this.directory != null) {
            if (this.directory.exists() && this.directory.isDirectory()) {
                exe.setWorkingDirectory(this.directory);
            }
            else {
                if (!this.directory.isDirectory()) {
                    throw new BuildException(this.directory + " is not a directory.", this.getLocation());
                }
                throw new BuildException("directory " + this.directory + " doesn't exist", this.getLocation());
            }
        }
        else {
            exe.setWorkingDirectory(this.getProject().getBaseDir());
        }
        this.log(toExecute.describeCommand(), 3);
        try {
            final int returncode = exe.execute();
            if (Execute.isFailure(returncode)) {
                final String msg = "'patch' failed with exit code " + returncode;
                if (this.failOnError) {
                    throw new BuildException(msg);
                }
                this.log(msg, 0);
            }
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
}
