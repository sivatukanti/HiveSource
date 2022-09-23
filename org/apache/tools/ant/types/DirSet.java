// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import java.util.Iterator;

public class DirSet extends AbstractFileSet implements ResourceCollection
{
    public DirSet() {
    }
    
    protected DirSet(final DirSet dirset) {
        super(dirset);
    }
    
    @Override
    public Object clone() {
        if (this.isReference()) {
            return ((DirSet)this.getRef(this.getProject())).clone();
        }
        return super.clone();
    }
    
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((DirSet)this.getRef(this.getProject())).iterator();
        }
        return new FileResourceIterator(this.getProject(), this.getDir(this.getProject()), this.getDirectoryScanner(this.getProject()).getIncludedDirectories());
    }
    
    public int size() {
        if (this.isReference()) {
            return ((DirSet)this.getRef(this.getProject())).size();
        }
        return this.getDirectoryScanner(this.getProject()).getIncludedDirsCount();
    }
    
    public boolean isFilesystemOnly() {
        return true;
    }
    
    @Override
    public String toString() {
        final DirectoryScanner ds = this.getDirectoryScanner(this.getProject());
        final String[] dirs = ds.getIncludedDirectories();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < dirs.length; ++i) {
            if (i > 0) {
                sb.append(';');
            }
            sb.append(dirs[i]);
        }
        return sb.toString();
    }
}
