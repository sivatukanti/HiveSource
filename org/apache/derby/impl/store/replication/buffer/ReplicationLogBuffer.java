// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.replication.buffer;

import java.util.NoSuchElementException;
import org.apache.derby.iapi.store.replication.master.MasterFactory;
import java.util.LinkedList;

public class ReplicationLogBuffer
{
    public static final int DEFAULT_NUMBER_LOG_BUFFERS = 10;
    private final LinkedList dirtyBuffers;
    private final LinkedList freeBuffers;
    private LogBufferElement currentDirtyBuffer;
    private boolean validOutBuffer;
    private byte[] outBufferData;
    private int outBufferStored;
    private long outBufferLastInstant;
    private final Object listLatch;
    private final Object outputLatch;
    private int defaultBufferSize;
    private final MasterFactory mf;
    
    public ReplicationLogBuffer(final int defaultBufferSize, final MasterFactory mf) {
        this.listLatch = new Object();
        this.outputLatch = new Object();
        this.defaultBufferSize = defaultBufferSize;
        this.mf = mf;
        this.outBufferData = new byte[defaultBufferSize];
        this.outBufferStored = 0;
        this.outBufferLastInstant = 0L;
        this.validOutBuffer = false;
        this.dirtyBuffers = new LinkedList();
        this.freeBuffers = new LinkedList();
        for (int i = 0; i < 10; ++i) {
            this.freeBuffers.addLast(new LogBufferElement(defaultBufferSize));
        }
        this.currentDirtyBuffer = this.freeBuffers.removeFirst();
    }
    
    public void appendLog(final long n, final byte[] array, final int n2, final int n3) throws LogBufferFullException {
        boolean b = false;
        synchronized (this.listLatch) {
            if (this.currentDirtyBuffer == null) {
                this.switchDirtyBuffer();
            }
            if (n3 > this.currentDirtyBuffer.freeSize()) {
                this.switchDirtyBuffer();
                b = true;
            }
            if (n3 <= this.currentDirtyBuffer.freeSize()) {
                this.currentDirtyBuffer.appendLog(n, array, n2, n3);
            }
            else {
                final LogBufferElement e = new LogBufferElement(n3);
                e.setRecyclable(false);
                e.appendLog(n, array, n2, n3);
                this.dirtyBuffers.addLast(e);
            }
        }
        if (b) {
            this.mf.workToDo();
        }
    }
    
    public boolean next() {
        synchronized (this.listLatch) {
            if (this.dirtyBuffers.size() == 0) {
                try {
                    this.switchDirtyBuffer();
                }
                catch (LogBufferFullException ex) {}
            }
            synchronized (this.outputLatch) {
                if (this.dirtyBuffers.size() > 0) {
                    final LogBufferElement e = this.dirtyBuffers.removeFirst();
                    final int max = Math.max(this.defaultBufferSize, e.size());
                    if (this.outBufferData.length != max) {
                        this.outBufferData = new byte[max];
                    }
                    System.arraycopy(e.getData(), 0, this.outBufferData, 0, e.size());
                    this.outBufferStored = e.size();
                    this.outBufferLastInstant = e.getLastInstant();
                    if (e.isRecyclable()) {
                        this.freeBuffers.addLast(e);
                    }
                    this.validOutBuffer = true;
                }
                else {
                    this.validOutBuffer = false;
                }
            }
        }
        return this.validOutBuffer;
    }
    
    public byte[] getData() throws NoSuchElementException {
        synchronized (this.outputLatch) {
            final byte[] array = new byte[this.getSize()];
            if (this.validOutBuffer) {
                System.arraycopy(this.outBufferData, 0, array, 0, this.getSize());
                return array;
            }
            throw new NoSuchElementException();
        }
    }
    
    public boolean validData() {
        synchronized (this.outputLatch) {
            return this.validOutBuffer;
        }
    }
    
    public int getSize() throws NoSuchElementException {
        synchronized (this.outputLatch) {
            if (this.validOutBuffer) {
                return this.outBufferStored;
            }
            throw new NoSuchElementException();
        }
    }
    
    public long getLastInstant() throws NoSuchElementException {
        synchronized (this.outputLatch) {
            if (this.validOutBuffer) {
                return this.outBufferLastInstant;
            }
            throw new NoSuchElementException();
        }
    }
    
    private void switchDirtyBuffer() throws LogBufferFullException {
        if (this.currentDirtyBuffer != null && this.currentDirtyBuffer.size() > 0) {
            this.dirtyBuffers.addLast(this.currentDirtyBuffer);
            this.currentDirtyBuffer = null;
        }
        if (this.currentDirtyBuffer == null) {
            try {
                (this.currentDirtyBuffer = this.freeBuffers.removeFirst()).init();
            }
            catch (NoSuchElementException ex) {
                throw new LogBufferFullException();
            }
        }
    }
    
    public int getFillInformation() {
        return this.dirtyBuffers.size() * 100 / 10;
    }
}
