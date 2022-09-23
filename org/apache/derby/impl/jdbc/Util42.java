// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.SQLException;
import java.sql.JDBCType;
import java.sql.SQLType;

public class Util42
{
    public static int getTypeAsInt(final SQLType sqlType) throws SQLException {
        if (sqlType instanceof JDBCType) {
            final int intValue = ((JDBCType)sqlType).getVendorTypeNumber();
            Util.checkForSupportedDataType(intValue);
            return intValue;
        }
        throw Util.generateCsSQLException("0A000.S.7", sqlType);
    }
}
