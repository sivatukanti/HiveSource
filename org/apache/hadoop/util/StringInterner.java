// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import com.google.common.collect.Interners;
import com.google.common.collect.Interner;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public class StringInterner
{
    private static final Interner<String> STRONG_INTERNER;
    
    public static String strongIntern(final String sample) {
        if (sample == null) {
            return null;
        }
        return StringInterner.STRONG_INTERNER.intern(sample);
    }
    
    public static String weakIntern(final String sample) {
        if (sample == null) {
            return null;
        }
        return sample.intern();
    }
    
    public static String[] internStringsInArray(final String[] strings) {
        for (int i = 0; i < strings.length; ++i) {
            strings[i] = weakIntern(strings[i]);
        }
        return strings;
    }
    
    static {
        STRONG_INTERNER = Interners.newStrongInterner();
    }
}
