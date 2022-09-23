// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.OutputStream;
import org.apache.derby.iapi.util.InterruptStatus;
import org.apache.derby.iapi.services.io.InputStreamUtil;
import java.io.EOFException;
import org.apache.derby.iapi.types.Resetable;
import java.io.InputStream;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.sql.SQLException;
import org.apache.derby.iapi.error.StandardException;
import java.io.IOException;
import org.apache.derby.iapi.jdbc.EngineLOB;
import java.sql.Blob;

final class EmbedBlob extends ConnectionChild implements Blob, EngineLOB
{
    private boolean materialized;
    private PositionedStoreStream myStream;
    private int locator;
    private long streamLength;
    private final int streamPositionOffset;
    private boolean isValid;
    private LOBStreamControl control;
    
    EmbedBlob(final byte[] array, final EmbedConnection embedConnection) throws SQLException {
        super(embedConnection);
        this.locator = 0;
        this.streamLength = -1L;
        this.isValid = true;
        try {
            this.control = new LOBStreamControl(embedConnection, array);
            this.materialized = true;
            this.streamPositionOffset = Integer.MIN_VALUE;
            embedConnection.addLOBReference(this);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    protected EmbedBlob(final DataValueDescriptor dataValueDescriptor, final EmbedConnection embedConnection) throws StandardException, SQLException {
        super(embedConnection);
        this.locator = 0;
        this.streamLength = -1L;
        this.isValid = true;
        if (dataValueDescriptor.hasStream()) {
            this.streamPositionOffset = this.handleStreamValue(dataValueDescriptor.getStream(), embedConnection);
        }
        else {
            this.materialized = true;
            this.streamPositionOffset = Integer.MIN_VALUE;
            final byte[] bytes = dataValueDescriptor.getBytes();
            try {
                this.control = new LOBStreamControl(this.getEmbedConnection(), bytes);
            }
            catch (IOException streamFailure) {
                throw Util.setStreamFailure(streamFailure);
            }
        }
        embedConnection.addLOBReference(this);
    }
    
    private int handleStreamValue(final InputStream inputStream, final EmbedConnection embedConnection) throws StandardException, SQLException {
        if (inputStream instanceof Resetable) {
            this.materialized = false;
            try {
                this.myStream = new PositionedStoreStream(inputStream);
                final BinaryToRawStream binaryToRawStream = new BinaryToRawStream(this.myStream, embedConnection);
                final int n = (int)this.myStream.getPosition();
                this.streamLength = binaryToRawStream.getLength();
                binaryToRawStream.close();
                return n;
            }
            catch (StandardException ex) {
                if (ex.getMessageId().equals("40XD0")) {
                    throw StandardException.newException("XJ073.S");
                }
                throw ex;
            }
            catch (IOException ex2) {
                throw StandardException.newException("XCL30.S", ex2, "BLOB");
            }
        }
        this.materialized = true;
        final int n = Integer.MIN_VALUE;
        try {
            this.control = new LOBStreamControl(this.getEmbedConnection());
            final BinaryToRawStream binaryToRawStream2 = new BinaryToRawStream(inputStream, embedConnection);
            final byte[] b = new byte[4096];
            long write = 0L;
            while (true) {
                final int read = binaryToRawStream2.read(b, 0, b.length);
                if (read < 1) {
                    break;
                }
                write = this.control.write(b, 0, read, write);
            }
            binaryToRawStream2.close();
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        return n;
    }
    
    private long setBlobPosition(final long n) throws StandardException, IOException {
        if (this.materialized) {
            if (n >= this.control.getLength()) {
                throw StandardException.newException("XJ076.S", new Long(n));
            }
        }
        else {
            try {
                this.myStream.reposition(n + this.streamPositionOffset);
            }
            catch (EOFException ex) {
                throw StandardException.newException("XJ076.S", ex, new Long(n));
            }
        }
        return n;
    }
    
    private int read(final long n) throws IOException, StandardException {
        int n2;
        if (this.materialized) {
            if (n >= this.control.getLength()) {
                return -1;
            }
            n2 = this.control.read(n);
        }
        else {
            this.myStream.reposition(n + this.streamPositionOffset);
            n2 = this.myStream.read();
        }
        return n2;
    }
    
    public long length() throws SQLException {
        this.checkValidity();
        try {
            if (this.materialized) {
                return this.control.getLength();
            }
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        if (this.streamLength != -1L) {
            return this.streamLength;
        }
        boolean b = false;
        try {
            synchronized (this.getConnectionSynchronization()) {
                final EmbedConnection embedConnection = this.getEmbedConnection();
                b = !embedConnection.isClosed();
                if (b) {
                    this.setupContextStack();
                }
                this.myStream.resetStream();
                final BinaryToRawStream binaryToRawStream = new BinaryToRawStream(this.myStream, this);
                this.streamLength = InputStreamUtil.skipUntilEOF(binaryToRawStream);
                binaryToRawStream.close();
                ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                return this.streamLength;
            }
        }
        catch (Throwable t) {
            throw this.handleMyExceptions(t);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    public byte[] getBytes(final long n, final int value) throws SQLException {
        this.checkValidity();
        boolean b = false;
        try {
            if (n < 1L) {
                throw StandardException.newException("XJ070.S", new Long(n));
            }
            if (value < 0) {
                throw StandardException.newException("XJ071.S", new Integer(value));
            }
            byte[] array;
            if (this.materialized) {
                array = new byte[value];
                final int read = this.control.read(array, 0, array.length, n - 1L);
                if (read == -1) {
                    InterruptStatus.restoreIntrFlagIfSeen();
                    return new byte[0];
                }
                if (read < value) {
                    final byte[] array2 = new byte[read];
                    System.arraycopy(array, 0, array2, 0, read);
                    array = array2;
                }
                InterruptStatus.restoreIntrFlagIfSeen();
            }
            else {
                synchronized (this.getConnectionSynchronization()) {
                    final EmbedConnection embedConnection = this.getEmbedConnection();
                    b = !embedConnection.isClosed();
                    if (b) {
                        this.setupContextStack();
                    }
                    this.setBlobPosition(n - 1L);
                    array = new byte[value];
                    final int loop = InputStreamUtil.readLoop(this.myStream, array, 0, value);
                    if (loop < value) {
                        final byte[] array3 = new byte[loop];
                        System.arraycopy(array, 0, array3, 0, loop);
                        ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                        return array3;
                    }
                    ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                }
            }
            return array;
        }
        catch (StandardException exception) {
            if (exception.getMessageId().equals("XJ079.S")) {
                exception = StandardException.newException("XJ076.S", new Long(n));
            }
            throw this.handleMyExceptions(exception);
        }
        catch (Throwable t) {
            throw this.handleMyExceptions(t);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    public InputStream getBinaryStream() throws SQLException {
        this.checkValidity();
        boolean b = false;
        try {
            if (this.materialized) {
                return this.control.getInputStream(0L);
            }
            synchronized (this.getConnectionSynchronization()) {
                final EmbedConnection embedConnection = this.getEmbedConnection();
                b = !embedConnection.isClosed();
                if (b) {
                    this.setupContextStack();
                }
                this.myStream.resetStream();
                final UpdatableBlobStream updatableBlobStream = new UpdatableBlobStream(this, new AutoPositioningStream(this, this.myStream, this));
                ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                return updatableBlobStream;
            }
        }
        catch (Throwable t) {
            throw this.handleMyExceptions(t);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    public long position(final byte[] array, final long value) throws SQLException {
        this.checkValidity();
        boolean b = false;
        try {
            if (value < 1L) {
                throw StandardException.newException("XJ070.S", new Long(value));
            }
            if (array == null) {
                throw StandardException.newException("XJ072.S");
            }
            if (array.length == 0) {
                return value;
            }
            synchronized (this.getConnectionSynchronization()) {
                final EmbedConnection embedConnection = this.getEmbedConnection();
                b = !embedConnection.isClosed();
                if (b) {
                    this.setupContextStack();
                }
                long n = this.setBlobPosition(value - 1L);
                final byte b2 = array[0];
                while (true) {
                    final int read = this.read(n++);
                    if (read == -1) {
                        ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                        return -1L;
                    }
                    if (read != b2) {
                        continue;
                    }
                    final long blobPosition = n;
                    if (this.checkMatch(array, n)) {
                        ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                        return blobPosition;
                    }
                    n = this.setBlobPosition(blobPosition);
                }
            }
        }
        catch (StandardException ex) {
            throw this.handleMyExceptions(ex);
        }
        catch (Throwable t) {
            throw this.handleMyExceptions(t);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    private boolean checkMatch(final byte[] array, long n) throws IOException, StandardException {
        for (int i = 1; i < array.length; ++i) {
            final int read = this.read(n++);
            if (read < 0 || read != array[i]) {
                return false;
            }
        }
        return true;
    }
    
    public long position(final Blob blob, final long value) throws SQLException {
        this.checkValidity();
        boolean b = false;
        try {
            if (value < 1L) {
                throw StandardException.newException("XJ070.S", new Long(value));
            }
            if (blob == null) {
                throw StandardException.newException("XJ072.S");
            }
            synchronized (this.getConnectionSynchronization()) {
                final EmbedConnection embedConnection = this.getEmbedConnection();
                b = !embedConnection.isClosed();
                if (b) {
                    this.setupContextStack();
                }
                long n = this.setBlobPosition(value - 1L);
                byte[] bytes;
                try {
                    bytes = blob.getBytes(1L, 1);
                }
                catch (SQLException ex2) {
                    throw StandardException.newException("XJ077.S");
                }
                if (bytes == null || bytes.length < 1) {
                    ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                    return value;
                }
                final byte b2 = bytes[0];
                while (true) {
                    final int read = this.read(n++);
                    if (read == -1) {
                        ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                        return -1L;
                    }
                    if (read != b2) {
                        continue;
                    }
                    final long blobPosition = n;
                    if (this.checkMatch(blob, n)) {
                        ConnectionChild.restoreIntrFlagIfSeen(b, embedConnection);
                        return blobPosition;
                    }
                    n = this.setBlobPosition(blobPosition);
                }
            }
        }
        catch (StandardException ex) {
            throw this.handleMyExceptions(ex);
        }
        catch (Throwable t) {
            throw this.handleMyExceptions(t);
        }
        finally {
            if (b) {
                this.restoreContextStack();
            }
        }
    }
    
    private boolean checkMatch(final Blob blob, long n) throws IOException, StandardException {
        InputStream binaryStream;
        try {
            binaryStream = blob.getBinaryStream();
        }
        catch (SQLException ex) {
            return false;
        }
        if (binaryStream == null) {
            return false;
        }
        if (binaryStream.read() < 0) {
            return false;
        }
        while (true) {
            final int read = binaryStream.read();
            if (read < 0) {
                return true;
            }
            final int read2 = this.read(n++);
            if (read != read2 || read2 < 0) {
                return false;
            }
        }
    }
    
    private SQLException handleMyExceptions(Throwable exception) throws SQLException {
        if (exception instanceof StandardException && ((StandardException)exception).getMessageId().equals("40XD0")) {
            exception = StandardException.newException("XJ073.S");
        }
        return this.handleException(exception);
    }
    
    protected void finalize() {
        if (!this.materialized) {
            this.myStream.closeStream();
        }
    }
    
    public int setBytes(final long n, final byte[] array) throws SQLException {
        return this.setBytes(n, array, 0, array.length);
    }
    
    public int setBytes(final long n, final byte[] array, final int n2, final int n3) throws SQLException {
        this.checkValidity();
        if (n - 1L > this.length()) {
            throw Util.generateCsSQLException("XJ076.S", new Long(n));
        }
        if (n < 1L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(n));
        }
        if (n2 < 0 || n2 > array.length) {
            throw Util.generateCsSQLException("XJ078.S", new Long(n2));
        }
        if (n3 < 0) {
            throw Util.generateCsSQLException("XJ071.S", new Long(n3));
        }
        if (n3 == 0) {
            return 0;
        }
        if (n3 > array.length - n2) {
            throw Util.generateCsSQLException("XJ079.S", new Long(n3));
        }
        try {
            if (this.materialized) {
                this.control.write(array, n2, n3, n - 1L);
            }
            else {
                (this.control = new LOBStreamControl(this.getEmbedConnection())).copyData(this.myStream, this.length());
                this.control.write(array, n2, n3, n - 1L);
                this.myStream.close();
                this.streamLength = -1L;
                this.materialized = true;
            }
            return n3;
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    public OutputStream setBinaryStream(final long n) throws SQLException {
        this.checkValidity();
        if (n - 1L > this.length()) {
            throw Util.generateCsSQLException("XJ076.S", new Long(n));
        }
        if (n < 1L) {
            throw Util.generateCsSQLException("XJ070.S", new Long(n));
        }
        try {
            if (this.materialized) {
                return this.control.getOutputStream(n - 1L);
            }
            (this.control = new LOBStreamControl(this.getEmbedConnection())).copyData(this.myStream, n - 1L);
            this.myStream.close();
            this.streamLength = -1L;
            this.materialized = true;
            return this.control.getOutputStream(n - 1L);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    public void truncate(final long value) throws SQLException {
        if (value > this.length()) {
            throw Util.generateCsSQLException("XJ079.S", new Long(value));
        }
        try {
            if (this.materialized) {
                this.control.truncate(value);
            }
            else {
                this.setBlobPosition(0L);
                (this.control = new LOBStreamControl(this.getEmbedConnection())).copyData(this.myStream, value);
                this.myStream.close();
                this.streamLength = -1L;
                this.materialized = true;
            }
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    public void free() throws SQLException {
        if (!this.isValid) {
            return;
        }
        this.isValid = false;
        if (this.locator != 0) {
            this.localConn.removeLOBMapping(this.locator);
        }
        this.streamLength = -1L;
        if (!this.materialized) {
            this.myStream.closeStream();
            this.myStream = null;
        }
        else {
            try {
                this.control.free();
                this.control = null;
            }
            catch (IOException streamFailure) {
                throw Util.setStreamFailure(streamFailure);
            }
        }
    }
    
    public InputStream getBinaryStream(final long n, final long n2) throws SQLException {
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
            return new UpdatableBlobStream(this, this.getBinaryStream(), n - 1L, n2);
        }
        catch (IOException streamFailure) {
            throw Util.setStreamFailure(streamFailure);
        }
    }
    
    private void checkValidity() throws SQLException {
        this.getEmbedConnection().checkIfClosed();
        if (!this.isValid) {
            throw this.newSQLException("XJ215.S");
        }
    }
    
    boolean isMaterialized() {
        return this.materialized;
    }
    
    public int getLocator() {
        if (this.locator == 0) {
            this.locator = this.localConn.addLOBMapping(this);
        }
        return this.locator;
    }
}
