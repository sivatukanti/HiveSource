// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.net.unix;

import java.nio.channels.ReadableByteChannel;
import java.io.OutputStream;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.commons.lang3.SystemUtils;
import java.nio.ByteBuffer;
import java.io.FileInputStream;
import java.io.FileDescriptor;
import java.nio.channels.ClosedChannelException;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import org.apache.hadoop.util.CloseableReferenceCount;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;

@InterfaceAudience.LimitedPrivate({ "HDFS" })
public class DomainSocket implements Closeable
{
    static final Logger LOG;
    private static boolean validateBindPaths;
    private static final String loadingFailureReason;
    final CloseableReferenceCount refCount;
    final int fd;
    private final String path;
    private final DomainInputStream inputStream;
    private final DomainOutputStream outputStream;
    private final DomainChannel channel;
    public static final int SEND_BUFFER_SIZE = 1;
    public static final int RECEIVE_BUFFER_SIZE = 2;
    public static final int SEND_TIMEOUT = 3;
    public static final int RECEIVE_TIMEOUT = 4;
    
    private static native void anchorNative();
    
    @VisibleForTesting
    static native void validateSocketPathSecurity0(final String p0, final int p1) throws IOException;
    
    public static String getLoadingFailureReason() {
        return DomainSocket.loadingFailureReason;
    }
    
    @VisibleForTesting
    public static void disableBindPathValidation() {
        DomainSocket.validateBindPaths = false;
    }
    
    public static String getEffectivePath(final String path, final int port) {
        return path.replace("_PORT", String.valueOf(port));
    }
    
    private DomainSocket(final String path, final int fd) {
        this.inputStream = new DomainInputStream();
        this.outputStream = new DomainOutputStream();
        this.channel = new DomainChannel();
        this.refCount = new CloseableReferenceCount();
        this.fd = fd;
        this.path = path;
    }
    
    private static native int bind0(final String p0) throws IOException;
    
    private void unreference(final boolean checkClosed) throws ClosedChannelException {
        if (checkClosed) {
            this.refCount.unreferenceCheckClosed();
        }
        else {
            this.refCount.unreference();
        }
    }
    
    public static DomainSocket bindAndListen(final String path) throws IOException {
        if (DomainSocket.loadingFailureReason != null) {
            throw new UnsupportedOperationException(DomainSocket.loadingFailureReason);
        }
        if (DomainSocket.validateBindPaths) {
            validateSocketPathSecurity0(path, 0);
        }
        final int fd = bind0(path);
        return new DomainSocket(path, fd);
    }
    
    public static DomainSocket[] socketpair() throws IOException {
        final int[] fds = socketpair0();
        return new DomainSocket[] { new DomainSocket("(anonymous0)", fds[0]), new DomainSocket("(anonymous1)", fds[1]) };
    }
    
    private static native int[] socketpair0() throws IOException;
    
    private static native int accept0(final int p0) throws IOException;
    
    public DomainSocket accept() throws IOException {
        this.refCount.reference();
        boolean exc = true;
        try {
            final DomainSocket ret = new DomainSocket(this.path, accept0(this.fd));
            exc = false;
            return ret;
        }
        finally {
            this.unreference(exc);
        }
    }
    
    private static native int connect0(final String p0) throws IOException;
    
    public static DomainSocket connect(final String path) throws IOException {
        if (DomainSocket.loadingFailureReason != null) {
            throw new UnsupportedOperationException(DomainSocket.loadingFailureReason);
        }
        final int fd = connect0(path);
        return new DomainSocket(path, fd);
    }
    
    public boolean isOpen() {
        return this.refCount.isOpen();
    }
    
    public String getPath() {
        return this.path;
    }
    
    public DomainInputStream getInputStream() {
        return this.inputStream;
    }
    
    public DomainOutputStream getOutputStream() {
        return this.outputStream;
    }
    
    public DomainChannel getChannel() {
        return this.channel;
    }
    
    private static native void setAttribute0(final int p0, final int p1, final int p2) throws IOException;
    
    public void setAttribute(final int type, final int size) throws IOException {
        this.refCount.reference();
        boolean exc = true;
        try {
            setAttribute0(this.fd, type, size);
            exc = false;
        }
        finally {
            this.unreference(exc);
        }
    }
    
    private native int getAttribute0(final int p0, final int p1) throws IOException;
    
    public int getAttribute(final int type) throws IOException {
        this.refCount.reference();
        boolean exc = true;
        try {
            final int attribute = this.getAttribute0(this.fd, type);
            exc = false;
            return attribute;
        }
        finally {
            this.unreference(exc);
        }
    }
    
    private static native void close0(final int p0) throws IOException;
    
    private static native void closeFileDescriptor0(final FileDescriptor p0) throws IOException;
    
    private static native void shutdown0(final int p0) throws IOException;
    
    @Override
    public void close() throws IOException {
        int count;
        try {
            count = this.refCount.setClosed();
        }
        catch (ClosedChannelException e2) {
            return;
        }
        boolean didShutdown = false;
        boolean interrupted = false;
        while (count > 0) {
            if (!didShutdown) {
                try {
                    shutdown0(this.fd);
                }
                catch (IOException e) {
                    DomainSocket.LOG.error("shutdown error: ", e);
                }
                didShutdown = true;
            }
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e3) {
                interrupted = true;
            }
            count = this.refCount.getReferenceCount();
        }
        close0(this.fd);
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
    
    public void shutdown() throws IOException {
        this.refCount.reference();
        boolean exc = true;
        try {
            shutdown0(this.fd);
            exc = false;
        }
        finally {
            this.unreference(exc);
        }
    }
    
    private static native void sendFileDescriptors0(final int p0, final FileDescriptor[] p1, final byte[] p2, final int p3, final int p4) throws IOException;
    
    public void sendFileDescriptors(final FileDescriptor[] descriptors, final byte[] jbuf, final int offset, final int length) throws IOException {
        this.refCount.reference();
        boolean exc = true;
        try {
            sendFileDescriptors0(this.fd, descriptors, jbuf, offset, length);
            exc = false;
        }
        finally {
            this.unreference(exc);
        }
    }
    
    private static native int receiveFileDescriptors0(final int p0, final FileDescriptor[] p1, final byte[] p2, final int p3, final int p4) throws IOException;
    
    public int recvFileInputStreams(final FileInputStream[] streams, final byte[] buf, final int offset, final int length) throws IOException {
        final FileDescriptor[] descriptors = new FileDescriptor[streams.length];
        boolean success = false;
        for (int i = 0; i < streams.length; ++i) {
            streams[i] = null;
        }
        this.refCount.reference();
        try {
            final int ret = receiveFileDescriptors0(this.fd, descriptors, buf, offset, length);
            int j = 0;
            int k = 0;
            while (j < descriptors.length) {
                if (descriptors[j] != null) {
                    streams[k++] = new FileInputStream(descriptors[j]);
                    descriptors[j] = null;
                }
                ++j;
            }
            success = true;
            return ret;
        }
        finally {
            if (!success) {
                for (int l = 0; l < descriptors.length; ++l) {
                    if (descriptors[l] != null) {
                        try {
                            closeFileDescriptor0(descriptors[l]);
                        }
                        catch (Throwable t) {
                            DomainSocket.LOG.warn(t.toString());
                        }
                    }
                    else if (streams[l] != null) {
                        try {
                            streams[l].close();
                        }
                        catch (Throwable t) {
                            DomainSocket.LOG.warn(t.toString());
                            streams[l] = null;
                        }
                        finally {
                            streams[l] = null;
                        }
                    }
                }
            }
            this.unreference(!success);
        }
    }
    
    private static native int readArray0(final int p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    private static native int available0(final int p0) throws IOException;
    
    private static native void write0(final int p0, final int p1) throws IOException;
    
    private static native void writeArray0(final int p0, final byte[] p1, final int p2, final int p3) throws IOException;
    
    private static native int readByteBufferDirect0(final int p0, final ByteBuffer p1, final int p2, final int p3) throws IOException;
    
    @Override
    public String toString() {
        return String.format("DomainSocket(fd=%d,path=%s)", this.fd, this.path);
    }
    
    static {
        if (SystemUtils.IS_OS_WINDOWS) {
            loadingFailureReason = "UNIX Domain sockets are not available on Windows.";
        }
        else if (!NativeCodeLoader.isNativeCodeLoaded()) {
            loadingFailureReason = "libhadoop cannot be loaded.";
        }
        else {
            String problem;
            try {
                anchorNative();
                problem = null;
            }
            catch (Throwable t) {
                problem = "DomainSocket#anchorNative got error: " + t.getMessage();
            }
            loadingFailureReason = problem;
        }
        LOG = LoggerFactory.getLogger(DomainSocket.class);
        DomainSocket.validateBindPaths = true;
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS" })
    public class DomainInputStream extends InputStream
    {
        @Override
        public int read() throws IOException {
            DomainSocket.this.refCount.reference();
            boolean exc = true;
            try {
                final byte[] b = { 0 };
                final int ret = readArray0(DomainSocket.this.fd, b, 0, 1);
                exc = false;
                return (ret >= 0) ? b[0] : -1;
            }
            finally {
                DomainSocket.this.unreference(exc);
            }
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            DomainSocket.this.refCount.reference();
            boolean exc = true;
            try {
                final int nRead = readArray0(DomainSocket.this.fd, b, off, len);
                exc = false;
                return nRead;
            }
            finally {
                DomainSocket.this.unreference(exc);
            }
        }
        
        @Override
        public int available() throws IOException {
            DomainSocket.this.refCount.reference();
            boolean exc = true;
            try {
                final int nAvailable = available0(DomainSocket.this.fd);
                exc = false;
                return nAvailable;
            }
            finally {
                DomainSocket.this.unreference(exc);
            }
        }
        
        @Override
        public void close() throws IOException {
            DomainSocket.this.close();
        }
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS" })
    public class DomainOutputStream extends OutputStream
    {
        @Override
        public void close() throws IOException {
            DomainSocket.this.close();
        }
        
        @Override
        public void write(final int val) throws IOException {
            DomainSocket.this.refCount.reference();
            boolean exc = true;
            try {
                final byte[] b = { (byte)val };
                writeArray0(DomainSocket.this.fd, b, 0, 1);
                exc = false;
            }
            finally {
                DomainSocket.this.unreference(exc);
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            DomainSocket.this.refCount.reference();
            boolean exc = true;
            try {
                writeArray0(DomainSocket.this.fd, b, off, len);
                exc = false;
            }
            finally {
                DomainSocket.this.unreference(exc);
            }
        }
    }
    
    @InterfaceAudience.LimitedPrivate({ "HDFS" })
    public class DomainChannel implements ReadableByteChannel
    {
        @Override
        public boolean isOpen() {
            return DomainSocket.this.isOpen();
        }
        
        @Override
        public void close() throws IOException {
            DomainSocket.this.close();
        }
        
        @Override
        public int read(final ByteBuffer dst) throws IOException {
            DomainSocket.this.refCount.reference();
            boolean exc = true;
            try {
                int nread = 0;
                if (dst.isDirect()) {
                    nread = readByteBufferDirect0(DomainSocket.this.fd, dst, dst.position(), dst.remaining());
                }
                else {
                    if (!dst.hasArray()) {
                        throw new AssertionError((Object)"we don't support using ByteBuffers that aren't either direct or backed by arrays");
                    }
                    nread = readArray0(DomainSocket.this.fd, dst.array(), dst.position() + dst.arrayOffset(), dst.remaining());
                }
                if (nread > 0) {
                    dst.position(dst.position() + nread);
                }
                exc = false;
                return nread;
            }
            finally {
                DomainSocket.this.unreference(exc);
            }
        }
    }
}
