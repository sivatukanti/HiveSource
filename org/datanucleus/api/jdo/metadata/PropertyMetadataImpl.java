// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FetchGroupMemberMetaData;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.PropertyMetaData;
import javax.jdo.metadata.PropertyMetadata;

public class PropertyMetadataImpl extends MemberMetadataImpl implements PropertyMetadata
{
    public PropertyMetadataImpl(final PropertyMetaData internal) {
        super(internal);
    }
    
    public PropertyMetadataImpl(final FetchGroupMemberMetaData internal) {
        super(internal);
    }
    
    @Override
    public PropertyMetaData getInternal() {
        return (PropertyMetaData)this.internalMD;
    }
    
    public String getFieldName() {
        return this.getInternal().getFieldName();
    }
    
    public PropertyMetadata setFieldName(final String name) {
        this.getInternal().setFieldName(name);
        return this;
    }
}
