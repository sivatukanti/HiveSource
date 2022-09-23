// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

class Compiler
{
    private final Prog prog;
    private static final int[] ANY_RUNE_NOT_NL;
    private static final int[] ANY_RUNE;
    
    private Compiler() {
        this.prog = new Prog();
        this.newInst(Inst.Op.FAIL);
    }
    
    static Prog compileRegexp(final Regexp re) {
        final Compiler c = new Compiler();
        final Frag f = c.compile(re);
        c.prog.patch(f.out, c.newInst(Inst.Op.MATCH).i);
        c.prog.start = f.i;
        return c.prog;
    }
    
    private Frag newInst(final Inst.Op op) {
        this.prog.addInst(op);
        return new Frag(this.prog.numInst() - 1);
    }
    
    private Frag nop() {
        final Frag f = this.newInst(Inst.Op.NOP);
        f.out = f.i << 1;
        return f;
    }
    
    private Frag fail() {
        return new Frag();
    }
    
    private Frag cap(final int arg) {
        final Frag f = this.newInst(Inst.Op.CAPTURE);
        f.out = f.i << 1;
        this.prog.getInst(f.i).arg = arg;
        if (this.prog.numCap < arg + 1) {
            this.prog.numCap = arg + 1;
        }
        return f;
    }
    
    private Frag cat(final Frag f1, final Frag f2) {
        if (f1.i == 0 || f2.i == 0) {
            return this.fail();
        }
        this.prog.patch(f1.out, f2.i);
        return new Frag(f1.i, f2.out);
    }
    
    private Frag alt(final Frag f1, final Frag f2) {
        if (f1.i == 0) {
            return f2;
        }
        if (f2.i == 0) {
            return f1;
        }
        final Frag f3 = this.newInst(Inst.Op.ALT);
        final Inst i = this.prog.getInst(f3.i);
        i.out = f1.i;
        i.arg = f2.i;
        f3.out = this.prog.append(f1.out, f2.out);
        return f3;
    }
    
    private Frag quest(final Frag f1, final boolean nongreedy) {
        final Frag f2 = this.newInst(Inst.Op.ALT);
        final Inst i = this.prog.getInst(f2.i);
        if (nongreedy) {
            i.arg = f1.i;
            f2.out = f2.i << 1;
        }
        else {
            i.out = f1.i;
            f2.out = (f2.i << 1 | 0x1);
        }
        f2.out = this.prog.append(f2.out, f1.out);
        return f2;
    }
    
    private Frag star(final Frag f1, final boolean nongreedy) {
        final Frag f2 = this.newInst(Inst.Op.ALT);
        final Inst i = this.prog.getInst(f2.i);
        if (nongreedy) {
            i.arg = f1.i;
            f2.out = f2.i << 1;
        }
        else {
            i.out = f1.i;
            f2.out = (f2.i << 1 | 0x1);
        }
        this.prog.patch(f1.out, f2.i);
        return f2;
    }
    
    private Frag plus(final Frag f1, final boolean nongreedy) {
        return new Frag(f1.i, this.star(f1, nongreedy).out);
    }
    
    private Frag empty(final int op) {
        final Frag f = this.newInst(Inst.Op.EMPTY_WIDTH);
        this.prog.getInst(f.i).arg = op;
        f.out = f.i << 1;
        return f;
    }
    
    private Frag rune(final int rune, final int flags) {
        return this.rune(new int[] { rune }, flags);
    }
    
    private Frag rune(final int[] runes, int flags) {
        final Frag f = this.newInst(Inst.Op.RUNE);
        final Inst i = this.prog.getInst(f.i);
        i.runes = runes;
        flags &= 0x1;
        if (runes.length != 1 || Unicode.simpleFold(runes[0]) == runes[0]) {
            flags &= 0xFFFFFFFE;
        }
        i.arg = flags;
        f.out = f.i << 1;
        if (((flags & 0x1) == 0x0 && runes.length == 1) || (runes.length == 2 && runes[0] == runes[1])) {
            i.op = Inst.Op.RUNE1;
        }
        else if (runes.length == 2 && runes[0] == 0 && runes[1] == 1114111) {
            i.op = Inst.Op.RUNE_ANY;
        }
        else if (runes.length == 4 && runes[0] == 0 && runes[1] == 9 && runes[2] == 11 && runes[3] == 1114111) {
            i.op = Inst.Op.RUNE_ANY_NOT_NL;
        }
        return f;
    }
    
    private Frag compile(final Regexp re) {
        switch (re.op) {
            case NO_MATCH: {
                return this.fail();
            }
            case EMPTY_MATCH: {
                return this.nop();
            }
            case LITERAL: {
                if (re.runes.length == 0) {
                    return this.nop();
                }
                Frag f = null;
                for (final int r : re.runes) {
                    final Frag f2 = this.rune(r, re.flags);
                    f = ((f == null) ? f2 : this.cat(f, f2));
                }
                return f;
            }
            case CHAR_CLASS: {
                return this.rune(re.runes, re.flags);
            }
            case ANY_CHAR_NOT_NL: {
                return this.rune(Compiler.ANY_RUNE_NOT_NL, 0);
            }
            case ANY_CHAR: {
                return this.rune(Compiler.ANY_RUNE, 0);
            }
            case BEGIN_LINE: {
                return this.empty(1);
            }
            case END_LINE: {
                return this.empty(2);
            }
            case BEGIN_TEXT: {
                return this.empty(4);
            }
            case END_TEXT: {
                return this.empty(8);
            }
            case WORD_BOUNDARY: {
                return this.empty(16);
            }
            case NO_WORD_BOUNDARY: {
                return this.empty(32);
            }
            case CAPTURE: {
                final Frag bra = this.cap(re.cap << 1);
                final Frag sub = this.compile(re.subs[0]);
                final Frag ket = this.cap(re.cap << 1 | 0x1);
                return this.cat(this.cat(bra, sub), ket);
            }
            case STAR: {
                return this.star(this.compile(re.subs[0]), (re.flags & 0x20) != 0x0);
            }
            case PLUS: {
                return this.plus(this.compile(re.subs[0]), (re.flags & 0x20) != 0x0);
            }
            case QUEST: {
                return this.quest(this.compile(re.subs[0]), (re.flags & 0x20) != 0x0);
            }
            case CONCAT: {
                if (re.subs.length == 0) {
                    return this.nop();
                }
                Frag f = null;
                for (final Regexp sub2 : re.subs) {
                    final Frag f2 = this.compile(sub2);
                    f = ((f == null) ? f2 : this.cat(f, f2));
                }
                return f;
            }
            case ALTERNATE: {
                if (re.subs.length == 0) {
                    return this.nop();
                }
                Frag f = null;
                for (final Regexp sub2 : re.subs) {
                    final Frag f2 = this.compile(sub2);
                    f = ((f == null) ? f2 : this.alt(f, f2));
                }
                return f;
            }
            default: {
                throw new IllegalStateException("regexp: unhandled case in compile");
            }
        }
    }
    
    static {
        ANY_RUNE_NOT_NL = new int[] { 0, 9, 11, 1114111 };
        ANY_RUNE = new int[] { 0, 1114111 };
    }
    
    private static class Frag
    {
        final int i;
        int out;
        
        Frag() {
            this(0, 0);
        }
        
        Frag(final int i) {
            this(i, 0);
        }
        
        Frag(final int i, final int out) {
            this.i = i;
            this.out = out;
        }
    }
}
