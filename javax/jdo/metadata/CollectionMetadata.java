// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface CollectionMetadata extends Metadata
{
    CollectionMetadata setElementType(final String p0);
    
    String getElementType();
    
    CollectionMetadata setEmbeddedElement(final boolean p0);
    
    Boolean getEmbeddedElement();
    
    CollectionMetadata setSerializedElement(final boolean p0);
    
    Boolean getSerializedElement();
    
    CollectionMetadata setDependentElement(final boolean p0);
    
    Boolean getDependentElement();
}
