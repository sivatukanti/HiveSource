// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.Range;

public class NumericEntityEscaper extends CodePointTranslator
{
    private final boolean between;
    private final Range<Integer> range;
    
    private NumericEntityEscaper(final int below, final int above, final boolean between) {
        this.range = Range.between(below, above);
        this.between = between;
    }
    
    public NumericEntityEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }
    
    public static NumericEntityEscaper below(final int codepoint) {
        return outsideOf(codepoint, Integer.MAX_VALUE);
    }
    
    public static NumericEntityEscaper above(final int codepoint) {
        return outsideOf(0, codepoint);
    }
    
    public static NumericEntityEscaper between(final int codepointLow, final int codepointHigh) {
        return new NumericEntityEscaper(codepointLow, codepointHigh, true);
    }
    
    public static NumericEntityEscaper outsideOf(final int codepointLow, final int codepointHigh) {
        return new NumericEntityEscaper(codepointLow, codepointHigh, false);
    }
    
    @Override
    public boolean translate(final int codepoint, final Writer out) throws IOException {
        if (this.between != this.range.contains(codepoint)) {
            return false;
        }
        out.write("&#");
        out.write(Integer.toString(codepoint, 10));
        out.write(59);
        return true;
    }
}
