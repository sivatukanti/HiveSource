// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.jdbc;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public abstract class HiveMetaDataResultSet<M> extends HiveBaseResultSet
{
    protected final List<M> data;
    
    public HiveMetaDataResultSet(final List<String> columnNames, final List<String> columnTypes, final List<M> data) throws SQLException {
        if (data != null) {
            this.data = new ArrayList<M>((Collection<? extends M>)data);
        }
        else {
            this.data = new ArrayList<M>();
        }
        if (columnNames != null) {
            this.columnNames = new ArrayList<String>(columnNames);
            this.normalizedColumnNames = new ArrayList<String>();
            for (final String colName : columnNames) {
                this.normalizedColumnNames.add(colName.toLowerCase());
            }
        }
        else {
            this.columnNames = new ArrayList<String>();
            this.normalizedColumnNames = new ArrayList<String>();
        }
        if (columnTypes != null) {
            this.columnTypes = new ArrayList<String>(columnTypes);
        }
        else {
            this.columnTypes = new ArrayList<String>();
        }
    }
    
    @Override
    public void close() throws SQLException {
    }
}
