// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.SequenceStrategy;

public interface SequenceMetadata extends Metadata
{
    String getName();
    
    SequenceStrategy getSequenceStrategy();
    
    SequenceMetadata setDatastoreSequence(final String p0);
    
    String getDatastoreSequence();
    
    SequenceMetadata setFactoryClass(final String p0);
    
    String getFactoryClass();
}
