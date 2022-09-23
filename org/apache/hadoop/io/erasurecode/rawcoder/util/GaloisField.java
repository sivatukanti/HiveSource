// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode.rawcoder.util;

import java.util.HashMap;
import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class GaloisField
{
    private static final int DEFAULT_FIELD_SIZE = 256;
    private static final int DEFAULT_PRIMITIVE_POLYNOMIAL = 285;
    private static final Map<Integer, GaloisField> instances;
    private final int[] logTable;
    private final int[] powTable;
    private final int[][] mulTable;
    private final int[][] divTable;
    private final int fieldSize;
    private final int primitivePeriod;
    private final int primitivePolynomial;
    
    private GaloisField(final int fieldSize, final int primitivePolynomial) {
        assert fieldSize > 0;
        assert primitivePolynomial > 0;
        this.fieldSize = fieldSize;
        this.primitivePeriod = fieldSize - 1;
        this.primitivePolynomial = primitivePolynomial;
        this.logTable = new int[fieldSize];
        this.powTable = new int[fieldSize];
        this.mulTable = new int[fieldSize][fieldSize];
        this.divTable = new int[fieldSize][fieldSize];
        int value = 1;
        for (int pow = 0; pow < fieldSize - 1; ++pow) {
            this.powTable[pow] = value;
            this.logTable[value] = pow;
            value *= 2;
            if (value >= fieldSize) {
                value ^= primitivePolynomial;
            }
        }
        for (int i = 0; i < fieldSize; ++i) {
            for (int j = 0; j < fieldSize; ++j) {
                if (i == 0 || j == 0) {
                    this.mulTable[i][j] = 0;
                }
                else {
                    int z = this.logTable[i] + this.logTable[j];
                    z = ((z >= this.primitivePeriod) ? (z - this.primitivePeriod) : z);
                    z = this.powTable[z];
                    this.mulTable[i][j] = z;
                }
            }
        }
        for (int i = 0; i < fieldSize; ++i) {
            for (int j = 1; j < fieldSize; ++j) {
                if (i == 0) {
                    this.divTable[i][j] = 0;
                }
                else {
                    int z = this.logTable[i] - this.logTable[j];
                    z = ((z < 0) ? (z + this.primitivePeriod) : z);
                    z = this.powTable[z];
                    this.divTable[i][j] = z;
                }
            }
        }
    }
    
    public static GaloisField getInstance(final int fieldSize, final int primitivePolynomial) {
        final int key = (fieldSize << 16 & 0xFFFF0000) + (primitivePolynomial & 0xFFFF);
        GaloisField gf;
        synchronized (GaloisField.instances) {
            gf = GaloisField.instances.get(key);
            if (gf == null) {
                gf = new GaloisField(fieldSize, primitivePolynomial);
                GaloisField.instances.put(key, gf);
            }
        }
        return gf;
    }
    
    public static GaloisField getInstance() {
        return getInstance(256, 285);
    }
    
    public int getFieldSize() {
        return this.fieldSize;
    }
    
    public int getPrimitivePolynomial() {
        return this.primitivePolynomial;
    }
    
    public int add(final int x, final int y) {
        assert x >= 0 && x < this.getFieldSize() && y >= 0 && y < this.getFieldSize();
        return x ^ y;
    }
    
    public int multiply(final int x, final int y) {
        assert x >= 0 && x < this.getFieldSize() && y >= 0 && y < this.getFieldSize();
        return this.mulTable[x][y];
    }
    
    public int divide(final int x, final int y) {
        assert x >= 0 && x < this.getFieldSize() && y > 0 && y < this.getFieldSize();
        return this.divTable[x][y];
    }
    
    public int power(int x, final int n) {
        assert x >= 0 && x < this.getFieldSize();
        if (n == 0) {
            return 1;
        }
        if (x == 0) {
            return 0;
        }
        x = this.logTable[x] * n;
        if (x < this.primitivePeriod) {
            return this.powTable[x];
        }
        x %= this.primitivePeriod;
        return this.powTable[x];
    }
    
    public void solveVandermondeSystem(final int[] x, final int[] y) {
        this.solveVandermondeSystem(x, y, x.length);
    }
    
    public void solveVandermondeSystem(final int[] x, final int[] y, final int len) {
        assert x.length <= len && y.length <= len;
        for (int i = 0; i < len - 1; ++i) {
            for (int j = len - 1; j > i; --j) {
                y[j] ^= this.mulTable[x[i]][y[j - 1]];
            }
        }
        for (int i = len - 1; i >= 0; --i) {
            for (int j = i + 1; j < len; ++j) {
                y[j] = this.divTable[y[j]][x[j] ^ x[j - i - 1]];
            }
            for (int j = i; j < len - 1; ++j) {
                y[j] ^= y[j + 1];
            }
        }
    }
    
    public void solveVandermondeSystem(final int[] x, final byte[][] y, final int[] outputOffsets, final int len, final int dataLen) {
        for (int i = 0; i < len - 1; ++i) {
            for (int j = len - 1; j > i; --j) {
                for (int idx2 = outputOffsets[j - 1], idx3 = outputOffsets[j]; idx3 < outputOffsets[j] + dataLen; ++idx3, ++idx2) {
                    y[j][idx3] ^= (byte)this.mulTable[x[i]][y[j - 1][idx2] & 0xFF];
                }
            }
        }
        for (int i = len - 1; i >= 0; --i) {
            for (int j = i + 1; j < len; ++j) {
                for (int idx3 = outputOffsets[j]; idx3 < outputOffsets[j] + dataLen; ++idx3) {
                    y[j][idx3] = (byte)this.divTable[y[j][idx3] & 0xFF][x[j] ^ x[j - i - 1]];
                }
            }
            for (int j = i; j < len - 1; ++j) {
                for (int idx2 = outputOffsets[j + 1], idx3 = outputOffsets[j]; idx3 < outputOffsets[j] + dataLen; ++idx3, ++idx2) {
                    y[j][idx3] ^= y[j + 1][idx2];
                }
            }
        }
    }
    
    public void solveVandermondeSystem(final int[] x, final ByteBuffer[] y, final int len) {
        for (int i = 0; i < len - 1; ++i) {
            for (int j = len - 1; j > i; --j) {
                final ByteBuffer p = y[j];
                for (int idx1 = p.position(), idx2 = y[j - 1].position(); idx1 < p.limit(); ++idx1, ++idx2) {
                    p.put(idx1, (byte)(p.get(idx1) ^ this.mulTable[x[i]][y[j - 1].get(idx2) & 0xFF]));
                }
            }
        }
        for (int i = len - 1; i >= 0; --i) {
            for (int j = i + 1; j < len; ++j) {
                final ByteBuffer p = y[j];
                for (int idx1 = p.position(); idx1 < p.limit(); ++idx1) {
                    p.put(idx1, (byte)this.divTable[p.get(idx1) & 0xFF][x[j] ^ x[j - i - 1]]);
                }
            }
            for (int j = i; j < len - 1; ++j) {
                final ByteBuffer p = y[j];
                for (int idx1 = p.position(), idx2 = y[j + 1].position(); idx1 < p.limit(); ++idx1, ++idx2) {
                    p.put(idx1, (byte)(p.get(idx1) ^ y[j + 1].get(idx2)));
                }
            }
        }
    }
    
    public int[] multiply(final int[] p, final int[] q) {
        final int len = p.length + q.length - 1;
        final int[] result = new int[len];
        for (int i = 0; i < len; ++i) {
            result[i] = 0;
        }
        for (int i = 0; i < p.length; ++i) {
            for (int j = 0; j < q.length; ++j) {
                result[i + j] = this.add(result[i + j], this.multiply(p[i], q[j]));
            }
        }
        return result;
    }
    
    public void remainder(final int[] dividend, final int[] divisor) {
        for (int i = dividend.length - divisor.length; i >= 0; --i) {
            final int ratio = this.divTable[dividend[i + divisor.length - 1]][divisor[divisor.length - 1]];
            for (int j = 0; j < divisor.length; ++j) {
                final int k = j + i;
                dividend[k] ^= this.mulTable[ratio][divisor[j]];
            }
        }
    }
    
    public int[] add(final int[] p, final int[] q) {
        final int len = Math.max(p.length, q.length);
        final int[] result = new int[len];
        for (int i = 0; i < len; ++i) {
            if (i < p.length && i < q.length) {
                result[i] = this.add(p[i], q[i]);
            }
            else if (i < p.length) {
                result[i] = p[i];
            }
            else {
                result[i] = q[i];
            }
        }
        return result;
    }
    
    public int substitute(final int[] p, final int x) {
        int result = 0;
        int y = 1;
        for (int i = 0; i < p.length; ++i) {
            result ^= this.mulTable[p[i]][y];
            y = this.mulTable[x][y];
        }
        return result;
    }
    
    public void substitute(final byte[][] p, final byte[] q, final int x) {
        int y = 1;
        for (int i = 0; i < p.length; ++i) {
            final byte[] pi = p[i];
            for (int j = 0; j < pi.length; ++j) {
                final int pij = pi[j] & 0xFF;
                q[j] ^= (byte)this.mulTable[pij][y];
            }
            y = this.mulTable[x][y];
        }
    }
    
    public void substitute(final byte[][] p, final int[] offsets, final int len, final byte[] q, final int offset, final int x) {
        int y = 1;
        for (int i = 0; i < p.length; ++i) {
            final byte[] pi = p[i];
            for (int iIdx = offsets[i], oIdx = offset; iIdx < offsets[i] + len; ++iIdx, ++oIdx) {
                final int pij = (pi != null) ? (pi[iIdx] & 0xFF) : 0;
                q[oIdx] ^= (byte)this.mulTable[pij][y];
            }
            y = this.mulTable[x][y];
        }
    }
    
    public void substitute(final ByteBuffer[] p, final int len, final ByteBuffer q, final int x) {
        int y = 1;
        for (int i = 0; i < p.length; ++i) {
            final ByteBuffer pi = p[i];
            final int pos = (pi != null) ? pi.position() : 0;
            for (int limit = (pi != null) ? pi.limit() : len, oIdx = q.position(), iIdx = pos; iIdx < limit; ++iIdx, ++oIdx) {
                final int pij = (pi != null) ? (pi.get(iIdx) & 0xFF) : 0;
                q.put(oIdx, (byte)(q.get(oIdx) ^ this.mulTable[pij][y]));
            }
            y = this.mulTable[x][y];
        }
    }
    
    public void remainder(final byte[][] dividend, final int[] divisor) {
        for (int i = dividend.length - divisor.length; i >= 0; --i) {
            for (int j = 0; j < divisor.length; ++j) {
                for (int k = 0; k < dividend[i].length; ++k) {
                    final int ratio = this.divTable[dividend[i + divisor.length - 1][k] & 0xFF][divisor[divisor.length - 1]];
                    dividend[j + i][k] = (byte)((dividend[j + i][k] & 0xFF) ^ this.mulTable[ratio][divisor[j]]);
                }
            }
        }
    }
    
    public void remainder(final byte[][] dividend, final int[] offsets, final int len, final int[] divisor) {
        for (int i = dividend.length - divisor.length; i >= 0; --i) {
            for (int j = 0; j < divisor.length; ++j) {
                for (int idx2 = offsets[j + i], idx3 = offsets[i + divisor.length - 1]; idx3 < offsets[i + divisor.length - 1] + len; ++idx3, ++idx2) {
                    final int ratio = this.divTable[dividend[i + divisor.length - 1][idx3] & 0xFF][divisor[divisor.length - 1]];
                    dividend[j + i][idx2] = (byte)((dividend[j + i][idx2] & 0xFF) ^ this.mulTable[ratio][divisor[j]]);
                }
            }
        }
    }
    
    public void remainder(final ByteBuffer[] dividend, final int[] divisor) {
        for (int i = dividend.length - divisor.length; i >= 0; --i) {
            for (int j = 0; j < divisor.length; ++j) {
                final ByteBuffer b1 = dividend[i + divisor.length - 1];
                final ByteBuffer b2 = dividend[j + i];
                for (int idx1 = b1.position(), idx2 = b2.position(); idx1 < b1.limit(); ++idx1, ++idx2) {
                    final int ratio = this.divTable[b1.get(idx1) & 0xFF][divisor[divisor.length - 1]];
                    b2.put(idx2, (byte)((b2.get(idx2) & 0xFF) ^ this.mulTable[ratio][divisor[j]]));
                }
            }
        }
    }
    
    public void gaussianElimination(final int[][] matrix) {
        assert matrix != null && matrix.length > 0 && matrix[0].length > 0 && matrix.length < matrix[0].length;
        final int height = matrix.length;
        final int width = matrix[0].length;
        for (int i = 0; i < height; ++i) {
            boolean pivotFound = false;
            for (int j = i; j < height; ++j) {
                if (matrix[i][j] != 0) {
                    final int[] tmp = matrix[i];
                    matrix[i] = matrix[j];
                    matrix[j] = tmp;
                    pivotFound = true;
                    break;
                }
            }
            if (pivotFound) {
                final int pivot = matrix[i][i];
                for (int k = i; k < width; ++k) {
                    matrix[i][k] = this.divide(matrix[i][k], pivot);
                }
                for (int k = i + 1; k < height; ++k) {
                    final int lead = matrix[k][i];
                    for (int l = i; l < width; ++l) {
                        matrix[k][l] = this.add(matrix[k][l], this.multiply(lead, matrix[i][l]));
                    }
                }
            }
        }
        for (int i = height - 1; i >= 0; --i) {
            for (int m = 0; m < i; ++m) {
                final int lead2 = matrix[m][i];
                for (int k2 = i; k2 < width; ++k2) {
                    matrix[m][k2] = this.add(matrix[m][k2], this.multiply(lead2, matrix[i][k2]));
                }
            }
        }
    }
    
    static {
        instances = new HashMap<Integer, GaloisField>();
    }
}
