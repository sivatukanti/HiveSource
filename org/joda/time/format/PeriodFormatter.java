// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.ReadWritablePeriod;
import java.io.IOException;
import java.io.Writer;
import org.joda.time.ReadablePeriod;
import org.joda.time.PeriodType;
import java.util.Locale;

public class PeriodFormatter
{
    private final PeriodPrinter iPrinter;
    private final PeriodParser iParser;
    private final Locale iLocale;
    private final PeriodType iParseType;
    
    public PeriodFormatter(final PeriodPrinter iPrinter, final PeriodParser iParser) {
        this.iPrinter = iPrinter;
        this.iParser = iParser;
        this.iLocale = null;
        this.iParseType = null;
    }
    
    private PeriodFormatter(final PeriodPrinter iPrinter, final PeriodParser iParser, final Locale iLocale, final PeriodType iParseType) {
        this.iPrinter = iPrinter;
        this.iParser = iParser;
        this.iLocale = iLocale;
        this.iParseType = iParseType;
    }
    
    public boolean isPrinter() {
        return this.iPrinter != null;
    }
    
    public PeriodPrinter getPrinter() {
        return this.iPrinter;
    }
    
    public boolean isParser() {
        return this.iParser != null;
    }
    
    public PeriodParser getParser() {
        return this.iParser;
    }
    
    public PeriodFormatter withLocale(final Locale locale) {
        if (locale == this.getLocale() || (locale != null && locale.equals(this.getLocale()))) {
            return this;
        }
        return new PeriodFormatter(this.iPrinter, this.iParser, locale, this.iParseType);
    }
    
    public Locale getLocale() {
        return this.iLocale;
    }
    
    public PeriodFormatter withParseType(final PeriodType periodType) {
        if (periodType == this.iParseType) {
            return this;
        }
        return new PeriodFormatter(this.iPrinter, this.iParser, this.iLocale, periodType);
    }
    
    public PeriodType getParseType() {
        return this.iParseType;
    }
    
    public void printTo(final StringBuffer sb, final ReadablePeriod readablePeriod) {
        this.checkPrinter();
        this.checkPeriod(readablePeriod);
        this.getPrinter().printTo(sb, readablePeriod, this.iLocale);
    }
    
    public void printTo(final Writer writer, final ReadablePeriod readablePeriod) throws IOException {
        this.checkPrinter();
        this.checkPeriod(readablePeriod);
        this.getPrinter().printTo(writer, readablePeriod, this.iLocale);
    }
    
    public String print(final ReadablePeriod readablePeriod) {
        this.checkPrinter();
        this.checkPeriod(readablePeriod);
        final PeriodPrinter printer = this.getPrinter();
        final StringBuffer sb = new StringBuffer(printer.calculatePrintedLength(readablePeriod, this.iLocale));
        printer.printTo(sb, readablePeriod, this.iLocale);
        return sb.toString();
    }
    
    private void checkPrinter() {
        if (this.iPrinter == null) {
            throw new UnsupportedOperationException("Printing not supported");
        }
    }
    
    private void checkPeriod(final ReadablePeriod readablePeriod) {
        if (readablePeriod == null) {
            throw new IllegalArgumentException("Period must not be null");
        }
    }
    
    public int parseInto(final ReadWritablePeriod readWritablePeriod, final String s, final int n) {
        this.checkParser();
        this.checkPeriod(readWritablePeriod);
        return this.getParser().parseInto(readWritablePeriod, s, n, this.iLocale);
    }
    
    public Period parsePeriod(final String s) {
        this.checkParser();
        return this.parseMutablePeriod(s).toPeriod();
    }
    
    public MutablePeriod parseMutablePeriod(final String s) {
        this.checkParser();
        final MutablePeriod mutablePeriod = new MutablePeriod(0L, this.iParseType);
        int into = this.getParser().parseInto(mutablePeriod, s, 0, this.iLocale);
        if (into >= 0) {
            if (into >= s.length()) {
                return mutablePeriod;
            }
        }
        else {
            into ^= -1;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(s, into));
    }
    
    private void checkParser() {
        if (this.iParser == null) {
            throw new UnsupportedOperationException("Parsing not supported");
        }
    }
}
