// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import org.apache.derby.iapi.services.loader.ClassInspector;

public interface LanguageFactory
{
    public static final String MODULE = "org.apache.derby.iapi.sql.LanguageFactory";
    
    ParameterValueSet newParameterValueSet(final ClassInspector p0, final int p1, final boolean p2);
    
    ResultDescription getResultDescription(final ResultDescription p0, final int[] p1);
    
    ResultDescription getResultDescription(final ResultColumnDescriptor[] p0, final String p1);
}
