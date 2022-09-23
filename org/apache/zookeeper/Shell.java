// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TimerTask;
import java.util.Timer;
import java.io.IOException;
import org.apache.zookeeper.common.Time;
import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

public abstract class Shell
{
    Logger LOG;
    public static final String USER_NAME_COMMAND = "whoami";
    public static final String SET_PERMISSION_COMMAND = "chmod";
    public static final String SET_OWNER_COMMAND = "chown";
    public static final String SET_GROUP_COMMAND = "chgrp";
    protected long timeOutInterval;
    private AtomicBoolean timedOut;
    public static final String ULIMIT_COMMAND = "ulimit";
    public static final boolean WINDOWS;
    private long interval;
    private long lastTime;
    private Map<String, String> environment;
    private File dir;
    private Process process;
    private int exitCode;
    private volatile AtomicBoolean completed;
    
    public static String[] getGroupsCommand() {
        return new String[] { "bash", "-c", "groups" };
    }
    
    public static String[] getGroupsForUserCommand(final String user) {
        return new String[] { "bash", "-c", "id -Gn " + user };
    }
    
    public static String[] getGET_PERMISSION_COMMAND() {
        return new String[] { Shell.WINDOWS ? "ls" : "/bin/ls", "-ld" };
    }
    
    public static String[] getUlimitMemoryCommand(final int memoryLimit) {
        if (Shell.WINDOWS) {
            return null;
        }
        return new String[] { "ulimit", "-v", String.valueOf(memoryLimit) };
    }
    
    public Shell() {
        this(0L);
    }
    
    public Shell(final long interval) {
        this.LOG = Logger.getLogger(Shell.class);
        this.timeOutInterval = 0L;
        this.interval = interval;
        this.lastTime = ((interval < 0L) ? 0L : (-interval));
    }
    
    protected void setEnvironment(final Map<String, String> env) {
        this.environment = env;
    }
    
    protected void setWorkingDirectory(final File dir) {
        this.dir = dir;
    }
    
    protected void run() throws IOException {
        if (this.lastTime + this.interval > Time.currentElapsedTime()) {
            return;
        }
        this.exitCode = 0;
        this.runCommand();
    }
    
    private void runCommand() throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(this.getExecString());
        Timer timeOutTimer = null;
        ShellTimeoutTimerTask timeoutTimerTask = null;
        this.timedOut = new AtomicBoolean(false);
        this.completed = new AtomicBoolean(false);
        if (this.environment != null) {
            builder.environment().putAll(this.environment);
        }
        if (this.dir != null) {
            builder.directory(this.dir);
        }
        this.process = builder.start();
        if (this.timeOutInterval > 0L) {
            timeOutTimer = new Timer();
            timeoutTimerTask = new ShellTimeoutTimerTask(this);
            timeOutTimer.schedule(timeoutTimerTask, this.timeOutInterval);
        }
        final BufferedReader errReader = new BufferedReader(new InputStreamReader(this.process.getErrorStream()));
        final BufferedReader inReader = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
        final StringBuffer errMsg = new StringBuffer();
        final Thread errThread = new Thread() {
            @Override
            public void run() {
                try {
                    for (String line = errReader.readLine(); line != null && !this.isInterrupted(); line = errReader.readLine()) {
                        errMsg.append(line);
                        errMsg.append(System.getProperty("line.separator"));
                    }
                }
                catch (IOException ioe) {
                    Shell.this.LOG.warn("Error reading the error stream", ioe);
                }
            }
        };
        try {
            errThread.start();
        }
        catch (IllegalStateException ex) {}
        try {
            this.parseExecResult(inReader);
            for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {}
            this.exitCode = this.process.waitFor();
            try {
                errThread.join();
            }
            catch (InterruptedException ie) {
                this.LOG.warn("Interrupted while reading the error stream", ie);
            }
            this.completed.set(true);
            if (this.exitCode != 0) {
                throw new ExitCodeException(this.exitCode, errMsg.toString());
            }
        }
        catch (InterruptedException ie2) {
            throw new IOException(ie2.toString());
        }
        finally {
            if (timeOutTimer != null && !this.timedOut.get()) {
                timeOutTimer.cancel();
            }
            try {
                inReader.close();
            }
            catch (IOException ioe) {
                this.LOG.warn("Error while closing the input stream", ioe);
            }
            if (!this.completed.get()) {
                errThread.interrupt();
            }
            try {
                errReader.close();
            }
            catch (IOException ioe) {
                this.LOG.warn("Error while closing the error stream", ioe);
            }
            this.process.destroy();
            this.lastTime = Time.currentElapsedTime();
        }
    }
    
    protected abstract String[] getExecString();
    
    protected abstract void parseExecResult(final BufferedReader p0) throws IOException;
    
    public Process getProcess() {
        return this.process;
    }
    
    public int getExitCode() {
        return this.exitCode;
    }
    
    public boolean isTimedOut() {
        return this.timedOut.get();
    }
    
    private void setTimedOut() {
        this.timedOut.set(true);
    }
    
    public static String execCommand(final String... cmd) throws IOException {
        return execCommand(null, cmd, 0L);
    }
    
    public static String execCommand(final Map<String, String> env, final String[] cmd, final long timeout) throws IOException {
        final ShellCommandExecutor exec = new ShellCommandExecutor(cmd, null, env, timeout);
        exec.execute();
        return exec.getOutput();
    }
    
    public static String execCommand(final Map<String, String> env, final String... cmd) throws IOException {
        return execCommand(env, cmd, 0L);
    }
    
    static {
        WINDOWS = System.getProperty("os.name").startsWith("Windows");
    }
    
    public static class ExitCodeException extends IOException
    {
        int exitCode;
        
        public ExitCodeException(final int exitCode, final String message) {
            super(message);
            this.exitCode = exitCode;
        }
        
        public int getExitCode() {
            return this.exitCode;
        }
    }
    
    public static class ShellCommandExecutor extends Shell
    {
        private String[] command;
        private StringBuffer output;
        
        public ShellCommandExecutor(final String[] execString) {
            this(execString, null);
        }
        
        public ShellCommandExecutor(final String[] execString, final File dir) {
            this(execString, dir, null);
        }
        
        public ShellCommandExecutor(final String[] execString, final File dir, final Map<String, String> env) {
            this(execString, dir, env, 0L);
        }
        
        public ShellCommandExecutor(final String[] execString, final File dir, final Map<String, String> env, final long timeout) {
            this.command = execString.clone();
            if (dir != null) {
                this.setWorkingDirectory(dir);
            }
            if (env != null) {
                this.setEnvironment(env);
            }
            this.timeOutInterval = timeout;
        }
        
        public void execute() throws IOException {
            this.run();
        }
        
        @Override
        protected String[] getExecString() {
            return this.command;
        }
        
        @Override
        protected void parseExecResult(final BufferedReader lines) throws IOException {
            this.output = new StringBuffer();
            final char[] buf = new char[512];
            int nRead;
            while ((nRead = lines.read(buf, 0, buf.length)) > 0) {
                this.output.append(buf, 0, nRead);
            }
        }
        
        public String getOutput() {
            return (this.output == null) ? "" : this.output.toString();
        }
        
        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            final String[] execString;
            final String[] args = execString = this.getExecString();
            for (final String s : execString) {
                if (s.indexOf(32) >= 0) {
                    builder.append('\"').append(s).append('\"');
                }
                else {
                    builder.append(s);
                }
                builder.append(' ');
            }
            return builder.toString();
        }
    }
    
    private static class ShellTimeoutTimerTask extends TimerTask
    {
        private Shell shell;
        
        public ShellTimeoutTimerTask(final Shell shell) {
            this.shell = shell;
        }
        
        @Override
        public void run() {
            final Process p = this.shell.getProcess();
            try {
                p.exitValue();
            }
            catch (Exception e) {
                if (p != null && !this.shell.completed.get()) {
                    this.shell.setTimedOut();
                    p.destroy();
                }
            }
        }
    }
}
