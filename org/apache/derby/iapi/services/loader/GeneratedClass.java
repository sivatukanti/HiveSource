// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.Context;

public interface GeneratedClass
{
    String getName();
    
    Object newInstance(final Context p0) throws StandardException;
    
    GeneratedMethod getMethod(final String p0) throws StandardException;
    
    int getClassLoaderVersion();
}
