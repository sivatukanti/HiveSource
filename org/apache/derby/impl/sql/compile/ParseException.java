// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public class ParseException extends Exception
{
    protected boolean specialConstructor;
    public Token currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    protected String eol;
    
    public ParseException(final Token currentToken, final int[][] expectedTokenSequences, final String[] tokenImage) {
        super("");
        this.eol = System.getProperty("line.separator", "\n");
        this.specialConstructor = true;
        this.currentToken = currentToken;
        this.expectedTokenSequences = expectedTokenSequences;
        this.tokenImage = tokenImage;
    }
    
    public ParseException() {
        this.eol = System.getProperty("line.separator", "\n");
        this.specialConstructor = false;
    }
    
    public ParseException(final String message) {
        super(message);
        this.eol = System.getProperty("line.separator", "\n");
        this.specialConstructor = false;
    }
    
    public String getMessage() {
        if (!this.specialConstructor) {
            return super.getMessage();
        }
        String str = "";
        int length = 0;
        for (int i = 0; i < this.expectedTokenSequences.length; ++i) {
            if (length < this.expectedTokenSequences[i].length) {
                length = this.expectedTokenSequences[i].length;
            }
            for (int j = 0; j < this.expectedTokenSequences[i].length; ++j) {
                str = str + this.tokenImage[this.expectedTokenSequences[i][j]] + " ";
            }
            if (this.expectedTokenSequences[i][this.expectedTokenSequences[i].length - 1] != 0) {
                str += "...";
            }
            str = str + this.eol + "    ";
        }
        String s = "Encountered \"";
        Token token = this.currentToken.next;
        for (int k = 0; k < length; ++k) {
            if (k != 0) {
                s += " ";
            }
            if (token.kind == 0) {
                s += this.tokenImage[0];
                break;
            }
            s += this.add_escapes(token.image);
            token = token.next;
        }
        return s + "\" at line " + this.currentToken.next.beginLine + ", column " + this.currentToken.next.beginColumn;
    }
    
    protected String add_escapes(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            switch (s.charAt(i)) {
                case '\0': {
                    break;
                }
                case '\b': {
                    sb.append("\\b");
                    break;
                }
                case '\t': {
                    sb.append("\\t");
                    break;
                }
                case '\n': {
                    sb.append("\\n");
                    break;
                }
                case '\f': {
                    sb.append("\\f");
                    break;
                }
                case '\r': {
                    sb.append("\\r");
                    break;
                }
                case '\"': {
                    sb.append("\\\"");
                    break;
                }
                case '\'': {
                    sb.append("\\'");
                    break;
                }
                case '\\': {
                    sb.append("\\\\");
                    break;
                }
                default: {
                    final char char1;
                    if ((char1 = s.charAt(i)) < ' ' || char1 > '~') {
                        final String string = "0000" + Integer.toString(char1, 16);
                        sb.append("\\u" + string.substring(string.length() - 4, string.length()));
                        break;
                    }
                    sb.append(char1);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
