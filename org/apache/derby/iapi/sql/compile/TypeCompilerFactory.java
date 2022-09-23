// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.types.TypeId;

public interface TypeCompilerFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.compile.TypeCompilerFactory";
    
    TypeCompiler getTypeCompiler(final TypeId p0);
}
