// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import org.joda.time.DateTimeZone;

public class CachedDateTimeZone extends DateTimeZone
{
    private static final long serialVersionUID = 5472298452022250685L;
    private static final int cInfoCacheMask;
    private final DateTimeZone iZone;
    private final transient Info[] iInfoCache;
    
    public static CachedDateTimeZone forZone(final DateTimeZone dateTimeZone) {
        if (dateTimeZone instanceof CachedDateTimeZone) {
            return (CachedDateTimeZone)dateTimeZone;
        }
        return new CachedDateTimeZone(dateTimeZone);
    }
    
    private CachedDateTimeZone(final DateTimeZone iZone) {
        super(iZone.getID());
        this.iInfoCache = new Info[CachedDateTimeZone.cInfoCacheMask + 1];
        this.iZone = iZone;
    }
    
    public DateTimeZone getUncachedZone() {
        return this.iZone;
    }
    
    @Override
    public String getNameKey(final long n) {
        return this.getInfo(n).getNameKey(n);
    }
    
    @Override
    public int getOffset(final long n) {
        return this.getInfo(n).getOffset(n);
    }
    
    @Override
    public int getStandardOffset(final long n) {
        return this.getInfo(n).getStandardOffset(n);
    }
    
    @Override
    public boolean isFixed() {
        return this.iZone.isFixed();
    }
    
    @Override
    public long nextTransition(final long n) {
        return this.iZone.nextTransition(n);
    }
    
    @Override
    public long previousTransition(final long n) {
        return this.iZone.previousTransition(n);
    }
    
    @Override
    public int hashCode() {
        return this.iZone.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof CachedDateTimeZone && this.iZone.equals(((CachedDateTimeZone)o).iZone));
    }
    
    private Info getInfo(final long n) {
        final int n2 = (int)(n >> 32);
        final Info[] iInfoCache = this.iInfoCache;
        final int n3 = n2 & CachedDateTimeZone.cInfoCacheMask;
        Info info = iInfoCache[n3];
        if (info == null || (int)(info.iPeriodStart >> 32) != n2) {
            info = this.createInfo(n);
            iInfoCache[n3] = info;
        }
        return info;
    }
    
    private Info createInfo(final long n) {
        long n2 = n & 0xFFFFFFFF00000000L;
        final Info info = new Info(this.iZone, n2);
        final long n3 = n2 | 0xFFFFFFFFL;
        Info info2 = info;
        while (true) {
            final long nextTransition = this.iZone.nextTransition(n2);
            if (nextTransition == n2 || nextTransition > n3) {
                break;
            }
            n2 = nextTransition;
            final Info info3 = info2;
            final Info iNextInfo = new Info(this.iZone, n2);
            info3.iNextInfo = iNextInfo;
            info2 = iNextInfo;
        }
        return info;
    }
    
    static {
        Integer integer;
        try {
            integer = Integer.getInteger("org.joda.time.tz.CachedDateTimeZone.size");
        }
        catch (SecurityException ex) {
            integer = null;
        }
        int n;
        if (integer == null) {
            n = 512;
        }
        else {
            int i = integer;
            --i;
            int n2 = 0;
            while (i > 0) {
                ++n2;
                i >>= 1;
            }
            n = 1 << n2;
        }
        cInfoCacheMask = n - 1;
    }
    
    private static final class Info
    {
        public final long iPeriodStart;
        public final DateTimeZone iZoneRef;
        Info iNextInfo;
        private String iNameKey;
        private int iOffset;
        private int iStandardOffset;
        
        Info(final DateTimeZone iZoneRef, final long iPeriodStart) {
            this.iOffset = Integer.MIN_VALUE;
            this.iStandardOffset = Integer.MIN_VALUE;
            this.iPeriodStart = iPeriodStart;
            this.iZoneRef = iZoneRef;
        }
        
        public String getNameKey(final long n) {
            if (this.iNextInfo == null || n < this.iNextInfo.iPeriodStart) {
                if (this.iNameKey == null) {
                    this.iNameKey = this.iZoneRef.getNameKey(this.iPeriodStart);
                }
                return this.iNameKey;
            }
            return this.iNextInfo.getNameKey(n);
        }
        
        public int getOffset(final long n) {
            if (this.iNextInfo == null || n < this.iNextInfo.iPeriodStart) {
                if (this.iOffset == Integer.MIN_VALUE) {
                    this.iOffset = this.iZoneRef.getOffset(this.iPeriodStart);
                }
                return this.iOffset;
            }
            return this.iNextInfo.getOffset(n);
        }
        
        public int getStandardOffset(final long n) {
            if (this.iNextInfo == null || n < this.iNextInfo.iPeriodStart) {
                if (this.iStandardOffset == Integer.MIN_VALUE) {
                    this.iStandardOffset = this.iZoneRef.getStandardOffset(this.iPeriodStart);
                }
                return this.iStandardOffset;
            }
            return this.iNextInfo.getStandardOffset(n);
        }
    }
}
