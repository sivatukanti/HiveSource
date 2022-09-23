// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.compiler;

import org.apache.derby.iapi.services.loader.ClassFactory;

public interface JavaFactory
{
    public static final String JAVA_FACTORY_PROPERTY = "derby.module.JavaCompiler";
    
    ClassBuilder newClassBuilder(final ClassFactory p0, final String p1, final int p2, final String p3, final String p4);
}
