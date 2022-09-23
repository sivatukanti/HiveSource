// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

public interface FetchGroupMetadata extends Metadata
{
    String getName();
    
    FetchGroupMetadata setPostLoad(final boolean p0);
    
    Boolean getPostLoad();
    
    MemberMetadata[] getMembers();
    
    int getNumberOfMembers();
    
    FieldMetadata newFieldMetadata(final String p0);
    
    PropertyMetadata newPropertyMetadata(final String p0);
}
