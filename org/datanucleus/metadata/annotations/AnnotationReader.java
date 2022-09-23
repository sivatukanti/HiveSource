// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.PackageMetaData;

public interface AnnotationReader
{
    String[] getSupportedAnnotationPackages();
    
    AbstractClassMetaData getMetaDataForClass(final Class p0, final PackageMetaData p1, final ClassLoaderResolver p2);
}
