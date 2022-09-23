// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.PropertyHelper;
import java.io.Reader;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.BufferedReader;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedInputStream;
import org.apache.tools.ant.Project;
import java.util.Stack;
import org.apache.tools.ant.types.Resource;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import java.util.ArrayList;
import org.apache.tools.ant.types.FilterChain;
import java.util.Vector;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.DataType;

public class ResourceList extends DataType implements ResourceCollection
{
    private final Vector<FilterChain> filterChains;
    private final ArrayList<ResourceCollection> textDocuments;
    private final Union cachedResources;
    private volatile boolean cached;
    private String encoding;
    
    public ResourceList() {
        this.filterChains = new Vector<FilterChain>();
        this.textDocuments = new ArrayList<ResourceCollection>();
        this.cachedResources = new Union();
        this.cached = false;
        this.encoding = null;
        this.cachedResources.setCache(true);
    }
    
    public void add(final ResourceCollection rc) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.textDocuments.add(rc);
        this.setChecked(false);
    }
    
    public final void addFilterChain(final FilterChain filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.filterChains.add(filter);
        this.setChecked(false);
    }
    
    public final void setEncoding(final String encoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.encoding = encoding;
    }
    
    @Override
    public void setRefid(final Reference r) throws BuildException {
        if (this.encoding != null) {
            throw this.tooManyAttributes();
        }
        if (this.filterChains.size() > 0 || this.textDocuments.size() > 0) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }
    
    public final synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return ((ResourceList)this.getCheckedRef()).iterator();
        }
        return this.cache().iterator();
    }
    
    public synchronized int size() {
        if (this.isReference()) {
            return ((ResourceList)this.getCheckedRef()).size();
        }
        return this.cache().size();
    }
    
    public synchronized boolean isFilesystemOnly() {
        if (this.isReference()) {
            return ((ResourceList)this.getCheckedRef()).isFilesystemOnly();
        }
        return this.cache().isFilesystemOnly();
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
            for (final ResourceCollection resourceCollection : this.textDocuments) {
                if (resourceCollection instanceof DataType) {
                    DataType.pushAndInvokeCircularReferenceCheck((DataType)resourceCollection, stk, p);
                }
            }
            for (final FilterChain filterChain : this.filterChains) {
                DataType.pushAndInvokeCircularReferenceCheck(filterChain, stk, p);
            }
            this.setChecked(true);
        }
    }
    
    private synchronized ResourceCollection cache() {
        if (!this.cached) {
            this.dieOnCircularReference();
            for (final ResourceCollection rc : this.textDocuments) {
                for (final Resource r : rc) {
                    this.cachedResources.add(this.read(r));
                }
            }
            this.cached = true;
        }
        return this.cachedResources;
    }
    
    private ResourceCollection read(final Resource r) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(r.getInputStream());
            Reader input = null;
            if (this.encoding == null) {
                input = new InputStreamReader(bis);
            }
            else {
                input = new InputStreamReader(bis, this.encoding);
            }
            final ChainReaderHelper crh = new ChainReaderHelper();
            crh.setPrimaryReader(input);
            crh.setFilterChains(this.filterChains);
            crh.setProject(this.getProject());
            final BufferedReader reader = new BufferedReader(crh.getAssembledReader());
            final Union streamResources = new Union();
            streamResources.setCache(true);
            String line = null;
            while ((line = reader.readLine()) != null) {
                streamResources.add(this.parse(line));
            }
            return streamResources;
        }
        catch (IOException ioe) {
            throw new BuildException("Unable to read resource " + r.getName() + ": " + ioe, ioe, this.getLocation());
        }
        finally {
            FileUtils.close(bis);
        }
    }
    
    private Resource parse(final String line) {
        final PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(this.getProject());
        final Object expanded = propertyHelper.parseProperties(line);
        if (expanded instanceof Resource) {
            return (Resource)expanded;
        }
        final String expandedLine = expanded.toString();
        final int colon = expandedLine.indexOf(":");
        if (colon != -1) {
            try {
                return new URLResource(expandedLine);
            }
            catch (BuildException ex) {}
        }
        return new FileResource(this.getProject(), expandedLine);
    }
}
