// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.sql;

public interface StatementGenerator
{
    public static final String OPTION_ALLOW_NULLS = "allowNulls";
    public static final String OPTION_SELECT_NUCLEUS_TYPE = "selectNucleusType";
    public static final String OPTION_RESTRICT_DISCRIM = "restrictDiscriminator";
    
    SQLStatement getStatement();
    
    void setParentStatement(final SQLStatement p0);
    
    StatementGenerator setOption(final String p0);
    
    StatementGenerator unsetOption(final String p0);
    
    boolean hasOption(final String p0);
}
