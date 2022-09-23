// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common;

import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;

public class StatsSetupConst
{
    public static final String NUM_FILES = "numFiles";
    public static final String NUM_PARTITIONS = "numPartitions";
    public static final String TOTAL_SIZE = "totalSize";
    public static final String ROW_COUNT = "numRows";
    public static final String RAW_DATA_SIZE = "rawDataSize";
    public static final String STATS_TMP_LOC = "hive.stats.tmp.loc";
    public static final String STATS_FILE_PREFIX = "tmpstats-";
    public static final String[] supportedStats;
    public static final String[] statsRequireCompute;
    public static final String[] fastStats;
    public static final String STATS_GENERATED_VIA_STATS_TASK = "STATS_GENERATED_VIA_STATS_TASK";
    public static final String DO_NOT_UPDATE_STATS = "DO_NOT_UPDATE_STATS";
    public static final String COLUMN_STATS_ACCURATE = "COLUMN_STATS_ACCURATE";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    
    public static boolean areStatsUptoDate(final Map<String, String> params) {
        final String statsAcc = params.get("COLUMN_STATS_ACCURATE");
        return statsAcc != null && statsAcc.equals("true");
    }
    
    static {
        supportedStats = new String[] { "numFiles", "numRows", "totalSize", "rawDataSize" };
        statsRequireCompute = new String[] { "numRows", "rawDataSize" };
        fastStats = new String[] { "numFiles", "totalSize" };
    }
    
    public enum StatDB
    {
        hbase {
            @Override
            public String getPublisher(final Configuration conf) {
                return "org.apache.hadoop.hive.hbase.HBaseStatsPublisher";
            }
            
            @Override
            public String getAggregator(final Configuration conf) {
                return "org.apache.hadoop.hive.hbase.HBaseStatsAggregator";
            }
        }, 
        jdbc {
            @Override
            public String getPublisher(final Configuration conf) {
                return "org.apache.hadoop.hive.ql.stats.jdbc.JDBCStatsPublisher";
            }
            
            @Override
            public String getAggregator(final Configuration conf) {
                return "org.apache.hadoop.hive.ql.stats.jdbc.JDBCStatsAggregator";
            }
        }, 
        counter {
            @Override
            public String getPublisher(final Configuration conf) {
                return "org.apache.hadoop.hive.ql.stats.CounterStatsPublisher";
            }
            
            @Override
            public String getAggregator(final Configuration conf) {
                if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVE_EXECUTION_ENGINE).equals("tez")) {
                    return "org.apache.hadoop.hive.ql.stats.CounterStatsAggregatorTez";
                }
                if (HiveConf.getVar(conf, HiveConf.ConfVars.HIVE_EXECUTION_ENGINE).equals("spark")) {
                    return "org.apache.hadoop.hive.ql.stats.CounterStatsAggregatorSpark";
                }
                return "org.apache.hadoop.hive.ql.stats.CounterStatsAggregator";
            }
        }, 
        fs {
            @Override
            public String getPublisher(final Configuration conf) {
                return "org.apache.hadoop.hive.ql.stats.fs.FSStatsPublisher";
            }
            
            @Override
            public String getAggregator(final Configuration conf) {
                return "org.apache.hadoop.hive.ql.stats.fs.FSStatsAggregator";
            }
        }, 
        custom {
            @Override
            public String getPublisher(final Configuration conf) {
                return HiveConf.getVar(conf, HiveConf.ConfVars.HIVE_STATS_DEFAULT_PUBLISHER);
            }
            
            @Override
            public String getAggregator(final Configuration conf) {
                return HiveConf.getVar(conf, HiveConf.ConfVars.HIVE_STATS_DEFAULT_AGGREGATOR);
            }
        };
        
        public abstract String getPublisher(final Configuration p0);
        
        public abstract String getAggregator(final Configuration p0);
    }
}
