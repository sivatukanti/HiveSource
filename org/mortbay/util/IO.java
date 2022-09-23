// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;
import org.mortbay.log.Log;
import java.io.OutputStream;
import java.io.InputStream;
import org.mortbay.thread.BoundedThreadPool;

public class IO extends BoundedThreadPool
{
    public static final String CRLF = "\r\n";
    public static final byte[] CRLF_BYTES;
    public static int bufferSize;
    private static NullOS __nullStream;
    private static ClosedIS __closedStream;
    private static NullWrite __nullWriter;
    
    public static IO instance() {
        return Singleton.__instance;
    }
    
    public static void copyThread(final InputStream in, final OutputStream out) {
        try {
            final Job job = new Job(in, out);
            if (!instance().dispatch(job)) {
                job.run();
            }
        }
        catch (Exception e) {
            Log.warn(e);
        }
    }
    
    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        copy(in, out, -1L);
    }
    
    public static void copyThread(final Reader in, final Writer out) {
        try {
            final Job job = new Job(in, out);
            if (!instance().dispatch(job)) {
                job.run();
            }
        }
        catch (Exception e) {
            Log.warn(e);
        }
    }
    
    public static void copy(final Reader in, final Writer out) throws IOException {
        copy(in, out, -1L);
    }
    
    public static void copy(final InputStream in, final OutputStream out, long byteCount) throws IOException {
        final byte[] buffer = new byte[IO.bufferSize];
        int len = IO.bufferSize;
        if (byteCount >= 0L) {
            while (byteCount > 0L) {
                if (byteCount < IO.bufferSize) {
                    len = in.read(buffer, 0, (int)byteCount);
                }
                else {
                    len = in.read(buffer, 0, IO.bufferSize);
                }
                if (len == -1) {
                    break;
                }
                byteCount -= len;
                out.write(buffer, 0, len);
            }
        }
        else {
            while (true) {
                len = in.read(buffer, 0, IO.bufferSize);
                if (len < 0) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
    }
    
    public static void copy(final Reader in, final Writer out, long byteCount) throws IOException {
        final char[] buffer = new char[IO.bufferSize];
        int len = IO.bufferSize;
        if (byteCount >= 0L) {
            while (byteCount > 0L) {
                if (byteCount < IO.bufferSize) {
                    len = in.read(buffer, 0, (int)byteCount);
                }
                else {
                    len = in.read(buffer, 0, IO.bufferSize);
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
                len = in.read(buffer, 0, IO.bufferSize);
                if (len == -1) {
                    break;
                }
                out.write(buffer, 0, len);
            }
        }
        else {
            while (true) {
                len = in.read(buffer, 0, IO.bufferSize);
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
        final FileInputStream in = new FileInputStream(from);
        final FileOutputStream out = new FileOutputStream(to);
        copy(in, out);
        in.close();
        out.close();
    }
    
    public static String toString(final InputStream in) throws IOException {
        return toString(in, null);
    }
    
    public static String toString(final InputStream in, final String encoding) throws IOException {
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
    
    public static void close(final InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        }
        catch (IOException e) {
            Log.ignore(e);
        }
    }
    
    public static byte[] readBytes(final InputStream in) throws IOException {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        copy(in, bout);
        return bout.toByteArray();
    }
    
    public static void close(final OutputStream os) {
        try {
            if (os != null) {
                os.close();
            }
        }
        catch (IOException e) {
            Log.ignore(e);
        }
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
    
    static {
        CRLF_BYTES = new byte[] { 13, 10 };
        IO.bufferSize = 16384;
        IO.__nullStream = new NullOS();
        IO.__closedStream = new ClosedIS();
        IO.__nullWriter = new NullWrite();
    }
    
    private static class Singleton
    {
        static final IO __instance;
        
        static {
            __instance = new IO();
            try {
                Singleton.__instance.start();
            }
            catch (Exception e) {
                Log.warn(e);
                System.exit(1);
            }
        }
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
                Log.ignore(e);
                try {
                    if (this.out != null) {
                        this.out.close();
                    }
                    if (this.write != null) {
                        this.write.close();
                    }
                }
                catch (IOException e2) {
                    Log.ignore(e2);
                }
            }
        }
    }
    
    private static class NullOS extends OutputStream
    {
        public void close() {
        }
        
        public void flush() {
        }
        
        public void write(final byte[] b) {
        }
        
        public void write(final byte[] b, final int i, final int l) {
        }
        
        public void write(final int b) {
        }
    }
    
    private static class ClosedIS extends InputStream
    {
        public int read() throws IOException {
            return -1;
        }
    }
    
    private static class NullWrite extends Writer
    {
        public void close() {
        }
        
        public void flush() {
        }
        
        public void write(final char[] b) {
        }
        
        public void write(final char[] b, final int o, final int l) {
        }
        
        public void write(final int b) {
        }
        
        public void write(final String s) {
        }
        
        public void write(final String s, final int o, final int l) {
        }
    }
}
