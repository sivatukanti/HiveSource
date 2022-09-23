// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import org.joda.time.MutableDateTime;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.LocalDate;
import org.joda.time.ReadWritableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.DateTimeUtils;
import java.io.Writer;
import java.io.IOException;
import org.joda.time.ReadableInstant;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;
import java.util.Locale;

public class DateTimeFormatter
{
    private final InternalPrinter iPrinter;
    private final InternalParser iParser;
    private final Locale iLocale;
    private final boolean iOffsetParsed;
    private final Chronology iChrono;
    private final DateTimeZone iZone;
    private final Integer iPivotYear;
    private final int iDefaultYear;
    
    public DateTimeFormatter(final DateTimePrinter dateTimePrinter, final DateTimeParser dateTimeParser) {
        this(DateTimePrinterInternalPrinter.of(dateTimePrinter), DateTimeParserInternalParser.of(dateTimeParser));
    }
    
    DateTimeFormatter(final InternalPrinter iPrinter, final InternalParser iParser) {
        this.iPrinter = iPrinter;
        this.iParser = iParser;
        this.iLocale = null;
        this.iOffsetParsed = false;
        this.iChrono = null;
        this.iZone = null;
        this.iPivotYear = null;
        this.iDefaultYear = 2000;
    }
    
    private DateTimeFormatter(final InternalPrinter iPrinter, final InternalParser iParser, final Locale iLocale, final boolean iOffsetParsed, final Chronology iChrono, final DateTimeZone iZone, final Integer iPivotYear, final int iDefaultYear) {
        this.iPrinter = iPrinter;
        this.iParser = iParser;
        this.iLocale = iLocale;
        this.iOffsetParsed = iOffsetParsed;
        this.iChrono = iChrono;
        this.iZone = iZone;
        this.iPivotYear = iPivotYear;
        this.iDefaultYear = iDefaultYear;
    }
    
    public boolean isPrinter() {
        return this.iPrinter != null;
    }
    
    public DateTimePrinter getPrinter() {
        return InternalPrinterDateTimePrinter.of(this.iPrinter);
    }
    
    InternalPrinter getPrinter0() {
        return this.iPrinter;
    }
    
    public boolean isParser() {
        return this.iParser != null;
    }
    
    public DateTimeParser getParser() {
        return InternalParserDateTimeParser.of(this.iParser);
    }
    
    InternalParser getParser0() {
        return this.iParser;
    }
    
    public DateTimeFormatter withLocale(final Locale locale) {
        if (locale == this.getLocale() || (locale != null && locale.equals(this.getLocale()))) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, locale, this.iOffsetParsed, this.iChrono, this.iZone, this.iPivotYear, this.iDefaultYear);
    }
    
    public Locale getLocale() {
        return this.iLocale;
    }
    
    public DateTimeFormatter withOffsetParsed() {
        if (this.iOffsetParsed) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, true, this.iChrono, null, this.iPivotYear, this.iDefaultYear);
    }
    
    public boolean isOffsetParsed() {
        return this.iOffsetParsed;
    }
    
    public DateTimeFormatter withChronology(final Chronology chronology) {
        if (this.iChrono == chronology) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, chronology, this.iZone, this.iPivotYear, this.iDefaultYear);
    }
    
    public Chronology getChronology() {
        return this.iChrono;
    }
    
    @Deprecated
    public Chronology getChronolgy() {
        return this.iChrono;
    }
    
    public DateTimeFormatter withZoneUTC() {
        return this.withZone(DateTimeZone.UTC);
    }
    
    public DateTimeFormatter withZone(final DateTimeZone dateTimeZone) {
        if (this.iZone == dateTimeZone) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, false, this.iChrono, dateTimeZone, this.iPivotYear, this.iDefaultYear);
    }
    
    public DateTimeZone getZone() {
        return this.iZone;
    }
    
    public DateTimeFormatter withPivotYear(final Integer obj) {
        if (this.iPivotYear == obj || (this.iPivotYear != null && this.iPivotYear.equals(obj))) {
            return this;
        }
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, this.iChrono, this.iZone, obj, this.iDefaultYear);
    }
    
    public DateTimeFormatter withPivotYear(final int i) {
        return this.withPivotYear(Integer.valueOf(i));
    }
    
    public Integer getPivotYear() {
        return this.iPivotYear;
    }
    
    public DateTimeFormatter withDefaultYear(final int n) {
        return new DateTimeFormatter(this.iPrinter, this.iParser, this.iLocale, this.iOffsetParsed, this.iChrono, this.iZone, this.iPivotYear, n);
    }
    
    public int getDefaultYear() {
        return this.iDefaultYear;
    }
    
    public void printTo(final StringBuffer sb, final ReadableInstant readableInstant) {
        try {
            this.printTo((Appendable)sb, readableInstant);
        }
        catch (IOException ex) {}
    }
    
    public void printTo(final Writer writer, final ReadableInstant readableInstant) throws IOException {
        this.printTo((Appendable)writer, readableInstant);
    }
    
    public void printTo(final Appendable appendable, final ReadableInstant readableInstant) throws IOException {
        this.printTo(appendable, DateTimeUtils.getInstantMillis(readableInstant), DateTimeUtils.getInstantChronology(readableInstant));
    }
    
    public void printTo(final StringBuffer sb, final long n) {
        try {
            this.printTo((Appendable)sb, n);
        }
        catch (IOException ex) {}
    }
    
    public void printTo(final Writer writer, final long n) throws IOException {
        this.printTo((Appendable)writer, n);
    }
    
    public void printTo(final Appendable appendable, final long n) throws IOException {
        this.printTo(appendable, n, null);
    }
    
    public void printTo(final StringBuffer sb, final ReadablePartial readablePartial) {
        try {
            this.printTo((Appendable)sb, readablePartial);
        }
        catch (IOException ex) {}
    }
    
    public void printTo(final Writer writer, final ReadablePartial readablePartial) throws IOException {
        this.printTo((Appendable)writer, readablePartial);
    }
    
    public void printTo(final Appendable appendable, final ReadablePartial readablePartial) throws IOException {
        final InternalPrinter requirePrinter = this.requirePrinter();
        if (readablePartial == null) {
            throw new IllegalArgumentException("The partial must not be null");
        }
        requirePrinter.printTo(appendable, readablePartial, this.iLocale);
    }
    
    public String print(final ReadableInstant readableInstant) {
        final StringBuilder sb = new StringBuilder(this.requirePrinter().estimatePrintedLength());
        try {
            this.printTo(sb, readableInstant);
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    public String print(final long n) {
        final StringBuilder sb = new StringBuilder(this.requirePrinter().estimatePrintedLength());
        try {
            this.printTo(sb, n);
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    public String print(final ReadablePartial readablePartial) {
        final StringBuilder sb = new StringBuilder(this.requirePrinter().estimatePrintedLength());
        try {
            this.printTo(sb, readablePartial);
        }
        catch (IOException ex) {}
        return sb.toString();
    }
    
    private void printTo(final Appendable appendable, final long n, Chronology selectChronology) throws IOException {
        final InternalPrinter requirePrinter = this.requirePrinter();
        selectChronology = this.selectChronology(selectChronology);
        DateTimeZone dateTimeZone = selectChronology.getZone();
        int offset = dateTimeZone.getOffset(n);
        long n2 = n + offset;
        if ((n ^ n2) < 0L && (n ^ (long)offset) >= 0L) {
            dateTimeZone = DateTimeZone.UTC;
            offset = 0;
            n2 = n;
        }
        requirePrinter.printTo(appendable, n2, selectChronology.withUTC(), offset, dateTimeZone, this.iLocale);
    }
    
    private InternalPrinter requirePrinter() {
        final InternalPrinter iPrinter = this.iPrinter;
        if (iPrinter == null) {
            throw new UnsupportedOperationException("Printing not supported");
        }
        return iPrinter;
    }
    
    public int parseInto(final ReadWritableInstant readWritableInstant, final String s, final int n) {
        final InternalParser requireParser = this.requireParser();
        if (readWritableInstant == null) {
            throw new IllegalArgumentException("Instant must not be null");
        }
        final long millis = readWritableInstant.getMillis();
        final Chronology chronology = readWritableInstant.getChronology();
        final int value = DateTimeUtils.getChronology(chronology).year().get(millis);
        final long n2 = millis + chronology.getZone().getOffset(millis);
        Chronology chronology2 = this.selectChronology(chronology);
        final DateTimeParserBucket dateTimeParserBucket = new DateTimeParserBucket(n2, chronology2, this.iLocale, this.iPivotYear, value);
        final int into = requireParser.parseInto(dateTimeParserBucket, s, n);
        readWritableInstant.setMillis(dateTimeParserBucket.computeMillis(false, s));
        if (this.iOffsetParsed && dateTimeParserBucket.getOffsetInteger() != null) {
            chronology2 = chronology2.withZone(DateTimeZone.forOffsetMillis(dateTimeParserBucket.getOffsetInteger()));
        }
        else if (dateTimeParserBucket.getZone() != null) {
            chronology2 = chronology2.withZone(dateTimeParserBucket.getZone());
        }
        readWritableInstant.setChronology(chronology2);
        if (this.iZone != null) {
            readWritableInstant.setZone(this.iZone);
        }
        return into;
    }
    
    public long parseMillis(final String s) {
        return new DateTimeParserBucket(0L, this.selectChronology(this.iChrono), this.iLocale, this.iPivotYear, this.iDefaultYear).doParseMillis(this.requireParser(), s);
    }
    
    public LocalDate parseLocalDate(final String s) {
        return this.parseLocalDateTime(s).toLocalDate();
    }
    
    public LocalTime parseLocalTime(final String s) {
        return this.parseLocalDateTime(s).toLocalTime();
    }
    
    public LocalDateTime parseLocalDateTime(final String s) {
        final InternalParser requireParser = this.requireParser();
        Chronology chronology = this.selectChronology(null).withUTC();
        final DateTimeParserBucket dateTimeParserBucket = new DateTimeParserBucket(0L, chronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
        int into = requireParser.parseInto(dateTimeParserBucket, s, 0);
        if (into >= 0) {
            if (into >= s.length()) {
                final long computeMillis = dateTimeParserBucket.computeMillis(true, s);
                if (dateTimeParserBucket.getOffsetInteger() != null) {
                    chronology = chronology.withZone(DateTimeZone.forOffsetMillis(dateTimeParserBucket.getOffsetInteger()));
                }
                else if (dateTimeParserBucket.getZone() != null) {
                    chronology = chronology.withZone(dateTimeParserBucket.getZone());
                }
                return new LocalDateTime(computeMillis, chronology);
            }
        }
        else {
            into ^= -1;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(s, into));
    }
    
    public DateTime parseDateTime(final String s) {
        final InternalParser requireParser = this.requireParser();
        Chronology chronology = this.selectChronology(null);
        final DateTimeParserBucket dateTimeParserBucket = new DateTimeParserBucket(0L, chronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
        int into = requireParser.parseInto(dateTimeParserBucket, s, 0);
        if (into >= 0) {
            if (into >= s.length()) {
                final long computeMillis = dateTimeParserBucket.computeMillis(true, s);
                if (this.iOffsetParsed && dateTimeParserBucket.getOffsetInteger() != null) {
                    chronology = chronology.withZone(DateTimeZone.forOffsetMillis(dateTimeParserBucket.getOffsetInteger()));
                }
                else if (dateTimeParserBucket.getZone() != null) {
                    chronology = chronology.withZone(dateTimeParserBucket.getZone());
                }
                DateTime withZone = new DateTime(computeMillis, chronology);
                if (this.iZone != null) {
                    withZone = withZone.withZone(this.iZone);
                }
                return withZone;
            }
        }
        else {
            into ^= -1;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(s, into));
    }
    
    public MutableDateTime parseMutableDateTime(final String s) {
        final InternalParser requireParser = this.requireParser();
        Chronology chronology = this.selectChronology(null);
        final DateTimeParserBucket dateTimeParserBucket = new DateTimeParserBucket(0L, chronology, this.iLocale, this.iPivotYear, this.iDefaultYear);
        int into = requireParser.parseInto(dateTimeParserBucket, s, 0);
        if (into >= 0) {
            if (into >= s.length()) {
                final long computeMillis = dateTimeParserBucket.computeMillis(true, s);
                if (this.iOffsetParsed && dateTimeParserBucket.getOffsetInteger() != null) {
                    chronology = chronology.withZone(DateTimeZone.forOffsetMillis(dateTimeParserBucket.getOffsetInteger()));
                }
                else if (dateTimeParserBucket.getZone() != null) {
                    chronology = chronology.withZone(dateTimeParserBucket.getZone());
                }
                final MutableDateTime mutableDateTime = new MutableDateTime(computeMillis, chronology);
                if (this.iZone != null) {
                    mutableDateTime.setZone(this.iZone);
                }
                return mutableDateTime;
            }
        }
        else {
            into ^= -1;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(s, into));
    }
    
    private InternalParser requireParser() {
        final InternalParser iParser = this.iParser;
        if (iParser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
        return iParser;
    }
    
    private Chronology selectChronology(Chronology chronology) {
        chronology = DateTimeUtils.getChronology(chronology);
        if (this.iChrono != null) {
            chronology = this.iChrono;
        }
        if (this.iZone != null) {
            chronology = chronology.withZone(this.iZone);
        }
        return chronology;
    }
}
