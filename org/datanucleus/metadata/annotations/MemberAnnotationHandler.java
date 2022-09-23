// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata.annotations;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.AbstractMemberMetaData;

public interface MemberAnnotationHandler
{
    void processMemberAnnotation(final AnnotationObject p0, final AbstractMemberMetaData p1, final ClassLoaderResolver p2);
}
