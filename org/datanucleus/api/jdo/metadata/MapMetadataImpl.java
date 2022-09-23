// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.MapMetaData;
import javax.jdo.metadata.MapMetadata;

public class MapMetadataImpl extends AbstractMetadataImpl implements MapMetadata
{
    public MapMetadataImpl(final MapMetaData internal) {
        super(internal);
    }
    
    public MapMetaData getInternal() {
        return (MapMetaData)this.internalMD;
    }
    
    public Boolean getDependentKey() {
        return this.getInternal().isDependentKey();
    }
    
    public Boolean getDependentValue() {
        return this.getInternal().isDependentValue();
    }
    
    public Boolean getEmbeddedKey() {
        return this.getInternal().isEmbeddedKey();
    }
    
    public Boolean getEmbeddedValue() {
        return this.getInternal().isEmbeddedValue();
    }
    
    public String getKeyType() {
        return this.getInternal().getKeyType();
    }
    
    public Boolean getSerializedKey() {
        return this.getInternal().isSerializedKey();
    }
    
    public Boolean getSerializedValue() {
        return this.getInternal().isSerializedValue();
    }
    
    public String getValueType() {
        return this.getInternal().getValueType();
    }
    
    public MapMetadata setDependentKey(final boolean flag) {
        this.getInternal().setDependentKey(flag);
        return this;
    }
    
    public MapMetadata setDependentValue(final boolean flag) {
        this.getInternal().setDependentValue(flag);
        return this;
    }
    
    public MapMetadata setEmbeddedKey(final boolean flag) {
        this.getInternal().setEmbeddedKey(flag);
        return this;
    }
    
    public MapMetadata setEmbeddedValue(final boolean flag) {
        this.getInternal().setEmbeddedValue(flag);
        return this;
    }
    
    public MapMetadata setKeyType(final String type) {
        this.getInternal().setKeyType(type);
        return this;
    }
    
    public MapMetadata setSerializedKey(final boolean flag) {
        this.getInternal().setSerializedKey(flag);
        return this;
    }
    
    public MapMetadata setSerializedValue(final boolean flag) {
        this.getInternal().setSerializedValue(flag);
        return this;
    }
    
    public MapMetadata setValueType(final String type) {
        this.getInternal().setValueType(type);
        return this;
    }
}
