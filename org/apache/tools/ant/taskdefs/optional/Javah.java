// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.facade.ImplementationSpecificArgument;
import org.apache.tools.ant.types.Reference;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapterFactory;
import org.apache.tools.ant.taskdefs.optional.javah.JavahAdapter;
import org.apache.tools.ant.util.facade.FacadeTaskHelper;
import org.apache.tools.ant.types.Path;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.Task;

public class Javah extends Task
{
    private Vector classes;
    private String cls;
    private File destDir;
    private Path classpath;
    private File outputFile;
    private boolean verbose;
    private boolean force;
    private boolean old;
    private boolean stubs;
    private Path bootclasspath;
    private FacadeTaskHelper facade;
    private Vector files;
    private JavahAdapter nestedAdapter;
    
    public Javah() {
        this.classes = new Vector(2);
        this.classpath = null;
        this.outputFile = null;
        this.verbose = false;
        this.force = false;
        this.old = false;
        this.stubs = false;
        this.facade = null;
        this.files = new Vector();
        this.nestedAdapter = null;
        this.facade = new FacadeTaskHelper(JavahAdapterFactory.getDefault());
    }
    
    public void setClass(final String cls) {
        this.cls = cls;
    }
    
    public ClassArgument createClass() {
        final ClassArgument ga = new ClassArgument();
        this.classes.addElement(ga);
        return ga;
    }
    
    public void addFileSet(final FileSet fs) {
        this.files.add(fs);
    }
    
    public String[] getClasses() {
        final ArrayList al = new ArrayList();
        if (this.cls != null) {
            final StringTokenizer tok = new StringTokenizer(this.cls, ",", false);
            while (tok.hasMoreTokens()) {
                al.add(tok.nextToken().trim());
            }
        }
        if (this.files.size() > 0) {
            final Enumeration e = this.files.elements();
            while (e.hasMoreElements()) {
                final FileSet fs = e.nextElement();
                final String[] includedClasses = fs.getDirectoryScanner(this.getProject()).getIncludedFiles();
                for (int i = 0; i < includedClasses.length; ++i) {
                    final String className = includedClasses[i].replace('\\', '.').replace('/', '.').substring(0, includedClasses[i].length() - 6);
                    al.add(className);
                }
            }
        }
        final Enumeration e = this.classes.elements();
        while (e.hasMoreElements()) {
            final ClassArgument arg = e.nextElement();
            al.add(arg.getName());
        }
        return al.toArray(new String[al.size()]);
    }
    
    public void setDestdir(final File destDir) {
        this.destDir = destDir;
    }
    
    public File getDestdir() {
        return this.destDir;
    }
    
    public void setClasspath(final Path src) {
        if (this.classpath == null) {
            this.classpath = src;
        }
        else {
            this.classpath.append(src);
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
    
    public void setBootclasspath(final Path src) {
        if (this.bootclasspath == null) {
            this.bootclasspath = src;
        }
        else {
            this.bootclasspath.append(src);
        }
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
    
    public Path getBootclasspath() {
        return this.bootclasspath;
    }
    
    public void setOutputFile(final File outputFile) {
        this.outputFile = outputFile;
    }
    
    public File getOutputfile() {
        return this.outputFile;
    }
    
    public void setForce(final boolean force) {
        this.force = force;
    }
    
    public boolean getForce() {
        return this.force;
    }
    
    public void setOld(final boolean old) {
        this.old = old;
    }
    
    public boolean getOld() {
        return this.old;
    }
    
    public void setStubs(final boolean stubs) {
        this.stubs = stubs;
    }
    
    public boolean getStubs() {
        return this.stubs;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public boolean getVerbose() {
        return this.verbose;
    }
    
    public void setImplementation(final String impl) {
        if ("default".equals(impl)) {
            this.facade.setImplementation(JavahAdapterFactory.getDefault());
        }
        else {
            this.facade.setImplementation(impl);
        }
    }
    
    public ImplementationSpecificArgument createArg() {
        final ImplementationSpecificArgument arg = new ImplementationSpecificArgument();
        this.facade.addImplementationArgument(arg);
        return arg;
    }
    
    public String[] getCurrentArgs() {
        return this.facade.getArgs();
    }
    
    public Path createImplementationClasspath() {
        return this.facade.getImplementationClasspath(this.getProject());
    }
    
    public void add(final JavahAdapter adapter) {
        if (this.nestedAdapter != null) {
            throw new BuildException("Can't have more than one javah adapter");
        }
        this.nestedAdapter = adapter;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.cls == null && this.classes.size() == 0 && this.files.size() == 0) {
            throw new BuildException("class attribute must be set!", this.getLocation());
        }
        if (this.cls != null && this.classes.size() > 0 && this.files.size() > 0) {
            throw new BuildException("set class attribute OR class element OR fileset, not 2 or more of them.", this.getLocation());
        }
        if (this.destDir != null) {
            if (!this.destDir.isDirectory()) {
                throw new BuildException("destination directory \"" + this.destDir + "\" does not exist or is not a directory", this.getLocation());
            }
            if (this.outputFile != null) {
                throw new BuildException("destdir and outputFile are mutually exclusive", this.getLocation());
            }
        }
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject()).concatSystemClasspath("last");
        }
        else {
            this.classpath = this.classpath.concatSystemClasspath("ignore");
        }
        final JavahAdapter ad = (this.nestedAdapter != null) ? this.nestedAdapter : JavahAdapterFactory.getAdapter(this.facade.getImplementation(), this, this.createImplementationClasspath());
        if (!ad.compile(this)) {
            throw new BuildException("compilation failed");
        }
    }
    
    public void logAndAddFiles(final Commandline cmd) {
        this.logAndAddFilesToCompile(cmd);
    }
    
    protected void logAndAddFilesToCompile(final Commandline cmd) {
        this.log("Compilation " + cmd.describeArguments(), 3);
        final StringBuffer niceClassList = new StringBuffer();
        final String[] c = this.getClasses();
        for (int i = 0; i < c.length; ++i) {
            cmd.createArgument().setValue(c[i]);
            niceClassList.append("    ");
            niceClassList.append(c[i]);
            niceClassList.append(StringUtils.LINE_SEP);
        }
        final StringBuffer prefix = new StringBuffer("Class");
        if (c.length > 1) {
            prefix.append("es");
        }
        prefix.append(" to be compiled:");
        prefix.append(StringUtils.LINE_SEP);
        this.log(prefix.toString() + niceClassList.toString(), 3);
    }
    
    public class ClassArgument
    {
        private String name;
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
}
