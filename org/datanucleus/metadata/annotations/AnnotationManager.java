// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.PackageMetaData;

public interface AnnotationManager
{
    AbstractClassMetaData getMetaDataForClass(final Class p0, final PackageMetaData p1, final ClassLoaderResolver p2);
    
    boolean getClassAnnotationHasHandler(final String p0);
    
    boolean getMemberAnnotationHasHandler(final String p0);
    
    ClassAnnotationHandler getHandlerForClassAnnotation(final String p0);
    
    MemberAnnotationHandler getHandlerForMemberAnnotation(final String p0);
}
