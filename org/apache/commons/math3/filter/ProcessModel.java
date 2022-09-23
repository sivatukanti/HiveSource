// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

public interface ProcessModel
{
    RealMatrix getStateTransitionMatrix();
    
    RealMatrix getControlMatrix();
    
    RealMatrix getProcessNoise();
    
    RealVector getInitialStateEstimate();
    
    RealMatrix getInitialErrorCovariance();
}
