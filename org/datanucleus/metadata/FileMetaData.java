// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;

public class FileMetaData extends MetaData
{
    protected transient MetaDataManager metaDataManager;
    protected MetadataFileType type;
    protected String filename;
    protected String catalog;
    protected String schema;
    protected Collection<QueryMetaData> queries;
    protected Collection<StoredProcQueryMetaData> storedProcs;
    protected Collection<QueryResultMetaData> queryResultMetaData;
    protected Collection<FetchPlanMetaData> fetchPlans;
    protected List<PackageMetaData> packages;
    protected List<EventListenerMetaData> listeners;
    
    public FileMetaData() {
        this.queries = null;
        this.storedProcs = null;
        this.queryResultMetaData = null;
        this.fetchPlans = null;
        this.packages = null;
        this.listeners = null;
    }
    
    public void setMetaDataManager(final MetaDataManager mmgr) {
        this.metaDataManager = mmgr;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public FileMetaData setFilename(final String filename) {
        this.filename = filename;
        return this;
    }
    
    public String getCatalog() {
        return this.catalog;
    }
    
    public FileMetaData setCatalog(final String catalog) {
        this.catalog = catalog;
        return this;
    }
    
    public String getSchema() {
        return this.schema;
    }
    
    public FileMetaData setSchema(final String schema) {
        this.schema = schema;
        return this;
    }
    
    public MetadataFileType getType() {
        return this.type;
    }
    
    public FileMetaData setType(final MetadataFileType type) {
        this.type = type;
        return this;
    }
    
    public int getNoOfQueries() {
        return (this.queries != null) ? this.queries.size() : 0;
    }
    
    public QueryMetaData[] getQueries() {
        return (QueryMetaData[])((this.queries == null) ? null : ((QueryMetaData[])this.queries.toArray(new QueryMetaData[this.queries.size()])));
    }
    
    public int getNoOfStoredProcQueries() {
        return (this.storedProcs != null) ? this.storedProcs.size() : 0;
    }
    
    public StoredProcQueryMetaData[] getStoredProcQueries() {
        return (StoredProcQueryMetaData[])((this.storedProcs == null) ? null : ((StoredProcQueryMetaData[])this.storedProcs.toArray(new StoredProcQueryMetaData[this.storedProcs.size()])));
    }
    
    public int getNoOfFetchPlans() {
        return (this.fetchPlans != null) ? this.fetchPlans.size() : 0;
    }
    
    public FetchPlanMetaData[] getFetchPlans() {
        return (FetchPlanMetaData[])((this.fetchPlans == null) ? null : ((FetchPlanMetaData[])this.fetchPlans.toArray(new FetchPlanMetaData[this.fetchPlans.size()])));
    }
    
    public int getNoOfPackages() {
        return (this.packages != null) ? this.packages.size() : 0;
    }
    
    public PackageMetaData getPackage(final int i) {
        if (this.packages == null) {
            return null;
        }
        return this.packages.get(i);
    }
    
    public PackageMetaData getPackage(final String name) {
        if (this.packages == null) {
            return null;
        }
        for (final PackageMetaData p : this.packages) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }
    
    public ClassMetaData getClass(final String pkg_name, final String class_name) {
        if (pkg_name == null || class_name == null) {
            return null;
        }
        final PackageMetaData pmd = this.getPackage(pkg_name);
        if (pmd != null) {
            return pmd.getClass(class_name);
        }
        return null;
    }
    
    public QueryMetaData newQueryMetadata(final String queryName) {
        final QueryMetaData qmd = new QueryMetaData(queryName);
        if (this.queries == null) {
            this.queries = new HashSet<QueryMetaData>();
        }
        this.queries.add(qmd);
        qmd.parent = this;
        return qmd;
    }
    
    public StoredProcQueryMetaData newStoredProcQueryMetaData(final String queryName) {
        final StoredProcQueryMetaData qmd = new StoredProcQueryMetaData(queryName);
        if (this.storedProcs == null) {
            this.storedProcs = new HashSet<StoredProcQueryMetaData>();
        }
        this.storedProcs.add(qmd);
        qmd.parent = this;
        return qmd;
    }
    
    public FetchPlanMetaData newFetchPlanMetadata(final String name) {
        final FetchPlanMetaData fpmd = new FetchPlanMetaData(name);
        if (this.fetchPlans == null) {
            this.fetchPlans = new HashSet<FetchPlanMetaData>();
        }
        this.fetchPlans.add(fpmd);
        fpmd.parent = this;
        return fpmd;
    }
    
    public PackageMetaData newPackageMetadata(final String name) {
        final PackageMetaData pmd = new PackageMetaData(name);
        if (this.packages == null) {
            this.packages = new ArrayList<PackageMetaData>();
        }
        else {
            for (final PackageMetaData p : this.packages) {
                if (pmd.getName().equals(p.getName())) {
                    return p;
                }
            }
        }
        this.packages.add(pmd);
        pmd.parent = this;
        return pmd;
    }
    
    public void addListener(final EventListenerMetaData listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<EventListenerMetaData>();
        }
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
            listener.parent = this;
        }
    }
    
    public List getListeners() {
        return this.listeners;
    }
    
    public void addQueryResultMetaData(final QueryResultMetaData resultMetaData) {
        if (this.queryResultMetaData == null) {
            this.queryResultMetaData = new HashSet<QueryResultMetaData>();
        }
        if (!this.queryResultMetaData.contains(resultMetaData)) {
            this.queryResultMetaData.add(resultMetaData);
            resultMetaData.parent = this;
        }
    }
    
    public QueryResultMetaData newQueryResultMetadata(final String name) {
        final QueryResultMetaData qrmd = new QueryResultMetaData(name);
        this.addQueryResultMetaData(qrmd);
        return qrmd;
    }
    
    public QueryResultMetaData[] getQueryResultMetaData() {
        if (this.queryResultMetaData == null) {
            return null;
        }
        return this.queryResultMetaData.toArray(new QueryResultMetaData[this.queryResultMetaData.size()]);
    }
    
    @Override
    public String toString(final String prefix, String indent) {
        if (indent == null) {
            indent = "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<jdo");
        if (this.catalog != null) {
            sb.append(" catalog=\"" + this.catalog + "\"");
        }
        if (this.schema != null) {
            sb.append(" schema=\"" + this.schema + "\"");
        }
        sb.append(">\n");
        if (this.packages != null) {
            final Iterator<PackageMetaData> iter = this.packages.iterator();
            while (iter.hasNext()) {
                sb.append(iter.next().toString(indent, indent));
            }
        }
        if (this.queries != null) {
            final Iterator iter2 = this.queries.iterator();
            while (iter2.hasNext()) {
                sb.append(iter2.next().toString(indent, indent));
            }
        }
        if (this.fetchPlans != null) {
            final Iterator iter2 = this.fetchPlans.iterator();
            while (iter2.hasNext()) {
                sb.append(iter2.next().toString(indent, indent));
            }
        }
        sb.append(super.toString(indent, indent));
        sb.append("</jdo>");
        return sb.toString();
    }
}
