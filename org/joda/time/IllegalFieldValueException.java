// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time;

public class IllegalFieldValueException extends IllegalArgumentException
{
    private static final long serialVersionUID = 6305711765985447737L;
    private final DateTimeFieldType iDateTimeFieldType;
    private final DurationFieldType iDurationFieldType;
    private final String iFieldName;
    private final Number iNumberValue;
    private final String iStringValue;
    private final Number iLowerBound;
    private final Number iUpperBound;
    private String iMessage;
    
    private static String createMessage(final String str, final Number obj, final Number n, final Number n2, final String str2) {
        final StringBuilder append = new StringBuilder().append("Value ").append(obj).append(" for ").append(str).append(' ');
        if (n == null) {
            if (n2 == null) {
                append.append("is not supported");
            }
            else {
                append.append("must not be larger than ").append(n2);
            }
        }
        else if (n2 == null) {
            append.append("must not be smaller than ").append(n);
        }
        else {
            append.append("must be in the range [").append(n).append(',').append(n2).append(']');
        }
        if (str2 != null) {
            append.append(": ").append(str2);
        }
        return append.toString();
    }
    
    private static String createMessage(final String str, final String str2) {
        final StringBuffer append = new StringBuffer().append("Value ");
        if (str2 == null) {
            append.append("null");
        }
        else {
            append.append('\"');
            append.append(str2);
            append.append('\"');
        }
        append.append(" for ").append(str).append(' ').append("is not supported");
        return append.toString();
    }
    
    public IllegalFieldValueException(final DateTimeFieldType iDateTimeFieldType, final Number iNumberValue, final Number iLowerBound, final Number iUpperBound) {
        super(createMessage(iDateTimeFieldType.getName(), iNumberValue, iLowerBound, iUpperBound, null));
        this.iDateTimeFieldType = iDateTimeFieldType;
        this.iDurationFieldType = null;
        this.iFieldName = iDateTimeFieldType.getName();
        this.iNumberValue = iNumberValue;
        this.iStringValue = null;
        this.iLowerBound = iLowerBound;
        this.iUpperBound = iUpperBound;
        this.iMessage = super.getMessage();
    }
    
    public IllegalFieldValueException(final DateTimeFieldType iDateTimeFieldType, final Number iNumberValue, final String s) {
        super(createMessage(iDateTimeFieldType.getName(), iNumberValue, null, null, s));
        this.iDateTimeFieldType = iDateTimeFieldType;
        this.iDurationFieldType = null;
        this.iFieldName = iDateTimeFieldType.getName();
        this.iNumberValue = iNumberValue;
        this.iStringValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }
    
    public IllegalFieldValueException(final DurationFieldType iDurationFieldType, final Number iNumberValue, final Number iLowerBound, final Number iUpperBound) {
        super(createMessage(iDurationFieldType.getName(), iNumberValue, iLowerBound, iUpperBound, null));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = iDurationFieldType;
        this.iFieldName = iDurationFieldType.getName();
        this.iNumberValue = iNumberValue;
        this.iStringValue = null;
        this.iLowerBound = iLowerBound;
        this.iUpperBound = iUpperBound;
        this.iMessage = super.getMessage();
    }
    
    public IllegalFieldValueException(final String iFieldName, final Number iNumberValue, final Number iLowerBound, final Number iUpperBound) {
        super(createMessage(iFieldName, iNumberValue, iLowerBound, iUpperBound, null));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = null;
        this.iFieldName = iFieldName;
        this.iNumberValue = iNumberValue;
        this.iStringValue = null;
        this.iLowerBound = iLowerBound;
        this.iUpperBound = iUpperBound;
        this.iMessage = super.getMessage();
    }
    
    public IllegalFieldValueException(final DateTimeFieldType iDateTimeFieldType, final String iStringValue) {
        super(createMessage(iDateTimeFieldType.getName(), iStringValue));
        this.iDateTimeFieldType = iDateTimeFieldType;
        this.iDurationFieldType = null;
        this.iFieldName = iDateTimeFieldType.getName();
        this.iStringValue = iStringValue;
        this.iNumberValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }
    
    public IllegalFieldValueException(final DurationFieldType iDurationFieldType, final String iStringValue) {
        super(createMessage(iDurationFieldType.getName(), iStringValue));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = iDurationFieldType;
        this.iFieldName = iDurationFieldType.getName();
        this.iStringValue = iStringValue;
        this.iNumberValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }
    
    public IllegalFieldValueException(final String iFieldName, final String iStringValue) {
        super(createMessage(iFieldName, iStringValue));
        this.iDateTimeFieldType = null;
        this.iDurationFieldType = null;
        this.iFieldName = iFieldName;
        this.iStringValue = iStringValue;
        this.iNumberValue = null;
        this.iLowerBound = null;
        this.iUpperBound = null;
        this.iMessage = super.getMessage();
    }
    
    public DateTimeFieldType getDateTimeFieldType() {
        return this.iDateTimeFieldType;
    }
    
    public DurationFieldType getDurationFieldType() {
        return this.iDurationFieldType;
    }
    
    public String getFieldName() {
        return this.iFieldName;
    }
    
    public Number getIllegalNumberValue() {
        return this.iNumberValue;
    }
    
    public String getIllegalStringValue() {
        return this.iStringValue;
    }
    
    public String getIllegalValueAsString() {
        String s = this.iStringValue;
        if (s == null) {
            s = String.valueOf(this.iNumberValue);
        }
        return s;
    }
    
    public Number getLowerBound() {
        return this.iLowerBound;
    }
    
    public Number getUpperBound() {
        return this.iUpperBound;
    }
    
    @Override
    public String getMessage() {
        return this.iMessage;
    }
    
    public void prependMessage(final String s) {
        if (this.iMessage == null) {
            this.iMessage = s;
        }
        else if (s != null) {
            this.iMessage = s + ": " + this.iMessage;
        }
    }
}
