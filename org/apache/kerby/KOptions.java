// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.Date;
import java.text.DateFormat;
import java.util.regex.Pattern;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class KOptions
{
    private final Map<KOption, KOption> options;
    
    public KOptions() {
        this.options = new HashMap<KOption, KOption>();
    }
    
    public static boolean parseSetValue(final KOptionInfo kopt, final String strValue) {
        final KOptionType kt = kopt.getType();
        if (kt == KOptionType.NOV) {
            return true;
        }
        if (strValue == null || strValue.isEmpty()) {
            return false;
        }
        if (kt == KOptionType.FILE) {
            kopt.setValue(new File(strValue));
        }
        else if (kt == KOptionType.DIR) {
            final File dir = new File(strValue);
            if (!dir.exists()) {
                throw new IllegalArgumentException("Invalid dir:" + strValue);
            }
            kopt.setValue(dir);
        }
        else {
            if (kt == KOptionType.INT) {
                try {
                    final Integer num = Integer.valueOf(strValue);
                    kopt.setValue(num);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Invalid integer:" + strValue);
                }
            }
            if (kt == KOptionType.STR) {
                kopt.setValue(strValue);
            }
            else if (kt == KOptionType.DATE) {
                final DateFormat df = new SimpleDateFormat("dd/MM/yy:HH:mm:ss");
                Date date = null;
                try {
                    date = df.parse(strValue);
                    kopt.setValue(date);
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException("Fail to parse the date: " + strValue);
                }
            }
            else if (kt == KOptionType.DURATION) {
                final Matcher matcherColon = Pattern.compile("\\d+(?::\\d+){0,2}").matcher(strValue);
                final Matcher matcherWord = Pattern.compile("(?:(\\d+)D)?(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?", 2).matcher(strValue);
                if (matcherColon.matches()) {
                    final String[] durations = strValue.split(":");
                    int duration;
                    if (durations.length == 1) {
                        duration = Integer.valueOf(durations[0]);
                    }
                    else if (durations.length == 2) {
                        duration = Integer.valueOf(durations[0]) * 3600 + Integer.valueOf(durations[1]) * 60;
                    }
                    else {
                        duration = Integer.valueOf(durations[0]) * 3600 + Integer.valueOf(durations[1]) * 60;
                        duration += Integer.valueOf(durations[2]);
                    }
                    kopt.setValue(duration);
                }
                else {
                    if (!matcherWord.matches()) {
                        throw new IllegalArgumentException("Text can't be parsed to a Duration: " + strValue);
                    }
                    final int[] durations2 = new int[4];
                    for (int i = 0; i < 4; ++i) {
                        final String durationMatch = matcherWord.group(i + 1);
                        if (durationMatch != null) {
                            durations2[i] = Integer.valueOf(durationMatch);
                        }
                    }
                    final int duration = durations2[0] * 86400 + durations2[1] * 3600 + durations2[2] * 60 + durations2[3];
                    kopt.setValue(duration);
                }
            }
            else {
                if (kt != KOptionType.BOOL) {
                    throw new IllegalArgumentException("Not recognised option:" + strValue);
                }
                kopt.setValue(Boolean.valueOf(strValue));
            }
        }
        return true;
    }
    
    public void add(final KOption option) {
        if (option != null) {
            this.options.put(option, option);
        }
    }
    
    public void add(final KOption option, final Object optionValue) {
        if (option != null) {
            option.getOptionInfo().setValue(optionValue);
            this.add(option);
        }
    }
    
    public boolean contains(final KOption option) {
        return this.options.containsKey(option);
    }
    
    public List<KOption> getOptions() {
        return new ArrayList<KOption>(this.options.keySet());
    }
    
    public KOption getOption(final KOption option) {
        if (!this.options.containsKey(option)) {
            return null;
        }
        return this.options.get(option);
    }
    
    public Object getOptionValue(final KOption option) {
        if (!this.contains(option)) {
            return null;
        }
        return this.options.get(option).getOptionInfo().getValue();
    }
    
    public String getStringOption(final KOption option) {
        final Object value = this.getOptionValue(option);
        if (value instanceof String) {
            return (String)value;
        }
        return null;
    }
    
    public boolean getBooleanOption(final KOption option, final Boolean defaultValue) {
        final Object value = this.getOptionValue(option);
        if (value instanceof String) {
            final String strVal = (String)value;
            if (strVal.equalsIgnoreCase("true") || strVal.equalsIgnoreCase("yes") || strVal.equals("1")) {
                return true;
            }
            if (strVal.equalsIgnoreCase("false") || strVal.equalsIgnoreCase("no") || strVal.equals("0")) {
                return false;
            }
        }
        else if (value instanceof Boolean) {
            return (boolean)value;
        }
        return defaultValue;
    }
    
    public int getIntegerOption(final KOption option) {
        final Object value = this.getOptionValue(option);
        if (value instanceof String) {
            final String strVal = (String)value;
            return Integer.parseInt(strVal);
        }
        if (value instanceof Integer) {
            return (int)value;
        }
        return -1;
    }
    
    public File getFileOption(final KOption option) {
        final Object value = this.getOptionValue(option);
        if (value instanceof File) {
            return (File)value;
        }
        return null;
    }
    
    public File getDirOption(final KOption option) {
        final Object value = this.getOptionValue(option);
        if (value instanceof File) {
            return (File)value;
        }
        return null;
    }
    
    public Date getDateOption(final KOption option) {
        final Object value = this.getOptionValue(option);
        if (value instanceof Date) {
            return (Date)value;
        }
        return null;
    }
}
