// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.CollectionMetaData;
import javax.jdo.metadata.CollectionMetadata;

public class CollectionMetadataImpl extends AbstractMetadataImpl implements CollectionMetadata
{
    public CollectionMetadataImpl(final CollectionMetaData internal) {
        super(internal);
    }
    
    public CollectionMetaData getInternal() {
        return (CollectionMetaData)this.internalMD;
    }
    
    public Boolean getDependentElement() {
        return this.getInternal().isDependentElement();
    }
    
    public String getElementType() {
        return this.getInternal().getElementType();
    }
    
    public Boolean getEmbeddedElement() {
        return this.getInternal().isEmbeddedElement();
    }
    
    public Boolean getSerializedElement() {
        return this.getInternal().isSerializedElement();
    }
    
    public CollectionMetadata setDependentElement(final boolean flag) {
        this.getInternal().setDependentElement(flag);
        return this;
    }
    
    public CollectionMetadata setElementType(final String type) {
        this.getInternal().setElementType(type);
        return this;
    }
    
    public CollectionMetadata setEmbeddedElement(final boolean flag) {
        this.getInternal().setEmbeddedElement(flag);
        return this;
    }
    
    public CollectionMetadata setSerializedElement(final boolean flag) {
        this.getInternal().setSerializedElement(flag);
        return this;
    }
}
