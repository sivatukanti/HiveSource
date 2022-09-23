// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.types.FileSet;

public class BCFileSet extends FileSet
{
    public BCFileSet() {
    }
    
    public BCFileSet(final FileSet fs) {
        super(fs);
    }
    
    @Override
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((FileSet)this.getRef(this.getProject())).iterator();
        }
        final FileResourceIterator result = new FileResourceIterator(this.getProject(), this.getDir());
        result.addFiles(this.getDirectoryScanner().getIncludedFiles());
        result.addFiles(this.getDirectoryScanner().getIncludedDirectories());
        return result;
    }
    
    @Override
    public int size() {
        if (this.isReference()) {
            return ((FileSet)this.getRef(this.getProject())).size();
        }
        return this.getDirectoryScanner().getIncludedFilesCount() + this.getDirectoryScanner().getIncludedDirsCount();
    }
}
