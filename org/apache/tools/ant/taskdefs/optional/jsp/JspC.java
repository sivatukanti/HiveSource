// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.jsp;

import java.util.Enumeration;
import java.util.Date;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapter;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.jsp.compilers.JspCompilerAdapterFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class JspC extends MatchingTask
{
    private Path classpath;
    private Path compilerClasspath;
    private Path src;
    private File destDir;
    private String packageName;
    private String compilerName;
    private String iepluginid;
    private boolean mapped;
    private int verbose;
    protected Vector compileList;
    Vector javaFiles;
    protected boolean failOnError;
    private File uriroot;
    private File webinc;
    private File webxml;
    protected WebAppParameter webApp;
    private static final String FAIL_MSG = "Compile failed, messages should have been provided.";
    
    public JspC() {
        this.compilerName = "jasper";
        this.verbose = 0;
        this.compileList = new Vector();
        this.javaFiles = new Vector();
        this.failOnError = true;
    }
    
    public void setSrcDir(final Path srcDir) {
        if (this.src == null) {
            this.src = srcDir;
        }
        else {
            this.src.append(srcDir);
        }
    }
    
    public Path getSrcDir() {
        return this.src;
    }
    
    public void setDestdir(final File destDir) {
        this.destDir = destDir;
    }
    
    public File getDestdir() {
        return this.destDir;
    }
    
    public void setPackage(final String pkg) {
        this.packageName = pkg;
    }
    
    public String getPackage() {
        return this.packageName;
    }
    
    public void setVerbose(final int i) {
        this.verbose = i;
    }
    
    public int getVerbose() {
        return this.verbose;
    }
    
    public void setFailonerror(final boolean fail) {
        this.failOnError = fail;
    }
    
    public boolean getFailonerror() {
        return this.failOnError;
    }
    
    public String getIeplugin() {
        return this.iepluginid;
    }
    
    public void setIeplugin(final String iepluginid) {
        this.iepluginid = iepluginid;
    }
    
    public boolean isMapped() {
        return this.mapped;
    }
    
    public void setMapped(final boolean mapped) {
        this.mapped = mapped;
    }
    
    public void setUribase(final File uribase) {
        this.log("Uribase is currently an unused parameter", 1);
    }
    
    public File getUribase() {
        return this.uriroot;
    }
    
    public void setUriroot(final File uriroot) {
        this.uriroot = uriroot;
    }
    
    public File getUriroot() {
        return this.uriroot;
    }
    
    public void setClasspath(final Path cp) {
        if (this.classpath == null) {
            this.classpath = cp;
        }
        else {
            this.classpath.append(cp);
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference r) {
        this.createClasspath().setRefid(r);
    }
    
    public Path getClasspath() {
        return this.classpath;
    }
    
    public void setCompilerclasspath(final Path cp) {
        if (this.compilerClasspath == null) {
            this.compilerClasspath = cp;
        }
        else {
            this.compilerClasspath.append(cp);
        }
    }
    
    public Path getCompilerclasspath() {
        return this.compilerClasspath;
    }
    
    public Path createCompilerclasspath() {
        if (this.compilerClasspath == null) {
            this.compilerClasspath = new Path(this.getProject());
        }
        return this.compilerClasspath.createPath();
    }
    
    public void setWebxml(final File webxml) {
        this.webxml = webxml;
    }
    
    public File getWebxml() {
        return this.webxml;
    }
    
    public void setWebinc(final File webinc) {
        this.webinc = webinc;
    }
    
    public File getWebinc() {
        return this.webinc;
    }
    
    public void addWebApp(final WebAppParameter webappParam) throws BuildException {
        if (this.webApp == null) {
            this.webApp = webappParam;
            return;
        }
        throw new BuildException("Only one webapp can be specified");
    }
    
    public WebAppParameter getWebApp() {
        return this.webApp;
    }
    
    public void setCompiler(final String compiler) {
        this.compilerName = compiler;
    }
    
    public Vector getCompileList() {
        return this.compileList;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.destDir == null) {
            throw new BuildException("destdir attribute must be set!", this.getLocation());
        }
        if (!this.destDir.isDirectory()) {
            throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", this.getLocation());
        }
        final File dest = this.getActualDestDir();
        AntClassLoader al = null;
        try {
            final JspCompilerAdapter compiler = JspCompilerAdapterFactory.getCompiler(this.compilerName, this, al = this.getProject().createClassLoader(this.compilerClasspath));
            if (this.webApp != null) {
                this.doCompilation(compiler);
                return;
            }
            if (this.src == null) {
                throw new BuildException("srcdir attribute must be set!", this.getLocation());
            }
            final String[] list = this.src.list();
            if (list.length == 0) {
                throw new BuildException("srcdir attribute must be set!", this.getLocation());
            }
            if (compiler.implementsOwnDependencyChecking()) {
                this.doCompilation(compiler);
                return;
            }
            final JspMangler mangler = compiler.createMangler();
            this.resetFileLists();
            int filecount = 0;
            for (int i = 0; i < list.length; ++i) {
                final File srcDir = this.getProject().resolveFile(list[i]);
                if (!srcDir.exists()) {
                    throw new BuildException("srcdir \"" + srcDir.getPath() + "\" does not exist!", this.getLocation());
                }
                final DirectoryScanner ds = this.getDirectoryScanner(srcDir);
                final String[] files = ds.getIncludedFiles();
                filecount = files.length;
                this.scanDir(srcDir, dest, mangler, files);
            }
            this.log("compiling " + this.compileList.size() + " files", 3);
            if (this.compileList.size() > 0) {
                this.log("Compiling " + this.compileList.size() + " source file" + ((this.compileList.size() == 1) ? "" : "s") + " to " + dest);
                this.doCompilation(compiler);
            }
            else if (filecount == 0) {
                this.log("there were no files to compile", 2);
            }
            else {
                this.log("all files are up to date", 3);
            }
        }
        finally {
            if (al != null) {
                al.cleanup();
            }
        }
    }
    
    private File getActualDestDir() {
        File dest = null;
        if (this.packageName == null) {
            dest = this.destDir;
        }
        else {
            final String path = this.destDir.getPath() + File.separatorChar + this.packageName.replace('.', File.separatorChar);
            dest = new File(path);
        }
        return dest;
    }
    
    private void doCompilation(final JspCompilerAdapter compiler) throws BuildException {
        compiler.setJspc(this);
        if (!compiler.execute()) {
            if (this.failOnError) {
                throw new BuildException("Compile failed, messages should have been provided.", this.getLocation());
            }
            this.log("Compile failed, messages should have been provided.", 0);
        }
    }
    
    protected void resetFileLists() {
        this.compileList.removeAllElements();
    }
    
    protected void scanDir(final File srcDir, final File dest, final JspMangler mangler, final String[] files) {
        final long now = new Date().getTime();
        for (int i = 0; i < files.length; ++i) {
            final String filename = files[i];
            final File srcFile = new File(srcDir, filename);
            final File javaFile = this.mapToJavaFile(mangler, srcFile, srcDir, dest);
            if (javaFile != null) {
                if (srcFile.lastModified() > now) {
                    this.log("Warning: file modified in the future: " + filename, 1);
                }
                boolean shouldCompile = false;
                shouldCompile = this.isCompileNeeded(srcFile, javaFile);
                if (shouldCompile) {
                    this.compileList.addElement(srcFile.getAbsolutePath());
                    this.javaFiles.addElement(javaFile);
                }
            }
        }
    }
    
    private boolean isCompileNeeded(final File srcFile, final File javaFile) {
        boolean shouldCompile = false;
        if (!javaFile.exists()) {
            shouldCompile = true;
            this.log("Compiling " + srcFile.getPath() + " because java file " + javaFile.getPath() + " does not exist", 3);
        }
        else if (srcFile.lastModified() > javaFile.lastModified()) {
            shouldCompile = true;
            this.log("Compiling " + srcFile.getPath() + " because it is out of date with respect to " + javaFile.getPath(), 3);
        }
        else if (javaFile.length() == 0L) {
            shouldCompile = true;
            this.log("Compiling " + srcFile.getPath() + " because java file " + javaFile.getPath() + " is empty", 3);
        }
        return shouldCompile;
    }
    
    protected File mapToJavaFile(final JspMangler mangler, final File srcFile, final File srcDir, final File dest) {
        if (!srcFile.getName().endsWith(".jsp")) {
            return null;
        }
        final String javaFileName = mangler.mapJspToJavaName(srcFile);
        return new File(dest, javaFileName);
    }
    
    public void deleteEmptyJavaFiles() {
        if (this.javaFiles != null) {
            final Enumeration e = this.javaFiles.elements();
            while (e.hasMoreElements()) {
                final File file = e.nextElement();
                if (file.exists() && file.length() == 0L) {
                    this.log("deleting empty output file " + file);
                    file.delete();
                }
            }
        }
    }
    
    public static class WebAppParameter
    {
        private File directory;
        
        public File getDirectory() {
            return this.directory;
        }
        
        public void setBaseDir(final File directory) {
            this.directory = directory;
        }
    }
}
