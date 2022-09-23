// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;

public interface ResultSetHelper
{
    String[] getColumnNames(final ResultSet p0) throws SQLException;
    
    String[] getColumnValues(final ResultSet p0) throws SQLException, IOException;
}
