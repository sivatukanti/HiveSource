// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.util.MergingMapper;
import org.apache.tools.ant.util.IdentityMapper;
import java.util.ArrayList;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.Reference;
import java.util.Iterator;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import java.util.Collection;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.DataType;

public class MappedResourceCollection extends DataType implements ResourceCollection, Cloneable
{
    private ResourceCollection nested;
    private Mapper mapper;
    private boolean enableMultipleMappings;
    private boolean cache;
    private Collection<Resource> cachedColl;
    
    public MappedResourceCollection() {
        this.nested = null;
        this.mapper = null;
        this.enableMultipleMappings = false;
        this.cache = false;
        this.cachedColl = null;
    }
    
    public synchronized void add(final ResourceCollection c) throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.nested != null) {
            throw new BuildException("Only one resource collection can be nested into mappedresources", this.getLocation());
        }
        this.setChecked(false);
        this.cachedColl = null;
        this.nested = c;
    }
    
    public Mapper createMapper() throws BuildException {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.mapper != null) {
            throw new BuildException("Cannot define more than one mapper", this.getLocation());
        }
        this.setChecked(false);
        this.mapper = new Mapper(this.getProject());
        this.cachedColl = null;
        return this.mapper;
    }
    
    public void add(final FileNameMapper fileNameMapper) {
        this.createMapper().add(fileNameMapper);
    }
    
    public void setEnableMultipleMappings(final boolean enableMultipleMappings) {
        this.enableMultipleMappings = enableMultipleMappings;
    }
    
    public void setCache(final boolean cache) {
        this.cache = cache;
    }
    
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return ((MappedResourceCollection)this.getCheckedRef()).isFilesystemOnly();
        }
        this.checkInitialized();
        return false;
    }
    
    public int size() {
        if (this.isReference()) {
            return ((MappedResourceCollection)this.getCheckedRef()).size();
        }
        this.checkInitialized();
        return this.cacheCollection().size();
    }
    
    public Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((MappedResourceCollection)this.getCheckedRef()).iterator();
        }
        this.checkInitialized();
        return this.cacheCollection().iterator();
    }
    
    @Override
    public void setRefid(final Reference r) {
        if (this.nested != null || this.mapper != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }
    
    @Override
    public Object clone() {
        try {
            final MappedResourceCollection c = (MappedResourceCollection)super.clone();
            c.nested = this.nested;
            c.mapper = this.mapper;
            c.cachedColl = null;
            return c;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
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
            this.checkInitialized();
            if (this.mapper != null) {
                DataType.pushAndInvokeCircularReferenceCheck(this.mapper, stk, p);
            }
            if (this.nested instanceof DataType) {
                DataType.pushAndInvokeCircularReferenceCheck((DataType)this.nested, stk, p);
            }
            this.setChecked(true);
        }
    }
    
    private void checkInitialized() {
        if (this.nested == null) {
            throw new BuildException("A nested resource collection element is required", this.getLocation());
        }
        this.dieOnCircularReference();
    }
    
    private synchronized Collection<Resource> cacheCollection() {
        if (this.cachedColl == null || !this.cache) {
            this.cachedColl = this.getCollection();
        }
        return this.cachedColl;
    }
    
    private Collection<Resource> getCollection() {
        final Collection<Resource> collected = new ArrayList<Resource>();
        final FileNameMapper m = (this.mapper != null) ? this.mapper.getImplementation() : new IdentityMapper();
        for (final Resource r : this.nested) {
            if (this.enableMultipleMappings) {
                final String[] n = m.mapFileName(r.getName());
                if (n == null) {
                    continue;
                }
                for (int i = 0; i < n.length; ++i) {
                    collected.add(new MappedResource(r, new MergingMapper(n[i])));
                }
            }
            else {
                collected.add(new MappedResource(r, m));
            }
        }
        return collected;
    }
}
