// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.error.StandardException;
import java.math.BigDecimal;

public abstract class NumberDataType extends DataType implements NumberDataValue
{
    static DataValueDescriptor ZERO_DECIMAL;
    static final BigDecimal ZERO;
    static final BigDecimal ONE;
    static final BigDecimal MAXLONG_PLUS_ONE;
    static final BigDecimal MINLONG_MINUS_ONE;
    
    public final NumberDataValue absolute(NumberDataValue numberDataValue) throws StandardException {
        if (this.isNegative()) {
            return this.minus(numberDataValue);
        }
        if (numberDataValue == null) {
            numberDataValue = (NumberDataValue)this.getNewNull();
        }
        numberDataValue.setValue(this);
        return numberDataValue;
    }
    
    public NumberDataValue sqrt(NumberDataValue numberDataValue) throws StandardException {
        if (numberDataValue == null) {
            numberDataValue = (NumberDataValue)this.getNewNull();
        }
        if (this.isNull()) {
            numberDataValue.setToNull();
            return numberDataValue;
        }
        double double1 = this.getDouble();
        if (this.isNegative()) {
            if (!new Double(double1).equals(new Double(-0.0))) {
                throw StandardException.newException("22013", this);
            }
            double1 = 0.0;
        }
        numberDataValue.setValue(Math.sqrt(double1));
        return numberDataValue;
    }
    
    public NumberDataValue plus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final int int1 = numberDataValue.getInt();
        final int int2 = numberDataValue2.getInt();
        final int value = int1 + int2;
        if (int1 < 0 == int2 < 0 && int1 < 0 != value < 0) {
            throw this.outOfRange();
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue minus(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final int value = numberDataValue.getInt() - numberDataValue2.getInt();
        if (numberDataValue.getInt() < 0 != numberDataValue2.getInt() < 0 && numberDataValue.getInt() < 0 != value < 0) {
            throw this.outOfRange();
        }
        numberDataValue3.setValue(value);
        return numberDataValue3;
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, NumberDataValue numberDataValue3) throws StandardException {
        if (numberDataValue3 == null) {
            numberDataValue3 = (NumberDataValue)this.getNewNull();
        }
        if (numberDataValue.isNull() || numberDataValue2.isNull()) {
            numberDataValue3.setToNull();
            return numberDataValue3;
        }
        final int int1 = numberDataValue2.getInt();
        if (int1 == 0) {
            throw StandardException.newException("22012");
        }
        numberDataValue3.setValue(numberDataValue.getInt() / int1);
        return numberDataValue3;
    }
    
    public NumberDataValue divide(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3, final int n) throws StandardException {
        return this.divide(numberDataValue, numberDataValue2, numberDataValue3);
    }
    
    public NumberDataValue mod(final NumberDataValue numberDataValue, final NumberDataValue numberDataValue2, final NumberDataValue numberDataValue3) throws StandardException {
        return null;
    }
    
    public final int compare(final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (this.typePrecedence() < dataValueDescriptor.typePrecedence()) {
            return -dataValueDescriptor.compare(this);
        }
        final boolean null = this.isNull();
        final boolean null2 = dataValueDescriptor.isNull();
        if (!null && !null2) {
            return this.typeCompare(dataValueDescriptor);
        }
        if (!null) {
            return -1;
        }
        if (!null2) {
            return 1;
        }
        return 0;
    }
    
    protected abstract int typeCompare(final DataValueDescriptor p0) throws StandardException;
    
    public final boolean compare(final int n, final DataValueDescriptor dataValueDescriptor, final boolean b, final boolean b2) throws StandardException {
        if (!b && (this.isNull() || dataValueDescriptor.isNull())) {
            return b2;
        }
        return super.compare(n, dataValueDescriptor, b, b2);
    }
    
    protected abstract boolean isNegative();
    
    public void setValue(final short value) throws StandardException {
        this.setValue((int)value);
    }
    
    public void setValue(final byte value) throws StandardException {
        this.setValue((int)value);
    }
    
    public void setValue(final Number n) throws StandardException {
        if (this.objectNull(n)) {
            return;
        }
        this.setValue(n.intValue());
    }
    
    void setObject(final Object o) throws StandardException {
        this.setValue((int)o);
    }
    
    public void setBigDecimal(final Number n) throws StandardException {
        if (this.objectNull(n)) {
            return;
        }
        final Comparable comparable = (Comparable)n;
        if (comparable.compareTo(NumberDataType.MINLONG_MINUS_ONE) == 1 && comparable.compareTo(NumberDataType.MAXLONG_PLUS_ONE) == -1) {
            this.setValue(n.longValue());
            return;
        }
        throw StandardException.newException("22003", this.getTypeName());
    }
    
    public int typeToBigDecimal() {
        return -5;
    }
    
    public int getDecimalValuePrecision() {
        return -1;
    }
    
    public int getDecimalValueScale() {
        return -1;
    }
    
    protected final boolean objectNull(final Object o) {
        if (o == null) {
            this.restoreToNull();
            return true;
        }
        return false;
    }
    
    public static float normalizeREAL(float n) throws StandardException {
        boolean b = Float.isNaN(n) || Float.isInfinite(n);
        if ((n < -3.402E38f || n > 3.402E38f || (n > 0.0f && n < 1.175E-37f) || (n < 0.0f && n > -1.175E-37f)) && useDB2Limits()) {
            b = true;
        }
        if (b) {
            throw StandardException.newException("22003", "REAL");
        }
        if (n == -0.0f) {
            n = 0.0f;
        }
        return n;
    }
    
    public static float normalizeREAL(final double n) throws StandardException {
        float n2 = (float)n;
        boolean b = Double.isNaN(n) || Double.isInfinite(n) || (n2 == 0.0f && n != 0.0);
        if ((n < -3.4020000005553803E38 || n > 3.4020000005553803E38 || (n > 0.0 && n < 1.1749999727240737E-37) || (n < 0.0 && n > -1.1749999727240737E-37)) && useDB2Limits()) {
            b = true;
        }
        if (b) {
            throw StandardException.newException("22003", "REAL");
        }
        if (n2 == -0.0f) {
            n2 = 0.0f;
        }
        return n2;
    }
    
    public static double normalizeDOUBLE(double n) throws StandardException {
        boolean b = Double.isNaN(n) || Double.isInfinite(n);
        if ((n < -1.79769E308 || n > 1.79769E308 || (n > 0.0 && n < 2.225E-307) || (n < 0.0 && n > -2.225E-307)) && useDB2Limits()) {
            b = true;
        }
        if (b) {
            throw StandardException.newException("22003", "DOUBLE");
        }
        if (n == -0.0) {
            n = 0.0;
        }
        return n;
    }
    
    private static boolean useDB2Limits() throws StandardException {
        final LanguageConnectionContext languageConnectionContext = (LanguageConnectionContext)ContextService.getContextOrNull("LanguageConnectionContext");
        return languageConnectionContext != null && !languageConnectionContext.getDataDictionary().checkVersion(220, null);
    }
    
    static {
        ZERO = BigDecimal.valueOf(0L);
        ONE = BigDecimal.valueOf(1L);
        MAXLONG_PLUS_ONE = BigDecimal.valueOf(Long.MAX_VALUE).add(NumberDataType.ONE);
        MINLONG_MINUS_ONE = BigDecimal.valueOf(Long.MIN_VALUE).subtract(NumberDataType.ONE);
    }
}
