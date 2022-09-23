// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.File;
import org.apache.tools.ant.util.JavaEnvUtils;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.ProjectComponent;
import java.lang.reflect.InvocationTargetException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.util.Watchdog;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import java.io.PrintStream;
import java.lang.reflect.Method;
import org.apache.tools.ant.types.Permissions;
import org.apache.tools.ant.types.CommandlineJava;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.util.TimeoutObserver;

public class ExecuteJava implements Runnable, TimeoutObserver
{
    private Commandline javaCommand;
    private Path classpath;
    private CommandlineJava.SysProperties sysProperties;
    private Permissions perm;
    private Method main;
    private Long timeout;
    private volatile Throwable caught;
    private volatile boolean timedOut;
    private Thread thread;
    
    public ExecuteJava() {
        this.javaCommand = null;
        this.classpath = null;
        this.sysProperties = null;
        this.perm = null;
        this.main = null;
        this.timeout = null;
        this.caught = null;
        this.timedOut = false;
        this.thread = null;
    }
    
    public void setJavaCommand(final Commandline javaCommand) {
        this.javaCommand = javaCommand;
    }
    
    public void setClasspath(final Path p) {
        this.classpath = p;
    }
    
    public void setSystemProperties(final CommandlineJava.SysProperties s) {
        this.sysProperties = s;
    }
    
    public void setPermissions(final Permissions permissions) {
        this.perm = permissions;
    }
    
    @Deprecated
    public void setOutput(final PrintStream out) {
    }
    
    public void setTimeout(final Long timeout) {
        this.timeout = timeout;
    }
    
    public void execute(final Project project) throws BuildException {
        final String classname = this.javaCommand.getExecutable();
        AntClassLoader loader = null;
        try {
            if (this.sysProperties != null) {
                this.sysProperties.setSystem();
            }
            Class<?> target = null;
            try {
                if (this.classpath == null) {
                    target = Class.forName(classname);
                }
                else {
                    loader = project.createClassLoader(this.classpath);
                    loader.setParent(project.getCoreLoader());
                    loader.setParentFirst(false);
                    loader.addJavaLibraries();
                    loader.setIsolated(true);
                    loader.setThreadContextLoader();
                    loader.forceLoadClass(classname);
                    target = Class.forName(classname, true, loader);
                }
            }
            catch (ClassNotFoundException e5) {
                throw new BuildException("Could not find " + classname + "." + " Make sure you have it in your" + " classpath");
            }
            this.main = target.getMethod("main", String[].class);
            if (this.main == null) {
                throw new BuildException("Could not find main() method in " + classname);
            }
            if ((this.main.getModifiers() & 0x8) == 0x0) {
                throw new BuildException("main() method in " + classname + " is not declared static");
            }
            if (this.timeout == null) {
                this.run();
            }
            else {
                this.thread = new Thread(this, "ExecuteJava");
                final Task currentThreadTask = project.getThreadTask(Thread.currentThread());
                project.registerThreadTask(this.thread, currentThreadTask);
                this.thread.setDaemon(true);
                final Watchdog w = new Watchdog(this.timeout);
                w.addTimeoutObserver(this);
                synchronized (this) {
                    this.thread.start();
                    w.start();
                    try {
                        this.wait();
                    }
                    catch (InterruptedException ex) {}
                    if (this.timedOut) {
                        project.log("Timeout: sub-process interrupted", 1);
                    }
                    else {
                        this.thread = null;
                        w.stop();
                    }
                }
            }
            if (this.caught != null) {
                throw this.caught;
            }
        }
        catch (BuildException e) {
            throw e;
        }
        catch (SecurityException e2) {
            throw e2;
        }
        catch (ThreadDeath e3) {
            throw e3;
        }
        catch (Throwable e4) {
            throw new BuildException(e4);
        }
        finally {
            if (loader != null) {
                loader.resetThreadContextLoader();
                loader.cleanup();
                loader = null;
            }
            if (this.sysProperties != null) {
                this.sysProperties.restoreSystem();
            }
        }
    }
    
    public void run() {
        final Object[] argument = { this.javaCommand.getArguments() };
        try {
            if (this.perm != null) {
                this.perm.setSecurityManager();
            }
            this.main.invoke(null, argument);
        }
        catch (InvocationTargetException e) {
            final Throwable t = e.getTargetException();
            if (!(t instanceof InterruptedException)) {
                this.caught = t;
            }
        }
        catch (Throwable t2) {
            this.caught = t2;
        }
        finally {
            if (this.perm != null) {
                this.perm.restoreSecurityManager();
            }
            synchronized (this) {
                this.notifyAll();
            }
        }
    }
    
    public synchronized void timeoutOccured(final Watchdog w) {
        if (this.thread != null) {
            this.timedOut = true;
            this.thread.interrupt();
        }
        this.notifyAll();
    }
    
    public synchronized boolean killedProcess() {
        return this.timedOut;
    }
    
    public int fork(final ProjectComponent pc) throws BuildException {
        final CommandlineJava cmdl = new CommandlineJava();
        cmdl.setClassname(this.javaCommand.getExecutable());
        final String[] args = this.javaCommand.getArguments();
        for (int i = 0; i < args.length; ++i) {
            cmdl.createArgument().setValue(args[i]);
        }
        if (this.classpath != null) {
            cmdl.createClasspath(pc.getProject()).append(this.classpath);
        }
        if (this.sysProperties != null) {
            cmdl.addSysproperties(this.sysProperties);
        }
        final Redirector redirector = new Redirector(pc);
        final Execute exe = new Execute(redirector.createHandler(), (this.timeout == null) ? null : new ExecuteWatchdog(this.timeout));
        exe.setAntRun(pc.getProject());
        if (Os.isFamily("openvms")) {
            setupCommandLineForVMS(exe, cmdl.getCommandline());
        }
        else {
            exe.setCommandline(cmdl.getCommandline());
        }
        try {
            final int rc = exe.execute();
            redirector.complete();
            return rc;
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
        finally {
            this.timedOut = exe.killedProcess();
        }
    }
    
    public static void setupCommandLineForVMS(final Execute exe, final String[] command) {
        exe.setVMLauncher(true);
        File vmsJavaOptionFile = null;
        try {
            final String[] args = new String[command.length - 1];
            System.arraycopy(command, 1, args, 0, command.length - 1);
            vmsJavaOptionFile = JavaEnvUtils.createVmsJavaOptionFile(args);
            vmsJavaOptionFile.deleteOnExit();
            final String[] vmsCmd = { command[0], "-V", vmsJavaOptionFile.getPath() };
            exe.setCommandline(vmsCmd);
        }
        catch (IOException e) {
            throw new BuildException("Failed to create a temporary file for \"-V\" switch");
        }
    }
}
