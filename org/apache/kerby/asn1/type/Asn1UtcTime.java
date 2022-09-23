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

public class Asn1UtcTime extends Asn1Simple<Date>
{
    public Asn1UtcTime() {
        this((Date)null);
    }
    
    public Asn1UtcTime(final long time) {
        super(UniversalTag.UTC_TIME, new Date(time * 1000L));
    }
    
    public Asn1UtcTime(final Date date) {
        super(UniversalTag.UTC_TIME, date);
    }
    
    @Override
    protected void toValue() throws IOException {
        String fixedDateStr;
        final String dateStr = fixedDateStr = new String(this.getBytes(), StandardCharsets.US_ASCII);
        final int strLen = fixedDateStr.length();
        if (strLen == 6) {
            fixedDateStr += "000000+0000";
        }
        else if (strLen == 7) {
            fixedDateStr = fixedDateStr.replace("Z", "000000+0000");
        }
        else if (strLen == 10) {
            fixedDateStr += "00+0000";
        }
        else if (strLen == 11) {
            fixedDateStr = fixedDateStr.replace("Z", "00+0000");
        }
        else if (strLen == 12) {
            fixedDateStr += "+0000";
        }
        else if (strLen == 13) {
            fixedDateStr = fixedDateStr.replace("Z", "+0000");
        }
        else if (strLen != 17) {
            throw new IllegalArgumentException("Bad utc time string " + dateStr);
        }
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssZ");
        sdf.setTimeZone(new SimpleTimeZone(0, "Z"));
        try {
            this.setValue(sdf.parse(fixedDateStr));
        }
        catch (ParseException e) {
            throw new IOException("Failed to parse " + dateStr + " as utc time", e);
        }
    }
    
    @Override
    protected void toBytes() {
        final Date date = this.getValue();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(0, "Z"));
        final String str = sdf.format(date);
        final byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
        this.setBytes(bytes);
    }
}
