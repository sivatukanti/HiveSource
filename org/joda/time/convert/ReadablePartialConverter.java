// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.convert;

import org.joda.time.DateTimeUtils;
import org.joda.time.ReadablePartial;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;

class ReadablePartialConverter extends AbstractConverter implements PartialConverter
{
    static final ReadablePartialConverter INSTANCE;
    
    protected ReadablePartialConverter() {
    }
    
    @Override
    public Chronology getChronology(final Object o, final DateTimeZone dateTimeZone) {
        return this.getChronology(o, (Chronology)null).withZone(dateTimeZone);
    }
    
    @Override
    public Chronology getChronology(final Object o, Chronology chronology) {
        if (chronology == null) {
            chronology = ((ReadablePartial)o).getChronology();
            chronology = DateTimeUtils.getChronology(chronology);
        }
        return chronology;
    }
    
    @Override
    public int[] getPartialValues(final ReadablePartial readablePartial, final Object o, final Chronology chronology) {
        final ReadablePartial readablePartial2 = (ReadablePartial)o;
        final int size = readablePartial.size();
        final int[] array = new int[size];
        for (int i = 0; i < size; ++i) {
            array[i] = readablePartial2.get(readablePartial.getFieldType(i));
        }
        chronology.validate(readablePartial, array);
        return array;
    }
    
    public Class<?> getSupportedType() {
        return ReadablePartial.class;
    }
    
    static {
        INSTANCE = new ReadablePartialConverter();
    }
}
