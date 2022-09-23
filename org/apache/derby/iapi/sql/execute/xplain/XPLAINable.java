// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.execute.xplain;

public interface XPLAINable
{
    void accept(final XPLAINVisitor p0);
    
    String getRSXplainType();
    
    String getRSXplainDetails();
    
    Object getResultSetDescriptor(final Object p0, final Object p1, final Object p2, final Object p3, final Object p4, final Object p5);
    
    Object getResultSetTimingsDescriptor(final Object p0);
    
    Object getSortPropsDescriptor(final Object p0);
    
    Object getScanPropsDescriptor(final Object p0);
}
