// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io;

import java.nio.charset.Charset;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.nio.charset.MalformedInputException;
import java.nio.CharBuffer;
import java.nio.charset.CodingErrorAction;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import java.util.Arrays;
import java.nio.charset.CharacterCodingException;
import java.nio.ByteBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.avro.reflect.Stringable;

@Stringable
@InterfaceAudience.Public
@InterfaceStability.Stable
public class Text extends BinaryComparable implements WritableComparable<BinaryComparable>
{
    private static final ThreadLocal<CharsetEncoder> ENCODER_FACTORY;
    private static final ThreadLocal<CharsetDecoder> DECODER_FACTORY;
    private static final byte[] EMPTY_BYTES;
    private byte[] bytes;
    private int length;
    public static final int DEFAULT_MAX_LEN = 1048576;
    private static final int LEAD_BYTE = 0;
    private static final int TRAIL_BYTE_1 = 1;
    private static final int TRAIL_BYTE = 2;
    static final int[] bytesFromUTF8;
    static final int[] offsetsFromUTF8;
    
    public Text() {
        this.bytes = Text.EMPTY_BYTES;
    }
    
    public Text(final String string) {
        this.set(string);
    }
    
    public Text(final Text utf8) {
        this.set(utf8);
    }
    
    public Text(final byte[] utf8) {
        this.set(utf8);
    }
    
    public byte[] copyBytes() {
        final byte[] result = new byte[this.length];
        System.arraycopy(this.bytes, 0, result, 0, this.length);
        return result;
    }
    
    @Override
    public byte[] getBytes() {
        return this.bytes;
    }
    
    @Override
    public int getLength() {
        return this.length;
    }
    
    public int charAt(final int position) {
        if (position > this.length) {
            return -1;
        }
        if (position < 0) {
            return -1;
        }
        final ByteBuffer bb = (ByteBuffer)ByteBuffer.wrap(this.bytes).position(position);
        return bytesToCodePoint(bb.slice());
    }
    
    public int find(final String what) {
        return this.find(what, 0);
    }
    
    public int find(final String what, final int start) {
        try {
            final ByteBuffer src = ByteBuffer.wrap(this.bytes, 0, this.length);
            final ByteBuffer tgt = encode(what);
            final byte b = tgt.get();
            src.position(start);
            while (src.hasRemaining()) {
                if (b == src.get()) {
                    src.mark();
                    tgt.mark();
                    boolean found = true;
                    final int pos = src.position() - 1;
                    while (tgt.hasRemaining()) {
                        if (!src.hasRemaining()) {
                            tgt.reset();
                            src.reset();
                            found = false;
                            break;
                        }
                        if (tgt.get() != src.get()) {
                            tgt.reset();
                            src.reset();
                            found = false;
                            break;
                        }
                    }
                    if (found) {
                        return pos;
                    }
                    continue;
                }
            }
            return -1;
        }
        catch (CharacterCodingException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    public void set(final String string) {
        try {
            final ByteBuffer bb = encode(string, true);
            this.bytes = bb.array();
            this.length = bb.limit();
        }
        catch (CharacterCodingException e) {
            throw new RuntimeException("Should not have happened ", e);
        }
    }
    
    public void set(final byte[] utf8) {
        this.set(utf8, 0, utf8.length);
    }
    
    public void set(final Text other) {
        this.set(other.getBytes(), 0, other.getLength());
    }
    
    public void set(final byte[] utf8, final int start, final int len) {
        this.setCapacity(len, false);
        System.arraycopy(utf8, start, this.bytes, 0, len);
        this.length = len;
    }
    
    public void append(final byte[] utf8, final int start, final int len) {
        this.setCapacity(this.length + len, true);
        System.arraycopy(utf8, start, this.bytes, this.length, len);
        this.length += len;
    }
    
    public void clear() {
        this.length = 0;
    }
    
    private void setCapacity(final int len, final boolean keepData) {
        if (this.bytes == null || this.bytes.length < len) {
            if (this.bytes != null && keepData) {
                this.bytes = Arrays.copyOf(this.bytes, Math.max(len, this.length << 1));
            }
            else {
                this.bytes = new byte[len];
            }
        }
    }
    
    @Override
    public String toString() {
        try {
            return decode(this.bytes, 0, this.length);
        }
        catch (CharacterCodingException e) {
            throw new RuntimeException("Should not have happened ", e);
        }
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final int newLength = WritableUtils.readVInt(in);
        this.readWithKnownLength(in, newLength);
    }
    
    public void readFields(final DataInput in, final int maxLength) throws IOException {
        final int newLength = WritableUtils.readVInt(in);
        if (newLength < 0) {
            throw new IOException("tried to deserialize " + newLength + " bytes of data!  newLength must be non-negative.");
        }
        if (newLength >= maxLength) {
            throw new IOException("tried to deserialize " + newLength + " bytes of data, but maxLength = " + maxLength);
        }
        this.readWithKnownLength(in, newLength);
    }
    
    public static void skip(final DataInput in) throws IOException {
        final int length = WritableUtils.readVInt(in);
        WritableUtils.skipFully(in, length);
    }
    
    public void readWithKnownLength(final DataInput in, final int len) throws IOException {
        this.setCapacity(len, false);
        in.readFully(this.bytes, 0, len);
        this.length = len;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.length);
        out.write(this.bytes, 0, this.length);
    }
    
    public void write(final DataOutput out, final int maxLength) throws IOException {
        if (this.length > maxLength) {
            throw new IOException("data was too long to write!  Expected less than or equal to " + maxLength + " bytes, but got " + this.length + " bytes.");
        }
        WritableUtils.writeVInt(out, this.length);
        out.write(this.bytes, 0, this.length);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Text && super.equals(o);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    public static String decode(final byte[] utf8) throws CharacterCodingException {
        return decode(ByteBuffer.wrap(utf8), true);
    }
    
    public static String decode(final byte[] utf8, final int start, final int length) throws CharacterCodingException {
        return decode(ByteBuffer.wrap(utf8, start, length), true);
    }
    
    public static String decode(final byte[] utf8, final int start, final int length, final boolean replace) throws CharacterCodingException {
        return decode(ByteBuffer.wrap(utf8, start, length), replace);
    }
    
    private static String decode(final ByteBuffer utf8, final boolean replace) throws CharacterCodingException {
        final CharsetDecoder decoder = Text.DECODER_FACTORY.get();
        if (replace) {
            decoder.onMalformedInput(CodingErrorAction.REPLACE);
            decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        }
        final String str = decoder.decode(utf8).toString();
        if (replace) {
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        return str;
    }
    
    public static ByteBuffer encode(final String string) throws CharacterCodingException {
        return encode(string, true);
    }
    
    public static ByteBuffer encode(final String string, final boolean replace) throws CharacterCodingException {
        final CharsetEncoder encoder = Text.ENCODER_FACTORY.get();
        if (replace) {
            encoder.onMalformedInput(CodingErrorAction.REPLACE);
            encoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
        }
        final ByteBuffer bytes = encoder.encode(CharBuffer.wrap(string.toCharArray()));
        if (replace) {
            encoder.onMalformedInput(CodingErrorAction.REPORT);
            encoder.onUnmappableCharacter(CodingErrorAction.REPORT);
        }
        return bytes;
    }
    
    public static String readString(final DataInput in) throws IOException {
        return readString(in, Integer.MAX_VALUE);
    }
    
    public static String readString(final DataInput in, final int maxLength) throws IOException {
        final int length = WritableUtils.readVIntInRange(in, 0, maxLength);
        final byte[] bytes = new byte[length];
        in.readFully(bytes, 0, length);
        return decode(bytes);
    }
    
    public static int writeString(final DataOutput out, final String s) throws IOException {
        final ByteBuffer bytes = encode(s);
        final int length = bytes.limit();
        WritableUtils.writeVInt(out, length);
        out.write(bytes.array(), 0, length);
        return length;
    }
    
    public static int writeString(final DataOutput out, final String s, final int maxLength) throws IOException {
        final ByteBuffer bytes = encode(s);
        final int length = bytes.limit();
        if (length > maxLength) {
            throw new IOException("string was too long to write!  Expected less than or equal to " + maxLength + " bytes, but got " + length + " bytes.");
        }
        WritableUtils.writeVInt(out, length);
        out.write(bytes.array(), 0, length);
        return length;
    }
    
    public static void validateUTF8(final byte[] utf8) throws MalformedInputException {
        validateUTF8(utf8, 0, utf8.length);
    }
    
    public static void validateUTF8(final byte[] utf8, final int start, final int len) throws MalformedInputException {
        int count = start;
        int leadByte = 0;
        int length = 0;
        int state = 0;
        while (count < start + len) {
            final int aByte = utf8[count] & 0xFF;
            Label_0363: {
                switch (state) {
                    case 0: {
                        leadByte = aByte;
                        length = Text.bytesFromUTF8[aByte];
                        switch (length) {
                            case 0: {
                                if (leadByte > 127) {
                                    throw new MalformedInputException(count);
                                }
                                break Label_0363;
                            }
                            case 1: {
                                if (leadByte < 194 || leadByte > 223) {
                                    throw new MalformedInputException(count);
                                }
                                state = 1;
                                break Label_0363;
                            }
                            case 2: {
                                if (leadByte < 224 || leadByte > 239) {
                                    throw new MalformedInputException(count);
                                }
                                state = 1;
                                break Label_0363;
                            }
                            case 3: {
                                if (leadByte < 240 || leadByte > 244) {
                                    throw new MalformedInputException(count);
                                }
                                state = 1;
                                break Label_0363;
                            }
                            default: {
                                throw new MalformedInputException(count);
                            }
                        }
                        break;
                    }
                    case 1: {
                        if (leadByte == 240 && aByte < 144) {
                            throw new MalformedInputException(count);
                        }
                        if (leadByte == 244 && aByte > 143) {
                            throw new MalformedInputException(count);
                        }
                        if (leadByte == 224 && aByte < 160) {
                            throw new MalformedInputException(count);
                        }
                        if (leadByte == 237 && aByte > 159) {
                            throw new MalformedInputException(count);
                        }
                    }
                    case 2: {
                        if (aByte < 128 || aByte > 191) {
                            throw new MalformedInputException(count);
                        }
                        if (--length == 0) {
                            state = 0;
                            break;
                        }
                        state = 2;
                        break;
                    }
                }
            }
            ++count;
        }
    }
    
    public static int bytesToCodePoint(final ByteBuffer bytes) {
        bytes.mark();
        final byte b = bytes.get();
        bytes.reset();
        final int extraBytesToRead = Text.bytesFromUTF8[b & 0xFF];
        if (extraBytesToRead < 0) {
            return -1;
        }
        int ch = 0;
        switch (extraBytesToRead) {
            case 5: {
                ch += (bytes.get() & 0xFF);
                ch <<= 6;
            }
            case 4: {
                ch += (bytes.get() & 0xFF);
                ch <<= 6;
            }
            case 3: {
                ch += (bytes.get() & 0xFF);
                ch <<= 6;
            }
            case 2: {
                ch += (bytes.get() & 0xFF);
                ch <<= 6;
            }
            case 1: {
                ch += (bytes.get() & 0xFF);
                ch <<= 6;
            }
            case 0: {
                ch += (bytes.get() & 0xFF);
                break;
            }
        }
        ch -= Text.offsetsFromUTF8[extraBytesToRead];
        return ch;
    }
    
    public static int utf8Length(final String string) {
        final CharacterIterator iter = new StringCharacterIterator(string);
        char ch = iter.first();
        int size = 0;
        while (ch != '\uffff') {
            if (ch >= '\ud800' && ch < '\udc00') {
                final char trail = iter.next();
                if (trail > '\udbff' && trail < '\ue000') {
                    size += 4;
                }
                else {
                    size += 3;
                    iter.previous();
                }
            }
            else if (ch < '\u0080') {
                ++size;
            }
            else if (ch < '\u0800') {
                size += 2;
            }
            else {
                size += 3;
            }
            ch = iter.next();
        }
        return size;
    }
    
    static {
        ENCODER_FACTORY = new ThreadLocal<CharsetEncoder>() {
            @Override
            protected CharsetEncoder initialValue() {
                return Charset.forName("UTF-8").newEncoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
            }
        };
        DECODER_FACTORY = new ThreadLocal<CharsetDecoder>() {
            @Override
            protected CharsetDecoder initialValue() {
                return Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
            }
        };
        EMPTY_BYTES = new byte[0];
        WritableComparator.define(Text.class, new Comparator());
        bytesFromUTF8 = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5 };
        offsetsFromUTF8 = new int[] { 0, 12416, 925824, 63447168, -100130688, -2113396608 };
    }
    
    public static class Comparator extends WritableComparator
    {
        public Comparator() {
            super(Text.class);
        }
        
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            final int n1 = WritableUtils.decodeVIntSize(b1[s1]);
            final int n2 = WritableUtils.decodeVIntSize(b2[s2]);
            return WritableComparator.compareBytes(b1, s1 + n1, l1 - n1, b2, s2 + n2, l2 - n2);
        }
    }
}
