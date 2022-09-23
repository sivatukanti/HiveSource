// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import java.nio.channels.ClosedByInterruptException;
import java.io.EOFException;
import org.apache.derby.iapi.util.InterruptDetectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import org.apache.derby.iapi.util.InterruptStatus;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import java.io.RandomAccessFile;
import org.apache.derby.io.StorageRandomAccessFile;
import org.apache.derby.iapi.store.raw.ContainerKey;
import java.nio.channels.FileChannel;

class RAFContainer4 extends RAFContainer
{
    private FileChannel ourChannel;
    private final Object channelCleanupMonitor;
    private volatile int threadsInPageIO;
    private volatile boolean restoreChannelInProgress;
    private boolean giveUpIO;
    private final Object giveUpIOm;
    private int iosInProgress;
    private ContainerKey currentIdentity;
    
    public RAFContainer4(final BaseDataFileFactory baseDataFileFactory) {
        super(baseDataFileFactory);
        this.ourChannel = null;
        this.channelCleanupMonitor = new Object();
        this.threadsInPageIO = 0;
        this.restoreChannelInProgress = false;
        this.giveUpIO = false;
        this.giveUpIOm = new Object();
        this.iosInProgress = 0;
    }
    
    private FileChannel getChannel(final StorageRandomAccessFile storageRandomAccessFile) {
        if (storageRandomAccessFile instanceof RandomAccessFile) {
            return ((RandomAccessFile)storageRandomAccessFile).getChannel();
        }
        return null;
    }
    
    private FileChannel getChannel() {
        if (this.ourChannel == null) {
            this.ourChannel = this.getChannel(this.fileData);
        }
        return this.ourChannel;
    }
    
    synchronized boolean openContainer(final ContainerKey currentIdentity) throws StandardException {
        this.currentIdentity = currentIdentity;
        return super.openContainer(currentIdentity);
    }
    
    synchronized void createContainer(final ContainerKey currentIdentity) throws StandardException {
        super.createContainer(this.currentIdentity = currentIdentity);
    }
    
    private void reopen() throws StandardException {
        this.ourChannel = null;
        this.reopenContainer(this.currentIdentity);
    }
    
    synchronized void closeContainer() {
        if (this.ourChannel != null) {
            try {
                this.ourChannel.close();
            }
            catch (IOException ex) {}
            finally {
                this.ourChannel = null;
            }
        }
        super.closeContainer();
    }
    
    protected void readPage(final long n, final byte[] array) throws IOException, StandardException {
        this.readPage(n, array, -1L);
    }
    
    private void readPage(final long n, final byte[] array, final long n2) throws IOException, StandardException {
        final boolean holdsLock = Thread.holdsLock(this);
        final boolean holdsLock2 = Thread.holdsLock(this.allocCache);
        final boolean b = holdsLock || holdsLock2;
        if (!b) {
            synchronized (this.channelCleanupMonitor) {
                int n3 = 120;
                while (this.restoreChannelInProgress) {
                    if (n3-- == 0) {
                        throw StandardException.newException("XSDG9.D");
                    }
                    try {
                        this.channelCleanupMonitor.wait(500L);
                    }
                    catch (InterruptedException ex2) {
                        InterruptStatus.setInterrupted();
                    }
                }
                ++this.threadsInPageIO;
            }
        }
        int i = 0;
        int n4 = 120;
        try {
            while (i == 0) {
                try {
                    if (n == 0L) {
                        synchronized (this) {
                            this.readPage0(n, array, n2);
                        }
                    }
                    else {
                        this.readPage0(n, array, n2);
                    }
                    i = 1;
                }
                catch (ClosedChannelException ex) {
                    this.handleClosedChannel(ex, b, n4--);
                }
            }
        }
        finally {
            if (!b) {
                synchronized (this.channelCleanupMonitor) {
                    --this.threadsInPageIO;
                }
            }
        }
    }
    
    private void readPage0(final long n, final byte[] array, final long n2) throws IOException, StandardException {
        final FileChannel channel;
        synchronized (this) {
            channel = this.getChannel();
        }
        if (channel != null) {
            final long n3 = n * this.pageSize;
            final ByteBuffer wrap = ByteBuffer.wrap(array);
            try {
                if (n2 == -1L) {
                    this.readFull(wrap, channel, n3);
                }
                else {
                    this.readFull(wrap, channel, n2);
                }
            }
            finally {}
            if (this.dataFactory.databaseEncrypted() && n != 0L && n != -1L) {
                this.decryptPage(array, this.pageSize);
            }
        }
        else {
            super.readPage(n, array);
        }
    }
    
    protected void writePage(final long n, final byte[] array, final boolean b) throws IOException, StandardException {
        final boolean holdsLock = Thread.holdsLock(this.allocCache);
        if (!holdsLock) {
            synchronized (this.channelCleanupMonitor) {
                int n2 = 120;
                while (this.restoreChannelInProgress) {
                    if (n2-- == 0) {
                        throw StandardException.newException("XSDG9.D");
                    }
                    try {
                        this.channelCleanupMonitor.wait(500L);
                    }
                    catch (InterruptedException ex2) {
                        InterruptStatus.setInterrupted();
                    }
                }
                ++this.threadsInPageIO;
            }
        }
        int i = 0;
        int n3 = 120;
        try {
            while (i == 0) {
                try {
                    if (n == 0L) {
                        synchronized (this) {
                            this.writePage0(n, array, b);
                        }
                    }
                    else {
                        this.writePage0(n, array, b);
                    }
                    i = 1;
                }
                catch (ClosedChannelException ex) {
                    this.handleClosedChannel(ex, holdsLock, n3--);
                }
            }
        }
        finally {
            if (!holdsLock) {
                synchronized (this.channelCleanupMonitor) {
                    --this.threadsInPageIO;
                }
            }
        }
    }
    
    private void handleClosedChannel(final ClosedChannelException ex, final boolean b, final int n) throws StandardException {
        if (ex instanceof AsynchronousCloseException) {
            if (Thread.currentThread().isInterrupted() && this.recoverContainerAfterInterrupt(ex.toString(), b)) {
                return;
            }
            this.awaitRestoreChannel(ex, b);
        }
        else {
            InterruptStatus.noteAndClearInterrupt("ClosedChannelException", this.threadsInPageIO, this.hashCode());
            this.awaitRestoreChannel(ex, b);
            if (n == 0) {
                throw StandardException.newException("XSDG9.D");
            }
        }
    }
    
    private void awaitRestoreChannel(final Exception ex, final boolean b) throws StandardException {
        if (b) {
            synchronized (this.giveUpIOm) {
                if (this.giveUpIO) {
                    throw StandardException.newException("XSDG9.D");
                }
            }
            throw new InterruptDetectedException();
        }
        synchronized (this.channelCleanupMonitor) {
            --this.threadsInPageIO;
        }
        int n = -1;
        synchronized (this.channelCleanupMonitor) {
            while (this.restoreChannelInProgress) {
                if (++n > 120) {
                    throw StandardException.newException("XSDG9.D", ex);
                }
                try {
                    this.channelCleanupMonitor.wait(500L);
                }
                catch (InterruptedException ex2) {
                    InterruptStatus.setInterrupted();
                }
            }
            ++this.threadsInPageIO;
        }
        synchronized (this.giveUpIOm) {
            if (this.giveUpIO) {
                --this.threadsInPageIO;
                throw StandardException.newException("XSDG9.D");
            }
        }
        if (n == -1) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex3) {
                InterruptStatus.setInterrupted();
            }
        }
    }
    
    private boolean recoverContainerAfterInterrupt(final String s, final boolean b) throws StandardException {
        if (b && this.restoreChannelInProgress) {
            InterruptStatus.noteAndClearInterrupt(s, this.threadsInPageIO, this.hashCode());
            return false;
        }
        synchronized (this.channelCleanupMonitor) {
            if (this.restoreChannelInProgress) {
                InterruptStatus.noteAndClearInterrupt(s, this.threadsInPageIO, this.hashCode());
                return false;
            }
            if (!b) {
                --this.threadsInPageIO;
            }
            this.restoreChannelInProgress = true;
        }
        int n = 120;
        while (true) {
            synchronized (this.channelCleanupMonitor) {
                if (this.threadsInPageIO == 0) {
                    break;
                }
                if (n-- == 0) {
                    this.restoreChannelInProgress = false;
                    this.channelCleanupMonitor.notifyAll();
                    throw StandardException.newException("XSDG9.D");
                }
            }
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex2) {
                InterruptStatus.setInterrupted();
            }
        }
        synchronized (this.channelCleanupMonitor) {
            try {
                InterruptStatus.noteAndClearInterrupt(s, this.threadsInPageIO, this.hashCode());
                synchronized (this) {}
                synchronized (this) {
                    try {
                        this.reopen();
                    }
                    catch (Exception ex) {
                        synchronized (this.giveUpIOm) {
                            this.giveUpIO = true;
                            throw StandardException.newException("XSDG9.D", ex);
                        }
                    }
                }
                if (!b) {
                    ++this.threadsInPageIO;
                }
            }
            finally {
                this.restoreChannelInProgress = false;
                this.channelCleanupMonitor.notifyAll();
            }
        }
        return true;
    }
    
    private void writePage0(final long n, final byte[] array, final boolean b) throws IOException, StandardException {
        final FileChannel channel;
        synchronized (this) {
            if (this.getCommittedDropState()) {
                return;
            }
            channel = this.getChannel();
        }
        if (channel != null) {
            final long n2 = n * this.pageSize;
            byte[] array2 = null;
            if (this.dataFactory.databaseEncrypted()) {
                array2 = new byte[this.pageSize];
            }
            final ByteBuffer wrap = ByteBuffer.wrap(this.updatePageArray(n, array, array2, false));
            this.dataFactory.writeInProgress();
            try {
                this.writeFull(wrap, channel, n2);
            }
            catch (ClosedChannelException ex) {
                synchronized (this) {
                    if (this.getCommittedDropState()) {
                        // monitorexit(this)
                        this.dataFactory.writeFinished();
                        return;
                    }
                    throw ex;
                }
            }
            finally {
                this.dataFactory.writeFinished();
            }
            if (b) {
                this.dataFactory.writeInProgress();
                try {
                    if (!this.dataFactory.dataNotSyncedAtAllocation) {
                        channel.force(false);
                    }
                }
                finally {
                    this.dataFactory.writeFinished();
                }
            }
            else {
                synchronized (this) {
                    this.needsSync = true;
                }
            }
        }
        else {
            super.writePage(n, array, b);
        }
    }
    
    void writeAtOffset(final StorageRandomAccessFile storageRandomAccessFile, final byte[] array, final long n) throws IOException, StandardException {
        FileChannel ourChannel = this.getChannel(storageRandomAccessFile);
        if (ourChannel == null) {
            super.writeAtOffset(storageRandomAccessFile, array, n);
            return;
        }
        this.ourChannel = ourChannel;
        int i = 0;
        while (i == 0) {
            synchronized (this) {
                ourChannel = this.getChannel();
            }
            try {
                this.writeFull(ByteBuffer.wrap(array), ourChannel, n);
                i = 1;
            }
            catch (ClosedChannelException ex) {
                this.handleClosedChannel(ex, true, -1);
            }
        }
    }
    
    byte[] getEmbryonicPage(final StorageRandomAccessFile storageRandomAccessFile, final long n) throws IOException, StandardException {
        if (this.getChannel(storageRandomAccessFile) != null) {
            final byte[] array = new byte[204];
            this.readPage(-1L, array, n);
            return array;
        }
        return super.getEmbryonicPage(storageRandomAccessFile, n);
    }
    
    private void readFull(final ByteBuffer byteBuffer, final FileChannel fileChannel, final long n) throws IOException, StandardException {
        while (byteBuffer.remaining() > 0) {
            if (fileChannel.read(byteBuffer, n + byteBuffer.position()) == -1) {
                throw new EOFException("Reached end of file while attempting to read a whole page.");
            }
            if (Thread.currentThread().isInterrupted() && !fileChannel.isOpen()) {
                throw new ClosedByInterruptException();
            }
        }
    }
    
    private void writeFull(final ByteBuffer byteBuffer, final FileChannel fileChannel, final long n) throws IOException {
        while (byteBuffer.remaining() > 0) {
            fileChannel.write(byteBuffer, n + byteBuffer.position());
            if (Thread.currentThread().isInterrupted() && !fileChannel.isOpen()) {
                throw new ClosedByInterruptException();
            }
        }
    }
    
    private static void debugTrace(final String s) {
    }
}
