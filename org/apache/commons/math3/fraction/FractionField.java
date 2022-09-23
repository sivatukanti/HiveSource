// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.fraction;

import org.apache.commons.math3.FieldElement;
import java.io.Serializable;
import org.apache.commons.math3.Field;

public class FractionField implements Field<Fraction>, Serializable
{
    private static final long serialVersionUID = -1257768487499119313L;
    
    private FractionField() {
    }
    
    public static FractionField getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public Fraction getOne() {
        return Fraction.ONE;
    }
    
    public Fraction getZero() {
        return Fraction.ZERO;
    }
    
    public Class<? extends FieldElement<Fraction>> getRuntimeClass() {
        return Fraction.class;
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final FractionField INSTANCE;
        
        static {
            INSTANCE = new FractionField(null);
        }
    }
}
