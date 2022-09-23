// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.TarFileSet;
import org.apache.tools.ant.types.ZipFileSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import java.util.List;
import org.apache.tools.ant.util.CollectionUtils;
import java.util.LinkedList;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.DataType;

public class Archives extends DataType implements ResourceCollection, Cloneable
{
    private Union zips;
    private Union tars;
    
    public Archives() {
        this.zips = new Union();
        this.tars = new Union();
    }
    
    public Union createZips() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        return this.zips;
    }
    
    public Union createTars() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        return this.tars;
    }
    
    public int size() {
        if (this.isReference()) {
            return ((Archives)this.getCheckedRef()).size();
        }
        this.dieOnCircularReference();
        int total = 0;
        final Iterator<ArchiveFileSet> i = this.grabArchives();
        while (i.hasNext()) {
            total += i.next().size();
        }
        return total;
    }
    
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((Archives)this.getCheckedRef()).iterator();
        }
        this.dieOnCircularReference();
        final List<Resource> l = new LinkedList<Resource>();
        final Iterator<ArchiveFileSet> i = this.grabArchives();
        while (i.hasNext()) {
            l.addAll(CollectionUtils.asCollection((Iterator<? extends Resource>)i.next().iterator()));
        }
        return l.iterator();
    }
    
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return ((Archives)this.getCheckedRef()).isFilesystemOnly();
        }
        this.dieOnCircularReference();
        return false;
    }
    
    @Override
    public void setRefid(final Reference r) {
        if (this.zips.getResourceCollections().size() > 0 || this.tars.getResourceCollections().size() > 0) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    @Override
    public Object clone() {
        try {
            final Archives a = (Archives)super.clone();
            a.zips = (Union)this.zips.clone();
            a.tars = (Union)this.tars.clone();
            return a;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }
    
    protected Iterator<ArchiveFileSet> grabArchives() {
        final List<ArchiveFileSet> l = new LinkedList<ArchiveFileSet>();
        for (final Resource r : this.zips) {
            l.add(this.configureArchive(new ZipFileSet(), r));
        }
        for (final Resource r : this.tars) {
            l.add(this.configureArchive(new TarFileSet(), r));
        }
        return l.iterator();
    }
    
    protected ArchiveFileSet configureArchive(final ArchiveFileSet afs, final Resource src) {
        afs.setProject(this.getProject());
        afs.setSrcResource(src);
        return afs;
    }
    
    @Override
    protected synchronized void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            DataType.pushAndInvokeCircularReferenceCheck(this.zips, stk, p);
            DataType.pushAndInvokeCircularReferenceCheck(this.tars, stk, p);
            this.setChecked(true);
        }
    }
}
