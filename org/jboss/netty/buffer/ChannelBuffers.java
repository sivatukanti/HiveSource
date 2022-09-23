// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.buffer;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CharacterCodingException;
import org.jboss.netty.util.CharsetUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class ChannelBuffers
{
    public static final ByteOrder BIG_ENDIAN;
    public static final ByteOrder LITTLE_ENDIAN;
    public static final ChannelBuffer EMPTY_BUFFER;
    private static final char[] HEXDUMP_TABLE;
    
    public static ChannelBuffer buffer(final int capacity) {
        return buffer(ChannelBuffers.BIG_ENDIAN, capacity);
    }
    
    public static ChannelBuffer buffer(final ByteOrder endianness, final int capacity) {
        if (endianness == ChannelBuffers.BIG_ENDIAN) {
            if (capacity == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new BigEndianHeapChannelBuffer(capacity);
        }
        else {
            if (endianness != ChannelBuffers.LITTLE_ENDIAN) {
                throw new NullPointerException("endianness");
            }
            if (capacity == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new LittleEndianHeapChannelBuffer(capacity);
        }
    }
    
    public static ChannelBuffer directBuffer(final int capacity) {
        return directBuffer(ChannelBuffers.BIG_ENDIAN, capacity);
    }
    
    public static ChannelBuffer directBuffer(final ByteOrder endianness, final int capacity) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (capacity == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final ChannelBuffer buffer = new ByteBufferBackedChannelBuffer(ByteBuffer.allocateDirect(capacity).order(endianness));
        buffer.clear();
        return buffer;
    }
    
    public static ChannelBuffer dynamicBuffer() {
        return dynamicBuffer(ChannelBuffers.BIG_ENDIAN, 256);
    }
    
    public static ChannelBuffer dynamicBuffer(final ChannelBufferFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        return new DynamicChannelBuffer(factory.getDefaultOrder(), 256, factory);
    }
    
    public static ChannelBuffer dynamicBuffer(final int estimatedLength) {
        return dynamicBuffer(ChannelBuffers.BIG_ENDIAN, estimatedLength);
    }
    
    public static ChannelBuffer dynamicBuffer(final ByteOrder endianness, final int estimatedLength) {
        return new DynamicChannelBuffer(endianness, estimatedLength);
    }
    
    public static ChannelBuffer dynamicBuffer(final int estimatedLength, final ChannelBufferFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        return new DynamicChannelBuffer(factory.getDefaultOrder(), estimatedLength, factory);
    }
    
    public static ChannelBuffer dynamicBuffer(final ByteOrder endianness, final int estimatedLength, final ChannelBufferFactory factory) {
        return new DynamicChannelBuffer(endianness, estimatedLength, factory);
    }
    
    public static ChannelBuffer wrappedBuffer(final byte[] array) {
        return wrappedBuffer(ChannelBuffers.BIG_ENDIAN, array);
    }
    
    public static ChannelBuffer wrappedBuffer(final ByteOrder endianness, final byte[] array) {
        if (endianness == ChannelBuffers.BIG_ENDIAN) {
            if (array.length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new BigEndianHeapChannelBuffer(array);
        }
        else {
            if (endianness != ChannelBuffers.LITTLE_ENDIAN) {
                throw new NullPointerException("endianness");
            }
            if (array.length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new LittleEndianHeapChannelBuffer(array);
        }
    }
    
    public static ChannelBuffer wrappedBuffer(final byte[] array, final int offset, final int length) {
        return wrappedBuffer(ChannelBuffers.BIG_ENDIAN, array, offset, length);
    }
    
    public static ChannelBuffer wrappedBuffer(final ByteOrder endianness, final byte[] array, final int offset, final int length) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (offset == 0) {
            if (length == array.length) {
                return wrappedBuffer(endianness, array);
            }
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new TruncatedChannelBuffer(wrappedBuffer(endianness, array), length);
        }
        else {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new SlicedChannelBuffer(wrappedBuffer(endianness, array), offset, length);
        }
    }
    
    public static ChannelBuffer wrappedBuffer(final ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        if (buffer.hasArray()) {
            return wrappedBuffer(buffer.order(), buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
        }
        return new ByteBufferBackedChannelBuffer(buffer);
    }
    
    public static ChannelBuffer wrappedBuffer(final ChannelBuffer buffer) {
        if (buffer.readable()) {
            return buffer.slice();
        }
        return ChannelBuffers.EMPTY_BUFFER;
    }
    
    public static ChannelBuffer wrappedBuffer(final byte[]... arrays) {
        return wrappedBuffer(ChannelBuffers.BIG_ENDIAN, arrays);
    }
    
    public static ChannelBuffer wrappedBuffer(final ByteOrder endianness, final byte[]... arrays) {
        switch (arrays.length) {
            case 0: {
                break;
            }
            case 1: {
                if (arrays[0].length != 0) {
                    return wrappedBuffer(endianness, arrays[0]);
                }
                break;
            }
            default: {
                final List<ChannelBuffer> components = new ArrayList<ChannelBuffer>(arrays.length);
                for (final byte[] a : arrays) {
                    if (a == null) {
                        break;
                    }
                    if (a.length > 0) {
                        components.add(wrappedBuffer(endianness, a));
                    }
                }
                return compositeBuffer(endianness, components, false);
            }
        }
        return ChannelBuffers.EMPTY_BUFFER;
    }
    
    private static ChannelBuffer compositeBuffer(final ByteOrder endianness, final List<ChannelBuffer> components, final boolean gathering) {
        switch (components.size()) {
            case 0: {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            case 1: {
                return components.get(0);
            }
            default: {
                return new CompositeChannelBuffer(endianness, components, gathering);
            }
        }
    }
    
    public static ChannelBuffer wrappedBuffer(final ChannelBuffer... buffers) {
        return wrappedBuffer(false, buffers);
    }
    
    public static ChannelBuffer wrappedBuffer(final boolean gathering, final ChannelBuffer... buffers) {
        switch (buffers.length) {
            case 0: {
                break;
            }
            case 1: {
                if (buffers[0].readable()) {
                    return wrappedBuffer(buffers[0]);
                }
                break;
            }
            default: {
                ByteOrder order = null;
                final List<ChannelBuffer> components = new ArrayList<ChannelBuffer>(buffers.length);
                for (final ChannelBuffer c : buffers) {
                    if (c == null) {
                        break;
                    }
                    if (c.readable()) {
                        if (order != null) {
                            if (!order.equals(c.order())) {
                                throw new IllegalArgumentException("inconsistent byte order");
                            }
                        }
                        else {
                            order = c.order();
                        }
                        if (c instanceof CompositeChannelBuffer) {
                            components.addAll(((CompositeChannelBuffer)c).decompose(c.readerIndex(), c.readableBytes()));
                        }
                        else {
                            components.add(c.slice());
                        }
                    }
                }
                return compositeBuffer(order, components, gathering);
            }
        }
        return ChannelBuffers.EMPTY_BUFFER;
    }
    
    public static ChannelBuffer wrappedBuffer(final ByteBuffer... buffers) {
        return wrappedBuffer(false, buffers);
    }
    
    public static ChannelBuffer wrappedBuffer(final boolean gathering, final ByteBuffer... buffers) {
        switch (buffers.length) {
            case 0: {
                break;
            }
            case 1: {
                if (buffers[0].hasRemaining()) {
                    return wrappedBuffer(buffers[0]);
                }
                break;
            }
            default: {
                ByteOrder order = null;
                final List<ChannelBuffer> components = new ArrayList<ChannelBuffer>(buffers.length);
                for (final ByteBuffer b : buffers) {
                    if (b == null) {
                        break;
                    }
                    if (b.hasRemaining()) {
                        if (order != null) {
                            if (!order.equals(b.order())) {
                                throw new IllegalArgumentException("inconsistent byte order");
                            }
                        }
                        else {
                            order = b.order();
                        }
                        components.add(wrappedBuffer(b));
                    }
                }
                return compositeBuffer(order, components, gathering);
            }
        }
        return ChannelBuffers.EMPTY_BUFFER;
    }
    
    public static ChannelBuffer copiedBuffer(final byte[] array) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, array);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final byte[] array) {
        if (endianness == ChannelBuffers.BIG_ENDIAN) {
            if (array.length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new BigEndianHeapChannelBuffer(array.clone());
        }
        else {
            if (endianness != ChannelBuffers.LITTLE_ENDIAN) {
                throw new NullPointerException("endianness");
            }
            if (array.length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new LittleEndianHeapChannelBuffer(array.clone());
        }
    }
    
    public static ChannelBuffer copiedBuffer(final byte[] array, final int offset, final int length) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, array, offset, length);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final byte[] array, final int offset, final int length) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final byte[] copy = new byte[length];
        System.arraycopy(array, offset, copy, 0, length);
        return wrappedBuffer(endianness, copy);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteBuffer buffer) {
        final int length = buffer.remaining();
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        final byte[] copy = new byte[length];
        final int position = buffer.position();
        try {
            buffer.get(copy);
        }
        finally {
            buffer.position(position);
        }
        return wrappedBuffer(buffer.order(), copy);
    }
    
    public static ChannelBuffer copiedBuffer(final ChannelBuffer buffer) {
        if (buffer.readable()) {
            return buffer.copy();
        }
        return ChannelBuffers.EMPTY_BUFFER;
    }
    
    public static ChannelBuffer copiedBuffer(final byte[]... arrays) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, arrays);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final byte[]... arrays) {
        switch (arrays.length) {
            case 0: {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            case 1: {
                if (arrays[0].length == 0) {
                    return ChannelBuffers.EMPTY_BUFFER;
                }
                return copiedBuffer(endianness, arrays[0]);
            }
            default: {
                int length = 0;
                for (final byte[] a : arrays) {
                    if (Integer.MAX_VALUE - length < a.length) {
                        throw new IllegalArgumentException("The total length of the specified arrays is too big.");
                    }
                    length += a.length;
                }
                if (length == 0) {
                    return ChannelBuffers.EMPTY_BUFFER;
                }
                final byte[] mergedArray = new byte[length];
                int i = 0;
                int j = 0;
                while (i < arrays.length) {
                    final byte[] a = arrays[i];
                    System.arraycopy(a, 0, mergedArray, j, a.length);
                    j += a.length;
                    ++i;
                }
                return wrappedBuffer(endianness, mergedArray);
            }
        }
    }
    
    public static ChannelBuffer copiedBuffer(final ChannelBuffer... buffers) {
        switch (buffers.length) {
            case 0: {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            case 1: {
                return copiedBuffer(buffers[0]);
            }
            default: {
                final ChannelBuffer[] copiedBuffers = new ChannelBuffer[buffers.length];
                for (int i = 0; i < buffers.length; ++i) {
                    copiedBuffers[i] = copiedBuffer(buffers[i]);
                }
                return wrappedBuffer(false, copiedBuffers);
            }
        }
    }
    
    public static ChannelBuffer copiedBuffer(final ByteBuffer... buffers) {
        switch (buffers.length) {
            case 0: {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            case 1: {
                return copiedBuffer(buffers[0]);
            }
            default: {
                final ChannelBuffer[] copiedBuffers = new ChannelBuffer[buffers.length];
                for (int i = 0; i < buffers.length; ++i) {
                    copiedBuffers[i] = copiedBuffer(buffers[i]);
                }
                return wrappedBuffer(false, copiedBuffers);
            }
        }
    }
    
    public static ChannelBuffer copiedBuffer(final CharSequence string, final Charset charset) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, string, charset);
    }
    
    public static ChannelBuffer copiedBuffer(final CharSequence string, final int offset, final int length, final Charset charset) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, string, offset, length, charset);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final CharSequence string, final Charset charset) {
        if (string == null) {
            throw new NullPointerException("string");
        }
        if (string instanceof CharBuffer) {
            return copiedBuffer(endianness, (CharBuffer)string, charset);
        }
        return copiedBuffer(endianness, CharBuffer.wrap(string), charset);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final CharSequence string, final int offset, final int length, final Charset charset) {
        if (string == null) {
            throw new NullPointerException("string");
        }
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        if (!(string instanceof CharBuffer)) {
            return copiedBuffer(endianness, CharBuffer.wrap(string, offset, offset + length), charset);
        }
        CharBuffer buf = (CharBuffer)string;
        if (buf.hasArray()) {
            return copiedBuffer(endianness, buf.array(), buf.arrayOffset() + buf.position() + offset, length, charset);
        }
        buf = buf.slice();
        buf.limit(length);
        buf.position(offset);
        return copiedBuffer(endianness, buf, charset);
    }
    
    public static ChannelBuffer copiedBuffer(final char[] array, final Charset charset) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, array, 0, array.length, charset);
    }
    
    public static ChannelBuffer copiedBuffer(final char[] array, final int offset, final int length, final Charset charset) {
        return copiedBuffer(ChannelBuffers.BIG_ENDIAN, array, offset, length, charset);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final char[] array, final Charset charset) {
        return copiedBuffer(endianness, array, 0, array.length, charset);
    }
    
    public static ChannelBuffer copiedBuffer(final ByteOrder endianness, final char[] array, final int offset, final int length, final Charset charset) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        return copiedBuffer(endianness, CharBuffer.wrap(array, offset, length), charset);
    }
    
    private static ChannelBuffer copiedBuffer(final ByteOrder endianness, final CharBuffer buffer, final Charset charset) {
        final CharBuffer src = buffer;
        final ByteBuffer dst = encodeString(src, charset);
        final ChannelBuffer result = wrappedBuffer(endianness, dst.array());
        result.writerIndex(dst.remaining());
        return result;
    }
    
    public static ChannelBuffer unmodifiableBuffer(ChannelBuffer buffer) {
        if (buffer instanceof ReadOnlyChannelBuffer) {
            buffer = ((ReadOnlyChannelBuffer)buffer).unwrap();
        }
        return new ReadOnlyChannelBuffer(buffer);
    }
    
    public static ChannelBuffer hexDump(final String hexString) {
        final int len = hexString.length();
        final byte[] hexData = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            hexData[i / 2] = (byte)((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return wrappedBuffer(hexData);
    }
    
    public static String hexDump(final ChannelBuffer buffer) {
        return hexDump(buffer, buffer.readerIndex(), buffer.readableBytes());
    }
    
    public static String hexDump(final ChannelBuffer buffer, final int fromIndex, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length == 0) {
            return "";
        }
        final int endIndex = fromIndex + length;
        final char[] buf = new char[length << 1];
        for (int srcIdx = fromIndex, dstIdx = 0; srcIdx < endIndex; ++srcIdx, dstIdx += 2) {
            System.arraycopy(ChannelBuffers.HEXDUMP_TABLE, buffer.getUnsignedByte(srcIdx) << 1, buf, dstIdx, 2);
        }
        return new String(buf);
    }
    
    public static int hashCode(final ChannelBuffer buffer) {
        final int aLen = buffer.readableBytes();
        final int intCount = aLen >>> 2;
        final int byteCount = aLen & 0x3;
        int hashCode = 1;
        int arrayIndex = buffer.readerIndex();
        if (buffer.order() == ChannelBuffers.BIG_ENDIAN) {
            for (int i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + buffer.getInt(arrayIndex);
                arrayIndex += 4;
            }
        }
        else {
            for (int i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + swapInt(buffer.getInt(arrayIndex));
                arrayIndex += 4;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            hashCode = 31 * hashCode + buffer.getByte(arrayIndex++);
        }
        if (hashCode == 0) {
            hashCode = 1;
        }
        return hashCode;
    }
    
    public static boolean equals(final ChannelBuffer bufferA, final ChannelBuffer bufferB) {
        final int aLen = bufferA.readableBytes();
        if (aLen != bufferB.readableBytes()) {
            return false;
        }
        final int longCount = aLen >>> 3;
        final int byteCount = aLen & 0x7;
        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();
        if (bufferA.order() == bufferB.order()) {
            for (int i = longCount; i > 0; --i) {
                if (bufferA.getLong(aIndex) != bufferB.getLong(bIndex)) {
                    return false;
                }
                aIndex += 8;
                bIndex += 8;
            }
        }
        else {
            for (int i = longCount; i > 0; --i) {
                if (bufferA.getLong(aIndex) != swapLong(bufferB.getLong(bIndex))) {
                    return false;
                }
                aIndex += 8;
                bIndex += 8;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            if (bufferA.getByte(aIndex) != bufferB.getByte(bIndex)) {
                return false;
            }
            ++aIndex;
            ++bIndex;
        }
        return true;
    }
    
    public static int compare(final ChannelBuffer bufferA, final ChannelBuffer bufferB) {
        final int aLen = bufferA.readableBytes();
        final int bLen = bufferB.readableBytes();
        final int minLength = Math.min(aLen, bLen);
        final int uintCount = minLength >>> 2;
        final int byteCount = minLength & 0x3;
        int aIndex = bufferA.readerIndex();
        int bIndex = bufferB.readerIndex();
        if (bufferA.order() == bufferB.order()) {
            for (int i = uintCount; i > 0; --i) {
                final long va = bufferA.getUnsignedInt(aIndex);
                final long vb = bufferB.getUnsignedInt(bIndex);
                if (va > vb) {
                    return 1;
                }
                if (va < vb) {
                    return -1;
                }
                aIndex += 4;
                bIndex += 4;
            }
        }
        else {
            for (int i = uintCount; i > 0; --i) {
                final long va = bufferA.getUnsignedInt(aIndex);
                final long vb = (long)swapInt(bufferB.getInt(bIndex)) & 0xFFFFFFFFL;
                if (va > vb) {
                    return 1;
                }
                if (va < vb) {
                    return -1;
                }
                aIndex += 4;
                bIndex += 4;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            final short va2 = bufferA.getUnsignedByte(aIndex);
            final short vb2 = bufferB.getUnsignedByte(bIndex);
            if (va2 > vb2) {
                return 1;
            }
            if (va2 < vb2) {
                return -1;
            }
            ++aIndex;
            ++bIndex;
        }
        return aLen - bLen;
    }
    
    public static int indexOf(final ChannelBuffer buffer, final int fromIndex, final int toIndex, final byte value) {
        if (fromIndex <= toIndex) {
            return firstIndexOf(buffer, fromIndex, toIndex, value);
        }
        return lastIndexOf(buffer, fromIndex, toIndex, value);
    }
    
    public static int indexOf(final ChannelBuffer buffer, final int fromIndex, final int toIndex, final ChannelBufferIndexFinder indexFinder) {
        if (fromIndex <= toIndex) {
            return firstIndexOf(buffer, fromIndex, toIndex, indexFinder);
        }
        return lastIndexOf(buffer, fromIndex, toIndex, indexFinder);
    }
    
    public static short swapShort(final short value) {
        return (short)(value << 8 | (value >>> 8 & 0xFF));
    }
    
    public static int swapMedium(final int value) {
        return (value << 16 & 0xFF0000) | (value & 0xFF00) | (value >>> 16 & 0xFF);
    }
    
    public static int swapInt(final int value) {
        return swapShort((short)value) << 16 | (swapShort((short)(value >>> 16)) & 0xFFFF);
    }
    
    public static long swapLong(final long value) {
        return (long)swapInt((int)value) << 32 | ((long)swapInt((int)(value >>> 32)) & 0xFFFFFFFFL);
    }
    
    private static int firstIndexOf(final ChannelBuffer buffer, int fromIndex, final int toIndex, final byte value) {
        fromIndex = Math.max(fromIndex, 0);
        if (fromIndex >= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        for (int i = fromIndex; i < toIndex; ++i) {
            if (buffer.getByte(i) == value) {
                return i;
            }
        }
        return -1;
    }
    
    private static int lastIndexOf(final ChannelBuffer buffer, int fromIndex, final int toIndex, final byte value) {
        fromIndex = Math.min(fromIndex, buffer.capacity());
        if (fromIndex < 0 || buffer.capacity() == 0) {
            return -1;
        }
        for (int i = fromIndex - 1; i >= toIndex; --i) {
            if (buffer.getByte(i) == value) {
                return i;
            }
        }
        return -1;
    }
    
    private static int firstIndexOf(final ChannelBuffer buffer, int fromIndex, final int toIndex, final ChannelBufferIndexFinder indexFinder) {
        fromIndex = Math.max(fromIndex, 0);
        if (fromIndex >= toIndex || buffer.capacity() == 0) {
            return -1;
        }
        for (int i = fromIndex; i < toIndex; ++i) {
            if (indexFinder.find(buffer, i)) {
                return i;
            }
        }
        return -1;
    }
    
    private static int lastIndexOf(final ChannelBuffer buffer, int fromIndex, final int toIndex, final ChannelBufferIndexFinder indexFinder) {
        fromIndex = Math.min(fromIndex, buffer.capacity());
        if (fromIndex < 0 || buffer.capacity() == 0) {
            return -1;
        }
        for (int i = fromIndex - 1; i >= toIndex; --i) {
            if (indexFinder.find(buffer, i)) {
                return i;
            }
        }
        return -1;
    }
    
    static ByteBuffer encodeString(final CharBuffer src, final Charset charset) {
        final CharsetEncoder encoder = CharsetUtil.getEncoder(charset);
        final ByteBuffer dst = ByteBuffer.allocate((int)(src.remaining() * (double)encoder.maxBytesPerChar()));
        try {
            CoderResult cr = encoder.encode(src, dst, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = encoder.flush(dst);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
        }
        catch (CharacterCodingException x) {
            throw new IllegalStateException(x);
        }
        dst.flip();
        return dst;
    }
    
    static String decodeString(final ByteBuffer src, final Charset charset) {
        final CharsetDecoder decoder = CharsetUtil.getDecoder(charset);
        final CharBuffer dst = CharBuffer.allocate((int)(src.remaining() * (double)decoder.maxCharsPerByte()));
        try {
            CoderResult cr = decoder.decode(src, dst, true);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
            cr = decoder.flush(dst);
            if (!cr.isUnderflow()) {
                cr.throwException();
            }
        }
        catch (CharacterCodingException x) {
            throw new IllegalStateException(x);
        }
        return dst.flip().toString();
    }
    
    private ChannelBuffers() {
    }
    
    static {
        BIG_ENDIAN = ByteOrder.BIG_ENDIAN;
        LITTLE_ENDIAN = ByteOrder.LITTLE_ENDIAN;
        EMPTY_BUFFER = new EmptyChannelBuffer();
        HEXDUMP_TABLE = new char[1024];
        final char[] DIGITS = "0123456789abcdef".toCharArray();
        for (int i = 0; i < 256; ++i) {
            ChannelBuffers.HEXDUMP_TABLE[i << 1] = DIGITS[i >>> 4 & 0xF];
            ChannelBuffers.HEXDUMP_TABLE[(i << 1) + 1] = DIGITS[i & 0xF];
        }
    }
}
