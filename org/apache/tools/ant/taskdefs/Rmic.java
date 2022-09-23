// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.rmi.Remote;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.FileNameMapper;
import java.io.IOException;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapterFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.taskdefs.rmic.RmicAdapter;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import org.apache.tools.ant.AntClassLoader;
import java.util.Vector;
import org.apache.tools.ant.types.Path;
import java.io.File;

public class Rmic extends MatchingTask
{
    public static final String ERROR_RMIC_FAILED = "Rmic failed; see the compiler error output for details.";
    private File baseDir;
    private File destDir;
    private String classname;
    private File sourceBase;
    private String stubVersion;
    private Path compileClasspath;
    private Path extDirs;
    private boolean verify;
    private boolean filtering;
    private boolean iiop;
    private String iiopOpts;
    private boolean idl;
    private String idlOpts;
    private boolean debug;
    private boolean includeAntRuntime;
    private boolean includeJavaRuntime;
    private Vector compileList;
    private AntClassLoader loader;
    private FacadeTaskHelper facade;
    public static final String ERROR_UNABLE_TO_VERIFY_CLASS = "Unable to verify class ";
    public static final String ERROR_NOT_FOUND = ". It could not be found.";
    public static final String ERROR_NOT_DEFINED = ". It is not defined.";
    public static final String ERROR_LOADING_CAUSED_EXCEPTION = ". Loading caused Exception: ";
    public static final String ERROR_NO_BASE_EXISTS = "base or destdir does not exist: ";
    public static final String ERROR_NOT_A_DIR = "base or destdir is not a directory:";
    public static final String ERROR_BASE_NOT_SET = "base or destdir attribute must be set!";
    private static final FileUtils FILE_UTILS;
    private String executable;
    private boolean listFiles;
    private RmicAdapter nestedAdapter;
    
    public Rmic() {
        this.verify = false;
        this.filtering = false;
        this.iiop = false;
        this.idl = false;
        this.debug = false;
        this.includeAntRuntime = true;
        this.includeJavaRuntime = false;
        this.compileList = new Vector();
        this.loader = null;
        this.executable = null;
        this.listFiles = false;
        this.nestedAdapter = null;
        this.facade = new FacadeTaskHelper("default");
    }
    
    public void setBase(final File base) {
        this.baseDir = base;
    }
    
    public void setDestdir(final File destdir) {
        this.destDir = destdir;
    }
    
    public File getDestdir() {
        return this.destDir;
    }
    
    public File getOutputDir() {
        if (this.getDestdir() != null) {
            return this.getDestdir();
        }
        return this.getBase();
    }
    
    public File getBase() {
        return this.baseDir;
    }
    
    public void setClassname(final String classname) {
        this.classname = classname;
    }
    
    public String getClassname() {
        return this.classname;
    }
    
    public void setSourceBase(final File sourceBase) {
        this.sourceBase = sourceBase;
    }
    
    public File getSourceBase() {
        return this.sourceBase;
    }
    
    public void setStubVersion(final String stubVersion) {
        this.stubVersion = stubVersion;
    }
    
    public String getStubVersion() {
        return this.stubVersion;
    }
    
    public void setFiltering(final boolean filter) {
        this.filtering = filter;
    }
    
    public boolean getFiltering() {
        return this.filtering;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public boolean getDebug() {
        return this.debug;
    }
    
    public synchronized void setClasspath(final Path classpath) {
        if (this.compileClasspath == null) {
            this.compileClasspath = classpath;
        }
        else {
            this.compileClasspath.append(classpath);
        }
    }
    
    public synchronized Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        return this.compileClasspath.createPath();
    }
    
    public void setClasspathRef(final Reference pathRef) {
        this.createClasspath().setRefid(pathRef);
    }
    
    public Path getClasspath() {
        return this.compileClasspath;
    }
    
    public void setVerify(final boolean verify) {
        this.verify = verify;
    }
    
    public boolean getVerify() {
        return this.verify;
    }
    
    public void setIiop(final boolean iiop) {
        this.iiop = iiop;
    }
    
    public boolean getIiop() {
        return this.iiop;
    }
    
    public void setIiopopts(final String iiopOpts) {
        this.iiopOpts = iiopOpts;
    }
    
    public String getIiopopts() {
        return this.iiopOpts;
    }
    
    public void setIdl(final boolean idl) {
        this.idl = idl;
    }
    
    public boolean getIdl() {
        return this.idl;
    }
    
    public void setIdlopts(final String idlOpts) {
        this.idlOpts = idlOpts;
    }
    
    public String getIdlopts() {
        return this.idlOpts;
    }
    
    public Vector getFileList() {
        return this.compileList;
    }
    
    public void setIncludeantruntime(final boolean include) {
        this.includeAntRuntime = include;
    }
    
    public boolean getIncludeantruntime() {
        return this.includeAntRuntime;
    }
    
    public void setIncludejavaruntime(final boolean include) {
        this.includeJavaRuntime = include;
    }
    
    public boolean getIncludejavaruntime() {
        return this.includeJavaRuntime;
    }
    
    public synchronized void setExtdirs(final Path extDirs) {
        if (this.extDirs == null) {
            this.extDirs = extDirs;
        }
        else {
            this.extDirs.append(extDirs);
        }
    }
    
    public synchronized Path createExtdirs() {
        if (this.extDirs == null) {
            this.extDirs = new Path(this.getProject());
        }
        return this.extDirs.createPath();
    }
    
    public Path getExtdirs() {
        return this.extDirs;
    }
    
    public Vector getCompileList() {
        return this.compileList;
    }
    
    public void setCompiler(final String compiler) {
        if (compiler.length() > 0) {
            this.facade.setImplementation(compiler);
        }
    }
    
    public String getCompiler() {
        this.facade.setMagicValue(this.getProject().getProperty("build.rmic"));
        return this.facade.getImplementation();
    }
    
    public ImplementationSpecificArgument createCompilerArg() {
        final ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }
    
    public String[] getCurrentCompilerArgs() {
        this.getCompiler();
        return this.facade.getArgs();
    }
    
    public void setExecutable(final String ex) {
        this.executable = ex;
    }
    
    public String getExecutable() {
        return this.executable;
    }
    
    public Path createCompilerClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }
    
    public void setListfiles(final boolean list) {
        this.listFiles = list;
    }
    
    public void add(final RmicAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one rmic adapter");
        }
        this.nestedAdapter = adapter;
    }
    
    @Override
    public void execute() throws BuildException {
        try {
            this.compileList.clear();
            final File outputDir = this.getOutputDir();
            if (outputDir == null) {
                throw new BuildException("base or destdir attribute must be set!", this.getLocation());
            }
            if (!outputDir.exists()) {
                throw new BuildException("base or destdir does not exist: " + outputDir, this.getLocation());
            }
            if (!outputDir.isDirectory()) {
                throw new BuildException("base or destdir is not a directory:" + outputDir, this.getLocation());
            }
            if (this.verify) {
                this.log("Verify has been turned on.", 3);
            }
            final RmicAdapter adapter = (this.nestedAdapter != null) ? this.nestedAdapter : RmicAdapterFactory.getRmic(this.getCompiler(), this, this.createCompilerClasspath());
            adapter.setRmic(this);
            final Path classpath = adapter.getClasspath();
            this.loader = this.getProject().createClassLoader(classpath);
            if (this.classname == null) {
                final DirectoryScanner ds = this.getDirectoryScanner(this.baseDir);
                final String[] files = ds.getIncludedFiles();
                this.scanDir(this.baseDir, files, adapter.getMapper());
            }
            else {
                final String path = this.classname.replace('.', File.separatorChar) + ".class";
                final File f = new File(this.baseDir, path);
                if (f.isFile()) {
                    this.scanDir(this.baseDir, new String[] { path }, adapter.getMapper());
                }
                else {
                    this.compileList.add(this.classname);
                }
            }
            final int fileCount = this.compileList.size();
            if (fileCount > 0) {
                this.log("RMI Compiling " + fileCount + " class" + ((fileCount > 1) ? "es" : "") + " to " + outputDir, 2);
                if (this.listFiles) {
                    for (int i = 0; i < fileCount; ++i) {
                        this.log(this.compileList.get(i).toString());
                    }
                }
                if (!adapter.execute()) {
                    throw new BuildException("Rmic failed; see the compiler error output for details.", this.getLocation());
                }
            }
            if (null != this.sourceBase && !outputDir.equals(this.sourceBase) && fileCount > 0) {
                if (this.idl) {
                    this.log("Cannot determine sourcefiles in idl mode, ", 1);
                    this.log("sourcebase attribute will be ignored.", 1);
                }
                else {
                    for (int j = 0; j < fileCount; ++j) {
                        this.moveGeneratedFile(outputDir, this.sourceBase, this.compileList.elementAt(j), adapter);
                    }
                }
            }
        }
        finally {
            this.cleanup();
        }
    }
    
    protected void cleanup() {
        if (this.loader != null) {
            this.loader.cleanup();
            this.loader = null;
        }
    }
    
    private void moveGeneratedFile(final File baseDir, final File sourceBaseFile, final String classname, final RmicAdapter adapter) throws BuildException {
        final String classFileName = classname.replace('.', File.separatorChar) + ".class";
        final String[] generatedFiles = adapter.getMapper().mapFileName(classFileName);
        for (int i = 0; i < generatedFiles.length; ++i) {
            final String generatedFile = generatedFiles[i];
            if (generatedFile.endsWith(".class")) {
                final String sourceFileName = StringUtils.removeSuffix(generatedFile, ".class") + ".java";
                final File oldFile = new File(baseDir, sourceFileName);
                if (oldFile.exists()) {
                    final File newFile = new File(sourceBaseFile, sourceFileName);
                    try {
                        if (this.filtering) {
                            Rmic.FILE_UTILS.copyFile(oldFile, newFile, new FilterSetCollection(this.getProject().getGlobalFilterSet()));
                        }
                        else {
                            Rmic.FILE_UTILS.copyFile(oldFile, newFile);
                        }
                        oldFile.delete();
                    }
                    catch (IOException ioe) {
                        final String msg = "Failed to copy " + oldFile + " to " + newFile + " due to " + ioe.getMessage();
                        throw new BuildException(msg, ioe, this.getLocation());
                    }
                }
            }
        }
    }
    
    protected void scanDir(final File baseDir, final String[] files, final FileNameMapper mapper) {
        String[] newFiles = files;
        if (this.idl) {
            this.log("will leave uptodate test to rmic implementation in idl mode.", 3);
        }
        else if (this.iiop && this.iiopOpts != null && this.iiopOpts.indexOf("-always") > -1) {
            this.log("no uptodate test as -always option has been specified", 3);
        }
        else {
            final SourceFileScanner sfs = new SourceFileScanner(this);
            newFiles = sfs.restrict(files, baseDir, this.getOutputDir(), mapper);
        }
        for (int i = 0; i < newFiles.length; ++i) {
            String name = newFiles[i].replace(File.separatorChar, '.');
            name = name.substring(0, name.lastIndexOf(".class"));
            this.compileList.addElement(name);
        }
    }
    
    public boolean isValidRmiRemote(final String classname) {
        try {
            final Class testClass = this.loader.loadClass(classname);
            return (!testClass.isInterface() || this.iiop || this.idl) && this.isValidRmiRemote(testClass);
        }
        catch (ClassNotFoundException e) {
            this.log("Unable to verify class " + classname + ". It could not be found.", 1);
        }
        catch (NoClassDefFoundError e2) {
            this.log("Unable to verify class " + classname + ". It is not defined.", 1);
        }
        catch (Throwable t) {
            this.log("Unable to verify class " + classname + ". Loading caused Exception: " + t.getMessage(), 1);
        }
        return false;
    }
    
    public Class getRemoteInterface(final Class testClass) {
        if (Remote.class.isAssignableFrom(testClass)) {
            final Class[] interfaces = testClass.getInterfaces();
            if (interfaces != null) {
                for (int i = 0; i < interfaces.length; ++i) {
                    if (Remote.class.isAssignableFrom(interfaces[i])) {
                        return interfaces[i];
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isValidRmiRemote(final Class testClass) {
        return this.getRemoteInterface(testClass) != null;
    }
    
    public ClassLoader getLoader() {
        return this.loader;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
    
    public class ImplementationSpecificArgument extends org.apache.tools.ant.util.facade.ImplementationSpecificArgument
    {
        public void setCompiler(final String impl) {
            super.setImplementation(impl);
        }
    }
}
