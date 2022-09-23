// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.inference;

import java.util.Iterator;
import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Collection;

public class OneWayAnova
{
    public double anovaFValue(final Collection<double[]> categoryData) throws NullArgumentException, DimensionMismatchException {
        final AnovaStats a = this.anovaStats(categoryData);
        return a.F;
    }
    
    public double anovaPValue(final Collection<double[]> categoryData) throws NullArgumentException, DimensionMismatchException, ConvergenceException, MaxCountExceededException {
        final AnovaStats a = this.anovaStats(categoryData);
        final FDistribution fdist = new FDistribution(a.dfbg, a.dfwg);
        return 1.0 - fdist.cumulativeProbability(a.F);
    }
    
    public boolean anovaTest(final Collection<double[]> categoryData, final double alpha) throws NullArgumentException, DimensionMismatchException, OutOfRangeException, ConvergenceException, MaxCountExceededException {
        if (alpha <= 0.0 || alpha > 0.5) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, alpha, 0, 0.5);
        }
        return this.anovaPValue(categoryData) < alpha;
    }
    
    private AnovaStats anovaStats(final Collection<double[]> categoryData) throws NullArgumentException, DimensionMismatchException {
        if (categoryData == null) {
            throw new NullArgumentException();
        }
        if (categoryData.size() < 2) {
            throw new DimensionMismatchException(LocalizedFormats.TWO_OR_MORE_CATEGORIES_REQUIRED, categoryData.size(), 2);
        }
        for (final double[] array : categoryData) {
            if (array.length <= 1) {
                throw new DimensionMismatchException(LocalizedFormats.TWO_OR_MORE_VALUES_IN_CATEGORY_REQUIRED, array.length, 2);
            }
        }
        int dfwg = 0;
        double sswg = 0.0;
        final Sum totsum = new Sum();
        final SumOfSquares totsumsq = new SumOfSquares();
        int totnum = 0;
        for (final double[] data : categoryData) {
            final Sum sum = new Sum();
            final SumOfSquares sumsq = new SumOfSquares();
            int num = 0;
            for (int i = 0; i < data.length; ++i) {
                final double val = data[i];
                ++num;
                sum.increment(val);
                sumsq.increment(val);
                ++totnum;
                totsum.increment(val);
                totsumsq.increment(val);
            }
            dfwg += num - 1;
            final double ss = sumsq.getResult() - sum.getResult() * sum.getResult() / num;
            sswg += ss;
        }
        final double sst = totsumsq.getResult() - totsum.getResult() * totsum.getResult() / totnum;
        final double ssbg = sst - sswg;
        final int dfbg = categoryData.size() - 1;
        final double msbg = ssbg / dfbg;
        final double mswg = sswg / dfwg;
        final double F = msbg / mswg;
        return new AnovaStats(dfbg, dfwg, F);
    }
    
    private static class AnovaStats
    {
        private final int dfbg;
        private final int dfwg;
        private final double F;
        
        private AnovaStats(final int dfbg, final int dfwg, final double F) {
            this.dfbg = dfbg;
            this.dfwg = dfwg;
            this.F = F;
        }
    }
}
