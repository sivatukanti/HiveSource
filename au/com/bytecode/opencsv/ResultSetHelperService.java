// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv;

import java.io.Reader;
import java.sql.Clob;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;

public class ResultSetHelperService implements ResultSetHelper
{
    public static final int CLOBBUFFERSIZE = 2048;
    private static final int NVARCHAR = -9;
    private static final int NCHAR = -15;
    private static final int LONGNVARCHAR = -16;
    private static final int NCLOB = 2011;
    
    public String[] getColumnNames(final ResultSet rs) throws SQLException {
        final List<String> names = new ArrayList<String>();
        final ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 0; i < metadata.getColumnCount(); ++i) {
            names.add(metadata.getColumnName(i + 1));
        }
        final String[] nameArray = new String[names.size()];
        return names.toArray(nameArray);
    }
    
    public String[] getColumnValues(final ResultSet rs) throws SQLException, IOException {
        final List<String> values = new ArrayList<String>();
        final ResultSetMetaData metadata = rs.getMetaData();
        for (int i = 0; i < metadata.getColumnCount(); ++i) {
            values.add(this.getColumnValue(rs, metadata.getColumnType(i + 1), i + 1));
        }
        final String[] valueArray = new String[values.size()];
        return values.toArray(valueArray);
    }
    
    private String handleObject(final Object obj) {
        return (obj == null) ? "" : String.valueOf(obj);
    }
    
    private String handleBigDecimal(final BigDecimal decimal) {
        return (decimal == null) ? "" : decimal.toString();
    }
    
    private String handleLong(final ResultSet rs, final int columnIndex) throws SQLException {
        final long lv = rs.getLong(columnIndex);
        return rs.wasNull() ? "" : Long.toString(lv);
    }
    
    private String handleInteger(final ResultSet rs, final int columnIndex) throws SQLException {
        final int i = rs.getInt(columnIndex);
        return rs.wasNull() ? "" : Integer.toString(i);
    }
    
    private String handleDate(final ResultSet rs, final int columnIndex) throws SQLException {
        final java.sql.Date date = rs.getDate(columnIndex);
        String value = null;
        if (date != null) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            value = dateFormat.format(date);
        }
        return value;
    }
    
    private String handleTime(final Time time) {
        return (time == null) ? null : time.toString();
    }
    
    private String handleTimestamp(final Timestamp timestamp) {
        final SimpleDateFormat timeFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        return (timestamp == null) ? null : timeFormat.format(timestamp);
    }
    
    private String getColumnValue(final ResultSet rs, final int colType, final int colIndex) throws SQLException, IOException {
        String value = "";
        switch (colType) {
            case -7:
            case 2000: {
                value = this.handleObject(rs.getObject(colIndex));
                break;
            }
            case 16: {
                final boolean b = rs.getBoolean(colIndex);
                value = Boolean.valueOf(b).toString();
                break;
            }
            case 2005:
            case 2011: {
                final Clob c = rs.getClob(colIndex);
                if (c != null) {
                    value = read(c);
                    break;
                }
                break;
            }
            case -5: {
                value = this.handleLong(rs, colIndex);
                break;
            }
            case 2:
            case 3:
            case 6:
            case 7:
            case 8: {
                value = this.handleBigDecimal(rs.getBigDecimal(colIndex));
                break;
            }
            case -6:
            case 4:
            case 5: {
                value = this.handleInteger(rs, colIndex);
                break;
            }
            case 91: {
                value = this.handleDate(rs, colIndex);
                break;
            }
            case 92: {
                value = this.handleTime(rs.getTime(colIndex));
                break;
            }
            case 93: {
                value = this.handleTimestamp(rs.getTimestamp(colIndex));
                break;
            }
            case -16:
            case -15:
            case -9:
            case -1:
            case 1:
            case 12: {
                value = rs.getString(colIndex);
                break;
            }
            default: {
                value = "";
                break;
            }
        }
        if (value == null) {
            value = "";
        }
        return value;
    }
    
    private static String read(final Clob c) throws SQLException, IOException {
        final StringBuilder sb = new StringBuilder((int)c.length());
        final Reader r = c.getCharacterStream();
        final char[] cbuf = new char[2048];
        int n;
        while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            sb.append(cbuf, 0, n);
        }
        return sb.toString();
    }
}
