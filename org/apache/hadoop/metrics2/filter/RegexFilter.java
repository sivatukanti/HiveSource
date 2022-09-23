// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.filter;

import com.google.re2j.Pattern;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class RegexFilter extends AbstractPatternFilter
{
    @Override
    protected Pattern compile(final String s) {
        return Pattern.compile(s);
    }
}
