// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import java.io.Serializable;

public class DiagonalMatrix extends AbstractRealMatrix implements Serializable
{
    private static final long serialVersionUID = 20121229L;
    private final double[] data;
    
    public DiagonalMatrix(final int dimension) throws NotStrictlyPositiveException {
        super(dimension, dimension);
        this.data = new double[dimension];
    }
    
    public DiagonalMatrix(final double[] d) {
        this(d, true);
    }
    
    public DiagonalMatrix(final double[] d, final boolean copyArray) {
        this.data = (copyArray ? d.clone() : d);
    }
    
    @Override
    public RealMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException, DimensionMismatchException {
        if (rowDimension != columnDimension) {
            throw new DimensionMismatchException(rowDimension, columnDimension);
        }
        return new DiagonalMatrix(rowDimension);
    }
    
    @Override
    public RealMatrix copy() {
        return new DiagonalMatrix(this.data);
    }
    
    public DiagonalMatrix add(final DiagonalMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkAdditionCompatible(this, m);
        final int dim = this.getRowDimension();
        final double[] outData = new double[dim];
        for (int i = 0; i < dim; ++i) {
            outData[i] = this.data[i] + m.data[i];
        }
        return new DiagonalMatrix(outData, false);
    }
    
    public DiagonalMatrix subtract(final DiagonalMatrix m) throws MatrixDimensionMismatchException {
        MatrixUtils.checkSubtractionCompatible(this, m);
        final int dim = this.getRowDimension();
        final double[] outData = new double[dim];
        for (int i = 0; i < dim; ++i) {
            outData[i] = this.data[i] - m.data[i];
        }
        return new DiagonalMatrix(outData, false);
    }
    
    public DiagonalMatrix multiply(final DiagonalMatrix m) throws DimensionMismatchException {
        MatrixUtils.checkMultiplicationCompatible(this, m);
        final int dim = this.getRowDimension();
        final double[] outData = new double[dim];
        for (int i = 0; i < dim; ++i) {
            outData[i] = this.data[i] * m.data[i];
        }
        return new DiagonalMatrix(outData, false);
    }
    
    @Override
    public RealMatrix multiply(final RealMatrix m) throws DimensionMismatchException {
        if (m instanceof DiagonalMatrix) {
            return this.multiply((DiagonalMatrix)m);
        }
        MatrixUtils.checkMultiplicationCompatible(this, m);
        final int nRows = m.getRowDimension();
        final int nCols = m.getColumnDimension();
        final double[][] product = new double[nRows][nCols];
        for (int r = 0; r < nRows; ++r) {
            for (int c = 0; c < nCols; ++c) {
                product[r][c] = this.data[r] * m.getEntry(r, c);
            }
        }
        return new Array2DRowRealMatrix(product, false);
    }
    
    @Override
    public double[][] getData() {
        final int dim = this.getRowDimension();
        final double[][] out = new double[dim][dim];
        for (int i = 0; i < dim; ++i) {
            out[i][i] = this.data[i];
        }
        return out;
    }
    
    public double[] getDataRef() {
        return this.data;
    }
    
    @Override
    public void setSubMatrix(final double[][] subMatrix, final int row, final int column) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double getEntry(final int row, final int column) throws OutOfRangeException {
        MatrixUtils.checkMatrixIndex(this, row, column);
        return (row == column) ? this.data[row] : 0.0;
    }
    
    @Override
    public void setEntry(final int row, final int column, final double value) throws OutOfRangeException, MathUnsupportedOperationException {
        if (row != column) {
            throw new MathUnsupportedOperationException();
        }
        MatrixUtils.checkMatrixIndex(this, row, column);
        this.data[row] = value;
    }
    
    @Override
    public void addToEntry(final int row, final int column, final double increment) throws OutOfRangeException, MathUnsupportedOperationException {
        if (row != column) {
            throw new MathUnsupportedOperationException();
        }
        MatrixUtils.checkMatrixIndex(this, row, column);
        final double[] data = this.data;
        data[row] += increment;
    }
    
    @Override
    public void multiplyEntry(final int row, final int column, final double factor) throws OutOfRangeException, MathUnsupportedOperationException {
        if (row != column) {
            throw new MathUnsupportedOperationException();
        }
        MatrixUtils.checkMatrixIndex(this, row, column);
        final double[] data = this.data;
        data[row] *= factor;
    }
    
    @Override
    public int getRowDimension() {
        return (this.data == null) ? 0 : this.data.length;
    }
    
    @Override
    public int getColumnDimension() {
        return this.getRowDimension();
    }
    
    @Override
    public double[] operate(final double[] v) throws DimensionMismatchException {
        return this.multiply(new DiagonalMatrix(v, false)).getDataRef();
    }
    
    @Override
    public double[] preMultiply(final double[] v) throws DimensionMismatchException {
        return this.operate(v);
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInRowOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInColumnOrder(final RealMatrixChangingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    @Override
    public double walkInColumnOrder(final RealMatrixPreservingVisitor visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
}
