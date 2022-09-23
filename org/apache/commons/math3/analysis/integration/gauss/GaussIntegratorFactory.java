// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.integration.gauss;

import org.apache.commons.math3.util.Pair;
import java.math.BigDecimal;

public class GaussIntegratorFactory
{
    private final BaseRuleFactory<Double> legendre;
    private final BaseRuleFactory<BigDecimal> legendreHighPrecision;
    
    public GaussIntegratorFactory() {
        this.legendre = new LegendreRuleFactory();
        this.legendreHighPrecision = new LegendreHighPrecisionRuleFactory();
    }
    
    public GaussIntegrator legendre(final int numberOfPoints) {
        return new GaussIntegrator(getRule(this.legendre, numberOfPoints));
    }
    
    public GaussIntegrator legendre(final int numberOfPoints, final double lowerBound, final double upperBound) {
        return new GaussIntegrator(transform(getRule(this.legendre, numberOfPoints), lowerBound, upperBound));
    }
    
    public GaussIntegrator legendreHighPrecision(final int numberOfPoints) {
        return new GaussIntegrator(getRule(this.legendreHighPrecision, numberOfPoints));
    }
    
    public GaussIntegrator legendreHighPrecision(final int numberOfPoints, final double lowerBound, final double upperBound) {
        return new GaussIntegrator(transform(getRule(this.legendreHighPrecision, numberOfPoints), lowerBound, upperBound));
    }
    
    private static Pair<double[], double[]> getRule(final BaseRuleFactory<? extends Number> factory, final int numberOfPoints) {
        return factory.getRule(numberOfPoints);
    }
    
    private static Pair<double[], double[]> transform(final Pair<double[], double[]> rule, final double a, final double b) {
        final double[] points = rule.getFirst();
        final double[] weights = rule.getSecond();
        final double scale = (b - a) / 2.0;
        final double shift = a + scale;
        for (int i = 0; i < points.length; ++i) {
            points[i] = points[i] * scale + shift;
            final double[] array = weights;
            final int n = i;
            array[n] *= scale;
        }
        return new Pair<double[], double[]>(points, weights);
    }
}
