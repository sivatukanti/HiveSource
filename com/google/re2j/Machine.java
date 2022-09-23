// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.Arrays;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

class Machine
{
    private RE2 re2;
    private final Prog prog;
    private final Queue q0;
    private final Queue q1;
    private List<Thread> pool;
    private boolean matched;
    private int[] matchcap;
    
    Machine(final RE2 re2) {
        this.pool = new ArrayList<Thread>();
        this.prog = re2.prog;
        this.re2 = re2;
        this.q0 = new Queue(this.prog.numInst());
        this.q1 = new Queue(this.prog.numInst());
        this.matchcap = new int[(this.prog.numCap < 2) ? 2 : this.prog.numCap];
    }
    
    void init(final int ncap) {
        for (final Thread t : this.pool) {
            t.cap = new int[ncap];
        }
        this.matchcap = new int[ncap];
    }
    
    int[] submatches() {
        if (this.matchcap.length == 0) {
            return Utils.EMPTY_INTS;
        }
        final int[] cap = new int[this.matchcap.length];
        System.arraycopy(this.matchcap, 0, cap, 0, this.matchcap.length);
        return cap;
    }
    
    private Thread alloc(final Inst inst) {
        final int n = this.pool.size();
        final Thread t = (n > 0) ? this.pool.remove(n - 1) : new Thread(this.matchcap.length);
        t.inst = inst;
        return t;
    }
    
    private void free(final Thread t) {
        this.pool.add(t);
    }
    
    boolean match(final MachineInput in, int pos, final int anchor) {
        final int startCond = this.re2.cond;
        if (startCond == -1) {
            return false;
        }
        if ((anchor == 1 || anchor == 2) && pos != 0) {
            return false;
        }
        this.matched = false;
        Arrays.fill(this.matchcap, -1);
        Queue runq = this.q0;
        Queue nextq = this.q1;
        int r = in.step(pos);
        int rune = r >> 3;
        int width = r & 0x7;
        int rune2 = -1;
        int width2 = 0;
        if (r != -8) {
            r = in.step(pos + width);
            rune2 = r >> 3;
            width2 = (r & 0x7);
        }
        int flag;
        if (pos == 0) {
            flag = Utils.emptyOpContext(-1, rune);
        }
        else {
            flag = in.context(pos);
        }
        while (true) {
            if (runq.isEmpty()) {
                if ((startCond & 0x4) != 0x0 && pos != 0) {
                    break;
                }
                if (this.matched) {
                    break;
                }
                if (!this.re2.prefix.isEmpty() && rune2 != this.re2.prefixRune && in.canCheckPrefix()) {
                    final int advance = in.index(this.re2, pos);
                    if (advance < 0) {
                        break;
                    }
                    pos += advance;
                    r = in.step(pos);
                    rune = r >> 3;
                    width = (r & 0x7);
                    r = in.step(pos + width);
                    rune2 = r >> 3;
                    width2 = (r & 0x7);
                }
            }
            if (!this.matched && (pos == 0 || anchor == 0)) {
                if (this.matchcap.length > 0) {
                    this.matchcap[0] = pos;
                }
                this.add(runq, this.prog.start, pos, this.matchcap, flag, null);
            }
            flag = Utils.emptyOpContext(rune, rune2);
            this.step(runq, nextq, pos, pos + width, rune, flag, anchor, pos == in.endPos());
            if (width == 0) {
                break;
            }
            if (this.matchcap.length == 0 && this.matched) {
                break;
            }
            pos += width;
            rune = rune2;
            width = width2;
            if (rune != -1) {
                r = in.step(pos + width);
                rune2 = r >> 3;
                width2 = (r & 0x7);
            }
            final Queue tmpq = runq;
            runq = nextq;
            nextq = tmpq;
        }
        nextq.clear(this.pool);
        return this.matched;
    }
    
    private void step(final Queue runq, final Queue nextq, final int pos, final int nextPos, final int c, final int nextCond, final int anchor, final boolean atEnd) {
        final boolean longest = this.re2.longest;
        for (int j = 0; j < runq.size; ++j) {
            final Queue.Entry entry = runq.dense[j];
            if (entry != null) {
                Thread t = entry.thread;
                if (t != null) {
                    if (longest && this.matched && t.cap.length > 0 && this.matchcap[0] < t.cap[0]) {
                        this.pool.add(t);
                    }
                    else {
                        final Inst i = t.inst;
                        boolean add = false;
                        switch (i.op) {
                            case MATCH: {
                                if (anchor == 2 && !atEnd) {
                                    break;
                                }
                                if (t.cap.length > 0 && (!longest || !this.matched || this.matchcap[1] < pos)) {
                                    t.cap[1] = pos;
                                    System.arraycopy(t.cap, 0, this.matchcap, 0, t.cap.length);
                                }
                                if (!longest) {
                                    for (int k = j + 1; k < runq.size; ++k) {
                                        final Queue.Entry d = runq.dense[k];
                                        if (d.thread != null) {
                                            this.pool.add(d.thread);
                                        }
                                    }
                                    runq.size = 0;
                                }
                                this.matched = true;
                                break;
                            }
                            case RUNE: {
                                add = i.matchRune(c);
                                break;
                            }
                            case RUNE1: {
                                add = (c == i.runes[0]);
                                break;
                            }
                            case RUNE_ANY: {
                                add = true;
                                break;
                            }
                            case RUNE_ANY_NOT_NL: {
                                add = (c != 10);
                                break;
                            }
                            default: {
                                throw new IllegalStateException("bad inst");
                            }
                        }
                        if (add) {
                            t = this.add(nextq, i.out, nextPos, t.cap, nextCond, t);
                        }
                        if (t != null) {
                            this.pool.add(t);
                        }
                    }
                }
            }
        }
        runq.size = 0;
    }
    
    private Thread add(final Queue q, final int pc, final int pos, final int[] cap, final int cond, Thread t) {
        if (pc == 0) {
            return t;
        }
        if (q.contains(pc)) {
            return t;
        }
        final Queue.Entry d = q.add(pc);
        final Inst inst = this.prog.getInst(pc);
        switch (inst.op()) {
            default: {
                throw new IllegalStateException("unhandled");
            }
            case FAIL: {
                break;
            }
            case ALT:
            case ALT_MATCH: {
                t = this.add(q, inst.out, pos, cap, cond, t);
                t = this.add(q, inst.arg, pos, cap, cond, t);
                break;
            }
            case EMPTY_WIDTH: {
                if ((inst.arg & ~cond) == 0x0) {
                    t = this.add(q, inst.out, pos, cap, cond, t);
                    break;
                }
                break;
            }
            case NOP: {
                t = this.add(q, inst.out, pos, cap, cond, t);
                break;
            }
            case CAPTURE: {
                if (inst.arg < cap.length) {
                    final int opos = cap[inst.arg];
                    cap[inst.arg] = pos;
                    this.add(q, inst.out, pos, cap, cond, null);
                    cap[inst.arg] = opos;
                    break;
                }
                t = this.add(q, inst.out, pos, cap, cond, t);
                break;
            }
            case MATCH:
            case RUNE:
            case RUNE1:
            case RUNE_ANY:
            case RUNE_ANY_NOT_NL: {
                if (t == null) {
                    t = this.alloc(inst);
                }
                else {
                    t.inst = inst;
                }
                if (cap.length > 0 && t.cap != cap) {
                    System.arraycopy(cap, 0, t.cap, 0, cap.length);
                }
                d.thread = t;
                t = null;
                break;
            }
        }
        return t;
    }
    
    private static class Thread
    {
        int[] cap;
        Inst inst;
        
        Thread(final int n) {
            this.cap = new int[n];
        }
    }
    
    private static class Queue
    {
        final Entry[] dense;
        final int[] sparse;
        int size;
        
        Queue(final int n) {
            this.sparse = new int[n];
            this.dense = new Entry[n];
        }
        
        boolean contains(final int pc) {
            final int j = this.sparse[pc];
            if (j >= this.size) {
                return false;
            }
            final Entry d = this.dense[j];
            return d != null && d.pc == pc;
        }
        
        boolean isEmpty() {
            return this.size == 0;
        }
        
        Entry add(final int pc) {
            final int j = this.size++;
            this.sparse[pc] = j;
            Entry e = this.dense[j];
            if (e == null) {
                final Entry[] dense = this.dense;
                final int n = j;
                final Entry entry = new Entry();
                dense[n] = entry;
                e = entry;
            }
            e.thread = null;
            e.pc = pc;
            return e;
        }
        
        void clear(final List<Thread> freePool) {
            for (int i = 0; i < this.size; ++i) {
                final Entry entry = this.dense[i];
                if (entry != null && entry.thread != null) {
                    freePool.add(entry.thread);
                }
            }
            this.size = 0;
        }
        
        @Override
        public String toString() {
            final StringBuilder out = new StringBuilder();
            out.append('{');
            for (int i = 0; i < this.size; ++i) {
                if (i != 0) {
                    out.append(", ");
                }
                out.append(this.dense[i].pc);
            }
            out.append('}');
            return out.toString();
        }
        
        static class Entry
        {
            int pc;
            Thread thread;
        }
    }
}
