// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.compile;

import org.apache.derby.iapi.error.StandardException;

public interface ASTVisitor extends Visitor
{
    public static final int AFTER_PARSE = 0;
    public static final int AFTER_BIND = 1;
    public static final int AFTER_OPTIMIZE = 2;
    
    void initializeVisitor() throws StandardException;
    
    void teardownVisitor() throws StandardException;
    
    void begin(final String p0, final int p1) throws StandardException;
    
    void end(final int p0) throws StandardException;
}
