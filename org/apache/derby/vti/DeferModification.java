// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.vti;

import java.sql.SQLException;

public interface DeferModification
{
    public static final int INSERT_STATEMENT = 1;
    public static final int UPDATE_STATEMENT = 2;
    public static final int DELETE_STATEMENT = 3;
    
    boolean alwaysDefer(final int p0) throws SQLException;
    
    boolean columnRequiresDefer(final int p0, final String p1, final boolean p2) throws SQLException;
    
    boolean subselectRequiresDefer(final int p0, final String p1, final String p2) throws SQLException;
    
    boolean subselectRequiresDefer(final int p0, final String p1) throws SQLException;
    
    void modificationNotify(final int p0, final boolean p1) throws SQLException;
}
