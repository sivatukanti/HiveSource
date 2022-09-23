// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;

public class Quantifier extends EnumeratedAttribute
{
    private static final String[] VALUES;
    public static final Quantifier ALL;
    public static final Quantifier ANY;
    public static final Quantifier ONE;
    public static final Quantifier MAJORITY;
    public static final Quantifier NONE;
    private static final Predicate ALL_PRED;
    private static final Predicate ANY_PRED;
    private static final Predicate ONE_PRED;
    private static final Predicate MAJORITY_PRED;
    private static final Predicate NONE_PRED;
    private static final Predicate[] PREDS;
    
    public Quantifier() {
    }
    
    public Quantifier(final String value) {
        this.setValue(value);
    }
    
    @Override
    public String[] getValues() {
        return Quantifier.VALUES;
    }
    
    public boolean evaluate(final boolean[] b) {
        int t = 0;
        for (int i = 0; i < b.length; ++i) {
            if (b[i]) {
                ++t;
            }
        }
        return this.evaluate(t, b.length - t);
    }
    
    public boolean evaluate(final int t, final int f) {
        final int index = this.getIndex();
        if (index == -1) {
            throw new BuildException("Quantifier value not set.");
        }
        return Quantifier.PREDS[index].eval(t, f);
    }
    
    static {
        VALUES = new String[] { "all", "each", "every", "any", "some", "one", "majority", "most", "none" };
        ALL = new Quantifier("all");
        ANY = new Quantifier("any");
        ONE = new Quantifier("one");
        MAJORITY = new Quantifier("majority");
        NONE = new Quantifier("none");
        ALL_PRED = new Predicate() {
            @Override
            boolean eval(final int t, final int f) {
                return f == 0;
            }
        };
        ANY_PRED = new Predicate() {
            @Override
            boolean eval(final int t, final int f) {
                return t > 0;
            }
        };
        ONE_PRED = new Predicate() {
            @Override
            boolean eval(final int t, final int f) {
                return t == 1;
            }
        };
        MAJORITY_PRED = new Predicate() {
            @Override
            boolean eval(final int t, final int f) {
                return t > f;
            }
        };
        NONE_PRED = new Predicate() {
            @Override
            boolean eval(final int t, final int f) {
                return t == 0;
            }
        };
        (PREDS = new Predicate[Quantifier.VALUES.length])[0] = Quantifier.ALL_PRED;
        Quantifier.PREDS[1] = Quantifier.ALL_PRED;
        Quantifier.PREDS[2] = Quantifier.ALL_PRED;
        Quantifier.PREDS[3] = Quantifier.ANY_PRED;
        Quantifier.PREDS[4] = Quantifier.ANY_PRED;
        Quantifier.PREDS[5] = Quantifier.ONE_PRED;
        Quantifier.PREDS[6] = Quantifier.MAJORITY_PRED;
        Quantifier.PREDS[7] = Quantifier.MAJORITY_PRED;
        Quantifier.PREDS[8] = Quantifier.NONE_PRED;
    }
    
    private abstract static class Predicate
    {
        abstract boolean eval(final int p0, final int p1);
    }
}
