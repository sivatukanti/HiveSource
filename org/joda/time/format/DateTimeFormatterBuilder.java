// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.util.Set;
import java.util.Iterator;
import org.joda.time.DateTimeUtils;
import org.joda.time.field.PreciseDateTimeField;
import org.joda.time.field.MillisDurationField;
import org.joda.time.MutableDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.joda.time.DateTimeField;
import org.joda.time.ReadablePartial;
import java.util.Locale;
import org.joda.time.Chronology;
import java.io.IOException;
import java.util.List;
import org.joda.time.DateTimeZone;
import java.util.Map;
import org.joda.time.DateTimeFieldType;
import java.util.ArrayList;

public class DateTimeFormatterBuilder
{
    private ArrayList<Object> iElementPairs;
    private Object iFormatter;
    
    public DateTimeFormatterBuilder() {
        this.iElementPairs = new ArrayList<Object>();
    }
    
    public DateTimeFormatter toFormatter() {
        final Object formatter = this.getFormatter();
        InternalPrinter internalPrinter = null;
        if (this.isPrinter(formatter)) {
            internalPrinter = (InternalPrinter)formatter;
        }
        InternalParser internalParser = null;
        if (this.isParser(formatter)) {
            internalParser = (InternalParser)formatter;
        }
        if (internalPrinter != null || internalParser != null) {
            return new DateTimeFormatter(internalPrinter, internalParser);
        }
        throw new UnsupportedOperationException("Both printing and parsing not supported");
    }
    
    public DateTimePrinter toPrinter() {
        final Object formatter = this.getFormatter();
        if (this.isPrinter(formatter)) {
            return InternalPrinterDateTimePrinter.of((InternalPrinter)formatter);
        }
        throw new UnsupportedOperationException("Printing is not supported");
    }
    
    public DateTimeParser toParser() {
        final Object formatter = this.getFormatter();
        if (this.isParser(formatter)) {
            return InternalParserDateTimeParser.of((InternalParser)formatter);
        }
        throw new UnsupportedOperationException("Parsing is not supported");
    }
    
    public boolean canBuildFormatter() {
        return this.isFormatter(this.getFormatter());
    }
    
    public boolean canBuildPrinter() {
        return this.isPrinter(this.getFormatter());
    }
    
    public boolean canBuildParser() {
        return this.isParser(this.getFormatter());
    }
    
    public void clear() {
        this.iFormatter = null;
        this.iElementPairs.clear();
    }
    
    public DateTimeFormatterBuilder append(final DateTimeFormatter dateTimeFormatter) {
        if (dateTimeFormatter == null) {
            throw new IllegalArgumentException("No formatter supplied");
        }
        return this.append0(dateTimeFormatter.getPrinter0(), dateTimeFormatter.getParser0());
    }
    
    public DateTimeFormatterBuilder append(final DateTimePrinter dateTimePrinter) {
        this.checkPrinter(dateTimePrinter);
        return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), null);
    }
    
    public DateTimeFormatterBuilder append(final DateTimeParser dateTimeParser) {
        this.checkParser(dateTimeParser);
        return this.append0(null, DateTimeParserInternalParser.of(dateTimeParser));
    }
    
    public DateTimeFormatterBuilder append(final DateTimePrinter dateTimePrinter, final DateTimeParser dateTimeParser) {
        this.checkPrinter(dateTimePrinter);
        this.checkParser(dateTimeParser);
        return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), DateTimeParserInternalParser.of(dateTimeParser));
    }
    
    public DateTimeFormatterBuilder append(final DateTimePrinter dateTimePrinter, final DateTimeParser[] array) {
        if (dateTimePrinter != null) {
            this.checkPrinter(dateTimePrinter);
        }
        if (array == null) {
            throw new IllegalArgumentException("No parsers supplied");
        }
        final int length = array.length;
        if (length != 1) {
            final InternalParser[] array2 = new InternalParser[length];
            int i;
            for (i = 0; i < length - 1; ++i) {
                if ((array2[i] = DateTimeParserInternalParser.of(array[i])) == null) {
                    throw new IllegalArgumentException("Incomplete parser array");
                }
            }
            array2[i] = DateTimeParserInternalParser.of(array[i]);
            return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), new MatchingParser(array2));
        }
        if (array[0] == null) {
            throw new IllegalArgumentException("No parser supplied");
        }
        return this.append0(DateTimePrinterInternalPrinter.of(dateTimePrinter), DateTimeParserInternalParser.of(array[0]));
    }
    
    public DateTimeFormatterBuilder appendOptional(final DateTimeParser dateTimeParser) {
        this.checkParser(dateTimeParser);
        return this.append0(null, new MatchingParser(new InternalParser[] { DateTimeParserInternalParser.of(dateTimeParser), null }));
    }
    
    private void checkParser(final DateTimeParser dateTimeParser) {
        if (dateTimeParser == null) {
            throw new IllegalArgumentException("No parser supplied");
        }
    }
    
    private void checkPrinter(final DateTimePrinter dateTimePrinter) {
        if (dateTimePrinter == null) {
            throw new IllegalArgumentException("No printer supplied");
        }
    }
    
    private DateTimeFormatterBuilder append0(final Object o) {
        this.iFormatter = null;
        this.iElementPairs.add(o);
        this.iElementPairs.add(o);
        return this;
    }
    
    private DateTimeFormatterBuilder append0(final InternalPrinter e, final InternalParser e2) {
        this.iFormatter = null;
        this.iElementPairs.add(e);
        this.iElementPairs.add(e2);
        return this;
    }
    
    public DateTimeFormatterBuilder appendLiteral(final char c) {
        return this.append0(new CharacterLiteral(c));
    }
    
    public DateTimeFormatterBuilder appendLiteral(final String s) {
        if (s == null) {
            throw new IllegalArgumentException("Literal must not be null");
        }
        switch (s.length()) {
            case 0: {
                return this;
            }
            case 1: {
                return this.append0(new CharacterLiteral(s.charAt(0)));
            }
            default: {
                return this.append0(new StringLiteral(s));
            }
        }
    }
    
    public DateTimeFormatterBuilder appendDecimal(final DateTimeFieldType dateTimeFieldType, final int n, int n2) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n2 < n) {
            n2 = n;
        }
        if (n < 0 || n2 <= 0) {
            throw new IllegalArgumentException();
        }
        if (n <= 1) {
            return this.append0(new UnpaddedNumber(dateTimeFieldType, n2, false));
        }
        return this.append0(new PaddedNumber(dateTimeFieldType, n2, false, n));
    }
    
    public DateTimeFormatterBuilder appendFixedDecimal(final DateTimeFieldType dateTimeFieldType, final int i) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (i <= 0) {
            throw new IllegalArgumentException("Illegal number of digits: " + i);
        }
        return this.append0(new FixedNumber(dateTimeFieldType, i, false));
    }
    
    public DateTimeFormatterBuilder appendSignedDecimal(final DateTimeFieldType dateTimeFieldType, final int n, int n2) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n2 < n) {
            n2 = n;
        }
        if (n < 0 || n2 <= 0) {
            throw new IllegalArgumentException();
        }
        if (n <= 1) {
            return this.append0(new UnpaddedNumber(dateTimeFieldType, n2, true));
        }
        return this.append0(new PaddedNumber(dateTimeFieldType, n2, true, n));
    }
    
    public DateTimeFormatterBuilder appendFixedSignedDecimal(final DateTimeFieldType dateTimeFieldType, final int i) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (i <= 0) {
            throw new IllegalArgumentException("Illegal number of digits: " + i);
        }
        return this.append0(new FixedNumber(dateTimeFieldType, i, true));
    }
    
    public DateTimeFormatterBuilder appendText(final DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        return this.append0(new TextField(dateTimeFieldType, false));
    }
    
    public DateTimeFormatterBuilder appendShortText(final DateTimeFieldType dateTimeFieldType) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        return this.append0(new TextField(dateTimeFieldType, true));
    }
    
    public DateTimeFormatterBuilder appendFraction(final DateTimeFieldType dateTimeFieldType, final int n, int n2) {
        if (dateTimeFieldType == null) {
            throw new IllegalArgumentException("Field type must not be null");
        }
        if (n2 < n) {
            n2 = n;
        }
        if (n < 0 || n2 <= 0) {
            throw new IllegalArgumentException();
        }
        return this.append0(new Fraction(dateTimeFieldType, n, n2));
    }
    
    public DateTimeFormatterBuilder appendFractionOfSecond(final int n, final int n2) {
        return this.appendFraction(DateTimeFieldType.secondOfDay(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendFractionOfMinute(final int n, final int n2) {
        return this.appendFraction(DateTimeFieldType.minuteOfDay(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendFractionOfHour(final int n, final int n2) {
        return this.appendFraction(DateTimeFieldType.hourOfDay(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendFractionOfDay(final int n, final int n2) {
        return this.appendFraction(DateTimeFieldType.dayOfYear(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendMillisOfSecond(final int n) {
        return this.appendDecimal(DateTimeFieldType.millisOfSecond(), n, 3);
    }
    
    public DateTimeFormatterBuilder appendMillisOfDay(final int n) {
        return this.appendDecimal(DateTimeFieldType.millisOfDay(), n, 8);
    }
    
    public DateTimeFormatterBuilder appendSecondOfMinute(final int n) {
        return this.appendDecimal(DateTimeFieldType.secondOfMinute(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendSecondOfDay(final int n) {
        return this.appendDecimal(DateTimeFieldType.secondOfDay(), n, 5);
    }
    
    public DateTimeFormatterBuilder appendMinuteOfHour(final int n) {
        return this.appendDecimal(DateTimeFieldType.minuteOfHour(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendMinuteOfDay(final int n) {
        return this.appendDecimal(DateTimeFieldType.minuteOfDay(), n, 4);
    }
    
    public DateTimeFormatterBuilder appendHourOfDay(final int n) {
        return this.appendDecimal(DateTimeFieldType.hourOfDay(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendClockhourOfDay(final int n) {
        return this.appendDecimal(DateTimeFieldType.clockhourOfDay(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendHourOfHalfday(final int n) {
        return this.appendDecimal(DateTimeFieldType.hourOfHalfday(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendClockhourOfHalfday(final int n) {
        return this.appendDecimal(DateTimeFieldType.clockhourOfHalfday(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendDayOfWeek(final int n) {
        return this.appendDecimal(DateTimeFieldType.dayOfWeek(), n, 1);
    }
    
    public DateTimeFormatterBuilder appendDayOfMonth(final int n) {
        return this.appendDecimal(DateTimeFieldType.dayOfMonth(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendDayOfYear(final int n) {
        return this.appendDecimal(DateTimeFieldType.dayOfYear(), n, 3);
    }
    
    public DateTimeFormatterBuilder appendWeekOfWeekyear(final int n) {
        return this.appendDecimal(DateTimeFieldType.weekOfWeekyear(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendWeekyear(final int n, final int n2) {
        return this.appendSignedDecimal(DateTimeFieldType.weekyear(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendMonthOfYear(final int n) {
        return this.appendDecimal(DateTimeFieldType.monthOfYear(), n, 2);
    }
    
    public DateTimeFormatterBuilder appendYear(final int n, final int n2) {
        return this.appendSignedDecimal(DateTimeFieldType.year(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendTwoDigitYear(final int n) {
        return this.appendTwoDigitYear(n, false);
    }
    
    public DateTimeFormatterBuilder appendTwoDigitYear(final int n, final boolean b) {
        return this.append0(new TwoDigitYear(DateTimeFieldType.year(), n, b));
    }
    
    public DateTimeFormatterBuilder appendTwoDigitWeekyear(final int n) {
        return this.appendTwoDigitWeekyear(n, false);
    }
    
    public DateTimeFormatterBuilder appendTwoDigitWeekyear(final int n, final boolean b) {
        return this.append0(new TwoDigitYear(DateTimeFieldType.weekyear(), n, b));
    }
    
    public DateTimeFormatterBuilder appendYearOfEra(final int n, final int n2) {
        return this.appendDecimal(DateTimeFieldType.yearOfEra(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendYearOfCentury(final int n, final int n2) {
        return this.appendDecimal(DateTimeFieldType.yearOfCentury(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendCenturyOfEra(final int n, final int n2) {
        return this.appendSignedDecimal(DateTimeFieldType.centuryOfEra(), n, n2);
    }
    
    public DateTimeFormatterBuilder appendHalfdayOfDayText() {
        return this.appendText(DateTimeFieldType.halfdayOfDay());
    }
    
    public DateTimeFormatterBuilder appendDayOfWeekText() {
        return this.appendText(DateTimeFieldType.dayOfWeek());
    }
    
    public DateTimeFormatterBuilder appendDayOfWeekShortText() {
        return this.appendShortText(DateTimeFieldType.dayOfWeek());
    }
    
    public DateTimeFormatterBuilder appendMonthOfYearText() {
        return this.appendText(DateTimeFieldType.monthOfYear());
    }
    
    public DateTimeFormatterBuilder appendMonthOfYearShortText() {
        return this.appendShortText(DateTimeFieldType.monthOfYear());
    }
    
    public DateTimeFormatterBuilder appendEraText() {
        return this.appendText(DateTimeFieldType.era());
    }
    
    public DateTimeFormatterBuilder appendTimeZoneName() {
        return this.append0(new TimeZoneName(0, null), null);
    }
    
    public DateTimeFormatterBuilder appendTimeZoneName(final Map<String, DateTimeZone> map) {
        final TimeZoneName timeZoneName = new TimeZoneName(0, map);
        return this.append0(timeZoneName, timeZoneName);
    }
    
    public DateTimeFormatterBuilder appendTimeZoneShortName() {
        return this.append0(new TimeZoneName(1, null), null);
    }
    
    public DateTimeFormatterBuilder appendTimeZoneShortName(final Map<String, DateTimeZone> map) {
        final TimeZoneName timeZoneName = new TimeZoneName(1, map);
        return this.append0(timeZoneName, timeZoneName);
    }
    
    public DateTimeFormatterBuilder appendTimeZoneId() {
        return this.append0(TimeZoneId.INSTANCE, TimeZoneId.INSTANCE);
    }
    
    public DateTimeFormatterBuilder appendTimeZoneOffset(final String s, final boolean b, final int n, final int n2) {
        return this.append0(new TimeZoneOffset(s, s, b, n, n2));
    }
    
    public DateTimeFormatterBuilder appendTimeZoneOffset(final String s, final String s2, final boolean b, final int n, final int n2) {
        return this.append0(new TimeZoneOffset(s, s2, b, n, n2));
    }
    
    public DateTimeFormatterBuilder appendPattern(final String s) {
        DateTimeFormat.appendPatternTo(this, s);
        return this;
    }
    
    private Object getFormatter() {
        Object iFormatter = this.iFormatter;
        if (iFormatter == null) {
            if (this.iElementPairs.size() == 2) {
                final Object value = this.iElementPairs.get(0);
                final Object value2 = this.iElementPairs.get(1);
                if (value != null) {
                    if (value == value2 || value2 == null) {
                        iFormatter = value;
                    }
                }
                else {
                    iFormatter = value2;
                }
            }
            if (iFormatter == null) {
                iFormatter = new Composite(this.iElementPairs);
            }
            this.iFormatter = iFormatter;
        }
        return iFormatter;
    }
    
    private boolean isPrinter(final Object o) {
        return o instanceof InternalPrinter && (!(o instanceof Composite) || ((Composite)o).isPrinter());
    }
    
    private boolean isParser(final Object o) {
        return o instanceof InternalParser && (!(o instanceof Composite) || ((Composite)o).isParser());
    }
    
    private boolean isFormatter(final Object o) {
        return this.isPrinter(o) || this.isParser(o);
    }
    
    static void appendUnknownString(final Appendable appendable, final int n) throws IOException {
        int n2 = n;
        while (--n2 >= 0) {
            appendable.append('\ufffd');
        }
    }
    
    static boolean csStartsWith(final CharSequence charSequence, final int n, final String s) {
        final int length = s.length();
        if (charSequence.length() - n < length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (charSequence.charAt(n + i) != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    static boolean csStartsWithIgnoreCase(final CharSequence charSequence, final int n, final String s) {
        final int length = s.length();
        if (charSequence.length() - n < length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            final char char1 = charSequence.charAt(n + i);
            final char char2 = s.charAt(i);
            if (char1 != char2) {
                final char upperCase = Character.toUpperCase(char1);
                final char upperCase2 = Character.toUpperCase(char2);
                if (upperCase != upperCase2 && Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    static class CharacterLiteral implements InternalPrinter, InternalParser
    {
        private final char iValue;
        
        CharacterLiteral(final char iValue) {
            this.iValue = iValue;
        }
        
        public int estimatePrintedLength() {
            return 1;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            appendable.append(this.iValue);
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            appendable.append(this.iValue);
        }
        
        public int estimateParsedLength() {
            return 1;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            if (n >= charSequence.length()) {
                return ~n;
            }
            final char char1 = charSequence.charAt(n);
            final char iValue = this.iValue;
            if (char1 != iValue) {
                final char upperCase = Character.toUpperCase(char1);
                final char upperCase2 = Character.toUpperCase(iValue);
                if (upperCase != upperCase2 && Character.toLowerCase(upperCase) != Character.toLowerCase(upperCase2)) {
                    return ~n;
                }
            }
            return n + 1;
        }
    }
    
    static class StringLiteral implements InternalPrinter, InternalParser
    {
        private final String iValue;
        
        StringLiteral(final String iValue) {
            this.iValue = iValue;
        }
        
        public int estimatePrintedLength() {
            return this.iValue.length();
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            appendable.append(this.iValue);
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            appendable.append(this.iValue);
        }
        
        public int estimateParsedLength() {
            return this.iValue.length();
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            if (DateTimeFormatterBuilder.csStartsWithIgnoreCase(charSequence, n, this.iValue)) {
                return n + this.iValue.length();
            }
            return ~n;
        }
    }
    
    abstract static class NumberFormatter implements InternalPrinter, InternalParser
    {
        protected final DateTimeFieldType iFieldType;
        protected final int iMaxParsedDigits;
        protected final boolean iSigned;
        
        NumberFormatter(final DateTimeFieldType iFieldType, final int iMaxParsedDigits, final boolean iSigned) {
            this.iFieldType = iFieldType;
            this.iMaxParsedDigits = iMaxParsedDigits;
            this.iSigned = iSigned;
        }
        
        public int estimateParsedLength() {
            return this.iMaxParsedDigits;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, int n) {
            int n2 = Math.min(this.iMaxParsedDigits, charSequence.length() - n);
            boolean b = false;
            int i = 0;
            while (i < n2) {
                final char char1 = charSequence.charAt(n + i);
                if (i == 0 && (char1 == '-' || char1 == '+') && this.iSigned) {
                    b = (char1 == '-');
                    final char char2;
                    if (i + 1 >= n2 || (char2 = charSequence.charAt(n + i + 1)) < '0') {
                        break;
                    }
                    if (char2 > '9') {
                        break;
                    }
                    if (b) {
                        ++i;
                    }
                    else {
                        ++n;
                    }
                    n2 = Math.min(n2 + 1, charSequence.length() - n);
                }
                else {
                    if (char1 < '0') {
                        break;
                    }
                    if (char1 > '9') {
                        break;
                    }
                    ++i;
                }
            }
            if (i == 0) {
                return ~n;
            }
            int int1;
            if (i >= 9) {
                int1 = Integer.parseInt(charSequence.subSequence(n, n += i).toString());
            }
            else {
                int j = n;
                if (b) {
                    ++j;
                }
                try {
                    int1 = charSequence.charAt(j++) - '0';
                }
                catch (StringIndexOutOfBoundsException ex) {
                    return ~n;
                }
                for (n += i; j < n; int1 = (int1 << 3) + (int1 << 1) + charSequence.charAt(j++) - 48) {}
                if (b) {
                    int1 = -int1;
                }
            }
            dateTimeParserBucket.saveField(this.iFieldType, int1);
            return n;
        }
    }
    
    static class UnpaddedNumber extends NumberFormatter
    {
        protected UnpaddedNumber(final DateTimeFieldType dateTimeFieldType, final int n, final boolean b) {
            super(dateTimeFieldType, n, b);
        }
        
        public int estimatePrintedLength() {
            return this.iMaxParsedDigits;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            try {
                FormatUtils.appendUnpaddedInteger(appendable, this.iFieldType.getField(chronology).get(n));
            }
            catch (RuntimeException ex) {
                appendable.append('\ufffd');
            }
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            if (readablePartial.isSupported(this.iFieldType)) {
                try {
                    FormatUtils.appendUnpaddedInteger(appendable, readablePartial.get(this.iFieldType));
                }
                catch (RuntimeException ex) {
                    appendable.append('\ufffd');
                }
            }
            else {
                appendable.append('\ufffd');
            }
        }
    }
    
    static class PaddedNumber extends NumberFormatter
    {
        protected final int iMinPrintedDigits;
        
        protected PaddedNumber(final DateTimeFieldType dateTimeFieldType, final int n, final boolean b, final int iMinPrintedDigits) {
            super(dateTimeFieldType, n, b);
            this.iMinPrintedDigits = iMinPrintedDigits;
        }
        
        public int estimatePrintedLength() {
            return this.iMaxParsedDigits;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            try {
                FormatUtils.appendPaddedInteger(appendable, this.iFieldType.getField(chronology).get(n), this.iMinPrintedDigits);
            }
            catch (RuntimeException ex) {
                DateTimeFormatterBuilder.appendUnknownString(appendable, this.iMinPrintedDigits);
            }
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            if (readablePartial.isSupported(this.iFieldType)) {
                try {
                    FormatUtils.appendPaddedInteger(appendable, readablePartial.get(this.iFieldType), this.iMinPrintedDigits);
                }
                catch (RuntimeException ex) {
                    DateTimeFormatterBuilder.appendUnknownString(appendable, this.iMinPrintedDigits);
                }
            }
            else {
                DateTimeFormatterBuilder.appendUnknownString(appendable, this.iMinPrintedDigits);
            }
        }
    }
    
    static class FixedNumber extends PaddedNumber
    {
        protected FixedNumber(final DateTimeFieldType dateTimeFieldType, final int n, final boolean b) {
            super(dateTimeFieldType, n, b, n);
        }
        
        @Override
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            final int into = super.parseInto(dateTimeParserBucket, charSequence, n);
            if (into < 0) {
                return into;
            }
            int n2 = n + this.iMaxParsedDigits;
            if (into != n2) {
                if (this.iSigned) {
                    final char char1 = charSequence.charAt(n);
                    if (char1 == '-' || char1 == '+') {
                        ++n2;
                    }
                }
                if (into > n2) {
                    return ~(n2 + 1);
                }
                if (into < n2) {
                    return ~into;
                }
            }
            return into;
        }
    }
    
    static class TwoDigitYear implements InternalPrinter, InternalParser
    {
        private final DateTimeFieldType iType;
        private final int iPivot;
        private final boolean iLenientParse;
        
        TwoDigitYear(final DateTimeFieldType iType, final int iPivot, final boolean iLenientParse) {
            this.iType = iType;
            this.iPivot = iPivot;
            this.iLenientParse = iLenientParse;
        }
        
        public int estimateParsedLength() {
            return this.iLenientParse ? 4 : 2;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, int n) {
            int b = charSequence.length() - n;
            if (!this.iLenientParse) {
                if (Math.min(2, b) < 2) {
                    return ~n;
                }
            }
            else {
                boolean b2 = false;
                boolean b3 = false;
                int i = 0;
                while (i < b) {
                    final char char1 = charSequence.charAt(n + i);
                    if (i == 0 && (char1 == '-' || char1 == '+')) {
                        b2 = true;
                        b3 = (char1 == '-');
                        if (b3) {
                            ++i;
                        }
                        else {
                            ++n;
                            --b;
                        }
                    }
                    else {
                        if (char1 < '0') {
                            break;
                        }
                        if (char1 > '9') {
                            break;
                        }
                        ++i;
                    }
                }
                if (i == 0) {
                    return ~n;
                }
                if (b2 || i != 2) {
                    int int1;
                    if (i >= 9) {
                        int1 = Integer.parseInt(charSequence.subSequence(n, n += i).toString());
                    }
                    else {
                        int j = n;
                        if (b3) {
                            ++j;
                        }
                        try {
                            int1 = charSequence.charAt(j++) - '0';
                        }
                        catch (StringIndexOutOfBoundsException ex) {
                            return ~n;
                        }
                        for (n += i; j < n; int1 = (int1 << 3) + (int1 << 1) + charSequence.charAt(j++) - 48) {}
                        if (b3) {
                            int1 = -int1;
                        }
                    }
                    dateTimeParserBucket.saveField(this.iType, int1);
                    return n;
                }
            }
            final char char2 = charSequence.charAt(n);
            if (char2 < '0' || char2 > '9') {
                return ~n;
            }
            final int n2 = char2 - '0';
            final char char3 = charSequence.charAt(n + 1);
            if (char3 < '0' || char3 > '9') {
                return ~n;
            }
            final int n3 = (n2 << 3) + (n2 << 1) + char3 - 48;
            int n4 = this.iPivot;
            if (dateTimeParserBucket.getPivotYear() != null) {
                n4 = dateTimeParserBucket.getPivotYear();
            }
            final int n5 = n4 - 50;
            int n6;
            if (n5 >= 0) {
                n6 = n5 % 100;
            }
            else {
                n6 = 99 + (n5 + 1) % 100;
            }
            dateTimeParserBucket.saveField(this.iType, n3 + (n5 + ((n3 < n6) ? 100 : 0) - n6));
            return n + 2;
        }
        
        public int estimatePrintedLength() {
            return 2;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            final int twoDigitYear = this.getTwoDigitYear(n, chronology);
            if (twoDigitYear < 0) {
                appendable.append('\ufffd');
                appendable.append('\ufffd');
            }
            else {
                FormatUtils.appendPaddedInteger(appendable, twoDigitYear, 2);
            }
        }
        
        private int getTwoDigitYear(final long n, final Chronology chronology) {
            try {
                int value = this.iType.getField(chronology).get(n);
                if (value < 0) {
                    value = -value;
                }
                return value % 100;
            }
            catch (RuntimeException ex) {
                return -1;
            }
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            final int twoDigitYear = this.getTwoDigitYear(readablePartial);
            if (twoDigitYear < 0) {
                appendable.append('\ufffd');
                appendable.append('\ufffd');
            }
            else {
                FormatUtils.appendPaddedInteger(appendable, twoDigitYear, 2);
            }
        }
        
        private int getTwoDigitYear(final ReadablePartial readablePartial) {
            if (readablePartial.isSupported(this.iType)) {
                try {
                    int value = readablePartial.get(this.iType);
                    if (value < 0) {
                        value = -value;
                    }
                    return value % 100;
                }
                catch (RuntimeException ex) {}
            }
            return -1;
        }
    }
    
    static class TextField implements InternalPrinter, InternalParser
    {
        private static Map<Locale, Map<DateTimeFieldType, Object[]>> cParseCache;
        private final DateTimeFieldType iFieldType;
        private final boolean iShort;
        
        TextField(final DateTimeFieldType iFieldType, final boolean iShort) {
            this.iFieldType = iFieldType;
            this.iShort = iShort;
        }
        
        public int estimatePrintedLength() {
            return this.iShort ? 6 : 20;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            try {
                appendable.append(this.print(n, chronology, locale));
            }
            catch (RuntimeException ex) {
                appendable.append('\ufffd');
            }
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            try {
                appendable.append(this.print(readablePartial, locale));
            }
            catch (RuntimeException ex) {
                appendable.append('\ufffd');
            }
        }
        
        private String print(final long n, final Chronology chronology, final Locale locale) {
            final DateTimeField field = this.iFieldType.getField(chronology);
            if (this.iShort) {
                return field.getAsShortText(n, locale);
            }
            return field.getAsText(n, locale);
        }
        
        private String print(final ReadablePartial readablePartial, final Locale locale) {
            if (!readablePartial.isSupported(this.iFieldType)) {
                return "\ufffd";
            }
            final DateTimeField field = this.iFieldType.getField(readablePartial.getChronology());
            if (this.iShort) {
                return field.getAsShortText(readablePartial, locale);
            }
            return field.getAsText(readablePartial, locale);
        }
        
        public int estimateParsedLength() {
            return this.estimatePrintedLength();
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            final Locale locale = dateTimeParserBucket.getLocale();
            Map<DateTimeFieldType, Object[]> map = TextField.cParseCache.get(locale);
            if (map == null) {
                map = new ConcurrentHashMap<DateTimeFieldType, Object[]>();
                TextField.cParseCache.put(locale, map);
            }
            final Object[] array = map.get(this.iFieldType);
            Map<String, Boolean> map2;
            int i;
            if (array == null) {
                map2 = new ConcurrentHashMap<String, Boolean>(32);
                final MutableDateTime.Property property = new MutableDateTime(0L, DateTimeZone.UTC).property(this.iFieldType);
                final int minimumValueOverall = property.getMinimumValueOverall();
                final int maximumValueOverall = property.getMaximumValueOverall();
                if (maximumValueOverall - minimumValueOverall > 32) {
                    return ~n;
                }
                i = property.getMaximumTextLength(locale);
                for (int j = minimumValueOverall; j <= maximumValueOverall; ++j) {
                    property.set(j);
                    map2.put(property.getAsShortText(locale), Boolean.TRUE);
                    map2.put(property.getAsShortText(locale).toLowerCase(locale), Boolean.TRUE);
                    map2.put(property.getAsShortText(locale).toUpperCase(locale), Boolean.TRUE);
                    map2.put(property.getAsText(locale), Boolean.TRUE);
                    map2.put(property.getAsText(locale).toLowerCase(locale), Boolean.TRUE);
                    map2.put(property.getAsText(locale).toUpperCase(locale), Boolean.TRUE);
                }
                if ("en".equals(locale.getLanguage()) && this.iFieldType == DateTimeFieldType.era()) {
                    map2.put("BCE", Boolean.TRUE);
                    map2.put("bce", Boolean.TRUE);
                    map2.put("CE", Boolean.TRUE);
                    map2.put("ce", Boolean.TRUE);
                    i = 3;
                }
                map.put(this.iFieldType, new Object[] { map2, i });
            }
            else {
                map2 = (Map<String, Boolean>)array[0];
                i = (int)array[1];
            }
            for (int k = Math.min(charSequence.length(), n + i); k > n; --k) {
                final String string = charSequence.subSequence(n, k).toString();
                if (map2.containsKey(string)) {
                    dateTimeParserBucket.saveField(this.iFieldType, string, locale);
                    return k;
                }
            }
            return ~n;
        }
        
        static {
            TextField.cParseCache = new ConcurrentHashMap<Locale, Map<DateTimeFieldType, Object[]>>();
        }
    }
    
    static class Fraction implements InternalPrinter, InternalParser
    {
        private final DateTimeFieldType iFieldType;
        protected int iMinDigits;
        protected int iMaxDigits;
        
        protected Fraction(final DateTimeFieldType iFieldType, final int iMinDigits, int iMaxDigits) {
            this.iFieldType = iFieldType;
            if (iMaxDigits > 18) {
                iMaxDigits = 18;
            }
            this.iMinDigits = iMinDigits;
            this.iMaxDigits = iMaxDigits;
        }
        
        public int estimatePrintedLength() {
            return this.iMaxDigits;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            this.printTo(appendable, n, chronology);
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            this.printTo(appendable, readablePartial.getChronology().set(readablePartial, 0L), readablePartial.getChronology());
        }
        
        protected void printTo(final Appendable appendable, final long n, final Chronology chronology) throws IOException {
            final DateTimeField field = this.iFieldType.getField(chronology);
            int iMinDigits = this.iMinDigits;
            long remainder;
            try {
                remainder = field.remainder(n);
            }
            catch (RuntimeException ex) {
                DateTimeFormatterBuilder.appendUnknownString(appendable, iMinDigits);
                return;
            }
            if (remainder == 0L) {
                while (--iMinDigits >= 0) {
                    appendable.append('0');
                }
                return;
            }
            final long[] fractionData = this.getFractionData(remainder, field);
            final long i = fractionData[0];
            final int n2 = (int)fractionData[1];
            String s;
            if ((i & 0x7FFFFFFFL) == i) {
                s = Integer.toString((int)i);
            }
            else {
                s = Long.toString(i);
            }
            int j;
            int n3;
            for (j = s.length(), n3 = n2; j < n3; --n3) {
                appendable.append('0');
                --iMinDigits;
            }
            if (iMinDigits < n3) {
                while (iMinDigits < n3 && j > 1 && s.charAt(j - 1) == '0') {
                    --n3;
                    --j;
                }
                if (j < s.length()) {
                    for (int k = 0; k < j; ++k) {
                        appendable.append(s.charAt(k));
                    }
                    return;
                }
            }
            appendable.append(s);
        }
        
        private long[] getFractionData(final long n, final DateTimeField dateTimeField) {
            final long unitMillis = dateTimeField.getDurationField().getUnitMillis();
            int iMaxDigits = this.iMaxDigits;
            long n2 = 0L;
            while (true) {
                switch (iMaxDigits) {
                    default: {
                        n2 = 1L;
                        break;
                    }
                    case 1: {
                        n2 = 10L;
                        break;
                    }
                    case 2: {
                        n2 = 100L;
                        break;
                    }
                    case 3: {
                        n2 = 1000L;
                        break;
                    }
                    case 4: {
                        n2 = 10000L;
                        break;
                    }
                    case 5: {
                        n2 = 100000L;
                        break;
                    }
                    case 6: {
                        n2 = 1000000L;
                        break;
                    }
                    case 7: {
                        n2 = 10000000L;
                        break;
                    }
                    case 8: {
                        n2 = 100000000L;
                        break;
                    }
                    case 9: {
                        n2 = 1000000000L;
                        break;
                    }
                    case 10: {
                        n2 = 10000000000L;
                        break;
                    }
                    case 11: {
                        n2 = 100000000000L;
                        break;
                    }
                    case 12: {
                        n2 = 1000000000000L;
                        break;
                    }
                    case 13: {
                        n2 = 10000000000000L;
                        break;
                    }
                    case 14: {
                        n2 = 100000000000000L;
                        break;
                    }
                    case 15: {
                        n2 = 1000000000000000L;
                        break;
                    }
                    case 16: {
                        n2 = 10000000000000000L;
                        break;
                    }
                    case 17: {
                        n2 = 100000000000000000L;
                        break;
                    }
                    case 18: {
                        n2 = 1000000000000000000L;
                        break;
                    }
                }
                if (unitMillis * n2 / n2 == unitMillis) {
                    break;
                }
                --iMaxDigits;
            }
            return new long[] { n * n2 / unitMillis, iMaxDigits };
        }
        
        public int estimateParsedLength() {
            return this.iMaxDigits;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            final DateTimeField field = this.iFieldType.getField(dateTimeParserBucket.getChronology());
            int min;
            long n2;
            long n3;
            int i;
            char char1;
            long n4;
            for (min = Math.min(this.iMaxDigits, charSequence.length() - n), n2 = 0L, n3 = field.getDurationField().getUnitMillis() * 10L, i = 0; i < min; ++i, n4 = n3 / 10L, n2 += (char1 - '0') * n4, n3 = n4) {
                char1 = charSequence.charAt(n + i);
                if (char1 < '0') {
                    break;
                }
                if (char1 > '9') {
                    break;
                }
            }
            final long n5 = n2 / 10L;
            if (i == 0) {
                return ~n;
            }
            if (n5 > 2147483647L) {
                return ~n;
            }
            dateTimeParserBucket.saveField(new PreciseDateTimeField(DateTimeFieldType.millisOfSecond(), MillisDurationField.INSTANCE, field.getDurationField()), (int)n5);
            return n + i;
        }
    }
    
    static class TimeZoneOffset implements InternalPrinter, InternalParser
    {
        private final String iZeroOffsetPrintText;
        private final String iZeroOffsetParseText;
        private final boolean iShowSeparators;
        private final int iMinFields;
        private final int iMaxFields;
        
        TimeZoneOffset(final String iZeroOffsetPrintText, final String iZeroOffsetParseText, final boolean iShowSeparators, int iMinFields, int iMaxFields) {
            this.iZeroOffsetPrintText = iZeroOffsetPrintText;
            this.iZeroOffsetParseText = iZeroOffsetParseText;
            this.iShowSeparators = iShowSeparators;
            if (iMinFields <= 0 || iMaxFields < iMinFields) {
                throw new IllegalArgumentException();
            }
            if (iMinFields > 4) {
                iMinFields = 4;
                iMaxFields = 4;
            }
            this.iMinFields = iMinFields;
            this.iMaxFields = iMaxFields;
        }
        
        public int estimatePrintedLength() {
            int length = 1 + this.iMinFields << 1;
            if (this.iShowSeparators) {
                length += this.iMinFields - 1;
            }
            if (this.iZeroOffsetPrintText != null && this.iZeroOffsetPrintText.length() > length) {
                length = this.iZeroOffsetPrintText.length();
            }
            return length;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            if (dateTimeZone == null) {
                return;
            }
            if (n2 == 0 && this.iZeroOffsetPrintText != null) {
                appendable.append(this.iZeroOffsetPrintText);
                return;
            }
            if (n2 >= 0) {
                appendable.append('+');
            }
            else {
                appendable.append('-');
                n2 = -n2;
            }
            final int n3 = n2 / 3600000;
            FormatUtils.appendPaddedInteger(appendable, n3, 2);
            if (this.iMaxFields == 1) {
                return;
            }
            n2 -= n3 * 3600000;
            if (n2 == 0 && this.iMinFields <= 1) {
                return;
            }
            final int n4 = n2 / 60000;
            if (this.iShowSeparators) {
                appendable.append(':');
            }
            FormatUtils.appendPaddedInteger(appendable, n4, 2);
            if (this.iMaxFields == 2) {
                return;
            }
            n2 -= n4 * 60000;
            if (n2 == 0 && this.iMinFields <= 2) {
                return;
            }
            final int n5 = n2 / 1000;
            if (this.iShowSeparators) {
                appendable.append(':');
            }
            FormatUtils.appendPaddedInteger(appendable, n5, 2);
            if (this.iMaxFields == 3) {
                return;
            }
            n2 -= n5 * 1000;
            if (n2 == 0 && this.iMinFields <= 3) {
                return;
            }
            if (this.iShowSeparators) {
                appendable.append('.');
            }
            FormatUtils.appendPaddedInteger(appendable, n2, 3);
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
        }
        
        public int estimateParsedLength() {
            return this.estimatePrintedLength();
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, int n) {
            int n2 = charSequence.length() - n;
            Label_0098: {
                if (this.iZeroOffsetParseText != null) {
                    if (this.iZeroOffsetParseText.length() == 0) {
                        if (n2 > 0) {
                            final char char1 = charSequence.charAt(n);
                            if (char1 == '-') {
                                break Label_0098;
                            }
                            if (char1 == '+') {
                                break Label_0098;
                            }
                        }
                        dateTimeParserBucket.setOffset(Integer.valueOf(0));
                        return n;
                    }
                    if (DateTimeFormatterBuilder.csStartsWithIgnoreCase(charSequence, n, this.iZeroOffsetParseText)) {
                        dateTimeParserBucket.setOffset(Integer.valueOf(0));
                        return n + this.iZeroOffsetParseText.length();
                    }
                }
            }
            if (n2 <= 1) {
                return ~n;
            }
            final char char2 = charSequence.charAt(n);
            boolean b;
            if (char2 == '-') {
                b = true;
            }
            else {
                if (char2 != '+') {
                    return ~n;
                }
                b = false;
            }
            --n2;
            ++n;
            if (this.digitCount(charSequence, n, 2) < 2) {
                return ~n;
            }
            final int twoDigits = FormatUtils.parseTwoDigits(charSequence, n);
            if (twoDigits > 23) {
                return ~n;
            }
            int n3 = twoDigits * 3600000;
            n2 -= 2;
            n += 2;
            Label_0569: {
                if (n2 > 0) {
                    final char char3 = charSequence.charAt(n);
                    boolean b2;
                    if (char3 == ':') {
                        b2 = true;
                        --n2;
                        ++n;
                    }
                    else {
                        if (char3 < '0' || char3 > '9') {
                            break Label_0569;
                        }
                        b2 = false;
                    }
                    final int digitCount = this.digitCount(charSequence, n, 2);
                    if (digitCount != 0 || b2) {
                        if (digitCount < 2) {
                            return ~n;
                        }
                        final int twoDigits2 = FormatUtils.parseTwoDigits(charSequence, n);
                        if (twoDigits2 > 59) {
                            return ~n;
                        }
                        n3 += twoDigits2 * 60000;
                        n2 -= 2;
                        n += 2;
                        if (n2 > 0) {
                            if (b2) {
                                if (charSequence.charAt(n) != ':') {
                                    break Label_0569;
                                }
                                --n2;
                                ++n;
                            }
                            final int digitCount2 = this.digitCount(charSequence, n, 2);
                            if (digitCount2 != 0 || b2) {
                                if (digitCount2 < 2) {
                                    return ~n;
                                }
                                final int twoDigits3 = FormatUtils.parseTwoDigits(charSequence, n);
                                if (twoDigits3 > 59) {
                                    return ~n;
                                }
                                n3 += twoDigits3 * 1000;
                                n2 -= 2;
                                n += 2;
                                if (n2 > 0) {
                                    if (b2) {
                                        if (charSequence.charAt(n) != '.' && charSequence.charAt(n) != ',') {
                                            break Label_0569;
                                        }
                                        --n2;
                                        ++n;
                                    }
                                    final int digitCount3 = this.digitCount(charSequence, n, 3);
                                    if (digitCount3 != 0 || b2) {
                                        if (digitCount3 < 1) {
                                            return ~n;
                                        }
                                        n3 += (charSequence.charAt(n++) - '0') * 100;
                                        if (digitCount3 > 1) {
                                            n3 += (charSequence.charAt(n++) - '0') * 10;
                                            if (digitCount3 > 2) {
                                                n3 += charSequence.charAt(n++) - '0';
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            dateTimeParserBucket.setOffset(Integer.valueOf(b ? (-n3) : n3));
            return n;
        }
        
        private int digitCount(final CharSequence charSequence, final int n, int b) {
            int i = Math.min(charSequence.length() - n, b);
            b = 0;
            while (i > 0) {
                final char char1 = charSequence.charAt(n + b);
                if (char1 < '0') {
                    break;
                }
                if (char1 > '9') {
                    break;
                }
                ++b;
                --i;
            }
            return b;
        }
    }
    
    static class TimeZoneName implements InternalPrinter, InternalParser
    {
        static final int LONG_NAME = 0;
        static final int SHORT_NAME = 1;
        private final Map<String, DateTimeZone> iParseLookup;
        private final int iType;
        
        TimeZoneName(final int iType, final Map<String, DateTimeZone> iParseLookup) {
            this.iType = iType;
            this.iParseLookup = iParseLookup;
        }
        
        public int estimatePrintedLength() {
            return (this.iType == 1) ? 4 : 20;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            appendable.append(this.print(n - n2, dateTimeZone, locale));
        }
        
        private String print(final long n, final DateTimeZone dateTimeZone, final Locale locale) {
            if (dateTimeZone == null) {
                return "";
            }
            switch (this.iType) {
                case 0: {
                    return dateTimeZone.getName(n, locale);
                }
                case 1: {
                    return dateTimeZone.getShortName(n, locale);
                }
                default: {
                    return "";
                }
            }
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
        }
        
        public int estimateParsedLength() {
            return (this.iType == 1) ? 4 : 20;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            final Map<String, DateTimeZone> iParseLookup = this.iParseLookup;
            final Map<String, DateTimeZone> map = (iParseLookup != null) ? iParseLookup : DateTimeUtils.getDefaultTimeZoneNames();
            String s = null;
            for (final String s2 : map.keySet()) {
                if (DateTimeFormatterBuilder.csStartsWith(charSequence, n, s2) && (s == null || s2.length() > s.length())) {
                    s = s2;
                }
            }
            if (s != null) {
                dateTimeParserBucket.setZone(map.get(s));
                return n + s.length();
            }
            return ~n;
        }
    }
    
    enum TimeZoneId implements InternalPrinter, InternalParser
    {
        INSTANCE;
        
        static final Set<String> ALL_IDS;
        static final int MAX_LENGTH;
        
        public int estimatePrintedLength() {
            return TimeZoneId.MAX_LENGTH;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            appendable.append((dateTimeZone != null) ? dateTimeZone.getID() : "");
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
        }
        
        public int estimateParsedLength() {
            return TimeZoneId.MAX_LENGTH;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            String s = null;
            for (final String s2 : TimeZoneId.ALL_IDS) {
                if (DateTimeFormatterBuilder.csStartsWith(charSequence, n, s2) && (s == null || s2.length() > s.length())) {
                    s = s2;
                }
            }
            if (s != null) {
                dateTimeParserBucket.setZone(DateTimeZone.forID(s));
                return n + s.length();
            }
            return ~n;
        }
        
        static {
            ALL_IDS = DateTimeZone.getAvailableIDs();
            int max = 0;
            final Iterator<String> iterator = TimeZoneId.ALL_IDS.iterator();
            while (iterator.hasNext()) {
                max = Math.max(max, iterator.next().length());
            }
            MAX_LENGTH = max;
        }
    }
    
    static class Composite implements InternalPrinter, InternalParser
    {
        private final InternalPrinter[] iPrinters;
        private final InternalParser[] iParsers;
        private final int iPrintedLengthEstimate;
        private final int iParsedLengthEstimate;
        
        Composite(final List<Object> list) {
            final ArrayList<InternalPrinter> list2 = new ArrayList<InternalPrinter>();
            final ArrayList list3 = new ArrayList<InternalParser>();
            this.decompose(list, (List<Object>)list2, list3);
            if (list2.contains(null) || list2.isEmpty()) {
                this.iPrinters = null;
                this.iPrintedLengthEstimate = 0;
            }
            else {
                final int size = list2.size();
                this.iPrinters = new InternalPrinter[size];
                int iPrintedLengthEstimate = 0;
                for (int i = 0; i < size; ++i) {
                    final InternalPrinter internalPrinter = list2.get(i);
                    iPrintedLengthEstimate += internalPrinter.estimatePrintedLength();
                    this.iPrinters[i] = internalPrinter;
                }
                this.iPrintedLengthEstimate = iPrintedLengthEstimate;
            }
            if (list3.contains(null) || list3.isEmpty()) {
                this.iParsers = null;
                this.iParsedLengthEstimate = 0;
            }
            else {
                final int size2 = list3.size();
                this.iParsers = new InternalParser[size2];
                int iParsedLengthEstimate = 0;
                for (int j = 0; j < size2; ++j) {
                    final InternalParser internalParser = list3.get(j);
                    iParsedLengthEstimate += internalParser.estimateParsedLength();
                    this.iParsers[j] = internalParser;
                }
                this.iParsedLengthEstimate = iParsedLengthEstimate;
            }
        }
        
        public int estimatePrintedLength() {
            return this.iPrintedLengthEstimate;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, Locale default1) throws IOException {
            final InternalPrinter[] iPrinters = this.iPrinters;
            if (iPrinters == null) {
                throw new UnsupportedOperationException();
            }
            if (default1 == null) {
                default1 = Locale.getDefault();
            }
            for (int length = iPrinters.length, i = 0; i < length; ++i) {
                iPrinters[i].printTo(appendable, n, chronology, n2, dateTimeZone, default1);
            }
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, Locale default1) throws IOException {
            final InternalPrinter[] iPrinters = this.iPrinters;
            if (iPrinters == null) {
                throw new UnsupportedOperationException();
            }
            if (default1 == null) {
                default1 = Locale.getDefault();
            }
            for (int length = iPrinters.length, i = 0; i < length; ++i) {
                iPrinters[i].printTo(appendable, readablePartial, default1);
            }
        }
        
        public int estimateParsedLength() {
            return this.iParsedLengthEstimate;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, int into) {
            final InternalParser[] iParsers = this.iParsers;
            if (iParsers == null) {
                throw new UnsupportedOperationException();
            }
            for (int length = iParsers.length, n = 0; n < length && into >= 0; into = iParsers[n].parseInto(dateTimeParserBucket, charSequence, into), ++n) {}
            return into;
        }
        
        boolean isPrinter() {
            return this.iPrinters != null;
        }
        
        boolean isParser() {
            return this.iParsers != null;
        }
        
        private void decompose(final List<Object> list, final List<Object> list2, final List<Object> list3) {
            for (int size = list.size(), i = 0; i < size; i += 2) {
                final Composite value = list.get(i);
                if (value instanceof Composite) {
                    this.addArrayToList(list2, value.iPrinters);
                }
                else {
                    list2.add(value);
                }
                final Composite value2 = list.get(i + 1);
                if (value2 instanceof Composite) {
                    this.addArrayToList(list3, value2.iParsers);
                }
                else {
                    list3.add(value2);
                }
            }
        }
        
        private void addArrayToList(final List<Object> list, final Object[] array) {
            if (array != null) {
                for (int i = 0; i < array.length; ++i) {
                    list.add(array[i]);
                }
            }
        }
    }
    
    static class MatchingParser implements InternalParser
    {
        private final InternalParser[] iParsers;
        private final int iParsedLengthEstimate;
        
        MatchingParser(final InternalParser[] iParsers) {
            this.iParsers = iParsers;
            int iParsedLengthEstimate = 0;
            int length = iParsers.length;
            while (--length >= 0) {
                final InternalParser internalParser = iParsers[length];
                if (internalParser != null) {
                    final int estimateParsedLength = internalParser.estimateParsedLength();
                    if (estimateParsedLength <= iParsedLengthEstimate) {
                        continue;
                    }
                    iParsedLengthEstimate = estimateParsedLength;
                }
            }
            this.iParsedLengthEstimate = iParsedLengthEstimate;
        }
        
        public int estimateParsedLength() {
            return this.iParsedLengthEstimate;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            final InternalParser[] iParsers = this.iParsers;
            final int length = iParsers.length;
            final Object saveState = dateTimeParserBucket.saveState();
            boolean b = false;
            int n2 = n;
            Object saveState2 = null;
            int n3 = n;
            int i = 0;
            while (i < length) {
                final InternalParser internalParser = iParsers[i];
                if (internalParser == null) {
                    if (n2 <= n) {
                        return n;
                    }
                    b = true;
                    break;
                }
                else {
                    final int into = internalParser.parseInto(dateTimeParserBucket, charSequence, n);
                    if (into >= n) {
                        if (into > n2) {
                            if (into >= charSequence.length() || i + 1 >= length || iParsers[i + 1] == null) {
                                return into;
                            }
                            n2 = into;
                            saveState2 = dateTimeParserBucket.saveState();
                        }
                    }
                    else if (into < 0) {
                        final int n4 = ~into;
                        if (n4 > n3) {
                            n3 = n4;
                        }
                    }
                    dateTimeParserBucket.restoreState(saveState);
                    ++i;
                }
            }
            if (n2 > n || (n2 == n && b)) {
                if (saveState2 != null) {
                    dateTimeParserBucket.restoreState(saveState2);
                }
                return n2;
            }
            return ~n3;
        }
    }
}
