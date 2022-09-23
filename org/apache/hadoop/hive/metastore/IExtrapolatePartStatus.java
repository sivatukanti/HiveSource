// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import java.util.Map;
import java.util.HashMap;

public interface IExtrapolatePartStatus
{
    public static final String[] colStatNames = { "LONG_LOW_VALUE", "LONG_HIGH_VALUE", "DOUBLE_LOW_VALUE", "DOUBLE_HIGH_VALUE", "BIG_DECIMAL_LOW_VALUE", "BIG_DECIMAL_HIGH_VALUE", "NUM_NULLS", "NUM_DISTINCTS", "AVG_COL_LEN", "MAX_COL_LEN", "NUM_TRUES", "NUM_FALSES", "AVG_NDV_LONG", "AVG_NDV_DOUBLE", "AVG_NDV_DECIMAL", "SUM_NUM_DISTINCTS" };
    public static final HashMap<String, Integer[]> indexMaps = new HashMap<String, Integer[]>() {
        {
            this.put("bigint", new Integer[] { 0, 1, 6, 7, 12, 15 });
            this.put("int", new Integer[] { 0, 1, 6, 7, 12, 15 });
            this.put("smallint", new Integer[] { 0, 1, 6, 7, 12, 15 });
            this.put("tinyint", new Integer[] { 0, 1, 6, 7, 12, 15 });
            this.put("timestamp", new Integer[] { 0, 1, 6, 7, 12, 15 });
            this.put("long", new Integer[] { 0, 1, 6, 7, 12, 15 });
            this.put("double", new Integer[] { 2, 3, 6, 7, 13, 15 });
            this.put("float", new Integer[] { 2, 3, 6, 7, 13, 15 });
            this.put("varchar", new Integer[] { 8, 9, 6, 7, 15 });
            this.put("char", new Integer[] { 8, 9, 6, 7, 15 });
            this.put("string", new Integer[] { 8, 9, 6, 7, 15 });
            this.put("boolean", new Integer[] { 10, 11, 6, 15 });
            this.put("binary", new Integer[] { 8, 9, 6, 15 });
            this.put("decimal", new Integer[] { 4, 5, 6, 7, 14, 15 });
            this.put("default", new Integer[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 15 });
        }
    };
    public static final ColStatType[] colStatTypes = { ColStatType.Long, ColStatType.Long, ColStatType.Double, ColStatType.Double, ColStatType.Decimal, ColStatType.Decimal, ColStatType.Long, ColStatType.Long, ColStatType.Double, ColStatType.Long, ColStatType.Long, ColStatType.Long, ColStatType.Double, ColStatType.Double, ColStatType.Double, ColStatType.Long };
    public static final AggrType[] aggrTypes = { AggrType.Min, AggrType.Max, AggrType.Min, AggrType.Max, AggrType.Min, AggrType.Max, AggrType.Sum, AggrType.Max, AggrType.Max, AggrType.Max, AggrType.Sum, AggrType.Sum, AggrType.Avg, AggrType.Avg, AggrType.Avg, AggrType.Sum };
    
    Object extrapolate(final Object[] p0, final Object[] p1, final int p2, final Map<String, Integer> p3);
    
    public enum ColStatType
    {
        Long, 
        Double, 
        Decimal;
    }
    
    public enum AggrType
    {
        Min, 
        Max, 
        Sum, 
        Avg;
    }
}
