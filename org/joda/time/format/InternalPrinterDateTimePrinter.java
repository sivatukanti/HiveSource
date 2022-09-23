// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import org.joda.time.ReadablePartial;
import java.io.Writer;
import java.io.IOException;
import java.util.Locale;
import org.joda.time.DateTimeZone;
import org.joda.time.Chronology;

class InternalPrinterDateTimePrinter implements DateTimePrinter, InternalPrinter
{
    private final InternalPrinter underlying;
    
    static DateTimePrinter of(final InternalPrinter internalPrinter) {
        if (internalPrinter instanceof DateTimePrinterInternalPrinter) {
            return ((DateTimePrinterInternalPrinter)internalPrinter).getUnderlying();
        }
        if (internalPrinter instanceof DateTimePrinter) {
            return (DateTimePrinter)internalPrinter;
        }
        if (internalPrinter == null) {
            return null;
        }
        return new InternalPrinterDateTimePrinter(internalPrinter);
    }
    
    private InternalPrinterDateTimePrinter(final InternalPrinter underlying) {
        this.underlying = underlying;
    }
    
    public int estimatePrintedLength() {
        return this.underlying.estimatePrintedLength();
    }
    
    public void printTo(final StringBuffer sb, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) {
        try {
            this.underlying.printTo(sb, n, chronology, n2, dateTimeZone, locale);
        }
        catch (IOException ex) {}
    }
    
    public void printTo(final Writer writer, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
        this.underlying.printTo(writer, n, chronology, n2, dateTimeZone, locale);
    }
    
    public void printTo(final Appendable appendable, final long n, final Chronology chronology, final int n2, final DateTimeZone dateTimeZone, final Locale locale) throws IOException {
        this.underlying.printTo(appendable, n, chronology, n2, dateTimeZone, locale);
    }
    
    public void printTo(final StringBuffer sb, final ReadablePartial readablePartial, final Locale locale) {
        try {
            this.underlying.printTo(sb, readablePartial, locale);
        }
        catch (IOException ex) {}
    }
    
    public void printTo(final Writer writer, final ReadablePartial readablePartial, final Locale locale) throws IOException {
        this.underlying.printTo(writer, readablePartial, locale);
    }
    
    public void printTo(final Appendable appendable, final ReadablePartial readablePartial, final Locale locale) throws IOException {
        this.underlying.printTo(appendable, readablePartial, locale);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof InternalPrinterDateTimePrinter && this.underlying.equals(((InternalPrinterDateTimePrinter)o).underlying));
    }
}
