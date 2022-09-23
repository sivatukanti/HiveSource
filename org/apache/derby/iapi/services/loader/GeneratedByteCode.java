// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.loader;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.context.Context;

public interface GeneratedByteCode
{
    void initFromContext(final Context p0) throws StandardException;
    
    void setGC(final GeneratedClass p0);
    
    void postConstructor() throws StandardException;
    
    GeneratedClass getGC();
    
    GeneratedMethod getMethod(final String p0) throws StandardException;
    
    Object e0() throws StandardException;
    
    Object e1() throws StandardException;
    
    Object e2() throws StandardException;
    
    Object e3() throws StandardException;
    
    Object e4() throws StandardException;
    
    Object e5() throws StandardException;
    
    Object e6() throws StandardException;
    
    Object e7() throws StandardException;
    
    Object e8() throws StandardException;
    
    Object e9() throws StandardException;
}
