// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
class PrintableString
{
    private static final char REPLACEMENT_CHAR = '?';
    private final String printableString;
    
    PrintableString(final String rawString) {
        final StringBuilder stringBuilder = new StringBuilder(rawString.length());
        int offset = 0;
        while (offset < rawString.length()) {
            final int codePoint = rawString.codePointAt(offset);
            offset += Character.charCount(codePoint);
            switch (Character.getType(codePoint)) {
                case 0:
                case 15:
                case 16:
                case 18:
                case 19: {
                    stringBuilder.append('?');
                    continue;
                }
                default: {
                    stringBuilder.append(Character.toChars(codePoint));
                    continue;
                }
            }
        }
        this.printableString = stringBuilder.toString();
    }
    
    @Override
    public String toString() {
        return this.printableString;
    }
}
