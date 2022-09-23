// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql;

import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;

public interface Statement
{
    PreparedStatement prepare(final LanguageConnectionContext p0) throws StandardException;
    
    PreparedStatement prepare(final LanguageConnectionContext p0, final boolean p1) throws StandardException;
    
    PreparedStatement prepareStorable(final LanguageConnectionContext p0, final PreparedStatement p1, final Object[] p2, final SchemaDescriptor p3, final boolean p4) throws StandardException;
    
    String getSource();
}
