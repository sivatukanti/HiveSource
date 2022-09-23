// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class EulerStepInterpolator extends RungeKuttaStepInterpolator
{
    private static final long serialVersionUID = 20111120L;
    
    public EulerStepInterpolator() {
    }
    
    public EulerStepInterpolator(final EulerStepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new EulerStepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        if (this.previousState != null && theta <= 0.5) {
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                this.interpolatedState[i] = this.previousState[i] + theta * this.h * this.yDotK[0][i];
            }
            System.arraycopy(this.yDotK[0], 0, this.interpolatedDerivatives, 0, this.interpolatedDerivatives.length);
        }
        else {
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                this.interpolatedState[i] = this.currentState[i] - oneMinusThetaH * this.yDotK[0][i];
            }
            System.arraycopy(this.yDotK[0], 0, this.interpolatedDerivatives, 0, this.interpolatedDerivatives.length);
        }
    }
}
