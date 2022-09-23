// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface MapMetadata extends Metadata
{
    MapMetadata setKeyType(final String p0);
    
    String getKeyType();
    
    MapMetadata setEmbeddedKey(final boolean p0);
    
    Boolean getEmbeddedKey();
    
    MapMetadata setSerializedKey(final boolean p0);
    
    Boolean getSerializedKey();
    
    MapMetadata setDependentKey(final boolean p0);
    
    Boolean getDependentKey();
    
    MapMetadata setValueType(final String p0);
    
    String getValueType();
    
    MapMetadata setEmbeddedValue(final boolean p0);
    
    Boolean getEmbeddedValue();
    
    MapMetadata setSerializedValue(final boolean p0);
    
    Boolean getSerializedValue();
    
    MapMetadata setDependentValue(final boolean p0);
    
    Boolean getDependentValue();
}
