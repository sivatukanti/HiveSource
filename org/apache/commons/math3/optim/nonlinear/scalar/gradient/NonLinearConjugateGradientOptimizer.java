// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.optim.nonlinear.scalar.gradient;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.optim.OptimizationData;
import org.apache.commons.math3.analysis.solvers.BrentSolver;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GradientMultivariateOptimizer;

public class NonLinearConjugateGradientOptimizer extends GradientMultivariateOptimizer
{
    private final Formula updateFormula;
    private final Preconditioner preconditioner;
    private final UnivariateSolver solver;
    private double initialStep;
    
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker) {
        this(updateFormula, checker, new BrentSolver(), new IdentityPreconditioner());
    }
    
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker, final UnivariateSolver lineSearchSolver) {
        this(updateFormula, checker, lineSearchSolver, new IdentityPreconditioner());
    }
    
    public NonLinearConjugateGradientOptimizer(final Formula updateFormula, final ConvergenceChecker<PointValuePair> checker, final UnivariateSolver lineSearchSolver, final Preconditioner preconditioner) {
        super(checker);
        this.initialStep = 1.0;
        this.updateFormula = updateFormula;
        this.solver = lineSearchSolver;
        this.preconditioner = preconditioner;
        this.initialStep = 1.0;
    }
    
    @Override
    public PointValuePair optimize(final OptimizationData... optData) throws TooManyEvaluationsException {
        this.parseOptimizationData(optData);
        return super.optimize(optData);
    }
    
    @Override
    protected PointValuePair doOptimize() {
        final ConvergenceChecker<PointValuePair> checker = this.getConvergenceChecker();
        final double[] point = this.getStartPoint();
        final GoalType goal = this.getGoalType();
        final int n = point.length;
        double[] r = this.computeObjectiveGradient(point);
        if (goal == GoalType.MINIMIZE) {
            for (int i = 0; i < n; ++i) {
                r[i] = -r[i];
            }
        }
        double[] steepestDescent = this.preconditioner.precondition(point, r);
        double[] searchDirection = steepestDescent.clone();
        double delta = 0.0;
        for (int j = 0; j < n; ++j) {
            delta += r[j] * searchDirection[j];
        }
        PointValuePair current = null;
        int iter = 0;
        int maxEval = this.getMaxEvaluations();
        while (true) {
            ++iter;
            final double objective = this.computeObjectiveValue(point);
            final PointValuePair previous = current;
            current = new PointValuePair(point, objective);
            if (previous != null && checker.converged(iter, previous, current)) {
                return current;
            }
            final UnivariateFunction lsf = new LineSearchFunction(point, searchDirection);
            final double uB = this.findUpperBound(lsf, 0.0, this.initialStep);
            final double step = this.solver.solve(maxEval, lsf, 0.0, uB, 1.0E-15);
            maxEval -= this.solver.getEvaluations();
            for (int k = 0; k < point.length; ++k) {
                final double[] array = point;
                final int n2 = k;
                array[n2] += step * searchDirection[k];
            }
            r = this.computeObjectiveGradient(point);
            if (goal == GoalType.MINIMIZE) {
                for (int k = 0; k < n; ++k) {
                    r[k] = -r[k];
                }
            }
            final double deltaOld = delta;
            final double[] newSteepestDescent = this.preconditioner.precondition(point, r);
            delta = 0.0;
            for (int l = 0; l < n; ++l) {
                delta += r[l] * newSteepestDescent[l];
            }
            double beta = 0.0;
            switch (this.updateFormula) {
                case FLETCHER_REEVES: {
                    beta = delta / deltaOld;
                    break;
                }
                case POLAK_RIBIERE: {
                    double deltaMid = 0.0;
                    for (int m = 0; m < r.length; ++m) {
                        deltaMid += r[m] * steepestDescent[m];
                    }
                    beta = (delta - deltaMid) / deltaOld;
                    break;
                }
                default: {
                    throw new MathInternalError();
                }
            }
            steepestDescent = newSteepestDescent;
            if (iter % n == 0 || beta < 0.0) {
                searchDirection = steepestDescent.clone();
            }
            else {
                for (int i2 = 0; i2 < n; ++i2) {
                    searchDirection[i2] = steepestDescent[i2] + beta * searchDirection[i2];
                }
            }
        }
    }
    
    private void parseOptimizationData(final OptimizationData... optData) {
        for (final OptimizationData data : optData) {
            if (data instanceof BracketingStep) {
                this.initialStep = ((BracketingStep)data).getBracketingStep();
                break;
            }
        }
    }
    
    private double findUpperBound(final UnivariateFunction f, final double a, final double h) {
        double yB;
        for (double yA = yB = f.value(a), step = h; step < Double.MAX_VALUE; step *= FastMath.max(2.0, yA / yB)) {
            final double b = a + step;
            yB = f.value(b);
            if (yA * yB <= 0.0) {
                return b;
            }
        }
        throw new MathIllegalStateException(LocalizedFormats.UNABLE_TO_BRACKET_OPTIMUM_IN_LINE_SEARCH, new Object[0]);
    }
    
    public enum Formula
    {
        FLETCHER_REEVES, 
        POLAK_RIBIERE;
    }
    
    public static class BracketingStep implements OptimizationData
    {
        private final double initialStep;
        
        public BracketingStep(final double step) {
            this.initialStep = step;
        }
        
        public double getBracketingStep() {
            return this.initialStep;
        }
    }
    
    public static class IdentityPreconditioner implements Preconditioner
    {
        public double[] precondition(final double[] variables, final double[] r) {
            return r.clone();
        }
    }
    
    private class LineSearchFunction implements UnivariateFunction
    {
        private final double[] currentPoint;
        private final double[] searchDirection;
        
        public LineSearchFunction(final double[] point, final double[] direction) {
            this.currentPoint = point.clone();
            this.searchDirection = direction.clone();
        }
        
        public double value(final double x) {
            final double[] shiftedPoint = this.currentPoint.clone();
            for (int i = 0; i < shiftedPoint.length; ++i) {
                final double[] array = shiftedPoint;
                final int n = i;
                array[n] += x * this.searchDirection[i];
            }
            final double[] gradient = GradientMultivariateOptimizer.this.computeObjectiveGradient(shiftedPoint);
            double dotProduct = 0.0;
            for (int j = 0; j < gradient.length; ++j) {
                dotProduct += gradient[j] * this.searchDirection[j];
            }
            return dotProduct;
        }
    }
}
