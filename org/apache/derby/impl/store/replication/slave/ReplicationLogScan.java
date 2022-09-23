// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.slave;

import java.util.NoSuchElementException;
import org.apache.derby.iapi.error.StandardException;

class ReplicationLogScan
{
    private byte[] logToScan;
    private int currentPosition;
    private long currentInstant;
    private int currentDataOffset;
    private byte[] currentData;
    private boolean hasInfo;
    private boolean isLogSwitch;
    
    protected ReplicationLogScan() {
    }
    
    protected void init(final byte[] logToScan) {
        this.logToScan = logToScan;
        this.currentPosition = 0;
        this.currentInstant = -1L;
        this.currentData = null;
        this.isLogSwitch = false;
        this.hasInfo = false;
    }
    
    protected boolean next() throws StandardException {
        if (this.currentPosition == this.logToScan.length) {
            return this.hasInfo = false;
        }
        try {
            final int retrieveInt = this.retrieveInt();
            if (retrieveInt == 0) {
                this.isLogSwitch = true;
                this.hasInfo = true;
            }
            else {
                this.currentInstant = this.retrieveLong();
                this.retrieveBytes(this.currentData = new byte[retrieveInt], retrieveInt);
                this.retrieveInt();
                this.isLogSwitch = false;
                this.hasInfo = true;
            }
        }
        catch (StandardException ex) {
            this.hasInfo = false;
            throw ex;
        }
        return this.hasInfo;
    }
    
    protected long getInstant() throws NoSuchElementException {
        if (!this.hasInfo) {
            throw new NoSuchElementException();
        }
        if (this.isLogSwitch) {
            return -1L;
        }
        return this.currentInstant;
    }
    
    protected int getDataLength() throws NoSuchElementException {
        if (!this.hasInfo) {
            throw new NoSuchElementException();
        }
        if (this.isLogSwitch) {
            return -1;
        }
        return this.currentData.length;
    }
    
    protected byte[] getData() throws NoSuchElementException {
        if (!this.hasInfo) {
            throw new NoSuchElementException();
        }
        if (this.isLogSwitch) {
            return null;
        }
        return this.currentData;
    }
    
    protected boolean hasValidInformation() {
        return this.hasInfo;
    }
    
    protected boolean isLogRecord() throws NoSuchElementException {
        if (!this.hasInfo) {
            throw new NoSuchElementException();
        }
        return !this.isLogSwitch;
    }
    
    protected boolean isLogFileSwitch() throws NoSuchElementException {
        if (!this.hasInfo) {
            throw new NoSuchElementException();
        }
        return this.isLogSwitch;
    }
    
    private void retrieveBytes(final byte[] array, final int n) throws StandardException {
        try {
            System.arraycopy(this.logToScan, this.currentPosition, array, 0, n);
            this.currentPosition += n;
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw StandardException.newException("XRE01", ex);
        }
    }
    
    private int retrieveInt() throws StandardException {
        try {
            return (this.logToScan[this.currentPosition++] << 24) + ((this.logToScan[this.currentPosition++] & 0xFF) << 16) + ((this.logToScan[this.currentPosition++] & 0xFF) << 8) + (this.logToScan[this.currentPosition++] & 0xFF);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw StandardException.newException("XRE01", ex);
        }
    }
    
    private long retrieveLong() throws StandardException {
        try {
            return ((long)this.logToScan[this.currentPosition++] << 56) + (((long)this.logToScan[this.currentPosition++] & 0xFFL) << 48) + (((long)this.logToScan[this.currentPosition++] & 0xFFL) << 40) + (((long)this.logToScan[this.currentPosition++] & 0xFFL) << 32) + (((long)this.logToScan[this.currentPosition++] & 0xFFL) << 24) + ((this.logToScan[this.currentPosition++] & 0xFF) << 16) + ((this.logToScan[this.currentPosition++] & 0xFF) << 8) + (this.logToScan[this.currentPosition++] & 0xFF);
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw StandardException.newException("XRE01", ex);
        }
    }
}
