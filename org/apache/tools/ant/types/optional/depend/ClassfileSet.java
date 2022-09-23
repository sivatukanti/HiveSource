// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional.depend;

import org.apache.tools.ant.types.DataType;
import java.util.Stack;
import java.util.Iterator;
import org.apache.tools.ant.util.StringUtils;
import java.util.Vector;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.types.FileSet;

public class ClassfileSet extends FileSet
{
    private List<String> rootClasses;
    private List<FileSet> rootFileSets;
    
    public ClassfileSet() {
        this.rootClasses = new ArrayList<String>();
        this.rootFileSets = new ArrayList<FileSet>();
    }
    
    public void addRootFileset(final FileSet rootFileSet) {
        this.rootFileSets.add(rootFileSet);
        this.setChecked(false);
    }
    
    protected ClassfileSet(final ClassfileSet s) {
        super(s);
        this.rootClasses = new ArrayList<String>();
        this.rootFileSets = new ArrayList<FileSet>();
        this.rootClasses.addAll(s.rootClasses);
    }
    
    public void setRootClass(final String rootClass) {
        this.rootClasses.add(rootClass);
    }
    
    @Override
    public DirectoryScanner getDirectoryScanner(final Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDirectoryScanner(p);
        }
        this.dieOnCircularReference(p);
        final DirectoryScanner parentScanner = super.getDirectoryScanner(p);
        final DependScanner scanner = new DependScanner(parentScanner);
        final Vector<String> allRootClasses = new Vector<String>(this.rootClasses);
        for (final FileSet additionalRootSet : this.rootFileSets) {
            final DirectoryScanner additionalScanner = additionalRootSet.getDirectoryScanner(p);
            final String[] files = additionalScanner.getIncludedFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].endsWith(".class")) {
                    final String classFilePath = StringUtils.removeSuffix(files[i], ".class");
                    final String className = classFilePath.replace('/', '.').replace('\\', '.');
                    allRootClasses.addElement(className);
                }
            }
            scanner.addBasedir(additionalRootSet.getDir(p));
        }
        scanner.setBasedir(this.getDir(p));
        scanner.setRootClasses(allRootClasses);
        scanner.scan();
        return scanner;
    }
    
    public void addConfiguredRoot(final ClassRoot root) {
        this.rootClasses.add(root.getClassname());
    }
    
    @Override
    public Object clone() {
        return new ClassfileSet(this.isReference() ? ((ClassfileSet)this.getRef(this.getProject())) : this);
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            for (final FileSet additionalRootSet : this.rootFileSets) {
                DataType.pushAndInvokeCircularReferenceCheck(additionalRootSet, stk, p);
            }
            this.setChecked(true);
        }
    }
    
    public static class ClassRoot
    {
        private String rootClass;
        
        public void setClassname(final String name) {
            this.rootClass = name;
        }
        
        public String getClassname() {
            return this.rootClass;
        }
    }
}
