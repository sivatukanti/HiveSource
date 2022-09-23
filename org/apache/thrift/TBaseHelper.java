// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Comparator;

public final class TBaseHelper
{
    private static final Comparator comparator;
    
    private TBaseHelper() {
    }
    
    public static int compareTo(final Object o1, final Object o2) {
        if (o1 instanceof Comparable) {
            return compareTo((Comparable)o1, (Comparable)o2);
        }
        if (o1 instanceof List) {
            return compareTo((List)o1, (List)o2);
        }
        if (o1 instanceof Set) {
            return compareTo((Set)o1, (Set)o2);
        }
        if (o1 instanceof Map) {
            return compareTo((Map)o1, (Map)o2);
        }
        if (o1 instanceof byte[]) {
            return compareTo((byte[])o1, (byte[])o2);
        }
        throw new IllegalArgumentException("Cannot compare objects of type " + o1.getClass());
    }
    
    public static int compareTo(final boolean a, final boolean b) {
        return Boolean.valueOf(a).compareTo(Boolean.valueOf(b));
    }
    
    public static int compareTo(final byte a, final byte b) {
        if (a < b) {
            return -1;
        }
        if (b < a) {
            return 1;
        }
        return 0;
    }
    
    public static int compareTo(final short a, final short b) {
        if (a < b) {
            return -1;
        }
        if (b < a) {
            return 1;
        }
        return 0;
    }
    
    public static int compareTo(final int a, final int b) {
        if (a < b) {
            return -1;
        }
        if (b < a) {
            return 1;
        }
        return 0;
    }
    
    public static int compareTo(final long a, final long b) {
        if (a < b) {
            return -1;
        }
        if (b < a) {
            return 1;
        }
        return 0;
    }
    
    public static int compareTo(final double a, final double b) {
        if (a < b) {
            return -1;
        }
        if (b < a) {
            return 1;
        }
        return 0;
    }
    
    public static int compareTo(final String a, final String b) {
        return a.compareTo(b);
    }
    
    public static int compareTo(final byte[] a, final byte[] b) {
        final int sizeCompare = compareTo(a.length, b.length);
        if (sizeCompare != 0) {
            return sizeCompare;
        }
        for (int i = 0; i < a.length; ++i) {
            final int byteCompare = compareTo(a[i], b[i]);
            if (byteCompare != 0) {
                return byteCompare;
            }
        }
        return 0;
    }
    
    public static int compareTo(final Comparable a, final Comparable b) {
        return a.compareTo(b);
    }
    
    public static int compareTo(final List a, final List b) {
        int lastComparison = compareTo(a.size(), b.size());
        if (lastComparison != 0) {
            return lastComparison;
        }
        for (int i = 0; i < a.size(); ++i) {
            lastComparison = TBaseHelper.comparator.compare(a.get(i), b.get(i));
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
    
    public static int compareTo(final Set a, final Set b) {
        int lastComparison = compareTo(a.size(), b.size());
        if (lastComparison != 0) {
            return lastComparison;
        }
        final SortedSet sortedA = new TreeSet(TBaseHelper.comparator);
        sortedA.addAll(a);
        final SortedSet sortedB = new TreeSet(TBaseHelper.comparator);
        sortedB.addAll(b);
        final Iterator iterA = sortedA.iterator();
        final Iterator iterB = sortedB.iterator();
        while (iterA.hasNext() && iterB.hasNext()) {
            lastComparison = TBaseHelper.comparator.compare(iterA.next(), iterB.next());
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
    
    public static int compareTo(final Map a, final Map b) {
        int lastComparison = compareTo(a.size(), b.size());
        if (lastComparison != 0) {
            return lastComparison;
        }
        final SortedMap sortedA = new TreeMap(TBaseHelper.comparator);
        sortedA.putAll(a);
        final Iterator<Map.Entry> iterA = (Iterator<Map.Entry>)sortedA.entrySet().iterator();
        final SortedMap sortedB = new TreeMap(TBaseHelper.comparator);
        sortedB.putAll(b);
        final Iterator<Map.Entry> iterB = (Iterator<Map.Entry>)sortedB.entrySet().iterator();
        while (iterA.hasNext() && iterB.hasNext()) {
            final Map.Entry entryA = iterA.next();
            final Map.Entry entryB = iterB.next();
            lastComparison = TBaseHelper.comparator.compare(entryA.getKey(), entryB.getKey());
            if (lastComparison != 0) {
                return lastComparison;
            }
            lastComparison = TBaseHelper.comparator.compare(entryA.getValue(), entryB.getValue());
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }
    
    public static void toString(final ByteBuffer bb, final StringBuilder sb) {
        final byte[] buf = bb.array();
        final int arrayOffset = bb.arrayOffset();
        final int offset = arrayOffset + bb.position();
        final int origLimit = arrayOffset + bb.limit();
        final int limit = (origLimit - offset > 128) ? (offset + 128) : origLimit;
        for (int i = offset; i < limit; ++i) {
            if (i > offset) {
                sb.append(" ");
            }
            sb.append(paddedByteString(buf[i]));
        }
        if (origLimit != limit) {
            sb.append("...");
        }
    }
    
    public static String paddedByteString(final byte b) {
        final int extended = (b | 0x100) & 0x1FF;
        return Integer.toHexString(extended).toUpperCase().substring(1);
    }
    
    public static byte[] byteBufferToByteArray(final ByteBuffer byteBuffer) {
        if (wrapsFullArray(byteBuffer)) {
            return byteBuffer.array();
        }
        final byte[] target = new byte[byteBuffer.remaining()];
        byteBufferToByteArray(byteBuffer, target, 0);
        return target;
    }
    
    public static boolean wrapsFullArray(final ByteBuffer byteBuffer) {
        return byteBuffer.hasArray() && byteBuffer.position() == 0 && byteBuffer.arrayOffset() == 0 && byteBuffer.remaining() == byteBuffer.capacity();
    }
    
    public static int byteBufferToByteArray(final ByteBuffer byteBuffer, final byte[] target, final int offset) {
        final int remaining = byteBuffer.remaining();
        System.arraycopy(byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), target, offset, remaining);
        return remaining;
    }
    
    public static ByteBuffer rightSize(final ByteBuffer in) {
        if (in == null) {
            return null;
        }
        if (wrapsFullArray(in)) {
            return in;
        }
        return ByteBuffer.wrap(byteBufferToByteArray(in));
    }
    
    public static ByteBuffer copyBinary(final ByteBuffer orig) {
        if (orig == null) {
            return null;
        }
        final ByteBuffer copy = ByteBuffer.wrap(new byte[orig.remaining()]);
        if (orig.hasArray()) {
            System.arraycopy(orig.array(), orig.arrayOffset() + orig.position(), copy.array(), 0, orig.remaining());
        }
        else {
            orig.slice().get(copy.array());
        }
        return copy;
    }
    
    public static byte[] copyBinary(final byte[] orig) {
        if (orig == null) {
            return null;
        }
        final byte[] copy = new byte[orig.length];
        System.arraycopy(orig, 0, copy, 0, orig.length);
        return copy;
    }
    
    static {
        comparator = new NestedStructureComparator();
    }
    
    private static class NestedStructureComparator implements Comparator, Serializable
    {
        public int compare(final Object oA, final Object oB) {
            if (oA == null && oB == null) {
                return 0;
            }
            if (oA == null) {
                return -1;
            }
            if (oB == null) {
                return 1;
            }
            if (oA instanceof List) {
                return TBaseHelper.compareTo((List)oA, (List)oB);
            }
            if (oA instanceof Set) {
                return TBaseHelper.compareTo((Set)oA, (Set)oB);
            }
            if (oA instanceof Map) {
                return TBaseHelper.compareTo((Map)oA, (Map)oB);
            }
            if (oA instanceof byte[]) {
                return TBaseHelper.compareTo((byte[])oA, (byte[])oB);
            }
            return TBaseHelper.compareTo((Comparable)oA, (Comparable)oB);
        }
    }
}
