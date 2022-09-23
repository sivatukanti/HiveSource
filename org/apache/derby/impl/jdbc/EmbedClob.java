// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.Writer;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.EOFException;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.types.StringDataValue;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.EngineLOB;
import java.sql.Clob;

final class EmbedClob extends ConnectionChild implements Clob, EngineLOB
{
    private InternalClob clob;
    private boolean isValid;
    private int locator;
    
    EmbedClob(final EmbedConnection embedConnection) throws SQLException {
        super(embedConnection);
        this.isValid = true;
        this.clob = new TemporaryClob(this);
        embedConnection.addLOBReference(this);
    }
    
    protected EmbedClob(final EmbedConnection embedConnection, final StringDataValue stringDataValue) throws StandardException, SQLException {
        super(embedConnection);
        this.isValid = true;
        if (stringDataValue.hasStream()) {
            final CharacterStreamDescriptor streamWithDescriptor = stringDataValue.getStreamWithDescriptor();
            try {
                this.clob = new StoreStreamClob(streamWithDescriptor, this);
            }
            catch (StandardException ex) {
                if (ex.getMessageId().equals("40XD0")) {
                    throw StandardException.newException("XJ073.S");
                }
                throw ex;
            }
        }
        else {
            try {
                this.clob = new TemporaryClob(stringDataValue.getString(), this);
            }
            catch (IOException streamFailure) {
                throw Util.setStreamFailure(streamFailure);
            }
        }
        embedConnection.addLOBReference(this);
    }
    
    public long length() throws SQLException {
        this.checkValidity();
        try {
            return this.clob.getCharLength();
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    public String getSubString(final long n, final int value) throws SQLException {
        this.checkValidity();
        if (n < 1L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(n));
        }
        if (value < 0) {
            throw Util.generateCsSQLException("XJ071.S", new Integer(value));
        }
        String copyValue;
        try {
            Reader internalReader;
            try {
                internalReader = this.clob.getInternalReader(n);
            }
            catch (EOFException ex) {
                throw Util.generateCsSQLException("XJ076.S", new Long(n), ex);
            }
            final char[] data = new char[value];
            int i;
            int read;
            for (i = 0; i < value; i += read) {
                read = internalReader.read(data, i, value - i);
                if (read == -1) {
                    break;
                }
            }
            internalReader.close();
            if (i == 0) {
                copyValue = "";
            }
            else {
                copyValue = String.copyValueOf(data, 0, i);
            }
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        return copyValue;
    }
    
    public Reader getCharacterStream() throws SQLException {
        this.checkValidity();
        try {
            return new ClobUpdatableReader(this);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    public InputStream getAsciiStream() throws SQLException {
        return new ReaderToAscii(this.getCharacterStream());
    }
    
    public long position(final String anObject, final long value) throws SQLException {
        this.checkValidity();
        if (value < 1L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(value));
        }
        if (anObject == null) {
            throw Util.generateCsSQLException("XJ072.S");
        }
        if ("".equals(anObject)) {
            return value;
        }
        boolean b = false;
        final EmbedConnection embedConnection = this.getEmbedConnection();
        try {
            final Object connectionSynchronization = this.getConnectionSynchronization();
            synchronized (connectionSynchronization) {
                b = !embedConnection.isClosed();
                if (b) {
                    this.setupContextStack();
                }
                int index = 0;
                long n = value;
                long n2 = -1L;
                Reader reader = this.clob.getInternalReader(value);
                final char[] cbuf = new char[4096];
                while (true) {
                    boolean b2 = false;
                    final int read = reader.read(cbuf);
                    if (read == -1) {
                        ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                        return -1L;
                    }
                    for (int i = 0; i < read; ++i) {
                        if (cbuf[i] == anObject.charAt(index)) {
                            if (index != 0 && n2 == -1L && cbuf[i] == anObject.charAt(0)) {
                                n2 = n + i + 1L;
                            }
                            if (++index == anObject.length()) {
                                ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                                return n + i - anObject.length() + 1L;
                            }
                        }
                        else if (index > 0) {
                            if (n2 == -1L) {
                                if (index > 1) {
                                    --i;
                                }
                                index = 0;
                            }
                            else {
                                index = 0;
                                if (n2 < n) {
                                    n = n2;
                                    reader.close();
                                    reader = this.clob.getInternalReader(n2);
                                    n2 = -1L;
                                    b2 = true;
                                    break;
                                }
                                i = (int)(n2 - n) - 1;
                                n2 = -1L;
                            }
                        }
                    }
                    if (b2) {
                        continue;
                    }
                    n += read;
                }
            }
        }
        catch (EOFException ex) {
            ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
            throw Util.generateCsSQLException("XJ076.S", ex);
        }
        catch (IOException streamFailure) {
            ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
            throw Util.setStreamFailure(streamFailure);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    public long position(final Clob clob, long value) throws SQLException {
        this.checkValidity();
        if (value < 1L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(value));
        }
        if (clob == null) {
            throw Util.generateCsSQLException("XJ072.S");
        }
        final boolean b = false;
        final EmbedConnection embedConnection = this.getEmbedConnection();
        try {
            synchronized (this.getConnectionSynchronization()) {
                final char[] value2 = new char[1024];
                int n = 0;
                while (true) {
                    long n2 = -1L;
                    final Reader characterStream = clob.getCharacterStream();
                    while (true) {
                        final int read = characterStream.read(value2, 0, value2.length);
                        if (read == -1) {
                            if (n == 0) {
                                ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                                return value;
                            }
                            ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                            return n2;
                        }
                        else {
                            if (read == 0) {
                                continue;
                            }
                            n = 1;
                            final long position = this.position(new String(value2, 0, read), value);
                            if (position == -1L) {
                                if (n2 == -1L) {
                                    ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                                    return -1L;
                                }
                                value = n2 + 1L;
                                break;
                            }
                            else {
                                if (n2 == -1L) {
                                    n2 = position;
                                }
                                else if (position != value) {
                                    value = n2 + 1L;
                                    break;
                                }
                                value = position + read;
                            }
                        }
                    }
                }
            }
        }
        catch (IOException streamFailure) {
            ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
            throw Util.setStreamFailure(streamFailure);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    public int setString(final long n, final String s) throws SQLException {
        return this.setString(n, s, 0, s.length());
    }
    
    public int setString(final long n, final String s, final int n2, final int value) throws SQLException {
        this.checkValidity();
        if (n < 1L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(n));
        }
        if (n > this.length() + 1L) {
            throw Util.generateCsSQLException("XJ076.S");
        }
        if (s == null) {
            throw Util.generateCsSQLException("XJ072.S");
        }
        if (s.length() == 0) {
            return 0;
        }
        if (n2 < 0 || n2 >= s.length()) {
            throw Util.generateCsSQLException("XJ078.S");
        }
        if (value < 0) {
            throw Util.generateCsSQLException("XJ071.S");
        }
        if (value + n2 > s.length()) {
            throw Util.generateCsSQLException("22011.S.1", new Integer(n2), new Integer(value), s);
        }
        try {
            if (!this.clob.isWritable()) {
                this.makeWritableClobClone();
            }
            this.clob.insertString(s.substring(n2, n2 + value), n);
        }
        catch (EOFException ex) {
            throw Util.generateCsSQLException("XJ076.S", new Long(n));
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        return s.length();
    }
    
    public OutputStream setAsciiStream(final long n) throws SQLException {
        this.checkValidity();
        try {
            return new ClobAsciiStream(this.clob.getWriter(n));
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    public Writer setCharacterStream(final long n) throws SQLException {
        this.checkValidity();
        try {
            if (!this.clob.isWritable()) {
                this.makeWritableClobClone();
            }
            return this.clob.getWriter(n);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    public void truncate(final long n) throws SQLException {
        this.checkValidity();
        if (n < 0L) {
            throw Util.generateCsSQLException("XJ071.S", new Long(n));
        }
        try {
            if (!this.clob.isWritable()) {
                this.makeWritableClobClone(n);
            }
            else {
                this.clob.truncate(n);
            }
        }
        catch (EOFException ex) {
            throw Util.generateCsSQLException("XJ079.S", new Long(n), ex);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    public void free() throws SQLException {
        if (this.isValid) {
            this.isValid = false;
            try {
                this.clob.release();
            }
            catch (IOException streamFailure) {
                throw Util.setStreamFailure(streamFailure);
            }
            finally {
                this.localConn.removeLOBMapping(this.locator);
                this.clob = null;
            }
        }
    }
    
    public Reader getCharacterStream(final long n, final long n2) throws SQLException {
        this.checkValidity();
        if (n <= 0L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(n));
        }
        if (n2 < 0L) {
            throw Util.generateCsSQLException("XJ071.S", new Long(n2));
        }
        if (n2 > this.length() - (n - 1L)) {
            throw Util.generateCsSQLException("XJ087.S", new Long(n), new Long(n2));
        }
        try {
            return new ClobUpdatableReader(this, n, n2);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    private void checkValidity() throws SQLException {
        this.localConn.checkIfClosed();
        if (!this.isValid) {
            throw this.newSQLException("XJ215.S");
        }
    }
    
    private void makeWritableClobClone() throws IOException, SQLException {
        final InternalClob clob = this.clob;
        this.clob = TemporaryClob.cloneClobContent(this.getEmbedConnection().getDBName(), this, clob);
        clob.release();
    }
    
    private void makeWritableClobClone(final long n) throws IOException, SQLException {
        final InternalClob clob = this.clob;
        this.clob = TemporaryClob.cloneClobContent(this.getEmbedConnection().getDBName(), this, clob, n);
        clob.release();
    }
    
    InternalClob getInternalClob() {
        return this.clob;
    }
    
    public int getLocator() {
        if (this.locator == 0) {
            this.locator = this.localConn.addLOBMapping(this);
        }
        return this.locator;
    }
}
