// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import org.datanucleus.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class FetchPlanMetaData extends MetaData
{
    String name;
    protected int maxFetchDepth;
    protected int fetchSize;
    protected List<FetchGroupMetaData> fetchGroups;
    
    public FetchPlanMetaData(final String name) {
        this.maxFetchDepth = -1;
        this.fetchSize = -1;
        this.fetchGroups = new ArrayList<FetchGroupMetaData>();
        this.name = name;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public final int getMaxFetchDepth() {
        return this.maxFetchDepth;
    }
    
    public FetchPlanMetaData setMaxFetchDepth(final int maxFetchDepth) {
        this.maxFetchDepth = maxFetchDepth;
        return this;
    }
    
    public FetchPlanMetaData setMaxFetchDepth(final String maxFetchDepth) {
        if (StringUtils.isWhitespace(maxFetchDepth)) {
            return this;
        }
        try {
            final int value = Integer.parseInt(maxFetchDepth);
            this.maxFetchDepth = value;
        }
        catch (NumberFormatException ex) {}
        return this;
    }
    
    public final int getFetchSize() {
        return this.fetchSize;
    }
    
    public int getNumberOfFetchGroups() {
        return this.fetchGroups.size();
    }
    
    public FetchPlanMetaData setFetchSize(final int fetchSize) {
        this.fetchSize = fetchSize;
        return this;
    }
    
    public FetchPlanMetaData setFetchSize(final String fetchSize) {
        if (StringUtils.isWhitespace(fetchSize)) {
            return this;
        }
        try {
            final int value = Integer.parseInt(fetchSize);
            this.fetchSize = value;
        }
        catch (NumberFormatException ex) {}
        return this;
    }
    
    public final FetchGroupMetaData[] getFetchGroupMetaData() {
        return this.fetchGroups.toArray(new FetchGroupMetaData[this.fetchGroups.size()]);
    }
    
    public void addFetchGroup(final FetchGroupMetaData fgmd) {
        this.fetchGroups.add(fgmd);
        fgmd.parent = this;
    }
    
    public FetchGroupMetaData newFetchGroupMetaData(final String name) {
        final FetchGroupMetaData fgmd = new FetchGroupMetaData(name);
        this.addFetchGroup(fgmd);
        return fgmd;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<fetch-plan name=\"" + this.name + "\"" + " max-fetch-depth=\"" + this.maxFetchDepth + "\"" + " fetch-size=\"" + this.fetchSize + "\"\n");
        for (final FetchGroupMetaData fgmd : this.fetchGroups) {
            sb.append(fgmd.toString(prefix + indent, indent));
        }
        sb.append(prefix + "</fetch-plan>\n");
        return sb.toString();
    }
}
