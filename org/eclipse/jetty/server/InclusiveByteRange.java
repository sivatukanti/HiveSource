// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.LazyList;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Enumeration;
import org.eclipse.jetty.util.log.Logger;

public class InclusiveByteRange
{
    private static final Logger LOG;
    long first;
    long last;
    
    public InclusiveByteRange(final long first, final long last) {
        this.first = 0L;
        this.last = 0L;
        this.first = first;
        this.last = last;
    }
    
    public long getFirst() {
        return this.first;
    }
    
    public long getLast() {
        return this.last;
    }
    
    public static List<InclusiveByteRange> satisfiableRanges(final Enumeration<String> headers, final long size) {
        Object satRanges = null;
        while (headers.hasMoreElements()) {
            final String header = headers.nextElement();
            final StringTokenizer tok = new StringTokenizer(header, "=,", false);
            String t = null;
            try {
                while (tok.hasMoreTokens()) {
                    try {
                        t = tok.nextToken().trim();
                        long first = -1L;
                        long last = -1L;
                        final int d = t.indexOf(45);
                        if (d < 0 || t.indexOf("-", d + 1) >= 0) {
                            if ("bytes".equals(t)) {
                                continue;
                            }
                            InclusiveByteRange.LOG.warn("Bad range format: {}", t);
                            break;
                        }
                        else {
                            if (d == 0) {
                                if (d + 1 >= t.length()) {
                                    InclusiveByteRange.LOG.warn("Bad range format: {}", t);
                                    continue;
                                }
                                last = Long.parseLong(t.substring(d + 1).trim());
                            }
                            else if (d + 1 < t.length()) {
                                first = Long.parseLong(t.substring(0, d).trim());
                                last = Long.parseLong(t.substring(d + 1).trim());
                            }
                            else {
                                first = Long.parseLong(t.substring(0, d).trim());
                            }
                            if (first == -1L && last == -1L) {
                                break;
                            }
                            if (first != -1L && last != -1L && first > last) {
                                break;
                            }
                            if (first >= size) {
                                continue;
                            }
                            final InclusiveByteRange range = new InclusiveByteRange(first, last);
                            satRanges = LazyList.add(satRanges, range);
                        }
                    }
                    catch (NumberFormatException e) {
                        InclusiveByteRange.LOG.warn("Bad range format: {}", t);
                        InclusiveByteRange.LOG.ignore(e);
                    }
                }
            }
            catch (Exception e2) {
                InclusiveByteRange.LOG.warn("Bad range format: {}", t);
                InclusiveByteRange.LOG.ignore(e2);
            }
        }
        return LazyList.getList(satRanges, true);
    }
    
    public long getFirst(final long size) {
        if (this.first < 0L) {
            long tf = size - this.last;
            if (tf < 0L) {
                tf = 0L;
            }
            return tf;
        }
        return this.first;
    }
    
    public long getLast(final long size) {
        if (this.first < 0L) {
            return size - 1L;
        }
        if (this.last < 0L || this.last >= size) {
            return size - 1L;
        }
        return this.last;
    }
    
    public long getSize(final long size) {
        return this.getLast(size) - this.getFirst(size) + 1L;
    }
    
    public String toHeaderRangeString(final long size) {
        final StringBuilder sb = new StringBuilder(40);
        sb.append("bytes ");
        sb.append(this.getFirst(size));
        sb.append('-');
        sb.append(this.getLast(size));
        sb.append("/");
        sb.append(size);
        return sb.toString();
    }
    
    public static String to416HeaderRangeString(final long size) {
        final StringBuilder sb = new StringBuilder(40);
        sb.append("bytes */");
        sb.append(size);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(60);
        sb.append(Long.toString(this.first));
        sb.append(":");
        sb.append(Long.toString(this.last));
        return sb.toString();
    }
    
    static {
        LOG = Log.getLogger(InclusiveByteRange.class);
    }
}
