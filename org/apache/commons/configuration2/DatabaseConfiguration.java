// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2;

import java.sql.Clob;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Collection;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.configuration2.convert.DisabledListDelimiterHandler;
import java.sql.PreparedStatement;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.event.ConfigurationErrorEvent;
import org.apache.commons.configuration2.io.ConfigurationLogger;
import javax.sql.DataSource;

public class DatabaseConfiguration extends AbstractConfiguration
{
    private static final String SQL_GET_PROPERTY = "SELECT * FROM %s WHERE %s =?";
    private static final String SQL_IS_EMPTY = "SELECT count(*) FROM %s WHERE 1 = 1";
    private static final String SQL_CLEAR_PROPERTY = "DELETE FROM %s WHERE %s =?";
    private static final String SQL_CLEAR = "DELETE FROM %s WHERE 1 = 1";
    private static final String SQL_GET_KEYS = "SELECT DISTINCT %s FROM %s WHERE 1 = 1";
    private DataSource dataSource;
    private String table;
    private String configurationNameColumn;
    private String keyColumn;
    private String valueColumn;
    private String configurationName;
    private boolean autoCommit;
    
    public DatabaseConfiguration() {
        this.initLogger(new ConfigurationLogger(DatabaseConfiguration.class));
        this.addErrorLogListener();
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public String getTable() {
        return this.table;
    }
    
    public void setTable(final String table) {
        this.table = table;
    }
    
    public String getConfigurationNameColumn() {
        return this.configurationNameColumn;
    }
    
    public void setConfigurationNameColumn(final String configurationNameColumn) {
        this.configurationNameColumn = configurationNameColumn;
    }
    
    public String getKeyColumn() {
        return this.keyColumn;
    }
    
    public void setKeyColumn(final String keyColumn) {
        this.keyColumn = keyColumn;
    }
    
    public String getValueColumn() {
        return this.valueColumn;
    }
    
    public void setValueColumn(final String valueColumn) {
        this.valueColumn = valueColumn;
    }
    
    public String getConfigurationName() {
        return this.configurationName;
    }
    
    public void setConfigurationName(final String configurationName) {
        this.configurationName = configurationName;
    }
    
    public boolean isAutoCommit() {
        return this.autoCommit;
    }
    
    public void setAutoCommit(final boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        final JdbcOperation<Object> op = new JdbcOperation<Object>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null) {
            @Override
            protected Object performOperation() throws SQLException {
                final ResultSet rs = this.openResultSet(String.format("SELECT * FROM %s WHERE %s =?", DatabaseConfiguration.this.table, DatabaseConfiguration.this.keyColumn), true, key);
                final List<Object> results = new ArrayList<Object>();
                while (rs.next()) {
                    final Object value = DatabaseConfiguration.this.extractPropertyValue(rs);
                    for (final Object o : DatabaseConfiguration.this.getListDelimiterHandler().parse(value)) {
                        results.add(o);
                    }
                }
                if (!results.isEmpty()) {
                    return (results.size() > 1) ? results : results.get(0);
                }
                return null;
            }
        };
        return op.execute();
    }
    
    @Override
    protected void addPropertyDirect(final String key, final Object obj) {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE, ConfigurationEvent.ADD_PROPERTY, key, obj) {
            @Override
            protected Void performOperation() throws SQLException {
                final StringBuilder query = new StringBuilder("INSERT INTO ");
                query.append(DatabaseConfiguration.this.table).append(" (");
                query.append(DatabaseConfiguration.this.keyColumn).append(", ");
                query.append(DatabaseConfiguration.this.valueColumn);
                if (DatabaseConfiguration.this.configurationNameColumn != null) {
                    query.append(", ").append(DatabaseConfiguration.this.configurationNameColumn);
                }
                query.append(") VALUES (?, ?");
                if (DatabaseConfiguration.this.configurationNameColumn != null) {
                    query.append(", ?");
                }
                query.append(")");
                final PreparedStatement pstmt = this.initStatement(query.toString(), false, key, String.valueOf(obj));
                if (DatabaseConfiguration.this.configurationNameColumn != null) {
                    pstmt.setString(3, DatabaseConfiguration.this.configurationName);
                }
                pstmt.executeUpdate();
                return null;
            }
        }.execute();
    }
    
    @Override
    protected void addPropertyInternal(final String key, final Object value) {
        final ListDelimiterHandler oldHandler = this.getListDelimiterHandler();
        try {
            this.setListDelimiterHandler(DisabledListDelimiterHandler.INSTANCE);
            super.addPropertyInternal(key, value);
        }
        finally {
            this.setListDelimiterHandler(oldHandler);
        }
    }
    
    @Override
    protected boolean isEmptyInternal() {
        final JdbcOperation<Integer> op = new JdbcOperation<Integer>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null) {
            @Override
            protected Integer performOperation() throws SQLException {
                final ResultSet rs = this.openResultSet(String.format("SELECT count(*) FROM %s WHERE 1 = 1", DatabaseConfiguration.this.table), true, new Object[0]);
                return rs.next() ? Integer.valueOf(rs.getInt(1)) : null;
            }
        };
        final Integer count = op.execute();
        return count == null || count == 0;
    }
    
    @Override
    protected boolean containsKeyInternal(final String key) {
        final JdbcOperation<Boolean> op = new JdbcOperation<Boolean>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, key, null) {
            @Override
            protected Boolean performOperation() throws SQLException {
                final ResultSet rs = this.openResultSet(String.format("SELECT * FROM %s WHERE %s =?", DatabaseConfiguration.this.table, DatabaseConfiguration.this.keyColumn), true, key);
                return rs.next();
            }
        };
        final Boolean result = op.execute();
        return result != null && result;
    }
    
    @Override
    protected void clearPropertyDirect(final String key) {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE, ConfigurationEvent.CLEAR_PROPERTY, key, null) {
            @Override
            protected Void performOperation() throws SQLException {
                final PreparedStatement ps = this.initStatement(String.format("DELETE FROM %s WHERE %s =?", DatabaseConfiguration.this.table, DatabaseConfiguration.this.keyColumn), true, key);
                ps.executeUpdate();
                return null;
            }
        }.execute();
    }
    
    @Override
    protected void clearInternal() {
        new JdbcOperation<Void>(ConfigurationErrorEvent.WRITE, ConfigurationEvent.CLEAR, null, null) {
            @Override
            protected Void performOperation() throws SQLException {
                this.initStatement(String.format("DELETE FROM %s WHERE 1 = 1", DatabaseConfiguration.this.table), true, new Object[0]).executeUpdate();
                return null;
            }
        }.execute();
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        final Collection<String> keys = new ArrayList<String>();
        new JdbcOperation<Collection<String>>(ConfigurationErrorEvent.READ, ConfigurationErrorEvent.READ, null, null) {
            @Override
            protected Collection<String> performOperation() throws SQLException {
                final ResultSet rs = this.openResultSet(String.format("SELECT DISTINCT %s FROM %s WHERE 1 = 1", DatabaseConfiguration.this.keyColumn, DatabaseConfiguration.this.table), true, new Object[0]);
                while (rs.next()) {
                    keys.add(rs.getString(1));
                }
                return keys;
            }
        }.execute();
        return keys.iterator();
    }
    
    public DataSource getDatasource() {
        return this.dataSource;
    }
    
    protected void close(final Connection conn, final Statement stmt, final ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        }
        catch (SQLException e) {
            this.getLogger().error("An error occurred on closing the result set", e);
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        }
        catch (SQLException e) {
            this.getLogger().error("An error occured on closing the statement", e);
        }
        try {
            if (conn != null) {
                conn.close();
            }
        }
        catch (SQLException e) {
            this.getLogger().error("An error occured on closing the connection", e);
        }
    }
    
    protected Object extractPropertyValue(final ResultSet rs) throws SQLException {
        Object value = rs.getObject(this.valueColumn);
        if (value instanceof Clob) {
            value = convertClob((Clob)value);
        }
        return value;
    }
    
    private static Object convertClob(final Clob clob) throws SQLException {
        final int len = (int)clob.length();
        return (len > 0) ? clob.getSubString(1L, len) : "";
    }
    
    private abstract class JdbcOperation<T>
    {
        private Connection conn;
        private PreparedStatement pstmt;
        private ResultSet resultSet;
        private final EventType<? extends ConfigurationErrorEvent> errorEventType;
        private final EventType<?> operationEventType;
        private final String errorPropertyName;
        private final Object errorPropertyValue;
        
        protected JdbcOperation(final EventType<? extends ConfigurationErrorEvent> errEvType, final EventType<?> opType, final String errPropName, final Object errPropVal) {
            this.errorEventType = errEvType;
            this.operationEventType = opType;
            this.errorPropertyName = errPropName;
            this.errorPropertyValue = errPropVal;
        }
        
        public T execute() {
            T result = null;
            try {
                this.conn = DatabaseConfiguration.this.getDatasource().getConnection();
                result = this.performOperation();
                if (DatabaseConfiguration.this.isAutoCommit()) {
                    this.conn.commit();
                }
            }
            catch (SQLException e) {
                DatabaseConfiguration.this.fireError(this.errorEventType, this.operationEventType, this.errorPropertyName, this.errorPropertyValue, e);
            }
            finally {
                DatabaseConfiguration.this.close(this.conn, this.pstmt, this.resultSet);
            }
            return result;
        }
        
        protected Connection getConnection() {
            return this.conn;
        }
        
        protected PreparedStatement createStatement(final String sql, final boolean nameCol) throws SQLException {
            String statement;
            if (nameCol && DatabaseConfiguration.this.configurationNameColumn != null) {
                final StringBuilder buf = new StringBuilder(sql);
                buf.append(" AND ").append(DatabaseConfiguration.this.configurationNameColumn).append("=?");
                statement = buf.toString();
            }
            else {
                statement = sql;
            }
            return this.pstmt = this.getConnection().prepareStatement(statement);
        }
        
        protected PreparedStatement initStatement(final String sql, final boolean nameCol, final Object... params) throws SQLException {
            final PreparedStatement ps = this.createStatement(sql, nameCol);
            int idx = 1;
            for (final Object param : params) {
                ps.setObject(idx++, param);
            }
            if (nameCol && DatabaseConfiguration.this.configurationNameColumn != null) {
                ps.setString(idx, DatabaseConfiguration.this.configurationName);
            }
            return ps;
        }
        
        protected ResultSet openResultSet(final String sql, final boolean nameCol, final Object... params) throws SQLException {
            return this.resultSet = this.initStatement(sql, nameCol, params).executeQuery();
        }
        
        protected abstract T performOperation() throws SQLException;
    }
}
