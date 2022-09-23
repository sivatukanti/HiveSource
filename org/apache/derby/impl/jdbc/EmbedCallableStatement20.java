// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.types.DataValueDescriptor;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.StringReader;
import org.apache.derby.iapi.types.StringDataValue;
import java.io.Reader;
import java.io.InputStream;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Blob;
import java.sql.Array;
import java.sql.Ref;
import java.util.Map;
import java.sql.SQLException;

public abstract class EmbedCallableStatement20 extends EmbedCallableStatement
{
    public EmbedCallableStatement20(final EmbedConnection embedConnection, final String s, final int n, final int n2, final int n3) throws SQLException {
        super(embedConnection, s, n, n2, n3);
    }
    
    public Object getObject(final int n, final Map map) throws SQLException {
        this.checkStatus();
        if (map == null) {
            throw Util.generateCsSQLException("XJ081.S", map, "map", "java.sql.CallableStatement.getObject");
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
    
    public void setRef(final int n, final Ref ref) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setArray(final int n, final Array array) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void registerOutParameter(final String s, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void registerOutParameter(final String s, final int n, final String s2) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void registerOutParameter(final String s, final int n, final int n2) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Ref getRef(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Blob getBlob(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Clob getClob(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Array getArray(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setNull(final String s, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setNull(final String s, final int n, final String s2) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setBoolean(final String s, final boolean b) throws SQLException {
        throw Util.notImplemented();
    }
    
    public boolean getBoolean(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setByte(final String s, final byte b) throws SQLException {
        throw Util.notImplemented();
    }
    
    public byte getByte(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setShort(final String s, final short n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public short getShort(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setInt(final String s, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public int getInt(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setLong(final String s, final long n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public long getLong(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setFloat(final String s, final float n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public float getFloat(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setDouble(final String s, final double n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public double getDouble(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setBigDecimal(final String s, final BigDecimal bigDecimal) throws SQLException {
        throw Util.notImplemented();
    }
    
    public BigDecimal getBigDecimal(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setString(final String s, final String s2) throws SQLException {
        throw Util.notImplemented();
    }
    
    public String getString(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setBytes(final String s, final byte[] array) throws SQLException {
        throw Util.notImplemented();
    }
    
    public byte[] getBytes(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setDate(final String s, final Date date) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setDate(final String s, final Date date, final Calendar calendar) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Date getDate(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setTime(final String s, final Time time) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Time getTime(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setTime(final String s, final Time time, final Calendar calendar) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setTimestamp(final String s, final Timestamp timestamp, final Calendar calendar) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Timestamp getTimestamp(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setObject(final String s, final Object o, final int n, final int n2) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Object getObject(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Object getObject(final String s, final Map map) throws SQLException {
        this.checkStatus();
        if (map == null) {
            throw Util.generateCsSQLException("XJ081.S", map, "map", "java.sql.CallableStatement.getObject");
        }
        if (!map.isEmpty()) {
            throw Util.notImplemented();
        }
        return this.getObject(s);
    }
    
    public void setObject(final String s, final Object o, final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public void setObject(final String s, final Object o) throws SQLException {
        throw Util.notImplemented();
    }
    
    public Reader getCharacterStream(final int i) throws SQLException {
        this.checkStatus();
        switch (this.getParms().getParameterMode(i)) {
            case 0:
            case 1: {
                throw this.newSQLException("XCL26.S", Integer.toString(i));
            }
            default: {
                Reader reader = null;
                final int parameterJDBCType = this.getParameterJDBCType(i);
                Label_0353: {
                    switch (parameterJDBCType) {
                        case -1:
                        case 1:
                        case 12:
                        case 2005: {
                            boolean b = false;
                            final Object connectionSynchronization = this.getConnectionSynchronization();
                            synchronized (connectionSynchronization) {
                                try {
                                    final StringDataValue stringDataValue = (StringDataValue)this.getParms().getParameterForGet(i - 1);
                                    if (!stringDataValue.isNull()) {
                                        b = true;
                                        this.setupContextStack();
                                        if (stringDataValue.hasStream()) {
                                            reader = new UTF8Reader(stringDataValue.getStreamWithDescriptor(), this, connectionSynchronization);
                                        }
                                        else {
                                            reader = new StringReader(stringDataValue.getString());
                                        }
                                    }
                                }
                                catch (Throwable t) {
                                    throw EmbedResultSet.noStateChangeException(t);
                                }
                                finally {
                                    if (b) {
                                        this.restoreContextStack();
                                    }
                                }
                            }
                            break Label_0353;
                        }
                        case -4:
                        case -3:
                        case -2:
                        case 2004: {
                            try {
                                final InputStream binaryStream = this.getBinaryStream(i);
                                if (binaryStream != null) {
                                    reader = new InputStreamReader(binaryStream, "UTF-16BE");
                                }
                                break Label_0353;
                            }
                            catch (UnsupportedEncodingException ex) {
                                throw this.newSQLException(ex.getMessage());
                            }
                            break;
                        }
                    }
                    throw this.newSQLException("22005", "java.io.Reader", Util.typeName(parameterJDBCType));
                }
                this.wasNull = (reader == null);
                return reader;
            }
        }
    }
    
    private InputStream getBinaryStream(final int n) throws SQLException {
        final int parameterJDBCType = this.getParameterJDBCType(n);
        switch (parameterJDBCType) {
            case -4:
            case -3:
            case -2:
            case 2004: {
                boolean b = false;
                synchronized (this.getConnectionSynchronization()) {
                    try {
                        final DataValueDescriptor parameterForGet = this.getParms().getParameterForGet(n - 1);
                        this.wasNull = parameterForGet.isNull();
                        if (this.wasNull) {
                            return null;
                        }
                        b = true;
                        this.setupContextStack();
                        InputStream inputStream;
                        if (parameterForGet.hasStream()) {
                            inputStream = new BinaryToRawStream(parameterForGet.getStream(), parameterForGet);
                        }
                        else {
                            inputStream = new ByteArrayInputStream(parameterForGet.getBytes());
                        }
                        return inputStream;
                    }
                    catch (Throwable t) {
                        throw EmbedResultSet.noStateChangeException(t);
                    }
                    finally {
                        if (b) {
                            this.restoreContextStack();
                        }
                    }
                }
                break;
            }
            default: {
                throw this.newSQLException("22005", "java.io.InputStream", Util.typeName(parameterJDBCType));
            }
        }
    }
}
