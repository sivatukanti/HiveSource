// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.util.ArrayList;
import java.util.HashMap;

public class CommandLine
{
    protected HashMap<String, Option> options;
    protected HashMap valueOptions;
    protected ArrayList optionList;
    protected String[] defaultArg;
    protected boolean displaysDash;
    
    public CommandLine() {
        this.options = new HashMap<String, Option>();
        this.valueOptions = new HashMap();
        this.optionList = new ArrayList();
        this.displaysDash = true;
    }
    
    public CommandLine(final boolean displaysDash) {
        this.options = new HashMap<String, Option>();
        this.valueOptions = new HashMap();
        this.optionList = new ArrayList();
        this.displaysDash = true;
        this.displaysDash = displaysDash;
    }
    
    public void addOption(final String shortName, final String longName, final String argName, final String desc) {
        Option option = null;
        if (StringUtils.isEmpty(shortName) && StringUtils.isEmpty(longName)) {
            throw new IllegalArgumentException("require shortName or longName");
        }
        if (StringUtils.notEmpty(argName)) {
            option = new WithArgOption(shortName, longName, desc, argName);
        }
        else {
            option = new NoArgOption(shortName, longName, desc);
        }
        this.optionList.add(option);
        if (StringUtils.notEmpty(shortName)) {
            this.options.put("-" + shortName, option);
            this.valueOptions.put(shortName, option);
        }
        if (StringUtils.notEmpty(longName)) {
            this.options.put("--" + longName, option);
            this.valueOptions.put(longName, option);
        }
    }
    
    public void parse(final String[] args) {
        final ArrayList defaultArg = new ArrayList();
        if (args == null || args.length == 0) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            if (!StringUtils.isEmpty(args[i])) {
                if (args[i].startsWith("-")) {
                    if (this.options.containsKey(args[i])) {
                        final Option option = this.options.get(args[i]);
                        if (option instanceof NoArgOption) {
                            ((NoArgOption)option).selected = true;
                        }
                        else {
                            if (args.length - 1 == i) {
                                throw new RuntimeException("option " + args[i] + " needs an argument");
                            }
                            ((WithArgOption)option).option = args[i + 1];
                            ++i;
                        }
                    }
                    else {
                        defaultArg.add(args[i]);
                    }
                }
                else {
                    defaultArg.add(args[i]);
                }
            }
        }
        if (defaultArg.size() == 0) {
            this.defaultArg = new String[0];
        }
        final String[] result = new String[defaultArg.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = defaultArg.get(i);
        }
        this.defaultArg = result;
    }
    
    public boolean hasOption(final String name) {
        if (!this.valueOptions.containsKey(name)) {
            throw new IllegalArgumentException("no such option " + name);
        }
        final Option option = this.valueOptions.get(name);
        if (option instanceof NoArgOption) {
            return ((NoArgOption)option).selected;
        }
        return StringUtils.notEmpty(((WithArgOption)option).option);
    }
    
    public String getOptionArg(final String name) {
        if (!this.valueOptions.containsKey(name)) {
            throw new IllegalArgumentException("no such option " + name);
        }
        final Option option = this.valueOptions.get(name);
        if (option instanceof NoArgOption) {
            return "" + ((NoArgOption)option).selected;
        }
        return ((WithArgOption)option).option;
    }
    
    @Override
    public String toString() {
        if (this.optionList.size() == 0) {
            return "[NO OPTIONS]";
        }
        final int maxLength = 80;
        final StringBuilder sb = new StringBuilder();
        int shortMax = 0;
        int longMax = 0;
        int argNameMax = 0;
        int descMax = 0;
        for (int i = 0; i < this.optionList.size(); ++i) {
            final Option o = this.optionList.get(i);
            if (o.shortName != null) {
                if (o.shortName != null && o.shortName.length() > shortMax) {
                    shortMax = o.shortName.length();
                }
                if (o.longName != null && o.longName.length() > longMax) {
                    longMax = o.longName.length();
                }
                if (o instanceof WithArgOption) {
                    final WithArgOption op = (WithArgOption)o;
                    if (op.name.length() > argNameMax) {
                        argNameMax = op.name.length();
                    }
                }
                if (o.description != null && o.description.length() > descMax) {
                    descMax = o.description.length();
                }
            }
        }
        if (shortMax > 0) {
            shortMax += 3;
        }
        if (longMax > 0) {
            longMax += 3;
        }
        if (argNameMax > 0) {
            argNameMax += 3;
        }
        for (int i = 0; i < this.optionList.size(); ++i) {
            int j = 0;
            final Option o2 = this.optionList.get(i);
            if (StringUtils.notEmpty(o2.shortName)) {
                if (this.displaysDash) {
                    sb.append("-");
                }
                sb.append(o2.shortName);
                j = o2.shortName.length() + 1;
            }
            while (j < shortMax) {
                sb.append(" ");
                ++j;
            }
            j = 0;
            if (StringUtils.notEmpty(o2.longName)) {
                sb.append("--");
                sb.append(o2.longName);
                j = o2.longName.length() + 2;
            }
            while (j < longMax) {
                sb.append(" ");
                ++j;
            }
            j = 0;
            if (o2 instanceof WithArgOption) {
                final WithArgOption op2 = (WithArgOption)o2;
                sb.append(op2.name);
                j = op2.name.length();
            }
            while (j < argNameMax) {
                sb.append(" ");
                ++j;
            }
            if (StringUtils.notEmpty(o2.description)) {
                int basePos;
                if (shortMax + longMax + argNameMax > maxLength) {
                    basePos = maxLength / 2;
                    sb.append("\n");
                    for (int k = 0; k < basePos; ++k) {
                        sb.append(" ");
                    }
                }
                else {
                    basePos = shortMax + longMax + argNameMax;
                }
                int pos = basePos;
                for (j = 0; j < o2.description.length(); ++j) {
                    sb.append(o2.description.charAt(j));
                    if (pos >= maxLength) {
                        if (j < o2.description.length() - 1 && o2.description.charAt(j + 1) != ' ') {
                            for (int p = sb.length() - 1; p >= 0; --p) {
                                if (sb.charAt(p) == ' ') {
                                    sb.insert(p, '\n');
                                    for (int l = 0; l < basePos - 1; ++l) {
                                        sb.insert(p + 1, " ");
                                    }
                                    break;
                                }
                            }
                        }
                        else {
                            sb.append("\n");
                            for (int m = 0; m < basePos; ++m) {
                                sb.append(" ");
                            }
                        }
                        pos = basePos;
                    }
                    ++pos;
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public String[] getDefaultArgs() {
        return this.defaultArg;
    }
    
    protected static class Option
    {
        final String shortName;
        final String longName;
        final String description;
        
        public Option(final String shortName, final String longName, final String desc) {
            this.shortName = shortName;
            this.longName = longName;
            this.description = desc;
        }
    }
    
    protected static class NoArgOption extends Option
    {
        boolean selected;
        
        public NoArgOption(final String shortName, final String longName, final String desc) {
            super(shortName, longName, desc);
        }
    }
    
    protected static class WithArgOption extends Option
    {
        String name;
        String option;
        
        public WithArgOption(final String shortName, final String longName, final String desc, final String name) {
            super(shortName, longName, desc);
            this.name = name;
        }
    }
}
