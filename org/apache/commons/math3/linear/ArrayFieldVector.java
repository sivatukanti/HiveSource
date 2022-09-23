// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.lang.reflect.Array;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Arrays;
import org.apache.commons.math3.Field;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class ArrayFieldVector<T extends FieldElement<T>> implements FieldVector<T>, Serializable
{
    private static final long serialVersionUID = 7648186910365927050L;
    private T[] data;
    private final Field<T> field;
    
    public ArrayFieldVector(final Field<T> field) {
        this(field, 0);
    }
    
    public ArrayFieldVector(final Field<T> field, final int size) {
        this.field = field;
        Arrays.fill(this.data = this.buildArray(size), field.getZero());
    }
    
    public ArrayFieldVector(final int size, final T preset) {
        this(preset.getField(), size);
        Arrays.fill(this.data, preset);
    }
    
    public ArrayFieldVector(final T[] d) throws NullArgumentException, ZeroException {
        if (d == null) {
            throw new NullArgumentException();
        }
        try {
            this.field = d[0].getField();
            this.data = d.clone();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] d) throws NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        }
        this.field = field;
        this.data = d.clone();
    }
    
    public ArrayFieldVector(final T[] d, final boolean copyArray) throws NullArgumentException, ZeroException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        this.field = d[0].getField();
        this.data = (copyArray ? d.clone() : d);
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] d, final boolean copyArray) throws NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        }
        this.field = field;
        this.data = (copyArray ? d.clone() : d);
    }
    
    public ArrayFieldVector(final T[] d, final int pos, final int size) throws NullArgumentException, NumberIsTooLargeException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        this.field = d[0].getField();
        System.arraycopy(d, pos, this.data = this.buildArray(size), 0, size);
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] d, final int pos, final int size) throws NullArgumentException, NumberIsTooLargeException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length < pos + size) {
            throw new NumberIsTooLargeException(pos + size, d.length, true);
        }
        this.field = field;
        System.arraycopy(d, pos, this.data = this.buildArray(size), 0, size);
    }
    
    public ArrayFieldVector(final FieldVector<T> v) throws NullArgumentException {
        if (v == null) {
            throw new NullArgumentException();
        }
        this.field = v.getField();
        this.data = this.buildArray(v.getDimension());
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = v.getEntry(i);
        }
    }
    
    public ArrayFieldVector(final ArrayFieldVector<T> v) throws NullArgumentException {
        if (v == null) {
            throw new NullArgumentException();
        }
        this.field = v.getField();
        this.data = v.data.clone();
    }
    
    public ArrayFieldVector(final ArrayFieldVector<T> v, final boolean deep) throws NullArgumentException {
        if (v == null) {
            throw new NullArgumentException();
        }
        this.field = v.getField();
        this.data = (deep ? v.data.clone() : v.data);
    }
    
    public ArrayFieldVector(final ArrayFieldVector<T> v1, final ArrayFieldVector<T> v2) throws NullArgumentException {
        if (v1 == null || v2 == null) {
            throw new NullArgumentException();
        }
        this.field = v1.getField();
        this.data = this.buildArray(v1.data.length + v2.data.length);
        System.arraycopy(v1.data, 0, this.data, 0, v1.data.length);
        System.arraycopy(v2.data, 0, this.data, v1.data.length, v2.data.length);
    }
    
    public ArrayFieldVector(final ArrayFieldVector<T> v1, final T[] v2) throws NullArgumentException {
        if (v1 == null || v2 == null) {
            throw new NullArgumentException();
        }
        this.field = v1.getField();
        this.data = this.buildArray(v1.data.length + v2.length);
        System.arraycopy(v1.data, 0, this.data, 0, v1.data.length);
        System.arraycopy(v2, 0, this.data, v1.data.length, v2.length);
    }
    
    public ArrayFieldVector(final T[] v1, final ArrayFieldVector<T> v2) throws NullArgumentException {
        if (v1 == null || v2 == null) {
            throw new NullArgumentException();
        }
        this.field = v2.getField();
        System.arraycopy(v1, 0, this.data = this.buildArray(v1.length + v2.data.length), 0, v1.length);
        System.arraycopy(v2.data, 0, this.data, v1.length, v2.data.length);
    }
    
    public ArrayFieldVector(final T[] v1, final T[] v2) throws NullArgumentException, ZeroException {
        if (v1 == null || v2 == null) {
            throw new NullArgumentException();
        }
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        System.arraycopy(v1, 0, this.data = this.buildArray(v1.length + v2.length), 0, v1.length);
        System.arraycopy(v2, 0, this.data, v1.length, v2.length);
        this.field = this.data[0].getField();
    }
    
    public ArrayFieldVector(final Field<T> field, final T[] v1, final T[] v2) throws NullArgumentException, ZeroException {
        if (v1 == null || v2 == null) {
            throw new NullArgumentException();
        }
        if (v1.length + v2.length == 0) {
            throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
        }
        System.arraycopy(v1, 0, this.data = this.buildArray(v1.length + v2.length), 0, v1.length);
        System.arraycopy(v2, 0, this.data, v1.length, v2.length);
        this.field = field;
    }
    
    private T[] buildArray(final int length) {
        return (T[])Array.newInstance(this.field.getRuntimeClass(), length);
    }
    
    public Field<T> getField() {
        return this.field;
    }
    
    public FieldVector<T> copy() {
        return new ArrayFieldVector((ArrayFieldVector<FieldElement>)this, true);
    }
    
    public FieldVector<T> add(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.add((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = this.buildArray(this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                out[i] = this.data[i].add(v.getEntry(i));
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> add(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].add(v.data[i]);
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public FieldVector<T> subtract(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.subtract((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = this.buildArray(this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                out[i] = this.data[i].subtract(v.getEntry(i));
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> subtract(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].subtract(v.data[i]);
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public FieldVector<T> mapAdd(final T d) throws NullArgumentException {
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].add(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapAddToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].add(d);
        }
        return this;
    }
    
    public FieldVector<T> mapSubtract(final T d) throws NullArgumentException {
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].subtract(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapSubtractToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].subtract(d);
        }
        return this;
    }
    
    public FieldVector<T> mapMultiply(final T d) throws NullArgumentException {
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].multiply(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapMultiplyToSelf(final T d) throws NullArgumentException {
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].multiply(d);
        }
        return this;
    }
    
    public FieldVector<T> mapDivide(final T d) throws NullArgumentException, MathArithmeticException {
        if (d == null) {
            throw new NullArgumentException();
        }
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].divide(d);
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapDivideToSelf(final T d) throws NullArgumentException, MathArithmeticException {
        if (d == null) {
            throw new NullArgumentException();
        }
        for (int i = 0; i < this.data.length; ++i) {
            this.data[i] = this.data[i].divide(d);
        }
        return this;
    }
    
    public FieldVector<T> mapInv() throws MathArithmeticException {
        final T[] out = this.buildArray(this.data.length);
        final T one = this.field.getOne();
        for (int i = 0; i < this.data.length; ++i) {
            try {
                out[i] = one.divide(this.data[i]);
            }
            catch (MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
            }
        }
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> mapInvToSelf() throws MathArithmeticException {
        final T one = this.field.getOne();
        for (int i = 0; i < this.data.length; ++i) {
            try {
                this.data[i] = one.divide(this.data[i]);
            }
            catch (MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
            }
        }
        return this;
    }
    
    public FieldVector<T> ebeMultiply(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.ebeMultiply((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = this.buildArray(this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                out[i] = this.data[i].multiply(v.getEntry(i));
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> ebeMultiply(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            out[i] = this.data[i].multiply(v.data[i]);
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public FieldVector<T> ebeDivide(final FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        try {
            return this.ebeDivide((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            this.checkVectorDimensions(v);
            final T[] out = this.buildArray(this.data.length);
            for (int i = 0; i < this.data.length; ++i) {
                try {
                    out[i] = this.data[i].divide(v.getEntry(i));
                }
                catch (MathArithmeticException e) {
                    throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
                }
            }
            return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
        }
    }
    
    public ArrayFieldVector<T> ebeDivide(final ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        this.checkVectorDimensions(v.data.length);
        final T[] out = this.buildArray(this.data.length);
        for (int i = 0; i < this.data.length; ++i) {
            try {
                out[i] = this.data[i].divide(v.data[i]);
            }
            catch (MathArithmeticException e) {
                throw new MathArithmeticException(LocalizedFormats.INDEX, new Object[] { i });
            }
        }
        return new ArrayFieldVector<T>(this.field, out, false);
    }
    
    public T[] getData() {
        return this.data.clone();
    }
    
    public T[] getDataRef() {
        return this.data;
    }
    
    public T dotProduct(final FieldVector<T> v) throws DimensionMismatchException {
        try {
            return this.dotProduct((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            this.checkVectorDimensions(v);
            T dot = this.field.getZero();
            for (int i = 0; i < this.data.length; ++i) {
                dot = dot.add(this.data[i].multiply(v.getEntry(i)));
            }
            return dot;
        }
    }
    
    public T dotProduct(final ArrayFieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.data.length);
        T dot = this.field.getZero();
        for (int i = 0; i < this.data.length; ++i) {
            dot = dot.add(this.data[i].multiply(v.data[i]));
        }
        return dot;
    }
    
    public FieldVector<T> projection(final FieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        return v.mapMultiply((T)this.dotProduct((FieldVector<FieldElement<FieldElement<FieldElement<FieldElement>>>>)v).divide((FieldElement<FieldElement<FieldElement<T>>>)v.dotProduct(v)));
    }
    
    public ArrayFieldVector<T> projection(final ArrayFieldVector<T> v) throws DimensionMismatchException, MathArithmeticException {
        return (ArrayFieldVector)v.mapMultiply((T)this.dotProduct((ArrayFieldVector<FieldElement<FieldElement<FieldElement<FieldElement>>>>)v).divide((FieldElement<FieldElement<FieldElement<T>>>)v.dotProduct(v)));
    }
    
    public FieldMatrix<T> outerProduct(final FieldVector<T> v) {
        try {
            return this.outerProduct((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            final int m = this.data.length;
            final int n = v.getDimension();
            final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(this.field, m, n);
            for (int i = 0; i < m; ++i) {
                for (int j = 0; j < n; ++j) {
                    out.setEntry(i, j, this.data[i].multiply(v.getEntry(j)));
                }
            }
            return out;
        }
    }
    
    public FieldMatrix<T> outerProduct(final ArrayFieldVector<T> v) {
        final int m = this.data.length;
        final int n = v.data.length;
        final FieldMatrix<T> out = new Array2DRowFieldMatrix<T>(this.field, m, n);
        for (int i = 0; i < m; ++i) {
            for (int j = 0; j < n; ++j) {
                out.setEntry(i, j, this.data[i].multiply(v.data[j]));
            }
        }
        return out;
    }
    
    public T getEntry(final int index) {
        return this.data[index];
    }
    
    public int getDimension() {
        return this.data.length;
    }
    
    public FieldVector<T> append(final FieldVector<T> v) {
        try {
            return this.append((ArrayFieldVector)v);
        }
        catch (ClassCastException cce) {
            return new ArrayFieldVector((ArrayFieldVector<FieldElement>)this, new ArrayFieldVector<FieldElement>((FieldVector<FieldElement>)v));
        }
    }
    
    public ArrayFieldVector<T> append(final ArrayFieldVector<T> v) {
        return new ArrayFieldVector<T>(this, v);
    }
    
    public FieldVector<T> append(final T in) {
        final T[] out = this.buildArray(this.data.length + 1);
        System.arraycopy(this.data, 0, out, 0, this.data.length);
        out[this.data.length] = in;
        return new ArrayFieldVector((Field<FieldElement>)this.field, out, false);
    }
    
    public FieldVector<T> getSubVector(final int index, final int n) throws OutOfRangeException, NotPositiveException {
        if (n < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
        }
        final ArrayFieldVector<T> out = new ArrayFieldVector<T>(this.field, n);
        try {
            System.arraycopy(this.data, index, out.data, 0, n);
        }
        catch (IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + n - 1);
        }
        return out;
    }
    
    public void setEntry(final int index, final T value) {
        try {
            this.data[index] = value;
        }
        catch (IndexOutOfBoundsException e) {
            this.checkIndex(index);
        }
    }
    
    public void setSubVector(final int index, final FieldVector<T> v) throws OutOfRangeException {
        try {
            try {
                this.set(index, (ArrayFieldVector)v);
            }
            catch (ClassCastException cce) {
                for (int i = index; i < index + v.getDimension(); ++i) {
                    this.data[i] = v.getEntry(i - index);
                }
            }
        }
        catch (IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + v.getDimension() - 1);
        }
    }
    
    public void set(final int index, final ArrayFieldVector<T> v) throws OutOfRangeException {
        try {
            System.arraycopy(v.data, 0, this.data, index, v.data.length);
        }
        catch (IndexOutOfBoundsException e) {
            this.checkIndex(index);
            this.checkIndex(index + v.data.length - 1);
        }
    }
    
    public void set(final T value) {
        Arrays.fill(this.data, value);
    }
    
    public T[] toArray() {
        return this.data.clone();
    }
    
    protected void checkVectorDimensions(final FieldVector<T> v) throws DimensionMismatchException {
        this.checkVectorDimensions(v.getDimension());
    }
    
    protected void checkVectorDimensions(final int n) throws DimensionMismatchException {
        if (this.data.length != n) {
            throw new DimensionMismatchException(this.data.length, n);
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        try {
            final FieldVector<T> rhs = (FieldVector<T>)other;
            if (this.data.length != rhs.getDimension()) {
                return false;
            }
            for (int i = 0; i < this.data.length; ++i) {
                if (!this.data[i].equals(rhs.getEntry(i))) {
                    return false;
                }
            }
            return true;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        int h = 3542;
        for (final T a : this.data) {
            h ^= a.hashCode();
        }
        return h;
    }
    
    private void checkIndex(final int index) throws OutOfRangeException {
        if (index < 0 || index >= this.getDimension()) {
            throw new OutOfRangeException(LocalizedFormats.INDEX, index, 0, this.getDimension() - 1);
        }
    }
}
