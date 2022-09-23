// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;

public interface DependableFinder
{
    Dependable getDependable(final DataDictionary p0, final UUID p1) throws StandardException;
    
    String getSQLObjectType();
}
