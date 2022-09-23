// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

class DateTimeParserInternalParser implements InternalParser
{
    private final DateTimeParser underlying;
    
    static InternalParser of(final DateTimeParser dateTimeParser) {
        if (dateTimeParser instanceof InternalParserDateTimeParser) {
            return (InternalParser)dateTimeParser;
        }
        if (dateTimeParser == null) {
            return null;
        }
        return new DateTimeParserInternalParser(dateTimeParser);
    }
    
    private DateTimeParserInternalParser(final DateTimeParser underlying) {
        this.underlying = underlying;
    }
    
    DateTimeParser getUnderlying() {
        return this.underlying;
    }
    
    public int estimateParsedLength() {
        return this.underlying.estimateParsedLength();
    }
    
    public int parseInto(final DateTimeParserBucket dateTimeParserBucket, final CharSequence charSequence, final int n) {
        return this.underlying.parseInto(dateTimeParserBucket, charSequence.toString(), n);
    }
}
