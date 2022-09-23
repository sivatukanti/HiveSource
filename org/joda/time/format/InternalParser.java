// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

interface InternalParser
{
    int estimateParsedLength();
    
    int parseInto(final DateTimeParserBucket p0, final CharSequence p1, final int p2);
}
