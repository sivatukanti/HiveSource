// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.sql.execute.NoPutResultSet;
import java.sql.Connection;
import java.io.IOException;
import org.apache.derby.iapi.sql.execute.ExecCursorTableReference;
import org.apache.derby.iapi.sql.PreparedStatement;
import org.apache.derby.iapi.sql.execute.CursorActivation;
import org.apache.derby.iapi.sql.ParameterValueSet;
import org.apache.derby.iapi.util.IdUtil;
import java.sql.Clob;
import java.sql.Blob;
import org.apache.derby.iapi.types.UserDataValue;
import org.apache.derby.iapi.types.VariableSizeDataValue;
import org.apache.derby.iapi.types.ReaderToUTF8Stream;
import org.apache.derby.iapi.types.RawToBinaryFormatStream;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.impl.sql.execute.ScrollInsensitiveResultSet;
import java.sql.ResultSetMetaData;
import java.net.URL;
import org.apache.derby.iapi.services.io.CloseFilterInputStream;
import org.apache.derby.iapi.services.io.LimitInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;
import org.apache.derby.iapi.types.StringDataValue;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.sql.Time;
import java.util.Calendar;
import java.sql.Date;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.sql.conn.StatementContext;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.util.InterruptStatus;
import java.util.Arrays;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Statement;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.Activation;
import java.sql.SQLWarning;
import org.apache.derby.iapi.sql.ResultSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import org.apache.derby.iapi.jdbc.EngineResultSet;

public class EmbedResultSet extends ConnectionChild implements EngineResultSet, Comparable
{
    public static long fetchedRowBase;
    protected static final int FIRST = 1;
    protected static final int NEXT = 2;
    protected static final int LAST = 3;
    protected static final int PREVIOUS = 4;
    protected static final int BEFOREFIRST = 5;
    protected static final int AFTERLAST = 6;
    protected static final int ABSOLUTE = 7;
    protected static final int RELATIVE = 8;
    private ExecRow currentRow;
    protected boolean wasNull;
    boolean isClosed;
    private boolean isOnInsertRow;
    private Object currentStream;
    private org.apache.derby.iapi.sql.ResultSet theResults;
    private boolean forMetaData;
    private SQLWarning topWarning;
    Activation singleUseActivation;
    final int order;
    private final ResultDescription resultDescription;
    private long maxRows;
    private final int maxFieldSize;
    private long NumberofFetchedRows;
    private final EmbedStatement stmt;
    private EmbedStatement owningStmt;
    private Statement applicationStmt;
    private final long timeoutMillis;
    private final boolean isAtomic;
    private final int concurrencyOfThisResultSet;
    private final ExecRow updateRow;
    private boolean[] columnGotUpdated;
    private boolean currentRowHasBeenUpdated;
    private int fetchDirection;
    private int fetchSize;
    private boolean[] columnUsedFlags;
    
    public EmbedResultSet(final EmbedConnection embedConnection, final org.apache.derby.iapi.sql.ResultSet theResults, final boolean forMetaData, final EmbedStatement applicationStmt, final boolean isAtomic) throws SQLException {
        super(embedConnection);
        this.NumberofFetchedRows = EmbedResultSet.fetchedRowBase;
        this.theResults = theResults;
        this.forMetaData = forMetaData;
        if (forMetaData) {
            this.singleUseActivation = theResults.getActivation();
        }
        this.owningStmt = applicationStmt;
        this.stmt = applicationStmt;
        this.applicationStmt = applicationStmt;
        this.timeoutMillis = ((applicationStmt == null) ? 0L : applicationStmt.timeoutMillis);
        this.isAtomic = isAtomic;
        if (applicationStmt == null) {
            this.concurrencyOfThisResultSet = 1007;
        }
        else if (applicationStmt.resultSetConcurrency == 1007) {
            this.concurrencyOfThisResultSet = 1007;
        }
        else if (!this.isForUpdate()) {
            this.concurrencyOfThisResultSet = 1007;
            this.addWarning(StandardException.newWarning("01J06"));
        }
        else {
            this.concurrencyOfThisResultSet = 1008;
        }
        this.resultDescription = this.theResults.getResultDescription();
        if (this.concurrencyOfThisResultSet == 1008) {
            final int columnCount = this.resultDescription.getColumnCount();
            final ExecutionFactory executionFactory = embedConnection.getLanguageConnection().getLanguageConnectionFactory().getExecutionFactory();
            try {
                this.columnGotUpdated = new boolean[columnCount];
                this.updateRow = executionFactory.getValueRow(columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                    this.updateRow.setColumn(i, this.resultDescription.getColumnDescriptor(i).getType().getNull());
                }
                this.initializeUpdateRowModifiers();
            }
            catch (StandardException ex) {
                throw noStateChangeException(ex);
            }
        }
        else {
            this.updateRow = null;
        }
        if (applicationStmt != null) {
            if (applicationStmt.resultSetType == 1003) {
                this.maxRows = applicationStmt.maxRows;
            }
            this.maxFieldSize = applicationStmt.MaxFieldSize;
        }
        else {
            this.maxFieldSize = 0;
        }
        this.order = embedConnection.getResultSetOrderId();
    }
    
    private void checkNotOnInsertRow() throws SQLException {
        if (this.isOnInsertRow) {
            throw this.newSQLException("24000");
        }
    }
    
    protected final void checkOnRow() throws SQLException {
        if (this.currentRow == null) {
            throw this.newSQLException("24000");
        }
    }
    
    private void initializeUpdateRowModifiers() {
        this.currentRowHasBeenUpdated = false;
        Arrays.fill(this.columnGotUpdated, false);
    }
    
    final int getColumnType(final int value) throws SQLException {
        if (!this.isOnInsertRow) {
            this.checkOnRow();
        }
        if (value < 1 || value > this.resultDescription.getColumnCount()) {
            throw this.newSQLException("S0022", new Integer(value));
        }
        return this.resultDescription.getColumnDescriptor(value).getType().getJDBCTypeId();
    }
    
    public boolean next() throws SQLException {
        if (this.maxRows != 0L) {
            ++this.NumberofFetchedRows;
            if (this.NumberofFetchedRows > this.maxRows) {
                this.closeCurrentStream();
                return false;
            }
        }
        return this.movePosition(2, 0, "next");
    }
    
    protected boolean movePosition(final int n, final String s) throws SQLException {
        return this.movePosition(n, 0, s);
    }
    
    protected boolean movePosition(final int n, final int n2, final String s) throws SQLException {
        this.closeCurrentStream();
        this.checkExecIfClosed(s);
        if (this.isOnInsertRow) {
            this.moveToCurrentRow();
        }
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            try {
                final LanguageConnectionContext languageConnection = this.getEmbedConnection().getLanguageConnection();
                ExecRow execRow = null;
                try {
                    final StatementContext pushStatementContext = languageConnection.pushStatementContext(this.isAtomic, this.concurrencyOfThisResultSet == 1007, this.getSQLText(), this.getParameterValueSet(), false, this.timeoutMillis);
                    switch (n) {
                        case 5: {
                            execRow = this.theResults.setBeforeFirstRow();
                            break;
                        }
                        case 1: {
                            execRow = this.theResults.getFirstRow();
                            break;
                        }
                        case 2: {
                            execRow = this.theResults.getNextRow();
                            break;
                        }
                        case 3: {
                            execRow = this.theResults.getLastRow();
                            break;
                        }
                        case 6: {
                            execRow = this.theResults.setAfterLastRow();
                            break;
                        }
                        case 4: {
                            execRow = this.theResults.getPreviousRow();
                            break;
                        }
                        case 7: {
                            execRow = this.theResults.getAbsoluteRow(n2);
                            break;
                        }
                        case 8: {
                            execRow = this.theResults.getRelativeRow(n2);
                            break;
                        }
                        default: {
                            execRow = null;
                            break;
                        }
                    }
                    languageConnection.popStatementContext(pushStatementContext, null);
                    InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
                }
                catch (Throwable t) {
                    throw this.closeOnTransactionError(t);
                }
                final SQLWarning warnings = this.theResults.getWarnings();
                if (warnings != null) {
                    this.addWarning(warnings);
                }
                final ExecRow currentRow = execRow;
                this.currentRow = currentRow;
                final boolean b = currentRow != null;
                if (!b && n == 2) {
                    if (!this.forMetaData || languageConnection.getActivationCount() <= 1) {
                        if (this.owningStmt != null && this.owningStmt.getResultSetType() == 1003) {
                            this.owningStmt.resultSetClosing(this);
                        }
                    }
                }
                if (this.columnUsedFlags != null) {
                    Arrays.fill(this.columnUsedFlags, false);
                }
                if (this.columnGotUpdated != null && this.currentRowHasBeenUpdated) {
                    this.initializeUpdateRowModifiers();
                }
                return b;
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public void close() throws SQLException {
        if (this.isClosed) {
            return;
        }
        this.closeCurrentStream();
        synchronized (this.getConnectionSynchronization()) {
            try {
                this.setupContextStack();
            }
            catch (SQLException ex) {
                return;
            }
            try {
                final LanguageConnectionContext languageConnection = this.getEmbedConnection().getLanguageConnection();
                try {
                    this.theResults.close();
                    if (this.singleUseActivation != null) {
                        this.singleUseActivation.close();
                        this.singleUseActivation = null;
                    }
                    InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
                }
                catch (Throwable t) {
                    throw this.handleException(t);
                }
                if (this.forMetaData) {
                    if (languageConnection.getActivationCount() <= 1) {
                        if (this.owningStmt != null) {
                            this.owningStmt.resultSetClosing(this);
                        }
                    }
                }
                else if (this.owningStmt != null) {
                    this.owningStmt.resultSetClosing(this);
                }
            }
            finally {
                this.markClosed();
                this.restoreContextStack();
            }
            this.currentRow = null;
        }
    }
    
    private void markClosed() {
        if (this.isClosed) {
            return;
        }
        this.isClosed = true;
        if (this.stmt != null) {
            this.stmt.closeMeOnCompletion();
        }
        if (this.owningStmt != null && this.owningStmt != this.stmt) {
            this.owningStmt.closeMeOnCompletion();
        }
    }
    
    public final boolean wasNull() throws SQLException {
        this.checkIfClosed("wasNull");
        return this.wasNull;
    }
    
    public final String getString(final int n) throws SQLException {
        this.checkIfClosed("getString");
        final int columnType = this.getColumnType(n);
        if (columnType == 2004 || columnType == 2005) {
            this.checkLOBMultiCall(n);
        }
        try {
            final DataValueDescriptor column = this.getColumn(n);
            if (this.wasNull = column.isNull()) {
                return null;
            }
            String s = column.getString();
            if (this.maxFieldSize > 0 && isMaxFieldSizeType(columnType) && s.length() > this.maxFieldSize) {
                s = s.substring(0, this.maxFieldSize);
            }
            return s;
        }
        catch (Throwable t) {
            throw noStateChangeException(t);
        }
    }
    
    public final boolean getBoolean(final int n) throws SQLException {
        this.checkIfClosed("getBoolean");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            return !null && column.getBoolean();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final byte getByte(final int n) throws SQLException {
        this.checkIfClosed("getByte");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return 0;
            }
            return column.getByte();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final short getShort(final int n) throws SQLException {
        this.checkIfClosed("getShort");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return 0;
            }
            return column.getShort();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final int getInt(final int n) throws SQLException {
        this.checkIfClosed("getInt");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return 0;
            }
            return column.getInt();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final long getLong(final int n) throws SQLException {
        this.checkIfClosed("getLong");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return 0L;
            }
            return column.getLong();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final float getFloat(final int n) throws SQLException {
        this.checkIfClosed("getFloat");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return 0.0f;
            }
            return column.getFloat();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final double getDouble(final int n) throws SQLException {
        this.checkIfClosed("getDouble");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return 0.0;
            }
            return column.getDouble();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final byte[] getBytes(final int n) throws SQLException {
        this.checkIfClosed("getBytes");
        final int columnType = this.getColumnType(n);
        if (columnType == 2004) {
            this.checkLOBMultiCall(n);
        }
        try {
            final DataValueDescriptor column = this.getColumn(n);
            if (this.wasNull = column.isNull()) {
                return null;
            }
            byte[] bytes = column.getBytes();
            if (this.maxFieldSize > 0 && isMaxFieldSizeType(columnType) && bytes.length > this.maxFieldSize) {
                final byte[] array = new byte[this.maxFieldSize];
                System.arraycopy(bytes, 0, array, 0, this.maxFieldSize);
                bytes = array;
            }
            return bytes;
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final Date getDate(final int n) throws SQLException {
        return this.getDate(n, null);
    }
    
    public final Time getTime(final int n) throws SQLException {
        return this.getTime(n, null);
    }
    
    public final Timestamp getTimestamp(final int n) throws SQLException {
        return this.getTimestamp(n, null);
    }
    
    public Date getDate(final int n, Calendar cal) throws SQLException {
        this.checkIfClosed("getDate");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return null;
            }
            if (cal == null) {
                cal = this.getCal();
            }
            return column.getDate(cal);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public Date getDate(final String s, final Calendar calendar) throws SQLException {
        this.checkIfClosed("getDate");
        return this.getDate(this.findColumnName(s), calendar);
    }
    
    public Time getTime(final int n, Calendar cal) throws SQLException {
        this.checkIfClosed("getTime");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return null;
            }
            if (cal == null) {
                cal = this.getCal();
            }
            return column.getTime(cal);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public Time getTime(final String s, final Calendar calendar) throws SQLException {
        this.checkIfClosed("getTime");
        return this.getTime(this.findColumnName(s), calendar);
    }
    
    public Timestamp getTimestamp(final String s, final Calendar calendar) throws SQLException {
        this.checkIfClosed("getTimestamp");
        return this.getTimestamp(this.findColumnName(s), calendar);
    }
    
    public Timestamp getTimestamp(final int n, Calendar cal) throws SQLException {
        this.checkIfClosed("getTimestamp");
        try {
            final DataValueDescriptor column = this.getColumn(n);
            final boolean null = column.isNull();
            this.wasNull = null;
            if (null) {
                return null;
            }
            if (cal == null) {
                cal = this.getCal();
            }
            return column.getTimestamp(cal);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public final Reader getCharacterStream(final int n) throws SQLException {
        this.checkIfClosed("getCharacterStream");
        int maxFieldSize = 0;
        Label_0160: {
            switch (this.getColumnType(n)) {
                case -1:
                case 1:
                case 12: {
                    maxFieldSize = this.maxFieldSize;
                    break Label_0160;
                }
                case 2005: {
                    maxFieldSize = 0;
                    break Label_0160;
                }
                case -4:
                case -3:
                case -2:
                case 2004: {
                    try {
                        final InputStream binaryStream = this.getBinaryStream(n);
                        if (binaryStream == null) {
                            return null;
                        }
                        return (Reader)(this.currentStream = new InputStreamReader(binaryStream, "UTF-16BE"));
                    }
                    catch (UnsupportedEncodingException ex) {
                        throw new SQLException(ex.getMessage());
                    }
                    break;
                }
            }
            throw this.dataTypeConversion("java.io.Reader", n);
        }
        final Object connectionSynchronization = this.getConnectionSynchronization();
        synchronized (connectionSynchronization) {
            boolean b = false;
            try {
                this.useStreamOrLOB(n);
                final StringDataValue stringDataValue = (StringDataValue)this.getColumn(n);
                final boolean null = stringDataValue.isNull();
                this.wasNull = null;
                if (null) {
                    return null;
                }
                b = true;
                this.setupContextStack();
                Reader currentStream;
                if (stringDataValue.hasStream()) {
                    CharacterStreamDescriptor characterStreamDescriptor = stringDataValue.getStreamWithDescriptor();
                    if (maxFieldSize > 0) {
                        characterStreamDescriptor = new CharacterStreamDescriptor.Builder().copyState(characterStreamDescriptor).maxCharLength(maxFieldSize).build();
                    }
                    currentStream = new UTF8Reader(characterStreamDescriptor, this, connectionSynchronization);
                }
                else {
                    String s = stringDataValue.getString();
                    if (maxFieldSize > 0 && s.length() > maxFieldSize) {
                        s = s.substring(0, maxFieldSize);
                    }
                    currentStream = new StringReader(s);
                }
                return (Reader)(this.currentStream = currentStream);
            }
            catch (Throwable t) {
                throw noStateChangeException(t);
            }
            finally {
                if (b) {
                    this.restoreContextStack();
                }
            }
        }
    }
    
    public final InputStream getAsciiStream(final int n) throws SQLException {
        this.checkIfClosed("getAsciiStream");
        switch (this.getColumnType(n)) {
            case -1:
            case 1:
            case 12:
            case 2005: {
                final Reader characterStream = this.getCharacterStream(n);
                if (characterStream == null) {
                    return null;
                }
                return new ReaderToAscii(characterStream);
            }
            case -4:
            case -3:
            case -2:
            case 2004: {
                return this.getBinaryStream(n);
            }
            default: {
                throw this.dataTypeConversion("java.io.InputStream(ASCII)", n);
            }
        }
    }
    
    public final InputStream getBinaryStream(final int n) throws SQLException {
        this.checkIfClosed("getBinaryStream");
        int maxFieldSize = 0;
        switch (this.getColumnType(n)) {
            case -4:
            case -3:
            case -2: {
                maxFieldSize = this.maxFieldSize;
                break;
            }
            case 2004: {
                maxFieldSize = 0;
                break;
            }
            default: {
                throw this.dataTypeConversion("java.io.InputStream", n);
            }
        }
        final Object connectionSynchronization = this.getConnectionSynchronization();
        synchronized (connectionSynchronization) {
            boolean b = false;
            try {
                this.useStreamOrLOB(n);
                final DataValueDescriptor column = this.getColumn(n);
                final boolean null = column.isNull();
                this.wasNull = null;
                if (null) {
                    return null;
                }
                b = true;
                this.setupContextStack();
                InputStream inputStream;
                if (column.hasStream()) {
                    inputStream = new BinaryToRawStream(column.getStream(), column);
                }
                else {
                    inputStream = new ByteArrayInputStream(column.getBytes());
                }
                if (maxFieldSize > 0) {
                    final LimitInputStream limitInputStream = new LimitInputStream(inputStream);
                    limitInputStream.setLimit(maxFieldSize);
                    inputStream = limitInputStream;
                }
                return (InputStream)(this.currentStream = new CloseFilterInputStream(inputStream));
            }
            catch (Throwable t) {
                throw noStateChangeException(t);
            }
            finally {
                if (b) {
                    this.restoreContextStack();
                }
            }
        }
    }
    
    public final String getString(final String s) throws SQLException {
        this.checkIfClosed("getString");
        return this.getString(this.findColumnName(s));
    }
    
    public final boolean getBoolean(final String s) throws SQLException {
        this.checkIfClosed("getBoolean");
        return this.getBoolean(this.findColumnName(s));
    }
    
    public final byte getByte(final String s) throws SQLException {
        this.checkIfClosed("getByte");
        return this.getByte(this.findColumnName(s));
    }
    
    public final short getShort(final String s) throws SQLException {
        this.checkIfClosed("getShort");
        return this.getShort(this.findColumnName(s));
    }
    
    public final int getInt(final String s) throws SQLException {
        this.checkIfClosed("getInt");
        return this.getInt(this.findColumnName(s));
    }
    
    public final long getLong(final String s) throws SQLException {
        this.checkIfClosed("getLong");
        return this.getLong(this.findColumnName(s));
    }
    
    public final float getFloat(final String s) throws SQLException {
        this.checkIfClosed("getFloat");
        return this.getFloat(this.findColumnName(s));
    }
    
    public final double getDouble(final String s) throws SQLException {
        this.checkIfClosed("getDouble");
        return this.getDouble(this.findColumnName(s));
    }
    
    public final byte[] getBytes(final String s) throws SQLException {
        this.checkIfClosed("getBytes");
        return this.getBytes(this.findColumnName(s));
    }
    
    public final Date getDate(final String s) throws SQLException {
        this.checkIfClosed("getDate");
        return this.getDate(this.findColumnName(s));
    }
    
    public final Time getTime(final String s) throws SQLException {
        this.checkIfClosed("getTime");
        return this.getTime(this.findColumnName(s));
    }
    
    public final Timestamp getTimestamp(final String s) throws SQLException {
        this.checkIfClosed("getTimestamp");
        return this.getTimestamp(this.findColumnName(s));
    }
    
    public final Reader getCharacterStream(final String s) throws SQLException {
        this.checkIfClosed("getCharacterStream");
        return this.getCharacterStream(this.findColumnName(s));
    }
    
    public final InputStream getAsciiStream(final String s) throws SQLException {
        this.checkIfClosed("getAsciiStream");
        return this.getAsciiStream(this.findColumnName(s));
    }
    
    public final InputStream getBinaryStream(final String s) throws SQLException {
        this.checkIfClosed("getBinaryStream");
        return this.getBinaryStream(this.findColumnName(s));
    }
    
    public URL getURL(final int n) throws SQLException {
        throw Util.notImplemented();
    }
    
    public URL getURL(final String s) throws SQLException {
        throw Util.notImplemented();
    }
    
    public final SQLWarning getWarnings() throws SQLException {
        this.checkIfClosed("getWarnings");
        return this.topWarning;
    }
    
    public final void clearWarnings() throws SQLException {
        this.checkIfClosed("clearWarnings");
        this.topWarning = null;
    }
    
    public final String getCursorName() throws SQLException {
        this.checkIfClosed("getCursorName");
        return this.theResults.getCursorName();
    }
    
    public final ResultSetMetaData getMetaData() throws SQLException {
        this.checkIfClosed("getMetaData");
        ResultSetMetaData metaData = this.resultDescription.getMetaData();
        if (metaData == null) {
            metaData = this.factory.newEmbedResultSetMetaData(this.resultDescription.getColumnInfo());
            this.resultDescription.setMetaData(metaData);
        }
        return metaData;
    }
    
    public final int getHoldability() throws SQLException {
        this.checkIfClosed("getHoldability");
        if (this.theResults.getActivation().getResultSetHoldability()) {
            return 1;
        }
        return 2;
    }
    
    public final Object getObject(final int n) throws SQLException {
        this.checkIfClosed("getObject");
        switch (this.getColumnType(n)) {
            case -1:
            case 1:
            case 12: {
                return this.getString(n);
            }
            case 2005: {
                return this.getClob(n);
            }
            case -4:
            case -3:
            case -2: {
                return this.getBytes(n);
            }
            case 2004: {
                return this.getBlob(n);
            }
            default: {
                try {
                    final DataValueDescriptor column = this.getColumn(n);
                    final boolean null = column.isNull();
                    this.wasNull = null;
                    if (null) {
                        return null;
                    }
                    return column.getObject();
                }
                catch (StandardException ex) {
                    throw noStateChangeException(ex);
                }
                break;
            }
        }
    }
    
    public final Object getObject(final String s) throws SQLException {
        this.checkIfClosed("getObject");
        return this.getObject(this.findColumnName(s));
    }
    
    public final int findColumn(final String s) throws SQLException {
        this.checkIfClosed("findColumn");
        return this.findColumnName(s);
    }
    
    public final Statement getStatement() throws SQLException {
        this.checkIfClosed("getStatement");
        return this.applicationStmt;
    }
    
    public final void setApplicationStatement(final Statement applicationStmt) {
        this.applicationStmt = applicationStmt;
    }
    
    public boolean isBeforeFirst() throws SQLException {
        return this.checkRowPosition(101, "isBeforeFirst");
    }
    
    public boolean isAfterLast() throws SQLException {
        return this.checkRowPosition(104, "isAfterLast");
    }
    
    public boolean isFirst() throws SQLException {
        return this.checkRowPosition(102, "isFirst");
    }
    
    public boolean isLast() throws SQLException {
        return this.checkRowPosition(103, "isLast");
    }
    
    public void beforeFirst() throws SQLException {
        this.checkScrollCursor("beforeFirst()");
        this.movePosition(5, "beforeFirst");
    }
    
    public void afterLast() throws SQLException {
        this.checkScrollCursor("afterLast()");
        this.movePosition(6, "afterLast");
    }
    
    public boolean first() throws SQLException {
        this.checkScrollCursor("first()");
        return this.movePosition(1, "first");
    }
    
    public boolean last() throws SQLException {
        this.checkScrollCursor("last()");
        return this.movePosition(3, "last");
    }
    
    public int getRow() throws SQLException {
        this.checkScrollCursor("getRow()");
        return this.theResults.getRowNumber();
    }
    
    public boolean absolute(final int n) throws SQLException {
        this.checkScrollCursor("absolute()");
        return this.movePosition(7, n, "absolute");
    }
    
    public boolean relative(final int n) throws SQLException {
        this.checkScrollCursor("relative()");
        return this.movePosition(8, n, "relative");
    }
    
    public boolean previous() throws SQLException {
        this.checkScrollCursor("previous()");
        return this.movePosition(4, "previous");
    }
    
    public void setFetchDirection(final int fetchDirection) throws SQLException {
        this.checkScrollCursor("setFetchDirection()");
        this.fetchDirection = fetchDirection;
    }
    
    public int getFetchDirection() throws SQLException {
        this.checkIfClosed("getFetchDirection");
        if (this.fetchDirection == 0) {
            return this.stmt.getFetchDirection();
        }
        return this.fetchDirection;
    }
    
    public void setFetchSize(final int n) throws SQLException {
        this.checkIfClosed("setFetchSize");
        if (n < 0 || (this.stmt.getMaxRows() != 0 && n > this.stmt.getMaxRows())) {
            throw Util.generateCsSQLException("XJ062.S", new Integer(n));
        }
        if (n > 0) {
            this.fetchSize = n;
        }
    }
    
    public int getFetchSize() throws SQLException {
        this.checkIfClosed("getFetchSize");
        if (this.fetchSize == 0) {
            return this.stmt.getFetchSize();
        }
        return this.fetchSize;
    }
    
    public int getType() throws SQLException {
        this.checkIfClosed("getType");
        return this.stmt.getResultSetType();
    }
    
    public int getConcurrency() throws SQLException {
        this.checkIfClosed("getConcurrency");
        return this.concurrencyOfThisResultSet;
    }
    
    public boolean rowUpdated() throws SQLException {
        this.checkIfClosed("rowUpdated");
        this.checkNotOnInsertRow();
        this.checkOnRow();
        boolean updated = false;
        try {
            if (this.isForUpdate() && this.getType() == 1004) {
                updated = ((ScrollInsensitiveResultSet)this.theResults).isUpdated();
            }
        }
        catch (Throwable t) {
            this.handleException(t);
        }
        return updated;
    }
    
    public boolean rowInserted() throws SQLException {
        this.checkIfClosed("rowInserted");
        this.checkNotOnInsertRow();
        this.checkOnRow();
        return false;
    }
    
    public boolean rowDeleted() throws SQLException {
        this.checkIfClosed("rowDeleted");
        this.checkNotOnInsertRow();
        this.checkOnRow();
        boolean deleted = false;
        try {
            if (this.isForUpdate() && this.getType() == 1004) {
                deleted = ((ScrollInsensitiveResultSet)this.theResults).isDeleted();
            }
        }
        catch (Throwable t) {
            this.handleException(t);
        }
        return deleted;
    }
    
    protected void checksBeforeUpdateXXX(final String s, final int value) throws SQLException {
        this.checksBeforeUpdateOrDelete(s, value);
        final ResultDescription resultDescription = this.theResults.getResultDescription();
        if (value < 1 || value > resultDescription.getColumnCount()) {
            throw Util.generateCsSQLException("XCL14.S", new Integer(value), String.valueOf(resultDescription.getColumnCount()));
        }
        if (resultDescription.getColumnDescriptor(value).getSourceTableName() == null) {
            throw Util.generateCsSQLException("XJ084.U", s);
        }
        if (!this.getMetaData().isWritable(value)) {
            throw Util.generateCsSQLException("42X31", this.theResults.getResultDescription().getColumnDescriptor(value).getName(), this.getCursorName());
        }
    }
    
    protected void checksBeforeUpdateOrDelete(final String s, final int n) throws SQLException {
        this.checkIfClosed(s);
        this.checkUpdatableCursor(s);
        if (!this.isOnInsertRow) {
            this.checkOnRow();
        }
    }
    
    protected DataValueDescriptor getDVDforColumnToBeUpdated(final int n, final String s) throws StandardException, SQLException {
        this.checksBeforeUpdateXXX(s, n);
        this.columnGotUpdated[n - 1] = true;
        this.currentRowHasBeenUpdated = true;
        return this.updateRow.getColumn(n);
    }
    
    protected void checksBeforeInsert() throws SQLException {
        this.checkIfClosed("insertRow");
        this.checkUpdatableCursor("insertRow");
        if (!this.isOnInsertRow) {
            throw this.newSQLException("XJ086.S");
        }
    }
    
    private void checksBeforeUpdateAsciiStream(final int n) throws SQLException {
        this.checksBeforeUpdateXXX("updateAsciiStream", n);
        if (!DataTypeDescriptor.isAsciiStreamAssignable(this.getColumnType(n))) {
            throw this.dataTypeConversion(n, "java.io.InputStream");
        }
    }
    
    private void checksBeforeUpdateBinaryStream(final int n) throws SQLException {
        this.checksBeforeUpdateXXX("updateBinaryStream", n);
        if (!DataTypeDescriptor.isBinaryStreamAssignable(this.getColumnType(n))) {
            throw this.dataTypeConversion(n, "java.io.InputStream");
        }
    }
    
    private void checksBeforeUpdateCharacterStream(final int n) throws SQLException {
        this.checksBeforeUpdateXXX("updateCharacterStream", n);
        if (!DataTypeDescriptor.isCharacterStreamAssignable(this.getColumnType(n))) {
            throw this.dataTypeConversion(n, "java.io.Reader");
        }
    }
    
    public void updateNull(final int n) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateNull").setToNull();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateBoolean(final int n, final boolean value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateBoolean").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateByte(final int n, final byte value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateByte").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateShort(final int n, final short value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateShort").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateInt(final int n, final int value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateInt").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateLong(final int n, final long value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateLong").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateFloat(final int n, final float value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateFloat").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateDouble(final int n, final double value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateDouble").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateString(final int n, final String value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateString").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateBytes(final int n, final byte[] value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateBytes").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateDate(final int n, final Date value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateDate").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateTime(final int n, final Time value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateTime").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateTimestamp(final int n, final Timestamp value) throws SQLException {
        try {
            this.getDVDforColumnToBeUpdated(n, "updateTimestamp").setValue(value);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateAsciiStream(final int n, final InputStream in, final long n2) throws SQLException {
        this.checksBeforeUpdateAsciiStream(n);
        Reader reader = null;
        if (in != null) {
            try {
                reader = new InputStreamReader(in, "ISO-8859-1");
            }
            catch (UnsupportedEncodingException ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        this.updateCharacterStreamInternal(n, reader, false, n2, "updateAsciiStream");
    }
    
    public void updateAsciiStream(final int n, final InputStream in) throws SQLException {
        this.checksBeforeUpdateAsciiStream(n);
        Reader reader = null;
        if (in != null) {
            try {
                reader = new InputStreamReader(in, "ISO-8859-1");
            }
            catch (UnsupportedEncodingException ex) {
                throw new SQLException(ex.getMessage());
            }
        }
        this.updateCharacterStreamInternal(n, reader, true, -1L, "updateAsciiStream");
    }
    
    public void updateBinaryStream(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.checksBeforeUpdateBinaryStream(n);
        if (inputStream == null) {
            this.updateNull(n);
            return;
        }
        this.updateBinaryStreamInternal(n, inputStream, false, n2, "updateBinaryStream");
    }
    
    public void updateBinaryStream(final int n, final InputStream inputStream) throws SQLException {
        this.checksBeforeUpdateBinaryStream(n);
        this.updateBinaryStreamInternal(n, inputStream, true, -1L, "updateBinaryStream");
    }
    
    private void updateBinaryStreamInternal(final int n, final InputStream inputStream, final boolean b, long n2, final String s) throws SQLException {
        RawToBinaryFormatStream rawToBinaryFormatStream;
        if (!b) {
            if (n2 < 0L) {
                throw this.newSQLException("XJ025.S");
            }
            if (n2 > 2147483647L) {
                throw this.newSQLException("22003", this.getColumnSQLType(n));
            }
            rawToBinaryFormatStream = new RawToBinaryFormatStream(inputStream, (int)n2);
        }
        else {
            n2 = -1L;
            rawToBinaryFormatStream = new RawToBinaryFormatStream(inputStream, this.getMaxColumnWidth(n), this.getColumnSQLType(n));
        }
        try {
            this.getDVDforColumnToBeUpdated(n, s).setValue(rawToBinaryFormatStream, (int)n2);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateCharacterStream(final int n, final Reader reader, final long n2) throws SQLException {
        this.checksBeforeUpdateCharacterStream(n);
        this.updateCharacterStreamInternal(n, reader, false, n2, "updateCharacterStream");
    }
    
    public void updateCharacterStream(final int n, final Reader reader) throws SQLException {
        this.checksBeforeUpdateCharacterStream(n);
        this.updateCharacterStreamInternal(n, reader, true, -1L, "updateCharacterStream");
    }
    
    private void updateCharacterStreamInternal(final int n, final Reader reader, final boolean b, final long n2, final String s) throws SQLException {
        try {
            if (reader == null) {
                this.updateNull(n);
                return;
            }
            final StringDataValue stringDataValue = (StringDataValue)this.getDVDforColumnToBeUpdated(n, s);
            stringDataValue.setStreamHeaderFormat(!this.getEmbedConnection().getDatabase().getDataDictionary().checkVersion(170, null));
            int n3 = -1;
            ReaderToUTF8Stream readerToUTF8Stream;
            if (!b) {
                if (n2 < 0L) {
                    throw this.newSQLException("XJ025.S");
                }
                if (n2 > 2147483647L) {
                    throw this.newSQLException("22003", this.getColumnSQLType(n));
                }
                n3 = (int)n2;
                int n4 = 0;
                if (this.getColumnType(n) == 2005) {
                    final int maxColumnWidth = this.getMaxColumnWidth(n);
                    if (n3 > maxColumnWidth) {
                        n4 = n3 - maxColumnWidth;
                        n3 = maxColumnWidth;
                    }
                }
                readerToUTF8Stream = new ReaderToUTF8Stream(reader, n3, n4, this.getColumnSQLType(n), stringDataValue.getStreamHeaderGenerator());
            }
            else {
                readerToUTF8Stream = new ReaderToUTF8Stream(reader, this.getMaxColumnWidth(n), this.getColumnSQLType(n), stringDataValue.getStreamHeaderGenerator());
            }
            stringDataValue.setValue(readerToUTF8Stream, n3);
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public void updateObject(final int n, final Object o, final int n2) throws SQLException {
        this.updateObject(n, o);
        this.adjustScale(n, n2);
    }
    
    protected void adjustScale(final int n, final int value) throws SQLException {
        final int columnType = this.getColumnType(n);
        if (columnType == 3 || columnType == 2) {
            if (value < 0) {
                throw this.newSQLException("XJ044.S", new Integer(value));
            }
            try {
                final DataValueDescriptor column = this.updateRow.getColumn(n);
                column.getLength();
                ((VariableSizeDataValue)column).setWidth(-1, value, false);
            }
            catch (StandardException ex) {
                throw noStateChangeException(ex);
            }
        }
    }
    
    public void updateObject(final int n, final Object value) throws SQLException {
        this.checksBeforeUpdateXXX("updateObject", n);
        if (this.getColumnType(n) == 2000) {
            try {
                ((UserDataValue)this.getDVDforColumnToBeUpdated(n, "updateObject")).setValue(value);
                return;
            }
            catch (StandardException ex) {
                throw noStateChangeException(ex);
            }
        }
        if (value == null) {
            this.updateNull(n);
            return;
        }
        if (value instanceof String) {
            this.updateString(n, (String)value);
            return;
        }
        if (value instanceof Boolean) {
            this.updateBoolean(n, (boolean)value);
            return;
        }
        if (value instanceof Short) {
            this.updateShort(n, (short)value);
            return;
        }
        if (value instanceof Integer) {
            this.updateInt(n, (int)value);
            return;
        }
        if (value instanceof Long) {
            this.updateLong(n, (long)value);
            return;
        }
        if (value instanceof Float) {
            this.updateFloat(n, (float)value);
            return;
        }
        if (value instanceof Double) {
            this.updateDouble(n, (double)value);
            return;
        }
        if (value instanceof byte[]) {
            this.updateBytes(n, (byte[])value);
            return;
        }
        if (value instanceof Date) {
            this.updateDate(n, (Date)value);
            return;
        }
        if (value instanceof Time) {
            this.updateTime(n, (Time)value);
            return;
        }
        if (value instanceof Timestamp) {
            this.updateTimestamp(n, (Timestamp)value);
            return;
        }
        if (value instanceof Blob) {
            this.updateBlob(n, (Blob)value);
            return;
        }
        if (value instanceof Clob) {
            this.updateClob(n, (Clob)value);
            return;
        }
        throw this.dataTypeConversion(n, value.getClass().getName());
    }
    
    public void updateNull(final String s) throws SQLException {
        this.checkIfClosed("updateNull");
        this.updateNull(this.findColumnName(s));
    }
    
    public void updateBoolean(final String s, final boolean b) throws SQLException {
        this.checkIfClosed("updateBoolean");
        this.updateBoolean(this.findColumnName(s), b);
    }
    
    public void updateByte(final String s, final byte b) throws SQLException {
        this.checkIfClosed("updateByte");
        this.updateByte(this.findColumnName(s), b);
    }
    
    public void updateShort(final String s, final short n) throws SQLException {
        this.checkIfClosed("updateShort");
        this.updateShort(this.findColumnName(s), n);
    }
    
    public void updateInt(final String s, final int n) throws SQLException {
        this.checkIfClosed("updateInt");
        this.updateInt(this.findColumnName(s), n);
    }
    
    public void updateLong(final String s, final long n) throws SQLException {
        this.checkIfClosed("updateLong");
        this.updateLong(this.findColumnName(s), n);
    }
    
    public void updateFloat(final String s, final float n) throws SQLException {
        this.checkIfClosed("updateFloat");
        this.updateFloat(this.findColumnName(s), n);
    }
    
    public void updateDouble(final String s, final double n) throws SQLException {
        this.checkIfClosed("updateDouble");
        this.updateDouble(this.findColumnName(s), n);
    }
    
    public void updateString(final String s, final String s2) throws SQLException {
        this.checkIfClosed("updateString");
        this.updateString(this.findColumnName(s), s2);
    }
    
    public void updateBytes(final String s, final byte[] array) throws SQLException {
        this.checkIfClosed("updateBytes");
        this.updateBytes(this.findColumnName(s), array);
    }
    
    public void updateDate(final String s, final Date date) throws SQLException {
        this.checkIfClosed("updateDate");
        this.updateDate(this.findColumnName(s), date);
    }
    
    public void updateTime(final String s, final Time time) throws SQLException {
        this.checkIfClosed("updateTime");
        this.updateTime(this.findColumnName(s), time);
    }
    
    public void updateTimestamp(final String s, final Timestamp timestamp) throws SQLException {
        this.checkIfClosed("updateTimestamp");
        this.updateTimestamp(this.findColumnName(s), timestamp);
    }
    
    public void updateAsciiStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.checkIfClosed("updateAsciiStream");
        this.updateAsciiStream(this.findColumnName(s), inputStream, n);
    }
    
    public void updateBinaryStream(final String s, final InputStream inputStream, final int n) throws SQLException {
        this.checkIfClosed("updateBinaryStream");
        this.updateBinaryStream(this.findColumnName(s), inputStream, n);
    }
    
    public void updateCharacterStream(final String s, final Reader reader, final int n) throws SQLException {
        this.checkIfClosed("updateCharacterStream");
        this.updateCharacterStream(this.findColumnName(s), reader, n);
    }
    
    public void updateObject(final String s, final Object o, final int n) throws SQLException {
        this.checkIfClosed("updateObject");
        this.updateObject(this.findColumnName(s), o, n);
    }
    
    public void updateObject(final String s, final Object o) throws SQLException {
        this.checkIfClosed("updateObject");
        this.updateObject(this.findColumnName(s), o);
    }
    
    public void insertRow() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.checksBeforeInsert();
            this.setupContextStack();
            final LanguageConnectionContext languageConnection = this.getEmbedConnection().getLanguageConnection();
            StatementContext pushStatementContext = null;
            try {
                int n = 0;
                final StringBuffer sb = new StringBuffer("INSERT INTO ");
                final StringBuffer sb2 = new StringBuffer("VALUES (");
                final CursorActivation lookupCursorActivation = languageConnection.lookupCursorActivation(this.getCursorName());
                sb.append(this.getFullBaseTableName(lookupCursorActivation.getPreparedStatement().getTargetTable()));
                final ResultDescription resultDescription = this.theResults.getResultDescription();
                sb.append(" (");
                for (int i = 1; i <= resultDescription.getColumnCount(); ++i) {
                    if (n != 0) {
                        sb.append(",");
                        sb2.append(",");
                    }
                    sb.append(IdUtil.normalToDelimited(resultDescription.getColumnDescriptor(i).getName()));
                    if (this.columnGotUpdated[i - 1]) {
                        sb2.append("?");
                    }
                    else {
                        sb2.append("DEFAULT");
                    }
                    n = 1;
                }
                sb.append(") ");
                sb2.append(") ");
                sb.append(sb2);
                final StatementContext statementContext = languageConnection.getStatementContext();
                Activation activation = null;
                if (statementContext != null) {
                    activation = statementContext.getActivation();
                }
                pushStatementContext = languageConnection.pushStatementContext(this.isAtomic, false, sb.toString(), null, false, 0L);
                pushStatementContext.setActivation(activation);
                final PreparedStatement prepareInternalStatement = languageConnection.prepareInternalStatement(sb.toString());
                final Activation activation2 = prepareInternalStatement.getActivation(languageConnection, false);
                pushStatementContext.setActivation(activation2);
                int j = 1;
                int n2 = 0;
                while (j <= resultDescription.getColumnCount()) {
                    if (this.columnGotUpdated[j - 1]) {
                        activation2.getParameterValueSet().getParameterForSet(n2++).setValue(this.updateRow.getColumn(j));
                    }
                    ++j;
                }
                prepareInternalStatement.executeSubStatement(lookupCursorActivation, activation2, true, 0L);
                activation2.close();
                languageConnection.popStatementContext(pushStatementContext, null);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (Throwable t) {
                throw this.closeOnTransactionError(t);
            }
            finally {
                if (pushStatementContext != null) {
                    languageConnection.popStatementContext(pushStatementContext, null);
                }
                this.restoreContextStack();
            }
        }
    }
    
    public void updateRow() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.checksBeforeUpdateOrDelete("updateRow", -1);
            this.checkNotOnInsertRow();
            this.setupContextStack();
            final LanguageConnectionContext languageConnection = this.getEmbedConnection().getLanguageConnection();
            StatementContext pushStatementContext = null;
            try {
                if (!this.currentRowHasBeenUpdated) {
                    return;
                }
                int n = 0;
                final StringBuffer sb = new StringBuffer("UPDATE ");
                final CursorActivation lookupCursorActivation = languageConnection.lookupCursorActivation(this.getCursorName());
                sb.append(this.getFullBaseTableName(lookupCursorActivation.getPreparedStatement().getTargetTable()));
                sb.append(" SET ");
                final ResultDescription resultDescription = this.theResults.getResultDescription();
                for (int i = 1; i <= resultDescription.getColumnCount(); ++i) {
                    if (this.columnGotUpdated[i - 1]) {
                        if (n != 0) {
                            sb.append(",");
                        }
                        sb.append(IdUtil.normalToDelimited(resultDescription.getColumnDescriptor(i).getName()) + "=?");
                        n = 1;
                    }
                }
                sb.append(" WHERE CURRENT OF " + IdUtil.normalToDelimited(this.getCursorName()));
                final StatementContext statementContext = languageConnection.getStatementContext();
                Activation activation = null;
                if (statementContext != null) {
                    activation = statementContext.getActivation();
                }
                pushStatementContext = languageConnection.pushStatementContext(this.isAtomic, false, sb.toString(), null, false, 0L);
                pushStatementContext.setActivation(activation);
                final PreparedStatement prepareInternalStatement = languageConnection.prepareInternalStatement(sb.toString());
                final Activation activation2 = prepareInternalStatement.getActivation(languageConnection, false);
                pushStatementContext.setActivation(activation2);
                int j = 1;
                int n2 = 0;
                while (j <= resultDescription.getColumnCount()) {
                    if (this.columnGotUpdated[j - 1]) {
                        activation2.getParameterValueSet().getParameterForSet(n2++).setValue(this.updateRow.getColumn(j));
                    }
                    ++j;
                }
                prepareInternalStatement.executeSubStatement(lookupCursorActivation, activation2, true, 0L);
                final SQLWarning warnings = activation2.getWarnings();
                if (warnings != null) {
                    this.addWarning(warnings);
                }
                activation2.close();
                if (this.getType() == 1003) {
                    this.currentRow = null;
                }
                else {
                    this.movePosition(8, 0, "relative");
                }
                languageConnection.popStatementContext(pushStatementContext, null);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (Throwable t) {
                throw this.closeOnTransactionError(t);
            }
            finally {
                if (pushStatementContext != null) {
                    languageConnection.popStatementContext(pushStatementContext, null);
                }
                this.restoreContextStack();
                this.initializeUpdateRowModifiers();
            }
        }
    }
    
    public void deleteRow() throws SQLException {
        synchronized (this.getConnectionSynchronization()) {
            this.checksBeforeUpdateOrDelete("deleteRow", -1);
            this.checkNotOnInsertRow();
            this.setupContextStack();
            final LanguageConnectionContext languageConnection = this.getEmbedConnection().getLanguageConnection();
            StatementContext pushStatementContext = null;
            try {
                final StringBuffer sb = new StringBuffer("DELETE FROM ");
                final CursorActivation lookupCursorActivation = languageConnection.lookupCursorActivation(this.getCursorName());
                sb.append(this.getFullBaseTableName(lookupCursorActivation.getPreparedStatement().getTargetTable()));
                sb.append(" WHERE CURRENT OF " + IdUtil.normalToDelimited(this.getCursorName()));
                final StatementContext statementContext = languageConnection.getStatementContext();
                Activation activation = null;
                if (statementContext != null) {
                    activation = statementContext.getActivation();
                }
                pushStatementContext = languageConnection.pushStatementContext(this.isAtomic, false, sb.toString(), null, false, 0L);
                pushStatementContext.setActivation(activation);
                final PreparedStatement prepareInternalStatement = languageConnection.prepareInternalStatement(sb.toString());
                final Activation activation2 = prepareInternalStatement.getActivation(languageConnection, false);
                pushStatementContext.setActivation(activation2);
                prepareInternalStatement.executeSubStatement(lookupCursorActivation, activation2, true, 0L);
                final SQLWarning warnings = activation2.getWarnings();
                if (warnings != null) {
                    this.addWarning(warnings);
                }
                activation2.close();
                this.currentRow = null;
                languageConnection.popStatementContext(pushStatementContext, null);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
            }
            catch (Throwable t) {
                throw this.closeOnTransactionError(t);
            }
            finally {
                if (pushStatementContext != null) {
                    languageConnection.popStatementContext(pushStatementContext, null);
                }
                this.restoreContextStack();
                this.initializeUpdateRowModifiers();
            }
        }
    }
    
    private String getFullBaseTableName(final ExecCursorTableReference execCursorTableReference) {
        return IdUtil.mkQualifiedName(execCursorTableReference.getSchemaName(), execCursorTableReference.getBaseName());
    }
    
    public void refreshRow() throws SQLException {
        throw Util.notImplemented();
    }
    
    public void cancelRowUpdates() throws SQLException {
        this.checksBeforeUpdateOrDelete("cancelRowUpdates", -1);
        this.checkNotOnInsertRow();
        this.initializeUpdateRowModifiers();
    }
    
    public void moveToInsertRow() throws SQLException {
        this.checkExecIfClosed("moveToInsertRow");
        this.checkUpdatableCursor("moveToInsertRow");
        synchronized (this.getConnectionSynchronization()) {
            try {
                this.setupContextStack();
                this.initializeUpdateRowModifiers();
                this.isOnInsertRow = true;
                for (int i = 1; i <= this.columnGotUpdated.length; ++i) {
                    this.updateRow.setColumn(i, this.resultDescription.getColumnDescriptor(i).getType().getNull());
                }
                InterruptStatus.restoreIntrFlagIfSeen(this.getEmbedConnection().getLanguageConnection());
            }
            catch (Throwable t) {
                this.handleException(t);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public void moveToCurrentRow() throws SQLException {
        this.checkExecIfClosed("moveToCurrentRow");
        this.checkUpdatableCursor("moveToCurrentRow");
        synchronized (this.getConnectionSynchronization()) {
            try {
                if (this.isOnInsertRow) {
                    this.initializeUpdateRowModifiers();
                    this.isOnInsertRow = false;
                }
                InterruptStatus.restoreIntrFlagIfSeen();
            }
            catch (Throwable t) {
                this.handleException(t);
            }
        }
    }
    
    public Blob getBlob(final int n) throws SQLException {
        this.closeCurrentStream();
        this.checkIfClosed("getBlob");
        this.useStreamOrLOB(n);
        synchronized (this.getConnectionSynchronization()) {
            if (this.getColumnType(n) != 2004) {
                throw this.dataTypeConversion("java.sql.Blob", n);
            }
            boolean b = false;
            try {
                final DataValueDescriptor column = this.getColumn(n);
                final EmbedConnection embedConnection = this.getEmbedConnection();
                final boolean null = column.isNull();
                this.wasNull = null;
                if (null) {
                    InterruptStatus.restoreIntrFlagIfSeen();
                    return null;
                }
                if (column.hasStream()) {
                    b = true;
                }
                if (b) {
                    this.setupContextStack();
                }
                final EmbedBlob embedBlob = new EmbedBlob(column, embedConnection);
                ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                return embedBlob;
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            finally {
                if (b) {
                    this.restoreContextStack();
                }
            }
        }
    }
    
    public final Clob getClob(final int n) throws SQLException {
        this.closeCurrentStream();
        this.checkIfClosed("getClob");
        this.useStreamOrLOB(n);
        synchronized (this.getConnectionSynchronization()) {
            if (this.getColumnType(n) != 2005) {
                throw this.dataTypeConversion("java.sql.Clob", n);
            }
            boolean b = false;
            final EmbedConnection embedConnection = this.getEmbedConnection();
            try {
                final StringDataValue stringDataValue = (StringDataValue)this.getColumn(n);
                embedConnection.getLanguageConnection();
                final boolean null = stringDataValue.isNull();
                this.wasNull = null;
                if (null) {
                    InterruptStatus.restoreIntrFlagIfSeen();
                    return null;
                }
                if (stringDataValue.hasStream()) {
                    b = true;
                    this.setupContextStack();
                }
                final EmbedClob embedClob = new EmbedClob(embedConnection, stringDataValue);
                ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                return embedClob;
            }
            catch (Throwable t) {
                throw this.handleException(t);
            }
            finally {
                if (b) {
                    this.restoreContextStack();
                }
            }
        }
    }
    
    public final Blob getBlob(final String s) throws SQLException {
        this.checkIfClosed("getBlob");
        return this.getBlob(this.findColumnName(s));
    }
    
    public final Clob getClob(final String s) throws SQLException {
        this.checkIfClosed("getClob");
        return this.getClob(this.findColumnName(s));
    }
    
    public void updateBlob(final int n, final Blob blob) throws SQLException {
        this.checksBeforeUpdateXXX("updateBlob", n);
        if (this.getColumnType(n) != 2004) {
            throw this.dataTypeConversion(n, "java.sql.Blob");
        }
        if (blob == null) {
            this.updateNull(n);
        }
        else {
            this.updateBinaryStreamInternal(n, blob.getBinaryStream(), false, blob.length(), "updateBlob");
        }
    }
    
    public void updateBlob(final String s, final Blob blob) throws SQLException {
        this.checkIfClosed("updateBlob");
        this.updateBlob(this.findColumnName(s), blob);
    }
    
    public void updateClob(final int n, final Clob clob) throws SQLException {
        this.checksBeforeUpdateXXX("updateClob", n);
        if (this.getColumnType(n) != 2005) {
            throw this.dataTypeConversion(n, "java.sql.Clob");
        }
        if (clob == null) {
            this.updateNull(n);
        }
        else {
            this.updateCharacterStreamInternal(n, clob.getCharacterStream(), false, clob.length(), "updateClob");
        }
    }
    
    public void updateClob(final String s, final Clob clob) throws SQLException {
        this.checkIfClosed("updateClob");
        this.updateClob(this.findColumnName(s), clob);
    }
    
    protected int findColumnName(final String s) throws SQLException {
        if (s == null) {
            throw this.newSQLException("XJ018.S");
        }
        final int columnInsenstive = this.resultDescription.findColumnInsenstive(s);
        if (columnInsenstive == -1) {
            throw this.newSQLException("S0022", s);
        }
        return columnInsenstive;
    }
    
    private final void closeCurrentStream() {
        if (this.currentStream != null) {
            try {
                synchronized (this) {
                    if (this.currentStream != null) {
                        if (this.currentStream instanceof Reader) {
                            ((Reader)this.currentStream).close();
                        }
                        else {
                            ((InputStream)this.currentStream).close();
                        }
                    }
                }
            }
            catch (IOException ex) {}
            finally {
                this.currentStream = null;
            }
        }
    }
    
    final void checkIfClosed(final String s) throws SQLException {
        if (this.isClosed || this.theResults.isClosed()) {
            if (!this.isClosed) {
                this.closeCurrentStream();
                this.markClosed();
            }
            throw this.newSQLException("XCL16.S", s);
        }
    }
    
    final void checkExecIfClosed(final String s) throws SQLException {
        this.checkIfClosed(s);
        final Connection applicationConnection = this.getEmbedConnection().getApplicationConnection();
        if (applicationConnection == null) {
            throw Util.noCurrentConnection();
        }
        if (applicationConnection.isClosed()) {
            this.closeCurrentStream();
            this.markClosed();
            throw Util.noCurrentConnection();
        }
    }
    
    protected String getSQLText() {
        if (this.stmt == null) {
            return null;
        }
        return this.stmt.getSQLText();
    }
    
    protected ParameterValueSet getParameterValueSet() {
        if (this.stmt == null) {
            return null;
        }
        return this.stmt.getParameterValueSet();
    }
    
    private static boolean isMaxFieldSizeType(final int n) {
        return n == -2 || n == -3 || n == -4 || n == 1 || n == 12 || n == -1;
    }
    
    final SQLException closeOnTransactionError(final Throwable t) throws SQLException {
        final SQLException handleException = this.handleException(t);
        if (t instanceof StandardException && ((StandardException)t).getSeverity() == 30000) {
            try {
                this.close();
            }
            catch (Throwable t2) {
                handleException.setNextException(this.handleException(t2));
            }
        }
        return handleException;
    }
    
    protected final DataValueDescriptor getColumn(final int value) throws SQLException, StandardException {
        this.closeCurrentStream();
        if (value < 1 || value > this.resultDescription.getColumnCount()) {
            throw this.newSQLException("S0022", new Integer(value));
        }
        if (this.isOnInsertRow || (this.currentRowHasBeenUpdated && this.columnGotUpdated[value - 1])) {
            return this.updateRow.getColumn(value);
        }
        this.checkOnRow();
        return this.currentRow.getColumn(value);
    }
    
    static final SQLException noStateChangeException(final Throwable t) {
        return TransactionResourceImpl.wrapInSQLException(t);
    }
    
    void setDynamicResultSet(final EmbedStatement owningStmt) {
        if (owningStmt != null) {
            this.owningStmt = owningStmt;
            this.applicationStmt = owningStmt.applicationStatement;
            this.localConn = owningStmt.getEmbedConnection();
        }
        else {
            this.localConn = this.localConn.rootConnection;
        }
        this.singleUseActivation = this.theResults.getActivation();
    }
    
    public final int compareTo(final Object o) {
        return this.order - ((EmbedResultSet)o).order;
    }
    
    private void checkScrollCursor(final String s) throws SQLException {
        this.checkIfClosed(s);
        if (this.stmt.getResultSetType() == 1003) {
            throw Util.newEmbedSQLException("XJ061.S", new Object[] { s }, StandardException.getSeverityFromIdentifier("XJ061.S"));
        }
    }
    
    private void checkUpdatableCursor(final String s) throws SQLException {
        if (this.getConcurrency() != 1008) {
            throw Util.generateCsSQLException("XJ083.U", s);
        }
    }
    
    private boolean checkRowPosition(final int n, final String s) throws SQLException {
        this.checkScrollCursor(s);
        synchronized (this.getConnectionSynchronization()) {
            this.setupContextStack();
            final LanguageConnectionContext languageConnection = this.getEmbedConnection().getLanguageConnection();
            try {
                final StatementContext pushStatementContext = languageConnection.pushStatementContext(this.isAtomic, this.concurrencyOfThisResultSet == 1007, this.getSQLText(), this.getParameterValueSet(), false, 0L);
                final boolean checkRowPosition = this.theResults.checkRowPosition(n);
                languageConnection.popStatementContext(pushStatementContext, null);
                InterruptStatus.restoreIntrFlagIfSeen(languageConnection);
                return checkRowPosition;
            }
            catch (Throwable t) {
                throw this.closeOnTransactionError(t);
            }
            finally {
                this.restoreContextStack();
            }
        }
    }
    
    public final boolean isForUpdate() {
        return this.theResults instanceof NoPutResultSet && ((NoPutResultSet)this.theResults).isForUpdate();
    }
    
    final String getColumnSQLType(final int n) {
        return this.resultDescription.getColumnDescriptor(n).getType().getTypeId().getSQLTypeName();
    }
    
    private final int getMaxColumnWidth(final int n) {
        return this.resultDescription.getColumnDescriptor(n).getType().getMaximumWidth();
    }
    
    private final SQLException dataTypeConversion(final String s, final int n) {
        return this.newSQLException("22005", s, this.getColumnSQLType(n));
    }
    
    private final SQLException dataTypeConversion(final int n, final String s) {
        return this.newSQLException("22005", this.getColumnSQLType(n), s);
    }
    
    final void useStreamOrLOB(final int n) throws SQLException {
        this.checkLOBMultiCall(n);
        this.columnUsedFlags[n - 1] = true;
    }
    
    private void checkLOBMultiCall(final int n) throws SQLException {
        if (this.columnUsedFlags == null) {
            this.columnUsedFlags = new boolean[this.getMetaData().getColumnCount()];
        }
        else if (this.columnUsedFlags[n - 1]) {
            throw this.newSQLException("XCL18.S");
        }
    }
    
    public final boolean isClosed() throws SQLException {
        if (this.isClosed) {
            return true;
        }
        try {
            this.checkExecIfClosed("");
            return false;
        }
        catch (SQLException ex) {
            return this.isClosed;
        }
    }
    
    private void addWarning(final SQLWarning sqlWarning) {
        if (this.topWarning == null) {
            this.topWarning = sqlWarning;
        }
        else {
            this.topWarning.setNextWarning(sqlWarning);
        }
    }
    
    public void updateAsciiStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkIfClosed("updateAsciiStream");
        this.updateAsciiStream(n, inputStream, (long)n2);
    }
    
    public void updateBinaryStream(final int n, final InputStream inputStream, final int n2) throws SQLException {
        this.checkIfClosed("updateBinaryStream");
        this.updateBinaryStream(n, inputStream, (long)n2);
    }
    
    public void updateCharacterStream(final int n, final Reader reader, final int n2) throws SQLException {
        this.checkIfClosed("updateCharacterStream");
        this.updateCharacterStream(n, reader, (long)n2);
    }
    
    public void updateAsciiStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        this.checkIfClosed("updateAsciiStream");
        this.updateAsciiStream(this.findColumnName(s), inputStream, n);
    }
    
    public void updateAsciiStream(final String s, final InputStream inputStream) throws SQLException {
        this.checkIfClosed("updateAsciiStream");
        this.updateAsciiStream(this.findColumnName(s), inputStream);
    }
    
    public void updateBinaryStream(final String s, final InputStream inputStream, final long n) throws SQLException {
        this.checkIfClosed("updateBinaryStream");
        this.updateBinaryStream(this.findColumnName(s), inputStream, n);
    }
    
    public void updateBinaryStream(final String s, final InputStream inputStream) throws SQLException {
        this.checkIfClosed("updateBinaryStream");
        this.updateBinaryStream(this.findColumnName(s), inputStream);
    }
    
    public void updateCharacterStream(final String s, final Reader reader, final long n) throws SQLException {
        this.checkIfClosed("updateCharacterStream");
        this.updateCharacterStream(this.findColumnName(s), reader, n);
    }
    
    public void updateCharacterStream(final String s, final Reader reader) throws SQLException {
        this.checkIfClosed("updateCharacterStream");
        this.updateCharacterStream(this.findColumnName(s), reader);
    }
    
    public void updateBlob(final int n, final InputStream inputStream, final long n2) throws SQLException {
        this.checksBeforeUpdateXXX("updateBlob", n);
        if (this.getColumnType(n) != 2004) {
            throw this.dataTypeConversion(n, "java.sql.Blob");
        }
        if (inputStream == null) {
            this.updateNull(n);
        }
        else {
            this.updateBinaryStreamInternal(n, inputStream, false, n2, "updateBlob");
        }
    }
    
    public void updateBlob(final int n, final InputStream inputStream) throws SQLException {
        this.checksBeforeUpdateXXX("updateBlob", n);
        if (this.getColumnType(n) != 2004) {
            throw this.dataTypeConversion(n, "java.sql.Blob");
        }
        this.updateBinaryStreamInternal(n, inputStream, true, -1L, "updateBlob");
    }
    
    public void updateBlob(final String s, final InputStream inputStream, final long n) throws SQLException {
        this.checkIfClosed("updateBlob");
        this.updateBlob(this.findColumnName(s), inputStream, n);
    }
    
    public void updateBlob(final String s, final InputStream inputStream) throws SQLException {
        this.checkIfClosed("updateBlob");
        this.updateBlob(this.findColumnName(s), inputStream);
    }
    
    public void updateClob(final int n, final Reader reader, final long n2) throws SQLException {
        this.checksBeforeUpdateXXX("updateClob", n);
        if (this.getColumnType(n) != 2005) {
            throw this.dataTypeConversion(n, "java.sql.Clob");
        }
        if (reader == null) {
            this.updateNull(n);
        }
        else {
            this.updateCharacterStreamInternal(n, reader, false, n2, "updateClob");
        }
    }
    
    public void updateClob(final int n, final Reader reader) throws SQLException {
        this.checksBeforeUpdateXXX("updateClob", n);
        if (this.getColumnType(n) != 2005) {
            throw this.dataTypeConversion(n, "java.sql.Clob");
        }
        this.updateCharacterStreamInternal(n, reader, true, -1L, "updateClob");
    }
    
    public void updateClob(final String s, final Reader reader, final long n) throws SQLException {
        this.checkIfClosed("updateClob");
        this.updateClob(this.findColumnName(s), reader, n);
    }
    
    public void updateClob(final String s, final Reader reader) throws SQLException {
        this.checkIfClosed("updateClob");
        this.updateClob(this.findColumnName(s), reader);
    }
    
    public boolean isNull(final int n) throws SQLException {
        try {
            return this.getColumn(n).isNull();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    public int getLength(final int n) throws SQLException {
        try {
            return this.getColumn(n).getLength();
        }
        catch (StandardException ex) {
            throw noStateChangeException(ex);
        }
    }
    
    static {
        EmbedResultSet.fetchedRowBase = 0L;
    }
}
