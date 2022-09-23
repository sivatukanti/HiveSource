// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional.depend;

import java.util.Enumeration;
import java.util.Hashtable;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.DirectoryScanner;

public class DependScanner extends DirectoryScanner
{
    public static final String DEFAULT_ANALYZER_CLASS = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
    private Vector<String> rootClasses;
    private Vector<String> included;
    private Vector<File> additionalBaseDirs;
    private DirectoryScanner parentScanner;
    
    public DependScanner(final DirectoryScanner parentScanner) {
        this.additionalBaseDirs = new Vector<File>();
        this.parentScanner = parentScanner;
    }
    
    public synchronized void setRootClasses(final Vector<String> rootClasses) {
        this.rootClasses = rootClasses;
    }
    
    @Override
    public String[] getIncludedFiles() {
        final String[] files = new String[this.getIncludedFilesCount()];
        for (int i = 0; i < files.length; ++i) {
            files[i] = this.included.elementAt(i);
        }
        return files;
    }
    
    @Override
    public synchronized int getIncludedFilesCount() {
        if (this.included == null) {
            throw new IllegalStateException();
        }
        return this.included.size();
    }
    
    @Override
    public synchronized void scan() throws IllegalStateException {
        this.included = new Vector<String>();
        final String analyzerClassName = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
        DependencyAnalyzer analyzer = null;
        try {
            final Class<? extends DependencyAnalyzer> analyzerClass = Class.forName(analyzerClassName).asSubclass(DependencyAnalyzer.class);
            analyzer = (DependencyAnalyzer)analyzerClass.newInstance();
        }
        catch (Exception e) {
            throw new BuildException("Unable to load dependency analyzer: " + analyzerClassName, e);
        }
        analyzer.addClassPath(new Path(null, this.basedir.getPath()));
        final Enumeration<File> e2 = this.additionalBaseDirs.elements();
        while (e2.hasMoreElements()) {
            final File additionalBaseDir = e2.nextElement();
            analyzer.addClassPath(new Path(null, additionalBaseDir.getPath()));
        }
        Enumeration<String> e3 = this.rootClasses.elements();
        while (e3.hasMoreElements()) {
            final String rootClass = e3.nextElement();
            analyzer.addRootClass(rootClass);
        }
        e3 = analyzer.getClassDependencies();
        final String[] parentFiles = this.parentScanner.getIncludedFiles();
        final Hashtable<String, String> parentSet = new Hashtable<String, String>();
        for (int i = 0; i < parentFiles.length; ++i) {
            parentSet.put(parentFiles[i], parentFiles[i]);
        }
        while (e3.hasMoreElements()) {
            final String classname = e3.nextElement();
            String filename = classname.replace('.', File.separatorChar);
            filename += ".class";
            final File depFile = new File(this.basedir, filename);
            if (depFile.exists() && parentSet.containsKey(filename)) {
                this.included.addElement(filename);
            }
        }
    }
    
    @Override
    public void addDefaultExcludes() {
    }
    
    @Override
    public String[] getExcludedDirectories() {
        return null;
    }
    
    @Override
    public String[] getExcludedFiles() {
        return null;
    }
    
    @Override
    public String[] getIncludedDirectories() {
        return new String[0];
    }
    
    @Override
    public int getIncludedDirsCount() {
        return 0;
    }
    
    @Override
    public String[] getNotIncludedDirectories() {
        return null;
    }
    
    @Override
    public String[] getNotIncludedFiles() {
        return null;
    }
    
    @Override
    public void setExcludes(final String[] excludes) {
    }
    
    @Override
    public void setIncludes(final String[] includes) {
    }
    
    @Override
    public void setCaseSensitive(final boolean isCaseSensitive) {
    }
    
    public void addBasedir(final File baseDir) {
        this.additionalBaseDirs.addElement(baseDir);
    }
}
