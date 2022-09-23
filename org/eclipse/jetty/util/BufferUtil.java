// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.resource.Resource;
import java.nio.MappedByteBuffer;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.BufferOverflowException;
import java.util.Arrays;
import java.nio.ByteBuffer;

public class BufferUtil
{
    static final int TEMP_BUFFER_SIZE = 4096;
    static final byte SPACE = 32;
    static final byte MINUS = 45;
    static final byte[] DIGIT;
    public static final ByteBuffer EMPTY_BUFFER;
    private static final int[] decDivisors;
    private static final int[] hexDivisors;
    private static final long[] decDivisorsL;
    
    public static ByteBuffer allocate(final int capacity) {
        final ByteBuffer buf = ByteBuffer.allocate(capacity);
        buf.limit(0);
        return buf;
    }
    
    public static ByteBuffer allocateDirect(final int capacity) {
        final ByteBuffer buf = ByteBuffer.allocateDirect(capacity);
        buf.limit(0);
        return buf;
    }
    
    public static void clear(final ByteBuffer buffer) {
        if (buffer != null) {
            buffer.position(0);
            buffer.limit(0);
        }
    }
    
    public static void clearToFill(final ByteBuffer buffer) {
        if (buffer != null) {
            buffer.position(0);
            buffer.limit(buffer.capacity());
        }
    }
    
    public static int flipToFill(final ByteBuffer buffer) {
        final int position = buffer.position();
        final int limit = buffer.limit();
        if (position == limit) {
            buffer.position(0);
            buffer.limit(buffer.capacity());
            return 0;
        }
        final int capacity = buffer.capacity();
        if (limit == capacity) {
            buffer.compact();
            return 0;
        }
        buffer.position(limit);
        buffer.limit(capacity);
        return position;
    }
    
    public static void flipToFlush(final ByteBuffer buffer, final int position) {
        buffer.limit(buffer.position());
        buffer.position(position);
    }
    
    public static byte[] toArray(final ByteBuffer buffer) {
        if (buffer.hasArray()) {
            final byte[] array = buffer.array();
            final int from = buffer.arrayOffset() + buffer.position();
            return Arrays.copyOfRange(array, from, from + buffer.remaining());
        }
        final byte[] to = new byte[buffer.remaining()];
        buffer.slice().get(to);
        return to;
    }
    
    public static boolean isEmpty(final ByteBuffer buf) {
        return buf == null || buf.remaining() == 0;
    }
    
    public static boolean hasContent(final ByteBuffer buf) {
        return buf != null && buf.remaining() > 0;
    }
    
    public static boolean isFull(final ByteBuffer buf) {
        return buf != null && buf.limit() == buf.capacity();
    }
    
    public static int length(final ByteBuffer buffer) {
        return (buffer == null) ? 0 : buffer.remaining();
    }
    
    public static int space(final ByteBuffer buffer) {
        if (buffer == null) {
            return 0;
        }
        return buffer.capacity() - buffer.limit();
    }
    
    public static boolean compact(final ByteBuffer buffer) {
        if (buffer.position() == 0) {
            return false;
        }
        final boolean full = buffer.limit() == buffer.capacity();
        buffer.compact().flip();
        return full && buffer.limit() < buffer.capacity();
    }
    
    public static int put(final ByteBuffer from, final ByteBuffer to) {
        final int remaining = from.remaining();
        int put;
        if (remaining > 0) {
            if (remaining <= to.remaining()) {
                to.put(from);
                put = remaining;
                from.position(from.limit());
            }
            else if (from.hasArray()) {
                put = to.remaining();
                to.put(from.array(), from.arrayOffset() + from.position(), put);
                from.position(from.position() + put);
            }
            else {
                put = to.remaining();
                final ByteBuffer slice = from.slice();
                slice.limit(put);
                to.put(slice);
                from.position(from.position() + put);
            }
        }
        else {
            put = 0;
        }
        return put;
    }
    
    @Deprecated
    public static int flipPutFlip(final ByteBuffer from, final ByteBuffer to) {
        return append(to, from);
    }
    
    public static void append(final ByteBuffer to, final byte[] b, final int off, final int len) throws BufferOverflowException {
        final int pos = flipToFill(to);
        try {
            to.put(b, off, len);
        }
        finally {
            flipToFlush(to, pos);
        }
    }
    
    public static void append(final ByteBuffer to, final byte b) {
        final int pos = flipToFill(to);
        try {
            to.put(b);
        }
        finally {
            flipToFlush(to, pos);
        }
    }
    
    public static int append(final ByteBuffer to, final ByteBuffer b) {
        final int pos = flipToFill(to);
        try {
            return put(b, to);
        }
        finally {
            flipToFlush(to, pos);
        }
    }
    
    public static int fill(final ByteBuffer to, final byte[] b, final int off, final int len) {
        final int pos = flipToFill(to);
        try {
            final int remaining = to.remaining();
            final int take = (remaining < len) ? remaining : len;
            to.put(b, off, take);
            return take;
        }
        finally {
            flipToFlush(to, pos);
        }
    }
    
    public static void readFrom(final File file, final ByteBuffer buffer) throws IOException {
        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        Throwable x0 = null;
        try {
            final FileChannel channel = raf.getChannel();
            for (long needed = raf.length(); needed > 0L && buffer.hasRemaining(); needed -= channel.read(buffer)) {}
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            $closeResource(x0, raf);
        }
    }
    
    public static void readFrom(final InputStream is, final int needed, final ByteBuffer buffer) throws IOException {
        final ByteBuffer tmp = allocate(8192);
        while (needed > 0 && buffer.hasRemaining()) {
            final int l = is.read(tmp.array(), 0, 8192);
            if (l < 0) {
                break;
            }
            tmp.position(0);
            tmp.limit(l);
            buffer.put(tmp);
        }
    }
    
    public static void writeTo(final ByteBuffer buffer, final OutputStream out) throws IOException {
        if (buffer.hasArray()) {
            out.write(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
            buffer.position(buffer.position() + buffer.remaining());
        }
        else {
            final byte[] bytes = new byte[4096];
            while (buffer.hasRemaining()) {
                final int byteCountToWrite = Math.min(buffer.remaining(), 4096);
                buffer.get(bytes, 0, byteCountToWrite);
                out.write(bytes, 0, byteCountToWrite);
            }
        }
    }
    
    public static String toString(final ByteBuffer buffer) {
        return toString(buffer, StandardCharsets.ISO_8859_1);
    }
    
    public static String toUTF8String(final ByteBuffer buffer) {
        return toString(buffer, StandardCharsets.UTF_8);
    }
    
    public static String toString(final ByteBuffer buffer, final Charset charset) {
        if (buffer == null) {
            return null;
        }
        final byte[] array = (byte[])(buffer.hasArray() ? buffer.array() : null);
        if (array == null) {
            final byte[] to = new byte[buffer.remaining()];
            buffer.slice().get(to);
            return new String(to, 0, to.length, charset);
        }
        return new String(array, buffer.arrayOffset() + buffer.position(), buffer.remaining(), charset);
    }
    
    public static String toString(final ByteBuffer buffer, final int position, final int length, final Charset charset) {
        if (buffer == null) {
            return null;
        }
        final byte[] array = (byte[])(buffer.hasArray() ? buffer.array() : null);
        if (array == null) {
            final ByteBuffer ro = buffer.asReadOnlyBuffer();
            ro.position(position);
            ro.limit(position + length);
            final byte[] to = new byte[length];
            ro.get(to);
            return new String(to, 0, to.length, charset);
        }
        return new String(array, buffer.arrayOffset() + position, length, charset);
    }
    
    public static int toInt(final ByteBuffer buffer) {
        return toInt(buffer, buffer.position(), buffer.remaining());
    }
    
    public static int toInt(final ByteBuffer buffer, final int position, final int length) {
        int val = 0;
        boolean started = false;
        boolean minus = false;
        final int limit = position + length;
        if (length <= 0) {
            throw new NumberFormatException(toString(buffer, position, length, StandardCharsets.UTF_8));
        }
        for (int i = position; i < limit; ++i) {
            final byte b = buffer.get(i);
            if (b <= 32) {
                if (started) {
                    break;
                }
            }
            else if (b >= 48 && b <= 57) {
                val = val * 10 + (b - 48);
                started = true;
            }
            else {
                if (b != 45 || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(toString(buffer));
    }
    
    public static int takeInt(final ByteBuffer buffer) {
        int val = 0;
        boolean started = false;
        boolean minus = false;
        int i;
        for (i = buffer.position(); i < buffer.limit(); ++i) {
            final byte b = buffer.get(i);
            if (b <= 32) {
                if (started) {
                    break;
                }
            }
            else if (b >= 48 && b <= 57) {
                val = val * 10 + (b - 48);
                started = true;
            }
            else {
                if (b != 45 || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            buffer.position(i);
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(toString(buffer));
    }
    
    public static long toLong(final ByteBuffer buffer) {
        long val = 0L;
        boolean started = false;
        boolean minus = false;
        for (int i = buffer.position(); i < buffer.limit(); ++i) {
            final byte b = buffer.get(i);
            if (b <= 32) {
                if (started) {
                    break;
                }
            }
            else if (b >= 48 && b <= 57) {
                val = val * 10L + (b - 48);
                started = true;
            }
            else {
                if (b != 45 || started) {
                    break;
                }
                minus = true;
            }
        }
        if (started) {
            return minus ? (-val) : val;
        }
        throw new NumberFormatException(toString(buffer));
    }
    
    public static void putHexInt(final ByteBuffer buffer, int n) {
        if (n < 0) {
            buffer.put((byte)45);
            if (n == Integer.MIN_VALUE) {
                buffer.put((byte)56);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                buffer.put((byte)48);
                return;
            }
            n = -n;
        }
        if (n < 16) {
            buffer.put(BufferUtil.DIGIT[n]);
        }
        else {
            boolean started = false;
            for (final int hexDivisor : BufferUtil.hexDivisors) {
                if (n < hexDivisor) {
                    if (started) {
                        buffer.put((byte)48);
                    }
                }
                else {
                    started = true;
                    final int d = n / hexDivisor;
                    buffer.put(BufferUtil.DIGIT[d]);
                    n -= d * hexDivisor;
                }
            }
        }
    }
    
    public static void putDecInt(final ByteBuffer buffer, int n) {
        if (n < 0) {
            buffer.put((byte)45);
            if (n == Integer.MIN_VALUE) {
                buffer.put((byte)50);
                n = 147483648;
            }
            else {
                n = -n;
            }
        }
        if (n < 10) {
            buffer.put(BufferUtil.DIGIT[n]);
        }
        else {
            boolean started = false;
            for (final int decDivisor : BufferUtil.decDivisors) {
                if (n < decDivisor) {
                    if (started) {
                        buffer.put((byte)48);
                    }
                }
                else {
                    started = true;
                    final int d = n / decDivisor;
                    buffer.put(BufferUtil.DIGIT[d]);
                    n -= d * decDivisor;
                }
            }
        }
    }
    
    public static void putDecLong(final ByteBuffer buffer, long n) {
        if (n < 0L) {
            buffer.put((byte)45);
            if (n == Long.MIN_VALUE) {
                buffer.put((byte)57);
                n = 223372036854775808L;
            }
            else {
                n = -n;
            }
        }
        if (n < 10L) {
            buffer.put(BufferUtil.DIGIT[(int)n]);
        }
        else {
            boolean started = false;
            for (final long aDecDivisorsL : BufferUtil.decDivisorsL) {
                if (n < aDecDivisorsL) {
                    if (started) {
                        buffer.put((byte)48);
                    }
                }
                else {
                    started = true;
                    final long d = n / aDecDivisorsL;
                    buffer.put(BufferUtil.DIGIT[(int)d]);
                    n -= d * aDecDivisorsL;
                }
            }
        }
    }
    
    public static ByteBuffer toBuffer(final int value) {
        final ByteBuffer buf = ByteBuffer.allocate(32);
        putDecInt(buf, value);
        return buf;
    }
    
    public static ByteBuffer toBuffer(final long value) {
        final ByteBuffer buf = ByteBuffer.allocate(32);
        putDecLong(buf, value);
        return buf;
    }
    
    public static ByteBuffer toBuffer(final String s) {
        return toBuffer(s, StandardCharsets.ISO_8859_1);
    }
    
    public static ByteBuffer toBuffer(final String s, final Charset charset) {
        if (s == null) {
            return BufferUtil.EMPTY_BUFFER;
        }
        return toBuffer(s.getBytes(charset));
    }
    
    public static ByteBuffer toBuffer(final byte[] array) {
        if (array == null) {
            return BufferUtil.EMPTY_BUFFER;
        }
        return toBuffer(array, 0, array.length);
    }
    
    public static ByteBuffer toBuffer(final byte[] array, final int offset, final int length) {
        if (array == null) {
            return BufferUtil.EMPTY_BUFFER;
        }
        return ByteBuffer.wrap(array, offset, length);
    }
    
    public static ByteBuffer toDirectBuffer(final String s) {
        return toDirectBuffer(s, StandardCharsets.ISO_8859_1);
    }
    
    public static ByteBuffer toDirectBuffer(final String s, final Charset charset) {
        if (s == null) {
            return BufferUtil.EMPTY_BUFFER;
        }
        final byte[] bytes = s.getBytes(charset);
        final ByteBuffer buf = ByteBuffer.allocateDirect(bytes.length);
        buf.put(bytes);
        buf.flip();
        return buf;
    }
    
    public static ByteBuffer toMappedBuffer(final File file) throws IOException {
        final FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
        Throwable x0 = null;
        try {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0L, file.length());
        }
        catch (Throwable t) {
            x0 = t;
            throw t;
        }
        finally {
            if (channel != null) {
                $closeResource(x0, channel);
            }
        }
    }
    
    public static boolean isMappedBuffer(final ByteBuffer buffer) {
        if (!(buffer instanceof MappedByteBuffer)) {
            return false;
        }
        final MappedByteBuffer mapped = (MappedByteBuffer)buffer;
        try {
            mapped.isLoaded();
            return true;
        }
        catch (UnsupportedOperationException e) {
            return false;
        }
    }
    
    public static ByteBuffer toBuffer(final Resource resource, final boolean direct) throws IOException {
        final int len = (int)resource.length();
        if (len < 0) {
            throw new IllegalArgumentException("invalid resource: " + String.valueOf(resource) + " len=" + len);
        }
        final ByteBuffer buffer = direct ? allocateDirect(len) : allocate(len);
        final int pos = flipToFill(buffer);
        if (resource.getFile() != null) {
            readFrom(resource.getFile(), buffer);
        }
        else {
            final InputStream is = resource.getInputStream();
            Throwable x0 = null;
            try {
                readFrom(is, len, buffer);
            }
            catch (Throwable t) {
                x0 = t;
                throw t;
            }
            finally {
                if (is != null) {
                    $closeResource(x0, is);
                }
            }
        }
        flipToFlush(buffer, pos);
        return buffer;
    }
    
    public static String toSummaryString(final ByteBuffer buffer) {
        if (buffer == null) {
            return "null";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("[p=");
        buf.append(buffer.position());
        buf.append(",l=");
        buf.append(buffer.limit());
        buf.append(",c=");
        buf.append(buffer.capacity());
        buf.append(",r=");
        buf.append(buffer.remaining());
        buf.append("]");
        return buf.toString();
    }
    
    public static String toDetailString(final ByteBuffer[] buffer) {
        final StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < buffer.length; ++i) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(toDetailString(buffer[i]));
        }
        builder.append(']');
        return builder.toString();
    }
    
    private static void idString(final ByteBuffer buffer, final StringBuilder out) {
        out.append(buffer.getClass().getSimpleName());
        out.append("@");
        if (buffer.hasArray() && buffer.arrayOffset() == 4) {
            out.append('T');
            final byte[] array = buffer.array();
            TypeUtil.toHex(array[0], out);
            TypeUtil.toHex(array[1], out);
            TypeUtil.toHex(array[2], out);
            TypeUtil.toHex(array[3], out);
        }
        else {
            out.append(Integer.toHexString(System.identityHashCode(buffer)));
        }
    }
    
    public static String toIDString(final ByteBuffer buffer) {
        final StringBuilder buf = new StringBuilder();
        idString(buffer, buf);
        return buf.toString();
    }
    
    public static String toDetailString(final ByteBuffer buffer) {
        if (buffer == null) {
            return "null";
        }
        final StringBuilder buf = new StringBuilder();
        idString(buffer, buf);
        buf.append("[p=");
        buf.append(buffer.position());
        buf.append(",l=");
        buf.append(buffer.limit());
        buf.append(",c=");
        buf.append(buffer.capacity());
        buf.append(",r=");
        buf.append(buffer.remaining());
        buf.append("]={");
        appendDebugString(buf, buffer);
        buf.append("}");
        return buf.toString();
    }
    
    private static void appendDebugString(final StringBuilder buf, final ByteBuffer buffer) {
        try {
            for (int i = 0; i < buffer.position(); ++i) {
                appendContentChar(buf, buffer.get(i));
                if (i == 16 && buffer.position() > 32) {
                    buf.append("...");
                    i = buffer.position() - 16;
                }
            }
            buf.append("<<<");
            for (int i = buffer.position(); i < buffer.limit(); ++i) {
                appendContentChar(buf, buffer.get(i));
                if (i == buffer.position() + 16 && buffer.limit() > buffer.position() + 32) {
                    buf.append("...");
                    i = buffer.limit() - 16;
                }
            }
            buf.append(">>>");
            final int limit = buffer.limit();
            buffer.limit(buffer.capacity());
            for (int j = limit; j < buffer.capacity(); ++j) {
                appendContentChar(buf, buffer.get(j));
                if (j == limit + 16 && buffer.capacity() > limit + 32) {
                    buf.append("...");
                    j = buffer.capacity() - 16;
                }
            }
            buffer.limit(limit);
        }
        catch (Throwable x) {
            Log.getRootLogger().ignore(x);
            buf.append("!!concurrent mod!!");
        }
    }
    
    private static void appendContentChar(final StringBuilder buf, final byte b) {
        if (b == 92) {
            buf.append("\\\\");
        }
        else if (b >= 32) {
            buf.append((char)b);
        }
        else if (b == 13) {
            buf.append("\\r");
        }
        else if (b == 10) {
            buf.append("\\n");
        }
        else if (b == 9) {
            buf.append("\\t");
        }
        else {
            buf.append("\\x").append(TypeUtil.toHexString(b));
        }
    }
    
    public static String toHexSummary(final ByteBuffer buffer) {
        if (buffer == null) {
            return "null";
        }
        final StringBuilder buf = new StringBuilder();
        buf.append("b[").append(buffer.remaining()).append("]=");
        for (int i = buffer.position(); i < buffer.limit(); ++i) {
            TypeUtil.toHex(buffer.get(i), buf);
            if (i == buffer.position() + 24 && buffer.limit() > buffer.position() + 32) {
                buf.append("...");
                i = buffer.limit() - 8;
            }
        }
        return buf.toString();
    }
    
    public static String toHexString(final ByteBuffer buffer) {
        if (buffer == null) {
            return "null";
        }
        return TypeUtil.toHexString(toArray(buffer));
    }
    
    public static void putCRLF(final ByteBuffer buffer) {
        buffer.put((byte)13);
        buffer.put((byte)10);
    }
    
    public static boolean isPrefix(final ByteBuffer prefix, final ByteBuffer buffer) {
        if (prefix.remaining() > buffer.remaining()) {
            return false;
        }
        int bi = buffer.position();
        for (int i = prefix.position(); i < prefix.limit(); ++i) {
            if (prefix.get(i) != buffer.get(bi++)) {
                return false;
            }
        }
        return true;
    }
    
    public static ByteBuffer ensureCapacity(final ByteBuffer buffer, final int capacity) {
        if (buffer == null) {
            return allocate(capacity);
        }
        if (buffer.capacity() >= capacity) {
            return buffer;
        }
        if (buffer.hasArray()) {
            return ByteBuffer.wrap(Arrays.copyOfRange(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset() + capacity), buffer.position(), buffer.remaining());
        }
        throw new UnsupportedOperationException();
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
        DIGIT = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70 };
        EMPTY_BUFFER = ByteBuffer.wrap(new byte[0]);
        decDivisors = new int[] { 1000000000, 100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1 };
        hexDivisors = new int[] { 268435456, 16777216, 1048576, 65536, 4096, 256, 16, 1 };
        decDivisorsL = new long[] { 1000000000000000000L, 100000000000000000L, 10000000000000000L, 1000000000000000L, 100000000000000L, 10000000000000L, 1000000000000L, 100000000000L, 10000000000L, 1000000000L, 100000000L, 10000000L, 1000000L, 100000L, 10000L, 1000L, 100L, 10L, 1L };
    }
}
