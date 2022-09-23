// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.types.resources.FileResourceIterator;
import java.util.Iterator;

public class FileSet extends AbstractFileSet implements ResourceCollection
{
    public FileSet() {
    }
    
    protected FileSet(final FileSet fileset) {
        super(fileset);
    }
    
    @Override
    public Object clone() {
        if (this.isReference()) {
            return ((FileSet)this.getRef(this.getProject())).clone();
        }
        return super.clone();
    }
    
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((FileSet)this.getRef(this.getProject())).iterator();
        }
        return new FileResourceIterator(this.getProject(), this.getDir(this.getProject()), this.getDirectoryScanner(this.getProject()).getIncludedFiles());
    }
    
    public int size() {
        if (this.isReference()) {
            return ((FileSet)this.getRef(this.getProject())).size();
        }
        return this.getDirectoryScanner(this.getProject()).getIncludedFilesCount();
    }
    
    public boolean isFilesystemOnly() {
        return true;
    }
}
