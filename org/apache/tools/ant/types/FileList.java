// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.types.resources.FileResourceIterator;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

public class FileList extends DataType implements ResourceCollection
{
    private List<String> filenames;
    private File dir;
    
    public FileList() {
        this.filenames = new ArrayList<String>();
    }
    
    protected FileList(final FileList filelist) {
        this.filenames = new ArrayList<String>();
        this.dir = filelist.dir;
        this.filenames = filelist.filenames;
        this.setProject(filelist.getProject());
    }
    
    @Override
    public void setRefid(final Reference r) throws BuildException {
        if (this.dir != null || this.filenames.size() != 0) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    public void setDir(final File dir) throws BuildException {
        this.checkAttributesAllowed();
        this.dir = dir;
    }
    
    public File getDir(final Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDir(p);
        }
        return this.dir;
    }
    
    public void setFiles(final String filenames) {
        this.checkAttributesAllowed();
        if (filenames != null && filenames.length() > 0) {
            final StringTokenizer tok = new StringTokenizer(filenames, ", \t\n\r\f", false);
            while (tok.hasMoreTokens()) {
                this.filenames.add(tok.nextToken());
            }
        }
    }
    
    public String[] getFiles(final Project p) {
        if (this.isReference()) {
            return this.getRef(p).getFiles(p);
        }
        if (this.dir == null) {
            throw new BuildException("No directory specified for filelist.");
        }
        if (this.filenames.size() == 0) {
            throw new BuildException("No files specified for filelist.");
        }
        return this.filenames.toArray(new String[this.filenames.size()]);
    }
    
    protected FileList getRef(final Project p) {
        return (FileList)this.getCheckedRef(p);
    }
    
    public void addConfiguredFile(final FileName name) {
        if (name.getName() == null) {
            throw new BuildException("No name specified in nested file element");
        }
        this.filenames.add(name.getName());
    }
    
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).iterator();
        }
        return new FileResourceIterator(this.getProject(), this.dir, this.filenames.toArray(new String[this.filenames.size()]));
    }
    
    public int size() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).size();
        }
        return this.filenames.size();
    }
    
    public boolean isFilesystemOnly() {
        return true;
    }
    
    public static class FileName
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
