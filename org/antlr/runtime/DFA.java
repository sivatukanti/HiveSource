// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

public class DFA
{
    protected short[] eot;
    protected short[] eof;
    protected char[] min;
    protected char[] max;
    protected short[] accept;
    protected short[] special;
    protected short[][] transition;
    protected int decisionNumber;
    protected BaseRecognizer recognizer;
    public static final boolean debug = false;
    
    public int predict(final IntStream input) throws RecognitionException {
        final int mark = input.mark();
        int s = 0;
        try {
            while (true) {
                final int specialState = this.special[s];
                if (specialState >= 0) {
                    s = this.specialStateTransition(specialState, input);
                    if (s == -1) {
                        this.noViableAlt(s, input);
                        return 0;
                    }
                    input.consume();
                }
                else {
                    if (this.accept[s] >= 1) {
                        return this.accept[s];
                    }
                    final char c = (char)input.LA(1);
                    if (c >= this.min[s] && c <= this.max[s]) {
                        final int snext = this.transition[s][c - this.min[s]];
                        if (snext < 0) {
                            if (this.eot[s] < 0) {
                                this.noViableAlt(s, input);
                                return 0;
                            }
                            s = this.eot[s];
                            input.consume();
                        }
                        else {
                            s = snext;
                            input.consume();
                        }
                    }
                    else if (this.eot[s] >= 0) {
                        s = this.eot[s];
                        input.consume();
                    }
                    else {
                        if (c == '\uffff' && this.eof[s] >= 0) {
                            return this.accept[this.eof[s]];
                        }
                        this.noViableAlt(s, input);
                        return 0;
                    }
                }
            }
        }
        finally {
            input.rewind(mark);
        }
    }
    
    protected void noViableAlt(final int s, final IntStream input) throws NoViableAltException {
        if (this.recognizer.state.backtracking > 0) {
            this.recognizer.state.failed = true;
            return;
        }
        final NoViableAltException nvae = new NoViableAltException(this.getDescription(), this.decisionNumber, s, input);
        this.error(nvae);
        throw nvae;
    }
    
    protected void error(final NoViableAltException nvae) {
    }
    
    public int specialStateTransition(final int s, final IntStream input) throws NoViableAltException {
        return -1;
    }
    
    public String getDescription() {
        return "n/a";
    }
    
    public static short[] unpackEncodedString(final String encodedString) {
        int size = 0;
        for (int i = 0; i < encodedString.length(); i += 2) {
            size += encodedString.charAt(i);
        }
        final short[] data = new short[size];
        int di = 0;
        for (int j = 0; j < encodedString.length(); j += 2) {
            final char n = encodedString.charAt(j);
            final char v = encodedString.charAt(j + 1);
            for (int k = 1; k <= n; ++k) {
                data[di++] = (short)v;
            }
        }
        return data;
    }
    
    public static char[] unpackEncodedStringToUnsignedChars(final String encodedString) {
        int size = 0;
        for (int i = 0; i < encodedString.length(); i += 2) {
            size += encodedString.charAt(i);
        }
        final char[] data = new char[size];
        int di = 0;
        for (int j = 0; j < encodedString.length(); j += 2) {
            final char n = encodedString.charAt(j);
            final char v = encodedString.charAt(j + 1);
            for (int k = 1; k <= n; ++k) {
                data[di++] = v;
            }
        }
        return data;
    }
}
