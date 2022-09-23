// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.ArrayMetaData;
import javax.jdo.metadata.ArrayMetadata;

public class ArrayMetadataImpl extends AbstractMetadataImpl implements ArrayMetadata
{
    public ArrayMetadataImpl(final ArrayMetaData internal) {
        super(internal);
    }
    
    public ArrayMetaData getInternal() {
        return (ArrayMetaData)this.internalMD;
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
    
    public ArrayMetadata setDependentElement(final boolean flag) {
        this.getInternal().setDependentElement(flag);
        return this;
    }
    
    public ArrayMetadata setElementType(final String type) {
        this.getInternal().setElementType(type);
        return this;
    }
    
    public ArrayMetadata setEmbeddedElement(final boolean flag) {
        this.getInternal().setEmbeddedElement(flag);
        return this;
    }
    
    public ArrayMetadata setSerializedElement(final boolean flag) {
        this.getInternal().setSerializedElement(flag);
        return this;
    }
}
