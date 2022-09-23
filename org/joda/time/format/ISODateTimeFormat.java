// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.format;

import java.util.HashSet;
import org.joda.time.DateTimeFieldType;
import java.util.Collection;

public class ISODateTimeFormat
{
    protected ISODateTimeFormat() {
    }
    
    public static DateTimeFormatter forFields(final Collection<DateTimeFieldType> collection, final boolean b, final boolean b2) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException("The fields must not be null or empty");
        }
        final HashSet<Object> set = (HashSet<Object>)new HashSet<DateTimeFieldType>(collection);
        final int size = set.size();
        boolean b3 = false;
        final DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
        if (set.contains(DateTimeFieldType.monthOfYear())) {
            b3 = dateByMonth(dateTimeFormatterBuilder, (Collection<DateTimeFieldType>)set, b, b2);
        }
        else if (set.contains(DateTimeFieldType.dayOfYear())) {
            b3 = dateByOrdinal(dateTimeFormatterBuilder, (Collection<DateTimeFieldType>)set, b, b2);
        }
        else if (set.contains(DateTimeFieldType.weekOfWeekyear())) {
            b3 = dateByWeek(dateTimeFormatterBuilder, (Collection<DateTimeFieldType>)set, b, b2);
        }
        else if (set.contains(DateTimeFieldType.dayOfMonth())) {
            b3 = dateByMonth(dateTimeFormatterBuilder, (Collection<DateTimeFieldType>)set, b, b2);
        }
        else if (set.contains(DateTimeFieldType.dayOfWeek())) {
            b3 = dateByWeek(dateTimeFormatterBuilder, (Collection<DateTimeFieldType>)set, b, b2);
        }
        else if (set.remove(DateTimeFieldType.year())) {
            dateTimeFormatterBuilder.append(Constants.ye);
            b3 = true;
        }
        else if (set.remove(DateTimeFieldType.weekyear())) {
            dateTimeFormatterBuilder.append(Constants.we);
            b3 = true;
        }
        time(dateTimeFormatterBuilder, (Collection<DateTimeFieldType>)set, b, b2, b3, set.size() < size);
        if (!dateTimeFormatterBuilder.canBuildFormatter()) {
            throw new IllegalArgumentException("No valid format for fields: " + collection);
        }
        try {
            collection.retainAll(set);
        }
        catch (UnsupportedOperationException ex) {}
        return dateTimeFormatterBuilder.toFormatter();
    }
    
    private static boolean dateByMonth(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final Collection<DateTimeFieldType> collection, final boolean b, final boolean b2) {
        boolean b3 = false;
        if (collection.remove(DateTimeFieldType.year())) {
            dateTimeFormatterBuilder.append(Constants.ye);
            if (collection.remove(DateTimeFieldType.monthOfYear())) {
                if (collection.remove(DateTimeFieldType.dayOfMonth())) {
                    appendSeparator(dateTimeFormatterBuilder, b);
                    dateTimeFormatterBuilder.appendMonthOfYear(2);
                    appendSeparator(dateTimeFormatterBuilder, b);
                    dateTimeFormatterBuilder.appendDayOfMonth(2);
                }
                else {
                    dateTimeFormatterBuilder.appendLiteral('-');
                    dateTimeFormatterBuilder.appendMonthOfYear(2);
                    b3 = true;
                }
            }
            else if (collection.remove(DateTimeFieldType.dayOfMonth())) {
                checkNotStrictISO(collection, b2);
                dateTimeFormatterBuilder.appendLiteral('-');
                dateTimeFormatterBuilder.appendLiteral('-');
                dateTimeFormatterBuilder.appendDayOfMonth(2);
            }
            else {
                b3 = true;
            }
        }
        else if (collection.remove(DateTimeFieldType.monthOfYear())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendMonthOfYear(2);
            if (collection.remove(DateTimeFieldType.dayOfMonth())) {
                appendSeparator(dateTimeFormatterBuilder, b);
                dateTimeFormatterBuilder.appendDayOfMonth(2);
            }
            else {
                b3 = true;
            }
        }
        else if (collection.remove(DateTimeFieldType.dayOfMonth())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendDayOfMonth(2);
        }
        return b3;
    }
    
    private static boolean dateByOrdinal(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final Collection<DateTimeFieldType> collection, final boolean b, final boolean b2) {
        boolean b3 = false;
        if (collection.remove(DateTimeFieldType.year())) {
            dateTimeFormatterBuilder.append(Constants.ye);
            if (collection.remove(DateTimeFieldType.dayOfYear())) {
                appendSeparator(dateTimeFormatterBuilder, b);
                dateTimeFormatterBuilder.appendDayOfYear(3);
            }
            else {
                b3 = true;
            }
        }
        else if (collection.remove(DateTimeFieldType.dayOfYear())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendDayOfYear(3);
        }
        return b3;
    }
    
    private static boolean dateByWeek(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final Collection<DateTimeFieldType> collection, final boolean b, final boolean b2) {
        boolean b3 = false;
        if (collection.remove(DateTimeFieldType.weekyear())) {
            dateTimeFormatterBuilder.append(Constants.we);
            if (collection.remove(DateTimeFieldType.weekOfWeekyear())) {
                appendSeparator(dateTimeFormatterBuilder, b);
                dateTimeFormatterBuilder.appendLiteral('W');
                dateTimeFormatterBuilder.appendWeekOfWeekyear(2);
                if (collection.remove(DateTimeFieldType.dayOfWeek())) {
                    appendSeparator(dateTimeFormatterBuilder, b);
                    dateTimeFormatterBuilder.appendDayOfWeek(1);
                }
                else {
                    b3 = true;
                }
            }
            else if (collection.remove(DateTimeFieldType.dayOfWeek())) {
                checkNotStrictISO(collection, b2);
                appendSeparator(dateTimeFormatterBuilder, b);
                dateTimeFormatterBuilder.appendLiteral('W');
                dateTimeFormatterBuilder.appendLiteral('-');
                dateTimeFormatterBuilder.appendDayOfWeek(1);
            }
            else {
                b3 = true;
            }
        }
        else if (collection.remove(DateTimeFieldType.weekOfWeekyear())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('W');
            dateTimeFormatterBuilder.appendWeekOfWeekyear(2);
            if (collection.remove(DateTimeFieldType.dayOfWeek())) {
                appendSeparator(dateTimeFormatterBuilder, b);
                dateTimeFormatterBuilder.appendDayOfWeek(1);
            }
            else {
                b3 = true;
            }
        }
        else if (collection.remove(DateTimeFieldType.dayOfWeek())) {
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendLiteral('W');
            dateTimeFormatterBuilder.appendLiteral('-');
            dateTimeFormatterBuilder.appendDayOfWeek(1);
        }
        return b3;
    }
    
    private static void time(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final Collection<DateTimeFieldType> obj, final boolean b, final boolean b2, final boolean b3, final boolean b4) {
        final boolean remove = obj.remove(DateTimeFieldType.hourOfDay());
        final boolean remove2 = obj.remove(DateTimeFieldType.minuteOfHour());
        final boolean remove3 = obj.remove(DateTimeFieldType.secondOfMinute());
        final boolean remove4 = obj.remove(DateTimeFieldType.millisOfSecond());
        if (!remove && !remove2 && !remove3 && !remove4) {
            return;
        }
        if (remove || remove2 || remove3 || remove4) {
            if (b2 && b3) {
                throw new IllegalArgumentException("No valid ISO8601 format for fields because Date was reduced precision: " + obj);
            }
            if (b4) {
                dateTimeFormatterBuilder.appendLiteral('T');
            }
        }
        Label_0266: {
            if (!remove || !remove2 || !remove3) {
                if (!remove || remove3 || remove4) {
                    if (b2 && b4) {
                        throw new IllegalArgumentException("No valid ISO8601 format for fields because Time was truncated: " + obj);
                    }
                    if (!remove) {
                        if ((remove2 && remove3) || (remove2 && !remove4)) {
                            break Label_0266;
                        }
                        if (remove3) {
                            break Label_0266;
                        }
                    }
                    if (b2) {
                        throw new IllegalArgumentException("No valid ISO8601 format for fields: " + obj);
                    }
                }
            }
        }
        if (remove) {
            dateTimeFormatterBuilder.appendHourOfDay(2);
        }
        else if (remove2 || remove3 || remove4) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
        if (b && remove && remove2) {
            dateTimeFormatterBuilder.appendLiteral(':');
        }
        if (remove2) {
            dateTimeFormatterBuilder.appendMinuteOfHour(2);
        }
        else if (remove3 || remove4) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
        if (b && remove2 && remove3) {
            dateTimeFormatterBuilder.appendLiteral(':');
        }
        if (remove3) {
            dateTimeFormatterBuilder.appendSecondOfMinute(2);
        }
        else if (remove4) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
        if (remove4) {
            dateTimeFormatterBuilder.appendLiteral('.');
            dateTimeFormatterBuilder.appendMillisOfSecond(3);
        }
    }
    
    private static void checkNotStrictISO(final Collection<DateTimeFieldType> obj, final boolean b) {
        if (b) {
            throw new IllegalArgumentException("No valid ISO8601 format for fields: " + obj);
        }
    }
    
    private static void appendSeparator(final DateTimeFormatterBuilder dateTimeFormatterBuilder, final boolean b) {
        if (b) {
            dateTimeFormatterBuilder.appendLiteral('-');
        }
    }
    
    public static DateTimeFormatter dateParser() {
        return Constants.dp;
    }
    
    public static DateTimeFormatter localDateParser() {
        return Constants.ldp;
    }
    
    public static DateTimeFormatter dateElementParser() {
        return Constants.dpe;
    }
    
    public static DateTimeFormatter timeParser() {
        return Constants.tp;
    }
    
    public static DateTimeFormatter localTimeParser() {
        return Constants.ltp;
    }
    
    public static DateTimeFormatter timeElementParser() {
        return Constants.tpe;
    }
    
    public static DateTimeFormatter dateTimeParser() {
        return Constants.dtp;
    }
    
    public static DateTimeFormatter dateOptionalTimeParser() {
        return Constants.dotp;
    }
    
    public static DateTimeFormatter localDateOptionalTimeParser() {
        return Constants.ldotp;
    }
    
    public static DateTimeFormatter date() {
        return yearMonthDay();
    }
    
    public static DateTimeFormatter time() {
        return Constants.t;
    }
    
    public static DateTimeFormatter timeNoMillis() {
        return Constants.tx;
    }
    
    public static DateTimeFormatter tTime() {
        return Constants.tt;
    }
    
    public static DateTimeFormatter tTimeNoMillis() {
        return Constants.ttx;
    }
    
    public static DateTimeFormatter dateTime() {
        return Constants.dt;
    }
    
    public static DateTimeFormatter dateTimeNoMillis() {
        return Constants.dtx;
    }
    
    public static DateTimeFormatter ordinalDate() {
        return Constants.od;
    }
    
    public static DateTimeFormatter ordinalDateTime() {
        return Constants.odt;
    }
    
    public static DateTimeFormatter ordinalDateTimeNoMillis() {
        return Constants.odtx;
    }
    
    public static DateTimeFormatter weekDate() {
        return Constants.wwd;
    }
    
    public static DateTimeFormatter weekDateTime() {
        return Constants.wdt;
    }
    
    public static DateTimeFormatter weekDateTimeNoMillis() {
        return Constants.wdtx;
    }
    
    public static DateTimeFormatter basicDate() {
        return Constants.bd;
    }
    
    public static DateTimeFormatter basicTime() {
        return Constants.bt;
    }
    
    public static DateTimeFormatter basicTimeNoMillis() {
        return Constants.btx;
    }
    
    public static DateTimeFormatter basicTTime() {
        return Constants.btt;
    }
    
    public static DateTimeFormatter basicTTimeNoMillis() {
        return Constants.bttx;
    }
    
    public static DateTimeFormatter basicDateTime() {
        return Constants.bdt;
    }
    
    public static DateTimeFormatter basicDateTimeNoMillis() {
        return Constants.bdtx;
    }
    
    public static DateTimeFormatter basicOrdinalDate() {
        return Constants.bod;
    }
    
    public static DateTimeFormatter basicOrdinalDateTime() {
        return Constants.bodt;
    }
    
    public static DateTimeFormatter basicOrdinalDateTimeNoMillis() {
        return Constants.bodtx;
    }
    
    public static DateTimeFormatter basicWeekDate() {
        return Constants.bwd;
    }
    
    public static DateTimeFormatter basicWeekDateTime() {
        return Constants.bwdt;
    }
    
    public static DateTimeFormatter basicWeekDateTimeNoMillis() {
        return Constants.bwdtx;
    }
    
    public static DateTimeFormatter year() {
        return Constants.ye;
    }
    
    public static DateTimeFormatter yearMonth() {
        return Constants.ym;
    }
    
    public static DateTimeFormatter yearMonthDay() {
        return Constants.ymd;
    }
    
    public static DateTimeFormatter weekyear() {
        return Constants.we;
    }
    
    public static DateTimeFormatter weekyearWeek() {
        return Constants.ww;
    }
    
    public static DateTimeFormatter weekyearWeekDay() {
        return Constants.wwd;
    }
    
    public static DateTimeFormatter hour() {
        return Constants.hde;
    }
    
    public static DateTimeFormatter hourMinute() {
        return Constants.hm;
    }
    
    public static DateTimeFormatter hourMinuteSecond() {
        return Constants.hms;
    }
    
    public static DateTimeFormatter hourMinuteSecondMillis() {
        return Constants.hmsl;
    }
    
    public static DateTimeFormatter hourMinuteSecondFraction() {
        return Constants.hmsf;
    }
    
    public static DateTimeFormatter dateHour() {
        return Constants.dh;
    }
    
    public static DateTimeFormatter dateHourMinute() {
        return Constants.dhm;
    }
    
    public static DateTimeFormatter dateHourMinuteSecond() {
        return Constants.dhms;
    }
    
    public static DateTimeFormatter dateHourMinuteSecondMillis() {
        return Constants.dhmsl;
    }
    
    public static DateTimeFormatter dateHourMinuteSecondFraction() {
        return Constants.dhmsf;
    }
    
    static final class Constants
    {
        private static final DateTimeFormatter ye;
        private static final DateTimeFormatter mye;
        private static final DateTimeFormatter dme;
        private static final DateTimeFormatter we;
        private static final DateTimeFormatter wwe;
        private static final DateTimeFormatter dwe;
        private static final DateTimeFormatter dye;
        private static final DateTimeFormatter hde;
        private static final DateTimeFormatter mhe;
        private static final DateTimeFormatter sme;
        private static final DateTimeFormatter fse;
        private static final DateTimeFormatter ze;
        private static final DateTimeFormatter lte;
        private static final DateTimeFormatter ym;
        private static final DateTimeFormatter ymd;
        private static final DateTimeFormatter ww;
        private static final DateTimeFormatter wwd;
        private static final DateTimeFormatter hm;
        private static final DateTimeFormatter hms;
        private static final DateTimeFormatter hmsl;
        private static final DateTimeFormatter hmsf;
        private static final DateTimeFormatter dh;
        private static final DateTimeFormatter dhm;
        private static final DateTimeFormatter dhms;
        private static final DateTimeFormatter dhmsl;
        private static final DateTimeFormatter dhmsf;
        private static final DateTimeFormatter t;
        private static final DateTimeFormatter tx;
        private static final DateTimeFormatter tt;
        private static final DateTimeFormatter ttx;
        private static final DateTimeFormatter dt;
        private static final DateTimeFormatter dtx;
        private static final DateTimeFormatter wdt;
        private static final DateTimeFormatter wdtx;
        private static final DateTimeFormatter od;
        private static final DateTimeFormatter odt;
        private static final DateTimeFormatter odtx;
        private static final DateTimeFormatter bd;
        private static final DateTimeFormatter bt;
        private static final DateTimeFormatter btx;
        private static final DateTimeFormatter btt;
        private static final DateTimeFormatter bttx;
        private static final DateTimeFormatter bdt;
        private static final DateTimeFormatter bdtx;
        private static final DateTimeFormatter bod;
        private static final DateTimeFormatter bodt;
        private static final DateTimeFormatter bodtx;
        private static final DateTimeFormatter bwd;
        private static final DateTimeFormatter bwdt;
        private static final DateTimeFormatter bwdtx;
        private static final DateTimeFormatter dpe;
        private static final DateTimeFormatter tpe;
        private static final DateTimeFormatter dp;
        private static final DateTimeFormatter ldp;
        private static final DateTimeFormatter tp;
        private static final DateTimeFormatter ltp;
        private static final DateTimeFormatter dtp;
        private static final DateTimeFormatter dotp;
        private static final DateTimeFormatter ldotp;
        
        private static DateTimeFormatter dateParser() {
            if (Constants.dp == null) {
                return new DateTimeFormatterBuilder().append(dateElementParser()).appendOptional(new DateTimeFormatterBuilder().appendLiteral('T').append(offsetElement()).toParser()).toFormatter();
            }
            return Constants.dp;
        }
        
        private static DateTimeFormatter localDateParser() {
            if (Constants.ldp == null) {
                return dateElementParser().withZoneUTC();
            }
            return Constants.ldp;
        }
        
        private static DateTimeFormatter dateElementParser() {
            if (Constants.dpe == null) {
                return new DateTimeFormatterBuilder().append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().append(yearElement()).appendOptional(new DateTimeFormatterBuilder().append(monthElement()).appendOptional(dayOfMonthElement().getParser()).toParser()).toParser(), new DateTimeFormatterBuilder().append(weekyearElement()).append(weekElement()).appendOptional(dayOfWeekElement().getParser()).toParser(), new DateTimeFormatterBuilder().append(yearElement()).append(dayOfYearElement()).toParser() }).toFormatter();
            }
            return Constants.dpe;
        }
        
        private static DateTimeFormatter timeParser() {
            if (Constants.tp == null) {
                return new DateTimeFormatterBuilder().appendOptional(literalTElement().getParser()).append(timeElementParser()).appendOptional(offsetElement().getParser()).toFormatter();
            }
            return Constants.tp;
        }
        
        private static DateTimeFormatter localTimeParser() {
            if (Constants.ltp == null) {
                return new DateTimeFormatterBuilder().appendOptional(literalTElement().getParser()).append(timeElementParser()).toFormatter().withZoneUTC();
            }
            return Constants.ltp;
        }
        
        private static DateTimeFormatter timeElementParser() {
            if (Constants.tpe == null) {
                final DateTimeParser parser = new DateTimeFormatterBuilder().append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().appendLiteral('.').toParser(), new DateTimeFormatterBuilder().appendLiteral(',').toParser() }).toParser();
                return new DateTimeFormatterBuilder().append(hourElement()).append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().append(minuteElement()).append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().append(secondElement()).appendOptional(new DateTimeFormatterBuilder().append(parser).appendFractionOfSecond(1, 9).toParser()).toParser(), new DateTimeFormatterBuilder().append(parser).appendFractionOfMinute(1, 9).toParser(), null }).toParser(), new DateTimeFormatterBuilder().append(parser).appendFractionOfHour(1, 9).toParser(), null }).toFormatter();
            }
            return Constants.tpe;
        }
        
        private static DateTimeFormatter dateTimeParser() {
            if (Constants.dtp == null) {
                return new DateTimeFormatterBuilder().append(null, new DateTimeParser[] { new DateTimeFormatterBuilder().appendLiteral('T').append(timeElementParser()).appendOptional(offsetElement().getParser()).toParser(), dateOptionalTimeParser().getParser() }).toFormatter();
            }
            return Constants.dtp;
        }
        
        private static DateTimeFormatter dateOptionalTimeParser() {
            if (Constants.dotp == null) {
                return new DateTimeFormatterBuilder().append(dateElementParser()).appendOptional(new DateTimeFormatterBuilder().appendLiteral('T').appendOptional(timeElementParser().getParser()).appendOptional(offsetElement().getParser()).toParser()).toFormatter();
            }
            return Constants.dotp;
        }
        
        private static DateTimeFormatter localDateOptionalTimeParser() {
            if (Constants.ldotp == null) {
                return new DateTimeFormatterBuilder().append(dateElementParser()).appendOptional(new DateTimeFormatterBuilder().appendLiteral('T').append(timeElementParser()).toParser()).toFormatter().withZoneUTC();
            }
            return Constants.ldotp;
        }
        
        private static DateTimeFormatter time() {
            if (Constants.t == null) {
                return new DateTimeFormatterBuilder().append(hourMinuteSecondFraction()).append(offsetElement()).toFormatter();
            }
            return Constants.t;
        }
        
        private static DateTimeFormatter timeNoMillis() {
            if (Constants.tx == null) {
                return new DateTimeFormatterBuilder().append(hourMinuteSecond()).append(offsetElement()).toFormatter();
            }
            return Constants.tx;
        }
        
        private static DateTimeFormatter tTime() {
            if (Constants.tt == null) {
                return new DateTimeFormatterBuilder().append(literalTElement()).append(time()).toFormatter();
            }
            return Constants.tt;
        }
        
        private static DateTimeFormatter tTimeNoMillis() {
            if (Constants.ttx == null) {
                return new DateTimeFormatterBuilder().append(literalTElement()).append(timeNoMillis()).toFormatter();
            }
            return Constants.ttx;
        }
        
        private static DateTimeFormatter dateTime() {
            if (Constants.dt == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(tTime()).toFormatter();
            }
            return Constants.dt;
        }
        
        private static DateTimeFormatter dateTimeNoMillis() {
            if (Constants.dtx == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(tTimeNoMillis()).toFormatter();
            }
            return Constants.dtx;
        }
        
        private static DateTimeFormatter ordinalDate() {
            if (Constants.od == null) {
                return new DateTimeFormatterBuilder().append(yearElement()).append(dayOfYearElement()).toFormatter();
            }
            return Constants.od;
        }
        
        private static DateTimeFormatter ordinalDateTime() {
            if (Constants.odt == null) {
                return new DateTimeFormatterBuilder().append(ordinalDate()).append(tTime()).toFormatter();
            }
            return Constants.odt;
        }
        
        private static DateTimeFormatter ordinalDateTimeNoMillis() {
            if (Constants.odtx == null) {
                return new DateTimeFormatterBuilder().append(ordinalDate()).append(tTimeNoMillis()).toFormatter();
            }
            return Constants.odtx;
        }
        
        private static DateTimeFormatter weekDateTime() {
            if (Constants.wdt == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.weekDate()).append(tTime()).toFormatter();
            }
            return Constants.wdt;
        }
        
        private static DateTimeFormatter weekDateTimeNoMillis() {
            if (Constants.wdtx == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.weekDate()).append(tTimeNoMillis()).toFormatter();
            }
            return Constants.wdtx;
        }
        
        private static DateTimeFormatter basicDate() {
            if (Constants.bd == null) {
                return new DateTimeFormatterBuilder().appendYear(4, 4).appendFixedDecimal(DateTimeFieldType.monthOfYear(), 2).appendFixedDecimal(DateTimeFieldType.dayOfMonth(), 2).toFormatter();
            }
            return Constants.bd;
        }
        
        private static DateTimeFormatter basicTime() {
            if (Constants.bt == null) {
                return new DateTimeFormatterBuilder().appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2).appendLiteral('.').appendFractionOfSecond(3, 9).appendTimeZoneOffset("Z", false, 2, 2).toFormatter();
            }
            return Constants.bt;
        }
        
        private static DateTimeFormatter basicTimeNoMillis() {
            if (Constants.btx == null) {
                return new DateTimeFormatterBuilder().appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2).appendTimeZoneOffset("Z", false, 2, 2).toFormatter();
            }
            return Constants.btx;
        }
        
        private static DateTimeFormatter basicTTime() {
            if (Constants.btt == null) {
                return new DateTimeFormatterBuilder().append(literalTElement()).append(basicTime()).toFormatter();
            }
            return Constants.btt;
        }
        
        private static DateTimeFormatter basicTTimeNoMillis() {
            if (Constants.bttx == null) {
                return new DateTimeFormatterBuilder().append(literalTElement()).append(basicTimeNoMillis()).toFormatter();
            }
            return Constants.bttx;
        }
        
        private static DateTimeFormatter basicDateTime() {
            if (Constants.bdt == null) {
                return new DateTimeFormatterBuilder().append(basicDate()).append(basicTTime()).toFormatter();
            }
            return Constants.bdt;
        }
        
        private static DateTimeFormatter basicDateTimeNoMillis() {
            if (Constants.bdtx == null) {
                return new DateTimeFormatterBuilder().append(basicDate()).append(basicTTimeNoMillis()).toFormatter();
            }
            return Constants.bdtx;
        }
        
        private static DateTimeFormatter basicOrdinalDate() {
            if (Constants.bod == null) {
                return new DateTimeFormatterBuilder().appendYear(4, 4).appendFixedDecimal(DateTimeFieldType.dayOfYear(), 3).toFormatter();
            }
            return Constants.bod;
        }
        
        private static DateTimeFormatter basicOrdinalDateTime() {
            if (Constants.bodt == null) {
                return new DateTimeFormatterBuilder().append(basicOrdinalDate()).append(basicTTime()).toFormatter();
            }
            return Constants.bodt;
        }
        
        private static DateTimeFormatter basicOrdinalDateTimeNoMillis() {
            if (Constants.bodtx == null) {
                return new DateTimeFormatterBuilder().append(basicOrdinalDate()).append(basicTTimeNoMillis()).toFormatter();
            }
            return Constants.bodtx;
        }
        
        private static DateTimeFormatter basicWeekDate() {
            if (Constants.bwd == null) {
                return new DateTimeFormatterBuilder().appendWeekyear(4, 4).appendLiteral('W').appendFixedDecimal(DateTimeFieldType.weekOfWeekyear(), 2).appendFixedDecimal(DateTimeFieldType.dayOfWeek(), 1).toFormatter();
            }
            return Constants.bwd;
        }
        
        private static DateTimeFormatter basicWeekDateTime() {
            if (Constants.bwdt == null) {
                return new DateTimeFormatterBuilder().append(basicWeekDate()).append(basicTTime()).toFormatter();
            }
            return Constants.bwdt;
        }
        
        private static DateTimeFormatter basicWeekDateTimeNoMillis() {
            if (Constants.bwdtx == null) {
                return new DateTimeFormatterBuilder().append(basicWeekDate()).append(basicTTimeNoMillis()).toFormatter();
            }
            return Constants.bwdtx;
        }
        
        private static DateTimeFormatter yearMonth() {
            if (Constants.ym == null) {
                return new DateTimeFormatterBuilder().append(yearElement()).append(monthElement()).toFormatter();
            }
            return Constants.ym;
        }
        
        private static DateTimeFormatter yearMonthDay() {
            if (Constants.ymd == null) {
                return new DateTimeFormatterBuilder().append(yearElement()).append(monthElement()).append(dayOfMonthElement()).toFormatter();
            }
            return Constants.ymd;
        }
        
        private static DateTimeFormatter weekyearWeek() {
            if (Constants.ww == null) {
                return new DateTimeFormatterBuilder().append(weekyearElement()).append(weekElement()).toFormatter();
            }
            return Constants.ww;
        }
        
        private static DateTimeFormatter weekyearWeekDay() {
            if (Constants.wwd == null) {
                return new DateTimeFormatterBuilder().append(weekyearElement()).append(weekElement()).append(dayOfWeekElement()).toFormatter();
            }
            return Constants.wwd;
        }
        
        private static DateTimeFormatter hourMinute() {
            if (Constants.hm == null) {
                return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).toFormatter();
            }
            return Constants.hm;
        }
        
        private static DateTimeFormatter hourMinuteSecond() {
            if (Constants.hms == null) {
                return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).append(secondElement()).toFormatter();
            }
            return Constants.hms;
        }
        
        private static DateTimeFormatter hourMinuteSecondMillis() {
            if (Constants.hmsl == null) {
                return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).append(secondElement()).appendLiteral('.').appendFractionOfSecond(3, 3).toFormatter();
            }
            return Constants.hmsl;
        }
        
        private static DateTimeFormatter hourMinuteSecondFraction() {
            if (Constants.hmsf == null) {
                return new DateTimeFormatterBuilder().append(hourElement()).append(minuteElement()).append(secondElement()).append(fractionElement()).toFormatter();
            }
            return Constants.hmsf;
        }
        
        private static DateTimeFormatter dateHour() {
            if (Constants.dh == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(ISODateTimeFormat.hour()).toFormatter();
            }
            return Constants.dh;
        }
        
        private static DateTimeFormatter dateHourMinute() {
            if (Constants.dhm == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinute()).toFormatter();
            }
            return Constants.dhm;
        }
        
        private static DateTimeFormatter dateHourMinuteSecond() {
            if (Constants.dhms == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinuteSecond()).toFormatter();
            }
            return Constants.dhms;
        }
        
        private static DateTimeFormatter dateHourMinuteSecondMillis() {
            if (Constants.dhmsl == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinuteSecondMillis()).toFormatter();
            }
            return Constants.dhmsl;
        }
        
        private static DateTimeFormatter dateHourMinuteSecondFraction() {
            if (Constants.dhmsf == null) {
                return new DateTimeFormatterBuilder().append(ISODateTimeFormat.date()).append(literalTElement()).append(hourMinuteSecondFraction()).toFormatter();
            }
            return Constants.dhmsf;
        }
        
        private static DateTimeFormatter yearElement() {
            if (Constants.ye == null) {
                return new DateTimeFormatterBuilder().appendYear(4, 9).toFormatter();
            }
            return Constants.ye;
        }
        
        private static DateTimeFormatter monthElement() {
            if (Constants.mye == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendMonthOfYear(2).toFormatter();
            }
            return Constants.mye;
        }
        
        private static DateTimeFormatter dayOfMonthElement() {
            if (Constants.dme == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfMonth(2).toFormatter();
            }
            return Constants.dme;
        }
        
        private static DateTimeFormatter weekyearElement() {
            if (Constants.we == null) {
                return new DateTimeFormatterBuilder().appendWeekyear(4, 9).toFormatter();
            }
            return Constants.we;
        }
        
        private static DateTimeFormatter weekElement() {
            if (Constants.wwe == null) {
                return new DateTimeFormatterBuilder().appendLiteral("-W").appendWeekOfWeekyear(2).toFormatter();
            }
            return Constants.wwe;
        }
        
        private static DateTimeFormatter dayOfWeekElement() {
            if (Constants.dwe == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfWeek(1).toFormatter();
            }
            return Constants.dwe;
        }
        
        private static DateTimeFormatter dayOfYearElement() {
            if (Constants.dye == null) {
                return new DateTimeFormatterBuilder().appendLiteral('-').appendDayOfYear(3).toFormatter();
            }
            return Constants.dye;
        }
        
        private static DateTimeFormatter literalTElement() {
            if (Constants.lte == null) {
                return new DateTimeFormatterBuilder().appendLiteral('T').toFormatter();
            }
            return Constants.lte;
        }
        
        private static DateTimeFormatter hourElement() {
            if (Constants.hde == null) {
                return new DateTimeFormatterBuilder().appendHourOfDay(2).toFormatter();
            }
            return Constants.hde;
        }
        
        private static DateTimeFormatter minuteElement() {
            if (Constants.mhe == null) {
                return new DateTimeFormatterBuilder().appendLiteral(':').appendMinuteOfHour(2).toFormatter();
            }
            return Constants.mhe;
        }
        
        private static DateTimeFormatter secondElement() {
            if (Constants.sme == null) {
                return new DateTimeFormatterBuilder().appendLiteral(':').appendSecondOfMinute(2).toFormatter();
            }
            return Constants.sme;
        }
        
        private static DateTimeFormatter fractionElement() {
            if (Constants.fse == null) {
                return new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toFormatter();
            }
            return Constants.fse;
        }
        
        private static DateTimeFormatter offsetElement() {
            if (Constants.ze == null) {
                return new DateTimeFormatterBuilder().appendTimeZoneOffset("Z", true, 2, 4).toFormatter();
            }
            return Constants.ze;
        }
        
        static {
            ye = yearElement();
            mye = monthElement();
            dme = dayOfMonthElement();
            we = weekyearElement();
            wwe = weekElement();
            dwe = dayOfWeekElement();
            dye = dayOfYearElement();
            hde = hourElement();
            mhe = minuteElement();
            sme = secondElement();
            fse = fractionElement();
            ze = offsetElement();
            lte = literalTElement();
            ym = yearMonth();
            ymd = yearMonthDay();
            ww = weekyearWeek();
            wwd = weekyearWeekDay();
            hm = hourMinute();
            hms = hourMinuteSecond();
            hmsl = hourMinuteSecondMillis();
            hmsf = hourMinuteSecondFraction();
            dh = dateHour();
            dhm = dateHourMinute();
            dhms = dateHourMinuteSecond();
            dhmsl = dateHourMinuteSecondMillis();
            dhmsf = dateHourMinuteSecondFraction();
            t = time();
            tx = timeNoMillis();
            tt = tTime();
            ttx = tTimeNoMillis();
            dt = dateTime();
            dtx = dateTimeNoMillis();
            wdt = weekDateTime();
            wdtx = weekDateTimeNoMillis();
            od = ordinalDate();
            odt = ordinalDateTime();
            odtx = ordinalDateTimeNoMillis();
            bd = basicDate();
            bt = basicTime();
            btx = basicTimeNoMillis();
            btt = basicTTime();
            bttx = basicTTimeNoMillis();
            bdt = basicDateTime();
            bdtx = basicDateTimeNoMillis();
            bod = basicOrdinalDate();
            bodt = basicOrdinalDateTime();
            bodtx = basicOrdinalDateTimeNoMillis();
            bwd = basicWeekDate();
            bwdt = basicWeekDateTime();
            bwdtx = basicWeekDateTimeNoMillis();
            dpe = dateElementParser();
            tpe = timeElementParser();
            dp = dateParser();
            ldp = localDateParser();
            tp = timeParser();
            ltp = localTimeParser();
            dtp = dateTimeParser();
            dotp = dateOptionalTimeParser();
            ldotp = localDateOptionalTimeParser();
        }
    }
}
