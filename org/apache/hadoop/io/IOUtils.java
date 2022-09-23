// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.lang.reflect.Constructor;
import org.apache.hadoop.fs.PathIOException;
import java.io.InterruptedIOException;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import org.apache.hadoop.util.Shell;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.io.FilenameFilter;
import java.io.File;
import java.nio.channels.FileChannel;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.net.Socket;
import org.apache.commons.logging.Log;
import java.io.EOFException;
import org.apache.hadoop.conf.Configuration;
import java.io.PrintStream;
import java.io.IOException;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.InputStream;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class IOUtils
{
    public static final Logger LOG;
    
    public static void copyBytes(InputStream in, OutputStream out, final int buffSize, final boolean close) throws IOException {
        try {
            copyBytes(in, out, buffSize);
            if (close) {
                out.close();
                out = null;
                in.close();
                in = null;
            }
        }
        finally {
            if (close) {
                closeStream(out);
                closeStream(in);
            }
        }
    }
    
    public static void copyBytes(final InputStream in, final OutputStream out, final int buffSize) throws IOException {
        final PrintStream ps = (out instanceof PrintStream) ? ((PrintStream)out) : null;
        final byte[] buf = new byte[buffSize];
        for (int bytesRead = in.read(buf); bytesRead >= 0; bytesRead = in.read(buf)) {
            out.write(buf, 0, bytesRead);
            if (ps != null && ps.checkError()) {
                throw new IOException("Unable to write to output stream.");
            }
        }
    }
    
    public static void copyBytes(final InputStream in, final OutputStream out, final Configuration conf) throws IOException {
        copyBytes(in, out, conf.getInt("io.file.buffer.size", 4096), true);
    }
    
    public static void copyBytes(final InputStream in, final OutputStream out, final Configuration conf, final boolean close) throws IOException {
        copyBytes(in, out, conf.getInt("io.file.buffer.size", 4096), close);
    }
    
    public static void copyBytes(InputStream in, OutputStream out, final long count, final boolean close) throws IOException {
        final byte[] buf = new byte[4096];
        long bytesRemaining = count;
        try {
            while (bytesRemaining > 0L) {
                final int bytesToRead = (int)((bytesRemaining < buf.length) ? bytesRemaining : buf.length);
                final int bytesRead = in.read(buf, 0, bytesToRead);
                if (bytesRead == -1) {
                    break;
                }
                out.write(buf, 0, bytesRead);
                bytesRemaining -= bytesRead;
            }
            if (close) {
                out.close();
                out = null;
                in.close();
                in = null;
            }
        }
        finally {
            if (close) {
                closeStream(out);
                closeStream(in);
            }
        }
    }
    
    public static int wrappedReadForCompressedData(final InputStream is, final byte[] buf, final int off, final int len) throws IOException {
        try {
            return is.read(buf, off, len);
        }
        catch (IOException ie) {
            throw ie;
        }
        catch (Throwable t) {
            throw new IOException("Error while reading compressed data", t);
        }
    }
    
    public static void readFully(final InputStream in, final byte[] buf, int off, final int len) throws IOException {
        int ret;
        for (int toRead = len; toRead > 0; toRead -= ret, off += ret) {
            ret = in.read(buf, off, toRead);
            if (ret < 0) {
                throw new IOException("Premature EOF from inputStream");
            }
        }
    }
    
    public static void skipFully(final InputStream in, final long len) throws IOException {
        long ret;
        for (long amt = len; amt > 0L; amt -= ret) {
            ret = in.skip(amt);
            if (ret == 0L) {
                final int b = in.read();
                if (b == -1) {
                    throw new EOFException("Premature EOF from inputStream after skipping " + (len - amt) + " byte(s).");
                }
                ret = 1L;
            }
        }
    }
    
    @Deprecated
    public static void cleanup(final Log log, final Closeable... closeables) {
        for (final Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                }
                catch (Throwable e) {
                    if (log != null && log.isDebugEnabled()) {
                        log.debug("Exception in closing " + c, e);
                    }
                }
            }
        }
    }
    
    public static void cleanupWithLogger(final Logger logger, final Closeable... closeables) {
        for (final Closeable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                }
                catch (Throwable e) {
                    if (logger != null) {
                        logger.debug("Exception in closing {}", c, e);
                    }
                }
            }
        }
    }
    
    public static void closeStream(final Closeable stream) {
        if (stream != null) {
            cleanupWithLogger(null, stream);
        }
    }
    
    public static void closeStreams(final Closeable... streams) {
        if (streams != null) {
            cleanupWithLogger(null, streams);
        }
    }
    
    public static void closeSocket(final Socket sock) {
        if (sock != null) {
            try {
                sock.close();
            }
            catch (IOException ignored) {
                IOUtils.LOG.debug("Ignoring exception while closing socket", ignored);
            }
        }
    }
    
    public static void writeFully(final WritableByteChannel bc, final ByteBuffer buf) throws IOException {
        do {
            bc.write(buf);
        } while (buf.remaining() > 0);
    }
    
    public static void writeFully(final FileChannel fc, final ByteBuffer buf, long offset) throws IOException {
        do {
            offset += fc.write(buf, offset);
        } while (buf.remaining() > 0);
    }
    
    public static List<String> listDirectory(final File dir, final FilenameFilter filter) throws IOException {
        final ArrayList<String> list = new ArrayList<String>();
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(dir.toPath())) {
            for (final Path entry : stream) {
                final Path fileName = entry.getFileName();
                if (fileName != null) {
                    final String fileNameStr = fileName.toString();
                    if (filter != null && !filter.accept(dir, fileNameStr)) {
                        continue;
                    }
                    list.add(fileNameStr);
                }
            }
        }
        catch (DirectoryIteratorException e) {
            throw e.getCause();
        }
        return list;
    }
    
    public static void fsync(final File fileToSync) throws IOException {
        if (!fileToSync.exists()) {
            throw new FileNotFoundException("File/Directory " + fileToSync.getAbsolutePath() + " does not exist");
        }
        final boolean isDir = fileToSync.isDirectory();
        if (isDir && Shell.WINDOWS) {
            return;
        }
        try (final FileChannel channel = FileChannel.open(fileToSync.toPath(), isDir ? StandardOpenOption.READ : StandardOpenOption.WRITE)) {
            fsync(channel, isDir);
        }
    }
    
    public static void fsync(final FileChannel channel, final boolean isDir) throws IOException {
        try {
            channel.force(true);
        }
        catch (IOException ioe) {
            if (!isDir) {
                throw ioe;
            }
            assert !Shell.LINUX && !Shell.MAC : "On Linux and MacOSX fsyncing a directory should not throw IOException, we just don't want to rely on that in production (undocumented). Got: " + ioe;
        }
    }
    
    public static IOException wrapException(final String path, final String methodName, final IOException exception) {
        if (exception instanceof InterruptedIOException || exception instanceof PathIOException) {
            return exception;
        }
        final String msg = String.format("Failed with %s while processing file/directory :[%s] in method:[%s]", exception.getClass().getName(), path, methodName);
        try {
            return wrapWithMessage(exception, msg);
        }
        catch (Exception ex) {
            return new PathIOException(path, exception);
        }
    }
    
    private static <T extends IOException> T wrapWithMessage(final T exception, final String msg) throws T, IOException {
        final Class<? extends Throwable> clazz = exception.getClass();
        try {
            final Constructor<? extends Throwable> ctor = clazz.getConstructor(String.class);
            final Throwable t = (Throwable)ctor.newInstance(msg);
            return (T)t.initCause(exception);
        }
        catch (Throwable e) {
            IOUtils.LOG.warn("Unable to wrap exception of type " + clazz + ": it has no (String) constructor", e);
            throw exception;
        }
    }
    
    public static byte[] readFullyToByteArray(final DataInput in) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while (true) {
                baos.write(in.readByte());
            }
        }
        catch (EOFException ex) {
            return baos.toByteArray();
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(IOUtils.class);
    }
    
    public static class NullOutputStream extends OutputStream
    {
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
        }
        
        @Override
        public void write(final int b) throws IOException {
        }
    }
}
