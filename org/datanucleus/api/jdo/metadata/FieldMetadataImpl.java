// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.FetchGroupMemberMetaData;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.FieldMetaData;
import javax.jdo.metadata.FieldMetadata;

public class FieldMetadataImpl extends MemberMetadataImpl implements FieldMetadata
{
    public FieldMetadataImpl(final FieldMetaData internal) {
        super(internal);
    }
    
    public FieldMetadataImpl(final FetchGroupMemberMetaData internal) {
        super(internal);
    }
}
