// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.access.conglomerate;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.store.access.conglomerate.Conglomerate;
import org.apache.derby.iapi.types.DataType;

public abstract class GenericConglomerate extends DataType implements Conglomerate
{
    public int getLength() throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public String getString() throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public Object getObject() throws StandardException {
        return this;
    }
    
    public DataValueDescriptor cloneValue(final boolean b) {
        return null;
    }
    
    public DataValueDescriptor getNewNull() {
        return null;
    }
    
    public void setValueFromResultSet(final ResultSet set, final int n, final boolean b) throws StandardException, SQLException {
        throw StandardException.newException("XSCH8.S");
    }
    
    protected void setFrom(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public String getTypeName() {
        return null;
    }
    
    public int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        throw StandardException.newException("XSCH8.S");
    }
    
    public static boolean hasCollatedColumns(final int[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != 0) {
                return true;
            }
        }
        return false;
    }
}
