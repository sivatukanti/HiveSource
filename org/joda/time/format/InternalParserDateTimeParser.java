// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

class InternalParserDateTimeParser implements DateTimeParser, InternalParser
{
    private final InternalParser underlying;
    
    static DateTimeParser of(final InternalParser internalParser) {
        if (internalParser instanceof DateTimeParserInternalParser) {
            return ((DateTimeParserInternalParser)internalParser).getUnderlying();
        }
        if (internalParser instanceof DateTimeParser) {
            return (DateTimeParser)internalParser;
        }
        if (internalParser == null) {
            return null;
        }
        return new InternalParserDateTimeParser(internalParser);
    }
    
    private InternalParserDateTimeParser(final InternalParser underlying) {
        this.underlying = underlying;
    }
    
    public int estimateParsedLength() {
        return this.underlying.estimateParsedLength();
    }
    
    public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
        return this.underlying.parseInto(dateTimeParserBucket, charSequence, n);
    }
    
    public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final String s, final int n) {
        return this.underlying.parseInto(dateTimeParserBucket, s, n);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof InternalParserDateTimeParser && this.underlying.equals(((InternalParserDateTimeParser)o).underlying));
    }
}
