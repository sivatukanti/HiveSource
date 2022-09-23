// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.compiler;

import org.apache.derby.iapi.util.ByteArray;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.GeneratedClass;

public interface ClassBuilder
{
    LocalField addField(final String p0, final String p1, final int p2);
    
    GeneratedClass getGeneratedClass() throws StandardException;
    
    ByteArray getClassBytecode() throws StandardException;
    
    String getName();
    
    String getFullName();
    
    MethodBuilder newMethodBuilder(final int p0, final String p1, final String p2);
    
    MethodBuilder newMethodBuilder(final int p0, final String p1, final String p2, final String[] p3);
    
    MethodBuilder newConstructorBuilder(final int p0);
}
