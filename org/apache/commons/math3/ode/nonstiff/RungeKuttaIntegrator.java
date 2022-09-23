// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.AbstractIntegrator;

public abstract class RungeKuttaIntegrator extends AbstractIntegrator
{
    private final double[] c;
    private final double[][] a;
    private final double[] b;
    private final RungeKuttaStepInterpolator prototype;
    private final double step;
    
    protected RungeKuttaIntegrator(final String name, final double[] c, final double[][] a, final double[] b, final RungeKuttaStepInterpolator prototype, final double step) {
        super(name);
        this.c = c;
        this.a = a;
        this.b = b;
        this.prototype = prototype;
        this.step = FastMath.abs(step);
    }
    
    @Override
    public void integrate(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(equations, t);
        this.setEquations(equations);
        final boolean forward = t > equations.getTime();
        final double[] y0 = equations.getCompleteState();
        final double[] y2 = y0.clone();
        final int stages = this.c.length + 1;
        final double[][] yDotK = new double[stages][];
        for (int i = 0; i < stages; ++i) {
            yDotK[i] = new double[y0.length];
        }
        final double[] yTmp = y0.clone();
        final double[] yDotTmp = new double[y0.length];
        final RungeKuttaStepInterpolator interpolator = (RungeKuttaStepInterpolator)this.prototype.copy();
        interpolator.reinitialize(this, yTmp, yDotK, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        interpolator.storeTime(equations.getTime());
        this.stepStart = equations.getTime();
        this.stepSize = (forward ? this.step : (-this.step));
        this.initIntegration(equations.getTime(), y0, t);
        this.isLastStep = false;
        do {
            interpolator.shift();
            this.computeDerivatives(this.stepStart, y2, yDotK[0]);
            for (int k = 1; k < stages; ++k) {
                for (int j = 0; j < y0.length; ++j) {
                    double sum = this.a[k - 1][0] * yDotK[0][j];
                    for (int l = 1; l < k; ++l) {
                        sum += this.a[k - 1][l] * yDotK[l][j];
                    }
                    yTmp[j] = y2[j] + this.stepSize * sum;
                }
                this.computeDerivatives(this.stepStart + this.c[k - 1] * this.stepSize, yTmp, yDotK[k]);
            }
            for (int m = 0; m < y0.length; ++m) {
                double sum2 = this.b[0] * yDotK[0][m];
                for (int l2 = 1; l2 < stages; ++l2) {
                    sum2 += this.b[l2] * yDotK[l2][m];
                }
                yTmp[m] = y2[m] + this.stepSize * sum2;
            }
            interpolator.storeTime(this.stepStart + this.stepSize);
            System.arraycopy(yTmp, 0, y2, 0, y0.length);
            System.arraycopy(yDotK[stages - 1], 0, yDotTmp, 0, y0.length);
            this.stepStart = this.acceptStep(interpolator, y2, yDotTmp, t);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                final double nextT = this.stepStart + this.stepSize;
                final boolean nextIsLast = forward ? (nextT >= t) : (nextT <= t);
                if (!nextIsLast) {
                    continue;
                }
                this.stepSize = t - this.stepStart;
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y2);
        this.stepStart = Double.NaN;
        this.stepSize = Double.NaN;
    }
}
