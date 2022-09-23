// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.Arrays;

class Regexp
{
    static final Regexp[] EMPTY_SUBS;
    Op op;
    int flags;
    Regexp[] subs;
    int[] runes;
    int min;
    int max;
    int cap;
    String name;
    
    Regexp(final Op op) {
        this.op = op;
    }
    
    Regexp(final Regexp that) {
        this.op = that.op;
        this.flags = that.flags;
        this.subs = that.subs;
        this.runes = that.runes;
        this.min = that.min;
        this.max = that.max;
        this.cap = that.cap;
        this.name = that.name;
    }
    
    void reinit() {
        this.flags = 0;
        this.subs = Regexp.EMPTY_SUBS;
        this.runes = null;
        final int cap = 0;
        this.max = cap;
        this.min = cap;
        this.cap = cap;
        this.name = null;
    }
    
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        this.appendTo(out);
        return out.toString();
    }
    
    private static void quoteIfHyphen(final StringBuilder out, final int rune) {
        if (rune == 45) {
            out.append('\\');
        }
    }
    
    private void appendTo(final StringBuilder out) {
        switch (this.op) {
            case NO_MATCH: {
                out.append("[^\\x00-\\x{10FFFF}]");
                break;
            }
            case EMPTY_MATCH: {
                out.append("(?:)");
                break;
            }
            case STAR:
            case PLUS:
            case QUEST:
            case REPEAT: {
                final Regexp sub = this.subs[0];
                if (sub.op.ordinal() > Op.CAPTURE.ordinal() || (sub.op == Op.LITERAL && sub.runes.length > 1)) {
                    out.append("(?:");
                    sub.appendTo(out);
                    out.append(')');
                }
                else {
                    sub.appendTo(out);
                }
                switch (this.op) {
                    case STAR: {
                        out.append('*');
                        break;
                    }
                    case PLUS: {
                        out.append('+');
                        break;
                    }
                    case QUEST: {
                        out.append('?');
                        break;
                    }
                    case REPEAT: {
                        out.append('{').append(this.min);
                        if (this.min != this.max) {
                            out.append(',');
                            if (this.max >= 0) {
                                out.append(this.max);
                            }
                        }
                        out.append('}');
                        break;
                    }
                }
                if ((this.flags & 0x20) != 0x0) {
                    out.append('?');
                    break;
                }
                break;
            }
            case CONCAT: {
                for (final Regexp sub2 : this.subs) {
                    if (sub2.op == Op.ALTERNATE) {
                        out.append("(?:");
                        sub2.appendTo(out);
                        out.append(')');
                    }
                    else {
                        sub2.appendTo(out);
                    }
                }
                break;
            }
            case ALTERNATE: {
                String sep = "";
                for (final Regexp sub3 : this.subs) {
                    out.append(sep);
                    sep = "|";
                    sub3.appendTo(out);
                }
                break;
            }
            case LITERAL: {
                if ((this.flags & 0x1) != 0x0) {
                    out.append("(?i:");
                }
                for (final int rune : this.runes) {
                    Utils.escapeRune(out, rune);
                }
                if ((this.flags & 0x1) != 0x0) {
                    out.append(')');
                    break;
                }
                break;
            }
            case ANY_CHAR_NOT_NL: {
                out.append("(?-s:.)");
                break;
            }
            case ANY_CHAR: {
                out.append("(?s:.)");
                break;
            }
            case CAPTURE: {
                if (this.name == null || this.name.isEmpty()) {
                    out.append('(');
                }
                else {
                    out.append("(?P<");
                    out.append(this.name);
                    out.append(">");
                }
                if (this.subs[0].op != Op.EMPTY_MATCH) {
                    this.subs[0].appendTo(out);
                }
                out.append(')');
                break;
            }
            case BEGIN_TEXT: {
                out.append("\\A");
                break;
            }
            case END_TEXT: {
                if ((this.flags & 0x100) != 0x0) {
                    out.append("(?-m:$)");
                    break;
                }
                out.append("\\z");
                break;
            }
            case BEGIN_LINE: {
                out.append('^');
                break;
            }
            case END_LINE: {
                out.append('$');
                break;
            }
            case WORD_BOUNDARY: {
                out.append("\\b");
                break;
            }
            case NO_WORD_BOUNDARY: {
                out.append("\\B");
                break;
            }
            case CHAR_CLASS: {
                if (this.runes.length % 2 != 0) {
                    out.append("[invalid char class]");
                    break;
                }
                out.append('[');
                if (this.runes.length == 0) {
                    out.append("^\\x00-\\x{10FFFF}");
                }
                else if (this.runes[0] == 0 && this.runes[this.runes.length - 1] == 1114111) {
                    out.append('^');
                    for (int i = 1; i < this.runes.length - 1; i += 2) {
                        final int lo = this.runes[i] + 1;
                        final int hi = this.runes[i + 1] - 1;
                        quoteIfHyphen(out, lo);
                        Utils.escapeRune(out, lo);
                        if (lo != hi) {
                            out.append('-');
                            quoteIfHyphen(out, hi);
                            Utils.escapeRune(out, hi);
                        }
                    }
                }
                else {
                    for (int i = 0; i < this.runes.length; i += 2) {
                        final int lo = this.runes[i];
                        final int hi = this.runes[i + 1];
                        quoteIfHyphen(out, lo);
                        Utils.escapeRune(out, lo);
                        if (lo != hi) {
                            out.append('-');
                            quoteIfHyphen(out, hi);
                            Utils.escapeRune(out, hi);
                        }
                    }
                }
                out.append(']');
                break;
            }
            default: {
                out.append(this.op);
                break;
            }
        }
    }
    
    int maxCap() {
        int m = 0;
        if (this.op == Op.CAPTURE) {
            m = this.cap;
        }
        if (this.subs != null) {
            for (final Regexp sub : this.subs) {
                final int n = sub.maxCap();
                if (m < n) {
                    m = n;
                }
            }
        }
        return m;
    }
    
    @Override
    public boolean equals(final Object that) {
        if (!(that instanceof Regexp)) {
            return false;
        }
        final Regexp x = this;
        final Regexp y = (Regexp)that;
        if (x.op != y.op) {
            return false;
        }
        switch (x.op) {
            case END_TEXT: {
                if ((x.flags & 0x100) != (y.flags & 0x100)) {
                    return false;
                }
                break;
            }
            case LITERAL:
            case CHAR_CLASS: {
                if (!Arrays.equals(x.runes, y.runes)) {
                    return false;
                }
                break;
            }
            case CONCAT:
            case ALTERNATE: {
                if (x.subs.length != y.subs.length) {
                    return false;
                }
                for (int i = 0; i < x.subs.length; ++i) {
                    if (!x.subs[i].equals(y.subs[i])) {
                        return false;
                    }
                }
                break;
            }
            case STAR:
            case PLUS:
            case QUEST: {
                if ((x.flags & 0x20) != (y.flags & 0x20) || !x.subs[0].equals(y.subs[0])) {
                    return false;
                }
                break;
            }
            case REPEAT: {
                if ((x.flags & 0x20) != (y.flags & 0x20) || x.min != y.min || x.max != y.max || !x.subs[0].equals(y.subs[0])) {
                    return false;
                }
                break;
            }
            case CAPTURE: {
                if (x.cap != y.cap || x.name != y.name || !x.subs[0].equals(y.subs[0])) {
                    return false;
                }
                break;
            }
        }
        return true;
    }
    
    static {
        EMPTY_SUBS = new Regexp[0];
    }
    
    enum Op
    {
        NO_MATCH, 
        EMPTY_MATCH, 
        LITERAL, 
        CHAR_CLASS, 
        ANY_CHAR_NOT_NL, 
        ANY_CHAR, 
        BEGIN_LINE, 
        END_LINE, 
        BEGIN_TEXT, 
        END_TEXT, 
        WORD_BOUNDARY, 
        NO_WORD_BOUNDARY, 
        CAPTURE, 
        STAR, 
        PLUS, 
        QUEST, 
        REPEAT, 
        CONCAT, 
        ALTERNATE, 
        LEFT_PAREN, 
        VERTICAL_BAR;
        
        boolean isPseudo() {
            return this.ordinal() >= Op.LEFT_PAREN.ordinal();
        }
    }
}
