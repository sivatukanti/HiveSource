// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class ZipFileSet extends ArchiveFileSet
{
    private String encoding;
    
    public ZipFileSet() {
        this.encoding = null;
    }
    
    protected ZipFileSet(final FileSet fileset) {
        super(fileset);
        this.encoding = null;
    }
    
    protected ZipFileSet(final ZipFileSet fileset) {
        super(fileset);
        this.encoding = null;
        this.encoding = fileset.encoding;
    }
    
    public void setEncoding(final String enc) {
        this.checkZipFileSetAttributesAllowed();
        this.encoding = enc;
    }
    
    public String getEncoding() {
        if (!this.isReference()) {
            return this.encoding;
        }
        final AbstractFileSet ref = this.getRef(this.getProject());
        if (ref instanceof ZipFileSet) {
            return ((ZipFileSet)ref).getEncoding();
        }
        return null;
    }
    
    @Override
    protected ArchiveScanner newArchiveScanner() {
        final ZipScanner zs = new ZipScanner();
        zs.setEncoding(this.encoding);
        return zs;
    }
    
    @Override
    protected AbstractFileSet getRef(final Project p) {
        this.dieOnCircularReference(p);
        final Object o = this.getRefid().getReferencedObject(p);
        if (o instanceof ZipFileSet) {
            return (AbstractFileSet)o;
        }
        if (o instanceof FileSet) {
            final ZipFileSet zfs = new ZipFileSet((FileSet)o);
            this.configureFileSet(zfs);
            return zfs;
        }
        final String msg = this.getRefid().getRefId() + " doesn't denote a zipfileset or a fileset";
        throw new BuildException(msg);
    }
    
    @Override
    public Object clone() {
        if (this.isReference()) {
            return ((ZipFileSet)this.getRef(this.getProject())).clone();
        }
        return super.clone();
    }
    
    private void checkZipFileSetAttributesAllowed() {
        if (this.getProject() == null || (this.isReference() && this.getRefid().getReferencedObject(this.getProject()) instanceof ZipFileSet)) {
            this.checkAttributesAllowed();
        }
    }
}
