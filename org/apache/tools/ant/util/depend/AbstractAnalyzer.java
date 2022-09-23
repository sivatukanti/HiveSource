// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.depend;

import java.util.zip.ZipFile;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.ant.util.VectorSet;
import org.apache.tools.ant.Project;
import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.types.Path;

public abstract class AbstractAnalyzer implements DependencyAnalyzer
{
    public static final int MAX_LOOPS = 1000;
    private Path sourcePath;
    private Path classPath;
    private final Vector<String> rootClasses;
    private boolean determined;
    private Vector<File> fileDependencies;
    private Vector<String> classDependencies;
    private boolean closure;
    
    protected AbstractAnalyzer() {
        this.sourcePath = new Path(null);
        this.classPath = new Path(null);
        this.rootClasses = new VectorSet<String>();
        this.determined = false;
        this.closure = true;
        this.reset();
    }
    
    public void setClosure(final boolean closure) {
        this.closure = closure;
    }
    
    public Enumeration<File> getFileDependencies() {
        if (!this.supportsFileDependencies()) {
            throw new RuntimeException("File dependencies are not supported by this analyzer");
        }
        if (!this.determined) {
            this.determineDependencies(this.fileDependencies, this.classDependencies);
        }
        return this.fileDependencies.elements();
    }
    
    public Enumeration<String> getClassDependencies() {
        if (!this.determined) {
            this.determineDependencies(this.fileDependencies, this.classDependencies);
        }
        return this.classDependencies.elements();
    }
    
    public File getClassContainer(final String classname) throws IOException {
        final String classLocation = classname.replace('.', '/') + ".class";
        return this.getResourceContainer(classLocation, this.classPath.list());
    }
    
    public File getSourceContainer(final String classname) throws IOException {
        final String sourceLocation = classname.replace('.', '/') + ".java";
        return this.getResourceContainer(sourceLocation, this.sourcePath.list());
    }
    
    public void addSourcePath(final Path sourcePath) {
        if (sourcePath == null) {
            return;
        }
        this.sourcePath.append(sourcePath);
        this.sourcePath.setProject(sourcePath.getProject());
    }
    
    public void addClassPath(final Path classPath) {
        if (classPath == null) {
            return;
        }
        this.classPath.append(classPath);
        this.classPath.setProject(classPath.getProject());
    }
    
    public void addRootClass(final String className) {
        if (className == null) {
            return;
        }
        if (!this.rootClasses.contains(className)) {
            this.rootClasses.addElement(className);
        }
    }
    
    public void config(final String name, final Object info) {
    }
    
    public void reset() {
        this.rootClasses.removeAllElements();
        this.determined = false;
        this.fileDependencies = new Vector<File>();
        this.classDependencies = new Vector<String>();
    }
    
    protected Enumeration<String> getRootClasses() {
        return this.rootClasses.elements();
    }
    
    protected boolean isClosureRequired() {
        return this.closure;
    }
    
    protected abstract void determineDependencies(final Vector<File> p0, final Vector<String> p1);
    
    protected abstract boolean supportsFileDependencies();
    
    private File getResourceContainer(final String resourceLocation, final String[] paths) throws IOException {
        for (int i = 0; i < paths.length; ++i) {
            final File element = new File(paths[i]);
            if (element.exists()) {
                if (element.isDirectory()) {
                    final File resource = new File(element, resourceLocation);
                    if (resource.exists()) {
                        return resource;
                    }
                }
                else {
                    ZipFile zipFile = null;
                    try {
                        zipFile = new ZipFile(element);
                        if (zipFile.getEntry(resourceLocation) != null) {
                            return element;
                        }
                    }
                    finally {
                        if (zipFile != null) {
                            zipFile.close();
                        }
                    }
                }
            }
        }
        return null;
    }
}
