// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.classification.tools;

import com.sun.javadoc.DocErrorReporter;
import jdiff.JDiff;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.LanguageVersion;

public class ExcludePrivateAnnotationsJDiffDoclet
{
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
    
    public static boolean start(final RootDoc root) {
        System.out.println(ExcludePrivateAnnotationsJDiffDoclet.class.getSimpleName());
        return JDiff.start(RootDocProcessor.process(root));
    }
    
    public static int optionLength(final String option) {
        final Integer length = StabilityOptions.optionLength(option);
        if (length != null) {
            return length;
        }
        return JDiff.optionLength(option);
    }
    
    public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
        StabilityOptions.validOptions(options, reporter);
        final String[][] filteredOptions = StabilityOptions.filterOptions(options);
        return JDiff.validOptions(filteredOptions, reporter);
    }
}
