// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

public interface DateTimeParser
{
    int estimateParsedLength();
    
    int parseInto(final DateTimeParserBucket p0, final String p1, final int p2);
}
