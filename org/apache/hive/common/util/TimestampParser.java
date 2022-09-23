// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.common.util;

import java.util.regex.Pattern;
import org.joda.time.format.DateTimeParserBucket;
import org.joda.time.DateTimeFieldType;
import java.util.regex.Matcher;
import org.joda.time.ReadWritableInstant;
import org.joda.time.MutableDateTime;
import java.sql.Timestamp;
import org.joda.time.format.DateTimePrinter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeParser;
import java.util.List;
import java.util.Arrays;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.DateTime;

public class TimestampParser
{
    protected static final String[] stringArray;
    protected static final String millisFormatString = "millis";
    protected static final DateTime startingDateValue;
    protected String[] formatStrings;
    protected DateTimeFormatter fmt;
    
    public TimestampParser() {
        this.formatStrings = null;
        this.fmt = null;
    }
    
    public TimestampParser(final TimestampParser tsParser) {
        this((String[])((tsParser.formatStrings == null) ? null : ((String[])Arrays.copyOf(tsParser.formatStrings, tsParser.formatStrings.length))));
    }
    
    public TimestampParser(final List<String> formatStrings) {
        this((String[])((formatStrings == null) ? null : ((String[])formatStrings.toArray(TimestampParser.stringArray))));
    }
    
    public TimestampParser(final String[] formatStrings) {
        this.formatStrings = null;
        this.fmt = null;
        this.formatStrings = formatStrings;
        if (formatStrings != null && formatStrings.length > 0) {
            final DateTimeParser[] parsers = new DateTimeParser[formatStrings.length];
            for (int idx = 0; idx < formatStrings.length; ++idx) {
                final String formatString = formatStrings[idx];
                if (formatString.equalsIgnoreCase("millis")) {
                    parsers[idx] = new MillisDateFormatParser();
                }
                else {
                    parsers[idx] = DateTimeFormat.forPattern(formatString).getParser();
                }
            }
            this.fmt = new DateTimeFormatterBuilder().append(null, parsers).toFormatter();
        }
    }
    
    public Timestamp parseTimestamp(final String strValue) throws IllegalArgumentException {
        if (this.fmt != null) {
            final MutableDateTime mdt = new MutableDateTime(TimestampParser.startingDateValue);
            final int ret = this.fmt.parseInto(mdt, strValue, 0);
            if (ret == strValue.length()) {
                return new Timestamp(mdt.getMillis());
            }
        }
        return Timestamp.valueOf(strValue);
    }
    
    static {
        stringArray = new String[0];
        startingDateValue = new DateTime(1970, 1, 1, 0, 0, 0, 0);
    }
    
    public static class MillisDateFormatParser implements DateTimeParser
    {
        private static final ThreadLocal<Matcher> numericMatcher;
        private static final DateTimeFieldType[] dateTimeFields;
        
        @Override
        public int estimateParsedLength() {
            return 13;
        }
        
        @Override
        public int parseInto(final DateTimeParserBucket bucket, final String text, final int position) {
            final String substr = text.substring(position);
            final Matcher matcher = MillisDateFormatParser.numericMatcher.get();
            matcher.reset(substr);
            if (!matcher.matches()) {
                return -1;
            }
            final long millis = Long.parseLong(matcher.group(1));
            final DateTime dt = new DateTime(millis);
            for (final DateTimeFieldType field : MillisDateFormatParser.dateTimeFields) {
                bucket.saveField(field, dt.get(field));
            }
            return substr.length();
        }
        
        static {
            numericMatcher = new ThreadLocal<Matcher>() {
                @Override
                protected Matcher initialValue() {
                    return Pattern.compile("(-?\\d+)(\\.\\d+)?$").matcher("");
                }
            };
            dateTimeFields = new DateTimeFieldType[] { DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour(), DateTimeFieldType.secondOfMinute(), DateTimeFieldType.millisOfSecond() };
        }
    }
}
