// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.InheritanceStrategy;

public interface InheritanceMetadata extends Metadata
{
    InheritanceMetadata setStrategy(final InheritanceStrategy p0);
    
    InheritanceStrategy getStrategy();
    
    InheritanceMetadata setCustomStrategy(final String p0);
    
    String getCustomStrategy();
    
    DiscriminatorMetadata newDiscriminatorMetadata();
    
    DiscriminatorMetadata getDiscriminatorMetadata();
    
    JoinMetadata newJoinMetadata();
    
    JoinMetadata getJoinMetadata();
}
