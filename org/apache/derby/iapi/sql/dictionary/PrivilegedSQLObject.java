// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.iapi.sql.depend.Provider;

public interface PrivilegedSQLObject extends UniqueSQLObjectDescriptor, Provider
{
    String getObjectTypeName();
}
