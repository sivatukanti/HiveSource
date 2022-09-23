// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.ResourceCollection;
import java.io.File;
import org.apache.tools.ant.types.Resource;

public abstract class ArchiveResource extends Resource
{
    private static final int NULL_ARCHIVE;
    private Resource archive;
    private boolean haveEntry;
    private boolean modeSet;
    private int mode;
    
    protected ArchiveResource() {
        this.haveEntry = false;
        this.modeSet = false;
        this.mode = 0;
    }
    
    protected ArchiveResource(final File a) {
        this(a, false);
    }
    
    protected ArchiveResource(final File a, final boolean withEntry) {
        this.haveEntry = false;
        this.modeSet = false;
        this.mode = 0;
        this.setArchive(a);
        this.haveEntry = withEntry;
    }
    
    protected ArchiveResource(final Resource a, final boolean withEntry) {
        this.haveEntry = false;
        this.modeSet = false;
        this.mode = 0;
        this.addConfigured(a);
        this.haveEntry = withEntry;
    }
    
    public void setArchive(final File a) {
        this.checkAttributesAllowed();
        this.archive = new FileResource(a);
    }
    
    public void setMode(final int mode) {
        this.checkAttributesAllowed();
        this.mode = mode;
        this.modeSet = true;
    }
    
    public void addConfigured(final ResourceCollection a) {
        this.checkChildrenAllowed();
        if (this.archive != null) {
            throw new BuildException("you must not specify more than one archive");
        }
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported as archives");
        }
        this.archive = a.iterator().next();
    }
    
    public Resource getArchive() {
        return this.isReference() ? ((ArchiveResource)this.getCheckedRef()).getArchive() : this.archive;
    }
    
    @Override
    public long getLastModified() {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getLastModified();
        }
        this.checkEntry();
        return super.getLastModified();
    }
    
    @Override
    public long getSize() {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).getSize();
        }
        this.checkEntry();
        return super.getSize();
    }
    
    @Override
    public boolean isDirectory() {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).isDirectory();
        }
        this.checkEntry();
        return super.isDirectory();
    }
    
    @Override
    public boolean isExists() {
        if (this.isReference()) {
            return ((Resource)this.getCheckedRef()).isExists();
        }
        this.checkEntry();
        return super.isExists();
    }
    
    public int getMode() {
        if (this.isReference()) {
            return ((ArchiveResource)this.getCheckedRef()).getMode();
        }
        this.checkEntry();
        return this.mode;
    }
    
    @Override
    public void setRefid(final Reference r) {
        if (this.archive != null || this.modeSet) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    @Override
    public int compareTo(final Resource another) {
        return this.equals(another) ? 0 : super.compareTo(another);
    }
    
    @Override
    public boolean equals(final Object another) {
        if (this == another) {
            return true;
        }
        if (this.isReference()) {
            return this.getCheckedRef().equals(another);
        }
        if (!another.getClass().equals(this.getClass())) {
            return false;
        }
        final ArchiveResource r = (ArchiveResource)another;
        return this.getArchive().equals(r.getArchive()) && this.getName().equals(r.getName());
    }
    
    @Override
    public int hashCode() {
        return super.hashCode() * ((this.getArchive() == null) ? ArchiveResource.NULL_ARCHIVE : this.getArchive().hashCode());
    }
    
    @Override
    public String toString() {
        return this.isReference() ? this.getCheckedRef().toString() : (this.getArchive().toString() + ':' + this.getName());
    }
    
    protected final synchronized void checkEntry() throws BuildException {
        this.dieOnCircularReference();
        if (this.haveEntry) {
            return;
        }
        final String name = this.getName();
        if (name == null) {
            throw new BuildException("entry name not set");
        }
        final Resource r = this.getArchive();
        if (r == null) {
            throw new BuildException("archive attribute not set");
        }
        if (!r.isExists()) {
            throw new BuildException(r.toString() + " does not exist.");
        }
        if (r.isDirectory()) {
            throw new BuildException(r + " denotes a directory.");
        }
        this.fetchEntry();
        this.haveEntry = true;
    }
    
    protected abstract void fetchEntry();
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            if (this.archive != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.archive, stk, p);
            }
            this.setChecked(true);
        }
    }
    
    static {
        NULL_ARCHIVE = Resource.getMagicNumber("null archive".getBytes());
    }
}
