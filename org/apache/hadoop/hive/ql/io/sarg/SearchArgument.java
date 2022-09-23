// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.ql.io.sarg;

import parquet.filter2.predicate.FilterPredicate;
import java.util.List;

public interface SearchArgument
{
    List<PredicateLeaf> getLeaves();
    
    TruthValue evaluate(final TruthValue[] p0);
    
    String toKryo();
    
    FilterPredicate toFilterPredicate();
    
    public enum TruthValue
    {
        YES, 
        NO, 
        NULL, 
        YES_NULL, 
        NO_NULL, 
        YES_NO, 
        YES_NO_NULL;
        
        public TruthValue or(final TruthValue right) {
            if (right == null || right == this) {
                return this;
            }
            if (right == TruthValue.YES || this == TruthValue.YES) {
                return TruthValue.YES;
            }
            if (right == TruthValue.YES_NULL || this == TruthValue.YES_NULL) {
                return TruthValue.YES_NULL;
            }
            if (right == TruthValue.NO) {
                return this;
            }
            if (this == TruthValue.NO) {
                return right;
            }
            if (this == TruthValue.NULL) {
                if (right == TruthValue.NO_NULL) {
                    return TruthValue.NULL;
                }
                return TruthValue.YES_NULL;
            }
            else {
                if (right != TruthValue.NULL) {
                    return TruthValue.YES_NO_NULL;
                }
                if (this == TruthValue.NO_NULL) {
                    return TruthValue.NULL;
                }
                return TruthValue.YES_NULL;
            }
        }
        
        public TruthValue and(final TruthValue right) {
            if (right == null || right == this) {
                return this;
            }
            if (right == TruthValue.NO || this == TruthValue.NO) {
                return TruthValue.NO;
            }
            if (right == TruthValue.NO_NULL || this == TruthValue.NO_NULL) {
                return TruthValue.NO_NULL;
            }
            if (right == TruthValue.YES) {
                return this;
            }
            if (this == TruthValue.YES) {
                return right;
            }
            if (this == TruthValue.NULL) {
                if (right == TruthValue.YES_NULL) {
                    return TruthValue.NULL;
                }
                return TruthValue.NO_NULL;
            }
            else {
                if (right != TruthValue.NULL) {
                    return TruthValue.YES_NO_NULL;
                }
                if (this == TruthValue.YES_NULL) {
                    return TruthValue.NULL;
                }
                return TruthValue.NO_NULL;
            }
        }
        
        public TruthValue not() {
            switch (this) {
                case NO: {
                    return TruthValue.YES;
                }
                case YES: {
                    return TruthValue.NO;
                }
                case NULL:
                case YES_NO:
                case YES_NO_NULL: {
                    return this;
                }
                case NO_NULL: {
                    return TruthValue.YES_NULL;
                }
                case YES_NULL: {
                    return TruthValue.NO_NULL;
                }
                default: {
                    throw new IllegalArgumentException("Unknown value: " + this);
                }
            }
        }
        
        public boolean isNeeded() {
            switch (this) {
                case NO:
                case NULL:
                case NO_NULL: {
                    return false;
                }
                default: {
                    return true;
                }
            }
        }
    }
    
    public interface Builder
    {
        Builder startOr();
        
        Builder startAnd();
        
        Builder startNot();
        
        Builder end();
        
        Builder lessThan(final String p0, final Object p1);
        
        Builder lessThanEquals(final String p0, final Object p1);
        
        Builder equals(final String p0, final Object p1);
        
        Builder nullSafeEquals(final String p0, final Object p1);
        
        Builder in(final String p0, final Object... p1);
        
        Builder isNull(final String p0);
        
        Builder between(final String p0, final Object p1, final Object p2);
        
        SearchArgument build();
    }
}
