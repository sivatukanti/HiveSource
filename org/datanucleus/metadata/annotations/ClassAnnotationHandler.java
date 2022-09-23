// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractClassMetaData;

public interface ClassAnnotationHandler
{
    void processClassAnnotation(final AnnotationObject p0, final AbstractClassMetaData p1, final ClassLoaderResolver p2);
}
