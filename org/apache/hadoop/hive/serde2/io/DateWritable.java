// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.io;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.WritableUtils;
import java.io.DataInput;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.hadoop.hive.serde2.lazybinary.LazyBinaryUtils;
import java.sql.Date;
import java.util.Calendar;
import java.util.TimeZone;
import org.apache.hadoop.io.WritableComparable;

public class DateWritable implements WritableComparable<DateWritable>
{
    private static final long MILLIS_PER_DAY;
    private static final ThreadLocal<TimeZone> LOCAL_TIMEZONE;
    private static final ThreadLocal<Calendar> UTC_CALENDAR;
    private static final ThreadLocal<Calendar> LOCAL_CALENDAR;
    private int daysSinceEpoch;
    
    public DateWritable() {
        this.daysSinceEpoch = 0;
    }
    
    public DateWritable(final DateWritable d) {
        this.daysSinceEpoch = 0;
        this.set(d);
    }
    
    public DateWritable(final Date d) {
        this.daysSinceEpoch = 0;
        this.set(d);
    }
    
    public DateWritable(final int d) {
        this.daysSinceEpoch = 0;
        this.set(d);
    }
    
    public void set(final int d) {
        this.daysSinceEpoch = d;
    }
    
    public void set(final Date d) {
        if (d == null) {
            this.daysSinceEpoch = 0;
            return;
        }
        this.set(dateToDays(d));
    }
    
    public void set(final DateWritable d) {
        this.set(d.daysSinceEpoch);
    }
    
    public Date get() {
        return this.get(true);
    }
    
    public Date get(final boolean doesTimeMatter) {
        return new Date(daysToMillis(this.daysSinceEpoch, doesTimeMatter));
    }
    
    public int getDays() {
        return this.daysSinceEpoch;
    }
    
    public long getTimeInSeconds() {
        return this.get().getTime() / 1000L;
    }
    
    public static Date timeToDate(final long l) {
        return new Date(l * 1000L);
    }
    
    public static long daysToMillis(final int d) {
        return daysToMillis(d, true);
    }
    
    public static long daysToMillis(final int d, final boolean doesTimeMatter) {
        final long utcMidnight = d * DateWritable.MILLIS_PER_DAY;
        final long utcMidnightOffset = DateWritable.LOCAL_TIMEZONE.get().getOffset(utcMidnight);
        final long hopefullyMidnight = utcMidnight - utcMidnightOffset;
        final long offsetAtHM = DateWritable.LOCAL_TIMEZONE.get().getOffset(hopefullyMidnight);
        if (utcMidnightOffset == offsetAtHM) {
            return hopefullyMidnight;
        }
        if (!doesTimeMatter) {
            return daysToMillis(d + 1) - (DateWritable.MILLIS_PER_DAY >> 1);
        }
        final Calendar utc = DateWritable.UTC_CALENDAR.get();
        final Calendar local = DateWritable.LOCAL_CALENDAR.get();
        utc.setTimeInMillis(utcMidnight);
        local.set(utc.get(1), utc.get(2), utc.get(5));
        return local.getTimeInMillis();
    }
    
    public static int millisToDays(final long millisLocal) {
        final long millisUtc = millisLocal + DateWritable.LOCAL_TIMEZONE.get().getOffset(millisLocal);
        int days;
        if (millisUtc >= 0L) {
            days = (int)(millisUtc / DateWritable.MILLIS_PER_DAY);
        }
        else {
            days = (int)((millisUtc - 86399999L) / DateWritable.MILLIS_PER_DAY);
        }
        return days;
    }
    
    public static int dateToDays(final Date d) {
        final long millisLocal = d.getTime();
        return millisToDays(millisLocal);
    }
    
    public void setFromBytes(final byte[] bytes, final int offset, final int length, final LazyBinaryUtils.VInt vInt) {
        LazyBinaryUtils.readVInt(bytes, offset, vInt);
        assert length == vInt.length;
        this.set(vInt.value);
    }
    
    public void writeToByteStream(final ByteStream.RandomAccessOutput byteStream) {
        LazyBinaryUtils.writeVInt(byteStream, this.getDays());
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        this.daysSinceEpoch = WritableUtils.readVInt(in);
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        WritableUtils.writeVInt(out, this.daysSinceEpoch);
    }
    
    @Override
    public int compareTo(final DateWritable d) {
        return this.daysSinceEpoch - d.daysSinceEpoch;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DateWritable && this.compareTo((DateWritable)o) == 0;
    }
    
    @Override
    public String toString() {
        return this.get(false).toString();
    }
    
    @Override
    public int hashCode() {
        return this.daysSinceEpoch;
    }
    
    static {
        MILLIS_PER_DAY = TimeUnit.DAYS.toMillis(1L);
        LOCAL_TIMEZONE = new ThreadLocal<TimeZone>() {
            @Override
            protected TimeZone initialValue() {
                return Calendar.getInstance().getTimeZone();
            }
        };
        UTC_CALENDAR = new ThreadLocal<Calendar>() {
            @Override
            protected Calendar initialValue() {
                return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            }
        };
        LOCAL_CALENDAR = new ThreadLocal<Calendar>() {
            @Override
            protected Calendar initialValue() {
                return Calendar.getInstance();
            }
        };
    }
}
