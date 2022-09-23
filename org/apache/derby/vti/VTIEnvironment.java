// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.io.Serializable;

public interface VTIEnvironment
{
    boolean isCompileTime();
    
    String getOriginalSQL();
    
    int getStatementIsolationLevel();
    
    void setSharedState(final String p0, final Serializable p1);
    
    Object getSharedState(final String p0);
}
