// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.FetchGroupMetaData;
import javax.jdo.metadata.FetchGroupMetadata;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.FetchPlanMetaData;
import javax.jdo.metadata.FetchPlanMetadata;

public class FetchPlanMetadataImpl extends AbstractMetadataImpl implements FetchPlanMetadata
{
    public FetchPlanMetadataImpl(final FetchPlanMetaData fpmd) {
        super(fpmd);
    }
    
    public FetchPlanMetaData getInternal() {
        return (FetchPlanMetaData)this.internalMD;
    }
    
    public FetchGroupMetadata[] getFetchGroups() {
        final FetchGroupMetaData[] baseData = this.getInternal().getFetchGroupMetaData();
        if (baseData == null) {
            return null;
        }
        final FetchGroupMetadataImpl[] fgs = new FetchGroupMetadataImpl[baseData.length];
        for (int i = 0; i < fgs.length; ++i) {
            fgs[i] = new FetchGroupMetadataImpl(baseData[i]);
            fgs[i].parent = this;
        }
        return fgs;
    }
    
    public int getNumberOfFetchGroups() {
        return this.getInternal().getNumberOfFetchGroups();
    }
    
    public FetchGroupMetadata newFetchGroupMetadata(final String name) {
        final FetchGroupMetaData internalFgmd = this.getInternal().newFetchGroupMetaData(name);
        final FetchGroupMetadataImpl fgmd = new FetchGroupMetadataImpl(internalFgmd);
        fgmd.parent = this;
        return fgmd;
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public int getFetchSize() {
        return this.getInternal().getFetchSize();
    }
    
    public FetchPlanMetadata setFetchSize(final int size) {
        this.getInternal().setFetchSize(size);
        return this;
    }
    
    public int getMaxFetchDepth() {
        return this.getInternal().getMaxFetchDepth();
    }
    
    public FetchPlanMetadata setMaxFetchDepth(final int depth) {
        this.getInternal().setMaxFetchDepth(depth);
        return this;
    }
}
