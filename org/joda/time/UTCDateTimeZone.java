// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

import java.util.SimpleTimeZone;
import java.util.TimeZone;

final class UTCDateTimeZone extends DateTimeZone
{
    static final DateTimeZone INSTANCE;
    private static final long serialVersionUID = -3513011772763289092L;
    
    public UTCDateTimeZone() {
        super("UTC");
    }
    
    @Override
    public String getNameKey(final long n) {
        return "UTC";
    }
    
    @Override
    public int getOffset(final long n) {
        return 0;
    }
    
    @Override
    public int getStandardOffset(final long n) {
        return 0;
    }
    
    @Override
    public int getOffsetFromLocal(final long n) {
        return 0;
    }
    
    @Override
    public boolean isFixed() {
        return true;
    }
    
    @Override
    public long nextTransition(final long n) {
        return n;
    }
    
    @Override
    public long previousTransition(final long n) {
        return n;
    }
    
    @Override
    public TimeZone toTimeZone() {
        return new SimpleTimeZone(0, this.getID());
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof UTCDateTimeZone;
    }
    
    @Override
    public int hashCode() {
        return this.getID().hashCode();
    }
    
    static {
        INSTANCE = new UTCDateTimeZone();
    }
}
