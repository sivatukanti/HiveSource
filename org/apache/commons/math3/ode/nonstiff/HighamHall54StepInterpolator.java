// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode.nonstiff;

import org.apache.commons.math3.ode.sampling.StepInterpolator;

class HighamHall54StepInterpolator extends RungeKuttaStepInterpolator
{
    private static final long serialVersionUID = 20111120L;
    
    public HighamHall54StepInterpolator() {
    }
    
    public HighamHall54StepInterpolator(final HighamHall54StepInterpolator interpolator) {
        super(interpolator);
    }
    
    @Override
    protected StepInterpolator doCopy() {
        return new HighamHall54StepInterpolator(this);
    }
    
    @Override
    protected void computeInterpolatedStateAndDerivatives(final double theta, final double oneMinusThetaH) {
        final double bDot0 = 1.0 + theta * (-7.5 + theta * (16.0 - 10.0 * theta));
        final double bDot2 = theta * (28.6875 + theta * (-91.125 + 67.5 * theta));
        final double bDot3 = theta * (-44.0 + theta * (152.0 - 120.0 * theta));
        final double bDot4 = theta * (23.4375 + theta * (-78.125 + 62.5 * theta));
        final double bDot5 = theta * 5.0 / 8.0 * (2.0 * theta - 1.0);
        if (this.previousState != null && theta <= 0.5) {
            final double hTheta = this.h * theta;
            final double b0 = hTheta * (1.0 + theta * (-3.75 + theta * (5.333333333333333 - 2.5 * theta)));
            final double b2 = hTheta * (theta * (14.34375 + theta * (-30.375 + theta * 135.0 / 8.0)));
            final double b3 = hTheta * (theta * (-22.0 + theta * (50.666666666666664 + theta * -30.0)));
            final double b4 = hTheta * (theta * (11.71875 + theta * (-26.041666666666668 + theta * 125.0 / 8.0)));
            final double b5 = hTheta * (theta * (-0.3125 + theta * 5.0 / 12.0));
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot0 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[2][i];
                final double yDot3 = this.yDotK[3][i];
                final double yDot4 = this.yDotK[4][i];
                final double yDot5 = this.yDotK[5][i];
                this.interpolatedState[i] = this.previousState[i] + b0 * yDot0 + b2 * yDot2 + b3 * yDot3 + b4 * yDot4 + b5 * yDot5;
                this.interpolatedDerivatives[i] = bDot0 * yDot0 + bDot2 * yDot2 + bDot3 * yDot3 + bDot4 * yDot4 + bDot5 * yDot5;
            }
        }
        else {
            final double theta2 = theta * theta;
            final double b0 = this.h * (-0.08333333333333333 + theta * (1.0 + theta * (-3.75 + theta * (5.333333333333333 + theta * -5.0 / 2.0))));
            final double b2 = this.h * (-0.84375 + theta2 * (14.34375 + theta * (-30.375 + theta * 135.0 / 8.0)));
            final double b3 = this.h * (1.3333333333333333 + theta2 * (-22.0 + theta * (50.666666666666664 + theta * -30.0)));
            final double b4 = this.h * (-1.3020833333333333 + theta2 * (11.71875 + theta * (-26.041666666666668 + theta * 125.0 / 8.0)));
            final double b5 = this.h * (-0.10416666666666667 + theta2 * (-0.3125 + theta * 5.0 / 12.0));
            for (int i = 0; i < this.interpolatedState.length; ++i) {
                final double yDot0 = this.yDotK[0][i];
                final double yDot2 = this.yDotK[2][i];
                final double yDot3 = this.yDotK[3][i];
                final double yDot4 = this.yDotK[4][i];
                final double yDot5 = this.yDotK[5][i];
                this.interpolatedState[i] = this.currentState[i] + b0 * yDot0 + b2 * yDot2 + b3 * yDot3 + b4 * yDot4 + b5 * yDot5;
                this.interpolatedDerivatives[i] = bDot0 * yDot0 + bDot2 * yDot2 + bDot3 * yDot3 + bDot4 * yDot4 + bDot5 * yDot5;
            }
        }
    }
}
