// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.sql.Array;
import java.sql.Ref;
import java.util.Map;
import java.io.InputStream;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.SQLDecimal;
import java.math.BigDecimal;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.ResultSet;

public class EmbedResultSet20 extends EmbedResultSet
{
    public EmbedResultSet20(final EmbedConnection embedConnection, final org.apache.derby.iapi.sql.ResultSet set, final boolean b, final EmbedStatement embedStatement, final boolean b2) throws SQLException {
        super(embedConnection, set, b, embedStatement, b2);
    }
    
    @Deprecated
    public final BigDecimal getBigDecimal(final int n, final int newScale) throws SQLException {
        final BigDecimal bigDecimal = this.getBigDecimal(n);
        if (bigDecimal != null) {
            return bigDecimal.setScale(newScale, 5);
        }
        return null;
    }
    
    public final BigDecimal getBigDecimal(final int n) throws SQLException {
        this.checkIfClosed("getBigDecimal");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return null;
            }
            return SQLDecimal.getBigDecimal(column);
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    @Deprecated
    public final BigDecimal getBigDecimal(final String s, final int n) throws SQLException {
        this.checkIfClosed("getBigDecimal");
        return this.getBigDecimal(this.findColumnName(s), n);
    }
    
    @Deprecated
    public final InputStream getUnicodeStream(final int n) throws SQLException {
        throw Util.notImplemented("getUnicodeStream");
    }
    
    @Deprecated
    public final InputStream getUnicodeStream(final String s) throws SQLException {
        throw Util.notImplemented("getUnicodeStream");
    }
    
    public final BigDecimal getBigDecimal(final String s) throws SQLException {
        this.checkIfClosed("getBigDecimal");
        return this.getBigDecimal(this.findColumnName(s));
    }
    
    public void updateBigDecimal(final int n, final BigDecimal bigDecimal) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateBigDecimal").setBigDecimal(bigDecimal);
        }
        catch (StandardException ex) {
            throw EmbedResultSet.noStateChangeException(ex);
        }
    }
    
    @Override
    public void updateObject(final int n, final Object o) throws SQLException {
        this.checksBeforeUpdateOrDelete("updateObject", n);
        this.getColumnType(n);
        if (o instanceof BigDecimal) {
            this.updateBigDecimal(n, (BigDecimal)o);
            return;
        }
        super.updateObject(n, o);
    }
    
    public void updateBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        this.checkIfClosed("updateBigDecimal");
        this.updateBigDecimal(this.findColumnName(s), bigDecimal);
    }
    
    public Object getObject(final int n, final Map map) throws SQLException {
        this.checkIfClosed("getObject");
        if (map == null) {
            throw Util.generateCsSQLException("XJ081.S", map, "map", "java.sql.ResultSet.getObject");
        }
        if (!map.isEmpty()) {
            throw Util.notImplemented();
        }
        return this.getObject(n);
    }
    
    public Ref getRef(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Array getArray(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Object getObject(final String s, final Map map) throws SQLException {
        this.checkIfClosed("getObject");
        return this.getObject(this.findColumn(s), map);
    }
    
    public Ref getRef(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Array getArray(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void updateRef(final int n, final Ref ref) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void updateRef(final String s, final Ref ref) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void updateArray(final int n, final Array array) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void updateArray(final String s, final Array array) throws SQLException {
        throw Util.notImplemented();
    }
}
