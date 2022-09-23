// 
// Decompiled by Procyon v0.5.36
// 

package org.joda.time.tz;

import org.joda.time.LocalDate;
import java.util.StringTokenizer;
import java.util.Comparator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.joda.time.DateTime;
import java.util.ArrayList;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.ReadWritableInstant;
import org.joda.time.MutableDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeField;
import java.util.Locale;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.HashMap;
import org.joda.time.DateTimeZone;
import java.io.DataOutputStream;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.chrono.ISOChronology;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.joda.time.Chronology;

public class ZoneInfoCompiler
{
    static DateTimeOfYear cStartOfYear;
    static Chronology cLenientISO;
    static ThreadLocal<Boolean> cVerbose;
    private Map<String, RuleSet> iRuleSets;
    private List<Zone> iZones;
    private List<String> iLinks;
    
    public static boolean verbose() {
        return ZoneInfoCompiler.cVerbose.get();
    }
    
    public static void main(final String[] array) throws Exception {
        if (array.length == 0) {
            printUsage();
            return;
        }
        File parent = null;
        File file = null;
        boolean b = false;
        int i;
        for (i = 0; i < array.length; ++i) {
            try {
                if ("-src".equals(array[i])) {
                    parent = new File(array[++i]);
                }
                else if ("-dst".equals(array[i])) {
                    file = new File(array[++i]);
                }
                else if ("-verbose".equals(array[i])) {
                    b = true;
                }
                else {
                    if ("-?".equals(array[i])) {
                        printUsage();
                        return;
                    }
                    break;
                }
            }
            catch (IndexOutOfBoundsException ex) {
                printUsage();
                return;
            }
        }
        if (i >= array.length) {
            printUsage();
            return;
        }
        final File[] array2 = new File[array.length - i];
        for (int n = 0; i < array.length; ++i, ++n) {
            array2[n] = ((parent == null) ? new File(array[i]) : new File(parent, array[i]));
        }
        ZoneInfoCompiler.cVerbose.set(b);
        new ZoneInfoCompiler().compile(file, array2);
    }
    
    private static void printUsage() {
        System.out.println("Usage: java org.joda.time.tz.ZoneInfoCompiler <options> <source files>");
        System.out.println("where possible options include:");
        System.out.println("  -src <directory>    Specify where to read source files");
        System.out.println("  -dst <directory>    Specify where to write generated files");
        System.out.println("  -verbose            Output verbosely (default false)");
    }
    
    static DateTimeOfYear getStartOfYear() {
        if (ZoneInfoCompiler.cStartOfYear == null) {
            ZoneInfoCompiler.cStartOfYear = new DateTimeOfYear();
        }
        return ZoneInfoCompiler.cStartOfYear;
    }
    
    static Chronology getLenientISOChronology() {
        if (ZoneInfoCompiler.cLenientISO == null) {
            ZoneInfoCompiler.cLenientISO = LenientChronology.getInstance(ISOChronology.getInstanceUTC());
        }
        return ZoneInfoCompiler.cLenientISO;
    }
    
    static void writeZoneInfoMap(final DataOutputStream dataOutputStream, final Map<String, DateTimeZone> map) throws IOException {
        final HashMap<Object, Object> hashMap = new HashMap<Object, Object>(map.size());
        final TreeMap<Short, String> treeMap = new TreeMap<Short, String>();
        short n = 0;
        for (final Map.Entry<String, DateTimeZone> entry : map.entrySet()) {
            final String value = entry.getKey();
            if (!hashMap.containsKey(value)) {
                final Short value2 = n;
                hashMap.put(value, value2);
                treeMap.put(value2, value);
                ++n;
                if (n == 0) {
                    throw new InternalError("Too many time zone ids");
                }
            }
            final String id = entry.getValue().getID();
            if (!hashMap.containsKey(id)) {
                final Short value3 = n;
                hashMap.put(id, value3);
                treeMap.put(value3, id);
                ++n;
                if (n == 0) {
                    throw new InternalError("Too many time zone ids");
                }
                continue;
            }
        }
        dataOutputStream.writeShort(treeMap.size());
        final Iterator<String> iterator2 = treeMap.values().iterator();
        while (iterator2.hasNext()) {
            dataOutputStream.writeUTF(iterator2.next());
        }
        dataOutputStream.writeShort(map.size());
        for (final Map.Entry<String, DateTimeZone> entry2 : map.entrySet()) {
            dataOutputStream.writeShort(hashMap.get(entry2.getKey()));
            dataOutputStream.writeShort(hashMap.get(entry2.getValue().getID()));
        }
    }
    
    static int parseYear(String lowerCase, final int n) {
        lowerCase = lowerCase.toLowerCase();
        if (lowerCase.equals("minimum") || lowerCase.equals("min")) {
            return Integer.MIN_VALUE;
        }
        if (lowerCase.equals("maximum") || lowerCase.equals("max")) {
            return Integer.MAX_VALUE;
        }
        if (lowerCase.equals("only")) {
            return n;
        }
        return Integer.parseInt(lowerCase);
    }
    
    static int parseMonth(final String s) {
        final DateTimeField monthOfYear = ISOChronology.getInstanceUTC().monthOfYear();
        return monthOfYear.get(monthOfYear.set(0L, s, Locale.ENGLISH));
    }
    
    static int parseDayOfWeek(final String s) {
        final DateTimeField dayOfWeek = ISOChronology.getInstanceUTC().dayOfWeek();
        return dayOfWeek.get(dayOfWeek.set(0L, s, Locale.ENGLISH));
    }
    
    static String parseOptional(final String s) {
        return s.equals("-") ? null : s;
    }
    
    static int parseTime(final String s) {
        final DateTimeFormatter hourMinuteSecondFraction = ISODateTimeFormat.hourMinuteSecondFraction();
        final MutableDateTime mutableDateTime = new MutableDateTime(0L, getLenientISOChronology());
        int n = 0;
        if (s.startsWith("-")) {
            n = 1;
        }
        if (hourMinuteSecondFraction.parseInto(mutableDateTime, s, n) == ~n) {
            throw new IllegalArgumentException(s);
        }
        int n2 = (int)mutableDateTime.getMillis();
        if (n == 1) {
            n2 = -n2;
        }
        return n2;
    }
    
    static char parseZoneChar(final char c) {
        switch (c) {
            case 'S':
            case 's': {
                return 's';
            }
            case 'G':
            case 'U':
            case 'Z':
            case 'g':
            case 'u':
            case 'z': {
                return 'u';
            }
            default: {
                return 'w';
            }
        }
    }
    
    static boolean test(final String s, final DateTimeZone dateTimeZone) {
        if (!s.equals(dateTimeZone.getID())) {
            return true;
        }
        long set = ISOChronology.getInstanceUTC().year().set(0L, 1850);
        final long set2 = ISOChronology.getInstanceUTC().year().set(0L, 2050);
        int offset = dateTimeZone.getOffset(set);
        String nameKey = dateTimeZone.getNameKey(set);
        final ArrayList<Object> list = new ArrayList<Object>();
        while (true) {
            final long nextTransition = dateTimeZone.nextTransition(set);
            if (nextTransition == set || nextTransition > set2) {
                long set3 = ISOChronology.getInstanceUTC().year().set(0L, 2050);
                final long set4 = ISOChronology.getInstanceUTC().year().set(0L, 1850);
                int size = list.size();
                while (--size >= 0) {
                    final long previousTransition = dateTimeZone.previousTransition(set3);
                    if (previousTransition == set3) {
                        break;
                    }
                    if (previousTransition < set4) {
                        break;
                    }
                    set3 = previousTransition;
                    final long longValue = list.get(size);
                    if (longValue - 1L != set3) {
                        System.out.println("*r* Error in " + dateTimeZone.getID() + " " + new DateTime(set3, ISOChronology.getInstanceUTC()) + " != " + new DateTime(longValue - 1L, ISOChronology.getInstanceUTC()));
                        return false;
                    }
                }
                return true;
            }
            set = nextTransition;
            final int offset2 = dateTimeZone.getOffset(set);
            final String nameKey2 = dateTimeZone.getNameKey(set);
            if (offset == offset2 && nameKey.equals(nameKey2)) {
                System.out.println("*d* Error in " + dateTimeZone.getID() + " " + new DateTime(set, ISOChronology.getInstanceUTC()));
                return false;
            }
            if (nameKey2 == null || (nameKey2.length() < 3 && !"??".equals(nameKey2))) {
                System.out.println("*s* Error in " + dateTimeZone.getID() + " " + new DateTime(set, ISOChronology.getInstanceUTC()) + ", nameKey=" + nameKey2);
                return false;
            }
            list.add(set);
            offset = offset2;
            nameKey = nameKey2;
        }
    }
    
    public ZoneInfoCompiler() {
        this.iRuleSets = new HashMap<String, RuleSet>();
        this.iZones = new ArrayList<Zone>();
        this.iLinks = new ArrayList<String>();
    }
    
    public Map<String, DateTimeZone> compile(final File file, final File[] array) throws IOException {
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                final BufferedReader bufferedReader = new BufferedReader(new FileReader(array[i]));
                this.parseDataFile(bufferedReader);
                bufferedReader.close();
            }
        }
        if (file != null) {
            if (!file.exists() && !file.mkdirs()) {
                throw new IOException("Destination directory doesn't exist and cannot be created: " + file);
            }
            if (!file.isDirectory()) {
                throw new IOException("Destination is not a directory: " + file);
            }
        }
        final TreeMap<String, DateTimeZone> treeMap = (TreeMap<String, DateTimeZone>)new TreeMap<Object, DateTimeZone>();
        System.out.println("Writing zoneinfo files");
        for (int j = 0; j < this.iZones.size(); ++j) {
            final Zone zone = this.iZones.get(j);
            final DateTimeZoneBuilder dateTimeZoneBuilder = new DateTimeZoneBuilder();
            zone.addToBuilder(dateTimeZoneBuilder, this.iRuleSets);
            final DateTimeZone dateTimeZone2;
            final DateTimeZone dateTimeZone = dateTimeZone2 = dateTimeZoneBuilder.toDateTimeZone(zone.iName, (boolean)(1 != 0));
            if (test(dateTimeZone2.getID(), dateTimeZone2)) {
                treeMap.put(dateTimeZone2.getID(), dateTimeZone2);
                if (file != null) {
                    if (verbose()) {
                        System.out.println("Writing " + dateTimeZone2.getID());
                    }
                    final File file2 = new File(file, dateTimeZone2.getID());
                    if (!file2.getParentFile().exists()) {
                        file2.getParentFile().mkdirs();
                    }
                    final FileOutputStream fileOutputStream = new FileOutputStream(file2);
                    try {
                        dateTimeZoneBuilder.writeTo(zone.iName, fileOutputStream);
                    }
                    finally {
                        fileOutputStream.close();
                    }
                    final FileInputStream fileInputStream = new FileInputStream(file2);
                    final DateTimeZone from = DateTimeZoneBuilder.readFrom(fileInputStream, dateTimeZone2.getID());
                    fileInputStream.close();
                    if (!dateTimeZone.equals(from)) {
                        System.out.println("*e* Error in " + dateTimeZone2.getID() + ": Didn't read properly from file");
                    }
                }
            }
        }
        for (int k = 0; k < 2; ++k) {
            for (int l = 0; l < this.iLinks.size(); l += 2) {
                final String str = this.iLinks.get(l);
                final String str2 = this.iLinks.get(l + 1);
                final DateTimeZone dateTimeZone3 = treeMap.get(str);
                if (dateTimeZone3 == null) {
                    if (k > 0) {
                        System.out.println("Cannot find time zone '" + str + "' to link alias '" + str2 + "' to");
                    }
                }
                else {
                    treeMap.put(str2, dateTimeZone3);
                }
            }
        }
        if (file != null) {
            System.out.println("Writing ZoneInfoMap");
            final File file3 = new File(file, "ZoneInfoMap");
            if (!file3.getParentFile().exists()) {
                file3.getParentFile().mkdirs();
            }
            final DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file3));
            try {
                final TreeMap<String, DateTimeZone> treeMap2 = new TreeMap<String, DateTimeZone>(String.CASE_INSENSITIVE_ORDER);
                treeMap2.putAll((Map<?, ?>)treeMap);
                writeZoneInfoMap(dataOutputStream, treeMap2);
            }
            finally {
                dataOutputStream.close();
            }
        }
        return treeMap;
    }
    
    public void parseDataFile(final BufferedReader bufferedReader) throws IOException {
        Zone zone = null;
        String s;
        while ((s = bufferedReader.readLine()) != null) {
            final String trim = s.trim();
            if (trim.length() != 0) {
                if (trim.charAt(0) == '#') {
                    continue;
                }
                final int index = s.indexOf(35);
                if (index >= 0) {
                    s = s.substring(0, index);
                }
                final StringTokenizer stringTokenizer = new StringTokenizer(s, " \t");
                if (Character.isWhitespace(s.charAt(0)) && stringTokenizer.hasMoreTokens()) {
                    if (zone == null) {
                        continue;
                    }
                    zone.chain(stringTokenizer);
                }
                else {
                    if (zone != null) {
                        this.iZones.add(zone);
                    }
                    zone = null;
                    if (!stringTokenizer.hasMoreTokens()) {
                        continue;
                    }
                    final String nextToken = stringTokenizer.nextToken();
                    if (nextToken.equalsIgnoreCase("Rule")) {
                        final Rule rule = new Rule(stringTokenizer);
                        final RuleSet set = this.iRuleSets.get(rule.iName);
                        if (set == null) {
                            this.iRuleSets.put(rule.iName, new RuleSet(rule));
                        }
                        else {
                            set.addRule(rule);
                        }
                    }
                    else if (nextToken.equalsIgnoreCase("Zone")) {
                        zone = new Zone(stringTokenizer);
                    }
                    else if (nextToken.equalsIgnoreCase("Link")) {
                        this.iLinks.add(stringTokenizer.nextToken());
                        this.iLinks.add(stringTokenizer.nextToken());
                    }
                    else {
                        System.out.println("Unknown line: " + s);
                    }
                }
            }
        }
        if (zone != null) {
            this.iZones.add(zone);
        }
    }
    
    static {
        ZoneInfoCompiler.cVerbose = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return Boolean.FALSE;
            }
        };
    }
    
    static class DateTimeOfYear
    {
        public final int iMonthOfYear;
        public final int iDayOfMonth;
        public final int iDayOfWeek;
        public final boolean iAdvanceDayOfWeek;
        public final int iMillisOfDay;
        public final char iZoneChar;
        
        DateTimeOfYear() {
            this.iMonthOfYear = 1;
            this.iDayOfMonth = 1;
            this.iDayOfWeek = 0;
            this.iAdvanceDayOfWeek = false;
            this.iMillisOfDay = 0;
            this.iZoneChar = 'w';
        }
        
        DateTimeOfYear(final StringTokenizer stringTokenizer) {
            int iMonthOfYear = 1;
            int iDayOfMonth = 1;
            int iDayOfWeek = 0;
            int time = 0;
            boolean iAdvanceDayOfWeek = false;
            char zoneChar = 'w';
            if (stringTokenizer.hasMoreTokens()) {
                iMonthOfYear = ZoneInfoCompiler.parseMonth(stringTokenizer.nextToken());
                if (stringTokenizer.hasMoreTokens()) {
                    final String nextToken = stringTokenizer.nextToken();
                    if (nextToken.startsWith("last")) {
                        iDayOfMonth = -1;
                        iDayOfWeek = ZoneInfoCompiler.parseDayOfWeek(nextToken.substring(4));
                        iAdvanceDayOfWeek = false;
                    }
                    else {
                        try {
                            iDayOfMonth = Integer.parseInt(nextToken);
                            iDayOfWeek = 0;
                            iAdvanceDayOfWeek = false;
                        }
                        catch (NumberFormatException ex) {
                            final int index = nextToken.indexOf(">=");
                            if (index > 0) {
                                iDayOfMonth = Integer.parseInt(nextToken.substring(index + 2));
                                iDayOfWeek = ZoneInfoCompiler.parseDayOfWeek(nextToken.substring(0, index));
                                iAdvanceDayOfWeek = true;
                            }
                            else {
                                final int index2 = nextToken.indexOf("<=");
                                if (index2 <= 0) {
                                    throw new IllegalArgumentException(nextToken);
                                }
                                iDayOfMonth = Integer.parseInt(nextToken.substring(index2 + 2));
                                iDayOfWeek = ZoneInfoCompiler.parseDayOfWeek(nextToken.substring(0, index2));
                                iAdvanceDayOfWeek = false;
                            }
                        }
                    }
                    if (stringTokenizer.hasMoreTokens()) {
                        final String nextToken2 = stringTokenizer.nextToken();
                        zoneChar = ZoneInfoCompiler.parseZoneChar(nextToken2.charAt(nextToken2.length() - 1));
                        if (nextToken2.equals("24:00")) {
                            final LocalDate localDate = (iDayOfMonth == -1) ? new LocalDate(2001, iMonthOfYear, 1).plusMonths(1) : new LocalDate(2001, iMonthOfYear, iDayOfMonth).plusDays(1);
                            iAdvanceDayOfWeek = (iDayOfMonth != -1 && iDayOfWeek != 0);
                            iMonthOfYear = localDate.getMonthOfYear();
                            iDayOfMonth = localDate.getDayOfMonth();
                            if (iDayOfWeek != 0) {
                                iDayOfWeek = (iDayOfWeek - 1 + 1) % 7 + 1;
                            }
                        }
                        else {
                            time = ZoneInfoCompiler.parseTime(nextToken2);
                        }
                    }
                }
            }
            this.iMonthOfYear = iMonthOfYear;
            this.iDayOfMonth = iDayOfMonth;
            this.iDayOfWeek = iDayOfWeek;
            this.iAdvanceDayOfWeek = iAdvanceDayOfWeek;
            this.iMillisOfDay = time;
            this.iZoneChar = zoneChar;
        }
        
        public void addRecurring(final DateTimeZoneBuilder dateTimeZoneBuilder, final String s, final int n, final int n2, final int n3) {
            dateTimeZoneBuilder.addRecurringSavings(s, n, n2, n3, this.iZoneChar, this.iMonthOfYear, this.iDayOfMonth, this.iDayOfWeek, this.iAdvanceDayOfWeek, this.iMillisOfDay);
        }
        
        public void addCutover(final DateTimeZoneBuilder dateTimeZoneBuilder, final int n) {
            dateTimeZoneBuilder.addCutover(n, this.iZoneChar, this.iMonthOfYear, this.iDayOfMonth, this.iDayOfWeek, this.iAdvanceDayOfWeek, this.iMillisOfDay);
        }
        
        @Override
        public String toString() {
            return "MonthOfYear: " + this.iMonthOfYear + "\n" + "DayOfMonth: " + this.iDayOfMonth + "\n" + "DayOfWeek: " + this.iDayOfWeek + "\n" + "AdvanceDayOfWeek: " + this.iAdvanceDayOfWeek + "\n" + "MillisOfDay: " + this.iMillisOfDay + "\n" + "ZoneChar: " + this.iZoneChar + "\n";
        }
    }
    
    private static class Rule
    {
        public final String iName;
        public final int iFromYear;
        public final int iToYear;
        public final String iType;
        public final DateTimeOfYear iDateTimeOfYear;
        public final int iSaveMillis;
        public final String iLetterS;
        
        Rule(final StringTokenizer stringTokenizer) {
            this.iName = stringTokenizer.nextToken().intern();
            this.iFromYear = ZoneInfoCompiler.parseYear(stringTokenizer.nextToken(), 0);
            this.iToYear = ZoneInfoCompiler.parseYear(stringTokenizer.nextToken(), this.iFromYear);
            if (this.iToYear < this.iFromYear) {
                throw new IllegalArgumentException();
            }
            this.iType = ZoneInfoCompiler.parseOptional(stringTokenizer.nextToken());
            this.iDateTimeOfYear = new DateTimeOfYear(stringTokenizer);
            this.iSaveMillis = ZoneInfoCompiler.parseTime(stringTokenizer.nextToken());
            this.iLetterS = ZoneInfoCompiler.parseOptional(stringTokenizer.nextToken());
        }
        
        public void addRecurring(final DateTimeZoneBuilder dateTimeZoneBuilder, final String s) {
            this.iDateTimeOfYear.addRecurring(dateTimeZoneBuilder, this.formatName(s), this.iSaveMillis, this.iFromYear, this.iToYear);
        }
        
        private String formatName(final String s) {
            final int index = s.indexOf(47);
            if (index > 0) {
                if (this.iSaveMillis == 0) {
                    return s.substring(0, index).intern();
                }
                return s.substring(index + 1).intern();
            }
            else {
                final int index2 = s.indexOf("%s");
                if (index2 < 0) {
                    return s;
                }
                final String substring = s.substring(0, index2);
                final String substring2 = s.substring(index2 + 2);
                String s2;
                if (this.iLetterS == null) {
                    s2 = substring.concat(substring2);
                }
                else {
                    s2 = substring + this.iLetterS + substring2;
                }
                return s2.intern();
            }
        }
        
        @Override
        public String toString() {
            return "[Rule]\nName: " + this.iName + "\n" + "FromYear: " + this.iFromYear + "\n" + "ToYear: " + this.iToYear + "\n" + "Type: " + this.iType + "\n" + this.iDateTimeOfYear + "SaveMillis: " + this.iSaveMillis + "\n" + "LetterS: " + this.iLetterS + "\n";
        }
    }
    
    private static class RuleSet
    {
        private List<Rule> iRules;
        
        RuleSet(final Rule rule) {
            (this.iRules = new ArrayList<Rule>()).add(rule);
        }
        
        void addRule(final Rule rule) {
            if (!rule.iName.equals(this.iRules.get(0).iName)) {
                throw new IllegalArgumentException("Rule name mismatch");
            }
            this.iRules.add(rule);
        }
        
        public void addRecurring(final DateTimeZoneBuilder dateTimeZoneBuilder, final String s) {
            for (int i = 0; i < this.iRules.size(); ++i) {
                this.iRules.get(i).addRecurring(dateTimeZoneBuilder, s);
            }
        }
    }
    
    private static class Zone
    {
        public final String iName;
        public final int iOffsetMillis;
        public final String iRules;
        public final String iFormat;
        public final int iUntilYear;
        public final DateTimeOfYear iUntilDateTimeOfYear;
        private Zone iNext;
        
        Zone(final StringTokenizer stringTokenizer) {
            this(stringTokenizer.nextToken(), stringTokenizer);
        }
        
        private Zone(final String s, final StringTokenizer stringTokenizer) {
            this.iName = s.intern();
            this.iOffsetMillis = ZoneInfoCompiler.parseTime(stringTokenizer.nextToken());
            this.iRules = ZoneInfoCompiler.parseOptional(stringTokenizer.nextToken());
            this.iFormat = stringTokenizer.nextToken().intern();
            int int1 = Integer.MAX_VALUE;
            DateTimeOfYear startOfYear = ZoneInfoCompiler.getStartOfYear();
            if (stringTokenizer.hasMoreTokens()) {
                int1 = Integer.parseInt(stringTokenizer.nextToken());
                if (stringTokenizer.hasMoreTokens()) {
                    startOfYear = new DateTimeOfYear(stringTokenizer);
                }
            }
            this.iUntilYear = int1;
            this.iUntilDateTimeOfYear = startOfYear;
        }
        
        void chain(final StringTokenizer stringTokenizer) {
            if (this.iNext != null) {
                this.iNext.chain(stringTokenizer);
            }
            else {
                this.iNext = new Zone(this.iName, stringTokenizer);
            }
        }
        
        public void addToBuilder(final DateTimeZoneBuilder dateTimeZoneBuilder, final Map<String, RuleSet> map) {
            addToBuilder(this, dateTimeZoneBuilder, map);
        }
        
        private static void addToBuilder(Zone iNext, final DateTimeZoneBuilder dateTimeZoneBuilder, final Map<String, RuleSet> map) {
            while (iNext != null) {
                dateTimeZoneBuilder.setStandardOffset(iNext.iOffsetMillis);
                if (iNext.iRules == null) {
                    dateTimeZoneBuilder.setFixedSavings(iNext.iFormat, 0);
                }
                else {
                    try {
                        dateTimeZoneBuilder.setFixedSavings(iNext.iFormat, ZoneInfoCompiler.parseTime(iNext.iRules));
                    }
                    catch (Exception ex) {
                        final RuleSet set = map.get(iNext.iRules);
                        if (set == null) {
                            throw new IllegalArgumentException("Rules not found: " + iNext.iRules);
                        }
                        set.addRecurring(dateTimeZoneBuilder, iNext.iFormat);
                    }
                }
                if (iNext.iUntilYear == Integer.MAX_VALUE) {
                    break;
                }
                iNext.iUntilDateTimeOfYear.addCutover(dateTimeZoneBuilder, iNext.iUntilYear);
                iNext = iNext.iNext;
            }
        }
        
        @Override
        public String toString() {
            final String string = "[Zone]\nName: " + this.iName + "\n" + "OffsetMillis: " + this.iOffsetMillis + "\n" + "Rules: " + this.iRules + "\n" + "Format: " + this.iFormat + "\n" + "UntilYear: " + this.iUntilYear + "\n" + this.iUntilDateTimeOfYear;
            if (this.iNext == null) {
                return string;
            }
            return string + "...\n" + this.iNext.toString();
        }
    }
}
