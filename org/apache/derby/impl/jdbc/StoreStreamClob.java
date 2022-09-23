// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.io.Writer;
import java.io.EOFException;
import java.io.Reader;
import java.sql.SQLException;
import org.apache.derby.iapi.util.UTF8Util;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import java.io.FilterReader;
import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;

final class StoreStreamClob implements InternalClob
{
    private volatile boolean released;
    private final PositionedStoreStream positionedStoreStream;
    private CharacterStreamDescriptor csd;
    private final ConnectionChild conChild;
    private final Object synchronizationObject;
    private UTF8Reader internalReader;
    private FilterReader unclosableInternalReader;
    
    public StoreStreamClob(final CharacterStreamDescriptor characterStreamDescriptor, final ConnectionChild conChild) throws StandardException {
        this.released = false;
        try {
            this.positionedStoreStream = new PositionedStoreStream(characterStreamDescriptor.getStream());
        }
        catch (StandardException ex) {
            if (ex.getMessageId().equals("40XD0")) {
                throw StandardException.newException("XJ073.S");
            }
            throw ex;
        }
        catch (IOException ex2) {
            throw StandardException.newException("XCL30.S", ex2, "CLOB");
        }
        this.conChild = conChild;
        this.synchronizationObject = conChild.getConnectionSynchronization();
        this.csd = new CharacterStreamDescriptor.Builder().copyState(characterStreamDescriptor).stream(this.positionedStoreStream).positionAware(true).curBytePos(0L).curCharPos(0L).build();
    }
    
    public void release() {
        if (!this.released) {
            if (this.internalReader != null) {
                this.internalReader.close();
            }
            this.positionedStoreStream.closeStream();
            this.released = true;
        }
    }
    
    public long getCharLength() throws SQLException {
        this.checkIfValid();
        if (this.csd.getCharLength() == 0L) {
            long skipUntilEOF = 0L;
            synchronized (this.synchronizationObject) {
                this.conChild.setupContextStack();
                try {
                    skipUntilEOF = UTF8Util.skipUntilEOF(new BufferedInputStream(this.getRawByteStream()));
                }
                catch (Throwable t) {
                    throw noStateChangeLOB(t);
                }
                finally {
                    ConnectionChild.restoreIntrFlagIfSeen(true, this.conChild.getEmbedConnection());
                    this.conChild.restoreContextStack();
                }
            }
            this.csd = new CharacterStreamDescriptor.Builder().copyState(this.csd).charLength(skipUntilEOF).build();
        }
        return this.csd.getCharLength();
    }
    
    public long getCharLengthIfKnown() {
        this.checkIfValid();
        return (this.csd.getCharLength() == 0L) ? -1L : this.csd.getCharLength();
    }
    
    public InputStream getRawByteStream() throws IOException, SQLException {
        this.checkIfValid();
        try {
            this.positionedStoreStream.reposition(this.csd.getDataOffset());
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
        return this.positionedStoreStream;
    }
    
    public Reader getReader(final long n) throws IOException, SQLException {
        this.checkIfValid();
        try {
            this.positionedStoreStream.reposition(0L);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
        final UTF8Reader utf8Reader = new UTF8Reader(this.csd, this.conChild, this.synchronizationObject);
        long skip;
        for (long n2 = n - 1L; n2 > 0L; n2 -= skip) {
            skip = utf8Reader.skip(n2);
            if (skip <= 0L) {
                throw new EOFException("Reached end-of-stream prematurely");
            }
        }
        return utf8Reader;
    }
    
    public Reader getInternalReader(final long n) throws IOException, SQLException {
        if (this.internalReader == null) {
            if (this.positionedStoreStream.getPosition() != 0L) {
                try {
                    this.positionedStoreStream.resetStream();
                }
                catch (StandardException ex) {
                    throw Util.generateCsSQLException(ex);
                }
            }
            this.internalReader = new UTF8Reader(this.csd, this.conChild, this.synchronizationObject);
            this.unclosableInternalReader = new FilterReader((Reader)this.internalReader) {
                public void close() {
                }
            };
        }
        try {
            this.internalReader.reposition(n);
        }
        catch (StandardException ex2) {
            throw Util.generateCsSQLException(ex2);
        }
        return this.unclosableInternalReader;
    }
    
    public long getUpdateCount() {
        return 0L;
    }
    
    public Writer getWriter(final long n) {
        throw new UnsupportedOperationException("A StoreStreamClob object is not updatable");
    }
    
    public long insertString(final String s, final long n) {
        throw new UnsupportedOperationException("A StoreStreamClob object is not updatable");
    }
    
    public boolean isReleased() {
        return this.released;
    }
    
    public boolean isWritable() {
        return false;
    }
    
    public void truncate(final long n) {
        throw new UnsupportedOperationException("A StoreStreamClob object is not updatable");
    }
    
    private static SQLException noStateChangeLOB(Throwable exception) {
        if (exception instanceof StandardException && ((StandardException)exception).getMessageId().equals("40XD0")) {
            exception = StandardException.newException("XJ073.S");
        }
        return EmbedResultSet.noStateChangeException(exception);
    }
    
    private void checkIfValid() {
        if (this.released) {
            throw new IllegalStateException("The Clob has been released and is not valid");
        }
    }
}
