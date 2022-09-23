// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.filter;

import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.linear.AnyMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.MatrixDimensionMismatchException;
import org.apache.commons.math3.linear.NonSquareMatrixException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealMatrix;

public class KalmanFilter
{
    private final ProcessModel processModel;
    private final MeasurementModel measurementModel;
    private RealMatrix transitionMatrix;
    private RealMatrix transitionMatrixT;
    private RealMatrix controlMatrix;
    private RealMatrix measurementMatrix;
    private RealMatrix measurementMatrixT;
    private RealVector stateEstimation;
    private RealMatrix errorCovariance;
    
    public KalmanFilter(final ProcessModel process, final MeasurementModel measurement) throws NullArgumentException, NonSquareMatrixException, DimensionMismatchException, MatrixDimensionMismatchException {
        MathUtils.checkNotNull(process);
        MathUtils.checkNotNull(measurement);
        this.processModel = process;
        this.measurementModel = measurement;
        MathUtils.checkNotNull(this.transitionMatrix = this.processModel.getStateTransitionMatrix());
        this.transitionMatrixT = this.transitionMatrix.transpose();
        if (this.processModel.getControlMatrix() == null) {
            this.controlMatrix = new Array2DRowRealMatrix();
        }
        else {
            this.controlMatrix = this.processModel.getControlMatrix();
        }
        MathUtils.checkNotNull(this.measurementMatrix = this.measurementModel.getMeasurementMatrix());
        this.measurementMatrixT = this.measurementMatrix.transpose();
        final RealMatrix processNoise = this.processModel.getProcessNoise();
        MathUtils.checkNotNull(processNoise);
        final RealMatrix measNoise = this.measurementModel.getMeasurementNoise();
        MathUtils.checkNotNull(measNoise);
        if (this.processModel.getInitialStateEstimate() == null) {
            this.stateEstimation = new ArrayRealVector(this.transitionMatrix.getColumnDimension());
        }
        else {
            this.stateEstimation = this.processModel.getInitialStateEstimate();
        }
        if (this.transitionMatrix.getColumnDimension() != this.stateEstimation.getDimension()) {
            throw new DimensionMismatchException(this.transitionMatrix.getColumnDimension(), this.stateEstimation.getDimension());
        }
        if (this.processModel.getInitialErrorCovariance() == null) {
            this.errorCovariance = processNoise.copy();
        }
        else {
            this.errorCovariance = this.processModel.getInitialErrorCovariance();
        }
        if (!this.transitionMatrix.isSquare()) {
            throw new NonSquareMatrixException(this.transitionMatrix.getRowDimension(), this.transitionMatrix.getColumnDimension());
        }
        if (this.controlMatrix != null && this.controlMatrix.getRowDimension() > 0 && this.controlMatrix.getColumnDimension() > 0 && (this.controlMatrix.getRowDimension() != this.transitionMatrix.getRowDimension() || this.controlMatrix.getColumnDimension() != 1)) {
            throw new MatrixDimensionMismatchException(this.controlMatrix.getRowDimension(), this.controlMatrix.getColumnDimension(), this.transitionMatrix.getRowDimension(), 1);
        }
        MatrixUtils.checkAdditionCompatible(this.transitionMatrix, processNoise);
        if (this.measurementMatrix.getColumnDimension() != this.transitionMatrix.getRowDimension()) {
            throw new MatrixDimensionMismatchException(this.measurementMatrix.getRowDimension(), this.measurementMatrix.getColumnDimension(), this.measurementMatrix.getRowDimension(), this.transitionMatrix.getRowDimension());
        }
        if (measNoise.getRowDimension() != this.measurementMatrix.getRowDimension() || measNoise.getColumnDimension() != 1) {
            throw new MatrixDimensionMismatchException(measNoise.getRowDimension(), measNoise.getColumnDimension(), this.measurementMatrix.getRowDimension(), 1);
        }
    }
    
    public int getStateDimension() {
        return this.stateEstimation.getDimension();
    }
    
    public int getMeasurementDimension() {
        return this.measurementMatrix.getRowDimension();
    }
    
    public double[] getStateEstimation() {
        return this.stateEstimation.toArray();
    }
    
    public RealVector getStateEstimationVector() {
        return this.stateEstimation.copy();
    }
    
    public double[][] getErrorCovariance() {
        return this.errorCovariance.getData();
    }
    
    public RealMatrix getErrorCovarianceMatrix() {
        return this.errorCovariance.copy();
    }
    
    public void predict() {
        this.predict((RealVector)null);
    }
    
    public void predict(final double[] u) throws DimensionMismatchException {
        this.predict(new ArrayRealVector(u));
    }
    
    public void predict(final RealVector u) throws DimensionMismatchException {
        if (u != null && u.getDimension() != this.controlMatrix.getColumnDimension()) {
            throw new DimensionMismatchException(u.getDimension(), this.controlMatrix.getColumnDimension());
        }
        this.stateEstimation = this.transitionMatrix.operate(this.stateEstimation);
        if (u != null) {
            this.stateEstimation = this.stateEstimation.add(this.controlMatrix.operate(u));
        }
        this.errorCovariance = this.transitionMatrix.multiply(this.errorCovariance).multiply(this.transitionMatrixT).add(this.processModel.getProcessNoise());
    }
    
    public void correct(final double[] z) throws NullArgumentException, DimensionMismatchException, SingularMatrixException {
        this.correct(new ArrayRealVector(z));
    }
    
    public void correct(final RealVector z) throws NullArgumentException, DimensionMismatchException, SingularMatrixException {
        MathUtils.checkNotNull(z);
        if (z.getDimension() != this.measurementMatrix.getRowDimension()) {
            throw new DimensionMismatchException(z.getDimension(), this.measurementMatrix.getRowDimension());
        }
        final RealMatrix s = this.measurementMatrix.multiply(this.errorCovariance).multiply(this.measurementMatrixT).add(this.measurementModel.getMeasurementNoise());
        final DecompositionSolver solver = new CholeskyDecomposition(s).getSolver();
        final RealMatrix invertedS = solver.getInverse();
        final RealVector innovation = z.subtract(this.measurementMatrix.operate(this.stateEstimation));
        final RealMatrix kalmanGain = this.errorCovariance.multiply(this.measurementMatrixT).multiply(invertedS);
        this.stateEstimation = this.stateEstimation.add(kalmanGain.operate(innovation));
        final RealMatrix identity = MatrixUtils.createRealIdentityMatrix(kalmanGain.getRowDimension());
        this.errorCovariance = identity.subtract(kalmanGain.multiply(this.measurementMatrix)).multiply(this.errorCovariance);
    }
}
