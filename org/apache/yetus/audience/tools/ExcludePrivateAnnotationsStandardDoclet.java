// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.yetus.audience.tools;

import com.sun.javadoc.DocErrorReporter;
import com.sun.tools.doclets.standard.Standard;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.LanguageVersion;
import org.apache.yetus.audience.InterfaceStability;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class ExcludePrivateAnnotationsStandardDoclet
{
    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }
    
    public static boolean start(final RootDoc root) {
        System.out.println(ExcludePrivateAnnotationsStandardDoclet.class.getSimpleName());
        return Standard.start(RootDocProcessor.process(root));
    }
    
    public static int optionLength(final String option) {
        final Integer length = StabilityOptions.optionLength(option);
        if (length != null) {
            return length;
        }
        return Standard.optionLength(option);
    }
    
    public static boolean validOptions(final String[][] options, final DocErrorReporter reporter) {
        StabilityOptions.validOptions(options, reporter);
        final String[][] filteredOptions = StabilityOptions.filterOptions(options);
        return Standard.validOptions(filteredOptions, reporter);
    }
}
