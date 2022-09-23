// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.TimerTask;
import java.util.Timer;
import java.io.InterruptedIOException;
import com.google.common.annotations.VisibleForTesting;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.io.IOException;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public abstract class Shell
{
    private static final Map<Shell, Object> CHILD_SHELLS;
    public static final Logger LOG;
    private static final String WINDOWS_PROBLEMS = "https://wiki.apache.org/hadoop/WindowsProblems";
    static final String WINUTILS_EXE = "winutils.exe";
    public static final String SYSPROP_HADOOP_HOME_DIR = "hadoop.home.dir";
    public static final String ENV_HADOOP_HOME = "HADOOP_HOME";
    private static final int JAVA_SPEC_VER;
    public static final int WINDOWS_MAX_SHELL_LENGTH = 8191;
    @Deprecated
    public static final int WINDOWS_MAX_SHELL_LENGHT = 8191;
    public static final String USER_NAME_COMMAND = "whoami";
    public static final Object WindowsProcessLaunchLock;
    public static final OSType osType;
    public static final boolean WINDOWS;
    public static final boolean SOLARIS;
    public static final boolean MAC;
    public static final boolean FREEBSD;
    public static final boolean LINUX;
    public static final boolean OTHER;
    public static final boolean PPC_64;
    public static final String ENV_NAME_REGEX = "[A-Za-z_][A-Za-z0-9_]*";
    public static final String SET_PERMISSION_COMMAND = "chmod";
    public static final String SET_OWNER_COMMAND = "chown";
    public static final String SET_GROUP_COMMAND = "chgrp";
    public static final String LINK_COMMAND = "ln";
    public static final String READ_LINK_COMMAND = "readlink";
    protected long timeOutInterval;
    private final AtomicBoolean timedOut;
    protected boolean inheritParentEnv;
    static final String E_DOES_NOT_EXIST = "does not exist";
    static final String E_IS_RELATIVE = "is not an absolute path.";
    static final String E_NOT_DIRECTORY = "is not a directory.";
    static final String E_NO_EXECUTABLE = "Could not locate Hadoop executable";
    static final String E_NOT_EXECUTABLE_FILE = "Not an executable file";
    static final String E_HADOOP_PROPS_UNSET = "HADOOP_HOME and hadoop.home.dir are unset.";
    static final String E_HADOOP_PROPS_EMPTY = "HADOOP_HOME or hadoop.home.dir set to an empty string";
    static final String E_NOT_A_WINDOWS_SYSTEM = "Not a Windows system";
    private static final File HADOOP_HOME_FILE;
    private static final IOException HADOOP_HOME_DIR_FAILURE_CAUSE;
    @Deprecated
    public static final String WINUTILS;
    private static final String WINUTILS_PATH;
    private static final File WINUTILS_FILE;
    private static final IOException WINUTILS_FAILURE;
    public static final boolean isSetsidAvailable;
    public static final String TOKEN_SEPARATOR_REGEX;
    private long interval;
    private long lastTime;
    private final boolean redirectErrorStream;
    private Map<String, String> environment;
    private File dir;
    private Process process;
    private int exitCode;
    private Thread waitingThread;
    private final AtomicBoolean completed;
    
    @Deprecated
    public static boolean isJava7OrAbove() {
        return true;
    }
    
    public static boolean isJavaVersionAtLeast(final int version) {
        return Shell.JAVA_SPEC_VER >= version;
    }
    
    public static void checkWindowsCommandLineLength(final String... commands) throws IOException {
        int len = 0;
        for (final String s : commands) {
            len += s.length();
        }
        if (len > 8191) {
            throw new IOException(String.format("The command line has a length of %d exceeds maximum allowed length of %d. Command starts with: %s", len, 8191, StringUtils.join("", commands).substring(0, 100)));
        }
    }
    
    static String bashQuote(final String arg) {
        final StringBuilder buffer = new StringBuilder(arg.length() + 2);
        buffer.append('\'');
        buffer.append(arg.replace("'", "'\\''"));
        buffer.append('\'');
        return buffer.toString();
    }
    
    private static OSType getOSType() {
        final String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return OSType.OS_TYPE_WIN;
        }
        if (osName.contains("SunOS") || osName.contains("Solaris")) {
            return OSType.OS_TYPE_SOLARIS;
        }
        if (osName.contains("Mac")) {
            return OSType.OS_TYPE_MAC;
        }
        if (osName.contains("FreeBSD")) {
            return OSType.OS_TYPE_FREEBSD;
        }
        if (osName.startsWith("Linux")) {
            return OSType.OS_TYPE_LINUX;
        }
        return OSType.OS_TYPE_OTHER;
    }
    
    public static String[] getGroupsCommand() {
        return Shell.WINDOWS ? new String[] { "cmd", "/c", "groups" } : new String[] { "groups" };
    }
    
    public static String[] getGroupsForUserCommand(final String user) {
        if (Shell.WINDOWS) {
            return new String[] { getWinUtilsPath(), "groups", "-F", "\"" + user + "\"" };
        }
        final String quotedUser = bashQuote(user);
        return new String[] { "bash", "-c", "id -gn " + quotedUser + "; id -Gn " + quotedUser };
    }
    
    public static String[] getGroupsIDForUserCommand(final String user) {
        if (Shell.WINDOWS) {
            return new String[] { getWinUtilsPath(), "groups", "-F", "\"" + user + "\"" };
        }
        final String quotedUser = bashQuote(user);
        return new String[] { "bash", "-c", "id -g " + quotedUser + "; id -G " + quotedUser };
    }
    
    public static String[] getUsersForNetgroupCommand(final String netgroup) {
        return new String[] { "getent", "netgroup", netgroup };
    }
    
    public static String[] getGetPermissionCommand() {
        return Shell.WINDOWS ? new String[] { getWinUtilsPath(), "ls", "-F" } : new String[] { "ls", "-ld" };
    }
    
    public static String[] getSetPermissionCommand(final String perm, final boolean recursive) {
        if (recursive) {
            return Shell.WINDOWS ? new String[] { getWinUtilsPath(), "chmod", "-R", perm } : new String[] { "chmod", "-R", perm };
        }
        return Shell.WINDOWS ? new String[] { getWinUtilsPath(), "chmod", perm } : new String[] { "chmod", perm };
    }
    
    public static String[] getSetPermissionCommand(final String perm, final boolean recursive, final String file) {
        final String[] baseCmd = getSetPermissionCommand(perm, recursive);
        final String[] cmdWithFile = Arrays.copyOf(baseCmd, baseCmd.length + 1);
        cmdWithFile[cmdWithFile.length - 1] = file;
        return cmdWithFile;
    }
    
    public static String[] getSetOwnerCommand(final String owner) {
        return Shell.WINDOWS ? new String[] { getWinUtilsPath(), "chown", "\"" + owner + "\"" } : new String[] { "chown", owner };
    }
    
    public static String[] getSymlinkCommand(final String target, final String link) {
        return Shell.WINDOWS ? new String[] { getWinUtilsPath(), "symlink", link, target } : new String[] { "ln", "-s", target, link };
    }
    
    public static String[] getReadlinkCommand(final String link) {
        return Shell.WINDOWS ? new String[] { getWinUtilsPath(), "readlink", link } : new String[] { "readlink", link };
    }
    
    public static String[] getCheckProcessIsAliveCommand(final String pid) {
        return getSignalKillCommand(0, pid);
    }
    
    public static String[] getSignalKillCommand(final int code, final String pid) {
        if (Shell.WINDOWS) {
            if (0 == code) {
                return new String[] { getWinUtilsPath(), "task", "isAlive", pid };
            }
            return new String[] { getWinUtilsPath(), "task", "kill", pid };
        }
        else {
            final String quotedPid = bashQuote(pid);
            if (Shell.isSetsidAvailable) {
                return new String[] { "bash", "-c", "kill -" + code + " -- -" + quotedPid };
            }
            return new String[] { "bash", "-c", "kill -" + code + " " + quotedPid };
        }
    }
    
    public static String getEnvironmentVariableRegex() {
        return Shell.WINDOWS ? "%([A-Za-z_][A-Za-z0-9_]*?)%" : "\\$([A-Za-z_][A-Za-z0-9_]*)";
    }
    
    public static File appendScriptExtension(final File parent, final String basename) {
        return new File(parent, appendScriptExtension(basename));
    }
    
    public static String appendScriptExtension(final String basename) {
        return basename + (Shell.WINDOWS ? ".cmd" : ".sh");
    }
    
    public static String[] getRunScriptCommand(final File script) {
        final String absolutePath = script.getAbsolutePath();
        return Shell.WINDOWS ? new String[] { "cmd", "/c", absolutePath } : new String[] { "bash", bashQuote(absolutePath) };
    }
    
    private static File checkHadoopHome() throws FileNotFoundException {
        String home = System.getProperty("hadoop.home.dir");
        if (home == null) {
            home = System.getenv("HADOOP_HOME");
        }
        return checkHadoopHomeInner(home);
    }
    
    @VisibleForTesting
    static File checkHadoopHomeInner(String home) throws FileNotFoundException {
        if (home == null) {
            throw new FileNotFoundException("HADOOP_HOME and hadoop.home.dir are unset.");
        }
        while (home.startsWith("\"")) {
            home = home.substring(1);
        }
        while (home.endsWith("\"")) {
            home = home.substring(0, home.length() - 1);
        }
        if (home.isEmpty()) {
            throw new FileNotFoundException("HADOOP_HOME or hadoop.home.dir set to an empty string");
        }
        final File homedir = new File(home);
        if (!homedir.isAbsolute()) {
            throw new FileNotFoundException("Hadoop home directory " + homedir + " " + "is not an absolute path.");
        }
        if (!homedir.exists()) {
            throw new FileNotFoundException("Hadoop home directory " + homedir + " " + "does not exist");
        }
        if (!homedir.isDirectory()) {
            throw new FileNotFoundException("Hadoop home directory " + homedir + " " + "is not a directory.");
        }
        return homedir;
    }
    
    private static String addOsText(final String message) {
        return Shell.WINDOWS ? (message + " -see " + "https://wiki.apache.org/hadoop/WindowsProblems") : message;
    }
    
    private static FileNotFoundException fileNotFoundException(final String text, final Exception ex) {
        return (FileNotFoundException)new FileNotFoundException(text).initCause(ex);
    }
    
    public static String getHadoopHome() throws IOException {
        return getHadoopHomeDir().getCanonicalPath();
    }
    
    private static File getHadoopHomeDir() throws FileNotFoundException {
        if (Shell.HADOOP_HOME_DIR_FAILURE_CAUSE != null) {
            throw fileNotFoundException(addOsText(Shell.HADOOP_HOME_DIR_FAILURE_CAUSE.toString()), Shell.HADOOP_HOME_DIR_FAILURE_CAUSE);
        }
        return Shell.HADOOP_HOME_FILE;
    }
    
    public static File getQualifiedBin(final String executable) throws FileNotFoundException {
        return getQualifiedBinInner(getHadoopHomeDir(), executable);
    }
    
    static File getQualifiedBinInner(final File hadoopHomeDir, final String executable) throws FileNotFoundException {
        final String binDirText = "Hadoop bin directory ";
        final File bin = new File(hadoopHomeDir, "bin");
        if (!bin.exists()) {
            throw new FileNotFoundException(addOsText(binDirText + "does not exist" + ": " + bin));
        }
        if (!bin.isDirectory()) {
            throw new FileNotFoundException(addOsText(binDirText + "is not a directory." + ": " + bin));
        }
        final File exeFile = new File(bin, executable);
        if (!exeFile.exists()) {
            throw new FileNotFoundException(addOsText("Could not locate Hadoop executable: " + exeFile));
        }
        if (!exeFile.isFile()) {
            throw new FileNotFoundException(addOsText("Not an executable file: " + exeFile));
        }
        try {
            return exeFile.getCanonicalFile();
        }
        catch (IOException e) {
            throw fileNotFoundException(e.toString(), e);
        }
    }
    
    public static String getQualifiedBinPath(final String executable) throws IOException {
        return getQualifiedBin(executable).getCanonicalPath();
    }
    
    public static boolean hasWinutilsPath() {
        return Shell.WINUTILS_PATH != null;
    }
    
    public static String getWinUtilsPath() {
        if (Shell.WINUTILS_FAILURE == null) {
            return Shell.WINUTILS_PATH;
        }
        throw new RuntimeException(Shell.WINUTILS_FAILURE.toString(), Shell.WINUTILS_FAILURE);
    }
    
    public static File getWinUtilsFile() throws FileNotFoundException {
        if (Shell.WINUTILS_FAILURE == null) {
            return Shell.WINUTILS_FILE;
        }
        throw fileNotFoundException(Shell.WINUTILS_FAILURE.toString(), Shell.WINUTILS_FAILURE);
    }
    
    public static boolean checkIsBashSupported() throws InterruptedIOException {
        if (Shell.WINDOWS) {
            return false;
        }
        boolean supported = true;
        try {
            final String[] args = { "bash", "-c", "echo 1000" };
            final ShellCommandExecutor shexec = new ShellCommandExecutor(args);
            shexec.execute();
        }
        catch (InterruptedIOException iioe) {
            Shell.LOG.warn("Interrupted, unable to determine if bash is supported", iioe);
            throw iioe;
        }
        catch (IOException ioe) {
            Shell.LOG.warn("Bash is not supported by the OS", ioe);
            supported = false;
        }
        catch (SecurityException se) {
            Shell.LOG.info("Bash execution is not allowed by the JVM security manager.Considering it not supported.");
            supported = false;
        }
        return supported;
    }
    
    private static boolean isSetsidSupported() {
        if (Shell.WINDOWS) {
            return false;
        }
        ShellCommandExecutor shexec = null;
        boolean setsidSupported = true;
        try {
            final String[] args = { "setsid", "bash", "-c", "echo $$" };
            shexec = new ShellCommandExecutor(args);
            shexec.execute();
        }
        catch (IOException ioe) {
            Shell.LOG.debug("setsid is not available on this machine. So not using it.");
            setsidSupported = false;
        }
        catch (SecurityException se) {
            Shell.LOG.debug("setsid is not allowed to run by the JVM security manager. So not using it.");
            setsidSupported = false;
        }
        catch (Error err) {
            if (err.getMessage() != null && err.getMessage().contains("posix_spawn is not a supported process launch mechanism") && (Shell.FREEBSD || Shell.MAC)) {
                Shell.LOG.info("Avoiding JDK-8047340 on BSD-based systems.", err);
                setsidSupported = false;
            }
        }
        finally {
            if (Shell.LOG.isDebugEnabled()) {
                Shell.LOG.debug("setsid exited with exit code " + ((shexec != null) ? Integer.valueOf(shexec.getExitCode()) : "(null executor)"));
            }
        }
        return setsidSupported;
    }
    
    protected Shell() {
        this(0L);
    }
    
    protected Shell(final long interval) {
        this(interval, false);
    }
    
    protected Shell(final long interval, final boolean redirectErrorStream) {
        this.timeOutInterval = 0L;
        this.timedOut = new AtomicBoolean(false);
        this.inheritParentEnv = true;
        this.completed = new AtomicBoolean(false);
        this.interval = interval;
        this.lastTime = ((interval < 0L) ? 0L : (-interval));
        this.redirectErrorStream = redirectErrorStream;
    }
    
    protected void setEnvironment(final Map<String, String> env) {
        this.environment = env;
    }
    
    protected void setWorkingDirectory(final File dir) {
        this.dir = dir;
    }
    
    protected void run() throws IOException {
        if (this.lastTime + this.interval > Time.monotonicNow()) {
            return;
        }
        this.exitCode = 0;
        if (Shell.MAC) {
            System.setProperty("jdk.lang.Process.launchMechanism", "POSIX_SPAWN");
        }
        this.runCommand();
    }
    
    private void runCommand() throws IOException {
        final ProcessBuilder builder = new ProcessBuilder(this.getExecString());
        Timer timeOutTimer = null;
        ShellTimeoutTimerTask timeoutTimerTask = null;
        this.timedOut.set(false);
        this.completed.set(false);
        if (!this.inheritParentEnv) {
            builder.environment().clear();
        }
        if (this.environment != null) {
            builder.environment().putAll(this.environment);
        }
        if (this.dir != null) {
            builder.directory(this.dir);
        }
        builder.redirectErrorStream(this.redirectErrorStream);
        if (Shell.WINDOWS) {
            synchronized (Shell.WindowsProcessLaunchLock) {
                this.process = builder.start();
            }
        }
        else {
            this.process = builder.start();
        }
        this.waitingThread = Thread.currentThread();
        Shell.CHILD_SHELLS.put(this, null);
        if (this.timeOutInterval > 0L) {
            timeOutTimer = new Timer("Shell command timeout");
            timeoutTimerTask = new ShellTimeoutTimerTask(this);
            timeOutTimer.schedule(timeoutTimerTask, this.timeOutInterval);
        }
        final BufferedReader errReader = new BufferedReader(new InputStreamReader(this.process.getErrorStream(), Charset.defaultCharset()));
        final BufferedReader inReader = new BufferedReader(new InputStreamReader(this.process.getInputStream(), Charset.defaultCharset()));
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
                    if (!Shell.this.isTimedOut()) {
                        Shell.LOG.warn("Error reading the error stream", ioe);
                    }
                    else {
                        Shell.LOG.debug("Error reading the error stream due to shell command timeout", ioe);
                    }
                }
            }
        };
        try {
            errThread.start();
        }
        catch (IllegalStateException ex) {}
        catch (OutOfMemoryError oe) {
            Shell.LOG.error("Caught " + oe + ". One possible reason is that ulimit setting of 'max user processes' is too low. If so, do 'ulimit -u <largerNum>' and try again.");
            throw oe;
        }
        try {
            this.parseExecResult(inReader);
            for (String line = inReader.readLine(); line != null; line = inReader.readLine()) {}
            this.exitCode = this.process.waitFor();
            joinThread(errThread);
            this.completed.set(true);
            if (this.exitCode != 0) {
                throw new ExitCodeException(this.exitCode, errMsg.toString());
            }
        }
        catch (InterruptedException ie) {
            final InterruptedIOException iie = new InterruptedIOException(ie.toString());
            iie.initCause(ie);
            throw iie;
        }
        finally {
            if (timeOutTimer != null) {
                timeOutTimer.cancel();
            }
            try {
                inReader.close();
            }
            catch (IOException ioe) {
                Shell.LOG.warn("Error while closing the input stream", ioe);
            }
            if (!this.completed.get()) {
                errThread.interrupt();
                joinThread(errThread);
            }
            try {
                errReader.close();
            }
            catch (IOException ioe) {
                Shell.LOG.warn("Error while closing the error stream", ioe);
            }
            this.process.destroy();
            this.waitingThread = null;
            Shell.CHILD_SHELLS.remove(this);
            this.lastTime = Time.monotonicNow();
        }
    }
    
    private static void joinThread(final Thread t) {
        while (t.isAlive()) {
            try {
                t.join();
            }
            catch (InterruptedException ie) {
                if (Shell.LOG.isWarnEnabled()) {
                    Shell.LOG.warn("Interrupted while joining on: " + t, ie);
                }
                t.interrupt();
            }
        }
    }
    
    protected abstract String[] getExecString();
    
    protected abstract void parseExecResult(final BufferedReader p0) throws IOException;
    
    public String getEnvironment(final String env) {
        return this.environment.get(env);
    }
    
    public Process getProcess() {
        return this.process;
    }
    
    public int getExitCode() {
        return this.exitCode;
    }
    
    public Thread getWaitingThread() {
        return this.waitingThread;
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
    
    public static void destroyAllShellProcesses() {
        synchronized (Shell.CHILD_SHELLS) {
            for (final Shell shell : Shell.CHILD_SHELLS.keySet()) {
                if (shell.getProcess() != null) {
                    shell.getProcess().destroy();
                }
            }
            Shell.CHILD_SHELLS.clear();
        }
    }
    
    public static Set<Shell> getAllShells() {
        synchronized (Shell.CHILD_SHELLS) {
            return new HashSet<Shell>(Shell.CHILD_SHELLS.keySet());
        }
    }
    
    public static Long getMemlockLimit(final Long ulimit) {
        if (Shell.WINDOWS) {
            return Math.min(2147483647L, ulimit);
        }
        return ulimit;
    }
    
    static {
        CHILD_SHELLS = Collections.synchronizedMap(new WeakHashMap<Shell, Object>());
        LOG = LoggerFactory.getLogger(Shell.class);
        JAVA_SPEC_VER = Math.max(8, Integer.parseInt(System.getProperty("java.specification.version").split("\\.")[0]));
        WindowsProcessLaunchLock = new Object();
        osType = getOSType();
        WINDOWS = (Shell.osType == OSType.OS_TYPE_WIN);
        SOLARIS = (Shell.osType == OSType.OS_TYPE_SOLARIS);
        MAC = (Shell.osType == OSType.OS_TYPE_MAC);
        FREEBSD = (Shell.osType == OSType.OS_TYPE_FREEBSD);
        LINUX = (Shell.osType == OSType.OS_TYPE_LINUX);
        OTHER = (Shell.osType == OSType.OS_TYPE_OTHER);
        PPC_64 = System.getProperties().getProperty("os.arch").contains("ppc64");
        File home;
        IOException ex;
        try {
            home = checkHadoopHome();
            ex = null;
        }
        catch (IOException ioe) {
            if (Shell.LOG.isDebugEnabled()) {
                Shell.LOG.debug("Failed to detect a valid hadoop home directory", ioe);
            }
            ex = ioe;
            home = null;
        }
        HADOOP_HOME_FILE = home;
        HADOOP_HOME_DIR_FAILURE_CAUSE = ex;
        IOException ioe2 = null;
        String path = null;
        File file = null;
        if (Shell.WINDOWS) {
            try {
                file = getQualifiedBin("winutils.exe");
                path = file.getCanonicalPath();
                ioe2 = null;
            }
            catch (IOException e) {
                Shell.LOG.warn("Did not find {}: {}", "winutils.exe", e);
                Shell.LOG.debug("Failed to find winutils.exe", e);
                file = null;
                path = null;
                ioe2 = e;
            }
        }
        else {
            ioe2 = new FileNotFoundException("Not a Windows system");
        }
        WINUTILS_PATH = path;
        WINUTILS_FILE = file;
        WINUTILS = path;
        WINUTILS_FAILURE = ioe2;
        isSetsidAvailable = isSetsidSupported();
        TOKEN_SEPARATOR_REGEX = (Shell.WINDOWS ? "[|\n\r]" : "[ \t\n\r\f]");
    }
    
    public enum OSType
    {
        OS_TYPE_LINUX, 
        OS_TYPE_WIN, 
        OS_TYPE_SOLARIS, 
        OS_TYPE_MAC, 
        OS_TYPE_FREEBSD, 
        OS_TYPE_OTHER;
    }
    
    public static class ExitCodeException extends IOException
    {
        private final int exitCode;
        
        public ExitCodeException(final int exitCode, final String message) {
            super(message);
            this.exitCode = exitCode;
        }
        
        public int getExitCode() {
            return this.exitCode;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("ExitCodeException ");
            sb.append("exitCode=").append(this.exitCode).append(": ");
            sb.append(super.getMessage());
            return sb.toString();
        }
    }
    
    public static class ShellCommandExecutor extends Shell implements CommandExecutor
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
            this(execString, dir, env, timeout, true);
        }
        
        public ShellCommandExecutor(final String[] execString, final File dir, final Map<String, String> env, final long timeout, final boolean inheritParentEnv) {
            this.command = execString.clone();
            if (dir != null) {
                this.setWorkingDirectory(dir);
            }
            if (env != null) {
                this.setEnvironment(env);
            }
            this.timeOutInterval = timeout;
            this.inheritParentEnv = inheritParentEnv;
        }
        
        @VisibleForTesting
        public long getTimeoutInterval() {
            return this.timeOutInterval;
        }
        
        @Override
        public void execute() throws IOException {
            for (final String s : this.command) {
                if (s == null) {
                    throw new IOException("(null) entry in command string: " + StringUtils.join(" ", this.command));
                }
            }
            this.run();
        }
        
        public String[] getExecString() {
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
        
        @Override
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
        
        @Override
        public void close() {
        }
    }
    
    private static class ShellTimeoutTimerTask extends TimerTask
    {
        private final Shell shell;
        
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
    
    public interface CommandExecutor
    {
        void execute() throws IOException;
        
        int getExitCode() throws IOException;
        
        String getOutput() throws IOException;
        
        void close();
    }
}
