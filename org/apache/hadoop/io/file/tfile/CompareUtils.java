// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import org.apache.hadoop.io.WritableComparator;
import java.io.Serializable;
import org.apache.hadoop.io.RawComparator;
import java.util.Comparator;

class CompareUtils
{
    private CompareUtils() {
    }
    
    public static final class BytesComparator implements Comparator<RawComparable>
    {
        private RawComparator<Object> cmp;
        
        public BytesComparator(final RawComparator<Object> cmp) {
            this.cmp = cmp;
        }
        
        @Override
        public int compare(final RawComparable o1, final RawComparable o2) {
            return this.compare(o1.buffer(), o1.offset(), o1.size(), o2.buffer(), o2.offset(), o2.size());
        }
        
        public int compare(final byte[] a, final int off1, final int len1, final byte[] b, final int off2, final int len2) {
            return this.cmp.compare(a, off1, len1, b, off2, len2);
        }
    }
    
    static final class ScalarLong implements Scalar
    {
        private long magnitude;
        
        public ScalarLong(final long m) {
            this.magnitude = m;
        }
        
        @Override
        public long magnitude() {
            return this.magnitude;
        }
    }
    
    public static final class ScalarComparator implements Comparator<Scalar>, Serializable
    {
        @Override
        public int compare(final Scalar o1, final Scalar o2) {
            final long diff = o1.magnitude() - o2.magnitude();
            if (diff < 0L) {
                return -1;
            }
            if (diff > 0L) {
                return 1;
            }
            return 0;
        }
    }
    
    public static final class MemcmpRawComparator implements RawComparator<Object>, Serializable
    {
        @Override
        public int compare(final byte[] b1, final int s1, final int l1, final byte[] b2, final int s2, final int l2) {
            return WritableComparator.compareBytes(b1, s1, l1, b2, s2, l2);
        }
        
        @Override
        public int compare(final Object o1, final Object o2) {
            throw new RuntimeException("Object comparison not supported");
        }
    }
    
    interface Scalar
    {
        long magnitude();
    }
}
