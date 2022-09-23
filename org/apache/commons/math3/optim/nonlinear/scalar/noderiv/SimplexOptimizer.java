// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.scalar.noderiv;

import org.apache.commons.math3.util.Pair;
import java.util.Comparator;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.optim.SimpleValueChecker;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

public class SimplexOptimizer extends MultivariateOptimizer
{
    private AbstractSimplex simplex;
    
    public SimplexOptimizer(final ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }
    
    public SimplexOptimizer(final double rel, final double abs) {
        this(new SimpleValueChecker(rel, abs));
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) {
        this.parseOptimizationData(optData);
        return super.optimize(optData);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        if (this.simplex == null) {
            throw new NullArgumentException();
        }
        final MultivariateFunction evalFunc = new MultivariateFunction() {
            public double value(final double[] point) {
                return MultivariateOptimizer.this.computeObjectiveValue(point);
            }
        };
        final boolean isMinim = this.getGoalType() == GoalType.MINIMIZE;
        final Comparator<PointValuePair> comparator = new Comparator<PointValuePair>() {
            public int compare(final PointValuePair o1, final PointValuePair o2) {
                final double v1 = ((Pair<K, Double>)o1).getValue();
                final double v2 = ((Pair<K, Double>)o2).getValue();
                return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
            }
        };
        this.simplex.build(this.getStartPoint());
        this.simplex.evaluate(evalFunc, comparator);
        PointValuePair[] previous = null;
        int iteration = 0;
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        while (true) {
            if (iteration > 0) {
                boolean converged = true;
                for (int i = 0; i < this.simplex.getSize(); ++i) {
                    final PointValuePair prev = previous[i];
                    converged = (converged && checker.converged(iteration, prev, this.simplex.getPoint(i)));
                }
                if (converged) {
                    break;
                }
            }
            previous = this.simplex.getPoints();
            this.simplex.iterate(evalFunc, comparator);
            ++iteration;
        }
        return this.simplex.getPoint(0);
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof AbstractSimplex) {
                this.simplex = (AbstractSimplex)data;
                break;
            }
        }
    }
}
