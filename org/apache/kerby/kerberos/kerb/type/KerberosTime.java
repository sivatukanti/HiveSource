// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type;

import org.apache.kerby.asn1.type.AbstractAsn1Type;
import java.util.Date;
import org.apache.kerby.asn1.type.Asn1GeneralizedTime;

public class KerberosTime extends Asn1GeneralizedTime
{
    public static final KerberosTime NEVER;
    public static final int MINUTE = 60000;
    public static final int DAY = 86400000;
    public static final int WEEK = 604800000;
    
    public KerberosTime() {
        super(System.currentTimeMillis() / 1000L * 1000L);
    }
    
    public KerberosTime(final long time) {
        super(time);
    }
    
    public long getTime() {
        return this.getValue().getTime();
    }
    
    public void setTime(final long time) {
        this.setValue(new Date(time));
    }
    
    public long getTimeInSeconds() {
        return this.getTime() / 1000L;
    }
    
    public boolean lessThan(final KerberosTime ktime) {
        return this.getValue().compareTo((Date)ktime.getValue()) < 0;
    }
    
    public boolean lessThan(final long time) {
        return this.getValue().getTime() < time;
    }
    
    public boolean greaterThan(final KerberosTime ktime) {
        return this.getValue().compareTo((Date)ktime.getValue()) > 0;
    }
    
    public boolean isInClockSkew(final long clockSkew) {
        final long delta = Math.abs(this.getTime() - System.currentTimeMillis());
        return delta < clockSkew;
    }
    
    public KerberosTime copy() {
        final long time = this.getTime();
        return new KerberosTime(time);
    }
    
    public KerberosTime extend(final long duration) {
        final long result = this.getTime() + duration;
        return new KerberosTime(result);
    }
    
    public long diff(final KerberosTime kerberosTime) {
        return this.getTime() - kerberosTime.getTime();
    }
    
    public static KerberosTime now() {
        return new KerberosTime(System.currentTimeMillis());
    }
    
    @Override
    public int hashCode() {
        return this.getValue().hashCode();
    }
    
    @Override
    public boolean equals(final Object that) {
        return this == that || (that instanceof KerberosTime && this.getValue().equals(((AbstractAsn1Type<Object>)that).getValue()));
    }
    
    static {
        NEVER = new KerberosTime(Long.MAX_VALUE);
    }
}
