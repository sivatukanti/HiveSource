// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.io;

import parquet.org.codehaus.jackson.util.CharTypes;
import parquet.org.codehaus.jackson.SerializableString;

public abstract class CharacterEscapes
{
    public static final int ESCAPE_NONE = 0;
    public static final int ESCAPE_STANDARD = -1;
    public static final int ESCAPE_CUSTOM = -2;
    
    public abstract int[] getEscapeCodesForAscii();
    
    public abstract SerializableString getEscapeSequence(final int p0);
    
    public static int[] standardAsciiEscapesForJSON() {
        final int[] esc = CharTypes.get7BitOutputEscapes();
        final int len = esc.length;
        final int[] result = new int[len];
        System.arraycopy(esc, 0, result, 0, esc.length);
        return result;
    }
}
