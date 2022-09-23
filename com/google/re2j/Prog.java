// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.ArrayList;
import java.util.List;

class Prog
{
    private final List<Inst> inst;
    int start;
    int numCap;
    
    Prog() {
        this.inst = new ArrayList<Inst>();
        this.numCap = 2;
    }
    
    Inst getInst(final int pc) {
        return this.inst.get(pc);
    }
    
    int numInst() {
        return this.inst.size();
    }
    
    void addInst(final Inst.Op op) {
        this.inst.add(new Inst(op));
    }
    
    Inst skipNop(int pc) {
        Inst i;
        for (i = this.inst.get(pc); i.op == Inst.Op.NOP || i.op == Inst.Op.CAPTURE; i = this.inst.get(pc), pc = i.out) {}
        return i;
    }
    
    boolean prefix(final StringBuilder prefix) {
        Inst i = this.skipNop(this.start);
        if (i.op() != Inst.Op.RUNE || i.runes.length != 1) {
            return i.op == Inst.Op.MATCH;
        }
        while (i.op() == Inst.Op.RUNE && i.runes.length == 1 && (i.arg & 0x1) == 0x0) {
            prefix.appendCodePoint(i.runes[0]);
            i = this.skipNop(i.out);
        }
        return i.op == Inst.Op.MATCH;
    }
    
    int startCond() {
        int flag = 0;
        int pc = this.start;
        while (true) {
            final Inst i = this.inst.get(pc);
            switch (i.op) {
                case EMPTY_WIDTH: {
                    flag |= i.arg;
                    break;
                }
                case FAIL: {
                    return -1;
                }
                case CAPTURE:
                case NOP: {
                    break;
                }
                default: {
                    return flag;
                }
            }
            pc = i.out;
        }
    }
    
    int next(final int l) {
        final Inst i = this.inst.get(l >> 1);
        if ((l & 0x1) == 0x0) {
            return i.out;
        }
        return i.arg;
    }
    
    void patch(int l, final int val) {
        while (l != 0) {
            final Inst i = this.inst.get(l >> 1);
            if ((l & 0x1) == 0x0) {
                l = i.out;
                i.out = val;
            }
            else {
                l = i.arg;
                i.arg = val;
            }
        }
    }
    
    int append(final int l1, final int l2) {
        if (l1 == 0) {
            return l2;
        }
        if (l2 == 0) {
            return l1;
        }
        int last = l1;
        while (true) {
            final int next = this.next(last);
            if (next == 0) {
                break;
            }
            last = next;
        }
        final Inst i = this.inst.get(last >> 1);
        if ((last & 0x1) == 0x0) {
            i.out = l2;
        }
        else {
            i.arg = l2;
        }
        return l1;
    }
    
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder();
        for (int pc = 0; pc < this.inst.size(); ++pc) {
            final int len = out.length();
            out.append(pc);
            if (pc == this.start) {
                out.append('*');
            }
            out.append("        ".substring(out.length() - len)).append(this.inst.get(pc)).append('\n');
        }
        return out.toString();
    }
}
