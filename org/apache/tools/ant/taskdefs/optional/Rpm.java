// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.taskdefs.ExecuteWatchdog;
import java.util.Map;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import org.apache.tools.ant.types.Commandline;
import java.io.File;
import org.apache.tools.ant.Task;

public class Rpm extends Task
{
    private static final String PATH1 = "PATH";
    private static final String PATH2 = "Path";
    private static final String PATH3 = "path";
    private String specFile;
    private File topDir;
    private String command;
    private String rpmBuildCommand;
    private boolean cleanBuildDir;
    private boolean removeSpec;
    private boolean removeSource;
    private File output;
    private File error;
    private boolean failOnError;
    private boolean quiet;
    
    public Rpm() {
        this.command = "-bb";
        this.rpmBuildCommand = null;
        this.cleanBuildDir = false;
        this.removeSpec = false;
        this.removeSource = false;
        this.failOnError = false;
        this.quiet = false;
    }
    
    @Override
    public void execute() throws BuildException {
        final Commandline toExecute = new Commandline();
        toExecute.setExecutable((this.rpmBuildCommand == null) ? this.guessRpmBuildCommand() : this.rpmBuildCommand);
        if (this.topDir != null) {
            toExecute.createArgument().setValue("--define");
            toExecute.createArgument().setValue("_topdir " + this.topDir);
        }
        toExecute.createArgument().setLine(this.command);
        if (this.cleanBuildDir) {
            toExecute.createArgument().setValue("--clean");
        }
        if (this.removeSpec) {
            toExecute.createArgument().setValue("--rmspec");
        }
        if (this.removeSource) {
            toExecute.createArgument().setValue("--rmsource");
        }
        toExecute.createArgument().setValue("SPECS/" + this.specFile);
        ExecuteStreamHandler streamhandler = null;
        OutputStream outputstream = null;
        OutputStream errorstream = null;
        if (this.error == null && this.output == null) {
            if (!this.quiet) {
                streamhandler = new LogStreamHandler(this, 2, 1);
            }
            else {
                streamhandler = new LogStreamHandler(this, 4, 4);
            }
        }
        else {
            Label_0305: {
                if (this.output != null) {
                    try {
                        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.output));
                        outputstream = new PrintStream(bos);
                        break Label_0305;
                    }
                    catch (IOException e) {
                        throw new BuildException(e, this.getLocation());
                    }
                }
                if (!this.quiet) {
                    outputstream = new LogOutputStream(this, 2);
                }
                else {
                    outputstream = new LogOutputStream(this, 4);
                }
            }
            Label_0394: {
                if (this.error != null) {
                    try {
                        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.error));
                        errorstream = new PrintStream(bos);
                        break Label_0394;
                    }
                    catch (IOException e) {
                        throw new BuildException(e, this.getLocation());
                    }
                }
                if (!this.quiet) {
                    errorstream = new LogOutputStream(this, 1);
                }
                else {
                    errorstream = new LogOutputStream(this, 4);
                }
            }
            streamhandler = new PumpStreamHandler(outputstream, errorstream);
        }
        final Execute exe = this.getExecute(toExecute, streamhandler);
        try {
            this.log("Building the RPM based on the " + this.specFile + " file");
            final int returncode = exe.execute();
            if (Execute.isFailure(returncode)) {
                final String msg = "'" + toExecute.getExecutable() + "' failed with exit code " + returncode;
                if (this.failOnError) {
                    throw new BuildException(msg);
                }
                this.log(msg, 0);
            }
        }
        catch (IOException e2) {
            throw new BuildException(e2, this.getLocation());
        }
        finally {
            FileUtils.close(outputstream);
            FileUtils.close(errorstream);
        }
    }
    
    public void setTopDir(final File td) {
        this.topDir = td;
    }
    
    public void setCommand(final String c) {
        this.command = c;
    }
    
    public void setSpecFile(final String sf) {
        if (sf == null || sf.trim().length() == 0) {
            throw new BuildException("You must specify a spec file", this.getLocation());
        }
        this.specFile = sf;
    }
    
    public void setCleanBuildDir(final boolean cbd) {
        this.cleanBuildDir = cbd;
    }
    
    public void setRemoveSpec(final boolean rs) {
        this.removeSpec = rs;
    }
    
    public void setRemoveSource(final boolean rs) {
        this.removeSource = rs;
    }
    
    public void setOutput(final File output) {
        this.output = output;
    }
    
    public void setError(final File error) {
        this.error = error;
    }
    
    public void setRpmBuildCommand(final String c) {
        this.rpmBuildCommand = c;
    }
    
    public void setFailOnError(final boolean value) {
        this.failOnError = value;
    }
    
    public void setQuiet(final boolean value) {
        this.quiet = value;
    }
    
    protected String guessRpmBuildCommand() {
        final Map env = Execute.getEnvironmentVariables();
        String path = env.get("PATH");
        if (path == null) {
            path = env.get("Path");
            if (path == null) {
                path = env.get("path");
            }
        }
        if (path != null) {
            final Path p = new Path(this.getProject(), path);
            final String[] pElements = p.list();
            for (int i = 0; i < pElements.length; ++i) {
                final File f = new File(pElements[i], "rpmbuild" + (Os.isFamily("dos") ? ".exe" : ""));
                if (f.canRead()) {
                    return f.getAbsolutePath();
                }
            }
        }
        return "rpm";
    }
    
    protected Execute getExecute(final Commandline toExecute, final ExecuteStreamHandler streamhandler) {
        final Execute exe = new Execute(streamhandler, null);
        exe.setAntRun(this.getProject());
        if (this.topDir == null) {
            this.topDir = this.getProject().getBaseDir();
        }
        exe.setWorkingDirectory(this.topDir);
        exe.setCommandline(toExecute.getCommandline());
        return exe;
    }
}
