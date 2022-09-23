// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.sampling.NordsieckStepInterpolator;
import org.apache.commons.math3.ode.ExpandableStatefulODE;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class AdamsBashforthIntegrator extends AdamsIntegrator
{
    private static final String METHOD_NAME = "Adams-Bashforth";
    
    public AdamsBashforthIntegrator(final int nSteps, final double minStep, final double maxStep, final double scalAbsoluteTolerance, final double scalRelativeTolerance) throws NumberIsTooSmallException {
        super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
    }
    
    public AdamsBashforthIntegrator(final int nSteps, final double minStep, final double maxStep, final double[] vecAbsoluteTolerance, final double[] vecRelativeTolerance) throws IllegalArgumentException {
        super("Adams-Bashforth", nSteps, nSteps, minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
    }
    
    @Override
    public void integrate(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException {
        this.sanityChecks(equations, t);
        this.setEquations(equations);
        final boolean forward = t > equations.getTime();
        final double[] y0 = equations.getCompleteState();
        final double[] y2 = y0.clone();
        final double[] yDot = new double[y2.length];
        final NordsieckStepInterpolator interpolator = new NordsieckStepInterpolator();
        interpolator.reinitialize(y2, forward, equations.getPrimaryMapper(), equations.getSecondaryMappers());
        this.initIntegration(equations.getTime(), y0, t);
        this.start(equations.getTime(), y2, t);
        interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
        interpolator.storeTime(this.stepStart);
        final int lastRow = this.nordsieck.getRowDimension() - 1;
        double hNew = this.stepSize;
        interpolator.rescale(hNew);
        this.isLastStep = false;
        do {
            double error = 10.0;
            while (error >= 1.0) {
                this.stepSize = hNew;
                error = 0.0;
                for (int i = 0; i < this.mainSetDimension; ++i) {
                    final double yScale = FastMath.abs(y2[i]);
                    final double tol = (this.vecAbsoluteTolerance == null) ? (this.scalAbsoluteTolerance + this.scalRelativeTolerance * yScale) : (this.vecAbsoluteTolerance[i] + this.vecRelativeTolerance[i] * yScale);
                    final double ratio = this.nordsieck.getEntry(lastRow, i) / tol;
                    error += ratio * ratio;
                }
                error = FastMath.sqrt(error / this.mainSetDimension);
                if (error >= 1.0) {
                    final double factor = this.computeStepGrowShrinkFactor(error);
                    hNew = this.filterStep(this.stepSize * factor, forward, false);
                    interpolator.rescale(hNew);
                }
            }
            final double stepEnd = this.stepStart + this.stepSize;
            interpolator.shift();
            interpolator.setInterpolatedTime(stepEnd);
            System.arraycopy(interpolator.getInterpolatedState(), 0, y2, 0, y0.length);
            this.computeDerivatives(stepEnd, y2, yDot);
            final double[] predictedScaled = new double[y0.length];
            for (int j = 0; j < y0.length; ++j) {
                predictedScaled[j] = this.stepSize * yDot[j];
            }
            final Array2DRowRealMatrix nordsieckTmp = this.updateHighOrderDerivativesPhase1(this.nordsieck);
            this.updateHighOrderDerivativesPhase2(this.scaled, predictedScaled, nordsieckTmp);
            interpolator.reinitialize(stepEnd, this.stepSize, predictedScaled, nordsieckTmp);
            interpolator.storeTime(stepEnd);
            this.stepStart = this.acceptStep(interpolator, y2, yDot, t);
            this.scaled = predictedScaled;
            this.nordsieck = nordsieckTmp;
            interpolator.reinitialize(stepEnd, this.stepSize, this.scaled, this.nordsieck);
            if (!this.isLastStep) {
                interpolator.storeTime(this.stepStart);
                if (this.resetOccurred) {
                    this.start(this.stepStart, y2, t);
                    interpolator.reinitialize(this.stepStart, this.stepSize, this.scaled, this.nordsieck);
                }
                final double factor2 = this.computeStepGrowShrinkFactor(error);
                final double scaledH = this.stepSize * factor2;
                final double nextT = this.stepStart + scaledH;
                final boolean nextIsLast = forward ? (nextT >= t) : (nextT <= t);
                hNew = this.filterStep(scaledH, forward, nextIsLast);
                final double filteredNextT = this.stepStart + hNew;
                final boolean filteredNextIsLast = forward ? (filteredNextT >= t) : (filteredNextT <= t);
                if (filteredNextIsLast) {
                    hNew = t - this.stepStart;
                }
                interpolator.rescale(hNew);
            }
        } while (!this.isLastStep);
        equations.setTime(this.stepStart);
        equations.setCompleteState(y2);
        this.resetInternalState();
    }
}
