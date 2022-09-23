// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class SynchronizedMultivariateSummaryStatistics extends MultivariateSummaryStatistics
{
    private static final long serialVersionUID = 7099834153347155363L;
    
    public SynchronizedMultivariateSummaryStatistics(final int k, final boolean isCovarianceBiasCorrected) {
        super(k, isCovarianceBiasCorrected);
    }
    
    @Override
    public synchronized void addValue(final double[] value) throws DimensionMismatchException {
        super.addValue(value);
    }
    
    @Override
    public synchronized int getDimension() {
        return super.getDimension();
    }
    
    @Override
    public synchronized long getN() {
        return super.getN();
    }
    
    @Override
    public synchronized double[] getSum() {
        return super.getSum();
    }
    
    @Override
    public synchronized double[] getSumSq() {
        return super.getSumSq();
    }
    
    @Override
    public synchronized double[] getSumLog() {
        return super.getSumLog();
    }
    
    @Override
    public synchronized double[] getMean() {
        return super.getMean();
    }
    
    @Override
    public synchronized double[] getStandardDeviation() {
        return super.getStandardDeviation();
    }
    
    @Override
    public synchronized RealMatrix getCovariance() {
        return super.getCovariance();
    }
    
    @Override
    public synchronized double[] getMax() {
        return super.getMax();
    }
    
    @Override
    public synchronized double[] getMin() {
        return super.getMin();
    }
    
    @Override
    public synchronized double[] getGeometricMean() {
        return super.getGeometricMean();
    }
    
    @Override
    public synchronized String toString() {
        return super.toString();
    }
    
    @Override
    public synchronized void clear() {
        super.clear();
    }
    
    @Override
    public synchronized boolean equals(final Object object) {
        return super.equals(object);
    }
    
    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getSumImpl() {
        return super.getSumImpl();
    }
    
    @Override
    public synchronized void setSumImpl(final StorelessUnivariateStatistic[] sumImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setSumImpl(sumImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getSumsqImpl() {
        return super.getSumsqImpl();
    }
    
    @Override
    public synchronized void setSumsqImpl(final StorelessUnivariateStatistic[] sumsqImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setSumsqImpl(sumsqImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getMinImpl() {
        return super.getMinImpl();
    }
    
    @Override
    public synchronized void setMinImpl(final StorelessUnivariateStatistic[] minImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setMinImpl(minImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getMaxImpl() {
        return super.getMaxImpl();
    }
    
    @Override
    public synchronized void setMaxImpl(final StorelessUnivariateStatistic[] maxImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setMaxImpl(maxImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getSumLogImpl() {
        return super.getSumLogImpl();
    }
    
    @Override
    public synchronized void setSumLogImpl(final StorelessUnivariateStatistic[] sumLogImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setSumLogImpl(sumLogImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }
    
    @Override
    public synchronized void setGeoMeanImpl(final StorelessUnivariateStatistic[] geoMeanImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setGeoMeanImpl(geoMeanImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic[] getMeanImpl() {
        return super.getMeanImpl();
    }
    
    @Override
    public synchronized void setMeanImpl(final StorelessUnivariateStatistic[] meanImpl) throws DimensionMismatchException, MathIllegalStateException {
        super.setMeanImpl(meanImpl);
    }
}
