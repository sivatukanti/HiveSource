// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.math.BigDecimal;
import java.util.Map;

public class LinearExtrapolatePartStatus implements IExtrapolatePartStatus
{
    @Override
    public Object extrapolate(final Object[] min, final Object[] max, final int colStatIndex, final Map<String, Integer> indexMap) {
        final int rightBorderInd = indexMap.size() - 1;
        final int minInd = indexMap.get(min[1]);
        final int maxInd = indexMap.get(max[1]);
        if (minInd == maxInd) {
            return min[0];
        }
        double decimalmin = 0.0;
        double decimalmax = 0.0;
        if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Decimal) {
            final BigDecimal bdmin = new BigDecimal(min[0].toString());
            decimalmin = bdmin.doubleValue();
            final BigDecimal bdmax = new BigDecimal(max[0].toString());
            decimalmax = bdmax.doubleValue();
        }
        if (LinearExtrapolatePartStatus.aggrTypes[colStatIndex] == AggrType.Max) {
            if (minInd < maxInd) {
                if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Long) {
                    return (long)min[0] + ((long)max[0] - (long)min[0]) * (rightBorderInd - minInd) / (maxInd - minInd);
                }
                if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Double) {
                    return (double)min[0] + ((double)max[0] - (double)min[0]) * (rightBorderInd - minInd) / (maxInd - minInd);
                }
                final double ret = decimalmin + (decimalmax - decimalmin) * (rightBorderInd - minInd) / (maxInd - minInd);
                return String.valueOf(ret);
            }
            else {
                if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Long) {
                    return (long)min[0] + ((long)max[0] - (long)min[0]) * minInd / (minInd - maxInd);
                }
                if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Double) {
                    return (double)min[0] + ((double)max[0] - (double)min[0]) * minInd / (minInd - maxInd);
                }
                final double ret = decimalmin + (decimalmax - decimalmin) * minInd / (minInd - maxInd);
                return String.valueOf(ret);
            }
        }
        else if (minInd < maxInd) {
            if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Long) {
                final Long ret2 = (long)max[0] - ((long)max[0] - (long)min[0]) * maxInd / (maxInd - minInd);
                return ret2;
            }
            if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Double) {
                final Double ret3 = (double)max[0] - ((double)max[0] - (double)min[0]) * maxInd / (maxInd - minInd);
                return ret3;
            }
            final double ret = decimalmax - (decimalmax - decimalmin) * maxInd / (maxInd - minInd);
            return String.valueOf(ret);
        }
        else {
            if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Long) {
                final Long ret2 = (long)max[0] - ((long)max[0] - (long)min[0]) * (rightBorderInd - maxInd) / (minInd - maxInd);
                return ret2;
            }
            if (LinearExtrapolatePartStatus.colStatTypes[colStatIndex] == ColStatType.Double) {
                final Double ret3 = (double)max[0] - ((double)max[0] - (double)min[0]) * (rightBorderInd - maxInd) / (minInd - maxInd);
                return ret3;
            }
            final double ret = decimalmax - (decimalmax - decimalmin) * (rightBorderInd - maxInd) / (minInd - maxInd);
            return String.valueOf(ret);
        }
    }
}
