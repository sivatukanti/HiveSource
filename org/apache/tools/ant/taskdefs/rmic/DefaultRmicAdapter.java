// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.rmic;

import java.io.File;
import org.apache.tools.ant.util.StringUtils;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import java.util.Random;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.taskdefs.Rmic;

public abstract class DefaultRmicAdapter implements RmicAdapter
{
    private Rmic attributes;
    private FileNameMapper mapper;
    private static final Random RAND;
    public static final String RMI_STUB_SUFFIX = "_Stub";
    public static final String RMI_SKEL_SUFFIX = "_Skel";
    public static final String RMI_TIE_SUFFIX = "_Tie";
    public static final String STUB_COMPAT = "-vcompat";
    public static final String STUB_1_1 = "-v1.1";
    public static final String STUB_1_2 = "-v1.2";
    public static final String STUB_OPTION_1_1 = "1.1";
    public static final String STUB_OPTION_1_2 = "1.2";
    public static final String STUB_OPTION_COMPAT = "compat";
    
    public void setRmic(final Rmic attributes) {
        this.attributes = attributes;
        this.mapper = new RmicFileNameMapper();
    }
    
    public Rmic getRmic() {
        return this.attributes;
    }
    
    protected String getStubClassSuffix() {
        return "_Stub";
    }
    
    protected String getSkelClassSuffix() {
        return "_Skel";
    }
    
    protected String getTieClassSuffix() {
        return "_Tie";
    }
    
    public FileNameMapper getMapper() {
        return this.mapper;
    }
    
    public Path getClasspath() {
        return this.getCompileClasspath();
    }
    
    protected Path getCompileClasspath() {
        final Path classpath = new Path(this.attributes.getProject());
        classpath.setLocation(this.attributes.getBase());
        Path cp = this.attributes.getClasspath();
        if (cp == null) {
            cp = new Path(this.attributes.getProject());
        }
        if (this.attributes.getIncludeantruntime()) {
            classpath.addExisting(cp.concatSystemClasspath("last"));
        }
        else {
            classpath.addExisting(cp.concatSystemClasspath("ignore"));
        }
        if (this.attributes.getIncludejavaruntime()) {
            classpath.addJavaRuntime();
        }
        return classpath;
    }
    
    protected Commandline setupRmicCommand() {
        return this.setupRmicCommand(null);
    }
    
    protected Commandline setupRmicCommand(final String[] options) {
        final Commandline cmd = new Commandline();
        if (options != null) {
            for (int i = 0; i < options.length; ++i) {
                cmd.createArgument().setValue(options[i]);
            }
        }
        final Path classpath = this.getCompileClasspath();
        cmd.createArgument().setValue("-d");
        cmd.createArgument().setFile(this.attributes.getOutputDir());
        if (this.attributes.getExtdirs() != null) {
            cmd.createArgument().setValue("-extdirs");
            cmd.createArgument().setPath(this.attributes.getExtdirs());
        }
        cmd.createArgument().setValue("-classpath");
        cmd.createArgument().setPath(classpath);
        final String stubOption = this.addStubVersionOptions();
        if (stubOption != null) {
            cmd.createArgument().setValue(stubOption);
        }
        if (null != this.attributes.getSourceBase()) {
            cmd.createArgument().setValue("-keepgenerated");
        }
        if (this.attributes.getIiop()) {
            this.attributes.log("IIOP has been turned on.", 2);
            cmd.createArgument().setValue("-iiop");
            if (this.attributes.getIiopopts() != null) {
                this.attributes.log("IIOP Options: " + this.attributes.getIiopopts(), 2);
                cmd.createArgument().setValue(this.attributes.getIiopopts());
            }
        }
        if (this.attributes.getIdl()) {
            cmd.createArgument().setValue("-idl");
            this.attributes.log("IDL has been turned on.", 2);
            if (this.attributes.getIdlopts() != null) {
                cmd.createArgument().setValue(this.attributes.getIdlopts());
                this.attributes.log("IDL Options: " + this.attributes.getIdlopts(), 2);
            }
        }
        if (this.attributes.getDebug()) {
            cmd.createArgument().setValue("-g");
        }
        String[] compilerArgs = this.attributes.getCurrentCompilerArgs();
        compilerArgs = this.preprocessCompilerArgs(compilerArgs);
        cmd.addArguments(compilerArgs);
        this.logAndAddFilesToCompile(cmd);
        return cmd;
    }
    
    protected String addStubVersionOptions() {
        final String stubVersion = this.attributes.getStubVersion();
        String stubOption = null;
        if (null != stubVersion) {
            if ("1.1".equals(stubVersion)) {
                stubOption = "-v1.1";
            }
            else if ("1.2".equals(stubVersion)) {
                stubOption = "-v1.2";
            }
            else if ("compat".equals(stubVersion)) {
                stubOption = "-vcompat";
            }
            else {
                this.attributes.log("Unknown stub option " + stubVersion);
            }
        }
        if (stubOption == null && !this.attributes.getIiop() && !this.attributes.getIdl()) {
            stubOption = "-vcompat";
        }
        return stubOption;
    }
    
    protected String[] preprocessCompilerArgs(final String[] compilerArgs) {
        return compilerArgs;
    }
    
    protected String[] filterJvmCompilerArgs(final String[] compilerArgs) {
        final int len = compilerArgs.length;
        final List args = new ArrayList(len);
        for (final String arg : compilerArgs) {
            if (!arg.startsWith("-J")) {
                args.add(arg);
            }
            else {
                this.attributes.log("Dropping " + arg + " from compiler arguments");
            }
        }
        final int count = args.size();
        return args.toArray(new String[count]);
    }
    
    protected void logAndAddFilesToCompile(final Commandline cmd) {
        final Vector compileList = this.attributes.getCompileList();
        this.attributes.log("Compilation " + cmd.describeArguments(), 3);
        final StringBuffer niceSourceList = new StringBuffer("File");
        final int cListSize = compileList.size();
        if (cListSize != 1) {
            niceSourceList.append("s");
        }
        niceSourceList.append(" to be compiled:");
        for (int i = 0; i < cListSize; ++i) {
            final String arg = compileList.elementAt(i);
            cmd.createArgument().setValue(arg);
            niceSourceList.append("    ");
            niceSourceList.append(arg);
        }
        this.attributes.log(niceSourceList.toString(), 3);
    }
    
    static {
        RAND = new Random();
    }
    
    private class RmicFileNameMapper implements FileNameMapper
    {
        RmicFileNameMapper() {
        }
        
        public void setFrom(final String s) {
        }
        
        public void setTo(final String s) {
        }
        
        public String[] mapFileName(final String name) {
            if (name == null || !name.endsWith(".class") || name.endsWith(DefaultRmicAdapter.this.getStubClassSuffix() + ".class") || name.endsWith(DefaultRmicAdapter.this.getSkelClassSuffix() + ".class") || name.endsWith(DefaultRmicAdapter.this.getTieClassSuffix() + ".class")) {
                return null;
            }
            final String base = StringUtils.removeSuffix(name, ".class");
            final String classname = base.replace(File.separatorChar, '.');
            if (DefaultRmicAdapter.this.attributes.getVerify() && !DefaultRmicAdapter.this.attributes.isValidRmiRemote(classname)) {
                return null;
            }
            String[] target = { name + ".tmp." + DefaultRmicAdapter.RAND.nextLong() };
            if (!DefaultRmicAdapter.this.attributes.getIiop() && !DefaultRmicAdapter.this.attributes.getIdl()) {
                if ("1.2".equals(DefaultRmicAdapter.this.attributes.getStubVersion())) {
                    target = new String[] { base + DefaultRmicAdapter.this.getStubClassSuffix() + ".class" };
                }
                else {
                    target = new String[] { base + DefaultRmicAdapter.this.getStubClassSuffix() + ".class", base + DefaultRmicAdapter.this.getSkelClassSuffix() + ".class" };
                }
            }
            else if (!DefaultRmicAdapter.this.attributes.getIdl()) {
                final int lastSlash = base.lastIndexOf(File.separatorChar);
                String dirname = "";
                int index = -1;
                if (lastSlash == -1) {
                    index = 0;
                }
                else {
                    index = lastSlash + 1;
                    dirname = base.substring(0, index);
                }
                final String filename = base.substring(index);
                try {
                    final Class c = DefaultRmicAdapter.this.attributes.getLoader().loadClass(classname);
                    if (c.isInterface()) {
                        target = new String[] { dirname + "_" + filename + DefaultRmicAdapter.this.getStubClassSuffix() + ".class" };
                    }
                    else {
                        final Class interf = DefaultRmicAdapter.this.attributes.getRemoteInterface(c);
                        final String iName = interf.getName();
                        String iDir = "";
                        int iIndex = -1;
                        final int lastDot = iName.lastIndexOf(".");
                        if (lastDot == -1) {
                            iIndex = 0;
                        }
                        else {
                            iIndex = lastDot + 1;
                            iDir = iName.substring(0, iIndex);
                            iDir = iDir.replace('.', File.separatorChar);
                        }
                        target = new String[] { dirname + "_" + filename + DefaultRmicAdapter.this.getTieClassSuffix() + ".class", iDir + "_" + iName.substring(iIndex) + DefaultRmicAdapter.this.getStubClassSuffix() + ".class" };
                    }
                }
                catch (ClassNotFoundException e) {
                    DefaultRmicAdapter.this.attributes.log("Unable to verify class " + classname + ". It could not be found.", 1);
                }
                catch (NoClassDefFoundError e2) {
                    DefaultRmicAdapter.this.attributes.log("Unable to verify class " + classname + ". It is not defined.", 1);
                }
                catch (Throwable t) {
                    DefaultRmicAdapter.this.attributes.log("Unable to verify class " + classname + ". Loading caused Exception: " + t.getMessage(), 1);
                }
            }
            return target;
        }
    }
}
