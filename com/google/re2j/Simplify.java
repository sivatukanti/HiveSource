// 
// Decompiled by Procyon v0.5.36
// 

package com.google.re2j;

import java.util.ArrayList;

class Simplify
{
    static Regexp simplify(final Regexp re) {
        if (re == null) {
            return null;
        }
        switch (re.op) {
            case CAPTURE:
            case CONCAT:
            case ALTERNATE: {
                Regexp nre = re;
                for (int i = 0; i < re.subs.length; ++i) {
                    final Regexp sub = re.subs[i];
                    final Regexp nsub = simplify(sub);
                    if (nre == re && nsub != sub) {
                        nre = new Regexp(re);
                        nre.runes = null;
                        nre.subs = Parser.subarray(re.subs, 0, re.subs.length);
                    }
                    if (nre != re) {
                        nre.subs[i] = nsub;
                    }
                }
                return nre;
            }
            case STAR:
            case PLUS:
            case QUEST: {
                final Regexp sub2 = simplify(re.subs[0]);
                return simplify1(re.op, re.flags, sub2, re);
            }
            case REPEAT: {
                if (re.min == 0 && re.max == 0) {
                    return new Regexp(Regexp.Op.EMPTY_MATCH);
                }
                final Regexp sub2 = simplify(re.subs[0]);
                if (re.max == -1) {
                    if (re.min == 0) {
                        return simplify1(Regexp.Op.STAR, re.flags, sub2, null);
                    }
                    if (re.min == 1) {
                        return simplify1(Regexp.Op.PLUS, re.flags, sub2, null);
                    }
                    final Regexp nre2 = new Regexp(Regexp.Op.CONCAT);
                    final ArrayList<Regexp> subs = new ArrayList<Regexp>();
                    for (int j = 0; j < re.min - 1; ++j) {
                        subs.add(sub2);
                    }
                    subs.add(simplify1(Regexp.Op.PLUS, re.flags, sub2, null));
                    nre2.subs = subs.toArray(new Regexp[subs.size()]);
                    return nre2;
                }
                else {
                    if (re.min == 1 && re.max == 1) {
                        return sub2;
                    }
                    ArrayList<Regexp> prefixSubs = null;
                    if (re.min > 0) {
                        prefixSubs = new ArrayList<Regexp>();
                        for (int k = 0; k < re.min; ++k) {
                            prefixSubs.add(sub2);
                        }
                    }
                    if (re.max > re.min) {
                        Regexp suffix = simplify1(Regexp.Op.QUEST, re.flags, sub2, null);
                        for (int j = re.min + 1; j < re.max; ++j) {
                            final Regexp nre3 = new Regexp(Regexp.Op.CONCAT);
                            nre3.subs = new Regexp[] { sub2, suffix };
                            suffix = simplify1(Regexp.Op.QUEST, re.flags, nre3, null);
                        }
                        if (prefixSubs == null) {
                            return suffix;
                        }
                        prefixSubs.add(suffix);
                    }
                    if (prefixSubs != null) {
                        final Regexp prefix = new Regexp(Regexp.Op.CONCAT);
                        prefix.subs = prefixSubs.toArray(new Regexp[prefixSubs.size()]);
                        return prefix;
                    }
                    return new Regexp(Regexp.Op.NO_MATCH);
                }
                break;
            }
            default: {
                return re;
            }
        }
    }
    
    private static Regexp simplify1(final Regexp.Op op, final int flags, final Regexp sub, Regexp re) {
        if (sub.op == Regexp.Op.EMPTY_MATCH) {
            return sub;
        }
        if (op == sub.op && (flags & 0x20) == (sub.flags & 0x20)) {
            return sub;
        }
        if (re != null && re.op == op && (re.flags & 0x20) == (flags & 0x20) && sub == re.subs[0]) {
            return re;
        }
        re = new Regexp(op);
        re.flags = flags;
        re.subs = new Regexp[] { sub };
        return re;
    }
    
    private Simplify() {
    }
}
