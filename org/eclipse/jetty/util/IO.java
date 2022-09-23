// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import org.eclipse.jetty.util.log.Logger;

public class IO
{
    private static final Logger LOG;
    public static final String CRLF = "\r\n";
    public static final byte[] CRLF_BYTES;
    public static final int bufferSize = 65536;
    private static NullOS __nullStream;
    private static ClosedIS __closedStream;
    private static NullWrite __nullWriter;
    private static PrintWriter __nullPrintWriter;
    
    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        copy(in, out, -1L);
    }
    
    public static void copy(final Reader in, final Writer out) throws IOException {
        copy(in, out, -1L);
    }
    
    public static void copy(final InputStream in, final OutputStream out, long byteCount) throws IOException {
        final byte[] buffer = new byte[65536];
        int len = 65536;
        if (byteCount >= 0L) {
            while (byteCount > 0L) {
                final int max = (byteCount < 65536L) ? ((int)byteCount) : 65536;
                len = in.read(buffer, 0, max);
                if (len == -1) {
                    break;
                }
                byteCount -= len;
                out.write(buffer, 0, len);
            }
        }
        else {
            while (true) {
                len = in.read(buffer, 0, 65536);
                if (len < 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
    }
    
    public static void copy(final Reader in, final Writer out, long byteCount) throws IOException {
        final char[] buffer = new char[65536];
        int len = 65536;
        if (byteCount >= 0L) {
            while (byteCount > 0L) {
                if (byteCount < 65536L) {
                    len = in.read(buffer, 0, (int)byteCount);
                }
                else {
                    len = in.read(buffer, 0, 65536);
                }
                if (len == -1) {
                    break;
                }
                byteCount -= len;
                out.write(buffer, 0, len);
            }
        }
        else if (out instanceof PrintWriter) {
            final PrintWriter pout = (PrintWriter)out;
            while (!pout.checkError()) {
                len = in.read(buffer, 0, 65536);
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
        else {
            while (true) {
                len = in.read(buffer, 0, 65536);
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
    }
    
    public static void copy(final File from, final File to) throws IOException {
        if (from.isDirectory()) {
            copyDir(from, to);
        }
        else {
            copyFile(from, to);
        }
    }
    
    public static void copyDir(final File from, final File to) throws IOException {
        if (to.exists()) {
            if (!to.isDirectory()) {
                throw new IllegalArgumentException(to.toString());
            }
        }
        else {
            to.mkdirs();
        }
        final File[] files = from.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; ++i) {
                final String name = files[i].getName();
                if (!".".equals(name)) {
                    if (!"..".equals(name)) {
                        copy(files[i], new File(to, name));
                    }
                }
            }
        }
    }
    
    public static void copyFile(final File from, final File to) throws IOException {
        final InputStream in = new FileInputStream(from);
        Throwable x0 = null;
        try {
            final OutputStream out = new FileOutputStream(to);
            Throwable x2 = null;
            try {
                copy(in, out);
            }
            catch (Throwable t) {
                x2 = t;
                throw t;
            }
            finally {
                $closeResource(x2, out);
            }
        }
        catch (Throwable t2) {
            x0 = t2;
            throw t2;
        }
        finally {
            $closeResource(x0, in);
        }
    }
    
    public static String toString(final InputStream in) throws IOException {
        return toString(in, (Charset)null);
    }
    
    public static String toString(final InputStream in, final String encoding) throws IOException {
        return toString(in, (encoding == null) ? null : Charset.forName(encoding));
    }
    
    public static String toString(final InputStream in, final Charset encoding) throws IOException {
        final StringWriter writer = new StringWriter();
        final InputStreamReader reader = (encoding == null) ? new InputStreamReader(in) : new InputStreamReader(in, encoding);
        copy(reader, writer);
        return writer.toString();
    }
    
    public static String toString(final Reader in) throws IOException {
        final StringWriter writer = new StringWriter();
        copy(in, writer);
        return writer.toString();
    }
    
    public static boolean delete(final File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isDirectory()) {
            final File[] files = file.listFiles();
            for (int i = 0; files != null && i < files.length; ++i) {
                delete(files[i]);
            }
        }
        return file.delete();
    }
    
    public static void close(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException ignore) {
            IO.LOG.ignore(ignore);
        }
    }
    
    public static void close(final InputStream is) {
        close((Closeable)is);
    }
    
    public static void close(final OutputStream os) {
        close((Closeable)os);
    }
    
    public static void close(final Reader reader) {
        close((Closeable)reader);
    }
    
    public static void close(final Writer writer) {
        close((Closeable)writer);
    }
    
    public static byte[] readBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        copy(in, bout);
        return bout.toByteArray();
    }
    
    public static long write(final GatheringByteChannel out, final ByteBuffer[] buffers, int offset, int length) throws IOException {
        long total = 0L;
    Label_0003:
        while (length > 0) {
            final long wrote = out.write(buffers, offset, length);
            if (wrote == 0L) {
                break;
            }
            total += wrote;
            for (int i = offset; i < buffers.length; ++i) {
                if (buffers[i].hasRemaining()) {
                    length -= i - offset;
                    offset = i;
                    continue Label_0003;
                }
            }
            length = 0;
        }
        return total;
    }
    
    public static OutputStream getNullStream() {
        return IO.__nullStream;
    }
    
    public static InputStream getClosedStream() {
        return IO.__closedStream;
    }
    
    public static Writer getNullWriter() {
        return IO.__nullWriter;
    }
    
    public static PrintWriter getNullPrintWriter() {
        return IO.__nullPrintWriter;
    }
    
    private static /* synthetic */ void $closeResource(final Throwable x0, final AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable exception) {
                x0.addSuppressed(exception);
            }
        }
        else {
            x1.close();
        }
    }
    
    static {
        LOG = Log.getLogger(IO.class);
        CRLF_BYTES = new byte[] { 13, 10 };
        IO.__nullStream = new NullOS();
        IO.__closedStream = new ClosedIS();
        IO.__nullWriter = new NullWrite();
        IO.__nullPrintWriter = new PrintWriter(IO.__nullWriter);
    }
    
    static class Job implements Runnable
    {
        InputStream in;
        OutputStream out;
        Reader read;
        Writer write;
        
        Job(final InputStream in, final OutputStream out) {
            this.in = in;
            this.out = out;
            this.read = null;
            this.write = null;
        }
        
        Job(final Reader read, final Writer write) {
            this.in = null;
            this.out = null;
            this.read = read;
            this.write = write;
        }
        
        @Override
        public void run() {
            try {
                if (this.in != null) {
                    IO.copy(this.in, this.out, -1L);
                }
                else {
                    IO.copy(this.read, this.write, -1L);
                }
            }
            catch (IOException e) {
                IO.LOG.ignore(e);
                try {
                    if (this.out != null) {
                        this.out.close();
                    }
                    if (this.write != null) {
                        this.write.close();
                    }
                }
                catch (IOException e2) {
                    IO.LOG.ignore(e2);
                }
            }
        }
    }
    
    private static class NullOS extends OutputStream
    {
        @Override
        public void close() {
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void write(final byte[] b) {
        }
        
        @Override
        public void write(final byte[] b, final int i, final int l) {
        }
        
        @Override
        public void write(final int b) {
        }
    }
    
    private static class ClosedIS extends InputStream
    {
        @Override
        public int read() throws IOException {
            return -1;
        }
    }
    
    private static class NullWrite extends Writer
    {
        @Override
        public void close() {
        }
        
        @Override
        public void flush() {
        }
        
        @Override
        public void write(final char[] b) {
        }
        
        @Override
        public void write(final char[] b, final int o, final int l) {
        }
        
        @Override
        public void write(final int b) {
        }
        
        @Override
        public void write(final String s) {
        }
        
        @Override
        public void write(final String s, final int o, final int l) {
        }
    }
}
