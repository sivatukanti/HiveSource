// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.analysis.solvers;

import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.analysis.UnivariateFunction;

public class BracketingNthOrderBrentSolver extends AbstractUnivariateSolver implements BracketedUnivariateSolver<UnivariateFunction>
{
    private static final double DEFAULT_ABSOLUTE_ACCURACY = 1.0E-6;
    private static final int DEFAULT_MAXIMAL_ORDER = 5;
    private static final int MAXIMAL_AGING = 2;
    private static final double REDUCTION_FACTOR = 0.0625;
    private final int maximalOrder;
    private AllowedSolution allowed;
    
    public BracketingNthOrderBrentSolver() {
        this(1.0E-6, 5);
    }
    
    public BracketingNthOrderBrentSolver(final double absoluteAccuracy, final int maximalOrder) throws NumberIsTooSmallException {
        super(absoluteAccuracy);
        if (maximalOrder < 2) {
            throw new NumberIsTooSmallException(maximalOrder, 2, true);
        }
        this.maximalOrder = maximalOrder;
        this.allowed = AllowedSolution.ANY_SIDE;
    }
    
    public BracketingNthOrderBrentSolver(final double relativeAccuracy, final double absoluteAccuracy, final int maximalOrder) throws NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy);
        if (maximalOrder < 2) {
            throw new NumberIsTooSmallException(maximalOrder, 2, true);
        }
        this.maximalOrder = maximalOrder;
        this.allowed = AllowedSolution.ANY_SIDE;
    }
    
    public BracketingNthOrderBrentSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy, final int maximalOrder) throws NumberIsTooSmallException {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
        if (maximalOrder < 2) {
            throw new NumberIsTooSmallException(maximalOrder, 2, true);
        }
        this.maximalOrder = maximalOrder;
        this.allowed = AllowedSolution.ANY_SIDE;
    }
    
    public int getMaximalOrder() {
        return this.maximalOrder;
    }
    
    @Override
    protected double doSolve() throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        final double[] x = new double[this.maximalOrder + 1];
        final double[] y = new double[this.maximalOrder + 1];
        x[0] = this.getMin();
        x[1] = this.getStartValue();
        x[2] = this.getMax();
        this.verifySequence(x[0], x[1], x[2]);
        y[1] = this.computeObjectiveValue(x[1]);
        if (Precision.equals(y[1], 0.0, 1)) {
            return x[1];
        }
        y[0] = this.computeObjectiveValue(x[0]);
        if (Precision.equals(y[0], 0.0, 1)) {
            return x[0];
        }
        int nbPoints;
        int signChangeIndex;
        if (y[0] * y[1] < 0.0) {
            nbPoints = 2;
            signChangeIndex = 1;
        }
        else {
            y[2] = this.computeObjectiveValue(x[2]);
            if (Precision.equals(y[2], 0.0, 1)) {
                return x[2];
            }
            if (y[1] * y[2] >= 0.0) {
                throw new NoBracketingException(x[0], x[2], y[0], y[2]);
            }
            nbPoints = 3;
            signChangeIndex = 2;
        }
        final double[] tmpX = new double[x.length];
        double xA = x[signChangeIndex - 1];
        double yA = y[signChangeIndex - 1];
        double absYA = FastMath.abs(yA);
        int agingA = 0;
        double xB = x[signChangeIndex];
        double yB = y[signChangeIndex];
        double absYB = FastMath.abs(yB);
        int agingB = 0;
        while (true) {
            final double xTol = this.getAbsoluteAccuracy() + this.getRelativeAccuracy() * FastMath.max(FastMath.abs(xA), FastMath.abs(xB));
            if (xB - xA <= xTol || FastMath.max(absYA, absYB) < this.getFunctionValueAccuracy()) {
                switch (this.allowed) {
                    case ANY_SIDE: {
                        return (absYA < absYB) ? xA : xB;
                    }
                    case LEFT_SIDE: {
                        return xA;
                    }
                    case RIGHT_SIDE: {
                        return xB;
                    }
                    case BELOW_SIDE: {
                        return (yA <= 0.0) ? xA : xB;
                    }
                    case ABOVE_SIDE: {
                        return (yA < 0.0) ? xB : xA;
                    }
                    default: {
                        throw new MathInternalError();
                    }
                }
            }
            else {
                double targetY;
                if (agingA >= 2) {
                    final int p = agingA - 2;
                    final double weightA = (1 << p) - 1;
                    final double weightB = p + 1;
                    targetY = (weightA * yA - weightB * 0.0625 * yB) / (weightA + weightB);
                }
                else if (agingB >= 2) {
                    final int p = agingB - 2;
                    final double weightA = p + 1;
                    final double weightB = (1 << p) - 1;
                    targetY = (weightB * yB - weightA * 0.0625 * yA) / (weightA + weightB);
                }
                else {
                    targetY = 0.0;
                }
                int start = 0;
                int end = nbPoints;
                double nextX;
                do {
                    System.arraycopy(x, start, tmpX, start, end - start);
                    nextX = this.guessX(targetY, tmpX, y, start, end);
                    if (nextX <= xA || nextX >= xB) {
                        if (signChangeIndex - start >= end - signChangeIndex) {
                            ++start;
                        }
                        else {
                            --end;
                        }
                        nextX = Double.NaN;
                    }
                } while (Double.isNaN(nextX) && end - start > 1);
                if (Double.isNaN(nextX)) {
                    nextX = xA + 0.5 * (xB - xA);
                    start = signChangeIndex - 1;
                    end = signChangeIndex;
                }
                final double nextY = this.computeObjectiveValue(nextX);
                if (Precision.equals(nextY, 0.0, 1)) {
                    return nextX;
                }
                if (nbPoints > 2 && end - start != nbPoints) {
                    nbPoints = end - start;
                    System.arraycopy(x, start, x, 0, nbPoints);
                    System.arraycopy(y, start, y, 0, nbPoints);
                    signChangeIndex -= start;
                }
                else if (nbPoints == x.length) {
                    --nbPoints;
                    if (signChangeIndex >= (x.length + 1) / 2) {
                        System.arraycopy(x, 1, x, 0, nbPoints);
                        System.arraycopy(y, 1, y, 0, nbPoints);
                        --signChangeIndex;
                    }
                }
                System.arraycopy(x, signChangeIndex, x, signChangeIndex + 1, nbPoints - signChangeIndex);
                x[signChangeIndex] = nextX;
                System.arraycopy(y, signChangeIndex, y, signChangeIndex + 1, nbPoints - signChangeIndex);
                y[signChangeIndex] = nextY;
                ++nbPoints;
                if (nextY * yA <= 0.0) {
                    xB = nextX;
                    yB = nextY;
                    absYB = FastMath.abs(yB);
                    ++agingA;
                    agingB = 0;
                }
                else {
                    xA = nextX;
                    yA = nextY;
                    absYA = FastMath.abs(yA);
                    agingA = 0;
                    ++agingB;
                    ++signChangeIndex;
                }
            }
        }
    }
    
    private double guessX(final double targetY, final double[] x, final double[] y, final int start, final int end) {
        for (int i = start; i < end - 1; ++i) {
            final int delta = i + 1 - start;
            for (int j = end - 1; j > i; --j) {
                x[j] = (x[j] - x[j - 1]) / (y[j] - y[j - delta]);
            }
        }
        double x2 = 0.0;
        for (int j = end - 1; j >= start; --j) {
            x2 = x[j] + x2 * (targetY - y[j]);
        }
        return x2;
    }
    
    public double solve(final int maxEval, final UnivariateFunction f, final double min, final double max, final AllowedSolution allowedSolution) throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max);
    }
    
    public double solve(final int maxEval, final UnivariateFunction f, final double min, final double max, final double startValue, final AllowedSolution allowedSolution) throws TooManyEvaluationsException, NumberIsTooLargeException, NoBracketingException {
        this.allowed = allowedSolution;
        return super.solve(maxEval, f, min, max, startValue);
    }
}
