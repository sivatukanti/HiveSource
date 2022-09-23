// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import org.joda.time.ReadablePartial;
import java.io.IOException;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import java.util.Map;
import org.joda.time.DateTime;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.ConcurrentHashMap;

public class DateTimeFormat
{
    static final int FULL = 0;
    static final int LONG = 1;
    static final int MEDIUM = 2;
    static final int SHORT = 3;
    static final int NONE = 4;
    static final int DATE = 0;
    static final int TIME = 1;
    static final int DATETIME = 2;
    private static final int PATTERN_CACHE_SIZE = 500;
    private static final ConcurrentHashMap<String, DateTimeFormatter> cPatternCache;
    private static final AtomicReferenceArray<DateTimeFormatter> cStyleCache;
    
    public static DateTimeFormatter forPattern(final String s) {
        return createFormatterForPattern(s);
    }
    
    public static DateTimeFormatter forStyle(final String s) {
        return createFormatterForStyle(s);
    }
    
    public static String patternForStyle(final String s, Locale default1) {
        final DateTimeFormatter formatterForStyle = createFormatterForStyle(s);
        if (default1 == null) {
            default1 = Locale.getDefault();
        }
        return ((StyleFormatter)formatterForStyle.getPrinter0()).getPattern(default1);
    }
    
    public static DateTimeFormatter shortDate() {
        return createFormatterForStyleIndex(3, 4);
    }
    
    public static DateTimeFormatter shortTime() {
        return createFormatterForStyleIndex(4, 3);
    }
    
    public static DateTimeFormatter shortDateTime() {
        return createFormatterForStyleIndex(3, 3);
    }
    
    public static DateTimeFormatter mediumDate() {
        return createFormatterForStyleIndex(2, 4);
    }
    
    public static DateTimeFormatter mediumTime() {
        return createFormatterForStyleIndex(4, 2);
    }
    
    public static DateTimeFormatter mediumDateTime() {
        return createFormatterForStyleIndex(2, 2);
    }
    
    public static DateTimeFormatter longDate() {
        return createFormatterForStyleIndex(1, 4);
    }
    
    public static DateTimeFormatter longTime() {
        return createFormatterForStyleIndex(4, 1);
    }
    
    public static DateTimeFormatter longDateTime() {
        return createFormatterForStyleIndex(1, 1);
    }
    
    public static DateTimeFormatter fullDate() {
        return createFormatterForStyleIndex(0, 4);
    }
    
    public static DateTimeFormatter fullTime() {
        return createFormatterForStyleIndex(4, 0);
    }
    
    public static DateTimeFormatter fullDateTime() {
        return createFormatterForStyleIndex(0, 0);
    }
    
    static void appendPatternTo(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final String s) {
        parsePatternTo(dateTimeFormatterBuilder, s);
    }
    
    protected DateTimeFormat() {
    }
    
    private static void parsePatternTo(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final String s) {
        final int length = s.length();
        final int[] array = { 0 };
        for (int i = 0; i < length; ++i) {
            array[0] = i;
            final String token = parseToken(s, array);
            i = array[0];
            final int length2 = token.length();
            if (length2 == 0) {
                break;
            }
            final char char1 = token.charAt(0);
            switch (char1) {
                case 71: {
                    dateTimeFormatterBuilder.appendEraText();
                    break;
                }
                case 67: {
                    dateTimeFormatterBuilder.appendCenturyOfEra(length2, length2);
                    break;
                }
                case 89:
                case 120:
                case 121: {
                    if (length2 == 2) {
                        boolean b = true;
                        if (i + 1 < length) {
                            final int[] array2 = array;
                            final int n = 0;
                            ++array2[n];
                            if (isNumericToken(parseToken(s, array))) {
                                b = false;
                            }
                            final int[] array3 = array;
                            final int n2 = 0;
                            --array3[n2];
                        }
                        switch (char1) {
                            case 120: {
                                dateTimeFormatterBuilder.appendTwoDigitWeekyear(new DateTime().getWeekyear() - 30, b);
                                break;
                            }
                            default: {
                                dateTimeFormatterBuilder.appendTwoDigitYear(new DateTime().getYear() - 30, b);
                                break;
                            }
                        }
                        break;
                    }
                    int n3 = 9;
                    if (i + 1 < length) {
                        final int[] array4 = array;
                        final int n4 = 0;
                        ++array4[n4];
                        if (isNumericToken(parseToken(s, array))) {
                            n3 = length2;
                        }
                        final int[] array5 = array;
                        final int n5 = 0;
                        --array5[n5];
                    }
                    switch (char1) {
                        case 120: {
                            dateTimeFormatterBuilder.appendWeekyear(length2, n3);
                            break;
                        }
                        case 121: {
                            dateTimeFormatterBuilder.appendYear(length2, n3);
                            break;
                        }
                        case 89: {
                            dateTimeFormatterBuilder.appendYearOfEra(length2, n3);
                            break;
                        }
                    }
                    break;
                }
                case 77: {
                    if (length2 < 3) {
                        dateTimeFormatterBuilder.appendMonthOfYear(length2);
                        break;
                    }
                    if (length2 >= 4) {
                        dateTimeFormatterBuilder.appendMonthOfYearText();
                        break;
                    }
                    dateTimeFormatterBuilder.appendMonthOfYearShortText();
                    break;
                }
                case 100: {
                    dateTimeFormatterBuilder.appendDayOfMonth(length2);
                    break;
                }
                case 97: {
                    dateTimeFormatterBuilder.appendHalfdayOfDayText();
                    break;
                }
                case 104: {
                    dateTimeFormatterBuilder.appendClockhourOfHalfday(length2);
                    break;
                }
                case 72: {
                    dateTimeFormatterBuilder.appendHourOfDay(length2);
                    break;
                }
                case 107: {
                    dateTimeFormatterBuilder.appendClockhourOfDay(length2);
                    break;
                }
                case 75: {
                    dateTimeFormatterBuilder.appendHourOfHalfday(length2);
                    break;
                }
                case 109: {
                    dateTimeFormatterBuilder.appendMinuteOfHour(length2);
                    break;
                }
                case 115: {
                    dateTimeFormatterBuilder.appendSecondOfMinute(length2);
                    break;
                }
                case 83: {
                    dateTimeFormatterBuilder.appendFractionOfSecond(length2, length2);
                    break;
                }
                case 101: {
                    dateTimeFormatterBuilder.appendDayOfWeek(length2);
                    break;
                }
                case 69: {
                    if (length2 >= 4) {
                        dateTimeFormatterBuilder.appendDayOfWeekText();
                        break;
                    }
                    dateTimeFormatterBuilder.appendDayOfWeekShortText();
                    break;
                }
                case 68: {
                    dateTimeFormatterBuilder.appendDayOfYear(length2);
                    break;
                }
                case 119: {
                    dateTimeFormatterBuilder.appendWeekOfWeekyear(length2);
                    break;
                }
                case 122: {
                    if (length2 >= 4) {
                        dateTimeFormatterBuilder.appendTimeZoneName();
                        break;
                    }
                    dateTimeFormatterBuilder.appendTimeZoneShortName(null);
                    break;
                }
                case 90: {
                    if (length2 == 1) {
                        dateTimeFormatterBuilder.appendTimeZoneOffset(null, "Z", false, 2, 2);
                        break;
                    }
                    if (length2 == 2) {
                        dateTimeFormatterBuilder.appendTimeZoneOffset(null, "Z", true, 2, 2);
                        break;
                    }
                    dateTimeFormatterBuilder.appendTimeZoneId();
                    break;
                }
                case 39: {
                    final String substring = token.substring(1);
                    if (substring.length() == 1) {
                        dateTimeFormatterBuilder.appendLiteral(substring.charAt(0));
                        break;
                    }
                    dateTimeFormatterBuilder.appendLiteral(new String(substring));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Illegal pattern component: " + token);
                }
            }
        }
    }
    
    private static String parseToken(final String s, final int[] array) {
        final StringBuilder sb = new StringBuilder();
        int i = array[0];
        final int length = s.length();
        final char char1 = s.charAt(i);
        if ((char1 >= 'A' && char1 <= 'Z') || (char1 >= 'a' && char1 <= 'z')) {
            sb.append(char1);
            while (i + 1 < length && s.charAt(i + 1) == char1) {
                sb.append(char1);
                ++i;
            }
        }
        else {
            sb.append('\'');
            boolean b = false;
            while (i < length) {
                final char char2 = s.charAt(i);
                if (char2 == '\'') {
                    if (i + 1 < length && s.charAt(i + 1) == '\'') {
                        ++i;
                        sb.append(char2);
                    }
                    else {
                        b = !b;
                    }
                }
                else {
                    if (!b && ((char2 >= 'A' && char2 <= 'Z') || (char2 >= 'a' && char2 <= 'z'))) {
                        --i;
                        break;
                    }
                    sb.append(char2);
                }
                ++i;
            }
        }
        array[0] = i;
        return sb.toString();
    }
    
    private static boolean isNumericToken(final String s) {
        final int length = s.length();
        if (length > 0) {
            switch (s.charAt(0)) {
                case 'C':
                case 'D':
                case 'F':
                case 'H':
                case 'K':
                case 'S':
                case 'W':
                case 'Y':
                case 'c':
                case 'd':
                case 'e':
                case 'h':
                case 'k':
                case 'm':
                case 's':
                case 'w':
                case 'x':
                case 'y': {
                    return true;
                }
                case 'M': {
                    if (length <= 2) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    private static DateTimeFormatter createFormatterForPattern(final String s) {
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("Invalid pattern specification");
        }
        DateTimeFormatter formatter = DateTimeFormat.cPatternCache.get(s);
        if (formatter == null) {
            final DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
            parsePatternTo(dateTimeFormatterBuilder, s);
            formatter = dateTimeFormatterBuilder.toFormatter();
            if (DateTimeFormat.cPatternCache.size() < 500) {
                final DateTimeFormatter dateTimeFormatter = DateTimeFormat.cPatternCache.putIfAbsent(s, formatter);
                if (dateTimeFormatter != null) {
                    formatter = dateTimeFormatter;
                }
            }
        }
        return formatter;
    }
    
    private static DateTimeFormatter createFormatterForStyle(final String str) {
        if (str == null || str.length() != 2) {
            throw new IllegalArgumentException("Invalid style specification: " + str);
        }
        final int selectStyle = selectStyle(str.charAt(0));
        final int selectStyle2 = selectStyle(str.charAt(1));
        if (selectStyle == 4 && selectStyle2 == 4) {
            throw new IllegalArgumentException("Style '--' is invalid");
        }
        return createFormatterForStyleIndex(selectStyle, selectStyle2);
    }
    
    private static DateTimeFormatter createFormatterForStyleIndex(final int n, final int n2) {
        final int i = (n << 2) + n + n2;
        if (i >= DateTimeFormat.cStyleCache.length()) {
            return createDateTimeFormatter(n, n2);
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.cStyleCache.get(i);
        if (dateTimeFormatter == null) {
            dateTimeFormatter = createDateTimeFormatter(n, n2);
            if (!DateTimeFormat.cStyleCache.compareAndSet(i, null, dateTimeFormatter)) {
                dateTimeFormatter = DateTimeFormat.cStyleCache.get(i);
            }
        }
        return dateTimeFormatter;
    }
    
    private static DateTimeFormatter createDateTimeFormatter(final int n, final int n2) {
        int n3 = 2;
        if (n == 4) {
            n3 = 1;
        }
        else if (n2 == 4) {
            n3 = 0;
        }
        final StyleFormatter styleFormatter = new StyleFormatter(n, n2, n3);
        return new DateTimeFormatter(styleFormatter, styleFormatter);
    }
    
    private static int selectStyle(final char c) {
        switch (c) {
            case 'S': {
                return 3;
            }
            case 'M': {
                return 2;
            }
            case 'L': {
                return 1;
            }
            case 'F': {
                return 0;
            }
            case '-': {
                return 4;
            }
            default: {
                throw new IllegalArgumentException("Invalid style character: " + c);
            }
        }
    }
    
    static {
        cPatternCache = new ConcurrentHashMap<String, DateTimeFormatter>();
        cStyleCache = new AtomicReferenceArray<DateTimeFormatter>(25);
    }
    
    static class StyleFormatter implements InternalPrinter, InternalParser
    {
        private static final ConcurrentHashMap<StyleFormatterCacheKey, DateTimeFormatter> cCache;
        private final int iDateStyle;
        private final int iTimeStyle;
        private final int iType;
        
        StyleFormatter(final int iDateStyle, final int iTimeStyle, final int iType) {
            this.iDateStyle = iDateStyle;
            this.iTimeStyle = iTimeStyle;
            this.iType = iType;
        }
        
        public int estimatePrintedLength() {
            return 40;
        }
        
        public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
            this.getFormatter(locale).getPrinter0().printTo(appendable, n, chronology, n2, dateTimeZone, locale);
        }
        
        public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
            this.getFormatter(locale).getPrinter0().printTo(appendable, readablePartial, locale);
        }
        
        public int estimateParsedLength() {
            return 40;
        }
        
        public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
            return this.getFormatter(dateTimeParserBucket.getLocale()).getParser0().parseInto(dateTimeParserBucket, charSequence, n);
        }
        
        private DateTimeFormatter getFormatter(Locale locale) {
            locale = ((locale == null) ? Locale.getDefault() : locale);
            final StyleFormatterCacheKey styleFormatterCacheKey = new StyleFormatterCacheKey(this.iType, this.iDateStyle, this.iTimeStyle, locale);
            DateTimeFormatter forPattern = StyleFormatter.cCache.get(styleFormatterCacheKey);
            if (forPattern == null) {
                forPattern = DateTimeFormat.forPattern(this.getPattern(locale));
                final DateTimeFormatter dateTimeFormatter = StyleFormatter.cCache.putIfAbsent(styleFormatterCacheKey, forPattern);
                if (dateTimeFormatter != null) {
                    forPattern = dateTimeFormatter;
                }
            }
            return forPattern;
        }
        
        String getPattern(final Locale locale) {
            DateFormat dateFormat = null;
            switch (this.iType) {
                case 0: {
                    dateFormat = DateFormat.getDateInstance(this.iDateStyle, locale);
                    break;
                }
                case 1: {
                    dateFormat = DateFormat.getTimeInstance(this.iTimeStyle, locale);
                    break;
                }
                case 2: {
                    dateFormat = DateFormat.getDateTimeInstance(this.iDateStyle, this.iTimeStyle, locale);
                    break;
                }
            }
            if (!(dateFormat instanceof SimpleDateFormat)) {
                throw new IllegalArgumentException("No datetime pattern for locale: " + locale);
            }
            return ((SimpleDateFormat)dateFormat).toPattern();
        }
        
        static {
            cCache = new ConcurrentHashMap<StyleFormatterCacheKey, DateTimeFormatter>();
        }
    }
    
    static class StyleFormatterCacheKey
    {
        private final int combinedTypeAndStyle;
        private final Locale locale;
        
        public StyleFormatterCacheKey(final int n, final int n2, final int n3, final Locale locale) {
            this.locale = locale;
            this.combinedTypeAndStyle = n + (n2 << 4) + (n3 << 8);
        }
        
        @Override
        public int hashCode() {
            return 31 * (31 * 1 + this.combinedTypeAndStyle) + ((this.locale == null) ? 0 : this.locale.hashCode());
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (!(o instanceof StyleFormatterCacheKey)) {
                return false;
            }
            final StyleFormatterCacheKey styleFormatterCacheKey = (StyleFormatterCacheKey)o;
            if (this.combinedTypeAndStyle != styleFormatterCacheKey.combinedTypeAndStyle) {
                return false;
            }
            if (this.locale == null) {
                if (styleFormatterCacheKey.locale != null) {
                    return false;
                }
            }
            else if (!this.locale.equals(styleFormatterCacheKey.locale)) {
                return false;
            }
            return true;
        }
    }
}
