// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

public class ParseException extends Exception
{
    protected boolean specialConstructor;
    public Token currentToken;
    public int[][] expectedTokenSequences;
    public String[] tokenImage;
    protected String eol;
    
    public ParseException(final Token currentTokenVal, final int[][] expectedTokenSequencesVal, final String[] tokenImageVal) {
        super("");
        this.eol = System.getProperty("line.separator", "\n");
        this.specialConstructor = true;
        this.currentToken = currentTokenVal;
        this.expectedTokenSequences = expectedTokenSequencesVal;
        this.tokenImage = tokenImageVal;
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
    
    @Override
    public String getMessage() {
        if (!this.specialConstructor) {
            return super.getMessage();
        }
        final StringBuilder expected = new StringBuilder();
        int maxSize = 0;
        for (final int[] expectedTokenSequence : this.expectedTokenSequences) {
            if (maxSize < expectedTokenSequence.length) {
                maxSize = expectedTokenSequence.length;
            }
            for (final int element : expectedTokenSequence) {
                expected.append(this.tokenImage[element]).append(" ");
            }
            if (expectedTokenSequence[expectedTokenSequence.length - 1] != 0) {
                expected.append("...");
            }
            expected.append(this.eol).append("    ");
        }
        String retval = "Encountered \"";
        Token tok = this.currentToken.next;
        for (int i = 0; i < maxSize; ++i) {
            if (i != 0) {
                retval += " ";
            }
            if (tok.kind == 0) {
                retval += this.tokenImage[0];
                break;
            }
            retval += this.add_escapes(tok.image);
            tok = tok.next;
        }
        retval = retval + "\" at line " + this.currentToken.next.beginLine + ", column " + this.currentToken.next.beginColumn;
        retval = retval + "." + this.eol;
        if (this.expectedTokenSequences.length == 1) {
            retval = retval + "Was expecting:" + this.eol + "    ";
        }
        else {
            retval = retval + "Was expecting one of:" + this.eol + "    ";
        }
        retval += expected.toString();
        return retval;
    }
    
    protected String add_escapes(final String str) {
        final StringBuilder retval = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            switch (str.charAt(i)) {
                case '\0': {
                    break;
                }
                case '\b': {
                    retval.append("\\b");
                    break;
                }
                case '\t': {
                    retval.append("\\t");
                    break;
                }
                case '\n': {
                    retval.append("\\n");
                    break;
                }
                case '\f': {
                    retval.append("\\f");
                    break;
                }
                case '\r': {
                    retval.append("\\r");
                    break;
                }
                case '\"': {
                    retval.append("\\\"");
                    break;
                }
                case '\'': {
                    retval.append("\\'");
                    break;
                }
                case '\\': {
                    retval.append("\\\\");
                    break;
                }
                default: {
                    final char ch;
                    if ((ch = str.charAt(i)) < ' ' || ch > '~') {
                        final String s = "0000" + Integer.toString(ch, 16);
                        retval.append("\\u" + s.substring(s.length() - 4, s.length()));
                        break;
                    }
                    retval.append(ch);
                    break;
                }
            }
        }
        return retval.toString();
    }
}
