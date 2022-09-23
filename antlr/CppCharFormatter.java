// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class CppCharFormatter implements CharFormatter
{
    public String escapeChar(final int n, final boolean b) {
        switch (n) {
            case 10: {
                return "\\n";
            }
            case 9: {
                return "\\t";
            }
            case 13: {
                return "\\r";
            }
            case 92: {
                return "\\\\";
            }
            case 39: {
                return "\\'";
            }
            case 34: {
                return "\\\"";
            }
            default: {
                if (n >= 32 && n <= 126) {
                    return String.valueOf((char)n);
                }
                if (n > 255) {
                    String s;
                    for (s = Integer.toString(n, 16); s.length() < 4; s = '0' + s) {}
                    return "\\u" + s;
                }
                return "\\" + Integer.toString(n, 8);
            }
        }
    }
    
    public String escapeString(final String s) {
        String string = new String();
        for (int i = 0; i < s.length(); ++i) {
            string += this.escapeChar(s.charAt(i), false);
        }
        return string;
    }
    
    public String literalChar(final int i) {
        String str = "0x" + Integer.toString(i, 16);
        if (i >= 0 && i <= 126) {
            str = str + " /* '" + this.escapeChar(i, true) + "' */ ";
        }
        return str;
    }
    
    public String literalString(final String s) {
        return "\"" + this.escapeString(s) + "\"";
    }
}
