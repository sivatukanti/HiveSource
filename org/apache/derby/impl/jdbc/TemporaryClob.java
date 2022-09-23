// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import org.apache.derby.iapi.jdbc.CharacterStreamDescriptor;
import java.io.EOFException;
import java.io.Reader;
import java.io.Writer;
import org.apache.derby.iapi.util.UTF8Util;
import java.io.BufferedInputStream;
import org.apache.derby.iapi.error.StandardException;
import java.io.InputStream;
import java.sql.SQLException;
import java.io.IOException;
import java.io.FilterReader;

final class TemporaryClob implements InternalClob
{
    private ConnectionChild conChild;
    private final LOBStreamControl bytes;
    private boolean released;
    private long cachedCharLength;
    private UTF8Reader internalReader;
    private FilterReader unclosableInternalReader;
    private final CharToBytePositionCache posCache;
    
    static InternalClob cloneClobContent(final String s, final ConnectionChild connectionChild, final InternalClob internalClob) throws IOException, SQLException {
        final TemporaryClob temporaryClob = new TemporaryClob(connectionChild);
        temporaryClob.copyClobContent(internalClob);
        return temporaryClob;
    }
    
    static InternalClob cloneClobContent(final String s, final ConnectionChild connectionChild, final InternalClob internalClob, final long n) throws IOException, SQLException {
        final TemporaryClob temporaryClob = new TemporaryClob(connectionChild);
        temporaryClob.copyClobContent(internalClob, n);
        return temporaryClob;
    }
    
    TemporaryClob(final ConnectionChild conChild) {
        this.released = false;
        this.posCache = new CharToBytePositionCache();
        if (conChild == null) {
            throw new NullPointerException("conChild cannot be <null>");
        }
        this.conChild = conChild;
        this.bytes = new LOBStreamControl(conChild.getEmbedConnection());
    }
    
    public synchronized void release() throws IOException {
        if (!this.released) {
            this.released = true;
            this.bytes.free();
            if (this.internalReader != null) {
                this.internalReader.close();
                this.internalReader = null;
                this.unclosableInternalReader = null;
            }
        }
    }
    
    public synchronized InputStream getRawByteStream() throws IOException {
        this.checkIfValid();
        return this.bytes.getInputStream(0L);
    }
    
    TemporaryClob(final String s, final ConnectionChild conChild) throws IOException, StandardException {
        this.released = false;
        this.posCache = new CharToBytePositionCache();
        if (conChild == null) {
            throw new NullPointerException("conChild cannot be <null>");
        }
        this.conChild = conChild;
        this.bytes = new LOBStreamControl(conChild.getEmbedConnection(), this.getByteFromString(s));
        this.cachedCharLength = s.length();
    }
    
    private long getBytePosition(final long n) throws IOException {
        long bytePos;
        if (n == this.posCache.getCharPos()) {
            bytePos = this.posCache.getBytePos();
        }
        else {
            long bytePos2 = 0L;
            long n2 = n - 1L;
            if (n > this.posCache.getCharPos()) {
                bytePos2 = this.posCache.getBytePos();
                n2 -= this.posCache.getCharPos() - 1L;
            }
            bytePos = bytePos2 + UTF8Util.skipFully(new BufferedInputStream(this.bytes.getInputStream(bytePos2)), n2);
            this.posCache.updateCachedPos(n, bytePos);
        }
        return bytePos;
    }
    
    public long getUpdateCount() {
        return this.bytes.getUpdateCount();
    }
    
    public synchronized Writer getWriter(final long n) throws IOException, SQLException {
        this.checkIfValid();
        if (n < this.posCache.getCharPos()) {
            this.posCache.reset();
        }
        return new ClobUtf8Writer(this, n);
    }
    
    public synchronized Reader getReader(final long lng) throws IOException, SQLException {
        this.checkIfValid();
        if (lng < 1L) {
            throw new IllegalArgumentException("Position must be positive: " + lng);
        }
        final UTF8Reader utf8Reader = new UTF8Reader(this.getCSD(), this.conChild, this.conChild.getConnectionSynchronization());
        long skip;
        for (long n = lng - 1L; n > 0L; n -= skip) {
            skip = utf8Reader.skip(n);
            if (skip <= 0L) {
                throw new EOFException("Reached end-of-stream prematurely");
            }
        }
        return utf8Reader;
    }
    
    public Reader getInternalReader(final long n) throws IOException, SQLException {
        if (this.internalReader == null) {
            this.internalReader = new UTF8Reader(this.getCSD(), this.conChild, this.conChild.getConnectionSynchronization());
            this.unclosableInternalReader = new FilterReader((Reader)this.internalReader) {
                public void close() {
                }
            };
        }
        try {
            this.internalReader.reposition(n);
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
        return this.unclosableInternalReader;
    }
    
    public synchronized long getCharLength() throws IOException {
        this.checkIfValid();
        if (this.cachedCharLength == 0L) {
            this.cachedCharLength = UTF8Util.skipUntilEOF(new BufferedInputStream(this.getRawByteStream()));
        }
        return this.cachedCharLength;
    }
    
    public synchronized long getCharLengthIfKnown() {
        this.checkIfValid();
        return (this.cachedCharLength == 0L) ? -1L : this.cachedCharLength;
    }
    
    public synchronized long getByteLength() throws IOException {
        this.checkIfValid();
        return this.bytes.getLength();
    }
    
    public synchronized long insertString(final String s, final long lng) throws IOException, SQLException {
        this.checkIfValid();
        if (lng < 1L) {
            throw new IllegalArgumentException("Position must be positive: " + lng);
        }
        final long cachedCharLength = this.cachedCharLength;
        this.updateInternalState(lng);
        final long bytePosition = this.getBytePosition(lng);
        final long length = this.bytes.getLength();
        final byte[] byteFromString = this.getByteFromString(s);
        if (bytePosition == length) {
            try {
                this.bytes.write(byteFromString, 0, byteFromString.length, bytePosition);
                return s.length();
            }
            catch (StandardException ex) {
                throw Util.generateCsSQLException(ex);
            }
        }
        long bytePosition2;
        try {
            bytePosition2 = this.getBytePosition(lng + s.length());
            this.posCache.updateCachedPos(lng, bytePosition);
        }
        catch (EOFException ex3) {
            bytePosition2 = length;
        }
        try {
            this.bytes.replaceBytes(byteFromString, bytePosition, bytePosition2);
        }
        catch (StandardException ex2) {
            throw Util.generateCsSQLException(ex2);
        }
        if (cachedCharLength != 0L) {
            final long cachedCharLength2 = lng - 1L + s.length();
            if (cachedCharLength2 > cachedCharLength) {
                this.cachedCharLength = cachedCharLength2;
            }
            else {
                this.cachedCharLength = cachedCharLength;
            }
        }
        return s.length();
    }
    
    public synchronized boolean isReleased() {
        return this.released;
    }
    
    public boolean isWritable() {
        return true;
    }
    
    public synchronized void truncate(final long cachedCharLength) throws IOException, SQLException {
        this.checkIfValid();
        try {
            this.bytes.truncate(UTF8Util.skipFully(new BufferedInputStream(this.getRawByteStream()), cachedCharLength));
            this.updateInternalState(cachedCharLength);
            this.cachedCharLength = cachedCharLength;
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    private byte[] getByteFromString(final String s) {
        final byte[] array = new byte[3 * s.length()];
        int n = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 >= '\u0001' && char1 <= '\u007f') {
                array[n++] = (byte)char1;
            }
            else if (char1 > '\u07ff') {
                array[n++] = (byte)(0xE0 | (char1 >> 12 & 0xF));
                array[n++] = (byte)(0x80 | (char1 >> 6 & 0x3F));
                array[n++] = (byte)(0x80 | (char1 >> 0 & 0x3F));
            }
            else {
                array[n++] = (byte)(0xC0 | (char1 >> 6 & 0x1F));
                array[n++] = (byte)(0x80 | (char1 >> 0 & 0x3F));
            }
        }
        final byte[] array2 = new byte[n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    private void copyClobContent(final InternalClob internalClob) throws IOException, SQLException {
        try {
            final long charLengthIfKnown = internalClob.getCharLengthIfKnown();
            if (charLengthIfKnown == -1L) {
                this.cachedCharLength = this.bytes.copyUtf8Data(internalClob.getRawByteStream(), Long.MAX_VALUE);
            }
            else {
                this.cachedCharLength = charLengthIfKnown;
                this.bytes.copyData(internalClob.getRawByteStream(), Long.MAX_VALUE);
            }
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    private void copyClobContent(final InternalClob internalClob, final long n) throws IOException, SQLException {
        try {
            final long charLengthIfKnown = internalClob.getCharLengthIfKnown();
            if (charLengthIfKnown > n || charLengthIfKnown == -1L) {
                this.cachedCharLength = this.bytes.copyUtf8Data(internalClob.getRawByteStream(), n);
            }
            else {
                if (charLengthIfKnown != n) {
                    throw new EOFException();
                }
                this.cachedCharLength = charLengthIfKnown;
                this.bytes.copyData(internalClob.getRawByteStream(), Long.MAX_VALUE);
            }
        }
        catch (StandardException ex) {
            throw Util.generateCsSQLException(ex);
        }
    }
    
    private final void checkIfValid() {
        if (this.released) {
            throw new IllegalStateException("The Clob has been released and is not valid");
        }
    }
    
    private final void updateInternalState(final long n) {
        if (this.internalReader != null) {
            this.internalReader.close();
            this.internalReader = null;
            this.unclosableInternalReader = null;
        }
        if (n < this.posCache.getCharPos()) {
            this.posCache.reset();
        }
        this.cachedCharLength = 0L;
    }
    
    private final CharacterStreamDescriptor getCSD() throws IOException {
        return new CharacterStreamDescriptor.Builder().positionAware(true).maxCharLength(2147483647L).stream(this.bytes.getInputStream(0L)).bufferable(this.bytes.getLength() > 4096L).byteLength(this.bytes.getLength()).charLength(this.cachedCharLength).build();
    }
    
    private static class CharToBytePositionCache
    {
        private long charPos;
        private long bytePos;
        
        CharToBytePositionCache() {
            this.charPos = 1L;
            this.bytePos = 0L;
        }
        
        long getBytePos() {
            return this.bytePos;
        }
        
        long getCharPos() {
            return this.charPos;
        }
        
        void updateCachedPos(final long charPos, final long n) {
            if (charPos - 1L > n) {
                throw new IllegalArgumentException("(charPos -1) cannot be greater than bytePos; " + (charPos - 1L) + " > " + n);
            }
            this.charPos = charPos;
            this.bytePos = n;
        }
        
        void reset() {
            this.charPos = 1L;
            this.bytePos = 0L;
        }
    }
}
