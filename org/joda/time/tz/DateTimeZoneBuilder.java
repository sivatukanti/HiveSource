// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import java.util.HashSet;
import java.util.Arrays;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import java.text.DateFormatSymbols;
import java.util.Locale;
import org.joda.time.DateTimeUtils;
import java.util.Iterator;
import java.util.Collection;
import org.joda.time.Chronology;
import org.joda.time.chrono.ISOChronology;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataInput;
import org.joda.time.DateTimeZone;
import java.io.InputStream;
import java.util.ArrayList;

public class DateTimeZoneBuilder
{
    private final ArrayList<RuleSet> iRuleSets;
    
    public static DateTimeZone readFrom(final InputStream in, final String s) throws IOException {
        if (in instanceof DataInput) {
            return readFrom((DataInput)in, s);
        }
        return readFrom((DataInput)new DataInputStream(in), s);
    }
    
    public static DateTimeZone readFrom(final DataInput dataInput, final String s) throws IOException {
        switch (dataInput.readUnsignedByte()) {
            case 70: {
                DateTimeZone utc = new FixedDateTimeZone(s, dataInput.readUTF(), (int)readMillis(dataInput), (int)readMillis(dataInput));
                if (utc.equals(DateTimeZone.UTC)) {
                    utc = DateTimeZone.UTC;
                }
                return utc;
            }
            case 67: {
                return CachedDateTimeZone.forZone(PrecalculatedZone.readFrom(dataInput, s));
            }
            case 80: {
                return PrecalculatedZone.readFrom(dataInput, s);
            }
            default: {
                throw new IOException("Invalid encoding");
            }
        }
    }
    
    static void writeMillis(final DataOutput dataOutput, final long n) throws IOException {
        if (n % 1800000L == 0L) {
            final long n2 = n / 1800000L;
            if (n2 << 58 >> 58 == n2) {
                dataOutput.writeByte((int)(n2 & 0x3FL));
                return;
            }
        }
        if (n % 60000L == 0L) {
            final long n3 = n / 60000L;
            if (n3 << 34 >> 34 == n3) {
                dataOutput.writeInt(0x40000000 | (int)(n3 & 0x3FFFFFFFL));
                return;
            }
        }
        if (n % 1000L == 0L) {
            final long n4 = n / 1000L;
            if (n4 << 26 >> 26 == n4) {
                dataOutput.writeByte(0x80 | (int)(n4 >> 32 & 0x3FL));
                dataOutput.writeInt((int)(n4 & -1L));
                return;
            }
        }
        dataOutput.writeByte((n < 0L) ? 255 : 192);
        dataOutput.writeLong(n);
    }
    
    static long readMillis(final DataInput dataInput) throws IOException {
        final int unsignedByte = dataInput.readUnsignedByte();
        switch (unsignedByte >> 6) {
            default: {
                return (unsignedByte << 26 >> 26) * 1800000L;
            }
            case 1: {
                return (unsignedByte << 26 >> 2 | dataInput.readUnsignedByte() << 16 | dataInput.readUnsignedByte() << 8 | dataInput.readUnsignedByte()) * 60000L;
            }
            case 2: {
                return ((long)unsignedByte << 58 >> 26 | (long)(dataInput.readUnsignedByte() << 24) | (long)(dataInput.readUnsignedByte() << 16) | (long)(dataInput.readUnsignedByte() << 8) | (long)dataInput.readUnsignedByte()) * 1000L;
            }
            case 3: {
                return dataInput.readLong();
            }
        }
    }
    
    private static DateTimeZone buildFixedZone(final String anObject, final String anObject2, final int n, final int n2) {
        if ("UTC".equals(anObject) && anObject.equals(anObject2) && n == 0 && n2 == 0) {
            return DateTimeZone.UTC;
        }
        return new FixedDateTimeZone(anObject, anObject2, n, n2);
    }
    
    public DateTimeZoneBuilder() {
        this.iRuleSets = new ArrayList<RuleSet>(10);
    }
    
    public DateTimeZoneBuilder addCutover(final int n, final char c, final int n2, final int n3, final int n4, final boolean b, final int n5) {
        if (this.iRuleSets.size() > 0) {
            this.iRuleSets.get(this.iRuleSets.size() - 1).setUpperLimit(n, new OfYear(c, n2, n3, n4, b, n5));
        }
        this.iRuleSets.add(new RuleSet());
        return this;
    }
    
    public DateTimeZoneBuilder setStandardOffset(final int standardOffset) {
        this.getLastRuleSet().setStandardOffset(standardOffset);
        return this;
    }
    
    public DateTimeZoneBuilder setFixedSavings(final String s, final int n) {
        this.getLastRuleSet().setFixedSavings(s, n);
        return this;
    }
    
    public DateTimeZoneBuilder addRecurringSavings(final String s, final int n, final int n2, final int n3, final char c, final int n4, final int n5, final int n6, final boolean b, final int n7) {
        if (n2 <= n3) {
            this.getLastRuleSet().addRule(new Rule(new Recurrence(new OfYear(c, n4, n5, n6, b, n7), s, n), n2, n3));
        }
        return this;
    }
    
    private RuleSet getLastRuleSet() {
        if (this.iRuleSets.size() == 0) {
            this.addCutover(Integer.MIN_VALUE, 'w', 1, 1, 0, false, 0);
        }
        return this.iRuleSets.get(this.iRuleSets.size() - 1);
    }
    
    public DateTimeZone toDateTimeZone(final String s, final boolean b) {
        if (s == null) {
            throw new IllegalArgumentException();
        }
        final ArrayList<Transition> list = new ArrayList<Transition>();
        DSTZone buildTailZone = null;
        long upperLimit = Long.MIN_VALUE;
        for (int size = this.iRuleSets.size(), i = 0; i < size; ++i) {
            final RuleSet set = this.iRuleSets.get(i);
            final Transition firstTransition = set.firstTransition(upperLimit);
            if (firstTransition != null) {
                this.addTransition(list, firstTransition);
                long n;
                int n2;
                RuleSet set2;
                Transition nextTransition;
                for (n = firstTransition.getMillis(), n2 = firstTransition.getSaveMillis(), set2 = new RuleSet(set); (nextTransition = set2.nextTransition(n, n2)) != null && (!this.addTransition(list, nextTransition) || buildTailZone == null); buildTailZone = set2.buildTailZone(s)) {
                    n = nextTransition.getMillis();
                    n2 = nextTransition.getSaveMillis();
                    if (buildTailZone == null && i == size - 1) {}
                }
                upperLimit = set2.getUpperLimit(n2);
            }
        }
        if (list.size() == 0) {
            if (buildTailZone != null) {
                return buildTailZone;
            }
            return buildFixedZone(s, "UTC", 0, 0);
        }
        else {
            if (list.size() == 1 && buildTailZone == null) {
                final Transition transition = list.get(0);
                return buildFixedZone(s, transition.getNameKey(), transition.getWallOffset(), transition.getStandardOffset());
            }
            final PrecalculatedZone create = PrecalculatedZone.create(s, b, list, buildTailZone);
            if (create.isCachable()) {
                return CachedDateTimeZone.forZone(create);
            }
            return create;
        }
    }
    
    private boolean addTransition(final ArrayList<Transition> list, final Transition transition) {
        final int size = list.size();
        if (size == 0) {
            list.add(transition);
            return true;
        }
        final Transition transition2 = list.get(size - 1);
        if (!transition.isTransitionFrom(transition2)) {
            return false;
        }
        int wallOffset = 0;
        if (size >= 2) {
            wallOffset = list.get(size - 2).getWallOffset();
        }
        if (transition.getMillis() + transition2.getWallOffset() != transition2.getMillis() + wallOffset) {
            list.add(transition);
            return true;
        }
        list.remove(size - 1);
        return this.addTransition(list, transition);
    }
    
    public void writeTo(final String s, final OutputStream out) throws IOException {
        if (out instanceof DataOutput) {
            this.writeTo(s, (DataOutput)out);
        }
        else {
            this.writeTo(s, (DataOutput)new DataOutputStream(out));
        }
    }
    
    public void writeTo(final String s, final DataOutput dataOutput) throws IOException {
        DateTimeZone dateTimeZone = this.toDateTimeZone(s, false);
        if (dateTimeZone instanceof FixedDateTimeZone) {
            dataOutput.writeByte(70);
            dataOutput.writeUTF(dateTimeZone.getNameKey(0L));
            writeMillis(dataOutput, dateTimeZone.getOffset(0L));
            writeMillis(dataOutput, dateTimeZone.getStandardOffset(0L));
        }
        else {
            if (dateTimeZone instanceof CachedDateTimeZone) {
                dataOutput.writeByte(67);
                dateTimeZone = ((CachedDateTimeZone)dateTimeZone).getUncachedZone();
            }
            else {
                dataOutput.writeByte(80);
            }
            ((PrecalculatedZone)dateTimeZone).writeTo(dataOutput);
        }
    }
    
    private static final class OfYear
    {
        final char iMode;
        final int iMonthOfYear;
        final int iDayOfMonth;
        final int iDayOfWeek;
        final boolean iAdvance;
        final int iMillisOfDay;
        
        static OfYear readFrom(final DataInput dataInput) throws IOException {
            return new OfYear((char)dataInput.readUnsignedByte(), dataInput.readUnsignedByte(), dataInput.readByte(), dataInput.readUnsignedByte(), dataInput.readBoolean(), (int)DateTimeZoneBuilder.readMillis(dataInput));
        }
        
        OfYear(final char c, final int iMonthOfYear, final int iDayOfMonth, final int iDayOfWeek, final boolean iAdvance, final int iMillisOfDay) {
            if (c != 'u' && c != 'w' && c != 's') {
                throw new IllegalArgumentException("Unknown mode: " + c);
            }
            this.iMode = c;
            this.iMonthOfYear = iMonthOfYear;
            this.iDayOfMonth = iDayOfMonth;
            this.iDayOfWeek = iDayOfWeek;
            this.iAdvance = iAdvance;
            this.iMillisOfDay = iMillisOfDay;
        }
        
        public long setInstant(final int n, final int n2, final int n3) {
            int n4;
            if (this.iMode == 'w') {
                n4 = n2 + n3;
            }
            else if (this.iMode == 's') {
                n4 = n2;
            }
            else {
                n4 = 0;
            }
            final ISOChronology instanceUTC = ISOChronology.getInstanceUTC();
            long n5 = this.setDayOfMonth(instanceUTC, instanceUTC.millisOfDay().set(instanceUTC.monthOfYear().set(instanceUTC.year().set(0L, n), this.iMonthOfYear), this.iMillisOfDay));
            if (this.iDayOfWeek != 0) {
                n5 = this.setDayOfWeek(instanceUTC, n5);
            }
            return n5 - n4;
        }
        
        public long next(long n, final int n2, final int n3) {
            int n4;
            if (this.iMode == 'w') {
                n4 = n2 + n3;
            }
            else if (this.iMode == 's') {
                n4 = n2;
            }
            else {
                n4 = 0;
            }
            n += n4;
            final ISOChronology instanceUTC = ISOChronology.getInstanceUTC();
            long n5 = this.setDayOfMonthNext(instanceUTC, instanceUTC.millisOfDay().add(instanceUTC.millisOfDay().set(instanceUTC.monthOfYear().set(n, this.iMonthOfYear), 0), this.iMillisOfDay));
            if (this.iDayOfWeek == 0) {
                if (n5 <= n) {
                    n5 = this.setDayOfMonthNext(instanceUTC, instanceUTC.year().add(n5, 1));
                }
            }
            else {
                n5 = this.setDayOfWeek(instanceUTC, n5);
                if (n5 <= n) {
                    n5 = this.setDayOfWeek(instanceUTC, this.setDayOfMonthNext(instanceUTC, instanceUTC.monthOfYear().set(instanceUTC.year().add(n5, 1), this.iMonthOfYear)));
                }
            }
            return n5 - n4;
        }
        
        public long previous(long n, final int n2, final int n3) {
            int n4;
            if (this.iMode == 'w') {
                n4 = n2 + n3;
            }
            else if (this.iMode == 's') {
                n4 = n2;
            }
            else {
                n4 = 0;
            }
            n += n4;
            final ISOChronology instanceUTC = ISOChronology.getInstanceUTC();
            long n5 = this.setDayOfMonthPrevious(instanceUTC, instanceUTC.millisOfDay().add(instanceUTC.millisOfDay().set(instanceUTC.monthOfYear().set(n, this.iMonthOfYear), 0), this.iMillisOfDay));
            if (this.iDayOfWeek == 0) {
                if (n5 >= n) {
                    n5 = this.setDayOfMonthPrevious(instanceUTC, instanceUTC.year().add(n5, -1));
                }
            }
            else {
                n5 = this.setDayOfWeek(instanceUTC, n5);
                if (n5 >= n) {
                    n5 = this.setDayOfWeek(instanceUTC, this.setDayOfMonthPrevious(instanceUTC, instanceUTC.monthOfYear().set(instanceUTC.year().add(n5, -1), this.iMonthOfYear)));
                }
            }
            return n5 - n4;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof OfYear) {
                final OfYear ofYear = (OfYear)o;
                return this.iMode == ofYear.iMode && this.iMonthOfYear == ofYear.iMonthOfYear && this.iDayOfMonth == ofYear.iDayOfMonth && this.iDayOfWeek == ofYear.iDayOfWeek && this.iAdvance == ofYear.iAdvance && this.iMillisOfDay == ofYear.iMillisOfDay;
            }
            return false;
        }
        
        public void writeTo(final DataOutput dataOutput) throws IOException {
            dataOutput.writeByte(this.iMode);
            dataOutput.writeByte(this.iMonthOfYear);
            dataOutput.writeByte(this.iDayOfMonth);
            dataOutput.writeByte(this.iDayOfWeek);
            dataOutput.writeBoolean(this.iAdvance);
            DateTimeZoneBuilder.writeMillis(dataOutput, this.iMillisOfDay);
        }
        
        private long setDayOfMonthNext(final Chronology chronology, long n) {
            try {
                n = this.setDayOfMonth(chronology, n);
            }
            catch (IllegalArgumentException ex) {
                if (this.iMonthOfYear != 2 || this.iDayOfMonth != 29) {
                    throw ex;
                }
                while (!chronology.year().isLeap(n)) {
                    n = chronology.year().add(n, 1);
                }
                n = this.setDayOfMonth(chronology, n);
            }
            return n;
        }
        
        private long setDayOfMonthPrevious(final Chronology chronology, long n) {
            try {
                n = this.setDayOfMonth(chronology, n);
            }
            catch (IllegalArgumentException ex) {
                if (this.iMonthOfYear != 2 || this.iDayOfMonth != 29) {
                    throw ex;
                }
                while (!chronology.year().isLeap(n)) {
                    n = chronology.year().add(n, -1);
                }
                n = this.setDayOfMonth(chronology, n);
            }
            return n;
        }
        
        private long setDayOfMonth(final Chronology chronology, long n) {
            if (this.iDayOfMonth >= 0) {
                n = chronology.dayOfMonth().set(n, this.iDayOfMonth);
            }
            else {
                n = chronology.dayOfMonth().set(n, 1);
                n = chronology.monthOfYear().add(n, 1);
                n = chronology.dayOfMonth().add(n, this.iDayOfMonth);
            }
            return n;
        }
        
        private long setDayOfWeek(final Chronology chronology, long add) {
            int n = this.iDayOfWeek - chronology.dayOfWeek().get(add);
            if (n != 0) {
                if (this.iAdvance) {
                    if (n < 0) {
                        n += 7;
                    }
                }
                else if (n > 0) {
                    n -= 7;
                }
                add = chronology.dayOfWeek().add(add, n);
            }
            return add;
        }
    }
    
    private static final class Recurrence
    {
        final OfYear iOfYear;
        final String iNameKey;
        final int iSaveMillis;
        
        static Recurrence readFrom(final DataInput dataInput) throws IOException {
            return new Recurrence(OfYear.readFrom(dataInput), dataInput.readUTF(), (int)DateTimeZoneBuilder.readMillis(dataInput));
        }
        
        Recurrence(final OfYear iOfYear, final String iNameKey, final int iSaveMillis) {
            this.iOfYear = iOfYear;
            this.iNameKey = iNameKey;
            this.iSaveMillis = iSaveMillis;
        }
        
        public OfYear getOfYear() {
            return this.iOfYear;
        }
        
        public long next(final long n, final int n2, final int n3) {
            return this.iOfYear.next(n, n2, n3);
        }
        
        public long previous(final long n, final int n2, final int n3) {
            return this.iOfYear.previous(n, n2, n3);
        }
        
        public String getNameKey() {
            return this.iNameKey;
        }
        
        public int getSaveMillis() {
            return this.iSaveMillis;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof Recurrence) {
                final Recurrence recurrence = (Recurrence)o;
                return this.iSaveMillis == recurrence.iSaveMillis && this.iNameKey.equals(recurrence.iNameKey) && this.iOfYear.equals(recurrence.iOfYear);
            }
            return false;
        }
        
        public void writeTo(final DataOutput dataOutput) throws IOException {
            this.iOfYear.writeTo(dataOutput);
            dataOutput.writeUTF(this.iNameKey);
            DateTimeZoneBuilder.writeMillis(dataOutput, this.iSaveMillis);
        }
        
        Recurrence rename(final String s) {
            return new Recurrence(this.iOfYear, s, this.iSaveMillis);
        }
        
        Recurrence renameAppend(final String str) {
            return this.rename((this.iNameKey + str).intern());
        }
    }
    
    private static final class Rule
    {
        final Recurrence iRecurrence;
        final int iFromYear;
        final int iToYear;
        
        Rule(final Recurrence iRecurrence, final int iFromYear, final int iToYear) {
            this.iRecurrence = iRecurrence;
            this.iFromYear = iFromYear;
            this.iToYear = iToYear;
        }
        
        public int getFromYear() {
            return this.iFromYear;
        }
        
        public int getToYear() {
            return this.iToYear;
        }
        
        public OfYear getOfYear() {
            return this.iRecurrence.getOfYear();
        }
        
        public String getNameKey() {
            return this.iRecurrence.getNameKey();
        }
        
        public int getSaveMillis() {
            return this.iRecurrence.getSaveMillis();
        }
        
        public long next(final long n, final int n2, final int n3) {
            final ISOChronology instanceUTC = ISOChronology.getInstanceUTC();
            final int n4 = n2 + n3;
            long n5 = n;
            int value;
            if (n == Long.MIN_VALUE) {
                value = Integer.MIN_VALUE;
            }
            else {
                value = instanceUTC.year().get(n + n4);
            }
            if (value < this.iFromYear) {
                n5 = instanceUTC.year().set(0L, this.iFromYear) - n4 - 1L;
            }
            long next = this.iRecurrence.next(n5, n2, n3);
            if (next > n && instanceUTC.year().get(next + n4) > this.iToYear) {
                next = n;
            }
            return next;
        }
    }
    
    private static final class Transition
    {
        private final long iMillis;
        private final String iNameKey;
        private final int iWallOffset;
        private final int iStandardOffset;
        
        Transition(final long iMillis, final Transition transition) {
            this.iMillis = iMillis;
            this.iNameKey = transition.iNameKey;
            this.iWallOffset = transition.iWallOffset;
            this.iStandardOffset = transition.iStandardOffset;
        }
        
        Transition(final long iMillis, final Rule rule, final int iStandardOffset) {
            this.iMillis = iMillis;
            this.iNameKey = rule.getNameKey();
            this.iWallOffset = iStandardOffset + rule.getSaveMillis();
            this.iStandardOffset = iStandardOffset;
        }
        
        Transition(final long iMillis, final String iNameKey, final int iWallOffset, final int iStandardOffset) {
            this.iMillis = iMillis;
            this.iNameKey = iNameKey;
            this.iWallOffset = iWallOffset;
            this.iStandardOffset = iStandardOffset;
        }
        
        public long getMillis() {
            return this.iMillis;
        }
        
        public String getNameKey() {
            return this.iNameKey;
        }
        
        public int getWallOffset() {
            return this.iWallOffset;
        }
        
        public int getStandardOffset() {
            return this.iStandardOffset;
        }
        
        public int getSaveMillis() {
            return this.iWallOffset - this.iStandardOffset;
        }
        
        public boolean isTransitionFrom(final Transition transition) {
            return transition == null || (this.iMillis > transition.iMillis && (this.iWallOffset != transition.iWallOffset || !this.iNameKey.equals(transition.iNameKey)));
        }
    }
    
    private static final class RuleSet
    {
        private static final int YEAR_LIMIT;
        private int iStandardOffset;
        private ArrayList<Rule> iRules;
        private String iInitialNameKey;
        private int iInitialSaveMillis;
        private int iUpperYear;
        private OfYear iUpperOfYear;
        
        RuleSet() {
            this.iRules = new ArrayList<Rule>(10);
            this.iUpperYear = Integer.MAX_VALUE;
        }
        
        RuleSet(final RuleSet set) {
            this.iStandardOffset = set.iStandardOffset;
            this.iRules = new ArrayList<Rule>(set.iRules);
            this.iInitialNameKey = set.iInitialNameKey;
            this.iInitialSaveMillis = set.iInitialSaveMillis;
            this.iUpperYear = set.iUpperYear;
            this.iUpperOfYear = set.iUpperOfYear;
        }
        
        public int getStandardOffset() {
            return this.iStandardOffset;
        }
        
        public void setStandardOffset(final int iStandardOffset) {
            this.iStandardOffset = iStandardOffset;
        }
        
        public void setFixedSavings(final String iInitialNameKey, final int iInitialSaveMillis) {
            this.iInitialNameKey = iInitialNameKey;
            this.iInitialSaveMillis = iInitialSaveMillis;
        }
        
        public void addRule(final Rule rule) {
            if (!this.iRules.contains(rule)) {
                this.iRules.add(rule);
            }
        }
        
        public void setUpperLimit(final int iUpperYear, final OfYear iUpperOfYear) {
            this.iUpperYear = iUpperYear;
            this.iUpperOfYear = iUpperOfYear;
        }
        
        public Transition firstTransition(final long n) {
            if (this.iInitialNameKey != null) {
                return new Transition(n, this.iInitialNameKey, this.iStandardOffset + this.iInitialSaveMillis, this.iStandardOffset);
            }
            final ArrayList<Rule> iRules = new ArrayList<Rule>(this.iRules);
            long millis = Long.MIN_VALUE;
            int saveMillis = 0;
            Transition transition = null;
            Transition nextTransition;
            while ((nextTransition = this.nextTransition(millis, saveMillis)) != null) {
                millis = nextTransition.getMillis();
                if (millis == n) {
                    transition = new Transition(n, nextTransition);
                    break;
                }
                if (millis > n) {
                    if (transition == null) {
                        for (final Rule rule : iRules) {
                            if (rule.getSaveMillis() == 0) {
                                transition = new Transition(n, rule, this.iStandardOffset);
                                break;
                            }
                        }
                    }
                    if (transition == null) {
                        transition = new Transition(n, nextTransition.getNameKey(), this.iStandardOffset, this.iStandardOffset);
                        break;
                    }
                    break;
                }
                else {
                    transition = new Transition(n, nextTransition);
                    saveMillis = nextTransition.getSaveMillis();
                }
            }
            this.iRules = iRules;
            return transition;
        }
        
        public Transition nextTransition(final long n, final int n2) {
            final ISOChronology instanceUTC = ISOChronology.getInstanceUTC();
            Rule rule = null;
            long n3 = Long.MAX_VALUE;
            final Iterator<Rule> iterator = this.iRules.iterator();
            while (iterator.hasNext()) {
                final Rule rule2 = iterator.next();
                final long next = rule2.next(n, this.iStandardOffset, n2);
                if (next <= n) {
                    iterator.remove();
                }
                else {
                    if (next > n3) {
                        continue;
                    }
                    rule = rule2;
                    n3 = next;
                }
            }
            if (rule == null) {
                return null;
            }
            if (instanceUTC.year().get(n3) >= RuleSet.YEAR_LIMIT) {
                return null;
            }
            if (this.iUpperYear < Integer.MAX_VALUE && n3 >= this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, n2)) {
                return null;
            }
            return new Transition(n3, rule, this.iStandardOffset);
        }
        
        public long getUpperLimit(final int n) {
            if (this.iUpperYear == Integer.MAX_VALUE) {
                return Long.MAX_VALUE;
            }
            return this.iUpperOfYear.setInstant(this.iUpperYear, this.iStandardOffset, n);
        }
        
        public DSTZone buildTailZone(final String s) {
            if (this.iRules.size() == 2) {
                final Rule rule = this.iRules.get(0);
                final Rule rule2 = this.iRules.get(1);
                if (rule.getToYear() == Integer.MAX_VALUE && rule2.getToYear() == Integer.MAX_VALUE) {
                    return new DSTZone(s, this.iStandardOffset, rule.iRecurrence, rule2.iRecurrence);
                }
            }
            return null;
        }
        
        static {
            YEAR_LIMIT = ISOChronology.getInstanceUTC().year().get(DateTimeUtils.currentTimeMillis()) + 100;
        }
    }
    
    private static final class DSTZone extends DateTimeZone
    {
        private static final long serialVersionUID = 6941492635554961361L;
        final int iStandardOffset;
        final Recurrence iStartRecurrence;
        final Recurrence iEndRecurrence;
        
        static DSTZone readFrom(final DataInput dataInput, final String s) throws IOException {
            return new DSTZone(s, (int)DateTimeZoneBuilder.readMillis(dataInput), Recurrence.readFrom(dataInput), Recurrence.readFrom(dataInput));
        }
        
        DSTZone(final String s, final int iStandardOffset, final Recurrence iStartRecurrence, final Recurrence iEndRecurrence) {
            super(s);
            this.iStandardOffset = iStandardOffset;
            this.iStartRecurrence = iStartRecurrence;
            this.iEndRecurrence = iEndRecurrence;
        }
        
        @Override
        public String getNameKey(final long n) {
            return this.findMatchingRecurrence(n).getNameKey();
        }
        
        @Override
        public int getOffset(final long n) {
            return this.iStandardOffset + this.findMatchingRecurrence(n).getSaveMillis();
        }
        
        @Override
        public int getStandardOffset(final long n) {
            return this.iStandardOffset;
        }
        
        @Override
        public boolean isFixed() {
            return false;
        }
        
        @Override
        public long nextTransition(final long n) {
            final int iStandardOffset = this.iStandardOffset;
            final Recurrence iStartRecurrence = this.iStartRecurrence;
            final Recurrence iEndRecurrence = this.iEndRecurrence;
            long next;
            try {
                next = iStartRecurrence.next(n, iStandardOffset, iEndRecurrence.getSaveMillis());
                if (n > 0L && next < 0L) {
                    next = n;
                }
            }
            catch (IllegalArgumentException ex) {
                next = n;
            }
            catch (ArithmeticException ex2) {
                next = n;
            }
            long next2;
            try {
                next2 = iEndRecurrence.next(n, iStandardOffset, iStartRecurrence.getSaveMillis());
                if (n > 0L && next2 < 0L) {
                    next2 = n;
                }
            }
            catch (IllegalArgumentException ex3) {
                next2 = n;
            }
            catch (ArithmeticException ex4) {
                next2 = n;
            }
            return (next > next2) ? next2 : next;
        }
        
        @Override
        public long previousTransition(long n) {
            ++n;
            final int iStandardOffset = this.iStandardOffset;
            final Recurrence iStartRecurrence = this.iStartRecurrence;
            final Recurrence iEndRecurrence = this.iEndRecurrence;
            long previous;
            try {
                previous = iStartRecurrence.previous(n, iStandardOffset, iEndRecurrence.getSaveMillis());
                if (n < 0L && previous > 0L) {
                    previous = n;
                }
            }
            catch (IllegalArgumentException ex) {
                previous = n;
            }
            catch (ArithmeticException ex2) {
                previous = n;
            }
            long previous2;
            try {
                previous2 = iEndRecurrence.previous(n, iStandardOffset, iStartRecurrence.getSaveMillis());
                if (n < 0L && previous2 > 0L) {
                    previous2 = n;
                }
            }
            catch (IllegalArgumentException ex3) {
                previous2 = n;
            }
            catch (ArithmeticException ex4) {
                previous2 = n;
            }
            return ((previous > previous2) ? previous : previous2) - 1L;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof DSTZone) {
                final DSTZone dstZone = (DSTZone)o;
                return this.getID().equals(dstZone.getID()) && this.iStandardOffset == dstZone.iStandardOffset && this.iStartRecurrence.equals(dstZone.iStartRecurrence) && this.iEndRecurrence.equals(dstZone.iEndRecurrence);
            }
            return false;
        }
        
        public void writeTo(final DataOutput dataOutput) throws IOException {
            DateTimeZoneBuilder.writeMillis(dataOutput, this.iStandardOffset);
            this.iStartRecurrence.writeTo(dataOutput);
            this.iEndRecurrence.writeTo(dataOutput);
        }
        
        private Recurrence findMatchingRecurrence(final long n) {
            final int iStandardOffset = this.iStandardOffset;
            final Recurrence iStartRecurrence = this.iStartRecurrence;
            final Recurrence iEndRecurrence = this.iEndRecurrence;
            long next;
            try {
                next = iStartRecurrence.next(n, iStandardOffset, iEndRecurrence.getSaveMillis());
            }
            catch (IllegalArgumentException ex) {
                next = n;
            }
            catch (ArithmeticException ex2) {
                next = n;
            }
            long next2;
            try {
                next2 = iEndRecurrence.next(n, iStandardOffset, iStartRecurrence.getSaveMillis());
            }
            catch (IllegalArgumentException ex3) {
                next2 = n;
            }
            catch (ArithmeticException ex4) {
                next2 = n;
            }
            return (next > next2) ? iStartRecurrence : iEndRecurrence;
        }
    }
    
    private static final class PrecalculatedZone extends DateTimeZone
    {
        private static final long serialVersionUID = 7811976468055766265L;
        private final long[] iTransitions;
        private final int[] iWallOffsets;
        private final int[] iStandardOffsets;
        private final String[] iNameKeys;
        private final DSTZone iTailZone;
        
        static PrecalculatedZone readFrom(final DataInput dataInput, final String s) throws IOException {
            final int unsignedShort = dataInput.readUnsignedShort();
            final String[] array = new String[unsignedShort];
            for (int i = 0; i < unsignedShort; ++i) {
                array[i] = dataInput.readUTF();
            }
            final int int1 = dataInput.readInt();
            final long[] array2 = new long[int1];
            final int[] array3 = new int[int1];
            final int[] array4 = new int[int1];
            final String[] array5 = new String[int1];
            for (int j = 0; j < int1; ++j) {
                array2[j] = DateTimeZoneBuilder.readMillis(dataInput);
                array3[j] = (int)DateTimeZoneBuilder.readMillis(dataInput);
                array4[j] = (int)DateTimeZoneBuilder.readMillis(dataInput);
                try {
                    int n;
                    if (unsignedShort < 256) {
                        n = dataInput.readUnsignedByte();
                    }
                    else {
                        n = dataInput.readUnsignedShort();
                    }
                    array5[j] = array[n];
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    throw new IOException("Invalid encoding");
                }
            }
            DSTZone from = null;
            if (dataInput.readBoolean()) {
                from = DSTZone.readFrom(dataInput, s);
            }
            return new PrecalculatedZone(s, array2, array3, array4, array5, from);
        }
        
        static PrecalculatedZone create(final String s, final boolean b, final ArrayList<Transition> list, DSTZone dstZone) {
            final int size = list.size();
            if (size == 0) {
                throw new IllegalArgumentException();
            }
            final long[] array = new long[size];
            final int[] array2 = new int[size];
            final int[] array3 = new int[size];
            final String[] array4 = new String[size];
            Transition transition = null;
            for (int i = 0; i < size; ++i) {
                final Transition transition2 = list.get(i);
                if (!transition2.isTransitionFrom(transition)) {
                    throw new IllegalArgumentException(s);
                }
                array[i] = transition2.getMillis();
                array2[i] = transition2.getWallOffset();
                array3[i] = transition2.getStandardOffset();
                array4[i] = transition2.getNameKey();
                transition = transition2;
            }
            String[] array5 = new String[5];
            final String[][] zoneStrings = new DateFormatSymbols(Locale.ENGLISH).getZoneStrings();
            for (int j = 0; j < zoneStrings.length; ++j) {
                final String[] array6 = zoneStrings[j];
                if (array6 != null && array6.length == 5 && s.equals(array6[0])) {
                    array5 = array6;
                }
            }
            final ISOChronology instanceUTC = ISOChronology.getInstanceUTC();
            for (int k = 0; k < array4.length - 1; ++k) {
                final String str = array4[k];
                final String str2 = array4[k + 1];
                final long n = array2[k];
                final long n2 = array2[k + 1];
                final long n3 = array3[k];
                final long n4 = array3[k + 1];
                final Period period = new Period(array[k], array[k + 1], PeriodType.yearMonthDay(), instanceUTC);
                if (n != n2 && n3 == n4 && str.equals(str2) && period.getYears() == 0 && period.getMonths() > 4 && period.getMonths() < 8 && str.equals(array5[2]) && str.equals(array5[4])) {
                    if (ZoneInfoCompiler.verbose()) {
                        System.out.println("Fixing duplicate name key - " + str2);
                        System.out.println("     - " + new DateTime(array[k], instanceUTC) + " - " + new DateTime(array[k + 1], instanceUTC));
                    }
                    if (n > n2) {
                        array4[k] = (str + "-Summer").intern();
                    }
                    else if (n < n2) {
                        array4[k + 1] = (str2 + "-Summer").intern();
                        ++k;
                    }
                }
            }
            if (dstZone != null && dstZone.iStartRecurrence.getNameKey().equals(dstZone.iEndRecurrence.getNameKey())) {
                if (ZoneInfoCompiler.verbose()) {
                    System.out.println("Fixing duplicate recurrent name key - " + dstZone.iStartRecurrence.getNameKey());
                }
                if (dstZone.iStartRecurrence.getSaveMillis() > 0) {
                    dstZone = new DSTZone(dstZone.getID(), dstZone.iStandardOffset, dstZone.iStartRecurrence.renameAppend("-Summer"), dstZone.iEndRecurrence);
                }
                else {
                    dstZone = new DSTZone(dstZone.getID(), dstZone.iStandardOffset, dstZone.iStartRecurrence, dstZone.iEndRecurrence.renameAppend("-Summer"));
                }
            }
            return new PrecalculatedZone(b ? s : "", array, array2, array3, array4, dstZone);
        }
        
        private PrecalculatedZone(final String s, final long[] iTransitions, final int[] iWallOffsets, final int[] iStandardOffsets, final String[] iNameKeys, final DSTZone iTailZone) {
            super(s);
            this.iTransitions = iTransitions;
            this.iWallOffsets = iWallOffsets;
            this.iStandardOffsets = iStandardOffsets;
            this.iNameKeys = iNameKeys;
            this.iTailZone = iTailZone;
        }
        
        @Override
        public String getNameKey(final long key) {
            final long[] iTransitions = this.iTransitions;
            final int binarySearch = Arrays.binarySearch(iTransitions, key);
            if (binarySearch >= 0) {
                return this.iNameKeys[binarySearch];
            }
            final int n = ~binarySearch;
            if (n < iTransitions.length) {
                if (n > 0) {
                    return this.iNameKeys[n - 1];
                }
                return "UTC";
            }
            else {
                if (this.iTailZone == null) {
                    return this.iNameKeys[n - 1];
                }
                return this.iTailZone.getNameKey(key);
            }
        }
        
        @Override
        public int getOffset(final long key) {
            final long[] iTransitions = this.iTransitions;
            final int binarySearch = Arrays.binarySearch(iTransitions, key);
            if (binarySearch >= 0) {
                return this.iWallOffsets[binarySearch];
            }
            final int n = ~binarySearch;
            if (n < iTransitions.length) {
                if (n > 0) {
                    return this.iWallOffsets[n - 1];
                }
                return 0;
            }
            else {
                if (this.iTailZone == null) {
                    return this.iWallOffsets[n - 1];
                }
                return this.iTailZone.getOffset(key);
            }
        }
        
        @Override
        public int getStandardOffset(final long key) {
            final long[] iTransitions = this.iTransitions;
            final int binarySearch = Arrays.binarySearch(iTransitions, key);
            if (binarySearch >= 0) {
                return this.iStandardOffsets[binarySearch];
            }
            final int n = ~binarySearch;
            if (n < iTransitions.length) {
                if (n > 0) {
                    return this.iStandardOffsets[n - 1];
                }
                return 0;
            }
            else {
                if (this.iTailZone == null) {
                    return this.iStandardOffsets[n - 1];
                }
                return this.iTailZone.getStandardOffset(key);
            }
        }
        
        @Override
        public boolean isFixed() {
            return false;
        }
        
        @Override
        public long nextTransition(long key) {
            final long[] iTransitions = this.iTransitions;
            final int binarySearch = Arrays.binarySearch(iTransitions, key);
            final int n = (binarySearch >= 0) ? (binarySearch + 1) : (~binarySearch);
            if (n < iTransitions.length) {
                return iTransitions[n];
            }
            if (this.iTailZone == null) {
                return key;
            }
            final long n2 = iTransitions[iTransitions.length - 1];
            if (key < n2) {
                key = n2;
            }
            return this.iTailZone.nextTransition(key);
        }
        
        @Override
        public long previousTransition(final long key) {
            final long[] iTransitions = this.iTransitions;
            final int binarySearch = Arrays.binarySearch(iTransitions, key);
            if (binarySearch >= 0) {
                if (key > Long.MIN_VALUE) {
                    return key - 1L;
                }
                return key;
            }
            else {
                final int n = ~binarySearch;
                if (n < iTransitions.length) {
                    if (n > 0) {
                        final long n2 = iTransitions[n - 1];
                        if (n2 > Long.MIN_VALUE) {
                            return n2 - 1L;
                        }
                    }
                    return key;
                }
                if (this.iTailZone != null) {
                    final long previousTransition = this.iTailZone.previousTransition(key);
                    if (previousTransition < key) {
                        return previousTransition;
                    }
                }
                final long n3 = iTransitions[n - 1];
                if (n3 > Long.MIN_VALUE) {
                    return n3 - 1L;
                }
                return key;
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o instanceof PrecalculatedZone) {
                final PrecalculatedZone precalculatedZone = (PrecalculatedZone)o;
                return this.getID().equals(precalculatedZone.getID()) && Arrays.equals(this.iTransitions, precalculatedZone.iTransitions) && Arrays.equals(this.iNameKeys, precalculatedZone.iNameKeys) && Arrays.equals(this.iWallOffsets, precalculatedZone.iWallOffsets) && Arrays.equals(this.iStandardOffsets, precalculatedZone.iStandardOffsets) && ((this.iTailZone != null) ? this.iTailZone.equals(precalculatedZone.iTailZone) : (null == precalculatedZone.iTailZone));
            }
            return false;
        }
        
        public void writeTo(final DataOutput dataOutput) throws IOException {
            final int length = this.iTransitions.length;
            final HashSet<String> set = new HashSet<String>();
            for (int i = 0; i < length; ++i) {
                set.add(this.iNameKeys[i]);
            }
            final int size = set.size();
            if (size > 65535) {
                throw new UnsupportedOperationException("String pool is too large");
            }
            final String[] array = new String[size];
            final Iterator<Object> iterator = set.iterator();
            int n = 0;
            while (iterator.hasNext()) {
                array[n] = iterator.next();
                ++n;
            }
            dataOutput.writeShort(size);
            for (int j = 0; j < size; ++j) {
                dataOutput.writeUTF(array[j]);
            }
            dataOutput.writeInt(length);
            for (int k = 0; k < length; ++k) {
                DateTimeZoneBuilder.writeMillis(dataOutput, this.iTransitions[k]);
                DateTimeZoneBuilder.writeMillis(dataOutput, this.iWallOffsets[k]);
                DateTimeZoneBuilder.writeMillis(dataOutput, this.iStandardOffsets[k]);
                final String anObject = this.iNameKeys[k];
                int l = 0;
                while (l < size) {
                    if (array[l].equals(anObject)) {
                        if (size < 256) {
                            dataOutput.writeByte(l);
                            break;
                        }
                        dataOutput.writeShort(l);
                        break;
                    }
                    else {
                        ++l;
                    }
                }
            }
            dataOutput.writeBoolean(this.iTailZone != null);
            if (this.iTailZone != null) {
                this.iTailZone.writeTo(dataOutput);
            }
        }
        
        public boolean isCachable() {
            if (this.iTailZone != null) {
                return true;
            }
            final long[] iTransitions = this.iTransitions;
            if (iTransitions.length <= 1) {
                return false;
            }
            double n = 0.0;
            int n2 = 0;
            for (int i = 1; i < iTransitions.length; ++i) {
                final long n3 = iTransitions[i] - iTransitions[i - 1];
                if (n3 < 63158400000L) {
                    n += n3;
                    ++n2;
                }
            }
            return n2 > 0 && n / n2 / 8.64E7 >= 25.0;
        }
    }
}
