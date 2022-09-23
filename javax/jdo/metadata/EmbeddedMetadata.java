// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface EmbeddedMetadata extends Metadata
{
    EmbeddedMetadata setOwnerMember(final String p0);
    
    String getOwnerMember();
    
    EmbeddedMetadata setNullIndicatorColumn(final String p0);
    
    String getNullIndicatorColumn();
    
    EmbeddedMetadata setNullIndicatorValue(final String p0);
    
    String getNullIndicatorValue();
    
    MemberMetadata[] getMembers();
    
    int getNumberOfMembers();
    
    FieldMetadata newFieldMetadata(final String p0);
    
    PropertyMetadata newPropertyMetadata(final String p0);
}
