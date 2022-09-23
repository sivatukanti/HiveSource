// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.OutputStream;
import java.util.Iterator;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterExtension;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapterFactory;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.util.SourceFileScanner;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.JavaEnvUtils;
import java.util.HashMap;
import org.apache.tools.ant.taskdefs.compilers.CompilerAdapter;
import java.util.Map;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;

public class Javac extends MatchingTask
{
    private static final String FAIL_MSG = "Compile failed; see the compiler error output for details.";
    private static final String JAVAC18 = "javac1.8";
    private static final String JAVAC17 = "javac1.7";
    private static final String JAVAC16 = "javac1.6";
    private static final String JAVAC15 = "javac1.5";
    private static final String JAVAC14 = "javac1.4";
    private static final String JAVAC13 = "javac1.3";
    private static final String JAVAC12 = "javac1.2";
    private static final String JAVAC11 = "javac1.1";
    private static final String MODERN = "modern";
    private static final String CLASSIC = "classic";
    private static final String EXTJAVAC = "extJavac";
    private static final FileUtils FILE_UTILS;
    private Path src;
    private File destDir;
    private Path compileClasspath;
    private Path compileSourcepath;
    private String encoding;
    private boolean debug;
    private boolean optimize;
    private boolean deprecation;
    private boolean depend;
    private boolean verbose;
    private String targetAttribute;
    private Path bootclasspath;
    private Path extdirs;
    private Boolean includeAntRuntime;
    private boolean includeJavaRuntime;
    private boolean fork;
    private String forkedExecutable;
    private boolean nowarn;
    private String memoryInitialSize;
    private String memoryMaximumSize;
    private FacadeTaskHelper facade;
    protected boolean failOnError;
    protected boolean listFiles;
    protected File[] compileList;
    private Map<String, Long> packageInfos;
    private String source;
    private String debugLevel;
    private File tmpDir;
    private String updatedProperty;
    private String errorProperty;
    private boolean taskSuccess;
    private boolean includeDestClasses;
    private CompilerAdapter nestedAdapter;
    private boolean createMissingPackageInfoClass;
    private static final byte[] PACKAGE_INFO_CLASS_HEADER;
    private static final byte[] PACKAGE_INFO_CLASS_FOOTER;
    
    public Javac() {
        this.debug = false;
        this.optimize = false;
        this.deprecation = false;
        this.depend = false;
        this.verbose = false;
        this.includeJavaRuntime = false;
        this.fork = false;
        this.forkedExecutable = null;
        this.nowarn = false;
        this.facade = null;
        this.failOnError = true;
        this.listFiles = false;
        this.compileList = new File[0];
        this.packageInfos = new HashMap<String, Long>();
        this.taskSuccess = true;
        this.includeDestClasses = true;
        this.nestedAdapter = null;
        this.createMissingPackageInfoClass = true;
        this.facade = new FacadeTaskHelper(this.assumedJavaVersion());
    }
    
    private String assumedJavaVersion() {
        if (JavaEnvUtils.isJavaVersion("1.4")) {
            return "javac1.4";
        }
        if (JavaEnvUtils.isJavaVersion("1.5")) {
            return "javac1.5";
        }
        if (JavaEnvUtils.isJavaVersion("1.6")) {
            return "javac1.6";
        }
        if (JavaEnvUtils.isJavaVersion("1.7")) {
            return "javac1.7";
        }
        if (JavaEnvUtils.isJavaVersion("1.8")) {
            return "javac1.8";
        }
        return "classic";
    }
    
    public String getDebugLevel() {
        return this.debugLevel;
    }
    
    public void setDebugLevel(final String v) {
        this.debugLevel = v;
    }
    
    public String getSource() {
        return (this.source != null) ? this.source : this.getProject().getProperty("ant.build.javac.source");
    }
    
    public void setSource(final String v) {
        this.source = v;
    }
    
    public Path createSrc() {
        if (this.src == null) {
            this.src = new Path(this.getProject());
        }
        return this.src.createPath();
    }
    
    protected Path recreateSrc() {
        this.src = null;
        return this.createSrc();
    }
    
    public void setSrcdir(final Path srcDir) {
        if (this.src == null) {
            this.src = srcDir;
        }
        else {
            this.src.append(srcDir);
        }
    }
    
    public Path getSrcdir() {
        return this.src;
    }
    
    public void setDestdir(final File destDir) {
        this.destDir = destDir;
    }
    
    public File getDestdir() {
        return this.destDir;
    }
    
    public void setSourcepath(final Path sourcepath) {
        if (this.compileSourcepath == null) {
            this.compileSourcepath = sourcepath;
        }
        else {
            this.compileSourcepath.append(sourcepath);
        }
    }
    
    public Path getSourcepath() {
        return this.compileSourcepath;
    }
    
    public Path createSourcepath() {
        if (this.compileSourcepath == null) {
            this.compileSourcepath = new Path(this.getProject());
        }
        return this.compileSourcepath.createPath();
    }
    
    public void setSourcepathRef(final Reference r) {
        this.createSourcepath().setRefid(r);
    }
    
    public void setClasspath(final Path classpath) {
        if (this.compileClasspath == null) {
            this.compileClasspath = classpath;
        }
        else {
            this.compileClasspath.append(classpath);
        }
    }
    
    public Path getClasspath() {
        return this.compileClasspath;
    }
    
    public Path createClasspath() {
        if (this.compileClasspath == null) {
            this.compileClasspath = new Path(this.getProject());
        }
        return this.compileClasspath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public void setBootclasspath(final Path bootclasspath) {
        if (this.bootclasspath == null) {
            this.bootclasspath = bootclasspath;
        }
        else {
            this.bootclasspath.append(bootclasspath);
        }
    }
    
    public Path getBootclasspath() {
        return this.bootclasspath;
    }
    
    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(this.getProject());
        }
        return this.bootclasspath.createPath();
    }
    
    public void setBootClasspathRef(final Reference r) {
        this.createBootclasspath().setRefid(r);
    }
    
    public void setExtdirs(final Path extdirs) {
        if (this.extdirs == null) {
            this.extdirs = extdirs;
        }
        else {
            this.extdirs.append(extdirs);
        }
    }
    
    public Path getExtdirs() {
        return this.extdirs;
    }
    
    public Path createExtdirs() {
        if (this.extdirs == null) {
            this.extdirs = new Path(this.getProject());
        }
        return this.extdirs.createPath();
    }
    
    public void setListfiles(final boolean list) {
        this.listFiles = list;
    }
    
    public boolean getListfiles() {
        return this.listFiles;
    }
    
    public void setFailonerror(final boolean fail) {
        this.failOnError = fail;
    }
    
    public void setProceed(final boolean proceed) {
        this.failOnError = !proceed;
    }
    
    public boolean getFailonerror() {
        return this.failOnError;
    }
    
    public void setDeprecation(final boolean deprecation) {
        this.deprecation = deprecation;
    }
    
    public boolean getDeprecation() {
        return this.deprecation;
    }
    
    public void setMemoryInitialSize(final String memoryInitialSize) {
        this.memoryInitialSize = memoryInitialSize;
    }
    
    public String getMemoryInitialSize() {
        return this.memoryInitialSize;
    }
    
    public void setMemoryMaximumSize(final String memoryMaximumSize) {
        this.memoryMaximumSize = memoryMaximumSize;
    }
    
    public String getMemoryMaximumSize() {
        return this.memoryMaximumSize;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public boolean getDebug() {
        return this.debug;
    }
    
    public void setOptimize(final boolean optimize) {
        this.optimize = optimize;
    }
    
    public boolean getOptimize() {
        return this.optimize;
    }
    
    public void setDepend(final boolean depend) {
        this.depend = depend;
    }
    
    public boolean getDepend() {
        return this.depend;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public boolean getVerbose() {
        return this.verbose;
    }
    
    public void setTarget(final String target) {
        this.targetAttribute = target;
    }
    
    public String getTarget() {
        return (this.targetAttribute != null) ? this.targetAttribute : this.getProject().getProperty("ant.build.javac.target");
    }
    
    public void setIncludeantruntime(final boolean include) {
        this.includeAntRuntime = include;
    }
    
    public boolean getIncludeantruntime() {
        return this.includeAntRuntime == null || this.includeAntRuntime;
    }
    
    public void setIncludejavaruntime(final boolean include) {
        this.includeJavaRuntime = include;
    }
    
    public boolean getIncludejavaruntime() {
        return this.includeJavaRuntime;
    }
    
    public void setFork(final boolean f) {
        this.fork = f;
    }
    
    public void setExecutable(final String forkExec) {
        this.forkedExecutable = forkExec;
    }
    
    public String getExecutable() {
        return this.forkedExecutable;
    }
    
    public boolean isForkedJavac() {
        return this.fork || "extJavac".equalsIgnoreCase(this.getCompiler());
    }
    
    public String getJavacExecutable() {
        if (this.forkedExecutable == null && this.isForkedJavac()) {
            this.forkedExecutable = this.getSystemJavac();
        }
        else if (this.forkedExecutable != null && !this.isForkedJavac()) {
            this.forkedExecutable = null;
        }
        return this.forkedExecutable;
    }
    
    public void setNowarn(final boolean flag) {
        this.nowarn = flag;
    }
    
    public boolean getNowarn() {
        return this.nowarn;
    }
    
    public ImplementationSpecificArgument createCompilerArg() {
        final ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }
    
    public String[] getCurrentCompilerArgs() {
        final String chosen = this.facade.getExplicitChoice();
        try {
            final String appliedCompiler = this.getCompiler();
            this.facade.setImplementation(appliedCompiler);
            String[] result = this.facade.getArgs();
            final String altCompilerName = this.getAltCompilerName(this.facade.getImplementation());
            if (result.length == 0 && altCompilerName != null) {
                this.facade.setImplementation(altCompilerName);
                result = this.facade.getArgs();
            }
            return result;
        }
        finally {
            this.facade.setImplementation(chosen);
        }
    }
    
    private String getAltCompilerName(final String anImplementation) {
        if ("javac1.7".equalsIgnoreCase(anImplementation) || "javac1.8".equalsIgnoreCase(anImplementation) || "javac1.6".equalsIgnoreCase(anImplementation) || "javac1.5".equalsIgnoreCase(anImplementation) || "javac1.4".equalsIgnoreCase(anImplementation) || "javac1.3".equalsIgnoreCase(anImplementation)) {
            return "modern";
        }
        if ("javac1.2".equalsIgnoreCase(anImplementation) || "javac1.1".equalsIgnoreCase(anImplementation)) {
            return "classic";
        }
        if ("modern".equalsIgnoreCase(anImplementation)) {
            final String nextSelected = this.assumedJavaVersion();
            if ("javac1.7".equalsIgnoreCase(nextSelected) || "javac1.8".equalsIgnoreCase(nextSelected) || "javac1.6".equalsIgnoreCase(nextSelected) || "javac1.5".equalsIgnoreCase(nextSelected) || "javac1.4".equalsIgnoreCase(nextSelected) || "javac1.3".equalsIgnoreCase(nextSelected)) {
                return nextSelected;
            }
        }
        if ("classic".equalsIgnoreCase(anImplementation)) {
            return this.assumedJavaVersion();
        }
        if ("extJavac".equalsIgnoreCase(anImplementation)) {
            return this.assumedJavaVersion();
        }
        return null;
    }
    
    public void setTempdir(final File tmpDir) {
        this.tmpDir = tmpDir;
    }
    
    public File getTempdir() {
        return this.tmpDir;
    }
    
    public void setUpdatedProperty(final String updatedProperty) {
        this.updatedProperty = updatedProperty;
    }
    
    public void setErrorProperty(final String errorProperty) {
        this.errorProperty = errorProperty;
    }
    
    public void setIncludeDestClasses(final boolean includeDestClasses) {
        this.includeDestClasses = includeDestClasses;
    }
    
    public boolean isIncludeDestClasses() {
        return this.includeDestClasses;
    }
    
    public boolean getTaskSuccess() {
        return this.taskSuccess;
    }
    
    public Path createCompilerClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }
    
    public void add(final CompilerAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one compiler adapter");
        }
        this.nestedAdapter = adapter;
    }
    
    public void setCreateMissingPackageInfoClass(final boolean b) {
        this.createMissingPackageInfoClass = b;
    }
    
    @Override
    public void execute() throws BuildException {
        this.checkParameters();
        this.resetFileLists();
        final String[] list = this.src.list();
        for (int i = 0; i < list.length; ++i) {
            final File srcDir = this.getProject().resolveFile(list[i]);
            if (!srcDir.exists()) {
                throw new BuildException("srcdir \"" + srcDir.getPath() + "\" does not exist!", this.getLocation());
            }
            final DirectoryScanner ds = this.getDirectoryScanner(srcDir);
            final String[] files = ds.getIncludedFiles();
            this.scanDir(srcDir, (this.destDir != null) ? this.destDir : srcDir, files);
        }
        this.compile();
        if (this.updatedProperty != null && this.taskSuccess && this.compileList.length != 0) {
            this.getProject().setNewProperty(this.updatedProperty, "true");
        }
    }
    
    protected void resetFileLists() {
        this.compileList = new File[0];
        this.packageInfos = new HashMap<String, Long>();
    }
    
    protected void scanDir(final File srcDir, final File destDir, final String[] files) {
        final GlobPatternMapper m = new GlobPatternMapper();
        final String[] extensions = this.findSupportedFileExtensions();
        for (int i = 0; i < extensions.length; ++i) {
            m.setFrom(extensions[i]);
            m.setTo("*.class");
            final SourceFileScanner sfs = new SourceFileScanner(this);
            final File[] newFiles = sfs.restrictAsFiles(files, srcDir, destDir, m);
            if (newFiles.length > 0) {
                this.lookForPackageInfos(srcDir, newFiles);
                final File[] newCompileList = new File[this.compileList.length + newFiles.length];
                System.arraycopy(this.compileList, 0, newCompileList, 0, this.compileList.length);
                System.arraycopy(newFiles, 0, newCompileList, this.compileList.length, newFiles.length);
                this.compileList = newCompileList;
            }
        }
    }
    
    private String[] findSupportedFileExtensions() {
        final String compilerImpl = this.getCompiler();
        final CompilerAdapter adapter = (this.nestedAdapter != null) ? this.nestedAdapter : CompilerAdapterFactory.getCompiler(compilerImpl, this, this.createCompilerClasspath());
        String[] extensions = null;
        if (adapter instanceof CompilerAdapterExtension) {
            extensions = ((CompilerAdapterExtension)adapter).getSupportedFileExtensions();
        }
        if (extensions == null) {
            extensions = new String[] { "java" };
        }
        for (int i = 0; i < extensions.length; ++i) {
            if (!extensions[i].startsWith("*.")) {
                extensions[i] = "*." + extensions[i];
            }
        }
        return extensions;
    }
    
    public File[] getFileList() {
        return this.compileList;
    }
    
    protected boolean isJdkCompiler(final String compilerImpl) {
        return "modern".equals(compilerImpl) || "classic".equals(compilerImpl) || "javac1.8".equals(compilerImpl) || "javac1.7".equals(compilerImpl) || "javac1.6".equals(compilerImpl) || "javac1.5".equals(compilerImpl) || "javac1.4".equals(compilerImpl) || "javac1.3".equals(compilerImpl) || "javac1.2".equals(compilerImpl) || "javac1.1".equals(compilerImpl);
    }
    
    protected String getSystemJavac() {
        return JavaEnvUtils.getJdkExecutable("javac");
    }
    
    public void setCompiler(final String compiler) {
        this.facade.setImplementation(compiler);
    }
    
    public String getCompiler() {
        String compilerImpl = this.getCompilerVersion();
        if (this.fork) {
            if (this.isJdkCompiler(compilerImpl)) {
                compilerImpl = "extJavac";
            }
            else {
                this.log("Since compiler setting isn't classic or modern, ignoring fork setting.", 1);
            }
        }
        return compilerImpl;
    }
    
    public String getCompilerVersion() {
        this.facade.setMagicValue(this.getProject().getProperty("build.compiler"));
        return this.facade.getImplementation();
    }
    
    protected void checkParameters() throws BuildException {
        if (this.src == null) {
            throw new BuildException("srcdir attribute must be set!", this.getLocation());
        }
        if (this.src.size() == 0) {
            throw new BuildException("srcdir attribute must be set!", this.getLocation());
        }
        if (this.destDir != null && !this.destDir.isDirectory()) {
            throw new BuildException("destination directory \"" + this.destDir + "\" does not exist " + "or is not a directory", this.getLocation());
        }
        if (this.includeAntRuntime == null && this.getProject().getProperty("build.sysclasspath") == null) {
            this.log(this.getLocation() + "warning: 'includeantruntime' was not set, " + "defaulting to build.sysclasspath=last; set to false for repeatable builds", 1);
        }
    }
    
    protected void compile() {
        final String compilerImpl = this.getCompiler();
        if (this.compileList.length > 0) {
            this.log("Compiling " + this.compileList.length + " source file" + ((this.compileList.length == 1) ? "" : "s") + ((this.destDir != null) ? (" to " + this.destDir) : ""));
            if (this.listFiles) {
                for (int i = 0; i < this.compileList.length; ++i) {
                    final String filename = this.compileList[i].getAbsolutePath();
                    this.log(filename);
                }
            }
            final CompilerAdapter adapter = (this.nestedAdapter != null) ? this.nestedAdapter : CompilerAdapterFactory.getCompiler(compilerImpl, this, this.createCompilerClasspath());
            adapter.setJavac(this);
            if (adapter.execute()) {
                if (!this.createMissingPackageInfoClass) {
                    return;
                }
                try {
                    this.generateMissingPackageInfoClasses((this.destDir != null) ? this.destDir : this.getProject().resolveFile(this.src.list()[0]));
                    return;
                }
                catch (IOException x) {
                    throw new BuildException(x, this.getLocation());
                }
            }
            this.taskSuccess = false;
            if (this.errorProperty != null) {
                this.getProject().setNewProperty(this.errorProperty, "true");
            }
            if (this.failOnError) {
                throw new BuildException("Compile failed; see the compiler error output for details.", this.getLocation());
            }
            this.log("Compile failed; see the compiler error output for details.", 0);
        }
    }
    
    private void lookForPackageInfos(final File srcDir, final File[] newFiles) {
        for (int i = 0; i < newFiles.length; ++i) {
            final File f = newFiles[i];
            if (f.getName().equals("package-info.java")) {
                final String path = Javac.FILE_UTILS.removeLeadingPath(srcDir, f).replace(File.separatorChar, '/');
                final String suffix = "/package-info.java";
                if (!path.endsWith(suffix)) {
                    this.log("anomalous package-info.java path: " + path, 1);
                }
                else {
                    final String pkg = path.substring(0, path.length() - suffix.length());
                    this.packageInfos.put(pkg, new Long(f.lastModified()));
                }
            }
        }
    }
    
    private void generateMissingPackageInfoClasses(final File dest) throws IOException {
        for (final Map.Entry<String, Long> entry : this.packageInfos.entrySet()) {
            final String pkg = entry.getKey();
            final Long sourceLastMod = entry.getValue();
            final File pkgBinDir = new File(dest, pkg.replace('/', File.separatorChar));
            pkgBinDir.mkdirs();
            final File pkgInfoClass = new File(pkgBinDir, "package-info.class");
            if (pkgInfoClass.isFile() && pkgInfoClass.lastModified() >= sourceLastMod) {
                continue;
            }
            this.log("Creating empty " + pkgInfoClass);
            final OutputStream os = new FileOutputStream(pkgInfoClass);
            try {
                os.write(Javac.PACKAGE_INFO_CLASS_HEADER);
                final byte[] name = pkg.getBytes("UTF-8");
                final int length = name.length + 13;
                os.write((byte)length / 256);
                os.write((byte)length % 256);
                os.write(name);
                os.write(Javac.PACKAGE_INFO_CLASS_FOOTER);
            }
            finally {
                os.close();
            }
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
        PACKAGE_INFO_CLASS_HEADER = new byte[] { -54, -2, -70, -66, 0, 0, 0, 49, 0, 7, 7, 0, 5, 7, 0, 6, 1, 0, 10, 83, 111, 117, 114, 99, 101, 70, 105, 108, 101, 1, 0, 17, 112, 97, 99, 107, 97, 103, 101, 45, 105, 110, 102, 111, 46, 106, 97, 118, 97, 1 };
        PACKAGE_INFO_CLASS_FOOTER = new byte[] { 47, 112, 97, 99, 107, 97, 103, 101, 45, 105, 110, 102, 111, 1, 0, 16, 106, 97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 2, 0, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 2, 0, 4 };
    }
    
    public class ImplementationSpecificArgument extends org.apache.tools.ant.util.facade.ImplementationSpecificArgument
    {
        public void setCompiler(final String impl) {
            super.setImplementation(impl);
        }
    }
}
