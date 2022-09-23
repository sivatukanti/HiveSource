// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.Map;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.Path;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.RedirectorElement;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import java.io.File;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.Task;

public class ExecTask extends Task
{
    private static final FileUtils FILE_UTILS;
    private String os;
    private String osFamily;
    private File dir;
    protected boolean failOnError;
    protected boolean newEnvironment;
    private Long timeout;
    private Environment env;
    protected Commandline cmdl;
    private String resultProperty;
    private boolean failIfExecFails;
    private String executable;
    private boolean resolveExecutable;
    private boolean searchPath;
    private boolean spawn;
    private boolean incompatibleWithSpawn;
    private String inputString;
    private File input;
    private File output;
    private File error;
    protected Redirector redirector;
    protected RedirectorElement redirectorElement;
    private boolean vmLauncher;
    
    public ExecTask() {
        this.failOnError = false;
        this.newEnvironment = false;
        this.timeout = null;
        this.env = new Environment();
        this.cmdl = new Commandline();
        this.failIfExecFails = true;
        this.resolveExecutable = false;
        this.searchPath = false;
        this.spawn = false;
        this.incompatibleWithSpawn = false;
        this.redirector = new Redirector(this);
        this.vmLauncher = true;
    }
    
    public ExecTask(final Task owner) {
        this.failOnError = false;
        this.newEnvironment = false;
        this.timeout = null;
        this.env = new Environment();
        this.cmdl = new Commandline();
        this.failIfExecFails = true;
        this.resolveExecutable = false;
        this.searchPath = false;
        this.spawn = false;
        this.incompatibleWithSpawn = false;
        this.redirector = new Redirector(this);
        this.vmLauncher = true;
        this.bindToOwner(owner);
    }
    
    public void setSpawn(final boolean spawn) {
        this.spawn = spawn;
    }
    
    public void setTimeout(final Long value) {
        this.timeout = value;
        this.incompatibleWithSpawn = true;
    }
    
    public void setTimeout(final Integer value) {
        this.setTimeout((value == null) ? null : new Long(value));
    }
    
    public void setExecutable(final String value) {
        this.executable = value;
        this.cmdl.setExecutable(value);
    }
    
    public void setDir(final File d) {
        this.dir = d;
    }
    
    public void setOs(final String os) {
        this.os = os;
    }
    
    public final String getOs() {
        return this.os;
    }
    
    public void setCommand(final Commandline cmdl) {
        this.log("The command attribute is deprecated.\nPlease use the executable attribute and nested arg elements.", 1);
        this.cmdl = cmdl;
    }
    
    public void setOutput(final File out) {
        this.output = out;
        this.incompatibleWithSpawn = true;
    }
    
    public void setInput(final File input) {
        if (this.inputString != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.input = input;
        this.incompatibleWithSpawn = true;
    }
    
    public void setInputString(final String inputString) {
        if (this.input != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.inputString = inputString;
        this.incompatibleWithSpawn = true;
    }
    
    public void setLogError(final boolean logError) {
        this.redirector.setLogError(logError);
        this.incompatibleWithSpawn |= logError;
    }
    
    public void setError(final File error) {
        this.error = error;
        this.incompatibleWithSpawn = true;
    }
    
    public void setOutputproperty(final String outputProp) {
        this.redirector.setOutputProperty(outputProp);
        this.incompatibleWithSpawn = true;
    }
    
    public void setErrorProperty(final String errorProperty) {
        this.redirector.setErrorProperty(errorProperty);
        this.incompatibleWithSpawn = true;
    }
    
    public void setFailonerror(final boolean fail) {
        this.failOnError = fail;
        this.incompatibleWithSpawn |= fail;
    }
    
    public void setNewenvironment(final boolean newenv) {
        this.newEnvironment = newenv;
    }
    
    public void setResolveExecutable(final boolean resolveExecutable) {
        this.resolveExecutable = resolveExecutable;
    }
    
    public void setSearchPath(final boolean searchPath) {
        this.searchPath = searchPath;
    }
    
    public boolean getResolveExecutable() {
        return this.resolveExecutable;
    }
    
    public void addEnv(final Environment.Variable var) {
        this.env.addVariable(var);
    }
    
    public Commandline.Argument createArg() {
        return this.cmdl.createArgument();
    }
    
    public void setResultProperty(final String resultProperty) {
        this.resultProperty = resultProperty;
        this.incompatibleWithSpawn = true;
    }
    
    protected void maybeSetResultPropertyValue(final int result) {
        if (this.resultProperty != null) {
            final String res = Integer.toString(result);
            this.getProject().setNewProperty(this.resultProperty, res);
        }
    }
    
    public void setFailIfExecutionFails(final boolean flag) {
        this.failIfExecFails = flag;
        this.incompatibleWithSpawn = true;
    }
    
    public void setAppend(final boolean append) {
        this.redirector.setAppend(append);
        this.incompatibleWithSpawn = true;
    }
    
    public void addConfiguredRedirector(final RedirectorElement redirectorElement) {
        if (this.redirectorElement != null) {
            throw new BuildException("cannot have > 1 nested <redirector>s");
        }
        this.redirectorElement = redirectorElement;
        this.incompatibleWithSpawn = true;
    }
    
    public void setOsFamily(final String osFamily) {
        this.osFamily = osFamily.toLowerCase(Locale.ENGLISH);
    }
    
    public final String getOsFamily() {
        return this.osFamily;
    }
    
    protected String resolveExecutable(final String exec, final boolean mustSearchPath) {
        if (!this.resolveExecutable) {
            return exec;
        }
        File executableFile = this.getProject().resolveFile(exec);
        if (executableFile.exists()) {
            return executableFile.getAbsolutePath();
        }
        if (this.dir != null) {
            executableFile = ExecTask.FILE_UTILS.resolveFile(this.dir, exec);
            if (executableFile.exists()) {
                return executableFile.getAbsolutePath();
            }
        }
        if (mustSearchPath) {
            Path p = null;
            final String[] environment = this.env.getVariables();
            if (environment != null) {
                for (int i = 0; i < environment.length; ++i) {
                    if (this.isPath(environment[i])) {
                        p = new Path(this.getProject(), this.getPath(environment[i]));
                        break;
                    }
                }
            }
            if (p == null) {
                final String path = this.getPath(Execute.getEnvironmentVariables());
                if (path != null) {
                    p = new Path(this.getProject(), path);
                }
            }
            if (p != null) {
                final String[] dirs = p.list();
                for (int j = 0; j < dirs.length; ++j) {
                    executableFile = ExecTask.FILE_UTILS.resolveFile(new File(dirs[j]), exec);
                    if (executableFile.exists()) {
                        return executableFile.getAbsolutePath();
                    }
                }
            }
        }
        return exec;
    }
    
    @Override
    public void execute() throws BuildException {
        if (!this.isValidOs()) {
            return;
        }
        final File savedDir = this.dir;
        this.cmdl.setExecutable(this.resolveExecutable(this.executable, this.searchPath));
        this.checkConfiguration();
        try {
            this.runExec(this.prepareExec());
        }
        finally {
            this.dir = savedDir;
        }
    }
    
    protected void checkConfiguration() throws BuildException {
        if (this.cmdl.getExecutable() == null) {
            throw new BuildException("no executable specified", this.getLocation());
        }
        if (this.dir != null && !this.dir.exists()) {
            throw new BuildException("The directory " + this.dir + " does not exist");
        }
        if (this.dir != null && !this.dir.isDirectory()) {
            throw new BuildException(this.dir + " is not a directory");
        }
        if (this.spawn && this.incompatibleWithSpawn) {
            this.getProject().log("spawn does not allow attributes related to input, output, error, result", 0);
            this.getProject().log("spawn also does not allow timeout", 0);
            this.getProject().log("finally, spawn is not compatible with a nested I/O <redirector>", 0);
            throw new BuildException("You have used an attribute or nested element which is not compatible with spawn");
        }
        this.setupRedirector();
    }
    
    protected void setupRedirector() {
        this.redirector.setInput(this.input);
        this.redirector.setInputString(this.inputString);
        this.redirector.setOutput(this.output);
        this.redirector.setError(this.error);
    }
    
    protected boolean isValidOs() {
        if (this.osFamily != null && !Os.isFamily(this.osFamily)) {
            return false;
        }
        final String myos = System.getProperty("os.name");
        this.log("Current OS is " + myos, 3);
        if (this.os != null && this.os.indexOf(myos) < 0) {
            this.log("This OS, " + myos + " was not found in the specified list of valid OSes: " + this.os, 3);
            return false;
        }
        return true;
    }
    
    public void setVMLauncher(final boolean vmLauncher) {
        this.vmLauncher = vmLauncher;
    }
    
    protected Execute prepareExec() throws BuildException {
        if (this.dir == null) {
            this.dir = this.getProject().getBaseDir();
        }
        if (this.redirectorElement != null) {
            this.redirectorElement.configure(this.redirector);
        }
        final Execute exe = new Execute(this.createHandler(), this.createWatchdog());
        exe.setAntRun(this.getProject());
        exe.setWorkingDirectory(this.dir);
        exe.setVMLauncher(this.vmLauncher);
        final String[] environment = this.env.getVariables();
        if (environment != null) {
            for (int i = 0; i < environment.length; ++i) {
                this.log("Setting environment variable: " + environment[i], 3);
            }
        }
        exe.setNewenvironment(this.newEnvironment);
        exe.setEnvironment(environment);
        return exe;
    }
    
    protected final void runExecute(final Execute exe) throws IOException {
        int returnCode = -1;
        if (!this.spawn) {
            returnCode = exe.execute();
            if (exe.killedProcess()) {
                final String msg = "Timeout: killed the sub-process";
                if (this.failOnError) {
                    throw new BuildException(msg);
                }
                this.log(msg, 1);
            }
            this.maybeSetResultPropertyValue(returnCode);
            this.redirector.complete();
            if (Execute.isFailure(returnCode)) {
                if (this.failOnError) {
                    throw new BuildException(this.getTaskType() + " returned: " + returnCode, this.getLocation());
                }
                this.log("Result: " + returnCode, 0);
            }
        }
        else {
            exe.spawn();
        }
    }
    
    protected void runExec(final Execute exe) throws BuildException {
        this.log(this.cmdl.describeCommand(), 3);
        exe.setCommandline(this.cmdl.getCommandline());
        try {
            this.runExecute(exe);
        }
        catch (IOException e) {
            if (this.failIfExecFails) {
                throw new BuildException("Execute failed: " + e.toString(), e, this.getLocation());
            }
            this.log("Execute failed: " + e.toString(), 0);
        }
        finally {
            this.logFlush();
        }
    }
    
    protected ExecuteStreamHandler createHandler() throws BuildException {
        return this.redirector.createHandler();
    }
    
    protected ExecuteWatchdog createWatchdog() throws BuildException {
        return (this.timeout == null) ? null : new ExecuteWatchdog(this.timeout);
    }
    
    protected void logFlush() {
    }
    
    private boolean isPath(final String line) {
        return line.startsWith("PATH=") || line.startsWith("Path=");
    }
    
    private String getPath(final String line) {
        return line.substring("PATH=".length());
    }
    
    private String getPath(final Map<String, String> map) {
        final String p = map.get("PATH");
        return (p != null) ? p : map.get("Path");
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
