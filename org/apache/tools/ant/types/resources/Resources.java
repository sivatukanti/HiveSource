// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.util.CollectionUtils;
import java.util.AbstractCollection;
import java.util.NoSuchElementException;
import java.util.Collections;
import java.util.List;
import org.apache.tools.ant.BuildException;
import java.util.Stack;
import java.io.File;
import org.apache.tools.ant.Project;
import java.util.Collection;
import java.util.Vector;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.DataType;

public class Resources extends DataType implements ResourceCollection
{
    public static final ResourceCollection NONE;
    public static final Iterator<Resource> EMPTY_ITERATOR;
    private Vector<ResourceCollection> rc;
    private Collection<Resource> coll;
    private boolean cache;
    
    public Resources() {
        this.cache = false;
    }
    
    public Resources(final Project project) {
        this.cache = false;
        this.setProject(project);
    }
    
    public synchronized void setCache(final boolean b) {
        this.cache = b;
    }
    
    public synchronized void add(final ResourceCollection c) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (c == null) {
            return;
        }
        if (this.rc == null) {
            this.rc = new Vector<ResourceCollection>();
        }
        this.rc.add(c);
        this.invalidateExistingIterators();
        this.coll = null;
        this.setChecked(false);
    }
    
    public synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.validate();
        return new FailFast(this, this.coll.iterator());
    }
    
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.validate();
        return this.coll.size();
    }
    
    public boolean isFilesystemOnly() {
        if (this.isReference()) {
            return this.getRef().isFilesystemOnly();
        }
        this.validate();
        final Iterator<ResourceCollection> i = this.getNested().iterator();
        while (i.hasNext()) {
            if (!i.next().isFilesystemOnly()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public synchronized String toString() {
        if (this.isReference()) {
            return this.getCheckedRef().toString();
        }
        this.validate();
        if (this.coll == null || this.coll.isEmpty()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (final Resource r : this.coll) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparatorChar);
            }
            sb.append(r);
        }
        return sb.toString();
    }
    
    @Override
    protected void dieOnCircularReference(final Stack<Object> stk, final Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        }
        else {
            for (final ResourceCollection resourceCollection : this.getNested()) {
                if (resourceCollection instanceof DataType) {
                    DataType.pushAndInvokeCircularReferenceCheck((DataType)resourceCollection, stk, p);
                }
            }
            this.setChecked(true);
        }
    }
    
    protected void invalidateExistingIterators() {
        FailFast.invalidate(this);
    }
    
    private ResourceCollection getRef() {
        return this.getCheckedRef(ResourceCollection.class, "ResourceCollection");
    }
    
    private synchronized void validate() {
        this.dieOnCircularReference();
        this.coll = ((this.coll == null) ? new MyCollection() : this.coll);
    }
    
    private synchronized List<ResourceCollection> getNested() {
        return (this.rc == null) ? Collections.emptyList() : this.rc;
    }
    
    static {
        NONE = new ResourceCollection() {
            public boolean isFilesystemOnly() {
                return true;
            }
            
            public Iterator<Resource> iterator() {
                return Resources.EMPTY_ITERATOR;
            }
            
            public int size() {
                return 0;
            }
        };
        EMPTY_ITERATOR = new Iterator<Resource>() {
            public Resource next() {
                throw new NoSuchElementException();
            }
            
            public boolean hasNext() {
                return false;
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private class MyCollection extends AbstractCollection<Resource>
    {
        private Collection<Resource> cached;
        
        MyCollection() {
        }
        
        @Override
        public int size() {
            return this.getCache().size();
        }
        
        @Override
        public Iterator<Resource> iterator() {
            return this.getCache().iterator();
        }
        
        private synchronized Collection<Resource> getCache() {
            Collection<Resource> coll = this.cached;
            if (coll == null) {
                coll = CollectionUtils.asCollection((Iterator<? extends Resource>)new MyIterator());
                if (Resources.this.cache) {
                    this.cached = coll;
                }
            }
            return coll;
        }
        
        private class MyIterator implements Iterator<Resource>
        {
            private Iterator<ResourceCollection> rci;
            private Iterator<Resource> ri;
            
            private MyIterator() {
                this.rci = Resources.this.getNested().iterator();
                this.ri = null;
            }
            
            public boolean hasNext() {
                boolean result;
                for (result = (this.ri != null && this.ri.hasNext()); !result && this.rci.hasNext(); result = this.ri.hasNext()) {
                    this.ri = this.rci.next().iterator();
                }
                return result;
            }
            
            public Resource next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return this.ri.next();
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
