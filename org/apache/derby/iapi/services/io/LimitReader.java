// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import java.io.IOException;
import java.io.Reader;

public final class LimitReader extends Reader implements Limit
{
    private int remainingCharacters;
    private boolean limitInPlace;
    private Reader reader;
    
    public LimitReader(final Reader reader) {
        this.reader = reader;
        this.clearLimit();
    }
    
    public int read() throws IOException {
        if (!this.limitInPlace) {
            return this.reader.read();
        }
        if (this.remainingCharacters == 0) {
            return -1;
        }
        final int read = this.reader.read();
        if (read >= 0) {
            --this.remainingCharacters;
        }
        return read;
    }
    
    public int read(final char[] array, final int n, int n2) throws IOException {
        if (!this.limitInPlace) {
            return this.reader.read(array, n, n2);
        }
        if (this.remainingCharacters == 0) {
            return -1;
        }
        if (this.remainingCharacters < n2) {
            n2 = this.remainingCharacters;
        }
        n2 = this.reader.read(array, n, n2);
        if (n2 >= 0) {
            this.remainingCharacters -= n2;
        }
        return n2;
    }
    
    public long skip(long skip) throws IOException {
        if (!this.limitInPlace) {
            return this.reader.skip(skip);
        }
        if (this.remainingCharacters == 0) {
            return 0L;
        }
        if (this.remainingCharacters < skip) {
            skip = this.remainingCharacters;
        }
        skip = this.reader.skip(skip);
        this.remainingCharacters -= (int)skip;
        return skip;
    }
    
    public void close() throws IOException {
        this.reader.close();
    }
    
    public void setLimit(final int remainingCharacters) {
        this.remainingCharacters = remainingCharacters;
        this.limitInPlace = true;
    }
    
    public final int getLimit() {
        return this.remainingCharacters;
    }
    
    public int clearLimit() {
        final int remainingCharacters = this.remainingCharacters;
        this.limitInPlace = false;
        this.remainingCharacters = -1;
        return remainingCharacters;
    }
}
