// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.math3.ode.nonstiff;

import java.util.HashMap;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.FieldDecompositionSolver;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.apache.commons.math3.linear.ArrayFieldVector;
import java.util.Arrays;
import org.apache.commons.math3.fraction.BigFraction;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import java.util.Map;

public class AdamsNordsieckTransformer
{
    private static final Map<Integer, AdamsNordsieckTransformer> CACHE;
    private final Array2DRowRealMatrix update;
    private final double[] c1;
    
    private AdamsNordsieckTransformer(final int nSteps) {
        final FieldMatrix<BigFraction> bigP = this.buildP(nSteps);
        final FieldDecompositionSolver<BigFraction> pSolver = new FieldLUDecomposition<BigFraction>(bigP).getSolver();
        final BigFraction[] u = new BigFraction[nSteps];
        Arrays.fill(u, BigFraction.ONE);
        final BigFraction[] bigC1 = pSolver.solve(new ArrayFieldVector<BigFraction>(u, false)).toArray();
        final BigFraction[][] shiftedP = bigP.getData();
        for (int i = shiftedP.length - 1; i > 0; --i) {
            shiftedP[i] = shiftedP[i - 1];
        }
        Arrays.fill(shiftedP[0] = new BigFraction[nSteps], BigFraction.ZERO);
        final FieldMatrix<BigFraction> bigMSupdate = pSolver.solve(new Array2DRowFieldMatrix<BigFraction>(shiftedP, false));
        this.update = MatrixUtils.bigFractionMatrixToRealMatrix(bigMSupdate);
        this.c1 = new double[nSteps];
        for (int j = 0; j < nSteps; ++j) {
            this.c1[j] = bigC1[j].doubleValue();
        }
    }
    
    public static AdamsNordsieckTransformer getInstance(final int nSteps) {
        synchronized (AdamsNordsieckTransformer.CACHE) {
            AdamsNordsieckTransformer t = AdamsNordsieckTransformer.CACHE.get(nSteps);
            if (t == null) {
                t = new AdamsNordsieckTransformer(nSteps);
                AdamsNordsieckTransformer.CACHE.put(nSteps, t);
            }
            return t;
        }
    }
    
    public int getNSteps() {
        return this.c1.length;
    }
    
    private FieldMatrix<BigFraction> buildP(final int nSteps) {
        final BigFraction[][] pData = new BigFraction[nSteps][nSteps];
        for (int i = 0; i < pData.length; ++i) {
            final BigFraction[] pI = pData[i];
            int aj;
            final int factor = aj = -(i + 1);
            for (int j = 0; j < pI.length; ++j) {
                pI[j] = new BigFraction(aj * (j + 2));
                aj *= factor;
            }
        }
        return new Array2DRowFieldMatrix<BigFraction>(pData, false);
    }
    
    public Array2DRowRealMatrix initializeHighOrderDerivatives(final double h, final double[] t, final double[][] y, final double[][] yDot) {
        final double[][] a = new double[2 * (y.length - 1)][this.c1.length];
        final double[][] b = new double[2 * (y.length - 1)][y[0].length];
        final double[] y2 = y[0];
        final double[] yDot2 = yDot[0];
        for (int i = 1; i < y.length; ++i) {
            final double di = t[i] - t[0];
            final double ratio = di / h;
            double dikM1Ohk = 1.0 / h;
            final double[] aI = a[2 * i - 2];
            final double[] aDotI = a[2 * i - 1];
            for (int j = 0; j < aI.length; ++j) {
                dikM1Ohk *= ratio;
                aI[j] = di * dikM1Ohk;
                aDotI[j] = (j + 2) * dikM1Ohk;
            }
            final double[] yI = y[i];
            final double[] yDotI = yDot[i];
            final double[] bI = b[2 * i - 2];
            final double[] bDotI = b[2 * i - 1];
            for (int k = 0; k < yI.length; ++k) {
                bI[k] = yI[k] - y2[k] - di * yDot2[k];
                bDotI[k] = yDotI[k] - yDot2[k];
            }
        }
        final QRDecomposition decomposition = new QRDecomposition(new Array2DRowRealMatrix(a, false));
        final RealMatrix x = decomposition.getSolver().solve(new Array2DRowRealMatrix(b, false));
        return new Array2DRowRealMatrix(x.getData(), false);
    }
    
    public Array2DRowRealMatrix updateHighOrderDerivativesPhase1(final Array2DRowRealMatrix highOrder) {
        return this.update.multiply(highOrder);
    }
    
    public void updateHighOrderDerivativesPhase2(final double[] start, final double[] end, final Array2DRowRealMatrix highOrder) {
        final double[][] data = highOrder.getDataRef();
        for (int i = 0; i < data.length; ++i) {
            final double[] dataI = data[i];
            final double c1I = this.c1[i];
            for (int j = 0; j < dataI.length; ++j) {
                final double[] array = dataI;
                final int n = j;
                array[n] += c1I * (start[j] - end[j]);
            }
        }
    }
    
    static {
        CACHE = new HashMap<Integer, AdamsNordsieckTransformer>();
    }
}
