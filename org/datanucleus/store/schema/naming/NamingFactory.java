// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.naming;

import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;

public interface NamingFactory
{
    NamingFactory setMaximumLength(final SchemaComponent p0, final int p1);
    
    NamingFactory setQuoteString(final String p0);
    
    NamingFactory setWordSeparator(final String p0);
    
    NamingFactory setNamingCase(final NamingCase p0);
    
    String getTableName(final AbstractClassMetaData p0);
    
    String getTableName(final AbstractMemberMetaData p0);
    
    String getColumnName(final AbstractClassMetaData p0, final ColumnType p1);
    
    String getColumnName(final AbstractMemberMetaData p0, final ColumnType p1);
    
    String getColumnName(final AbstractMemberMetaData p0, final ColumnType p1, final int p2);
}
