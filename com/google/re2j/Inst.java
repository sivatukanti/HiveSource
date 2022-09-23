// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

class Inst
{
    Op op;
    int out;
    int arg;
    int[] runes;
    
    Inst(final Op op) {
        this.op = op;
    }
    
    Op op() {
        switch (this.op) {
            case RUNE1:
            case RUNE_ANY:
            case RUNE_ANY_NOT_NL: {
                return Op.RUNE;
            }
            default: {
                return this.op;
            }
        }
    }
    
    boolean matchRune(final int r) {
        if (this.runes.length != 1) {
            for (int j = 0; j < this.runes.length && j <= 8; j += 2) {
                if (r < this.runes[j]) {
                    return false;
                }
                if (r <= this.runes[j + 1]) {
                    return true;
                }
            }
            int lo = 0;
            int hi = this.runes.length / 2;
            while (lo < hi) {
                final int m = lo + (hi - lo) / 2;
                final int c = this.runes[2 * m];
                if (c <= r) {
                    if (r <= this.runes[2 * m + 1]) {
                        return true;
                    }
                    lo = m + 1;
                }
                else {
                    hi = m;
                }
            }
            return false;
        }
        final int r2 = this.runes[0];
        if (r == r2) {
            return true;
        }
        if ((this.arg & 0x1) != 0x0) {
            for (int r3 = Unicode.simpleFold(r2); r3 != r2; r3 = Unicode.simpleFold(r3)) {
                if (r == r3) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        switch (this.op) {
            case ALT: {
                return "alt -> " + this.out + ", " + this.arg;
            }
            case ALT_MATCH: {
                return "altmatch -> " + this.out + ", " + this.arg;
            }
            case CAPTURE: {
                return "cap " + this.arg + " -> " + this.out;
            }
            case EMPTY_WIDTH: {
                return "empty " + this.arg + " -> " + this.out;
            }
            case MATCH: {
                return "match";
            }
            case FAIL: {
                return "fail";
            }
            case NOP: {
                return "nop -> " + this.out;
            }
            case RUNE: {
                if (this.runes == null) {
                    return "rune <null>";
                }
                return "rune " + escapeRunes(this.runes) + (((this.arg & 0x1) != 0x0) ? "/i" : "") + " -> " + this.out;
            }
            case RUNE1: {
                return "rune1 " + escapeRunes(this.runes) + " -> " + this.out;
            }
            case RUNE_ANY: {
                return "any -> " + this.out;
            }
            case RUNE_ANY_NOT_NL: {
                return "anynotnl -> " + this.out;
            }
            default: {
                throw new IllegalStateException("unhandled case in Inst.toString");
            }
        }
    }
    
    private static String escapeRunes(final int[] runes) {
        final StringBuilder out = new StringBuilder();
        out.append('\"');
        for (final int rune : runes) {
            Utils.escapeRune(out, rune);
        }
        out.append('\"');
        return out.toString();
    }
    
    enum Op
    {
        ALT, 
        ALT_MATCH, 
        CAPTURE, 
        EMPTY_WIDTH, 
        FAIL, 
        MATCH, 
        NOP, 
        RUNE, 
        RUNE1, 
        RUNE_ANY, 
        RUNE_ANY_NOT_NL;
    }
}
