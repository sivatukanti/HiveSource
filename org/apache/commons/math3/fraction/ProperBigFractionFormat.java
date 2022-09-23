// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.fraction;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.text.ParsePosition;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;

public class ProperBigFractionFormat extends BigFractionFormat
{
    private static final long serialVersionUID = -6337346779577272307L;
    private NumberFormat wholeFormat;
    
    public ProperBigFractionFormat() {
        this(AbstractFormat.getDefaultNumberFormat());
    }
    
    public ProperBigFractionFormat(final NumberFormat format) {
        this(format, (NumberFormat)format.clone(), (NumberFormat)format.clone());
    }
    
    public ProperBigFractionFormat(final NumberFormat wholeFormat, final NumberFormat numeratorFormat, final NumberFormat denominatorFormat) {
        super(numeratorFormat, denominatorFormat);
        this.setWholeFormat(wholeFormat);
    }
    
    @Override
    public StringBuffer format(final BigFraction fraction, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        BigInteger num = fraction.getNumerator();
        final BigInteger den = fraction.getDenominator();
        final BigInteger whole = num.divide(den);
        num = num.remainder(den);
        if (!BigInteger.ZERO.equals(whole)) {
            this.getWholeFormat().format(whole, toAppendTo, pos);
            toAppendTo.append(' ');
            if (num.compareTo(BigInteger.ZERO) < 0) {
                num = num.negate();
            }
        }
        this.getNumeratorFormat().format(num, toAppendTo, pos);
        toAppendTo.append(" / ");
        this.getDenominatorFormat().format(den, toAppendTo, pos);
        return toAppendTo;
    }
    
    public NumberFormat getWholeFormat() {
        return this.wholeFormat;
    }
    
    @Override
    public BigFraction parse(final String source, final ParsePosition pos) {
        final BigFraction ret = super.parse(source, pos);
        if (ret != null) {
            return ret;
        }
        final int initialIndex = pos.getIndex();
        AbstractFormat.parseAndIgnoreWhitespace(source, pos);
        BigInteger whole = this.parseNextBigInteger(source, pos);
        if (whole == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        AbstractFormat.parseAndIgnoreWhitespace(source, pos);
        BigInteger num = this.parseNextBigInteger(source, pos);
        if (num == null) {
            pos.setIndex(initialIndex);
            return null;
        }
        if (num.compareTo(BigInteger.ZERO) < 0) {
            pos.setIndex(initialIndex);
            return null;
        }
        final int startIndex = pos.getIndex();
        final char c = AbstractFormat.parseNextCharacter(source, pos);
        switch (c) {
            case '\0': {
                return new BigFraction(num);
            }
            case '/': {
                AbstractFormat.parseAndIgnoreWhitespace(source, pos);
                final BigInteger den = this.parseNextBigInteger(source, pos);
                if (den == null) {
                    pos.setIndex(initialIndex);
                    return null;
                }
                if (den.compareTo(BigInteger.ZERO) < 0) {
                    pos.setIndex(initialIndex);
                    return null;
                }
                final boolean wholeIsNeg = whole.compareTo(BigInteger.ZERO) < 0;
                if (wholeIsNeg) {
                    whole = whole.negate();
                }
                num = whole.multiply(den).add(num);
                if (wholeIsNeg) {
                    num = num.negate();
                }
                return new BigFraction(num, den);
            }
            default: {
                pos.setIndex(initialIndex);
                pos.setErrorIndex(startIndex);
                return null;
            }
        }
    }
    
    public void setWholeFormat(final NumberFormat format) {
        if (format == null) {
            throw new NullArgumentException(LocalizedFormats.WHOLE_FORMAT, new Object[0]);
        }
        this.wholeFormat = format;
    }
}
