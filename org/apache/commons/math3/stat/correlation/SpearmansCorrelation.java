// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.linear.RealMatrix;

public class SpearmansCorrelation
{
    private final RealMatrix data;
    private final RankingAlgorithm rankingAlgorithm;
    private final PearsonsCorrelation rankCorrelation;
    
    public SpearmansCorrelation() {
        this(new NaturalRanking());
    }
    
    public SpearmansCorrelation(final RankingAlgorithm rankingAlgorithm) {
        this.data = null;
        this.rankingAlgorithm = rankingAlgorithm;
        this.rankCorrelation = null;
    }
    
    public SpearmansCorrelation(final RealMatrix dataMatrix) {
        this(dataMatrix, new NaturalRanking());
    }
    
    public SpearmansCorrelation(final RealMatrix dataMatrix, final RankingAlgorithm rankingAlgorithm) {
        this.data = dataMatrix.copy();
        this.rankingAlgorithm = rankingAlgorithm;
        this.rankTransform(this.data);
        this.rankCorrelation = new PearsonsCorrelation(this.data);
    }
    
    public RealMatrix getCorrelationMatrix() {
        return this.rankCorrelation.getCorrelationMatrix();
    }
    
    public PearsonsCorrelation getRankCorrelation() {
        return this.rankCorrelation;
    }
    
    public RealMatrix computeCorrelationMatrix(final RealMatrix matrix) {
        final RealMatrix matrixCopy = matrix.copy();
        this.rankTransform(matrixCopy);
        return new PearsonsCorrelation().computeCorrelationMatrix(matrixCopy);
    }
    
    public RealMatrix computeCorrelationMatrix(final double[][] matrix) {
        return this.computeCorrelationMatrix(new BlockRealMatrix(matrix));
    }
    
    public double correlation(final double[] xArray, final double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        }
        if (xArray.length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { xArray.length, 2 });
        }
        return new PearsonsCorrelation().correlation(this.rankingAlgorithm.rank(xArray), this.rankingAlgorithm.rank(yArray));
    }
    
    private void rankTransform(final RealMatrix matrix) {
        for (int i = 0; i < matrix.getColumnDimension(); ++i) {
            matrix.setColumn(i, this.rankingAlgorithm.rank(matrix.getColumn(i)));
        }
    }
}
