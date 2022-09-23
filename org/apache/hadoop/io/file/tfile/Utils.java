// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import java.util.Comparator;
import java.util.List;
import org.apache.hadoop.io.Text;
import java.io.DataInput;
import java.io.IOException;
import java.io.DataOutput;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class Utils
{
    private Utils() {
    }
    
    public static void writeVInt(final DataOutput out, final int n) throws IOException {
        writeVLong(out, n);
    }
    
    public static void writeVLong(final DataOutput out, final long n) throws IOException {
        if (n < 128L && n >= -32L) {
            out.writeByte((int)n);
            return;
        }
        final long un = (n < 0L) ? (~n) : n;
        final int len = (64 - Long.numberOfLeadingZeros(un)) / 8 + 1;
        int firstByte = (int)(n >> (len - 1) * 8);
        switch (len) {
            case 1: {
                firstByte >>= 8;
            }
            case 2: {
                if (firstByte < 20 && firstByte >= -20) {
                    out.writeByte(firstByte - 52);
                    out.writeByte((int)n);
                    return;
                }
                firstByte >>= 8;
            }
            case 3: {
                if (firstByte < 16 && firstByte >= -16) {
                    out.writeByte(firstByte - 88);
                    out.writeShort((int)n);
                    return;
                }
                firstByte >>= 8;
            }
            case 4: {
                if (firstByte < 8 && firstByte >= -8) {
                    out.writeByte(firstByte - 112);
                    out.writeShort((int)n >>> 8);
                    out.writeByte((int)n);
                    return;
                }
                out.writeByte(len - 129);
                out.writeInt((int)n);
            }
            case 5: {
                out.writeByte(len - 129);
                out.writeInt((int)(n >>> 8));
                out.writeByte((int)n);
            }
            case 6: {
                out.writeByte(len - 129);
                out.writeInt((int)(n >>> 16));
                out.writeShort((int)n);
            }
            case 7: {
                out.writeByte(len - 129);
                out.writeInt((int)(n >>> 24));
                out.writeShort((int)(n >>> 8));
                out.writeByte((int)n);
            }
            case 8: {
                out.writeByte(len - 129);
                out.writeLong(n);
            }
            default: {
                throw new RuntimeException("Internal error");
            }
        }
    }
    
    public static int readVInt(final DataInput in) throws IOException {
        final long ret = readVLong(in);
        if (ret > 2147483647L || ret < -2147483648L) {
            throw new RuntimeException("Number too large to be represented as Integer");
        }
        return (int)ret;
    }
    
    public static long readVLong(final DataInput in) throws IOException {
        final int firstByte = in.readByte();
        if (firstByte >= -32) {
            return firstByte;
        }
        switch ((firstByte + 128) / 8) {
            case 7:
            case 8:
            case 9:
            case 10:
            case 11: {
                return firstByte + 52 << 8 | in.readUnsignedByte();
            }
            case 3:
            case 4:
            case 5:
            case 6: {
                return firstByte + 88 << 16 | in.readUnsignedShort();
            }
            case 1:
            case 2: {
                return firstByte + 112 << 24 | in.readUnsignedShort() << 8 | in.readUnsignedByte();
            }
            case 0: {
                final int len = firstByte + 129;
                switch (len) {
                    case 4: {
                        return in.readInt();
                    }
                    case 5: {
                        return (long)in.readInt() << 8 | (long)in.readUnsignedByte();
                    }
                    case 6: {
                        return (long)in.readInt() << 16 | (long)in.readUnsignedShort();
                    }
                    case 7: {
                        return (long)in.readInt() << 24 | (long)(in.readUnsignedShort() << 8) | (long)in.readUnsignedByte();
                    }
                    case 8: {
                        return in.readLong();
                    }
                    default: {
                        throw new IOException("Corrupted VLong encoding");
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException("Internal error");
            }
        }
    }
    
    public static void writeString(final DataOutput out, final String s) throws IOException {
        if (s != null) {
            final Text text = new Text(s);
            final byte[] buffer = text.getBytes();
            final int len = text.getLength();
            writeVInt(out, len);
            out.write(buffer, 0, len);
        }
        else {
            writeVInt(out, -1);
        }
    }
    
    public static String readString(final DataInput in) throws IOException {
        final int length = readVInt(in);
        if (length == -1) {
            return null;
        }
        final byte[] buffer = new byte[length];
        in.readFully(buffer);
        return Text.decode(buffer);
    }
    
    public static <T> int lowerBound(final List<? extends T> list, final T key, final Comparator<? super T> cmp) {
        int low = 0;
        int high = list.size();
        while (low < high) {
            final int mid = low + high >>> 1;
            final T midVal = (T)list.get(mid);
            final int ret = cmp.compare((Object)midVal, (Object)key);
            if (ret < 0) {
                low = mid + 1;
            }
            else {
                high = mid;
            }
        }
        return low;
    }
    
    public static <T> int upperBound(final List<? extends T> list, final T key, final Comparator<? super T> cmp) {
        int low = 0;
        int high = list.size();
        while (low < high) {
            final int mid = low + high >>> 1;
            final T midVal = (T)list.get(mid);
            final int ret = cmp.compare((Object)midVal, (Object)key);
            if (ret <= 0) {
                low = mid + 1;
            }
            else {
                high = mid;
            }
        }
        return low;
    }
    
    public static <T> int lowerBound(final List<? extends Comparable<? super T>> list, final T key) {
        int low = 0;
        int high = list.size();
        while (low < high) {
            final int mid = low + high >>> 1;
            final Comparable<? super T> midVal = (Comparable<? super T>)list.get(mid);
            final int ret = midVal.compareTo((Object)key);
            if (ret < 0) {
                low = mid + 1;
            }
            else {
                high = mid;
            }
        }
        return low;
    }
    
    public static <T> int upperBound(final List<? extends Comparable<? super T>> list, final T key) {
        int low = 0;
        int high = list.size();
        while (low < high) {
            final int mid = low + high >>> 1;
            final Comparable<? super T> midVal = (Comparable<? super T>)list.get(mid);
            final int ret = midVal.compareTo((Object)key);
            if (ret <= 0) {
                low = mid + 1;
            }
            else {
                high = mid;
            }
        }
        return low;
    }
    
    public static final class Version implements Comparable<Version>
    {
        private final short major;
        private final short minor;
        
        public Version(final DataInput in) throws IOException {
            this.major = in.readShort();
            this.minor = in.readShort();
        }
        
        public Version(final short major, final short minor) {
            this.major = major;
            this.minor = minor;
        }
        
        public void write(final DataOutput out) throws IOException {
            out.writeShort(this.major);
            out.writeShort(this.minor);
        }
        
        public int getMajor() {
            return this.major;
        }
        
        public int getMinor() {
            return this.minor;
        }
        
        public static int size() {
            return 4;
        }
        
        @Override
        public String toString() {
            return "v" + this.major + "." + this.minor;
        }
        
        public boolean compatibleWith(final Version other) {
            return this.major == other.major;
        }
        
        @Override
        public int compareTo(final Version that) {
            if (this.major != that.major) {
                return this.major - that.major;
            }
            return this.minor - that.minor;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof Version && this.compareTo((Version)other) == 0);
        }
        
        @Override
        public int hashCode() {
            return (this.major << 16) + this.minor;
        }
    }
}
