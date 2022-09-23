// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.SequenceStrategy;

public interface PackageMetadata extends Metadata
{
    String getName();
    
    PackageMetadata setCatalog(final String p0);
    
    String getCatalog();
    
    PackageMetadata setSchema(final String p0);
    
    String getSchema();
    
    ClassMetadata[] getClasses();
    
    ClassMetadata newClassMetadata(final String p0);
    
    ClassMetadata newClassMetadata(final Class p0);
    
    int getNumberOfClasses();
    
    InterfaceMetadata[] getInterfaces();
    
    InterfaceMetadata newInterfaceMetadata(final String p0);
    
    InterfaceMetadata newInterfaceMetadata(final Class p0);
    
    int getNumberOfInterfaces();
    
    SequenceMetadata[] getSequences();
    
    SequenceMetadata newSequenceMetadata(final String p0, final SequenceStrategy p1);
    
    int getNumberOfSequences();
}
