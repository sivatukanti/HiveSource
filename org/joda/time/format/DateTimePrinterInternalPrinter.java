// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import org.joda.time.ReadablePartial;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

class DateTimePrinterInternalPrinter implements InternalPrinter
{
    private final DateTimePrinter underlying;
    
    static InternalPrinter of(final DateTimePrinter dateTimePrinter) {
        if (dateTimePrinter instanceof InternalPrinterDateTimePrinter) {
            return (InternalPrinter)dateTimePrinter;
        }
        if (dateTimePrinter == null) {
            return null;
        }
        return new DateTimePrinterInternalPrinter(dateTimePrinter);
    }
    
    private DateTimePrinterInternalPrinter(final DateTimePrinter underlying) {
        this.underlying = underlying;
    }
    
    DateTimePrinter getUnderlying() {
        return this.underlying;
    }
    
    public int estimatePrintedLength() {
        return this.underlying.estimatePrintedLength();
    }
    
    public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
        if (appendable instanceof StringBuffer) {
            this.underlying.printTo((StringBuffer)appendable, n, chronology, n2, dateTimeZone, locale);
        }
        if (appendable instanceof Writer) {
            this.underlying.printTo((Writer)appendable, n, chronology, n2, dateTimeZone, locale);
        }
        final StringBuffer sb = new StringBuffer(this.estimatePrintedLength());
        this.underlying.printTo(sb, n, chronology, n2, dateTimeZone, locale);
        appendable.append(sb);
    }
    
    public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
        if (appendable instanceof StringBuffer) {
            this.underlying.printTo((StringBuffer)appendable, readablePartial, locale);
        }
        if (appendable instanceof Writer) {
            this.underlying.printTo((Writer)appendable, readablePartial, locale);
        }
        final StringBuffer sb = new StringBuffer(this.estimatePrintedLength());
        this.underlying.printTo(sb, readablePartial, locale);
        appendable.append(sb);
    }
}
