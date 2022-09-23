// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.dfp;

import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.Incrementor;

public class BracketingNthOrderBrentSolverDFP
{
    private static final int MAXIMAL_AGING = 2;
    private final int maximalOrder;
    private final Dfp functionValueAccuracy;
    private final Dfp absoluteAccuracy;
    private final Dfp relativeAccuracy;
    private final Incrementor evaluations;
    
    public BracketingNthOrderBrentSolverDFP(final Dfp relativeAccuracy, final Dfp absoluteAccuracy, final Dfp functionValueAccuracy, final int maximalOrder) throws NumberIsTooSmallException {
        this.evaluations = new Incrementor();
        if (maximalOrder < 2) {
            throw new NumberIsTooSmallException(maximalOrder, 2, true);
        }
        this.maximalOrder = maximalOrder;
        this.absoluteAccuracy = absoluteAccuracy;
        this.relativeAccuracy = relativeAccuracy;
        this.functionValueAccuracy = functionValueAccuracy;
    }
    
    public int getMaximalOrder() {
        return this.maximalOrder;
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    public Dfp getAbsoluteAccuracy() {
        return this.absoluteAccuracy;
    }
    
    public Dfp getRelativeAccuracy() {
        return this.relativeAccuracy;
    }
    
    public Dfp getFunctionValueAccuracy() {
        return this.functionValueAccuracy;
    }
    
    public Dfp solve(final int maxEval, final UnivariateDfpFunction f, final Dfp min, final Dfp max, final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        return this.solve(maxEval, f, min, max, min.add(max).divide(2), allowedSolution);
    }
    
    public Dfp solve(final int maxEval, final UnivariateDfpFunction f, final Dfp min, final Dfp max, final Dfp startValue, final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
        MathUtils.checkNotNull(f);
        this.evaluations.setMaximalCount(maxEval);
        this.evaluations.resetCount();
        final Dfp zero = startValue.getZero();
        final Dfp nan = zero.newInstance((byte)1, (byte)3);
        final Dfp[] x = new Dfp[this.maximalOrder + 1];
        final Dfp[] y = new Dfp[this.maximalOrder + 1];
        x[0] = min;
        x[1] = startValue;
        x[2] = max;
        this.evaluations.incrementCount();
        y[1] = f.value(x[1]);
        if (y[1].isZero()) {
            return x[1];
        }
        this.evaluations.incrementCount();
        y[0] = f.value(x[0]);
        if (y[0].isZero()) {
            return x[0];
        }
        int nbPoints;
        int signChangeIndex;
        if (y[0].multiply(y[1]).negativeOrNull()) {
            nbPoints = 2;
            signChangeIndex = 1;
        }
        else {
            this.evaluations.incrementCount();
            y[2] = f.value(x[2]);
            if (y[2].isZero()) {
                return x[2];
            }
            if (!y[1].multiply(y[2]).negativeOrNull()) {
                throw new NoBracketingException(x[0].toDouble(), x[2].toDouble(), y[0].toDouble(), y[2].toDouble());
            }
            nbPoints = 3;
            signChangeIndex = 2;
        }
        final Dfp[] tmpX = new Dfp[x.length];
        Dfp xA = x[signChangeIndex - 1];
        Dfp yA = y[signChangeIndex - 1];
        final Dfp absXA = xA.abs();
        Dfp absYA = yA.abs();
        int agingA = 0;
        Dfp xB = x[signChangeIndex];
        Dfp yB = y[signChangeIndex];
        final Dfp absXB = xB.abs();
        Dfp absYB = yB.abs();
        int agingB = 0;
        while (true) {
            final Dfp maxX = absXA.lessThan(absXB) ? absXB : absXA;
            final Dfp maxY = absYA.lessThan(absYB) ? absYB : absYA;
            final Dfp xTol = this.absoluteAccuracy.add(this.relativeAccuracy.multiply(maxX));
            if (xB.subtract(xA).subtract(xTol).negativeOrNull() || maxY.lessThan(this.functionValueAccuracy)) {
                switch (allowedSolution) {
                    case ANY_SIDE: {
                        return absYA.lessThan(absYB) ? xA : xB;
                    }
                    case LEFT_SIDE: {
                        return xA;
                    }
                    case RIGHT_SIDE: {
                        return xB;
                    }
                    case BELOW_SIDE: {
                        return yA.lessThan(zero) ? xA : xB;
                    }
                    case ABOVE_SIDE: {
                        return yA.lessThan(zero) ? xB : xA;
                    }
                    default: {
                        throw new MathInternalError((Throwable)null);
                    }
                }
            }
            else {
                Dfp targetY;
                if (agingA >= 2) {
                    targetY = yB.divide(16).negate();
                }
                else if (agingB >= 2) {
                    targetY = yA.divide(16).negate();
                }
                else {
                    targetY = zero;
                }
                int start = 0;
                int end = nbPoints;
                Dfp nextX;
                do {
                    System.arraycopy(x, start, tmpX, start, end - start);
                    nextX = this.guessX(targetY, tmpX, y, start, end);
                    if (!nextX.greaterThan(xA) || !nextX.lessThan(xB)) {
                        if (signChangeIndex - start >= end - signChangeIndex) {
                            ++start;
                        }
                        else {
                            --end;
                        }
                        nextX = nan;
                    }
                } while (nextX.isNaN() && end - start > 1);
                if (nextX.isNaN()) {
                    nextX = xA.add(xB.subtract(xA).divide(2));
                    start = signChangeIndex - 1;
                    end = signChangeIndex;
                }
                this.evaluations.incrementCount();
                final Dfp nextY = f.value(nextX);
                if (nextY.isZero()) {
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
                if (nextY.multiply(yA).negativeOrNull()) {
                    xB = nextX;
                    yB = nextY;
                    absYB = yB.abs();
                    ++agingA;
                    agingB = 0;
                }
                else {
                    xA = nextX;
                    yA = nextY;
                    absYA = yA.abs();
                    agingA = 0;
                    ++agingB;
                    ++signChangeIndex;
                }
            }
        }
    }
    
    private Dfp guessX(final Dfp targetY, final Dfp[] x, final Dfp[] y, final int start, final int end) {
        for (int i = start; i < end - 1; ++i) {
            final int delta = i + 1 - start;
            for (int j = end - 1; j > i; --j) {
                x[j] = x[j].subtract(x[j - 1]).divide(y[j].subtract(y[j - delta]));
            }
        }
        Dfp x2 = targetY.getZero();
        for (int k = end - 1; k >= start; --k) {
            x2 = x[k].add(x2.multiply(targetY.subtract(y[k])));
        }
        return x2;
    }
}
