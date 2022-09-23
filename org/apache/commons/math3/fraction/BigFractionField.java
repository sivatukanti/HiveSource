// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.fraction;

import org.apache.commons.math3.FieldElement;
import java.io.Serializable;
import org.apache.commons.math3.Field;

public class BigFractionField implements Field<BigFraction>, Serializable
{
    private static final long serialVersionUID = -1699294557189741703L;
    
    private BigFractionField() {
    }
    
    public static BigFractionField getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public BigFraction getOne() {
        return BigFraction.ONE;
    }
    
    public BigFraction getZero() {
        return BigFraction.ZERO;
    }
    
    public Class<? extends FieldElement<BigFraction>> getRuntimeClass() {
        return BigFraction.class;
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final BigFractionField INSTANCE;
        
        static {
            INSTANCE = new BigFractionField(null);
        }
    }
}
