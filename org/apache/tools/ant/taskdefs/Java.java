// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;
import org.apache.tools.ant.taskdefs.condition.Os;
import java.io.InputStream;
import org.apache.tools.ant.util.KeepAliveInputStream;
import java.io.IOException;
import org.apache.tools.ant.types.Assertions;
import org.apache.tools.ant.types.PropertySet;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.types.RedirectorElement;
import java.io.File;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.Task;

public class Java extends Task
{
    private CommandlineJava cmdl;
    private Environment env;
    private boolean fork;
    private boolean newEnvironment;
    private File dir;
    private boolean failOnError;
    private Long timeout;
    private String inputString;
    private File input;
    private File output;
    private File error;
    protected Redirector redirector;
    protected RedirectorElement redirectorElement;
    private String resultProperty;
    private Permissions perm;
    private boolean spawn;
    private boolean incompatibleWithSpawn;
    private static final String TIMEOUT_MESSAGE = "Timeout: killed the sub-process";
    
    public Java() {
        this.cmdl = new CommandlineJava();
        this.env = new Environment();
        this.fork = false;
        this.newEnvironment = false;
        this.dir = null;
        this.failOnError = false;
        this.timeout = null;
        this.redirector = new Redirector(this);
        this.perm = null;
        this.spawn = false;
        this.incompatibleWithSpawn = false;
    }
    
    public Java(final Task owner) {
        this.cmdl = new CommandlineJava();
        this.env = new Environment();
        this.fork = false;
        this.newEnvironment = false;
        this.dir = null;
        this.failOnError = false;
        this.timeout = null;
        this.redirector = new Redirector(this);
        this.perm = null;
        this.spawn = false;
        this.incompatibleWithSpawn = false;
        this.bindToOwner(owner);
    }
    
    @Override
    public void execute() throws BuildException {
        final File savedDir = this.dir;
        final Permissions savedPermissions = this.perm;
        int err = -1;
        try {
            this.checkConfiguration();
            err = this.executeJava();
            if (err != 0) {
                if (this.failOnError) {
                    throw new ExitStatusException("Java returned: " + err, err, this.getLocation());
                }
                this.log("Java Result: " + err, 0);
            }
            this.maybeSetResultPropertyValue(err);
        }
        finally {
            this.dir = savedDir;
            this.perm = savedPermissions;
        }
    }
    
    public int executeJava() throws BuildException {
        return this.executeJava(this.getCommandLine());
    }
    
    protected void checkConfiguration() throws BuildException {
        final String classname = this.getCommandLine().getClassname();
        if (classname == null && this.getCommandLine().getJar() == null) {
            throw new BuildException("Classname must not be null.");
        }
        if (!this.fork && this.getCommandLine().getJar() != null) {
            throw new BuildException("Cannot execute a jar in non-forked mode. Please set fork='true'. ");
        }
        if (this.spawn && !this.fork) {
            throw new BuildException("Cannot spawn a java process in non-forked mode. Please set fork='true'. ");
        }
        if (this.getCommandLine().getClasspath() != null && this.getCommandLine().getJar() != null) {
            this.log("When using 'jar' attribute classpath-settings are ignored. See the manual for more information.", 3);
        }
        if (this.spawn && this.incompatibleWithSpawn) {
            this.getProject().log("spawn does not allow attributes related to input, output, error, result", 0);
            this.getProject().log("spawn also does not allow timeout", 0);
            this.getProject().log("finally, spawn is not compatible with a nested I/O <redirector>", 0);
            throw new BuildException("You have used an attribute or nested element which is not compatible with spawn");
        }
        if (this.getCommandLine().getAssertions() != null && !this.fork) {
            this.log("Assertion statements are currently ignored in non-forked mode");
        }
        if (this.fork) {
            if (this.perm != null) {
                this.log("Permissions can not be set this way in forked mode.", 1);
            }
            this.log(this.getCommandLine().describeCommand(), 3);
        }
        else {
            if (this.getCommandLine().getVmCommand().size() > 1) {
                this.log("JVM args ignored when same JVM is used.", 1);
            }
            if (this.dir != null) {
                this.log("Working directory ignored when same JVM is used.", 1);
            }
            if (this.newEnvironment || null != this.env.getVariables()) {
                this.log("Changes to environment variables are ignored when same JVM is used.", 1);
            }
            if (this.getCommandLine().getBootclasspath() != null) {
                this.log("bootclasspath ignored when same JVM is used.", 1);
            }
            if (this.perm == null) {
                this.perm = new Permissions(true);
                this.log("running " + this.getCommandLine().getClassname() + " with default permissions (exit forbidden)", 3);
            }
            this.log("Running in same VM " + this.getCommandLine().describeJavaCommand(), 3);
        }
        this.setupRedirector();
    }
    
    protected int executeJava(final CommandlineJava commandLine) {
        try {
            if (this.fork) {
                if (!this.spawn) {
                    return this.fork(commandLine.getCommandline());
                }
                this.spawn(commandLine.getCommandline());
                return 0;
            }
            else {
                try {
                    this.run(commandLine);
                    return 0;
                }
                catch (ExitException ex) {
                    return ex.getStatus();
                }
            }
        }
        catch (BuildException e) {
            if (e.getLocation() == null && this.getLocation() != null) {
                e.setLocation(this.getLocation());
            }
            if (this.failOnError) {
                throw e;
            }
            if ("Timeout: killed the sub-process".equals(e.getMessage())) {
                this.log("Timeout: killed the sub-process");
            }
            else {
                this.log(e);
            }
            return -1;
        }
        catch (ThreadDeath t) {
            throw t;
        }
        catch (Throwable t2) {
            if (this.failOnError) {
                throw new BuildException(t2, this.getLocation());
            }
            this.log(t2);
            return -1;
        }
    }
    
    public void setSpawn(final boolean spawn) {
        this.spawn = spawn;
    }
    
    public void setClasspath(final Path s) {
        this.createClasspath().append(s);
    }
    
    public Path createClasspath() {
        return this.getCommandLine().createClasspath(this.getProject()).createPath();
    }
    
    public Path createBootclasspath() {
        return this.getCommandLine().createBootclasspath(this.getProject()).createPath();
    }
    
    public Permissions createPermissions() {
        return this.perm = ((this.perm == null) ? new Permissions() : this.perm);
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setJar(final File jarfile) throws BuildException {
        if (this.getCommandLine().getClassname() != null) {
            throw new BuildException("Cannot use 'jar' and 'classname' attributes in same command.");
        }
        this.getCommandLine().setJar(jarfile.getAbsolutePath());
    }
    
    public void setClassname(final String s) throws BuildException {
        if (this.getCommandLine().getJar() != null) {
            throw new BuildException("Cannot use 'jar' and 'classname' attributes in same command");
        }
        this.getCommandLine().setClassname(s);
    }
    
    public void setArgs(final String s) {
        this.log("The args attribute is deprecated. Please use nested arg elements.", 1);
        this.getCommandLine().createArgument().setLine(s);
    }
    
    public void setCloneVm(final boolean cloneVm) {
        this.getCommandLine().setCloneVm(cloneVm);
    }
    
    public Commandline.Argument createArg() {
        return this.getCommandLine().createArgument();
    }
    
    public void setResultProperty(final String resultProperty) {
        this.resultProperty = resultProperty;
        this.incompatibleWithSpawn = true;
    }
    
    protected void maybeSetResultPropertyValue(final int result) {
        final String res = Integer.toString(result);
        if (this.resultProperty != null) {
            this.getProject().setNewProperty(this.resultProperty, res);
        }
    }
    
    public void setFork(final boolean s) {
        this.fork = s;
    }
    
    public void setJvmargs(final String s) {
        this.log("The jvmargs attribute is deprecated. Please use nested jvmarg elements.", 1);
        this.getCommandLine().createVmArgument().setLine(s);
    }
    
    public Commandline.Argument createJvmarg() {
        return this.getCommandLine().createVmArgument();
    }
    
    public void setJvm(final String s) {
        this.getCommandLine().setVm(s);
    }
    
    public void addSysproperty(final Environment.Variable sysp) {
        this.getCommandLine().addSysproperty(sysp);
    }
    
    public void addSyspropertyset(final PropertySet sysp) {
        this.getCommandLine().addSyspropertyset(sysp);
    }
    
    public void setFailonerror(final boolean fail) {
        this.failOnError = fail;
        this.incompatibleWithSpawn |= fail;
    }
    
    public void setDir(final File d) {
        this.dir = d;
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
    
    public void setMaxmemory(final String max) {
        this.getCommandLine().setMaxmemory(max);
    }
    
    public void setJVMVersion(final String value) {
        this.getCommandLine().setVmversion(value);
    }
    
    public void addEnv(final Environment.Variable var) {
        this.env.addVariable(var);
    }
    
    public void setNewenvironment(final boolean newenv) {
        this.newEnvironment = newenv;
    }
    
    public void setAppend(final boolean append) {
        this.redirector.setAppend(append);
        this.incompatibleWithSpawn = true;
    }
    
    public void setTimeout(final Long value) {
        this.timeout = value;
        this.incompatibleWithSpawn |= (this.timeout != null);
    }
    
    public void addAssertions(final Assertions asserts) {
        if (this.getCommandLine().getAssertions() != null) {
            throw new BuildException("Only one assertion declaration is allowed");
        }
        this.getCommandLine().setAssertions(asserts);
    }
    
    public void addConfiguredRedirector(final RedirectorElement redirectorElement) {
        if (this.redirectorElement != null) {
            throw new BuildException("cannot have > 1 nested redirectors");
        }
        this.redirectorElement = redirectorElement;
        this.incompatibleWithSpawn = true;
    }
    
    @Override
    protected void handleOutput(final String output) {
        if (this.redirector.getOutputStream() != null) {
            this.redirector.handleOutput(output);
        }
        else {
            super.handleOutput(output);
        }
    }
    
    public int handleInput(final byte[] buffer, final int offset, final int length) throws IOException {
        return this.redirector.handleInput(buffer, offset, length);
    }
    
    @Override
    protected void handleFlush(final String output) {
        if (this.redirector.getOutputStream() != null) {
            this.redirector.handleFlush(output);
        }
        else {
            super.handleFlush(output);
        }
    }
    
    @Override
    protected void handleErrorOutput(final String output) {
        if (this.redirector.getErrorStream() != null) {
            this.redirector.handleErrorOutput(output);
        }
        else {
            super.handleErrorOutput(output);
        }
    }
    
    @Override
    protected void handleErrorFlush(final String output) {
        if (this.redirector.getErrorStream() != null) {
            this.redirector.handleErrorFlush(output);
        }
        else {
            super.handleErrorFlush(output);
        }
    }
    
    protected void setupRedirector() {
        this.redirector.setInput(this.input);
        this.redirector.setInputString(this.inputString);
        this.redirector.setOutput(this.output);
        this.redirector.setError(this.error);
        if (this.redirectorElement != null) {
            this.redirectorElement.configure(this.redirector);
        }
        if (!this.spawn && this.input == null && this.inputString == null) {
            this.redirector.setInputStream(new KeepAliveInputStream(this.getProject().getDefaultInputStream()));
        }
    }
    
    private void run(final CommandlineJava command) throws BuildException {
        try {
            final ExecuteJava exe = new ExecuteJava();
            exe.setJavaCommand(command.getJavaCommand());
            exe.setClasspath(command.getClasspath());
            exe.setSystemProperties(command.getSystemProperties());
            exe.setPermissions(this.perm);
            exe.setTimeout(this.timeout);
            this.redirector.createStreams();
            exe.execute(this.getProject());
            this.redirector.complete();
            if (exe.killedProcess()) {
                throw new BuildException("Timeout: killed the sub-process");
            }
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
    
    private int fork(final String[] command) throws BuildException {
        final Execute exe = new Execute(this.redirector.createHandler(), this.createWatchdog());
        this.setupExecutable(exe, command);
        try {
            final int rc = exe.execute();
            this.redirector.complete();
            if (exe.killedProcess()) {
                throw new BuildException("Timeout: killed the sub-process");
            }
            return rc;
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
    
    private void spawn(final String[] command) throws BuildException {
        final Execute exe = new Execute();
        this.setupExecutable(exe, command);
        try {
            exe.spawn();
        }
        catch (IOException e) {
            throw new BuildException(e, this.getLocation());
        }
    }
    
    private void setupExecutable(final Execute exe, final String[] command) {
        exe.setAntRun(this.getProject());
        this.setupWorkingDir(exe);
        this.setupEnvironment(exe);
        this.setupCommandLine(exe, command);
    }
    
    private void setupEnvironment(final Execute exe) {
        final String[] environment = this.env.getVariables();
        if (environment != null) {
            for (int i = 0; i < environment.length; ++i) {
                this.log("Setting environment variable: " + environment[i], 3);
            }
        }
        exe.setNewenvironment(this.newEnvironment);
        exe.setEnvironment(environment);
    }
    
    private void setupWorkingDir(final Execute exe) {
        if (this.dir == null) {
            this.dir = this.getProject().getBaseDir();
        }
        else if (!this.dir.exists() || !this.dir.isDirectory()) {
            throw new BuildException(this.dir.getAbsolutePath() + " is not a valid directory", this.getLocation());
        }
        exe.setWorkingDirectory(this.dir);
    }
    
    private void setupCommandLine(final Execute exe, final String[] command) {
        if (Os.isFamily("openvms")) {
            this.setupCommandLineForVMS(exe, command);
        }
        else {
            exe.setCommandline(command);
        }
    }
    
    private void setupCommandLineForVMS(final Execute exe, final String[] command) {
        ExecuteJava.setupCommandLineForVMS(exe, command);
    }
    
    protected void run(final String classname, final Vector<String> args) throws BuildException {
        final CommandlineJava cmdj = new CommandlineJava();
        cmdj.setClassname(classname);
        for (int size = args.size(), i = 0; i < size; ++i) {
            cmdj.createArgument().setValue(args.elementAt(i));
        }
        this.run(cmdj);
    }
    
    public void clearArgs() {
        this.getCommandLine().clearJavaArgs();
    }
    
    protected ExecuteWatchdog createWatchdog() throws BuildException {
        if (this.timeout == null) {
            return null;
        }
        return new ExecuteWatchdog(this.timeout);
    }
    
    private void log(final Throwable t) {
        final StringWriter sw = new StringWriter();
        final PrintWriter w = new PrintWriter(sw);
        t.printStackTrace(w);
        w.close();
        this.log(sw.toString(), 0);
    }
    
    public CommandlineJava getCommandLine() {
        return this.cmdl;
    }
    
    public CommandlineJava.SysProperties getSysProperties() {
        return this.getCommandLine().getSystemProperties();
    }
}
