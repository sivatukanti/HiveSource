// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface ArrayMetadata extends Metadata
{
    ArrayMetadata setElementType(final String p0);
    
    String getElementType();
    
    ArrayMetadata setEmbeddedElement(final boolean p0);
    
    Boolean getEmbeddedElement();
    
    ArrayMetadata setSerializedElement(final boolean p0);
    
    Boolean getSerializedElement();
    
    ArrayMetadata setDependentElement(final boolean p0);
    
    Boolean getDependentElement();
}
