// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.asn1.type;

import java.text.ParseException;
import java.io.IOException;
import java.util.TimeZone;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.nio.charset.StandardCharsets;
import org.apache.kerby.asn1.UniversalTag;
import java.util.Date;

public class Asn1GeneralizedTime extends Asn1Simple<Date>
{
    public Asn1GeneralizedTime() {
        this((Date)null);
    }
    
    public Asn1GeneralizedTime(final long time) {
        super(UniversalTag.GENERALIZED_TIME, new Date(time));
    }
    
    public Asn1GeneralizedTime(final Date date) {
        super(UniversalTag.GENERALIZED_TIME, date);
    }
    
    @Override
    protected void toValue() throws IOException {
        String fixedDateStr;
        final String dateStr = fixedDateStr = new String(this.getBytes(), StandardCharsets.US_ASCII);
        final boolean withZ = dateStr.endsWith("Z");
        final String timeZonePart = this.getTimeZonePart(dateStr);
        final boolean withZone = timeZonePart != null;
        final String millSecs = this.getMillSeconds(dateStr);
        fixedDateStr = dateStr.substring(0, 14) + millSecs;
        SimpleDateFormat sdf;
        if (withZ) {
            sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            sdf.setTimeZone(new SimpleTimeZone(0, "Z"));
        }
        else if (withZone) {
            fixedDateStr += timeZonePart;
            sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSz");
            sdf.setTimeZone(new SimpleTimeZone(0, "Z"));
        }
        else {
            sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            sdf.setTimeZone(new SimpleTimeZone(0, TimeZone.getDefault().getID()));
        }
        try {
            this.setValue(sdf.parse(fixedDateStr));
        }
        catch (ParseException e) {
            throw new IOException("Failed to parse as generalized time string " + dateStr);
        }
    }
    
    @Override
    protected void toBytes() {
        final Date date = this.getValue();
        final SimpleDateFormat dateF = new SimpleDateFormat("yyyyMMddHHmmss'Z'");
        dateF.setTimeZone(new SimpleTimeZone(0, "Z"));
        final String str = dateF.format(date);
        final byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
        this.setBytes(bytes);
    }
    
    private String getMillSeconds(final String dateStr) {
        final char[] millDigits = { '0', '0', '0' };
        final int iPos = dateStr.indexOf(46);
        if (iPos > 0) {
            if (iPos != 14) {
                throw new IllegalArgumentException("Bad generalized time string, with improper milli seconds " + dateStr);
            }
            char chr;
            for (int j = 0, i = 15; i < dateStr.length() && j < millDigits.length; millDigits[j++] = chr, ++i) {
                chr = dateStr.charAt(i);
                if ('0' > chr || chr > '9') {
                    break;
                }
            }
        }
        return new String(millDigits);
    }
    
    private String getTimeZonePart(final String dateStr) {
        int iPos = dateStr.indexOf(43);
        if (iPos == -1) {
            iPos = dateStr.indexOf(45);
        }
        if (iPos > 0 && iPos != dateStr.length() - 5) {
            throw new IllegalArgumentException("Bad generalized time string, with improper timezone part " + dateStr);
        }
        if (iPos > 0) {
            return dateStr.substring(iPos);
        }
        return null;
    }
}
