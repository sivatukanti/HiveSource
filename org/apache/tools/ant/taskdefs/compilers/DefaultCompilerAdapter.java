// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.compilers;

import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.ExecuteStreamHandler;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.LogStreamHandler;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public abstract class DefaultCompilerAdapter implements CompilerAdapter, CompilerAdapterExtension
{
    private static final int COMMAND_LINE_LIMIT;
    private static final FileUtils FILE_UTILS;
    protected Path src;
    protected File destDir;
    protected String encoding;
    protected boolean debug;
    protected boolean optimize;
    protected boolean deprecation;
    protected boolean depend;
    protected boolean verbose;
    protected String target;
    protected Path bootclasspath;
    protected Path extdirs;
    protected Path compileClasspath;
    protected Path compileSourcepath;
    protected Project project;
    protected Location location;
    protected boolean includeAntRuntime;
    protected boolean includeJavaRuntime;
    protected String memoryInitialSize;
    protected String memoryMaximumSize;
    protected File[] compileList;
    protected Javac attributes;
    protected static final String lSep;
    
    public DefaultCompilerAdapter() {
        this.debug = false;
        this.optimize = false;
        this.deprecation = false;
        this.depend = false;
        this.verbose = false;
    }
    
    public void setJavac(final Javac attributes) {
        this.attributes = attributes;
        this.src = attributes.getSrcdir();
        this.destDir = attributes.getDestdir();
        this.encoding = attributes.getEncoding();
        this.debug = attributes.getDebug();
        this.optimize = attributes.getOptimize();
        this.deprecation = attributes.getDeprecation();
        this.depend = attributes.getDepend();
        this.verbose = attributes.getVerbose();
        this.target = attributes.getTarget();
        this.bootclasspath = attributes.getBootclasspath();
        this.extdirs = attributes.getExtdirs();
        this.compileList = attributes.getFileList();
        this.compileClasspath = attributes.getClasspath();
        this.compileSourcepath = attributes.getSourcepath();
        this.project = attributes.getProject();
        this.location = attributes.getLocation();
        this.includeAntRuntime = attributes.getIncludeantruntime();
        this.includeJavaRuntime = attributes.getIncludejavaruntime();
        this.memoryInitialSize = attributes.getMemoryInitialSize();
        this.memoryMaximumSize = attributes.getMemoryMaximumSize();
    }
    
    public Javac getJavac() {
        return this.attributes;
    }
    
    public String[] getSupportedFileExtensions() {
        return new String[] { "java" };
    }
    
    protected Project getProject() {
        return this.project;
    }
    
    protected Path getCompileClasspath() {
        final Path classpath = new Path(this.project);
        if (this.destDir != null && this.getJavac().isIncludeDestClasses()) {
            classpath.setLocation(this.destDir);
        }
        Path cp = this.compileClasspath;
        if (cp == null) {
            cp = new Path(this.project);
        }
        if (this.includeAntRuntime) {
            classpath.addExisting(cp.concatSystemClasspath("last"));
        }
        else {
            classpath.addExisting(cp.concatSystemClasspath("ignore"));
        }
        if (this.includeJavaRuntime) {
            classpath.addJavaRuntime();
        }
        return classpath;
    }
    
    protected Commandline setupJavacCommandlineSwitches(final Commandline cmd) {
        return this.setupJavacCommandlineSwitches(cmd, false);
    }
    
    protected Commandline setupJavacCommandlineSwitches(final Commandline cmd, final boolean useDebugLevel) {
        final Path classpath = this.getCompileClasspath();
        Path sourcepath = null;
        if (this.compileSourcepath != null) {
            sourcepath = this.compileSourcepath;
        }
        else {
            sourcepath = this.src;
        }
        final String memoryParameterPrefix = this.assumeJava11() ? "-J-" : "-J-X";
        if (this.memoryInitialSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log("Since fork is false, ignoring memoryInitialSize setting.", 1);
            }
            else {
                cmd.createArgument().setValue(memoryParameterPrefix + "ms" + this.memoryInitialSize);
            }
        }
        if (this.memoryMaximumSize != null) {
            if (!this.attributes.isForkedJavac()) {
                this.attributes.log("Since fork is false, ignoring memoryMaximumSize setting.", 1);
            }
            else {
                cmd.createArgument().setValue(memoryParameterPrefix + "mx" + this.memoryMaximumSize);
            }
        }
        if (this.attributes.getNowarn()) {
            cmd.createArgument().setValue("-nowarn");
        }
        if (this.deprecation) {
            cmd.createArgument().setValue("-deprecation");
        }
        if (this.destDir != null) {
            cmd.createArgument().setValue("-d");
            cmd.createArgument().setFile(this.destDir);
        }
        cmd.createArgument().setValue("-classpath");
        if (this.assumeJava11()) {
            final Path cp = new Path(this.project);
            final Path bp = this.getBootClassPath();
            if (bp.size() > 0) {
                cp.append(bp);
            }
            if (this.extdirs != null) {
                cp.addExtdirs(this.extdirs);
            }
            cp.append(classpath);
            cp.append(sourcepath);
            cmd.createArgument().setPath(cp);
        }
        else {
            cmd.createArgument().setPath(classpath);
            if (sourcepath.size() > 0) {
                cmd.createArgument().setValue("-sourcepath");
                cmd.createArgument().setPath(sourcepath);
            }
            if (this.target != null) {
                cmd.createArgument().setValue("-target");
                cmd.createArgument().setValue(this.target);
            }
            final Path bp2 = this.getBootClassPath();
            if (bp2.size() > 0) {
                cmd.createArgument().setValue("-bootclasspath");
                cmd.createArgument().setPath(bp2);
            }
            if (this.extdirs != null && this.extdirs.size() > 0) {
                cmd.createArgument().setValue("-extdirs");
                cmd.createArgument().setPath(this.extdirs);
            }
        }
        if (this.encoding != null) {
            cmd.createArgument().setValue("-encoding");
            cmd.createArgument().setValue(this.encoding);
        }
        if (this.debug) {
            if (useDebugLevel && !this.assumeJava11()) {
                final String debugLevel = this.attributes.getDebugLevel();
                if (debugLevel != null) {
                    cmd.createArgument().setValue("-g:" + debugLevel);
                }
                else {
                    cmd.createArgument().setValue("-g");
                }
            }
            else {
                cmd.createArgument().setValue("-g");
            }
        }
        else if (this.getNoDebugArgument() != null) {
            cmd.createArgument().setValue(this.getNoDebugArgument());
        }
        if (this.optimize) {
            cmd.createArgument().setValue("-O");
        }
        if (this.depend) {
            if (this.assumeJava11()) {
                cmd.createArgument().setValue("-depend");
            }
            else if (this.assumeJava12()) {
                cmd.createArgument().setValue("-Xdepend");
            }
            else {
                this.attributes.log("depend attribute is not supported by the modern compiler", 1);
            }
        }
        if (this.verbose) {
            cmd.createArgument().setValue("-verbose");
        }
        this.addCurrentCompilerArgs(cmd);
        return cmd;
    }
    
    protected Commandline setupModernJavacCommandlineSwitches(final Commandline cmd) {
        this.setupJavacCommandlineSwitches(cmd, true);
        if (!this.assumeJava13()) {
            final String t = this.attributes.getTarget();
            if (this.attributes.getSource() != null) {
                cmd.createArgument().setValue("-source");
                cmd.createArgument().setValue(this.adjustSourceValue(this.attributes.getSource()));
            }
            else if (t != null && this.mustSetSourceForTarget(t)) {
                this.setImplicitSourceSwitch(cmd, t, this.adjustSourceValue(t));
            }
        }
        return cmd;
    }
    
    protected Commandline setupModernJavacCommand() {
        final Commandline cmd = new Commandline();
        this.setupModernJavacCommandlineSwitches(cmd);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }
    
    protected Commandline setupJavacCommand() {
        return this.setupJavacCommand(false);
    }
    
    protected Commandline setupJavacCommand(final boolean debugLevelCheck) {
        final Commandline cmd = new Commandline();
        this.setupJavacCommandlineSwitches(cmd, debugLevelCheck);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }
    
    protected void logAndAddFilesToCompile(final Commandline cmd) {
        this.attributes.log("Compilation " + cmd.describeArguments(), 3);
        final StringBuffer niceSourceList = new StringBuffer("File");
        if (this.compileList.length != 1) {
            niceSourceList.append("s");
        }
        niceSourceList.append(" to be compiled:");
        niceSourceList.append(StringUtils.LINE_SEP);
        for (int i = 0; i < this.compileList.length; ++i) {
            final String arg = this.compileList[i].getAbsolutePath();
            cmd.createArgument().setValue(arg);
            niceSourceList.append("    ");
            niceSourceList.append(arg);
            niceSourceList.append(StringUtils.LINE_SEP);
        }
        this.attributes.log(niceSourceList.toString(), 3);
    }
    
    protected int executeExternalCompile(final String[] args, final int firstFileName) {
        return this.executeExternalCompile(args, firstFileName, true);
    }
    
    protected int executeExternalCompile(final String[] args, final int firstFileName, final boolean quoteFiles) {
        String[] commandArray = null;
        File tmpFile = null;
        try {
            if (Commandline.toString(args).length() > DefaultCompilerAdapter.COMMAND_LINE_LIMIT && firstFileName >= 0) {
                BufferedWriter out = null;
                try {
                    tmpFile = DefaultCompilerAdapter.FILE_UTILS.createTempFile("files", "", this.getJavac().getTempdir(), true, true);
                    out = new BufferedWriter(new FileWriter(tmpFile));
                    for (int i = firstFileName; i < args.length; ++i) {
                        if (quoteFiles && args[i].indexOf(" ") > -1) {
                            args[i] = args[i].replace(File.separatorChar, '/');
                            out.write("\"" + args[i] + "\"");
                        }
                        else {
                            out.write(args[i]);
                        }
                        out.newLine();
                    }
                    out.flush();
                    commandArray = new String[firstFileName + 1];
                    System.arraycopy(args, 0, commandArray, 0, firstFileName);
                    commandArray[firstFileName] = "@" + tmpFile;
                }
                catch (IOException e) {
                    throw new BuildException("Error creating temporary file", e, this.location);
                }
                finally {
                    FileUtils.close(out);
                }
            }
            else {
                commandArray = args;
            }
            try {
                final Execute exe = new Execute(new LogStreamHandler(this.attributes, 2, 1));
                if (Os.isFamily("openvms")) {
                    exe.setVMLauncher(true);
                }
                exe.setAntRun(this.project);
                exe.setWorkingDirectory(this.project.getBaseDir());
                exe.setCommandline(commandArray);
                exe.execute();
                return exe.getExitValue();
            }
            catch (IOException e2) {
                throw new BuildException("Error running " + args[0] + " compiler", e2, this.location);
            }
        }
        finally {
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }
    
    @Deprecated
    protected void addExtdirsToClasspath(final Path classpath) {
        classpath.addExtdirs(this.extdirs);
    }
    
    protected void addCurrentCompilerArgs(final Commandline cmd) {
        cmd.addArguments(this.getJavac().getCurrentCompilerArgs());
    }
    
    protected boolean assumeJava11() {
        return "javac1.1".equals(this.attributes.getCompilerVersion());
    }
    
    protected boolean assumeJava12() {
        return "javac1.2".equals(this.attributes.getCompilerVersion());
    }
    
    protected boolean assumeJava13() {
        return "javac1.3".equals(this.attributes.getCompilerVersion());
    }
    
    protected boolean assumeJava14() {
        return this.assumeJavaXY("javac1.4", "1.4");
    }
    
    protected boolean assumeJava15() {
        return this.assumeJavaXY("javac1.5", "1.5");
    }
    
    protected boolean assumeJava16() {
        return this.assumeJavaXY("javac1.6", "1.6");
    }
    
    protected boolean assumeJava17() {
        return this.assumeJavaXY("javac1.7", "1.7");
    }
    
    protected boolean assumeJava18() {
        return this.assumeJavaXY("javac1.8", "1.8");
    }
    
    private boolean assumeJavaXY(final String javacXY, final String javaEnvVersionXY) {
        return javacXY.equals(this.attributes.getCompilerVersion()) || ("classic".equals(this.attributes.getCompilerVersion()) && JavaEnvUtils.isJavaVersion(javaEnvVersionXY)) || ("modern".equals(this.attributes.getCompilerVersion()) && JavaEnvUtils.isJavaVersion(javaEnvVersionXY)) || ("extJavac".equals(this.attributes.getCompilerVersion()) && JavaEnvUtils.isJavaVersion(javaEnvVersionXY));
    }
    
    protected Path getBootClassPath() {
        final Path bp = new Path(this.project);
        if (this.bootclasspath != null) {
            bp.append(this.bootclasspath);
        }
        return bp.concatSystemBootClasspath("ignore");
    }
    
    protected String getNoDebugArgument() {
        return this.assumeJava11() ? null : "-g:none";
    }
    
    private void setImplicitSourceSwitch(final Commandline cmd, final String target, final String source) {
        this.attributes.log("", 1);
        this.attributes.log("          WARNING", 1);
        this.attributes.log("", 1);
        this.attributes.log("The -source switch defaults to " + this.getDefaultSource() + ".", 1);
        this.attributes.log("If you specify -target " + target + " you now must also specify -source " + source + ".", 1);
        this.attributes.log("Ant will implicitly add -source " + source + " for you.  Please change your build file.", 1);
        cmd.createArgument().setValue("-source");
        cmd.createArgument().setValue(source);
    }
    
    private String getDefaultSource() {
        if (this.assumeJava15() || this.assumeJava16()) {
            return "1.5 in JDK 1.5 and 1.6";
        }
        if (this.assumeJava17()) {
            return "1.7 in JDK 1.7";
        }
        if (this.assumeJava18()) {
            return "1.8 in JDK 1.8";
        }
        return "";
    }
    
    private boolean mustSetSourceForTarget(String t) {
        if (this.assumeJava14()) {
            return false;
        }
        if (t.startsWith("1.")) {
            t = t.substring(2);
        }
        return t.equals("1") || t.equals("2") || t.equals("3") || t.equals("4") || ((t.equals("5") || t.equals("6")) && !this.assumeJava15() && !this.assumeJava16()) || (t.equals("7") && !this.assumeJava17());
    }
    
    private String adjustSourceValue(final String source) {
        return (source.equals("1.1") || source.equals("1.2")) ? "1.3" : source;
    }
    
    static {
        if (Os.isFamily("os/2")) {
            COMMAND_LINE_LIMIT = 1000;
        }
        else {
            COMMAND_LINE_LIMIT = 4096;
        }
        FILE_UTILS = FileUtils.getFileUtils();
        lSep = StringUtils.LINE_SEP;
    }
}
