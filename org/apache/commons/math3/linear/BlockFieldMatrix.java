// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.Field;
import java.io.Serializable;
import org.apache.commons.math3.FieldElement;

public class BlockFieldMatrix<T extends FieldElement<T>> extends AbstractFieldMatrix<T> implements Serializable
{
    public static final int BLOCK_SIZE = 36;
    private static final long serialVersionUID = -4602336630143123183L;
    private final T[][] blocks;
    private final int rows;
    private final int columns;
    private final int blockRows;
    private final int blockColumns;
    
    public BlockFieldMatrix(final Field<T> field, final int rows, final int columns) throws NotStrictlyPositiveException {
        super(field, rows, columns);
        this.rows = rows;
        this.columns = columns;
        this.blockRows = (rows + 36 - 1) / 36;
        this.blockColumns = (columns + 36 - 1) / 36;
        this.blocks = createBlocksLayout(field, rows, columns);
    }
    
    public BlockFieldMatrix(final T[][] rawData) throws DimensionMismatchException {
        this(rawData.length, rawData[0].length, toBlocksLayout(rawData), false);
    }
    
    public BlockFieldMatrix(final int rows, final int columns, final T[][] blockData, final boolean copyArray) throws DimensionMismatchException, NotStrictlyPositiveException {
        super(AbstractFieldMatrix.extractField(blockData), rows, columns);
        this.rows = rows;
        this.columns = columns;
        this.blockRows = (rows + 36 - 1) / 36;
        this.blockColumns = (columns + 36 - 1) / 36;
        if (copyArray) {
            this.blocks = AbstractFieldMatrix.buildArray(this.getField(), this.blockRows * this.blockColumns, -1);
        }
        else {
            this.blocks = blockData;
        }
        int index = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock, ++index) {
                if (blockData[index].length != iHeight * this.blockWidth(jBlock)) {
                    throw new DimensionMismatchException(blockData[index].length, iHeight * this.blockWidth(jBlock));
                }
                if (copyArray) {
                    this.blocks[index] = blockData[index].clone();
                }
            }
        }
    }
    
    public static <T extends FieldElement<T>> T[][] toBlocksLayout(final T[][] rawData) throws DimensionMismatchException {
        final int rows = rawData.length;
        final int columns = rawData[0].length;
        final int blockRows = (rows + 36 - 1) / 36;
        final int blockColumns = (columns + 36 - 1) / 36;
        for (int i = 0; i < rawData.length; ++i) {
            final int length = rawData[i].length;
            if (length != columns) {
                throw new DimensionMismatchException(columns, length);
            }
        }
        final Field<T> field = AbstractFieldMatrix.extractField(rawData);
        final T[][] blocks = AbstractFieldMatrix.buildArray(field, blockRows * blockColumns, -1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, rows);
            final int iHeight = pEnd - pStart;
            for (int jBlock = 0; jBlock < blockColumns; ++jBlock) {
                final int qStart = jBlock * 36;
                final int qEnd = FastMath.min(qStart + 36, columns);
                final int jWidth = qEnd - qStart;
                final T[] block = AbstractFieldMatrix.buildArray(field, iHeight * jWidth);
                blocks[blockIndex] = block;
                int index = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    System.arraycopy(rawData[p], qStart, block, index, jWidth);
                    index += jWidth;
                }
                ++blockIndex;
            }
        }
        return blocks;
    }
    
    public static <T extends FieldElement<T>> T[][] createBlocksLayout(final Field<T> field, final int rows, final int columns) {
        final int blockRows = (rows + 36 - 1) / 36;
        final int blockColumns = (columns + 36 - 1) / 36;
        final T[][] blocks = AbstractFieldMatrix.buildArray(field, blockRows * blockColumns, -1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, rows);
            final int iHeight = pEnd - pStart;
            for (int jBlock = 0; jBlock < blockColumns; ++jBlock) {
                final int qStart = jBlock * 36;
                final int qEnd = FastMath.min(qStart + 36, columns);
                final int jWidth = qEnd - qStart;
                blocks[blockIndex] = AbstractFieldMatrix.buildArray(field, iHeight * jWidth);
                ++blockIndex;
            }
        }
        return blocks;
    }
    
    @Override
    public FieldMatrix<T> createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
        return new BlockFieldMatrix(this.getField(), rowDimension, columnDimension);
    }
    
    @Override
    public FieldMatrix<T> copy() {
        final BlockFieldMatrix<T> copied = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
        for (int i = 0; i < this.blocks.length; ++i) {
            System.arraycopy(this.blocks[i], 0, copied.blocks[i], 0, this.blocks[i].length);
        }
        return copied;
    }
    
    @Override
    public FieldMatrix<T> add(final FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        try {
            return this.add((BlockFieldMatrix)m);
        }
        catch (ClassCastException cce) {
            this.checkAdditionCompatible(m);
            final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                    final T[] outBlock = out.blocks[blockIndex];
                    final T[] tBlock = this.blocks[blockIndex];
                    final int pStart = iBlock * 36;
                    final int pEnd = FastMath.min(pStart + 36, this.rows);
                    final int qStart = jBlock * 36;
                    final int qEnd = FastMath.min(qStart + 36, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; ++p) {
                        for (int q = qStart; q < qEnd; ++q) {
                            outBlock[k] = tBlock[k].add(m.getEntry(p, q));
                            ++k;
                        }
                    }
                    ++blockIndex;
                }
            }
            return out;
        }
    }
    
    public BlockFieldMatrix<T> add(final BlockFieldMatrix<T> m) throws MatrixDimensionMismatchException {
        this.checkAdditionCompatible(m);
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final T[] outBlock = out.blocks[blockIndex];
            final T[] tBlock = this.blocks[blockIndex];
            final T[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k].add(mBlock[k]);
            }
        }
        return out;
    }
    
    @Override
    public FieldMatrix<T> subtract(final FieldMatrix<T> m) throws MatrixDimensionMismatchException {
        try {
            return this.subtract((BlockFieldMatrix)m);
        }
        catch (ClassCastException cce) {
            this.checkSubtractionCompatible(m);
            final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                    final T[] outBlock = out.blocks[blockIndex];
                    final T[] tBlock = this.blocks[blockIndex];
                    final int pStart = iBlock * 36;
                    final int pEnd = FastMath.min(pStart + 36, this.rows);
                    final int qStart = jBlock * 36;
                    final int qEnd = FastMath.min(qStart + 36, this.columns);
                    int k = 0;
                    for (int p = pStart; p < pEnd; ++p) {
                        for (int q = qStart; q < qEnd; ++q) {
                            outBlock[k] = tBlock[k].subtract(m.getEntry(p, q));
                            ++k;
                        }
                    }
                    ++blockIndex;
                }
            }
            return out;
        }
    }
    
    public BlockFieldMatrix<T> subtract(final BlockFieldMatrix<T> m) throws MatrixDimensionMismatchException {
        this.checkSubtractionCompatible(m);
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final T[] outBlock = out.blocks[blockIndex];
            final T[] tBlock = this.blocks[blockIndex];
            final T[] mBlock = m.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k].subtract(mBlock[k]);
            }
        }
        return out;
    }
    
    @Override
    public FieldMatrix<T> scalarAdd(final T d) {
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final T[] outBlock = out.blocks[blockIndex];
            final T[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k].add(d);
            }
        }
        return out;
    }
    
    @Override
    public FieldMatrix<T> scalarMultiply(final T d) {
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, this.columns);
        for (int blockIndex = 0; blockIndex < out.blocks.length; ++blockIndex) {
            final T[] outBlock = out.blocks[blockIndex];
            final T[] tBlock = this.blocks[blockIndex];
            for (int k = 0; k < outBlock.length; ++k) {
                outBlock[k] = tBlock[k].multiply(d);
            }
        }
        return out;
    }
    
    @Override
    public FieldMatrix<T> multiply(final FieldMatrix<T> m) throws DimensionMismatchException {
        try {
            return this.multiply((BlockFieldMatrix)m);
        }
        catch (ClassCastException cce) {
            this.checkMultiplicationCompatible(m);
            final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, m.getColumnDimension());
            final T zero = this.getField().getZero();
            int blockIndex = 0;
            for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
                final int pStart = iBlock * 36;
                final int pEnd = FastMath.min(pStart + 36, this.rows);
                for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                    final int qStart = jBlock * 36;
                    final int qEnd = FastMath.min(qStart + 36, m.getColumnDimension());
                    final T[] outBlock = out.blocks[blockIndex];
                    for (int kBlock = 0; kBlock < this.blockColumns; ++kBlock) {
                        final int kWidth = this.blockWidth(kBlock);
                        final T[] tBlock = this.blocks[iBlock * this.blockColumns + kBlock];
                        final int rStart = kBlock * 36;
                        int k = 0;
                        for (int p = pStart; p < pEnd; ++p) {
                            final int lStart = (p - pStart) * kWidth;
                            final int lEnd = lStart + kWidth;
                            for (int q = qStart; q < qEnd; ++q) {
                                T sum = zero;
                                int r = rStart;
                                for (int l = lStart; l < lEnd; ++l) {
                                    sum = sum.add(tBlock[l].multiply(m.getEntry(r, q)));
                                    ++r;
                                }
                                outBlock[k] = outBlock[k].add(sum);
                                ++k;
                            }
                        }
                    }
                    ++blockIndex;
                }
            }
            return out;
        }
    }
    
    public BlockFieldMatrix<T> multiply(final BlockFieldMatrix<T> m) throws DimensionMismatchException {
        this.checkMultiplicationCompatible(m);
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, m.columns);
        final T zero = this.getField().getZero();
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                final int jWidth = out.blockWidth(jBlock);
                final int jWidth2 = jWidth + jWidth;
                final int jWidth3 = jWidth2 + jWidth;
                final int jWidth4 = jWidth3 + jWidth;
                final T[] outBlock = out.blocks[blockIndex];
                for (int kBlock = 0; kBlock < this.blockColumns; ++kBlock) {
                    final int kWidth = this.blockWidth(kBlock);
                    final T[] tBlock = this.blocks[iBlock * this.blockColumns + kBlock];
                    final T[] mBlock = m.blocks[kBlock * m.blockColumns + jBlock];
                    int k = 0;
                    for (int p = pStart; p < pEnd; ++p) {
                        final int lStart = (p - pStart) * kWidth;
                        final int lEnd = lStart + kWidth;
                        for (int nStart = 0; nStart < jWidth; ++nStart) {
                            T sum = zero;
                            int l;
                            int n;
                            for (l = lStart, n = nStart; l < lEnd - 3; l += 4, n += jWidth4) {
                                sum = sum.add(tBlock[l].multiply(mBlock[n])).add(tBlock[l + 1].multiply(mBlock[n + jWidth])).add(tBlock[l + 2].multiply(mBlock[n + jWidth2])).add(tBlock[l + 3].multiply(mBlock[n + jWidth3]));
                            }
                            while (l < lEnd) {
                                sum = sum.add(tBlock[l++].multiply(mBlock[n]));
                                n += jWidth;
                            }
                            outBlock[k] = outBlock[k].add(sum);
                            ++k;
                        }
                    }
                }
                ++blockIndex;
            }
        }
        return out;
    }
    
    @Override
    public T[][] getData() {
        final T[][] data = AbstractFieldMatrix.buildArray(this.getField(), this.getRowDimension(), this.getColumnDimension());
        final int lastColumns = this.columns - (this.blockColumns - 1) * 36;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, this.rows);
            int regularPos = 0;
            int lastPos = 0;
            for (int p = pStart; p < pEnd; ++p) {
                final T[] dataP = data[p];
                int blockIndex = iBlock * this.blockColumns;
                int dataPos = 0;
                for (int jBlock = 0; jBlock < this.blockColumns - 1; ++jBlock) {
                    System.arraycopy(this.blocks[blockIndex++], regularPos, dataP, dataPos, 36);
                    dataPos += 36;
                }
                System.arraycopy(this.blocks[blockIndex], lastPos, dataP, dataPos, lastColumns);
                regularPos += 36;
                lastPos += lastColumns;
            }
        }
        return data;
    }
    
    @Override
    public FieldMatrix<T> getSubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), endRow - startRow + 1, endColumn - startColumn + 1);
        final int blockStartRow = startRow / 36;
        final int rowsShift = startRow % 36;
        final int blockStartColumn = startColumn / 36;
        final int columnsShift = startColumn % 36;
        int pBlock = blockStartRow;
        for (int iBlock = 0; iBlock < out.blockRows; ++iBlock) {
            final int iHeight = out.blockHeight(iBlock);
            int qBlock = blockStartColumn;
            for (int jBlock = 0; jBlock < out.blockColumns; ++jBlock) {
                final int jWidth = out.blockWidth(jBlock);
                final int outIndex = iBlock * out.blockColumns + jBlock;
                final T[] outBlock = out.blocks[outIndex];
                final int index = pBlock * this.blockColumns + qBlock;
                final int width = this.blockWidth(qBlock);
                final int heightExcess = iHeight + rowsShift - 36;
                final int widthExcess = jWidth + columnsShift - 36;
                if (heightExcess > 0) {
                    if (widthExcess > 0) {
                        final int width2 = this.blockWidth(qBlock + 1);
                        this.copyBlockPart(this.blocks[index], width, rowsShift, 36, columnsShift, 36, outBlock, jWidth, 0, 0);
                        this.copyBlockPart(this.blocks[index + 1], width2, rowsShift, 36, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                        this.copyBlockPart(this.blocks[index + this.blockColumns], width, 0, heightExcess, columnsShift, 36, outBlock, jWidth, iHeight - heightExcess, 0);
                        this.copyBlockPart(this.blocks[index + this.blockColumns + 1], width2, 0, heightExcess, 0, widthExcess, outBlock, jWidth, iHeight - heightExcess, jWidth - widthExcess);
                    }
                    else {
                        this.copyBlockPart(this.blocks[index], width, rowsShift, 36, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                        this.copyBlockPart(this.blocks[index + this.blockColumns], width, 0, heightExcess, columnsShift, jWidth + columnsShift, outBlock, jWidth, iHeight - heightExcess, 0);
                    }
                }
                else if (widthExcess > 0) {
                    final int width2 = this.blockWidth(qBlock + 1);
                    this.copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, 36, outBlock, jWidth, 0, 0);
                    this.copyBlockPart(this.blocks[index + 1], width2, rowsShift, iHeight + rowsShift, 0, widthExcess, outBlock, jWidth, 0, jWidth - widthExcess);
                }
                else {
                    this.copyBlockPart(this.blocks[index], width, rowsShift, iHeight + rowsShift, columnsShift, jWidth + columnsShift, outBlock, jWidth, 0, 0);
                }
                ++qBlock;
            }
            ++pBlock;
        }
        return out;
    }
    
    private void copyBlockPart(final T[] srcBlock, final int srcWidth, final int srcStartRow, final int srcEndRow, final int srcStartColumn, final int srcEndColumn, final T[] dstBlock, final int dstWidth, final int dstStartRow, final int dstStartColumn) {
        final int length = srcEndColumn - srcStartColumn;
        int srcPos = srcStartRow * srcWidth + srcStartColumn;
        int dstPos = dstStartRow * dstWidth + dstStartColumn;
        for (int srcRow = srcStartRow; srcRow < srcEndRow; ++srcRow) {
            System.arraycopy(srcBlock, srcPos, dstBlock, dstPos, length);
            srcPos += srcWidth;
            dstPos += dstWidth;
        }
    }
    
    @Override
    public void setSubMatrix(final T[][] subMatrix, final int row, final int column) throws DimensionMismatchException, OutOfRangeException, NoDataException, NullArgumentException {
        MathUtils.checkNotNull(subMatrix);
        final int refLength = subMatrix[0].length;
        if (refLength == 0) {
            throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
        }
        final int endRow = row + subMatrix.length - 1;
        final int endColumn = column + refLength - 1;
        this.checkSubMatrixIndex(row, endRow, column, endColumn);
        for (final T[] subRow : subMatrix) {
            if (subRow.length != refLength) {
                throw new DimensionMismatchException(refLength, subRow.length);
            }
        }
        final int blockStartRow = row / 36;
        final int blockEndRow = (endRow + 36) / 36;
        final int blockStartColumn = column / 36;
        final int blockEndColumn = (endColumn + 36) / 36;
        for (int iBlock = blockStartRow; iBlock < blockEndRow; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final int firstRow = iBlock * 36;
            final int iStart = FastMath.max(row, firstRow);
            final int iEnd = FastMath.min(endRow + 1, firstRow + iHeight);
            for (int jBlock = blockStartColumn; jBlock < blockEndColumn; ++jBlock) {
                final int jWidth = this.blockWidth(jBlock);
                final int firstColumn = jBlock * 36;
                final int jStart = FastMath.max(column, firstColumn);
                final int jEnd = FastMath.min(endColumn + 1, firstColumn + jWidth);
                final int jLength = jEnd - jStart;
                final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int i = iStart; i < iEnd; ++i) {
                    System.arraycopy(subMatrix[i - row], jStart - column, block, (i - firstRow) * jWidth + (jStart - firstColumn), jLength);
                }
            }
        }
    }
    
    @Override
    public FieldMatrix<T> getRowMatrix(final int row) throws OutOfRangeException {
        this.checkRowIndex(row);
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), 1, this.columns);
        final int iBlock = row / 36;
        final int iRow = row - iBlock * 36;
        int outBlockIndex = 0;
        int outIndex = 0;
        T[] outBlock = out.blocks[outBlockIndex];
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            final int available = outBlock.length - outIndex;
            if (jWidth > available) {
                System.arraycopy(block, iRow * jWidth, outBlock, outIndex, available);
                outBlock = out.blocks[++outBlockIndex];
                System.arraycopy(block, iRow * jWidth, outBlock, 0, jWidth - available);
                outIndex = jWidth - available;
            }
            else {
                System.arraycopy(block, iRow * jWidth, outBlock, outIndex, jWidth);
                outIndex += jWidth;
            }
        }
        return out;
    }
    
    @Override
    public void setRowMatrix(final int row, final FieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        try {
            this.setRowMatrix(row, (BlockFieldMatrix)matrix);
        }
        catch (ClassCastException cce) {
            super.setRowMatrix(row, matrix);
        }
    }
    
    public void setRowMatrix(final int row, final BlockFieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        if (matrix.getRowDimension() != 1 || matrix.getColumnDimension() != nCols) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
        }
        final int iBlock = row / 36;
        final int iRow = row - iBlock * 36;
        int mBlockIndex = 0;
        int mIndex = 0;
        T[] mBlock = matrix.blocks[mBlockIndex];
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            final int available = mBlock.length - mIndex;
            if (jWidth > available) {
                System.arraycopy(mBlock, mIndex, block, iRow * jWidth, available);
                mBlock = matrix.blocks[++mBlockIndex];
                System.arraycopy(mBlock, 0, block, iRow * jWidth, jWidth - available);
                mIndex = jWidth - available;
            }
            else {
                System.arraycopy(mBlock, mIndex, block, iRow * jWidth, jWidth);
                mIndex += jWidth;
            }
        }
    }
    
    @Override
    public FieldMatrix<T> getColumnMatrix(final int column) throws OutOfRangeException {
        this.checkColumnIndex(column);
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), this.rows, 1);
        final int jBlock = column / 36;
        final int jColumn = column - jBlock * 36;
        final int jWidth = this.blockWidth(jBlock);
        int outBlockIndex = 0;
        int outIndex = 0;
        T[] outBlock = out.blocks[outBlockIndex];
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                if (outIndex >= outBlock.length) {
                    outBlock = out.blocks[++outBlockIndex];
                    outIndex = 0;
                }
                outBlock[outIndex++] = block[i * jWidth + jColumn];
            }
        }
        return out;
    }
    
    @Override
    public void setColumnMatrix(final int column, final FieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        try {
            this.setColumnMatrix(column, (BlockFieldMatrix)matrix);
        }
        catch (ClassCastException cce) {
            super.setColumnMatrix(column, matrix);
        }
    }
    
    void setColumnMatrix(final int column, final BlockFieldMatrix<T> matrix) throws MatrixDimensionMismatchException, OutOfRangeException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        if (matrix.getRowDimension() != nRows || matrix.getColumnDimension() != 1) {
            throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
        }
        final int jBlock = column / 36;
        final int jColumn = column - jBlock * 36;
        final int jWidth = this.blockWidth(jBlock);
        int mBlockIndex = 0;
        int mIndex = 0;
        T[] mBlock = matrix.blocks[mBlockIndex];
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                if (mIndex >= mBlock.length) {
                    mBlock = matrix.blocks[++mBlockIndex];
                    mIndex = 0;
                }
                block[i * jWidth + jColumn] = mBlock[mIndex++];
            }
        }
    }
    
    @Override
    public FieldVector<T> getRowVector(final int row) throws OutOfRangeException {
        this.checkRowIndex(row);
        final T[] outData = AbstractFieldMatrix.buildArray(this.getField(), this.columns);
        final int iBlock = row / 36;
        final int iRow = row - iBlock * 36;
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            System.arraycopy(block, iRow * jWidth, outData, outIndex, jWidth);
            outIndex += jWidth;
        }
        return new ArrayFieldVector<T>(this.getField(), outData, false);
    }
    
    @Override
    public void setRowVector(final int row, final FieldVector<T> vector) throws MatrixDimensionMismatchException, OutOfRangeException {
        try {
            this.setRow(row, ((ArrayFieldVector)vector).getDataRef());
        }
        catch (ClassCastException cce) {
            super.setRowVector(row, vector);
        }
    }
    
    @Override
    public FieldVector<T> getColumnVector(final int column) throws OutOfRangeException {
        this.checkColumnIndex(column);
        final T[] outData = AbstractFieldMatrix.buildArray(this.getField(), this.rows);
        final int jBlock = column / 36;
        final int jColumn = column - jBlock * 36;
        final int jWidth = this.blockWidth(jBlock);
        int outIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                outData[outIndex++] = block[i * jWidth + jColumn];
            }
        }
        return new ArrayFieldVector<T>(this.getField(), outData, false);
    }
    
    @Override
    public void setColumnVector(final int column, final FieldVector<T> vector) throws OutOfRangeException, MatrixDimensionMismatchException {
        try {
            this.setColumn(column, ((ArrayFieldVector)vector).getDataRef());
        }
        catch (ClassCastException cce) {
            super.setColumnVector(column, vector);
        }
    }
    
    @Override
    public T[] getRow(final int row) throws OutOfRangeException {
        this.checkRowIndex(row);
        final T[] out = AbstractFieldMatrix.buildArray(this.getField(), this.columns);
        final int iBlock = row / 36;
        final int iRow = row - iBlock * 36;
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            System.arraycopy(block, iRow * jWidth, out, outIndex, jWidth);
            outIndex += jWidth;
        }
        return out;
    }
    
    @Override
    public void setRow(final int row, final T[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
        this.checkRowIndex(row);
        final int nCols = this.getColumnDimension();
        if (array.length != nCols) {
            throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
        }
        final int iBlock = row / 36;
        final int iRow = row - iBlock * 36;
        int outIndex = 0;
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            System.arraycopy(array, outIndex, block, iRow * jWidth, jWidth);
            outIndex += jWidth;
        }
    }
    
    @Override
    public T[] getColumn(final int column) throws OutOfRangeException {
        this.checkColumnIndex(column);
        final T[] out = AbstractFieldMatrix.buildArray(this.getField(), this.rows);
        final int jBlock = column / 36;
        final int jColumn = column - jBlock * 36;
        final int jWidth = this.blockWidth(jBlock);
        int outIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                out[outIndex++] = block[i * jWidth + jColumn];
            }
        }
        return out;
    }
    
    @Override
    public void setColumn(final int column, final T[] array) throws MatrixDimensionMismatchException, OutOfRangeException {
        this.checkColumnIndex(column);
        final int nRows = this.getRowDimension();
        if (array.length != nRows) {
            throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
        }
        final int jBlock = column / 36;
        final int jColumn = column - jBlock * 36;
        final int jWidth = this.blockWidth(jBlock);
        int outIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int iHeight = this.blockHeight(iBlock);
            final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
            for (int i = 0; i < iHeight; ++i) {
                block[i * jWidth + jColumn] = array[outIndex++];
            }
        }
    }
    
    @Override
    public T getEntry(final int row, final int column) throws OutOfRangeException {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        final int iBlock = row / 36;
        final int jBlock = column / 36;
        final int k = (row - iBlock * 36) * this.blockWidth(jBlock) + (column - jBlock * 36);
        return this.blocks[iBlock * this.blockColumns + jBlock][k];
    }
    
    @Override
    public void setEntry(final int row, final int column, final T value) throws OutOfRangeException {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        final int iBlock = row / 36;
        final int jBlock = column / 36;
        final int k = (row - iBlock * 36) * this.blockWidth(jBlock) + (column - jBlock * 36);
        this.blocks[iBlock * this.blockColumns + jBlock][k] = value;
    }
    
    @Override
    public void addToEntry(final int row, final int column, final T increment) throws OutOfRangeException {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        final int iBlock = row / 36;
        final int jBlock = column / 36;
        final int k = (row - iBlock * 36) * this.blockWidth(jBlock) + (column - jBlock * 36);
        final T[] blockIJ = this.blocks[iBlock * this.blockColumns + jBlock];
        blockIJ[k] = blockIJ[k].add(increment);
    }
    
    @Override
    public void multiplyEntry(final int row, final int column, final T factor) throws OutOfRangeException {
        this.checkRowIndex(row);
        this.checkColumnIndex(column);
        final int iBlock = row / 36;
        final int jBlock = column / 36;
        final int k = (row - iBlock * 36) * this.blockWidth(jBlock) + (column - jBlock * 36);
        final T[] blockIJ = this.blocks[iBlock * this.blockColumns + jBlock];
        blockIJ[k] = blockIJ[k].multiply(factor);
    }
    
    @Override
    public FieldMatrix<T> transpose() {
        final int nRows = this.getRowDimension();
        final int nCols = this.getColumnDimension();
        final BlockFieldMatrix<T> out = new BlockFieldMatrix<T>(this.getField(), nCols, nRows);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockColumns; ++iBlock) {
            for (int jBlock = 0; jBlock < this.blockRows; ++jBlock) {
                final T[] outBlock = out.blocks[blockIndex];
                final T[] tBlock = this.blocks[jBlock * this.blockColumns + iBlock];
                final int pStart = iBlock * 36;
                final int pEnd = FastMath.min(pStart + 36, this.columns);
                final int qStart = jBlock * 36;
                final int qEnd = FastMath.min(qStart + 36, this.rows);
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    final int lInc = pEnd - pStart;
                    int l = p - pStart;
                    for (int q = qStart; q < qEnd; ++q) {
                        outBlock[k] = tBlock[l];
                        ++k;
                        l += lInc;
                    }
                }
                ++blockIndex;
            }
        }
        return out;
    }
    
    @Override
    public int getRowDimension() {
        return this.rows;
    }
    
    @Override
    public int getColumnDimension() {
        return this.columns;
    }
    
    @Override
    public T[] operate(final T[] v) throws DimensionMismatchException {
        if (v.length != this.columns) {
            throw new DimensionMismatchException(v.length, this.columns);
        }
        final T[] out = AbstractFieldMatrix.buildArray(this.getField(), this.rows);
        final T zero = this.getField().getZero();
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                final int qStart = jBlock * 36;
                final int qEnd = FastMath.min(qStart + 36, this.columns);
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    T sum = zero;
                    int q;
                    for (q = qStart; q < qEnd - 3; q += 4) {
                        sum = sum.add(block[k].multiply(v[q])).add(block[k + 1].multiply(v[q + 1])).add(block[k + 2].multiply(v[q + 2])).add(block[k + 3].multiply(v[q + 3]));
                        k += 4;
                    }
                    while (q < qEnd) {
                        sum = sum.add(block[k++].multiply(v[q++]));
                    }
                    out[p] = out[p].add(sum);
                }
            }
        }
        return out;
    }
    
    @Override
    public T[] preMultiply(final T[] v) throws DimensionMismatchException {
        if (v.length != this.rows) {
            throw new DimensionMismatchException(v.length, this.rows);
        }
        final T[] out = AbstractFieldMatrix.buildArray(this.getField(), this.columns);
        final T zero = this.getField().getZero();
        for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
            final int jWidth = this.blockWidth(jBlock);
            final int jWidth2 = jWidth + jWidth;
            final int jWidth3 = jWidth2 + jWidth;
            final int jWidth4 = jWidth3 + jWidth;
            final int qStart = jBlock * 36;
            final int qEnd = FastMath.min(qStart + 36, this.columns);
            for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
                final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                final int pStart = iBlock * 36;
                final int pEnd = FastMath.min(pStart + 36, this.rows);
                for (int q = qStart; q < qEnd; ++q) {
                    int k = q - qStart;
                    T sum = zero;
                    int p;
                    for (p = pStart; p < pEnd - 3; p += 4) {
                        sum = sum.add(block[k].multiply(v[p])).add(block[k + jWidth].multiply(v[p + 1])).add(block[k + jWidth2].multiply(v[p + 2])).add(block[k + jWidth3].multiply(v[p + 3]));
                        k += jWidth4;
                    }
                    while (p < pEnd) {
                        sum = sum.add(block[k].multiply(v[p++]));
                        k += jWidth;
                    }
                    out[q] = out[q].add(sum);
                }
            }
        }
        return out;
    }
    
    @Override
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            for (int pEnd = FastMath.min(pStart + 36, this.rows), p = pStart; p < pEnd; ++p) {
                for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int qStart = jBlock * 36;
                    final int qEnd = FastMath.min(qStart + 36, this.columns);
                    final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p - pStart) * jWidth;
                    for (int q = qStart; q < qEnd; ++q) {
                        block[k] = visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            for (int pEnd = FastMath.min(pStart + 36, this.rows), p = pStart; p < pEnd; ++p) {
                for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int qStart = jBlock * 36;
                    final int qEnd = FastMath.min(qStart + 36, this.columns);
                    final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p - pStart) * jWidth;
                    for (int q = qStart; q < qEnd; ++q) {
                        visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; ++iBlock) {
            final int p0 = iBlock * 36;
            final int pStart = FastMath.max(startRow, p0);
            for (int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow), p2 = pStart; p2 < pEnd; ++p2) {
                for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int q0 = jBlock * 36;
                    final int qStart = FastMath.max(startColumn, q0);
                    final int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
                    final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        block[k] = visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; ++iBlock) {
            final int p0 = iBlock * 36;
            final int pStart = FastMath.max(startRow, p0);
            for (int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow), p2 = pStart; p2 < pEnd; ++p2) {
                for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; ++jBlock) {
                    final int jWidth = this.blockWidth(jBlock);
                    final int q0 = jBlock * 36;
                    final int qStart = FastMath.max(startColumn, q0);
                    final int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
                    final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                final int qStart = jBlock * 36;
                final int qEnd = FastMath.min(qStart + 36, this.columns);
                final T[] block = this.blocks[blockIndex];
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    for (int q = qStart; q < qEnd; ++q) {
                        block[k] = visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
                ++blockIndex;
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor) {
        visitor.start(this.rows, this.columns, 0, this.rows - 1, 0, this.columns - 1);
        int blockIndex = 0;
        for (int iBlock = 0; iBlock < this.blockRows; ++iBlock) {
            final int pStart = iBlock * 36;
            final int pEnd = FastMath.min(pStart + 36, this.rows);
            for (int jBlock = 0; jBlock < this.blockColumns; ++jBlock) {
                final int qStart = jBlock * 36;
                final int qEnd = FastMath.min(qStart + 36, this.columns);
                final T[] block = this.blocks[blockIndex];
                int k = 0;
                for (int p = pStart; p < pEnd; ++p) {
                    for (int q = qStart; q < qEnd; ++q) {
                        visitor.visit(p, q, block[k]);
                        ++k;
                    }
                }
                ++blockIndex;
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; ++iBlock) {
            final int p0 = iBlock * 36;
            final int pStart = FastMath.max(startRow, p0);
            final int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow);
            for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; ++jBlock) {
                final int jWidth = this.blockWidth(jBlock);
                final int q0 = jBlock * 36;
                final int qStart = FastMath.max(startColumn, q0);
                final int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
                final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int p2 = pStart; p2 < pEnd; ++p2) {
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        block[k] = visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    @Override
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor, final int startRow, final int endRow, final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
        this.checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(this.rows, this.columns, startRow, endRow, startColumn, endColumn);
        for (int iBlock = startRow / 36; iBlock < 1 + endRow / 36; ++iBlock) {
            final int p0 = iBlock * 36;
            final int pStart = FastMath.max(startRow, p0);
            final int pEnd = FastMath.min((iBlock + 1) * 36, 1 + endRow);
            for (int jBlock = startColumn / 36; jBlock < 1 + endColumn / 36; ++jBlock) {
                final int jWidth = this.blockWidth(jBlock);
                final int q0 = jBlock * 36;
                final int qStart = FastMath.max(startColumn, q0);
                final int qEnd = FastMath.min((jBlock + 1) * 36, 1 + endColumn);
                final T[] block = this.blocks[iBlock * this.blockColumns + jBlock];
                for (int p2 = pStart; p2 < pEnd; ++p2) {
                    int k = (p2 - p0) * jWidth + qStart - q0;
                    for (int q2 = qStart; q2 < qEnd; ++q2) {
                        visitor.visit(p2, q2, block[k]);
                        ++k;
                    }
                }
            }
        }
        return visitor.end();
    }
    
    private int blockHeight(final int blockRow) {
        return (blockRow == this.blockRows - 1) ? (this.rows - blockRow * 36) : 36;
    }
    
    private int blockWidth(final int blockColumn) {
        return (blockColumn == this.blockColumns - 1) ? (this.columns - blockColumn * 36) : 36;
    }
}
