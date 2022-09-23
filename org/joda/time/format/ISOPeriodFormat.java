// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

public class ISOPeriodFormat
{
    private static PeriodFormatter cStandard;
    private static PeriodFormatter cAlternate;
    private static PeriodFormatter cAlternateExtended;
    private static PeriodFormatter cAlternateWithWeeks;
    private static PeriodFormatter cAlternateExtendedWihWeeks;
    
    protected ISOPeriodFormat() {
    }
    
    public static PeriodFormatter standard() {
        if (ISOPeriodFormat.cStandard == null) {
            ISOPeriodFormat.cStandard = new PeriodFormatterBuilder().appendLiteral("P").appendYears().appendSuffix("Y").appendMonths().appendSuffix("M").appendWeeks().appendSuffix("W").appendDays().appendSuffix("D").appendSeparatorIfFieldsAfter("T").appendHours().appendSuffix("H").appendMinutes().appendSuffix("M").appendSecondsWithOptionalMillis().appendSuffix("S").toFormatter();
        }
        return ISOPeriodFormat.cStandard;
    }
    
    public static PeriodFormatter alternate() {
        if (ISOPeriodFormat.cAlternate == null) {
            ISOPeriodFormat.cAlternate = new PeriodFormatterBuilder().appendLiteral("P").printZeroAlways().minimumPrintedDigits(4).appendYears().minimumPrintedDigits(2).appendMonths().appendDays().appendSeparatorIfFieldsAfter("T").appendHours().appendMinutes().appendSecondsWithOptionalMillis().toFormatter();
        }
        return ISOPeriodFormat.cAlternate;
    }
    
    public static PeriodFormatter alternateExtended() {
        if (ISOPeriodFormat.cAlternateExtended == null) {
            ISOPeriodFormat.cAlternateExtended = new PeriodFormatterBuilder().appendLiteral("P").printZeroAlways().minimumPrintedDigits(4).appendYears().appendSeparator("-").minimumPrintedDigits(2).appendMonths().appendSeparator("-").appendDays().appendSeparatorIfFieldsAfter("T").appendHours().appendSeparator(":").appendMinutes().appendSeparator(":").appendSecondsWithOptionalMillis().toFormatter();
        }
        return ISOPeriodFormat.cAlternateExtended;
    }
    
    public static PeriodFormatter alternateWithWeeks() {
        if (ISOPeriodFormat.cAlternateWithWeeks == null) {
            ISOPeriodFormat.cAlternateWithWeeks = new PeriodFormatterBuilder().appendLiteral("P").printZeroAlways().minimumPrintedDigits(4).appendYears().minimumPrintedDigits(2).appendPrefix("W").appendWeeks().appendDays().appendSeparatorIfFieldsAfter("T").appendHours().appendMinutes().appendSecondsWithOptionalMillis().toFormatter();
        }
        return ISOPeriodFormat.cAlternateWithWeeks;
    }
    
    public static PeriodFormatter alternateExtendedWithWeeks() {
        if (ISOPeriodFormat.cAlternateExtendedWihWeeks == null) {
            ISOPeriodFormat.cAlternateExtendedWihWeeks = new PeriodFormatterBuilder().appendLiteral("P").printZeroAlways().minimumPrintedDigits(4).appendYears().appendSeparator("-").minimumPrintedDigits(2).appendPrefix("W").appendWeeks().appendSeparator("-").appendDays().appendSeparatorIfFieldsAfter("T").appendHours().appendSeparator(":").appendMinutes().appendSeparator(":").appendSecondsWithOptionalMillis().toFormatter();
        }
        return ISOPeriodFormat.cAlternateExtendedWihWeeks;
    }
}
