// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.util.Arrays;
import org.joda.time.DurationField;
import org.joda.time.IllegalInstantException;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.DurationFieldType;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import java.util.Locale;
import org.joda.time.Chronology;

public class DateTimeParserBucket
{
    private final Chronology iChrono;
    private final long iMillis;
    private final Locale iLocale;
    private final int iDefaultYear;
    private final DateTimeZone iDefaultZone;
    private final Integer iDefaultPivotYear;
    private DateTimeZone iZone;
    private Integer iOffset;
    private Integer iPivotYear;
    private SavedField[] iSavedFields;
    private int iSavedFieldsCount;
    private boolean iSavedFieldsShared;
    private Object iSavedState;
    
    @Deprecated
    public DateTimeParserBucket(final long n, final Chronology chronology, final Locale locale) {
        this(n, chronology, locale, null, 2000);
    }
    
    @Deprecated
    public DateTimeParserBucket(final long n, final Chronology chronology, final Locale locale, final Integer n2) {
        this(n, chronology, locale, n2, 2000);
    }
    
    public DateTimeParserBucket(final long iMillis, Chronology chronology, final Locale locale, final Integer iDefaultPivotYear, final int iDefaultYear) {
        chronology = DateTimeUtils.getChronology(chronology);
        this.iMillis = iMillis;
        this.iDefaultZone = chronology.getZone();
        this.iChrono = chronology.withUTC();
        this.iLocale = ((locale == null) ? Locale.getDefault() : locale);
        this.iDefaultYear = iDefaultYear;
        this.iDefaultPivotYear = iDefaultPivotYear;
        this.iZone = this.iDefaultZone;
        this.iPivotYear = this.iDefaultPivotYear;
        this.iSavedFields = new SavedField[8];
    }
    
    public void reset() {
        this.iZone = this.iDefaultZone;
        this.iOffset = null;
        this.iPivotYear = this.iDefaultPivotYear;
        this.iSavedFieldsCount = 0;
        this.iSavedFieldsShared = false;
        this.iSavedState = null;
    }
    
    public long parseMillis(final DateTimeParser dateTimeParser, final CharSequence charSequence) {
        this.reset();
        return this.doParseMillis(DateTimeParserInternalParser.of(dateTimeParser), charSequence);
    }
    
    long doParseMillis(final InternalParser internalParser, final CharSequence charSequence) {
        int into = internalParser.parseInto(this, charSequence, 0);
        if (into >= 0) {
            if (into >= charSequence.length()) {
                return this.computeMillis(true, charSequence);
            }
        }
        else {
            into ^= -1;
        }
        throw new IllegalArgumentException(FormatUtils.createErrorMessage(charSequence.toString(), into));
    }
    
    public Chronology getChronology() {
        return this.iChrono;
    }
    
    public Locale getLocale() {
        return this.iLocale;
    }
    
    public DateTimeZone getZone() {
        return this.iZone;
    }
    
    public void setZone(final DateTimeZone iZone) {
        this.iSavedState = null;
        this.iZone = iZone;
    }
    
    @Deprecated
    public int getOffset() {
        return (this.iOffset != null) ? this.iOffset : 0;
    }
    
    public Integer getOffsetInteger() {
        return this.iOffset;
    }
    
    @Deprecated
    public void setOffset(final int i) {
        this.iSavedState = null;
        this.iOffset = i;
    }
    
    public void setOffset(final Integer iOffset) {
        this.iSavedState = null;
        this.iOffset = iOffset;
    }
    
    public Integer getPivotYear() {
        return this.iPivotYear;
    }
    
    @Deprecated
    public void setPivotYear(final Integer iPivotYear) {
        this.iPivotYear = iPivotYear;
    }
    
    public void saveField(final DateTimeField dateTimeField, final int n) {
        this.obtainSaveField().init(dateTimeField, n);
    }
    
    public void saveField(final DateTimeFieldType dateTimeFieldType, final int n) {
        this.obtainSaveField().init(dateTimeFieldType.getField(this.iChrono), n);
    }
    
    public void saveField(final DateTimeFieldType dateTimeFieldType, final String s, final Locale locale) {
        this.obtainSaveField().init(dateTimeFieldType.getField(this.iChrono), s, locale);
    }
    
    private SavedField obtainSaveField() {
        SavedField[] iSavedFields = this.iSavedFields;
        final int iSavedFieldsCount = this.iSavedFieldsCount;
        if (iSavedFieldsCount == iSavedFields.length || this.iSavedFieldsShared) {
            final SavedField[] iSavedFields2 = new SavedField[(iSavedFieldsCount == iSavedFields.length) ? (iSavedFieldsCount * 2) : iSavedFields.length];
            System.arraycopy(iSavedFields, 0, iSavedFields2, 0, iSavedFieldsCount);
            iSavedFields = (this.iSavedFields = iSavedFields2);
            this.iSavedFieldsShared = false;
        }
        this.iSavedState = null;
        SavedField savedField = iSavedFields[iSavedFieldsCount];
        if (savedField == null) {
            final SavedField[] array = iSavedFields;
            final int n = iSavedFieldsCount;
            final SavedField savedField2 = new SavedField();
            array[n] = savedField2;
            savedField = savedField2;
        }
        this.iSavedFieldsCount = iSavedFieldsCount + 1;
        return savedField;
    }
    
    public Object saveState() {
        if (this.iSavedState == null) {
            this.iSavedState = new SavedState();
        }
        return this.iSavedState;
    }
    
    public boolean restoreState(final Object iSavedState) {
        if (iSavedState instanceof SavedState && ((SavedState)iSavedState).restoreState(this)) {
            this.iSavedState = iSavedState;
            return true;
        }
        return false;
    }
    
    public long computeMillis() {
        return this.computeMillis(false, (CharSequence)null);
    }
    
    public long computeMillis(final boolean b) {
        return this.computeMillis(b, (CharSequence)null);
    }
    
    public long computeMillis(final boolean b, final String s) {
        return this.computeMillis(b, (CharSequence)s);
    }
    
    public long computeMillis(final boolean b, final CharSequence charSequence) {
        SavedField[] iSavedFields = this.iSavedFields;
        final int iSavedFieldsCount = this.iSavedFieldsCount;
        if (this.iSavedFieldsShared) {
            iSavedFields = (this.iSavedFields = this.iSavedFields.clone());
            this.iSavedFieldsShared = false;
        }
        sort(iSavedFields, iSavedFieldsCount);
        if (iSavedFieldsCount > 0) {
            final DurationField field = DurationFieldType.months().getField(this.iChrono);
            final DurationField field2 = DurationFieldType.days().getField(this.iChrono);
            final DurationField durationField = iSavedFields[0].iField.getDurationField();
            if (compareReverse(durationField, field) >= 0 && compareReverse(durationField, field2) <= 0) {
                this.saveField(DateTimeFieldType.year(), this.iDefaultYear);
                return this.computeMillis(b, charSequence);
            }
        }
        long n = this.iMillis;
        try {
            for (int i = 0; i < iSavedFieldsCount; ++i) {
                n = iSavedFields[i].set(n, b);
            }
            if (b) {
                for (int j = 0; j < iSavedFieldsCount; ++j) {
                    n = iSavedFields[j].set(n, j == iSavedFieldsCount - 1);
                }
            }
        }
        catch (IllegalFieldValueException ex) {
            if (charSequence != null) {
                ex.prependMessage("Cannot parse \"" + (Object)charSequence + '\"');
            }
            throw ex;
        }
        if (this.iOffset != null) {
            n -= this.iOffset;
        }
        else if (this.iZone != null) {
            final int offsetFromLocal = this.iZone.getOffsetFromLocal(n);
            n -= offsetFromLocal;
            if (offsetFromLocal != this.iZone.getOffset(n)) {
                String str = "Illegal instant due to time zone offset transition (" + this.iZone + ')';
                if (charSequence != null) {
                    str = "Cannot parse \"" + (Object)charSequence + "\": " + str;
                }
                throw new IllegalInstantException(str);
            }
        }
        return n;
    }
    
    private static void sort(final SavedField[] a, final int toIndex) {
        if (toIndex > 10) {
            Arrays.sort(a, 0, toIndex);
        }
        else {
            for (int i = 0; i < toIndex; ++i) {
                for (int n = i; n > 0 && a[n - 1].compareTo(a[n]) > 0; --n) {
                    final SavedField savedField = a[n];
                    a[n] = a[n - 1];
                    a[n - 1] = savedField;
                }
            }
        }
    }
    
    static int compareReverse(final DurationField durationField, final DurationField durationField2) {
        if (durationField == null || !durationField.isSupported()) {
            if (durationField2 == null || !durationField2.isSupported()) {
                return 0;
            }
            return -1;
        }
        else {
            if (durationField2 == null || !durationField2.isSupported()) {
                return 1;
            }
            return -durationField.compareTo(durationField2);
        }
    }
    
    class SavedState
    {
        final DateTimeZone iZone;
        final Integer iOffset;
        final SavedField[] iSavedFields;
        final int iSavedFieldsCount;
        
        SavedState() {
            this.iZone = DateTimeParserBucket.this.iZone;
            this.iOffset = DateTimeParserBucket.this.iOffset;
            this.iSavedFields = DateTimeParserBucket.this.iSavedFields;
            this.iSavedFieldsCount = DateTimeParserBucket.this.iSavedFieldsCount;
        }
        
        boolean restoreState(final DateTimeParserBucket dateTimeParserBucket) {
            if (dateTimeParserBucket != DateTimeParserBucket.this) {
                return false;
            }
            dateTimeParserBucket.iZone = this.iZone;
            dateTimeParserBucket.iOffset = this.iOffset;
            dateTimeParserBucket.iSavedFields = this.iSavedFields;
            if (this.iSavedFieldsCount < dateTimeParserBucket.iSavedFieldsCount) {
                dateTimeParserBucket.iSavedFieldsShared = true;
            }
            dateTimeParserBucket.iSavedFieldsCount = this.iSavedFieldsCount;
            return true;
        }
    }
    
    static class SavedField implements Comparable<SavedField>
    {
        DateTimeField iField;
        int iValue;
        String iText;
        Locale iLocale;
        
        void init(final DateTimeField iField, final int iValue) {
            this.iField = iField;
            this.iValue = iValue;
            this.iText = null;
            this.iLocale = null;
        }
        
        void init(final DateTimeField iField, final String iText, final Locale iLocale) {
            this.iField = iField;
            this.iValue = 0;
            this.iText = iText;
            this.iLocale = iLocale;
        }
        
        long set(long n, final boolean b) {
            if (this.iText == null) {
                n = this.iField.set(n, this.iValue);
            }
            else {
                n = this.iField.set(n, this.iText, this.iLocale);
            }
            if (b) {
                n = this.iField.roundFloor(n);
            }
            return n;
        }
        
        public int compareTo(final SavedField savedField) {
            final DateTimeField iField = savedField.iField;
            final int compareReverse = DateTimeParserBucket.compareReverse(this.iField.getRangeDurationField(), iField.getRangeDurationField());
            if (compareReverse != 0) {
                return compareReverse;
            }
            return DateTimeParserBucket.compareReverse(this.iField.getDurationField(), iField.getDurationField());
        }
    }
}
