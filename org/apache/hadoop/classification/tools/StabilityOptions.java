// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.classification.tools;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import com.sun.javadoc.DocErrorReporter;
import java.util.Locale;

class StabilityOptions
{
    public static final String STABLE_OPTION = "-stable";
    public static final String EVOLVING_OPTION = "-evolving";
    public static final String UNSTABLE_OPTION = "-unstable";
    
    public static Integer optionLength(final String option) {
        final String opt = option.toLowerCase(Locale.ENGLISH);
        if (opt.equals("-unstable")) {
            return 1;
        }
        if (opt.equals("-evolving")) {
            return 1;
        }
        if (opt.equals("-stable")) {
            return 1;
        }
        return null;
    }
    
    public static void validOptions(final String[][] options, final DocErrorReporter reporter) {
        for (int i = 0; i < options.length; ++i) {
            final String opt = options[i][0].toLowerCase(Locale.ENGLISH);
            if (opt.equals("-unstable")) {
                RootDocProcessor.stability = "-unstable";
            }
            else if (opt.equals("-evolving")) {
                RootDocProcessor.stability = "-evolving";
            }
            else if (opt.equals("-stable")) {
                RootDocProcessor.stability = "-stable";
            }
        }
    }
    
    public static String[][] filterOptions(final String[][] options) {
        final List<String[]> optionsList = new ArrayList<String[]>();
        for (int i = 0; i < options.length; ++i) {
            if (!options[i][0].equalsIgnoreCase("-unstable") && !options[i][0].equalsIgnoreCase("-evolving") && !options[i][0].equalsIgnoreCase("-stable")) {
                optionsList.add(options[i]);
            }
        }
        final String[][] filteredOptions = new String[optionsList.size()][];
        int j = 0;
        for (final String[] option : optionsList) {
            filteredOptions[j++] = option;
        }
        return filteredOptions;
    }
}
