// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

class JavaCharFormatter implements CharFormatter
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
                return b ? "\\'" : "'";
            }
            case 34: {
                return b ? "\"" : "\\\"";
            }
            default: {
                if (n >= 32 && n <= 126) {
                    return String.valueOf((char)n);
                }
                if (0 <= n && n <= 15) {
                    return "\\u000" + Integer.toString(n, 16);
                }
                if (16 <= n && n <= 255) {
                    return "\\u00" + Integer.toString(n, 16);
                }
                if (256 <= n && n <= 4095) {
                    return "\\u0" + Integer.toString(n, 16);
                }
                return "\\u" + Integer.toString(n, 16);
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
    
    public String literalChar(final int n) {
        return "'" + this.escapeChar(n, true) + "'";
    }
    
    public String literalString(final String s) {
        return "\"" + this.escapeString(s) + "\"";
    }
}
